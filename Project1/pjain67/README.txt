# CS4641: Project 1
Author: Paras Jain <paras@gatech.edu>

## Instructions for running experiments
Weka was used to run the experiments:
1. Download the Weka jar from https://weka.wikispaces.com. Copy the path to the jar.
2. In src/, you will find a series of shell scripts. In each, you will need to update the WEKAPATH to the path to your Weka jar.
3. Run the appropriate script as necessary.

The general structure of the shell scripts is there is one to run the Weka experiments and then a parser that parses the wekaout files to extract relevant data.

## Decision trees
- bash ./decision-trees.sh
- bash ./learning_curve_decision_tree.sh
- bash ./decision-trees-learningcurve.sh
- bash ./decision-trees-parser.sh

## Boosting
- bash ./boosting.sh
- bash ./boosting-learningcurve.sh
- bash ./boosting-learningcurve-parser.sh
- bash ./boosting-parser.sh

## SVMs
- bash ./svm.sh
- bash ./svm-parser.sh

## kNN
- bash ./knn.sh
- bash ./knn-parser.sh

## Neural networks
- bash ./neural-networks.sh
- bash ./neural-networks-parallel-runner.sh
- bash ./neuralnet-parser.sh

