do "$ENV{SRCPATH}/socks.pl";


sub ircSendWho($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $channel = $netData->{channel};
   return sockWrite(
      $netData->{'FD'},
      "WHO $channel\r\n",
      -1
   );
}

#   These are the IRC Botnet Callback functions.
#   each of these corresponds to the required bot callbacks.

sub  ircOpen($$) { 
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $host = $netData->{'server'};
   my $port = $netData->{'port'};
   my $result = sockOpen($netData->{'FD'}, $host, $port);
   if( $result ) {
      botNetLog($myNets,$thisNet,"Opening an IRC connection to $host:$port");
   } else {
      botNetLog($myNets,$thisNet,"Could not open connection to $host:$port");
   }
   return $result;
}

sub ircClose($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);
   botNetLog($myNets,$thisNet,"Closing IRC connection.");
   return sockClose($netData->{'FD'});
}


# specifying a size less than 1 tells sockRead to read a line
# this returns a struct which has the data in 'payload'
sub ircRead($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = sockRead($netData->{'FD'}, -1);
   if( $payload ) {
      return { payload=>$payload };
   } 
   return undef;
}

# specifying a size less than 1 tells sockWrite to write the whole
# line.
sub ircConnect($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $nick = $netData->{'nick'};
   my $name = $netData->{'name'};
   my $uid = $netData->{'uid'};

   my $passwd = $netData->{'passwd'};

   my $passstr = "";
   if( $passwd ) {
	$passstr = "PASS $passwd\r\n";
   } 

   return sockWrite(
      $netData->{'FD'},
      $passstr."NICK $nick\r\nUSER $uid * * :$name\r\n",
      -1
   );
}

sub ircInvite($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $victim = shift;
   my $message = shift;
   my $netData = botNetInfo($myNets,$thisNet);
   my $channel = $netData->{'channel'};

   return sockWrite(
      $netData->{'FD'},
      "INVITE $victim $channel\r\n",
      -1
   );
}
sub ircJoin($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);
   my $key = "";

   my $channel = $netData->{'channel'};

   if( $netData->{'key'} ) {
	$key = " ".$netData->{'key'}." ";
   }
   return sockWrite(
      $netData->{'FD'},
      "JOIN $channel$key\r\n",
      -1
   );
}

sub ircQuit($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $reason = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   return sockWrite(
      $netData->{'FD'},
      "QUIT :$reason\r\n",
      -1
   );
}

sub ircLeave($$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $channel = $netData->{'channel'}; 
   return sockWrite(
      $netData->{'FD'},
      "PART $channel\r\n",
      -1
   );
}

sub ircEmotePublic($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};
   my $channel = $netData->{'channel'}; 
   return sockWrite(
      $netData->{'FD'},
      "PRIVMSG $channel :$payload\r\n",
      -1
   );
}

sub ircEmotePrivate($$$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;
   my $victim = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};
   my $channel = $netData->{'channel'}; 
   return sockWrite(
      $netData->{'FD'},
      "PRIVMSG $victim :$payload\r\n",
      -1
   );
}

sub ircNoticePublic($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};
   my $channel = $netData->{'channel'}; 
   return sockWrite(
      $netData->{'FD'},
      "NOTICE $channel :$payload\r\n",
      -1
   );
}

sub ircNoticePrivate($$$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $victim = shift;
   my $message = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my $payload = $message->{'payload'};
   my $channel = $netData->{'channel'}; 
   return sockWrite(
      $netData->{'FD'},
      "NOTICE $victim :$payload\r\n",
      -1
   );
}

# Parse an IRC message.
#
# Creates a data structure as follows:
# %cmdstruct
#    FRM => $(nick|server[!username[@hostname]])
#    NICK=> nick
#    CMD => $irc_command
#    PRM => @parameters
#    RAW => $raw_irc_message

