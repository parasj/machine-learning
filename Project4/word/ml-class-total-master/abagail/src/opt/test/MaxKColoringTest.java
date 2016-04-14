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
import opt.ga.MaxKColorFitnessFunction;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.SwapMutation;
import opt.ga.Vertex;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author kmandal
 * @version 1.0
 */
public class MaxKColoringTest {
    /** The n value */
    private static final int N = 1000; // number of vertices
    private static final int L =3; // L adjacent nodes per vertex
   // private static final int K = 10; // K possible colors
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws FileNotFoundException{
    	PrintWriter rhcMovesOut = new PrintWriter("RHC_maxk_moves.dat");
    	PrintWriter saMovesOut = new PrintWriter("SA_maxk_moves.dat");
    	PrintWriter gaMovesOut = new PrintWriter("GA_maxk_moves.dat");
    	PrintWriter mimicMovesOut = new PrintWriter("MIM_maxk_moves.dat");
    	PrintWriter rhcTimeOut = new PrintWriter("RHC_maxk_time.dat");
    	PrintWriter saTimeOut = new PrintWriter("SA_maxk_time.dat");
    	PrintWriter gaTimeOut = new PrintWriter("GA_maxk_time.dat");
    	PrintWriter mimicTimeOut = new PrintWriter("MIM_maxk_time.dat");
    	
    	int K = 10;
    	
    	for (int m=0;m<8;m++){
    	
        Random random = new Random(N*L);
        // create the random velocity
        Vertex[] vertices = new Vertex[N];
        for (int i = 0; i < N; i++) {
            Vertex vertex = new Vertex();
            vertices[i] = vertex;	
            vertex.setAdjMatrixSize(L);
            for(int j = 0; j <L; j++ ){
            	 vertex.getAadjacencyColorMatrix().add(random.nextInt(N*L));
            }
        }
        /*for (int i = 0; i < N; i++) {
            Vertex vertex = vertices[i];
            System.out.println(Arrays.toString(vertex.getAadjacencyColorMatrix().toArray()));
        }*/
        // for rhc, sa, and ga we use a permutation based encoding
        MaxKColorFitnessFunction ef = new MaxKColorFitnessFunction(vertices);
        Distribution odd = new DiscretePermutationDistribution(K);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new SingleCrossOver();
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        
        Distribution df = new DiscreteDependencyTree(.1); 
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        long starttime = System.currentTimeMillis();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 20000);
        fit.train();
        System.out.println("RHC: " + ef.value(rhc.getOptimal()));
        System.out.println(ef.foundConflict());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        rhcTimeOut.println(K + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        rhcMovesOut.println(K + "\t" + ef.value(rhc.getOptimal()));
        
        starttime = System.currentTimeMillis();
        SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .1, hcp);
        fit = new FixedIterationTrainer(sa, 20000);
        fit.train();
        System.out.println("SA: " + ef.value(sa.getOptimal()));
        System.out.println(ef.foundConflict());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        saTimeOut.println(K + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        saMovesOut.println(K + "\t" + ef.value(sa.getOptimal()));
        
        starttime = System.currentTimeMillis();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 10, 60, gap);
        fit = new FixedIterationTrainer(ga, 50);
        fit.train();
        System.out.println("GA: " + ef.value(ga.getOptimal()));
        System.out.println(ef.foundConflict());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        gaTimeOut.println(K + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        gaMovesOut.println(K + "\t" + ef.value(ga.getOptimal()));
        
        starttime = System.currentTimeMillis();
        MIMIC mimic = new MIMIC(200, 100, pop);
        fit = new FixedIterationTrainer(mimic, 5);
        fit.train();
        System.out.println("MIMIC: " + ef.value(mimic.getOptimal()));  
        System.out.println(ef.foundConflict());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        mimicTimeOut.println(K + "\t" + (System.currentTimeMillis() - starttime));
        System.out.println("============================");
        
        mimicMovesOut.println(K + "\t" + ef.value(mimic.getOptimal()));
        
        K = K + 5;
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
