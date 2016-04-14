package com.proj3;

import shared.DataSet;
import weka.classifiers.Evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 * code
 */
public class NeuralNetTrainer {
    private String csv;
    private final String ext;
    private final String set;

    private MultilayerPerceptron nn;

    public NeuralNetTrainer(String csv, String ext, String set) {
        this.csv = csv;
        this.ext = ext;
        this.set = set;

        String[] options = new String[1];
        options[0] = "-N 500";

        nn = new MultilayerPerceptron();
        try {
            nn.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CSVLoader loader = new CSVLoader();
        try {
            loader.setSource(new File(csv));
            Instances data = loader.getDataSet();
            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NeuralNetTrainer train() {
        return this;
    }

    public NeuralNetTrainer test() {
        return this;
    }


    public static String write(NeuralNetTrainer neuralNetTrainer) {
        return "";
    }
}
