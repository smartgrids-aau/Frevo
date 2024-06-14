/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig, Sebastian Krell
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simsoccer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import components.simsoccer.model.Evaluator;
import components.simsoccer.model.MobileObject;
import components.simsoccer.model.NngaPlayer;
import components.simsoccer.model.SimPlayer;
import components.simsoccer.model.infoObject;

import core.AbstractMultiProblem;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

public class SimSoccer extends AbstractMultiProblem {

	private AbstractRepresentation[] nets = new AbstractRepresentation[2]; // player
																			// controllers
	SimServer simserver;
	/** Players in an array */
	SimPlayer[][] playersinteams;// = new SimPlayer[2][11];
	ArrayList<MobileObject> objects;// array for all mobile objects (players + ball)
	public int stepnumber; // number of total steps
	public boolean withmonitor;

	public boolean withpause = false;

	/** Team names */
	public String[] tname = new String[2];

	/** Position scores */
	public int[] scores = new int[2];
	public int[] balldistscores = new int[2];
	public int[][] kicknum;
	public int[] kicknumsum = new int[2];
	public int[] falsekicks = new int[2];
	public int[] ball_goal_scores = new int[2];

	/** Number of real scores */
	public int[] scorenum = new int[2];

	private infoObject object = new infoObject();
	public long[] finalscores = new long[2];
	public int aktStep;
	private Evaluator evaluator = new Evaluator();
	private Display display;
	protected DisplayWorker sw;

	// Stop after no_move_steps steps without action
	private static int no_move_steps = 10;

	// The weights for evaluation
	public static int POSITION_WEIGHT;
	public static int NEAREST_BALL_DISTANCE_WEIGHT;
	public static int KICK_WEIGHT;
	public static int BALL_GOAL_WEIGHT;
	public static int SCORE_WEIGHT;
	public static int PLAYERS_PER_TEAM;
	public static int EVALTIME;

	@Override
	public void replayWithVisualization(AbstractRepresentation[] candidates) {
		nets[0] = candidates[0];
		nets[0].reset();
		nets[1] = candidates[1];
		nets[1].reset();
		this.simserver = new SimServer(this);

		display = new Display(this);

		sw = new DisplayWorker();
		sw.execute();

	}

	private class DisplayWorker extends SwingWorker<Void, Integer> {

		@Override
		protected Void doInBackground() throws Exception {
			loadParameters();
			withmonitor = true;
			withpause = true;
			runSimulation();
			return null;
		}

		public void setProgressToPublish(int p) {
			publish(p);
		}

		protected void process(List<Integer> results) {
			if (withpause)
				display.updateDisplay();
		}

	}

	@Override
	public List<RepresentationWithScore> evaluateFitness(
			AbstractRepresentation[] candidates) {
		loadParameters();
		this.nets[0] = candidates[0];
		this.nets[1] = candidates[1];
		this.simserver = new SimServer(this);
		withmonitor = false;
		withpause = false;
		
		return runSimulation();
	}

	private void loadParameters() {
		POSITION_WEIGHT = Integer.parseInt(getProperties().get(
				"position_weight").getValue());
		NEAREST_BALL_DISTANCE_WEIGHT = Integer.parseInt(getProperties().get(
				"ball_distance_weight").getValue());
		KICK_WEIGHT = Integer.parseInt(getProperties().get("kick_weight")
				.getValue());
		BALL_GOAL_WEIGHT = Integer.parseInt(getProperties().get(
				"ball_goal_weight").getValue());
		SCORE_WEIGHT = Integer.parseInt(getProperties().get("score_weight")
				.getValue());
		PLAYERS_PER_TEAM = Integer.parseInt(getProperties().get(
				"playersPerTeam").getValue());
		EVALTIME = Integer.parseInt(getProperties().get("evaltime").getValue());
	}

