package assignment4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.FullActionModel;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.common.GoalBasedRF;
import burlap.oomdp.singleagent.common.SimpleAction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.singleagent.explorer.VisualExplorer;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.visualizer.ObjectPainter;
import burlap.oomdp.visualizer.StateRenderLayer;
import burlap.oomdp.visualizer.StaticPainter;
import burlap.oomdp.visualizer.Visualizer;

public class ExampleGridWorld implements DomainGenerator {

	public static final String ATTX = "x";
	public static final String ATTY = "y";

	public static final String CLASSAGENT = "agent";
	public static final String CLASSLOCATION = "location";

	public static final String ACTIONNORTH = "north";
	public static final String ACTIONSOUTH = "south";
	public static final String ACTIONEAST = "east";
	public static final String ACTIONWEST = "west";

	public static final String PFAT = "at";
	
	public static double finalReward = 0;
	
	public static double finalVITime;
	
	public static double finalPITime;
	
	public static double finalSarsaTime;
	

	// ordered so first dimension is x
	protected int[][] map = new int[][] 
			{ { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
		      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
			  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
			  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
			  { 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1 },
			  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, 
			  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
			  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
			  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
			  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, };
			  
			  
			  
			  
			
//		{ { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
//	      { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1 , 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0 },
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
//	      { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1 , 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0 },
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
//	      { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1 , 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0 },
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
//	      { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1 , 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//		  { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
//		  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0 },
//		  { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 , 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0}, };
			
			

	@Override
	public Domain generateDomain() {

		SADomain domain = new SADomain();

		Attribute xatt = new Attribute(domain, ATTX, Attribute.AttributeType.INT);
		xatt.setLims(0, 10);

		Attribute yatt = new Attribute(domain, ATTY, Attribute.AttributeType.INT);
		yatt.setLims(0, 10);

		ObjectClass agentClass = new ObjectClass(domain, CLASSAGENT);
		agentClass.addAttribute(xatt);
		agentClass.addAttribute(yatt);

		ObjectClass locationClass = new ObjectClass(domain, CLASSLOCATION);
		locationClass.addAttribute(xatt);
		locationClass.addAttribute(yatt);

		new Movement(ACTIONNORTH, domain, 0);
		new Movement(ACTIONSOUTH, domain, 1);
		new Movement(ACTIONEAST, domain, 2);
		new Movement(ACTIONWEST, domain, 3);

		new AtLocation(domain);

		return domain;
	}

	public static State getExampleState(Domain domain) {
		State s = new MutableState();
		ObjectInstance agent = new MutableObjectInstance(domain.getObjectClass(CLASSAGENT), "agent0");
		agent.setValue(ATTX, 0);
		agent.setValue(ATTY, 0);

		ObjectInstance location = new MutableObjectInstance(domain.getObjectClass(CLASSLOCATION), "location0");
//		location.setValue(ATTX, 87);
//		location.setValue(ATTY, 21);
		location.setValue(ATTX, 10);
		location.setValue(ATTY, 10);

		s.addObject(agent);
		s.addObject(location);

		return s;
	}

	public StateRenderLayer getStateRenderLayer() {
		StateRenderLayer rl = new StateRenderLayer();
		rl.addStaticPainter(new WallPainter());
		rl.addObjectClassPainter(CLASSLOCATION, new LocationPainter());
		rl.addObjectClassPainter(CLASSAGENT, new AgentPainter());

		return rl;
	}

	public Visualizer getVisualizer() {
		return new Visualizer(this.getStateRenderLayer());
	}

	protected class Movement extends SimpleAction implements FullActionModel {

		// 0: north; 1: south; 2:east; 3: west
		protected double[] directionProbs = new double[4];

		public Movement(String actionName, Domain domain, int direction) {
			super(actionName, domain);
			for (int i = 0; i < 4; i++) {
				if (i == direction) {
					directionProbs[i] = 0.8;
				} else {
					directionProbs[i] = 0.2 / 3.;
				}
			}
		}

		@Override
		protected State performActionHelper(State s, GroundedAction groundedAction) {
			// get agent and current position
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSAGENT);
			int curX = agent.getIntValForAttribute(ATTX);
			int curY = agent.getIntValForAttribute(ATTY);

			// sample directon with random roll
			double r = Math.random();
			double sumProb = 0.;
			int dir = 0;
			for (int i = 0; i < this.directionProbs.length; i++) {
				sumProb += this.directionProbs[i];
				if (r < sumProb) {
					dir = i;
					break; // found direction
				}
			}

			// get resulting position
			int[] newPos = this.moveResult(curX, curY, dir);

			// set the new position
			agent.setValue(ATTX, newPos[0]);
			agent.setValue(ATTY, newPos[1]);

			// return the state we just modified
			return s;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, GroundedAction groundedAction) {
			// get agent and current position
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSAGENT);
			int curX = agent.getIntValForAttribute(ATTX);
			int curY = agent.getIntValForAttribute(ATTY);

			List<TransitionProbability> tps = new ArrayList<TransitionProbability>(4);
			TransitionProbability noChangeTransition = null;
			for (int i = 0; i < this.directionProbs.length; i++) {
				int[] newPos = this.moveResult(curX, curY, i);
				if (newPos[0] != curX || newPos[1] != curY) {
					// new possible outcome
					State ns = s.copy();
					ObjectInstance nagent = ns.getFirstObjectOfClass(CLASSAGENT);
					nagent.setValue(ATTX, newPos[0]);
					nagent.setValue(ATTY, newPos[1]);

					// create transition probability object and add to our list
					// of outcomes
					tps.add(new TransitionProbability(ns, this.directionProbs[i]));
				} else {
					// this direction didn't lead anywhere new
					// if there are existing possible directions
					// that wouldn't lead anywhere, aggregate with them
					if (noChangeTransition != null) {
						noChangeTransition.p += this.directionProbs[i];
					} else {
						// otherwise create this new state and transition
						noChangeTransition = new TransitionProbability(s.copy(), this.directionProbs[i]);
						tps.add(noChangeTransition);
					}
				}
			}

			return tps;
		}

