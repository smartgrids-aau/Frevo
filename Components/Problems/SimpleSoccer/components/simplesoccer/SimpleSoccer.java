/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig, Sebastian Krell
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simplesoccer;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import main.FrevoMain;

import components.simplesoccer.model.Evaluator;
import components.simplesoccer.model.InfoObject;
import components.simplesoccer.model.NearestInfoPlayer;
import components.simplesoccer.model.OmnidirectionalPlayer;
import components.simplesoccer.model.SimPlayer;

import core.AbstractMultiProblem;
import core.AbstractRepresentation;
import core.ComponentType;
import core.ProblemXMLData;
import core.XMLFieldEntry;
import fullyMeshedNet.FullyMeshedNet;

/**
 * NOTES: Origo of the field is in the middle!
 */
public class SimpleSoccer extends AbstractMultiProblem {

	/** Representations for each team */
	public AbstractRepresentation[] nets = new AbstractRepresentation[2];

	/** Soccer simulation server */
	public SimpleServer simpleserver;
	/**
	 * 2D array of players players. First parameter is the team, second is the
	 * player number
	 */
	public SimPlayer[][] playersinteams;
	/** Number of total simulation steps */
	public int stepnumber;
	/** Indicates if the display should be turned on */
	public boolean withmonitor;
	/**
	 * Indicates if the game should be paused between each step to allow visual
	 * representation
	 */
	public boolean withpause;

	/**
	 * Indicates if the game should be saved as a set of images
	 */
	public boolean withsave;

	/** Team names */
	public String[] tname = new String[2];

	/** Position scores */
	public int[] field_distribution_scores = new int[2];
	public int[] balldistscores = new int[2];
	//public int[][] kicknum; //= new int[2][11];
	public int[] kicknumsum = new int[2];
	public int[] falsekicks = new int[2];
	public int[] ball_goal_scores = new int[2];

	/** Number of real scores */
	public int[] scorenum = new int[2];

	private InfoObject object;
	public long[] finalscores = new long[2];
	public int aktStep;
	private Evaluator evaluator;
	private SimpleDisplay display;
	protected DisplayWorker sw;
	public boolean runSimulation = true;

	// Stop after no_move_steps steps without action
	private static final int no_move_steps = 10;

	// The weights for evaluation
	public static int POSITION_WEIGHT;
	public static int NEAREST_BALL_DISTANCE_WEIGHT;
	public static int KICK_WEIGHT;
	public static int BALL_GOAL_WEIGHT;
	public static int SCORE_WEIGHT;
	public static int PLAYERS_PER_TEAM;
	public static int EVALTIME;
	public static boolean CARTESIANOUTPUT;
	public static int MAX_NUMBER_OF_KICKS = 10;
	public static boolean APPLY_STAMINA_MODEL = false;
	public static PlayerModel playerModel = PlayerModel.NEARESTINFOPLAYER;

	@Override
	public void replayWithVisualization(AbstractRepresentation[] candidates) {
		this.nets[0] = candidates[0];
		this.nets[1] = candidates[1];
		this.simpleserver = new SimpleServer(this);

		display = new SimpleDisplay(this);

		sw = new DisplayWorker();
		withpause = true;
		withmonitor = true;
		sw.execute();

	}

