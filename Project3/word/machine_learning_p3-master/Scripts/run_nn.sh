#!/bin/bash
set -e

data_src="/home/qk/Projects/machine_learning_p3/Data/Breast_Cancer_Dataset/"
output_src="/home/qk/Projects/machine_learning_p3/Results/Breast_Cancer_Dataset/Neural_Network_after_Dim_Reduction_Results/"
custom_ext=".txt"

for entry in "$data_src"/*
do
	curr_file=$(basename "$entry")
	file_no_ext="${curr_file%.*}"
	#echo "$data_src$curr_file"
	java -Xmx1024m weka.classifiers.functions.MultilayerPerceptron -L 0.03 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a -t $data_src$curr_file >> $output_src$file_no_ext$custom_ext
done