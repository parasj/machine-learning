package com.jainlabs;

/**
 * Project2
 */
public class NeuralNetworkResult {
    public long iterations;

    public long trainTimeNano;
    public long testTimeNano;

    public int truePositives;
    public int trueNegatives;
    public int falsePositives;
    public int falseNegatives;

    public int sampleCount;

    public void updateResult(boolean expected, boolean result) {
        if (expected && result) truePositives++;
        else if (expected && !result) falseNegatives++;
        else if (!expected && result) falsePositives++;
        else trueNegatives++;

        sampleCount++;
    }

    public double accuracy() {
        return (1.0 * (truePositives + trueNegatives)) / sampleCount;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%nIterations: %d, ", iterations));
        sb.append(String.format("Train Time: %d ms, ", trainTimeNano / 1000000));
        sb.append(String.format("Test Time: %d ms, ", testTimeNano / 1000000));
        sb.append(String.format("Accuracy: %d, ", (int) (100 * accuracy())));
        sb.append(String.format("(TP: %d, ", 100 * truePositives / sampleCount));
        sb.append(String.format("TN: %d, ", 100 * trueNegatives / sampleCount));
        sb.append(String.format("FP: %d, ", 100 * falsePositives / sampleCount));
        sb.append(String.format("FN: %d)", 100 * falseNegatives / sampleCount));

        return sb.toString();
    }

    public String[] csvEntries() {
        return String.format("%d#%d#%d#%f#%d#%d#%d#%d",
                iterations,
                trainTimeNano / 1000000,
                testTimeNano / 1000000,
                accuracy(),
                truePositives,
                trueNegatives,
                falsePositives,
                falseNegatives).split("#");
    }

    public static String[] csvHeader() {
        return new String[] {   "Iterations",
                                "Train Time (ms)",
                                "Test Time (ms)",
                                "Accuracy",
                                "True Positives",
                                "True Negatives",
                                "False Negatives",
                                "False Positives"   };
    }

}