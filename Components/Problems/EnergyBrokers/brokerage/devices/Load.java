package brokerage.devices;

import hems.Debugger;
import hems.devices.loads.DeviceStateModel;
import hems.devices.loads.OperationStats;
import hems.devices.loads.StateOperation;
import hems.devices.loads.UsageModel;
import hems.devices.loads.UsageModel.DevType;
import hems.devices.modelManager.RemoteManagerUanavailableException;
import hems.market.priceModel.PriceModel;
import hems.market.priceModel.StaticPriceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import brokerage.Market;
import brokerage.Market.SLA;

public class Load {
	protected String name;
	protected double credit;						// balance
	protected double expenses;						// total expenses from the trade
	protected double reimbursedCredit;
	
	protected UsageModel usageModel;				// appliance usage profile
	protected DeviceStateModel stateModel;			// power profile
	protected PriceModel utilityFunction;
	
	// load specific attributes
	protected boolean started;						// the device is operating
	protected boolean newStateToRun;				// the device concluded a state and has to run a new one
	
	protected boolean enabledToRun;
	protected boolean previousStatus;				// state of the device at the previous time instant (ON/OFF)
	
	protected int deviceState;						// current state
	protected long startTime;						// start time in milliseconds
	protected ArrayList<Integer> runs;				// time intervals already run
	protected long lastSuccessfulRunTime;			// time (in millisecs) the device could run for the last time (and make an offer for the current state)
	
	protected boolean decisionToOperateWasTaken;	// did the agent decide to start operate already?
	protected long timeDecisionToOperateWasTaken;	// time the decision to run was made (in millisecs)
	
	protected double temporaryReward;					// utility delivered by operating the device in a specific state, under the price sensitivity
	public ArrayList<OperationStats> operations;		// stats per each operation of the device
	protected OperationStats currentOperationStats;		// reference to the current operation -> to be able to modify it after adding it to the list
	
	protected Random randomGen;
	protected int[] demand;							// expected demand/duration for the load
	
	protected SimpleDateFormat format = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
	
	public Load(String name, 
			double credit, 
			UsageModel usageModel, 
			DeviceStateModel stateModel, 
			PriceModel utilityFunction,
			Random randomGen){
		
		this.randomGen = randomGen;
		
		this.name = name;
		this.expenses = 0.0;
		this.credit = credit;
		this.reimbursedCredit = 0.0;
		
		this.usageModel = usageModel;
		this.stateModel = stateModel;
		this.utilityFunction = utilityFunction;
		
		// initialize the load
		this.deviceState = 0;
		this.started = false;
		this.enabledToRun = false;
		
		this.temporaryReward = 0;
		this.operations = new ArrayList<OperationStats>();
		
		this.decisionToOperateWasTaken = false;
		this.timeDecisionToOperateWasTaken = 0;
		this.lastSuccessfulRunTime = 0;
		this.runs = new ArrayList<Integer>();
		
		this.timeLastRequest = 0;
		this.demand = new int[2];
	}
	
	public String getName(){
		return this.name;
	}
	
	public int[] getOffer(Calendar simulationTime, double[] priceVector){
		/*
		 * In dynamic memory allocation we normally distinguish in:
		 * - first fit: allocate the 1st hole big enough to contain the state
		 * - best fit: allocate the smallest hole big enough to contain the state
		 * - worst fit: allocate the biggest hole available
		 */
		return bestFit(simulationTime, priceVector);
	}
	
