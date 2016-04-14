CS 4641: Unsupervised Learning and Dimensionality Reduction
===========================================================

My code for reproducing the results given in the analysis for CS 4641 Assignment 3. It should be importable and runnable in Eclipse with two dependencies: Weka 3.6.9 and ABAGAIL (from my fork at https://github.com/nybblr/abagail).

Source Code
-----------
All the results for clustering and dimensionality reduction were computed in WEKA; configurations are included alongside the results in the analysis. However, the ICA transform is not included in WEKA, and thus is computed with ABAGAIL and can be recomputed with the ReduceRunner. The path to the dataset will vary depending on your system, so change the paths in ReduceRunner and DataSetWorker before executing.

ProjectRunner can also be used to run NN training, compute other transforms, and cluster, but is not tested to be usable since end results for all the algorithms (other than ICA) were computed in WEKA.
