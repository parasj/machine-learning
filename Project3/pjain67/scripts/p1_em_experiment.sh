#!/bin/bash
OUTPUTPATH="../data/out/EMExperiment1"

VOTEDATA="../data/in/vote.arff"
SPAMDATA="../data/in/spambase.arff"

rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH/logs


EMVote() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar weka.clusterers.EM -I 500 -N 2 -M $1 -S 100 -t $VOTEDATA -c last > $OUTPUTPATH/logs/vote-$1.log
	RESULT=$(cat $OUTPUTPATH/logs/vote-$1.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	echo "$1	$RESULT"
}

EMSpam() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar weka.clusterers.EM -I 500 -N 2 -M $1 -S 100 -t $SPAMDATA -c last > $OUTPUTPATH/logs/spam-$1.log
	RESULT=$(cat $OUTPUTPATH/logs/spam-$1.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	echo "$1	$RESULT"
}

# echo "Vote"
# EMVote 1.0E-1
# EMVote 1.0E-3
# EMVote 1.0E-6
EMVote 1.0E-9
# EMVote 1.0E-12

# echo "Spam"
# EMSpam 1.0E-1
# EMSpam 1.0E-3
# EMSpam 1.0E-6
EMSpam 1.0E-9
# EMSpam 1.0E-12