	public List<RepresentationWithScore> runSimulation() {

		createTeamNames();
		createTeams();
		initialize();

		placePlayers();
		placeBall();
		if (withmonitor)
			sw.setProgressToPublish(aktStep);
		/* Simulator is ready */

		calculateStep();

		// Track positions to stop simulation after 10 steps without action
		Point2D.Double[][] prev_pos = new Point2D.Double[2][PLAYERS_PER_TEAM];
		for (int k = 0; k < PLAYERS_PER_TEAM; k++) // initialize positions
		{
			prev_pos[0][k] = playersinteams[0][k].position;
			prev_pos[1][k] = playersinteams[1][k].position;
		}
		int no_movement = 0;
		boolean just_fitness = false;

		if (withpause)
			pause(100);

		for (aktStep = 0; aktStep < stepnumber; aktStep++) {
			// if (saveMovie) saveFrame();

			// If nobody is moving
			if (just_fitness) {
				calcFitness();
				continue;
			}

			calculateStep();
			

			// any player moving?
			boolean no_change = true;
			for (int k = 0; k < PLAYERS_PER_TEAM; k++) {
				if (prev_pos[0][k].equals(playersinteams[0][k].position)
						&& prev_pos[1][k].equals(playersinteams[1][k].position))
					continue;

				for (int j = 0; j < PLAYERS_PER_TEAM; j++) {
					// refresh reference-values
					prev_pos[0][j] = playersinteams[0][j].position;
					prev_pos[1][j] = playersinteams[1][j].position;
					no_change = false;
				}
				break;

			}

			if (no_change)
				no_movement++;
			else
				no_movement = 0;

			if (no_movement >= no_move_steps)
				just_fitness = true;

			if (withpause)
				pause(100);

		}
		// calculate results

		if (kicknumsum[0] > 10)
			kicknumsum[0] = 10; // only the first 10 kicks count
		if (kicknumsum[1] > 10)
			kicknumsum[1] = 10;
		if (withmonitor)
			System.out.println("p. scores: " + scores[0] + "/" + scores[1]
					+ " kicknumsum: " + kicknumsum[0] + "/" + kicknumsum[1]
					+ " falsekicks: " + falsekicks[0] + "/" + falsekicks[1]
					+ " balldist: " + balldistscores[0] + "/"
					+ balldistscores[1] + " ball_goal: " + ball_goal_scores[0]
					+ "/" + ball_goal_scores[1]);
		finalscores[0] = (scores[0] * POSITION_WEIGHT)
				+ ((kicknumsum[0] * KICK_WEIGHT) - (falsekicks[0] * 10000))
				+ (scorenum[0] * SCORE_WEIGHT)
				+ (balldistscores[0] * NEAREST_BALL_DISTANCE_WEIGHT)
				+ (ball_goal_scores[0] * BALL_GOAL_WEIGHT);
		finalscores[1] = (scores[1] * POSITION_WEIGHT)
				+ ((kicknumsum[1] * KICK_WEIGHT) - (falsekicks[1] * 10000))
				+ (scorenum[1] * SCORE_WEIGHT)
				+ (balldistscores[1] * NEAREST_BALL_DISTANCE_WEIGHT)
				+ (ball_goal_scores[1] * BALL_GOAL_WEIGHT);
		if (withmonitor)
			System.out.println(finalscores[0] + "," + finalscores[1]
					+ ", kicks: " + kicknumsum[0] + " / " + kicknumsum[1]
					+ " bgdist: " + ball_goal_scores[0] + " / "
					+ ball_goal_scores[1]);

		// we have the results now
		List<RepresentationWithScore> result = new LinkedList<RepresentationWithScore>();

		RepresentationWithScore player1 = new RepresentationWithScore(nets[0],
				1);
		RepresentationWithScore player2 = new RepresentationWithScore(nets[1],
				1);

		if (finalscores[0] > finalscores[1]) {
			player2.setScore(-1);
		} else if (finalscores[0] < finalscores[1]) {
			player1.setScore(-1);
		}

		result.add(player1);
		result.add(player2);
		
		return result;

	}

	/** Creates team names (display only) */
	private void createTeamNames() {
		this.tname[0] = "Team "+nets[0].getHash();
		this.tname[1] = "Team "+nets[1].getHash();
	}

