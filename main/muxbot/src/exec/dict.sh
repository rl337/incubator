#!/bin/sh

wget -q -O - "http://www.merriam-webster.com/dictionary/$1" | \
	grep 'Main Entry:' | \
	sed -e 's/.*Main Entry\w*:\w*//g' | \
	sed -e 's/<[^>]*>/ /g' | \
	sed -e 's/[ ][ ]*/ /g' | \
	sed -e 's/&gt;/>/g' | \
	sed -e 's/&lt;/</g' \
	2> /dev/null
