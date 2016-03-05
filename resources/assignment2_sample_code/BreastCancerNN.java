/**
 * 
 */
package optproblems.nn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import shared.Instance;
import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;

/**
 * BreastCancerNN.java
 * 
 * @author Karthik
 *
 */
public class BreastCancerNN {
	
	/** The network. */
	private BackPropagationNetwork network;
	private static Instance[] patterns;
	private static ArrayList<ArrayList<double[]>> data;
	public static int TESTS = 10;
	
	/**
	 * Instantiates a new breast cancer neural network.
	 */
	public BreastCancerNN() {
		
        BackPropagationNetworkFactory factory = 
            new BackPropagationNetworkFactory();
        
        data = new ArrayList<ArrayList<double[]>>();
        
        try {
			BufferedReader br = new BufferedReader(new FileReader("BCTest.data"));
			
			String curline = br.readLine();
			String[] splitCurline;
			double[] nextData;
			
			while (curline != null) {
				splitCurline = curline.split(",");
				nextData = new double[splitCurline.length - 2];
				for (int i = 1; i <= nextData.length - 2; i++)
					nextData[i - 1] = Double.parseDouble(splitCurline[i]);
				data.add(new ArrayList<double[]>());
				data.get(data.size() - 1).add(nextData);
				data.get(data.size() - 1).add(
						new double[]{
								Double.parseDouble(
										splitCurline[splitCurline.length - 1])});
				curline = br.readLine();
			}
						
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        patterns = new Instance[data.size()];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = new Instance(data.get(i).get(0));
            patterns[i].setLabel(new Instance(data.get(i).get(1)));
        }
                
        network = factory.createClassificationNetwork(
           new int[] { 30, 30, 1});
	}
	
	/**
	 * Sets the weights.
	 * 
	 * @param weights the new weights
	 */
	public void setWeights(double[] weights) {
		network.setWeights(weights);
	}
	
	/**
	 * Cross.
	 * 
	 * @param one the one
	 * @param two the two
	 * @return the breast cancer nn
	 */
	public static BreastCancerNN[] cross(BreastCancerNN one, BreastCancerNN two) {
		
		double[] weightsOne = new double[one.network.getWeights().length];
		
		for (int i = 0; i <= weightsOne.length / 2; i++)
			weightsOne[i] = one.network.getWeights()[i];
		for (int i = weightsOne.length / 2 + 1; i <= weightsOne.length - 1; i++)
			weightsOne[i] = two.network.getWeights()[i];
		
		double[] weightsTwo = new double[one.network.getWeights().length];
		
		for (int i = 0; i <= weightsTwo.length / 2; i++)
			weightsTwo[i] = two.network.getWeights()[i];
		for (int i = weightsTwo.length / 2 + 1; i <= weightsTwo.length - 1; i++)
			weightsTwo[i] = one.network.getWeights()[i];
		
		return new BreastCancerNN[]{createBreastCancerNN(weightsOne), createBreastCancerNN(weightsTwo)};
	}
	
	/**
	 * Mutate.
	 * 
	 * @param cur the cur
	 * @return the breast cancer nn
	 */
	public static BreastCancerNN mutate(BreastCancerNN cur) {
		BreastCancerNN ret = createBreastCancerNN(cur.network.getWeights());
		ret.network.getWeights()[(int)(Math.random() * ret.network.getWeights().length)] = -1.5 + Math.random() * 3;
		return ret;
	}
	
	/**
	 * Fitness.
	 * 
	 * @return the double
	 */
	public double fitness() {
		return fitness(network.getWeights());
	}
	
	/**
	 * Fitness.
	 * 
	 * @param weights the weights
	 * @return the double
	 */
	public double fitness(double[] weights) {
		network.setWeights(weights);
		int correct = 0;
		
        for (int i = 0; i <= TESTS - 1; i++) {
        	
        	int instanceIndex = (int)(Math.random() * patterns.length);
            network.setInputValues(patterns[instanceIndex].getData());
            network.run();
            
            double correctLabel = data.get(instanceIndex).get(1)[0];
            double proposedLabel = network.getOutputValues().get(0);
            
            //System.out.println("Correct label: " + correctLabel);
            //System.out.println("Proposed label: " + proposedLabel);
            //System.out.println();
            
            if (Math.abs(correctLabel - proposedLabel) <= 0.4)
            	correct += 1;
        }
        
        return correct * 1.0;
	}
	
	/**
	 * Gets the representation.
	 * 
	 * @return the representation
	 */
	public double[] getRepresentation() {
		return network.getWeights();
	}
	
	/**
	 * Creates the breast cancer nn.
	 * 
	 * @param weights the weights
	 * @return the breast cancer nn
	 */
	public static BreastCancerNN createBreastCancerNN(double[] weights) { 
		BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
		BackPropagationNetwork ret = factory.createClassificationNetwork(new int[]{30, 30, 1});
		BreastCancerNN bcnn = new BreastCancerNN();
		bcnn.network = ret;
		ret.setWeights(weights);
		return bcnn;
	}
}
