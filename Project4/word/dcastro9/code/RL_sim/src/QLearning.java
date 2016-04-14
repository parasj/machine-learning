
import java.util.Random;
import java.util.Vector;

public class QLearning //implements Algorithms
{
	private double maxLearningRate, pjog,epsilon;
	private final int pathCost = 1;
	private Maze myMaze;
	private int[][] policy;
	private double[][][] qsa;
	private boolean decayingLR;
	
	public boolean isBestAct = true;
	public boolean receivedPenalty = false;
	State start, currState;
	
	private ValueFunction currValues;
    private int numEpisodes;
    
    double learningRate;
    
    ValueFunction optVals;
    ValueFunction evaluatedVals;
    int[][] optPolicy;
    boolean isOptValCalc;
    double PRECISION = 0.01;

	
	//QLSimulator ui;
	
	static class Properties {
	    public static int PJOG=1;
	    public static int LearningRate=2;
	    public static int Epsilon=3;
	    public static int DecayingLR=4;
	}
	
	public QLearning(Maze _maze, double _pjog, double _lr, double _epsilon, boolean _decayingLR)
	{
		myMaze = _maze;
		pjog = _pjog;
		maxLearningRate = _lr;
		epsilon = _epsilon;
		decayingLR = _decayingLR;
		
		start = new State(0,0);
		currState = new State(0,0);
		
		currValues = new ValueFunction(myMaze.width, myMaze.height);
		policy = new int[myMaze.width][myMaze.height];
		qsa = new double[myMaze.width][myMaze.height][Action.numActions];
		initialize();
		
		evaluatedVals = new ValueFunction(myMaze.width, myMaze.height);
		optVals = new ValueFunction(myMaze.width, myMaze.height);
	}
	
	public void initialize()
	{
	    learningRate = maxLearningRate;
	    currState.copy(start);
	    numEpisodes = 0;
		//Initialise the qsa array with random numbers
	    currValues.initialize();
		Random rand = new Random();
		for (int i=0;i<qsa.length;i++)
			for(int j=0;j<qsa[i].length;j++)	
				for(int k=0;k<qsa[i][j].length;k++)
					qsa[i][j][k] = 0;
			

		//Initialise policy for all states as -1
		for(int i=0;i<policy.length;i++)
			for (int j=0;j<policy[i].length;j++)
				policy[i][j] = -1;
	}
	
	public void setProperty (int name, String value)
	{
	    if(name==Properties.PJOG) {
	        pjog = Double.parseDouble(value);
	    }
	    else if (name==Properties.Epsilon){
	        epsilon = Double.parseDouble(value);
	    }
	    else if (name==Properties.LearningRate){
	        maxLearningRate = Double.parseDouble(value);
	    }
	    else if (name==Properties.DecayingLR){
	        decayingLR = (new Boolean(value)).booleanValue();
	    }
	}
	
	public boolean step()
	{
		double transitionCost;
		int currAction;
		State nextState;
		
		if(reachedGoal(currState)) {
			currState.copy(start);
			numEpisodes++;
			
			if(decayingLR)
			    learningRate = (1000.0*maxLearningRate)/(1000.0+numEpisodes);
			else
			    learningRate = maxLearningRate;
			
			if(0==numEpisodes%1000)
			System.out.println(numEpisodes+","+learningRate);
			
			return true;
		}
		
		
		
		//Select action using epsilon greedy exploration policy
		currAction = chooseAction(currState, Math.random());
		double currStateQ = qsa[currState.x][currState.y][currAction];

		//Perform choosen action based on pjog (noise of environment)
		nextState = Action.performAction(currState, currAction, pjog);
		//Utility.show(" next st="+nextState.x+","+nextState.y);
		
		//If not a valid transition stay in same state and add penalty;
		if(!myMaze.isValidTransition(currState, nextState)) {
			transitionCost = myMaze.getReward(currState, nextState);//add reward or penalty
			receivedPenalty = true;
			nextState.copy(currState);
		}
		else { //transition cost = pathCost
			transitionCost = pathCost;
			receivedPenalty = false;
		}
		
		double nextStateQmin = getMinQsa(nextState);
		
		//System.out.println("qsa before=="+qsa[currState.x][currState.y][0]+","+qsa[currState.x][currState.y][1]+","+qsa[currState.x][currState.y][2]+","+qsa[currState.x][currState.y][3]);
		currStateQ = currStateQ*(1-learningRate) + (learningRate*(transitionCost + nextStateQmin));
		
		qsa[currState.x][currState.y][currAction] = currStateQ;
		//System.out.println("qsa after =="+qsa[currState.x][currState.y][0]+","+qsa[currState.x][currState.y][1]+","+qsa[currState.x][currState.y][2]+","+qsa[currState.x][currState.y][3]);			
		
		//policy[currState.x][currState.y] = getBestAction(qsa[currState.x][currState.y]);
		policy[currState.x][currState.y] = getBestAction(qsa[currState.x][currState.y]);
		//System.out.println("policy= "+policy[currState.x][currState.y]);
		currState.copy(nextState);
		
		if(reachedGoal(currState)) {
			//System.out.println("Goal Reached");
			//System.out.println("=============================");
			//Utility.delay(2000);
			//currState.equals(start);
		}
		
		return false;
	}
	
	public void execute(int numIterations)
	{
		currState.copy(start);
		while(!reachedGoal(currState))
		{
			step();
		}		
	}
	
	public ValueFunction getValueFunction()
	{
	    
		for(int i=0;i<myMaze.width;i++)
			for (int j=0;j<myMaze.height;j++)
			    currValues.stateValue[i][j]=getMinQsa(new State(i,j));
		
	    return currValues;
	}
	
