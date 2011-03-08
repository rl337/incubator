#!/usr/bin/perl

%timezones = (
   'GMT' => [ "Greenwich Mean Time", 0 ],
   'WAT' => [ "West Africa Time", -1 ],
   'AT'  => [ "Azores Time", -2 ],
   'AST' => [ "Atlantic Standard Time", -4 ],
   'EST' => [ "Eastern Standard Time", -5 ],
   'CST' => [ "Central Standard Time", -6 ],
   'MST' => [ "Mountain Standard Time", -7 ],
   'PST' => [ "Pacific Standard Time", -8 ],
   'YST' => [ "Yukon Standard Time", -9 ],
   'AHST' => [ "Alaska-Hawaii Standard Time", -10 ],
   'NT' => [ "Nome Time", -11 ],
   'IDLW' => [ "International Date Line West", -12 ],
   'CET' => [ "Central European Time", 1 ],
   'MET' => [ "Middle European Time", 1 ],
   'EET' => [ "Eastern European Time and USSR Zone 1", 2 ],
   'BT' => [ "Baghdad Time and USSR Zone 2", 3 ],
   'IST' => [ "Indian Standard Time", 5.5 ],
   'ZP4' => [ "USSR Zone 3", 4 ],
   'ZP5' => [ "USSR Zone 4", 5 ],
   'ZP6' => [ "USSR Zone 5", 6 ],
   'ZP7' => [ "USSR Zone 6", 7 ],
   'WAST' => [ "West Australian Standard Time and USSR Zone 7", 8 ],
   'JST' => [ "Japan Standard Time and USSR Zone 8", 9 ],
   'ACT' => [ "Australian Central Time", 9.5 ],
   'EAST' => [ "East Australian Standard Time and USSR Zone 9", 10 ],
   'IDLE' => [ "International Date Line East", 12 ],
   'NZST' => [ "New Zealand Standard Time", 12 ],
   'NZT' => [ "New Zealand Time", 12 ],
   'NZDT' => [ "New Zealand Daylight Time", 12 ],
   
   'FST' => [ "French Summer Time", 1 ],
   'MEST' => [ "Middle European Summer Time", 1 ],
   'MESZ' => [ "Middle European Summer Time", 1 ],
   'SST' => [ "Swedish Summer Time", 1 ],


   'SWT' => [ "Swedish Winter Time", 1 ],
   'FWT' => [ "French Winter Time", 1 ],
   'MEWT' => [ "Middle European Winter Time", 1 ],
   'AHDT' => [ "Alaska-Hawaii Daylight Time", -10 ],
   'WADT' => [ "West Australian Daylight Time and USSR Zone 7", 8 ],
   'EADT' => [ "East Australian Daylight Time", 11 ],
   'YDT' => [ "Yukon Daylight Time", -9 ],
   'PDT' => [ "Pacific Daylight Time", -8 ],
   'MDT' => [ "Mountain Daylight Time", -7 ],
   'CDT' => [ "Central Daylight Time", -6 ],
   'EDT' => [ "Eastern Daylight Time", -5 ],
   'ADT' => [ "Atlantic Daylight Time", -4 ]
);

%countries = (
   'usa' => [ 'EST', 'CST', 'MST', 'PST', 'YST','AHST' ],
   'us' => [ 'EST', 'CST', 'MST', 'PST', 'YST','AHST' ],
   'united states' => [ 'EST', 'CST', 'MST', 'PST', 'YST','AHST' ],
   'canada' => [ 'EST', 'CST', 'MST', 'PST', 'YST','AHST' ],
   'ca' => [ 'EST', 'CST', 'MST', 'PST', 'YST','AHST' ],
   'mexico' => [ 'CST', 'MST' ],
   'mx' => [ 'CST', 'MST' ],
   'china' => [ 'WAST' ],
   'ch' => [ 'WAST' ],
   'australia' => [ 'WAST', 'ACT', 'EAST' ],
   'au' => [ 'WAST', 'ACT', 'EAST' ],
   'uk' => [ 'GMT' ],
   'japan' => [ 'JST' ],
   'jp' => [ 'JST' ],
   'india' => [ 'IST' ],
   'indiana' => [ 'EST' ],
   'arizona' => [ 'MST' ],
   'ussr' => [ 'EET', 'BT', 'ZP4', 'ZP5', 'ZP6', 'ZP7', 'WAST', 'JST'  ],
   'united kingdom' => [ 'GMT' ]
);


if( scalar @ARGV < 1 )  {
   print "Usage: time.pl <timezone or country>\n";
   exit(0);
}

my $request = shift @ARGV;
if( !$request ){ $request="EST";}
my $found = 0;
my $result = "Unknown location";
my $desc;
my $offset;

my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = gmtime(time);

if( defined $timezones{uc $request} ) {
   $found = 1;
   ($desc, $offset) = @{$timezones{uc $request}};
   $result = "$desc is " ;
}
   
if( $found ) {
   $hour = ($hour+$offset)%24;
   $min = $min + ( $offset * 60 ) % 60;
   if( $min > 60 ) {
	$hour ++;
	$min = $min - 60;
   }

   my $hour12 = $hour>12?$hour-12:$hour;
   $result .= ''.
        (($hour12<10)?"0$hour12":$hour12).':'.
        (($min<10)?"0$min":$min).':'.
        (($sec<10)?"0$sec":$sec).
        (($hour>=12)?" pm":" am")
   ;
   print "$result\n";
   exit(0);
}

if( defined $countries{$request} ) {
   $result = join ( ', ', @{$countries{$request}} );
   print "$request has the following timezones: $result\n";
}

