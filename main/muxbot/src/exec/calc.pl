#!/usr/bin/perl
use strict;

if( scalar @ARGV < 1 ) {
   print "Usage: calc.pl <expression>\n";
   exit 1;
}

sub openParen {
   my $lside = shift;
   my $rside = shift;
   my $tokens = shift;
   my $parenCount = 1;
   my $result = [];

   my $token = shift @{$tokens};
   while( scalar @{$tokens} && $parenCount > 0) { 
      if( $token->{'value'} eq ')' ) { $parenCount--; }
      if( $token->{'value'} eq '(' ) { $parenCount++; }
      if( ($parenCount > 0 ) || ( $token->{'value'} ne ')' ) ) {
         push @{$result}, $token;
         $token = shift @{$tokens};
      }
   }
   return parser($result);
}

sub closeParen {
   my $lside = shift;
   my $rside = shift;
   my $tokens = shift;
   return 0;
}

sub calcAddition {
   my $lside = shift;
   my $rside = shift;
   return $lside + $rside;
}

sub calcSubtraction {
   my $lside = shift;
   my $rside = shift;
   return $lside - $rside;
}

sub calcMultiplication {
   my $lside = shift;
   my $rside = shift;
   return $lside * $rside;
}

sub calcDivision {
   my $lside = shift;
   my $rside = shift;
   return $lside / $rside;
}

sub calcModulus {
   my $lside = shift;
   my $rside = shift;
   return $lside % $rside;
}

sub calcSin {
   my $lside = shift;
   my $rside = shift;
   return sin($rside);
}

sub calcCos {
   my $lside = shift;
   my $rside = shift;
   return cos($rside);
}

sub calcRound {
   my $lside = shift;
   my $rside = shift;
   return int($rside+0.5);
}

sub calcTrunc {
   my $lside = shift;
   my $rside = shift;
   return int($rside);
}

sub calcRand {
   my $lside = shift;
   my $rside = shift;
   return rand($rside);
}

sub calcDice {
   my $lside = shift;
   my $rside = shift;

   my $total = 0;
   for(my $i = 0; $i < $lside; $i++) {
      $total += int(rand($rside) + 1);
   }

   return $total;
}

sub calcAbs {
   my $lside = shift;
   my $rside = shift;
   return abs($rside);
}

sub calcSqrt {
   my $lside = shift;
   my $rside = shift;
   return sqrt($rside);
}

sub calcCombination {
   my $lside = shift;
   my $rside = shift;
   return (calcFactorial($lside))/(calcFactorial($lside-$rside)*calcFactorial($rside));
}

sub calcPermute {
   my $lside = shift;
   my $rside = shift;
   return (calcFactorial($lside))/(calcFactorial($lside-$rside));
}

sub calcAnd {
   use integer;
   my $lside = int shift;
   my $rside = int shift;
   return $lside & $rside;
}

sub calcOr {
   use integer;
   my $lside = int shift;
   my $rside = int shift;
   return $lside | $rside;
}

sub calcXor {
   use integer;
   my $lside = int shift;
   my $rside = int shift;
   return $lside ^ $rside;
}

sub calcNot {
   use integer;
   my $lside = int shift;
   my $rside = int shift;
   return int $rside ^ 0xFFFF;
}

sub calcFactorial {
   my $lside = int shift;
   my $rside = int shift;
   my $result = 1;
   my $count = 0;

   while( $count++ < $lside ) {
      $result = $result * $count ;
   }
   return $result;
}

sub calcLn {
   my $lside = shift;
   my $rside = shift;

   return log($rside);
}

sub calcLog {
   my $lside = shift;
   my $rside = shift;

   return log($rside) / log(10);
}

sub calcLogN {
   my $lside = shift;
   my $rside = shift;

   return log($rside) / log($lside);
}

sub calcExponent {
   my $lside = shift;
   my $rside = shift;
   my $result = $lside;
   my $negative = 0;

   return $lside ** $rside;
}


