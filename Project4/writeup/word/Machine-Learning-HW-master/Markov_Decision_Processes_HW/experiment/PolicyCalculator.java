package experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import rl.MazeMarkovDecisionProcess;
import rl.Policy;
import rl.PolicyLearner;
import shared.Trainer;

public class PolicyCalculator {
	
	private final Function<MazeMarkovDecisionProcess, ? extends PolicyLearner> learnerFactory;
	private final Function<PolicyLearner, ? extends Trainer> trainerFactory;
	
	private List<Long> trainingTimes = new ArrayList<>();

	public PolicyCalculator(Function<MazeMarkovDecisionProcess, ? extends PolicyLearner> learnerFactory,
			Function<PolicyLearner, ? extends Trainer> trainerFactory) {
		this.learnerFactory = learnerFactory;
		this.trainerFactory = trainerFactory;
	}

	public Policy calcPolicy(MazeMarkovDecisionProcess mdp) {
		PolicyLearner iter = learnerFactory.apply(mdp);
		Trainer trainer = trainerFactory.apply(iter);

		long start = System.currentTimeMillis();
		trainer.train();
		long finish = System.currentTimeMillis();
		trainingTimes.add(finish - start);

		Policy policy = iter.getPolicy();
		
		/*
		MazeMarkovDecisionProcessVisualization visualizer = new MazeMarkovDecisionProcessVisualization(mdp);
		System.out.println(visualizer.toString(policy));
		*/

		return policy;
	}
	
	public String getTrainingTimesString() {
		StringJoiner joiner = new StringJoiner(",");
		for (Long time : trainingTimes) {
			joiner.add("" + time);
		}
		
		return joiner.toString();
	}
}
