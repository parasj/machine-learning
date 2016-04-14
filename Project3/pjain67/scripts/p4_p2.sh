#!/bin/bash
export OUTPUTPATH="../data/out/p4_p2"
export VOTEDATA="vote.arff"
export SPAMDATA="spambase.arff"
export DATADIR="../data/out/reduce"

export PCA_SPAM1="spam-pca-28-.7.arff"
export PCA_SPAM1="spam-pca-28-.7.arff"
export PCA_SPAM1="spam-pca-28-.7.arff"
export PCA_SPAM1="spam-pca-28-.7.arff"
export PCA_SPAM1="spam-pca-28-.7.arff"

rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

nn() {
	id=$( date +%N )
	OUT="$3"

	java -Xmx1500M -cp weka.jar \
		weka.classifiers.functions.MultilayerPerceptron \
		-L 0.3 \
		-M 0.1 \
		-N 500 \
		-V 0 \
		-S 0 \
		-E 20 \
		-H a -split-percentage 70 \
		-t "$1/$2" -no-cv > $OUTPUTPATH/$OUT.wekaout

	RESULT=$(cat $OUTPUTPATH/$OUT.wekaout | grep "Correctly Classified Instances")

	echo "$3"
	echo "$RESULT"
}
export -f nn

sem -j -1 nn $DATADIR spam-pca-1-.05.arff .05
sem -j -1 nn $DATADIR spam-pca-1-.1.arff .1
sem -j -1 nn $DATADIR spam-pca-11-.4.arff .4
sem -j -1 nn $DATADIR spam-pca-16-.5.arff .5
sem -j -1 nn $DATADIR spam-pca-22-.6.arff .6
sem -j -1 nn $DATADIR spam-pca-28-.7.arff .7
sem -j -1 nn $DATADIR spam-pca-3-.2.arff .2
sem -j -1 nn $DATADIR spam-pca-32-.75.arff .75
sem -j -1 nn $DATADIR spam-pca-35-.8.arff .8
sem -j -1 nn $DATADIR spam-pca-44-.9.arff .9
sem -j -1 nn $DATADIR spam-pca-49-.95.arff .95
sem -j -1 nn $DATADIR spam-pca-7-.3.arff .3

sem --wait
wait