package com.jainlabs;

import com.ABIGAIL.KnapsackTest;

import java.util.Arrays;
import java.util.List;

/**
 * code
 */
public class KnapsackRunner {
    public static void main(String[] args) {
        OptimizationResult results = new OptimizationResult();
        List<Integer> vals = Arrays.asList(8, 16, 32, 64, 128, 256);
        vals.parallelStream().forEach(x -> results.add(x, KnapsackTest.run(x)));
        results.print();
        results.writeToCSV("knapsack-result.tsv");
    }
}
