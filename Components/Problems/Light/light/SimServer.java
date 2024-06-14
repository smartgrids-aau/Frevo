package light;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jodk.lang.FastMath;
import utils.NESRandom;

public class SimServer {

	//-----Server parameters-------
	/**
	 * Real-time length of one time step in ms
	 */
	public static final double STEPLENGTH = 100;
	
	/** Dimensions of the square field */
	public static final int FIELDDIM = 200;
	public static final double GRIDCELLSIZE = 20.0f;
	
	//------------Communication constants
	public static final int SETSPEED = 100;
	
	//-------------Simulator constants------------
	public static final double LIGHTSOURCE_PLACEMENT_RADIUS = 80;
	public static final double LIGHTSOURCE_SIZE = 10;
	
	/** Location of the light source */
	public Point2D targetlocation;
	/** Reference to parent object */
	private Light parent;
	public boolean succeeded = false;
	
	//-------------Advanced fitness---------------
	private List<Point2D> robotMoves;
	private Point lastMove;
	private HashSet<Point> robotGridCellsVisited;
	private double gridCellSize = GRIDCELLSIZE;
	
	public SimServer(Light parent, long seed) {
		this.parent = parent;
		
		initfield(seed);
	}
	
	private void initfield(long seed) { 	
		
		Random generator = new NESRandom(seed);

		//place light source on a random point on a circle
		double angle = FastMath.toRadians(generator.nextInt(360));
		//place target randomly outside this
		targetlocation = new Point2D.Double((LIGHTSOURCE_PLACEMENT_RADIUS+generator.nextInt(25)) * FastMath.cos(angle),(LIGHTSOURCE_PLACEMENT_RADIUS+generator.nextInt(25)) * FastMath.sin(angle));
		
		robotMoves = new CopyOnWriteArrayList< Point2D >();
		lastMove = null;
		robotGridCellsVisited = new HashSet<Point>();

	}
	
	public void calculateAll() {
		//calculate intentions
		calcint (parent.robot);
		//check if objective is completed
		if (getDistance(targetlocation, parent.robot.getPosition()) <= Robot.ROBOT_DIAMETER/2+1) {
			succeeded = true;
		}
	}
	
	private void calcint(Robot robot) {
		Intention intent =  consumeIntention(robot);
		switch (intent.intId) {
		case SimServer.SETSPEED : {//only one implemented so far
			double leftspeed = intent.param1;
			double rightspeed = intent.param2;
			//re-scale power to have negative as well (backward)
			leftspeed = (leftspeed*2*Robot.MOTOR_SPEED_MAX)-Robot.MOTOR_SPEED_MAX;
			rightspeed = (rightspeed*2*Robot.MOTOR_SPEED_MAX)-Robot.MOTOR_SPEED_MAX;
			
			//calculate movement
			double time = STEPLENGTH/1000;
			double diff = leftspeed-rightspeed;
			double sum = leftspeed+rightspeed;
			double theta_speed = ( (Robot.ROBOT_WHEEL_DIAMETER)/2.0)/(Robot.ROBOT_DIAMETER)*diff*2;
			
			//angular change
			robot.setBodyDirection(robot.getBodyDirection()+FastMath.toDegrees(theta_speed*time));

			double x = Robot.ROBOT_WHEEL_DIAMETER/4.0*sum*FastMath.sin(FastMath.toRadians(robot.getBodyDirection()) )*time;
			
			double y = Robot.ROBOT_WHEEL_DIAMETER/4.0*sum*(-1)*Math.cos(FastMath.toRadians(robot.getBodyDirection()) )*time;
			
			//calculate new position
			Point2D newpos = new Point2D.Double (robot.getPosition().getX() + x, robot.getPosition().getY() + y);
			
			if (validatePoint(newpos))	{ //object is not in blocked zone

				robot.setPosition(newpos); //everything is fine, move
				Point robotcenter = new Point ((int)(robot.getPosition().getX()),(int)(robot.getPosition().getY()));
				robotMoves.add(robotcenter);
				addPositionToGridCellsVisited(robotcenter);
				lastMove = robotcenter;
			} else { //robot would leave zone
				//do nothing
			}
			
			break;
		}
		}
	}
	
	public void addPositionToGridCellsVisited(Point coord){
		//convert position to grid cell
		Point gridCoord = convertToGridCoord(coord);
		if ((lastMove != null) && (!gridCoord.equals(convertToGridCoord(lastMove)))){
			//check if it is the map, if not, add it
			if (!robotGridCellsVisited.contains(gridCoord)){
				robotGridCellsVisited.add(gridCoord);							

				//System.out.println("Grid coord added: " + gridCoord.getX() + ", "+ gridCoord.getY());
			}					
		}		
	}		
	
	public Point convertToGridCoord(Point coord){
		double prop = calcResizeProp();
		Point origo = calcOrigo();
		
		
		Point convCoord = new Point ((int)(coord.x* prop+origo.x),(int)(coord.y * prop +origo.y ));
		
		Point gridcoord = new Point(
				(int)(Math.floor(convCoord.getX() / getGridCellSize()) ), 
				(int)(Math.floor(convCoord.getY() / getGridCellSize()) ));
		return gridcoord;
	}
	
