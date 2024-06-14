/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simsoccer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import net.jodk.lang.FastMath;

import components.simsoccer.model.Ball;
import components.simsoccer.model.Controller;
import components.simsoccer.model.FieldObject;
import components.simsoccer.model.Goal;
import components.simsoccer.model.Intention;
import components.simsoccer.model.Line;
import components.simsoccer.model.MobileObject;
import components.simsoccer.model.SimPlayer;
import components.simsoccer.model.VisibleObject;

/**
 * 
 * @author Istvan Fehervari MSc Simulation server faking the rcsserver to have
 *         faster training session
 * 
 */
public class SimServer {

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
	public static final double GOAL_WIDTH = 14.02;
	// ------------Communication constants
	public static final int KICK = 100;
	public static final int DASH = 101;
	public static final int MOVE = 102;
	public static final int TURN = 103;
	public static final int TURNNECK = 104;
	public static final int SAY = 105;
	public static final int SCORE = 106;

	// Simulation objects
	public Ball ball = new Ball(new Point2D.Double(0, 0));
	public Goal LeftGoal = new Goal(new Point2D.Double(-55, 0), 0);
	public Goal RightGoal = new Goal(new Point2D.Double(55, 0), 1);
	public static Line TopLine = new Line(0);
	public static Line RightLine = new Line(1);
	public static Line BottomLine = new Line(2);
	public static Line LeftLine = new Line(3);
	public SimSoccer master;
	/**
	 * An integer like side: 0 left, 1 right, containing the side who kicked the
	 * ball last time
	 */
	public int whokickedlast;

	public SimServer(SimSoccer master) {
		this.master = master;
		initialize();
	}

	private void initialize() {
		LeftGoal.side = 0;
		RightGoal.side = 1;
	}

	/**
	 * The full cycle of the simulation model: understanding intentions, moving
	 * players, calculating stamina and collision
	 */
	public void calculateAll() {
		for (int j = 0; j < SimSoccer.PLAYERS_PER_TEAM; j++) {
			calcint(master.playersinteams[0][j]);
			calcint(master.playersinteams[1][j]);
		}

		for (int j = 0; j < SimSoccer.PLAYERS_PER_TEAM; j++) {
			calcmodel(master.playersinteams[0][j]);
			calcmodel(master.playersinteams[1][j]);

		}
		calcmodel(ball);

		// CHANGES: collision model has been causing unresolvable situations,
		// thus it is disabled here and moved to the movement processing part
		// calcCollision();

		// check whether the ball is out of the field
		checkball();
		// check whether we have the ball in the goal area
		checkforgoal();
	}

	/**
	 * Checks the ball position, if it is out of the field it will be placed to
	 * the border
	 */
	private void checkball() {
		Point2D.Double pos = ball.position;
		if (!ValidatePoint(pos)) {
			master.falsekicks[whokickedlast]++;

			if (pos.y < -(FIELDDIM.y / 2))
				ball.position.y = 1 - (FIELDDIM.y / 2);
			else if (pos.y > (FIELDDIM.y / 2))
				ball.position.y = (FIELDDIM.y / 2) - 1;
			else if (pos.x < -(FIELDDIM.x / 2))
				ball.position.x = 1 - (FIELDDIM.x / 2);
			else if (pos.x > (FIELDDIM.x / 2))
				ball.position.x = (FIELDDIM.x / 2) - 1;
		}

	}

	/**
	 * Checks whether the ball is in the goal zone, if it is then places
	 * everything to the starting position
	 */
	private void checkforgoal() {
		if ((isBallinGoal(LeftGoal)) || (isBallinGoal(RightGoal))) { // OR
			master.placePlayers();
			master.placeBall();
		}

	}

