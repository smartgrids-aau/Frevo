package components.ddrones;


import java.awt.AWTException;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingWorker;

import utils.ScreenCapture;

import components.ddrones.blobGenerator.Blob;
import components.ddrones.blobGenerator.BlobGenerator;
import components.ddrones.blobGenerator.BlobMap;

import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.XMLFieldEntry;


public class ddrones extends AbstractSingleProblem {

	/** Number of drones used in the simulation */
	public static int DRONENUMBER;
	/** The maximum time allowed for the drones to accomplish the mission */
	public static int MAXTIME;
	/** Percentage of the field blocked by obstacles */
	public static int OBSTACLEPERCENTAGE;
	/** Number of times the simulation is executed */
	public static int EVALUATIONNUMBER;
	/** Grid width */
	public static int GRIDWIDTH;
	/** Grid height */
	public static int GRIDHEIGHT;

	public static ControllerModel DRONECONTROLLER;

	public static final int DISPLAYWAIT = 100;
	public static final int FREE = 0;
	public static final int BLOCKED = 1;
	public AbstractRepresentation nnetwork;
	public boolean withmonitor = false;
	public int stepnumber;
	public int aktStep;
	public boolean succeeded = false;
	public boolean[][] visitedgrid;
	public int visitedzones;
	public int[][] fieldgrid;
	private boolean saveMovie = false;

	private DronesDisplay display;
	protected DisplayWorker sw;

	public static ArrayList<BlobMapWithPositions> maps = new ArrayList<BlobMapWithPositions>();

	/** Array containing the drones */
	public Drone[] drones;

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		loadParameters();
		this.nnetwork = candidate;

