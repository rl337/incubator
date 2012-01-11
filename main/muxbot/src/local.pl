do "$ENV{SRCPATH}/user.pl";
use strict;

my $helpText = {
   'help' => [
      'usage: help [command]',
      'Returns the help string for a command. '.
      'If no command is specified, list all available commands.'
   ],
   'who' => [
      'usage: who [network]',
      'Returns a list of users on a connected network. '.
      'If no network is specified, lists all connected networks.'
   ],
   'invite' => [
      'usage: invite',
      'invites you into the bot\'s room if you have access.'
   ],
   'link' => [
      'usage: link <network>',
      'tries to link the current network to another active net.',
      'linking allows for the forwarding of channel chatter between',
      'the linked networks.'
   ],
   'links' => [
      'usage: links [<network>]',
      'lists all links from specified network. If no network specified',
      'then lists all links from current network.'
   ],
   'unlink' => [
      'usage: unlink <network>',
      'tries to unlink the current network to another linked net.'
   ],
   'weather' => [
      'usage: weather <zipcode>',
      'gets the current weather report from weather.com.'
   ],
   'whois' => [
      'usage: whois <network> <nick>',
      'Returns a description of nick on a specified network.'
   ],
   'greet' => [
      'usage: greet [greeting]',
      'Sets your onJoin greeting, or reports it if no params'
   ],
   'getquote' => [
      'usage: getquote [quote number]',
      'gets a quote from the channel\'s quote list. if no number',
      'is specified, returns a random quote.'
   ],
   'newquote' => [
      'usage: newquote <text>',
      'sets a quote in the channel\'s quote list.'
   ],
   'emote' => [
      'usage: emote <text>',
      'sends <text> as a raw bot message to the server.'
   ],
   'calc' => [
      'usage: calc <expression>',
      'causes the bot to evaluate a math expression.'
   ],
   'connect' => [
      'usage: connect <network>',
      'Connects the bot to a specified network that is defined '.
      'in the bot configuration'
   ],
   'disconnect' => [
      'usage: disconnect <network>',
      'Disconnects a bot from a specified network.'
   ],
   'op' => [
      'usage: op [<nick>]',
      'gives <nick> ops, if no nick is specified, gives you ops'
   ],
   'voice' => [
      'usage: voice [<nick>]',
      'gives <nick> voice, if no nick is specified, gives you voice'
   ],
   'devoice' => [
      'usage: devoice [<nick>]',
      'removes voice from <nick> voice, if no nick is specified, devoices you.'
   ],
   'sign' => [
      'usage: sign [<zodiac sign>]',
      'reports the daily horoscope from Astrocenter.com for that sign.'
   ],
   'deop' => [
      'usage: deop [<nick>]',
      'revokes ops from <nick> if no nick is specified, deops you.'
   ],
   'ebay' => [
      'usage: ebay [hits,] <search>',
      'does a simple ebay search for desired items.',
      'Note that public requests are limited to 2 hits and',
      'requests in general are limited to max of 10 hits'
   ],
   'quote' => [
      'usage: quote <symbol>',
      'reports the current price of a stock from yahoo.'
   ],
   'dict' => [
      'usage: dict <word>',
      'does a dictionary lookup from the online',
      'WordNet (wn) database.'
   ],
   'udict' => [
      'usage: udict <phrase>',
      'Queries urbandictionary.com for a definition of the phrase',
   ],
   'perms' => [
      'usage: perms [nick]',
      'looks up your permissions, or the permissions',
      'of the specified nick.'
   ],
   'users' => [
      'usage: users',
      'lists what users are defined in',
      'the permissions file'
   ],
   '8ball' => [
      'usage: 8ball <string>',
      'ask the magic 8ball a question.'
   ],
   'die' => [
      'usage: die',
      'Causes bot to terminate'
   ],
   'rehash' => [
      'usage: rehash',
      'Causes bot to reload its configuration files'
   ],
   'deals' => [
      'usage: deals',
      'Searches slickdeals.net for the slickest deals',
      'that have come up today.',
   ],
   'translate' => [
      'usage: translate <from> <to> <text>',
      'Attempts to translate text through translate.google.com from',
      'language specified as <from> to the language specified by <to>'
   ],
   'time' => [
      'usage: quote [<timezone>]',
      'reports the current time of a given timezone.',
      'if no timezones are specified, displays time ',
      'local to the bot.'
   ]
};


