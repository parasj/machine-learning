#!/bin/bash
WEKAPATH="/opt/homebrew-cask/Caskroom/weka/3.6.13/weka-3-6-13"
OUTPUTPATH="../data/out/boosting"

VOTEDATA="../data/vote.arff"
SPAMDATA="../data/spambase.arff"


rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

adaboost_pruned() {
	OUT="$2-pruned-$3-$4"
	echo $OUT
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I $3 -split-percentage 70 -t $1 -W weka.classifiers.trees.J48 -- -C $4 -M 2  > "$OUTPUTPATH/$OUT.wekaout"
}

adaboost_unpruned() {
	OUT="$2-unpruned-$3"
	echo $OUT
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I $3 -split-percentage 70 -t $1 -W weka.classifiers.trees.J48 -- -U -M 2  > "$OUTPUTPATH/$OUT.wekaout"
}

adaboost_unpruned $VOTEDATA "vote" 10
adaboost_unpruned $VOTEDATA "vote" 50
adaboost_unpruned $VOTEDATA "vote" 100
adaboost_unpruned $SPAMDATA "spam" 10
adaboost_unpruned $SPAMDATA "spam" 50
adaboost_unpruned $SPAMDATA "spam" 100

adaboost_pruned $VOTEDATA "vote" 10 0.125
adaboost_pruned $VOTEDATA "vote" 50 0.125
adaboost_pruned $VOTEDATA "vote" 100 0.125
adaboost_pruned $SPAMDATA "spam" 10 0.125
adaboost_pruned $SPAMDATA "spam" 50 0.125
adaboost_pruned $SPAMDATA "spam" 100 0.125

adaboost_pruned $VOTEDATA "vote" 10 0.25
adaboost_pruned $VOTEDATA "vote" 50 0.25
adaboost_pruned $VOTEDATA "vote" 100 0.25
adaboost_pruned $SPAMDATA "spam" 10 0.25
adaboost_pruned $SPAMDATA "spam" 50 0.25
adaboost_pruned $SPAMDATA "spam" 100 0.25

adaboost_pruned $VOTEDATA "vote" 10 0.5
adaboost_pruned $VOTEDATA "vote" 50 0.5
adaboost_pruned $VOTEDATA "vote" 100 0.5
adaboost_pruned $SPAMDATA "spam" 10 0.5
adaboost_pruned $SPAMDATA "spam" 50 0.5
adaboost_pruned $SPAMDATA "spam" 100 0.5