#!/bin/bash
export OUTPUTPATH="../data/out/p3"
export VOTEDATA="../data/in/vote.arff"
export SPAMDATA="../data/in/spambase.arff"
export DATADIR="../data/out/reduce/"
export PCA_SPAM="spam-pca-28-.7.arff"
export PCA_VOTE="vote-pca-5-.7.arff"
export ICA_SPAM="spam-ica--1.arff"
export ICA_VOTE="vote-ica--1.arff"

export RP_VOTE1="vote-rp-10-35.arff"
export RP_VOTE2="vote-rp-10-45.arff"
export RP_VOTE3="vote-rp-10-55.arff"

export RP_SPAM1="spam-rp-10-35.arff"
export RP_SPAM2="spam-rp-10-45.arff"
export RP_SPAM3="spam-rp-10-55.arff"

export RS_VOTE1="vote-rs-35.arff"
export RS_VOTE2="vote-rs-45.arff"
export RS_VOTE3="vote-rs-55.arff"

export RS_SPAM1="spam-rs-35.arff"
export RS_SPAM2="spam-rs-45.arff"
export RS_SPAM3="spam-rs-55.arff"



rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH/logs



# kmeans $DATAFILE N 
kmeans() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar \
		weka.clusterers.SimpleKMeans \
		-t $DATADIR$1 \
		-N $2 \
		-A "weka.core.EuclideanDistance -R first-last" \
		-I 500 \
		-S 10 \
		-c last > $OUTPUTPATH/logs/kmeans-$1-$2.log

	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.filters.unsupervised.attribute.AddCluster \
			-W "weka.clusterers.SimpleKMeans -N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10" \
			-c last \
			-i $DATADIR$1 \
			-o $OUTPUTPATH/kmeans-$1-$2.arff

	# RESULT=$(cat $OUTPUTPATH/logs/kmeans-$1-$2.log | grep "Within cluster sum of squared errors" | tr -dc '0-9.')
	RESULT=$(cat $OUTPUTPATH/logs/kmeans-$1-$2.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	echo "kmeans($1, $2)	$RESULT"
}
export -f kmeans

# em $DATAFILE
em() {
	java -Xmx2000M -classpath $CLASSPATH:./weka.jar \
		weka.clusterers.EM \
		-I 500 \
		-N 2 \
		-M 1e-6 \
		-S 100 \
		-t $DATADIR$1 \
		-c last > $OUTPUTPATH/logs/em-$1.log

	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.filters.unsupervised.attribute.AddCluster \
			-W "weka.clusterers.EM -I 100 -N 2 -M 1.0E-6 -S 100" \
			-c last \
			-i $DATADIR$1 \
			-o $OUTPUTPATH/em-$1-$2.arff

	RESULT=$(cat $OUTPUTPATH/logs/em-$1.log | grep "Incorrectly clustered instances" | cut -c 40- | tr -d '[[:space:]]')
	echo "em($1)	$RESULT"
}
export -f em


# echo "PCA -> kmeans, em"
# sem -j -1 kmeans $PCA_VOTE 2
# sem -j -1 em $PCA_VOTE
sem -j -1 kmeans $PCA_SPAM 2
sem -j -1 em $PCA_SPAM

# echo "ICA -> kmeans, em"
# sem -j -1 kmeans $ICA_VOTE 2
# sem -j -1 em $ICA_VOTE
sem -j -1 kmeans $ICA_SPAM 2
sem -j -1 em $ICA_SPAM

sem --wait

echo "RP -> kmeans, em"
# sem -j -1 kmeans $RP_VOTE1 2
# sem -j -1 em $RP_VOTE1

# sem -j -1 kmeans $RP_VOTE2 2
# sem -j -1 em $RP_VOTE2

# sem -j -1 kmeans $RP_VOTE3 2
# sem -j -1 em $RP_VOTE3

sem -j -1 kmeans $RP_SPAM1 2
sem -j -1 em $RP_SPAM1

# sem -j -1 kmeans $RP_SPAM2 2
# sem -j -1 em $RP_SPAM2

# sem -j -1 kmeans $RP_SPAM3 2
# sem -j -1 em $RP_SPAM3

echo "RS -> kmeans, em"
# sem -j -1 kmeans $RS_VOTE1 2
# sem -j -1 em $RS_VOTE1

# sem -j -1 kmeans $RS_VOTE2 2
# sem -j -1 em $RS_VOTE2

# sem -j -1 kmeans $RS_VOTE3 2
# sem -j -1 em $RS_VOTE3

sem -j -1 kmeans $RS_SPAM1 2
sem -j -1 em $RS_SPAM1

# sem -j -1 kmeans $RS_SPAM2 2
# sem -j -1 em $RS_SPAM2

# sem -j -1 kmeans $RS_SPAM3 2
# sem -j -1 em $RS_SPAM3

sem --wait
wait