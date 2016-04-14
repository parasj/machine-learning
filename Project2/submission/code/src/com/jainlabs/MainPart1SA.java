package com.jainlabs;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * code
 */
public class MainPart1SA {
    public static void main(String[] args) {
        List<Double> values = Arrays.asList(0.9999, 0.999, 0.995, 0.99, 0.95, 0.9, 0.75, 0.5, 0.25, 0.1, 0.05);
        List<List<NeuralNetworkResult>> trials = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            System.out.println("Trial " + (i + 1));
            trials.add(values.stream().parallel()
                    .map(c -> new NeuralNetworkRunner("vote.arff", UseOptAlg.SimulatedAnnealing, 500, c))
                    .map(NeuralNetworkRunner::run)
                    .collect(Collectors.toList()));
        }

        List<Double> ap =
                IntStream.range(0, values.size())
                    .mapToObj(currentIter ->
                            trials.stream()
                                .map(x -> x.get(currentIter))
                                .mapToDouble(NeuralNetworkResult::accuracy)
                                .average().getAsDouble())
                    .collect(Collectors.toList());

        System.out.println(ap);
        writeToCSV(values, ap);
    }

    private static void writeToCSV(List<Double> col1, List<Double> col2) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("results/sa-cooling1.tsv"), '\t');
            writer.writeNext(new String[] { "Cooling Value", "Average Performance" });
            for (int i = 0; i < col1.size(); i++)
                writer.writeNext(new String[] { col1.get(i).toString(), col2.get(i).toString() });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