my %constants = (
   'pi' => { name=>'pi', value=>3.14159265258979323 },
   'g' =>  { name=>'Acc due to Gravity', value=>9.80665 },
   'e' =>  { name=>'ln(1)', value=>2.71828 },
   'N' =>  { name=>'Avogadro number', value=>6.0221367E23 },
   'c' =>  { name=>'Speed of Light', value=>2.99792458E8 },
   'h' =>  { name=>'Planck Constant', value=>2.71828182845905 },
   'l2g' =>  { name=>'gallons per liter', value=>0.264172052 },
   'g2l' =>  { name=>'liters per gallon', value=>3.78541178 },
   'oz2tsp' =>  { name=>'teaspoons per fluid oz', value=>6 },
   'oz2tbsp' =>  { name=>'tablespoons per fluid oz', value=>2 },
   'oz2tbs' =>  { name=>'tablespoons per fluid oz', value=>2 },
   'oz2cup' =>  { name=>'cups per fluid oz', value=>0.12 },
   'ft2inches' =>  { name=>'inches per foot', value=>12 },
   'ft2meter' =>  { name=>'meters per foot', value=>0.3048 },
   'ft2m' =>  { name=>'meters per foot', value=>0.3048 },
   'mi2km' =>  { name=>'miles per kilometer', value=>1.609344 },
   'km2mi' =>  { name=>'kilometers per mile', value=>0.621371192 },
   'm2km' =>  { name=>'kilometers per meter', value=>0.001 },
   'km2m' =>  { name=>'meters per kilometer', value=>1000 },
   'ft2yd' =>  { name=>'yards per foot', value=>0.3333 },
   'lb2kg' =>  { name=>'pounds per kilogram', value=>0.4536 },
   'mi2ft' =>  { name=>'feet per mile', value=>5280 }
);

sub resIdentifier {
   my $id = shift;
   if( $constants{$id} ) {
      my $value = $constants{$id}->{'value'};
      my $name = $constants{$id}->{'name'};
      print "$name($id) is $value. ";
      return $value;
   } else {
      return 0;
   }
}

my %operators = (
   '(' => { function => \&openParen,          priority=>40, lp=>0, rp=>0 },
   ')' => { function => \&closeParen,         priority=>40, lp=>0, rp=>0 },

   'round' => { function => \&calcRound,      priority=>35, lp=>0, rp=>1 },
   'trunc' => { function => \&calcTrunc,      priority=>35, lp=>0, rp=>1 },
   'rand' => { function => \&calcRand,        priority=>35, lp=>0, rp=>1 },
   'sin' => { function => \&calcSin,          priority=>35, lp=>0, rp=>1 },
   'cos' => { function => \&calcCos,          priority=>35, lp=>0, rp=>1 },
   'abs' => { function => \&calcAbs,          priority=>35, lp=>0, rp=>1 },
   'sqrt' => { function => \&calcSqrt,        priority=>35, lp=>0, rp=>1 },
   'd' => { function => \&calcDice,        priority=>35, lp=>1, rp=>1 },

   'ln' => { function => \&calcLn,            priority=>35, lp=>0, rp=>1 },
   'log' => { function => \&calcLog,            priority=>35, lp=>0, rp=>1 },
   'logn' => { function => \&calcLogN,            priority=>35, lp=>1, rp=>1 },

   '^' => { function => \&calcExponent,       priority=>30, lp=>1, rp=>1 },
   '!' => { function => \&calcFactorial,      priority=>30, lp=>1, rp=>0 },
   '*' => { function => \&calcMultiplication, priority=>20, lp=>1, rp=>1 },
   'x' => { function => \&calcMultiplication, priority=>20, lp=>1, rp=>1 },
   '/' => { function => \&calcDivision,       priority=>20, lp=>1, rp=>1 },
   '%' => { function => \&calcModulus,        priority=>20, lp=>1, rp=>1 },
   '+' => { function => \&calcAddition,       priority=>10, lp=>1, rp=>1 },
   '-' => { function => \&calcSubtraction,    priority=>10, lp=>1, rp=>1 },

   'taken' => { function => \&calcCombination,priority=>7, lp=>1, rp=>1 },
   'C' => { function => \&calcCombination,priority=>7, lp=>1, rp=>1 },
   'permute' => { function => \&calcPermute,priority=>7, lp=>1, rp=>1 },
   'P' => { function => \&calcPermute,priority=>7, lp=>1, rp=>1 },

   '&' => { function => \&calcAnd,            priority=>5, lp=>1, rp=>1 },
   '|' => { function => \&calcOr,             priority=>5, lp=>1, rp=>1 },
   'and' => { function => \&calcAnd,          priority=>5, lp=>1, rp=>1 },
   'or' => { function => \&calcOr,            priority=>5, lp=>1, rp=>1 },
   'not' => { function => \&calcNot,          priority=>5, lp=>0, rp=>1 },
   'xor' => { function => \&calcXor,          priority=>5, lp=>1, rp=>1 }
);

