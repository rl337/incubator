#!/usr/bin/perl
use strict;
use NDBM_File;
use Fcntl;

do "$ENV{SRCPATH}/user.pl";
do "$ENV{SRCPATH}/local.pl";

# The following words are used to describe the
# parameters to the functions below.
#
# botNetsData
# A datastructure that looks like the one described in
# tables.pl. More or less it's a hash whose key
# is an identifier describing a particular bot Network
# that is to be connected to. The value of each key is
# an instance of botNetInfo.
#
# botNetInfo
# This is data about a single bot Network. It has information
# such as nickname, server, bot name, uid, etc.
# 
# botNet
# This is a string which can be used to index a botNetData
# structure.
#
# UserData
# Some data stored in a botNetInfo about a user. Only exists
# Runtime.
#
# User
# used to index UserData hashes.

# value substitution hash.

my %subValueHash = (
   'yes' => 1,
   'true' => 1,
   'on' => 1,
   'no' => 0,
   'off' => 0,
   'false' => 0
);

# subroutine: botNetsData botNetParseConfig($configFileName) 
# This function simply parses through the bot's base config
# file and returns a structure. This function should not
# be called unless you just want a virginal network struct.

# if you want to do a configuration on the bot, use either
# botNetInitConfig if it's the first call, or you use
# botNetReconfig for runtime changes.

sub botNetParseConfig {
   my $configFileName = shift;
   my $currEntry;
   my $result;
   my $line=10000;
   my $thisLine;

   open(CNF, "$ENV{CNFPATH}/$configFileName") or return undef;

   while(<CNF>) {
      $thisLine=$_;
      next if $thisLine=~/^#/;
      $line++;
      $thisLine=~s/[\s][\s]*/ /g;
      if( ! ($thisLine=~/^\s/) ) {
         $currEntry=~s/^\s*(.*)\s*$/$1/g;

         my @fields = split(/:/,$currEntry);
         my $netName = shift @fields;
         if( $netName ) {
            my $myStruct;
            foreach my $elements ( @fields ) {
               my ( $key, $value ) = split( /=/, $elements );
               $key=~s/\s*(.*[\S])\s*$/$1/g;
               $value=~s/\s*(.*[\S])\s*$/$1/g;
               $myStruct->{$key}=(
                  defined($subValueHash{lc $value})
                     ?$subValueHash{lc $value}
                     :$value);
            }
            $result->{$netName}=$myStruct;
         }
         $currEntry = $thisLine;
      } elsif ( $currEntry ) {
         $currEntry.=$thisLine;
      }
   }
   return $result;
}

# subroutine: botNetsData botNetInitConfig($configFileName) 
#
# reads in the network infromation from specified file.
# This should be run the first time, subsequent calls
# should made to botNetReconfig for runtime changes in
# the config file.

sub botNetInitConfig($) {
   my $networkFile = shift;
   my %networkTypes;
   my $result = botNetParseConfig($networkFile);
   if ( ! $result ) { return undef; } 

   foreach my $thisNet ( keys %{$result} ){
      my $thisNetInfo = $result->{$thisNet};

      if( ! $thisNetInfo->{'network'} ) {
         $thisNetInfo->{'network'} = 'irc';
      }
      my $netType = $thisNetInfo->{'network'};
      if( !defined( $networkTypes{$netType} )) {
         $networkTypes{$netType} = [ $thisNet ];
      } else {
         push @{$networkTypes{$netType}}, $thisNet;
      }

      if( $thisNetInfo->{'logging'} ) {
         my $logFileName=$thisNet.'LOG';
         eval "\$thisNetInfo->{'LOGFD'} = \*$logFileName;";
         if( $@ ) {print "$@\n";}
      }
      if( $thisNetInfo->{'users'} ) {
         $thisNetInfo->{'PERMS'} = 
             usrReadConfig($thisNetInfo->{'users'});
      }
      foreach my $thisLink ( split( /,/, $thisNetInfo->{'links'} ) ) {
         botNetSetLinked($result, $thisNet, $thisLink);
      }
      my $FDFileName = $thisNet.'FD';
      eval "\$thisNetInfo->{'FD'} = \*$FDFileName;";
      if( $@ ) {print "$@\n";}

   }
   foreach my $netType ( keys %networkTypes ){
      my $funcRef;

      my $doFilename = "$ENV{SRCPATH}/$netType".".pl";
      do "$doFilename";
      if( $! || $@ ) {
         print $!.$@."\n"; 
         exit(1);
      }

      my $callbackConstructor = $netType."CreateCallbacks";
      eval "\$funcRef = \\\&$callbackConstructor;";
      next if $@;

      my $callbacks = &{$funcRef}();
      foreach my $myNet ( @{$networkTypes{$netType}} ) {
         my $thisNetInfo = $result->{$myNet};
         $thisNetInfo->{callbacks} = $callbacks;
      }
   }

   return $result;
}