		return runSimulation(false);
	}

	// adjust representation input size
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {
		// Adjust to controller
		XMLFieldEntry c = properties.get("controller");
		XMLFieldEntry inputn = requirements.get("inputnumber");
		
		try {
			int controller = Integer.parseInt(c.getValue());
			
			if (controller == 5) {
				inputn.setValue(Integer.toString(4));
			} else if (controller == 6) {
				inputn.setValue(Integer.toString(12));
			} else if (controller == 7) {
				inputn.setValue(Integer.toString(28));
			} else
				inputn.setValue(Integer.toString(8));
		} catch (Exception e) {
			// try enum instead
			ControllerModel controller = ControllerModel.valueOf(c.getValue());
			
			if (controller == ControllerModel.Evolved_non_cooperative) {
				inputn.setValue(Integer.toString(4));
			} else if (controller == ControllerModel.Evolved_cooperative_8_sensors) {
				inputn.setValue(Integer.toString(12));
			} else if (controller == ControllerModel.Evolved_cooperative_24_sensors) {
				inputn.setValue(Integer.toString(12));
			} else
				inputn.setValue(Integer.toString(8));
			
		}

		

		return requirements;
	}

	private void loadParameters() {
		DRONENUMBER = Integer.parseInt(getProperties().get("number_of_drones")
				.getValue());
		MAXTIME = Integer
				.parseInt(getProperties().get("cutofftime").getValue());
		OBSTACLEPERCENTAGE = Integer.parseInt(getProperties().get(
				"obstaclepercentage").getValue());
		EVALUATIONNUMBER = Integer.parseInt(getProperties().get("evalnumber")
				.getValue());
		GRIDWIDTH = Integer.parseInt(getProperties().get("gridwidth")
				.getValue());
		GRIDHEIGHT = Integer.parseInt(getProperties().get("gridheight")
				.getValue());
		
		String controller = getProperties().get("controller").getValue();
		try {
			DRONECONTROLLER = ControllerModel.valueOf(controller);
		} catch (Exception e) {
			// fall back to support old results (deprecated)
			DRONECONTROLLER = ControllerModel.Evolved_cooperative_4_sensors;
		}

		synchronized (maps) {
			// check map storage
			if (maps.size() < EVALUATIONNUMBER) {
				reCreateMaps();
			}
		}
	}
	
	private void reCreateMaps() {
		BlobGenerator bgen = new BlobGenerator(generator);
		System.err.print("Generating maps... ");
		for (int i = 0; i < EVALUATIONNUMBER; i++) {
			BlobMap bm = bgen.generate(GRIDWIDTH, GRIDHEIGHT, 10,
					(OBSTACLEPERCENTAGE / 100.0),true);
			BlobMapWithPositions bmp = new BlobMapWithPositions(bm);
			bmp.generateStartingPoints(DRONENUMBER);
			//This construct is necessary instead of simple clearing and the readding all items
			//due to maps beeing static and synchronisation problemss
			if(maps.size() > i)
				maps.set(i, bmp);
			else
				maps.add(bmp);
		}
		System.err.println("done");
	}

	private double runSimulation(boolean withmonitor) {
		this.withmonitor = withmonitor;
		this.drones = new Drone[DRONENUMBER];
		double fitness = 0;
		int evNumber;

		if (withmonitor) 
			evNumber = 1;
		else
			evNumber = EVALUATIONNUMBER;

		// Iterate through
		for (int n = 0; n < evNumber; n++) {

			// create team
			for (int i = 0; i < DRONENUMBER; i++) {
				drones[i] = new Drone(new DroneController(nnetwork.clone(),
						this));
			}

			// initialize time
			stepnumber = MAXTIME;

			// initialize field
			// initfield(); old method replaced by blobgenerator
			int w = GRIDWIDTH;
			int h = GRIDHEIGHT;
			fieldgrid = new int[w][h];
			visitedgrid = new boolean[w][h];

			// initiate visitedgrid
			for (int px = 0; px > w; px++) {
				for (int py = 0; py > h; py++) {
					visitedgrid[px][py] = false;
				}
			}

			visitedzones = 0;

			// copy fieldgrid
			BlobMapWithPositions m = maps.get(n);
			for (int x = 0; x < m.WIDTH; x++) {
				for (int y = 0; y < m.HEIGHT; y++) {
					if (m.map[x][y] > FREE)
						fieldgrid[x][y] = BLOCKED;
				}
			}

			// place drones
			// placedrones();
			for (int i = 0; i < m.positions.size(); i++) {
				Point p = m.positions.get(i);
				drones[i].setPosition(p);
				visitedgrid[p.x][p.y] = true;
				visitedzones++;
				//fieldgrid[p.x][p.y] = BLOCKED;// block the zone as a mark of
												// occupy
			}

			if (withmonitor) {
				sw.setProgressToPublish(aktStep);
				if (saveMovie) saveFrame();
			}

			/* Simulator is ready */
			if (withmonitor)
				pause(DISPLAYWAIT); // pause 100 it was
			for (aktStep = 0; aktStep < stepnumber; aktStep++) {

				calculateStep();
				if (withmonitor) {
					if (saveMovie) saveFrame();
					pause(DISPLAYWAIT); // pause 100 it was
				}
				if (succeeded) {
					System.out.println("Finished earlier at " + aktStep);
					break;
				}

			}
			// old fitness
			// fitness += getVisitedZones() - aktStep;
			// coverage-based fitness
			int blocked = m.getNumberofBlocked();
			//System.out.println ("blocked: "+blocked);
			double coverage = (double) getVisitedZones()
					/ (double) ((GRIDHEIGHT) * (GRIDWIDTH) - blocked);
			
			if (withmonitor)
				System.out.println("Coverage: " + coverage + " ("
						+ getVisitedZones() + "/"
						+ (((GRIDHEIGHT) * (GRIDWIDTH)) - blocked));
			fitness += (coverage);
		}

		// System.out.println (fitness);
		return fitness / evNumber;

	}

	/*
	 * private void placedrones() { // drones initial placement policy?
	 * 
	 * for (int i = 0; i < DRONENUMBER; i++) { boolean placed = false; while
	 * (!placed) { int xd = generator.nextInt(GRIDWIDTH - 2) + 1; //
	 * (int)((GRIDWIDTH)*Math.random()); int yd = generator.nextInt(GRIDHEIGHT -
	 * 2) + 1;// (int)((GRIDHEIGHT)*Math.random()); if (fieldgrid[xd][yd] !=
	 * BLOCKED) { drones[i].setPosition(new Point(xd, yd)); visitedgrid[xd][yd]
	 * = true; visitedzones++; fieldgrid[xd][yd] = BLOCKED;// block the zone as
	 * a mark of // occupy placed = true; } } } }
	 */

	/** Returns true if given position is already occupied by a drone */
	public boolean isFieldOccupied(int x, int y) {
		boolean res = false;
		for (int i = 0; i < drones.length; i++) {
			Point pos = drones[i].getPosition();
			if ((pos.x == x) && (pos.y == y)) {
				res = true;
				break;
			}
		}
		return res;
	}
	
	private void saveFrame() {
		try {
			ScreenCapture.createImage(display, "./Images/Frame_"+fix(aktStep)+".bmp");
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static String fix(int num) {
		String ret = Integer.toString(num);
		if (num < 10) ret = "00"+ret;
		else if (num < 100) ret = "0"+ret;
		return ret;
	}

	public int getVisitedZones() {
		int counter = 0;
		for (int px = 0; px < visitedgrid.length; px++) {
			for (int py = 0; py < visitedgrid[0].length; py++) {
				if (visitedgrid[px][py])
					counter++;
			}
		}
		return counter;
	}

	public void calculateStep() {

		for (int k = 0; k < DRONENUMBER; k++) {
			drones[k].getController().postInfo(drones);
		}

		if (withmonitor)
			sw.setProgressToPublish(aktStep);

		// calculate new visited fields
		// if (getVisitedZones() == (GRIDWIDTH*GRIDHEIGHT-blocked)) succeeded =
		// true;

	}

	class BlobMapWithPositions {
		ArrayList<Point> positions = new ArrayList<Point>();

		public static final int BLOCKED = 1;
		public static final int FREE = 0;
		public int WIDTH, HEIGHT;
		private int blocked = -1;

		public ArrayList<Blob> blobs = new ArrayList<Blob>();

		public int map[][];

		public BlobMapWithPositions(BlobMap bm) {
			this.WIDTH = bm.WIDTH;
			this.HEIGHT = bm.HEIGHT;
			this.map = bm.map;
			this.blobs = bm.blobs;
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

		public void generateStartingPoints(int n) {
			ArrayList<Point> plist = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				boolean placed = false;
				while (!placed) {
					int xd = generator.nextInt(GRIDWIDTH - 2) + 1;
					int yd = generator.nextInt(GRIDHEIGHT - 2) + 1;
					if (this.map[xd][yd] < BLOCKED) {
						plist.add(new Point(xd, yd));
						placed = true;
					}
				}
			}

			positions = plist;
		}

	}

	public Drone[] getDrones() {
		return drones;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		loadParameters();
		reCreateMaps();//force recreate them (seed might have changed due to gui)
		this.nnetwork = candidate;
		this.drones = new Drone[DRONENUMBER];

		display = new DronesDisplay(this);

		sw = new DisplayWorker();
		sw.execute();
	}

	private class DisplayWorker extends SwingWorker<Void, Integer> {

		@Override
		protected Void doInBackground() throws Exception {
			runSimulation(true);
			return null;
		}

		public void setProgressToPublish(int p) {
			publish(p);
		}

		protected void process(List<Integer> results) {
			display.updateDisplay();
		}

	}

	protected synchronized void pause(int ms) {
		try {
			this.wait(ms);
		} catch (InterruptedException ex) {
		}
	}

	public Drone getDrone(int newx, int newy) {
		for (Drone d : drones) {
			if ((d.getPosition().x == newx) && (d.getPosition().y == newy)) {
				return d;
			}
		}
		return null;
	}

	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}
	
}