	public int[][] getPolicy()
	{
		return policy;
	}
	
	public double[][][] getQsa()
	{
		return qsa;
	}
	
	public State getCurrState()
	{
		return currState;
	}
	
	
	//returns the best action with probability (1-pjog)
	//returns other actions with prob. (1-pjog)/numOfActions
	private int chooseAction(State currState, double randNum)
	{
	    
		int bestAction = getBestAction(qsa[currState.x][currState.y]);
		double d = epsilon/(Action.numActions);
		int choosenAction = bestAction;
		
		for(int i=0;i<Action.numActions;i++) {
			if (randNum < (i+1)*d ) {
				choosenAction=i;
				break;
			}
		}
		Utility.show("BestAction:"+bestAction);
		Utility.show("Rand"+randNum);
		Utility.show("ChoosenAction:"+choosenAction);
		if(choosenAction == bestAction)
		    isBestAct = true;
		else 
		    isBestAct = false;
		
		return choosenAction;
	}
	
	private int getBestAction(double[] actions)
	{
		double min = actions[0];
		int bestAction = 0;
		for (int i=1;i<actions.length;i++)
		{
			if (min>actions[i])
			{
				min = actions[i];
				bestAction = i;
			}
		}
		return bestAction;
	}
	
	private double getMinQsa(State st)
	{
		double min = qsa[st.x][st.y][0];
		int bestAction = 0;
		for (int i=0;i<qsa[st.x][st.y].length;i++)
		{
			if (min>qsa[st.x][st.y][i])
			{
				min = qsa[st.x][st.y][i];
				bestAction = i;
			}
		}
		return min;
	}
	
	private boolean reachedGoal(State s)
	{
		if(myMaze.goals.contains(s))
			return true;
		else
			return false;
	}

	public int[][] getOptPolicy()
	{
		return optPolicy;
	}
	
	public ValueFunction getEvaluatedVals()
	{
		return evaluatedVals;
	}
	
	public ValueFunction getOptVals()
	{
		return optVals;
	}
	private void calcTrueValues()
	{
//		System.out.println("calc'ing opt values");
		ValueIteration valitr = new ValueIteration(myMaze, pjog, 0.01);
		while(!valitr.step());
		optVals = valitr.getValueFunction();
		optPolicy = valitr.getPolicy();
		isOptValCalc = true;
	}
	
	private double computeScore()
	{
		double netScore=0;
		for(int i=0;i<evaluatedVals.stateValue.length;i++)
			for(int j=0;j<evaluatedVals.stateValue[i].length;j++)
			{
				netScore += Math.abs(optVals.stateValue[i][j] - evaluatedVals.stateValue[i][j]);
			}
		return netScore;
	}
	
	public double evalPolicy()
	{
		evaluatedVals.initialize();
    	ValueFunction evalVals = new ValueFunction(myMaze.width, myMaze.height);
    	ValueFunction prevEvalVals = new ValueFunction(myMaze.width, myMaze.height);
    	prevEvalVals.initialize();
    	
    	State currState, desiredNextState;
		double maxDelta = 0, delta = 0, v=0, maxV, minV=10000, prob,safe;
		boolean valueConverged = false;
		int valueIters=0, MAX_VALUE_ALLOWED = 1000;
		
		while (!valueConverged)
		{
			evalVals.initialize();
		    maxDelta = 0;
		    maxV =0;
			for(int i=0; i<myMaze.width; i++) {
				for(int j=0; j<myMaze.height;j++) {
					v = 0;
				    currState = new State(i,j);
				    if(myMaze.goals.contains(currState)) {
				        evalVals.stateValue[i][j] = 0;
				        continue;
				    }
					Vector allNext = new Vector(myMaze.getSuccessors(currState));	//vector of successor states
					if(-1 == policy[i][j])
					{
						evalVals.stateValue[i][j] = 0;
						continue;
					}

					desiredNextState = Action.performAction(currState, policy[i][j]);
					
				    for(int m=0;m<allNext.size();m++)
				    {
			            State s = (State)allNext.get(m);
			            
			            if(!desiredNextState.equals(s))
			                prob = pjog/(Action.numActions-1);
			            else
			                prob = 1-pjog;
			            
			            if(myMaze.isValidTransition(currState,s))
			                safe = prevEvalVals.stateValue[s.x][s.y];
			            else
			                safe = myMaze.getReward(currState,s) + prevEvalVals.stateValue[i][j];
			            
			            v += prob*safe;        
				    }
				    v=v+1;//this 1 is added as pathcost or transition cost
				    evalVals.stateValue[i][j] = v;
				    
				    maxV = (maxV<v) ? v : maxV;
				    
					delta = Math.abs(evalVals.stateValue[i][j] - prevEvalVals.stateValue[i][j]);
					if(maxDelta<delta) 
						maxDelta = delta;
				}
			}
			valueIters++;
			
			if(maxDelta<PRECISION)
				valueConverged = true;
			
			if(maxV>MAX_VALUE_ALLOWED)
			    valueConverged = true;
			
			for(int i=0;i<myMaze.width;i++)
				for(int j=0;j<myMaze.height;j++)
					prevEvalVals.stateValue[i][j] = evalVals.stateValue[i][j]; 
		}
		for(int i=0;i<myMaze.width;i++)
			for(int j=0;j<myMaze.height;j++)
				evaluatedVals.stateValue[i][j] = evalVals.stateValue[i][j];
		return computeScore();
	}
	
}
