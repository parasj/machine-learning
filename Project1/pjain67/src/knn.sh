#!/bin/bash
WEKAPATH="/opt/homebrew-cask/Caskroom/weka/3.6.13/weka-3-6-13"
OUTPUTPATH="../data/out/knn"

VOTEDATA="../data/vote.arff"
SPAMDATA="../data/spambase.arff"


rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

knn_unweighted() {
	OUT="$2-unweighted-$3"
	echo $OUT
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.lazy.IBk -K $3 -W 0 -split-percentage 70 -t $1 -A "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"" > "$OUTPUTPATH/$OUT.wekaout" &
}

knn_weighted() {
	OUT="$2-weighted-$3"
	echo $OUT
	java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.lazy.IBk -K $3 -F -W 0 -split-percentage 70 -t $1 -A "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"" > "$OUTPUTPATH/$OUT.wekaout" &
}

knn_unweighted $VOTEDATA "vote" 1
knn_unweighted $VOTEDATA "vote" 5
knn_unweighted $VOTEDATA "vote" 10
knn_unweighted $VOTEDATA "vote" 15
knn_unweighted $VOTEDATA "vote" 20
knn_unweighted $VOTEDATA "vote" 30
knn_unweighted $VOTEDATA "vote" 100

wait

knn_weighted $VOTEDATA "vote" 1
knn_weighted $VOTEDATA "vote" 5
knn_weighted $VOTEDATA "vote" 10
knn_weighted $VOTEDATA "vote" 15
knn_weighted $VOTEDATA "vote" 20
knn_weighted $VOTEDATA "vote" 30
knn_weighted $VOTEDATA "vote" 100

wait

knn_unweighted $SPAMDATA "spam" 1
knn_unweighted $SPAMDATA "spam" 5
knn_unweighted $SPAMDATA "spam" 10
knn_unweighted $SPAMDATA "spam" 15
knn_unweighted $SPAMDATA "spam" 20
knn_unweighted $SPAMDATA "spam" 30
knn_unweighted $SPAMDATA "spam" 100

wait

knn_weighted $SPAMDATA "spam" 1
knn_weighted $SPAMDATA "spam" 5
knn_weighted $SPAMDATA "spam" 10
knn_weighted $SPAMDATA "spam" 15
knn_weighted $SPAMDATA "spam" 20
knn_weighted $SPAMDATA "spam" 30
knn_weighted $SPAMDATA "spam" 100

wait