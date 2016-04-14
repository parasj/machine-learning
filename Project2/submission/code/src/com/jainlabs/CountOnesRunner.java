package com.jainlabs;

import com.ABIGAIL.CountOnesTest;

import java.util.Arrays;
import java.util.List;

/**
 * code
 */
public class CountOnesRunner {
    public static void main(String[] args) {
        OptimizationResult results = new OptimizationResult();
        List<Integer> vals = Arrays.asList(1, 2, 4, 6, 8, 10, 15, 20, 30, 40, 50, 60, 70, 80, 100);
        vals.parallelStream()
                .forEach(x -> results.add(x, CountOnesTest.run(x)));
        results.print();
        results.writeToCSV("count-ones-result.tsv");
    }
}
