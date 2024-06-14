package brokerage;

import graphics.FrevoWindow;
import hems.Debugger;
import hems.devices.generators.GenerationModel;
import hems.devices.generators.Generator;
import hems.devices.generators.capabilityModel.timebased.TimeBasedGenerator;
import hems.devices.generators.capabilityModel.timebased.TimeBasedTradeableAmount;
import hems.devices.generators.photovoltaics.AzimuthZenithAngle;
import hems.devices.generators.photovoltaics.MeasuredPV;
import hems.devices.generators.photovoltaics.ModeledPV;
import hems.devices.generators.photovoltaics.PSA;
import hems.devices.generators.photovoltaics.SolarCalculations;
import hems.devices.generators.weather.IntervalBasedWeather;
import hems.devices.generators.weather.TimeBasedCloudFactor;
import hems.devices.generators.weather.TimeserieWeather;
import hems.devices.generators.weather.Weather;
import hems.devices.loads.DeviceStateModel;
import hems.devices.loads.OperationStats;
import hems.devices.loads.StateOperation;
import hems.devices.loads.UsageModel;
import hems.devices.mainGrid.capabilityModel.CapabilityModel;
import hems.devices.mainGrid.capabilityModel.IntervalBasedCapability;
import hems.devices.modelManager.RemoteManagerUanavailableException;
import hems.display.report.LoggerTimeserie;
import hems.market.priceModel.PriceModel;
import hems.market.priceModel.StaticPriceModel;
import hems.market.priceModel.tariffbased.Tariff;
import hems.market.priceModel.tariffbased.TariffPriceModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.FrevoMain;

import org.dom4j.Document;
import org.json.simple.parser.JSONParser;

import utils.NESRandom;
import brokerage.devices.Load;
import core.AbstractMethod;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ComponentType;
import core.ComponentXMLData;
import core.ProblemXMLData;
import core.RepresentationComparator;

public class Market extends AbstractSingleProblem{
	
	protected boolean reportingMode;
	
	public final static int[] offeredSLADurations = {0, 10, 30, 60, 120, 600, 1800}; // offered SLAs in seconds
	public final static int[] offeredSLARuntimes = {1, 11, 31, 61, 121, 601, 1801};
	
	//protected double[] utilities = {0.2, 0.18, 0.25, 0.15, 0.3, 0.3};
	protected double[] utilities = {0.9, 0.9, 0.9, 0.9, 0.9, 0.9};
	
	protected Broker broker;
	
	protected JSONParser jsonParser;
	protected SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

	protected Calendar simulationTime;			// start time
	protected Calendar simulationBeginning;			// start time
	protected int simulationDuration;				// simulation lenght
	protected int allocationTimeslotLenght;			// allocation length

	protected double limitPrice;
	public Random randomGen;
	
	protected double latitude;
	protected double longitude;
	protected double height;
	
	// information on the grid connection
	protected Generator gridProvisioning;		// generation model
	protected PriceModel get;					// grid energy tariff
	protected CapabilityModel gridConstraints;	// max power boughtable by the grid
	protected PriceModel fit;					// feed-in tariff
	
	// information on the available local generation
	protected ArrayList<GenerationModel> reGenerators;	// GenerationModel = (reservation-price, Generator)
	
	protected ArrayList<Load> loads;
	protected Map<String,Integer> initialOrder;
	protected int power_columns;
	
	protected Weather weather;
	protected LoggerTimeserie logger;
	
	protected HashMap<Load, SLA> serviceLevelAgreements;
	
	protected double gridProduction;
	protected double expectedGridProduction;
	
	protected double localProduction;
	protected double expectedLocalProduction;
	
	protected double actualCurrentDemand;
	protected double currentDemand;
	protected double expectedDemand;
	
	protected double[] window_gridProduction;
	protected double[] window_localProduction;
	
	protected double[] window_fit;
	protected double[] window_get;
	
	BufferedReader bufferRead;
	
	protected double reimbursement_penalty;
	
	protected int[] numberOfSoldSLAs;
	
