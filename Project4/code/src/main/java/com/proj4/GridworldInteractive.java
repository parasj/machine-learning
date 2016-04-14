package com.proj4;

import burlap.domain.singleagent.gridworld.*;
import burlap.oomdp.core.*;
import burlap.oomdp.core.states.*;
import burlap.oomdp.singleagent.explorer.*;
import burlap.oomdp.visualizer.*;

/**
 * code
 */
public class GridworldInteractive {
    private final Domain domain;
    private final VisualExplorer exp;

    public GridworldInteractive(int x, int y, double success) {
        GridWorldDomain gw = new GridWorldDomain(x, y); //11x11 grid world
        gw.setMapToFourRooms(); //four rooms layout
        gw.setProbSucceedTransitionDynamics(success); //stochastic transitions with 0.8 success rate
        domain = gw.generateDomain(); //generate the grid world domain

        //setup initial state
        State s = GridWorldDomain.getOneAgentOneLocationState(domain);
        GridWorldDomain.setAgent(s, 0, 0);
        GridWorldDomain.setLocation(s, 0, 10, 10);

        //create visualizer and explorer
        Visualizer v = GridWorldVisualizer.getVisualizer(gw.getMap());
        exp = new VisualExplorer(domain, v, s);

        bindKeys();
    }

    private void bindKeys() {
        //set control keys to use w-s-a-d
        exp.addKeyAction("w", GridWorldDomain.ACTIONNORTH);
        exp.addKeyAction("s", GridWorldDomain.ACTIONSOUTH);
        exp.addKeyAction("a", GridWorldDomain.ACTIONWEST);
        exp.addKeyAction("d", GridWorldDomain.ACTIONEAST);

        exp.initGUI();
    }

    public GridworldInteractive() {
        this(11, 11, 0.8);
    }
}
