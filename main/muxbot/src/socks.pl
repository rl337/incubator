use Socket;
use strict;

# subroutine: Integer sockOpen(TypeGlob FD, String host, Integer port)
# opens a socket to host on port and associates it with FD.
# returns 1 if successful and 0 if there was an error.

sub sockOpen($$$){
   my ($fhost, $port, $iaddr, $paddr, $proto, $fd);

   $fd=shift @_;
   $fhost=shift @_;
   $port=shift @_;

   if ($port =~ /\D/) { $port = getservbyname($port, 'tcp'); }
   return 0 unless $port;

   eval {
      local $SIG{ALRM} = sub { die "Connect request timed out."; };
      local $SIG{PIPE} = sub { die "Broken Pipe."; };
      alarm 10; # our timeout is 10 seconds
      $iaddr = inet_aton($fhost) || die "Couldn't convert aton";
      $paddr = sockaddr_in($port, $iaddr);

      $proto = getprotobyname('tcp');
      socket($fd, PF_INET, SOCK_STREAM, $proto)  || die "couldn't open socket";
      connect($fd, $paddr)    || die "connect failed.";
      alarm 0;
   };
   if( $@ ) { return 0; }
   alarm 0;
   return 1;
}

# subroutine: String sockRead(TypeGlob FD, size)
# attempts to read from socket refered to
# by FD. it returns the string read if
# it was successful, otherwise returns undef. 

# If a size is specified, it tries to read exactly
# that number of bytes from the socket... otherwise
# it reads up until a newline.

sub sockRead($$) {
   my $myfd=shift @_;
   my $mysize=shift @_;
   my $myline="";
   my $mychr="";

   eval {
      local $SIG{ALRM} = sub { die "socket Read request timed out."; };
      local $SIG{PIPE} = sub { die "Broken Pipe."; };
      alarm 10;
      if( $mysize > -1 ) {
         sysread($myfd,$myline,$mysize);
      } else {   
         while ( (sysread($myfd, $mychr, 1)) && ("$mychr" ne "\n") ) {
            $myline = $myline . $mychr;
         }
      }
      alarm 0;
   };
   if( $@ ) { alarm 0; return undef; }
   if( length($myline) < 1 ) { return undef; }
   return $myline;
}

# subroutine: sockWrite(TypeGlob FD, String str, size)
# writes str to the socket associated with the 
# typeglob FD.
sub sockWrite($$$) {
   my ($myfd, $myline, $mysize);

   $myfd=shift @_;
   $myline=shift @_;
   $mysize=shift @_;

   eval {
      local $SIG{ALRM} = sub { die "socket Write timed out."; };
      local $SIG{PIPE} = sub { die "Broken Pipe."; };
      alarm 10;
      if( $mysize > 0 ) {
         syswrite($myfd, $myline, $mysize);
      } else {
         syswrite($myfd, $myline);
      }
      alarm 0;
   };
   if( $@ ) { alarm 0; return undef; }
   return 1;
}


# subroutine sockClose(TypeGlob FD)
# shuts down a socket
sub sockClose($) {
   eval {
      local $SIG{ALRM} = sub { die "socket Write timed out."; };
      local $SIG{PIPE} = sub { die "Broken Pipe."; };
      alarm 10;
      shutdown($_[0],2);
   };
   alarm 0;
}