	protected void initSimulation(int generation) throws Exception{
		try{
			this.simulationTime = Calendar.getInstance();
			this.simulationBeginning = Calendar.getInstance(); 
			this.allocationTimeslotLenght = Integer.parseInt(getProperties().get("allocation_length").getValue());
			
			this.randomGen = getRandom();
			this.limitPrice = Double.parseDouble(getProperties().get("limit_price").getValue());
			
			this.timesCouldBuyPower = 0;
			this.timesCouldNotBuyPower = 0;
			this.numberOfSoldSLAs = new int[offeredSLADurations.length];
		
			this.simulationDuration = Integer.parseInt(getProperties().get("simulation_duration").getValue());
			
			// set penalties
			this.reimbursement_penalty = Double.parseDouble(getProperties().get("reimbursement_penalty").getValue());
			
			if(Boolean.parseBoolean(getProperties().get("incremental_evolution").getValue())){
				incrementalEvolution(generation);
			}else{
				this.simulationTime.setTime(format.parse(getProperties().get("market_start_time").getValue()));
				this.simulationBeginning.setTime(this.simulationTime.getTime());
				
				this.setupScenario(	Integer.parseInt(getProperties().get("grid_type").getValue()), 
									Integer.parseInt(getProperties().get("weather_type").getValue()));
			}
			
			if(this.reportingMode) this.setupLogger();
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
	
	protected void incrementalEvolution(int generation) throws Exception{
		
		/*
		if(generation < 250)	this.simulationTime.setTime( format.parse("2014 12 31 23:50:00") );
		else 					this.simulationTime.setTime( format.parse("2015 6 21 23:50:00") );
			
		this.simulationBeginning.setTime(this.simulationTime.getTime());
		
		
		// **** Winter scenario
		if(generation < 25) this.setupScenario(0, 0);
		else if(generation < 50) this.setupScenario(0, 1);
				
		else if(generation < 75) this.setupScenario(1, 0);
		else if(generation < 100) this.setupScenario(1, 1);
				
		else if(generation < 125) this.setupScenario(2, 0);
		else if(generation < 150) this.setupScenario(2, 1);
				
		else if(generation < 175) this.setupScenario(3, 0);
		else if(generation < 200) this.setupScenario(3, 1);
				
		else if(generation < 225) this.setupScenario(4, 0);
		else if(generation < 250) this.setupScenario(4, 1);
			
		// **** Summer scenario
		else if(generation < 275) this.setupScenario(0, 0);
		else if(generation < 300) this.setupScenario(0, 1);
				
		else if(generation < 325) this.setupScenario(1, 0);
		else if(generation < 350) this.setupScenario(1, 1);
				
		else if(generation < 375) this.setupScenario(2, 0);
		else if(generation < 400) this.setupScenario(2, 1);
				
		else if(generation < 425) this.setupScenario(3, 0);
		else if(generation < 450) this.setupScenario(3, 1);
				
		else if(generation < 475) this.setupScenario(4, 0);
		else if(generation < 500) this.setupScenario(4, 1);
		*/
		
		int gridType = 0;		// 0: 0W, 		1: 1.5kW,	2. 1-3kW,	3: 3kW,	4: 6kW
		int weatherType = 0;	// 0: ideal,	1: actual
		
		if(generation < 250){
			// ----- Simpler task (ideal weather, summer then winter) for first 250 generations -----
			if(generation < 125)	this.simulationTime.setTime( format.parse("2015 6 21 23:50:00") );	// summer
			else					this.simulationTime.setTime( format.parse("2014 12 31 23:50:00") );	// winter
			
			// set P_grid type (250/5 = 50)
			if(generation < 50) gridType = 4;
			else if(generation < 100) gridType = 3;
			else if(generation < 150) gridType = 2;
			else if(generation < 200) gridType = 1;
			else gridType = 0;
			
		}else{ // >=250
			// ------ Harder task (real weather) for later generations -----
			weatherType = 1;	// real weather
			
			if(generation < 375)	this.simulationTime.setTime( format.parse("2015 6 21 23:50:00") );	// summer
			else					this.simulationTime.setTime( format.parse("2014 12 31 23:50:00") );	// winter
			
			// set P_grid type (250/5 = 50)
			if(generation < 300) gridType = 4;
			else if(generation < 350) gridType = 3;
			else if(generation < 400) gridType = 2;
			else if(generation < 450) gridType = 1;
			else gridType = 0;
		}
		
		this.simulationBeginning.setTime(this.simulationTime.getTime());
		this.setupScenario(gridType, weatherType);
	}
	
	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		try {
			this.reportingMode = false;		// disable logging
			initSimulation( Integer.parseInt( candidate.getPropertyValue("generation")) );
			this.broker = new Broker( candidate, this.limitPrice);
			this.broker.setANNBroker( BType.valueOf( getProperties().get("broker_type").getValue() ) );
			
			this.simulate(1); 	// in the simplest case we only have 1 round (no auction involved)
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return computeFitness();
	}
	
	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select a destination folder for the simulation report");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setSelectedFile(new File(System.getProperty("user.home")));
		
		try {
			int returnVal = chooser.showSaveDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				String folder = chooser.getSelectedFile().getPath();

				this.reportingMode = true;	// enable logging
				initSimulation( -1 );
				this.broker = new Broker(candidate, this.limitPrice);
				this.broker.setANNBroker( BType.valueOf( getProperties().get("broker_type").getValue() ) );
				
				this.simulate(1); 	// in the simplest case we only have 1 round (no auction involved)
							
				this.logger.saveToCSV(folder+"broker_power.csv", 		0);
				this.logger.saveToCSV(folder+"broker_price_loads.csv",	1);
				this.logger.saveToCSV(folder+"broker_willingness.csv",	2);		
				this.logger.saveMVToCSV(folder+"broker_price.csv",		0);
				
				double[] availability = getAvgAvailability();
				System.out.println(	"PAR:"+getPAR()								+"\n"+
									"A:"+availability[0]+", U:"+availability[1]	+"\n"+
									"Reactivity: "+getSystemReactivity()+" ("+this.timesCouldBuyPower+", "+this.timesCouldNotBuyPower+")"	+"\n"+
									"Fitness:"+computeFitness() 				+"\n"+
									"Profit:"+computeProfit() 					+"\n"+
									"Income:"+broker.getIncome() 				+"\n"+
									"FIT_income:"+broker.getIncomeFromFIT() 	+"\n"+
									"Supply costs:"+broker.getIncomeFromFIT() 	+"\n"+
									"Reimbursement costs:"+broker.getReimbursementExpenses());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void replayWithoutVisualization(File resultFile, AbstractRepresentation candidate) {
		// ************ Compare multiple result files **********
		FileWriter writer = null;
		try {
			writer = new FileWriter(System.getProperty("user.home")+"/"+"reran_"+getProperties().get("broker_type").getValue()+".csv");
			
			String soldSLAs = "";
			for(int d=0; d<Market.offeredSLADurations.length; d++){
				soldSLAs += Market.offeredSLADurations[d];
				if(d < Market.offeredSLADurations.length-1) soldSLAs += ",";
			}
			writer.append("File,CustomName,brokerType,gridType,weatherType,beginning,duration,seed,PAR,Max,Avg,Availability,Unavailability,Reactivity,CBP,CNBP,fitness,profit,income,fit_income,supply_cost,reimbursement,"+soldSLAs+"\n");		
			
			
			// setup the scenario
			int gridType = Integer.parseInt(getProperties().get("grid_type").getValue());
			int weatherType = Integer.parseInt(getProperties().get("weather_type").getValue());
			
			String simulationBeginning = getProperties().get("market_start_time").getValue();
			int simulationDuration = Integer.parseInt(getProperties().get("simulation_duration").getValue());
			int allocationTimeslotLength = Integer.parseInt(getProperties().get("allocation_length").getValue());
			double limitPrice = Double.parseDouble(getProperties().get("limit_price").getValue());
		
					
			for(int seed=12345; 
					seed <= 12345; 	//seed < 12445; // 100 evaluations for each learned broker 
					seed++)
			{
			// one can either only rerun the same seed used for learning or else simulate multiple initial seeds
				//int seed = Integer.parseInt(this.getProperties().get("StartingSeed").getValue()); // impossible to access this configentry	
				
				this.replayCandidate(candidate, 
						resultFile,
						writer,
						seed,
						simulationBeginning,
						simulationDuration,
						allocationTimeslotLength,
						limitPrice,
						
						gridType, 
						weatherType);
				
				
			}
			
			writer.flush();
			writer.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	


	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}
	
	// ------------------------------------------- Simulation loop -------------------------------------------
	protected void simulate(int rounds) throws RemoteManagerUanavailableException{
		serviceLevelAgreements = new HashMap<Load, SLA>(); 		// active SLAs for each load
		ArrayList<Double> values = null; 		// P_re, P_grid, Load_1, ..., Load_n
		
		// setup a window to improve the price forecasting
		Calendar[] timeWindow = new Calendar[offeredSLADurations.length];
		
		window_gridProduction = new double[offeredSLADurations.length];
		window_localProduction = new double[offeredSLADurations.length];
		window_fit = new double[offeredSLADurations.length];
		window_get = new double[offeredSLADurations.length];
		
		bufferRead = new BufferedReader(new InputStreamReader(System.in));
		
		while( this.simulationTime.getTimeInMillis()/1000 - this.simulationBeginning.getTimeInMillis()/1000 <= this.simulationDuration ){
			
			// update the window
			for(int i = 0; i<timeWindow.length; i++){
				timeWindow[i] = Calendar.getInstance();
				timeWindow[i].setTime(this.simulationTime.getTime());
				timeWindow[i].add(Calendar.SECOND, - offeredSLARuntimes[i]); // get back of SLAduration[i] seconds
			}
			
			//System.out.println(this.format.format(this.simulationTime.getTime())+" - "+this.format.format(this.simulationTime.getTime())+" = "+ (this.simulationTime.getTimeInMillis()/1000 - this.simulationBeginning.getTimeInMillis()/1000) );
			if(this.reportingMode){
				values = new ArrayList<Double>(); for(int i=0; i < power_columns; i++) values.add(0.0); // we need to initialize it because we then need to use the initialOrdering of the loads
			}
			
			// 1. retrieve currently available power
			gridProduction = this.gridProvisioning.getCurrentProduction(this.simulationTime, weather);
			localProduction = 0.0; 	for(GenerationModel g : this.reGenerators){	localProduction += g.getCurrentProduction(simulationTime, weather);	}
			if(this.reportingMode){
				values.set(0, localProduction); 	//values[0] = localProduction;
				values.set(1, gridProduction); 		//values[1] = gridProduction;
			}
			
			// let's hypotize that at t+1 the available power won't change much
			expectedGridProduction = gridProduction;
			expectedLocalProduction = localProduction;
			
			// 2. build the window to pass to the ANN
			for(int i =0; i<timeWindow.length; i++){
				window_gridProduction[i] = this.gridProvisioning.getCurrentProduction(timeWindow[i], weather);																// get grid availability at time t-d_SLA_i
				window_localProduction[i] = 0.0; for(GenerationModel g : this.reGenerators) window_localProduction[i] += g.getCurrentProduction(timeWindow[i], weather);	// get local production at time t-d_SLA_i
				
				window_fit[i] = fit.getPrice(timeWindow[i]);
				window_get[i] = get.getPrice(timeWindow[i]);
			}
			
			//Debugger.println(this.format.format(this.simulationTime.getTime())+" - "+"Grid: "+gridProduction+" Local: "+localProduction);
			
			// 2. calculate previously allocated supply
			currentDemand = 0.0;		// demand at simulation time (planned according to the SLAs)
			actualCurrentDemand = 0.0; 	// actual demand of the load operation
			expectedDemand = 0.0;		// demand at allocation time (simulation time + 1)
			
			// update the SLAs status
			for(Load l : loads){
				
				if(serviceLevelAgreements.containsKey(l) && serviceLevelAgreements.get(l) != null){	// if there is a SLA for load l
					
					SLA s = serviceLevelAgreements.get(l);
					Debugger.printLoadInfo("\t"+this.format.format(simulationTime.getTime())+" SLA for "+s.client.getName()+" for "+s.amount+"W lasting "+s.duration+" being updated to "+(s.duration-1));
					
					// check current supply to see if it can cover the SLA at this time instant
					if( ((localProduction + gridProduction) - currentDemand) >= s.amount){
						// leftover supply is enough to power the load at this time instant
						
						s.updateSLAduration();							// decrease duration left for the SLA
						l.allowToRun(s.amount, simulationTime);			// allocate s.amount Watts for the load at the current time
					
						// remove the SLA if the leftover duration is 0
						if(s.duration == 0){
							serviceLevelAgreements.put(l, null);
							Debugger.printLoadInfo("\t-> Expired SLA for "+s.client.getName());
						}else{
							expectedDemand += s.amount;					// increase the expected demand iff the SLA will run at t+1
						}
						
						currentDemand += s.amount;										// add the demand of the SLA to the overall IDEAL demand of the smart microgrid
						
						//System.out.print(l.getActualDemand(simulationTime)+", ");
						l.updateStatus(simulationTime);									// update load status based on previous action (should always be after allowToRun)
						//System.out.println(l.getActualDemand(simulationTime));
						actualCurrentDemand += l.getActualDemand(simulationTime);		// get the power actually used by the load (the SLA could last longer than necessary)
						
						if(this.reportingMode)	values.set(2+initialOrder.get(l.getName()), (double) s.amount);	// value[2+i] = P_i	// add the load demand for plotting purposes
						//values.set(2+initialOrder.get(l.getName()), l.getActualDemand(simulationTime));
					}else{
						// the available supply is not enough to power the load
						l.allowToRun(0, simulationTime);								// allow to run 0 Watts
						l.updateStatus(simulationTime);									// update load status
						if(this.reportingMode)	values.set(2+initialOrder.get(l.getName()), 0.0);
						
						// destroy the SLA and punish the broker
						serviceLevelAgreements.put(l, null);
						//Debugger.printLoadInfo("\t-> Available "+((localProduction + gridProduction) - currentDemand)+"W << "+s.amount+"W: destroyed SLA for "+s.client.getName());
						//System.out.println("\t-> Available "+((localProduction + gridProduction) - currentDemand)+"W << "+s.amount+"W: destroyed remaining ("+s.amount+"W*"+s.duration+"s,"+s.unitPrice+"Eur/kWh) SLA for "+s.client.getName()+" ("+((s.amount / 1000.0) * (s.unitPrice / 3600.0) * s.duration)+"Eur)");
						
						// reimburse the load and charge the broker for the difference
						this.broker.reimburse(s.amount, s.duration, s.unitPrice);
						l.reimburse(s.amount, s.duration, s.unitPrice);
					}
					
				}else{
					l.allowToRun(0, simulationTime);			// allow to run 0 Watts
					l.updateStatus(simulationTime);				// update load status based on previous action (should always be after allowToRun)
					if(this.reportingMode)	values.set(2+initialOrder.get(l.getName()), 0.0);
				}
			}
			
			/******************************************************************************************/
			// update the available local supply according to the actual current demand
			localProduction -= actualCurrentDemand; //currentDemand;	// the broker pays the power actually drawn by the load
			
			// manage payment of the broker towards the grid and local generators
			if(localProduction < 0){
				// if localProduction < 0 then demand was bigger than local supply
				this.broker.charge(Math.abs(localProduction), get.getPrice(simulationTime));	// charge the broker for the energy he bought from the grid
				gridProduction += localProduction; 												// same to say gridProduction -= Math.abs(localProduction);
				localProduction = 0;															// we can't have negative P_re, now set to 0 for correctness
			}else{
				// if localProduction > 0 then we can inject it in the grid and get paid for that (for the allocation lenght)
				//this.broker.addIncome(localProduction, this.allocationTimeslotLenght, fit.getPrice(simulationTime));
				this.broker.addIncomeFromFIT(localProduction, this.allocationTimeslotLenght, fit.getPrice(simulationTime));
			}
			/******************************************************************************************/
			expectedLocalProduction -= expectedDemand;
			if(expectedLocalProduction < 0){
				expectedGridProduction += expectedLocalProduction;
				expectedLocalProduction = 0;
			}
			/******************************************************************************************/
			if(this.reportingMode){
				values.set(values.size()-7, actualCurrentDemand);
				values.set(values.size()-6, currentDemand);
				values.set(values.size()-5, expectedDemand);
				
				values.set(values.size()-4, localProduction);
				values.set(values.size()-3, expectedLocalProduction);
				
				values.set(values.size()-2, gridProduction);
				values.set(values.size()-1, expectedGridProduction);
				
				//Debugger.println("\t"+values.toString());
				logger.addInstant(0, simulationTime, values);
			}
			
			// 3. query the broker to make a price for future provisioning and ask each load to buy it
			this.trade(rounds);
			
			this.simulationTime.add(Calendar.SECOND, 1);
		}
	}
	

	public class SLA{
		public Load client;
		
		public int amount;
		public int duration;
		
		public double unitPrice;	
		
		public SLA(Load client, int amount, int duration, double unitPrice){
			this.client = client;
			this.amount = amount;
			this.duration = duration;
			this.unitPrice = unitPrice;
		}
		
		public void updateSLAduration(){
			this.duration--;
		}
	}
	
	
	public double getSystemReactivity(){
		return (this.timesCouldBuyPower + this.timesCouldNotBuyPower) > 0 ? 
				this.timesCouldBuyPower / (double) (this.timesCouldBuyPower + this.timesCouldNotBuyPower) :
					1.0;	// no load has even requested power!! uncertainty condition
	}
	
	protected int timesCouldBuyPower;
	protected int timesCouldNotBuyPower;
	
	protected void trade(int rounds) throws RemoteManagerUanavailableException{
		
		ArrayList<Double> values;
		ArrayList<Double> willingness = new ArrayList<Double>(); for(Load l: loads) willingness.add(new Double(0.0));
		
		ArrayList<String> queriedLoads = new ArrayList<String>();
		//for(int round=0; round<rounds; round++){
			
			double intraTime = 0; //round / (double) rounds;
			
			boolean needPriceRecalculation;
			do{
				needPriceRecalculation = false;
				
				// formulate a price based on current power availability
				double[] priceSLAs = this.broker.getOffer(simulationTime,
															getSunIntensityByTime(simulationTime),	// ideal sunlight intensity (based on the period)
															weather.getSunFactor(simulationTime),	// current sunlight intensity (based on the weather model)
															
															expectedLocalProduction,
															localProduction,
															window_localProduction,
																				
															fit.getPrice(simulationTime),
															window_fit,
															
															expectedGridProduction,
															gridProduction, 
															window_gridProduction,
																				
															get.getPrice(simulationTime),
															window_get,
																				
															intraTime,
															expectedDemand,
															currentDemand);
				
				if(this.reportingMode){
					values = new ArrayList<Double>();
					// log all prices for the current timestamp and round
					for(int p=0; p <priceSLAs.length; p++) values.add(priceSLAs[p]); 
					logger.addMultivalueInstant(0, simulationTime, values);
				}
				
				// shuffle order of loads to have same chance of selection
				Collections.shuffle(loads, this.randomGen);
				
				// check each load to see if they wanna buy at the formulated price
				for(int l=0; l < loads.size() && !needPriceRecalculation; l++){
					Load load = loads.get(l);	// select the currently visited load
					
					if( !serviceLevelAgreements.containsKey(load) || serviceLevelAgreements.get(load) == null	// pick only loads who are not yet being supplied energy already 
					){
						// get the load demand if not already done at this time
						int[] demand = load.getExpectedDemand(simulationTime);	// get the demand for each SLA for the selected load
						// demand[0]: power
						// demand[1]: duration
						//Debugger.printLoadInfo(load.getName()+": "+demand[0]+"W, "+demand[1]+"secs "+this.format.format(simulationTime.getTime()));
						
						if(demand[0] > 0 
								//&& !queriedLoads.contains(load.getName())	// iff were not yet asked in this trading day 
							){
							// if there is a willingness to buy power then try to sell it to the load by passing the price vector
							willingness.set(initialOrder.get(load.getName()), 1.0);				// set the load willingness to buy to true
							
							int[] offer_quantityVector = load.getOffer(simulationTime, priceSLAs); 	// quantity vector: 1 element for each SLA duration
							
							int total_requestedPower = 0; for(int o : offer_quantityVector) total_requestedPower += o;
							
							// ************ Allocate SLAs for the load iff there is enough power available ************
							if(expectedGridProduction + expectedLocalProduction > total_requestedPower){
								
								if(!queriedLoads.contains(load.getName())){
									timesCouldBuyPower++;	// the load could potentially buy enough power to operate its state, keep track
									queriedLoads.add(load.getName());
									Debugger.printLoadInfo("\t\t"+load.getName()+" has the chance to buy power (Tot_requested:"+total_requestedPower+"W) "+this.format.format(simulationTime.getTime()));
								}
								
								for(int d=0; d < offer_quantityVector.length && !needPriceRecalculation; d++){
									if(offer_quantityVector[d] > 0){
										SLA s = new SLA(load, 
												offer_quantityVector[d],	// demand for the SLA
												offeredSLARuntimes[d],		// duration for the SLA
												priceSLAs[d]);				// price formulated by the broker
										serviceLevelAgreements.put(load, s);	// add the SLA for the load
										
										this.numberOfSoldSLAs[d]++;		// increase the number of sold agreements for the duration d
										
										Debugger.printLoadInfo("\t-> Added agreements for load "+load.getName()+" for "+demand[0]+"W for "+(s.duration-1)+" sec at "+this.format.format(simulationTime.getTime()));
										//System.out.println("\t-> SLA matched for "+load.getName()+" for "+demand[0]+"W for "+s.duration+" sec at "+this.format.format(simulationTime.getTime()));
									
										//try{ String scan = bufferRead.readLine(); }catch(IOException e){ e.printStackTrace();}
										
										// add payment for the broker for the SLA
										this.broker.addIncome(s.amount, s.duration, s.unitPrice);				// pay the broker for the sold SLA
										load.charge((s.amount / 1000.0) * (s.unitPrice / 3600.0) * s.duration);	// charge the load for the bought SLA
										
										// update the total power supply
										expectedLocalProduction -= s.amount;
										if(expectedLocalProduction < 0){	// if P_re is not enough to supply s.amount
											expectedGridProduction += expectedLocalProduction;	// reduce P_grid for the difference
											expectedLocalProduction = 0;
										}
										// update the total amount of sold demand
										expectedDemand += s.amount;
										
										// recalculate price vector as the load bought the power a SLA was created for it 
										needPriceRecalculation = true;
									}
								}
								
								if(!serviceLevelAgreements.containsKey(load) || serviceLevelAgreements.get(load) == null){
									String prices = ""; for(double p : priceSLAs) prices += p+" ";
									Debugger.printLoadInfo("\t"+this.format.format(simulationTime.getTime())+": Proposed price vector "+prices+" too high for load "+load.getName());
								}
								
							}else{
								
								Debugger.printLoadInfo("\t\tNot enough total power ("+(expectedGridProduction+expectedLocalProduction)+"W) to supply the demand of "+load.getName()+" ("+total_requestedPower+"W)");
								
								if(!queriedLoads.contains(load.getName())){
									timesCouldNotBuyPower++;	// the load could NOT buy enough power to operate its state, keep track for the current trade iff not yet done
									queriedLoads.add(load.getName());
									Debugger.printLoadInfo("\t\t"+load.getName()+" has NO chance to buy power");
								}
									
							}
							//if(load.getName().equals("coffeemachine")) try{ String scan = bufferRead.readLine(); }catch(IOException e){ e.printStackTrace();}
						}
					}
					
				}
			
			}while(needPriceRecalculation);	// repeat as long as price recalculation is necessary
			
		// go to the next round if necessary
		//}
		if(this.reportingMode) logger.addInstant(2, simulationTime, willingness);
	}
	
	protected int[] getSoldSLAs(){
		return this.numberOfSoldSLAs;
	}
	
	protected double[] getPAR(){
		double[] par = new double[3];
		double[] maxAndAvg = this.logger.getAverageAndMaxValue(0, power_columns-7);
		
		par[0] = maxAndAvg[0] > 0 && maxAndAvg[1] > 0 ?
				maxAndAvg[0] / maxAndAvg[1] : 0;
		par[1] = maxAndAvg[0];
		par[2] = maxAndAvg[1];
		
		//System.out.println("MAX:"+maxAndAvg[0]+", AVG:"+maxAndAvg[1]);
		return par;
	}
	
	protected double[] getAvgAvailability(){
		double result[] = new double[2];
		double counter = 0.0;
		// *********
		/*
		// compute the average over all state availability averages
		for(Load l : this.loads){
			if(l.operations.size() > 0){
				counter++;	// get only those loads that actually operated
				double l_result[] = new double[2];
				for(OperationStats o : l.operations){
					l_result[0] += o.getAvailability();
					l_result[1] += o.getUnavailability();
				}
				result[0] += l_result[0] / (double) l.operations.size();
				result[1] += l_result[1] / (double) l.operations.size();
			}
		}
		result[0] /= counter;
		result[1] /= counter;
		// *********
		*/
		
		// compute the average in a flat way over all states
		for(Load l : this.loads){
			for(OperationStats o : l.operations){
				result[0] += o.getAvailability();
				result[1] += o.getUnavailability();
				counter++;
			}
		}
		result[0] /= counter > 0? 
						counter : 1;
		result[1] /= counter > 0? 
						counter : 1;
		
		// TODO: make sure only those loads with completed states are considered in the counting
		
		return result;
	}
	
	protected double computeFitness(){
		// the fitness is the profit of the broker, which is basically obtained from the earnings
		// minus the costs to buy energy from the suppliers and the penalty of violated SLAs
		// in particular the broker should be proportionally penalized to the difference in amount and duration	
		
		// the goal is therefore to minimize the SLA violation in order to maximise the profit
		return (this.broker.income + this.broker.incomeFromFIT) 
				-	( this.broker.expenses 
					+ this.reimbursement_penalty*this.broker.reimbursement);
	}
	
	protected double computeProfit(){
		return (this.broker.income + this.broker.incomeFromFIT) - (this.broker.expenses + this.broker.reimbursement);
	}
	
	protected double getSunIntensityByTime(Calendar time){
		// returns the irradiance (intensity of solar radiation) in [W/m^2], which, when at the Zenit (21 June in Europe) is about 1050 W/m^2
		double intensity = 0.0;
		
		AzimuthZenithAngle sun = PSA.calculateSolarPosition((GregorianCalendar) time, latitude, longitude);		
		double zenitAngle = sun.getZenithAngle();
			    
		if(zenitAngle < 90)	intensity = SolarCalculations.getSolarIntensity(zenitAngle, height);
		
		//return intensity;
		return intensity / 1050.0; // to return a value in [0,1] 
	}
	
	protected void setupLogger(){
		power_columns = 2+this.loads.size()+7;
		
		logger = new LoggerTimeserie();
		
		// 1st timeserie
		String[] powerTS = new String[power_columns]; powerTS[0] = "P_re"; powerTS[1] = "P_grid";
		for(int i=2; i<2+loads.size();i++) powerTS[i] = this.loads.get(i-2).getName();
		
		powerTS[powerTS.length-7] = "actual_current_demand";
		powerTS[powerTS.length-6] = "current_demand";
		powerTS[powerTS.length-5] = "expected_demand";
		powerTS[powerTS.length-4] = "left_P_re";
		powerTS[powerTS.length-3] = "expected_P_re";
		powerTS[powerTS.length-2] = "left_P_grid";
		powerTS[powerTS.length-1] = "expected_P_grid";
		
		logger.addTimeserie(0, powerTS);
		
		// 2nd timeserie is a multivalued one
		String[] priceTS = new String[this.offeredSLADurations.length+1]; 
		for(int i=0; i<offeredSLADurations.length; i++) priceTS[i] = "SLA_"+this.offeredSLADurations[i];
		priceTS[this.offeredSLADurations.length] = "index";
		logger.useMultivalueTS();
		logger.addMultivalueTimeserie(0, priceTS);
		
		String[] price_loadsTS = new String[this.loads.size()];	// 3rd timeserie
		String[] willingnessTS = new String[loads.size()];		// 4th timeserie
		
		for(int i=0; i<loads.size(); i++){
			price_loadsTS[i] = this.loads.get(i).getName();
			willingnessTS[i] = this.loads.get(i).getName();
		}
		
		logger.addTimeserie(1, price_loadsTS);
		logger.addTimeserie(2, willingnessTS);
	}
	
	protected void setupScenario(int gridProvisioningType, int weatherType) throws Exception{
		
		String weather_folder ="";
		String event_ds_folder = "";
		if(System.getProperty("os.name").contains("Mac")){	
			event_ds_folder = "/Users/andreamonacchi/Documents/workspace/willingnessExtraction/starting_events/benjamin/";
			weather_folder = "/Users/andreamonacchi/Documents/workspace/willingnessExtraction/weather_model_klagenfurt/";
		}else if(InetAddress.getLocalHost().getHostName().contains("phenom")){ 
			event_ds_folder = "/home/pilillo/Documents/benjamin/";
			weather_folder = "/home/pilillo/Documents/weather_model_klagenfurt/";
		}else{ 
			event_ds_folder = "/home/sci/amonacch/simulations/starting_events/benjamin/"; //"benjamin/short/";
			weather_folder = "/home/sci/amonacch/simulations/weather_model_klagenfurt/";
		}
		
		// ------ setup a grid model ------
		// define the availability of energy in the grid
		ArrayList<TimeBasedTradeableAmount> provisioningPlan_amount = new ArrayList<TimeBasedTradeableAmount>();
		switch(gridProvisioningType){
			case 0:
				provisioningPlan_amount.add(new TimeBasedTradeableAmount(0,0, 23,59, 0));		// 0kW (00:00:00 till 23:59:59 included)
				break;
				
			case 1:
				provisioningPlan_amount.add(new TimeBasedTradeableAmount(0,0, 23,59, 1500));	// 3kW (00:00:00 till 23:59:59 included)
				break;
				
			case 2:
				provisioningPlan_amount.add(new TimeBasedTradeableAmount(6,0, 17,59,3000));		// 3 kW from 6 am to 6 pm
				provisioningPlan_amount.add(new TimeBasedTradeableAmount(18,0, 05,59,1000));
				break;
				
			case 3:
				provisioningPlan_amount.add(new TimeBasedTradeableAmount(0,0, 23,59, 3000));	// 3kW (00:00:00 till 23:59:59 included)
				break;
			
			case 4:
				provisioningPlan_amount.add(new TimeBasedTradeableAmount(0,0, 23,59, 6000));	// 6kW (00:00:00 till 23:59:59 included)
				break;
		}
		
		//provisioningPlan_amount.add(new TimeBasedTradeableAmount(0,0, 23,59, 0));	// 3kW (00:00:00 till 23:59:59 included)
		
		gridProvisioning = new TimeBasedGenerator(new IntervalBasedCapability(provisioningPlan_amount));

		// define the grid energy tariff
		ArrayList<Tariff> provisioningPlan_price = new ArrayList<Tariff>();
			provisioningPlan_price.add(new Tariff("day", 6,	0, 21, 0, 0.29));
			provisioningPlan_price.add(new Tariff("night", 21, 0, 6, 0, 0.15));
		get = new TariffPriceModel(provisioningPlan_price);
		
		//gridConstraints = getCapabilityModel(capModel.get("power-capability"));
		
		// define the feed-in tariff
		ArrayList<Tariff> fitPlan_price = new ArrayList<Tariff>();
			fitPlan_price.add(new Tariff("day", 6,	0, 21, 0, 0.04));
			fitPlan_price.add(new Tariff("night", 21, 0, 6, 0, 0.02));
		fit = new TariffPriceModel(fitPlan_price);			
		
		// ------ model location --------
		latitude = 46.6;
		longitude = 14.4;
		height = 0.446;
		
		// ------ setup generators ------
		reGenerators = new ArrayList<GenerationModel>();
		// each generator consists of a reservation price and a generation model
		ArrayList<Tariff> pvProductionCost = new ArrayList<Tariff>();
			pvProductionCost.add(new Tariff("pv", 0, 0, 0, 1, 0.18));
		
		double PVPowerPeak = 3300.0; // P_re = 3.3 kW
		Generator g = null;
		// ------ setup weather model ------
		switch(weatherType){
			case 0:
				// ------ setup weather model ------
				ArrayList<TimeBasedCloudFactor> model_0 = new ArrayList<TimeBasedCloudFactor>();
				model_0.add(new TimeBasedCloudFactor(	new GregorianCalendar(simulationBeginning.get(Calendar.YEAR), 0, 1, 0, 0, 0).get(Calendar.DAY_OF_YEAR),
																0,0,0, 		// h, m, s
																0.0,		// cloud factor
																new GregorianCalendar( simulationBeginning.get(Calendar.YEAR), 11, 31, 0, 0, 0).get(Calendar.DAY_OF_YEAR),
																0,0,0));	// h, m, s
				weather = new IntervalBasedWeather(model_0);		
				// add generator using the ideal weather
				g = new ModeledPV(PVPowerPeak,				// peak power
									0.15,					// efficiency
									latitude,	//46.6,		// latitude
									longitude,	//14.4, 	// longitude
									height, 	//0.446,	// height
									50.0);					// size
				break;
				
			case 1:
				weather = new TimeserieWeather(weather_folder+"klagenfurt_weather.ts");
				g = new MeasuredPV(PVPowerPeak);				
				break;
		}
		reGenerators.add(new GenerationModel(new TariffPriceModel(pvProductionCost), g));
		
		// ------ setup loads ------
		this.initialOrder = new HashMap<String,Integer>();
		
		loads = new ArrayList<Load>();

		// ******* Entertainment system
		DeviceStateModel stateModel = new DeviceStateModel();
		stateModel.addState(180, 3600, 7200);
		UsageModel usageModel = new UsageModel();
		usageModel.buildTimeserie(event_ds_folder+"events_device_0.ts");
		
		loads.add(new Load("entertainment", 10.0, usageModel, stateModel, new StaticPriceModel(utilities[0]), this.randomGen));
		this.initialOrder.put(loads.get(0).getName(), 0);
		
		// ******* Dishwasher
		DeviceStateModel stateModel_2 = new DeviceStateModel();
		stateModel_2.addState(2100, 300, 7200);		// water heating			- 5 min at 2100W
		stateModel_2.addState(100, 120, 10);		// 2nd state				- 2 min at 100W
		stateModel_2.addState(300, 60, 10);			// 3rd state				- 1 min at 300W
		stateModel_2.addState(100, 120, 10);		// 4th state				- 2 min at 100W
		stateModel_2.addState(2100, 300, 180);		// rinse					- 5 min at 2100W
		UsageModel usageModel_2 = new UsageModel();
		usageModel_2.buildTimeserie(event_ds_folder+"events_device_4.ts");
		loads.add(new Load("dishwasher", 10.0, usageModel_2, stateModel_2, new StaticPriceModel(utilities[1]), this.randomGen));
		this.initialOrder.put(loads.get(1).getName(), 1);
		
		// ******* Dryer (20 min made of 2 min heating states)
		DeviceStateModel stateModel_3 = new DeviceStateModel(); 
		stateModel_3.addState(2500, 120, 7200);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		stateModel_3.addState(2500, 120, 10);	// 2 min long heating state
		UsageModel usageModel_3 = new UsageModel();
		usageModel_3.buildTimeserie(event_ds_folder+"events_device_3.ts");
		loads.add(new Load("tumble_dryer", 10.0, usageModel_3, stateModel_3, new StaticPriceModel(utilities[2]), this.randomGen));
		this.initialOrder.put(loads.get(2).getName(), 2);
		
		// ******* Washing machine (quick washing cycle, 15 min)
		DeviceStateModel stateModel_4 = new DeviceStateModel();
		stateModel_4.addState(2100, 120, 7200); // water heating			- 2 min at 2100W
		stateModel_4.addState(300, 300, 10);	// motor spinning			- 5 min at 300W
		stateModel_4.addState(200, 120, 60);	// water pump for rinsing	- 2 min at 200W
		stateModel_4.addState(600, 300, 180);	// spin dryer				- 5 min at 600W
		stateModel_4.addState(200, 60, 60);		// water pump				- 1 min at 200W
		UsageModel usageModel_4 = new UsageModel();
		usageModel_4.buildTimeserie(event_ds_folder+"events_device_2.ts");
		loads.add(new Load("washing_machine", 10.0, usageModel_4, stateModel_4, new StaticPriceModel(utilities[3]), this.randomGen));
		this.initialOrder.put(loads.get(3).getName(), 3);
		
		// ******* Fridge
		DeviceStateModel stateModel_5 = new DeviceStateModel();
		stateModel_5.addState(200, 30);		// initial peak	(30 secs)
		stateModel_5.addState(160, 600);	// cooling state (10 min)
		UsageModel usageModel_5 = new UsageModel();
		usageModel_5.buildRandom(0.8, 0.5);
		loads.add(new Load("fridge", 10.0, usageModel_5, stateModel_5, new StaticPriceModel(utilities[4]), this.randomGen));
		this.initialOrder.put(loads.get(4).getName(), 4);

		// ******* Coffee machine
		DeviceStateModel stateModel_6 = new DeviceStateModel();
		stateModel_6.addState(2000, 60, 0);
		UsageModel usageModel_6 = new UsageModel();
		usageModel_6.buildTimeserie(event_ds_folder+"events_device_7.ts");
		loads.add(new Load("coffeemachine", 10.0, usageModel_6, stateModel_6, new StaticPriceModel(utilities[5]), this.randomGen));
		this.initialOrder.put(loads.get(5).getName(), 5);
	}
	
	public void loadResult(File resultFile, int seed, Writer writer,
			String simulationBeginning, 
			int simulationDuration, int allocationTimeslotLength,
			double limitPrice,
			int gridType,
			int weatherType){
		try {
						
			// Create new FREVO instance (as in the main of FrevoMain)
			FrevoMain.loadInstallDirectory();
			FrevoMain.initComponentDirectories();
			
			FrevoMain.loadComponents(false);
			FrevoMain.loadProperties();
					
			// Load session part (as in FrevoWindow.loadFile)
			Document doc = FrevoMain.loadSession(resultFile);
			
			if (FrevoMain.getExtension(resultFile).equals(
					FrevoMain.FREVO_RESULT_EXTENSION)) {
				// Load populations -> Call method's own loader
				try {
					// Instantiate components
					ComponentXMLData method = FrevoMain.getSelectedComponent(ComponentType.FREVO_METHOD);

					AbstractMethod m = method.getNewMethodInstance(new NESRandom(seed));
					
					// Loaded populations
					ArrayList<ArrayList<AbstractRepresentation>> populations = m.loadFromXML(doc);
					// sort all representations before visualizing
					for (ArrayList<AbstractRepresentation> representations : populations) {
						Collections.sort(representations, new RepresentationComparator());
					}
					
					/*
					for(ArrayList<AbstractRepresentation> representations : populations){
						System.out.println("--- New population of "+representations.size()+" elements");
						for(AbstractRepresentation r : representations){
							System.out.println(r.getFitness());
						}
						System.out.println("---");
					}*/
					
					// ---
					ProblemXMLData problem = (ProblemXMLData) FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM);
					AbstractSingleProblem ip = (AbstractSingleProblem) problem.getNewProblemInstance();
					ip.setRandom(new NESRandom(seed));
					
					// get candidate with highest fitness and replay it in the market
					((Market)ip).replayCandidate(populations.get(0).get(0),
															resultFile,
															writer, seed,
															simulationBeginning, simulationDuration, allocationTimeslotLength, 
															limitPrice, gridType, weatherType);
					// ---
					
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	
	private static ArrayList<File> listFilesForFolder(File folder, String extension){
		ArrayList<File> files = new ArrayList<File>();
		
		int bestGeneration = -1;
		File bestInCurrentFolder = null;
		
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	// expand subdirectory
	            files.addAll( listFilesForFolder(fileEntry, extension) );
	        } else {
	        	// look for latest generations	        	
	        	if(fileEntry.getName().endsWith(extension)){
	        		int generation = Integer.parseInt(fileEntry.getName().substring(fileEntry.getName().lastIndexOf("_g")+2,	fileEntry.getName().lastIndexOf(" (")));
	        		if(generation > bestGeneration){
	        			bestInCurrentFolder = fileEntry;
	        			bestGeneration = generation;
	        		}
	        	}
	        }
	    }
		
		if(bestGeneration > 0) files.add(bestInCurrentFolder);
		
		return files;
	}
	
	public void replayCandidate(AbstractRepresentation candidate, 
			File resultFile,
			Writer writer, int seed,
			String simulationBeginning,
			int simulationDuration,
			int allocationTimeslotLength,
			double limitPrice,
			
			int gridType, 
			int weatherType){
		
		this.reportingMode = true;	// enable logging
		
		// init simulation settings
		try{
			this.simulationTime = Calendar.getInstance();
			this.simulationBeginning = Calendar.getInstance(); 
			this.allocationTimeslotLenght = allocationTimeslotLength;
			
			this.randomGen = getRandom();
			this.limitPrice = limitPrice;
			
			this.timesCouldBuyPower = 0;
			this.timesCouldNotBuyPower = 0;
			this.numberOfSoldSLAs = new int[offeredSLADurations.length];
		
			this.simulationDuration = simulationDuration;
			
			// set penalties			
			this.reimbursement_penalty = Double.parseDouble(getProperties().get("reimbursement_penalty").getValue());
			
			this.simulationTime.setTime(format.parse(simulationBeginning));
			this.simulationBeginning.setTime(this.simulationTime.getTime());
				
			this.setupScenario(	gridType, weatherType);
			
			if(this.reportingMode) this.setupLogger();
			
			// -----------
			
			this.broker = new Broker(candidate, this.limitPrice);
			this.broker.setANNBroker( BType.valueOf( getProperties().get("broker_type").getValue() ) );
			
			this.simulate(1); 			// in the simplest case we only have 1 round (no auction involved)
			/*
			if(writeVerbose){
				
				this.logger.saveToCSV(folder+"broker_power.csv", 		0);
				this.logger.saveToCSV(folder+"broker_price_loads.csv",	1);
				this.logger.saveToCSV(folder+"broker_willingness.csv",	2);		
				this.logger.saveMVToCSV(folder+"broker_price.csv",		0);
			}
			*/
			// gather results
			double[] availability = getAvgAvailability();
			
			String soldSLAs = "";
			for(int d=0; d<this.numberOfSoldSLAs.length; d++){
				soldSLAs += this.getSoldSLAs()[d];
				if(d<this.numberOfSoldSLAs.length-1) soldSLAs += ",";
			}
			
			writer.append(
						resultFile.getParent()		+","	
						+resultFile.getName()		+","
						
						+broker.type.name()			+","
						+gridType					+","
						+weatherType				+","
						+simulationBeginning		+","
						+simulationDuration			+","
						+seed						+","
						
						+getPAR()[0]				+","
						+getPAR()[1]				+","
						+getPAR()[2]				+","
						+availability[0]			+","
						+availability[1]			+","
						+getSystemReactivity()		+","
						+this.timesCouldBuyPower	+","
						+this.timesCouldNotBuyPower	+","
						
						+computeFitness()			+","
						+computeProfit()			+","
						+broker.getIncome()			+","
						+broker.getIncomeFromFIT()	+","
						+broker.getSupplyCosts()	+","
						+broker.getReimbursementExpenses()+","
						+soldSLAs
						+"\n");
			
			
		}catch(ParseException e){
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) throws Exception{
		
		String result_folder = System.getProperty("user.dir")+"/";	// directory from which the program was launched
		
		
		//***********************
		/*
		Market m = new Market();
		m.reportingMode = true;
		m.broker = new Broker(null, 1.0);
		m.broker.setHardCodedBroker(BType.pessimistic);	
		m.randomGen = new Random(12345);
		
		m.simulationTime = Calendar.getInstance();
		m.simulationTime.setTime(m.format.parse("2014 12 31 23:50:00"));
		
		//System.out.println(m.simulationTime.getActualMaximum(Calendar.DAY_OF_YEAR));	// test
		
		m.simulationBeginning = Calendar.getInstance(); 
		m.simulationBeginning.setTime(m.simulationTime.getTime());
		
		//m.simulationDuration = 8000;
		//m.simulationDuration = 86400; // 1D
		//m.simulationDuration = 172800;
		//m.simulationDuration = 259200; // 3D
		m.simulationDuration = 604800; // 7D
		//m.simulationDuration = 2678400;	// 31D
		
		m.allocationTimeslotLenght = 1;
		m.limitPrice = 1.0;
		
		m.setupScenario(0);
		m.setupLogger();
		*/
		// -----------
		/*
		m.logger = new LoggerTimeserie(); String[] sunlight = {"Sunlight"};	m.logger.addTimeserie(0, sunlight);
		
		m.simulationTime.setTime(m.format.parse("2015 01 01 00:00:00"));
		m.simulationDuration = 31536000;
		
		while( m.simulationTime.getTimeInMillis()/1000 - m.simulationBeginning.getTimeInMillis()/1000 <= m.simulationDuration ){
			//System.out.println(m.format.format(m.simulationTime.getTime()) +": "+ m.getSunIntensityByTime(m.simulationTime));
			ArrayList<Double> values = new ArrayList<Double>();
			values.add(m.getSunIntensityByTime(m.simulationTime));
			
			m.logger.addInstant(0, m.simulationTime, values);
			
			m.simulationTime.add(Calendar.HOUR_OF_DAY, 1);
		}*/
		//m.logger.saveToCSV("/Users/andreamonacchi/Desktop/test_sunlight.csv", 0);
		/*
		m.simulationDuration = 200; // 1D
		while( m.simulationTime.getTimeInMillis()/1000 - m.simulationBeginning.getTimeInMillis()/1000 <= m.simulationDuration ){
		
			System.out.println(m.format.format(m.simulationTime.getTime())+": "+ m.gridProvisioning.getCurrentProduction(m.simulationTime, m.weather) );
			
			m.simulationTime.add(Calendar.SECOND, 1);
		}*/
		// -----------
		/*
		m.simulate(1);
		
		m.logger.saveToCSV("/Users/andreamonacchi/Desktop/test_broker_power.csv", 0);
		m.logger.saveToCSV("/Users/andreamonacchi/Desktop/test_broker_price_loads.csv", 1);
		m.logger.saveToCSV("/Users/andreamonacchi/Desktop/test_broker_willingness.csv", 2);
		m.logger.saveMVToCSV("/Users/andreamonacchi/Desktop/test_broker_price.csv", 0);
		
		for(Load l : m.loads){
			System.out.println(l.getName()+": C:"+l.getCredit()+", E:"+l.getExpenses()+", Re:"+l.getReimbursedCredit());
			for(OperationStats o : l.operations){
				for(StateOperation s : o.getAllStatesStats()){
					System.out.println( "State_"+s.deviceState +(s.concluded?"*":"") +", decided:"+
										m.format.format(new Date(s.firstOfferTime))+", started:"+
										m.format.format(new Date(s.stateBeginningTime))+"("+s.startDelay+"), "+
										"lasted:"+s.operationDuration+" (ended:"+m.format.format(s.stateBeginningTime+s.operationDuration*1000)+")"+
										", u:"+s.uptime+
										", i:"+s.interruptions+", "+s.getAvailability()+", "+s.getUnavailability());
					System.out.println("\tReward:"+s.reward);
				}
			}
		}
		
		double[] availability = m.getAvgAvailability();
		System.out.println("PAR:"+m.getPAR()+", A:"+availability[0]+", U:"+availability[1]);
		System.out.println("Reactivity: "+m.getSystemReactivity()+", "+m.timesCouldBuyPower+", "+m.timesCouldNotBuyPower);
		
		double profit = m.computeProfit();
		m.broker.getIncome()
		double fitness = m.computeFitness();
		
		//System.out.println(((this.broker.income+this.broker.incomeFromFIT)-(this.broker.expenses+this.broker.reimbursement))+" = ("+this.broker.income+"+"+this.broker.incomeFromFIT+") - ("+this.broker.expenses+"+"+this.broker.reimbursement+")");
		*/
	
		// *** Experimenting rule-based brokers ***
		/*
		int seedStart = 12345;
		int seedStop  = 12444;
		
		for(int w=0; w<=1; w++){	// change the weather from static (clear-sky model) to measured timeserie	
			FileWriter verbose_writer = new FileWriter(result_folder+"verbose_result_w"+w+".csv");
			FileWriter summary_writer = new FileWriter(result_folder+"summary_result_w"+w+".csv");

			verbose_writer.append("brokerType,gridType,seed,PAR,Max,Avg,A,U,R,CBP,CNBP,income,fit_income,supply_cost,reimbursement,profit,fitness\n");
			
			summary_writer.append("brokerType,gridType,"
								+ "minPAR,maxPAR,avgPAR,"
								+ "minA,maxA,avgA,"
								+ "minR,maxR,avgR,"
								+ "minPi1,maxPi1,avgPi1,"
								+ "minPi2,maxPi2,avgPi2,"
								+ "minC1,maxC1,avgC1,"
								+ "minC2,maxC2,avgC2,"
								+ "minPi,maxPi,avgPi,"
								+ "minPhi,maxPhi,avgPhi\n");
			
			for(int season=0; season<=0; season++){	// season 0 or 1
			
				for(int b=0; b<=1; b++){	// change broker type (0: pessimistic, 1: optimistic)
					
					for(int gridType=0; gridType<=4; gridType++){	// change grid provisioning plan (0..4)
						
						// compute the average for this broker type at the selected provisioning plan
						double[][] stats = new double[(seedStop-seedStart)+1][9]; // A,R,Pi1,Pi2,C1,C2,Pi,Phi
						
						for(int seed=seedStart; seed<=seedStop; seed++){	// test multiple seeds
							Market m = new Market();
							m.reportingMode = true;
							m.broker = new Broker(null, 1.0);
							m.broker.setHardCodedBroker(b==0 ? BType.pessimistic : BType.optimistic);	
							m.randomGen = new Random(seed);
							
							m.simulationTime = Calendar.getInstance();
							m.simulationTime.setTime(season == 0? 
														m.format.parse("2014 12 31 23:50:00") :	// winter
														m.format.parse("2015 6 21 23:50:00"));	// summer
							
							m.simulationBeginning = Calendar.getInstance(); 
							m.simulationBeginning.setTime(m.simulationTime.getTime());
							
							m.simulationDuration = 604800; // 7D
							//m.simulationDuration = 300;
							
							m.allocationTimeslotLenght = 1;
							m.limitPrice = 1.0;
							
							m.setupScenario(gridType, w);
							m.setupLogger();
							
							m.simulate(1);
							
							stats[seed-seedStart][0] = m.getPAR()[0];
							stats[seed-seedStart][1] = m.getAvgAvailability()[0];
							stats[seed-seedStart][2] = m.getSystemReactivity();
							stats[seed-seedStart][3] = m.broker.getIncome();
							stats[seed-seedStart][4] = m.broker.getIncomeFromFIT();
							stats[seed-seedStart][5] = m.broker.getSupplyCosts();
							stats[seed-seedStart][6] = m.broker.getReimbursementExpenses();
							stats[seed-seedStart][7] = m.computeProfit();
							stats[seed-seedStart][8] = m.computeFitness();
							
							verbose_writer.append(m.broker.type.name()+","+		//b+","+
												gridType+","+
												seed+","+
									
												m.getPAR()[0]+","+
												m.getPAR()[1]+","+
												m.getPAR()[2]+","+
												m.getAvgAvailability()[0]+","+
												m.getAvgAvailability()[1]+","+
												m.getSystemReactivity()+","+
												m.timesCouldBuyPower+","+
												m.timesCouldNotBuyPower+","+
												
												m.broker.getIncome()+","+
												m.broker.getIncomeFromFIT()+","+
												m.broker.getSupplyCosts()+","+
												m.broker.getReimbursementExpenses()+","+
												m.computeProfit()+","+
												m.computeFitness()+"\n");
						}
						
						// compute the aggregate stats for this broker and grid provisioning type
						double[][] aggr_stats = new double[3][9];
						for(int j=0; j<9; j++){	// initialise each param using first element of stats
							aggr_stats[0][j] = stats[0][j];		// MIN
							aggr_stats[1][j] = stats[0][j];		// MAX 
							aggr_stats[2][j] += stats[0][j];	// AVG	
						}
						
						for(int i=1; i <= (seedStop-seedStart); i++){	// loop on each simulation result
							for(int j=0; j<9; j++){	// loop on each param
								if( stats[i][j] < aggr_stats[0][j] ) aggr_stats[0][j] = stats[i][j];
								if( stats[i][j] > aggr_stats[1][j] ) aggr_stats[0][j] = stats[i][j];
								aggr_stats[2][j] += stats[i][j];
								//System.out.println(j+":"+stats[i][j]);
							}
						}
						//for(int j=0; j<9; j++) aggr_stats[2][j] /= seedStop-seedStart;	// compute the average
						
						summary_writer.append(b+","+gridType+",");
						for(int j=0; j<9; j++){
							//System.out.println (aggr_stats[2][j] +"/"+ ((seedStop-seedStart)+1) +"= "+ (aggr_stats[2][j] / ((seedStop-seedStart)+1)));
							summary_writer.append(aggr_stats[0][j]+","+aggr_stats[1][j]+","+(aggr_stats[2][j] / ((seedStop-seedStart)+1))); // append(Min,Max,Avg)
							if(j<8) summary_writer.append(",");
							else summary_writer.append("\n");
						}
					}
				}
			}
			summary_writer.flush();
			summary_writer.close();
			verbose_writer.flush();
			verbose_writer.close();
		}
		*/
	
		/*
		// ********* check weather type ********
		for(int w=0; w<=1; w++){	// check both static and actual weather
			Market m = new Market();
			m.reportingMode = false;
			m.broker = new Broker(null, 1.0);
			m.broker.setHardCodedBroker(BType.pessimistic);	
			m.randomGen = new Random(12345);
			
			m.simulationTime = Calendar.getInstance();
			//m.simulationTime.setTime(m.format.parse("2014 12 31 23:50:00"));
			m.simulationTime.setTime(m.format.parse("2015 6 21 23:50:00"));
			
			m.simulationBeginning = Calendar.getInstance(); 
			m.simulationBeginning.setTime(m.simulationTime.getTime());
			m.simulationDuration = 604800; // 2592000;
			m.allocationTimeslotLenght = 1;
			m.limitPrice = 1.0;
			m.setupScenario(0, w);
			m.setupLogger();
			
			FileWriter writer = new FileWriter(result_folder+"weather_ts_w"+w+".csv");
			writer.append("timestamp,sunintensity,production\n");
			
			while( m.simulationTime.getTimeInMillis()/1000L - m.simulationBeginning.getTimeInMillis()/1000L <= m.simulationDuration ){
				if(w==0)	writer.append(m.format.format(m.simulationTime.getTime())+","+m.getSunIntensityByTime(m.simulationTime)+","+m.reGenerators.get(0).getCurrentProduction(m.simulationTime, m.weather)+"\n");
				else 		writer.append(m.format.format(m.simulationTime.getTime())+","+m.weather.getSunFactor(m.simulationTime)+","+m.reGenerators.get(0).getCurrentProduction(m.simulationTime, m.weather)+"\n");
				m.simulationTime.add(Calendar.MINUTE, 1);
			}
			writer.flush();
			writer.close();
		}*/
		
		
		
		// ************ Compare multiple result files **********
		FileWriter writer = new FileWriter(result_folder+"candidate_comparison_"+"ANN_B_FMN"+".csv");
		String candidate_folder = "exp_all_in_one"; //"exp0_no_increvo";
		
		
		String soldSLAs = "";
		for(int d=0; d<Market.offeredSLADurations.length; d++){
			soldSLAs += Market.offeredSLADurations[d];
			if(d < Market.offeredSLADurations.length-1) soldSLAs += ",";
		}
		writer.append("File,CustomName,brokerType,gridType,weatherType,beginning,duration,seed,PAR,Max,Avg,Availability,Unavailability,Reactivity,CBP,CNBP,fitness,profit,income,fit_income,supply_cost,reimbursement,"+soldSLAs+"\n");		
		
		// select latest generation from each result folder
		for(File f : Market.listFilesForFolder(new File(System.getProperty("user.name").equals("andreamonacchi") ? 
															"/Users/andreamonacchi/Desktop/"+candidate_folder+"/" :		// local dir 
															"/home/sci/amonacch/simulations/FREVO/Results/"+candidate_folder+"/"),	// remote dir (curie)
														".zre")){
			/*
			for(int seed=12345; 
					seed <= 12355;  // 10 evaluations for each learned broker 
					seed++){
				//System.out.println(f.getAbsolutePath());
				System.out.println("Analyzing "+f.getPath());
				
				for(int season=0; season<=1; season++){				// season 0 or 1
					for(int gridType=0; gridType<=4; gridType++){	// change grid provisioning plan (0..4)
						for(int w=0; w<=1; w++){					// ideal VS real weather
							for(int duration=0; duration<=1; duration++){
								System.out.println("\t"+
													(season == 0 ? "2014 12 31 23:50:00" : "2015 6 21 23:50:00")+" "+
													(duration == 0 ? 86400 : 604800)+ // 1Day VS 7Days
													" Grid_"+gridType+
													" Weather_"+w);
							
								Market m = new Market();
								m.loadResult(f, seed, writer, 
												season == 0 ? "2014 12 31 23:50:00" : "2015 6 21 23:50:00", 
												duration == 0 ? 86400 : 604800,
												1, 1.0,
												gridType,
												w);
								writer.flush();
							}
						}
					}
				}
			}
			//writer.flush();
			*/
			
			if(f.getPath().contains("ANN_B") && f.getPath().contains("FMN")){	// avoid repeating already done simulations
			
				// the season and the grid type depend on the loaded candidate
				int season = f.getPath().contains("2014") ? 0 : 1;
				int gridType = Integer.parseInt( f.getPath().substring( f.getPath().indexOf("/g")+2, f.getPath().indexOf("/g")+3) ); 
				
				for(int seed=12345; 
						seed <= 12345;
						//seed <= 12355;  // 10 evaluations for each learned broker 
						seed++){
					
					System.out.println("Analyzing "+f.getPath());
					
					for(int w=0; w<=1; w++){					// ideal VS real weather
						for(int duration=0; duration<=1; duration++){
							System.out.println("\t"+
												(season == 0 ? "2014 12 31 23:50:00" : "2015 6 21 23:50:00")+" "+
												(duration == 0 ? 86400 : 604800)+ // 1Day VS 7Days
												" Grid_"+gridType+
												" Weather_"+w);
								
							Market m = new Market();
							m.loadResult(f, seed, writer, 
											season == 0 ? "2014 12 31 23:50:00" : "2015 6 21 23:50:00", 
											duration == 0 ? 86400 : 604800,
											1, 1.0,
											gridType,
											w);
							writer.flush();
						}
					}	
				}
			}
		}
		
		writer.close();
	}
}