	private double calcResizeProp(){
		int border = 20;		
		double prop = (double)(Math.min( parent.getDisplayWidth() - border, parent.getDisplayHeight() - border)) / (double)(SimServer.FIELDDIM);
		return prop;
	}
	
	private Point calcOrigo(){
		int border = 20;		
		int  u = Math.min(parent.getDisplayWidth() - border, parent.getDisplayHeight() - border);
		return new Point(10+u/2,10+u/2);
	}

	/**
	 * Calculates the length of an origo-based vector
	 * @return
	 */
	public static double getLength(Point2D.Double p1) {
		return FastMath.hypot(p1.x, p1.y);
	}
	
	/** Returns false if the point is outside of the zone */
	private static boolean validatePoint(Point2D pos) {
		//check boundary
		if ((pos.getX() < - (FIELDDIM/2-Robot.ROBOT_DIAMETER/2)  ) || (pos.getY()<-(FIELDDIM/2-Robot.ROBOT_DIAMETER/2)) || (pos.getX()>(FIELDDIM/2-Robot.ROBOT_DIAMETER/2)) || (pos.getY() > (FIELDDIM/2-Robot.ROBOT_DIAMETER/2))) {
			return false;
		}
		return true;
	}
	
	private static Intention consumeIntention(Robot robot) {
		Intention intent;
		if (robot.buffer.size() == 0) { intent = null; System.err.println ("no intention");}
		else {
			intent = robot.buffer.remove(0);
		}
		return intent;
	}

	/**
	 * Calculates the difference between two angles given in degrees. 
	 * A positive result indicates that the second angle is further counterclockwise.  
	 * @param alpha first angle
	 * @param beta second angle
	 * @return difference in degrees between -180 and +180
	 */
	static public double angleDiffDegrees(double alpha,double beta) {
		double phi = (beta % 360)-(alpha % 360);
		
		if (phi > 180) phi-= 360;
		if (phi < -180) phi+= 360;
		
		return phi;
	}
	
	public void getVisuals(Robot robot) {
		double sensorleft = 0, sensorright = 0;	
		
		Point2D pos = robot.getPosition();
		double dir = robot.getBodyDirection();
		dir = fixangle (dir);
		
		//left light sensor
		Point2D sensorcenter = new Point2D.Double (
				// facing NORD is actually 90 degree, so before converting, subtract
				pos.getX() + FastMath.sin(FastMath.toRadians(dir-90))*(Robot.ROBOT_DIAMETER/2.0), 
				pos.getY() - FastMath.cos(FastMath.toRadians(dir-90))*(Robot.ROBOT_DIAMETER/2.0));
		double distance = getDistance (targetlocation,sensorcenter);
		double dx = targetlocation.getX() - sensorcenter.getX();
		double dy = targetlocation.getY() - sensorcenter.getY();
		
		double angle = FastMath.toDegrees(FastMath.atan2(dx,-dy));
		angle = fixangle (angle);
		
		if ( (Math.abs(angleDiffDegrees(angle,dir)) < Robot.LIGHT_SENSOR_ANGLE/2) && (distance <= Robot.LIGHT_SENSOR_RANGE)) {
			//in range
			sensorleft = distance;
		}
		
		//right sensor
		sensorcenter = new Point2D.Double (
				pos.getX() + FastMath.sin(FastMath.toRadians(dir+90))*(Robot.ROBOT_DIAMETER/2.0), 
				pos.getY() - FastMath.cos(FastMath.toRadians(dir+90))*(Robot.ROBOT_DIAMETER/2.0));
		distance = getDistance (targetlocation,sensorcenter);
		dx = targetlocation.getX() - sensorcenter.getX();
		dy = targetlocation.getY() - sensorcenter.getY();
		angle = FastMath.toDegrees(FastMath.atan2(dx,-dy));
		angle = fixangle (angle);
		
		if ( (Math.abs(angleDiffDegrees(angle,dir)) < Robot.LIGHT_SENSOR_ANGLE/2) && (distance <= Robot.LIGHT_SENSOR_RANGE)) {
			//in range
			sensorright = distance;
		}
		robot.getController().setSensorValues(new double[]{sensorleft,sensorright});
		
	}
	
	/** Converts angle into 0..360 range.*/ 
	private static double fixangle (double angle) {
		if (angle >= 0) {
			return angle % 360;
		}
		
		angle = angle % 360;
		return 360+angle;	
	}
	
	public static double getDistance(Point2D obj1, Point2D obj2) {
		return FastMath.hypot(obj1.getX() - obj2.getX(), obj1.getY() - obj2.getY());
	}
	
	public int getNumberOfCellsVisited(){
		return robotGridCellsVisited.size();
	}

	public double getNumberOfCellsVisitedWeighted(){
		double weightedSum=0;
		for (Point p: robotGridCellsVisited) {
			double x=p.getX();
			double y=p.getY();
			weightedSum += 5.0 / (5.0+x*x+y*y);
		}
		return weightedSum;
	}
	
	public Point2D getRobotMove(int i){
		return robotMoves.get(i);
	}
	
	
	public  List<Point2D> getRobotMoves(){
		return robotMoves;		
	}
	
	public double getGridCellSize() {
		return gridCellSize;
	}

	public void setGridCellSize(double gridCellSize) {
		this.gridCellSize = gridCellSize;
	}	

}
