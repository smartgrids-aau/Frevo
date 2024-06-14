package fehervari.robotsoccer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import net.jodk.lang.FastMath;
import core.AbstractMultiProblem.RepresentationWithScore;

public class SoccerServer {

	/** Width of the field in meters */
	public static final double FIELD_WIDTH = 10.5;
	/** Height of the field in meters */
	public static final double FIELD_HEIGHT = 6.8;

	public static final double BALL_ACC_MAX = 0.7;
	public static final double GOAL_DEPTH = 0.5;// 0.15;
	public static final double GOAL_WIDTH = 0.732;

	/** Contains the simulation parameters */
	private ParameterSet parameters;

	private Random generator;

	/**
	 * Indicates how often (in timesteps) should the distribution calculation
	 * take place.
	 */
	private static final int DISTRIBUTION_SCORE_INTERVAL = 30;
	/**
	 * Indicates how often (in timesteps) should the ball-player calculation
	 * take place.
	 */
	private static final int BALL_PLAYER_DISTANCE_SCORE_INTERVAL = 30;
	/** Maximum reward obtainable for ball-player distance. */
	private static final int BALL_PLAYER_DISTANCE_COEFF = 5000;
	private static final double BALL_PLAYER_DISTANCE_DECAY_RATE = FIELD_WIDTH / 5;

	private static final double BALL_GOAL_DISTANCE_COEFF = 50;

	/**
	 * Kicking coefficient regulating the maximum value that can be obtained by
	 * the number of kicks
	 */
	private static final double KICKING_COEFF = 50;

	/** Reward for each goals. */
	private static final int GOAL_COEFF = 2000000;

	/**
	 * Contains the players for both teams. First argument is the team (0 or 1),
	 * second is the player number (0 .. playersperteam-1).
	 */
	SoccerRobot[][] players;

	/** Reference to the ball. */
	Ball ball;

	static final Goal leftGoal = new Goal(0);
	static final Goal rightGoal = new Goal(1);

	/** Actual simulation step */
	long actualStep = 0;
	/** Maximum number of simulation steps */
	long stepNumber;

	/** Contains how many goals have happened so far in the game. */
	int[] goals = new int[2];
	/** Calculates the number of captured points for both sides. */
	private int[] distribution_scores = new int[2];
	private double[] ball_player_distance = new double[2];
	/** Contains the number of kicks for both teams. */
	private int[] number_of_kicks = new int[2];
	private double[] ball_goal_distance = new double[2];
	/** Final fitness points of each team. */
	private double[] fitness = new double[2];

	/** Source point from kicking */
	private Point2D kicking_source;
	/** Indicates the contribution of both teams for the last kick. */
	private boolean[] kicking_source_team = new boolean[2];
	private long lastkick_timestep = -1;

	private boolean isInterrupted = false;

	/**
	 * List containing the control points used for calculating field
	 * distribution.
	 */
	private static final ArrayList<Point2D> distributionControlPoints = new ArrayList<Point2D>() {
		private static final long serialVersionUID = -8179641868273290272L;
		{
			for (int row = 0; row < 7; row++) {
				for (int col = 0; col < 9; col++) {
					add(new Point2D.Double(-4 + col, 3 - row));
				}
			}
		}
	};

	SoccerServer(ParameterSet parameters, Random random) {
		this.parameters = parameters;
		this.generator = random;

		// create individual player controllers for each team
		createTeams();

		placePlayers();

		placeBall(null);
	}

	/** Executes the simulation. */
	public void runSimulation(JPanel display) {
		stepNumber = parameters.getSimulationTimeSec()
				* parameters.getSimulationStepsPerSecond();

		// reset fitness scores
		resetFitness();

		for (actualStep = 0; actualStep < stepNumber; actualStep++) {
			if (isInterrupted)
				break;
			// send visual information
			for (SoccerRobot robot : players[0])
				calculateSensors(robot);
			for (SoccerRobot robot : players[1])
				calculateSensors(robot);

			// process robot intentions
			for (SoccerRobot robot : players[0]) {
				robot.getController().process();
				robot.incrementKickingStamina();
			}
			for (SoccerRobot robot : players[1]) {
				robot.getController().process();
				robot.incrementKickingStamina();
			}

			// calculate physics
			calculateChanges();

			// check fitness
			calcIterativeFitness();

			// update display
			if (display != null) {
				display.repaint();

				pause(1000 / parameters.getSimulationStepsPerSecond());
			}
		}

		// calculate final fitness for both teams
		for (int t = 0; t < 2; t++) {
			// reset
			fitness[t] = 0;

			// field distribution
			fitness[t] += distribution_scores[t];

			// ball-player distance
			fitness[t] += BALL_PLAYER_DISTANCE_COEFF
					* FastMath.exp(-ball_player_distance[t]
							/ BALL_PLAYER_DISTANCE_DECAY_RATE);

			// kicking
			int kicks = number_of_kicks[t] > 10 ? 10 : number_of_kicks[t];
			if (kicks != 0)
				fitness[t] += FastMath.pow(2, kicks) * KICKING_COEFF;

			// ball-goal distance
			fitness[t] += BALL_GOAL_DISTANCE_COEFF
					* FastMath.pow3(-ball_goal_distance[t] / 2.5) + 1;

			// goals
			fitness[t] += GOAL_COEFF * goals[t];
		}

	}

