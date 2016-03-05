#!/bin/bash
WEKAPATH="/opt/homebrew-cask/Caskroom/weka/3.6.13/weka-3-6-13"
OUTPUTPATH="../data/out/boosting_learn"

VOTEDATA="../data/vote.arff"
SPAMDATA="../data/spambase.arff"


rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

boost() {
	echo "$2-$3"
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -split-percentage $3 -t $1 -W weka.classifiers.trees.J48 -- -U -M 2  > "$OUTPUTPATH/$2-$3.wekaout"
}


boost $SPAMDATA "spam-unpruned" 1
boost $SPAMDATA "spam-unpruned" 2
boost $SPAMDATA "spam-unpruned" 3
boost $SPAMDATA "spam-unpruned" 4
boost $SPAMDATA "spam-unpruned" 5
boost $SPAMDATA "spam-unpruned" 7
boost $SPAMDATA "spam-unpruned" 10
boost $SPAMDATA "spam-unpruned" 20
boost $SPAMDATA "spam-unpruned" 30
boost $SPAMDATA "spam-unpruned" 40
boost $SPAMDATA "spam-unpruned" 50
boost $SPAMDATA "spam-unpruned" 60
boost $SPAMDATA "spam-unpruned" 70
boost $SPAMDATA "spam-unpruned" 80
boost $SPAMDATA "spam-unpruned" 90
