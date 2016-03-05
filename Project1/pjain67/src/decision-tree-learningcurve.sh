#!/bin/bash

DATAPATH="../data/out/decisiontree_learn"
DATAPATHOUT="../data/out/decisiontree_learn/parsed"

rm -r $DATAPATHOUT
mkdir -p $DATAPATHOUT

parse() {
	if [[ -f $1 ]]; then
		OUT=$DATAPATHOUT/$(basename $1)
		grep "Root mean squared error" $1 | cut -c33- | sed 's/^[ \t]*//;s/[ \t]*$//' | tr "\n" "\t" > $OUT
	fi
}

for file in $DATAPATH/*
do
	parse $file
done
