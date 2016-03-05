#!/bin/bash

DATAPATH="../data/out/boosting"
DATAPATHOUT="../data/out/boosting/parsed"
mkdir -p $DATAPATHOUT

parse() {
	if [[ -f $1 ]]; then
		OUT=$DATAPATHOUT/$(basename $1)
		cat $1 | grep "Options" > $OUT
		cat $1 | grep "Number of performed Iterations" >> $OUT
		cat $1 | grep "Correctly Classified Instances" >> $OUT
		cat $1 | grep "Time taken to build model" >> $OUT
		cat $1 | grep "Time taken to test model on training split" >> $OUT
	fi
}

for file in $DATAPATH/*
do
	parse $file
done