#!/bin/bash
WEKAPATH="/opt/homebrew-cask/Caskroom/weka/3.6.13/weka-3-6-13"
OUTPUTPATH="../data/out/decisiontree_learn"

VOTEDATA="../data/vote.arff"
SPAMDATA="../data/spambase.arff"


rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

j48_unpruned() {
	echo "$2-$3"
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.trees.J48 -U -M 2 -split-percentage $3 -t $1 > "$OUTPUTPATH/$2-$3.wekaout"
}


j48_unpruned $SPAMDATA "spam-unpruned" 1
j48_unpruned $SPAMDATA "spam-unpruned" 2
j48_unpruned $SPAMDATA "spam-unpruned" 3
j48_unpruned $SPAMDATA "spam-unpruned" 4
j48_unpruned $SPAMDATA "spam-unpruned" 5
j48_unpruned $SPAMDATA "spam-unpruned" 7
j48_unpruned $SPAMDATA "spam-unpruned" 10
j48_unpruned $SPAMDATA "spam-unpruned" 20
j48_unpruned $SPAMDATA "spam-unpruned" 30
j48_unpruned $SPAMDATA "spam-unpruned" 40
j48_unpruned $SPAMDATA "spam-unpruned" 50
j48_unpruned $SPAMDATA "spam-unpruned" 60
j48_unpruned $SPAMDATA "spam-unpruned" 70
j48_unpruned $SPAMDATA "spam-unpruned" 80
j48_unpruned $SPAMDATA "spam-unpruned" 90


j48_unpruned $VOTEDATA "vote-unpruned" 1
j48_unpruned $VOTEDATA "vote-unpruned" 2
j48_unpruned $VOTEDATA "vote-unpruned" 3
j48_unpruned $VOTEDATA "vote-unpruned" 4
j48_unpruned $VOTEDATA "vote-unpruned" 5
j48_unpruned $VOTEDATA "vote-unpruned" 7
j48_unpruned $VOTEDATA "vote-unpruned" 10
j48_unpruned $VOTEDATA "vote-unpruned" 10
j48_unpruned $VOTEDATA "vote-unpruned" 20
j48_unpruned $VOTEDATA "vote-unpruned" 30
j48_unpruned $VOTEDATA "vote-unpruned" 40
j48_unpruned $VOTEDATA "vote-unpruned" 50
j48_unpruned $VOTEDATA "vote-unpruned" 60
j48_unpruned $VOTEDATA "vote-unpruned" 70
j48_unpruned $VOTEDATA "vote-unpruned" 80
j48_unpruned $VOTEDATA "vote-unpruned" 90
