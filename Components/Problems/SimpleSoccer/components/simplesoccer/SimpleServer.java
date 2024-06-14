/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simplesoccer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import net.jodk.lang.FastMath;

import components.simplesoccer.model.Ball;
import components.simplesoccer.model.FieldObject;
import components.simplesoccer.model.Goal;
import components.simplesoccer.model.Intention;
import components.simplesoccer.model.Line;
import components.simplesoccer.model.MobileObject;
import components.simplesoccer.model.NearestInfoPlayer;
import components.simplesoccer.model.OmnidirectionalPlayer;
import components.simplesoccer.model.SimPlayer;

/**
 * Some concepts of this simulation server are described in http://dangermouse.brynmawr.edu/pyrobot/tars/rcsoccersim.pdf
 * 
 * @author Istvan Fehervari MSc Simulation server for a simple soccer game
 */
public class SimpleServer {

	public static int wilsCounter = 0;

	// -----Server parameters-------
	/**
	 * Real-time length of one time step in ms
	 */
	public static final int STEPLENGTH = 100;
	public static final double BALLSIZE = 0.085;
	public static final double PLAYERSIZE = 0.3;
	public static final double VISIBLEANGLE = 90;
	public static final double VISIBLEDISTANCE = 3;
	public static final double UNUMFARLENGTH = 20;
	public static final double UNUMTOOFARLENGHT = 40;
	public static final double TEAMFARLENGTH = 40;
	public static final double TEAMTOOFARLENGTH = 60;
	public static final double MINPOWER = -100;
	public static final double MAXPOWER = 100;
	public static final Point FIELDDIM = new Point(108, 68);
	public static final int STAMINAMAX = 4000;
	public static final double RECOVERY_MIN = 0.5;
	public static final double DASHPOWERRATE = 0.006;
	public static final double EFFORTMIN = 0.6;
	public static final double EFFORTMAX = 1;
	public static final double PLAYER_ACC_MAX = 1;
	public static final double PLAYER_SPEED_MAX = 1.2;
	public static final double PLAYER_DECAY = 0.4;
	public static final double BALL_DECAY = 0.94;
	public static final double MINMOMENT = -180;
	public static final double MAXMOMENT = 180;
	public static final double INERTIAMOMENT = 5;
	public static final double KICKABLE_MARGIN = 0.7;
	public static final double KICK_POWER_RATE = 0.027;
	public static final double BALL_ACC_MAX = 2.7;
	public static final double BALL_SPEED_MAX = 2.7;
	public static final double GOAL_WIDTH = 14.02;// was 34.02
	
	// the stamina model
	public static final double STAMINA_INC_MAX = 45;
	public static final double RECOVER_DEC_THR = 0.3;
	public static final double RECOVER_DEC = 0.002;
	public static final double EFFORT_DEC_THR = 0.3;
	public static final double EFFORT_INC_THR = 0.6;
	public static final double EFFORT_DEC = 0.005;
	public static final double EFFORT_INC = 0.01;
	
	// ------------Communication constants
	public static final int KICK = 100;
	public static final int DASH = 101;
	public static final int MOVE = 102;
	public static final int TURN = 103;
	public static final int TURNNECK = 104;
	public static final int SAY = 105;
	public static final int SCORE = 106;
	public static final int DASHTO = 107;

	// Simulation objects
	public Ball ball = new Ball(new Point2D.Double(0, 0));
	public Goal leftGoal = new Goal(new Point2D.Double(-55, 0), 0);
	public Goal rightGoal = new Goal(new Point2D.Double(55, 0), 1);
	public static Line TopLine = new Line(0);
	public static Line RightLine = new Line(1);
	public static Line BottomLine = new Line(2);
	public static Line LeftLine = new Line(3);
	public SimpleSoccer master;
	/**
	 * An integer like side: 0 left, 1 right, containing the side who kicked the
	 * ball last time
	 */
	public int whokickedlast;

