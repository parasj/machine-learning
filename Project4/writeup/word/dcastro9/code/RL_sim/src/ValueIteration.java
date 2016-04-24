
import java.util.Date;
import java.util.Vector;


public class ValueIteration implements Algorithms
{
	private double precision;
	private boolean converged;
	private double pjog;
	private final int pathCost = 1;
	private Maze myMaze;
	private ValueFunction currValues;
	private ValueFunction prevValues;
	private int[][] policy;
	private int iters;
	private long timeTaken;
	
	static class Properties {
	    public static int PJOG=1;
	    public static int ConvergenceError=2;
	}
	
	public ValueIteration(Maze _maze, double _pjog, double convError)
	{
		myMaze = _maze;
		pjog = _pjog;
		precision = convError;
		
		currValues = new ValueFunction(myMaze.width, myMaze.height);
		prevValues = new ValueFunction(myMaze.width, myMaze.height);
		policy = new int[myMaze.width][myMaze.height];
		initialize();
	}
	
	public void initialize()
	{
	    converged = false;
	    iters = 0;
	    timeTaken=0;
		currValues.initialize();
		prevValues.initialize();
		
		for(int i=0;i<myMaze.width;i++)
			for(int j=0;j<myMaze.height;j++)
				policy[i][j] = Action.UP; 
	}
	
	public void setProperty (int name, String value)
	{
	    if(name==Properties.PJOG) {
	        pjog = Double.parseDouble(value);
	    }
	    else if (name==Properties.ConvergenceError){
	         precision = Double.parseDouble(value);
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
		return policy;
	}
	
	public boolean step()
	{
	    long startTime = new Date().getTime();
		State currState;
		State desiredNextState;
		
		double value,minV;
		double prob, safe;
		double maxDelta = 0;
		double delta;
		
		if(converged) {
		    long endTime = new Date().getTime();
			timeTaken += (endTime - startTime);
			return true;
		}
		    
		for(int i=0; i<myMaze.width; i++) {
			for(int j=0; j<myMaze.height;j++) {
			    minV= Integer.MAX_VALUE;
			    currState = new State(i,j);
			    
			    if(myMaze.goals.contains(currState)) {
			    	currValues.stateValue[i][j]=0;
			    	policy[i][j]=-1;
			    	continue;
			    }
			    
			    Vector allNext = new Vector(myMaze.getSuccessors(currState));	//vector of states
				
			    for (int a=0; a<Action.numActions; a++) {
			    	desiredNextState = Action.performAction(currState, a);
				    value=0;
				    for(int m=0;m<allNext.size();m++) {
			            State s = (State)allNext.get(m);
			            //Utility.show(s.x+","+s.y+" "+"->"+myMaze.getReward(s.x,s.y));
			            
			            if(!desiredNextState.equals(s))
			                prob = pjog/(Action.numActions-1);
			            else
			                prob = 1-pjog;
			            
			            if(myMaze.isValidTransition(currState,s))
			                safe = prevValues.stateValue[s.x][s.y];
			            else
			                safe = myMaze.getReward(currState,s) + prevValues.stateValue[i][j];
			            
			            value += prob*safe;
				    }
				    value = 1+value;
				    
			        if (minV>value) {
			            minV = value;
			            policy[i][j]=a;
			        }
		   		}
				//Utility.show("");
				currValues.stateValue[i][j] = minV;
				delta = Math.abs(currValues.stateValue[i][j] - prevValues.stateValue[i][j]);
				maxDelta = (maxDelta<delta) ? delta : maxDelta;
			}	
		}
		converged = (maxDelta<precision) ? true : false;
			
		//Assign currValues to prevValues
		for(int i=0;i<myMaze.width;i++){
			for(int j=0;j<myMaze.height;j++){
				prevValues.stateValue[i][j] = currValues.stateValue[i][j]; 
			}
		}
		
		currValues.initialize();
		iters++;
		
		long endTime = new Date().getTime();
		timeTaken += (endTime - startTime);
		
		return converged;
	}
	
	public long getTime()
	{
	    return timeTaken;
	}
	
	/* used for debugging only...
	 * displays on the console the values of all the states as a result of value iteration. 
	 * also alongside the values it prints the best action to be taken based on the values
	 */
	void displayValues(double[][] val,int[][] policy)
	{
	    Utility.show("Curr values are");
	    for(int i=myMaze.height-1;i>=0;i--) {
	        for(int j=0;j<myMaze.width;j++) {
                System.out.print((int)Utility.dec3(val[j][i])+" ");
                switch(policy[j][i]) {
                	case Action.UP:
                		System.out.print("^");
                		break;
                	case Action.DOWN:
                		System.out.print("v");
                		break;
                	case Action.LEFT:
                		System.out.print("<");
                		break;
                	case Action.RIGHT:
                		System.out.print(">");
                		break;
				}
                System.out.print("\t");
	        }
	        Utility.show();
	    }
	    
	}
	
	/* used for debugging only...
	 * prints on the console the states inside the container 'next'
	 */
	void printNextStates(Vector next)
	{
	    State s = new State(0,0);
	    for(int i=0;i<next.size();i++) {
	        s = (State)next.get(i);
	        System.out.print(s.x+","+s.y+" ");
	    }
	}
}