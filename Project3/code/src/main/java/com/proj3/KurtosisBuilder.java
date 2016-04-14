package com.proj3;

import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;

import java.util.*;

/**
 * code
 */
public class KurtosisBuilder {
    Map<Integer, List<Double>> d;

    public KurtosisBuilder() {
        d = Collections.synchronizedMap(new TreeMap<>());
    }

    public void insert(int k, double v) {
        List<Double> l = d.get(k);
        if (l == null) {
            l = Collections.synchronizedList(new ArrayList<>());
            d.put(k, l);
        }
        l.add(v);
    }

    public double[] kurtosis() {
        return d.values().parallelStream()
                .filter(l -> l != null)
                .map(l -> {
                    Kurtosis k = new Kurtosis();
                    double[] arr =
                            l.stream()
                                    .mapToDouble(i -> i)
                                    .toArray();
                    return k.evaluate(arr);
                })
                .mapToDouble(i -> i)
                .toArray();
    }
}
