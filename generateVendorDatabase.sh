#!/bin/sh
curl -O http://standards-oui.ieee.org/oui.txt | sed -n "s/[ \t]*(base 16)[ \t]*//p" | dos2unix > app/src/main/res/raw/oui.txt