	/**
	 * Resets the ball's position. If <i>robot</i> is <code>null</code> then the
	 * ball will be placed to the 0,0 position. Otherwise it will be given to
	 * the robot in the argument.
	 */
	private void placeBall(SoccerRobot robot) {
		if (ball == null)
			ball = new Ball(this);

		// initial starting position for ball
		ball.speedVector.x = 0;
		ball.speedVector.y = 0;
		ball.accelerationVector.x = 0;
		ball.accelerationVector.y = 0;

		if (robot == null) {
			ball.setPosition(new Point2D.Double(0, 0));
		} else {
			// give ball to robot
			Point2D ballpos = robot.getPosition();
			if (robot.team == 0) {
				ballpos.setLocation(
						ballpos.getX() + 0.05
								+ (robot.getDiameter() + ball.getDiameter())
								/ 2, ballpos.getY());
			} else {
				ballpos.setLocation(
						ballpos.getX() - 0.05
								- (robot.getDiameter() + ball.getDiameter())
								/ 2, ballpos.getY());
			}
			ball.setPosition(ballpos);
		}
	}

	/** Places players to their starting position */
	private void placePlayers() {
		// place according to team size, maximum 11/ team
		if (parameters.getPlayersPerTeam() > 11)
			throw new Error("Teamsize larger than 11 is not implemented!");

		for (int p = 0; p < parameters.getPlayersPerTeam(); p++) {
			// x position
			double x = 0;

			// first 3 players in front row
			if (p < 3)
				x = 1;
			// next 5 players in a second row
			else if (p < 8)
				x = 2;
			// one defender in a third row
			else if (p == 8)
				x = 3;
			// last 2 in a fourth row
			else
				x = 4;

			// y position
			double y = 0;

			if ((p == 0) || (p == 3) || (p == 8))
				y = 0;
			else if ((p == 1) || (p == 4) || (p == 9))
				y = 1;
			else if ((p == 2) || (p == 5) || (p == 10))
				y = -1;
			else if (p == 6)
				y = 2;
			else if (p == 7)
				y = -2;

			players[0][p].setPosition(new Point2D.Double(-x, y));
			players[1][p].setPosition(new Point2D.Double(x, -y));
		}

		// Turns + nullify speed and acceleration
		for (int i = 0; i < parameters.getPlayersPerTeam(); i++) {
			players[0][i].setBodyDirection(0);
			players[1][i].setBodyDirection(180);
			players[0][i].setSpeed(0.5f, 0.5f);
			players[1][i].setSpeed(0.5f, 0.5f);
		}
	}

	/** Resets the fitness */
	private void resetFitness() {
		distribution_scores[0] = 0;
		distribution_scores[1] = 0;

		ball_player_distance[0] = 0;
		ball_player_distance[1] = 0;

		number_of_kicks[0] = 0;
		number_of_kicks[1] = 0;

		ball_goal_distance[0] = 0;
		ball_goal_distance[1] = 0;

		kicking_source = null;
		kicking_source_team[0] = false;
		kicking_source_team[1] = false;

		goals[0] = 0;
		goals[1] = 0;

		fitness[0] = 0;
		fitness[1] = 0;
	}

	private void calcIterativeFitness() {
		// accumulate field distribution
		if (actualStep % DISTRIBUTION_SCORE_INTERVAL == 0) {
			for (Point2D cp : distributionControlPoints) {
				double d_t1 = getMinimumTeamDistance(0, cp);
				double d_t2 = getMinimumTeamDistance(1, cp);
				if (d_t1 < d_t2) {
					distribution_scores[0]++;
				} else if (d_t1 > d_t2)
					distribution_scores[1]++;
			}
		}

		// accumulate ball-player distance
		if (actualStep % BALL_PLAYER_DISTANCE_SCORE_INTERVAL == 0) {
			ball_player_distance[0] += getMinimumTeamDistance(0,
					ball.getPosition());
			ball_player_distance[1] += getMinimumTeamDistance(1,
					ball.getPosition());
		}

	}

	/**
	 * Returns the distance between the given point and the closest player from
	 * the provided team.
	 */
	private double getMinimumTeamDistance(int team, Point2D point) {
		double d_min = Double.MAX_VALUE;
		for (SoccerRobot robot : players[team]) {
			double d_robot = point.distance(robot.getPosition());
			if (d_robot < d_min)
				d_min = d_robot;
		}
		return d_min;
	}

