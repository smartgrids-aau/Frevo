package nilm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.json.simple.parser.ParseException;

import core.AbstractSingleProblem;
import core.AbstractRepresentation;

/**
 * This class tries to address the load-disaggregation problem using evolutionary algorithms and ANNs
 * In particular, we are interested in detecting use of the following devices:
 * fridge/freezer, dishwasher, washing machine, dryer, stove/oven, lighting, baseline-load, other
 * 
 * For each of the device group we desire to provide as output the average power for the time window of observation
 * 
 * @author andreamonacchi
 *
 */

public class Nilm extends AbstractSingleProblem {

	private double limitPower;		// max power handlable by the system
	public ArrayList<String> datasetFiles;
	private boolean useNoise;
	private boolean[] useDevices;
	private double[][] previousPowerValues;
	private double[] previousAggregatedPowerValues;
	private int windowSize;
	
	private int maxDaysToRun;
	
	// for frevo
	public void initSimulation(){
		this.loadDataset(getProperties().get("dataset_folder").getValue());
		this.maxDaysToRun = Integer.parseInt(getProperties().get("number_of_days").getValue());
		this.windowSize = Integer.parseInt(getProperties().get("window_size").getValue());
		this.useNoise = Boolean.parseBoolean(getProperties().get("use_noise").getValue());
		this.limitPower = Double.parseDouble(getProperties().get("limit_power").getValue());
		
		this.useDevices = new boolean[9]; Arrays.fill(useDevices, true);
		
		this.previousPowerValues = new double[useDevices.length][windowSize];
		this.previousAggregatedPowerValues = new double[windowSize];
	}
	
	// for the manual initialization
	public void initSimulation(boolean useNoise, boolean[] useDevices, int windowSize){
		this.windowSize = windowSize;
		this.useNoise = useNoise;
		this.useDevices = useDevices;
		this.previousPowerValues = new double[useDevices.length][windowSize];
		this.previousAggregatedPowerValues = new double[windowSize];
	}
	
	public void loadDataset(String folder){
		datasetFiles = new ArrayList<String>();
		listFilesForFolder(folder);
		Collections.sort(datasetFiles);
		//System.out.println(datasetFiles.size()+" files found in the folder");
	}
	
	public void loadDayDataset(String folder){
		datasetFiles = new ArrayList<String>();
		Collections.sort(datasetFiles);
	}
	
	private void listFilesForFolder(String folder){
		for (File entry : new File(folder).listFiles()) {
	        if (entry.isDirectory()) {
	            listFilesForFolder(entry.getAbsolutePath());
	        } else {
	        	if(entry.getName().endsWith(".csv"))
	        		datasetFiles.add(entry.getAbsolutePath());
	        }
	    }
	}
	