sub lexer {
   my $inString = shift;
   my @token;
   my @result;

   $inString=~s/\s+/ /g; 
   while( length($inString) ) {
      $inString=~s/^\s+//g;
      ( $inString=~s/^([\d]*\.[\d]+)// &&
         (@token = ('number', $1))  )    ||
      ( $inString=~s/^([\d]+)// &&
         (@token = ('number', $1))  )    ||
      ( $inString=~s/^([a-zA-Z_][\w]*)// && 
         (@token = ('id', $1))  )    ||
      ( $inString=~s/^(\W)// && 
         (@token = ('operator', $1))  ) ;

      push(@result, { 'type'=>$token[0],  'value'=>$token[1] } ) ;
   }

   my $lastTok;
   my @newResult;
   foreach my $thisTok ( @result ) {
      if(  $thisTok->{'value'} eq '-' ) {
         if( ! $lastTok || 
             ( ($lastTok->{'type'} eq 'operator') && 
                ( ($lastTok->{'value'} eq '(') || ($lastTok->{'rp'}) )
              ) ){
            push(@newResult, { 'type' => 'number',  'value'=> -1 } );
            push(@newResult, { 'type' => 'operator','value'=> '*'} );
         } else {
            push @newResult, $thisTok;
         }
      } else { 
         push @newResult, $thisTok;
      }
      $lastTok = $thisTok;
   }

   return \@newResult;
}

sub parser {
   my $tokens = shift;
   my $maxPriority=0;
   my $maxPriorityIndex=0;
   my $hasOperators = 0;
   my $result = [ ];

   if( scalar @{$tokens} < 1 ) { return 0; }
   if( scalar @{$tokens} == 1 ) {
      my $token = shift @{$tokens};
      if( $token->{'type'} ne "number" ) {
         print "Parse error: $token->{'type'} found when expecting number. ";
         return 0;
      } else {
         return $token->{'value'};
      }
   }

   foreach my $token ( @{$tokens} ) {
      if( $token->{'type'} eq "operator" ) {
         $hasOperators++;
         my $operator = $token->{'value'};
         if( $maxPriority < $operators{$operator}->{'priority'} ) {
            $maxPriority=$operators{$operator}->{'priority'};
         }
      }
   }

   if( $hasOperators < 1 )  { 
      print "Operator expected but value found instead.\n";
      exit(0);
   }


   while( scalar @{$tokens} ) {
      my $token = shift @{$tokens};
      if( $token->{'type'} ne "operator" ) { push @{$result}, $token; }
      else {
         my $operator = $token->{'value'};
         my $opData = $operators{$operator};
         if( $opData->{'priority'} == $maxPriority ) {
            my ($lparam, $rparam);
            
            if( $opData->{'lp'} ) { 
               if( scalar @{$result} > 0 ) { $lparam = pop @{$result}; }
               else {print "$operator requires a left parameter. \n"; exit(0);}
            }
            if( $opData->{'rp'} ) { 
               if( scalar @{$tokens} > 0 ) { $rparam = shift @{$tokens}; }
               else {print "$operator requires a right parameter. \n"; exit(0);}
            }

            if( defined( $operators{$operator} ) ) {
               my $funceval=&{$operators{$operator}->{'function'}}(
                               $lparam?$lparam->{'value'}:0,
                               $rparam?$rparam->{'value'}:0,
                               $tokens
               );
               push( @{$result}, {'type'=>'number', 'value'=>$funceval} );
            } else {
               print "Parse error: unknown operator, $operator.";
               return 0;
            }

         } else { push @{$result}, $token; }
      }
   }
   return parser($result);
}

sub printTokens {
   my $tokens =shift;

   foreach my $token ( @{$tokens} ) {
      print $token->{'value'}." ";
   }
   print "\n";
}

my $expression = shift @ARGV;
my $tokens = lexer($expression);

my $thisString;
my %identifiers;
foreach my $token ( @{$tokens} ) {
   if( $token->{'type'} eq 'id' ) {
      my $id = $token->{'value'};
      my $value;
      if( defined($identifiers{$id}) ) {
         $value = $identifiers{$id};
      } else {
         $value = resIdentifier($token->{'value'});
         $identifiers{$id} = $value;
      }
      if( $value == 0 ) {
         $token->{'type'} = 'operator';
      } else {
         $token->{'type'} = 'number';
         $token->{'value'} = $value;
      }
   }
}

print "result: ".parser($tokens)."\n";