	// Adjust requirement

	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {

		XMLFieldEntry inputnumber = requirements.get("inputnumber");
		XMLFieldEntry player_model = getProperties().get("controller_model");

		// set default
		int in = 16;

		if (player_model != null) {
			PlayerModel pm = PlayerModel.valueOf(player_model.getValue());

			if (pm == PlayerModel.OMNIDIRECTIONALINFOPLAYER) {
				in = 4 + 4 + 1 + OmnidirectionalPlayer.SECTIONS
						+ OmnidirectionalPlayer.SECTIONS;
			} else if (pm == PlayerModel.NEARESTINFOPLAYER) {
				in = 16;
			}
		}

		XMLFieldEntry applyStaminaEntry = properties.get("apply_stamina_model");

		if (applyStaminaEntry != null) {
			boolean applyStamina = Boolean.parseBoolean(applyStaminaEntry
					.getValue());
			if (applyStamina) {
				// stamina model adds one more input
				in++;
			}
		}

		inputnumber.setValue(Integer.toString(in));

		return requirements;
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
			display.updateDisplay();
		}

	}

	@Override
	public List<RepresentationWithScore> evaluateFitness(
			final AbstractRepresentation[] candidates) {
		// load parameters
		loadParameters();

		// assign controller representations
		this.nets[0] = candidates[0];
		this.nets[1] = candidates[1];

		// create new server
		this.simpleserver = new SimpleServer(this);

		// turn off display
		withpause = false;
		withmonitor = false;

		// run simulation and return fitness
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
		EVALTIME = Integer.parseInt(getProperties().get("evaluation_time")
				.getValue());
		CARTESIANOUTPUT = Boolean.parseBoolean(getProperties().get(
				"isCartesian_interpretation").getValue());

		XMLFieldEntry maxKicksEntry = getProperties().get("max_kicks");
		// we have to check that this entry really exists
		if (maxKicksEntry != null) {
			MAX_NUMBER_OF_KICKS = Integer.parseInt(maxKicksEntry.getValue());
		}

		XMLFieldEntry applyStaminaEntry = getProperties().get(
				"apply_stamina_model");
		// we have to check that this entry really exists
		if (applyStaminaEntry != null) {
			APPLY_STAMINA_MODEL = Boolean.parseBoolean(applyStaminaEntry
					.getValue());
		}

		// load player model
		XMLFieldEntry player_model = getProperties().get("controller_model");
		if (player_model != null) {
			playerModel = PlayerModel.valueOf(player_model.getValue());
		}
	}

	public List<RepresentationWithScore> runSimulation() {

		// reset scores
		for (int i = 0; i < 2; i++) {
			field_distribution_scores[i] = 0;
			ball_goal_scores[i] = 0;
			balldistscores[i] = 0;
			kicknumsum[i] = 0;
		}

		// create team names based on their side
		createTeamNames();

		// create individual player controllers for each team
		createTeams();

		// init
		resetScores();

		// place players
		placePlayers();

		// place ball
		resetBall();

		// publish actual progress (0)
		if (withmonitor)
			sw.setProgressToPublish(aktStep);

		/* Simulator is ready */

		calculateStep();

		// Track positions to stop simulation after some steps without action

		Point2D.Double[][] prev_pos = new Point2D.Double[2][PLAYERS_PER_TEAM];
		for (int k = 0; k < PLAYERS_PER_TEAM; k++) // initialize positions
		{
			prev_pos[0][k] = playersinteams[0][k].position;
			prev_pos[1][k] = playersinteams[1][k].position;
		}

		int no_movement_counter = 0;
		boolean skip_calculation = false;

		if (withpause)
			pause(100);

		// go through all simulation steps
		for (aktStep = 0; aktStep < stepnumber; aktStep++) {
			while (!runSimulation) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// save frame if needed
			if (withsave) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
				while (display.saving) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
			}
			// saveFrame();

			// If nobody is moving
			if (skip_calculation) {
				calcFitness();
				// TODO consume intentions?
				// continue; ?
				break;
			}

			// any player moving?
			boolean no_change = true;
			for (int k = 0; k < PLAYERS_PER_TEAM; k++) {
				if (prev_pos[0][k].equals(playersinteams[0][k].position)
						&& prev_pos[1][k].equals(playersinteams[1][k].position))
					continue;

				// there was a change
				for (int j = 0; j < PLAYERS_PER_TEAM; j++) {
					// update reference-values
					prev_pos[0][j] = playersinteams[0][j].position;
					prev_pos[1][j] = playersinteams[1][j].position;
					no_change = false;
				}
				break;

			}

			if (no_change)
				no_movement_counter++;
			else
				// reset counter
				no_movement_counter = 0;

			if (no_movement_counter >= no_move_steps)
				skip_calculation = true;

			calculateStep();
			if (withpause)
				pause(100); // pause 100 it was
		}
		// calculate results

		if (kicknumsum[0] > MAX_NUMBER_OF_KICKS)
			kicknumsum[0] = MAX_NUMBER_OF_KICKS; // only the first 10 kicks
													// count
		if (kicknumsum[1] > MAX_NUMBER_OF_KICKS)
			kicknumsum[1] = MAX_NUMBER_OF_KICKS;

		if (withmonitor)
			System.out.println("p. scores: " + field_distribution_scores[0]
					+ "/" + field_distribution_scores[1] + " kicknumsum: "
					+ kicknumsum[0] + "/" + kicknumsum[1] + " falsekicks: "
					+ falsekicks[0] + "/" + falsekicks[1] + " balldist: "
					+ balldistscores[0] + "/" + balldistscores[1]
					+ " ball_goal: " + ball_goal_scores[0] + "/"
					+ ball_goal_scores[1]);

		finalscores[0] = (field_distribution_scores[0] * POSITION_WEIGHT)
				+ (kicknumsum[0] - falsekicks[0] * 2) * KICK_WEIGHT
				+ (scorenum[0] * SCORE_WEIGHT)
				+ (balldistscores[0] * NEAREST_BALL_DISTANCE_WEIGHT)
				+ (ball_goal_scores[0] * BALL_GOAL_WEIGHT);

		finalscores[1] = (field_distribution_scores[1] * POSITION_WEIGHT)
				+ (kicknumsum[1] - falsekicks[1] * 2) * KICK_WEIGHT
				+ (scorenum[1] * SCORE_WEIGHT)
				+ (balldistscores[1] * NEAREST_BALL_DISTANCE_WEIGHT)
				+ (ball_goal_scores[1] * BALL_GOAL_WEIGHT);

		if ((FrevoMain.DEBUGLEVEL & 0x08) > 0)
			System.out.println("POSITION_WEIGHT=" + POSITION_WEIGHT
					+ " KICK_WEIGHT=" + KICK_WEIGHT + " SCORE_WEIGHT="
					+ SCORE_WEIGHT + " NEAREST_BALL_DISTANCE_WEIGHT="
					+ NEAREST_BALL_DISTANCE_WEIGHT + " BALL_GOAL_WEIGHT="
					+ BALL_GOAL_WEIGHT);

		if (withmonitor)
			System.out.println(finalscores[0] + "," + finalscores[1]
					+ ", kicks: " + kicknumsum[0] + " / " + kicknumsum[1]
					+ " bgdist: " + ball_goal_scores[0] + " / "
					+ ball_goal_scores[1] + " score: " + scorenum[0] + " / "
					+ scorenum[1]);

		if (withmonitor) {
			System.out.println("1st team: " + finalscores[0] + "(final) = "
					+ scorenum[0] + "(score)*" + SCORE_WEIGHT + " + "
					+ ball_goal_scores[0] + "(goal)*" + BALL_GOAL_WEIGHT
					+ " + " + balldistscores[0] + "(ball_distance)*"
					+ NEAREST_BALL_DISTANCE_WEIGHT + " + "
					+ (kicknumsum[0] - falsekicks[0] * 2) + "(kick)*"
					+ KICK_WEIGHT + " + " + field_distribution_scores[0]
					+ "(position)*" + POSITION_WEIGHT);

			System.out.println("2nd team: " + finalscores[1] + "(final) = "
					+ scorenum[1] + "(score)*" + SCORE_WEIGHT + " + "
					+ ball_goal_scores[1] + "(goal)*" + BALL_GOAL_WEIGHT
					+ " + " + balldistscores[1] + "(ball_distance)*"
					+ NEAREST_BALL_DISTANCE_WEIGHT + " + "
					+ (kicknumsum[1] - falsekicks[1] * 2) + "(kick)*"
					+ KICK_WEIGHT + " + " + field_distribution_scores[1]
					+ "(position)*" + POSITION_WEIGHT);
		}

		// we have the results now
		List<RepresentationWithScore> result = new LinkedList<RepresentationWithScore>();

		RepresentationWithScore player1 = new RepresentationWithScore(nets[0],
				1);
		RepresentationWithScore player2 = new RepresentationWithScore(nets[1],
				1);

		player1.setHiddenFitness(finalscores[0]);
		player2.setHiddenFitness(finalscores[1]);
		
		if (finalscores[0] > finalscores[1]) {
			player2.setScore(-1);
			result.add(player1);
			result.add(player2);
		} else if (finalscores[0] < finalscores[1]) {
			player1.setScore(-1);
			result.add(player2);
			result.add(player1);
		} else {
			// tie, order should not matter
			result.add(player1);
			result.add(player2);
		}

		return result;
	}

	/** Creates the respective team names */
	private void createTeamNames() {
		this.tname[0] = "Team A (" + nets[0].getHash() + ")";
		this.tname[1] = "Team B (" + nets[1].getHash() + ")";
	}

	/**
	 * Creates the team with their respective controllers
	 * */
	private void createTeams() {
		playersinteams = new SimPlayer[2][PLAYERS_PER_TEAM];
		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < PLAYERS_PER_TEAM; i++) {
				if (playerModel == PlayerModel.NEARESTINFOPLAYER) {
					playersinteams[k][i] = new SimPlayer(tname[k], k, i,
							new NearestInfoPlayer(nets[k], this));
				} else if (playerModel == PlayerModel.OMNIDIRECTIONALINFOPLAYER) {
					playersinteams[k][i] = new SimPlayer(tname[k], k, i,
							new OmnidirectionalPlayer(nets[k], this));
				} else {
					System.err
							.println("FATAL ERROR: Unspecified player controller: "
									+ playerModel);
				}
			}
		}

		// add teammates and opponents
		if (playerModel == PlayerModel.OMNIDIRECTIONALINFOPLAYER) {
			for (int k = 0; k < 2; k++) {
				for (int i = 0; i < PLAYERS_PER_TEAM; i++) {
					SimPlayer player = playersinteams[k][i];
					OmnidirectionalPlayer player_controller = (OmnidirectionalPlayer) (player
							.getController());

					// teammates
					ArrayList<SimPlayer> teammates = new ArrayList<SimPlayer>();
					for (int ii = 0; ii < SimpleSoccer.PLAYERS_PER_TEAM; ii++) {
						SimPlayer tplayer = playersinteams[k][ii];
						if (tplayer != player) {
							teammates.add(tplayer);
						}
					}

					player_controller.setTeamPlayers(teammates);

					// opponents
					ArrayList<SimPlayer> opponents = new ArrayList<SimPlayer>();
					for (int ii = 0; ii < SimpleSoccer.PLAYERS_PER_TEAM; ii++) {
						SimPlayer tplayer = playersinteams[player
								.getOppositeSide()][ii];
						opponents.add(tplayer);
					}

					player_controller.setOpponents(opponents);
				}
			}

		}

	}

	/** Pauses this thread for the passed time in <i>ms</i> */
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
		// get new intentions
		for (int k = 0; k < PLAYERS_PER_TEAM; k++) {
			playersinteams[0][k].getController().postInfo();
			playersinteams[1][k].getController().postInfo();
		}
		// calculate changes

		simpleserver.calculateAll();

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
			simpleserver.getVisuals(playersinteams[0][k]);
			simpleserver.getVisuals(playersinteams[1][k]);
		}
	}

	private void calcFitness() {
		// allocation every 5 seconds, ball distance to nearest player every 4
		// seconds, ball to goal every 2 seconds measured
		int aktmsec = aktStep * SimpleServer.STEPLENGTH;

		// Player distribution on the field
		if ((aktmsec % 5000) == 0) {

			object.setBall(simpleserver.ball.position.x,
					simpleserver.ball.position.y); // set ball

			for (int t = 0; t < 2; t++) {
				for (int p = 0; p < PLAYERS_PER_TEAM; p++) {
					object.setPlayer(t, p, playersinteams[t][p].position.x,
							playersinteams[t][p].position.y); // set players
				}
			}

			evaluator.calculate(object);

			if ((FrevoMain.DEBUGLEVEL & 0x08) > 0) {
				System.out.print("fit: " + evaluator.getScore(0) + " : "
						+ evaluator.getScore(1));
			}

			field_distribution_scores[0] += evaluator.getScore(0);
			field_distribution_scores[1] += evaluator.getScore(1);
		}

		// Ball distance to the nearest player
		if ((aktmsec % 4000) == 0) {

			object.setBall(simpleserver.ball.position.x,
					simpleserver.ball.position.y); // set ball

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
			double d1 = SimpleServer.getDistance(simpleserver.ball,
					simpleserver.rightGoal);
			double d2 = SimpleServer.getDistance(simpleserver.ball,
					simpleserver.leftGoal);

			if (d1 < d2) {
				ball_goal_scores[0]++;
				if (d1 < 7) {
					ball_goal_scores[0] += 10;
				} else if (d1 < 20) {
					ball_goal_scores[0] += 3;
				} else if (d1 < 30) {
					ball_goal_scores[0]++;
				}
			} else if (d1 > d2) {
				if (d2 < 7) {
					ball_goal_scores[1] += 10;
				} else if (d2 < 20) {
					ball_goal_scores[1] += 3;
				} else if (d2 < 30) {
					ball_goal_scores[1]++;
				}
				ball_goal_scores[1]++;
			}
		}
	}

	/** Resets individual fitness scores */
	private void resetScores() {
		// reset individual fitness components
		for (int s = 0; s < 2; s++) {
			field_distribution_scores[s] = 0;
			balldistscores[s] = 0;
			ball_goal_scores[s] = 0;
			falsekicks[s] = 0;
			scorenum[s] = 0;
		}

		// reset number of kicks for each player
		/*for (int u = 0; u < PLAYERS_PER_TEAM; u++) {
			kicknum[0][u] = 0;
			kicknum[1][u] = 0;
		}*/
		 object = new InfoObject(PLAYERS_PER_TEAM);
		 evaluator = new Evaluator(PLAYERS_PER_TEAM);
		
		// calculate the total number of simulation steps
		stepnumber = EVALTIME / SimpleServer.STEPLENGTH;

		// TODO what is this?? (istvan)
		object.goals[0] = simpleserver.leftGoal.position;
		object.goals[1] = simpleserver.rightGoal.position;
	}

	public int teamname2int(String teamname) {
		if (teamname.equals(tname[0]))
			return 0;
		else if (teamname.equals(tname[1]))
			return 1;

		throw new Error("Wrong team name!");
	}

	/**
	 * Places all players to the starting positions
	 */
	public void placePlayers() {
		/*// place according to team size, maximum 11/ team
		if (PLAYERS_PER_TEAM > 11)
			throw new Error("Teamsize larger than 11 is not implemented!");*/

		for (int p = 0; p < PLAYERS_PER_TEAM; p++) {
			// x position
			double x = 0;

			// first 3 players in front row
			if (p < 3)
				x = 10;
			// next 5 players in a second row
			else if (p < 8)
				x = 20;
			// one defender in a third row
			else if (p == 8)
				x = 30;
			// last 2 in a fourth row
			else
				x = 40;

			// y position
			double y = 0;

			if ((p == 0) || (p == 3) || (p == 8))
				y = 0;
			else if ((p == 1) || (p == 4) || (p == 9))
				y = 10;
			else if ((p == 2) || (p == 5) || (p == 10))
				y = -10;
			else if (p == 6)
				y = 20;
			else if (p == 7)
				y = -20;
			else
				y = getRandom().nextDouble() *10;

			playersinteams[0][p].position = new Point2D.Double(-x, y);
			playersinteams[1][p].position = new Point2D.Double(x, -y);
		}

		// Turns + nullify speed and acceleration
		for (int i = 0; i < PLAYERS_PER_TEAM; i++) {
			playersinteams[0][i].setBodyDirection(90);
			playersinteams[1][i].setBodyDirection(270);
			playersinteams[0][i].speedVector = new Point2D.Double(0, 0);
			playersinteams[1][i].speedVector = new Point2D.Double(0, 0);
			playersinteams[0][i].accelerationVector = new Point2D.Double(0, 0);
			playersinteams[1][i].accelerationVector = new Point2D.Double(0, 0);
		}
	}

	/**
	 * Places the ball to the starting position, which is the middle of the
	 * field (0,0)
	 */
	public void resetBall() {
		// reset position to the middle of the field
		simpleserver.ball.position = new Point2D.Double(0, 0);
		// reset speed to zero
		simpleserver.ball.speedVector = new Point2D.Double(0, 0);
		// reset acceleration to zero
		simpleserver.ball.accelerationVector = new Point2D.Double(0, 0);
	}

	public static void main(String[] args) throws Exception {
		int population = 1;
		int seed = 12345;

		// Fully connected first
		ArrayList<AbstractRepresentation> players = new ArrayList<AbstractRepresentation>(
				26 * 6);

		// read all from gen 0
		int gen = 0;

		System.out.println("2c,4c,6c,2p,4p,6p");

		for (gen = 0; gen <= 500; gen += 20) {
			if (gen == 500)
				gen--;

			players.add(new FullyMeshedNet(getResultFile(gen, 2,
					FULLYCONNECTED, true, seed, population), 0));
			players.add(new FullyMeshedNet(getResultFile(gen, 4,
					FULLYCONNECTED, true, seed, population), 0));
			players.add(new FullyMeshedNet(getResultFile(gen, 6,
					FULLYCONNECTED, true, seed, population), 0));
			players.add(new FullyMeshedNet(getResultFile(gen, 2,
					FULLYCONNECTED, false, seed, population), 0));
			players.add(new FullyMeshedNet(getResultFile(gen, 4,
					FULLYCONNECTED, false, seed, population), 0));
			players.add(new FullyMeshedNet(getResultFile(gen, 6,
					FULLYCONNECTED, false, seed, population), 0));

		}

		int size = players.size();
		// reset points
		int[] points = new int[size];
		for (int i = 0; i < size; i++)
			points[i] = 0;

		for (int a = 0; a < size - 1; a++) {
			for (int b = a + 1; b < size; b++) {
				AbstractRepresentation player1 = players.get(a);
				AbstractRepresentation player2 = players.get(b);

				// play game

				ProblemXMLData problemData = (ProblemXMLData) FrevoMain
						.getSelectedComponent(ComponentType.FREVO_PROBLEM);

				SimpleSoccer sc = (SimpleSoccer) problemData
						.getNewProblemInstance();
				problemData.adjustRequirements();

				List<RepresentationWithScore> result = sc
						.evaluateFitness(new AbstractRepresentation[] {
								player1, player2 });

				if (result.get(0).getScore() == result.get(1).getScore()) {
					// draw
					points[a] += 1;
					points[b] += 1;
				} else if (result.get(0).getRepresentation() == player1) {
					points[a] += 2;
					// System.out.println ("1");
				} else if (result.get(0).getRepresentation() == player2) {
					// System.out.println ("2");
					points[b] += 2;
				} else {
					System.err.println("ERROR in ranking!");
					System.exit(-1);
				}
			}
		}

		// print results
		for (int i = 0; i < size; i += 6)
			System.out.println(points[i] + "," + points[i + 1] + ","
					+ points[i + 2] + "," + points[i + 3] + "," + points[i + 4]
					+ "," + points[i + 5]);

	}

	private static final int FULLYCONNECTED = 1;

	private static File getResultFile(int generation, int hiddenneurons,
			int ntype, boolean isCartesian, int seed, int populations) {
		StringBuilder sb = new StringBuilder();
		sb.append("/Users/ifeherva/Works/demesos/Sourcecode/Frevo/Results/ssoccer_");

		if (ntype == FULLYCONNECTED)
			sb.append("fc_");
		else
			sb.append("ff_");

		sb.append("h").append(hiddenneurons).append("_");

		if (isCartesian)
			sb.append("c_");
		else
			sb.append("p_");

		StringBuilder gs = new StringBuilder(Integer.toString(generation));
		if (generation < 100) {
			while (gs.length() != 3)
				gs.insert(0, "0");
		}

		sb.append("p").append(populations).append("/seed_").append(seed)
				.append("/Simplified Robot Soccer_g").append(gs.toString()).append(".zre");

		return new File(sb.toString());
	}

}