		protected int[] moveResult(int curX, int curY, int direction) {

			// first get change in x and y from direction using 0: north; 1:
			// south; 2:east; 3: west
			int xdelta = 0;
			int ydelta = 0;
			if (direction == 0) {
				ydelta = 1;
			} else if (direction == 1) {
				ydelta = -1;
			} else if (direction == 2) {
				xdelta = 1;
			} else {
				xdelta = -1;
			}

			int nx = curX + xdelta;
			int ny = curY + ydelta;

			int width = ExampleGridWorld.this.map.length;
			int height = ExampleGridWorld.this.map[0].length;

			// make sure new position is valid (not a wall or off bounds)
			if (nx < 0 || nx >= width || ny < 0 || ny >= height || ExampleGridWorld.this.map[nx][ny] == 1) {
				nx = curX;
				ny = curY;
			}

			return new int[] { nx, ny };

		}

	}

	protected class AtLocation extends PropositionalFunction {

		public AtLocation(Domain domain) {
			super(PFAT, domain, new String[] { CLASSAGENT, CLASSLOCATION });
		}

		@Override
		public boolean isTrue(State s, String... params) {
			ObjectInstance agent = s.getObject(params[0]);
			ObjectInstance location = s.getObject(params[1]);

			int ax = agent.getIntValForAttribute(ATTX);
			int ay = agent.getIntValForAttribute(ATTY);

			int lx = location.getIntValForAttribute(ATTX);
			int ly = location.getIntValForAttribute(ATTY);

			return ax == lx && ay == ly;
		}

	}

	public class WallPainter implements StaticPainter {

		@Override
		public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {

			// walls will be filled in black
			g2.setColor(Color.BLACK);

			// set up floats for the width and height of our domain
			float fWidth = ExampleGridWorld.this.map.length;
			float fHeight = ExampleGridWorld.this.map[0].length;

			// determine the width of a single cell
			// on our canvas such that the whole map can be painted
			float width = cWidth / fWidth;
			float height = cHeight / fHeight;

			// pass through each cell of our map and if it's a wall, paint a
			// black rectangle on our
			// cavas of dimension widthxheight
			for (int i = 0; i < ExampleGridWorld.this.map.length; i++) {
				for (int j = 0; j < ExampleGridWorld.this.map[0].length; j++) {

					// is there a wall here?
					if (ExampleGridWorld.this.map[i][j] == 1) {

						// left coordinate of cell on our canvas
						float rx = i * width;

						// top coordinate of cell on our canvas
						// coordinate system adjustment because the java canvas
						// origin is in the top left instead of the bottom right
						float ry = cHeight - height - j * height;

						// paint the rectangle
						g2.fill(new Rectangle2D.Float(rx, ry, width, height));

					}

				}
			}

		}

	}

	public class AgentPainter implements ObjectPainter {

		@Override
		public void paintObject(Graphics2D g2, State s, ObjectInstance ob, float cWidth, float cHeight) {

			// agent will be filled in gray
			g2.setColor(Color.GRAY);

			// set up floats for the width and height of our domain
			float fWidth = ExampleGridWorld.this.map.length;
			float fHeight = ExampleGridWorld.this.map[0].length;

			// determine the width of a single cell on our canvas
			// such that the whole map can be painted
			float width = cWidth / fWidth;
			float height = cHeight / fHeight;

			int ax = ob.getIntValForAttribute(ATTX);
			int ay = ob.getIntValForAttribute(ATTY);

			// left coordinate of cell on our canvas
			float rx = ax * width;

			// top coordinate of cell on our canvas
			// coordinate system adjustment because the java canvas
			// origin is in the top left instead of the bottom right
			float ry = cHeight - height - ay * height;

			// paint the rectangle
			g2.fill(new Ellipse2D.Float(rx, ry, width, height));

		}

	}

	public class LocationPainter implements ObjectPainter {

		@Override
		public void paintObject(Graphics2D g2, State s, ObjectInstance ob, float cWidth, float cHeight) {

			// agent will be filled in blue
			g2.setColor(Color.BLUE);

			// set up floats for the width and height of our domain
			float fWidth = ExampleGridWorld.this.map.length;
			float fHeight = ExampleGridWorld.this.map[0].length;

			// determine the width of a single cell on our canvas
			// such that the whole map can be painted
			float width = cWidth / fWidth;
			float height = cHeight / fHeight;

			int ax = ob.getIntValForAttribute(ATTX);
			int ay = ob.getIntValForAttribute(ATTY);

			// left coordinate of cell on our canvas
			float rx = ax * width;

			// top coordinate of cell on our canvas
			// coordinate system adjustment because the java canvas
			// origin is in the top left instead of the bottom right
			float ry = cHeight - height - ay * height;

			// paint the rectangle
			g2.fill(new Rectangle2D.Float(rx, ry, width, height));

		}

	}

	public static class ExampleRF implements RewardFunction {

		int goalX;
		int goalY;
		

		public ExampleRF(int goalX, int goalY) {
			this.goalX = goalX;
			this.goalY = goalY;
			
		}

		@Override
		public double reward(State s, GroundedAction a, State sprime) {

			// get location of agent in next state
			ObjectInstance agent = sprime.getFirstObjectOfClass(CLASSAGENT);
			int ax = agent.getIntValForAttribute(ATTX);
			int ay = agent.getIntValForAttribute(ATTY);

			// are they at goal location?
			if (ax == this.goalX && ay == this.goalY) {
				
//				return 30000.;
				return 100.;
			}
			

			return -1;
		}

	}

	public static class ExampleTF implements TerminalFunction {

		int goalX;
		int goalY;

		public ExampleTF(int goalX, int goalY) {
			this.goalX = goalX;
			this.goalY = goalY;
		}

		@Override
		public boolean isTerminal(State s) {

			// get location of agent in next state
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSAGENT);
			int ax = agent.getIntValForAttribute(ATTX);
			int ay = agent.getIntValForAttribute(ATTY);

			// are they at goal location?
			if (ax == this.goalX && ay == this.goalY) {
				return true;
			}

			return false;
		}

	}

	public double valueIterationExample(String outputPath, Domain domain, State initialState, RewardFunction rf,
			TerminalFunction tf, HashableStateFactory hf, double gamma, PrintWriter out, PrintWriter outIter,
			PrintWriter rOut) throws Exception {

		double totalR = 0;
		double start = System.currentTimeMillis();
		System.out.println("start: " + start);
		Planner planner = new ValueIteration(domain, rf, tf, gamma, hf, 0.001, 1000);
		Policy p = planner.planFromState(initialState);
		double end = System.currentTimeMillis() - start;
		finalVITime = end + finalVITime;
		System.out.println("end: " + end);
		ValueIteration val = (ValueIteration) planner;
//		p.evaluateBehavior(initialState, rf, tf, totalR).writeToFile(outputPath + "vi");
		p.evaluateBehavior(initialState, rf, tf, totalR);

		// visualize the value function and policy.
		simpleValueFunctionVis((ValueFunction) planner, p, initialState, hf, domain);

//		out.println(gamma + "\t" + end);
		outIter.println(gamma + "\t" + val.returnPasses());
		
		totalR = p.returnTotalReward();
	
		
		return totalR;

	}

	public double policyIterationExample(String outputPath, Domain domain, State initialState, RewardFunction rf,
			TerminalFunction tf, HashableStateFactory hf, double gamma, double maxDelta, int maxEvaluationIterations,
			int maxPolicyIterations, PrintWriter out, PrintWriter outIter, PrintWriter rOut, PrintWriter viOut) throws Exception{
		
		double totalR = 0;
		double start = System.currentTimeMillis();
		System.out.println("start: " + start);
		Planner planner = new PolicyIteration(domain, rf, tf, gamma, hf, maxDelta, maxEvaluationIterations,
				maxPolicyIterations);
		Policy p = planner.planFromState(initialState);
		double end = System.currentTimeMillis() - start;
		finalPITime = end + finalPITime;

		System.out.println("end: " + end);
		PolicyIteration pol = (PolicyIteration)planner;
		
//		p.evaluateBehavior(initialState, rf, tf, totalR).writeToFile(outputPath + "pi");
		p.evaluateBehavior(initialState, rf, tf, totalR);
		simpleValueFunctionVis((ValueFunction)planner, p, initialState, hf, domain);
		
		totalR = p.returnTotalReward();
		
//		out.println(gamma + "\t" + end);
		outIter.println(gamma + "\t" + pol.returnIterations());
		viOut.println(gamma + "\t" + pol.getTotalValueIterations());
		
		return totalR;
		
	}
	
	public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p, State initialState, HashableStateFactory hf, Domain domain){

		List<State> allStates = StateReachability.getReachableStates(initialState, 
							(SADomain)domain, hf);
//		ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
//						allStates, valueFunction, p);
		ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
				allStates, valueFunction, p);
		gui.initGUI();

	}
	
	public void sarsaLearningExample(String outputPath, Domain domain, HashableStateFactory hf, double gamma,
			double learningRate, SimulatedEnvironment env, PrintWriter out, PrintWriter outIter) {
		
		double start = System.currentTimeMillis();
		LearningAgent agent = new SarsaLam(domain, gamma, hf, 0., learningRate, 10000, 0.3);

		// run learning for 50 episodes
		for (int i = 0; i < 100; i++) {
			EpisodeAnalysis ea = agent.runLearningEpisode(env, 10000);

			ea.writeToFile(outputPath + "sarsa_" + i);
			System.out.println(i + ": " + ea.maxTimeStep());

			// reset environment for next learning episode
			env.resetEnvironment();
			
		}
		
		double end = System.currentTimeMillis() - start;
//		out.println(gamma + "\t" + end);
		finalSarsaTime = end + finalSarsaTime;
	}
	
	public void experimenterAndPlotter(SimulatedEnvironment env, StateConditionTest goalCondition, RewardFunction rf){
		
//		//different reward function for more interesting results
//		((SimulatedEnvironment)env).setRf(new GoalBasedRF(goalCondition, 5.0, -0.1));
		((SimulatedEnvironment)env).setRf(rf);


	}
	
	public void learnPlots(SimulatedEnvironment env, LearningAgentFactory sarsaLearningFactory,
			LearningAgentFactory sarsaLearningFactory2) {
		LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 1, 100, sarsaLearningFactory,
				sarsaLearningFactory2);
		exp.setUpPlottingConfiguration(500, 250, 2, 1000, TrialMode.MOSTRECENTANDAVERAGE,
				PerformanceMetric.CUMULATIVESTEPSPEREPISODE, PerformanceMetric.AVERAGEEPISODEREWARD);

		exp.startExperiment();
		exp.writeStepAndEpisodeDataToCSV("expData");
	}
	
	
	

	public static void main(String[] args) throws Exception{

		ExampleGridWorld gen = new ExampleGridWorld();
		final Domain domain = gen.generateDomain();
		String outputPath = "output/"; // directory to record results
		double gamma = 0.5;
		double maxDelta = 0.01;
		int maxEvaluationIterations = 1000;
		int maxPolicyIterations = 100;
		double learningRate = 0.1;
		double totalVIReward = 0;
		double totalPIReward = 0;
		
		State initialState = ExampleGridWorld.getExampleState(domain);
//		RewardFunction rf = new ExampleRF(87, 21);
//		TerminalFunction tf = new ExampleTF(87, 21);
		RewardFunction rf = new ExampleRF(10, 10);
		TerminalFunction tf = new ExampleTF(10, 10);
		StateConditionTest goalCondition = new TFGoalCondition(tf);

		SimulatedEnvironment env = new SimulatedEnvironment(domain, rf, tf, initialState);

		final HashableStateFactory hf = new SimpleHashableStateFactory();

		Visualizer v = gen.getVisualizer();
		
		VisualExplorer exp = new VisualExplorer(domain, env, v);

		LearningAgentFactory qLearningFactory = new LearningAgentFactory() {
			@Override
			public String getAgentName() {
				return "Q-Learning";
			}

			@Override
			public LearningAgent generateAgent() {
				return new QLearning(domain, 0.99, hf, 0.3, 0.99);
			}
		};

		LearningAgentFactory sarsaLearningFactory = new LearningAgentFactory() {
			@Override
			public String getAgentName() {
				return "Q = 0";
			}

			@Override
			public LearningAgent generateAgent() {
				return new SarsaLam(domain, 0.99, hf, 0.0, 0.9, 1000,  1);
//				return new SarsaLam(domain, 0.99, hf, 0.0, 0.1, 1000, 1);
			}
		};
		
		LearningAgentFactory sarsaLearningFactory2 = new LearningAgentFactory() {
			@Override
			public String getAgentName() {
				return "Q = 100";
			}

			@Override
			public LearningAgent generateAgent() {
				return new SarsaLam(domain, 0.99, hf, 100.0, 0.9, 1000, 1);
//				return new SarsaLam(domain, 0.99, hf, 30000.0, 0.9, 1000, 1);
			}
		};
		
		
		double start = System.currentTimeMillis();
		
		
		/*
		 * generated the plots for SARSA and Q-learning
		 */
//		gen.learnPlots(env, sarsaLearningFactory, sarsaLearningFactory2);
		
		gen.learnPlots(env, qLearningFactory, qLearningFactory);
		
		double end = System.currentTimeMillis() - start;
		System.out.println(end);
		
		PrintWriter out1 = new PrintWriter("VITime.dat");
		PrintWriter out2 = new PrintWriter("PITime.dat");
		PrintWriter out3 = new PrintWriter("VIIter.dat");
		PrintWriter out4 = new PrintWriter("PIIter.dat");
		PrintWriter out5 = new PrintWriter("SarsaTime.dat");
		PrintWriter out6 = new PrintWriter("SarsaIter.dat");
		PrintWriter out7 = new PrintWriter("VIReward.dat");
		PrintWriter out8 = new PrintWriter("PIReward.dat");
		PrintWriter out9 = new PrintWriter("PIInnerIter.dat");
		
		
		/*
		 * This generated the data files for PI, VI, and SARSA.  It also generates the policy maps and grid world visual
		 */
		for (int i = 0; i < 6; i++) {

//			for (int m = 0; m < 10; m++) {
//				totalVIReward = gen.valueIterationExample(outputPath, domain, initialState, rf, tf, hf, gamma, out1,
//						out3, out7) + totalVIReward;
//				
//
//				totalPIReward = gen.policyIterationExample(outputPath, domain, initialState, rf, tf, hf, gamma,
//						maxDelta, maxEvaluationIterations, maxPolicyIterations, out2, out4, out8, out9) + totalPIReward;
				
//				 gen.sarsaLearningExample(outputPath, domain, hf, gamma,
//				 learningRate, env, out5, out6);

//			}

			
//			out7.println(gen.finalVITime / 10 + "\t" + totalVIReward / 10);
//			out8.println(gen.finalPITime / 10 + "\t" + totalPIReward / 10);
			out7.println(gamma + "\t" + totalVIReward / 10);
			out8.println(gamma  + "\t" + totalPIReward / 10);
			out1.println(gamma + "\t" + finalVITime/10);
			out2.println(gamma + "\t" + finalPITime/10);
			out5.println(gamma + "\t" + finalSarsaTime/10);
			//change this to .001 if you are trying to generate data files for the large grid world
			gamma = gamma + 0.1;
			totalVIReward = 0;
			totalVIReward = 0;
			totalPIReward = 0;
			gen.finalVITime = 0;
			gen.finalPITime = 0;
			gen.finalSarsaTime = 0;
			

		}
		
		out1.close();
		out2.close();
		out3.close();
		out4.close();
		out5.close();
		out6.close();
		out7.close();
		out8.close();
		out9.close();

		

//		 exp.addKeyAction("w", ACTIONNORTH);
//		 exp.addKeyAction("s", ACTIONSOUTH);
//		 exp.addKeyAction("d", ACTIONEAST);
//		 exp.addKeyAction("a", ACTIONWEST);

		exp.initGUI();

	}

}
