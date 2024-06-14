package light;

import java.awt.AWTException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import javax.swing.SwingWorker;

import net.jodk.lang.FastMath;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import utils.ScreenCapture;

/**
 * A simple problem where a robot with light sensors should find the light source
 * 
 * @author Istvan Fehervari, Sergii Zhevzhyk
 *
 */
public class Light extends AbstractSingleProblem {
	
	public AbstractRepresentation nnetwork;
	public boolean withmonitor = false;
	public boolean withpause; // pauses to make games watchable
	/** The maximum time allowed for the drones to accomplish the mission */
	private int MAXTIME;
	/** Number of times the simulation is executed */
	private int EVALUATIONNUMBER;
	/** Display will be treated as a grid of square cells with this size */
	private double DISPLAY_GRID_SIZE;
	/** How should the fitness be calculated. */
	private FitnessCalculationMethod FITNESS_CALCULATION_TYPE;
	/** Real time between redrawing visualization */
	public static final int DISPLAYWAIT = 100;
	public Robot robot;
	public int currentstep;
	public boolean isRunning = false;
	private boolean saveMovie = false;
	public int maxsteps;
	protected DisplayWorker sw;
	private SimDisplay display;
	public SimServer simserver;
	private boolean logging = true;
	private BufferedWriter out;
	
