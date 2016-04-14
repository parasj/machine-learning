package com.proj3;

import shared.DataSet;
import shared.DataSetWriter;
import shared.Instance;
import shared.filt.*;
import shared.reader.ArffDataSetReader;
import util.linalg.*;
import util.linalg.Vector;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * code
 */
public class Worker {
    private static final double EPSILON = 1;

    public static TitledDataSet read(String filename, String name) {
        try {
            return new TitledDataSet(name, (new ArffDataSetReader(FileLocator.DATA_DIR + "in/" + filename)).read());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static void runRPReconstructExperiment(TitledDataSet tds, List<Integer> dims, int trials) {
        int len = tds.getData().get(0).size();
        List<double[]> results = dims.stream()
                .map(d -> IntStream.range(0, trials)
                        .mapToDouble(underscore -> rpReconstructionExperiment(tds, d, len))
                        .toArray())
                .collect(Collectors.toList());

        int index = (int)(Math.random()*Math.min(dims.size(), results.size()));

        System.out.println(Arrays.toString(results.get(index)));

//        for (int i = 0; i < Math.min(dims.size(), results.size()); i++) {
//            System.out.printf("%d\t%.4f%n", dims.get(i), results.get(i));
//        }

    }

    public static double rpReconstructionExperiment(TitledDataSet tds, int dim, int len) {
        ReversibleFilter rp = new RandomizedProjectionFilter(dim, len);
        DataSet data = tds.getData();
        List<Instance> orig_instances =
                Arrays.asList(tds.getData().getInstances()).stream()
                    .map(i -> (Instance) i.copy())
                    .collect(Collectors.toList());

        rp.filter(data);
        rp.reverse(data);

        List<Instance> new_instances =
                Arrays.asList(tds.getData().getInstances()).stream()
                        .map(i -> (Instance) i.copy())
                        .collect(Collectors.toList());

        tds.getData().setInstances(orig_instances.toArray(new Instance[orig_instances.size()]));

        List<Double> deltas = new ArrayList<>();

        double result = IntStream.range(0, Math.min(orig_instances.size(), new_instances.size()))
                .mapToObj(i -> new Tuple<>(orig_instances.get(i), new_instances.get(i)))
                .map(t -> new Tuple<>(t._1.getData(), t._2.getData()))
                .mapToDouble(t -> {
                    Vector origi = t._1;
                    Vector newi = t._2;

                    int n = Math.min(origi.size(), newi.size());
                    int m = IntStream.range(0, n)
                            .map(i -> {
                                deltas.add(Math.abs(origi.get(i) - newi.get(i)));
                                if (Math.abs(origi.get(i) - newi.get(i)) < EPSILON) return 1;
                                else return 0;
                            }).sum();

                    return (m * 1.0) / n;
                }).summaryStatistics().getAverage();

//        System.out.println(deltas.stream().mapToDouble(i -> i).summaryStatistics());

        return result;
    }

    public static List<Tuple<String, String>> reduce(TitledDataSet tds, int dim) {
        List<Tuple<Tuple<String, String>, DataSet>> reduced = getFilters(tds, dim)
            .stream()
            .map(t -> {
                String filename = String.format("%sout/reduced/%s-%s-%d.csv", FileLocator.DATA_DIR, tds.getName(), t._2, dim);
                String filenamerev = String.format("%sout/reduced/reverse-%s-%s-%d.csv", FileLocator.DATA_DIR, tds.getName(), t._2, dim);
                t._1.filter(tds.getData());

                KurtosisBuilder k = new KurtosisBuilder();
                Arrays.asList(tds.getData().getInstances())
                        .parallelStream()
                        .map(Instance::getData)
                        .forEach(v -> {
                            for (int i = 0; i < v.size(); i++) {
                                k.insert(i, v.get(i));
                            }
                        });

                String transaction = t._2 + "(" + tds.getName() + ")";
                System.out.printf("\t%s:%s\t->\t%s%n", transaction, filename.substring(filename.lastIndexOf('/')), Arrays.toString(k.kurtosis()));

                safeWrite(new DataSetWriter(tds.getData(), filename));
                t._1.reverse(tds.getData());

                safeWrite(new DataSetWriter(tds.getData(), filenamerev));

                return new Tuple<>(new Tuple<>(filename, t._2 + "-" + dim), tds.getData());
            })
            .collect(Collectors.toList());

//        System.out.println(reduced.size());
//        Arrays.asList(reduced.get(0)._2.getInstances())
//                .stream()
//                .map(Instance::getData)
//                .forEach(System.out::println);


        return reduced.stream().map(a -> a._1).collect(Collectors.toList());
    }

    private static void safeWrite(DataSetWriter w) {
        try {
            w.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Tuple<ReversibleFilter, String>> getFilters(TitledDataSet tds, int DIM) {
        List<Tuple<ReversibleFilter, String>> filters = new ArrayList<>();
//        filters.add(new Tuple<>(new PrincipalComponentAnalysis(tds.getData(), DIM), "pca"));
        filters.add(new Tuple<>(new IndependentComponentAnalysis(tds.getData(), DIM), "ica"));
        filters.add(new Tuple<>(new RandomizedProjectionFilter(DIM, tds.getData().get(0).size()), "rp"));
//        filters.add(new Tuple<>(new InsignificantComponentAnalysis(tds.getData(), DIM), "insig"));
        return filters;
    }
}
