CS4641 Project 3
Paras Jain, pjain67, 903080163

Bash scripts are in scripts/
ABIGAIL java code is in code

You need gradle from gradle.org

# Instructions to run
## Phase 1: Kmeans/EM
- run p1_em_experiment.sh and p1_kmeans_experiment

## Phase 2: dimensionality reduction algorithms
- run p2_runner.sh
- for ica, use the student expansion pack from http://weka.sourceforge.net/packageMetaData/StudentFilters/index.html
- for kurtosis, use abigail. Go to code/ and type gradle run

## Phase 3: Kmeans/EM + dimensionality reduction algorithms
- use scripts/p3.sh

## Phase 4: dimensionality reduction algorithms -> NN
- use scripts/p4.sh and scripts/p4_p2.sh


## Phase 4: Kmeans/EM + dimensionality reduction algorithms -> NN
- use scripts/p5.sh