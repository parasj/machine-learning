package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import experiments.Permutator;
import exploration.GoalPolicyTrainer;
import maze.Motion;
import maze.NegativeMazeMDP;
import rl.DecayingEpsilonGreedyStrategy;
import rl.EpsilonGreedyStrategy;
import rl.ExplorationStrategy;
import rl.MarkovDecisionProcess;
import rl.MazeMarkovDecisionProcess;
import rl.MazeMarkovDecisionProcessVisualization;
import rl.Policy;
import rl.PolicyIteration;
import rl.PolicyLearner;
import rl.SarsaLambda;
import shared.FixedIterationTrainer;
import shared.ThresholdTrainer;
import shared.Trainer;

public class Program {

	public static void main(String[] args) throws FileNotFoundException {
		double motionFailureProbability = .1;
		double trapCost = 300;
		double defaultTimePenalty = .1;
		double gamma = 0.99;
		double defaultReward = trapCost;
		
		Function<PolicyLearner, Trainer> trainerFactory = (learner) -> new ThresholdTrainer(learner, 1E-2, 10000);
		//Distinguisher<Policy> distinguisher = new OptimalPolicyDistinguisher(sampleMdp);
		
		Map<Character, Integer> reps = invertMap(PolicyVisualizer.getDefaultRepresentations());
		PolicyReader reader = new PolicyReader(reps);
		NegativeMazeMDP sampleMdp = NegativeMazeMDP.createMaze(new File("mdp/smallbridge.txt"),
				0, trapCost, 1, defaultTimePenalty);
		Policy policy = reader.readPolicy("output/mdp/convert.txt", sampleMdp);
		PolicyVisualizer visualizer = new PolicyVisualizer(sampleMdp);
		System.out.println(visualizer.toString(policy));
		
//		System.out.println("Running iterations");
//		runIterations("smallbridge", motionFailureProbability, trapCost,
//				defaultTimePenalty, gamma, defaultReward, trainerFactory);
//		
//		int numIterations = 100000000;
////		runSarsa("cliffs", motionFailureProbability, trapCost,
////				defaultTimePenalty, gamma, defaultReward, numIterations);
////		runSarsa("bridge", motionFailureProbability, trapCost,
////				defaultTimePenalty, gamma, defaultReward, numIterations);
//		System.out.println("Running SARSA");
//		runSarsa("smallbridge", motionFailureProbability, trapCost,
//				defaultTimePenalty, gamma, defaultReward, numIterations);
//
//		System.out.println("Running ranges experiment");
//		runPolicyRangeExperiment(motionFailureProbability, trapCost,
//				defaultTimePenalty, gamma, defaultReward, trainerFactory);
		
//		PolicyLearnerFactory learnerFactory = new PolicyLearnerFactory(
//				(maze) -> new ValueIterationMod(gamma, maze), "ValueIteration");
//		PolicyRangeFinder policyFinder = new PolicyRangeFinder()
//		Experiment experiment = new Experiment(trainerFactory);
//		experiment.run(learnerFactory, , "smallbridge", "output/mdp");
				
//		PolicyCalculator policyCalculator = new PolicyCalculator(mdpCreator, learnerFactory, trainerFactory, maze);
//		List<PolicyRange> policies = policyRangeFinder.findPolicyRanges(policyCalculator);
//		for (PolicyRange policy : policies) {
//			System.out.println(policy.toString(visualizer));
//			System.out.println();
//			System.out.println("--------------------------");
//			System.out.println();
//		}
	}
	
	private static <K, V> Map<V, K> invertMap(Map<K, V> map) {
		Map<V, K> ret = new HashMap<>();
		for (Entry<K, V> entry : map.entrySet()) {
			ret.put(entry.getValue(), entry.getKey());
		}
		
		return ret;
	}

	private static void runSarsa(String mazeName,
			double motionFailureProbability, double trapCost,
			double defaultTimePenalty, double gamma, double defaultReward,
			int numIterations) throws FileNotFoundException {
		File mazeFile = new File("mdp/" + mazeName + ".txt");
		char[][] maze = NegativeMazeMDP.loadMaze(mazeFile);
		NegativeMazeMDP sampleMdp = NegativeMazeMDP.createMaze(maze, 0, trapCost, 1, defaultTimePenalty);
		PolicyVisualizer visualizer = new PolicyVisualizer(sampleMdp);
		Function<PolicyLearner, Trainer> trainerFactory = 
				(learner) -> new GoalPolicyTrainer(learner, sampleMdp, numIterations, 100, 1000);

		List<Double> exploreChances = new ArrayList<>();
		for (double exploreChance = 0.15; exploreChance < 0.8; exploreChance += 0.1) {
			exploreChances.add(exploreChance);
		}

		List<Double> learningRates = new ArrayList<>();
		for (double learningRate = 0.1; learningRate <= 0.8; learningRate += 0.2) {
			learningRates.add(learningRate);
		}
		
		Permutator perm = new Permutator(new int[] { exploreChances.size(), learningRates.size() });
		perm.stream().parallel().forEach((config) -> {
			double exploreChance = exploreChances.get(config[0]);
			double learningRate = learningRates.get(config[1]);
			ExplorationStrategy exploreStrat = new EpsilonGreedyExplorationMod(exploreChance);
			Function<MazeMarkovDecisionProcess, PolicyLearner> sarsaFactory = (mdp) -> new SarsaLearnerMod(
					.5, gamma, learningRate, 1, exploreStrat, mdp);		
			String learnerName = String.format("SARSA_%.2frate_%.2fexplore",
					learningRate, exploreChance);

			System.out.println("Starting " + learnerName);

			PolicyCalculator policyCalculator = new PolicyCalculator(
					sarsaFactory, trainerFactory);
			NegativeMazeMDP mazeMdp = NegativeMazeMDP.createMaze(maze, motionFailureProbability,
					trapCost, defaultReward, defaultTimePenalty);

			try {
				runLearner(mazeName, learnerName, visualizer, policyCalculator, mazeMdp);
			} catch (FileNotFoundException fnfe) {
				System.out.println("Could not write file for " + learnerName);
				fnfe.printStackTrace();
			}
		});
	}