	/**
	 * Returns the smallest SLA able to contain the state
	 * @param simulationTime
	 * @param priceVector
	 * @return
	 */
	public int[] bestFit(Calendar simulationTime, double[] priceVector){
		int[] quantityVector = new int[priceVector.length];
		Arrays.fill(quantityVector, 0);
		
		// in case it needs to run the loads will try to buy the SLA which is big enough to fit its state entirely as well as be cheap enough to suit the utility price function
		if(demand[0] > 0){
			ArrayList<Integer> candidateSLAs = new ArrayList<Integer>();	// contains the indexes of all candidate SLAs
			
			for(int i=0; i<priceVector.length; i++){	// loop on all SLA prices
				if(priceVector[i] <= this.utilityFunction.getPrice(simulationTime) ){
					// the load could accept this SLA iff the duration is long enough (what's enough?)
					candidateSLAs.add(i);
				}
			}
			
			boolean allocated = false;
			Debugger.printLoadInfo("\t"+this.name+" needs "+demand[0]+"W for "+demand[1]+" secs (State_"+this.deviceState+")");
			
			for(int c = 0; c < candidateSLAs.size() && !allocated; c++){			// loop on all candidate SLAs starting from the smallest available (ASC)
				Debugger.println("\t\tchecking SLA lasting "+Market.offeredSLADurations[candidateSLAs.get(c)]+": ");
				if(demand[1] <= Market.offeredSLADurations[candidateSLAs.get(c)]	// get this SLA iff its duration is enough to contain the load state
						|| c == candidateSLAs.get(candidateSLAs.size() - 1) ){		// or if the current SLA is the biggest candidate possible
					quantityVector[c] = demand[0];		// return the state peak power as demand for the SLA at position c
					allocated = true;					// get out the loop
					Debugger.println("is long enough to contain the state or there are no other ones to check");
				}else{
					Debugger.println("too short to contain the state");
				}
			}
			//if(candidateSLAs.isEmpty()) System.out.println("\t"+this.name+": no candidate SLAs (utility="+this.utilityFunction.getPrice(simulationTime)+")");
		}
		
		return quantityVector;
	}
	
	protected long timeLastRequest;
	
	public int[] getExpectedDemand(Calendar simulationTime) throws RemoteManagerUanavailableException{
		// if we did not check the willingness at this time let's do it
		if(simulationTime.getTimeInMillis() - timeLastRequest > 0){
			
			timeLastRequest = simulationTime.getTimeInMillis();		// last time we checked the willingness of the load is NOW
			
			Calendar allocationTime = Calendar.getInstance(); 
			allocationTime.setTime(simulationTime.getTime()); 
			allocationTime.add(Calendar.SECOND, 1);
			
			demand = new int[2];
			demand[0] = 0;	// Peak power
			demand[1] = 0;	// duration
			
			if(started){ // the device is ON
				
				// check the duration of the device to manage the multiple states
				int elapsedSeconds = this.getElapsedSeconds(allocationTime);
				
				if(this.enabledToRun) 
					Debugger.printLoadInfo("\t"+this.name+" has been running for "+(this.enabledToRun?(elapsedSeconds-1):elapsedSeconds)
							+" secs (started:"+this.format.format(this.startTime)+"), now:"+this.format.format(simulationTime.getTime())
							+", alloc:"+this.format.format(allocationTime.getTime()));
				
				// the current state is over if running at the next instant would make the state last longer than it needs
				if(elapsedSeconds > stateModel.getDuration(deviceState)){ // > or >= ????
					// we have completed the current state
								
					if(this.stateModel.hasFurtherStates(this.deviceState)){
						// we have further states to run
						newStateToRun = true;
									
						// go to next state (+1 mod lenght)
						this.deviceState = this.stateModel.next(deviceState);
									
						// get the demand for the next state
						demand[0] = this.stateModel.getPeakPower(this.deviceState);
						demand[1] = this.stateModel.getDuration(this.deviceState);
						
						Debugger.printLoadInfo("\t"+this.name+": Another state should be run! now:"+this.format.format(simulationTime.getTime()));
					}else{
						// we do NOT have other states to run, we conclude the operation
						started = false;
						this.decisionToOperateWasTaken = false;
									
						// add a completed run for the current time interval
						this.usageModel.addConcludedOperationForCurrentInterval();
									
						// go to next state (+1 mod lenght)
						this.deviceState = this.stateModel.next(deviceState);
						
						demand[0] = 0;
						demand[1] = 0;
						Debugger.printLoadInfo("\t"+this.name+": No other states to run! Go OFF! now:"+this.format.format(simulationTime.getTime()));
					}
								
					// clean the runs for the current concluded state
					runs.clear();
								
				}else{
					// we need to complete the current state, possibly with no interruptions
					demand[0] = this.stateModel.getPeakPower(this.deviceState);
					demand[1] = this.stateModel.getDuration(this.deviceState) - (this.enabledToRun?(elapsedSeconds-1):elapsedSeconds);
					// avoid writing the following when not running, as it might be misleading
					if(this.enabledToRun) Debugger.printLoadInfo("\t"+this.name+": can complete State_"+this.deviceState+" in "+demand[1]+" secs (now:"+this.format.format(simulationTime.getTime())+")");
				}
			}else{
				// **** the device is OFF ****
				// check if the device wants to run but for some reason could not make a first offer
				if(decisionToOperateWasTaken){
					demand[0] = this.stateModel.getPeakPower(this.deviceState);
					demand[1] = this.stateModel.getDuration(this.deviceState);
					
					Debugger.printLoadInfo("\t\t"+this.name+": already decided to operate at "+this.format.format( timeDecisionToOperateWasTaken ));
					
				}else if( this.randomGen.nextDouble() > (1.0 - this.usageModel.getInstantaneousWillingnessToStart(allocationTime) )){ 						
					demand[0] = this.stateModel.getPeakPower(0);
					demand[1] = this.stateModel.getDuration(0);
					
					// keep track of the time the decision to start was taken
					this.timeDecisionToOperateWasTaken = simulationTime.getTimeInMillis();
					this.decisionToOperateWasTaken = true;
					
					Debugger.printLoadInfo("\t\t"+this.name+": decision to operate was taken at "+this.format.format(simulationTime.getTime())+" ("+simulationTime.getTimeInMillis()+")");
				}
			}
		
		}
		// otherwise we return what we already have in demand[]
		/*
		else{
			
		}*/
		
		return demand;
	}
	
