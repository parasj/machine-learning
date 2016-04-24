import java.util.Vector;

public class StateInfo {
	private Vector[] preds = new Vector[Action.numActions];

	StateActionInfo[] actionSuccs = new StateActionInfo[Action.numActions];
	
	public StateInfo()
	{
		for(int i=0;i<Action.numActions;i++)
		{
			actionSuccs[i] = new StateActionInfo();
			preds[i] = new Vector();
		}
	}
	
	//while adding successor for a state check whether the state already exists 
	public void addSuccs(int action, State st, double reward)
	{
		actionSuccs[action].updateData(st, reward);
	}

	public double getMinQsa()
	{
		double min = actionSuccs[0].qsa;
		
		for (int i=1;i<actionSuccs.length;i++)
				min = (actionSuccs[i].qsa<min) ? actionSuccs[i].qsa : min;
		
		return min;
	}
	
	public int getBestAction()
	{
		int bestAction = 0;	// 0 means first action
		double min = actionSuccs[0].qsa;
		for (int i=1;i<actionSuccs.length;i++) {
			if (actionSuccs[i].qsa<min)
			{
				min = actionSuccs[i].qsa;
				bestAction = i;
			}
		}
		return bestAction;
	}

	//while adding predecessor for a state check whether the state already exists 
	public void addPreds(State st, int action)
	{
		StateAndVisits snv = new StateAndVisits(st);
		int position = preds[action].indexOf(snv);
		if(-1 == position)
			preds[action].add(snv);
		else
		{
			snv = (StateAndVisits)preds[action].get(position);
			snv.incrementVisits();
		}
	}
	
	public Vector getSuccs(int action)
	{
		return actionSuccs[action].succs;
	}
	
	public Vector[] getPreds()
	{
		return preds;
	}
}
