package Matlab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyICA {
	public static void main(String[] args)
	{
		double[][] input = new double[14980][15];
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("data/EEG_NN.data"));
			String curline;
			curline = br.readLine();
			String[] splitCurline;
			double[] nextData;
			int j = 0;
			while (curline != null) {
				System.out.println(j+" "+curline);
				splitCurline = curline.split(",");
				//nextData = new double[splitCurline.length];
				//			System.out.println(splitCurline.length);
				for (int i = 0; i < 14; i++)
					input[j][i] = Double.parseDouble(splitCurline[i]);

				/*data.get(data.size() - 1).add(
						new double[]{
								Double.parseDouble(
										splitCurline[splitCurline.length - 1])});*/
				if(splitCurline[splitCurline.length-1].equals("Open"))
					input[j][splitCurline.length-1]=1.0;
				else
					input[j][splitCurline.length-1]=0.0;
				j++;
				//data.add(new ArrayList<double[]>());
				//data.get(data.size() - 1).add(nextData);
				//labels.add(splitCurline[splitCurline.length-1]);
				curline = br.readLine();
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		double[][] result = FastICA.fastICA(input, 10, 0.95 ,5);
		System.out.println(result);
	}
}
