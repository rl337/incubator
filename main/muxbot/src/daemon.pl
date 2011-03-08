#!/usr/bin/perl
use strict;
use Config;
do "$ENV{SRCPATH}/muxbot.pl";

my $daemonUsageStr="usage: muxbot [-l logfile] [-p pidfile] [-f configfile]";

# This holds all the default values for command line parameters.
my $daemonConfig = {
   'log' => 'muxbot.log',
   'pid' => 'muxbot.pid',
   'config' => 'network.conf',
};

# this will eventually hold the actual daemon internal data structure.
# this gets instantiated further down when the daemon is launched.
# this is global to allow signal handlers to be able to see it.
my $daemonInstance;

# the following is our default signal handler.  all it
# really does is adds an entry to @{$daemonInstance->{SIG}}
# which will allow our daemon to handle its own signal
# handling. We want to make sure $daemonInstance->{'SIG'}
# actually exists since it's perfectly possible to get
# a signal before its been initialized.

sub signalHandler {
   my $sigName = shift;

   if( $daemonInstance->{'SIG'} ) {
      push @{$daemonInstance->{'SIG'}}, $sigName;
   }
   return ;
}

# here we set all of our signal handlers to be the
# default signal handler. this uses the globals
# %Config from the Configpackage and the %SIG
# which holds all signal handlers.

# Commenting out. Was causing problems when setting SIGZERO
# foreach my $sigName (split(' ', $Config{sig_name})) {
#  if( $sigName ) {
# 	$SIG{$sigName}=\&signalHandler;
#    }
# }

$SIG{'INT'}=\&signalHandler;
$SIG{'QUIT'}=\&signalHandler;
# We want to ignore child termination signals.
$SIG{'CLD'}='IGNORE';

# all functions begining with 'option' are used to handle command
# line options. They take two parameters, a reference to the above
# mentioned daemonConfig and a reference to @ARGV.

sub optionLogfile {
   my ($argList, $config)=@_;

   my $newLogFile = shift @{$argList};
   if ( $newLogFile ) {
      $config->{'log'} = $newLogFile;
   }
}

sub optionPidfile {
   my ($argList, $config)=@_;

   my $newPidFile = shift @{$argList};
   if ( $newPidFile ) {
      $config->{'pid'} = $newPidFile;
   }
}

sub optionConfigfile {
   my ($argList, $config)=@_;

   my $newConfigFile = shift @{$argList};
   if ( $newConfigFile ) {
      $config->{'config'} = $newConfigFile;
   }
}

# this structure holds the command line parameters
# map. it associates an option with what function
# to use with it.

my $commandLineOptions = {
   '-l' => \&optionLogfile,
   '-p' => \&optionPidfile,
   '-f' => \&optionConfigfile
};

# handle any parameters coming in from the 
# command line. if an unknown parameter is
# detected, exit with something other than 0
# and print a usage string.

while ( scalar @ARGV ) {
   my $myParam = shift @ARGV;
   if( defined $commandLineOptions->{$myParam} ) {
      &{$commandLineOptions->{$myParam}}(\@ARGV, $daemonConfig);
   } else {
      print $daemonUsageStr;
      exit(1);
   }
}


# This subroutine gets run whenever any sortof termination
# occurs, this should clean up any resources allocated
# etc. 
sub daemonTerminate  {
   my $config = $_[0];
   my $instance = $_[1];

   muxbotTerminate($config, $instance); 
}

# This subroutine should be the body of the daemon
# we wish to invoke.
sub daemonExecute {
   my $config = $_[0];
   my $instance = $_[1];

   muxbotBody($config, $instance);
}

# This function handles the setup and initialization of
# the daemon process which gets spawned off. all daemon
# specific initialization should go here.
sub daemonStartup {
   my $config = shift;
   my $instance = muxbotInit($config);   
   return $instance;
}

# we're going to fork and store our process ID
# in the $pidfile.  If fork returned 0, then
# proceed with the loading of the bot, otherwise
# write the pidfile and exit.

my $pidfile = $daemonConfig->{'pid'};
my $myPid = fork( );

if( defined( $myPid) ){
    if( $myPid ne 0 ) { # check if we're the parent
      if( open( PIDFILE, '>'.$ENV{RUNPATH}.'/'.$pidfile)  ){
         print PIDFILE "$myPid";
         close(PIDFILE);
      } else {
         print "Could not create pidfile, $pidfile.";
         exit(1);
      }
      exit(0);
   } else {
# Our child process is what actually runs the daemon.
# have it execute our daemon-specific functioning.
      $daemonInstance = daemonStartup($daemonConfig);

      daemonExecute($daemonConfig, $daemonInstance);
      daemonTerminate($daemonConfig, $daemonInstance);
   }
} else {
   print "Could not fork process\n";
   exit(1);
}

# the only way we could have gotten here is via normal
# termination of the child, daemon process. remove pidfile.
unlink($ENV{RUNPATH}.'/'.$pidfile);
