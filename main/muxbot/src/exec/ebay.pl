#!/usr/bin/perl

use strict;

if( scalar @ARGV < 2 ) {
   print "Usage: ebay.pl <maxhits> <search>\n";
   exit 0;
}

my $quantity = shift @ARGV;
my $request = shift @ARGV;
$request=~s/[\n\r]//g;
$request=~s/[\n\r]//g;
$request=~s/([^A-Za-z_0-9])/'%'.unpack('H2',"$1")/ge;

my $eBayURL = 'http://search.ebay.com/search/search.dll';
my $eBayQuery = '?'.
   'MfcISAPICommand=GetResult&'.
   'QUERY='. $request. '&'.
   'ht=1&'.
   'ebaytag1=ebayreg&'.
   'ebaytag1code=&'.
   'SortProperty=MetaEndSort'
;

my $result = `wget -q -O - '$eBayURL$eBayQuery'`;
$result=~s/[\n\r]//g;
$result=~s/[^<]*<([^>]*)>([^<]*)/$1 $2\n/g;
my @alltags = split(/\n/, $result);
my @mytags = grep(/ViewItem/, @alltags);

shift @mytags;
shift @mytags;

while( $quantity-- ) {
   my $myItem = shift @mytags;
   next unless $myItem;
   $myItem=~s/[^"]*"([^"]*)"/$1/g;
   my ($itemid, $desc) = split(/ /, $myItem, 2);
#   $itemid=~s/.*=([0-9]*)$/$1/g;
   print "$desc $itemid\n";
}