	public SimpleServer(SimpleSoccer master) {
		this.master = master;
		initialize();
	}

	private void initialize() {
		leftGoal.side = 0;
		rightGoal.side = 1;
	}

	/**
	 * The full cycle of the simulation model: understanding intentions, moving
	 * players, calculating stamina and collision
	 */
	public void calculateAll() {
		for (int j = 0; j < SimpleSoccer.PLAYERS_PER_TEAM; j++) {
			calcint(master.playersinteams[0][j]);
			calcint(master.playersinteams[1][j]);
		}

		for (int j = 0; j < SimpleSoccer.PLAYERS_PER_TEAM; j++) {
			calcmodel(master.playersinteams[0][j]);
			calcmodel(master.playersinteams[1][j]);

		}
		calcmodel(ball);
		// calcCollision(); //removed collision model

		// check whether we have the ball in the goal area
		checkforgoal();

		// check whether the ball is out of the field
		checkball();

	}

	/**
	 * Checks the ball position, if it is out of the field it will be placed to
	 * the border
	 */
	private void checkball() {
		Point2D.Double pos = ball.position;
		if (!ValidatePoint(pos)) {
			// master.placeBall();
			// master.placePlayers();
			master.falsekicks[whokickedlast]++;
			ball.speedVector = new Point2D.Double(0.0, 0.0);
			if (pos.y < -(FIELDDIM.y / 2))
				ball.position.y = 1 - (FIELDDIM.y / 2);
			else if (pos.y > (FIELDDIM.y / 2))
				ball.position.y = (FIELDDIM.y / 2) - 1;

			// except for the goal, the field has a border
			if ((ball.position.y < ((-1) * (GOAL_WIDTH / 2)))
					|| (ball.position.y > ((GOAL_WIDTH / 2)))) {
				if (pos.x < -(FIELDDIM.x / 2))
					ball.position.x = 1 - (FIELDDIM.x / 2);
				else if (pos.x > (FIELDDIM.x / 2))
					ball.position.x = (FIELDDIM.x / 2) - 1;
			}
		}

	}

	/**
	 * Checks whether the ball is in the goal zone, if it is then places
	 * everything to the starting position
	 */
	private void checkforgoal() {
		if ((isBallinGoal(leftGoal)) || (isBallinGoal(rightGoal))) {
			master.placePlayers();
			master.resetBall();
			// System.out.println
			// ("GOAL in game "+master.nets[0].getUniqueName()+" vs. "+master.nets[1].getUniqueName()+"!!!!");
		}

	}

	private boolean isBallinGoal(Goal goal) {

		if ((ball.position.y >= ((-1) * (GOAL_WIDTH / 2)))
				&& (ball.position.y <= ((GOAL_WIDTH / 2)))) {
			if (goal.getSide() == 0) { // left side
				if ((ball.position.x <= goal.position.x) /*
														 * && (ball.position.x
														 * >=
														 * (goal.position.x-3.5
														 * ))
														 */) { // depths
					master.scorenum[1]++;
					return true;
				}
				return false;
			}
			// right side
			if ((ball.position.x >= goal.position.x) /*
													 * && (ball.position.x <=
													 * (goal.position.x+3.5))
													 */) { // depths
				master.scorenum[0]++;
				return true;
			}
			return false;

		}
		return false;
	}

	/**
	 * Checks all players whether they are colliding with each other or with the
	 * ball
	 */
	/*
	 * private void calcCollision() { boolean wasCol = true;
	 * 
	 * /* while (wasCol) { wasCol = false; for (int k = 0;k<21;k++) { for (int i
	 * = k+1;i<22;i++) { if (checkcollision(getColPlayer(k),getColPlayer(i)))
	 * wasCol = true; } } for (int b=0;b<22;b++) { if
	 * (checkcollision(getColPlayer(b),ball) ) wasCol = true ; }
	 * 
	 * } }
	 */

