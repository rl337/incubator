#!/usr/bin/perl

use strict;

my $localStr=shift @ARGV;
my $result;

$localStr=~s/[\r\n]*$//g;
my $httpgetbinary = "wget";
my $httpgetopts = "-q -O -";


sub getQuoteResult($) {
	my $ticker = shift @_;
	my $stockQuery="?s=$ticker&f=sl1d1t1c1ohgv&e=.csv";
	my $queryURL = "http://finance.yahoo.com/d/quotes.csv$stockQuery";
	my $QueryResult = `$httpgetbinary $httpgetopts "$queryURL"`;
	$QueryResult=~s/[\n\r"]//g;
	return split(/,/, $QueryResult);
}

if( scalar split(/ /,$localStr) < 2 ) {
	my ($ticker, $value, $date, $time, $delta, $last,$high,$low, $volume) =
		getQuoteResult($localStr);
	if ( $value > 0 ) {
		$result="[$time] $ticker $value ($delta) ".
		"Low: $low Last: $last High: $high Volume: $volume";
	}
} else {
	foreach my $myTicker ( split(/ /, $localStr) ) {
		my ($ticker, $value, $date, $time, $delta, $last,$high,$low, $volume) =
			getQuoteResult($myTicker);
		if( $value ) {
			$result.="$ticker $value ($delta) ";
		}
	}
}

print $result;


