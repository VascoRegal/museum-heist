#!/bin/bash

if [ ! -n "$1" ];
then
	printf "usage:\n sh run.sh <log_file>\n"
	exit 1	
fi

find . -name "*.class" -type f -print0 | xargs -0 /bin/rm -f
javac MuseumHeist.java
java  MuseumHeist $1
