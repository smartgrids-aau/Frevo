package brokerage;

import java.util.ArrayList;
import java.util.Calendar;

import core.AbstractRepresentation;

public class Broker {
	
	protected double reimbursement;
	protected double expenses;
	protected double income;
	protected double incomeFromFIT;
	
	protected AbstractRepresentation brain;
	protected double limitPrice;
	
	//pessimistic,optimistic,ANN_A,ANN_B,ANN_C
	//protected int[] input_no = {0, 0, 6, 5, 34, 33};
	
	//protected boolean hardcoded;
	protected BType type;
	
	public Broker(AbstractRepresentation brain, double limitPrice){
		this.brain = brain;
		this.limitPrice = limitPrice;
		//this.hardcoded = false;
		this.type = BType.pessimistic;
		
		this.reimbursement = 0.0;
		this.expenses = 0.0;
		this.income = 0.0;
		this.incomeFromFIT = 0.0;
	}
	
	public double getIncome(){
		return income;
	}
	
	public double getIncomeFromFIT(){
		return incomeFromFIT;
	}
	
	public double getSupplyCosts(){
		return expenses;
	}
	
	public double getReimbursementExpenses(){
		return reimbursement;
	}
	
	public void setHardCodedBroker(BType type){
		this.type = type;
	}
	
	public void setANNBroker(BType type){
		this.type = type;
		/*
		int input_number = input_no[type.ordinal()];
		System.out.println("XML Requested type "+type.name() +" with inputs "+input_number);
		ProblemXMLData p = (ProblemXMLData) FrevoMain.getSelectedComponent(core.ComponentType.FREVO_PROBLEM);
		System.out.println("Loaded "+p.getProperties().get("broker_type").getValue()+" in_no:"+p.getRequiredNumberOfInputs()+", out_no:"+p.getRequiredNumberOfOutputs());
		*/
	}
	
	public void reimburse(double amount, int duration, double unitPrice){
		this.reimbursement += (amount / 1000.0) * (unitPrice / 3600.0) * duration;
	}
	
	public void charge(double amount,		// amount bought in Watts 
						double unitPrice){	// price in kWh [E = P * t]
		// convert the amount in kW with amount / 1000
		// 1 h is 3600 sec so unitPrice / 3600 is the price in kwsec
		this.expenses += (amount / 1000.0) * (unitPrice / 3600.0);
	}
	
	public void addIncome(double amount, int duration, double unitPrice){
		this.income += (amount / 1000.0) * (unitPrice / 3600.0) * duration;
	}
	
	public void addIncomeFromFIT(double amount, int duration, double unitPrice){
		this.incomeFromFIT += (amount / 1000.0) * (unitPrice / 3600.0) * duration;
	}
	
	public double[] usePessimistic(Calendar simulationTime, 
			double idealSunLightIntensity, 
			double actualSunlightIntensity,
			
			double expectedRenewablePower,
			double renewablePower, 
			double[] window_renewablePower,
			
			double fit, 
			double[] window_fit,
			
			double expectedGridPower,
			double gridPower, 
			double[] window_gridPower,
			
			double get, 
			double[] window_get,
			
			double intraTime,
			double expectedDemand,
			double currentDemand){
		
		double[] result = new double[Market.offeredSLADurations.length];

		if(expectedRenewablePower + expectedGridPower > 0)	result[0] = ((expectedRenewablePower * fit) + (expectedGridPower * get)) / (expectedRenewablePower + expectedGridPower);
		else result[0] = this.limitPrice;
		
		for(int i=1; i < result.length; i++) result[i] = this.limitPrice;
		return result;
	}
	
