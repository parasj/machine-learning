package com.jainlabs;

import com.ABIGAIL.TravelingSalesmanTest;

import java.util.Arrays;
import java.util.List;

/**
 * code
 */
public class TSPRunner {
    public static void main(String[] args) {
        OptimizationResult results = new OptimizationResult();
        List<Integer> vals = Arrays.asList(8, 16, 32, 64, 70, 80);
        vals.parallelStream().forEach(x -> results.add(x, TravelingSalesmanTest.run(x)));
        results.print();
        results.writeToCSV("tsp-result.tsv");
    }
}