	/**
	 * Special function to return all the players for collision detection.
	 * 
	 * @param num
	 *            0-10 return players from team 1, 11-21 team 2
	 * @return
	 */
	/*
	 * private SimPlayer getColPlayer (int num) { if (num <
	 * SimpleSoccer.PLAYERS_PER_TEAM) return master.playersinteams[0][num]; else
	 * { return master.playersinteams[1][num-SimpleSoccer.PLAYERS_PER_TEAM]; } }
	 */

	/**
	 * Checks the collision between two players, if they overlap they will be
	 * moved back then velocities will be multiplied by -1
	 * 
	 * @param simPlayer
	 * @param simPlayer2
	 * @return true if there were a collision
	 */
	/*
	 * private boolean checkcollision(MobileObject p1, MobileObject p2) { double
	 * distance = getDistance (p1,p2); double twosize; if (p2.getType() == 2)
	 * twosize = SimpleServer.BALLSIZE+SimpleServer.PLAYERSIZE; else twosize =
	 * (SimpleServer.PLAYERSIZE*2);
	 * 
	 * //if they colloide if (distance < (twosize) ) { //move them to not
	 * overlap (touch+lot more+ random) double shiftlength =
	 * ((twosize-distance)/2+3); //amount to be moved each player double
	 * shiftprop = shiftlength/distance; double shiftx =
	 * (-1)*(p2.position.x-p1.position.x)*shiftprop; double shifty =
	 * (-1)*(p2.position.y-p1.position.y)*shiftprop; p1.position = new
	 * Point2D.Double (p1.position.x+shiftx , p1.position.y+shifty); //multiply
	 * the vectors with -1 p1.speedVector = new
	 * Point2D.Double(p1.speedVector.x*(-1),p1.speedVector.y*(-1)); return true;
	 * } else return false; }
	 */

	/**
	 * Calculates the positions regarding the vectors, applies decay, sets
	 * acceleration to zero
	 */
	private void calcmodel(MobileObject obj) {
		Point2D.Double oldposition = obj.position;
		Point2D.Double oldspeedvector = obj.speedVector;

		Point2D.Double pos = new Point2D.Double(obj.position.x
				+ obj.speedVector.x, obj.position.y + obj.speedVector.y);
		boolean colloided = false;
		if (ValidatePoint(pos)) { // object is within boundary
			// check collision
			if (obj.getType() == 2) { // ball should not go into players
				for (int i = 0; i < SimpleSoccer.PLAYERS_PER_TEAM; i++) {
					if ((getDistance(pos, master.playersinteams[0][i].position) < (PLAYERSIZE + BALLSIZE))
							|| (getDistance(pos,
									master.playersinteams[1][i].position) < (PLAYERSIZE + BALLSIZE))) {
						colloided = true;
						break;
					}
				}
			} else if (obj.getType() == 1) { // players should not go into other
												// players
				for (int i = 0; i < SimpleSoccer.PLAYERS_PER_TEAM; i++) {
					if ((getDistance(pos, master.playersinteams[0][i].position) < (PLAYERSIZE * 2))
							&& (obj != master.playersinteams[0][i])) {
						colloided = true;
						break;
					} else if ((getDistance(pos,
							master.playersinteams[1][i].position) < (PLAYERSIZE * 2))
							&& (obj != master.playersinteams[1][i])) {
						colloided = true;
						break;
					}
				}
			}
			colloided = false;
			if (!colloided)
				obj.position = pos;
			else
				obj.speedVector = new Point2D.Double(0, 0); // stop if collided
			/*
			 * //apply decay obj.applydecay(); //set acc to zero
			 * obj.accelerationVector = new Point2D.Double(0,0);
			 */
		}

		// apply decay
		obj.applydecay();
		// set acc to zero
		obj.accelerationVector = new Point2D.Double(0, 0);

		if ((java.lang.Double.isNaN(obj.position.x))
				|| (java.lang.Double.isNaN(obj.position.y))) {
			System.err.println("x:" + oldposition.x + " y:" + oldposition.y
					+ " speedvector_x:" + oldspeedvector.x + " y:"
					+ oldspeedvector.y);
			System.err.println("Error while processing object type "
					+ obj.getType() + "! position or speed is NaN");

		}

	}

