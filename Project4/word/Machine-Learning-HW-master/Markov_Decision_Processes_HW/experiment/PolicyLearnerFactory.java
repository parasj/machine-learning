package experiment;

import java.util.function.Function;

import rl.MazeMarkovDecisionProcess;
import rl.PolicyLearner;

public class PolicyLearnerFactory implements Function<MazeMarkovDecisionProcess, PolicyLearner> {

	private final Function<MazeMarkovDecisionProcess, PolicyLearner> factory;
	public final String learnerName;

	public PolicyLearnerFactory(Function<MazeMarkovDecisionProcess, PolicyLearner> factory, String learnerName) {
		this.factory = factory;
		this.learnerName = learnerName;
	}
	
	@Override
	public PolicyLearner apply(MazeMarkovDecisionProcess mdp) {
		return factory.apply(mdp);
	}
}
