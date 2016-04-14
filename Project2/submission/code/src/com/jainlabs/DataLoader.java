package com.jainlabs;

import dist.*;
import opt.*;
import opt.test.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import shared.ErrorMeasure;
import func.nn.backprop.*;
import shared.filt.*;
import shared.reader.*;

import java.util.*;
import java.io.*;
import java.text.*;

import java.io.*;

/**
 * Project2
 */
public class DataLoader {
    public static final int traintest = 60;

    public Data loadARFF(String filename) throws Exception {
        DataSet data = (new ArffDataSetReader(filename)).read();
        (new LabelSplitFilter()).filter(data);
        DataSetLabelBinarySeperator.seperateLabels(data);
        DataSetDescription desc = data.getDescription();
        return splitData(data, desc);
    }

    private Data splitData(DataSet data, DataSetDescription desc) {
        TestTrainSplitFilter split = new TestTrainSplitFilter(traintest);
        split.filter(data);
        return new Data(split.getTrainingSet(), split.getTestingSet(), desc);
    }
}
