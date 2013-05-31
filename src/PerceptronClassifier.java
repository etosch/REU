import Jama.Matrix;
import java.util.Vector;
import java.lang.reflect.*;
import java.io.*;

public class PerceptronClassifier {
    
    public static double alpha = 0.01;

    public static double[] initializeWeights(int size){
	// fill this out and return an initialized weight vector
	return new double[size];
    }

    public static void iterate(double[] weights, Training dataObj){
	// this needs to update the weights for one iteration
	// you will need some way of tracking where you are right now in the 
	// training data. You can keep a pointer to the current record somewhere
	// or decide to keep the training instances in a stack, rather than a Matrix
	return;
    }

    public static boolean stoppingCondition(double[] weights, Training dataObj){
	// stop if the error is sufficiently small, if we've run out of training data, 
	// or if we have been running for too long.
	return true;
    }

    public static void classify(double[] weights, Training dataObj){
	//use the weights to classify the test data and print out the errors
	return;
    }

    public static void main(String[] args) {
	// When supplied with a class name and a data file, we will train and test
	try{
	    if (args.length < 2)
		throw new Exception("Insufficient number of arguments");
	    String className = args[0]; //without .class at the end
	    String dataFile = args[1];
	    if (args.length > 2){
		String alpha = args[2];
		PerceptronClassifier.alpha = Double.parseDouble(alpha);
	    }
	    Training dataObj  = (Training) Class.forName(className).getConstructor(String.class).newInstance(dataFile);
	    dataObj.setParameters();
	    double[] weights = PerceptronClassifier.initializeWeights(dataObj.getNumFeatures());
	    do{
		PerceptronClassifier.iterate(weights, dataObj);
	    } while (! PerceptronClassifier.stoppingCondition(weights, dataObj));
	    PerceptronClassifier.classify(weights, dataObj);
	}catch(Exception e){
	    e.printStackTrace();
	}
    }
}

abstract class Training<D, C> {
    // Training is an abstract superclass that supplies functionality for all 
    // inputs. The generic type D represents the type of the data. All operations on 
    // training data that are parameterized by type occur within this class; the 
    // Perceptron does not need to know anything about this parameter    
    // You will need to decide where/how to implement feature functions.

    // Documentation for the Java Matrix library can be found at http://math.nist.gov/javanumerics/jama/doc/

    public Vector<Vector<D>> rawData;
    public Matrix trainingData;
    public Matrix testingData;
    public C[] trainingLabels;
    public C[] testingLabels;

    // this should set rawData. You can format the raw data file any way you want, but I would
    // suggest representing it as a csv with either the first or last entry set as the label.
    // You will probably want to add 
    abstract Vector<Vector<D>> loadData(String filename) throws Exception;

    // this should set the trainingData, testingData, training Labels, and testing Labels
    // You will need to somehow partition your rawData vector so that some number are put into 
    // the trainingData vector and some are put into the testingData vector. 
    // This function should map the rawData values to doubles (since that's the type that the 
    // Matrix takes. 
    abstract void setParameters();

    public int getTrainingSize(){
	//returns the number of training instances
	return trainingData.getArray().length;
    }
    public int getNumFeatures(){
	// returns the size of the feature vector
	return trainingData.getArray()[0].length;
    }
    public int testingSize(){
	//returns the number of testing instances
	return testingData.getArray().length;
    }

}


/** PUT THESE IN A NEW FILE LATER **/

class TypeNotFoundException extends Exception {
    
    public TypeNotFoundException(String message){
	super(message);
    }

}

enum BoolClasses { TRUE, FALSE, MAYBE }

class Bools extends Training<Boolean, BoolClasses>{

    public Bools(String filename) throws Exception {
	this.rawData = loadData(filename);
    }
 
    public Vector<Vector<Boolean>> loadData(String filename) throws Exception{
	BufferedReader br = new BufferedReader(new FileReader(filename));
	Vector<Vector<Boolean>> data = new Vector<Vector<Boolean>>();
	String line = "";
	while ((line=br.readLine()) != null){
	    Vector<Boolean> featureVector = new Vector<Boolean>();
	    for (String val : line.split(",\\s*")){
		if (val.equals("0"))
		    featureVector.add(false);
		else if (val.equals("1"))
		    featureVector.add(true);
		else throw new TypeNotFoundException("Unexpected bit type" + val);
	    }
	    data.add(featureVector);
	}
	return data;
    }

    public void setParameters(){
	// set the first 8 to be training and the last 2 to be testing
	this.trainingLabels = new BoolClasses[8]; 
	this.testingLabels = new BoolClasses[2];
	int featureVectorSize = this.rawData.get(0).size();
	double[][] trainingArray = new double[8][featureVectorSize-1]; 
	double[][] testingArray = new double[2][featureVectorSize-1];
	for (int i = 0 ; i < this.rawData.size() ; i++){
	    Vector<Boolean> featVec = this.rawData.get(i);
	    if (i < 8){
		trainingLabels[i] = featVec.get(0) ? BoolClasses.FALSE : BoolClasses.TRUE;
		for (int j = 1 ; j < featureVectorSize ; j++)
		    trainingArray[i][j-1] = featVec.get(j) ? 1.0 : 0.0;
	    } else {
		testingLabels[i-8] = featVec.get(0) ? BoolClasses.FALSE : BoolClasses.TRUE;
		for (int j = 1 ; j < featureVectorSize ; j++)
		    testingArray[i-8][j-1] = featVec.get(j) ? 1.0 : 0.0;
	    }
	}
	this.trainingData = new Matrix(trainingArray);
	this.testingData = new Matrix(testingArray);
    }

}
