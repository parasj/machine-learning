package maze;

import java.util.HashMap;
import java.util.Map;

class MotionProbability {

	private Map<Integer, Double> weights = new HashMap<>();
	private double totalWeight;
	
	public void addWeight(int stateNum, double weight) {
		double currentWeight;
		if (weights.containsKey(stateNum)) {
			currentWeight = weights.get(stateNum);
		} else {
			currentWeight = 0;
		}
		
		weights.put(stateNum, currentWeight + weight);
		totalWeight += weight;
	}
	
	public double getProbability(int stateNum) {
		if (weights.containsKey(stateNum)) {
			return weights.get(stateNum) / totalWeight;
		} else {
			return 0;
		}
	}
}