	private ArrayList<Float> getWallSensorValues(SoccerRobot robot) {
		ArrayList<Float> wallSensorValues = new ArrayList<Float>(
				SoccerRobot.CAMERA_RESOLUTION);

		// reset sensor values to zero
		for (int i = 0; i < SoccerRobot.CAMERA_RESOLUTION; i++) {
			wallSensorValues.add(0f);
		}

		// robot coordinates
		double robotx = robot.getPosition().getX();
		double roboty = robot.getPosition().getY();

		// calculate dominant rays
		double[] angles = new double[SoccerRobot.CAMERA_RESOLUTION + 1];
		double fullsectionarc = (double) SoccerRobot.CAMERA_ANGLE_WALLS
				/ SoccerRobot.CAMERA_RESOLUTION;
		double leftsideangle = normalizeAngle(robot.getBodyDirection()
				+ (SoccerRobot.CAMERA_ANGLE_WALLS / 2));
		
		for (int a = 0; a < SoccerRobot.CAMERA_RESOLUTION + 1; a++) {
			angles[a] = normalizeAngle(leftsideangle - (a * fullsectionarc));
		}

		// distance to walls
		double distance_top = (FIELD_HEIGHT / 2) - roboty;
		double distance_bottom = roboty + (FIELD_HEIGHT / 2);
		double distance_left = robotx + (FIELD_WIDTH / 2);
		double distance_right = (FIELD_WIDTH / 2) - robotx;

		// add Wall
		for (int a = 0; a < angles.length; a++) {
			double ray = angles[a];
			double ray_rad = FastMath.toRadians(ray);
			double raylength;
			if ((ray >= 0) && (ray < 90)) {
				double toRightWall = Math.abs(distance_right
						/ FastMath.cos(ray_rad));
				double toTopWall = Math.abs(distance_top
						/ FastMath.sin(ray_rad));
				raylength = toRightWall < toTopWall ? toRightWall : toTopWall;
			} else if ((ray >= 90) && (ray < 180)) {
				double toLeftWall = Math.abs(distance_left
						/ FastMath.cos(ray_rad));
				double toTopWall = Math.abs(distance_top
						/ FastMath.sin(ray_rad));
				raylength = toLeftWall < toTopWall ? toLeftWall : toTopWall;
			} else if ((ray >= 180) && (ray < 270)) {
				double toLeftWall = Math.abs(distance_left
						/ FastMath.cos(ray_rad));
				double toBottomWall = Math.abs(distance_bottom
						/ FastMath.sin(ray_rad));
				raylength = toLeftWall < toBottomWall ? toLeftWall
						: toBottomWall;
			} else {
				double toRightWall = Math.abs(distance_right
						/ FastMath.cos(ray_rad));
				double toBottomWall = Math.abs(distance_bottom
						/ FastMath.sin(ray_rad));
				raylength = toRightWall < toBottomWall ? toRightWall
						: toBottomWall;
			}

			if (a == 0) {
				if (raylength > SoccerRobot.CAMERA_RANGE) {
					wallSensorValues.set(0, 0f);
				} else
					wallSensorValues.set(0,
							(float) (SoccerRobot.CAMERA_RANGE / raylength));
			} else {
				float mysensorvalue = 0f;
				if (raylength <= SoccerRobot.CAMERA_RANGE)
					mysensorvalue = (float) (SoccerRobot.CAMERA_RANGE / raylength);

				// higher sensor value (object is closer) will be saved
				if (wallSensorValues.get(a - 1) < mysensorvalue)
					wallSensorValues.set(a - 1, mysensorvalue);
			}
		}
		return wallSensorValues;
	}

	private ArrayList<Float> getRobotSensorValues(SoccerRobot robot, int team) {
		ArrayList<Float> robotSensorValues = new ArrayList<Float>(
				SoccerRobot.CAMERA_RESOLUTION);

		float[] sumsensordata = new float[SoccerRobot.CAMERA_RESOLUTION];

		for (int j = 0; j < sumsensordata.length; j++) {
			sumsensordata[j] = 0.0f;
		}

		// calculate dominant rays
		double[] angles = new double[SoccerRobot.CAMERA_RESOLUTION + 1];
		double leftsideangle = normalizeAngle(robot.getBodyDirection()
				+ (SoccerRobot.CAMERA_ANGLE_ROBOTS / 2));

		double rightsideangle = normalizeAngle(robot.getBodyDirection()
				- (SoccerRobot.CAMERA_ANGLE_ROBOTS / 2));

		double fullsectionarc = (double) SoccerRobot.CAMERA_ANGLE_ROBOTS
				/ SoccerRobot.CAMERA_RESOLUTION;

		for (int a = 0; a < SoccerRobot.CAMERA_RESOLUTION + 1; a++) {
			angles[a] = normalizeAngle(leftsideangle - (a * fullsectionarc));
		}

		// check each team mate
		for (SoccerRobot teammate : players[team]) {
			if (teammate == robot)
				continue;
			float[] sensordata = getSensorData(robot, teammate, angles,
					leftsideangle, rightsideangle);

			for (int i = 0; i < SoccerRobot.CAMERA_RESOLUTION; i++) {
				if (sensordata[i] > sumsensordata[i])
					sumsensordata[i] = sensordata[i];
			}
		}
		// add them to list
		for (int i = 0; i < sumsensordata.length; i++) {
			robotSensorValues.add(sumsensordata[i]);
		}

		return robotSensorValues;
	}

	private ArrayList<Float> getBallSensorValues(SoccerRobot robot) {
		ArrayList<Float> ballSensorValues = new ArrayList<Float>(
				SoccerRobot.CAMERA_RESOLUTION);

		// calculate dominant rays
		double[] angles = new double[SoccerRobot.CAMERA_RESOLUTION + 1];
		double leftsideangle = normalizeAngle(robot.getBodyDirection()
				+ (SoccerRobot.CAMERA_ANGLE_BALL / 2));

		double rightsideangle = normalizeAngle(robot.getBodyDirection()
				- (SoccerRobot.CAMERA_ANGLE_BALL / 2));

		double fullsectionarc = (double) SoccerRobot.CAMERA_ANGLE_BALL
				/ SoccerRobot.CAMERA_RESOLUTION;

		for (int a = 0; a < SoccerRobot.CAMERA_RESOLUTION + 1; a++) {
			angles[a] = normalizeAngle(leftsideangle - (a * fullsectionarc));
		}

		float[] sumsensordata = getSensorData(robot, ball, angles,
				leftsideangle, rightsideangle);

		for (int i = 0; i < sumsensordata.length; i++)
			ballSensorValues.add(sumsensordata[i]);

		return ballSensorValues;
	}

