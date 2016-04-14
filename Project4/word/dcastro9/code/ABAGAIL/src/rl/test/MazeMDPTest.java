package rl.test;

import rl.DecayingEpsilonGreedyStrategy;
import rl.EpsilonGreedyStrategy;
import rl.GreedyStrategy;
import rl.MazeCreator;
import rl.MazeMarkovDecisionProcess;
import rl.MazeMarkovDecisionProcessVisualization;
import rl.Policy;
import rl.PolicyIteration;
import rl.QLambda;
import rl.SarsaLambda;
import rl.ValueIteration;
import shared.FixedIterationTrainer;
import shared.ThresholdTrainer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Runs a maze test for a maze I created.
 * @author guillory, dcastro9
 * @version 1.1
 */
public class MazeMDPTest {
    public static void main(String[] args) throws Exception {
        runMazeMDP();
    }

    /**
     * Runs Value Iteration, Policy Iteration, and Q-Learning on our Maze.
     */
    public static void runMazeMDP() {
        // 14 in height, 18 in length
        char[][] mazeDesign =   {{'#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#'},
                                 {'#',' ',' ',' ',' ','#',' ',' ',' ',' ',' ',' ',' ','#',' ',' ',' ','#'},
                                 {'#',' ','#','#',' ',' ','#','#',' ',' ','#','#',' ',' ','#','#',' ','#'},
                                 {'#',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','#'},
                                 {'#',' ',' ','#','#','#',' ','#','#',' ',' ','#',' ','#',' ','#',' ','#'},
                                 {'#',' ',' ',' ',' ',' ',' ',' ','#',' ',' ',' ',' ',' ',' ',' ',' ','#'},
                                 {'#',' ','#','#',' ',' ','#','#',' ',' ','#','#',' ','#','#','#',' ','#'},
                                 {'#',' ',' ',' ',' ',' ',' ',' ',' ','#',' ',' ',' ',' ',' ',' ',' ','#'},
                                 {'#',' ',' ','#',' ',' ',' ','#','#',' ',' ','#',' ',' ',' ','#',' ','#'},
                                 {'#',' ',' ',' ',' ',' ',' ','#',' ',' ',' ',' ','#',' ',' ',' ',' ','#'},
                                 {'#',' ','#','#',' ',' ','#','#',' ','#','#','#',' ',' ','#','#',' ','#'},
                                 {'#',' ',' ','#',' ',' ',' ',' ',' ',' ',' ',' ',' ','#',' ',' ',' ','#'},
                                 {'#',' ',' ','#',' ',' ',' ','#',' ',' ',' ','#',' ',' ',' ','#',' ','#'},
                                 {'#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#'}};
        MazeMarkovDecisionProcess maze = new MazeMarkovDecisionProcess(mazeDesign, 16, 12, 1, 1, 0.1);
        System.out.println(maze);
        
        ValueIteration vi = new ValueIteration(.95, maze);
        ThresholdTrainer tt = new ThresholdTrainer(vi);
        long startTime = System.currentTimeMillis();
        tt.train();
        Policy p = vi.getPolicy();
        long finishTime = System.currentTimeMillis();
        System.out.println("Value iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        MazeMarkovDecisionProcessVisualization mazeVis =
            new MazeMarkovDecisionProcessVisualization(maze);
        System.out.println(mazeVis.toString(p));
        BufferedImage image = new BufferedImage(mazeVis.getWidth(), mazeVis.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        mazeVis.drawMaze(graphics2D);
        try {
            ImageIO.write(image,"jpeg", new File("out.jpeg"));
        }
        catch (IOException e) {
            // failed to save file.
        }

        PolicyIteration pi = new PolicyIteration(.95, maze);
        tt = new ThresholdTrainer(pi);
        startTime = System.currentTimeMillis();
        tt.train();
        p = pi.getPolicy();
        finishTime = System.currentTimeMillis();
        System.out.println("Policy iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        System.out.println(mazeVis.toString(p));

        //testQLearningIterations(maze, 5000, 100000, 5000);
        int iterations = 50000;
        QLambda ql = new QLambda(.5, .95, .2, 1, new EpsilonGreedyStrategy(.3), maze);
        FixedIterationTrainer fit = new FixedIterationTrainer(ql, iterations);
        startTime = System.currentTimeMillis();
        fit.train();
        p = ql.getPolicy();
        finishTime = System.currentTimeMillis();
        System.out.println("Q lambda learned : " + p);
        System.out.println("in " + iterations + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        System.out.println("Acquiring " + ql.getTotalReward() + " reward");
        System.out.println(mazeVis.toString(p));
    }

    public static void testQLearningIterations(MazeMarkovDecisionProcess maze, int initial, int maximum, int interval) {
        for (int iterations = initial; iterations < maximum; iterations += interval) {
            QLambda ql1 = new QLambda(.5, .95, .2, 1, new EpsilonGreedyStrategy(.3), maze);
            FixedIterationTrainer fit = new FixedIterationTrainer(ql1, iterations);
            long startTime = System.currentTimeMillis();
            fit.train();
            Policy p1 = ql1.getPolicy();
            long finishTime = System.currentTimeMillis();
            System.out.println("Q lambda learned : " + p1);
            System.out.println("in " + iterations + " iterations");
            System.out.println("and " + (finishTime - startTime) + " ms");
            System.out.println("Acquiring " + ql1.getTotalReward() + " reward");
        }
    }
}
