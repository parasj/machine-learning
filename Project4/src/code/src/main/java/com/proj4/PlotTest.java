package com.proj4;


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
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.auxiliary.common.ConstantStateGenerator;
import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.common.GoalBasedRF;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * code
 */
public class PlotTest {
    private final SinglePFTF tf;
    private final GoalBasedRF rf;
    private final ConstantStateGenerator sg;
    private final SimpleHashableStateFactory hashingFactory;
    private final Domain domain;
    private final State s;

    public PlotTest(int x, int y, double p) {
        GridWorldDomain gw = new GridWorldDomain(11,11); //11x11 grid world
        gw.setMapToFourRooms(); //four rooms layout
        gw.setProbSucceedTransitionDynamics(0.8); //stochastic transitions with 0.8 success rate
        domain = gw.generateDomain();

        //setup initial state
        s = GridWorldDomain.getOneAgentOneLocationState(domain);
        GridWorldDomain.setAgent(s, 0, 0);
        GridWorldDomain.setLocation(s, 0, 10, 10);

        //ends when the agent reaches a location
        tf = new SinglePFTF(domain.
                getPropFunction(GridWorldDomain.PFATLOCATION));

        //reward function definition
        rf = new GoalBasedRF(new TFGoalCondition(tf), 5., -0.1);

        //initial state generator
        sg = new ConstantStateGenerator(s);
        hashingFactory = new SimpleHashableStateFactory(); // set up the state hashing system for looking up states

    }

    public void runValueIteration(boolean b){
        Planner planner = new ValueIteration(domain, rf, tf, 0.99, hashingFactory, 0.001, 10000);
        Policy p = planner.planFromState(s);
        p.evaluateBehavior(s, rf, tf).writeToFile(Config.outPath("gridworld", "vi"));

        if (b) simpleValueFunctionVis((ValueFunction) planner, p);
    }


    public void runPolicyIteration(boolean b){
        Planner planner = new PolicyIteration(domain, rf, tf, 0.99, hashingFactory, 0.001, 10000, 1000);
        Policy p = planner.planFromState(s);
        p.evaluateBehavior(s, rf, tf).writeToFile(Config.outPath("gridworld", "pi"));

        if (b) simpleValueFunctionVis((ValueFunction) planner, p);
    }

    public void runQLearning() {
        LearningAgentFactory qLearningFactory = new QLearningAgentFactory(domain, hashingFactory);

        SimulatedEnvironment env = new SimulatedEnvironment(domain, rf, tf, sg);
        final LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 10, 100, qLearningFactory);

        exp.setUpPlottingConfiguration(500, 250, 2, 1000, TrialMode.MOSTRECENTANDAVERAGE,
                PerformanceMetric.CUMULATIVESTEPSPEREPISODE,
                PerformanceMetric.AVERAGEEPISODEREWARD);

        exp.startExperiment();
    }

    public void runQLearning(int iters){
        SimulatedEnvironment env = new SimulatedEnvironment(domain, rf, tf, sg);
        LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0., 1.);

        try {
            PrintWriter writer = new PrintWriter(Config.outPath("gridworld", "qlearn.tsv"), "UTF-8");

            for(int i = 0; i < iters; i++){
                EpisodeAnalysis ea = agent.runLearningEpisode(env);

                ea.writeToFile(Config.outPath("gridworld/qlearn", "ql_" + i));
                writer.println(i + "\t" + ea.maxTimeStep());

                env.resetEnvironment();
            }

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p){
        List<State> allStates = StateReachability.getReachableStates(s, (SADomain) domain, hashingFactory);
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
                allStates, valueFunction, p);
        gui.initGUI();
    }

}
