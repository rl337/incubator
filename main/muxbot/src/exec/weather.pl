#!/usr/bin/perl
use strict;

if( scalar @ARGV < 1 ) {
   print "usage: weather.pl <zipcode>\n";
   exit(0);
}

my $zipcode=$ARGV[0];
$zipcode=~s/\s//g;

my $param=$zipcode;
my $useragent = 'Mozilla/5.0 (compatible; Konqueror/3.0.0-10; Linux)';

# my $myresult = `lynx --source 'http://www.weather.com/weather/detail/$param'`;
my $myresult = `wget -q -U '$useragent' -O - 'http://www.w3.weather.com/weather/narrative/$param'`;

$myresult =~s/([^<]*)<[^>]*>/<$1>/g;
$myresult =~s/[\s]+/ /g;
$myresult =~s/[<>]+/\n/g;
$myresult =~s/&[^;]*;/ /g;
my @lines = split( /[\n]+/, $myresult );
my $line = shift @lines;
my $result;

while( $line = shift @lines ) {
   $line=~s/^\s*(.*)\s*$/\1/g;
   last if $line=~/Detailed Local Forecast For/;
}

# if( $line ) { $result.="$line "; }
while( $line = shift @lines ) {
   $line=~s/^\s*(.*)\s*$/\1/g;
   last if( eval "\$line=~/$param/" );
}

while( $line = shift @lines ) {
   $line=~s/^\s*(.*)\s*$/\1/g;
   next if( $line=~/Last updated/ ); # skip the date
   next if ( !$line);
   last if( $line=~/Back\ to\ previous\ page/ );
   $result.="$line ";
}

$result=~s/weather.com//g;

$result=~s/[Ss]unday/Sun/g;
$result=~s/[Mm]onday/Mon/g;
$result=~s/[tT]uesday/Tue/g;
$result=~s/[wW]ednesday/Wed/g;
$result=~s/[tT]hursday/Thrs/g;
$result=~s/[Ff]riday/Fri/g;
$result=~s/[Ss]aturday/Sat/g;

$result=~s/[tT]omorrow/Tom./g;
$result=~s/[Ss]cattered/scatt./g;
$result=~s/[Aa]ccumulation[s]*/fall/g;
$result=~s/[Oo]f [aA]bout/of/g;
$result=~s/[oO]ne/1/g;
$result=~s/[Tt]wo/2/g;
$result=~s/[Tt]ree/3/g;

$result=~s/[iI]nch[es]*/in/g;
$result=~s/[fF]oot/ft/g;
$result=~s/[fF]eet/ft/g;

$result=~s/[Pp]artly/ptly/g;
$result=~s/[Ww]inds/Wnd:/g;

$result=~s/[Ff]ollowed\s*[bB]y/then/g;

$result=~s/[ ]*Alerts//g;
$result=~s/ - Local Forecast - //g;
$result=~s/ No long term advisories at this time //g;
$result=~s/Last updated //g;
$result=~s/a few/some/g;
$result=~s/Standard Time/-/g;
$result=~s/[Pp]recipitation/rain/g;
$result=~s/chance of rain/rain chance/g;
$result=~s/Daylight Time/-/g;
$result=~s/[aA] mix of ([\S]*) and ([\S]*)/mixed $1\/$2/g;
$result=~s/Forecast //g;
$result=~s/[Hh]igh[s]* in the/highs/g;
$result=~s/[Ll]ow[s]* in the/lows/g;
$result=~s/[Hh]igh[s]* near/highs/g;
$result=~s/[Ll]ow[s]* near/lows/g;
$result=~s/([NESW]+) to ([NESW]+)/$1\/$2/g;
$result=~s/([0-9]+) to ([0-9]+)/$1-$2/g;
$result=~s/ and/,/g;
$result=~s/ with /\//g;

$result=~s/highs/Hi:/g;
$result=~s/lows/Lo:/g;
print "$result\n";
exit 0;

