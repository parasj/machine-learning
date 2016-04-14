/*
 * Created on Oct 22, 2004
 *
 * 
 */

/**
 * @author ryk
 *
 */

public class ValueFunction{
	
	double[][] stateValue;
	double[][][] qValues;
	
	//this constructor is used when you have knowledge about the environment
	//example by valueIteration and policy iteration - model based algorithms
	public ValueFunction(int width, int height)
	{
		stateValue = new double[width][height];
		qValues = null;
	}

	//this constructor is used when you donr have any knowledge about the environment
	//example by Q learning prioritized sweeping - model free algorithms
	public ValueFunction(int width, int height, int numOfActions)
	{
		stateValue = null;
		qValues = new double[width][height][numOfActions];
	}
	
	/* used for debugging only...
	 * displays the values as a result of the value updation on the console
	 */
	void displayValues()
	{
		for(int i=0;i<stateValue.length;i++) {
			for(int j=0;j<stateValue[i].length;j++) {
				System.out.print(Utility.dec3(stateValue[i][j])+"\t");
			}
			//System.out.println();
		}
		System.out.println();
	}
	
	/*initializes all the state values to zero
	 */
	public void initialize()
	{
		for(int i=0;i<stateValue.length;i++)
			for (int j=0;j<stateValue[i].length;j++)
				stateValue[i][j] = 0;
	}
	
	/* for value iteration & policy iteration - returns the maximum of the statevalues
	 * for Q learning & prioritized sweeping - returns the maximum of the Q values
	 */
	public double getMax()
	{
		if(null!=stateValue)
		{
			double max=stateValue[0][0];
			for(int i=0;i<stateValue.length;i++)
				for(int j=0;j<stateValue[i].length;j++)
					if(stateValue[i][j]>max)
						max=stateValue[i][j];
			return max;		
		}
		else 
		{
			double max=qValues[0][0][0];
			
			for (int i=0;i<qValues.length;i++)
				for(int j=0;j<qValues[i].length;j++)
					for(int k=0;k<qValues[i][j].length;k++)
						if (max < qValues[i][j][k])
							max = qValues[i][j][k];

						
			return max;		
		}
	}
	
}
