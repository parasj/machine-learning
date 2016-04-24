
1.  The code to run random hill clibing, simulated annealing, and genetic algs is contained in onne class called CrxTest.java.  This class takes in 1 cmd line parameter that can be used to change number of training iterations.  It prints out the algorithm name (abbreviated) along with 5 numbers.  They are, in this order, number of iterations, training error, testing error, training time, testing time.

2.  The code included will output dat files which are used in GNUPlot.  The dat files follow a naming convention.  There are 3 files *_test_error which contain the number of iterations used and the test erro associated with those iterations.  There are 3 files *_train_error which contain the number of iterations used and the training error associated with those iterations.  There are 3 files named *_train_time and *_test_time which include the number of iterations and the test or training error associated.  The prefixes on each of these files are GA, RHC, and SA which stand for Genetic Algorithm, Random hill Climbing, and Simulated Annealing.

3.  You will need GNUPlot to plot the data.  It can be found at http://sourceforge.net/projects/gnuplot/?source=typ_redirect

4.  Run the code and it will generate .dat files for each algorithm.  In order for the code to work the data sets must be in the same directory as the code class.  These .dat files can be plugged into GNUPlot.    

5.  In GNUPlot make sure the terminal type is set to 'wxt'.  There should be a message output on startup informing you of your terminal type. In the GNUPlot cmd line cd to the directory containing the .dat files.   Enter the following command:

		 plot '*_train_error.dat' u 1:2 smooth bezier title "Training Error",'*_test_error.dat' u 1:2 smooth bezier title "Test Error"

	Replace the * with SA, GA, or RHC depending on what you're looking for.


6.  To find the sum of swuared error for the ANN used in assignment 1 luanch weka.  Use the tictactoe_training.arff file that is provided to train a MultiLayerPerceptron.  Set the MultiLayerPerceptron hidden layers to 7.  After training the net use the tictactoe_test.arff file as a test set.  The GUI will show a sum of squared errors in its output.






References:

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

5.  Code used to generate weights for neural network was obtained from Adam Acosta and Pushkar in our class off the Piazza forums.

6.  Code used to for Random hill climbing, genetic algs, simulated annealing, and MIMIC was obtained from Pushkar's ABAGAIL libraries on github.