sub ircParse($$$) { 
   my $myNets = shift;
   my $thisNet = shift;
   my $readInput = shift;

   my $inStr = $readInput->{'payload'};
   
   $inStr=~s/[\n\r]*$//g; 
   my @myline = split(/ /,$inStr);
   my $cmdstruct = { };
   my @params = ( ); 
   my ($mystr,$myRaw);
   $myRaw = $inStr;

   $mystr = shift @myline;
   if( substr($mystr, 0, 1) eq ":" ) {
      $cmdstruct->{FRM}=substr($mystr,1);
      ($cmdstruct->{NICK},$address)=split(/!/,$cmdstruct->{FRM});
      $cmdstruct->{CMD}=shift @myline;
   } else {
      $cmdstruct->{CMD}=$mystr;
   } 
   while ( $mystr = shift @myline) {
      last if ( substr($mystr,0,1) eq ":" ); 
      push @params, $mystr;
   } 
   if ( substr($mystr,0,1) eq ":" ) {
      $mystr=substr($mystr,1);
   } 
   for (@myline) {
      $mystr = sprintf("%s %s", $mystr, $_);
   }

   push(@params, $mystr);
   $cmdstruct->{PRM}=\@params;
   $cmdstruct->{RAW}=$myRaw;

   return $cmdstruct;
}

# Anything that begins ircCmd is a locally dispatched function.
# they are called directly from the ircInterpret callback


sub ircCmdPing($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;

   my $netData = botNetInfo($myNets,$thisNet);
   my @param = @{$ircMsg->{PRM}};
   my $reply = $param[0];
   sockWrite(
      $netData->{'FD'},
      "PONG $reply\r\n",
      -1
   );
}

sub ircCmdNick($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my @param = @{$ircMsg->{PRM}};
   my $oldNick = $ircMsg->{'NICK'};
   my $newNick = $ircMsg->{PRM}[0];
   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "<$thisNet> $oldNick is now known as $newNick."
   );
   botNetUserDel($myNets, $thisNet, $oldNick);
   ircSendWho($myNets,$thisNet);
}

sub ircCmdJoin($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $nick = $ircMsg->{'NICK'};
   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "$thisNet: $nick has joined the channel."
   );

   ircCmdOnJoin($myNets, $thisNet, $ircMsg->{FRM});
   ircSendWho($myNets,$thisNet);
}

sub ircCmdPart($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $nick = $ircMsg->{'NICK'};
   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "$thisNet: $nick has left the channel."
   );

   ircCmdOnPart($myNets, $thisNet, $ircMsg->{FRM});
   botNetUserDel($myNets, $thisNet, $nick);
}

sub ircCmdQuit($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $nick = $ircMsg->{'NICK'};
   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "<$thisNet> $nick has quit."
   );

   ircCmdOnPart($myNets, $thisNet, $ircMsg->{FRM});
   botNetUserDel($myNets, $thisNet, $nick);
}

sub ircCmdTopic($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $nick = $ircMsg->{'NICK'};
   my $topic = $ircMsg->{PRM}[0];
   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "<$thisNet> $nick set the topic to $topic."
   );
}

sub ircCmdKick($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my $nick = $ircMsg->{'NICK'};
   my $victim = $ircMsg->{PRM}[1];
   botNetWallEmoteExcept(
      $myNets,
      $thisNet,
      "<$thisNet> $victim was kicked from the channel by $nick."
   );
   botNetUserDel($myNets, $thisNet, $victim);
}

sub ircCmdUserData($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

   my @userEntry=@{$ircMsg->{PRM}};
   my ($reqNick, $myChannel, $uid, $host, $server, $nick, $status, $gecos) = 
         @{$ircMsg->{PRM}};
   botNetUserAdd($myNets, $thisNet, $nick, $host, $uid, $status, $gecos);
}

