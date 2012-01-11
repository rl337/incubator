use strict;

do "$ENV{SRCPATH}/socks.pl";

my $version="0.01";
my $tocVersion="1.0";
my $roastingKey="Tic/Toc";

my %aimMessageTypes = (
   'signon' => 1,
   'data' => 2,
   'error' => 3,
   'signoff' => 4,
   'keepalive'=> 5,
   1 => 'signon',
   2 => 'data',
   3 => 'error',
   4 => 'signoff',
   5 => 'keepalive'
);

# $string aimPasswdRoast($passwd, $roastString)
# this takes a password and roasting string and does
# a "roast" on the password. This is done by simply
# XORing then converting to hex.
sub aimPasswdRoast($$) {
   my ($passwd, $roast) = @_;
   my $index=0;
   my $result;
   while( $index lt length $passwd ) {
      my $a = substr($passwd,$index,1);
      my $b = substr($roast, $index%length($roast),1);
      $result.=unpack('H2', $a^$b);
      $index++;
   }
   return "0x$result";
}

sub aimSFLAPParse($) {
   my( $id, $type, $seq, $len, $data ) = unpack("CCnnA*", $_[0] );

   return { id=>$id, type=>$type, seq=>$seq, len=>$len, payload=>$data };
}

sub aimSFLAPAssemble($$$$) {
   my ($myNets, $thisNet, $type, $data) = @_;
   my $format;
   my $netData = botNetInfo($myNets, $thisNet);
   my $buffer;

   if ($type == $aimMessageTypes{'data'}) {
      $buffer=pack("CCnn",ord("*"),$type,$netData->{SEQ}++,length($data)+1);
      $buffer.=$data;
      $buffer.=pack("C",chr(0));
   } else { 
      $buffer=pack("CCnn",ord("*"),$type,$netData->{SEQ}++,length($data));
      $buffer.=$data;
   }
   return $buffer;
}


sub aimOpen($$) { 
   my $myNets = shift;
   my $thisNet = shift;

   my $netData = botNetInfo($myNets, $thisNet);

   my $host = $netData->{'server'};
   my $port = $netData->{'port'};
   my $result = sockOpen( $netData->{'FD'}, $host, $port);

   if( $result ) {
      botNetLog($myNets,$thisNet,
         "Opening an SFLAP connection to $host:$port"
      );
   } else {
      botNetLog($myNets,$thisNet,
         "could not open connection to $host:$port"
      );
   }
   return $result;
}

sub aimClose($$) { 
   my $myNets = shift;
   my $thisNet = shift;

   my $netData = botNetInfo($myNets, $thisNet);

   botNetLog($myNets, $thisNet, "Closing SFLAP connection");
   return sockClose( $netData->{'FD'});
}

sub aimConnect($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   $netData->{SEQ}=0;
   $netData->{'FLAPON'} = 0;
   aimWrite($myNets,$thisNet,$aimMessageTypes{'signon'},"FLAPON\r\n\r\n");
   $netData->{'FLAPON'} = 1;
   $netData->{'MSGQUEUE'} = [ ];
   $netData->{'LASTSEND'} = 0;
   $netData->{'THROTTLE'} = 0;
}

sub aimWrite($$$$) {
   my $myNets=shift;
   my $thisNet=shift;
   my $myType = shift;
   my $myData = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   if( $netData->{'FLAPON'} ) {

   # if we're in SFLAP mode, then we queue it up and dispatch it later.
      my $SFLAPData = aimSFLAPAssemble($myNets, $thisNet, $myType, $myData);
      my $flapInfo = aimSFLAPParse($SFLAPData);
      sockWrite(
         $netData->{'FD'},
         $SFLAPData,
         $flapInfo->{'len'}+6
      );
   } else {
      return sockWrite($netData->{'FD'}, $myData, -1 );
   }
}

sub aimRead($$) { 
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets, $thisNet);

   my $aimStruct;
   if( $netData->{FLAPON} ) {
      my $header = sockRead( $netData->{'FD'}, 6 );
      if( ! $header ) { return undef; }
      $aimStruct = aimSFLAPParse($header);
      $aimStruct->{'payload'} = sockRead($netData->{'FD'}, $aimStruct->{'len'});
   } else {
      my $inStr = sockRead($netData->{'FD'}, -1);
      if( ! $inStr ) { return undef; }
      $aimStruct->{'payload'} = $inStr;
   }
   return $aimStruct;
}