	private boolean isBallinGoal(Goal goal) {

		if ((ball.position.y >= ((-1) * (GOAL_WIDTH / 2)))
				&& (ball.position.y <= ((GOAL_WIDTH / 2)))) {
			if (goal.getSide() == 0) { // left side
				if ((ball.position.x <= goal.position.x)
						&& (ball.position.x >= (goal.position.x - 3.5))) { // depths
					return true;
				}
				return false;
			}
			// right side
			if ((ball.position.x >= goal.position.x)
					&& (ball.position.x <= (goal.position.x + 3.5))) { // depths
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Checks all players whether they are colloiding with eachother or with the
	 * ball
	 */
	/*private void calcCollision() {
		boolean wasCol = true;

		while (wasCol) {
			wasCol = false;
			for (int k = 0; k < 21; k++) {
				for (int i = k + 1; i < 22; i++) {
					if (checkcollision(getColPlayer(k), getColPlayer(i)))
						wasCol = true;
				}
			}
			for (int b = 0; b < 22; b++) {
				if (checkcollision(getColPlayer(b), ball))
					wasCol = true;
			}

		}
	}*/

	/**
	 * Special function to return all the players for collision detection.
	 * 
	 * @param num
	 *            E.g. for 11 players 0-10 return players from team 1, 11-21 team 2
	 * @return
	 */
	/*private SimPlayer getColPlayer(int num) {
		if (num < SimSoccer.PLAYERS_PER_TEAM)
			return master.playersinteams[0][num];
		return master.playersinteams[1][num - SimSoccer.PLAYERS_PER_TEAM];
	}*/

	/**
	 * Checks the collision between two players, if they overlap they will be
	 * moved back then velocities will be multiplied by -1
	 * 
	 * @param simPlayer
	 * @param simPlayer2
	 * @return true if there were a collision
	 */
	/*private static boolean checkcollision(MobileObject p1, MobileObject p2) {
		double distance = getDistance(p1, p2);
		double twosize;
		if (p2.getType() == 2)
			twosize = SimServer.BALLSIZE + SimServer.PLAYERSIZE;
		else
			twosize = (SimServer.PLAYERSIZE * 2);

		// if they colloide
		if (distance < (twosize)) {
			// move them to not interlap (touch+lot more+ random)
			double shiftlength = ((twosize - distance) / 2 + 3); // amount to be
																	// moved
																	// each
																	// player
			double shiftprop = shiftlength / distance;
			double shiftx = (-1) * (p2.position.x - p1.position.x) * shiftprop;
			double shifty = (-1) * (p2.position.y - p1.position.y) * shiftprop;
			p1.position = new Point2D.Double(p1.position.x + shiftx,
					p1.position.y + shifty);
			// multiply the vectors with -1
			p1.speedVector = new Point2D.Double(p1.speedVector.x * (-1),
					p1.speedVector.y * (-1));
			return true;
		}

		return false;
	}*/

	/**
	 * Calculates the positions regarding the vectors, applies decay, sets
	 * acceleration to zero
	 */
	private static void calcmodel(MobileObject obj) {
		// move obj
		Point2D.Double pos = new Point2D.Double(obj.position.x
				+ obj.speedVector.x, obj.position.y + obj.speedVector.y);
		
		// check if the new position does not collide 
		obj.position = pos;

		// apply decay
		obj.applydecay();
		// set acc to zero
		obj.accelerationVector = new Point2D.Double(0, 0);
	}

	/**
	 * Calculates intention for a given player: MOVE, TURN, DASH, KICK
	 * 
	 * @param player
	 */
	private void calcint(SimPlayer player) {
		Intention intent = consumeIntention(player);
		switch (intent.intId) {
		case SimServer.MOVE: { // this happens instantly
			player.setPosition(new Point2D.Double(intent.param1, intent.param2));
			break;
		}
		case SimServer.TURN: {
			double moment = intent.param1;
			if (moment > SimServer.MAXMOMENT)
				moment = SimServer.MAXMOMENT;
			else if (moment < SimServer.MINMOMENT)
				moment = SimServer.MINMOMENT;

			double speed = getLength(player.speedVector);
			if (speed != 0) {
				moment = moment / (1 + SimServer.INERTIAMOMENT * speed);
			}
			player.setBodyDirection((player.bodyDirection + moment) % 360);
			break;
		}
		case SimServer.DASH: { // set the vectors
			// calculate edp (effective dash power)
			double power = intent.param1;
			// If power is too small do nothing
			if (power > SimServer.MINPOWER) {
				if (power > SimServer.MAXPOWER)
					power = SimServer.MAXPOWER;
				// power = player.consumeStamina(power); TODO Stamina model
				double edp = player.getEffort() * SimServer.DASHPOWERRATE
						* power;
				Point2D.Double accvect = getPlayerAccVector(player, edp);
				// add new vector and normalize it
				player.addAccVector(accvect);

			}
			break;
		}
		case SimServer.KICK: {
			// check whether the player is close enough to kick
			double distance = (getDistance(player, ball) - (SimServer.PLAYERSIZE + SimServer.BALLSIZE));
			if ((distance <= SimServer.KICKABLE_MARGIN) && (distance > 0)) {
				// player is not off the field
				if (ValidatePoint(player.position)) {
					double power = intent.param1;
					double kickdirection = intent.param2;
					if (power > SimServer.MINPOWER) {
						if (power > MAXPOWER)
							power = MAXPOWER;
						double ep = power * KICK_POWER_RATE;
						double relangle = getRelativeangle(player, ball);
						ep = ep
								* (1 - (0.25 * (relangle / 180)) - 0.25 * (distance / KICKABLE_MARGIN));
						Point2D.Double accvect = makeBallAccVector(ball, ep,
								kickdirection);
						ball.addAccVector(accvect);
						// increase kick number for results
						master.kicknumsum[player.getSide()]++;
						whokickedlast = player.getSide();
					}
				}
			}
			break;
		}
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
		return absdirection - player.bodyDirection;
	}

	/**
	 * Returns a normalized acceleration vector for players
	 * 
	 * @param player
	 * @param edp
	 * @return
	 */
	private static Double getPlayerAccVector(SimPlayer player, double edp) {
		double direction = player.bodyDirection;
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

	private static Double makeBallAccVector(Ball ball, double ep,
			double direction) {
		double l = BALL_ACC_MAX / ep;
		Point2D.Double Accvector = new Point2D.Double(l
				* FastMath.sin(FastMath.toRadians(direction)), l * (-1)
				* FastMath.cos(FastMath.toRadians(direction)));
		return Accvector;
	}

	/**
	 * Puts the visual data to the controller. So far only ball, goals, lines
	 * and team/opponent players are reported.
	 * 
	 * @param c
	 *            the Controller to be used
	 */
	public void getVisuals(SimPlayer player) {
		// Team players go to the visible objects array
		// Ball
		checkSeen(player, ball);
		// Team players
		for (int i = 0; i < SimSoccer.PLAYERS_PER_TEAM; i++) {
			if (player != master.playersinteams[player.side][i])
				checkSeen(player, master.playersinteams[player.side][i]);
		}
		// Opponents
		for (int i = 0; i < SimSoccer.PLAYERS_PER_TEAM; i++) {
			checkSeen(player,
					master.playersinteams[player.getOppositeSide()][i]);
		}

		// Goals
		checkSeen(player, LeftGoal);
		checkSeen(player, RightGoal);
		// Lines
		checklines(player);
	}

	private static void checklines(SimPlayer player) {
		Controller c = player.getController();
		double incl = FastMath.tan(FastMath
				.toRadians(player.bodyDirection - 90));
		// intersection with top line:
		double intsy = (FIELDDIM.y / (-2));
		double intsx = (intsy - player.position.y) / incl;
		Point2D.Double IntsPointTopLine = new Point2D.Double(intsx, intsy);

		// intersection with bottom line:
		intsy = FIELDDIM.y / 2;
		intsx = (intsy - player.position.y) / incl;
		Point2D.Double IntsPointBottomLine = new Point2D.Double(intsx, intsy);

		// intersection with right line
		intsx = FIELDDIM.x / 2;
		intsy = (incl * intsx) + player.position.y;
		Point2D.Double IntsPointRightLine = new Point2D.Double(intsx, intsy);

		// intersection with left line
		intsx = FIELDDIM.x / (-2);
		intsy = (incl * intsx) + player.position.y;
		Point2D.Double IntsPointLeftLine = new Point2D.Double(intsx, intsy);

		if (ValidatePoint(IntsPointLeftLine))
			c.infoSeeLine(getDistance(player.position, IntsPointLeftLine));
		if (ValidatePoint(IntsPointTopLine))
			c.infoSeeLine(getDistance(player.position, IntsPointTopLine));
		if (ValidatePoint(IntsPointBottomLine))
			c.infoSeeLine(getDistance(player.position, IntsPointBottomLine));
		if (ValidatePoint(IntsPointRightLine))
			c.infoSeeLine(getDistance(player.position, IntsPointRightLine));

	}

	/**
	 * Returns false if the point is outside of the field boundaries
	 * 
	 * @param point
	 * @return
	 */
	private static boolean ValidatePoint(Point2D.Double point) {
		if (point.y < -(FIELDDIM.y / 2))
			return false;
		else if (point.y > (FIELDDIM.y / 2))
			return false;
		else if (point.x < -(FIELDDIM.x / 2))
			return false;
		else if (point.x > (FIELDDIM.x / 2))
			return false;
		else
			return true;
	}

	/**
	 * Returns true if the given player is inside the boundaries
	 */
	/*
	 * private boolean isPlayerInField (SimPlayer player) { if
	 * (((-1)*(FIELDDIM.x/2) < player.position.x) && (player.position.x <
	 * (FIELDDIM.x/2))) { if (((-1)*(FIELDDIM.y/2) < player.position.y) &&
	 * (player.position.y < (FIELDDIM.y/2))) return true; else return false; }
	 * else return false; }
	 */
	/**
	 * Returns true if obj1 (player) sees obj2 and sends it to the controller
	 */
	private static boolean checkSeen(SimPlayer obj1, FieldObject obj2) {
		Controller c = obj1.getController();
		boolean isseen = false;
		double obj1dir = obj1.bodyDirection;
		double distance = getDistance(obj1, obj2);
		// calculate vector pointing from obj1 to obj2
		Point2D.Double vector = new Point2D.Double(obj2.position.x
				- obj1.position.x, obj2.position.y - obj1.position.y);
		double vectorlength = getLength(vector);
		// the direction required to point exactly to the object
		double direction = FastMath.toDegrees(FastMath.acos(((-1) * (vector.y))
				/ vectorlength));
		if (vector.x < 0)
			direction = 360 - direction;

		if (distance <= VISIBLEDISTANCE) { // in the close environment
			isseen = true;
		} else {
			if ((obj1dir - (VISIBLEANGLE / 2) <= direction)
					&& (direction <= (obj1dir + (VISIBLEANGLE / 2))))
				isseen = true; // it is in the range
			else
				isseen = false;
		}
		double relativedir = direction - obj1dir;
		// add data to controller
		if (isseen) {
			// ball
			if (obj2.getType() == 2)
				c.infoSeeBall(distance, relativedir);
			// player
			if (obj2.getType() == 1) {
				if ((distance < (TEAMTOOFARLENGTH + TEAMFARLENGTH) / 2)) {
					VisibleObject object = new VisibleObject();
					object.direction = relativedir;
					object.distance = distance;

					if (obj1.getSide() == ((SimPlayer) (obj2)).getSide()) {
						object.team = 0;
					} else
						object.team = 1;
					c.addItem(object);
				}
			}
			// goal
			if (obj2.getType() == 4) {
				if (((Goal) obj2).side == 0)
					c.setGoalLeft(distance, relativedir);
				if (((Goal) obj2).side == 1)
					c.setGoalRight(distance, relativedir);
			}
		}
		return isseen;
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