	public double runDay(AbstractRepresentation candidate, String filepath){
		String line;
		String[] second;
		double aggregatedPower;
		ArrayList<Float> inputValues = new ArrayList<Float>();
		ArrayList<Float> outputValues = new ArrayList<Float>();
		
		double dayRMSE = 0;
		int lines = 0;		// number of rows in the file (num of secs)
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			//int xx = 0;
			while ((line = br.readLine()) != null){ // && xx<10) {
				second = line.split(",");
				aggregatedPower = 0;
				
				//xx++;
				
				String devs ="";
				Double currentValue = 0.0;
				// get aggregated power and record window of previous elements
				for(int i=0; i<useDevices.length; i++){
					if(useNoise | useDevices[i]){
						// make sure that missing values are threated correctly
						// take previous value as a policy
						try{
							currentValue = Double.parseDouble(second[i+1]);						
						}catch(NumberFormatException e){
							currentValue = previousPowerValues[i][0];	// get previous value (if 0 is gonna be 0 though)
						}
						aggregatedPower += currentValue;
					}
					
					// shift elements to the left (biggest is oldest, smallest is newest)
					for(int t=this.windowSize-1; t>0; t--){
						previousPowerValues[i][t] = previousPowerValues[i][t-1];
					}
					previousPowerValues[i][0] = currentValue;
					
					devs += " "+currentValue;
				}
				
				for(int t=this.windowSize-1; t>0; t--){
					previousAggregatedPowerValues[t] = previousAggregatedPowerValues[t-1];
				}
				previousAggregatedPowerValues[0] = aggregatedPower;
				
				/*
				System.out.println(aggregatedPower + " = "+ devs);
				for(int i=0; i< this.windowSize; i++){
					System.out.print("t_"+i+":"+previousAggregatedPowerValues[i]+", ");
				}
				System.out.println("\n--");
				*/
				/*
				for(int d=0; d<this.useDevices.length; d++){
					for(int i=0; i< this.windowSize; i++){
						System.out.print("t_"+d+"_"+i+":"+previousPowerValues[d][i]+", ");
					}
					System.out.println("");
				}	
				System.out.println("\n-------");
				*/
				
				inputValues.clear();
				for(int i=0; i<this.windowSize; i++) 
					inputValues.add((float) (previousAggregatedPowerValues[i] / (double) this.limitPower));
				
				outputValues = candidate.getOutput(inputValues);
				
				// scale the result in the range [0, limitPower]
				double[] result = new double[outputValues.size()];
				for(int i=0; i<outputValues.size(); i++){
					//result[i] = (double) outputValues.get(i) * this.limitPower;	// scale up values
					result[i] = (double) outputValues.get(i);	// scale up values
				}
					
				
				double[] expectedResult = new double[outputValues.size()]; 
				//expectedResult[0] = computeAverage(previousAggregatedPowerValues);	
				//expectedResult[0] = computeAverageOnPowerData(previousAggregatedPowerValues);	// average of all former aggregated values
				for(int i=0; i<outputValues.size(); i++){
					expectedResult[i] = computeAverageOnPowerData(previousPowerValues[i]);
				}
				
				dayRMSE += compute_rmse(result, expectedResult);
				lines++;
			}
			// computer average daily RMSE
			//dayRMSE = dayRMSE > 0 ? dayRMSE / (double) lines : dayRMSE;
			//System.out.println(dayRMSE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dayRMSE;
	}
	
	private double computeAverageOnPowerData(double[] powerValues){
		/*
		double[] scaledValues = new double[powerValues.length];
		for(int i=0; i<powerValues.length; i++){
			scaledValues[i] = powerValues[i] / (double) this.limitPower;	// normalize between 0 and 1
		}
		return computeAverage(scaledValues);
		*/
		return computeAverage(powerValues) / (double) this.limitPower;
	}
	
	private double computeAverage(double[] normalizedValues){
		double result = 0;
		for(Double d : normalizedValues) result += d;
		return result / (double) normalizedValues.length;
	}
	
	private double compute_rmse(double[] result, double[] expectedResult){
		double rmse = 0.0;
		
		for(int i=0; i<result.length; i++){
			// compute the error (for regression models)
			rmse += Math.sqrt((result[i] - expectedResult[i]) * (result[i] - expectedResult[i]));
			//System.out.println("Expected "+expectedResult[i]+", computed: "+result[i]);
		}
		rmse /= (double) result.length;		// compute mse = AVG(err**2)
		
		return rmse;
	}
	
	@Override
	/** Evaluates the given representation by calculating its corresponding fitness value. A higher fitness means better performance.<br>
	 * @param candidate The candidate solution to be evaluated.
	 * @return the corresponding fitness value. */
	public double evaluateCandidate(AbstractRepresentation candidate) {
		initSimulation();
		
		double result = 0;
		int performedDays = 0;
		
		// run days from the dataset
		for(int d=0; 
				d < this.maxDaysToRun 
				//&& (result == 0 | (result / (double) performedDays) < 0.5)
				; d++){
			// run the day
			result += this.runDay(candidate, this.datasetFiles.get(d));
			performedDays++;
		}
		/*
		System.out.println( (this.limitPower - (result > 0.0 ? result / (double) performedDays : result)) + ": "+
							this.limitPower + " - " + (result > 0.0 ? result / (double) performedDays : result) );
		*/
		return - (result / (double) performedDays);
	}
	
	@Override
	public void replayWithVisualization(AbstractRepresentation candidate){
		
		ArrayList<Float> testArray = new ArrayList<Float>();
		double avg = 0;
		System.out.print("[");
		for(int t=0; t<120; t++){
			testArray.add( (float) Math.random() );
			avg += testArray.get(t);
			System.out.print(testArray.get(t));
			if(t<119) System.out.println(", ");
		}
		System.out.println("]");
		System.out.println("Expected: "+(avg /= (double) 120));
		System.out.println("Computed: "+candidate.getOutput(testArray));
		
	}
	
	/** Returns the achievable maximum fitness of this problem. A representations with this fitness value cannot be improved any further. */
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

}
