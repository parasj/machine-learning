#!/bin/bash
WEKAPATH="/opt/homebrew-cask/Caskroom/weka/3.6.13/weka-3-6-13"
OUTPUTPATH="../data/out/svm"

VOTEDATA="../data/vote.arff"
SPAMDATA="../data/spambase.arff"


rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

echo "" > task_cache

svm_poly() {
	OUT="$2-poly-$3"
	echo $OUT
	echo "java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.functions.SMO -split-percentage 70 -t $1 -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E $3\" > \"$OUTPUTPATH/$OUT.wekaout\"" >> task_cache
}

svm_rbf() {
	OUT="$2-rbf-$3"
	echo $OUT
	echo "java -Xmx1000M -cp $WEKAPATH/weka.jar weka.classifiers.functions.SMO -split-percentage 70 -t $1 -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G $3\" > \"$OUTPUTPATH/$OUT.wekaout\"" >> task_cache
}

svm_poly $VOTEDATA "vote" 1
svm_poly $VOTEDATA "vote" 2
svm_poly $VOTEDATA "vote" 3
svm_poly $VOTEDATA "vote" 5
svm_poly $VOTEDATA "vote" 10
svm_poly $SPAMDATA "spam" 1
svm_poly $SPAMDATA "spam" 2
svm_poly $SPAMDATA "spam" 3
svm_poly $SPAMDATA "spam" 5
svm_poly $SPAMDATA "spam" 10

svm_rbf $VOTEDATA "vote" 0.001
svm_rbf $VOTEDATA "vote" 0.010
svm_rbf $VOTEDATA "vote" 0.100
svm_rbf $VOTEDATA "vote" 1.000
svm_rbf $SPAMDATA "spam" 0.001
svm_rbf $SPAMDATA "spam" 0.010
svm_rbf $SPAMDATA "spam" 0.100
svm_rbf $SPAMDATA "spam" 1.000

parallel --eta < task_cache