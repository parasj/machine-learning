package com.ABIGAIL;

import java.util.Arrays;

import com.jainlabs.OptimizationResultTuple;
import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class CountOnesTest {
    public static OptimizationResultTuple run(int N) {


        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new CountOnesEvaluationFunction();
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new UniformCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);


        double st = System.nanoTime();

        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200);
        fit.train();
        double rhcr = ef.value(rhc.getOptimal());

        double rhct = System.nanoTime() - st;


        st = System.nanoTime();

        SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
        fit = new FixedIterationTrainer(sa, 200);
        fit.train();
        double sar = ef.value(sa.getOptimal());

        double sat = System.nanoTime() - st;



        st = System.nanoTime();

        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(20, 20, 0, gap);
        fit = new FixedIterationTrainer(ga, 300);
        fit.train();
        double gar = ef.value(ga.getOptimal());

        double gatime = System.nanoTime() - st;



        st = System.nanoTime();

        MIMIC mimic = new MIMIC(50, 10, pop);
        fit = new FixedIterationTrainer(mimic, 100);
        fit.train();
        double mimicr = ef.value(mimic.getOptimal());

        double mimictime = System.nanoTime() - st;

        return new OptimizationResultTuple(rhcr, sar, gar, mimicr, rhct, sat, gatime, mimictime);
    }
}