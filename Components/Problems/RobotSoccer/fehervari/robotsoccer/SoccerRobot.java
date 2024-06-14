package fehervari.robotsoccer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** Soccer robot class */
public class SoccerRobot extends FieldObject {

	/** Current facing angle */
	private double bodyDirection;

	/** Buffer for intentions */
	public List<Intention> buffer = new LinkedList<Intention>();

	/** Team ID, 0 for left, 1 for right */
	public final int team;
	/** Number of the robot within the team */
	public final int number;

	// ---------------Robot parameters---------------------------
	/** Motor maximum angular speed */
	public static final double MOTOR_SPEED_MAX = 20;
	/** Robot base diameter */
	public static final double ROBOT_DIAMETER = 0.25;
	/** Robot wheel diameter */
	public static final double ROBOT_WHEEL_DIAMETER = 0.125;
	/** Camera range */
	public static final double CAMERA_RANGE = 6;
	
	/** Camera angle detecting robots in degree */
	public static final double CAMERA_ANGLE_ROBOTS = 135;
	/** Camera angle detecting walls in degree */
	public static final double CAMERA_ANGLE_WALLS = 360;
	/** Camera angle detecting goals in degree */
	public static final double CAMERA_ANGLE_GOALS = 360;
	/** Camera angle detecting the ball in degree */
	public static final double CAMERA_ANGLE_BALL = 135;
	
	/** Camera resolution, number of distinct sections */
	public static final int CAMERA_RESOLUTION = 4;
	/** Maximum distance in which the robot is able kick the ball */
	public static final double KICKING_DISTANCE = 0.25;
	/** Minimum number of timesteps needed between kicks */
	private static final int KICK_RECOVERY_INTERVAL = 5;


	private int stamina = KICK_RECOVERY_INTERVAL;

	/** Controller of the robot */
	private RobotController controller;

	/** List of sensory input values */
	private ArrayList<Float> sensorValuesWall;
	public ArrayList<Float> getSensorValuesWall() {
		return sensorValuesWall;
	}

	public ArrayList<Float> getSensorValuesTeamMates() {
		return sensorValuesTeamMates;
	}

	public ArrayList<Float> getSensorValuesOpponents() {
		return sensorValuesOpponents;
	}

	public ArrayList<Float> getSensorValuesBall() {
		return sensorValuesBall;
	}

	public ArrayList<Float> getSensorValuesGoal() {
		return sensorValuesGoal;
	}

	private ArrayList<Float> sensorValuesTeamMates;
	private ArrayList<Float> sensorValuesOpponents;
	private ArrayList<Float> sensorValuesBall;
	private ArrayList<Float> sensorValuesGoal;

	/** Indicates if this robot can kick the ball */
	private boolean ballinRange = false;

	public SoccerRobot(int team, int number, RobotController controller) {
		this.controller = controller;
		this.controller.setRobot(this);
		this.team = team;
		this.number = number;
	}

	public RobotController getController() {
		return this.controller;
	}

	public void setBodyDirection(double bodydirection) {
		this.bodyDirection = bodydirection;
	}

	public double getBodyDirection() {
		return bodyDirection;
	}

	/**
	 * Sets the speed of the motors. Power is interpreted on a 0..1 scale where
	 * 1 is full power forwards while 0 is the same just backwards. 0.5 will
	 * issue a 0 power. This action happens immediately.
	 */
	public void setSpeed(float leftpower, float rightpower) {
		buffer.add(new Intention(Intention.SETSPEED, leftpower, rightpower,
				null));
	}

	/**
	 * Kicks the ball with the given power towards the given direction. Power
	 * scales from 0..1 while direction is -1..0..1 where 0 is to kick the ball
	 * in the facing direction, -1 and 1 is to kick exactly backwards.
	 */
	public void kick(double power, double direction) {
		// convert power
		if (power > 1)
			power = 1;
		else if (power < 0)
			power = 0;

		// convert direction
		if (direction > 1)
			direction = 1;
		else if (direction < -1)
			direction = -1;

		double newdirection = SoccerServer.normalizeAngle(bodyDirection
				+ direction * 180);

		buffer.add(new Intention(Intention.KICK, power, newdirection, null));
		stamina = 0;
	}

	public void addBallSensorValues(ArrayList<Float> sensorValues) {
		this.sensorValuesBall = sensorValues;
	}

	public void addTeamMateSensorValues(ArrayList<Float> sensorValues) {
		this.sensorValuesTeamMates = sensorValues;
	}

	public void addOpponentSensorValues(ArrayList<Float> sensorValues) {
		this.sensorValuesOpponents = sensorValues;
	}

	public void addWallSensorValues(ArrayList<Float> sensorValues) {
		this.sensorValuesWall = sensorValues;
	}
	
	public void addGoalSensorValues(ArrayList<Float> sensorValues) {
		this.sensorValuesGoal = sensorValues;
	}

	public double getDiameter() {
		return ROBOT_DIAMETER;
	}

	public void incrementKickingStamina() {
		if (stamina != KICK_RECOVERY_INTERVAL)
			stamina++;
	}

	public void setBallInKickingRange(boolean inrange) {
		ballinRange = inrange;
	}

	/** Returns true if this robot can kick the ball. */
	public boolean canKick() {
		if ((ballinRange) && (stamina == KICK_RECOVERY_INTERVAL))
			return true;
		
		return false;
	}
}
