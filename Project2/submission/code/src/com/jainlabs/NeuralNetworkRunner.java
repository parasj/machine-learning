package com.jainlabs;

import com.sun.javaws.exceptions.InvalidArgumentException;
import dist.*;
import func.nn.feedfwd.*;
import opt.*;
import opt.prob.MIMIC;
import opt.test.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;
import shared.tester.*;
import shared.tester.Comparison;
import util.linalg.*;


import java.util.*;
import java.io.*;
import java.text.*;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Project2
 */
public class NeuralNetworkRunner {
    private final String dataFileName;
    private final UseOptAlg optAlgo;
    private Data data;
    private DataSet train, test;
    private final static int IN = 16, OUT = 1;
    private int iterations;
    private double cooling;
    private static FeedForwardNeuralNetworkFactory nnFact = new FeedForwardNeuralNetworkFactory();
    private int initialPopulation;

    public NeuralNetworkRunner(String dataFileName, UseOptAlg optAlgo, int iterations, int initialPopulation) {
        this(dataFileName, optAlgo, iterations, 0.999, initialPopulation);
    }

    public NeuralNetworkRunner(String dataFileName, UseOptAlg optAlgo, int iterations, double cooling) {
        this(dataFileName, optAlgo, iterations, cooling, 40);
    }

    public NeuralNetworkRunner(String dataFileName, UseOptAlg optAlgo, int iterations) {
        this(dataFileName, optAlgo, iterations, 0.999, 40);
    }

    public NeuralNetworkRunner(String dataFileName, UseOptAlg alg, int iterations, double cooling, int initialPopulation) {
        this.dataFileName = dataFileName;
        this.optAlgo = alg;
        this.iterations = iterations;
        this.cooling = cooling;
        this.initialPopulation = initialPopulation;
        loadData(dataFileName);
    }

    public NeuralNetworkResult run() {
        long st, et;
        NeuralNetworkResult result = new NeuralNetworkResult();
        result.iterations = iterations;

        FeedForwardNetwork nn = nnFact.createClassificationNetwork(makeConfig());
        ErrorMeasure losslayer = new SumOfSquaresError();
        NeuralNetworkOptimizationProblem nnproblem = new NeuralNetworkOptimizationProblem(train, nn, losslayer);
        OptimizationAlgorithm optproblem = makeOptProblem(nnproblem);
        FixedIterationTrainer fit = new FixedIterationTrainer(optproblem, iterations);

            st = System.nanoTime();
        fit.train();
            et = System.nanoTime();
            result.trainTimeNano = et - st;

        nn.setWeights(optproblem.getOptimal().getData());
        // Tester t = new NeuralNetworkTester(nn, result.rawOutput, result.confusionTestMatrix); // , result.accuracyTestMetric


        testNN(nn, result);
        return result;
    }

    private NeuralNetworkResult testNN(FeedForwardNetwork nn, NeuralNetworkResult result) {
        long st, et;

        st = System.nanoTime();

        Instance[] testInstances = test.getInstances();

        List<Boolean> labels = Arrays.stream(testInstances)
                .map(i -> i.getLabel().getBoolean())
                .collect(Collectors.toList());

        List<Boolean> results = Arrays.stream(testInstances)
                .map(Instance::getData)
                .map(i -> runNet(nn, i))
                .map(r -> r > 0.5)
                .collect(Collectors.toList());

        for (int i = 0; i < results.size(); i ++) {
            result.updateResult(labels.get(i), results.get(i));
        }

        et = System.nanoTime();
        result.testTimeNano = et - st;

        return result;
    }

    private Double runNet(FeedForwardNetwork nn, util.linalg.Vector i) {
        nn.setInputValues(i);
        nn.run();
        return nn.getOutputValues().get(0);
    }

    private OptimizationAlgorithm makeOptProblem(NeuralNetworkOptimizationProblem nnproblem) {
        switch(optAlgo) {
            case RandomizedHillClimbing:
                return new RandomizedHillClimbing(nnproblem);
            case SimulatedAnnealing:
                return new SimulatedAnnealing(10, cooling, nnproblem);
            case GeneticAlgorithms:
                return new StandardGeneticAlgorithm(initialPopulation, 2, 2, nnproblem);
        }

        throw new UnsupportedOperationException();
    }

    private int[] makeConfig() {
        int[] config = new int[3];

        config[0] = IN;
        config[1] = (2 * (IN + OUT)) / 3;
        config[2] = OUT;

        return config;
    }

    private void loadData(String dataFileName) {
        DataLoader dataLoader = new DataLoader();

        try {
            data = dataLoader.loadARFF(dataFileName);
            train = data.getTrain();
            test = data.getTest();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
