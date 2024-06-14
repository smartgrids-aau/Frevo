/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simplesoccer.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import main.FrevoMain;

import components.simplesoccer.SimpleServer;
import components.simplesoccer.SimpleSoccer;
import core.AbstractRepresentation;
import net.jodk.lang.FastMath;

/**
 * The controller of a player, basically this class contains the behavior of the
 * soccer player controlled by the passed representation component. The player
 * will make decision based on nearest teammate, nearest opponent, distance to
 * goal, distance to ball.
 */
public class NearestInfoPlayer implements Controller {

	protected Point2D.Double relativeBallPos = new Point2D.Double();
	protected Point2D.Double relativeNearestOppPos = new Point2D.Double();
	protected Point2D.Double relativeNearestTeamPlayerPos = new Point2D.Double();
	protected Point2D.Double relativeOppGoalPos = new Point2D.Double();
	protected Point2D.Double relativeOwnGoalPos = new Point2D.Double();
	protected double border_top;
	protected double border_bottom;
	protected double border_left;
	protected double border_right;

	protected SimPlayer skiiplayer;
	protected SimpleSoccer master;
	protected AbstractRepresentation net;
	protected ArrayList<Float> input = new ArrayList<Float>();
	protected ArrayList<Float> output = new ArrayList<Float>();
	public int kickCount = 0;

	public NearestInfoPlayer(AbstractRepresentation net, SimpleSoccer master) {
		this.net = net.clone();
		net.reset();
		this.master = master;
	}

	public SimpleSoccer getSession() {
		return this.master;
	}

	public Player getPlayer() {
		return skiiplayer;
	}

	public void setPlayer(Player p) {
		skiiplayer = (SimPlayer) p;
	}

	/** Reset the state of the controller. */
	public void preInfo() {
	}

	/** Controls the client by interpreting the state of the controller. */
	public void postInfo() {
		input.clear();
		output.clear();

		final boolean useCartesianOutput = SimpleSoccer.CARTESIANOUTPUT;

		net.reset(); // TODO reset nn STRANGE: somehow reset helps in evolution

		// Input neurons for ball
		double balldist = SimpleServer.getLength(relativeBallPos);
		addInput(balldist, relativeBallPos);

		// Input neurons for nearest team mate
		double nearestmatedist = SimpleServer
				.getLength(relativeNearestTeamPlayerPos);
		addInput(nearestmatedist, relativeNearestTeamPlayerPos);

		// Input neurons for nearest opponent
		double nearestoppdist = SimpleServer.getLength(relativeNearestOppPos);
		addInput(nearestoppdist, relativeNearestOppPos);

		// Input neurons for lines
		input.add((float) (1 / (1 + border_top)));
		input.add((float) (1 / (1 + border_bottom)));
		input.add((float) (1 / (1 + border_left)));
		input.add((float) (1 / (1 + border_right)));

		if (getPlayer().getSide() == 1) {
			// team is playing from right-to-left side,
			// input needs to be switched

			for (int i = 0; i < input.size(); i += 2) {
				float h;
				h = input.get(i);
				input.set(i, input.get(i + 1));
				input.set(i + 1, h);
			}
		}

		if ((FrevoMain.DEBUGLEVEL & 0x04) > 0) {
			System.out.println("Team:" + getPlayer().getSide() + "P:"
					+ getPlayer().getNumber() + " dist to ball:" + balldist
					+ "(" + relativeBallPos.x + "," + relativeBallPos.y + ")");
			for (int i = 0; i < input.size(); i++)
				System.out.print("i" + i + ":" + input.get(i) + " ");
			System.out.println();
		}

		if (SimpleSoccer.APPLY_STAMINA_MODEL) {
			addStaminaInput();
		}

		// calculate outputs
		output = net.getOutput(input);

		if ((FrevoMain.DEBUGLEVEL & 0x04) > 0) {
			for (int i = 0; i < output.size(); i++)
				System.out.print("o" + i + ":" + output.get(i) + " ");
			System.out.println();
		}

		// get ball speed
		double ballspeed = SimpleServer
				.getLength(this.getSession().simpleserver.ball.speedVector);

		if (useCartesianOutput) // output is given in cartesian form
		{
			double direction, power;

			double x[] = new double[4];
			for (int i = 0; i < 4; i++) {
				// scale up values to [-1.0,1.0]
				x[i] = output.get(i) * 2.0 - 1.0;

				if (getPlayer().getSide() == 1) {
					// team is playing from right-to-left side,
					// input needs to be switched
					x[i] = -x[i];
				}
			}

			if ((balldist < 0.7) && (ballspeed < 1.5)) { // we can kick the ball
				direction = FastMath.atan2(x[2], x[3]) * 180.0 / Math.PI;
				power = 100 * FastMath.sqrt(x[2] * x[2] + x[3] * x[3]);
				getPlayer().kick((int) power, direction); // scale to -1..0..1
			} else {
				direction = FastMath.atan2(x[0], x[1]) * 180.0 / Math.PI;
				power = 100 * FastMath.sqrt(x[0] * x[0] + x[1] * x[1]);
				getPlayer().dashto((int) power, direction);
			}
		} else {
			double direction;
			// output is given in polar coordinate form
			// this is more difficult for the NN
			if ((balldist < 0.7) && (ballspeed < 1.5)) { // we can kick the ball
				direction = output.get(3) * 360;
				if (getPlayer().getSide() == 1)
					direction += 180;
				getPlayer().kick((int) (output.get(2) * 100), direction); // scale
																			// to
																			// -1..0..1
			} else {
				direction = output.get(1) * 360;
				if (getPlayer().getSide() == 1)
					direction += 180;

				getPlayer().dashto((int) (output.get(0) * 100), direction);
			}
		}
	}

