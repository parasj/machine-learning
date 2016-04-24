package experiment;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import maze.Motion;
import maze.NegativeMazeMDP;
import rl.Policy;

public class OptimalPolicyDistinguisher implements Distinguisher<Policy> {
	
	private final NegativeMazeMDP mdp;

	public OptimalPolicyDistinguisher(NegativeMazeMDP mdp) {
		this.mdp = mdp;
	}

	@Override
	public boolean areEqual(Policy p1, Policy p2) {
		Stack<Integer> desiredPath = new Stack<>();
		Set<Integer> visited = new HashSet<>();
		int currentState = mdp.sampleInitialState();

		while (!mdp.isTerminalState(currentState)) {
			if (!visited.contains(currentState)) {
				int desiredAction1 = p1.getAction(currentState);
				int desiredAction2 = p2.getAction(currentState);
				
				if (desiredAction1 != desiredAction2) {
					return false;
				}
				
				if (mdp.isTerminalState(currentState)) {
					return true;
				}
				
				Motion desiredMotion = Motion.create(mdp, desiredAction1);
				desiredPath.add(desiredMotion.getReverseState(currentState));
				desiredPath.add(desiredMotion.getLeftState(currentState));
				desiredPath.add(desiredMotion.getRightState(currentState));
				desiredPath.add(desiredMotion.getForwardState(currentState));
				
				visited.add(currentState);
			}

			currentState = desiredPath.pop();
		}

		return true;
	}

}