	public double[] useOptimistic(Calendar simulationTime, 
			double idealSunLightIntensity, 
			double actualSunlightIntensity, 
			
			double expectedRenewablePower,
			double renewablePower, 
			double[] window_renewablePower,
			
			double fit, 
			double[] window_fit,
			
			double expectedGridPower,
			double gridPower, 
			double[] window_gridPower,
			
			double get, 
			double[] window_get,
			
			double intraTime,
			double expectedDemand,
			double currentDemand){

		double[] result = new double[Market.offeredSLADurations.length];
		
		if(expectedRenewablePower + expectedGridPower > 0)	result[0] = ((expectedRenewablePower * fit) + (expectedGridPower * get)) / (expectedRenewablePower + expectedGridPower);
		else result[0] = this.limitPrice;
		
		for(int i=1; i < result.length; i++) result[i] = result[0];		// assign the same price to all SLAs
		return result;
	}
	
	public double[] getOffer(Calendar simulationTime, 
								double idealSunLightIntensity, 
								double actualSunlightIntensity,
								
								double expectedRenewablePower,
								double renewablePower, 
								double[] window_renewablePower,
								
								double fit, 
								double[] window_fit,
								
								double expectedGridPower,
								double gridPower, 
								double[] window_gridPower,
								
								double get, 
								double[] window_get,
								
								double intraTime,
								double expectedDemand,
								double currentDemand){
		
		double[] result = null;
		
		switch(type){
			case pessimistic:
				result = usePessimistic(simulationTime, idealSunLightIntensity, actualSunlightIntensity, expectedRenewablePower, renewablePower, window_renewablePower, fit, window_fit, expectedGridPower, gridPower, window_gridPower, get, window_get, intraTime, expectedDemand, currentDemand);
				break;
				
			case optimistic:
				result = useOptimistic(simulationTime, idealSunLightIntensity, actualSunlightIntensity, expectedRenewablePower, renewablePower, window_renewablePower, fit, window_fit, expectedGridPower, gridPower, window_gridPower, get, window_get, intraTime, expectedDemand, currentDemand);
				break;
				
			case ANN_A:
				result = useStructureA(simulationTime, idealSunLightIntensity, actualSunlightIntensity, expectedRenewablePower, renewablePower, window_renewablePower, fit, window_fit, expectedGridPower, gridPower, window_gridPower, get, window_get, intraTime, expectedDemand, currentDemand);
				break;
				
			case ANN_B:
				result = useStructureB(simulationTime, idealSunLightIntensity, actualSunlightIntensity, expectedRenewablePower, renewablePower, window_renewablePower, fit, window_fit, expectedGridPower, gridPower, window_gridPower, get, window_get, intraTime, expectedDemand, currentDemand);
				break;
				
			case ANN_C:
				result = useStructureC(simulationTime, idealSunLightIntensity, actualSunlightIntensity, expectedRenewablePower, renewablePower, window_renewablePower, fit, window_fit, expectedGridPower, gridPower, window_gridPower, get, window_get, intraTime, expectedDemand, currentDemand);
				break;
			
			case ANN_D:
				result = useStructureD(simulationTime, idealSunLightIntensity, actualSunlightIntensity, expectedRenewablePower, renewablePower, window_renewablePower, fit, window_fit, expectedGridPower, gridPower, window_gridPower, get, window_get, intraTime, expectedDemand, currentDemand);
				break;
			/*
			case ANN_E:
				result = useStructureE(simulationTime, idealSunLightIntensity, actualSunlightIntensity, expectedRenewablePower, renewablePower, window_renewablePower, fit, window_fit, expectedGridPower, gridPower, window_gridPower, get, window_get, intraTime, expectedDemand, currentDemand);
				break;
				*/
		}
	
		return result;
	}
	
