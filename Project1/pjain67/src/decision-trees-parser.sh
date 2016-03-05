#!/bin/bash

DATAPATH="../data/out/decisiontree"
DATAPATHOUT="../data/out/decisiontree/parsed"
mkdir -p $DATAPATHOUT

parse() {
	if [[ -f $1 ]]; then
		OUT=$DATAPATHOUT/$(basename $1)
		cat $1 | grep "Options" > $OUT
		cat $1 | grep "Number of Leaves" >> $OUT
		cat $1 | grep "Size of the tree" >> $OUT
		cat $1 | grep "Correctly Classified Instances" >> $OUT
		cat $1 | grep "Time taken to build model" >> $OUT
		cat $1 | grep "Time taken to test model on training split" >> $OUT
	fi
}

for file in $DATAPATH/*
do
	parse $file
done