	public double getActualDemand(Calendar simulationTime){
		return this.getStateRemainingDuration(simulationTime) >= 0 ? this.demand[0] : 0.0;
	}
	
	public int getStateRemainingDuration(Calendar simulationTime){
		int elapsedSeconds = this.getElapsedSeconds(simulationTime);
		return this.stateModel.getDuration(this.deviceState) - elapsedSeconds;//(this.enabledToRun?(elapsedSeconds-1):elapsedSeconds);
	}
	
	public void allowToRun(int allocatedAmount, Calendar simulationTime){
		//if(allocatedAmount > 0 && allocatedAmount < requested) System.out.println("\t"+this.format.format(simulationTime.getTime())+": "+this.name+" requested "+requested+" W and got "+allocatedAmount+" W");
		
		// manage the operation model
		this.previousStatus = this.enabledToRun;
		
		if(allocatedAmount > 0){
			this.enabledToRun = true;
			
		//	if(this.getElapsedSeconds(simulationTime) < this.stateModel.getDuration(this.deviceState) ){
			if(this.getStateRemainingDuration(simulationTime) > 0){
				this.temporaryReward += allocatedAmount * this.utilityFunction.getPrice(simulationTime) / (1000*3600);
			}
			/*
			else{
				System.out.println("Running within a SLA longer than necessary!!");
			}*/
			
		}else{
			this.enabledToRun = false;
		}
		
		/*
		// manage the operation model
		this.previousStatus = this.enabledToRun;
		
		if(allocatedAmount > 0){
			this.enabledToRun = true;
			this.temporaryReward += allocatedAmount * this.utilityFunction.getPrice(simulationTime) / (1000*3600);
		}else{
			this.enabledToRun = false;
		}
		*/
	}
	
	public int getElapsedSeconds(Calendar simulationTime){
		// check the duration of the device to manage the multiple states
		int elapsedSeconds = 0;
		// include the current state in case we are running
		if(this.enabledToRun) elapsedSeconds = (int) ((simulationTime.getTimeInMillis() - this.startTime) / 1000);
		// check how long the device has been already running
		for(Integer s : runs) elapsedSeconds += s;
		return elapsedSeconds;
	}
	
