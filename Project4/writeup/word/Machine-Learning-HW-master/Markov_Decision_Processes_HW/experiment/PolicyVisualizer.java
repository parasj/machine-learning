package experiment;

import java.util.HashMap;
import java.util.Map;

import maze.NegativeMazeMDP;
import rl.MazeMarkovDecisionProcess;
import rl.Policy;

public class PolicyVisualizer {
	
	private final NegativeMazeMDP mmdp;
	private Map<Integer, Character> actionRepresentations;

	public PolicyVisualizer(NegativeMazeMDP mmdp, Map<Integer, Character> actionRepresentations) {
		this.mmdp = mmdp;
		this.actionRepresentations = actionRepresentations;
	}
	
	public PolicyVisualizer(NegativeMazeMDP mmdp) {
		this(mmdp, getDefaultRepresentations());
	}
	
	public static Map<Integer, Character> getDefaultRepresentations() {
		Map<Integer, Character> reps = new HashMap<>();
		reps.put(MazeMarkovDecisionProcess.MOVE_DOWN, 'V');
		reps.put(MazeMarkovDecisionProcess.MOVE_LEFT, '<');
		reps.put(MazeMarkovDecisionProcess.MOVE_RIGHT, '>');
		reps.put(MazeMarkovDecisionProcess.MOVE_UP, '^');
		
		return reps;
	}

	/**
     * Get a string visualization of the maze with a policy
     * @param p the policy
     * @return the string
     */
    public String toString(Policy p) {
        String ret = "";
        for (int y = 0; y < mmdp.getHeight(); y++) {
            for (int x = 0; x < mmdp.getWidth(); x++) {
            	int currentState = mmdp.stateFor(x,y);
                if (mmdp.isObstacle(x, y)) {
                    ret += MazeMarkovDecisionProcess.OBSTACLE;
                } else if (mmdp.isGoal(currentState)) {
                	ret += "x";
                } else if (mmdp.isTrap(currentState)) {
                	ret += '!';
                } else if (mmdp.sampleInitialState() == currentState) {
                	ret += 'o';
                } else {
                    int a = p.getAction(currentState);
                    if (actionRepresentations.containsKey(a)) {
                    	ret += actionRepresentations.get(a);
                    } else {
                    	ret += "?";
                    }
                }
            }
            ret += "\n";
        }
        return ret;
    }
}
