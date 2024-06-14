package light;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Robot {
	
	private Point2D position;
	public Point2D speedVector = new Point2D.Double(0,0);
	public Double leftSpeed = 0.0;
	public Double rightSpeed = 0.0;
	public Double leftAcceleration = 0.0;
	public Double rightAcceleration = 0.0;
	private double bodyDirection;
	public List<Intention> buffer = new ArrayList<Intention>();
	
	//---------------Robot parameters---------------------------
	/** Motor maximum angular speed */
	public static final double MOTOR_SPEED_MAX = 12;
	/** Robot base diameter */
	public static final double ROBOT_DIAMETER = 10.0;
	/** Robot wheel diameter */
	public static final double ROBOT_WHEEL_DIAMETER = 5.0;
	/** Light sensor range */
	public static final double LIGHT_SENSOR_RANGE = 70;
	/** Light sensor angle in degree */
	public static final double LIGHT_SENSOR_ANGLE = 45;
	
	//------------- Sensors -----------------
	/** Left light sensor position from robot center */
	public static final Point2D.Double leftLightSensorOffset = new Point2D.Double(-2.5,0);
	
	/** Display color for the robot base */
	public static final Color BASECOLOR = java.awt.Color.BLUE;
	/** Display color for the robot's wheels */
	public static final Color WHEELCOLOR = java.awt.Color.BLUE;
	
	private RobotController controller;
	
	public Point2D getPosition() {
		return this.position;
	}
	
	public void setPosition (Point2D pos) {
		this.position = pos;
	}

	public Robot (RobotController c) {
		this.controller = c;
		this.controller.setRobot(this);
		//place it in the middle
		this.setPosition(new Point2D.Double(0,0));
	}
	
	public RobotController getController() {
		return this.controller;
	}
	
	public void setSpeed(double leftpower, double rightpower) {
		buffer.add(new Intention(SimServer.SETSPEED,leftpower,rightpower,null));
	}
	
	/** Set body facing direction in degrees, 12:00 is 0 */
	public void setBodyDirection(double d) {
		this.bodyDirection = d;
	}

	/** Returns body direction in degrees, 0 means facing 12:00.*/
	public double getBodyDirection() {
		return this.bodyDirection;
	}
	
	/** Returns max speed of the robot.*/
	public static double getMaxSpeed() {
		return MOTOR_SPEED_MAX*ROBOT_WHEEL_DIAMETER/2.0;
	}

}
