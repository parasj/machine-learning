package assignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.Debug.Random;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

public class TicTacToe {

	public Instances returnTrainingSet() throws Exception {
		//String dir = System.getProperty("user.dir") + "chess_king_rook_king_pawn_training.arff";
		URL path = TicTacToe.class.getResource("tictactoe_training.arff");
		File f = new File(path.getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Instances data = new Instances(reader);
		reader.close();
		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}

	public Instances returnTestSet() throws Exception {
		//String dir = System.getProperty("user.dir") + "chess_king_rook_king_pawn_test.arff";
		URL path = TicTacToe.class.getResource("tictactoe_test.arff");
		File f = new File(path.getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Instances data = new Instances(reader);
		reader.close();
		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}
	
	public Evaluation returnCrossVal(Classifier cls) throws Exception{
		Instances train = returnTrainingSet();
		cls.buildClassifier(train);
		Evaluation eval = new Evaluation(train);
		eval.crossValidateModel(cls, train, 10, new Random(1));
		//PrintWriter out = new PrintWriter("Comp" + "DecTreeTrainingFPR.dat");
		//return eval.falsePositiveRate(0);
		return eval;
	}
	
	public void outputFNRForTestSetToFile(PrintWriter out, Classifier cls, int iter) throws Exception{
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			cls.buildClassifier(newData);
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(cls, test);
			out.println(index*10 + "\t" + eval.falseNegativeRate(0));
		}
	}
	
	public void outputFNRForTrainingSetToFile(PrintWriter out, Classifier cls, int iter) throws Exception {
		for (int i = iter; i > -1; i--) {
			Instances train = returnTrainingSet();
			int index = 10 - i;
			Instances newData = setPercent(train, i);
			cls.buildClassifier(newData);
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(cls, newData);
			out.println(index * 10 + "\t" + eval.falseNegativeRate(0));
		}
	}
	
	
	public void DecisionTreeTraining() throws Exception {
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		// train classifier
		Classifier cls = new J48();
		PrintWriter out = new PrintWriter("DecTreeTrainingFPRTic.dat");
		for (int i=10; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			cls.buildClassifier(newData);
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(cls, newData);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		
		out = new PrintWriter("DecTreeTestFPRTic.dat");
		for (int i=10; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			cls.buildClassifier(newData);
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(cls, test);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();

		out = new PrintWriter("DecTreeTrainingFNRTic.dat");
		for (int i=10; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			cls.buildClassifier(newData);
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(cls, newData);
			out.println(index*10 + "\t" + eval.falseNegativeRate(0));
		}
		out.close();

		out = new PrintWriter("DecTreeTestFNRTic.dat");
		for (int i=10; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			cls.buildClassifier(newData);
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(cls, test);
			out.println(index*10 + "\t" + eval.falseNegativeRate(0));
		}
		out.close();	
	}
	
	public void ANNTraining() throws Exception{
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		MultilayerPerceptron mlp = new MultilayerPerceptron(); 
		mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 100 -V 0 -S 0 -E 20 -H 2"));
		PrintWriter out = new PrintWriter("ANNTrainingFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			mlp.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(mlp, newData);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		
		out = new PrintWriter("ANNTestFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			mlp.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(mlp, test);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("ANNTrainingFNRTic.dat");
		outputFNRForTrainingSetToFile(out, mlp, 9);
		out.close();
		
		out = new PrintWriter("ANNTestFNRTic.dat");
		outputFNRForTestSetToFile(out, mlp, 9);
		out.close();
	}
	
	public void IBKTraining() throws Exception{
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		IBk ibk = new IBk(3);
		PrintWriter out = new PrintWriter("IBKTrainingFPRTic.dat");
		for (int i=10; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			ibk.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(ibk, newData);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("IBKTestFPRTic.dat");
		for (int i=10; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			ibk.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(ibk, test);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("IBKTrainingFNRTic.dat");
		outputFNRForTrainingSetToFile(out, ibk, 10);
		out.close();
		
		out = new PrintWriter("IBKTestFNRTic.dat");
		outputFNRForTestSetToFile(out, ibk, 10);
		out.close();
	}
	
	public void SMOTrainingPolyKernel() throws Exception{
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		SMO smo = new SMO();
		PolyKernel poly = new PolyKernel();
		String[] options = new String[2];
		options[0] = "-E";
		options[1] = "5";
		poly.setOptions(options);
		smo.setKernel(poly);
		PrintWriter out = new PrintWriter("SMOTrainingFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			smo.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(smo, newData);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("SMOTestFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			smo.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(smo, test);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("SMOTrainingFNRTic.dat");
		outputFNRForTrainingSetToFile(out, smo, 9);
		out.close();
		
		out = new PrintWriter("SMOTestFNRTic.dat");
		outputFNRForTestSetToFile(out, smo, 9);
		out.close();
	}
	
	public void SMOTrainingRBFKernel() throws Exception{
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		SMO smo = new SMO();
		RBFKernel rbf = new RBFKernel();
		String[] options = new String[2];
		options[0] = "-G";
		options[1] = "0.5";
		rbf.setOptions(options);
		smo.setKernel(rbf);	
		PrintWriter out = new PrintWriter("SMORBFTrainingFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			smo.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(smo, newData);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("SMORBFTestFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			smo.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(smo, test);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("SMORBFTrainingFNRTic.dat");
		outputFNRForTrainingSetToFile(out, smo, 9);
		out.close();
		
		out = new PrintWriter("SMORBFTestFNRTic.dat");
		outputFNRForTestSetToFile(out, smo, 9);
		out.close();
	}
	
	public void boosting() throws Exception{
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		AdaBoostM1 ada = new AdaBoostM1();
		String[] options = new String[4];
		options[0] = "-W";
		options[1] = "weka.classifiers.trees.J48";
		options[2] = "-I";
		options[3] = "200";
		ada.setOptions(options);
		PrintWriter out = new PrintWriter("ADATrainingFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			ada.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(ada, newData);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();
		out = new PrintWriter("ADATestFPRTic.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances newData = setPercent(train, i);
			ada.buildClassifier(newData); 
			Evaluation eval = new Evaluation(newData);
			eval.evaluateModel(ada, test);
			out.println(index*10 + "\t" + eval.falsePositiveRate(0));
		}
		out.close();		
		out = new PrintWriter("ADATrainingFNRTic.dat");
		outputFNRForTrainingSetToFile(out, ada, 10);
		out.close();
		
		out = new PrintWriter("ADATestFNRTic.dat");
		outputFNRForTestSetToFile(out, ada, 10);
		out.close();
	
	}
	
	public Instances setPercent(Instances train, int removeAmount) throws Exception {
		// Evaluation eval = decisionTree();
		RemovePercentage remove = new RemovePercentage();
		// System.out.println("Training FPRTic");
		remove.setInputFormat(train);
		remove.setPercentage(removeAmount * 10);
		Instances newData = Filter.useFilter(train, remove);
		return newData;

	}

	public void decisionTreeNodeChanger() throws Exception{
		//PrintWriter out = new PrintWriter("SMORBFTrainingFPR.dat");
		double[] treeSize = new double[10];
		double prevTreeSize = 0;
		int count = 0;
		double[] FPR = new double[10];
		double[] FPRCrossEval = new double[10];
		double[] FNR = new double[10];
		double[] FNRCrossEval = new double[10];
		boolean done = false;
		Integer j = new Integer(0);
		Instances train = returnTrainingSet();
		Instances test = returnTestSet();
		J48 cls = new J48();
		String[] options = new String[4];
		
		while(count<10){
			j=j+1;
			options[0] = "-M";
			options[1] = j.toString();
			options[2] = "-O";
			options[3] = "-U";
			System.out.println(options[1]);
			cls.setOptions(options);
			cls.buildClassifier(train); 
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(cls, train);
			//Evaluation crossEval = returnCrossVal(cls);
			Evaluation crossEval = new Evaluation(train);
			crossEval.evaluateModel(cls,test);
	
			if (prevTreeSize != cls.measureTreeSize()){
				treeSize[count] = cls.measureTreeSize();
				FPR[count] = eval.falsePositiveRate(0);
				FPRCrossEval[count] = crossEval.falsePositiveRate(0);
				FNR[count] = eval.falseNegativeRate(0);
				FNRCrossEval[count] = crossEval.falseNegativeRate(0);
				prevTreeSize = cls.measureTreeSize();
				count++;
			}
			
			if (count == 9 && !done){
				PrintWriter out = new PrintWriter("CompDecTreeTrainingFPRTic.dat");
				for (int i=9; i>-1; i--){	
					out.println(treeSize[i] + "\t" + FPR[i]);
				}
				out.close();
				out = new PrintWriter("CompDecTreeTestFPRTic.dat");
				for (int i=9; i>-1; i--){	
					out.println(treeSize[i] + "\t" + FPRCrossEval[i]);
				}
				out.close();
				out = new PrintWriter("CompDecTreeTrainingFNRTic.dat");
				for (int i=9; i>-1; i--){	
					out.println(treeSize[i] + "\t" + FNR[i]);
				}
				out.close();
				out = new PrintWriter("CompDecTreeTestFNRTic.dat");
				for (int i=9; i>-1; i--){	
					out.println(treeSize[i] + "\t" + FNRCrossEval[i]);
				}
				out.close();
				done = true;
			}	
		}			
	}
	
	public void changeKForIBK() throws Exception{
		Integer j = new Integer(0);
		Instances train = returnTrainingSet();
		String[] options = new String[2];
		IBk ibk = new IBk();
		PrintWriter out = new PrintWriter("CompIBKTrainingFPRTic.dat");
		PrintWriter outCross = new PrintWriter("CompIBKTestFPRTic.dat");
		PrintWriter outFNR = new PrintWriter("CompIBKTrainingFNRTic.dat");
		PrintWriter outCrossFNR = new PrintWriter("CompIBKTestFNRTic.dat");

		for (int i=1; i<10; i++){
			j = i;
			options[0] = "-K";
			options[1] = j.toString();
			ibk.setOptions(options);
			ibk.buildClassifier(train); 
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(ibk, train);
			Evaluation crossEval = returnCrossVal(ibk);
			out.println(i + "\t" + eval.falsePositiveRate(0));
			outCross.println(i + "\t" + crossEval.falsePositiveRate(0));
			outFNR.println(i + "\t" + eval.falseNegativeRate(0));
			outCrossFNR.println(i + "\t" + crossEval.falseNegativeRate(0));
		}
		out.close();
		outCross.close();
		outFNR.close();
		outCrossFNR.close();
	}
	
	public void changeHiddenLayersANN()  throws Exception{
		Instances train = returnTrainingSet();
		MultilayerPerceptron mlp = new MultilayerPerceptron(); 
		PrintWriter out = new PrintWriter("CompANNTrainingFPRTic.dat");
		PrintWriter outCross = new PrintWriter("CompANNTestFPRTic.dat");
		PrintWriter outFNR = new PrintWriter("CompANNTrainingFNRTic.dat");
		PrintWriter outCrossFNR = new PrintWriter("CompANNTestFNRTic.dat");
		for (int i=0; i<10; i++){
			mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 100 -V 0 -S 0 -E 20 -H " + i));
			mlp.buildClassifier(train); 
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(mlp, train);
			Evaluation crossEval = returnCrossVal(mlp);
			out.println(i + "\t" + eval.falsePositiveRate(0));
			outCross.println(i + "\t" + crossEval.falsePositiveRate(0));
			outFNR.println(i + "\t" + eval.falseNegativeRate(0));
			outCrossFNR.println(i + "\t" + crossEval.falseNegativeRate(0));
		}
		out.close();
		outCross.close();
		outFNR.close();
		outCrossFNR.close();
	}
	
	public void numIterationsforBoosting() throws Exception{
		Integer j = new Integer(0);
		Instances train = returnTrainingSet();
		AdaBoostM1 ada = new AdaBoostM1();
		String[] options = new String[4];
		PrintWriter out = new PrintWriter("NumIterADATrainingFPRTic.dat");
		PrintWriter outCross = new PrintWriter("NumIterADATestFPRTic.dat");
		PrintWriter outFNR = new PrintWriter("NumIterADATrainingFNRTic.dat");
		PrintWriter outCrossFNR = new PrintWriter("NumIterADATestFNRTic.dat");
		for (int i = 1; i < 200; i++) {
			j = i;
			options[0] = "-W";
			options[1] = "weka.classifiers.trees.J48";
			options[2] = "-I";
			options[3] = j.toString();
			ada.setOptions(options);
			ada.buildClassifier(train); 
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(ada, train);
			Evaluation crossEval = returnCrossVal(ada);
			out.println(i + "\t" + eval.falsePositiveRate(0));
			outCross.println(i + "\t" + crossEval.falsePositiveRate(0));
			outFNR.println(i + "\t" + eval.falseNegativeRate(0));
			outCrossFNR.println(i + "\t" + crossEval.falseNegativeRate(0));
		}
		out.close();
		outCross.close();
		outFNR.close();
		outCrossFNR.close();
	}
	
	public void changeNumberOfEpochsANN()  throws Exception{
		Instances train = returnTrainingSet();
		MultilayerPerceptron mlp = new MultilayerPerceptron(); 
		PrintWriter out = new PrintWriter("NumEpochsANNTrainingFPRTic.dat");
		PrintWriter outCross = new PrintWriter("NumEpochsANNTestFPRTic.dat");
		PrintWriter outFNR = new PrintWriter("NumEpochsANNTrainingFNRTic.dat");
		PrintWriter outCrossFNR = new PrintWriter("NumEpochsANNTestFNRTic.dat");
		for (int i=1; i<11; i++){
			mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -V 0 -S 0 -E 20 -H 2 -N " + i));
			mlp.buildClassifier(train); 
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(mlp, train);
			Evaluation crossEval = returnCrossVal(mlp);
			out.println(i + "\t" + eval.falsePositiveRate(0));
			outCross.println(i + "\t" + crossEval.falsePositiveRate(0));
			outFNR.println(i + "\t" + eval.falseNegativeRate(0));
			outCrossFNR.println(i + "\t" + crossEval.falseNegativeRate(0));
		}
		out.close();
		outCross.close();
		outFNR.close();
		outCrossFNR.close();
	}


	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TicTacToe cs = new TicTacToe();
		cs.DecisionTreeTraining();
		cs.ANNTraining();
		cs.IBKTraining();
		cs.SMOTrainingPolyKernel();
		cs.SMOTrainingRBFKernel();
		cs.boosting();
		
		//stuff below this is for complexity model
		cs.decisionTreeNodeChanger();
		cs.changeKForIBK();
		cs.changeHiddenLayersANN();
		cs.numIterationsforBoosting();
		cs.changeNumberOfEpochsANN();
	}

}
