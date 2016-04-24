1.  Setup IDE to allow weka imports.  I used Eclipse.  If you want to allow weka imports you must add them to the java build path under preferences.  Or you can use the command line, in which case, you'll need to add the weka api to your class path.  You will also need the jars necessary to import Java.io and java.net.URL .  You must also import the StudentFilters package.  The jar is included with my submission.  You will need the TPP jar as well which I have included with my submission.  

2.  The data sets are included. chess_king_rook_king_pawn_training and  tictactoe_training_nom_labels are used to run all the experiments except TPP. chess_king_rook_king_pawn_training_tpp and tictactoe_training_nom_labels_tpp are used for TPP.

3.  Code is included that breaks down training data into percentage increments, builds the classifier, and applies the classifier to cross validation.  The results are output in a .dat file.  The class title ChessSet outputs the .dat files for the Chess data set.  The ChessSet.java files must be in the assignment1 package as indicated by line 1 of both files.  The data sets chess_king_rook_king_pawn_training and chess_king_rook_king_pawn_training_tpp must also be in the same folder as ChessSet.java.

4.  You will need GNUPlot to plot the data.  It can be found at http://sourceforge.net/projects/gnuplot/?source=typ_redirect

5.  Run the code and it will generate .dat files for each algorithms training and test set for False negative rate and false positive rate.  In order for the code to work the data sets must be in the same directory as the code class.  These .dat files can be plugged into GNUPlot. 


6.  In GNUPlot make sure the terminal type is set to 'wxt'.  There should be a message output on startup informing you of your terminal type. In the GNUPlot cmd line cd to the directory containing the .dat files.   Enter the following command:

	plot 'ANNTrainingXX.dat' u 1:2 smooth bezier title "Training Set",'ANNTestXXX.dat' u 1:2 smooth bezier title "Test Set",'ANNTrainingXXX.dat' u 1:2 smooth bezier title "Training Set" dashtype (20,20) linecolor rgbcolor "red",'ANNTestXXX.dat' u 1:2 smooth bezier title "Test Set" dashtype (20,20)
	
	where ANNTrainingXXX.dat and ANNTestXXX.dat are the names of your .dat files containing the error rates.  Replace the XXX with PCA, ICA, RP, or TPP for part 4.  For part 5 replace XXX with EM or KMeans if you want the error vs k graphs.  Use EMLC or KMeansLC as the suffix if you want the learning curves for the clusters as a feature.  
	
7.  In Weka to apply the dimensional reductions techniques:
		- load in your data set
		- in preprocessing tab click filters -> unsupervised -> attribute and then choose the technique you want to use.
		
		For TPP you must go to the Select Attribute tab
			- For Attribute Evaluator choose TPPAttributeEvaluation
			- For Search Method choose TPPAttributeSearch
			- Right click the result in the Results list and choose save reduced data.
			- Now go back to preprocessing tab and choose the saved data set
			- Go to clustering tab and run cluster experiments.  
	
8.  The kurtosis plots were made by saving the data in csv format in excel after applying ICA.  Excel was used to compute and plot the kurtosis.  

9.  To get eigenvalues of attributes go to Select attributes tab and select PrincipalComponents in the Attribute Evaluator section.  

10.  To recreate cluster plots: go to Clustering tab.  Run your cluster algorithm and in results list right click and choose Visualize cluster assignments.  	
	
References

1.  The Chess Data set is the Chess (King-Rook vs King-Pawn) Data set obtained from the UCI Repository at 
	Lichman, M. (2013). UCI Machine Learning Repository [http://archive.ics.uci.edu/ml]. Irvine, CA: University of California, School of Information and Computer Science

	-The specific URL for this data set is
	 http://archive.ics.uci.edu/ml/datasets/Chess+%28King-Rook+vs.+King-Pawn%29	
	 
2.  The Tic Tac Toe set is the Tic-Tac_toe Endgame Data Set obtain from the UCI Repository at
	Lichman, M. (2013). UCI Machine Learning Repository [http://archive.ics.uci.edu/ml]. Irvine, CA: University of California, School of Information and Computer Science	

	-The specific URL for this data set is
	 http://archive.ics.uci.edu/ml/datasets/Tic-Tac-Toe+Endgame
	 
3.  The Weka Java API was used to implement the algorithms.  The API can be found at 
		http://weka.sourceforge.net/doc.stable/		
		
4.  Machine Learning, Tom Mitchell, McGraw Hill, 1997	

5.  Student Filters which includes the FastICA:
		https://github.com/cgearhart/students-filters/
		
6.  TPP for the Targeted Projection Pursuit:
		http://weka.sourceforge.net/packageMetaData/TPP/index.html