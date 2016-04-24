package experiment;

import java.util.ArrayList;
import java.util.List;

import rl.ExplorationStrategy;
import dist.Distribution;

public class EpsilonGreedyExplorationMod implements ExplorationStrategy {
    /**
     * The epsilon value
     */
    private double epsilon;
    
    /**
     * Make a epsilon greedy strategy
     * @param epsilon the epsilon value
     */
    public EpsilonGreedyExplorationMod(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * @see rl.ExplorationStrategy#action(double[])
     */
    public int action(double[] qvalues) {
        if (Distribution.random.nextDouble() < epsilon) {
            return Distribution.random.nextInt(qvalues.length);
        }

        int best = 0;
        List<Integer> bests = new ArrayList<>();
        bests.add(best);
        for (int i = 1; i < qvalues.length; i++) {
        	double difference = Math.abs(qvalues[best] - qvalues[i]);
        	
        	if (difference < 0.0001) {
        		bests.add(i);
        	} else if (qvalues[best] < qvalues[i]) {
        		bests.clear();
                best = i;
                bests.add(best);
            }
        }
        
        int iNextAction = Distribution.random.nextInt(bests.size());
        int nextAction = bests.get(iNextAction);
        return nextAction;
    }
}