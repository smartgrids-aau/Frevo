package components.cdrones;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import net.jodk.lang.FastMath;

import components.cdrones.cdrones.BlobMapWithPositions;
import components.controllers.CoverageController;
import components.controllers.DiscreteOutputEvolvedController;
import components.controllers.EvolvedController;

public class DronesServer {

	public static final int DEFAULT_FIELDDIM_X = 200;
	public static final int DEFAULT_FIELDDIM_Y = 160;
	public static final int DEFAULT_MAP_RESOLUTION = 10;
	
	// -----Server parameters-------
	/** Real-time length of one time step in ms */
	public static final int STEPLENGTH = 100;
	/** Dimensions of the field, should be divisible by FIELDDIMENSION */
	public static Point FIELDDIM = new Point(150, 150);//200 160

	// Constants for field states
	/** Field is blocked */
	public static final int BLOCKED = 1;
	public static final int FREE = 0;
	public static final int TARGET = 2;

	// ------------Communication constants-------------
	public static final int DASHTO = 100;

	public static final double DRONE_ACC_MAX = 1;
	public static final double DRONE_SPEED_MAX = 0.42;
	public static final double DRONE_DECAY = 0.4;
	private static final double DASHPOWERRATE = 0.006;
	private static final double PLAYER_ACC_MAX = 1;
	public static final double MINPOWER = -100;
	public static final double MAXPOWER = 100;

	/**
	 * Grid containing the obstacles. 0 means empty, 1 means obstacle, 2 means
	 * target in that area
	 */
	public int[][] fieldgrid;
	public int[][] visitedgrid;
	public int visitedzones;
	public int coverablezones;

	public Point targetlocation;

	/** Reference to parent object */
	private cdrones parent;

	// private Random generator;

	public DronesServer(cdrones parent, BlobMapWithPositions bmp) {
		this.parent = parent;

		initfield(bmp);
	}

	private void initfield(BlobMapWithPositions bmp) {
		// number of accessible fields
		int w = DronesServer.FIELDDIM.x;
		int h = DronesServer.FIELDDIM.y;
		fieldgrid = new int[w][h];
		visitedgrid = new int[w][h];

		// initiate visitedgrid
		for (int px = 0; px > w; px++) {
			for (int py = 0; py > h; py++) {
				visitedgrid[px][py] = 0;
			}
		}

		visitedzones = 0;
		coverablezones = 0;

		// copy fieldgrid
		for (int x = 0; x < DronesServer.FIELDDIM.x; x++) {
			for (int y = 0; y < DronesServer.FIELDDIM.y; y++) {
				if (bmp.map[x][y] > FREE) {
					fieldgrid[x][y] = BLOCKED;
					visitedgrid[x][y] = 1;// set covered cells to visited
												// before summing up
				} else {
					coverablezones++;
				}

			}
		}

		// place drones
		for (int i = 0; i < parent.drones.length; i++) {
			Point p = bmp.positions.get(i);
			parent.drones[i].setPosition(new Point2D.Double(p.x, p.y));
			// visit own cell
			if (visitedgrid[p.x][FIELDDIM.y - p.y] <= 1) {
				visitedgrid[p.x][FIELDDIM.y - p.y] = 1;
				visitedzones++;
			}
		}

	}

	public void visitZone(Point z) {
		if (visitedgrid[z.x][z.y] == 0) {
			//visitedgrid[z.x][z.y] = true;
			visitedzones++;
		}
		visitedgrid[z.x][z.y]++;
	}

	/*
	 * private void fill(int x, int y, boolean[][] map) { if (map[x][y] ==
	 * false) { return; } map[x][y] = false; // propagate if (x != 0) fill(x -
	 * 1, y, map); if (y != 0) fill(x, y - 1, map); if (x < map.length - 1)
	 * fill(x + 1, y, map); if (y < map[0].length - 1) fill(x, y + 1, map);
	 * return; }
	 */

	public void calculateAll() {
		// calculate intentions
		for (int j = 0; j < parent.DRONENUMBER; j++) {
			calcint(parent.drones[j]);
		}

		for (int j = 0; j < parent.DRONENUMBER; j++) {
			calcmodel(parent.drones[j]);
		}

	}