	private static void runIterations(String mazeName,
			double motionFailureProbability, double trapCost,
			double defaultTimePenalty, double gamma, double defaultReward,
			Function<PolicyLearner, Trainer> trainerFactory)
			throws FileNotFoundException {
		Function<MazeMarkovDecisionProcess, PolicyLearner> valueFactoryFn =
				(mdp) -> new ValueIterationMod(gamma, mdp);
		Function<MazeMarkovDecisionProcess, PolicyLearner> policyFactoryFn =
				(mdp) -> new PolicyIteration(gamma, mdp);

		PolicyCalculator valuePolicyCalculator = new PolicyCalculator(valueFactoryFn, trainerFactory);
		PolicyCalculator policyPolicyCalculator = new PolicyCalculator(policyFactoryFn, trainerFactory);
		
		File mazeFile = new File("mdp/" + mazeName + ".txt");
		NegativeMazeMDP mazeMdp = NegativeMazeMDP.createMaze(mazeFile, motionFailureProbability,
				trapCost, defaultReward, defaultTimePenalty);
		PolicyVisualizer visualizer = new PolicyVisualizer(mazeMdp);

		runLearner(mazeName, "ValueItration", visualizer, valuePolicyCalculator, mazeMdp);
		runLearner(mazeName, "PolicyItration", visualizer, policyPolicyCalculator, mazeMdp);
	}

	private static void runLearner(String mazeName, String learnerName,
			PolicyVisualizer visualizer,
			PolicyCalculator valuePolicyCalculator, NegativeMazeMDP mazeMdp)
			throws FileNotFoundException {
		long start = System.currentTimeMillis();
		Policy policy = valuePolicyCalculator.calcPolicy(mazeMdp);
		long end = System.currentTimeMillis();
		long time = end - start;
		savePolicy(visualizer, policy, new File("output/mdp/" + mazeName + "_" + learnerName + ".txt"), time);
		System.out.println("Finished " + mazeName + " " + learnerName);
	}
	
	private static void savePolicy(PolicyVisualizer visualizer, 
			Policy policy, File outputFile, long time) throws FileNotFoundException {
		try (PrintStream printer = new PrintStream(outputFile)) {
			printer.println(visualizer.toString(policy));
			printer.println();
			printer.println(time);
		}
	}

	private static void runPolicyRangeExperiment(
			double motionFailureProbability, double trapCost,
			double defaultTimePenalty, double gamma, double defaultReward,
			Function<PolicyLearner, Trainer> trainerFactory) {
		List<String> mazeNames = new ArrayList<>();
//		mazeNames.add("cliffs");
//		mazeNames.add("bridge");
		String mazeName = "smallbridge";
		mazeNames.add(mazeName);
		
		List<PolicyRangeFinder> rangeFinders = new ArrayList<>();
		MazeFactory rewardMazeFactory = new MazeFactory(
				(m, reward) -> NegativeMazeMDP.createMaze(
						m, motionFailureProbability, trapCost, reward, defaultTimePenalty));
		rangeFinders.add(new PolicyRangeFinder(rewardMazeFactory,
				1, 100000, 32, 1, "Reward"));

		MazeFactory timeMazeFactory = new MazeFactory(
				(m, penalty) -> NegativeMazeMDP.createMaze(m, motionFailureProbability, trapCost,
						defaultReward, penalty));
		rangeFinders.add(new PolicyRangeFinder(timeMazeFactory, 0, 300, 8, .5, "TimePenalty"));

		MazeFactory motionMazeFactory = new MazeFactory(
				(m, probability) -> NegativeMazeMDP.createMaze(m, probability, trapCost,
						defaultReward, defaultTimePenalty));
		rangeFinders.add(new PolicyRangeFinder(motionMazeFactory, 0, .7, .05, 0.001, "MotionFailure"));
		
		

		//PolicyLearner iter = new PolicyIteration(gamma, mdp);
		List<PolicyLearnerFactory> learnerFactories = new ArrayList<>();
		Function<MazeMarkovDecisionProcess, PolicyLearner> valueFactoryFn = (mdp) -> new ValueIterationMod(gamma, mdp);
		learnerFactories.add(new PolicyLearnerFactory(valueFactoryFn, "ValueIteration"));
//		Function<MazeMarkovDecisionProcess, PolicyLearner> policyFactoryFn = (mdp) -> new PolicyIteration(gamma, mdp);
//		learnerFactories.add(new PolicyLearnerFactory(policyFactoryFn, "PolicyIteration"));
		
		Experiment experiment = new Experiment(trainerFactory);
		experiment.run(learnerFactories, rangeFinders, mazeNames, "output/mdp");
	}
	
	
	private static boolean policiesAreEqual(Policy p1, Policy p2) {
		int[] p1Actions = p1.getActions();
		int[] p2Actions = p2.getActions();
		return Arrays.equals(p1Actions, p2Actions);
	}
}
