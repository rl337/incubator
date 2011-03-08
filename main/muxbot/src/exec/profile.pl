#!/usr/bin/perl
use strict;

if( scalar @ARGV < 1 ) {
   print "usage: profile.pl <ticker>\n";
   exit(0);
}

my $ticker=$ARGV[0];
$ticker=~s/\s//g;

my $param=$ticker.".html";
my $firstLetter = substr($ticker,0,1);

my $myresult = `wget -q -O - 'http://biz.yahoo.com/p/$firstLetter/$param'`;
$myresult =~s/([^<]*)<[^>]*>/<$1>/g;
$myresult =~s/[\s]+/ /g;
$myresult =~s/[<>]+/\n/g;
my @lines = split( /[\n]+/, $myresult );
my $line = shift @lines;
my $result;

while( $line = shift @lines ) {
   $line=~s/^\s*(.*)\s*$/\1/g;
   last if( $line=~/BUSINESS\ SUMMARY/ );
}

while( $line = shift @lines ) {
   $line=~s/^\s*(.*)\s*$/\1/g;
   $result = "$result$line";
   last if( $line=~/Key Statistics/ );
}

$result=~s/&nbsp;//g;

if( $result=~/^\s*$/ )  {
   print "Could not find profile.\n";
} else {
   print substr($result,0,510)."\n";
}