	/**
	 * Calculates the positions regarding the vectors, applies decay, sets
	 * acceleration to zero
	 */
	private void calcmodel(Drone drone) {

		// calculate new position
		Point2D.Double newpos = new Point2D.Double(drone.getPosition().x
				+ drone.speedVector.x, drone.getPosition().y
				+ drone.speedVector.y);

		// System.out.println
		// (drone.getPosition().x+", "+drone.getPosition().y);
		if (ValidatePoint(newpos)) { // object is not in blocked zone
			drone.setPosition(newpos);
		} else { // drone would drive into a blocked zone
			drone.getController().reportFailedMove();
		}

		// apply decay
		drone.applydecay();
		// set acc to zero
		drone.accelerationVector = new Point2D.Double(0, 0);
	}

	private boolean ValidatePoint(Point2D.Double pos) {
		// check map boundary
		if ((pos.x < Drone.DRONE_SIZE / 2)
				|| (pos.y < Drone.DRONE_SIZE / 2)
				|| (pos.x > FIELDDIM.x - (Drone.DRONE_SIZE / 2))
				|| (pos.y > FIELDDIM.y - (Drone.DRONE_SIZE / 2))) {
			return false;
		}

		// check fields around
		Point f = getFieldfromPoint(pos);
		if ((intersects(pos, f)) || (fieldgrid[f.x][f.y] >= BLOCKED))
			return false;

		boolean left = (f.x > 0);
		boolean right = (f.x < fieldgrid.length - 1);
		boolean top = (f.y > 0);
		boolean bottom = (f.y < fieldgrid[0].length - 1);

		if (left) {
			if ((intersects(pos, new Point(f.x - 1, f.y)))
					&& (fieldgrid[f.x - 1][f.y] >= BLOCKED))
				return false;
			if (top) {
				if ((intersects(pos, new Point(f.x - 1, f.y - 1)))
						&& (fieldgrid[f.x - 1][f.y - 1] >= BLOCKED))
					return false;
			}
			if (bottom) {
				if ((intersects(pos, new Point(f.x - 1, f.y + 1)))
						&& (fieldgrid[f.x - 1][f.y + 1] >= BLOCKED))
					return false;
			}
		}
		if (right) {
			if ((intersects(pos, new Point(f.x + 1, f.y)))
					&& (fieldgrid[f.x + 1][f.y] >= BLOCKED))
				return false;
			if (top) {
				if ((intersects(pos, new Point(f.x + 1, f.y - 1)))
						&& (fieldgrid[f.x + 1][f.y - 1] >= BLOCKED))
					return false;
			}
			if (bottom) {
				if ((intersects(pos, new Point(f.x + 1, f.y + 1)))
						&& (fieldgrid[f.x + 1][f.y + 1] >= BLOCKED))
					return false;
			}
		}
		if (top) {
			if ((intersects(pos, new Point(f.x, f.y - 1)))
					&& (fieldgrid[f.x][f.y - 1] >= BLOCKED))
				return false;
		}
		if (bottom) {
			if ((intersects(pos, new Point(f.x, f.y + 1)))
					&& (fieldgrid[f.x][f.y + 1] >= BLOCKED))
				return false;
		}
		return true;
	}

	/** Calculates if a drone's position intersects with the field */
	private static boolean intersects(Point2D.Double dronepos, Point field) {
		Point2D.Double rect = getFieldCenterfromField(field);
		Point2D.Double circle = dronepos;
		Point2D.Double circleDistance = new Point2D.Double();

		double halfunit = 1.0 / 2.0;
		double r = Drone.DRONE_SIZE / 2.0;

		circleDistance.x = FastMath.abs(circle.x - rect.x);
		circleDistance.y = FastMath.abs(circle.y - rect.y);

		if (circleDistance.x > (halfunit + r))
			return false;
		if (circleDistance.y > (halfunit + r))
			return false;

		if (circleDistance.x <= (halfunit + r))
			return true;
		if (circleDistance.y <= (halfunit + r))
			return true;

		double cornerDistance_sq = ((circleDistance.x - halfunit) * (circleDistance.x - halfunit))
				+ ((circleDistance.y - halfunit) * (circleDistance.y - halfunit));

		if (cornerDistance_sq <= (r*r))
			return true;
		
		return false;
	}

	private static void calcint(Drone drone) {
		Intention intent = consumeIntention(drone);
		switch (intent.intId) {
		case DronesServer.DASHTO: {// it is a turn and a dash together
			double moment = intent.param2;// direction

			if (java.lang.Double.isNaN(moment)) {
				System.err
						.println("Error while processing dashto intention, moment is NaN");
				// System.exit(1);
			}

			drone.setBodyDirection(moment % 360);

			// calculate edp (effective dash power)
			double power = intent.param1;
			// If power is too small do nothing
			if (power < MINPOWER)
				power = MINPOWER;
			if (power > MAXPOWER)
				power = MAXPOWER;

			double edp = DASHPOWERRATE * power; // effort = 1 always
			Point2D.Double accvect = getPlayerAccVector(drone, edp);
			// add new vector and normalize it
			drone.addAccVector(accvect);

			break;
		}
		}
	}

