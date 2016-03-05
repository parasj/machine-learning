#!/bin/bash

DATAPATH="../data/out/knn"
DATAPATHOUT="../data/out/knn/parsed"

rm -r $DATAPATHOUT
mkdir -p $DATAPATHOUT

parse() {
	if [[ -f $1 ]]; then
		OUT=$DATAPATHOUT/$(basename $1)
		perf=$(grep "Correctly Classified Instances" $1 | cut -c53- | rev | cut -c2- | rev | sed 's/^[ \t]*//;s/[ \t]*$//' | tr "\n" "\t")
		t1=$(cat $1 | grep "Time taken to build model"  | cut -c27- | rev | cut -c8- | rev | sed 's/^[ \t]*//;s/[ \t]*$//')
		t2=$(cat $1 | grep "Time taken to test model on training split"  | cut -c44- | rev | cut -c8- | rev | sed 's/^[ \t]*//;s/[ \t]*$//')
		echo "$perf	$t1	$t2" > $OUT
	fi
}

for file in $DATAPATH/*
do
	parse $file
done
