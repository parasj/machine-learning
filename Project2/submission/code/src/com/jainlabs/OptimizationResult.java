package com.jainlabs;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * code
 */
public class OptimizationResult {
    private SortedMap<Double, OptimizationResultTuple> data;

    public OptimizationResult() {
        this.data = new TreeMap<>();
    }

    public void add(double var, OptimizationResultTuple t) {
        data.put(var, t);
    }

    public void print() {
        data.keySet().forEach(key -> System.out.printf("%f: %s%n", key, data.get(key).toString()));
    }

    public void writeToCSV(String filename) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("results/" + filename), '\t');
            String[] header = new String[] { "IndependentVariable", "RHC", "SA", "GA", "MIMIC" };
            writer.writeNext(header);
            data.entrySet().forEach(e -> writer.writeNext(e.getValue().toCSV(e.getKey())));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
