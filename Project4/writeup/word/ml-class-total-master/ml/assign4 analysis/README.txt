1.  BURLAP was used for this assignment.  Download it from https://github.com/jmacglashan/burlap

2.  A few files in BURLAP were modified to allow the graphs to be made.  The classes modified are ValueIteration, PolicyIteration, LearningAlgorithmExperimenter, Policy.  These are included in the submission.  

3.  ExampleGridWorld was modified and has been included.  You can use this class to generate all the results and graphs shown in the analysis.  The small grid world is set as default.  If you want to use the large grid world comment out lines 81-91, 226-227, 528, 698-699 and uncomment lines 97 - 184, 224-225, 527, 696-697.  To generate the data files graphs you must run ExampleGridWorld.  WARNING: the main method contains everything that was run for this experiment.  Running all of it will take many hours.  Instead you can comment out things you are not looking for.  Theres some comments littered throughout the main method that will indicate what each section of the code is running what.

	- to generate the .dat files to plot the iterations, time, and rewards vs gamma you will need to uncomment lines 776 - 819.  Comment out line 755.
		-For the large grid world you need to change gamma to 0.98 on line 687.  Also you must change line 800 to gamma = gamma + 0.001;
		-For the large grid world you must comment out lines 579 and 611 or the program will run forever because the agent can't reach the goal state.
	- to generate the SARSA curves, cumulative steps, average rewards etc. uncomment line 755.  You can comment out line 776 - 819.
		-The defaults for the learning factories are set to run the curves for changing intial Q's.  To view the learning rate curves comment out line 730 and uncomment line 731.
		-For the large grid world comment out line 743 and uncomment line 744.


4.  You will need GNUPlot to plot the data.  It can be found at http://sourceforge.net/projects/gnuplot/?source=typ_redirect

5.  Run the code and it will generate .dat files for each algorithms training and test set for False negative rate and false positive rate.  In order for the code to work the data sets must be in the same directory as the code class.  These .dat files can be plugged into GNUPlot.



 6.  In GNUPlot make sure the terminal type is set to 'wxt'.  There should be a message output on startup informing you of your terminal type. In the GNUPlot cmd line cd to the directory containing the .dat files.   Enter the following commands:
 
 plot 'VIIter.dat' u 1:2 smooth bezier title "VI",'PIIter.dat' u 1:2 smooth bezier title "PI"

 plot 'VITime.dat' u 1:2 smooth bezier title "VI",'PITime.dat' u 1:2 smooth bezier title "PI"
 
 plot 'VIReward.dat' u 1:2 smooth bezier title "VI",'PIReward.dat' u 1:2 smooth bezier title "PI"
 
  plot 'PIInnerIter.dat' u 1:2 smooth bezier title "PI"
 
	The first command will plot the iterations graph and the second command will plot the time graph.  The third command will plot the reward graphs.  The fourth command will generate the inner value iterations graph for PI.  
	
	
	
References

1.  Machine Learning, Tom Mitchell, McGraw Hill, 1997	

2.  BURLAP code https://github.com/jmacglashan/burlap

3.  Learning exploration strategies in reinforcement based learning.
  		https://flowers.inria.fr/mlopes/myrefs/13-aamas-leo.pdf
		
4.  Reinforcement Learning.  http://home.deib.polimi.it/restelli/MyWebSite/pdf/rl5.pdf		

5.  Value Iteration. 
		https://www.cs.cmu.edu/afs/cs/project/jair/pub/volume4/kaelbling96a-html/node19.html
	