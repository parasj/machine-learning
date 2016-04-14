import shared.*;
import shared.tester.AccuracyTestMetric;
import shared.tester.ConfusionMatrixTestMetric;
import shared.tester.NeuralNetworkTester;
import shared.tester.RawOutputTestMetric;
import func.nn.backprop.*;

import java.io.*;
import java.text.*;

public class NeuralNetTrainer implements Runnable {
    private final Instance[] instances;

    /**
     * Number of nodes per layer.
     */
    private int inputLayerNodes, hiddenLayerNodes, outputLayerNodes;
    
    /**
     * The number of hidden layers in the Neural Network
     */
    private int numHiddenLayers;
    
    
    /**
     * The number of iterations to train the network over
     */
    private final int trainingIterations;

    
    /**
     * Directory to output the results to
     */
    private final String dataDir = "results/";
    
    /**
     * What to append after the {@link DataSet}'s name to the results file
     */
    private final String extension;
    
    /**
     * The name of the {@link DataSet}. Used for writing results
     */
    private final String setName;
    
    /**
     * The {@link DataSet} to train and evaluate the Neural Net on
     */
    private final DataSet set;
    
    /** 
     * Error bound
     */
    private double epsilon; 
    
    private final BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    private final GradientErrorMeasure measure = new SumOfSquaresError();    
    private final WeightUpdateRule updateRule = new RPROPUpdateRule();
    private final DecimalFormat df = new DecimalFormat("0.000");
    
    /**
     * Construct a NeuralNetTrainer with the given parameters using the default epsilon of 0.5
     * @param iterations the number of iterations to train over
     * @param set {@link DataSet} to train on
     * @param extension string to append to the results file (used to delineate on set of results from another)
     * @param name of the {@link DataSet}
     */
    public NeuralNetTrainer(int iterations, DataSet set, String extension, String name) {
    	setArbitraryDefaults();
    	
    	this.trainingIterations = iterations;
    	this.instances = set.getInstances();
    	this.set = set;
    	this.extension = extension;
    	this.setName = name;
    	this.inputLayerNodes = this.set.get(0).size();
    }
    
    /**
     * Construct a NeuralNetTrainer with the given parameters with a user-given epsilon value
     * @param iterations
     * @param set
     * @param extension
     * @param name
     */
    public NeuralNetTrainer(int iterations, DataSet set, String extension, String name, double epsilon) {
    	this(iterations, set, extension, name);
    	this.setEpsilon(epsilon);
    }
    
    /**
     * Set arbitrary defaults for settable values: number of hidden layers, number of nodes per hidden layer, 
     * epsilon, and the number of nodes in the output layer
     */
    private void setArbitraryDefaults() {
    	this.setNumHiddenLayers(1);
    	this.setHiddenLayerNodes(10);
    	this.setEpsilon(1e-6);
    	this.setOutputLayerNodes(1);
    	
    }
    
    /**
     * Train the neural network on the {@link DataSet} provided upon construction, then
     * evaluate its accuracy and print the results to a file
     */
    public void run() {		
    	double correct = 0, incorrect = 0;

        BackPropagationNetwork network = factory.createClassificationNetwork(
           new int[] { inputLayerNodes, getHiddenLayerNodes(), getOutputLayerNodes() });

        Trainer trainer = new FixedIterationTrainer(
               new BatchBackPropagationTrainer(set, network,
            		   measure, updateRule), trainingIterations);
        
        long start = System.nanoTime();
        trainer.train(); //throw away its return value.
        long end = System.nanoTime();
        double trainingTime = (end - start)/Math.pow(10,9);
        
        // Evaluate the accuracy of the neural network
        AccuracyTestMetric atm = new AccuracyTestMetric();
        ConfusionMatrixTestMetric cmtm = new ConfusionMatrixTestMetric(set.getDescription().getLabelDescription());
        RawOutputTestMetric rotm = new RawOutputTestMetric();
        NeuralNetworkTester tester = new NeuralNetworkTester(network, atm, cmtm, rotm);
        tester.setEpsilon(this.getEpsilon());
        
        start = System.nanoTime();
        tester.test(instances);
        end = System.nanoTime();
        double testingTime = (end - start)/Math.pow(10,9);

        try {
            BufferedWriter bw = new BufferedWriter(
            		new FileWriter(new File(dataDir + "nn_results_" + setName+extension+".txt")));
            bw.write(atm.getResults());
            bw.newLine();
            bw.write(String.format("Training time: %f\nTesting time: %f", trainingTime, testingTime));
            bw.newLine();
            bw.newLine();
            bw.write(cmtm.getResults());
            bw.newLine();
            bw.write(rotm.getResults());
			bw.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}      
    }

    /**
     * @return the number of nodes in the output layer
     */
	public int getOutputLayerNodes() {
		return outputLayerNodes;
	}

	/**
	 * @param outputLayerNodes the desired number of nodes in the output layer
	 */
	public void setOutputLayerNodes(int outputLayerNodes) {
		this.outputLayerNodes = outputLayerNodes;
	}

	/**
	 * @return the number of hidden layers in the neural network
	 */
	public int getNumHiddenLayers() {
		return numHiddenLayers;
	}

	/**
	 * @param numHiddenLayers the number of hidden layers desired in the neural network
	 */
	public void setNumHiddenLayers(int numHiddenLayers) {
		this.numHiddenLayers = numHiddenLayers;
	}

	/**
	 * @return the number of nodes per hidden layer
	 */
	public int getHiddenLayerNodes() {
		return hiddenLayerNodes;
	}

	/**
	 * @param hiddenLayerNodes the desired number of nodes per hidden layer
	 */
	public void setHiddenLayerNodes(int hiddenLayerNodes) {
		this.hiddenLayerNodes = hiddenLayerNodes;
	}

	/**
	 * @return the epsilon
	 */
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * @param epsilon the epsilon to set
	 */
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

}
