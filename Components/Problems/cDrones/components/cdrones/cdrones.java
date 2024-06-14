package components.cdrones;


import java.awt.AWTException;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.jodk.lang.FastMath;
import utils.ScreenCapture;

import components.cdrones.blobGenerator.BlobGenerator;
import components.cdrones.blobGenerator.BlobMap;
import components.controllers.CoverageController;
import components.controllers.DiscreteOutputEvolvedController;
import components.controllers.EvolvedController;
import components.controllers.RandomDirectionController;
import components.controllers.RandomWalkController;

import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.XMLFieldEntry;

public class cdrones extends AbstractSingleProblem {

	/** Number of drones used in the simulation */
	public int DRONENUMBER;
	/** The maximum time allowed for the drones to accomplish the mission */
	public static int MAXTIME;
	/** Number of times the simulation is executed */
	public static int EVALUATIONNUMBER;
	/**
	 * Use advanced controller which can detect the proximity of all other
	 * drones
	 */
	public static ControllerType CONTROLLERTYPE;
	/** Number of random input neurons */
	public static int RANDOMINPUTS;
	/** Basestep for the advanced controller */
	public static int BASETEP;

	/** Percentage of the field blocked by obstacles */
	public static float OBSTACLEPERCENTAGE;

	public static boolean RANDOMPOSITIONSTART;

	/** Real time between redrawing visualization */
	static public int DISPLAYWAIT = 10;

	public int discretize = DronesServer.DEFAULT_MAP_RESOLUTION;// 10

	/** Array containing the map setups for all simulation runs */
	public static ArrayList<BlobMapWithPositions> maps = new ArrayList<BlobMapWithPositions>();

	public static boolean allowCommunications = true;

	public static double extreme_output_penalty = 0;

	public static boolean commFitness = true;

	public AbstractRepresentation nnetwork;
	public DronesServer dronesserver;
	public boolean withmonitor = false;
	private boolean saveMovie = false;
	public int stepnumber;
	public int aktStep;
	protected DisplayWorker sw;
	private DronesDisplay display;
	public boolean isRunning = false;
	
	public static int NUMBEROFDIGITS = 0;  

	/** Array containing the drones */
	public Drone[] drones;

	// Adjust requirement
	@SuppressWarnings("incomplete-switch")
	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {

		XMLFieldEntry pctrt = properties.get("ControllerType");
		ControllerType controller = ControllerType.valueOf(pctrt.getValue());
		
		XMLFieldEntry inputn = requirements.get("inputnumber");
		XMLFieldEntry outputn = requirements.get("outputnumber");

		switch (controller) {
		case EVOLVEDCONTROLLER:			
			// 4 wall, 4 radio, 2 position, 2 direction
			inputn.setValue(Integer.toString(4 + 4 + 2 + 2));
			// 2 motor, 1 radio
			outputn.setValue(Integer.toString(3));
			break;
			
		case DISCRETEOUTPUTEVOLVEDCONTROLLER:
			// 4 wall, 4 radio, 2 position, 2 direction
			inputn.setValue(Integer.toString(4 + 4 + 2 + 2));
			
			XMLFieldEntry numdigprop = properties.get("numberofdigits");
			if (numdigprop == null)
				throw new Error ("Number of digits not defined!");
			
			NUMBEROFDIGITS = Integer.parseInt(numdigprop.getValue());
			
			// digits + radio
			outputn.setValue(Integer.toString(NUMBEROFDIGITS+1));
			break;
		}

		return requirements;
	}

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		loadParameters();

		this.nnetwork = candidate;

		// get normal fitness
		double fivefitness = runSimulation(false, 0);
		return fivefitness;
		// get fitness with one drone
		// DRONENUMBER = 1;
		// double onefitness = runSimulation(false);

