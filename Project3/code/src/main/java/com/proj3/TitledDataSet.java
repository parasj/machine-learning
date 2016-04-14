package com.proj3;

import shared.DataSet;

/**
 * code
 */
public class TitledDataSet {
    private final String name;
    private final DataSet data;

    public String getName() {
        return name;
    }

    public DataSet getData() {
        return data;
    }

    public TitledDataSet(String name, DataSet data) {
        this.name = name;
        this.data = data;
    }
}
