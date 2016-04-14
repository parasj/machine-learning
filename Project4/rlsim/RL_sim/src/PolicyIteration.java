import java.util.Date;
import java.util.Vector;

public class PolicyIteration implements Algorithms {

	private double precision;
	private boolean converged;
	private int[][] currPolicy;
	private Maze myMaze;
	private double pjog;
	private ValueFunction currValues;
	private ValueFunction prevValues;
	private int iters;
	private long timeTaken;
	
	private int valueIters;
	
	private static int MAX_VALUE_ITERS;//=100;
	private static int MAX_VALUE_ALLOWED;//=5000;
	
	static class Properties {
	    public static int PJOG = 1;
	    public static int ConvergenceError = 2;
	    public static int ValueFunctionLimit = 3;
	    public static int IterationLimit = 4;
	}
	
	public PolicyIteration(Maze _maze, double _pjog, double convError, int valueFuncLimit, int iterLimit)
	{
		myMaze = _maze;
		pjog = _pjog;
		MAX_VALUE_ALLOWED = valueFuncLimit;
		MAX_VALUE_ITERS = iterLimit;
		
		currValues = new ValueFunction(myMaze.width, myMaze.height);
		prevValues = new ValueFunction(myMaze.width, myMaze.height);
		currPolicy = new int[myMaze.width][myMaze.height];
		precision = convError;
		converged = false;
		initialize();
	}

	public void initialize()
	{
		currValues.initialize();
	    converged = false;
		iters =0;
		timeTaken=0;
		
		for(int i=0;i<myMaze.width;i++)
			for(int j=0;j<myMaze.height;j++)
				currPolicy[i][j] = Action.UP; 
	}
	
	public void setProperty (int name, String value)
	{
	    if(name==Properties.PJOG) {
	        pjog = Double.parseDouble(value);
	    }
	    else if (name==Properties.ConvergenceError){
	         precision = Double.parseDouble(value);
	    }
	    else if (name==Properties.ValueFunctionLimit){
	         MAX_VALUE_ALLOWED = Integer.parseInt(value);
	    }
	    else if (name==Properties.IterationLimit){
	        MAX_VALUE_ITERS = Integer.parseInt(value);
	    }
	}
	
	public int getNumOfIters()
	{
	    return iters;
	}
	
	public void execute(int numIters)
	{
		for(int iter=0; iter<numIters; iter++) {
			step();
		}
	}
	
	public ValueFunction getValueFunction()
	{
		return prevValues;
	}
	
	public int[][] getPolicy()
	{
		return currPolicy;
	}
	
	public long getTime()
	{
	    return timeTaken;
	}
	
	public boolean step()
	{
	    long startTime = new Date().getTime();
		State currState;
		State desiredNextState;
		double prob, safe;
		double v=0,minV=10000;
		
		double maxDelta = 0;
		double delta = 0;;
		boolean valueConverged = false;
		
		if(converged) {
		    long endTime = new Date().getTime();
			timeTaken += (endTime - startTime);
			return true;
		}
		
		/*
		 * This for loop calculates the value of each state based on the policy
		 * so based on current policy -> currValues is updated
		 */

		int valueIters=0;
		double maxV;
		while (!valueConverged && valueIters<MAX_VALUE_ITERS)
//		while (!valueConverged)
		{
		    maxDelta = 0;
		    maxV =0;
		    //System.out.println("valueIters"+valueIters);
			for(int i=0; i<myMaze.width; i++) {
				for(int j=0; j<myMaze.height;j++) {
					v = 0;
				    currState = new State(i,j);
				    if(myMaze.goals.contains(currState)) {
				        currValues.stateValue[i][j] = 0;
				        continue;
				    }
					Vector allNext = new Vector(myMaze.getSuccessors(currState));	//vector of successor states
					desiredNextState = Action.performAction(currState, currPolicy[i][j]);
					
				    for(int m=0;m<allNext.size();m++)
				    {
			            State s = (State)allNext.get(m);
			            
			            if(!desiredNextState.equals(s))
			                prob = pjog/(Action.numActions-1);
			            else
			                prob = 1-pjog;
			            
			            if(myMaze.isValidTransition(currState,s))
			                safe = prevValues.stateValue[s.x][s.y];
			            else
			                safe = myMaze.getReward(currState,s) + prevValues.stateValue[i][j];
			            
			            v += prob*safe;        
				    }
				    v=v+1;
				    currValues.stateValue[i][j] = v;
				    maxV = (maxV<v) ? v : maxV;
				    
					delta = Math.abs(currValues.stateValue[i][j] - prevValues.stateValue[i][j]);
					//System.out.println(", maxdelta:"+maxDelta+", delta:"+delta);
					if(maxDelta<delta) {
						maxDelta = delta;
						//System.out.println("================new maxdelta= "+maxDelta);
					}
				}
			}
			//System.out.print("maxValue:"+maxV);
			//System.out.println(", maxdelta:"+maxDelta);
			valueIters++;
			
			if(maxDelta<precision)
				valueConverged = true;
			
			if(maxV>MAX_VALUE_ALLOWED)
			    valueConverged = true;
			
			for(int i=0;i<myMaze.width;i++)
				for(int j=0;j<myMaze.height;j++)
					prevValues.stateValue[i][j] = currValues.stateValue[i][j]; 
		
			currValues.initialize();
		}

		/*
		 *this loop updates the policy based on the current values calculated in the above "for" loop
		 *so based on currValues the policy is updated 
		 */
		converged=true;
		int action=0;
		for(int i=0; i<myMaze.width; i++) {
			for(int j=0; j<myMaze.height;j++){
				currState = new State(i,j);
			    if(myMaze.goals.contains(currState)) {
			    	currPolicy[i][j]=-1;
			    	continue;
			    }

			    minV=Double.MAX_VALUE;
				for (int a=0; a<Action.numActions; a++) {
				    State nextState = Action.performAction(currState, a);
				    if(!myMaze.isValidTransition(currState,nextState))
				        continue;
				    
				    double vl = prevValues.stateValue[nextState.x][nextState.y];
		            if (minV>vl){
	            		minV = vl;
	            		action = a;
		            }			    
		   		}

        		if(currPolicy[i][j]!=action) {	            		    
        		    //System.out.println(iters+"here:"+currPolicy[i][j]+","+action);
        		    converged=false;
        		    currPolicy[i][j]=action;
        		}
        		
			}
		}
		iters++;
		long endTime = new Date().getTime();
		timeTaken += (endTime - startTime);
		return converged;	
	}
}
