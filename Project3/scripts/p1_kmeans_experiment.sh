#!/bin/bash
OUTPUTPATH="../data/out/KMeansDistExperiment"

VOTEDATA="../data/in/vote.arff"
SPAMDATA="../data/in/spambase.arff"

# rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH/logs


kmeansEucVote() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar weka.clusterers.SimpleKMeans \
		-t $VOTEDATA \
		-O $OUTPUTPATH/test.arff \
		-N $1 -A "weka.core.EuclideanDistance -R first-last" -I 500 -S 10 -c last > $OUTPUTPATH/logs/vote-kmeans-euclidean-$1.log
	# RESULT=$(cat $OUTPUTPATH/logs/vote-kmeans-euclidean-$1.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	RESULT=$(cat $OUTPUTPATH/logs/vote-kmeans-euclidean-$1.log | grep "Within cluster sum of squared errors" | tr -dc '0-9.')
	echo "$1	$RESULT"
}

kmeansEucSpam() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar weka.clusterers.SimpleKMeans -t $SPAMDATA -N $1 -A "weka.core.EuclideanDistance -R first-last" -I 500 -S 10 -c last > $OUTPUTPATH/logs/spam-kmeans-euclidean-$1.log
	# RESULT=$(cat $OUTPUTPATH/logs/spam-kmeans-euclidean-$1.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	RESULT=$(cat $OUTPUTPATH/logs/spam-kmeans-euclidean-$1.log | grep "Within cluster sum of squared errors" | tr -dc '0-9.')
	echo "$1	$RESULT"
}

kmeansManVote() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar weka.clusterers.SimpleKMeans -t $VOTEDATA -N $1 -A "weka.core.ManhattanDistance -R first-last" -I 500 -S 10 -c last > $OUTPUTPATH/logs/vote-kmeans-manhattan-$1.log
	RESULT=$(cat $OUTPUTPATH/logs/vote-kmeans-manhattan-$1.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	echo "$1	$RESULT"
}

kmeansManSpam() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar weka.clusterers.SimpleKMeans -t $SPAMDATA -N $1 -A "weka.core.ManhattanDistance -R first-last" -I 500 -S 10 -c last > $OUTPUTPATH/logs/spam-kmeans-manhattan-$1.log
	RESULT=$(cat $OUTPUTPATH/logs/spam-kmeans-manhattan-$1.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	echo "$1	$RESULT"
}

echo "EuclideanDistance-Vote"
kmeansEucVote 1
# kmeansEucVote 2
# kmeansEucVote 3
# kmeansEucVote 4
# kmeansEucVote 5
# kmeansEucVote 6
# kmeansEucVote 7
# kmeansEucVote 8
# kmeansEucVote 10
# kmeansEucVote 20
# kmeansEucVote 30
# kmeansEucVote 40
# kmeansEucVote 50
# kmeansEucVote 100
# kmeansEucVote 150

# echo "EuclideanDistance-Spam"
# kmeansEucSpam 1
# kmeansEucSpam 2
# kmeansEucSpam 3
# kmeansEucSpam 4
# kmeansEucSpam 5
# kmeansEucSpam 6
# kmeansEucSpam 7
# kmeansEucSpam 8
# kmeansEucSpam 10
# kmeansEucSpam 20
# kmeansEucSpam 30
# kmeansEucSpam 40
# kmeansEucSpam 50
# kmeansEucSpam 100
# kmeansEucSpam 150

# echo "ManhattanDistance-Vote"
# kmeansManVote 1
# kmeansManVote 2
# kmeansManVote 3
# kmeansManVote 4
# kmeansManVote 5
# kmeansManVote 6
# kmeansManVote 7
# kmeansManVote 8
# kmeansManVote 10
# kmeansManVote 20
# kmeansManVote 30
# kmeansManVote 40
# kmeansManVote 50
# kmeansManVote 100
# kmeansManVote 150

# echo "ManhattanDistance-Spam"
# kmeansManSpam 1
# kmeansManSpam 2
# kmeansManSpam 3
# kmeansManSpam 4
# kmeansManSpam 5
# kmeansManSpam 6
# kmeansManSpam 7
# kmeansManSpam 8
# kmeansManSpam 10
# kmeansManSpam 20
# kmeansManSpam 30
# kmeansManSpam 40
# kmeansManSpam 50
# kmeansManSpam 100
# kmeansManSpam 150