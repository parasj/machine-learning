#!/bin/bash
export OUTPUTPATH="../data/out/reduce"
export VOTEDATA="../data/in/vote.arff"
export VOTEREALDATA="../data/in/vote_numeric.arff"
export SPAMDATA="../data/in/spambase.arff"

# rm -r $OUTPUTPATH
mkdir -p $OUTPUTPATH/logs

pca_vote() {
	java -Xmx1000M -classpath $CLASSPATH:./weka.jar weka.filters.supervised.attribute.AttributeSelection \
			-c last \
			-E "weka.attributeSelection.PrincipalComponents -R $1 -A 5" \
			-S "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N -1" \
			-i $VOTEDATA \
			-o $OUTPUTPATH/vote-pca-$1.arff

	java -classpath $CLASSPATH:./weka.jar weka.attributeSelection.PrincipalComponents \
			-R $1 \
			-s "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N -1" \
			-i $VOTEDATA > $OUTPUTPATH/logs/vote-pca-$1.log

	NUMBER=$(cat $OUTPUTPATH/logs/vote-pca-$1.log | grep "Selected attributes" | awk -F: '{print $NF}' | tr -d '[[:space:]]')
	echo "$NUMBER	$1"

	cat $OUTPUTPATH/vote-pca-$1.arff > $OUTPUTPATH/vote-pca-$NUMBER-$1.arff
	rm $OUTPUTPATH/vote-pca-$1.arff
}
export -f pca_vote

pca_spam() {
	java -Xmx1000M -classpath $CLASSPATH:./weka.jar weka.filters.supervised.attribute.AttributeSelection \
			-c last \
			-E "weka.attributeSelection.PrincipalComponents -R $1 -A 5" \
			-S "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N -1" \
			-i $SPAMDATA \
			-o $OUTPUTPATH/spam-pca-$1.arff

	java -classpath $CLASSPATH:./weka.jar weka.attributeSelection.PrincipalComponents \
			-R $1 \
			-s "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N -1" \
			-i $SPAMDATA > $OUTPUTPATH/logs/spam-pca-$1.log

	NUMBER=$(cat $OUTPUTPATH/logs/spam-pca-$1.log | grep "Selected attributes" | awk -F: '{print $NF}' | tr -d '[[:space:]]')
	echo "$NUMBER	$1"

	cat $OUTPUTPATH/spam-pca-$1.arff > $OUTPUTPATH/spam-pca-$NUMBER-$1.arff
	rm $OUTPUTPATH/spam-pca-$1.arff
}
export -f pca_spam

ica_vote() {
	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.Run -no-scan weka.filters.unsupervised.attribute.IndependentComponents \
			-W -A $1 -N 200 -T 1.0E-4 \
			-c last \
			-i $VOTEREALDATA \
			-o $OUTPUTPATH/vote-ica-$1.arff

	# NUMBER=$(cat $OUTPUTPATH/logs/spam-ica-$1.log | grep "Selected attributes" | awk -F: '{print $NF}' | tr -d '[[:space:]]')
	echo "$NUMBER	$1"

	# cat $OUTPUTPATH/spam-pca-$1.arff > $OUTPUTPATH/spam-pca-$NUMBER-$1.arff
	# rm $OUTPUTPATH/spam-pca-$1.arff
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $VOTEDATA -o $OUTPUTPATH/vote-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/vote-ica-$1.log
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $SPAMDATA -o $OUTPUTPATH/spam-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/spam-ica-$1.log
}
export -f ica_vote

ica_spam() {
	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.Run -no-scan weka.filters.unsupervised.attribute.IndependentComponents \
			-W -A $1 -N 200 -T 1.0E-4 \
			-c last \
			-i $SPAMDATA \
			-o $OUTPUTPATH/spam-ica-$1.arff

	# NUMBER=$(cat $OUTPUTPATH/logs/spam-ica-$1.log | grep "Selected attributes" | awk -F: '{print $NF}' | tr -d '[[:space:]]')
	echo "$NUMBER	$1"

	# cat $OUTPUTPATH/spam-pca-$1.arff > $OUTPUTPATH/spam-pca-$NUMBER-$1.arff
	# rm $OUTPUTPATH/spam-pca-$1.arff
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $VOTEDATA -o $OUTPUTPATH/vote-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/vote-ica-$1.log
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $SPAMDATA -o $OUTPUTPATH/spam-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/spam-ica-$1.log
}
export -f ica_spam

