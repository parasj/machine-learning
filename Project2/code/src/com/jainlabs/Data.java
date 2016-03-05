package com.jainlabs;

import shared.DataSet;
import shared.DataSetDescription;

/**
 * Project2
 */
public class Data {
    private final DataSet train;
    private final DataSet test;
    private final DataSetDescription desc;

    public Data(DataSet train, DataSet test, DataSetDescription desc) {
        this.train = train;
        this.test = test;
        this.desc = desc;
    }

    public DataSet getTrain() {
        return train;
    }

    public DataSetDescription getDesc() {
        return desc;
    }

    public DataSet getTest() {
        return test;
    }
}
