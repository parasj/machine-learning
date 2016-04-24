package opt.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.Distribution;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.SwapNeighbor;
import opt.ga.CrossoverFunction;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.NQueensFitnessFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.SwapMutation;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * @author kmanda1
 * @version 1.0
 */
public class NQueensTest {
    /** The n value */
   // private static final int N = 10;
    /** The t value */
    
    public static void main(String[] args) throws FileNotFoundException{
    	PrintWriter rhcMovesOut = new PrintWriter("RHC_queen_moves.dat");
    	PrintWriter saMovesOut = new PrintWriter("SA_queen_moves.dat");
    	PrintWriter gaMovesOut = new PrintWriter("GA_queen_moves.dat");
    	PrintWriter mimicMovesOut = new PrintWriter("MIM_queen_moves.dat");
    	PrintWriter rhcTimeOut = new PrintWriter("RHC_queen_time.dat");
    	PrintWriter saTimeOut = new PrintWriter("SA_queen_time.dat");
    	PrintWriter gaTimeOut = new PrintWriter("GA_queen_time.dat");
    	PrintWriter mimicTimeOut = new PrintWriter("MIM_queen_time.dat");
    	int N = 100;
    	
    	for (int j=0; j<1; j++){
    		
        int[] ranges = new int[N];
        Random random = new Random(N);
        for (int i = 0; i < N; i++) {
        	ranges[i] = random.nextInt();
        }
        NQueensFitnessFunction ef = new NQueensFitnessFunction();
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 100);
        
        long starttime = System.currentTimeMillis();
        fit.train();
        System.out.println("RHC: " + ef.value(rhc.getOptimal()));
        System.out.println("RHC: Board Position: ");
//        System.out.println(ef.boardPositions());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        rhcTimeOut.println(N + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        rhcMovesOut.println(N + "\t" + ef.value(rhc.getOptimal()));
        
        
        SimulatedAnnealing sa = new SimulatedAnnealing(1E1, .1, hcp);
        fit = new FixedIterationTrainer(sa, 100);
        
        
        starttime = System.currentTimeMillis();
        fit.train();
        System.out.println("SA: " + ef.value(sa.getOptimal()));
        System.out.println("SA: Board Position: ");
//        System.out.println(ef.boardPositions());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        saTimeOut.println(N + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        saMovesOut.println(N + "\t" + ef.value(sa.getOptimal()));
        
        starttime = System.currentTimeMillis();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 0, 10, gap);
        fit = new FixedIterationTrainer(ga, 100);
        fit.train();
        System.out.println("GA: " + ef.value(ga.getOptimal()));
        System.out.println("GA: Board Position: ");
//        System.out.println(ef.boardPositions());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        gaTimeOut.println(N + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        gaMovesOut.println(N + "\t" + ef.value(ga.getOptimal()));
        
        starttime = System.currentTimeMillis();
        MIMIC mimic = new MIMIC(200, 10, pop);
        fit = new FixedIterationTrainer(mimic, 5);
        fit.train();
        System.out.println("MIMIC: " + ef.value(mimic.getOptimal()));
        System.out.println("MIMIC: Board Position: ");
//        System.out.println(ef.boardPositions());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        mimicTimeOut.println(N + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        mimicMovesOut.println(N + "\t" + ef.value(mimic.getOptimal()));
        
        N = N+5;
    	}
        rhcMovesOut.close();
    	saMovesOut.close();
    	gaMovesOut.close();
    	mimicMovesOut.close();
    	rhcTimeOut.close();
    	saTimeOut.close(); 
    	gaTimeOut.close(); 
    	mimicTimeOut.close();
    }
    
    
}