# subroutine: Bool botNetReconfig( botNetsData, configFile )
# This subroutine is used to reload preferences on the
# fly. It modifies the currently running botNetsData
# structure.

sub botNetReconfig($$) {
   my $myNets = shift;
   my $networkFile = shift;

   my $newConfig = botNetParseConfig($networkFile);
   if ( ! $newConfig ) { return undef; } 

   foreach my $thisNet ( keys %{$newConfig} ){
      my $newNetInfo = $newConfig->{$thisNet};
      my $oldNetInfo = $myNets->{$thisNet};

# Copy the new preferences over onto the old ones. We don't just assign
# the new hashref to the old ones because we want to preserve the runtime
# values within the old struct.
      foreach my $netOption ( keys %{$newNetInfo} ){
         $oldNetInfo->{$netOption} = $newNetInfo->{$netOption};
      }

# If we have logging set, but no LOGFD, create a new LOGFD.
      if( $oldNetInfo->{'logging'} && ! $oldNetInfo->{'LOGFD'} ) {
         my $logFileName=$thisNet.'LOG';
         eval "\$oldNetInfo->{'LOGFD'} = \*$logFileName;";
         if( $@ ) {print "$@\n";}
      }

# if we don't already have a PERMS entry, create one.
      if( $oldNetInfo->{'users'} && ! $oldNetInfo->{'PERMS'} ) {
         $oldNetInfo->{'PERMS'} = 
             usrReadConfig($oldNetInfo->{'users'});
      }

# Check to see if we already have a File descriptor. If we don't,
# create a new one.
      if( ! $oldNetInfo->{'FD'} ) {
         my $FDFileName = $thisNet.'FD';
         eval "\$oldNetInfo->{'FD'} = \*$FDFileName;";
         if( $@ ) {print "$@\n";}
      }

# set which nets are linked by default.
      foreach my $thisLink ( split( /,/, $oldNetInfo->{'links'} ) ) {
         botNetSetLinked($myNets, $thisNet, $thisLink);
      }
   }
   return 1;
}

# subroutine botNetCloseAll(botNetsData, $message)
#   This function closes all connections and sets
# them passive. 

sub botNetCloseAll($$) {
   my $myNets=$_[0];
   my $message=$_[1];

   foreach my $reqNet ( botNetList($myNets) ) {                  
      next if ! botNetIsOn($myNets, $reqNet);
      botNetQuit($myNets, $reqNet, $message);
      botNetSetPassive($myNets,$reqNet);
      botNetUserClear($myNets,$reqNet);
      botNetLog($myNets, $reqNet, $message);
      botNetClose($myNets,$reqNet);
   }
}

# subroutine: @botNet botNetList(botNetsData)
#    This function takes an botNetsData structure
# and returns a list of active botNet names for the 
# bot networks described in the NetsData parameter.

sub botNetList($) {
   my $botNetsData = shift;
   my @mylist;
   foreach my $myNet (keys %{$botNetsData}) {
      if( botNetIsActive($botNetsData,$myNet) ){
         push @mylist, $myNet;
      }
   }
   return @mylist;
}

