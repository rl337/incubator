#!/usr/bin/perl
use strict;

if( scalar @ARGV < 1 ) {
   print "usage: astrology.pl <sign>\n";
   exit(0);
}

my %signhash=(
   'capricorn'=>1,
   'aries'=>1,
   'cancer'=>1,
   'virgo'=>1,
   'sagittarius'=>1,
   'scorpio'=>1,
   'libra'=>1,
   'gemini'=>1,
   'leo'=>1,
   'taurus'=>1,
   'aquarius'=>1,
   'pisces'=>1
);

my $sign = lc $ARGV[0];
$sign=~s/\s//g;

if( !defined($signhash{$sign}) ) {
   print "That is not a valid sign.\n";
   exit(0);
}


my $param=$sign."dailyhoroscope.html";
my $myresult = `wget -q -O - 'http://astrology.yahoo.com/us/astrology/today/$param'`;
$myresult =~s/([^<]*)<[^>]*>/$1/g;
my @lines = split( /[\s]*[\n]+[\s]*/, $myresult );
my $line;

while( scalar @lines ) {
   $line = shift @lines;
   last if $line=~/by Astrocenter.com/;
}

$line = shift @lines;

print "$line\n";
