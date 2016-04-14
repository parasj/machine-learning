package com.proj4;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.auxiliary.StateGenerator;
import burlap.oomdp.auxiliary.common.ConstantStateGenerator;
import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.GoalBasedRF;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;

/**
 * code
 */
public class GridWorldRunner {
    private final Domain domain;
    private final State s;
    private final TerminalFunction tf;
    private final RewardFunction rf;
    private final StateGenerator sg;
    private final HashableStateFactory hashingFactory;
    private final SimulatedEnvironment env;

    public GridWorldRunner(int x, int y, double p) {
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
        env = new SimulatedEnvironment(domain, rf, tf, s);
    }


}
