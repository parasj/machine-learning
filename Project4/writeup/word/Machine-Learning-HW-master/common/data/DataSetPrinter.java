package data;

import java.io.PrintStream;

import shared.DataSet;
import shared.Instance;

public class DataSetPrinter {

	public void printData(PrintStream printer, DataSet data) {
		for (Instance each : data) {
			printer.println(each.getLabel().getDiscrete() + "," + each.getData());
		}
	}
}