	/**
	 * Calculates intention for a given player: MOVE, TURN, DASH, KICK
	 * 
	 * @param player
	 */
	private void calcint(SimPlayer player) {
		Intention intent = consumeIntention(player);
		switch (intent.intId) {
		case SimpleServer.MOVE: { // this happens instantly
			player.setPosition(new Point2D.Double(intent.param1, intent.param2));
			break;
		}
		case SimpleServer.TURN: {
			double moment = intent.param1;
			if (moment > SimpleServer.MAXMOMENT)
				moment = SimpleServer.MAXMOMENT;
			else if (moment < SimpleServer.MINMOMENT)
				moment = SimpleServer.MINMOMENT;

			double speed = getLength(player.speedVector);
			if (speed != 0) {
				moment = moment / (1 + SimpleServer.INERTIAMOMENT * speed);
			}
			player.setBodyDirection((player.getBodyDirection() + moment) % 360);
			break;
		}
		case SimpleServer.DASH: { // set the vectors
			// calculate edp (effective dash power)
			double power = intent.param1;
			// If power is too small do nothing
			if (power > SimpleServer.MINPOWER) {
				if (power > SimpleServer.MAXPOWER)
					power = SimpleServer.MAXPOWER;
				// power = player.consumeStamina(power);
				double edp = player.getEffort() * SimpleServer.DASHPOWERRATE
						* power;
				Point2D.Double accvect = getPlayerAccVector(player, edp);
				// add new vector and normalize it
				player.addAccVector(accvect);

			}
			break;
		}
		case SimpleServer.DASHTO: {// it is a turn and a dash together
			// first turn
			double moment = intent.param2;// direction

			if (java.lang.Double.isNaN(moment)) {
				System.err
						.println("Error while processing dashto intention, moment is NaN");			
			}

			player.setBodyDirection(moment % 360);
			// then dash
			// calculate edp (effective dash power)
			double power = intent.param1;
			// If power is too small do nothing
			if (power < SimpleServer.MINPOWER)
				power = SimpleServer.MINPOWER;
			if (power > SimpleServer.MAXPOWER)
				power = SimpleServer.MAXPOWER;

			double edp = player.getEffort() * SimpleServer.DASHPOWERRATE
					* power; // effort = 1 always
			Point2D.Double accvect = getPlayerAccVector(player, edp);
			// add new vector and normalize it
			player.addAccVector(accvect);
			
			if (SimpleSoccer.APPLY_STAMINA_MODEL) {
				// dash reduces the stamina value
				player.consumeStamina(player.getEffort() * power);
			}
			break;
		}
		case SimpleServer.KICK: {
			// check whether the player is close enough to kick
			double distance = (getDistance(player, ball) - (SimpleServer.PLAYERSIZE + SimpleServer.BALLSIZE));
			if (distance <= SimpleServer.KICKABLE_MARGIN) {
				// player is not off the field
				if (ValidatePoint(player.position)) {
					double power = intent.param1;
					double kickdirection = intent.param2;
					if (power != 0) {
						if (power < SimpleServer.MINPOWER)
							power = MINPOWER;
						if (power > SimpleServer.MAXPOWER)
							power = MAXPOWER;
						if ((java.lang.Double.isNaN(ball.speedVector.x))
								|| (java.lang.Double.isNaN(ball.speedVector.y))) {
							System.err
									.println("Stop before applying stuff to ball pos");
						}
						double ep = player.getEffort() * power * KICK_POWER_RATE;
						double relangle = getRelativeangle(player, ball);
						ep = ep
								* (1 - (0.25 * (relangle / 180)) - 0.25 * (distance / KICKABLE_MARGIN));
						Point2D.Double accvect = makeBallAccVector(ball, ep,
								kickdirection);
						ball.addAccVector(accvect);
						// increase kick number for results
						master.kicknumsum[player.getSide()]++;
						whokickedlast = player.getSide();
						if ((java.lang.Double.isNaN(ball.speedVector.x))
								|| (java.lang.Double.isNaN(ball.speedVector.y))) {
							System.err
									.println("Stop after applying stuff to ball pos, power:"
											+ power);
						}
						
						if (SimpleSoccer.APPLY_STAMINA_MODEL) {
							// kick reduces the stamina value
							player.consumeStamina(player.getEffort() * power);			
						}						
					}
				}
			}
			break;
		}
		}
		
		if (SimpleSoccer.APPLY_STAMINA_MODEL) {
			// if stamina is below recovery decrement threshold, recovery is reduced
			if (player.getStamina() <= RECOVER_DEC_THR * STAMINAMAX ) {
				player.setRecovery(player.getRecovery() - RECOVER_DEC);			
			}		
			
			// if stamina is below effort decrement threshold, effort is reduced
			if (player.getStamina() <= EFFORT_DEC_THR * STAMINAMAX) {
				player.setEffort(player.getEffort() - EFFORT_DEC);
			}
			
			// if stamina is above effort increment threshold, effort is increased
			if (player.getStamina() >= EFFORT_INC_THR*STAMINAMAX) {
				player.setEffort(player.getEffort() + EFFORT_INC);			
			}
			
			// recovery the stamina a bit 
			player.setStamina((int)(player.getStamina() + player.getRecovery() * STAMINA_INC_MAX));
		}
	}
	