	/** Calculates the sensory input of the given robot */
	private void calculateSensors(SoccerRobot robot) {
		// **********************************************
		// walls
		// **********************************************
		robot.addWallSensorValues(getWallSensorValues(robot));

		// **********************************************
		// Teammates
		// **********************************************
		robot.addTeamMateSensorValues(getRobotSensorValues(robot, robot.team));

		// **********************************************
		// Opponents
		// **********************************************
		robot.addOpponentSensorValues(getRobotSensorValues(robot,
				robot.team ^ 1));

		// **********************************************
		// Ball
		// **********************************************
		robot.addBallSensorValues(getBallSensorValues(robot));

		// check if robot can kick the ball
		double d_ball = robot.getPosition().distance(ball.getPosition());
		if ((d_ball < SoccerRobot.KICKING_DISTANCE)
				&& (d_ball > (SoccerRobot.ROBOT_DIAMETER + ball.getDiameter()) / 2))
			robot.setBallInKickingRange(true);
		else
			robot.setBallInKickingRange(false);

		// **********************************************
		// Goal
		// **********************************************

		// add opponent's Goal
		Goal goal;
		if (robot.team == 0)
			goal = rightGoal;
		else
			goal = leftGoal;

		robot.addGoalSensorValues(getSensorDataForGoal(robot, goal));
	}

