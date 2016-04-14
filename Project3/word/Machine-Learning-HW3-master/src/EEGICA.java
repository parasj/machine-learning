import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import shared.DataSet;
import shared.Instance;
import shared.filt.ContinuousToDiscreteFilter;
import shared.filt.IndependentComponentAnalysis;
import util.linalg.Matrix;
import util.linalg.RectangularMatrix;


public class EEGICA {
	public static void main(String[] args){
		ArrayList<ArrayList<double[]>>data = new ArrayList<ArrayList<double[]>>();
        ArrayList<Double> labels = new ArrayList<Double>();
        
        try {
        	BufferedReader br = new BufferedReader(new FileReader("data/EEG_NN.data"));
			
			String curline = br.readLine();
			String[] splitCurline;
			double[] nextData;
			
			while (curline != null) {
				splitCurline = curline.split(",");
				nextData = new double[splitCurline.length];
//				System.out.println(splitCurline.length);
				for (int i = 0; i <= nextData.length - 2; i++)
					nextData[i] = Double.parseDouble(splitCurline[i]);

				/*data.get(data.size() - 1).add(
						new double[]{
								Double.parseDouble(
										splitCurline[splitCurline.length - 1])});*/
				if(splitCurline[splitCurline.length-1].equals("Open"))
	            	nextData[splitCurline.length-1]=1.0;
	            else
	            	nextData[splitCurline.length-1]=0.0;
				data.add(new ArrayList<double[]>());
				data.get(data.size() - 1).add(nextData);
				//labels.add(splitCurline[splitCurline.length-1]);
				curline = br.readLine();
			}
						
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        Instance[] patterns = new Instance[data.size()];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = new Instance(data.get(i).get(0));
            //patterns[i].setLabel(new Instance(labels.get(i)));
            //patterns[i].setLabel(new Instance(data.get(i).get(1)));
        }
        DataSet d = new DataSet(patterns);
//        System.out.println("Before randomizing");
//        System.out.println(d);
//        Matrix projection = new RectangularMatrix(new double[][]{ {.6, .6}, {.4, .6}});
//        for (int i = 0; i < d.size(); i++) {
//            Instance instance = d.get(i);
//            instance.setData(projection.times(instance.getData()));
//        }
        System.out.println("Before ICA");
        //System.out.println(d);
        IndependentComponentAnalysis filter = new IndependentComponentAnalysis(d, 6);
        //ICA1 filter = new ICA1(d, 6);
        System.out.println("done making it");
        filter.filter(d);
        System.out.println("After ICA");
//        ContinuousToDiscreteFilter ctdf = new ContinuousToDiscreteFilter(9);
//        ctdf.filter(d);
        PrintWriter writer;
		try {
			writer = new PrintWriter("out/eeg_ica.txt", "UTF-8");
	        writer.println(d);
	        writer.close();
	        writer = new PrintWriter("out/eeg_ica_proj.txt", "UTF-8");
	        writer.println(filter.getProjection());
	        writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//        System.out.println(d);
        
	}
}