	public void updateStatus(Calendar currentTime){
		this.usageModel.updateInterval(currentTime);
		
		// ---- Manage the device status ----
		if(this.enabledToRun){	
					
			if(started){
				// keep the time going
						
				// check if the current state is a new one
				if(this.newStateToRun){
					
					// add uptime, even though we might have no interruptions we need to track the uptime for stats
					int run_time = (int) ((this.lastSuccessfulRunTime - this.startTime) / 1000);
					// there can exist run_time == 0 because the last instant of a state could be run after a pause
					// which implies that startime = t and lastsuccessfulruntime = t, thus lastsuccessfulruntime - starttime == 0
					//if(run_time > 0) {
						this.currentOperationStats.addUptime(run_time);	// S_t+1 might be run right after S_t so we would have
					//}
					
					// *** State_n -> State_n+1 ***
					this.startTime = currentTime.getTimeInMillis();
					this.newStateToRun = false;
					
					this.currentOperationStats.concludeStateOperation(this.lastSuccessfulRunTime, temporaryReward);
					
					// --- create a new operation information object for the current state ---
					int delayedStart = ((int) ((currentTime.getTimeInMillis() - this.lastSuccessfulRunTime) / 1000)) -1;
							
					temporaryReward = 0;
							
					this.currentOperationStats.addStateOperation(this.lastSuccessfulRunTime, 
																this.deviceState,
																delayedStart,
																this.startTime);
	
					Debugger.printLoadInfo("\t"+this.name+": ** Running new state "+this.deviceState+" at "+this.format.format(currentTime.getTime())+" (delay: "+delayedStart+"secs)");
					
				}else if(!previousStatus){
					// *** PAUSE -> RUNNING ***
					Debugger.printLoadInfo("\t"+this.name+": ** Update: PAUSE -> Running ** at "+this.format.format(currentTime.getTime()));
					
					this.startTime = currentTime.getTimeInMillis();
						
					// compute the amount of time the device was paused while running an atomic state
					int pausedSeconds = ((int) ((currentTime.getTimeInMillis() - this.lastSuccessfulRunTime) / 1000)) -1; 
					// -1 is needed because the time is taken between two consecutive running times
							
					// update the operation information object
					this.currentOperationStats.addInterruption(pausedSeconds);	// the delay is added for the current state
				}
				/*
				else{
					System.out.println("intra-SLA");
				}*/
						
			}else{
				// *** OFF -> ON ***
				// the device was not running previously
				this.startTime = currentTime.getTimeInMillis();
				started = true;		// the device has started running,
						
				// compute the time the device has waited before starting a state (which might be the first start, or one of the following states)
				int delayedStart = ((int) ((currentTime.getTimeInMillis() - this.timeDecisionToOperateWasTaken) / 1000)) -1;
				// we need -1 because the time between the offer was made and accepted/run is always at least 1
						
				Debugger.printLoadInfo("\t"+this.name+": ** The device has been waiting "+delayedStart+
										" before starting (now:"+this.format.format(currentTime.getTime())+
										", firstOffer was:"+this.format.format(new Date(this.timeDecisionToOperateWasTaken))+")");
						
				// create a new operation information object for the current state
				this.currentOperationStats = new OperationStats();
				// add this operation to the list of all operations of the device
				this.operations.add(currentOperationStats);
				this.currentOperationStats.addStateOperation(timeDecisionToOperateWasTaken,
															this.deviceState, 
															delayedStart,
															this.startTime);
			}
			
			// last successful running time was now
			if(this.getStateRemainingDuration(currentTime) >= 0){		// skip this if we are inside a SLA but the load does not need to run
				lastSuccessfulRunTime = currentTime.getTimeInMillis();	// we put it here because we used the current value to compute the pausedSeconds
			}
		}else{
			// check if the device was started and was running at the previous time instant
			// but also make sure that the device is not in between two states, as this would mess up the counting of the elapsed time
			if(started && previousStatus && !this.newStateToRun){
				// *** Running -> Paused ***
				// the device did not win the contention for this state
				// its running interval was stopped at the current time,
				// we add the sub-interval to the runs arraylist to keep track of the whole duration
				int run_time = (int) ((currentTime.getTimeInMillis() - this.startTime) / 1000);
				runs.add(run_time);
				this.currentOperationStats.addUptime(run_time);
				Debugger.printLoadInfo("\t"+this.name+": Running->Paused (at)"+this.format.format(currentTime.getTime()));
			}else if(!started && previousStatus){
				// *** ON -> OFF ***
				Debugger.printLoadInfo("\t"+this.name+": **STOP** "+this.format.format(currentTime.getTime()));
				
				int run_time = (int) ((this.lastSuccessfulRunTime - this.startTime) / 1000);
				//if(run_time == 0) System.out.println(this.lastSuccessfulRunTime+", "+this.startTime);
				this.currentOperationStats.addUptime(run_time);		// add uptime, even though we might have no interruptions we need to track the uptime for stats
				
				// the device was previously ON and it is not started anymore, which happens only when the state is concluded
				this.currentOperationStats.concludeStateOperation(this.lastSuccessfulRunTime, temporaryReward);
				temporaryReward = 0;
			}
			
			// else the device was not yet started
			// or it is in between two states and its run time was already counted previously
		}
	}
	