	public void getVisuals(Drone drone) {
		// Visuals depending on the type of the controller
		if (cdrones.CONTROLLERTYPE == ControllerType.RANDOMDIRECTIONCONTROLLER) {
			// no input needed
		} else if (cdrones.CONTROLLERTYPE == ControllerType.COVERAGEBASEDCONTROLLER) {
			// add drones in vicinity
			CoverageController c = (CoverageController) drone.getController();
			ArrayList<Drone> dlist = new ArrayList<Drone>();
			for (int i = 0; i < parent.DRONENUMBER; i++) {
				Drone otherDrone = parent.drones[i];
				if ((otherDrone.id_number != drone.id_number)
						&& (getDistance(otherDrone, drone) <= Drone.COMMUNICATION_RANGE)) {
					dlist.add(otherDrone);

				}
			}
			c.addDronesInRange(dlist);
		} else if (cdrones.CONTROLLERTYPE == ControllerType.EVOLVEDCONTROLLER) {

			// add drones in communication range
			EvolvedController c = (EvolvedController) drone.getController();
			ArrayList<Drone> dlist = new ArrayList<Drone>();
			
			for (int i = 0; i < parent.DRONENUMBER; i++) {
				Drone otherDrone = parent.drones[i];
				if (otherDrone.id_number != drone.id_number) {
					if (getDistance(otherDrone, drone) <= Drone.COMMUNICATION_RANGE)
						dlist.add(otherDrone);
				}
			}
			//System.out.println (dlist.size());
			c.addDronesInRadioRange(dlist);

			// add sensor input
			// +1 is just to ensure it is higher than the range
			double[] sensordata = new double[] { Drone.DETECTION_RANGE + 1,
					Drone.DETECTION_RANGE + 1, Drone.DETECTION_RANGE + 1,
					Drone.DETECTION_RANGE + 1 };
			
			drone.BUMPER_BL_ON = false;
			drone.BUMPER_BR_ON = false;
			drone.BUMPER_TL_ON = false;
			drone.BUMPER_TR_ON = false;
			
			Point2D.Double dloc = drone.getPosition();
			int leftx = (int) dloc.x - Drone.DETECTION_RANGE;
			if (leftx < 0)
				leftx = 0;

			int rightx = (int) dloc.x + Drone.DETECTION_RANGE;
			if (rightx > fieldgrid.length - 1)
				rightx = fieldgrid.length - 1;

			int topy = (int) dloc.y + Drone.DETECTION_RANGE;
			if (topy > fieldgrid[0].length - 1)
				topy = fieldgrid[0].length - 1;

			int bottomy = (int) dloc.y - Drone.DETECTION_RANGE;
			if (bottomy < 0)
				bottomy = 0;
			
			for (int x = leftx; x <= rightx; x++) {
				for (int y = bottomy; y <= topy; y++) {
					Point2D.Double fieldmidpoint = new Point2D.Double(x+0.5,y+0.5);
					double dist = DronesServer.getDistance(dloc,
							fieldmidpoint);
					if ((dist <= Drone.DETECTION_RANGE)
							&& (fieldgrid[x][fieldgrid[0].length-y-1] == DronesServer.BLOCKED)) {
						
						// find location
						if (fieldmidpoint.x < dloc.x) {// left
							if (fieldmidpoint.y > dloc.y) {// top-left
								if (dist < sensordata[0]) {
									sensordata[0] = dist;
									drone.BUMPER_TL_ON = true;
								}
							} else {// bottom-left
								if (dist < sensordata[2]) {
									sensordata[2] = dist;
									drone.BUMPER_BL_ON = true;
								}
							}
						} else {// right
							if (fieldmidpoint.y > dloc.y) {// top-right
								if (dist < sensordata[1]) {
									sensordata[1] = dist;
									drone.BUMPER_TR_ON = true;
								}
							} else {// bottom-right
								if (dist < sensordata[3]) {
									sensordata[3] = dist;
									drone.BUMPER_BR_ON = true;
								}
							}
						}
					}
												
				}
			}

			// return sensor data
			c.addSensorData(sensordata);
		} else if (cdrones.CONTROLLERTYPE == ControllerType.DISCRETEOUTPUTEVOLVEDCONTROLLER) {

			drone.RADIO_BL_ON = false;
			drone.RADIO_BR_ON = false;
			drone.RADIO_TL_ON = false;
			drone.RADIO_TR_ON = false;
			
			// add drones in communication range
			DiscreteOutputEvolvedController c = (DiscreteOutputEvolvedController) drone.getController();
			ArrayList<Drone> dlist = new ArrayList<Drone>();
			for (int i = 0; i < parent.DRONENUMBER; i++) {
				Drone otherDrone = parent.drones[i];
				if (otherDrone.id_number != drone.id_number) {
					if (getDistance(otherDrone, drone) <= Drone.COMMUNICATION_RANGE)
						dlist.add(otherDrone);
				}
			}
			c.addDronesInRadioRange(dlist);

			// add sensor input
			// +1 is just to ensure it is higher than the range
			double[] sensordata = new double[] { Drone.DETECTION_RANGE + 1,
					Drone.DETECTION_RANGE + 1, Drone.DETECTION_RANGE + 1,
					Drone.DETECTION_RANGE + 1 };
			
			drone.BUMPER_BL_ON = false;
			drone.BUMPER_BR_ON = false;
			drone.BUMPER_TL_ON = false;
			drone.BUMPER_TR_ON = false;

			Point2D.Double dloc = drone.getPosition();
			int leftx = (int) dloc.x - Drone.DETECTION_RANGE;
			if (leftx < 0)
				leftx = 0;

			int rightx = (int) dloc.x + Drone.DETECTION_RANGE;
			if (rightx > fieldgrid.length - 1)
				rightx = fieldgrid.length - 1;

			int topy = (int) dloc.y + Drone.DETECTION_RANGE;
			if (topy > fieldgrid[0].length - 1)
				topy = fieldgrid[0].length - 1;

			int bottomy = (int) dloc.y - Drone.DETECTION_RANGE;
			if (bottomy < 0)
				bottomy = 0;
			
			for (int x = leftx; x <= rightx; x++) {
				for (int y = bottomy; y <= topy; y++) {
					Point2D.Double fieldmidpoint = new Point2D.Double(x+0.5,y+0.5);
					double dist = DronesServer.getDistance(dloc,
							fieldmidpoint);
					if ((dist <= Drone.DETECTION_RANGE)
							&& (fieldgrid[x][fieldgrid[0].length-y-1] == DronesServer.BLOCKED)) {
						
						// find location
						if (fieldmidpoint.x < dloc.x) {// left
							if (fieldmidpoint.y > dloc.y) {// top-left
								if (dist < sensordata[0]) {
									sensordata[0] = dist;
									drone.BUMPER_TL_ON = true;
								}
							} else {// bottom-left
								if (dist < sensordata[2]) {
									sensordata[2] = dist;
									drone.BUMPER_BL_ON = true;
								}
							}
						} else {// right
							if (fieldmidpoint.y > dloc.y) {// top-right
								if (dist < sensordata[1]) {
									sensordata[1] = dist;
									drone.BUMPER_TR_ON = true;
								}
							} else {// bottom-right
								if (dist < sensordata[3]) {
									sensordata[3] = dist;
									drone.BUMPER_BR_ON = true;
								}
							}
						}
					}
												
				}
			}

			// return sensor data
			c.addSensorData(sensordata);
		}

	}

