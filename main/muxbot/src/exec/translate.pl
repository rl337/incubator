#!/usr/bin/perl

use strict;

my %languagehash = (
	'arabic'	=>	'ar',
	'chinese'	=>	'zh-CH',
	'dutch'	=>	'nl',
	'english'	=>	'en',
	'eng'		=>	'en',
	'en'		=>	'en',
	'de'		=>	'de',
	'german'	=>	'de',
	'ger'		=>	'de',
	'germ'		=>	'de',
	'greek'		=>	'el',
	'es'		=>	'es',
	'japanese'	=>	'jp',
	'korean'	=>	'ko',
	'spanish'	=>	'es',
	'span'		=>	'es',
	'espanol'	=>	'es',
	'french'	=>	'fr',
	'fr'		=>	'fr',
	'fren'		=>	'fr',
	'it'		=>	'it',
	'italian'	=>	'it',
	'ital'		=>	'it',
	'portuguese'	=>	'pt',
	'pt'		=>	'pt',
	'port'		=>	'pt',
	'portugese'	=>	'pt'
);

my $from = shift @ARGV;
my $to = shift @ARGV;

my $fromlang = $languagehash{$from};
my $tolang = $languagehash{$to};
my $text = shift @ARGV;
my $useragent = 'Mozilla/5.0 (compatible; Konqueror/3.0.0-10; Linux)';

if( !$fromlang ) {
	print "$from language is not a supported language";
	exit 0;
}

if( !$tolang ) {
	print "$to language is not a supported language";
	exit 0;
}

my $url = "http://translate.google.com/translate_t";

$text=~s/[\n\r]//g;
$text=~s/[\n\r]//g;
$text=~s/([^A-Za-z_0-9])/'%'.unpack('H2',"$1")/ge;

my $paramstr = "?text=$text&langpair=$fromlang|$tolang&hl=en&ie=ASCII&oe=UTF-8&submit=Translate";

my $result = `/usr/local/bin/wget -q -O - -U '$useragent' '$url$paramstr'`;

$result =~s/[\s\n\r]+/ /g;
$result =~s/<textarea[^>]*>([^<]*)<\/textarea>/\nTRANSLATE:$1\n/g;
$result =~s/([^<]*)<[^>]*>/$1\n/g;
$result =~s/^\s*//g;
$result =~s/&[^;]*;/ /g;

my ($original, $translation) = grep(/TRANSLATE:/, split(/\n/, $result));
$translation=~s/^\s*TRANSLATE://g;
$translation=~s/\s$//g;
print "$translation\n";
