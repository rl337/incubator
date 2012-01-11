#!/usr/bin/perl

use strict;

my %flagNameHash = (
   'A' => 'AUTOOP',
   'V' => 'AUTOVOICE',
   'G' => 'GREETING',
   'N' => 'NOTES'
);

sub directiveInclude {
   my $groupsRef = shift;
   my $usersRef = shift;
   my $params = shift;

   my $incFile = usrReadConfig($params);

   foreach my $newGroup ( keys %{$incFile->{'groups'}} ) {
      $groupsRef->{$newGroup} = $incFile->{'groups'}->{$newGroup};
   }

   foreach my $newUser ( keys %{$incFile->{'users'}} ) {
      $usersRef->{$newUser} = $incFile->{'users'}->{$newUser};
   }
}

my %directiveHash = (
   'include' => \&directiveInclude
);

# usrReadConfig(Configfile)

# This function reads the file associated with configFile
# parses through it and returns a structure with that
# looks like this:

# result = {
#   users => %users
#   groups => %groups
# }

# users = {
#   hosts=>\@hostmaskList,
#   groups=>\@groupsList,
#   flags=>\@flagsList
# }

# groups = @commands
my $uniqueVal = 0; 

sub usrReadConfig {
   my $userFile = shift;
   my $fileGlob;

   eval '$fileGlob = \*'."FDTMP_$uniqueVal".';';
   $uniqueVal++;
   open($fileGlob, $ENV{CNFPATH}.'/'.$userFile) or return undef;

   my $currEntry;
   my %groups;
   my %users;
   my $line=10000;

   while(<$fileGlob>) {
      next if /^#/;

# if it starts with a $, it's a directive. deal with it here.
      if( /^\$/ ) {
         my ($directive, $params) = split(/\s+/,$_,2);
         $params=~s/[\n\r]*//g;
         $directive=~s/^\$(.*)/$1/g;
         if( defined($directiveHash{$directive}) ) {
            &{$directiveHash{$directive}}(\%groups, \%users, $params);
         }
         next;
      }

# it's not a comment (#) or a directive($) deal with it now.
      $line++;
      if( ! /^\s/ ) {
         $currEntry=~s/^\s*(.*[\S])\s*$/$1/g;
         if( $currEntry=~/^\%/ ) {
            my ($groupName, $cmdList) = split(/:/,$currEntry);
            my @cmds=split(/\s+/,$cmdList);
            $groups{lc substr($groupName,1)}=\@cmds;
         } elsif ( $currEntry ) {
            next if $currEntry=~/^\s+$/;
            $currEntry=~s/\s*:\s*/:/g;
            my ($username, $hostmasks, $groups, $flags) = split(/:/,$currEntry);
            my @hostmaskList=split(/\s+/,$hostmasks);
            my @groupsList=split(/\s+/,$groups);
            my @flagsList=split(/\s*/,$flags);
            $users{lc $username}= {
               hosts=>\@hostmaskList,
               groups=>\@groupsList,
               flags=>\@flagsList,
               line=>$line
            };
         }
         $currEntry = $_;
      } elsif ( $currEntry ) {
         $currEntry.=$_;
      }

   }
   my $result = {
      groups => \%groups,
      users => \%users
   };
   return $result;
}

#  usrExpandPerms( permData, group )
#  takes a permissions data structure returned by usrReadConfig
#  and a group and returns a complete list of commands allowed
#  by that group. 
sub usrExpandPerms{
   my $permData = shift;
   my $myGroup = shift;
   my @result;
   my $groupData=$permData->{groups}->{$myGroup};
   if( $groupData ) {
      foreach my $myPerm ( grep(/.+/,@{$groupData}) ) {
         if( $myPerm=~/^\%/ ) { 
            my $subRes = usrExpandPerms($permData, substr($myPerm,1));
            push @result, @{$subRes};
         } else {
            push @result, $myPerm;
         }
      }
      return \@result;
   }
   return undef;
}

sub usrFlagName { 
   return $flagNameHash{$_[0]};
}

sub usrGetFlags {
   my $permData = shift;
   my $myUser = shift;
   return $permData->{'users'}->{$myUser}->{'flags'};
}

sub usrGetPerms {
   my $permData = shift;
   my $myUser = shift;

   my $userData =  $permData->{users}->{$myUser};


   # if the user doesn't exist, give them default permissions.
   if( ! $userData ) {
      return usrExpandPerms($permData, 'default');
   }

   my @result;
   foreach my $group ( @{$userData->{groups}} ) {
      my $permRef=usrExpandPerms($permData, substr($group,1));
      if( $permRef ) {
         push @result, @{$permRef};
      }
   }
   return \@result;
}

# subroutine: usrCheckFlag(permData, handle, flag)
# returns true if this user has specified flag set
# in his/her permissions.

sub usrCheckFlag {
   my $permData = shift;
   my $myUser = shift;
   my $flag = shift;

   my $myFlags = usrGetFlags($permData, $myUser);
   if( $myFlags ) {
      my $result;
      eval "\$result=scalar grep(/^$flag\$/,\@\{\$myFlags\});";
      return $result;
   }
   return 0;
}

#  usrCheckPerm(permData, handle, command)
# returns true if the handle has permission to execute that command.
sub usrCheckPerm {
   my $permData = shift;
   my $myUser = shift;
   my $command = shift;

   my $usrPerms = usrGetPerms($permData, $myUser);
   if( $usrPerms ) {
      my $result;
      eval "\$result=scalar grep(/^$command\$/,\@\{\$usrPerms\});";
      return $result;
   }
   return 0;
}

# usrHostmaskMatch(permData, hostmask)
# returns the handle that hostmask matches
# returns undef if no handles match the hostmask.

sub usrHostmaskMatch {
   my $permData = shift;
   my $hostmask = shift;

   my ($unick, $uhostmask) = split(/!/,$hostmask);
   my ($uuid, $uhost) = split(/\@/,$uhostmask);

   my %order;
   foreach my $user ( keys %{$permData->{users}} ){
      $order{$permData->{users}->{$user}->{line}} = $user;
   }

   foreach my $index ( sort keys %order ){
      my $user = $order{$index};
      foreach my $mask ( @{$permData->{users}->{$user}->{hosts}} ) {
         my ($nickresult, $hostresult, $uidresult);
         my ($nick, $hostmask) = split(/!/,$mask);
         my ($uid, $host) = split(/\@/,$hostmask);
         eval "\$nickresult = \$unick=~/\\A$nick\\Z/;";
         eval "\$hostresult = \$uhost=~/\\A$host\\Z/;";
         eval "\$uidresult = \$uuid=~/\\A$uid\\Z/;";
         if ($nickresult && $hostresult && $uidresult){
            return $user;
         }
      }
   }
   return undef;
}

