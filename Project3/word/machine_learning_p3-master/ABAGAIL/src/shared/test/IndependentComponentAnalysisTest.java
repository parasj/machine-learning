package shared.test;

import shared.DataSet;
import shared.Instance;
import shared.filt.IndependentComponentAnalysis;
import util.linalg.Matrix;
import util.linalg.RectangularMatrix;

import java.util.*;
import java.io.*;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class IndependentComponentAnalysisTest {

    // Define the attributes and instances, setup dataset.
    private static int numAttributes;
    private static int numInstances;
    private static Instance[] instances;
    private static String dataFileSource;
    private static DataSet set;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
        dataFileSource = args[0];
        numAttributes = Integer.parseInt(args[2]);
        numInstances = Integer.parseInt(args[3]);
        instances = initializeInstances();
        set = new DataSet(instances);
        int components = Integer.parseInt(args[1]);
        IndependentComponentAnalysis filter = new IndependentComponentAnalysis(set, components);
        filter.filter(set);
        System.out.println("@RELATION bcica" + args[1]);
        for (int i = 0; i < components; i++) {
            System.out.println("@ATTRIBUTE ica" + i + "     REAL");
        }
        System.out.println("@ATTRIBUTE class    {0.000000,1.000000}");
        System.out.println("");
        System.out.println("@DATA");
        System.out.println(set);
          
    }

    private static Instance[] initializeInstances() {

        double[][][] attributes = new double[numInstances][][];

        try {
            // M = 0, B = 1
            BufferedReader br = new BufferedReader(new FileReader(new File(dataFileSource)));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[numAttributes];
                attributes[i][1] = new double[1];

                for(int j = 0; j < numAttributes; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // dataset is already formatted to have 0 or 1 for classification.
            instances[i].setLabel(new Instance(attributes[i][1][0]));

        }

        return instances;
    }

}