	public void charge(double money){
		credit -= money;
		expenses += money;
	}
	
	public double getCredit(){
		return credit;
	}
	
	public double getExpenses(){
		return expenses;
	}
	
	public double getReimbursedCredit(){
		return reimbursedCredit;
	}
	
	/**
	 * To be used when the load has sold energy/power
	 * @param money
	 */
	public void getPaid(double money){
		credit += money;
	}
	
	/**
	 * To be used when a SLA can't be fulfilled, load's costs are reduced and consequently its credit
	 * @param money
	 */
	public void reimburse(double amount, int duration, double unitPrice){
		double money = (amount / 1000.0) * (unitPrice / 3600.0) * duration;
		//credit += money;
		//expenses -= money;
		this.reimbursedCredit += money;
	}
	
	public static void main(String [] args) throws Exception{
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
		Calendar simulationTime = Calendar.getInstance();
		simulationTime.setTime(format.parse("2015 01 01 06:35:00"));
		
		Calendar simulationBeginning = Calendar.getInstance(); 
		simulationBeginning.setTime(simulationTime.getTime());
		
		int simulationDuration = 10000;
		
		int allocationTimeslotLenght = 1;
		double limitPrice = 1.0;
		
		//Random randomGen = getRandom();
		Random randomGen = new Random(1234);
		
		DeviceStateModel stateModel = new DeviceStateModel();
		stateModel.addState(300, 1);
		stateModel.addState(300, 2);
		stateModel.addState(300, 1);
		stateModel.addState(300, 2);
		stateModel.addState(800, 20, 5);
		stateModel.addState(50, 50, 5);
		UsageModel usageModel = new UsageModel();
		usageModel.buildRandom(0.6, 0.5);
		Load l = new Load("test", 10.0, usageModel, stateModel, new StaticPriceModel(0.2), randomGen);
		
		SLA sla = null;
		Market m = new Market();
		
		while( simulationTime.getTimeInMillis()/1000 - simulationBeginning.getTimeInMillis()/1000 <= simulationDuration ){
			System.out.println(format.format(simulationTime.getTime()));
			
			
			
			if(sla != null){
				
				// allow the load to operate
				sla.client.allowToRun(sla.amount, simulationTime);
				
				if(sla.duration <= 0){ 
					sla = null;
					Debugger.printLoadInfo("\tRemoving SLA");
				}else sla.updateSLAduration();
				
			}else{
				l.allowToRun(0, simulationTime);
			}
			
			
			
			
			
			l.updateStatus(simulationTime);
			
			if(sla == null){
				int[] demand = l.getExpectedDemand(simulationTime);
				
				if(demand[0] > 0 && demand[1] > 0){
					Debugger.printLoadInfo("\t"+l.getName()+" wants "+demand[0]+"W for "+demand[1]+" sec ");
					
					double test = randomGen.nextDouble();
					System.out.println("\t\t"+test+" >0.4?");
					if(test  > 0.5){
						sla = m.new SLA(l, demand[0], demand[1], 0.5);
						Debugger.printLoadInfo("\tAdded agreements for load "+l.getName()+" for "+demand[0]+"W for "+demand[1]+" sec ");
					}
				}
			}
			/*
			if(sla != null){
				sla.updateSLAduration();
				// allow the load to operate
				sla.client.allowToRun(sla.amount, simulationTime);
				
				if(sla.duration == 0){
					sla = null;
					Debugger.printLoadInfo("\tRemoving SLA");
				}
			}else{
				l.allowToRun(0, simulationTime);
			}*/
			
			
			simulationTime.add(Calendar.SECOND, 1);
		}
		
		for(OperationStats o : l.operations){
			for(StateOperation s : o.getAllStatesStats()){
				System.out.println( "State_"+s.deviceState + ", "+
									format.format(s.firstOfferTime)+", "+
									format.format(s.stateBeginningTime)+" (del:"+s.startDelay+"), dur:"+s.operationDuration
									+", up:"+s.uptime
									+", in:"+
									s.interruptions+",");
			}
		}
	}
}
