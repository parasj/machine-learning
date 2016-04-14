#!/bin/bash
set -e

# Compile
ant

# Runs ICA on Breast Cancer Dataset
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/wdbc.txt' 1 30 569 >> '/home/qk/Projects/machine_learning_p3/Data/Breast_Cancer_Dataset/ica_1_reduced.arff'
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/wdbc.txt' 3 30 569 >> '/home/qk/Projects/machine_learning_p3/Data/Breast_Cancer_Dataset/ica_3_reduced.arff'
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/wdbc.txt' 5 30 569 >> '/home/qk/Projects/machine_learning_p3/Data/Breast_Cancer_Dataset/ica_5_reduced.arff'
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/wdbc.txt' 10 30 569 >> '/home/qk/Projects/machine_learning_p3/Data/Breast_Cancer_Dataset/ica_10_reduced.arff'
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/wdbc.txt' 15 30 569 >> '/home/qk/Projects/machine_learning_p3/Data/Breast_Cancer_Dataset/ica_15_reduced.arff'

# Runs ICA on Mammographic Masses Dataset
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/mm.txt' 1 5 961 >> '/home/qk/Projects/machine_learning_p3/Data/Mammographic_Masses_Dataset/ica_1_reduced.arff'
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/mm.txt' 2 5 961 >> '/home/qk/Projects/machine_learning_p3/Data/Mammographic_Masses_Dataset/ica_2_reduced.arff'
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/mm.txt' 3 5 961 >> '/home/qk/Projects/machine_learning_p3/Data/Mammographic_Masses_Dataset/ica_3_reduced.arff'
java -cp ABAGAIL.jar shared.test.IndependentComponentAnalysisTest 'src/datasets/mm.txt' 4 5 961 >> '/home/qk/Projects/machine_learning_p3/Data/Mammographic_Masses_Dataset/ica_4_reduced.arff'