#!/bin/bash
WEKAPATH="/opt/homebrew-cask/Caskroom/weka/3.6.13/weka-3-6-13"
OUTPUTPATH="../data/out/decisiontree"

VOTEDATA="../data/vote.arff"
SPAMDATA="../data/spambase.arff"


rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

j48_unpruned() {
	echo "$2"
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.trees.J48 -U -M 2 -split-percentage 70 -t $1 > "$OUTPUTPATH/$2.wekaout"
}

j48_pruned() {
	echo "$2-$3"
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.trees.J48 -C $3 -M 2 -split-percentage 70 -t $1 > "$OUTPUTPATH/$2-$3.wekaout"
}

j48_unpruned $VOTEDATA "vote-unpruned"
j48_unpruned $SPAMDATA "spam-unpruned"

j48_pruned $VOTEDATA "vote-pruned" 0.0625
j48_pruned $SPAMDATA "spam-pruned" 0.0625

j48_pruned $VOTEDATA "vote-pruned" 0.125
j48_pruned $SPAMDATA "spam-pruned" 0.125

j48_pruned $VOTEDATA "vote-pruned" 0.25
j48_pruned $SPAMDATA "spam-pruned" 0.25

j48_pruned $VOTEDATA "vote-pruned" 0.5
j48_pruned $SPAMDATA "spam-pruned" 0.5