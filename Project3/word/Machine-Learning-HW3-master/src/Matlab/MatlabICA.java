package Matlab;

/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    MatlabICA.java
 *    Copyright (C) 2002 Sugato Basu and Mikhail Bilenko 
 *
 */


import  java.io.*;
import  java.util.*;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeEvaluator;
import weka.attributeSelection.AttributeTransformer;
import  weka.core.*;
import  weka.filters.unsupervised.attribute.ReplaceMissingValues;
import  weka.filters.unsupervised.attribute.Normalize;
import  weka.filters.unsupervised.attribute.NominalToBinary;
import  weka.filters.unsupervised.attribute.Remove;
import  weka.filters.Filter;

/**
 * Class for performing independent components analysis/transformation. <p>
 *
 * Valid options are:<p>
 * -D <br>
 * Don't normalize the input data. <p>
 *
 * -T <br>
 * Transform through the IC space and back to the original space. <p>
 * 
 * -N <br> num
 * Number of independant components
 *
 * -A <br> approach
 * ICA Approach
 *
 * -F <br> function
 * ICA function
 *
 * @author Sugato Basu
 * @author Mikhail Bilenko
 * @version $Revision: 1.2 $
 */
public class MatlabICA 
  implements AttributeEvaluator, AttributeTransformer, OptionHandler {
  
  /** The data to transform analyse/transform */
  private Instances m_trainInstances;

  /** Keep a copy for the class attribute (if set) */
  private Instances m_trainCopy;

  /** The header for the transformed data format */
  private Instances m_transformedFormat;

  /** The header for data transformed back to the original space */
  private Instances m_originalSpaceFormat;

  /** Data has a class set */
  private boolean m_hasClass;

  /** Class index */
  private int m_classIndex;

  /** Number of attributes */
  private int m_numAttribs;

  /** Number of instances */
  private int m_numInstances;

  /** Name of the Matlab program file that computes ICA */
  protected String m_ICAMFile = new String("/var/local/MatlabICA.m");

  /** Will hold the mixing matrix */
  protected double [][] m_mixingMatrix;

  /** Will hold the inverse of the mixing matrix */
  protected double [][] m_inverseMixingMatrix;

  /** Will hold the independent components */
  protected double [][] m_independentComponents;

  /** A timestamp suffix for matching vectors with attributes */
  String m_timestamp = null;

  /** Name of the file where attribute names will be stored */
  String m_icaAttributeFilename = null;
  /** Name of the file where attribute names will be stored */
  String m_icaAttributeFilenameBase = new String("/var/local/ICAattributes");
  
  /** Name of the file where dataMatrix will be stored */
  public String m_dataFilename = new String("/var/local/ICAdataMatrix.txt");
  
  /** Name of the file where mixingMatrix will be stored */
  public String m_mixingMatrixFilename = null;
  public String m_mixingMatrixFilenameBase = new String("/var/local/ICAmixingMatrix");

  /** Name of the file where inverseMixingMatrix will be stored */
  public String m_inverseMixingMatrixFilename = new String("/var/local/ICAinverseMixingMatrix.txt");

  /** Name of the file where independentComponents will be stored */
  public String m_independentComponentsFilename = null;
  public String m_independentComponentsFilenameBase = new String("/var/local/ICAindependentComponents");
    
  /** Filters for original data */
  private ReplaceMissingValues m_replaceMissingFilter;
  private Normalize m_normalizeFilter;
  private Remove m_attributeFilter;
  
  /** The number of attributes in the ic transformed data */
  private int m_outputNumAtts = -1;
  
  /** normalize the input data? */
  private boolean m_normalize = true;

  /** transform the data through the ic space and back to the original
      space ? */
  private boolean m_transBackToOriginal = false;

  /** The attribute evaluator to use */
  private ASEvaluation m_eval = new weka.attributeSelection.ChiSquaredAttributeEval();

  /** load eigenvalues of covariance matrix from file? */
  protected boolean m_loadEigenValuesFromFile = false;

  /** set m_loadEigenValuesFromFile */
  public void setLoadEigenValuesFromFile(boolean choice) {
    m_loadEigenValuesFromFile = choice;
  }
  
  /** get m_loadEigenValuesFromFile */
  public boolean getLoadEigenValuesFromFile () {
    return m_loadEigenValuesFromFile;
  }

  /** load eigenvectors of covariance matrix from file? */
  protected boolean m_loadEigenVectorsFromFile = false;

  /** set m_loadEigenVectorsFromFile */
  public void setLoadEigenVectorsFromFile(boolean choice) {
    m_loadEigenVectorsFromFile = choice;
  }
  
  /** get m_loadEigenVectorsFromFile */
  public boolean getLoadEigenVectorsFromFile () {
    return m_loadEigenVectorsFromFile;
  }

  /** number of Independent Components */
  protected int m_NumIndependentComponents = 2;

  /** set number of Independent Components */
  public void setNumIndependentComponents(int n) {
    m_NumIndependentComponents = n;
    System.out.println("Number of ICA components: " + n);
  }

  /** get number of Independent Components */
  public int getNumIndependentComponents() {
    return m_NumIndependentComponents;
  }

  /* Define possible ICA approaches */
  public static final int APPROACH_SYMM = 0;
  public static final int APPROACH_DEFL = 1;
  public static final Tag[] TAGS_APPROACH = {
    new Tag(APPROACH_SYMM, "symm"),
    new Tag(APPROACH_DEFL, "defl")
  };
  protected int m_ICAapproach = APPROACH_SYMM;

  /** get ICA approach */
  public SelectedTag getICAapproach ()
  {
    return new SelectedTag(m_ICAapproach, TAGS_APPROACH);
  }

  /** set ICA approach */
  public void setICAapproach (SelectedTag approach)
  {
    if (approach.getTags() == TAGS_APPROACH) {
      System.out.println("Approach: " + approach.getSelectedTag().getReadable());
      m_ICAapproach = approach.getSelectedTag().getID();
    }
  }

  /* Define possible ICA functions */
  public static final int FUNCTION_TANH = 0;
  public static final int FUNCTION_GAUSS = 1;
  public static final int FUNCTION_POW3 = 2;
  public static final int FUNCTION_SKEW = 3;
  public static final Tag[] TAGS_FUNCTION = {
    new Tag(FUNCTION_TANH, "tanh"),
    new Tag(FUNCTION_GAUSS, "gauss"),
    new Tag(FUNCTION_POW3, "pow3"),
    new Tag(FUNCTION_SKEW, "skew")
  };
  protected int m_ICAfunction = FUNCTION_TANH;

  /** get ICA function */
  public SelectedTag getICAfunction ()
  {
    return new SelectedTag(m_ICAfunction, TAGS_FUNCTION);
  }

  /** set ICA function */
  public void setICAfunction (SelectedTag function)
  {
    if (function.getTags() == TAGS_FUNCTION) {
      System.out.println("Function: " + function.getSelectedTag().getReadable());
      m_ICAfunction = function.getSelectedTag().getID();
    }
  }

  /**
   * Returns a string describing this attribute transformer
   * @return a description of the evaluator suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Performs a independent components analysis and transformation of "
      +"the data. Use in conjunction with a Ranker search. Dimensionality "
      +"reduction is accomplished by choosing enough eigenvectors to "
      +"account for some percentage of the variance in the original data---"
      +"default 0.95 (95%). Attribute noise can be filtered by transforming "
      +"to the IC space, eliminating some of the worst eigenvectors, and "
      +"then transforming back to the original space.";
  }

  /**
   * Returns an enumeration describing the available options. <p>
   *
   * @return an enumeration of all the available options.
   **/
  public Enumeration listOptions () {
    Vector newVector = new Vector(3);
    newVector.addElement(new Option("\tDon't normalize input data." 
				    , "D", 0, "-D"));
    
    newVector.addElement(new Option("\tTransform through the IC space and "
				    +"\n\tback to the original space."
				    , "O", 0, "-O"));

    newVector.addElement(new Option("\tNumber of independant components." 
				    , "N", 1, "-N"));

    newVector.addElement(new Option("\tICA approach." 
				    , "A", 1, "-A"));

    newVector.addElement(new Option("\tICA function." 
				    , "F", 1, "-F"));

    return  newVector.elements();
  }

  /**
   * Parses a given list of options.
   *
   * Valid options are:<p>
   * -D <br>
   * Don't normalize the input data. <p>
   *
   * -T <br>
   * Transform through the IC space and back to the original space. <p>
   *
   * -N <br> num
   * Number of independant components
   *
   * -A <br> approach
   * ICA Approach
   *
   * -F <br> function
   * ICA function
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  public void setOptions (String[] options)
    throws Exception
  {
    resetOptions();
    String optionString;

    setNormalize(!Utils.getFlag('D', options));
    setTransformBackToOriginal(Utils.getFlag('O', options));

    optionString = Utils.getOption('F', options);
    if (optionString.length() != 0) {
      setICAfunction(new SelectedTag(Integer.parseInt(optionString), TAGS_FUNCTION));
    }
    optionString = Utils.getOption('A', options);
    if (optionString.length() != 0) {
      setICAapproach(new SelectedTag(Integer.parseInt(optionString), TAGS_APPROACH));
    }
    optionString = Utils.getOption('N', options);
    if (optionString.length() != 0) {
      setNumIndependentComponents(Integer.parseInt(optionString));
    }
  }

  /**
   * Reset to defaults
   */
  private void resetOptions() {
    m_normalize = false;
    m_transBackToOriginal = false;
    m_ICAfunction = 0;
    m_ICAapproach = 0;
    m_eval = new weka.attributeSelection.ChiSquaredAttributeEval();
    m_NumIndependentComponents = 2;
  }

  /**
   * Sets the attribute evaluator
   *
   * @param evaluator the evaluator with all options set.
   */
  public void setEvaluator(ASEvaluation evaluator) {
    m_eval = evaluator;
  }

  /**
   * Gets the attribute evaluator used
   *
   * @return the attribute evaluator
   */
  public ASEvaluation getEvaluator() {
    return m_eval;
  }


  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String normalizeTipText() {
    return "Normalize input data.";
  }

  /**
   * Set whether input data will be normalized.
   * @param n true if input data is to be normalized
   */
  public void setNormalize(boolean n) {
    m_normalize = n;
  }

  /**
   * Gets whether or not input data is to be normalized
   * @return true if input data is to be normalized
   */
  public boolean getNormalize() {
    return m_normalize;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String transformBackToOriginalTipText() {
    return "Transform through the IC space and back to the original space. "
      +"If only the best n ICs are retained (by setting varianceCovered < 1) "
      +"then this option will give a dataset in the original space but with "
      +"less attribute noise.";
  }

  /**
   * Sets whether the data should be transformed back to the original
   * space
   * @param b true if the data should be transformed back to the
   * original space
   */
  public void setTransformBackToOriginal(boolean b) {
    m_transBackToOriginal = b;
  }
  
  /**
   * Gets whether the data is to be transformed back to the original
   * space.
   * @return true if the data is to be transformed back to the original space
   */
  public boolean getTransformBackToOriginal() {
    return m_transBackToOriginal;
  }

  /**
   * Gets the current settings of MatlabICA
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {

    String[] options = new String[10];
    int current = 0;

    if (!getNormalize()) {
      options[current++] = " -D";
    }

    if (getTransformBackToOriginal()) {
      options[current++] = " -O";
    }
    options[current++] = " -A";
    options[current++] = "" + getICAapproach().getSelectedTag().getReadable();
    options[current++] = " -F";
    options[current++] = "" + getICAfunction().getSelectedTag().getReadable();
    options[current++] = " -N";
    options[current++] = "" + getNumIndependentComponents();
    
    while (current < options.length) {
      options[current++] = "";
    }
    return  options;
  }

  /**
   * Initializes independent components and performs the analysis
   * @param data the instances to analyse/transform
   * @exception Exception if analysis fails
   */
  public void buildEvaluator(Instances data) throws Exception {
    buildAttributeConstructor(data);
  }

  private void buildAttributeConstructor (Instances data) throws Exception {

    System.out.println("data.numInstances: " + data.numInstances());
    m_independentComponents = null;
    m_outputNumAtts = -1;
    m_attributeFilter = null;

    if (data.checkForStringAttributes()) {
      throw  new UnsupportedAttributeTypeException("Can't handle string attributes!");
    }
    m_trainInstances = data;
    System.out.println("ClassIndex is " + m_trainInstances.classIndex());

    // make a copy of the training data so that we can get the class
    // column to append to the transformed data (if necessary)
    m_trainCopy = new Instances(m_trainInstances);
    System.out.println("Copied instances");
    m_replaceMissingFilter = new ReplaceMissingValues();
    m_replaceMissingFilter.setInputFormat(m_trainInstances);
    m_trainInstances = Filter.useFilter(m_trainInstances, 
					m_replaceMissingFilter);
    System.out.println("Replaced missing values");

    if (m_normalize) {
      m_normalizeFilter = new Normalize();
      m_normalizeFilter.setInputFormat(m_trainInstances);
      m_trainInstances = Filter.useFilter(m_trainInstances, m_normalizeFilter);
      System.out.println("Normalized");
    }

    // delete any attributes with only one distinct value or are all missing
    Vector deleteCols = new Vector();
    for (int i=0;i<m_trainInstances.numAttributes();i++) {
      if (m_trainInstances.numDistinctValues(i) <=1) {
	deleteCols.addElement(new Integer(i));
      }
    }
    System.out.println("Deleted single-value attributes");

    if (m_trainInstances.classIndex() >=0) {
      // get rid of the class column
      m_hasClass = true;
      m_classIndex = m_trainInstances.classIndex();
      deleteCols.addElement(new Integer(m_classIndex));
      System.out.println("Deleted class attributes");
    }

    // remove columns from the data if necessary
    if (deleteCols.size() > 0) {
      m_attributeFilter = new Remove();
      int [] todelete = new int [deleteCols.size()];
      for (int i=0;i<deleteCols.size();i++) {
	todelete[i] = ((Integer)(deleteCols.elementAt(i))).intValue();
      }
      m_attributeFilter.setAttributeIndicesArray(todelete);
      m_attributeFilter.setInvertSelection(false);
      m_attributeFilter.setInputFormat(m_trainInstances);
      m_trainInstances = Filter.useFilter(m_trainInstances, m_attributeFilter);
    }
    System.out.println("Removed attributes filtered above");
    
    m_numInstances = m_trainInstances.numInstances();
    m_numAttribs = m_trainInstances.numAttributes();

    if (m_timestamp == null) { 
      m_timestamp = getLogTimestamp();
      m_icaAttributeFilename = new String(m_icaAttributeFilenameBase + m_timestamp + ".txt");
      m_mixingMatrixFilename = new String(m_mixingMatrixFilenameBase + m_timestamp + ".txt");
      m_independentComponentsFilename = new String(m_independentComponentsFilenameBase + m_timestamp + ".txt");
    }

    MatlabPCA.dumpAttributeNames(m_trainInstances, m_icaAttributeFilename);

    System.out.println("About to run ICA on " + m_numInstances + " instances, each with " + m_numAttribs + " attributes");
    dumpInstances(m_dataFilename);
    prepareMatlab(m_ICAMFile);
    runMatlab(m_ICAMFile, "/var/local/ICAMatlab.output");
    System.out.println("Done training ... now parsing matlab output files");

    m_mixingMatrix = readColumnVectors(m_mixingMatrixFilename);
    m_inverseMixingMatrix = readColumnVectors(m_inverseMixingMatrixFilename);
    m_independentComponents = readColumnVectors(m_independentComponentsFilename);
    if (m_mixingMatrix == null || m_independentComponents == null || m_inverseMixingMatrix == null) {
      System.out.println("WARNING!! Could not parse matlab output files");
      m_originalSpaceFormat = setOutputFormatOriginal();
      m_transformedFormat = m_originalSpaceFormat;
      m_outputNumAtts = m_originalSpaceFormat.numAttributes();
    }
    else {
      System.out.println("Successfully parsed matlab output files");
      System.out.println("MixingMatrix: " + m_mixingMatrix.length + "x" + m_mixingMatrix[0].length);
      System.out.println("InverseMixingMatrix: " + m_inverseMixingMatrix.length + "x" + m_inverseMixingMatrix[0].length);
      m_transformedFormat = setOutputFormat();
      if (m_transBackToOriginal) {
	m_originalSpaceFormat = setOutputFormatOriginal();
      }
    }

    // Build the attribute evaluator
    if (m_trainInstances.classIndex() >= 0) {
      m_eval.buildEvaluator(transformedData());
    }
  }

  /**
   * Read column vectors from a text file
   * @param name file name
   * @return a <code>double[][]</code> value
   * @exception Exception if an error occurs
   * @returns double[][] array corresponding to vectors
   */
  public double[][] readColumnVectors(String name) throws Exception {
    BufferedReader r = new BufferedReader(new FileReader(name));
    int numAttributes = -1, numVectors = -1;
        
    // number of rows
    String s =  r.readLine();
    try {
      numAttributes = (int)Double.parseDouble(s);
    } catch (Exception e) {
      System.err.println("Couldn't parse " + s + " as Double");
    }
    
    // number of columns
    s = r.readLine();
    try { 
      numVectors = (int)Double.parseDouble(s);
    } catch (Exception e) {
      System.err.println("Couldn't parse " + s + " as Double");
    }

    if (numAttributes == 0 || numVectors == 0)
      return null;

    double[][] vectors = new double[numAttributes][numVectors];
    int i = 0, j = 0;
    while ((s = r.readLine()) != null) {
      StringTokenizer tokenizer = new StringTokenizer(s);
      while (tokenizer.hasMoreTokens()) {
	String value = tokenizer.nextToken();
	try { 
	  vectors[i][j] = Double.parseDouble(value);
	} catch (Exception e) {
	  System.err.println("Couldn't parse " + value + " as double");
	}
	j++;
	if (j > numVectors) {
	  System.err.println("Too many vectors(" + j + " instead of " + numVectors + ") in line: " + s);
	}
      }
      if (j != numVectors) {
	System.err.println("Too few vectors(" + j + " instead of " + numVectors + ") in line: " + s);
      }
      j = 0;
      i++;
      if (i > numAttributes) {
	System.err.println("Too many attributes: " + i + " expecting " + numAttributes + " attributes");
      }
    }
    if (i != numAttributes) {
      System.err.println("Too few attributes: " + i + " expecting " + numAttributes + " attributes");
    }
    return vectors;
  }

  /**
   * Returns just the header for the transformed data (ie. an empty
   * set of instances. This is so that AttributeSelection can
   * determine the structure of the transformed data without actually
   * having to get all the transformed data through getTransformedData().
   * @return the header of the transformed data.
   * @exception Exception if the header of the transformed data can't
   * be determined.
   */
  public Instances transformedHeader() throws Exception {
    if (m_independentComponents == null) {
      // throw new Exception("Independent components hasn't been built yet");
      System.out.println("WARNING!! Independent components could not be built, returning original data");
    }
    if (m_transBackToOriginal) {
      return m_originalSpaceFormat;
    } else {
      return m_transformedFormat;
    }
  }

  /**
   * Gets the transformed training data.
   * @return the transformed training data
   * @exception Exception if transformed data can't be returned
   */
  public Instances transformedData() throws Exception {
    if (m_independentComponents == null) {
      //      throw new Exception("Independent components hasn't been built yet");
      System.out.println("WARNING!! Independent components could not be built, returning original data");
      return m_trainCopy;
    }
    Instances output;

    if (m_transBackToOriginal) {
      output = new Instances(m_originalSpaceFormat);
    } else {
      output = new Instances(m_transformedFormat);
    }
    for (int i=0;i<m_trainCopy.numInstances();i++) {
      Instance converted = convertInstance(m_trainCopy.instance(i));
      System.out.println("Converted instance: " + converted);
      output.add(converted);
    }

    return output;
  }

  /**
   * Evaluates the merit of a transformed attribute. This is defined
   * to be 1 minus the cumulative variance explained. Merit can't
   * be meaningfully evaluated if the data is to be transformed back
   * to the original space.
   * @param att the attribute to be evaluated
   * @return the merit of a transformed attribute
   * @exception Exception if attribute can't be evaluated
   */
  public double evaluateAttribute(int att) throws Exception {
    if (m_independentComponents == null) {
      //      throw new Exception("Independent components hasn't been built yet!");
      System.out.println("WARNING!! Independent components could not be built, returning original data");
    }
    if (!(m_eval instanceof AttributeEvaluator)) {
      throw new Exception("Invalid attribute evaluator!");
    }
    if (m_trainInstances.classIndex() < 0) {
      return 1;
    } else {
      return ((AttributeEvaluator)m_eval).evaluateAttribute(att);
    }
  }

  /**
   * Dump data matrix into a file
   */
  private void dumpInstances(String tempFile) {
    try { 
      PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(tempFile)));
      for (int k = 0; k < m_numInstances; k++) {
	Instance instance = m_trainInstances.instance(k);
	for (int j = 0; j < m_numAttribs; j++) {
	  writer.print(instance.value(j) + " ");
	}
	writer.println();
      }
      writer.close();
    } catch (Exception e) {
      System.err.println("Could not create a temporary file for dumping the data matrix: " + e);
    }
  }

  /** Create matlab m-file for ICA
   * @param filename file where matlab script is created
   */
  public void prepareMatlab(String filename) {
    try{
      PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(filename)));
      writer.println("addpath /var/local;");
      writer.println("DATA = load('/var/local/ICAdataMatrix.txt');");
      if (m_loadEigenValuesFromFile == true) {
	writer.println("E = load('/var/local/PCAeigenValues.txt');");
      }
      if (m_loadEigenVectorsFromFile == true) {
	writer.println("V = load('/var/local/PCAeigenVectors.txt');");
      }
      writer.print("[IC, A, invA] = fastica(DATA'"); // take transpose of data matrix, to make each instance a column
      if (m_loadEigenValuesFromFile == true) {
	writer.print(",'pcaD',E");
      }
      if (m_loadEigenVectorsFromFile == true) {
	writer.print(",'pcaE',V");
      }
      writer.print(",'approach','" + TAGS_APPROACH[m_ICAapproach].getReadable() + "'");
      writer.print(",'g','" + TAGS_FUNCTION[m_ICAfunction].getReadable() + "'");
      writer.print(",'numOfIC'," + m_NumIndependentComponents);
      writer.println(",'displayMode','off','stabilization','on');");
      writer.println("[ICnumRows, ICnumCols] = size(IC);");
      writer.println("[AnumRows, AnumCols] = size(A);");
      writer.println("[invAnumRows, invAnumCols] = size(invA);\n");

      writer.println("save " + m_mixingMatrixFilename + " AnumRows AnumCols A -ASCII -DOUBLE");
      writer.println("save " + m_inverseMixingMatrixFilename + " invAnumRows invAnumCols invA -ASCII -DOUBLE");
      writer.println("save " + m_independentComponentsFilename + " ICnumRows ICnumCols IC -ASCII -DOUBLE");
      writer.close();
    } 
    catch (Exception e) {
      System.err.println("Could not create matlab file: " + e);
    }
  }


  /** Run matlab in command line with a given argument
   * @param inFile file to be input to Matlab
   * @param outFile file where results are stored
   */
  public static void runMatlab(String inFile, String outFile) {
    // call matlab to do the dirty work
    try {
      int exitValue;
      do {
	System.out.println("Starting to run matlab");
	Process proc = Runtime.getRuntime().exec("matlab -tty < " + inFile + " > " + outFile);
	exitValue = proc.waitFor();
	if (exitValue != 0) {
	  System.err.println("WARNING!!!!!  Matlab returned exit value 1, trying again in 5 mins!");
	  Thread.sleep(300000);
	}
      } while (exitValue != 0);
      System.out.println("End of running matlab, exitValue = " + exitValue);
    } 
    catch (Exception e) {
      System.err.println("Problems running matlab: " + e);
    }
  } 
      

  /**
   * Return a summary of the analysis
   * @return a summary of the analysis.
   */
  private String independentComponentsSummary() {
    StringBuffer result = new StringBuffer();
    double cumulative = 0.0;
    Instances output = null;
    int numVectors=0;

    try {
      output = setOutputFormat();
      numVectors = (output.classIndex() < 0) 
	? output.numAttributes()
	: output.numAttributes()-1;
    } catch (Exception ex) {
    }
    // Todo: Add IC summary to result string
    result.append("\nAttribute ranking filter:\n");
    result.append(m_eval.toString());
    return result.toString();
  }

  /**
   * Returns a description of this attribute transformer
   * @return a String describing this attribute transformer
   */
  public String toString() {
    if (m_independentComponents == null) {
      return "Independent components hasn't been built yet!";
    } else {
      return "\tIndependent Components Attribute Transformer\n\n"
	+independentComponentsSummary();
    }
  }

  /**
   * Return a matrix as a String
   * @param matrix that is decribed as a string
   * @return a String describing a matrix
   */
  private String matrixToString(double [][] matrix) {
    StringBuffer result = new StringBuffer();
    int last = matrix.length - 1;

    for (int i = 0; i <= last; i++) {
      for (int j = 0; j <= last; j++) {
	result.append(Utils.doubleToString(matrix[i][j],6,2)+" ");
	if (j == last) {
	  result.append('\n');
	}
      }
    }
    return result.toString();
  }

  /**
   * Convert a ic transformed instance back to the original space
   */
  private Instance convertInstanceToOriginal(Instance inst)
    throws Exception {
    double[] newVals = null;

    if (m_hasClass) {
      newVals = new double[m_numAttribs+1];
    } else {
      newVals = new double[m_numAttribs];
    }

    if (m_hasClass) {
      // class is always appended as the last attribute
      newVals[m_numAttribs] = inst.value(inst.numAttributes() - 1);
    }

    for (int i = 0; i < m_numAttribs; i++) {
      for (int j = 0; j < m_outputNumAtts - 1; j++) {
	newVals[i] += m_mixingMatrix[i][j] * inst.value(j);
      }
    }
    
    if (inst instanceof SparseInstance) {
      return new SparseInstance(inst.weight(), newVals);
    } else {
      return new Instance(inst.weight(), newVals);
    }      
  }

  /**
   * Transform an instance in original (unormalized) format. Convert back
   * to the original space if requested.
   * @param instance an instance in the original (unormalized) format
   * @return a transformed instance
   * @exception Exception if instance cant be transformed
   */
  public Instance convertInstance(Instance instance) throws Exception {

    if (m_independentComponents == null) {
      //      throw new Exception("convertInstance: Independent components not " +"built yet");
      System.out.println("WARNING!! Independent components could not be built, returning original data");
    }

    double[] newVals = new double[m_outputNumAtts];
    Instance tempInst = (Instance)instance.copy();
    if (!instance.equalHeaders(m_trainCopy.instance(0))) {
      throw new Exception("Can't convert instance: header's don't match: MatlabICA");
    }

    m_replaceMissingFilter.input(tempInst);
    m_replaceMissingFilter.batchFinished();
    tempInst = m_replaceMissingFilter.output();

    if (m_normalize) {
      m_normalizeFilter.input(tempInst);
      m_normalizeFilter.batchFinished();
      tempInst = m_normalizeFilter.output();
    }

    if (m_attributeFilter != null) {
      m_attributeFilter.input(tempInst);
      m_attributeFilter.batchFinished();
      tempInst = m_attributeFilter.output();
    }

    if (m_hasClass) {
       newVals[m_outputNumAtts - 1] = instance.value(instance.classIndex());
    }

    for (int i=0; i<m_outputNumAtts-1; i++) {
      for (int j = 0; j < m_numAttribs; j++) {
	newVals[i] += (m_inverseMixingMatrix[i][j] *  tempInst.value(j));
      }
    }
    
    if (!m_transBackToOriginal) {
      if (instance instanceof SparseInstance) {
      return new SparseInstance(instance.weight(), newVals);
      } else {
	return new Instance(instance.weight(), newVals);
      }      
    } else {
      if (instance instanceof SparseInstance) {
	return convertInstanceToOriginal(new SparseInstance(instance.weight(), 
							    newVals));
      } else {
	return convertInstanceToOriginal(new Instance(instance.weight(),
						      newVals));
      }
    }
  }

  /**
   * Set up the header for the IC->original space dataset
   */
  private Instances setOutputFormatOriginal() throws Exception {
    FastVector attributes = new FastVector();
    
    for (int i = 0; i < m_numAttribs; i++) {
      String att = m_trainInstances.attribute(i).name();
      attributes.addElement(new Attribute(att));
    }
    
    if (m_hasClass) {
      attributes.addElement(m_trainCopy.classAttribute().copy());
    }

    Instances outputFormat = 
      new Instances(m_trainCopy.relationName()+"->IC->original space",
		    attributes, 0);
    
    // set the class to be the last attribute if necessary
    if (m_hasClass) {
      outputFormat.setClassIndex(outputFormat.numAttributes()-1);
    }

    return outputFormat;
  }

  /**
   * Set the format for the transformed data
   * @return a set of empty Instances (header only) in the new format
   * @exception Exception if the output format can't be set
   */
  private Instances setOutputFormat() throws Exception {
    if (m_independentComponents == null) {
      return null;
    }

    double cumulative = 0.0;
    FastVector attributes = new FastVector();
     for (int i=0; i<m_inverseMixingMatrix.length; i++) {
       StringBuffer attName = new StringBuffer("ICAattribute" + i);
       attributes.addElement(new Attribute(attName.toString()));
     }
        
     if (m_hasClass) {
       attributes.addElement(m_trainCopy.classAttribute().copy());
     }

     Instances outputFormat = 
       new Instances(m_trainInstances.relationName()+"_independent components",
		     attributes, 0);

     // set the class to be the last attribute if necessary
     if (m_hasClass) {
       outputFormat.setClassIndex(outputFormat.numAttributes()-1);
     }
     
     m_outputNumAtts = outputFormat.numAttributes();
     System.out.println("m_outputNumAtts: " + m_outputNumAtts);
     return outputFormat;
  }


  /** Get a timestamp string as a weak uniqueid
   * @returns a timestamp string in the form "mmddhhmmssS"
   */     
  public static String getLogTimestamp() {
    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
    String DATE_FORMAT = "MMddHHmmssS";
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
    
    sdf.setTimeZone(TimeZone.getDefault());          
    return (sdf.format(cal.getTime()));
  }
  
  /**
   * Main method for testing this class
   * @param argv should contain the command line arguments to the
   * evaluator/transformer (see AttributeSelection)
   */
  public static void main(String [] argv) {
    try {
      //      String name = "../../data/20newsgroups/different-100_fromCCS.arff";
      //String name = "/u/ml/software/weka-latest/data/iris.arff";
    	//String name = "C:/Users/Matt/Documents/Gtech/Spring 2014/CS 4641/HW3/Arff Files/eeg.arff";
    	String name = "data/eeg.arff";
      if (argv.length == 1) {
	name = argv[0];
      }
      else {
	System.err.println("No data filename given as argument, running on default file " + name);
      }
      
      Reader r = new BufferedReader(new FileReader(name));
      Instances data = new Instances(r);
      data.setClassIndex(data.numAttributes() - 1);

      MatlabICA mica = new MatlabICA();
      mica.setNumIndependentComponents(2);
      mica.buildEvaluator(data);
      mica.transformedData();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

@Override
public Instances transformedData(Instances arg0) throws Exception {
	// TODO Auto-generated method stub
	return null;
}  
}