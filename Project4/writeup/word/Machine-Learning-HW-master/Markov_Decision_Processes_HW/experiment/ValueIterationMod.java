package experiment;

import rl.MarkovDecisionProcess;
import rl.Policy;
import rl.PolicyLearner;

public class ValueIterationMod implements PolicyLearner {

	private static final long serialVersionUID = 1L;
	private double gamma;
    private MarkovDecisionProcess process;
    private double[] values;
    
    /**
     * Make a new value iteration
     * @param gamma the gamma decay value
     */
    public ValueIterationMod(double gamma, MarkovDecisionProcess process) {
        this.gamma = gamma;
        this.process = process;
        // the values
        values = new double[process.getStateCount()];
        for (int i = 0; i < process.getStateCount(); i++) {
            double maxActionVal = process.reward(i, 0);
            for (int a = 1; a < process.getActionCount(); a++) {
                maxActionVal = Math.max(maxActionVal, process.reward(i, a));
            }
            values[i] = maxActionVal;
        }
    }
    
    /**
     * @see shared.Trainer#train()
     */
    public double train() {
        int stateCount = process.getStateCount();
        int actionCount = process.getActionCount();
        double difference = 0;
        // loop through all the states
        for (int i = 0; i < stateCount; i++) {
            // find the maximum action
            double maxActionVal = -Double.MAX_VALUE;
            for (int a = 0; a < actionCount; a++) {
                double actionVal = 0;
                for (int j = 0; j < stateCount; j++) {
                	double transitionProb = process.transitionProbability(i, j, a);
                	//System.out.println(i + " ==" + a + "==> " + j + "\t" + transitionProb);
                    actionVal += transitionProb * values[j];
                }
                actionVal = process.reward(i, a) + gamma * actionVal;
                if (actionVal > maxActionVal) {
                    maxActionVal = actionVal;
                }
            }

            if (!process.isTerminalState(i)) {
				difference = Math.max(Math.abs(values[i] - maxActionVal), difference);
				values[i] = maxActionVal;
            }
        }
        return difference;
    }
    /**
     * @see rl.PolicyLearner#getPolicy()
     */
    public Policy getPolicy() {
        int stateCount = process.getStateCount();
        int actionCount = process.getActionCount();
        // calculate the policy based on the values
        int[] policy = new int[stateCount];
        for (int i = 0; i < stateCount; i++) {
            // find the maximum action
            double maxActionVal = -Double.MAX_VALUE;
            int maxAction = 0;
            for (int a = 0; a < actionCount; a++) {
                double actionVal = 0;
                for (int j = 0; j < stateCount; j++) {
                    actionVal += process.transitionProbability(i, j, a)
                       * values[j];
                }
                actionVal = process.reward(i, a) + gamma * actionVal;
                if (actionVal > maxActionVal) {
                    maxActionVal = actionVal;
                    maxAction = a;
                }
            }
            policy[i] = maxAction;
        }
        return new Policy(policy);
    }
}