sub aimQuit($$) {
   my $myNets = shift;
   my $thisNet = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $channel = $netData->{'channel'};
   return aimWrite (
      $myNets, 
      $thisNet,
      $aimMessageTypes{'signoff'},
      ""
   );
}

sub aimJoin($$) {
   my $myNets = shift;
   my $thisNet = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $channel = $netData->{'channel'};
   return aimWrite (
      $myNets, 
      $thisNet,
      $aimMessageTypes{'data'},
      "toc_chat_join 4 \"".aimEncode($channel)."\""
   );
}

sub aimLeave($$) {
   my $myNets = shift;
   my $thisNet = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $channel = $netData->{'CHATROOMID'};
   return aimWrite (
      $myNets, 
      $thisNet,
      $aimMessageTypes{'data'},
      "toc_chat_leave $channel"
   );
}

sub aimInvite($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $victim = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $channel = $netData->{'CHATROOMID'};
   return aimWrite (
      $myNets, 
      $thisNet,
      $aimMessageTypes{'data'},
      "toc_chat_invite $channel \"".
         aimEncode($message).
	 "\" \"".
         aimEncode($victim).
	 "\""
   );
}

sub aimEmotePublic($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};
   push @{$netData->{'MSGQUEUE'}}, aimEncode($payload);
   return 1;
}


sub aimEmotePrivate($$$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $victim = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};
   my $channel = $netData->{'channel'};
   return aimWrite (
      $myNets, 
      $thisNet,
      $aimMessageTypes{'data'},
      "toc_send_im $victim \"".aimEncode($payload)."\""
   );
}

sub aimNoticePublic($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};

   push @{$netData->{'MSGQUEUE'}}, aimEncode($payload);
   return 1;
}

sub aimNoticePrivate($$$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $victim = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};
   my $channel = $netData->{'channel'};
   return aimWrite (
      $myNets, 
      $thisNet,
      $aimMessageTypes{'data'},
      "toc_send_im $victim \"".aimEncode($payload)."\""
   );
}

sub aimParse($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $readInput = shift;
   my $payload;

   my $netData = botNetInfo($myNets,$thisNet);

   if( $readInput->{'type'} ne $aimMessageTypes{'data'} ) {
      $payload = "FLAP SIGNON";
#      $netData->{'SEQ'} = $readInput->{'seq'};
   } else {
      $payload = $readInput->{'payload'};
   }
   my ($tocCmd,$rest) = split(/:/,$payload,2);
   $rest =~s/([^<]*)<[^>]*>/$1/g;
   my $cmdstruct = {
      CMD=>$tocCmd,
      BODY=>$rest,
      RAW=>$readInput->{'payload'}
   };

   return $cmdstruct;
}

sub aimCmdSignOn($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   aimWrite(
      $myNets,
      $thisNet,
      $aimMessageTypes{'data'}, 
      "toc_init_done"
   );
   aimWrite(
      $myNets,
      $thisNet,
      $aimMessageTypes{'data'}, 
      "toc_add_permit "
   );
   aimWrite(
      $myNets,
      $thisNet,
      $aimMessageTypes{'data'}, 
      "toc_add_deny "
   );
   aimJoin($myNets, $thisNet);
}

sub aimCmdFlapSignon {
   my $myNets = shift;
   my $thisNet = shift;
   my $InputStruct = shift;
   my ($auth, $port, $nick, $passwd, $language, $version);

   my $netData = botNetInfo($myNets,$thisNet);
   $auth = $netData->{'auth'};
   $port = $netData->{'authport'};
   $language = $netData->{'language'};
   $version = $netData->{'version'};
   $nick = $netData->{'nick'};
   $passwd = aimPasswdRoast($netData->{'password'}, $netData->{'roast'});
   aimWrite(
      $myNets,
      $thisNet,
      $aimMessageTypes{'signon'}, 
      pack("Nnna*", 1, 1, length($nick), $nick)
   );

   my $signonStr = "".
      "toc_signon $auth $port $nick $passwd $language ".
         "\"". aimEncode('muxbot') . "\""
   ;
   aimWrite(
      $myNets,
      $thisNet,
      $aimMessageTypes{'data'},
      $signonStr
   );
}

sub aimCmdConfig($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

}

