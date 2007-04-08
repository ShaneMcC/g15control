#!/bin/sh
COMPILECOMMAND="javac uk/org/dataforce/g15/plugins/*/*.java uk/org/dataforce/g15/G15Control.java"
RUNCOMMAND="java uk.org.dataforce.g15.G15Control"

if [ "$1" = "fork" ]; then
	$COMPILECOMMAND && nohup $RUNCOMMAND >/dev/null 2>&1 &
	echo "Forked"	
else 
	$COMPILECOMMAND && $RUNCOMMAND
fi