	private ArrayList<Float> getSensorDataForGoal(final SoccerRobot robot,
			final Goal goal) {

		ArrayList<Float> goalSensorValues = new ArrayList<Float>(
				SoccerRobot.CAMERA_RESOLUTION);

		for (int j = 0; j < SoccerRobot.CAMERA_RESOLUTION; j++) {
			goalSensorValues.add(0f);
		}

		// calculate dominant rays
		double[] angles = new double[SoccerRobot.CAMERA_RESOLUTION + 1];
		double leftsideangle = normalizeAngle(robot.getBodyDirection()
				+ (SoccerRobot.CAMERA_ANGLE_GOALS / 2));

		/*double rightsideangle = normalizeAngle(robot.getBodyDirection()
				- (SoccerRobot.CAMERA_ANGLE_GOALS / 2));*/

		double fullsectionarc = (double) SoccerRobot.CAMERA_ANGLE_GOALS
				/ SoccerRobot.CAMERA_RESOLUTION;

		for (int a = 0; a < SoccerRobot.CAMERA_RESOLUTION + 1; a++) {
			angles[a] = normalizeAngle(leftsideangle - (a * fullsectionarc));
		}

		// robot coordinates
		double robotx = robot.getPosition().getX();
		double roboty = robot.getPosition().getY();

		double distance;

		// distance based on robot's y
		if (roboty > goal.toppoint.getY()) {
			distance = new Point2D.Double(goal.getPosition().getX(),
					goal.getDiameter() / 2).distance(robot.getPosition());
		} else if (robot.getPosition().getY() > goal.bottompoint.getY()) {
			distance = Math.abs(robotx - goal.getPosition().getX());
		} else {
			distance = new Point2D.Double(goal.getPosition().getX(),
					-goal.getDiameter() / 2).distance(robot.getPosition());
		}

		if (distance > SoccerRobot.CAMERA_RANGE) {
			return goalSensorValues;
		}

		// goal is in range
		double dx = goal.toppoint.getX() - robotx;
		double dy_top = goal.toppoint.getY() - roboty;
		double dy_bottom = goal.bottompoint.getY() - roboty;
		// distances
		double d_top = goal.toppoint.distance(robot.getPosition());
		double d_bottom = goal.toppoint.distance(robot.getPosition());

		// angle between goal edges
		double angletotop = normalizeAngle(FastMath.toDegrees(FastMath.atan2(
				dy_top, dx)));
		double angletobottom = normalizeAngle(FastMath.toDegrees(FastMath
				.atan2(dy_bottom, dx)));

		// check section by section
		for (int a = 0; a < angles.length - 1; a++) {
			double closest;

			boolean isBottomIn = isAngleWithinBounds(angletobottom, angles[a],
					angles[a + 1]);
			boolean isTopIn = isAngleWithinBounds(angletotop, angles[a],
					angles[a + 1]);

			// both points are within the boundaries
			if (isBottomIn && isTopIn) {
				if (roboty > goal.bottompoint.getY()
						&& roboty < goal.toppoint.getY()) {
					closest = Math.abs(robotx - goal.getPosition().getX());
				} else
					closest = Math.min(d_top, d_bottom);
			}
			// lower point lies in cone only
			else if (isBottomIn && !isTopIn) {
				// below
				if (roboty < goal.bottompoint.getY()) {
					closest = d_bottom;
				}
				// between
				else if (roboty < goal.toppoint.getY()) {
					double directangle = goal == leftGoal ? 180 : 0;
					if (isAngleWithinBounds(directangle, angles[a],
							angles[a + 1])) {
						closest = Math.abs(robotx - goal.getPosition().getX());
					} else {
						if (goal == leftGoal) {
							closest = Math.abs(robotx
									- goal.getPosition().getX())
									/ FastMath.cos(FastMath
											.toRadians(angles[a + 1] - 180));
						} else {
							closest = Math.abs(robotx
									- goal.getPosition().getX())
									/ FastMath.cos(FastMath
											.toRadians(360 - angles[a]));
						}
					}
				}
				// above
				else {
					if (goal == leftGoal) {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(angles[a + 1] - 180));
					} else {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(360 - angles[a]));
					}
				}
			}
			// upper point lies in cone only
			else if (!isBottomIn && isTopIn) {
				// above
				if (roboty > goal.toppoint.getY()) {
					closest = d_top;
				}
				// between
				else if (roboty > goal.bottompoint.getY()) {
					double directangle = goal == leftGoal ? 180 : 0;
					if (isAngleWithinBounds(directangle, angles[a],
							angles[a + 1])) {
						closest = Math.abs(robotx - goal.getPosition().getX());
					} else {
						if (goal == leftGoal) {
							closest = Math.abs(robotx
									- goal.getPosition().getX())
									/ FastMath.cos(FastMath
											.toRadians(180 - angles[a]));
						} else {
							closest = Math.abs(robotx
									- goal.getPosition().getX())
									/ FastMath.cos(FastMath
											.toRadians(angles[a + 1]));
						}
					}
				}
				// below
				else {
					if (goal == leftGoal) {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(180 - angles[a]));
					} else {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(angles[a + 1]));
					}
				}
			}
			// no points in between : robot does not see or too close
			else {
				closest = -1;
				if (goal == leftGoal
						&& isAngleWithinBounds(angles[a], angletobottom,
								angletotop)
						&& isAngleWithinBounds(angles[a + 1], angletobottom,
								angletotop)) {
					// below
					if (roboty < goal.bottompoint.getY()) {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(180 - angles[a]));
					}
					// between
					else if (roboty < goal.toppoint.getY()) {
						if (isAngleWithinBounds(180, angles[a], angles[a + 1])) {
							closest = Math.abs(robotx
									- goal.getPosition().getX());
						} else {
							// looking downwards
							if (angles[a + 1] > 180) {
								closest = Math.abs(robotx
										- goal.getPosition().getX())
										/ FastMath
												.cos(FastMath
														.toRadians(angles[a + 1] - 180));
							}
							// looking upwards
							else {
								closest = Math.abs(robotx
										- goal.getPosition().getX())
										/ FastMath.cos(FastMath
												.toRadians(180 - angles[a]));
							}
						}
					}
					// above
					else {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(angles[a + 1] - 180));
					}
				} else if (goal == rightGoal
						&& isAngleWithinBounds(angles[a], angletotop,
								angletobottom)
						&& isAngleWithinBounds(angles[a + 1], angletotop,
								angletobottom)) {

					// below
					if (roboty < goal.bottompoint.getY()) {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(angles[a + 1]));
					}
					// between
					else if (roboty < goal.toppoint.getY()) {
						if (isAngleWithinBounds(0, angles[a], angles[a + 1])) {
							closest = Math.abs(robotx
									- goal.getPosition().getX());
						} else {
							// looking upwards
							if (angles[a + 1] < 90) {
								closest = Math.abs(robotx
										- goal.getPosition().getX())
										/ FastMath.cos(FastMath
												.toRadians(angles[a + 1]));
							}
							// looking downwards
							else {
								closest = Math.abs(robotx
										- goal.getPosition().getX())
										/ FastMath.cos(FastMath
												.toRadians(360 - angles[a]));
							}
						}
					}
					// above
					else {
						closest = Math.abs(robotx - goal.getPosition().getX())
								/ FastMath.cos(FastMath
										.toRadians(360 - angles[a]));
					}
				}
			}

			if (Double.isNaN(closest)) {
				System.err.println("Closest is NaN!");
			}
			if (closest == 0) {
				System.err.println("Closest is 0!");
			}
			// add sensor value from distance
			if (closest == -1)
				goalSensorValues.set(a,0f);
			else
				goalSensorValues.set(a,(float) (SoccerRobot.CAMERA_RANGE / closest));

		}

		return goalSensorValues;
	}

	/**
	 * Returns an array of distance data from the robot to the object for the
	 * given sections and boundary angles. This function should not be used for
	 * goals.
	 */
	private float[] getSensorData(final SoccerRobot robot,
			final FieldObject object, final double[] angles,
			final double leftsideangle, final double rightsideangle) {

		float[] result = new float[SoccerRobot.CAMERA_RESOLUTION];

		// distance between the objects
		double dist = robot.getPosition().distance(object.getPosition());

		// ignore robot if it is too far away
		if (dist > SoccerRobot.CAMERA_RANGE)
			return result;
		else if (dist < object.getDiameter() / 2) {
			// object is way too close: sees nothing
			return result;
		}

		// robot coordinates
		double robotx = robot.getPosition().getX();
		double roboty = robot.getPosition().getY();

		// target object's body radius
		double other_radius = object.getDiameter() / 2;

		double dx = object.getPosition().getX() - robotx;
		double dy = object.getPosition().getY() - roboty;

		// angle between the two robots in absolute system
		double anglebetween = normalizeAngle(FastMath.toDegrees(FastMath.atan2(
				dy, dx)));

		// angle between the tangent and the center in local relative system
		double halfangle = FastMath.toDegrees(FastMath
				.asin(other_radius / dist));

		if (Double.isNaN(halfangle)) {
			System.err.println("ERROR: halfangle d=(" + dist + ")"
					+ " target: " + object.getClass().getSimpleName());
			System.err.println("Position robot: (" + robotx + ", " + roboty
					+ ")");
			System.err.println("Position target: ("
					+ object.getPosition().getX() + ", "
					+ object.getPosition().getY() + ")");
			System.err.println("other=" + other_radius + "  dist=" + dist);

			stop();
			// System.exit(1);

		}

		// angles to tangles to other robot in absolute system
		double lefttangent = normalizeAngle(anglebetween + halfangle);
		double righttangent = normalizeAngle(anglebetween - halfangle);

		// check if robot is in visual range
		if (isAngleWithinBounds(righttangent, leftsideangle, rightsideangle)) {

			// check section by section
			for (int a = 0; a < angles.length - 1; a++) {
				// double section_left = leftsideangle

				// check if robot covers the actual section
				if ((isAngleWithinBounds(lefttangent, angles[a], angles[a + 1]) || isAngleWithinBounds(
						righttangent, angles[a], angles[a + 1]))
						|| (isAngleWithinBounds(angles[a], lefttangent,
								righttangent) && isAngleWithinBounds(
								angles[a + 1], lefttangent, righttangent))) {

					// calculate closest distance
					double closest;
					// robot is partially occluding, shortest path is the chord
					if (!isAngleWithinBounds(anglebetween, angles[a],
							angles[a + 1])) {
						// calculate closest section boundary to obtain chord
						// distance
						double diff_left_between = Math.abs(anglebetween
								- angles[a]);
						if (diff_left_between > 180)
							diff_left_between = 360 - diff_left_between;

						double diff_right_between = Math.abs(anglebetween
								- angles[a + 1]);
						if (diff_right_between > 180)
							diff_right_between = 360 - diff_right_between;

						double alpha_deg = diff_left_between < diff_right_between ? diff_left_between
								: diff_right_between;

						// sinus law
						double sin_alpha = FastMath.sin(FastMath
								.toRadians(alpha_deg));
						double beta_deg = FastMath.toDegrees(FastMath.asin(dist
								/ other_radius * sin_alpha));
						double gamma_deg = 180 - alpha_deg - beta_deg;
						closest = (other_radius * FastMath.sin(FastMath
								.toRadians(gamma_deg))) / (sin_alpha);
						if (Double.isNaN(closest)) {
							System.out.println("NAN: " + closest);
						}
					} // robot's center is inbetween section range
					else {
						closest = dist - other_radius;
					}

					if (closest == 0) {
						System.out.println("Closest is NaN");
					}

					// add sensor value from distance
					result[a] = (float) (SoccerRobot.CAMERA_RANGE / closest);

				}
			}
		}

		return result;
	}

	/** Returns true if the target angle is inbetween the given limits */
	private boolean isAngleWithinBounds(double target, double leftside,
			double rightside) {
		// get angle that rotates leftside to rightside
		double arc = normalizeAngle(leftside - rightside);
		// get angle needed to rotate leftside to target
		double targetarc = normalizeAngle(leftside - target);

		if (targetarc <= arc)
			return true;

		return false;
	}

	static double normalizeAngle(double angle) {
		angle = angle % 360;
		if (angle < 0)
			angle = 360 + angle;
		return angle % 360;
	}

	/** Calculates changes */
	private void calculateChanges() {
		// calculate all robots
		for (int team = 0; team < 2; team++)
			for (SoccerRobot robot : players[team]) {
				// consume intentions
				Intention intent = robot.buffer.remove(0);

				switch (intent.intId) {
				case Intention.SETSPEED:
					double leftspeed = intent.param1;
					double rightspeed = intent.param2;

					// re-scale power to have negative as well (backward)
					leftspeed = (leftspeed * 2 * SoccerRobot.MOTOR_SPEED_MAX)
							- SoccerRobot.MOTOR_SPEED_MAX;
					rightspeed = (rightspeed * 2 * SoccerRobot.MOTOR_SPEED_MAX)
							- SoccerRobot.MOTOR_SPEED_MAX;

					// calculate movement
					double time = 1.0 / parameters
							.getSimulationStepsPerSecond();
					double diff = leftspeed - rightspeed;
					double sum = leftspeed + rightspeed;
					double theta_speed = ((SoccerRobot.ROBOT_WHEEL_DIAMETER) / 2.0)
							/ (SoccerRobot.ROBOT_DIAMETER) * diff * 2;

					// angular change
					double newdirection = (robot.getBodyDirection() + FastMath
							.toDegrees(theta_speed * time)) % 360;
					if (newdirection < 0)
						newdirection = 360 + newdirection;
					robot.setBodyDirection(newdirection);

					double x = SoccerRobot.ROBOT_WHEEL_DIAMETER
							/ 4.0
							* sum
							* FastMath.cos(FastMath.toRadians(robot
									.getBodyDirection())) * time;

					double y = SoccerRobot.ROBOT_WHEEL_DIAMETER
							/ 4.0
							* sum
							* FastMath.sin(FastMath.toRadians(robot
									.getBodyDirection())) * time;

					// calculate new position
					Point2D newpos = new Point2D.Double(robot.getPosition()
							.getX() + x, robot.getPosition().getY() + y);

					// validate point to be inside the field
					if (!isRobotInGameField(newpos))
						break;

					// check robot collision with teammates
					boolean isColliding = false;
					double tmax = 1;
					boolean dothestep = true;
					for (SoccerRobot teammate : players[robot.team]) {
						if (teammate == robot)
							continue;
						double distance = teammate.getPosition().distance(
								newpos);
						if (distance < SoccerRobot.ROBOT_DIAMETER) {
							isColliding = true;
							double x1 = robot.getPosition().getX();
							double y1 = robot.getPosition().getY();
							double x2 = teammate.getPosition().getX();
							double y2 = teammate.getPosition().getY();
							double dmin = SoccerRobot.ROBOT_DIAMETER;
							double p = (x * (x1 - x2) + y * (y1 - y2))
									/ (FastMath.pow2(x) + FastMath.pow2(y));
							double q = (FastMath.pow2(x1 - x2)
									+ FastMath.pow2(y1 - y2) - FastMath
										.pow2(dmin))
									/ (FastMath.pow2(x) + FastMath.pow2(y));
							double t1 = -p
									+ FastMath.sqrt(FastMath.pow2(p) - q);
							double t2 = -p
									- FastMath.sqrt(FastMath.pow2(p) - q);
							if ((t1 >= 0) && (t1 < tmax))
								tmax = t1;
							if ((t2 >= 0) && (t2 < tmax))
								tmax = t2;

							Point2D newpostest = new Point2D.Double(robot
									.getPosition().getX() + x * tmax * 0.9,
									robot.getPosition().getY() + y * tmax * 0.9);
							double dtest = teammate.getPosition().distance(
									newpostest);
							if (dtest < dmin)
								dothestep = false;
						}
					}

					// check robot collision with opponents
					// if (!isColliding) {
					for (SoccerRobot opponent : players[robot.team ^ 1]) {
						double distance = opponent.getPosition().distance(
								newpos);
						if (distance < SoccerRobot.ROBOT_DIAMETER) {
							isColliding = true;
							double x1 = robot.getPosition().getX();
							double y1 = robot.getPosition().getY();
							double x2 = opponent.getPosition().getX();
							double y2 = opponent.getPosition().getY();
							double dmin = SoccerRobot.ROBOT_DIAMETER;
							double p = (x * (x1 - x2) + y * (y1 - y2))
									/ (FastMath.pow2(x) + FastMath.pow2(y));
							double q = (FastMath.pow2(x1 - x2)
									+ FastMath.pow2(y1 - y2) - FastMath
										.pow2(dmin))
									/ (FastMath.pow2(x) + FastMath.pow2(y));
							double t1 = -p
									+ FastMath.sqrt(FastMath.pow2(p) - q);
							double t2 = -p
									- FastMath.sqrt(FastMath.pow2(p) - q);
							if ((t1 >= 0) && (t1 < tmax))
								tmax = t1;
							if ((t2 >= 0) && (t2 < tmax))
								tmax = t2;

							Point2D newpostest = new Point2D.Double(robot
									.getPosition().getX() + x * tmax * 0.9,
									robot.getPosition().getY() + y * tmax * 0.9);
							double dtest = opponent.getPosition().distance(
									newpostest);
							if (dtest < dmin)
								dothestep = false;

						}
					}
					// }

					// check for the ball
					double distance = ball.getPosition().distance(newpos);
					if (distance < (SoccerRobot.ROBOT_DIAMETER + ball
							.getDiameter()) / 2) {
						isColliding = true;
						double x1 = robot.getPosition().getX();
						double y1 = robot.getPosition().getY();
						double x2 = ball.getPosition().getX();
						double y2 = ball.getPosition().getY();
						double dmin = (SoccerRobot.ROBOT_DIAMETER + ball
								.getDiameter()) / 2;
						double p = (x * (x1 - x2) + y * (y1 - y2))
								/ (FastMath.pow2(x) + FastMath.pow2(y));
						double q = (FastMath.pow2(x1 - x2)
								+ FastMath.pow2(y1 - y2) - FastMath.pow2(dmin))
								/ (FastMath.pow2(x) + FastMath.pow2(y));
						double t1 = -p + FastMath.sqrt(FastMath.pow2(p) - q);
						double t2 = -p - FastMath.sqrt(FastMath.pow2(p) - q);
						if ((t1 >= 0) && (t1 < tmax))
							tmax = t1;
						if ((t2 >= 0) && (t2 < tmax))
							tmax = t2;

						Point2D newpostest = new Point2D.Double(robot
								.getPosition().getX() + x * tmax * 0.9, robot
								.getPosition().getY() + y * tmax * 0.9);
						double dtest = ball.getPosition().distance(newpostest);
						if (dtest < dmin)
							dothestep = false;

					}

					if (!isColliding)
						robot.setPosition(newpos);
					else {
						if (tmax == 1)
							tmax = 0;
						if (dothestep) {
							Point2D newposshort = new Point2D.Double(robot
									.getPosition().getX() + x * tmax * 0.9,
									robot.getPosition().getY() + y * tmax * 0.9);
							robot.setPosition(newposshort);
						}
					}
					break;

				case Intention.KICK:
					// secondary check to avoid cheating controllers
					double d_ball = robot.getPosition().distance(
							ball.getPosition());
					if (d_ball < SoccerRobot.KICKING_DISTANCE) {
						// kicking is possible
						double power = intent.param1;
						double kickdirection = intent.param2;

						if (power > 0) {
							power *= BALL_ACC_MAX;

							double l = power;

							Point2D.Double accVector = new Point2D.Double(l
									* FastMath.cos(FastMath
											.toRadians(kickdirection)), l
									* FastMath.sin(FastMath
											.toRadians(kickdirection)));

							ball.addAccVector(accVector);

							// increase kick number for results
							number_of_kicks[robot.team]++;

							if (lastkick_timestep == actualStep) {
								// someone else already kicked it in this
								// timestep, add this robot's team's
								// contribution
								kicking_source_team[robot.team] = true;
							} else {
								// new kick
								lastkick_timestep = actualStep;
								// this team contributes
								kicking_source_team[robot.team] = true;
								// other team might not
								kicking_source_team[robot.team ^ 1] = false;

								if (kicking_source != null) {
									// there was a previous kick, evaluate
									// distance rolled
									evaluateBallDistance();
								}
								// register new kicking source
								kicking_source = new Point2D.Double(ball
										.getPosition().getX(), ball
										.getPosition().getY());
							}

						}
					}

					break;
				}
			}

		// calculate ball
		calculateBall();

		// calculate if goal happened, place the ball to random player of
		// the "losing" side
		int ballPosRes = isPointInGoalArea(ball.getPosition());
		// reset player positions
		if (ballPosRes != 0) {
			placePlayers();
		}
		if (ballPosRes == -1) {
			// right team scored a goal
			goals[1]++;
			placeBall(players[0][generator.nextInt(players[0].length)]);
			// System.out.println("Right team scored!");
		} else if (ballPosRes == 1) {
			// left team scored a goal
			goals[0]++;
			placeBall(players[1][generator.nextInt(players[1].length)]);
			// System.out.println("Left team scored!");
		}

	}

	/**
	 * Checks if the given point lies in any of the goal areas. Returns -1 if it
	 * is in the left side, 1 if the right one and 0 if point is outside of
	 * these areas.
	 */
	private static int isPointInGoalArea(Point2D point) {
		double x = point.getX();
		double y = point.getY();

		// check left
		if ((x < -FIELD_WIDTH / 2) && (x > -FIELD_WIDTH / 2 - GOAL_DEPTH)
				&& (y < GOAL_WIDTH) && (y > -GOAL_WIDTH)) {
			return -1;
		}

		// check right
		if ((x > FIELD_WIDTH / 2) && (x < FIELD_WIDTH / 2 + GOAL_DEPTH)
				&& (y < GOAL_WIDTH) && (y > -GOAL_WIDTH)) {
			return 1;
		}

		// point is not in any goal
		return 0;
	}

	/** Returns true if given point is within the field */
	private boolean isPointInGameField(Point2D point) {

		if (point.getX() < -FIELD_WIDTH / 2)
			return false;
		if (point.getX() > FIELD_WIDTH / 2)
			return false;
		if (point.getY() < -FIELD_HEIGHT / 2)
			return false;
		if (point.getY() > FIELD_HEIGHT / 2)
			return false;

		return true;
	}

	/** Returns true if given point is within the field */
	private boolean isRobotInGameField(Point2D point) {

		if (point.getX() < (-FIELD_WIDTH / 2)
				+ (SoccerRobot.ROBOT_DIAMETER / 2))
			return false;
		if (point.getX() > (FIELD_WIDTH / 2) - (SoccerRobot.ROBOT_DIAMETER / 2))
			return false;
		if (point.getY() < (-FIELD_HEIGHT / 2)
				+ (SoccerRobot.ROBOT_DIAMETER / 2))
			return false;
		if (point.getY() > (FIELD_HEIGHT / 2)
				- (SoccerRobot.ROBOT_DIAMETER / 2))
			return false;

		return true;
	}

	private void calculateBall() {
		// ball physics
		Point2D oldposition = ball.getPosition();
		Point2D.Double oldspeedvector = ball.speedVector;

		Point2D.Double newpos = new Point2D.Double(oldposition.getX()
				+ oldspeedvector.x, oldposition.getY() + oldspeedvector.y);

		// check collision (bumping effect, care for goal)
		if (!isPointInGameField(newpos) && (isPointInGoalArea(newpos) == 0)) {
			// ball bumps back
			if ((newpos.getX() > FIELD_WIDTH / 2)
					|| (newpos.getX() < -FIELD_WIDTH / 2)) {
				ball.speedVector.x *= -1;
			}

			if ((newpos.getY() > FIELD_HEIGHT / 2)
					|| (newpos.getY() < -FIELD_HEIGHT / 2)) {
				ball.speedVector.y *= -1;
			}
		} else {
			// no collision
			ball.setPosition(newpos);
		}

		// apply decay
		ball.applydecay();
		// reset acceleration
		ball.accelerationVector = new Point2D.Double(0, 0);

	}

	/** Returns the result of the simulation with descending fitness. */
	public List<RepresentationWithScore> getResults() {
		// return real results
		List<RepresentationWithScore> results = new ArrayList<RepresentationWithScore>();
		
		if (fitness[0] > fitness[1]) {
			results.add(new RepresentationWithScore(parameters.nets[0], fitness[0]));
			results.add(new RepresentationWithScore(parameters.nets[1], fitness[1]));
		} else {
			results.add(new RepresentationWithScore(parameters.nets[1], fitness[1]));
			results.add(new RepresentationWithScore(parameters.nets[0], fitness[0]));
		}
		
		return results;
	}

	/** Initialize and create soccer robots and their controllers */
	private void createTeams() {
		players = new SoccerRobot[2][parameters.getPlayersPerTeam()];

		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < parameters.getPlayersPerTeam(); i++) {
				players[k][i] = new SoccerRobot(k, i, new EvolvedController(
						parameters.nets[k].clone()));
			}
		}
	}

	protected synchronized void pause(int ms) {
		try {
			this.wait(ms);
		} catch (InterruptedException ex) {
		}
	}

	public synchronized void stop() {
		this.isInterrupted = true;
	}

	/** Evaluates ball distance */
	void evaluateBallDistance() {
		if (kicking_source_team[0]) {
			double dbg_before = kicking_source
					.distance(rightGoal.getPosition());
			double dbg_after = ball.getPosition().distance(
					rightGoal.getPosition());

			double dbg_diff = dbg_after - dbg_before;

			ball_goal_distance[0] += dbg_diff;
		}
		if (kicking_source_team[1]) {
			double dbg_before = kicking_source.distance(leftGoal.getPosition());
			double dbg_after = ball.getPosition().distance(
					leftGoal.getPosition());

			double dbg_diff = dbg_after - dbg_before;

			ball_goal_distance[1] += dbg_diff;
		}

	}
}