	private void Log(String s) {
		try {
			out.write(s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Size of the square cells for Light grid
	 */
	public double getDisplayGridSize () {
		return DISPLAY_GRID_SIZE;
	}
	
	private void loadParameters() {
		MAXTIME = Integer.parseInt(getProperties().get("simulationtime").getValue());
		EVALUATIONNUMBER = Integer.parseInt(getProperties().get("evalnumber").getValue());
		
		// the new parameters are introduced later, so if missing from xml, 
		// just use default
		try {
			DISPLAY_GRID_SIZE = Double.parseDouble( getProperties().get("gridcellsize").getValue() );
			FITNESS_CALCULATION_TYPE = parseFitnessType( getProperties().get("fitnesscalculation").getValue() );
		} catch (Exception ex) {
			DISPLAY_GRID_SIZE = 20.0f;
			FITNESS_CALCULATION_TYPE = FitnessCalculationMethod.TIME_POSITION;
		}
	}

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		if (logging) {
			try {
				out = new BufferedWriter(new FileWriter("light-log.txt", true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		loadParameters();
		
		this.nnetwork = candidate;
		
		withmonitor = false;
		withpause = false;
		
		double res = runSimulation();
		
		if (logging) {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		loadParameters();
		this.nnetwork = candidate;
		
		display = new SimDisplay(this);
	}
	
	private double runSimulation () {
		if (withmonitor) {
			EVALUATIONNUMBER = 1;
		}
		
		int success = 0;
		double fitness = 0;
		if (this.withmonitor) isRunning = true;
		
		//Iterate through
		for (int n = 0; n < EVALUATIONNUMBER; n++) {
			//create robot
			this.robot = new Robot(new RobotController(nnetwork,this));
			robot.setBodyDirection(generator.nextInt(360));
		
			//initialize time
			maxsteps = MAXTIME / (int)SimServer.STEPLENGTH;
			// create new simulation environment
			if (withmonitor) this.simserver = new SimServer(this,Long.parseLong(display.seedField.getText()));

			else {
				this.simserver = new SimServer(this, (1234+n));
			}
			
			this.simserver.setGridCellSize(DISPLAY_GRID_SIZE);

			if (withmonitor) {
				sw.setProgressToPublish(currentstep);
			}
			if (saveMovie) saveFrame();		
			/* Simulator is ready */
			
			try {
				calculateStep();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (withpause)
				pause(DISPLAYWAIT); //pause 100 it was
			for (currentstep = 0; currentstep < maxsteps; currentstep++) {
				
				if (saveMovie) saveFrame();
				calculateStep();
				if (withpause)
					pause(DISPLAYWAIT); //pause 100 it was
				if (simserver.succeeded) {
					success ++;
					break;
				}
				if ((withmonitor) && (isRunning == false)) break;
			}
			
			//TODO: add somehow an image generation here,
			//      so that I do not have to run evaluation of recorded result to find it out
			
			// calculate fitness
			switch (FITNESS_CALCULATION_TYPE) {
			case TIME_POSITION:
				/* old implementation
				 	fitness += 0.7 * calcTimeFitness(maxsteps, currentstep) + 0.3 * calcDistanceFitness();
				 */
				fitness += ((maxsteps != currentstep) ?  0.3 : 0) + 0.4 * calcTimeFitness(maxsteps, currentstep) 
						+ 0.3 * calcDistanceFitness();
				break;
			case TIME_POSITION_TRACK:
				/* old implementation
				  fitness += 0.5 * calcTimeFitness(maxsteps, currentstep)
						+ 0.3 * calcDistanceFitness()
						+ 0.2 * calcAreaCoveredFitness(currentstep);
						*/
				fitness += 0.35 * calcTimeFitness(maxsteps, currentstep) + ((maxsteps != currentstep) ?  0.3 : 0) 
						+ 0.25 * calcDistanceFitness()
						+ 0.1 * calcAreaCoveredFitness(currentstep);
				break;
			case TIME_POSITION_WEIGHTEDTRACK:
				fitness += 0.5 * calcTimeFitness(maxsteps, currentstep)
						+ 0.3 * calcDistanceFitness()
						+ 0.2 * calcWeightedAreaCoveredFitness(currentstep);
				break;
			}

			if (withmonitor)
				System.out.println("Fitness: " + fitness);
		}
		if (withpause)
			display.startButton.setEnabled(true);
		isRunning = false;
		
		if (logging) {
			// make sure that the decimal separator is a dot
			DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
			mySymbols.setDecimalSeparator('.');
			DecimalFormat fm = new DecimalFormat("0.0000",mySymbols);  
			Log(fm.format(fitness / EVALUATIONNUMBER) + " " +  ((double)success)/EVALUATIONNUMBER);
		}
		
		return fitness / EVALUATIONNUMBER;		
	}

	/**
	 * A fitness which motivates robot to "catch" his target.
	 * @param maxsteps
	 * @param currentstep
	 * @return returns a fitness value between 1 (best case - impossible to reach) and 0 (worst).
	 * If the target hasn't been reached, the method returns 0
	 */
	private static double calcTimeFitness(int maxsteps, int currentstep) {
		// old implementation
		// return (maxsteps - currentstep) / (double) maxsteps;
		double result =  FastMath.exp((maxsteps - currentstep) / (double) maxsteps) / Math.E;
		return result;
	}

	/**
	 * This fitness are used for moving robot closer to his target.
	 * @return returns a value between 1 (best case) and 0 (worst)
	 */
	private double calcDistanceFitness() {
		double distanceFromLight = SimServer.getDistance(robot.getPosition(),
				simserver.targetlocation);

		if (distanceFromLight > Robot.LIGHT_SENSOR_RANGE)
			return 0.0;
		
		/*
		 old impl
		 return (Robot.LIGHT_SENSOR_RANGE - distanceFromLight)
				/ Robot.LIGHT_SENSOR_RANGE;
		 */
		
		double result = FastMath.exp((Robot.LIGHT_SENSOR_RANGE - distanceFromLight)
				/ Robot.LIGHT_SENSOR_RANGE) / Math.E;
		
		return result;
	}

	/**
	 * A fitness for exploring purposes of new territories.  
	 * @param maxsteps
	 * @return returns a value between 1 (best case) and 0 (worst).
	 */	
	private double calcAreaCoveredFitness(int maxsteps) {
		// TODO : doesn't work
		//double maxLengthCovered= Robot.getMaxSpeed() * maxsteps * SimServer.STEPLENGTH/1000.0;
		//double  maxCellsCovered = maxLengthCovered / simserver.getGridCellSize();
		//double result = simserver.getNumberOfCellsVisited() / maxCellsCovered;
		//return result;
		
		int border = 20;
		double maxCells = ((getDisplayWidth() - border) /(double)(SimServer.GRIDCELLSIZE)) * 
				((getDisplayHeight() - border) /(double)(SimServer.GRIDCELLSIZE)); 
		return FastMath.exp (simserver.getNumberOfCellsVisited() / maxCells) / Math.E;
	}	

	//returns a value between 1 (best case) and 0 (worst)
	//currently, the value of 1 is never reached due to weights <= 1
	private double calcWeightedAreaCoveredFitness(int maxsteps) {
		double maxLengthCovered= Robot.getMaxSpeed() * maxsteps * SimServer.STEPLENGTH/1000.0;
		double  maxCellsCovered = maxLengthCovered / simserver.getGridCellSize();
		
		return simserver.getNumberOfCellsVisitedWeighted() / maxCellsCovered;
	}	
		
	public void calculateStep() {
		//send sensor data
		sendSensordata();
		
		// get new intentions
		robot.getController().postInfo();
		
		//calculate changes
		simserver.calculateAll();

		if (withmonitor) sw.setProgressToPublish(currentstep);
		
	}
	
	/** Sends sensor data to the drones */
	public void sendSensordata() {
		// reset controllers
		robot.getController().preInfo();
		
		//Add visual information
		simserver.getVisuals(robot);
	}
	
	private void saveFrame() {
		try {
			ScreenCapture.createImage(display, "./Images/Frame_"+fix(currentstep)+".bmp");
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class DisplayWorker extends SwingWorker<Void,Integer> {

		@Override
		protected Void doInBackground() throws Exception {
			//loadParameters();	
			withmonitor = true;
			withpause = true;
			runSimulation();
			return null;
		}
		
		public void setProgressToPublish (int p) {
			publish(p);
	    }
		
	    protected void process(List<Integer> results) {
	    	display.updateDisplay();
	    }
		
	}
	
	private static String fix(int num) {
		String ret = Integer.toString(num);
		if (num < 10) ret = "00"+ret;
		else if (num < 100) ret = "0"+ret;
		return ret;
	}
	
	protected synchronized void pause (int ms) {
	    try {
	      this.wait(ms);
	    } catch (InterruptedException ex) {
	    }
	}
	
	public void runBackground() {
		sw = new DisplayWorker();
		sw.execute();
	}


	private static FitnessCalculationMethod parseFitnessType(String name) {
		// could use valueOf too, but that is not enough flexible
		return FitnessCalculationMethod.fromString(name);
	}

	public int getDisplayWidth(){
		// not the nicest.. but in case not in replaywithvisualization
		// (display not initialized) just give back some default
		if(display != null)
			return display.getWidth();

		return SimDisplay.DEFAULT_DISPLAY_WIDTH;
	}
	
	public int getDisplayHeight(){
		// not the nicest.. but in case not in replaywithvisualization
		// (display not initialized) just give back some default
		if(display != null)
			return display.getHeight();
		
		return SimDisplay.DEFAULT_DISPLAY_HEIGHT;
	}
	
	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}
}
