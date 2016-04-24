package exploration;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import maze.Motion;
import maze.NegativeMazeMDP;
import rl.Policy;
import rl.PolicyLearner;
import shared.Trainer;

public class GoalPolicyTrainer implements Trainer {
	private static final long serialVersionUID = 1L;

	private final PolicyLearner thingToTrain;
	private final int maxIterations;
	private final int batchSize;
	private final NegativeMazeMDP mdp;
	private final int numSolidifierIterations;
	
	private int numIterations = 0;


	public GoalPolicyTrainer(PolicyLearner thingToTrain, NegativeMazeMDP mdp,
			int maxIterations, int batchSize, int numSolidifierIterations) {
		this.thingToTrain = thingToTrain;
		this.mdp = mdp;
		this.maxIterations = maxIterations;
		this.batchSize = batchSize;
		this.numSolidifierIterations = numSolidifierIterations;
	}

	@Override
	public double train() {
		double lastError = 0;
		while (numIterations < maxIterations && !policyFindsGoal()) {
			for (int i = 0; i < batchSize; i++) {
				lastError = thingToTrain.train();
			}
		
			numIterations += batchSize;
		}
		
		policyFindsGoal();
		
		if (numIterations >= maxIterations) {
			System.out.println("Max iterations hit");
		}
		
//		for (int i = 0; i < numSolidifierIterations; i++) {
//			lastError = thingToTrain.train();
//		}
		
		return lastError;
	}

	private boolean policyFindsGoal() {
		Stack<Integer> desiredPath = new Stack<>();
		Set<Integer> visited = new HashSet<>();
		Policy policy = thingToTrain.getPolicy();
		desiredPath.add(mdp.sampleInitialState());

		while (!desiredPath.isEmpty() && !mdp.isTerminalState(desiredPath.peek())) {
			int currentState = desiredPath.pop();
			if (!visited.contains(currentState)) {
				int desiredAction = policy.getAction(currentState);
				
				if (desiredAction == -1) {
					return false;
				}
				
				Motion desiredMotion = Motion.create(mdp, desiredAction);

				// assumes that reverse is not possible
				//desiredPath.add(desiredMotion.getReverseState(currentState));
				desiredPath.add(desiredMotion.getLeftState(currentState));
				desiredPath.add(desiredMotion.getRightState(currentState));
				desiredPath.add(desiredMotion.getForwardState(currentState));
				
				visited.add(currentState);
			}
		}
		
		if (desiredPath.isEmpty()) {
			return false;
		} else {
			int currentState = desiredPath.peek();
			return mdp.reward(currentState, policy.getAction(currentState)) >= 0;
		}
	}
}