sub aimCmdNick($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $inStr = $message->{'BODY'};
   my $channel = $netData->{'channel'};

   $netData->{'NICK'} = $inStr;
}

sub aimCmdIM($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
   my $cmdPrefix;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'BODY'};
   my $channel = $netData->{'channel'};

   if( defined $netData->{'prefix'} ) {
      $cmdPrefix=$netData->{'prefix'};
      $cmdPrefix=~s/([\W])/\\$1/g;
   } else {
      $cmdPrefix='!';
   }
   my ($who, $auto, $localStr) = split(/:/,$payload,3);
   if ( eval "\$localStr=~/^$cmdPrefix/;" ) {
      botNetLocalCmdDispatch(
         $myNets, $thisNet, "$who\!USER\@AIM", $who, $localStr
      );
   } else {
      botNetEmotePrivate($myNets, $thisNet, $who,
         "I am a robot, a faceless automata.  Talking to me is useless ".
	 "unless you enjoy reading the same message, namely this message ".
	 "over and over and over again.  For help with commands, you can ".
	 "issue the command: ".$cmdPrefix."help to get a list of commands ".
	 "that i accept.  Remember to prefix any command to me with a ".
	 $cmdPrefix
      );
   }
}

sub aimCmdBuddy($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
}

my $aimErrorCodes =  {
   '901' => '$params[0] not currently available',
   '902' => 'Warning of $1 not currently available',
   '903' => 'A message has been dropped, you are exceeding the '.
            'server speed limit',
   '911' => 'Error validating input',
   '912' => 'Invalid account',
   '913' => 'Error encountered while processing request',
   '914' => 'Service unavailable',
   '950' => 'Chat in $params[0] is unavailable',
   '960' => 'You are sending messages too fast to $params[1]',
   '961' => 'You missed an IM from $params[0] because it was too big',
   '962' => 'You missed an IM from $params[0] because it was sent too fast',
   '970' => 'Failure',
   '971' => 'Too many matches',
   '972' => 'Need more qualifiers',
   '973' => 'Dir service temporarily unavailable',
   '974' => 'Email lookup restricted',
   '975' => 'Keyboard ignored',
   '976' => 'No Keywords',
   '977' => 'Language not supported',
   '978' => 'Country not supported',
   '979' => 'Failure unknown, $param[0]',
   '980' => 'Incorrect nickname or password',
   '981' => 'The service is temprarily unavailable',
   '982' => 'Your warning level is currently too high to sign on',
   '983' => 'You have been connecting and disconnecting too frequently '.
            'wait 10 minutes and try again.',
   '989' => 'An unknown signon error has occured, $param[0]'
};

sub aimCmdError($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
 
   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'BODY'};

   my ($errorCode, @params) = split(/:/, $payload);
   if( $aimErrorCodes->{$errorCode} ) {
      my $result;
      if( ! $params[0] ) { $params[0] = "Unknown"; }
      eval "\$result = \"$aimErrorCodes->{$errorCode}\";";
      botNetLog($myNets, $thisNet, $result);
      botNetWallEmoteExcept(
         $myNets,
         $thisNet,
         "<$thisNet> Error: $result"
      );
   }
}

sub aimCmdEviled($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
 
   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'BODY'};
   my ($evilLevel, $who) = split(/:/, $payload);
   if( ! $who ) { $who = "anonymous"; }
   botNetLog($myNets, $thisNet, "Evil Level increased to $evilLevel by $who");
}

sub aimCmdChatJoin($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $payload = $message->{'BODY'};
   my ($chatID, $name) = split(/:/,$payload,2);
   my $netData = botNetInfo($myNets,$thisNet);

   $netData->{CHATROOMID} = $chatID;
   $netData->{JOINED} = 1;
   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "<$thisNet> entered the room."
   );
}

sub aimCmdChatLeft($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'BODY'};

   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "<$thisNet> got a CHAT_LEFT message, attempting a rejoin in ~5 seconds"
   );
   botNetLog($myNets, $thisNet, "Recieved a CHAT_LEFT. Rejoining.");
   botNetUserClear($myNets,$thisNet);
   $netData->{JOINED} = 0;
}