	public static double fixangle(double angle) {
		if (angle >= 0) {
			return angle % 360;
		}
		
		angle = angle % 360;
		return 360 + angle;
	}

	// returns the coverage percentage as a value between 0..1;
	public double getCoveragePercentage() {
		/*
		 * int visited = 0; int[][] vgrid = visitedgrid; int nCoverable = 0; //
		 * number of coverable fields
		 * 
		 * for (int x = 0; x < vgrid.length; x++) { for (int y = 0; y <
		 * vgrid[0].length; y++) { if (vgrid[x][y] == 1) visited++; if
		 * (fieldgrid[x][y] != BLOCKED) nCoverable++; } }
		 */

		double res = ((double) visitedzones) / (double) coverablezones;
		return res;
	}

	/**
	 * Calculates the difference between two angles given in degrees. A positive
	 * result indicates that the second angle is further counterclockwise.
	 * 
	 * @param alpha
	 *            first angle
	 * @param beta
	 *            second angle
	 * @return difference in degrees between -180 and +180
	 */
	static public double angleDiffDegrees(double alpha, double beta) {
		double phi = (beta % 360) - (alpha % 360);

		if (phi > 180)
			phi -= 360;
		if (phi < -180)
			phi += 360;

		return phi;
	}

	public static double getDistance(Point2D.Double obj1, Point2D.Double obj2) {
		double xdif = obj1.x - obj2.x;
		double ydif = obj1.y - obj2.y;
		return FastMath.hypot(xdif, ydif);
	}