rp_vote() {
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar weka.filters.supervised.attribute.AttributeSelection \
	# 		-c last \
	# 		-E "weka.attributeSelection.PrincipalComponents -R $1 -A 5" \
	# 		-S "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N -1" \
	# 		-i $SPAMDATA \
	# 		-o $OUTPUTPATH/spam-pca-$1.arff

	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.filters.unsupervised.attribute.RandomProjection \
			-N $1 -D SPARSE1 -M -R $2 \
			-c last \
			-i $VOTEDATA \
			-o $OUTPUTPATH/vote-rp-$1-$2.arff

	# NUMBER=$(cat $OUTPUTPATH/logs/spam-ica-$1.log | grep "Selected attributes" | awk -F: '{print $NF}' | tr -d '[[:space:]]')
	echo "$NUMBER	$1	$2"

	# cat $OUTPUTPATH/spam-pca-$1.arff > $OUTPUTPATH/spam-pca-$NUMBER-$1.arff
	# rm $OUTPUTPATH/spam-pca-$1.arff
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $VOTEDATA -o $OUTPUTPATH/vote-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/vote-ica-$1.log
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $SPAMDATA -o $OUTPUTPATH/spam-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/spam-ica-$1.log
}
export -f rp_vote


rp_spam() {
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar weka.filters.supervised.attribute.AttributeSelection \
	# 		-c last \
	# 		-E "weka.attributeSelection.PrincipalComponents -R $1 -A 5" \
	# 		-S "weka.attributeSelection.Ranker -T -1.7976931348623157E308 -N -1" \
	# 		-i $SPAMDATA \
	# 		-o $OUTPUTPATH/spam-pca-$1.arff

	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.filters.unsupervised.attribute.RandomProjection \
			-N $1 -D SPARSE1 -M -R $2 \
			-c last \
			-i $SPAMDATA \
			-o $OUTPUTPATH/spam-rp-$1-$2.arff

	# NUMBER=$(cat $OUTPUTPATH/logs/spam-ica-$1.log | grep "Selected attributes" | awk -F: '{print $NF}' | tr -d '[[:space:]]')
	echo "$NUMBER	$1	$2"

	# cat $OUTPUTPATH/spam-pca-$1.arff > $OUTPUTPATH/spam-pca-$NUMBER-$1.arff
	# rm $OUTPUTPATH/spam-pca-$1.arff
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $VOTEDATA -o $OUTPUTPATH/vote-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/vote-ica-$1.log
	# java -Xmx1000M -classpath $CLASSPATH:./weka.jar:./StudentFilters.jar weka.filters.unsupervised.attribute.IndependentComponent -i $SPAMDATA -o $OUTPUTPATH/spam-ica-$1.arff -W -A $1 -N 200 -T 1E-4 > $OUTPUTPATH/logs/spam-ica-$1.log
}
export -f rp_spam

rs_vote() {
	echo "$1"
	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.filters.unsupervised.attribute.RandomSubset \
			-S $1 \
			-c last \
			-i $VOTEDATA \
			-o $OUTPUTPATH/vote-rs-$1.arff
}
export -f rs_vote

rs_spam() {
	echo "$1"
	java -Xmx1000M -classpath $CLASSPATH:./weka.jar \
			weka.filters.unsupervised.attribute.RandomSubset \
			-S $1 \
			-c last \
			-i $SPAMDATA \
			-o $OUTPUTPATH/spam-rs-$1.arff
}
export -f rs_spam

# echo "# of PCA clusters	% Variance Captured"
# sem -j -1 pca_vote .95
# sem -j -1 pca_vote .9
# sem -j -1 pca_vote .8
# sem -j -1 pca_vote .75
# sem -j -1 pca_vote .7
# sem -j -1 pca_vote .6
# sem -j -1 pca_vote .5
# sem -j -1 pca_vote .4

# echo "# of PCA clusters	% Variance Captured"
# sem -j -1 pca_spam .95
# sem -j -1 pca_spam .9
# sem -j -1 pca_spam .8
# sem -j -1 pca_spam .75
# sem -j -1 pca_spam .7
# sem -j -1 pca_spam .6
# sem -j -1 pca_spam .5
# sem -j -1 pca_spam .4
# sem -j -1 pca_spam .3
# sem -j -1 pca_spam .2
# sem -j -1 pca_spam .1
# sem -j -1 pca_spam .05

# echo "ICA vote"
# sem -j -1 ica_vote -1

# echo "ICA spam"
# sem -j -1 ica_spam -1

# echo "RandomProjection vote"
# for (( i = 20; i < 60; i++ )); do
# 	sem -j -1 rp_vote 10 $i
# done

# echo "RandomProjection vote"
# for (( i = 20; i < 60; i++ )); do
# 	sem -j -1 rp_vote 10 $i
# done

echo "RandomSubsets vote"
for (( i = 20; i < 60; i++ )); do
	sem -j -1 rp_vote 10 $i
	sem -j -1 rs_vote $i
	sem -j -1 rp_spam 10 $i
	sem -j -1 rs_spam $i
done

sem --wait; echo sem --wait done
wait