	/**
	 * Returns the relative direction of the ball to the player
	 * 
	 * @param player
	 * @param ball
	 * @return
	 */
	private static double getRelativeangle(SimPlayer player, Ball ball) {
		// calculate vector pointing from player to ball
		Point2D.Double vector = new Point2D.Double(ball.position.x
				- player.position.x, ball.position.y - player.position.y);
		double vectorlength = getLength(vector);
		// the direction required to point exactly to the object
		double absdirection = FastMath.toDegrees(FastMath
				.acos(((-1) * (vector.y)) / vectorlength));
		if (vector.x < 0)
			absdirection = 360 - absdirection;
		return absdirection - player.getBodyDirection();
	}

	/**
	 * Returns a normalized acceleration vector for players
	 * 
	 * @param player
	 * @param edp
	 * @return
	 */
	private static Double getPlayerAccVector(SimPlayer player, double edp) {
		double direction = player.getBodyDirection();
		Point2D.Double Accvector = new Point2D.Double(edp
				* FastMath.sin(FastMath.toRadians(direction)), edp * (-1)
				* FastMath.cos(FastMath.toRadians(direction)));
		if (getLength(Accvector) > PLAYER_ACC_MAX) {
			// normalize
			double newlength = PLAYER_ACC_MAX / edp;
			Accvector = new Point2D.Double(Accvector.x * newlength, Accvector.y
					* newlength);
			return Accvector;
		}
		
		return Accvector;
	}

	private static Double makeBallAccVector(Ball ball, double ep, double direction) {
		double l = BALL_ACC_MAX / ep; // handle if ep is 0!
		Point2D.Double Accvector = new Point2D.Double(l
				* FastMath.sin(FastMath.toRadians(direction)), l * (-1)
				* FastMath.cos(FastMath.toRadians(direction)));
		return Accvector;
	}