# subroutine: @botNet botNetBroadcastList(botNetsData, botNet)
#    This function takes an botNetsData structure
# and returns a list of botNet strings for the 
# bot networks described in the NetsData that are
# both On and linked to the given net.

sub botNetBroadcastList($$) {
   my $botNetsData = shift;
   my $currNet = shift;

   my @mylist;
   foreach my $thisNet (keys %{$botNetsData}) {
      if( botNetIsLinked($botNetsData,$thisNet,$currNet) && 
          botNetIsOn($botNetsData,$thisNet)  ){
          push @mylist, $thisNet;
      }
   }
   return @mylist;
}

sub botNetFD($$) {
   my $botNetsData = $_[0];
   my $callingNet = $_[1];

   my $netData = botNetInfo($botNetsData, $callingNet);
   if( defined $netData ) {
      return $netData->{FD};
   }
   return undef;
}

#subroutine: botNetInfo botNetInfo(botNetsData, botNet)
sub botNetInfo($$) {
   my $botNetsData = $_[0];
   my $callingNet = $_[1];
   if( defined $botNetsData->{$callingNet} ) {
      return $botNetsData->{$callingNet};
   }
   return undef;
}

#subroutine: UserPerms botNetPerms(botNetsData, botNet)
# this returns a user permissions structure related to
# the the specified botNet.
sub botNetPerms($$) {
   my $botNetsData = $_[0];
   my $callingNet = $_[1];

   my $thisNetData = botNetInfo($botNetsData,$callingNet);
   if( defined $thisNetData ) {
      return $thisNetData->{'PERMS'};
   }
   return undef;
}

# subroutine: botNetOpen(botNetsData, botNet)
# takes an botNetsData structure and attempts to
# make a connection to a server in that net. If
# successful then the FD typeglob of the botNetInfo
# structure within the botNetsData structure is
# associated with the newly opened socket and the 
# function returns true.  if the call fails, 
#  the function returns 0;
sub botNetOpen($$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $result;

   my $thisNetData = botNetInfo($botNetsData,$botNet);
   my $callbacks = $thisNetData->{'callbacks'};
   
   if( defined( $thisNetData->{LOGFD} ) ){
      my $logFile = $thisNetData->{logfile};
      open(
         $thisNetData->{LOGFD},
	 ">>".$ENV{LOGPATH}.'/'.($logFile?$logFile:$botNet)
      );
   }
   if( defined( $callbacks->{'FUNC-OPEN'} ) ){
      $result = &{$callbacks->{'FUNC-OPEN'}}($botNetsData, $botNet);
      if( $result ) { botNetSetOn($botNetsData, $botNet); }
   }

   return $result;;
}

sub botNetConnect($$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $result;

   my $thisNetData = botNetInfo($botNetsData,$botNet);
   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-CONNECT'} ) ){
      $result = &{$callbacks->{'FUNC-CONNECT'}}($botNetsData, $botNet);
   }
   return $result;
}

sub botNetJoin($$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $result;

   my $thisNetData = botNetInfo($botNetsData,$botNet);
   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-JOIN'} ) ){
      $result = &{$callbacks->{'FUNC-JOIN'}}($botNetsData, $botNet);
   }
   return $result;
}

sub botNetLeave($$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $result;

   my $thisNetData = botNetInfo($botNetsData,$botNet);
   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-LEAVE'} ) ){
      $result = &{$callbacks->{'FUNC-LEAVE'}}($botNetsData, $botNet);
   }
   return $result;
}

# subroutine: botNetLog(botNetsData, botNet, message)
sub botNetLog($$$){
   my $botNetsData = shift;
   my $botNet = shift;
   my $logString = shift; 

   if( defined( $botNetsData->{$botNet}->{LOGFD} ) ){
      print {$botNetsData->{$botNet}->{LOGFD}} "$logString\n";
   }
}

# subroutine: botNetClose(botNetsData, botNet)
# takes an botNetsData structure and attempts to
# close the connection associated with the 
# botNetInfo structure refered to by botNet