sub localHelp {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($reqHelp, $misc) = split(/ /,$localStr);

   my $result;
   if( !defined $helpText->{$reqHelp} ) {
      $result="Help available on these commands: ".join(' ',sort keys %{$helpText});
      botNetNotice( $myNets, $callingNet, $victim, $result );
   } else {
      foreach ( @{$helpText->{$reqHelp}} ) {
         botNetNotice( $myNets, $callingNet, $victim,$_ );
      }
   }
}

sub localExec {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   if( $localStr ne "" ) {
      my $result = eval $localStr;
      if( $result ) {
         botNetNotice($myNets, $callingNet, $victim,"The exec returned: $result");
      }
   }
}

sub localInvite {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   botNetInvite(
      $myNets, 
      $callingNet,
      $victim,
      "For a good time..."
   );
}

sub localConnect {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($reqNet, $misc) = split(/ /,$localStr);

   if( defined botNetInfo($myNets,$reqNet) && !botNetIsActive($myNets,$reqNet)){
      botNetNotice($myNets,$callingNet,$victim,"Attempting a connect to $reqNet.");
      if( botNetOpen($myNets,$reqNet) ) {
         botNetConnect($myNets,$reqNet);
         botNetSetActive($myNets,$reqNet);
      } else {
         botNetNotice($myNets,$callingNet,$victim,"Connect to $reqNet failed.");
      }
   }
}

sub localLink {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($reqNet, $misc) = split(/ /,$localStr);

   if( defined botNetInfo($myNets,$reqNet) && 
           botNetIsActive( $myNets, $callingNet, $reqNet) && 
           $reqNet ne $callingNet && 
           !botNetIsLinked( $myNets, $callingNet, $reqNet)){
      botNetNotice($myNets,$callingNet,$victim,"Starting a link to $reqNet.");

      my $reqNetInfo = botNetInfo($myNets, $reqNet);
      botNetNotice( $myNets,
                 $reqNet,
                 {TO=>$reqNetInfo->{'channel'} },
                 "Starting a link to $callingNet."
      );
      botNetSetLinked($myNets,$reqNet,$callingNet);
   } else {
      botNetNotice($myNets,$callingNet,$victim,
         "Param must be a valid, active, and unlinked network."
      );
   }
}

sub localLinks {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($reqNet, $misc) = split(/ /,$localStr);

   if( ! $reqNet ) { 
      $reqNet = $callingNet;
   }

   my @links = botNetBroadcastList($myNets, $reqNet);

   if( scalar @links ) {
      botNetNotice($myNets,$callingNet,$victim,
         "$reqNet is linked to: ".join(' ', @links)
      );
   } else {
      botNetNotice($myNets,$callingNet,$victim,"$reqNet has no links." );
   }
}

sub localUnLink {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($reqNet, $misc) = split(/ /,$localStr);

   if( defined botNetInfo($myNets,$reqNet) && 
           botNetIsActive( $myNets, $callingNet, $reqNet) && 
           botNetIsLinked( $myNets, $callingNet, $reqNet)){
      botNetNotice($myNets,$callingNet,$victim,"Terminated link to $reqNet.");
      my $reqNetInfo = botNetInfo($myNets, $reqNet);
      botNetNotice( $myNets,
                 $reqNet,
                 {TO=>$reqNetInfo->{'channel'} },
                 "Terminated link to $callingNet."
      );
      botNetSetUnLinked($myNets,$reqNet,$callingNet);
   } else {
      botNetNotice($myNets,$callingNet,$victim,
         "Param must be a valid, active, and linked network."
      );
   }
}