		// return (fivefitness+onefitness)/2.0;
	}

	public void loadParameters() {
		Hashtable<String, XMLFieldEntry> properties = getProperties();
		DRONENUMBER = Integer.parseInt(properties.get("number_of_drones")
				.getValue());
		MAXTIME = Integer.parseInt(properties.get("cutofftime").getValue());
		OBSTACLEPERCENTAGE = Float.parseFloat(properties.get(
				"obstaclepercentage").getValue());
		EVALUATIONNUMBER = Integer.parseInt(properties.get("evalnumber")
				.getValue());

		RANDOMINPUTS = Integer.parseInt(properties.get("numberofrandominputs")
				.getValue());
		RANDOMPOSITIONSTART = Boolean.parseBoolean(properties
				.get("randomstart").getValue());
		BASETEP = Integer.parseInt(properties.get("basestep").getValue());

		int field_x = DronesServer.DEFAULT_FIELDDIM_X;
		int field_y = DronesServer.DEFAULT_FIELDDIM_Y;
		XMLFieldEntry fieldxp = properties.get("fieldWidthX");
		if (fieldxp != null)
			field_x = Integer.parseInt(fieldxp.getValue());

		XMLFieldEntry fieldyp = properties.get("fieldWidthY");
		if (fieldyp != null)
			field_y = Integer.parseInt(fieldyp.getValue());

		DronesServer.FIELDDIM = new Point(field_x, field_y);

		XMLFieldEntry resolutionp = properties.get("resolution");
		if (resolutionp != null)
			discretize = Integer.parseInt(resolutionp.getValue());
		
		//load number of digits
		XMLFieldEntry numdigprop = properties.get("numberofdigits");
		if (numdigprop != null) {
			NUMBEROFDIGITS = Integer.parseInt(numdigprop.getValue());
		}
			
		// load fitness type
		XMLFieldEntry cfitprop = properties.get("commFitness");
		if (cfitprop == null)
			commFitness = false;
		else {
			commFitness = Boolean.parseBoolean(cfitprop.getValue());
		}

		// load controller type
		try {
			CONTROLLERTYPE = ControllerType.valueOf(getProperties().get(
					"ControllerType").getValue());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		synchronized (maps) {
			// check map storage
			if (maps.size() < EVALUATIONNUMBER) {
				reCreateMaps();
			}
		}
	}

	public void reCreateMaps() {
		// long begin = System.currentTimeMillis();
		maps.clear();
		BlobGenerator bgen = new BlobGenerator(generator);

		// System.out.print("Generating maps... ");

		// need to convert it down! (using discretize)
		
		int seeds = 10;
		
		if (OBSTACLEPERCENTAGE > 0.2)
			seeds = 5;

		for (int i = 0; i < EVALUATIONNUMBER; i++) {
			BlobMap bm = bgen.generate(DronesServer.FIELDDIM.x / discretize,
					DronesServer.FIELDDIM.y / discretize, seeds,
					OBSTACLEPERCENTAGE, true);
			BlobMapWithPositions bmp = new BlobMapWithPositions(bm);
			bmp.generateStartingPoints(DRONENUMBER, RANDOMPOSITIONSTART);
			maps.add(bmp);
		}
		// long end = System.currentTimeMillis();

		// System.out.println("done in "+(end-begin)+" ms");
	}

	public double runSimulation(boolean with_monitor, int mapnum) {
		this.withmonitor = with_monitor;
		int evNumber;
		if (withmonitor) {
			evNumber = 1;
		}
		else
		{
			evNumber = EVALUATIONNUMBER;
		}

		this.drones = new Drone[DRONENUMBER];

		if (this.withmonitor)
			isRunning = true;

		double fitness = 0;

		// Iterate through
		for (int n = 0; n < evNumber; n++) {
			if (!withmonitor)
				mapnum = n;

			if (commFitness) {
				withmonitor = false;// run nocomm silently
				allowCommunications = false;
				double f_nocomm = runEval(mapnum);

				if (with_monitor) {
					System.out.println("Fitness without communication: "
							+ f_nocomm);
					withmonitor = true;
				}
				allowCommunications = true;
				double f_comm = runEval(mapnum);

				if (f_comm > f_nocomm) {
					fitness += f_comm * 1.2;// add 20% bonus if it is better
				} else {
					fitness += f_nocomm;
				}
			} else {
				fitness += runEval(mapnum);
			}

		}
		if (withmonitor)
			display.startButton.setEnabled(true);

		isRunning = false;
		return fitness / evNumber;

	}

	/**
	 * @param withmonitor
	 * @param mapnum
	 * @param dfit
	 * @param n
	 * @return
	 */
	private double runEval(int mapnum) {

		double dfit = 0;

		for (int i = 0; i < DRONENUMBER; i++) {

			switch (CONTROLLERTYPE) {
			case RANDOMWALKCONTROLLER:
				drones[i] = new Drone(new RandomWalkController(generator), i);
				break;
			case RANDOMDIRECTIONCONTROLLER:
				drones[i] = new Drone(new RandomDirectionController(generator),
						i);
				break;
			case EVOLVEDCONTROLLER:
				drones[i] = new Drone(new EvolvedController(nnetwork.clone(),
						this), i);
				break;
			case DISCRETEOUTPUTEVOLVEDCONTROLLER:
				drones[i] = new Drone(new DiscreteOutputEvolvedController(nnetwork.clone(),
						this), i);
				break;
			case COVERAGEBASEDCONTROLLER:
				drones[i] = new Drone(new CoverageController(generator), i);
				break;
			}
		}

		// initialize time
		stepnumber = MAXTIME;

		// create new simulation environment
		//if (withmonitor)
		//	this.dronesserver = new DronesServer(this, maps.get(mapnum));
		//else
			this.dronesserver = new DronesServer(this, maps.get(mapnum));

		if (saveMovie)
			saveFrame();

		calculateStep();

		if (withmonitor) {
			sw.setProgressToPublish(aktStep);
			pause(DISPLAYWAIT);
		}

		for (aktStep = 0; aktStep < stepnumber; aktStep++) {

			if (saveMovie)
				saveFrame();

			calculateStep();

			if (withmonitor)
				pause(DISPLAYWAIT);

			if ((withmonitor) && (isRunning == false))
				break;
		}

		dfit += dronesserver.getCoveragePercentage();

		// TODO: Wil's code - beware!
		if (extreme_output_penalty != 0) {
			double fitnessPenalty = 0.0;
			for (int i = 0; i < DRONENUMBER; i++) {
				EvolvedController controller = (EvolvedController) drones[i]
						.getController();
				fitnessPenalty += controller.getExtremeFrequency();
			}
			dfit -= extreme_output_penalty * fitnessPenalty / DRONENUMBER;
		}

		// TODO: end of Wil's code

		if (withmonitor) {
			if (commFitness) {
				System.out.println("Fitness with communication: " + dfit);
			} else
				System.out.println("Average covered area: " + (dfit * 100)
						+ "%");

		}

		return dfit;
	}

	public void calculateStep() {
		// send sensor data
		sendSensordata();

		// get new intentions
		for (int k = 0; k < DRONENUMBER; k++) {
			drones[k].getController().postInfo();
		}

		// calculate changes
		dronesserver.calculateAll();
		if (withmonitor)
			sw.setProgressToPublish(aktStep);

		// calculate visited fields from drones positions and their range
		for (int k = 0; k < DRONENUMBER; k++) {
			Point2D.Double dloc = drones[k].getPosition();
			int leftx = (int) dloc.x - Drone.DETECTION_RANGE;
			if (leftx < 0)
				leftx = 0;

			int rightx = (int) dloc.x + Drone.DETECTION_RANGE;
			if (rightx > dronesserver.fieldgrid.length - 1)
				rightx = dronesserver.fieldgrid.length - 1;

			int topy = (int) dloc.y + Drone.DETECTION_RANGE;
			if (topy > dronesserver.fieldgrid[0].length - 1)
				topy = dronesserver.fieldgrid[0].length - 1;

			int bottomy = (int) dloc.y - Drone.DETECTION_RANGE;
			if (bottomy < 0)
				bottomy = 0;

			for (int x = leftx; x <= rightx; x++) {
				for (int y = bottomy; y <= topy; y++) {
					double dist = DronesServer.getDistance(dloc,
							new Point2D.Double(x + 0.5, y + 0.5));
					if ((dist <= Drone.DETECTION_RANGE)
							&& (dronesserver.fieldgrid[x][dronesserver.fieldgrid[0].length
									- y - 1] != DronesServer.BLOCKED))
						dronesserver.visitZone(new Point(x,
								dronesserver.fieldgrid[0].length - y - 1));
				}
			}
		}

	}

	/** Sends sensor data to the drones */
	public void sendSensordata() {
		// reset controllers
		for (int k = 0; k < DRONENUMBER; k++) {
			drones[k].getController().preInfo();
		}
		// Add visual information
		for (int k = 0; k < DRONENUMBER; k++) {
			dronesserver.getVisuals(drones[k]);
		}
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		loadParameters();
		this.nnetwork = candidate;
		this.drones = new Drone[DRONENUMBER];

		display = new DronesDisplay(this);
	}

	public void runBackground(int mapnum) {
		sw = new DisplayWorker(mapnum);
		sw.execute();

	}

	private void saveFrame() {
		try {
			ScreenCapture.createImage(display, "d://Works//demesos//Sourcecode//Frevo//Images//drones_8/Frame_" + fix(aktStep)
					+ ".bmp");
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String fix(int num) {
		String ret = Integer.toString(num);
		if (num < 10)
			ret = "000" + ret;
		else if (num < 100)
			ret = "00" + ret;
		else if (num < 1000)
			ret = "0" + ret;
		return ret;
	}

	class BlobMapWithPositions {
		ArrayList<Point> positions = new ArrayList<Point>();

		public static final int BLOCKED = 1;
		public static final int FREE = 0;
		public int bmWIDTH, bmHEIGHT;
		private int blocked = -1;

		// public ArrayList<Blob> blobs = new ArrayList<Blob>();

		public int map[][];

		public BlobMapWithPositions(BlobMap bm) {
			this.bmWIDTH = bm.WIDTH;
			this.bmHEIGHT = bm.HEIGHT;

			map = new int[DronesServer.FIELDDIM.x][DronesServer.FIELDDIM.y];
			// convert low-res bm map to normal one
			for (int x = 0; x < bm.map.length; x++) {
				for (int y = 0; y < bm.map[0].length; y++) {
					if (bm.map[x][y] >= BLOCKED) {
						for (int xi = 0; xi < discretize; xi++) {
							for (int yi = 0; yi < discretize; yi++) {
								map[x * discretize + xi][y * discretize + yi] = bm.map[x][y];
							}
						}
					}
				}
			}

			// this.blobs = bm.blobs;
		}

		public int getNumberofBlocked() {
			if (blocked == -1)
				calculateBlocked();
			return blocked;
		}

		private void calculateBlocked() {
			blocked = 0;
			for (int y = 0; y < map.length; y++) {
				for (int x = 0; x < map[0].length; x++) {
					if (map[x][y] == BLOCKED)
						blocked++;
				}
			}
		}

		public void generateStartingPoints(int dronenum,
				boolean isRandomPositions) {
			ArrayList<Point> plist = new ArrayList<Point>();

			if (isRandomPositions) {
				for (int i = 0; i < dronenum; i++) {
					boolean placed = false;
					while (!placed) {
						int xd = generator.nextInt(DronesServer.FIELDDIM.x - 2) + 1;
						int yd = generator.nextInt(DronesServer.FIELDDIM.y - 2) + 1;
						if (this.map[xd][DronesServer.FIELDDIM.y - yd] < BLOCKED) {
							plist.add(new Point(xd, yd));
							placed = true;
						}
					}
				}
			} else {// places drones in a circle of (0,0)
				// randomize central point
				/*
				 * int x, y; int radius = 15; while (true) { x =
				 * generator.nextInt(DronesServer.FIELDDIM.x - (2 * radius)) +
				 * radius; y = generator.nextInt(DronesServer.FIELDDIM.y - (2 *
				 * radius)) + radius;
				 * 
				 * if ((this.map[x][y] < BLOCKED) && (this.map[x + radius][y] <
				 * BLOCKED) && (this.map[x][y + radius] < BLOCKED) &&
				 * (this.map[x - radius][y] < BLOCKED) && (this.map[x][y -
				 * radius] < BLOCKED) && (this.map[x + radius][y + radius] <
				 * BLOCKED) && (this.map[x + radius][y - radius] < BLOCKED) &&
				 * (this.map[x - radius][y - radius] < BLOCKED) && (this.map[x -
				 * radius][y + radius] < BLOCKED)) { break; } }
				 */

				Point centralpoint = new Point(15, 15);
				int radius = 5;
				// place drones
				double angle = 360.0 / dronenum;
				for (int i = 0; i < dronenum; i++) {
					Point loc = new Point(centralpoint.x
							+ (int) (FastMath.cos(FastMath
									.toRadians(i * angle)) * radius),
							centralpoint.y
									+ (int) (FastMath
											.sin(FastMath.toRadians(i
													* angle)) * radius));
					plist.add(loc);
					// System.out.println (loc.x+","+loc.y);
					// plist.add(new Point(5,5));
				}

			}

			positions = plist;
		}

	}

	private class DisplayWorker extends SwingWorker<Void, Integer> {

		private int mapnum = 0;

		public DisplayWorker(int mnum) {
			mapnum = mnum;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// loadParameters(); //not needed
			runSimulation(true, mapnum);
			return null;
		}

		public void setProgressToPublish(int p) {
			publish(p);
		}

		protected void process(List<Integer> results) {
			display.updateDisplay();
		}

		@Override
		public void done() {
			// code to ensure that exceptions are handled and not swallowed
			try {
				get();
			} catch (final InterruptedException ex) {
				throw new RuntimeException(ex);
			} catch (final ExecutionException ex) {
				throw new RuntimeException(ex.getCause());
			}
		}

	}

	protected synchronized void pause(int ms) {
		try {
			this.wait(ms);
		} catch (InterruptedException ex) {
		}
	}

	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

}
