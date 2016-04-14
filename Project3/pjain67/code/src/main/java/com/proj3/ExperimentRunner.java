package com.proj3;

import shared.DataSet;
import shared.filt.LabelSelectFilter;
import shared.reader.CSVDataSetReader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * code
 */
public class ExperimentRunner {
    public List<Tuple<String, String>> spambase_reduced;
    public final static List<Integer> DIMS = Arrays.asList(1, 3, 5, 10);

    public List<Tuple<String, String>> runReduce(String d) {
        List<Tuple<String, String>> flattened = new ArrayList<>();
        DIMS.parallelStream()
                .map(dim -> Worker.reduce(Worker.read(d + ".arff", d), dim))
                .forEach(flattened::addAll);

        return flattened;
    }

    public void runRP(String d, int max, int delta) {
        List<Integer> dims = IntStream.range(1, max)
                                    .filter(i -> i % delta == 0)
                                    .mapToObj(i -> i)
                                    .collect(Collectors.toList());

        System.out.println(dims);

        Worker.runRPReconstructExperiment(Worker.read(d + ".arff", d), dims, 100);
    }

    private void runNNReduced(List<Tuple<String, String>> reduced, String set) {
        reduced.parallelStream()
                .map(fn -> new Tuple<>(safeReadCSV(fn._1), fn))
                .map(ds -> {
                    LabelSelectFilter lsl = new LabelSelectFilter(ds._1.get(0).size() - 2); // second to last parameter
                    lsl.filter(ds._1);
//                    return new NeuralNetTrainer(ds._1, ds._2._2, set);
                    return "";
                })
//                .map(NeuralNetTrainer::train)
//                .map(NeuralNetTrainer::test)
//                .map(NeuralNetTrainer::write)
                .map(f -> String.format("\tNN(REDUCED)\t->\t%s", f))
                .forEach(System.out::println);
    }

    private DataSet safeReadCSV(String s) {
        try {
            return (new CSVDataSetReader(s)).read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void run() {
//        System.out.println("Reducing spambase");
//        spambase_reduced = runReduce("vote");
//        spambase_reduced = runReduce("spambase");
//        System.out.println("Running NN on reduced spambase");
//        System.out.println("RP(vote)");
//        runRP("vote", 15, 2);
//        System.out.println("RP(spambase)");
//        runRP("spambase", 58, 2);
//        runRP("spambase_discrete", 58, 2);
        System.out.println("RP(vote)");
        runRP("vote", 16, 1);

    }
}