#!/bin/sh

RESULT=`wget -q -O - "http://www.urbandictionary.com/define.php?term=$1"`

echo "$RESULT" | grep -i "<i>$1</i> isn't defined" > /dev/null 2>&1 
if [ $? -eq 0 ]; then
	echo "No Definition Found"
	exit 0
fi

echo "$RESULT" | grep '<div class="definition">' | tr '[A-Z]' '[a-z]' | sed -e 's/<[^>]*>//g' | \
	sed -e 's/who has/with/g' | 
	sed -e 's/people/ppl/g' | 
	sed -e 's/one/1/g' | 
	sed -e 's/two/2/g' | 
	sed -e 's/three/3/g' | 
	sed -e 's/four/4/g' | 
	sed -e 's/usually/often/g' | 
	sed -e 's/&quot;/"/g' | 
	sed -e 's/&amp;/\&/g' | 
	sed -e 's/refer[r]*ing to/about/g' | 
	sed -e 's/larger and larger/larger/g' | 
	sed -e 's/got it all wrong/are wrong/g' 2> /dev/null

