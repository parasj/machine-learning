#!/bin/bash
WEKAPATH="/opt/homebrew-cask/Caskroom/weka/3.6.13/weka-3-6-13"
OUTPUTPATH="../data/out/neuralnet"

VOTEDATA="../data/vote.arff"
SPAMDATA="../data/spambase.arff"


rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

nn() {
	OUT="$2-L$3-M$4-E$5"
	# nn DATA NAME LEARNINGRATE MOMENTUM EPOCHS
	echo "java -Xmx4000M -cp $WEKAPATH/weka.jar weka.classifiers.functions.MultilayerPerceptron -L $3 -M $4 -N $5 -V 0 -S 0 -E 20 -H a -split-percentage 70 -t $1 -no-cv > $OUTPUTPATH/$OUT.wekaout" >> nnrunner.sh
}

echo "" > nnrunner.sh

nn $VOTEDATA "vote" 0.1 0 64
nn $VOTEDATA "vote" 0.1 0 256
nn $VOTEDATA "vote" 0.1 0 1024
nn $VOTEDATA "vote" 0.1 0 4096
nn $VOTEDATA "vote" 0.1 0 16384

nn $VOTEDATA "vote" 0.4 0 64
nn $VOTEDATA "vote" 0.4 0 256
nn $VOTEDATA "vote" 0.4 0 1024
nn $VOTEDATA "vote" 0.4 0 4096
nn $VOTEDATA "vote" 0.4 0 16384

nn $VOTEDATA "vote" 0.1 0.2 64
nn $VOTEDATA "vote" 0.1 0.2 256
nn $VOTEDATA "vote" 0.1 0.2 1024
nn $VOTEDATA "vote" 0.1 0.2 4096
nn $VOTEDATA "vote" 0.1 0.2 16384


nn $VOTEDATA "vote" 0.1 0.2 64
nn $VOTEDATA "vote" 0.1 0.2 256
nn $VOTEDATA "vote" 0.1 0.2 1024
nn $VOTEDATA "vote" 0.1 0.2 4096
nn $VOTEDATA "vote" 0.1 0.2 16384



nn $SPAMDATA "spam" 0.1 0 64
nn $SPAMDATA "spam" 0.1 0 256
nn $SPAMDATA "spam" 0.1 0 1024
nn $SPAMDATA "spam" 0.1 0 4096
nn $SPAMDATA "spam" 0.1 0 16384

nn $SPAMDATA "spam" 0.4 0 64
nn $SPAMDATA "spam" 0.4 0 256
nn $SPAMDATA "spam" 0.4 0 1024
nn $SPAMDATA "spam" 0.4 0 4096
nn $SPAMDATA "spam" 0.4 0 16384

nn $SPAMDATA "spam" 0.1 0.2 64
nn $SPAMDATA "spam" 0.1 0.2 256
nn $SPAMDATA "spam" 0.1 0.2 1024
nn $SPAMDATA "spam" 0.1 0.2 4096
nn $SPAMDATA "spam" 0.1 0.2 16384


nn $SPAMDATA "spam" 0.1 0.2 64
nn $SPAMDATA "spam" 0.1 0.2 256
nn $SPAMDATA "spam" 0.1 0.2 1024
nn $SPAMDATA "spam" 0.1 0.2 4096
nn $SPAMDATA "spam" 0.1 0.2 16384

./neural-network-parallel-runner.shjig