sub botNetClose($$) {
   my $botNetsData = shift;
   my $botNet = shift;

   my $result = undef;
   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-CLOSE'} ) ){
      $result = &{$callbacks->{'FUNC-CLOSE'}}($botNetsData, $botNet);
   }

   if( defined( $botNetsData->{$botNet}->{LOGFD} ) ){
      close($botNetsData->{$botNet}->{LOGFD});
      botNetSetOff($botNetsData, $botNet);
   }
}

sub botNetEmote($$$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $victim = shift;
   my $message = shift;
   my $result = undef;

   if( $victim ) {
      botNetEmotePublic($botNetsData, $botNet, $message);
   } else {
      botNetEmotePrivate($botNetsData, $botNet, $victim, $message);
   }
}

sub botNetNotice($$$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $victim = shift;
   my $message = shift;
   my $result = undef;

   # If we have nonotice set on, never use notices.
   if( $botNetsData->{$botNet}->{'nonotice'} ) {
      botNetEmote($botNetsData, $botNet, $victim, $message);
      return;
   }

   if( $victim ) {
      botNetNoticePrivate($botNetsData, $botNet, $victim, $message);
   } else {
      botNetNoticePublic($botNetsData, $botNet, $message);
   }
}


# subroutine: botNetEmotePublic(botNetsData, botNet, String)
# tries to send a emote message to a public 'room' on a network
sub botNetEmotePublic($$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $message = shift;
   my $result = undef;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-EMOTE-PUBLIC'} ) ){
      $result = &{$callbacks->{'FUNC-EMOTE-PUBLIC'}}(
         $botNetsData,
	 $botNet,
	 { 'payload' => $message }
      );
   }
   return $result;
}

sub botNetInvite($$$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $victim = shift;
   my $message = shift;

   my $result;
   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-INVITE'} ) ){
      $result = &{$callbacks->{'FUNC-INVITE'}}(
         $botNetsData,
         $botNet,
         $victim,
         $message
      );
   }
   return $result;
}


# subroutine: botNetNoticePublic(botNetsData, botNet, String)
# tries to send a notice message to an entire room on a network
sub botNetNoticePublic($$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $message = shift;
   my $result = undef;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-NOTICE-PUBLIC'} ) ){
      $result = &{$callbacks->{'FUNC-NOTICE-PUBLIC'}}(
         $botNetsData,
	 $botNet,
	 { 'payload' => $message }
      );
   }
   return $result;
}

# subroutine: botNetNoticePrivate(botNetsData, botNet, Victim, String)
# tries to send a notice message to a specific person on a network
sub botNetNoticePrivate($$$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $victim = shift;
   my $message = shift;

   my $result = undef;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-NOTICE-PRIVATE'} ) ){
      $result = &{$callbacks->{'FUNC-NOTICE-PRIVATE'}}(
         $botNetsData,
	 $botNet,
	 $victim, 
	 { 'payload' => $message }
      );
   }
   return $result;
}

# subroutine: botNetEmotePrivate(botNetsData, botNet, Victim, String)
# tries to send an emote message to a specific person on a net.
# to botNet
sub botNetEmotePrivate($$$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $victim = shift;
   my $message = shift;

   my $result = undef;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-EMOTE-PRIVATE'} ) ){
      $result = &{$callbacks->{'FUNC-EMOTE-PRIVATE'}}(
         $botNetsData,
	 $botNet,
	 $victim, 
	 { 'payload' => $message }
      );
   }
   return $result;
}

# subroutine: botNetQuit(botNetsData, botNet, $message)
# sends this network an applicable quit message
sub botNetQuit($$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $reason = shift;

   my $result = undef;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-QUIT'} ) ){
      $result = &{$callbacks->{'FUNC-QUIT'}}($botNetsData, $botNet, $reason);
   }
   return $result;
}

sub botNetParse($$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $botInput = shift;
   my $result;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-PARSE'} ) ){
      $result = &{$callbacks->{'FUNC-PARSE'}}($botNetsData, $botNet, $botInput);
   }
   return $result;
}

