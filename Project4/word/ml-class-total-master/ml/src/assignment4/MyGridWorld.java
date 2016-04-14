package assignment4;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.environment.Environment;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.visualizer.Visualizer;

public class MyGridWorld {
	
	
	GridWorldDomain gwdg;
	Domain domain;
	RewardFunction rf;
	TerminalFunction tf;
	StateConditionTest goalCondition;
	State initialState;
	HashableStateFactory hashingFactory;
	Environment env;
	
	public MyGridWorld(){
		
		//create the domain
		gwdg = new GridWorldDomain(11000, 11000);
		gwdg.setMapToFourRooms();
		domain = gwdg.generateDomain();

		//define the task
		rf = new UniformCostRF();
		tf = new SinglePFTF(domain.getPropFunction(GridWorldDomain.PFATLOCATION));
		goalCondition = new TFGoalCondition(tf);

		//set up the initial state of the task
//		initialState = GridWorldDomain.getOneAgentNLocationState(domain, 1);
		initialState = GridWorldDomain.getOneAgentOneLocationState(domain);
		GridWorldDomain.setAgent(initialState, 0, 0);
		GridWorldDomain.setLocation(initialState, 0, 10000, 10000);

		//set up the state hashing system for tabular algorithms
		hashingFactory = new SimpleHashableStateFactory();

		//set up the environment for learning algorithms
		env = new SimulatedEnvironment(domain, rf, tf, initialState);

		
			
	}
	
	public void visualize(String outputPath){
		Visualizer v = GridWorldVisualizer.getVisualizer(gwdg.getMap());
		new EpisodeSequenceVisualizer(v, domain, outputPath);
	}
	
	public void valueIterationExample(String outputPath){
		
		Planner planner = new ValueIteration(domain, rf, tf, 0.99, hashingFactory, 0.001, 10000);
		Policy p = planner.planFromState(initialState);
		p.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "vi");
		
	}

	
	public void QLearningExample(String outputPath){
		
		LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0., 1.);

		//run learning for 50 episodes
		for(int i = 0; i < 50; i++){
			EpisodeAnalysis ea = agent.runLearningEpisode(env);

			ea.writeToFile(outputPath + "ql_" + i);
			System.out.println(i + ": " + ea.maxTimeStep());

			//reset environment for next learning episode
			env.resetEnvironment();
		}
		
	}	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MyGridWorld example = new MyGridWorld();
		
		String outputPath = "output/"; //directory to record results
		
		//we will call planning and learning algorithms here
		
		example.valueIterationExample(outputPath);
		//example.QLearningExample(outputPath);
		
		//run the visualizer
		//example.visualize(outputPath);

	}

}