sub ircCmdPrivmsg($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;
   my $netData = botNetInfo($myNets,$thisNet);

# figure out what prefix the bot uses to determine commands
   my $cmdPrefix;
   if( defined $netData->{'prefix'} ) {
      $cmdPrefix=$netData->{'prefix'};
      $cmdPrefix=~s/([\W])/\\$1/g;
   } else {
      $cmdPrefix='!';
   }
   my $localStr = $ircMsg->{PRM}[1];
   my $to = $ircMsg->{PRM}[0];
   my $nick = $ircMsg->{NICK};

   # If the bot origionally sent this message, ignore it.
   if( $nick eq $netData->{nick} ) {
      return;
   }

   if( $localStr=~/^\x01/ ) {
      ctcpInterpret($myNets, $thisNet, $ircMsg);
   } elsif ( eval "\$localStr=~/^$cmdPrefix/;" ) {
      my $recipient;
      if( $to eq $netData->{nick} ) {
         $recipient=$nick;
      } else {
         $recipient=$netData->{channel};
      }
      botNetLocalCmdDispatch(
         $myNets, $thisNet, $ircMsg->{FRM}, $recipient, $localStr
      );
   } elsif ( $to ne $netData->{nick} ) {
      botNetWallEmoteExcept(
         $myNets,
         $thisNet,
         "<$thisNet:$nick> $localStr"
      );
   }
}

sub ircTimedWhoRequest($$) {
   my $myNets=shift;
   my $thisNet=shift;
   ircSendWho($myNets, $thisNet);
}


sub ircTimedEvents($$) {
   my $myNets=shift;
   my $thisNet=shift;
   ircTimedWhoRequest($myNets, $thisNet);
}

sub ircIdleEvents($$) {
   my $myNets=shift;
   my $thisNet=shift;

}

sub ircCmdNop {

}

sub ircCmdInit($$$) {
   my $myNets = shift;
   my $thisNet = shift;
   my $ircMsg = shift;

   ircJoin($myNets, $thisNet);
}

my $ircTerminals = {
   'PING' => \&ircCmdPing,
   'PRIVMSG' => \&ircCmdPrivmsg,
   'JOIN' => \&ircCmdJoin,
   'PART' => \&ircCmdPart,
   'QUIT' => \&ircCmdQuit,
   'MODE' => \&ircCmdNop,
   'NICK' => \&ircCmdNick,
   'TOPIC' => \&ircCmdTopic,
   'KICK' => \&ircCmdKick,
   'NOTICE' => \&ircCmdNop,
   '001' => \&ircCmdInit,
   '352' => \&ircCmdUserData,
   '002' => \&ircCmdNop,
   '003' => \&ircCmdNop,
   '004' => \&ircCmdNop,
   '250' => \&ircCmdNop,
   '251' => \&ircCmdNop,
   '252' => \&ircCmdNop,
   '254' => \&ircCmdNop,
   '255' => \&ircCmdNop,
   '265' => \&ircCmdNop,
   '266' => \&ircCmdNop,
   '315' => \&ircCmdNop,
   '332' => \&ircCmdNop,
   '333' => \&ircCmdNop,
   '353' => \&ircCmdNop,
   '366' => \&ircCmdNop,
   '372' => \&ircCmdNop,
   '375' => \&ircCmdNop,
   '376' => \&ircCmdNop
};


sub ircInterpret($$$) {
   my $myNets=shift;
   my $thisNet=shift;
   my $parseOutput=shift;
   my $myCmd = $parseOutput->{CMD};
   if( defined( $ircTerminals->{$myCmd} ) ){
      &{$ircTerminals->{$myCmd}}($myNets,$thisNet,$parseOutput);
   } else {
      botNetLog($myNets, $thisNet, $parseOutput->{RAW});
   }
}