sub botNetInterpret($$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $parseOutput = shift;
   my $result;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-INTERPRET'} ) ){
      $result = &{$callbacks->{'FUNC-INTERPRET'}}(
         $botNetsData,
         $botNet,
         $parseOutput
      );
   }
   return $result;
}

# subroutine: botNetRead(botNetsData, botNet)
# tries to read from the TypeGlob in the botNetsData
# data structure refered to by botNet. 
sub botNetRead($$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $result;

   my $thisNetData = botNetInfo($botNetsData,$botNet);

   my $callbacks = $thisNetData->{'callbacks'};
   if( defined( $callbacks->{'FUNC-READ'} ) ){
      $result = &{$callbacks->{'FUNC-READ'}}($botNetsData, $botNet);
   }
   if( !defined($result) ) { 
      botNetClose($botNetsData, $botNet);
      return undef;
   }
   return $result;
}


sub botNetTimedEvents($) {
   my $botNetsData = shift;
   my $result;
   foreach my $netName ( botNetList($botNetsData) ) {
      my $thisNetData = botNetInfo($botNetsData,$netName);
      my $callbacks = $thisNetData->{'callbacks'};
      if( defined( $callbacks->{'FUNC-TIMEDEVENTS'} ) ){
         $result = &{$callbacks->{'FUNC-TIMEDEVENTS'}}(
            $botNetsData, $netName
	 );
      }
   }
   return $result;
}

sub botNetIdleEvents($) {
   my $botNetsData = shift;
   my $result;
   foreach my $netName ( botNetList($botNetsData) ) {
      my $thisNetData = botNetInfo($botNetsData,$netName);
      my $callbacks = $thisNetData->{'callbacks'};
      if( defined( $callbacks->{'FUNC-IDLEEVENTS'} ) ){
         $result = &{$callbacks->{'FUNC-IDLEEVENTS'}}(
            $botNetsData, $netName
	 );
      }
   }
   return $result;
}

# subroutine botNetEmoteWall(botNetsData, String)
# sends String to all bot networks within botNetsData using
# notice
sub botNetNoticeWall($$) {
   my $myNets=$_[0];
   my $wallMsg=$_[1];
   foreach my $thisNet (botNetList($myNets) ) {
      botNetNoticePublic($myNets, $thisNet, $wallMsg);
   }
}

# subroutine botNetEmoteWall(botNetsData, String)
# sends String to all bot networks within botNetsData using
# emote
sub botNetEmoteWall($$) {
   my $myNets=$_[0];
   my $wallMsg=$_[1];
   foreach my $thisNet (botNettList($myNets) ) {
      botNetEmotePublic($myNets, $thisNet, $wallMsg);
   }
}

# subroutine botNetWallEmoteExcept(botNetsData, botNet, String)
# sends String to all bot networks within botNetsData
# using emote except to botNet
sub botNetWallEmoteExcept {
   my $myNets=$_[0];
   my $exceptNet=$_[1];
   my $wallMsg=$_[2];
   foreach my $thisNet (botNetBroadcastList($myNets, $exceptNet) ) {
      if( $thisNet ne $exceptNet ) {
         botNetEmotePublic($myNets, $thisNet, $wallMsg);
      }
   }
}

# subroutine botNetWallNoticeExcept(botNetsData, botNet, String)
# sends String to all bot networks within botNetsData
# using emote except to botNet
sub botNetWallNoticeExcept {
   my $myNets=$_[0];
   my $exceptNet=$_[1];
   my $wallMsg=$_[2];
   foreach my $thisNet (botNetBroadcastList($myNets, $exceptNet) ) {
      if( $thisNet ne $exceptNet ) {
         botNetNoticePublic($myNets, $thisNet, $wallMsg);
      }
   }
}

# subroutine: botNetIsActive(botNetData, botNet)
sub botNetIsActive {
   my $myNets=$_[0];
   my $botNet=$_[1];
   return $myNets->{$botNet}->{'active'};
}

# subroutine: botNetIsOn(botNetData, botNet)
sub botNetIsOn {
   my $myNets=$_[0];
   my $botNet=$_[1];
   return $myNets->{$botNet}->{'ON'};
}