sub localDisconnect {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($reqNet, $misc) = split(/ /,$localStr);

   if( defined botNetInfo($myNets,$reqNet) && botNetIsActive($myNets,$reqNet) ){
      botNetNotice($myNets,$callingNet,$victim,"disconnecting from $reqNet.");
      ircQuit($myNets, $reqNet, "User requested termination.");
      botNetClose($myNets,$reqNet);
      botNetSetPassive($myNets,$reqNet);
      botNetUserClear($myNets,$reqNet);
   }
}

sub localDie {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   botNetCloseAll( $myNets, "my owner told me to... die..." );
# send ourselves the term signal.
   kill('TERM', $$);
}

sub localWhois {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($reqNet, $reqNick, $misc) = split(/ /,$localStr);
   if ( !defined botNetInfo($myNets,$reqNet) || !defined $reqNick ) { return; }
   my $Usr=botNetUser($myNets,$reqNet,$reqNick);
   if ( !defined $Usr ) { return ; }
   botNetNotice($myNets,$callingNet, $victim, sprintf("%s %s %s\@%s (%s)",
         $reqNick,
         $Usr->{status},
         $Usr->{uid},
         $Usr->{host},
         $Usr->{gecos}
      )
   );
}

sub localWho {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   my ($reqNet, $reqNick, $misc) = split(/ /,$localStr);

   my $result = ""; 
   if ( defined botNetInfo($myNets,$reqNet) ) {
      my @netUsers;
      foreach (botNetUserList($myNets,$reqNet)) {
         my $myUser = botNetUser($myNets,$reqNet,$_);
         my $myStats = $myUser->{status};
         $myStats=~s/[GH]*//g;
         push @netUsers,"$myStats$_";
      }
      $result =join(' ',@netUsers);
      if ($result eq "") { $result = "No data available for $reqNet"; }
      botNetNotice($myNets,$callingNet,$victim,$result);
   } else {
      $result='Currently Connected Networks: '.join(' ',botNetList($myNets));
      botNetNotice($myNets,$callingNet,$victim,$result);
   }
}

sub localDict {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   my $QueryResult;
   if( $localStr ){
      $QueryResult = `$ENV{SRCPATH}/exec/dict.sh '$localStr'`;
      $QueryResult=~s/[\r\n]/ /g;
      $QueryResult=~s/[ ][ ]+/ /g;
   } else {
      $QueryResult="No word provided.";
   }
   botNetNotice($myNets,$callingNet,$victim,substr($QueryResult,0,510));
}

sub localUDict {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   my $QueryResult;
   if( $localStr ){
      $QueryResult = `$ENV{SRCPATH}/exec/udict.sh '$localStr'`;
      $QueryResult=~s/[\r\n]/ /g;
      $QueryResult=~s/[ ][ ]+/ /g;
   } else {
      $QueryResult="No word provided.";
   }
   botNetNotice($myNets,$callingNet,$victim,substr($QueryResult,0,510));
}

sub localNewQuote {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my $quoteno = botNetSetQuote($myNets, $callingNet, $user, $localStr);
   my $result;
   if( !$quoteno ) {
      $result = "Could not add quote.";
   } else {
      $result = "Added quote #$quoteno";
   }
   botNetNotice($myNets,$callingNet,$victim,$result);
}

sub localGetQuote {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $quotenumber=$_[4];

   my $result = botNetGetQuote($myNets, $callingNet, $user, $quotenumber);

   if( ! $result ) {
      $result = "No quote associated with that quote number.";
   }
   botNetNotice($myNets, $callingNet, $victim, $result);
}

