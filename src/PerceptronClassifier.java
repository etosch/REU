import Jama.Matrix;
import java.util.Vector;
import java.lang.reflect.*;
import java.io.*;

public class PerceptronClassifier {
    
    public static double alpha = 0.01;
    public static double threshold = 0.5;
    
    

    public static double[] initializeWeights(int size){
	double[] weights = new double[size];
	
	for(int x=0; x<size; x++){
		weights[x]= Math.random()/5; //initialize weights to small random value
	}
	return weights;
    }

    public static void iterate(double[] weights, Training dataObj){
	// this needs to update the weights for one iteration
	// you will need some way of tracking where you are right now in the 
	// training data. You can keep a pointer to the current record somewhere
	// or decide to keep the training instances in a stack, rather than a Matrix
        int sum=0;
	
	for(int x =0; x<weights.length; x++){
		sum+=weights[x]*dataObj.trainingData.get(dataObj.curTrainingData, x);
		
	}
	
	Binary expected = dataObj.trainingLabels[dataObj.curTrainingData];
	//if classification doesn't match expected output, adjust weights and threshold
	switch(expected){
            case CLASS1:
                if(sum>=threshold){
                    threshold = threshold - alpha*(expected.ordinal() - sum);
                    for(int x=0; x<weights.length; x++){
                            weights[x] = weights[x] + alpha*(expected.ordinal() - sum)*dataObj.trainingData.get(dataObj.curTrainingData, x);
                    }
                }
            case CLASS2:  
                if(sum<threshold){
                    threshold = threshold - alpha*(expected.ordinal() - sum);
                    for(int x=0; x<weights.length; x++){
                            weights[x] = weights[x] + alpha*(expected.ordinal() - sum)*dataObj.trainingData.get(dataObj.curTrainingData, x);
                    }
                }
        }
	
	
	dataObj.curTrainingData++;
	return;
    }

    public static boolean stoppingCondition(double[] weights, Training dataObj){ //unfinished
	// stop if the error is sufficiently small, if we've run out of training data, 
	// or if we have been running for too long.
	if(dataObj.curTrainingData>dataObj.trainingData.length){
		return true;
	}else{
            return false;
        }
    }

    public static void classify(double[] weights, Training dataObj){
	//use the weights to classify the test data and print out the errors
	double sum = 0;
        double actualClass1 = 0; 
        double actualClass2 = 0; 
        double classified1 = 0;
        double classified2 = 0;
        
	for(int y=0; y<dataObj.testingSize(); y++){
            for(int x =0; x<weights.length; x++){
                    Binary expected = dataObj.testingLabels[y];
                    sum+=weights[x]*dataObj.testingData.get(y, x);
                    System.out.print("Classified as: ");
                    switch(expected){
                        case CLASS1:
                            if(sum<threshold){
                                System.out.println("Correctly classified as Class 1");
                                classified1++;
                            }else{
                                System.out.println("Incorrectly classified as Class 2");
                            }
                            actualClass1++;
                        case CLASS2:
                            if(sum<threshold){
                                System.out.println("Inorrectly classified as Class 1");
                            }else{
                                System.out.println("Correctly classified as Class 2");
                                classified2++;
                            }
                            actualClass2++;
                    }
                    
                    System.out.println("Percentage of Class 1 correctly classified: "+classified1/actualClass1);
                    System.out.println("Percentate of Class 2 correctly classified: "+classified2/actualClass2);
                    

            }
        }
	
	
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

abstract class Training<D> {
    // Training is an abstract superclass that supplies functionality for all 
    // inputs. The generic type D represents the type of the data. All operations on 
    // training data that are parameterized by type occur within this class; the 
    // Perceptron does not need to know anything about this parameter    
    // You will need to decide where/how to implement feature functions.

    // Documentation for the Java Matrix library can be found at http://math.nist.gov/javanumerics/jama/doc/
    
    public int curTrainingData; //index of which training feature vector you're on
    
    public Vector<Vector<D>> rawData;
    public Matrix trainingData;
    public Matrix testingData;
    public Binary[] trainingLabels;
    public Binary[] testingLabels;

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

enum Binary { CLASS1, CLASS2 }

class Bools extends Training<Boolean>{

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
        final int TrainingIterations = 8;
	this.trainingLabels = new Binary[TrainingIterations]; 
	this.testingLabels = new Binary[2];
	int featureVectorSize = this.rawData.get(0).size();
	double[][] trainingArray = new double[8][featureVectorSize-1]; 
	double[][] testingArray = new double[2][featureVectorSize-1];
	for (int i = 0 ; i < this.rawData.size() ; i++){
	    Vector<Boolean> featVec = this.rawData.get(i);
	    if (i < 8){
		trainingLabels[i] = featVec.get(0) ? Binary.CLASS1 : Binary.CLASS2;
		for (int j = 1 ; j < featureVectorSize ; j++)
		    trainingArray[i][j-1] = featVec.get(j) ? 1.0 : 0.0;
	    } else {
		testingLabels[i-8] = featVec.get(0) ? Binary.CLASS1 : Binary.CLASS2;
		for (int j = 1 ; j < featureVectorSize ; j++)
		    testingArray[i-8][j-1] = featVec.get(j) ? 1.0 : 0.0;
	    }
	}
	this.trainingData = new Matrix(trainingArray);
	this.testingData = new Matrix(testingArray);
    }

}