sub aimCmdChatIn($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $cmdPrefix;

   my $payload = $message->{'BODY'};
   my ($chatID, $who, $auto, $chatStr) = split(/:/,$payload,4);
   my $netData = botNetInfo($myNets,$thisNet);

   if ($who eq $netData->{'nick'} ) {
      return;
   }

   if( defined $netData->{'prefix'} ) {
      $cmdPrefix=$netData->{'prefix'};
      $cmdPrefix=~s/([\W])/\\$1/g;
   } else {
      $cmdPrefix='!';
   }
   
   if ( eval "\$chatStr=~/^$cmdPrefix/;" ) {
      botNetLocalCmdDispatch(
         $myNets, $thisNet, "$who\!USER\@AIM", "", $chatStr
      );
   } else {
      botNetWallEmoteExcept(
         $myNets,
         $thisNet,
         "<$thisNet:$who> $chatStr"
      );
   }
}

sub aimCmdChatUpdateBuddy($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $payload = $message->{'BODY'};
   my ($chatID,$inside,@list) = split(/:/,$payload);

   my $netData = botNetInfo($myNets,$thisNet);

   my $names = join(', ', @list);
   if( $inside eq 'T' ) {
      botNetWallEmoteExcept(
         $myNets,
         $thisNet,
         "$thisNet: $names joined the AIM Chat Room."
      );
      foreach my $nick (@list) {
         aimCmdOnJoin($myNets, $thisNet,"$nick\!USER\@AIM");
         botNetUserAdd($myNets, $thisNet, $nick, "AIM", $nick, "", "");
      }
   } else {
      botNetWallEmoteExcept(
         $myNets,
         $thisNet,
         "$thisNet: $names left the AIM Chat Room."
      );
      foreach my $nick (@list) {
         botNetUserDel($myNets, $thisNet, $nick);
      }
   }
}

sub aimCmdChatInvite($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
}

sub aimCmdGotoURL($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
}

sub aimCmdDirStatus($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
}

sub aimCmdAdminNickStatus($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
}

sub aimCmdAdminPasswdStatus($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
}

sub aimCmdRvousPropose($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
}

my $aimTerminals = {
   'SIGN_ON' => \&aimCmdSignOn,
   'FLAP SIGNON' => \&aimCmdFlapSignon,
   'CONFIG'  => \&aimCmdConfig,
   'NICK'    => \&aimCmdNick,
   'IM_IN'   => \&aimCmdIM,
   'UPDATE_BUDDY' => \&aimCmdBuddy,
   'ERROR'   => \&aimCmdError,
   'EVILED'  => \&aimCmdEviled,
   'CHAT_JOIN' => \&aimCmdChatJoin,
   'CHAT_IN' => \&aimCmdChatIn,
   'CHAT_UPDATE_BUDDY' => \&aimCmdChatUpdateBuddy,
   'CHAT_INVITE' => \&aimCmdChatInvite,
   'CHAT_LEFT' => \&aimCmdChatLeft,
   'GOTO_URL' => \&aimCmdGotoURL,
   'DIR_STATUS' => \&aimCmdDirStatus,
   'ADMIN_NICK_STATUS' => \&aimCmdAdminNickStatus,
   'ADMIN_PASSWD_STATUS' => \&aimCmdAdminPasswdStatus,
   'RVOUS_PROPOSE' => \&aimCmdRvousPropose
};

sub aimInterpret($$$) {
   my $myNets=shift;
   my $thisNet=shift;
   my $parseOutput=shift;
   my $myCmd = $parseOutput->{CMD};
   if( defined( $aimTerminals->{$myCmd} ) ){
      &{$aimTerminals->{$myCmd}}($myNets,$thisNet,$parseOutput);
   } else {
      botNetLog($myNets, $thisNet, $parseOutput->{RAW});
   }
}