sub ircCreateCallbacks {
   return {
      'FUNC-OPEN'=> \&ircOpen,
      'FUNC-CLOSE'=> \&ircClose,
      'FUNC-READ'=> \&ircRead,
      'FUNC-PARSE'=> \&ircParse,
      'FUNC-INTERPRET'=> \&ircInterpret,
      'FUNC-CONNECT'=> \&ircConnect,
      'FUNC-QUIT'=> \&ircQuit,
      'FUNC-JOIN'=> \&ircJoin,
      'FUNC-INVITE'=> \&ircInvite,
      'FUNC-LEAVE'=> \&ircLeave,
      'FUNC-TIMEDEVENTS'=> \&ircTimedEvents,
      'FUNC-IDLEEVENTS'=> \&ircIdleEvents,
      'FUNC-EMOTE-PUBLIC'=> \&ircEmotePublic,
      'FUNC-EMOTE-PRIVATE'=> \&ircEmotePrivate,
      'FUNC-NOTICE-PUBLIC'=> \&ircNoticePublic,
      'FUNC-NOTICE-PRIVATE'=> \&ircNoticePrivate
   };
}

###### IRC OnJoin and OnPart stuff

sub ircJoinAutoOp {
   my $myNets = shift;
   my $callingNet = shift;
   my $userHostmask = shift;

   my $myPerms = botNetPerms($myNets, $callingNet);
   my $myUser = usrHostmaskMatch($myPerms, $userHostmask);
   my $netData = botNetInfo($myNets,$callingNet);
   my $channel = $netData->{channel};
   my ($nick, $rest) = split(/!/, $userHostmask);
   if( usrCheckFlag($myPerms, $myUser, 'A') ){
      sockWrite(
         $netData->{FD},
         "MODE $channel +o $nick\r\n",
	 -1
     );
   }
}

sub ircJoinAutoVoice {
   my $myNets = shift;
   my $callingNet = shift;
   my $userHostmask = shift;

   my $myPerms = botNetPerms($myNets, $callingNet);
   my $myUser = usrHostmaskMatch($myPerms, $userHostmask);
   my $netData = botNetInfo($myNets,$callingNet);
   my $channel = $netData->{channel};
   my ($nick, $rest) = split(/!/, $userHostmask);
   if( usrCheckFlag($myPerms, $myUser, 'V') ){
      sockWrite(
         $netData->{FD},
         "MODE $channel +v $nick\r\n",
	 -1
     );
   }
}

sub ircJoinGreeting {
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
      sockWrite(
         $netData->{FD},
         "PRIVMSG $channel :[$myUser] $greeting\r\n",
	 -1
     );
   }
}

my @ircOnJoinList = (
   \&ircJoinAutoOp,
   \&ircJoinAutoVoice,
   \&ircJoinGreeting
);

sub ircCmdOnJoin {
   my $myNets = shift;
   my $callingNet = shift;
   my $userHostmask = shift;

   foreach my $myFnc ( @ircOnJoinList ) {
      eval { &{$myFnc}($myNets, $callingNet, $userHostmask); };
   }
}

sub ircPartAutoReJoin {
   my $myNets = shift;
   my $callingNet = shift;
   my $userHostmask = shift;

   my ($thisNick,$rest) = split(/!/, $userHostmask);
   if( $thisNick eq $myNets->{$callingNet}->{'nick'} ) {
      ircJoin($myNets, $callingNet);
   }
}

my @ircOnPartList = (
   \&ircPartAutoReJoin
);

sub ircCmdOnPart {
   my $myNets = shift;
   my $callingNet = shift;
   my $userHostmask = shift;

   foreach my $myFnc ( @ircOnPartList ) {
      eval { &{$myFnc}($myNets, $callingNet, $userHostmask); };
   }
}


####       CTCP Handling stuff here.


#  ircCTCPPrivate( ircNetsData, ircNet, ircMsg, str )
#  Sends a CTCP message to the origionator of the message.

