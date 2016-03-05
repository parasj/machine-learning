package com.jainlabs;

import com.opencsv.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {

    public static void main(String[] args) {
        long st = System.nanoTime();
        List<Integer> values = Arrays.asList(5, 10, 20, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 7000, 10000, 15000, 20000);

        try {
            List<NeuralNetworkResult> results = values.stream().parallel()
                    .map(i -> new NeuralNetworkRunner("vote.arff", UseOptAlg.RandomizedHillClimbing, i))
                    .map(NeuralNetworkRunner::run)
                    .collect(Collectors.toList());
            writeToCSV(results, "rhc");
            System.out.println("rhc");

            results = values.stream().parallel()
                    .map(i -> new NeuralNetworkRunner("vote.arff", UseOptAlg.SimulatedAnnealing, i))
                    .map(NeuralNetworkRunner::run)
                    .collect(Collectors.toList());
            writeToCSV(results, "sa");
            System.out.println("sa");



            results = values.stream().parallel()
                    .map(i -> new NeuralNetworkRunner("vote.arff", UseOptAlg.GeneticAlgorithms, i))
                    .map(NeuralNetworkRunner::run)
                    .collect(Collectors.toList());
            writeToCSV(results, "ga");
            System.out.println("ga");
        } catch (IOException e) {
            e.printStackTrace();
        }

        long et = System.nanoTime();
        System.out.printf("%d ms", (et - st) / 1000000);
    }

    private static void writeToCSV(List<NeuralNetworkResult> results, String type) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("results/4nn-output-" + type + ".tsv"), '\t');
        writer.writeNext(NeuralNetworkResult.csvHeader());
        writer.writeAll(results.stream().map(NeuralNetworkResult::csvEntries).collect(Collectors.toList()));
        writer.close();
    }
}
