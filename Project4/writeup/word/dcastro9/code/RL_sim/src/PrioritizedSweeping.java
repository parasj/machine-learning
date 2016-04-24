import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class PrioritizedSweeping //implements Algorithms
{
	private double epsilon, discountFactor, pjog;
	private final int PATH_COST= 1;
	private int BACKUPS_ALLOWED;
	private Maze myMaze;

	private StateInfo[][] model;
	private int[][] policy;
	private int[][] selectedAction;
	private double[][][] qsa;
	private ValueFunction currValues;
	public boolean isBestAct = true;
	public boolean receivedPenalty = false;
	State start, curr, next;;
//	double currStateJ, nextStateJ ;		//estimated "reward to go" for the current state
	double priority;
	
	PSSimulator ui;
	DecimalFormat df = new DecimalFormat("0.0");
	Vector backedUpStates;
	

	PriorityQueue pQueue;	//priority queue
	double tinyThreshold;
	final int NUMBER_OF_BACKUPS = 10;
	
	//for analysis purposes
	ValueFunction optVals;
	boolean isOptValCalc;
	double PRECISION=0.01;
	double policyScore;//score of the policy that u get after each episode
	int numEpisodes;
	long startTime, stopTime;
	Random rand = new Random();
	
	public int totalBackups = 0;
	public int numSteps = 0;
	
	public int maxCounter;
    public int maxCounterLast=0;
    public int averageBackups;
    
    //Constant for time-worn exploration
    private static final int Min_Transition_Required = 3;
	
	static class Properties {
	    public static int PJOG=1;
	    public static int MaxBackups=2;
	    public static int Epsilon=3;
	    public static int TinyThreshold=4;
	}
	
	public PrioritizedSweeping(Maze _maze, double _pjog, double _epsilon, int MaxBackUps, double _tinyThreshold)
	{
		myMaze = _maze;
		start = new State(0,0);
		curr = new State(0,0);
		
		pjog = _pjog;
		epsilon = _epsilon;
		BACKUPS_ALLOWED=MaxBackUps;

		model = new StateInfo[myMaze.width][myMaze.height];
		policy = new int[myMaze.width][myMaze.height];
		selectedAction = new int[myMaze.width][myMaze.height];
		
		tinyThreshold = _tinyThreshold;
		
		qsa = new double[myMaze.width][myMaze.height][Action.numActions];
		
		//for analysis purposes
		optVals = new ValueFunction(myMaze.width, myMaze.height);
		isOptValCalc = false;
		
		calcTrueValues();
		initialize();
	}
	
	public void setSeed(long seed)
	{
	    rand.setSeed(seed);
	}
	
	
	public void initialize()
	{
		//initialize pqueue
		pQueue = new PriorityQueue();
		
		//initialize all estimated "value to go" for each state to zero
		for (int i=0;i<model.length;i++)
			for(int j=0;j<model[i].length;j++)
			{
//				System.out.println("i,j "+i+j);
				model[i][j] = new StateInfo();
			}
		
		//initialise policy for all states as -1
		for(int i=0;i<policy.length;i++)
			for (int j=0;j<policy[i].length;j++)
			{
				policy[i][j] = -1;
				selectedAction[i][j] = -1;
			}
		
		//initialize all currValues.stateValues as 0
		currValues = new ValueFunction(myMaze.width, myMaze.height);
		
		//initialize qsa
		for(int i=0;i<qsa.length;i++)
			for (int j=0;j<qsa[i].length;j++)
				for (int k=0;k<qsa[i][j].length;k++)
					qsa[i][j][k] = 0;
		
		
		backedUpStates = new Vector();
		
		numEpisodes = 0;
		policyScore = 0;
		//calcTrueValues();
		maxCounter =0;
	}
	
	public void setProperty (int name, String value)
	{
	    if(name==Properties.PJOG) {
	        pjog = Double.parseDouble(value);
	    }
	    else if (name==Properties.Epsilon){
	        epsilon = Double.parseDouble(value);
	    }
	    else if (name==Properties.MaxBackups){
	        BACKUPS_ALLOWED = Integer.parseInt(value);
	    }
	    else if (name==Properties.TinyThreshold){
	        tinyThreshold = Double.parseDouble(value);
	    }
	}
	
	public ValueFunction getValueFunction()
	{
		for(int i=0;i<currValues.stateValue.length;i++)
			for(int j=0;j<currValues.stateValue[i].length;j++)
			{
				currValues.stateValue[i][j] = model[i][j].getMinQsa();
			}
	    return currValues;
	}
	
	public int[][] getPolicy()
	{
		for(int i=0;i<policy.length;i++)
			for(int j=0;j<policy[i].length;j++)
				policy[i][j] = model[i][j].getBestAction();
		return policy;
	}
	
	public double[][][] getQsa()
	{
		return qsa;
	}
	
	public State getCurrState()
	{
		return curr;
	}
	
	private boolean reachedGoal(State s)
	{
		if(myMaze.goals.contains(s))
			return true;
		else
			return false;
	}
	
	public void execute(int numIterations)
	{
		curr.copy(start);
		while(!reachedGoal(curr))
		{
			step();
		}		
	}
	
	
	private State getStart()
	{
	    int x = rand.nextInt(myMaze.width);
	    int y = rand.nextInt(myMaze.height);
	    //System.out.print("("+x+","+y+")");
	    State start = new State(x,y);
	    return start;
	}
	public boolean step()
	{
		double reward;
		int currAction;
		
		if(reachedGoal(curr)) {
			//recordStopTime();
		    maxCounterLast =maxCounter;
		    maxCounter=0;
			numEpisodes += 1;
			//curr=copy(start);
			curr = getStart();
			if(numSteps!=0)
			    averageBackups = totalBackups/numSteps;
			else 
			    averageBackups = 0;
			totalBackups = 0;
			numSteps =0;
			/*policyScore = evalPolicy();
			System.out.print(numEpisodes+"\t"+policyScore+"\n");
			*/
			return true;
		}
		
		//Choose action using epsilon greedy exploration policy
		currAction = chooseAction(curr, Math.random());

		//Perform the choosen action. Noise in environment is represented using PJOG
		next = Action.performAction(curr, currAction, pjog); 
		
		if(!myMaze.isValidTransition(curr, next)) {
			//stay in same state and add penalty 
			reward = myMaze.getReward(curr, next);
			receivedPenalty = true;
			next.copy(curr);
		}
		else {
			//else transition cost = pathCost
			reward = PATH_COST;			
			receivedPenalty = false;
		}
		
		model[curr.x][curr.y].addSuccs(currAction, next, reward);
		model[next.x][next.y].addPreds(curr, currAction);

		pQueue.placeOnTop(curr, priority);
//		if((deltaQ/qBefore) > 0.4)//change is greater than 40% of original value
		
		processQueue();
		
		curr.copy(next);
		numSteps++;
		return false;
	}

	//Chooses the best action according to epsilon greedy exploration policy
	private int chooseAction(State curr, double randNum)
	{    
		int bestAction = model[curr.x][curr.y].getBestAction();
		selectedAction[curr.x][curr.y] = bestAction;	//updates only for display purposes
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

	
	//this contains the actual prioritized sweeping algorithm
	private void processQueue()
	{
		//Utility.show("===========>Processing Queue<================");
		int counter = 0;
		while(!pQueue.isEmpty() && counter<=BACKUPS_ALLOWED)
		{
			//Utility.show("Counter: "+counter);
			//pQueue.printQueue();
			//step 2.1
			State top = pQueue.pop();

			double jOldVal = model[top.x][top.y].getMinQsa();
			double roMin = Double.MAX_VALUE;
			int minAction = 0;
			
			//performing step 2.2
			for(int i=0;i<Action.numActions;i++)
			{
				model[top.x][top.y].actionSuccs[i].qsa = calcExpectedVal(top,i);
				if(model[top.x][top.y].actionSuccs[i].qsa<roMin)
				{
					roMin  = model[top.x][top.y].actionSuccs[i].qsa;
					minAction = i;
				}
			}
			double deltaMax = Math.abs(roMin - jOldVal);	//step 2.3
			
			//step 2.4 : no computation required under current implementation as
			// we use getMinQsa for Ji-hat value.

			//step 2.5
			Vector[] preds = model[top.x][top.y].getPreds();
			for(int i=0;i<Action.numActions;i++)
			{
				Vector predStates = preds[i];
				for(int j=0;j<predStates.size();j++)
				{
					StateAndVisits snv = (StateAndVisits)predStates.get(j);
					int num  = snv.getVisits();
					
					StateActionInfo temp = model[snv.state.x][snv.state.y].actionSuccs[i];
					int den = temp.actionCounter;
					
					double prob = (double)num/den;
					double priority = prob*deltaMax;

					if(priority>tinyThreshold)
						pQueue.insert(snv.state, priority);
				}
			}
			//filling qsa array for visualization purposes
			for(int i=0;i<Action.numActions;i++)
				qsa[top.x][top.y][i] = model[top.x][top.y].actionSuccs[i].qsa;

			counter+=1;
//			currValues.stateValue[top.x][top.y] = model[top.x][top.y].getMinQsa();
		}
		totalBackups += (counter-1); 
		if (counter-1>maxCounter)
		    maxCounter=counter-1;
		//Utility.show("===========>Backupstate over<================");
		//Utility.show("");
	}
	
	private double calcExpectedVal(State s, int a)
	{
		double val;
		StateInfo currStInfo= model[s.x][s.y];
		
		//number of times action a was executed till now
		int actionCtr = currStInfo.actionSuccs[a].actionCounter;
		if (0==actionCtr)
			return 0;
		//Added by vivek recently to do what andrew suggested
		//if (actionCtr < Min_Transition_Required)
		//    return 0;
		
		//estimated reward associated with state-action pair s-a
		double estReward = currStInfo.actionSuccs[a].totalReward/actionCtr;
		
		//all successors for action a 
		Vector succs = currStInfo.getSuccs(a);
		
		double sum = 0;
		StateAndVisits temp;
		for(int i=0;i<succs.size();i++)
		{
			temp = (StateAndVisits)succs.get(i);

			int num = temp.getVisits();	//num of times transition from temp to currSt was done
			int den = actionCtr;		//num of times action a was executed
			double prob = (double)num/den;
			
			double jVal = model[temp.state.x][temp.state.y].getMinQsa();
			sum += prob*jVal;
		}
		
		val = estReward + sum;
		return val;
	}
	
	void printPredecessors(Vector preds)
	{
//		StateActionInfo sap;
//		for(int i=0;i<preds.size();i++)
//		{
//			sap = (StateActionInfo)preds.get(i);
//			sap.printState();
//		}
	}
	
	void printBackedUpStates()
	{
		System.out.println("printing backedup states");
		State st;
		for(int i=0;i<backedUpStates.size();i++)
		{
			st = (State)backedUpStates.get(i);
			st.printState();
		}
		System.out.println();
	}
	
	//for visualization purposes
	public Vector getBackedUpStates()
	{
		return backedUpStates;
	}
	
	public double evalPolicy()
	{
		double score=0;
		int[][] currPolicy;
		currPolicy = getPolicy();
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
		score = computeScore(evalVals);
		return score;
	}
    	
	private double computeScore(ValueFunction evalVals)
	{
		double netScore=0;
		for(int i=0;i<evalVals.stateValue.length;i++)
			for(int j=0;j<evalVals.stateValue[i].length;j++)
			{
				netScore += Math.abs(optVals.stateValue[i][j] - evalVals.stateValue[i][j]);
			}
		
		netScore = netScore/(myMaze.height*myMaze.width);
		return netScore;
	}
	
	public double getScore()
	{
		return policyScore;
	}
	
	public double getTime()
	{
//		System.out.println("time= "+(stopTime - startTime));
		return (stopTime - startTime);
	}
	
	private void calcTrueValues()
	{
//		System.out.println("calc'ing opt values");
		ValueIteration valitr = new ValueIteration(myMaze, pjog, PRECISION);
		while(!valitr.step());
		optVals = valitr.getValueFunction();
		isOptValCalc = true;
	}
	
	public void recordStartTime()
	{
	    startTime = new Date().getTime();
//	    System.out.println("start time= "+startTime);
	}
	
	public void recordStopTime()
	{
	    stopTime = new Date().getTime();
//	    System.out.println("stop time= "+stopTime);
	}
	
	public void setEpsilon(double _eps)
	{
		epsilon = _eps;
	}
	
	
}