	private void createTeams() {
		playersinteams = new SimPlayer[2][PLAYERS_PER_TEAM];
		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < PLAYERS_PER_TEAM; i++) {
				playersinteams[k][i] = new SimPlayer(tname[k], k, i,
						new NngaPlayer(nets[k], this));
			}
		}
	}

	protected synchronized void pause(int ms) {
		try {
			this.wait(ms);
		} catch (InterruptedException ex) {
		}
	}

	/**
	 * Sends sensor data to the agents and collects their intentions
	 */
	private void calculateStep() {
		sendSensordata();

		// calculate intentions
		for (int k = 0; k < PLAYERS_PER_TEAM; k++) {
			playersinteams[0][k].getController().postInfo();
			playersinteams[1][k].getController().postInfo();
		}
		// calculate changes
		simserver.calculateAll();
		// update objects
		// update display
		if (withmonitor)
			sw.setProgressToPublish(aktStep);

		calcFitness();
	}

	/** Sends sensor data to the players */
	public void sendSensordata() {
		// set preinfo stage to controller
		for (int k = 0; k < PLAYERS_PER_TEAM; k++) {
			playersinteams[0][k].getController().preInfo();
			playersinteams[1][k].getController().preInfo();
		}
		// Add visual information
		for (int k = 0; k < PLAYERS_PER_TEAM; k++) {
			simserver.getVisuals((playersinteams[0][k]));
			simserver.getVisuals((playersinteams[1][k]));
		}
	}

	private void calcFitness() {
		// allocation every 5 seconds, ball distance to nearest player every 4
		// seconds, ball to goal every 2 seconds measured
		int aktmsec = aktStep * SimServer.STEPLENGTH;

		// Player distribution on the field
		if ((aktmsec % 5000) == 0) {

			object.setBall(simserver.ball.position.x, simserver.ball.position.y); // set
																					// ball

			for (int t = 0; t < 2; t++) {
				for (int p = 0; p < PLAYERS_PER_TEAM; p++) {
					object.setPlayer(t, p, playersinteams[t][p].position.x,
							playersinteams[t][p].position.y); // set players
				}
			}

			evaluator.calculate(object);

			scores[0] += evaluator.getScore(0);
			scores[1] += evaluator.getScore(1);
		}

		// Ball distance to the nearest player
		if ((aktmsec % 4000) == 0) {

			object.setBall(simserver.ball.position.x, simserver.ball.position.y); // set ball
			for (int t = 0; t < 2; t++) {
				for (int p = 0; p < PLAYERS_PER_TEAM; p++) {
					object.setPlayer(t, p, playersinteams[t][p].position.x,
							playersinteams[t][p].position.y); // set players
				}
			}

			evaluator.calculate(object);
			balldistscores[0] += evaluator.getBalldistScore(0);
			balldistscores[1] += evaluator.getBalldistScore(1);
		}

		// Ball distance to opponents goal
		if ((aktmsec % 2000) == 0) {
			double d1 = SimServer.getDistance(simserver.ball,
					simserver.RightGoal);
			double d2 = SimServer.getDistance(simserver.ball,
					simserver.LeftGoal);

			if (d1 < d2)
				ball_goal_scores[0]++;
			else if (d1 > d2)
				ball_goal_scores[1]++;
		}
	}

	private void initialize() {
		for (int s = 0; s < 2; s++) {
			scores[s] = 0;
			balldistscores[s] = 0;
			ball_goal_scores[s] = 0;
			falsekicks[s] = 0;
			scorenum[s] = 0;
		}

		kicknum = new int[2][PLAYERS_PER_TEAM];
		for (int u = 0; u < PLAYERS_PER_TEAM; u++) {
			kicknum[0][u] = 0;
			kicknum[1][u] = 0;
		}

		stepnumber = EVALTIME / SimServer.STEPLENGTH;
		object.goals[0] = simserver.LeftGoal.position;
		object.goals[1] = simserver.RightGoal.position;
	}

	public int teamname2int(String teamname) {
		if (teamname.equals(tname[0]))
			return 0;
		else if (teamname.equals(tname[1]))
			return 1;
		else
			throw new Error("Wrong team name!");
	}

	/**
	 * Places all players to the starting positions
	 */
	public void placePlayers() {
		// Left team
		playersinteams[0][0].position = new Point2D.Double(-10.0, 0.0);
		playersinteams[0][1].position = new Point2D.Double(-10.0, 10.0);
		playersinteams[0][2].position = new Point2D.Double(-10.0, -10.0);
		playersinteams[0][3].position = new Point2D.Double(-20.0, 0.0);
		playersinteams[0][4].position = new Point2D.Double(-20.0, 10.0);
		playersinteams[0][5].position = new Point2D.Double(-20.0, -10.0);
		playersinteams[0][6].position = new Point2D.Double(-20.0, 20.0);
		playersinteams[0][7].position = new Point2D.Double(-20.0, -20.0);
		playersinteams[0][8].position = new Point2D.Double(-30.0, 0);
		playersinteams[0][9].position = new Point2D.Double(-40.0, 10.0);
		playersinteams[0][10].position = new Point2D.Double(-40.0, -10.0);

		// Right Team
		playersinteams[1][0].position = new Point2D.Double(10, 0);
		playersinteams[1][1].position = new Point2D.Double(10, 10);
		playersinteams[1][2].position = new Point2D.Double(10, -10);
		playersinteams[1][3].position = new Point2D.Double(20, 0);
		playersinteams[1][4].position = new Point2D.Double(20, 10);
		playersinteams[1][5].position = new Point2D.Double(20, -10);
		playersinteams[1][6].position = new Point2D.Double(20, 20);
		playersinteams[1][7].position = new Point2D.Double(20, -20);
		playersinteams[1][8].position = new Point2D.Double(30, 0);
		playersinteams[1][9].position = new Point2D.Double(40, 10);
		playersinteams[1][10].position = new Point2D.Double(40, -10);

		// Turns + nullify speed and acceleration
		for (int i = 0; i < PLAYERS_PER_TEAM; i++) {
			playersinteams[0][i].bodyDirection = 90;
			playersinteams[1][i].bodyDirection = 270;
			playersinteams[0][i].speedVector = new Point2D.Double(0, 0);
			playersinteams[1][i].speedVector = new Point2D.Double(0, 0);
			playersinteams[0][i].accelerationVector = new Point2D.Double(0, 0);
			playersinteams[1][i].accelerationVector = new Point2D.Double(0, 0);
		}
	}

	/**
	 * Places the ball to the starting position
	 */
	public void placeBall() {
		simserver.ball.position = new Point2D.Double(0, 0);
		simserver.ball.speedVector = new Point2D.Double(0, 0);
		simserver.ball.accelerationVector = new Point2D.Double(0, 0);
	}

	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {
		// no modifications
		return requirements;
	}

}
