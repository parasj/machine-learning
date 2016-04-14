package assignment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;

import tpp.TargetedProjectionPursuit;
import weka.attributeSelection.PrincipalComponents;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Debug.Random;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.AddCluster;
import weka.filters.unsupervised.attribute.IndependentComponents;
import weka.filters.unsupervised.attribute.RandomProjection;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.attributeSelection.*;

public class ChessSet3 {
	
	public Instances returnTrainingSet() throws Exception {
		//String dir = System.getProperty("user.dir") + "chess_king_rook_king_pawn_training.arff";
		URL path = ChessSet3.class.getResource("chess_king_rook_king_pawn_training.arff");
		File f = new File(path.getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Instances data = new Instances(reader);
		reader.close();
		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}

	public Instances returnTppTrainingSet() throws Exception {
		//String dir = System.getProperty("user.dir") + "chess_king_rook_king_pawn_training.arff";
		URL path = ChessSet3.class.getResource("chess_king_rook_king_pawn_training_tpp.arff");
		File f = new File(path.getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Instances data = new Instances(reader);
		reader.close();
		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}
	
	public Evaluation returnCrossVal(Classifier cls, Instances newData) throws Exception{
		//Instances train = returnTrainingSet();
		cls.buildClassifier(newData);
		Evaluation eval = new Evaluation(newData);
		eval.evaluateModel(cls, newData);
		eval.crossValidateModel(cls, newData, 10, new Random(1));
		//PrintWriter out = new PrintWriter("Comp" + "DecTreeTrainingFPR.dat");
		//return eval.falsePositiveRate(0);
		return eval;
	}
	
	public Instances setPercent(Instances train, int removeAmount) throws Exception {
		// Evaluation eval = decisionTree();
		RemovePercentage remove = new RemovePercentage();
		// System.out.println("Training FPR");
		remove.setInputFormat(train);
		remove.setPercentage(removeAmount * 10);
		Instances newData = Filter.useFilter(train, remove);
		return newData;

	}
	
	public Instances prinComp(Instances interData) throws Exception{
		PrincipalComponents pca = new PrincipalComponents();
		pca.setOptions(Utils.splitOptions("-R 0.95 -A 5 -M -1"));
		pca.buildEvaluator(interData);
		return pca.transformedData(interData);
	}
	
	public Instances indepComp(Instances pcaData) throws Exception{
		IndependentComponents ica = new IndependentComponents();
		ica.setOptions(Utils.splitOptions("-W -A -1 -N 200 -T 1.0E-4"));
		ica.setInputFormat(pcaData);
		return ica.useFilter(pcaData, ica);
	}
	
	public Instances randProj(Instances interData) throws Exception{
		RandomProjection rp = new RandomProjection();
		rp.setOptions(Utils.splitOptions("-N 10 -R 42 -D Gaussian"));
		rp.setInputFormat(interData);
		return rp.useFilter(interData, rp);
	}
	
	public Instances createKMeansCluster(Instances newData, int k) throws Exception{
		SimpleKMeans skm = new SimpleKMeans();
		AddCluster ac = new AddCluster();
		skm.setOptions(Utils.splitOptions("-init 2 -max-candidates 100 -periodic-pruning 10000 -min-density 2.0 -t1 10.25 -t2 10.0 -N " + k + " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10"));
		ac.setClusterer(skm);
		ac.setInputFormat(newData);
		return ac.useFilter(newData, ac);
	}
	
	public Instances createEMCluster(Instances newData, int k) throws Exception{
		EM em = new EM();
		AddCluster ac = new AddCluster();
		em.setOptions(Utils.splitOptions("-I 200 -N " + k +  " -X 10 -max " + k + " -ll-cv 1.0E-6 -ll-iter 1.0E-6 -M 1.0E-6 -K 10 -num-slots 1 -S 100"));
		ac.setClusterer(em);
		ac.setInputFormat(newData);
		return ac.useFilter(newData, ac);
	}
	
	public Instances removeAttr(Instances newData) throws Exception{
		Remove rm = new Remove();
		rm.setOptions(Utils.splitOptions("-R 1-36"));
		Reorder re = new Reorder();
		re.setOptions(Utils.splitOptions("-R last-first"));
		rm.setInputFormat(newData);
		Instances unorderedData = Filter.useFilter(newData, rm);
		re.setInputFormat(unorderedData);
		return Filter.useFilter(unorderedData, re);
		
	}
	
	public Instances reorderClassandCluster(Instances newData) throws Exception {
		newData.setClassIndex(36);
		return newData;
	}
	
	public void trainData(Instances finalData, PrintWriter out, Classifier cls, int index) throws Exception{
			cls.buildClassifier(finalData); 
			Evaluation eval = new Evaluation(finalData);
			eval.evaluateModel(cls, finalData);
			//out.println(index*10 + "\t" + eval.falsePositiveRate(0));
			out.println(index*10 + "\t" + eval.errorRate());
	}
	
	public void testData(Instances finalData, PrintWriter out, Classifier cls, int index) throws Exception{
			cls.buildClassifier(finalData); 
			Evaluation crossEval = returnCrossVal(cls, finalData);
			out.println(index*10 + "\t" + crossEval.errorRate());	
	}
	
	public void ANNTraining() throws Exception{
		Instances train = returnTrainingSet();
		Instances tppTrain = returnTppTrainingSet();
		//Instances test = returnTestSet();
		MultilayerPerceptron mlp = new MultilayerPerceptron(); 
		mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 100 -V 0 -S 0 -E 20 -H 2"));
		PrintWriter out = new PrintWriter("ANNTrainingPCA.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = setPercent(train, i);
			Instances finalData = prinComp(interData);
			trainData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTestPCA.dat");	
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = setPercent(train, i);
			Instances finalData = prinComp(interData);
			testData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTrainingICA.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = setPercent(train, i);
			Instances pcaData = prinComp(interData);
			Instances finalData = indepComp(pcaData);
			trainData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTestICA.dat");	
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = setPercent(train, i);
			Instances pcaData = prinComp(interData);
			Instances finalData = indepComp(pcaData);
			testData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTrainingRP.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = setPercent(train, i);;
			Instances finalData = randProj(interData);
			trainData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTestRP.dat");	
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = setPercent(train, i);
			Instances finalData = randProj(interData);
			testData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTrainingTPP.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances finalData = setPercent(tppTrain, i);;		
			trainData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTestTPP.dat");	
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances finalData = setPercent(tppTrain, i);		
			testData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTrainingErr.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances finalData = setPercent(train, i);;		
			trainData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTestErr.dat");	
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances finalData = setPercent(train, i);		
			testData(finalData, out, mlp, index);
		}	
		out.close();	
		
		out = new PrintWriter("ANNTrainingKMeans.dat");
		for (int i=20; i>1; i--){
			int index = 22-i;
			Instances finalData = removeAttr(createKMeansCluster(train, i));
			trainData(finalData, out, mlp, index);	
		}
		out.close();
		
		out = new PrintWriter("ANNTestKMeans.dat");
		for (int i=20; i>1; i--){
			int index = 22-i;
			Instances finalData = removeAttr(createKMeansCluster(train, i));
			testData(finalData, out, mlp, index);	
		}
		out.close();
		
		out = new PrintWriter("ANNTrainingEM.dat");
		for (int i=20; i>1; i--){
			int index = 22-i;
			Instances finalData = removeAttr(createEMCluster(train, i));
			trainData(finalData, out, mlp, index);	
		}
		out.close();
		
		out = new PrintWriter("ANNTestEM.dat");
		for (int i=20; i>1; i--){
			int index = 22-i;
			Instances finalData = removeAttr(createEMCluster(train, i));
			testData(finalData, out, mlp, index);	
		}
		out.close();
		
		out = new PrintWriter("ANNTrainingKMeansLC.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = createKMeansCluster(train, 5);
			Instances finalData = setPercent(reorderClassandCluster(interData), i);		
			trainData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTestKMeansLC.dat");
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = createKMeansCluster(train, 5);
			Instances finalData = setPercent(reorderClassandCluster(interData), i);		
			testData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTrainingEMLC.dat");	
		for (int i=9; i>-1; i--){
			int index = 10-i;
			Instances interData = createEMCluster(train, 2);
			Instances finalData = setPercent(reorderClassandCluster(interData), i);		
			trainData(finalData, out, mlp, index);
		}	
		out.close();
		
		out = new PrintWriter("ANNTestEMLC.dat");	
		for (int i=9; i>-1; i--){
			int index = 10-i;			
			Instances interData = createEMCluster(train, 2);
			Instances finalData = setPercent(reorderClassandCluster(interData), i);		
			testData(finalData, out, mlp, index);
		}	
		out.close();
		
		
	}
	

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		ChessSet3 cs = new ChessSet3();

		cs.ANNTraining();
	}

}
