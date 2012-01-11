#!/usr/bin/perl

use strict;

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
$year+=1900;
$mon++;
 
my $url = "http://www.slickdeals.net/?from=$year-$mon-$mday&to=$year-$mon-$mday";
my $useragent = 'Mozilla/5.0 (compatible; Konqueror/3.0.0-10; Linux)';

my $result = `wget -q -O -  -U '$useragent' '$url'`;
my @lines = split(/\s*\n/, $result);

my @deals = grep(/newsquick/, @lines);

my $result = join(" | ", @deals);
$result=~s/<[^\>]+>//g;

print "$result";