sub aimEncode($){
   my $inString = $_[0];

   $inString=~s/([\\{}\(\)\[\]\$\"])/\\$1/g;
   return $inString;
}

sub aimTimedEvents{
   my $myNets=shift;
   my $thisNet=shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $now = time();
   if( ($now - $netData->{LASTCHECK}) > 5 ) {
      if( $netData->{JOINED} < 1 ) {
         botNetWallEmoteExcept(
            $myNets,
            $thisNet,
            "<$thisNet> attempting to rejoin room."
         );
         aimJoin($myNets, $thisNet);
         botNetLog($myNets,$thisNet,
            "Attempting to rejoin."
         );
      }
      $netData->{LASTCHECK} = $now;
   }
   if( ($now - $netData->{TIMECHECK}) > 3600 ) {
      botNetLog($myNets,$thisNet,
         "Another hour. $now"
      );
      $netData->{TIMECHECK} = $now;
   }
}

my %aimThrottleValues = (
   1 => 0, 2 => 0, 3 => 1,
   4 => 1, 5 => 1, 6 => 2,
   7 => 3, 8 => 4, 9 => 5,
  10 => 5
);

sub aimIdleEvents{
   my $myNets=shift;
   my $thisNet=shift;
   my $netData = botNetInfo($myNets,$thisNet);


# throttle messages being sent to the TOC server.
   my $now = time();
   my $currThrottle = $netData->{THROTTLE};
   my $chatID = $netData->{CHATROOMID};

   my $queueSize = scalar @{$netData->{MSGQUEUE}};
   if( $queueSize ) {
      if( (($netData->{LASTSEND}+$aimThrottleValues{$currThrottle}) < $now) ){
         if( ($currThrottle < 10)  && ($queueSize < 10 ) ) {
            my $queueEntry = shift @{$netData->{MSGQUEUE}};
            aimWrite(
               $myNets,
               $thisNet,
               $aimMessageTypes{'data'},
               "toc_chat_send $chatID \"".aimEncode($queueEntry)."\""
            );
            $netData->{THROTTLE}++; 
         } else {
	    my $dumpStr="Hi-Traffic Dump: ";
            while( scalar @{$netData->{MSGQUEUE}} ) {
	       my $addStr = aimEncode($netData->{MSGQUEUE}->[0]);
	       last if ( length( $dumpStr.$addStr."\n" )+32 > 2048 );
	       $dumpStr.="\n".$addStr;
	       shift @{$netData->{MSGQUEUE}};
            }
            aimWrite(
               $myNets,
               $thisNet,
               $aimMessageTypes{'data'},
               "toc_chat_send $chatID \"$dumpStr\""
            );
	 }
         $netData->{LASTSEND} = $now;
      }
   }  
   if( ($now - $netData->{LASTSEND} ) > 5 ) {
      $netData->{LASTSEND} = $now;
      $netData->{THROTTLE}-=($currThrottle>0)?1:0;
   }
}


sub aimCreateCallbacks {
   return {
      'FUNC-OPEN'=> \&aimOpen,
      'FUNC-CLOSE'=> \&aimClose,
      'FUNC-READ'=> \&aimRead,
      'FUNC-PARSE'=> \&aimParse,
      'FUNC-INTERPRET'=> \&aimInterpret,
      'FUNC-CONNECT'=> \&aimConnect,
      'FUNC-QUIT'=> \&aimQuit,
      'FUNC-JOIN'=> \&aimJoin,
      'FUNC-LEAVE'=> \&aimLeave,
      'FUNC-INVITE'=> \&aimInvite,
      'FUNC-TIMEDEVENTS'=> \&aimTimedEvents,
      'FUNC-IDLEEVENTS'=> \&aimIdleEvents,
      'FUNC-EMOTE-PUBLIC'=> \&aimEmotePublic,
      'FUNC-EMOTE-PRIVATE'=> \&aimEmotePrivate,
      'FUNC-NOTICE-PUBLIC'=> \&aimNoticePublic,
      'FUNC-NOTICE-PRIVATE'=> \&aimNoticePrivate
   };
}

sub aimJoinGreeting {
   my $myNets = shift;
   my $callingNet = shift;
   my $userHostmask = shift;

   my $myPerms = botNetPerms($myNets, $callingNet);
   my $myUser = usrHostmaskMatch($myPerms, $userHostmask);
   my $netData = botNetInfo($myNets,$callingNet);
   my $channel = $netData->{channel};
   my $greeting = botNetGetGreeting($myNets, $callingNet, $userHostmask);
   if ( !$greeting ) { return ; }
   my ($nick, $rest) = split(/!/, $userHostmask);
   if( usrCheckFlag($myPerms, $myUser, 'G') ){
      aimEmotePublic(
         $myNets,
	 $callingNet,
         "[$myUser] $greeting"
      );
   }
}


my @aimOnJoinList = (
   \&aimJoinGreeting
);

sub aimCmdOnJoin($$$) {
   my $myNets = shift;
   my $callingNet = shift;
   my $userHostmask = shift;

   foreach my $myFnc ( @aimOnJoinList ) {
      eval { &{$myFnc}($myNets, $callingNet, $userHostmask); };
   }
}


