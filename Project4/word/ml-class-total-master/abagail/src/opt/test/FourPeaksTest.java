package opt.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

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
import opt.example.FourPeaksEvaluationFunction;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * Copied from ContinuousPeaksTest
 * 
 * @version 1.0
 */
public class FourPeaksTest {
	/** The n value */
	//private static final int N = 100;
	/** The t value */
	// private static final int T = N / 5;

	private static final int T = 11;

	public static void main(String[] args) throws FileNotFoundException{
		
		int N = 80;
		
		PrintWriter rhcMovesOut = new PrintWriter("RHC_peak_max.dat");
    	PrintWriter saMovesOut = new PrintWriter("SA_peak_max.dat");
    	PrintWriter gaMovesOut = new PrintWriter("GA_peak_max.dat");
    	PrintWriter mimicMovesOut = new PrintWriter("MIM_peak_max.dat");
    	PrintWriter rhcTimeOut = new PrintWriter("RHC_peak_time.dat");
    	PrintWriter saTimeOut = new PrintWriter("SA_peak_time.dat");
    	PrintWriter gaTimeOut = new PrintWriter("GA_peak_time.dat");
    	PrintWriter mimicTimeOut = new PrintWriter("MIM_peak_time.dat");
		
    	for (int l = 0; l<5; l++){
    		
    		double rhcAv = 0;
    		double saAv = 0;
    		double gaAv = 0;
    		double mimAv = 0;
    		
    		double rhcTime = 0;
    		double saTime = 0;
    		double gaTime = 0;
    		double mimTime = 0;
    	
		for (int m = 0; m < 10; m++) {

			int[] ranges = new int[N];
			Arrays.fill(ranges, 2);
			EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
			Distribution odd = new DiscreteUniformDistribution(ranges);
			NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
			MutationFunction mf = new DiscreteChangeOneMutation(ranges);
			CrossoverFunction cf = new SingleCrossOver();
			Distribution df = new DiscreteDependencyTree(.1, ranges);
			HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
			GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
			ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

			RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
			FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
			long starttime = System.currentTimeMillis();
			fit.train();
			System.out.println("RHC: " + ef.value(rhc.getOptimal()));
			System.out.println("Time : " + (System.currentTimeMillis() - starttime));
			System.out.println("============================");
			rhcTime = rhcTime + System.currentTimeMillis() - starttime;
			rhcAv = rhcAv + ef.value(rhc.getOptimal());
			
			SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .05, hcp);
			fit = new FixedIterationTrainer(sa, 200000);
			starttime = System.currentTimeMillis();
			fit.train();
			System.out.println("SA: " + ef.value(sa.getOptimal()));
			System.out.println("Time : " + (System.currentTimeMillis() - starttime));
			System.out.println("============================");
			saTime = saTime + System.currentTimeMillis() - starttime;
			saAv = saAv + ef.value(sa.getOptimal());

			//StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
			 StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(500, 400, 3, gap);
			fit = new FixedIterationTrainer(ga, 1000);
			starttime = System.currentTimeMillis();
			fit.train();
			System.out.println("GA: " + ef.value(ga.getOptimal()));
			System.out.println("Time : " + (System.currentTimeMillis() - starttime));
			System.out.println("============================");
			gaTime = gaTime + System.currentTimeMillis() - starttime;
			gaAv = gaAv + ef.value(ga.getOptimal());
			
			MIMIC mimic = new MIMIC(2000, 20, pop);
			fit = new FixedIterationTrainer(mimic, 1);
			starttime = System.currentTimeMillis();
			fit.train();
			System.out.println("MIMIC: " + ef.value(mimic.getOptimal()));
			System.out.println("Time : " + (System.currentTimeMillis() - starttime));
			System.out.println("============================");
			mimTime =mimTime +  System.currentTimeMillis() - starttime;
			mimAv = mimAv + ef.value(mimic.getOptimal());
		}
		
		rhcAv = rhcAv/10;
		saAv = saAv/10;
		gaAv = gaAv/10;
		mimAv = mimAv/10;
		
		rhcTime = rhcTime/10;
		saTime = saTime/10;
		gaTime = gaTime/10;
		mimTime = mimTime/10;
		
		rhcTimeOut.println(N + "\t" + rhcTime);
		rhcMovesOut.println(N + "\t" + rhcAv);
		saTimeOut.println(N + "\t" + saTime);
		saMovesOut.println(N + "\t" + saAv);
		gaTimeOut.println(N + "\t" + gaTime);
		gaMovesOut.println(N + "\t" + gaAv);
		mimicTimeOut.println(N + "\t" + mimTime);
		mimicMovesOut.println(N + "\t" + mimAv);
		
		N = N + 10;
    	}
		
//		System.out.println(rhcAv);
//		System.out.println(saAv);
//		System.out.println(gaAv);
//		System.out.println(mimAv);
		
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
