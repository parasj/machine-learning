#!/bin/bash
export OUTPUTPATH="../data/out/p5"
export DATADIR="../data/out/p3"

rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH

nn() {
	id=$( date +%N )
	OUT="$3"

	java -Xmx1500M -cp weka.jar \
		weka.classifiers.functions.MultilayerPerceptron \
		-L 0.3 \
		-M 0.1 \
		-N 500 \
		-V 0 \
		-S 0 \
		-E 20 \
		-H a -split-percentage 70 \
		-t "$1/$2" -no-cv > $OUTPUTPATH/$OUT.wekaout

	RESULT=$(cat $OUTPUTPATH/$OUT.wekaout | grep "Correctly Classified Instances")

	echo "$3"
	echo "$RESULT"
}
export -f nn

sem -j -1 nn $DATADIR em-spam-ica--1.arff-.arff "ICA-EM"
sem -j -1 nn $DATADIR em-spam-pca-28-.7.arff-.arff "PCA-EM"
sem -j -1 nn $DATADIR em-spam-rp-10-35.arff-.arff "RP-EM"
sem -j -1 nn $DATADIR em-spam-rs-35.arff-.arff "RS-EM"

sem -j -1 nn $DATADIR kmeans-spam-ica--1.arff-2.arff "ICA-K-means"
sem -j -1 nn $DATADIR kmeans-spam-pca-28-.7.arff-2.arff "PCA-K-means"
sem -j -1 nn $DATADIR kmeans-spam-rp-10-35.arff-2.arff "RP-K-means"
sem -j -1 nn $DATADIR kmeans-spam-rs-35.arff-2.arff "RS-K-means"

sem --wait
wait