sub ircCTCPPrivate {
   my $myNets = $_[0];
   my $callingNet = $_[1];
   my $ircMsg = $_[2];
   my $sendTxt = $_[3];

   my $netData = botNetInfo($myNets,$callingNet);
   my $nick = $ircMsg->{NICK};
   return sockWrite(
      $netData->{'FD'},
      "PRIVMSG $nick :\x01$sendTxt\x01\r\n",
      -1
   );
}

sub ircCTCPReplyPrivate {
   my $myNets = $_[0];
   my $callingNet = $_[1];
   my $ircMsg = $_[2];
   my $sendTxt = $_[3];

   my $netData = botNetInfo($myNets,$callingNet);
   my $nick = $ircMsg->{NICK};
   return sockWrite(
      $netData->{'FD'},
      "NOTICE $nick :\x01$sendTxt\x01\r\n",
      -1
   );
}

my $ctcpHelpText = {
   'ping' => [
      'usage: ping',
      'report back the current time to help determine lag.'
   ],
   'version' => [
      'usage: version',
      'report back the make/model and current version of this client.'
   ],
   'help' => [
      'usage: help [<topic>]',
      'returns the help text for a given ctcp command or lists',
      'or returns a list of available help topics.'
   ],
   'action' => [
      'usage: action <text>',
      'sends the bot a CTCP action.'
   ]
};

sub ctcpVersion {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $ircMsg=$_[2];
   my $localStr=$_[3];

   my $result = "VERSION perl MuxBot by rlee\@tokyo3.com";
   ircCTCPReplyPrivate($myNets, $callingNet, $ircMsg, $result);
}

sub ctcpPing {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $ircMsg=$_[2];
   my $localStr=$_[3];

   ircCTCPReplyPrivate($myNets, $callingNet, $ircMsg, "PING $localStr");
}

sub ctcpAction {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $ircMsg=$_[2];
   my $localStr=$_[3];

   my $nick = $ircMsg->{NICK};
   botNetWallEmoteExcept(
      $myNets,
      $callingNet,
      "$callingNet: $nick $localStr"
   );
}

sub ctcpHelp {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $ircMsg=$_[2];
   my $localStr=$_[3];

   my ($reqHelp, $misc) = split(/ /,$localStr);

   my $result;
   if( !defined $ctcpHelpText->{$reqHelp} ) {
      $result="Help available on these commands: ".join(' ',keys %{$ctcpHelpText});
      ircCTCPReplyPrivate( $myNets, $callingNet, $ircMsg, $result );
   } else {
      foreach ( @{$ctcpHelpText->{$reqHelp}} ) {
         ircCTCPReplyPrivate( $myNets, $callingNet, $ircMsg, "HELP $_" );
      }
   }
}

my $ctcpTerminals = {
   'action' => \&ctcpAction,
   'version' => \&ctcpVersion,
   'ping' => \&ctcpPing,
   'help' => \&ctcpHelp
};

# subroutine: ctcpInterpret(botNetsData, botNet, botMsg)
# this takes the IRCMsg and breaks it down for local
# consumption. 

sub ctcpInterpret {
   my $myNets = $_[0];
   my $callingNet = $_[1];
   my $ircMsg = $_[2];
   my $ctcpStr = $ircMsg->{PRM}[1];

   $ctcpStr=~s/^\x01(.*)\x01$/$1/g;
   $ctcpStr=~s/[\n\r]*$//g;

   my ($ctcpCmd, $ctcpPrm) = split(/\s/,$ctcpStr,2);
   my $myCmd = lc $ctcpCmd;
   if( defined $ctcpTerminals->{$myCmd} ) {
         eval { &{$ctcpTerminals->{$myCmd}}($myNets,$callingNet,$ircMsg,$ctcpPrm); };
         if( $@ ) {
            ircNotice($myNets,$callingNet,$ircMsg,$@);
         }
   } else {
      ircCTCPReplyPrivate($myNets,$callingNet,$ircMsg,
         "Unknown CTCP command, use 'help' for a list of commands"
      );
   }
}
