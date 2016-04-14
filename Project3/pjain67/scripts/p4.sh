#!/bin/bash
export OUTPUTPATH="../data/out/p4"
export VOTEDATA="vote.arff"
export SPAMDATA="spambase.arff"
export DATADIR="../data/out/reduce"

export PCA_SPAM="spam-pca-28-.7.arff"
export ICA_SPAM="spam-ica--1.arff"
export ICA_SPAM2="spam-ica-reduced.arff"
export RP_SPAM="spam-rp-10-35.arff"
export RS_SPAM="spam-rs-35.arff"

rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

nn() {
	id=$( date +%N )
	OUT="$2-$3"

	java -Xmx1500M -cp weka.jar \
		weka.classifiers.functions.MultilayerPerceptron \
		-L 0.3 \
		-M 0.1 \
		-N $3 \
		-V 0 \
		-S 0 \
		-E 20 \
		-H a -split-percentage 70 \
		-t $1/$2 -no-cv > $OUTPUTPATH/$OUT.wekaout

	echo $OUT
}
export -f nn

# sem -j -1 nn $DATADIR $PCA_SPAM
sem -j -1 nn $DATADIR $ICA_SPAM 500
sem -j -1 nn $DATADIR $ICA_SPAM 2000
# sem -j -1 nn $DATADIR $ICA_SPAM 5000
# sem -j -1 nn $DATADIR $ICA_SPAM 10000
# sem -j -1 nn $DATADIR $RP_SPAM
# sem -j -1 nn $DATADIR $RS_SPAM
# sem -j -1 nn "../data/in" $SPAMDATA

sem --wait
wait