	public double[] useStructureA(Calendar simulationTime, 
			double idealSunLightIntensity, 
			double actualSunlightIntensity,
			
			double expectedRenewablePower,
			double renewablePower, 
			double[] window_renewablePower,
			
			double fit, 
			double[] window_fit,
			
			double expectedGridPower,
			double gridPower, 
			double[] window_gridPower,
			
			double get, 
			double[] window_get,
			
			double intraTime,
			double expectedDemand,
			double currentDemand){
		
		ArrayList<Float> inputValues = new ArrayList<Float>();
		ArrayList<Float> outputValues = new ArrayList<Float>();
		// -----
		inputValues.add((float) Math.sin( (Math.PI * simulationTime.get(Calendar.DAY_OF_YEAR)) / (double) simulationTime.getActualMaximum(Calendar.DAY_OF_YEAR) ));	// day of the year
		inputValues.add((float) Math.sin( (Math.PI * simulationTime.get(Calendar.HOUR_OF_DAY)) / 23.0 ));	// hour of the day	
			
		inputValues.add((float) expectedRenewablePower);														// P_re
		inputValues.add((float) (fit / this.limitPrice));														// fit
		inputValues.add((float) expectedGridPower);																// P_grid
		inputValues.add((float) (get / this.limitPrice));														// get
		// -----
		outputValues = this.brain.getOutput(inputValues);
			
		// scale the result in the range [0, limitPrice]
		double[] result = new double[outputValues.size()];
		for(int i=0; i<outputValues.size(); i++){
			result[i] =  ((double) outputValues.get(i)) * this.limitPrice;
		}
		return result;
	}
	
	public double[] useStructureB(Calendar simulationTime, 
			double idealSunLightIntensity, 
			double actualSunlightIntensity,
			
			double expectedRenewablePower,
			double renewablePower, 
			double[] window_renewablePower,
			
			double fit, 
			double[] window_fit,
			
			double expectedGridPower,
			double gridPower, 
			double[] window_gridPower,
			
			double get, 
			double[] window_get,
			
			double intraTime,
			double expectedDemand,
			double currentDemand){
		
		ArrayList<Float> inputValues = new ArrayList<Float>();
		ArrayList<Float> outputValues = new ArrayList<Float>();
		
		// -----
		//inputValues.add((float) idealSunLightIntensity);					// ideal sunlight
		inputValues.add((float) actualSunlightIntensity);					// actual sunlight
		
		inputValues.add((float) expectedRenewablePower);					// P_re
		inputValues.add((float) (fit / this.limitPrice));					// fit
		inputValues.add((float) expectedGridPower);							// P_grid
		inputValues.add((float) (get / this.limitPrice));					// get
		// -----
		outputValues = this.brain.getOutput(inputValues);
			
		// scale the result in the range [0, limitPrice]
		double[] result = new double[outputValues.size()];
		for(int i=0; i<outputValues.size(); i++){
			result[i] =  ((double) outputValues.get(i)) * this.limitPrice;
		}
		return result;
	}

	/*
	public double[] useStructureE(Calendar simulationTime, 
			double idealSunLightIntensity, 
			double actualSunlightIntensity,
			
			double expectedRenewablePower,
			double renewablePower, 
			double[] window_renewablePower,
			
			double fit, 
			double[] window_fit,
			
			double expectedGridPower,
			double gridPower, 
			double[] window_gridPower,
			
			double get, 
			double[] window_get,
			
			double intraTime,
			double expectedDemand,
			double currentDemand){
		
		ArrayList<Float> inputValues = new ArrayList<Float>();
		ArrayList<Float> outputValues = new ArrayList<Float>();
		
		// -----
		inputValues.add((float) idealSunLightIntensity);					// ideal sunlight
		//inputValues.add((float) actualSunlightIntensity);					// actual sunlight
		
		inputValues.add((float) expectedRenewablePower);					// P_re
		inputValues.add((float) (fit / this.limitPrice));					// fit
		inputValues.add((float) expectedGridPower);							// P_grid
		inputValues.add((float) (get / this.limitPrice));					// get
		// -----
		outputValues = this.brain.getOutput(inputValues);
			
		// scale the result in the range [0, limitPrice]
		double[] result = new double[outputValues.size()];
		for(int i=0; i<outputValues.size(); i++){
			result[i] =  ((double) outputValues.get(i)) * this.limitPrice;
		}
		return result;
	}*/
	