	/**
	 * Puts the visual data to the controller.
	 */
	public void getVisuals(SimPlayer player) {
		double x = player.position.x;
		double y = player.position.y;
		
		// Ball
		player.getController().setRelativePosBall(ball.position.x - x,
				ball.position.y - y);

		if (player.getController() instanceof NearestInfoPlayer) {
			// Team players
			SimPlayer nearestTeamPlayer = null;
			if (player != master.playersinteams[player.side][0])
				nearestTeamPlayer = master.playersinteams[player.side][0];
			else
				nearestTeamPlayer = master.playersinteams[player.side][1];
			double distance = getDistance(player, nearestTeamPlayer);
			for (int i = 0; i < SimpleSoccer.PLAYERS_PER_TEAM; i++) {
				if (player != master.playersinteams[player.side][i]) {
					double newdistance = getDistance(player,
							master.playersinteams[player.side][i]);
					if ((newdistance < distance)
							&& (player != master.playersinteams[player.side][i])) {
						nearestTeamPlayer = master.playersinteams[player.side][i];
						distance = newdistance;
					}
				}
			}
			player.getController().setRelativePosNearestPlayer(
					nearestTeamPlayer.position.x - x,
					nearestTeamPlayer.position.y - y);

			// Opponents
			SimPlayer nearestOppPlayer = master.playersinteams[player
					.getOppositeSide()][0];
			double oppdistance = getDistance(player, nearestOppPlayer);
			for (int i = 1; i < SimpleSoccer.PLAYERS_PER_TEAM; i++) {
				double newdistance = getDistance(player,
						master.playersinteams[player.getOppositeSide()][i]);
				if (newdistance < oppdistance) {
					nearestOppPlayer = master.playersinteams[player
							.getOppositeSide()][i];
					oppdistance = newdistance;
				}
			}
			player.getController().setRelativePosNearestOpponent(
					nearestOppPlayer.position.x - x,
					nearestOppPlayer.position.y - y);
		} else if (player.getController() instanceof OmnidirectionalPlayer) {
			// team and opponent pointers are added at team creation time. Controller reads necessary data directly.
		} else {
			System.err.println ("FATAL ERROR: Cannot determine player controller while passing visual inputs!");
		}
		

		// Goals
		if (player.side == 0) {
			player.getController().setRelativePosOppGoal(
					this.rightGoal.position.x - x,
					this.rightGoal.position.y - y);
			player.getController().setRelativePosOwnGoal(
					this.leftGoal.position.x - x, this.leftGoal.position.y - y);
		} else {
			player.getController().setRelativePosOwnGoal(
					this.rightGoal.position.x - x,
					this.rightGoal.position.y - y);
			player.getController().setRelativePosOppGoal(
					this.leftGoal.position.x - x, this.leftGoal.position.y - y);
		}

		// Lines
		player.getController().setRelativePosBorders((FIELDDIM.y / 2) + y,
				(FIELDDIM.y / 2) - y, (FIELDDIM.x / 2) + x,
				(FIELDDIM.x / 2) - x);
	}

	/**
	 * Returns false if the point is outside of the field boundaries
	 * 
	 * @param point
	 */
	private static boolean ValidatePoint(Point2D.Double point) {
		if (point.y < -(FIELDDIM.y / 2))
			return false;
		else if (point.y > (FIELDDIM.y / 2))
			return false;

		// except for the goal, the field has a border
		if ((point.y < ((-1) * (GOAL_WIDTH / 2)))
				|| (point.y > ((GOAL_WIDTH / 2)))) {
			if (point.x < -(FIELDDIM.x / 2))
				return false;
			else if (point.x > (FIELDDIM.x / 2))
				return false;
		}
		return true;
	}

	public static double getDistance(FieldObject obj1, FieldObject obj2) {
		return FastMath.hypot(obj1.position.x - obj2.position.x,
				obj1.position.y - obj2.position.y);
	}

	public static double getDistance(Point2D.Double p1, Point2D.Double p2) {
		return FastMath.hypot(p1.x - p2.x, p1.y - p2.y);
	}

	/**
	 * Calculates the length of an origo-based vector
	 * 
	 * @return
	 */
	public static double getLength(Point2D.Double p1) {
		return FastMath.hypot(p1.x, p1.y);
	}

	private static Intention consumeIntention(SimPlayer player) {
		Intention intent;
		if (player.buffer.size() == 0) {
			intent = null;
			System.out.println("no intention");
		} else {
			intent = player.buffer.remove(0);
		}
		return intent;
	}
}