sub localGreet {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my $myNetPerms = botNetPerms($myNets, $callingNet);

   my $flagCheck = usrCheckFlag( 
                      $myNetPerms,
                      usrHostmaskMatch($myNetPerms, $user),
                      'G'
                   ) ;

   if ( ! $flagCheck ) {
      botNetNotice($myNets,$callingNet,$victim,
         "You need the 'G' flag to use the Greet command."
      );
   }

   $localStr=~s/[\r\n]*$//g;
   if( !$localStr ) {
      my $myGreeting=botNetGetGreeting($myNets, $callingNet, $user);
      my $result;
      if( $myGreeting ) {
         $result= "Greeting: $myGreeting";
      } else {
         $result = "No Greeting Set";
      }
      botNetNotice($myNets,$callingNet,$victim, $result );
      return;
   }

   if ($localStr eq "delete") {
      botNetUnsetGreeting($myNets, $callingNet, $user);
      botNetNotice($myNets,$callingNet,$victim, "Greeting was unset." );
   } else {
      botNetSetGreeting($myNets, $callingNet, $user, $localStr);
      botNetNotice($myNets,$callingNet,$victim, "Greeting set to $localStr" );
   }
}

sub localCalc {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   if( !$localStr ) {
      botNetNotice($myNets,$callingNet,$victim,
         "The calc command requires at least one parameter."
      );
      return;
   }

   my $QueryResult = `$ENV{SRCPATH}/exec/calc.pl '$localStr'`;
   $QueryResult=~s/[\n\r"]//g;

   if( $QueryResult )  {
      botNetNotice($myNets,$callingNet,$victim,$QueryResult);
   } else {
      botNetNotice($myNets,$callingNet,$victim,"calc returned no value.");
   }
}

sub localSign {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   if( !$localStr ) {
      botNetNotice($myNets,$callingNet,$victim,"The sign command requires at least one parameter.");
      return;
   }

   my $QueryResult = `$ENV{SRCPATH}/exec/astrology.pl $localStr`;
   $QueryResult=~s/[\n\r"]//g;

   eval "\$QueryResult=~s/\%s/$victim/g";

   if( $QueryResult )  {
      botNetNotice($myNets,$callingNet,$victim,$QueryResult);
   } else {
      botNetNotice($myNets,$callingNet,$victim,"Astrocenter.com returned no value.");
   }
}

sub localWeather {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   if( !$localStr ) {
      botNetNotice($myNets,$callingNet,$victim,"The weather command requires at least one parameter.");
      return;
   }

   my $QueryResult = `$ENV{SRCPATH}/exec/weather.pl $localStr`;
   $QueryResult=~s/[\n\r"]//g;

   if( $QueryResult )  {
      botNetNotice($myNets,$callingNet,$victim,$QueryResult);
   } else {
      botNetNotice($myNets,$callingNet,$victim,"weather.com could not find a report for that zipcode.");
   }
}


sub localProfile {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   if( !$localStr ) {
      botNetNotice($myNets,$callingNet,$victim,"The Profile command requires at least one parameter.");
      return;
   }

   my $QueryResult = `$ENV{SRCPATH}/exec/profile.pl $localStr`;
   $QueryResult=~s/[\n\r"]//g;

   if( $QueryResult )  {
      botNetNotice($myNets,$callingNet,$victim,$QueryResult);
   } else {
      botNetNotice($myNets,$callingNet,$victim,"Yahoo couldn't find a profile for that ticker.");
   }
}

sub localQuote {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];
   my $result;

   $localStr=~s/[\r\n]*$//g;

   my $QueryResult = `$ENV{SRCPATH}/exec/quote.pl "$localStr"`;
   $QueryResult=~s/[\n\r"]//g;

   if( $QueryResult )  {
      botNetNotice($myNets,$callingNet,$victim,$QueryResult);
   } else {
      botNetNotice($myNets,$callingNet,$victim,"No such ticker found.");
   }
}

sub localTime {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;

   my $QueryResult = `$ENV{SRCPATH}/exec/time.pl '$localStr'`;

   botNetNotice($myNets,$callingNet,$victim,$QueryResult);
}


sub localRehash {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   botNetNotice($myNets,$callingNet,$victim,
      "Starting a rehash, sending myself a SIGHUP"
   );
   kill('HUP',$$);
}

sub localPerms {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $hostMask=$_[3];
   my $localStr=$_[4];
   my $user;

   $localStr=~s/[\r\n]*$//g;

   my $myPerms = botNetPerms($myNets, $callingNet);
   if( ! $localStr ) {
      $localStr = $user=usrHostmaskMatch($myPerms, $hostMask);
   } else {
      my $myUserInfo = botNetUser($myNets, $callingNet, lc $localStr);
      if( $myUserInfo ) {
         $user=usrHostmaskMatch($myPerms, 
            $localStr.
            '!'. $myUserInfo->{'uid'} .
            '@'. $myUserInfo->{'host'} 
         );
      }
   }

   if( $myPerms->{users}->{$user} ) {
      my $userData = $myPerms->{users}->{$user};
      botNetNotice($myNets,$callingNet,$victim,
          "Nick: $localStr -- identified as: $user ".$userData->{line}
      );
      botNetNotice($myNets,$callingNet,$victim,
         "Hostmasks: ".join(' ', @{$userData->{hosts}})
      );
      botNetNotice($myNets,$callingNet,$victim,
         "Commands: ".join(' ', @{usrGetPerms($myPerms, $user)} )
      );
      my @myFlags;
      foreach my $theFlag ( @{$userData->{flags}} ) { 
         push @myFlags, usrFlagName($theFlag);
      }
      botNetNotice($myNets,$callingNet,$victim,
         "Flags: ".join(' ', @myFlags)
      );
   } else {
      botNetNotice($myNets,$callingNet,$victim,
         "Nick: $localStr is not a recognized user. Default Privledges"
      );
   }
}

sub localDeop {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   my $netData = botNetInfo($myNets,$callingNet);

   if( $netData->{'network'} ne "irc" ) {
      botNetNotice($myNets, $callingNet, $victim,
         "the Deop command has no meaning on this network."
      );
      return;
   }
   my $host;
   if( !$localStr ) {
      ($localStr, $host) = split(/!/, $user );
   }
   my $modeline;
   foreach ( split(/\s+/, $localStr) ) { $modeline.="o"; }

   my $channel = $netData->{'channel'};
   sockWrite($netData->{'FD'},"MODE $channel -$modeline $localStr\r\n");
}

sub localUsers {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];
   my $result;
   my %order;

   my $myPerms = botNetPerms($myNets, $callingNet);
   foreach my $user ( keys %{$myPerms->{users}} ){
      $order{$myPerms->{users}->{$user}->{line}} = $user;
   }

   foreach my $index ( sort keys %order ){
      $result.=$order{$index}."($index) ";
   }

   botNetNotice($myNets,$callingNet,$victim,$result);
}

sub localDeVoice {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;

   my $netData = botNetInfo($myNets,$callingNet);

   if( $netData->{'network'} ne "irc" ) {
      botNetNotice($myNets, $callingNet, $victim,
         "the DeVoice command has no meaning on this network."
      );
      return;
   }
   my $host;
   if( !$localStr ) {
      ($localStr, $host) = split(/!/, $user );
   }
   my $modeline;
   foreach ( split(/\s+/, $localStr) ) { $modeline.="v"; }

   my $channel = $netData->{'channel'};
   sockWrite($netData->{'FD'},"MODE $channel -$modeline $localStr\r\n");
}

sub localVoice {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;

   my $netData = botNetInfo($myNets,$callingNet);

   if( $netData->{'network'} ne "irc" ) {
      botNetNotice($myNets, $callingNet, $victim,
         "the Voice command has no meaning on this network."
      );
      return;
   }
   my $host;
   if( !$localStr ) {
      ($localStr, $host) = split(/!/, $user );
   }
   my $modeline;
   foreach ( split(/\s+/, $localStr) ) { $modeline.="v"; }

   my $channel = $netData->{'channel'};
   sockWrite($netData->{'FD'},"MODE $channel +$modeline $localStr\r\n");
}

sub localOp {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   my $netData = botNetInfo($myNets,$callingNet);

   if( $netData->{'network'} ne "irc" ) {
      botNetNotice($myNets, $callingNet, $victim,
         "the Op command has no meaning on this network."
      );
      return;
   }
   my $host;
   if( !$localStr ) {
      ($localStr, $host) = split(/!/, $user );
   }
   my $modeline;
   foreach ( split(/\s+/, $localStr) ) { $modeline.="o"; }

   my $channel = $netData->{'channel'};
   sockWrite($netData->{'FD'},"MODE $channel +$modeline $localStr\r\n");
}

sub localEmote {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my $modeline;
   my $netData = botNetInfo($myNets,$callingNet);
   if( $netData->{'network'} eq 'irc' ) {
      sockWrite( $netData->{'FD'}, "$localStr\r\n", -1);
   } else {
      botNetNotice($myNets, $callingNet, $victim, 
         "You cannot emote to this network."
      );
   }
}

my @str8ball = (
   "Sorry, snowballs have been known to have better chances in hell.",
   "To say 'thats a negative' would be an understatement.",
   "Um no, now excuse me while i laugh this off... ",
   "Just one word. No.",
   "No way.",
   "iie! iie! Chigaimasu YO!",
   "um... whatever.",
   "I have no idea.",
   "Possibly. Maybe wait a little longer.",
   "It's a definate maybe.",
   "It could go either way.",
   "Try asking again some other time.",
   "Ask yourself while looking in the mirror. What would you say?",
   "Sure.",
   "Why not?",
   "Seems logical to me.",
   "If it were a fish, it'd bite your ass.",
   "Why are you asking the obvious. Duh.",
   "Yes.",
   "Definately.",
   "Obviously"
);

sub local8ball {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   $localStr=~s/[\r\n]*$//g;
   my $result="You must ask a QUESTION for the 8ball to work.";
   # if( $localStr=~/\s*\?\s*$/) {
      my $index = rand(scalar @str8ball);
   
      $result = "The 8ball says: $str8ball[$index]";
   # }

   botNetNotice($myNets,$callingNet,$victim,$result);
}

sub localDeals {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my $QueryResult = `$ENV{SRCPATH}/exec/deals.pl`;

   botNetNotice($myNets,$callingNet,$victim,$QueryResult);
}

sub localTranslate {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my ($fromlang, $tolang, $body) = split(/ /, $localStr, 3);

   botNetNotice($myNets,$callingNet,$victim,"Translating: $body");
   my $QueryResult;
      $QueryResult .= `$ENV{SRCPATH}/exec/translate.pl '$fromlang' '$tolang' '$body'`;

   botNetNotice($myNets,$callingNet,$victim,$QueryResult);
}



sub localEbay {
   my $myNets=$_[0];
   my $callingNet=$_[1];
   my $victim=$_[2];
   my $user=$_[3];
   my $localStr=$_[4];

   my $defaultLines = 2;
   my $maxLines = 10;
   my @params = split(',', $localStr, 2);

   my $QueryResult;
   if( scalar @params > 1 ) {
      if( $victim ne $myNets->{$callingNet}->{nick} ) {
         if( $params[0] > $defaultLines ) {
            $params[0]=$defaultLines;
            $QueryResult="public request shortenend to $defaultLines lines\n";
         }
      }
      if( $params[0] > $maxLines ) {
         $params[0]=$maxLines;
         $QueryResult="Request shortenend to $maxLines lines\n";
      }
      $QueryResult .= `$ENV{SRCPATH}/exec/ebay.pl $params[0] '$params[1]'`;
   } else {
      $QueryResult = `$ENV{SRCPATH}/exec/ebay.pl $defaultLines '$params[0]'`;
   }
   my @results = split('\n', $QueryResult);

   foreach my $URL ( @results ) {
      botNetNotice($myNets,$callingNet,$victim,$URL);
   }
   if( scalar @results < 1 ) {
      botNetNotice($myNets,$callingNet,$victim,"eBay returned no matches.");
   }
}

my $localTerminals = {
   'help' => \&localHelp,
   'who' => \&localWho,
   'whois' => \&localWhois,
   'connect' => \&localConnect,
   'disconnect' => \&localDisconnect,
   'ebay' => \&localEbay,
   'dict' => \&localDict,
   'udict' => \&localUDict,
   'die' => \&localDie,
   'time' => \&localTime,
   'invite' => \&localInvite,
   'rehash' => \&localRehash,
   'op' => \&localOp,
   'voice' => \&localVoice,
   'devoice' => \&localDeVoice,
   'sign' => \&localSign,
   'profile' => \&localProfile,
   'weather' => \&localWeather,
   'deals' => \&localDeals,
   'calc' => \&localCalc,
   'link' => \&localLink,
   'links' => \&localLinks,
   'unlink' => \&localUnLink,
   'greet' => \&localGreet,
   'getquote' => \&localGetQuote,
   'newquote' => \&localNewQuote,
   'deop' => \&localDeop,
   'emote' => \&localEmote,
   'perms' => \&localPerms,
   '8ball' => \&local8ball,
   'exec' => \&localExec,
   'users' => \&localUsers,
   'translate' => \&localTranslate,
   'quote' => \&localQuote
};

# subroutine: localInterpret(botNetsData, botNet, botMsg)
# this takes the botMsg and breaks it down for local
# consumption. 

sub localInterpret {
   my $myNets = $_[0];
   my $callingNet = $_[1];
   my $user = $_[2];
   my $victim = $_[3];
   my $localStr = $_[4];

# figure out what prefix the bot uses to determine commands
   my $cmdPrefix;
   if( defined $myNets->{$callingNet}->{'prefix'} ) {
      $cmdPrefix=$myNets->{$callingNet}->{'prefix'};
      $cmdPrefix=~s/([\W])/\\$1/g;
   } else {
      $cmdPrefix='!';
   }

   eval "\$localStr=~s/^$cmdPrefix(.\*)/\$1/g;";

   $localStr=~s/[\n\r]*$//g;
   my ($localCmd, $localPrm) = split(/\s/,$localStr,2);
   my $myNetPerms = botNetPerms($myNets, $callingNet);

   my $permCheck = usrCheckPerm( 
                      $myNetPerms,
                      usrHostmaskMatch($myNetPerms, $user),
                      $localCmd
                   );

   if( defined $localTerminals->{$localCmd} ) {
      if( $permCheck ) {
         eval { 
            local $SIG{ALRM} = sub { die "Operation timed out.###"; };
            alarm 10; # our timeout is 10 seconds
            &{$localTerminals->{$localCmd}}(
               $myNets,$callingNet,$victim,$user,$localPrm
            );
            alarm 0;
         };
         if( $@ ) {
            $@=~s/\#\#\#(.*)$//g;
            botNetNotice($myNets,$callingNet,$victim,$@);
         }
      } else {
         if( ! $myNets->{$callingNet}->{'quiet'} ) {
             botNetNoticePrivate($myNets,$callingNet,$victim,
                 "You do not have permission to execute that command."
             );
	 }
      }
   } else {
      if( ! $myNets->{$callingNet}->{'quiet'} ) {
         botNetNoticePrivate($myNets,$callingNet,$victim,
            "Unknown command, use 'help' for a list of commands"
         );
      }
   }
}