# subroutine: botNetIsLinked(botNetData, botNet, botNet)
sub botNetIsLinked {
   my $botNetsData = shift;
   my $botNet1 = shift;
   my $botNet2 = shift;

   my $linkData = $botNetsData->{$botNet1}->{LINKS};
   return $linkData->{$botNet2};
}

# subroutine: botNetSetLinked(botNetData, botNet, botNet)
sub botNetSetLinked {
   my $botNetsData = shift;
   my $botNet1 = shift;
   my $botNet2 = shift;

   $botNetsData->{$botNet1}->{LINKS}->{$botNet2}=1;
   $botNetsData->{$botNet2}->{LINKS}->{$botNet1}=1;
}

# subroutine: botNetSetUnLinked(botNetData, botNet, botNet)
sub botNetSetUnLinked {
   my $botNetsData = shift;
   my $botNet1 = shift;
   my $botNet2 = shift;

   delete $botNetsData->{$botNet1}->{LINKS}->{$botNet2};
   delete $botNetsData->{$botNet2}->{LINKS}->{$botNet1};
}

# subroutine: botNetSetActive(botNetData, botNet)
sub botNetSetActive {
   my $botNetsData = shift;
   my $botNet = shift;
   $botNetsData->{$botNet}->{'active'}=1;
}

# subroutine: botNetSetOn(botNetData, botNet)
sub botNetSetOn {
   my $botNetsData = shift;
   my $botNet = shift;
   $botNetsData->{$botNet}->{'ON'}=1;
}

# subroutine: botNetSetPassive(botNetData, botNet)
sub botNetSetPassive {
   my $botNetsData = shift;
   my $botNet = shift;
   $botNetsData->{$botNet}->{'active'}=0;
}

# subroutine: botNetSetOff(botNetData, botNet)
sub botNetSetOff {
   my $botNetsData = shift;
   my $botNet = shift;
   $botNetsData->{$botNet}->{'ON'}=0;
}

# botNetUser functions.
# these functions are used to interact with User stuff from
# an bot level... it just abstracts the stuff written in user.pl


# subroutine: botNetAddUser(botNetsData,botNet,User,Host,Uid,Status,Gecos)

sub botNetUserAdd {
   my $userHash = {
      'uid'   => $_[4],
      'host'  => $_[3],
      'status' => $_[5],
      'gecos' => $_[6],
   };

   $_[0]->{$_[1]}->{USERS}->{lc $_[2]} = $userHash;
}

# subroutine: botNetUserDel(botNetData,botNet,User)
sub botNetUserDel {
   delete $_[0]->{$_[1]}->{USERS}->{$_[2]};
}

# subroutine: botNetUserList(botNetData,botNet)
sub botNetUserList {
   keys %{$_[0]->{$_[1]}->{USERS}};
}

# subroutine: botNetUserClear(botNetData,botNet)
sub botNetUserClear {
   $_[0]->{$_[1]}->{USERS}=undef;
}

# subroutine: botNetUser(botNetData,botNet,User)
sub botNetUser {
   return $_[0]->{$_[1]}->{USERS}->{lc $_[2]};
}


# subroutine: botNetGetGreeting(botNetData, botNet, hostmask )

sub botNetGetGreeting {
   my %greetHash;
   my $greetFile = $_[0]->{$_[1]}->{'greetings'};
   tie( %greetHash,"NDBM_File", "$ENV{RUNPATH}/$greetFile", O_RDWR | O_CREAT, 0640)
      or return undef;
   my $myPerms = botNetPerms($_[0], $_[1]) or return undef; 
   my $myUser = usrHostmaskMatch($myPerms, $_[2] ) or return undef;
   my $result = $greetHash{$myUser};
   untie(%greetHash);
   return $result;
}


# subroutine: botNetSetGreeting(botNetData, botNet, hostmask, greet )

