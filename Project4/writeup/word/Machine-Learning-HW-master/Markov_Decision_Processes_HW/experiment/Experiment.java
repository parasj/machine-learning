package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Function;

import maze.NegativeMazeMDP;
import rl.Policy;
import rl.PolicyLearner;
import shared.Trainer;
import experiments.Permutator;

public class Experiment {
	
	private final Function<PolicyLearner, Trainer> trainerFactory;

	public Experiment(Function<PolicyLearner, Trainer> trainerFactory) {
		this.trainerFactory = trainerFactory;
	}

	public void run(PolicyLearnerFactory learnerFactory, PolicyRangeFinder rangeFinder,
			String mazeName, String outputFolder) throws FileNotFoundException {
		char[][] maze = NegativeMazeMDP.loadMaze(new File("mdp/" + mazeName + ".txt"));
		NegativeMazeMDP sampleMaze = NegativeMazeMDP.createMaze(maze, 0, 0, 0, 0);
		PolicyCalculator policyCalculator = new PolicyCalculator(learnerFactory, trainerFactory);
		Distinguisher<Policy> distinguisher = new OptimalPolicyDistinguisher(sampleMaze);
		List<PolicyRange> policies = rangeFinder.findPolicyRanges(distinguisher, maze, policyCalculator);
		
		String outputFileName = outputFolder + "/" + mazeName + "_" + learnerFactory.learnerName +
				"_" + rangeFinder.settingName + ".txt";
		
		PolicyVisualizer visualizer = new PolicyVisualizer(sampleMaze);
		try(PrintStream printer = new PrintStream(outputFileName)) {
			for (PolicyRange policy : policies) {
				printer.println(policy.toString(visualizer));
				printer.println();
				printer.println("--------------------------");
				printer.println();
			}
			
			printer.println(policyCalculator.getTrainingTimesString());
		}
		
		System.out.println("Finished " + outputFileName);
	}
	
	public void run(List<PolicyLearnerFactory> learnerFactories, List<PolicyRangeFinder> policyFinders,
			List<String> mazeNames, String outputFolder) {
		int[] sizes = {
			learnerFactories.size(),
			policyFinders.size(),
			mazeNames.size()
		};
		Permutator perm = new Permutator(sizes);
		perm.stream().parallel().forEach((config) -> {
			PolicyLearnerFactory learnerFactory = learnerFactories.get(config[0]);
			PolicyRangeFinder policyFinder = policyFinders.get(config[1]);
			String mazeName = mazeNames.get(config[2]);
			try {
				run(learnerFactory, policyFinder, mazeName, outputFolder);
			} catch (Exception e) {
				System.out.println("Exception for " + mazeName + " " + learnerFactory.learnerName +
						" " + policyFinder.settingName);
				e.printStackTrace();
			}
		});
	}
}