	public static double getDistance(Drone obj1, Drone obj2) {
		double xdif = obj1.getPosition().x - obj2.getPosition().x;
		double ydif = obj1.getPosition().y - obj2.getPosition().y;
		
		return FastMath.hypot(xdif, ydif);
	}

	/**
	 * Returns the given coordinates converted into the drone's coordinate
	 * system. Drone's coordinate system means that the facing direction is the
	 * y axis.
	 */
	public static Point2D.Double getRelativeCoordinates(double xaxis,
			Point2D.Double coords) {
		double angle = xaxis;
		angle = FastMath.toRadians(angle);
		double x = coords.x;
		double y = coords.y;
		return new Point2D.Double(x * FastMath.cos(angle) - y
				* FastMath.sin(angle), x
				* FastMath.sin(angle) + y
				* FastMath.cos(angle));
	}

	/**
	 * Returns a normalized acceleration vector
	 * 
	 * @param player
	 * @param edp
	 * @return
	 */
	private static Double getPlayerAccVector(Drone drone, double edp) {
		double direction = drone.getBodyDirection();
		Point2D.Double Accvector = new Point2D.Double(
				edp
						* FastMath.cos(FastMath
								.toRadians(direction)), edp
						* FastMath.sin(FastMath
								.toRadians(direction)));
		if (getLength(Accvector) > PLAYER_ACC_MAX) {
			// normalize
			double newlength = PLAYER_ACC_MAX / edp;
			Accvector = new Point2D.Double(Accvector.x * newlength, Accvector.y
					* newlength);
			return Accvector;
		} 
		return Accvector;
	}

	/**
	 * Calculates the length of an origo-based vector
	 * 
	 * @return
	 */
	public static double getLength(Point2D.Double p1) {
		return FastMath
				.sqrt((p1.x*p1.x) + (p1.y*p1.y));
	}

	/** Translates the coordinates of a field to real-coordinate system */
	public static Point getFieldfromPoint(Point2D.Double point) {
		Point result = new Point();
		int px = (int) point.x;
		int py = (int) point.y;
		result.x = (px);
		result.y = FIELDDIM.y - (py) - 1;

		return result;
	}

	public static Point2D.Double getFieldCenterfromField(Point field) {
		Point2D.Double result = new Point2D.Double();
		double res = 1.0;
		result.x = res * field.x + res / 2;
		result.y = -(res * field.y + res / 2);

		return result;
	}

	/** Returns the geometric (true) distance of a field and a drone */
	public static double getDistance(Point field, Drone d) {
		double res = 1.0;
		Point2D.Double dp = d.getPosition();
		Point2D.Double fc = getFieldCenterfromField(field);
		// center above
		if ((fc.x - res <= dp.x) && (dp.x <= fc.x + res) && (dp.y > fc.y))
			return FastMath.abs(fc.y - dp.y + res);
		// center below
		else if ((fc.x - res <= dp.x) && (dp.x <= fc.x + res) && (dp.y < fc.y))
			return FastMath.abs(dp.y - fc.y + res);
		// center left
		else if ((fc.y + res >= dp.y) && (dp.y >= fc.y - res) && (dp.x < fc.x))
			return FastMath.abs(fc.x - dp.x - res);
		// center right
		else if ((fc.y + res >= dp.y) && (dp.y >= fc.y - res) && (dp.x > fc.x))
			return FastMath.abs(dp.x - fc.x - res);
		// top left
		else if ((dp.x < fc.x) && (dp.y > fc.y))
			return (getDistance(dp, new Point2D.Double(fc.x - res, fc.y + res)));
		// top right
		else if ((dp.x > fc.x) && (dp.y > fc.y))
			return (getDistance(dp, new Point2D.Double(fc.x + res, fc.y + res)));
		// bottom left
		else if ((dp.x < fc.x) && (dp.y < fc.y))
			return (getDistance(dp, new Point2D.Double(fc.x - res, fc.y - res)));
		// bottom right
		else
			/* if ( (dp.x<fc.x) && (dp.y < fc.y ) ) */return (getDistance(dp,
					new Point2D.Double(fc.x + res, fc.y - res)));
	}

	private static Intention consumeIntention(Drone drone) {
		Intention intent;
		if (drone.buffer.size() == 0) {
			intent = null;
			System.out.println("no intention");
		} else {
			intent = drone.buffer.remove(0);
		}
		return intent;
	}
}