	public double[] useStructureC(Calendar simulationTime, 
			double idealSunLightIntensity, 
			double actualSunlightIntensity,
			
			double expectedRenewablePower,
			double renewablePower, 
			double[] window_renewablePower,
			
			double fit, 
			double[] window_fit,
			
			double expectedGridPower,
			double gridPower, 
			double[] window_gridPower,
			
			double get, 
			double[] window_get,
			
			double intraTime,
			double expectedDemand,
			double currentDemand){
		
		ArrayList<Float> inputValues = new ArrayList<Float>();
		ArrayList<Float> outputValues = new ArrayList<Float>();
		// -----
		inputValues.add((float) Math.sin( (Math.PI * simulationTime.get(Calendar.DAY_OF_YEAR)) / (double) simulationTime.getActualMaximum(Calendar.DAY_OF_YEAR) ));	// day of the year
		inputValues.add((float) Math.sin( (Math.PI * simulationTime.get(Calendar.HOUR_OF_DAY)) / 23.0 ));	// hour of the day	
			
		inputValues.add((float) expectedRenewablePower);														// P_re
		for(int i=0; i < window_renewablePower.length; i++) inputValues.add((float) window_renewablePower[i]);	// [P_re]
		inputValues.add((float) (fit / this.limitPrice));														// fit
		for(int i=0; i < window_fit.length; i++) inputValues.add((float) (window_fit[i] / this.limitPrice));	// [fit]
		inputValues.add((float) expectedGridPower);																// P_grid
		for(int i=0; i < window_gridPower.length; i++) inputValues.add((float) window_gridPower[i]);			// [P_grid]
		inputValues.add((float) (get / this.limitPrice));														// get
		for(int i=0; i < window_get.length; i++) inputValues.add((float) (window_get[i] / this.limitPrice));	// [get]
		// -----
		outputValues = this.brain.getOutput(inputValues);
			
		// scale the result in the range [0, limitPrice]
		double[] result = new double[outputValues.size()];
		for(int i=0; i<outputValues.size(); i++){
			result[i] =  ((double) outputValues.get(i)) * this.limitPrice;
		}
		return result;
	}
	
	public double[] useStructureD(Calendar simulationTime, 
			double idealSunLightIntensity, 
			double actualSunlightIntensity,
			
			double expectedRenewablePower,
			double renewablePower, 
			double[] window_renewablePower,
			
			double fit, 
			double[] window_fit,
			
			double expectedGridPower,
			double gridPower, 
			double[] window_gridPower,
			
			double get, 
			double[] window_get,
			
			double intraTime,
			double expectedDemand,
			double currentDemand){
		
		ArrayList<Float> inputValues = new ArrayList<Float>();
		ArrayList<Float> outputValues = new ArrayList<Float>();
		
		// -----
		//inputValues.add((float) idealSunLightIntensity);					// ideal sunlight
		inputValues.add((float) actualSunlightIntensity);					// actual sunlight
			
		inputValues.add((float) expectedRenewablePower);														// P_re
		for(int i=0; i < window_renewablePower.length; i++) inputValues.add((float) window_renewablePower[i]);	// [P_re]
		inputValues.add((float) (fit / this.limitPrice));														// fit
		for(int i=0; i < window_fit.length; i++) inputValues.add((float) (window_fit[i] / this.limitPrice));	// [fit]
		inputValues.add((float) expectedGridPower);																// P_grid
		for(int i=0; i < window_gridPower.length; i++) inputValues.add((float) window_gridPower[i]);			// [P_grid]
		inputValues.add((float) (get / this.limitPrice));														// get
		for(int i=0; i < window_get.length; i++) inputValues.add((float) (window_get[i] / this.limitPrice));	// [get]
		// -----
		outputValues = this.brain.getOutput(inputValues);
			
		// scale the result in the range [0, limitPrice]
		double[] result = new double[outputValues.size()];
		for(int i=0; i<outputValues.size(); i++){
			result[i] =  ((double) outputValues.get(i)) * this.limitPrice;
		}
		return result;
	}
	
	
}
