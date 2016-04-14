Machine Learning Project 3
Username: dcastro9
Email: dcastro9@gatech.edu
Name: Daniel Castro

Directory Breakdown:

Code/ABAGAIL/ : This is the ABAGAIL Java directory, it was modified in order to properly output 
.arff files for the dimensionality reduction problem for Independent Component Analysis. Everything
else was run with Weka. If you'd like to reproduce the results, the following command should do it:
'sh Code/ABAGAIL/run_experiments.sh'

Data/ : Contains the data files, including dimensionality reduction results.

Paper/ : Contains the LaTeX files for compiling my paper (pdf is available though).

Results/ : Contains all of the results in their respective folders. Dimensionality Reduction results
can be found under Data/ since the output are processed data files.

Scripts/ : Contains a bash script for running my neural network on all of my data files. Made to my
local directory so this probably will not run without modifications to the script. It just runs the
Weka Multilayer Perceptron on all the data files (can be done easily through the UI).


