package data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import shared.DataSet;
import shared.Instance;


public class UciDataReader {
	
	private File file;

	public UciDataReader(File file) {
		this.file = file;
	}
	
	public DataSet read() throws FileNotFoundException {
		Scanner fileScan = new Scanner(file);
		
		List<Instance> instances = new ArrayList<>();
		while(fileScan.hasNextLine()) {
			instances.add(readLine(fileScan.nextLine()));
		}
		
		fileScan.close();
		
		return new DataSet(instances.toArray(new Instance[] {}));
	}
	
	private Instance readLine(String line) {
		Scanner scan = new Scanner(line);
		scan.useDelimiter(",");

		boolean category = readCategory(scan);
		List<Double> attribs = new ArrayList<Double>();
		while (scan.hasNext()) {
			attribs.add(Double.parseDouble(scan.next()));
		}
		
		double[] values = transformWrappedDoubleArrayToPrimitiveDoubleArray(attribs.toArray(new Double[] {}));
		return new Instance(values, category);
	}
	
	private boolean readCategory(Scanner scan) {
		double input = Double.parseDouble(scan.next());
		return input < 0.5;
	}
	
	private double[] transformWrappedDoubleArrayToPrimitiveDoubleArray(Double[] input) {
		double[] ret = new double[input.length];
		for (int i = 0; i < input.length; i++) {
			ret[i] = input[i];
		}
		
		return ret;
	}
}
