package com.proj4;

import rl.*;
import shared.ThresholdTrainer;

import java.util.*;

/**
 * code
 */

// pun not intended
public class MazeRunnerOld {
    private List<String> mazes = Arrays.asList("small");

    public void run() {
         mazes.stream()
                 .map(Config::mapPath)
                 .map(this::loadMap)
                 .forEach(this::mazeExperiment);

    }

    public MazeMarkovDecisionProcess loadMap(String filename) {
        try {
            return MazeMarkovDecisionProcess.load(filename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public void mazeExperiment(MazeMarkovDecisionProcess mdp) {
        MazeMarkovDecisionProcessVisualization mazeVis = new MazeMarkovDecisionProcessVisualization(mdp);

        valueIterationExperiment(mdp, mazeVis);
        policyIterationExperiment(mdp, mazeVis);
    }

    private void valueIterationExperiment(MazeMarkovDecisionProcess mdp, MazeMarkovDecisionProcessVisualization mazeVis) {
        ValueIteration vi = new ValueIteration(.95, mdp);
        ThresholdTrainer tt = new ThresholdTrainer(vi);

        long startTime = System.currentTimeMillis();
        tt.train();
        Policy p = vi.getPolicy();
        long finishTime = System.currentTimeMillis();

        System.out.println("Value iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");

        System.out.println(mazeVis.toString(p));
    }

    private void policyIterationExperiment(MazeMarkovDecisionProcess mdp, MazeMarkovDecisionProcessVisualization mazeVis) {
        PolicyIteration pi = new PolicyIteration(.95, mdp);
        ThresholdTrainer tt = new ThresholdTrainer(pi);

        long startTime = System.currentTimeMillis();
        tt.train();
        Policy p = pi.getPolicy();
        long finishTime = System.currentTimeMillis();

        System.out.println("Policy iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");

        System.out.println(mazeVis.toString(p));
    }
}
