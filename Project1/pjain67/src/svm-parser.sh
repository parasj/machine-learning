#!/bin/bash

DATAPATH="../data/out/svm"
DATAPATHOUT="../data/out/svm/parsed"

rm -r $DATAPATHOUT
mkdir -p $DATAPATHOUT

parse() {
	if [[ -f $1 ]]; then
		OUT=$DATAPATHOUT/$(basename $1)
		grep "Correctly Classified Instances" $1 | cut -c53- | rev | cut -c2- | rev | sed 's/^[ \t]*//;s/[ \t]*$//' | tr "\n" "\t" > $OUT
		echo "" >> $OUT
		cat $1 | grep "Time taken to build model" >> $OUT
		cat $1 | grep "Time taken to test model on training split" >> $OUT
	fi
}

for file in $DATAPATH/*
do
	parse $file
done
