#!/usr/bin/perl

push @INC, $ENV{LIBPATH};

do "$ENV{SRCPATH}/botNet.pl";

print STDERR "PATH WAS: $ENV{SRCPATH}/botNet.pl\n$@\n";
$| = 1;

use strict;

sub signalHUP {
   my $thisSignal = $_[0];
   my $initParams = $_[1];

   my $myNets = $initParams->{'nets'};
   my $statReport; 
   botNetNoticeWall($myNets, "Recieved a SIGHUP, reloading files.");
   if( botNetReconfig($myNets, $initParams->{'config'}) ) {
      $statReport = "Muxbot Config was reloaded";
   } else {
      $statReport = "Muxbot Config was NOT reloaded";
   }

   foreach my $thisNet ( botNetList($myNets) ) {
      my $thisNetInfo = botNetInfo( $myNets, $thisNet );
      my $cnfresult;
      if( $thisNetInfo->{'users'} ) {
         if( $cnfresult = usrReadConfig($thisNetInfo->{'users'}) ) {
            $thisNetInfo->{PERMS} = $cnfresult;
            $statReport.=", $thisNet was reloaded";
         } else {
            $statReport.=", $thisNet NOT reloaded";
         }
      }
   } 
   botNetNoticeWall($myNets,"Network Reload Status: $statReport.");
}

sub signalDefault {
   my $thisSignal = $_[0];
   my $initParams = $_[1];

   botNetCloseAll($initParams->{'nets'}, "recieved a SIG$thisSignal");

   $initParams->{'RUN'}=0;
}

# these are the signals we have special traps for
# all other ones get the signalDefault function.
my %signalHash = (
   'HUP' => \&signalHUP
);


# This function is used to setup all the muxbot data
# structures. 
# it takes a string representing the config file as 
# a parameter.
sub muxbotInit {
   my $config = $_[0];

   my $result = {
      'config' => $config->{'config'},
      'nets' =>  botNetInitConfig($config->{'config'}),
      'tick' =>  10,
      'sleep' => 1,
      'SIG' => [ ]
   };

   return $result unless open( DAEMONLOGFILE, ">>$ENV{LOGPATH}/$config->{log}" );
   $result->{MUXBOTLOGFD} = \*DAEMONLOGFILE;
   return $result;
}

# bits fhbits(botNetsData)
sub fhbits {
   my $myNets=$_[0];
   my ($bits);
   foreach my $netName ( botNetList($myNets) ) {
      vec($bits,fileno( botNetFD($myNets,$netName) ),1) = 1;
   }
   return $bits;
}

# int fhisset(botNetsData, bits, botNet)
sub fhisset {
   my $myNets=$_[0];
   my $bits =$_[1];
   my $botNet = $_[2];
   vec($bits,fileno(botNetFD($myNets,$botNet)),1);
}

sub muxbotTerminate {
   my $initParams = $_[0];
   muxbotLog($initParams, "Terminating muxbot operations");
   if( defined $initParams->{MUXBOTLOGFD} ) {
      close( $initParams->{MUXBOTLOGFD} );
   }
}

sub muxbotLog { 
   my $myInstance = shift;
   my $message = shift;

   my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
   $year+=1900; 
   $mon=$mon<10?"0$mon":$mon;
   $mday=$mday<10?"0$mday":$mday;
   $hour=$hour<10?"0$hour":$hour;
   $min=$min<10?"0$min":$min;
   $sec=$sec<10?"0$sec":$sec;
   if( defined $myInstance->{MUXBOTLOGFD} ) {
      my $prefix="[$year/$mon/$mday $hour:$min:$sec] muxbot: ";
      print {$myInstance->{MUXBOTLOGFD}} "$prefix $message\n";
   }
}


# this is the main body of the muxbot. 
# it takes the data structure created by muxbotInit as
# a parameter

sub muxbotBody {
   my $initConfig = $_[0];
   my $initParams = $_[1];

   my ($mybits,$thisInput,$fdout,$myInput,$fdmask);
   my $botNets = $initParams->{'nets'};
   my $updateInterval = $initParams->{'tick'}; # time between timed events
   my $sleepwait = $initParams->{'sleep'}; # time to wait for i/o
   my $sigList = $initParams->{'SIG'};     # list of signals recieved
   my $timeclock=0;   # this will hold the 'current time' during run

   my $selectretry = 0; # this is used in case select() returns < 0 
   $initParams->{RUN} = 1; # when we want to stop running, set to 0
# open connections to all active networks
   foreach my $netName (botNetList($botNets) ) {
     my $thisNetInfo = botNetInfo($botNets, $netName);
     if ( botNetOpen($botNets, $netName ) ) {
        botNetConnect($botNets, $netName);
        muxbotLog( $initParams, "Opened connection to $netName.");
     } else {
        muxbotLog( $initParams, "Connection to $netName FAILED.");
     }
   }

# this is the big loop. this is where it all happens.

   muxbotLog( $initParams, "Startup complete proceeding with execution");
   while( $initParams->{RUN} gt 0 ) {
      $mybits = fhbits($botNets);
      my $result = select($fdout=$mybits, undef, $fdmask=$mybits, $sleepwait);
      if( $result lt 0 ) { 
         $selectretry++;  # select has failed this many times.
         if( $selectretry > 4 ) { # we'll retry 5 times before we quit.
            botNetCloseAll($botNets, "select retry failed 5 times.");
	    $initParams->{RUN} = 0;
	 }
	 next;
      } else {
         $selectretry=0; # reset the selectretry variable, we succeeded
      }
      foreach my $netName ( botNetList($botNets) ) {
         if( botNetIsOn($botNets, $netName) ) {
            if( fhisset($botNets, $fdout, $netName ) ) { 
	       $myInput = botNetRead($botNets, $netName);
               if( $myInput ) {
                  $thisInput = botNetParse($botNets, $netName, $myInput);
                  botNetInterpret($botNets, $netName, $thisInput);
               } else {
                  muxbotLog( $initParams, "Error on read to $netName, closing.");
	          botNetClose($botNets, $netName);
                  if( botNetOpen($botNets, $netName) ) {
   	             botNetConnect($botNets, $netName);
                  }
               }
            }
         } else {
            if( botNetOpen($botNets, $netName) ) {
   	        botNetConnect($botNets, $netName);
            }
         }
      }
      if ( $timeclock <  time() ){
         $timeclock = time() + $updateInterval;
         botNetTimedEvents($botNets);
      } else {
         botNetIdleEvents($botNets);
      }
      while ( scalar @{$sigList} gt 0 ) {
         my $thisSignal = shift @{$sigList};
         muxbotLog( $initParams, "Handling SIG$thisSignal Signal.");
         if( defined $signalHash{$thisSignal} ){
            &{$signalHash{$thisSignal}}($thisSignal, $initParams);
         } else {
            signalDefault($thisSignal, $initParams);
         }
      }
   }
}