sub botNetSetGreeting {
   my %greetHash;
   my $greetFile = $_[0]->{$_[1]}->{'greetings'};
   tie( %greetHash,"NDBM_File", "$ENV{RUNPATH}/$greetFile", O_RDWR | O_CREAT, 0640)
       or return undef;
   my $myPerms = botNetPerms($_[0], $_[1]) or return undef; 
   my $myUser = usrHostmaskMatch($myPerms, $_[2] ) or return undef;
   $greetHash{$myUser}=$_[3];
   untie(%greetHash);
}

sub botNetUnsetGreeting {
   my %greetHash;
   my $greetFile = $_[0]->{$_[1]}->{'greetings'};
   tie( %greetHash,"NDBM_File", "$ENV{RUNPATH}/$greetFile", O_RDWR | O_CREAT, 0640)
       or return undef;
   my $myPerms = botNetPerms($_[0], $_[1]) or return undef; 
   my $myUser = usrHostmaskMatch($myPerms, $_[2] ) or return undef;
   delete $greetHash{$myUser};
   untie(%greetHash);
}

sub botNetSetQuote {
   my %quoteHash;
   my $quoteFile = $_[0]->{$_[1]}->{'quotes'};
   tie( %quoteHash, "NDBM_File", "$ENV{RUNPATH}/$quoteFile", O_RDWR | O_CREAT, 0640)
     or return undef;
   my $myPerms = botNetPerms($_[0], $_[1]) or return undef; 
   my $myUser = usrHostmaskMatch($myPerms, $_[2] ) or return undef;
   if( ! defined($quoteHash{'0'}) ) {
      $quoteHash{'0'}=0;
   }
   my $quoteno = ++$quoteHash{'0'};
   $quoteHash{$quoteHash{'0'}}=$_[3];
   untie(%quoteHash);
   return $quoteno;
}

sub botNetGetQuote {
   my %quoteHash;
   my $quoteFile = $_[0]->{$_[1]}->{'quotes'};
   tie( %quoteHash,"NDBM_File", "$ENV{RUNPATH}/$quoteFile", O_RDWR | O_CREAT, 0640)
     or return undef;
   my $myPerms = botNetPerms($_[0], $_[1]) or return undef; 
   my $myUser = usrHostmaskMatch($myPerms, $_[2] ) or return undef;

   my $quote = $_[3];
   if( ! $quote ) {
      $quote = int(rand($quoteHash{'0'})+1);
   }

   my $result = $quoteHash{$quote};
   untie(%quoteHash);
   return $result;
}

my %callbackHash = ( 
   'open' => 	'FUNC-OPEN',
   'close'=> 	'FUNC-CLOSE',
   'read' => 	'FUNC-READ',
   'parse'=> 	'FUNC-PARSE',
   'interpret' => 'FUNC-INTERPRET',
   'quit' => 	'FUNC-QUIT',
   'join' => 	'FUNC-JOIN',
   'leave'=> 	'FUNC-LEAVE',
   'emote-public' => 'FUNC-EMOTE-PUBLIC',
   'emote-private'=> 'FUNC-EMOTE-PRIVATE',
   'notice-public'=> 'FUNC-NOTICE-PUBLIC',
   'notice-private'=> 'FUNC-NOTICE-PRIVATE',
   'process-invite'=> 'FUNC-PROCESS-INVITE',
   'process-join'  => 'FUNC-PROCESS-JOIN',
   'process-leave' => 'FUNC-PROCESS-LEAVE',
   'process-quit'  => 'FUNC-PROCESS-QUIT',
   'process-kick'  => 'FUNC-PROCESS-QUIT',
   'process-namechange' => 'FUNC-PROCES-NAMECHANGE',
   'process-modechange' => 'FUNC-PROCESS-MODECHANGE',
   'process-notice' => 'FUNC-PROCESS-NOTICE',
   'process-emote' => 'FUNC-PROCESS-EMOTE'
);

sub botNetLocalCmdDispatch($$$$$) {
   my $botNetsData = shift;
   my $botNet = shift;
   my $userHostMask = shift;
   my $victim = shift;
   my $inStr = shift;

   localInterpret($botNetsData, $botNet, $userHostMask, $victim, $inStr);
}