	protected void addStaminaInput() {
		input.add(((float) skiiplayer.getStamina()) / SimpleServer.STAMINAMAX);
	}

	protected void addInput(double distance, Point2D.Double relativepos) {
		if (distance < 0.7)
			distance = 0.7;
		// top-bottom
		if (relativepos.y <= 0) {
			input.add((float) (Math.abs(relativepos.y / FastMath.pow2(distance)))); // top
			input.add(0f); // bottom
		} else {
			input.add(0f); // top
			input.add((float) (Math.abs(relativepos.y / FastMath.pow2(distance)))); // bottom
		}

		// left-right
		if (relativepos.x <= 0) {
			input.add((float) (Math.abs(relativepos.x / FastMath.pow2(distance)))); // left
			input.add(0f); // right
		} else {
			input.add(0f); // left
			input.add((float) (Math.abs(relativepos.x / FastMath.pow2(distance)))); // right
		}
	}

	@Override
	public void setRelativePosBall(double x, double y) {
		this.relativeBallPos.x = x;
		this.relativeBallPos.y = y;
	}

	@Override
	public void setRelativePosBorders(double top, double bottom, double left,
			double right) {

		this.border_top = top;
		this.border_bottom = bottom;
		this.border_left = left;
		this.border_right = right;
	}

	@Override
	public void setRelativePosNearestOpponent(double x, double y) {
		this.relativeNearestOppPos.x = x;
		this.relativeNearestOppPos.y = y;
	}

	@Override
	public void setRelativePosNearestPlayer(double x, double y) {
		this.relativeNearestTeamPlayerPos.x = x;
		this.relativeNearestTeamPlayerPos.y = y;
	}

	@Override
	public void setRelativePosOppGoal(double x, double y) {
		this.relativeOppGoalPos.x = x;
		this.relativeOppGoalPos.y = y;
	}

	@Override
	public void setRelativePosOwnGoal(double x, double y) {
		this.relativeOwnGoalPos.x = x;
		this.relativeOwnGoalPos.y = y;
	}

}