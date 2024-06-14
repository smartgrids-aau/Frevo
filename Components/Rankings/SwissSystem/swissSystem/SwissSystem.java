package swissSystem;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import utils.NESRandom;
import core.AbstractComponent;
import core.AbstractMultiProblem;
import core.AbstractMultiProblem.RepresentationWithScore;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.ProblemXMLData;
import core.XMLFieldEntry;

/**
 * <p>A Swiss-system tournament is a tournament format involving several rounds of competition
 * where the winners are the players with the highest aggregate of points earned from each round. 
 * Players meet one-to-one in each round and are paired using a predetermined formula 
 * to match players with similar skill (though they may be paired by random draw). </br> 
 * (Thank you Wikipedia: <a href="http://en.wikipedia.org/wiki/Swiss-system_tournament">Swiss-system tournament</a>)</p>
 * 
 * @author Istvan Fehervari, Sergii Zhevzhyk
 */
public class SwissSystem extends AbstractRanking {

	public SwissSystem(Hashtable<String, XMLFieldEntry> properties) {
		super(properties);
	}

	private AbstractRepresentation bestCandidate;
	/** Number of players */
	private int playerNumber;

	/** Array with points for each player, so it's very important table for calculating of final results*/
	private ArrayList<SwissElement> maintable;

	/** A list containing the next pairings */
	private List<Point> buffer = new LinkedList<Point>();

	private TreeSet<Integer> values = new TreeSet<Integer>();

	/** Number of points the winner gets */
	static int WINPOINT;
	/** Number of points the player get when it is a tie */
	static int TIEPOINT;
	private int N_TREADS;

	NESRandom generator;
	
	static final boolean DEBUG = false;

	class EvaluationRunnable implements Runnable {
		private ProblemXMLData problem;
		private ArrayList<AbstractRepresentation> pop;
		private NESRandom random;
		private Point pair;

		public EvaluationRunnable(ProblemXMLData problem,
				ArrayList<AbstractRepresentation> pop, NESRandom random, Point p) {
			this.problem = problem;
			this.pop = pop;
			this.random = random;
			this.pair = p;
		}

		@Override
		public void run() {
			try {
				if (pair.y >= pop.size()) {
					// this player receives a bye
					addResult(pair.x, pair.y, 1, true);
					return;
				}
				
				// evaluate game
				AbstractMultiProblem p;
				AbstractComponent comp = problem.getNewProblemInstance();
				if (comp instanceof AbstractMultiProblem) {
					p = (AbstractMultiProblem) comp;
				} else {
					throw new Error(
							"Swiss System requires an instance of AbstractMultiProblem");
				}

				AbstractRepresentation p1 = pop.get(pair.x);
				AbstractRepresentation p2 = pop.get(pair.y);

				p.setRandom(random.clone());

				List<RepresentationWithScore> gameresult = p
						.evaluateFitness(new AbstractRepresentation[] { p1, p2 });

				if (gameresult.get(0).getScore() == gameresult.get(1)
						.getScore()) {
					// tie
					addResult(pair.x, pair.y, 0);
					addResult(pair.y, pair.x, 0);
				} else {
					if (gameresult.get(0).getRepresentation() == p1) {
						// first player won
						addResult(pair.x, pair.y, 1);
						addResult(pair.y, pair.x, -1);
					} else {
						// second player won
						addResult(pair.x, pair.y, -1);
						addResult(pair.y, pair.x, 1);
					}
				}

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int sortCandidates(final ArrayList<AbstractRepresentation> pop,
			final ProblemXMLData problem, NESRandom random) {
		// load from properties
		XMLFieldEntry win = getProperties().get("points_win");
		WINPOINT = Integer.parseInt(win.getValue());
		XMLFieldEntry tie = getProperties().get("points_tie");
		TIEPOINT = Integer.parseInt(tie.getValue());
		XMLFieldEntry nthreads = getProperties().get("parallelthreads");
		N_TREADS = Integer.parseInt(nthreads.getValue());

		generator = random;

		// total number of evaluations
		int number_of_evaluations = 0;

		this.playerNumber = pop.size();
		
		// number of rounds based on log2
		int roundNumber = calcRoundNumber(playerNumber);

		// create initial pairings
		initialize();

		// Iterate through the rounds
		for (int r = 0; r < roundNumber; r++) { // take every round
			if (DEBUG) {
				System.out.println("Round " + r + " buffer: " + buffer.size());

				for (Point p : buffer) {
					System.out.println(pop.get(p.x).getHash() + " vs. "
							+ pop.get(p.y).getHash());
				}

				System.out.println();
			}

			// Single Thread
			if (N_TREADS <= 1) {

				// take every pairing in the round
				while (!buffer.isEmpty()) {
					try {
						Point pair = consumeNextGame();
						if (pair.y >= playerNumber ) {
							// this is buy player which receives points for nothing but only one time.
							// it's only possible for odd numbers of players. 
							addResult(pair.x, pair.y, 1, true);
							continue;
						}
						
						// evaluate game
						AbstractMultiProblem p;
						AbstractComponent comp = problem
								.getNewProblemInstance();
						if (comp instanceof AbstractMultiProblem) {
							p = (AbstractMultiProblem) comp;
						} else {
							throw new Error(
									"Swiss System requires an instance of AbstractMultiProblem");
						}

						AbstractRepresentation p1 = pop.get(pair.x);
						AbstractRepresentation p2 = pop.get(pair.y);

						p.setRandom(random.clone());

						List<RepresentationWithScore> gameresult = p
								.evaluateFitness(new AbstractRepresentation[] {
										p1, p2 });

						number_of_evaluations++;
							
						// analyzing results of the game
						if (gameresult.get(0).getScore() == gameresult.get(1)
								.getScore()) {
							// tie
							addResult(pair.x, pair.y, 0);
							addResult(pair.y, pair.x, 0);
							if (DEBUG)
								System.out.println(p1.getHash() + " vs "
										+ p2.getHash() + " tie");
						} else {
							if (gameresult.get(0).getRepresentation() == p1) {
								// first player won
								addResult(pair.x, pair.y, 1);
								addResult(pair.y, pair.x, -1);
								if (DEBUG)
									System.out.println(p1.getHash() + " vs "
											+ p2.getHash() + " 1");
							} else {
								// second player won
								if (DEBUG)
									System.out.println(p1.getHash() + " vs "
											+ p2.getHash() + " 2");
								addResult(pair.x, pair.y, -1);
								addResult(pair.y, pair.x, 1);
							}
						}

					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (Error e) {
						e.printStackTrace();
					}
				}

			} else {
				// Multi-threading
				// evaluate using thread pool

				ExecutorService executor = Executors
						.newFixedThreadPool(N_TREADS);

				while (!buffer.isEmpty()) {
					Point p = consumeNextGame();
					Runnable worker = new EvaluationRunnable(problem, pop,
							random, p);
					executor.execute(worker);
					number_of_evaluations++;
				}

				// shutdown all threads and then wait while they finished
				executor.shutdown();
				
				// This will make the executor accept no new threads and
				// finish all existing threads in the queue executor.shutdown();
				// Wait until all threads are finish
				while (!executor.isTerminated()) {
					try {
						executor.awaitTermination(300, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

			// do not calculate next round if it will not be processed
			if (r < roundNumber - 1)
				calcnextRound();
		}

		// copy points as fitness
		for (int i = 0; i < pop.size(); i++)
			pop.get(i).setFitness(maintable.get(i).getPoints());

		Collections.sort(pop, Collections.reverseOrder());

		if (DEBUG) {
			System.out.println();
			for (AbstractRepresentation rep : pop) {
				System.out.println(rep.getHash() + " " + rep.getFitness());
			}
			System.out.println();
		}

		bestCandidate = pop.get(0);
		return number_of_evaluations;
	}

	/**
	 * Returns a list enumerating the players in a descending order starting
	 * with the one having the most points
	 * 
	 * @return
	 */
	public List<Integer> getResults() {
		List<Integer> results = new ArrayList<Integer>();
		Collections.sort(maintable, Collections.reverseOrder());

		for (int i = 0; i < maintable.size(); i++) {
			results.add(maintable.get(i).getId());
		}

		return results;
	}

	/**
	 * Calculates next round and puts it into the buffer. Invoke this only after
	 * consuming all pairings from the buffer using consumeNextGame()
	 */
	private void calcnextRound() {
		if (buffer.size() != 0) {
			System.err
					.println("Warning! Tournament buffer is not empty! There might be problems");
		}

		// Get different point groups
		values.clear();

		for (SwissElement se : maintable) {
			values.add(se.getPoints());
		}

		// list of players that could not be paired
		ArrayList<SwissElement> unpaired = new ArrayList<SwissElement>();

		// we need bye player only if we have odd number of players
		boolean needByePlayer = maintable.size() % 2 != 0;
		// shows that bye player has been already selected. this flag works only for odd number of players.
		boolean arrangedByeGame = false;
		
		// pair players with similar points
		while (values.size() > 0) {

			// players with points
			int point = values.pollLast();

			// collect players with that many points + the unpaired players from
			// the previous round
			ArrayList<SwissElement> playersingroup = new ArrayList<SwissElement>(
					unpaired);
			unpaired.clear();

			for (int i = 0; i < maintable.size(); i++) {
				SwissElement se = maintable.get(i);
				if (se.getPoints() == point)
					playersingroup.add(se);
			}

			// even number of players, can be ranked against each other
			while (playersingroup.size() > 0) {
				// pick first
				SwissElement s1 = playersingroup.remove(0);
				if (needByePlayer && !arrangedByeGame && !s1.playedWithByePlayer()) 
				{
					// if first player wasn't bye player so far that it receives a bye and we are going to next player
					buffer.add(new Point(maintable.indexOf(s1), maintable.size()));
					arrangedByeGame = true;	
					continue;
				}				
				
				// try to pair it
				boolean paired = false;

				Iterator<SwissElement> it = playersingroup.iterator();

				while (it.hasNext()) {
					SwissElement s2 = it.next();
					
					if (needByePlayer && !arrangedByeGame && !s2.playedWithByePlayer()) 
					{
						// player receives a bye
						buffer.add(new Point(maintable.indexOf(s2), maintable.size()));
						it.remove();
						arrangedByeGame = true;
					} else if (!s1.isAlreadyPlayed(s2)) {
						// add to buffer as future game for next round
						buffer.add(new Point(maintable.indexOf(s1), maintable
								.indexOf(s2)));
						// remove it to avoid future pairing
						it.remove();

						// stop search
						paired = true;
						break;
					}
				}

				if (!paired) {
					// could not find a pair
					unpaired.add(s1);
				}
			}
		}

		// if there are still players that were not paired then pair them by
		// force unless they played against each other already

		while (unpaired.size() > 0) {
			SwissElement s1 = unpaired.remove(0);

			for (int s2i = 0; s2i < unpaired.size(); s2i++) {
				SwissElement s2 = unpaired.get(s2i);
				if (!s1.isAlreadyPlayed(s2)) {
					buffer.add(new Point(maintable.indexOf(s1), maintable
							.indexOf(s2)));
					unpaired.remove(s2i);
					break;
				}
			}
		}

		// unpaired should be 0 then here.
	}

	/**
	 * Adds result to the tournament table with addition flag for bye players
	 * 
	 * @param whoId the id of the first player
	 * @param against the id of the second player which was the opponent in this game
	 * @param res +1 means a win, -1 a loss, 0 a tie
	 * @param byePlayer indicates the game of bye player
	 */
	public synchronized void addResult(int whoId, int against, int res, boolean byePlayer) {
		if (((res == 0) || (res == 1)) || (res == -1)) {
			SwissElement se = maintable.get(whoId);
			if (byePlayer) {
				se.setGameWithByePlayer(true);
			} else {
				se.addResult(maintable.get(against), res);
			}
		} else {
			throw new Error("invalid result added: " + res);
		}
	}
	
	/**
	 * Adds result to the tournament table
	 * 
	 * @param whoId the id of the first player
	 * @param against the id of the second player which was the opponent in this game
	 * @param res
	 *            +1 means a win, -1 a loss, 0 a tie
	 */
	public void addResult(int whoId, int against, int res) {
		addResult(whoId, against, res, false);
	}

	/**
	 * Returns the next game then removes it from the buffer
	 * 
	 * @return a Point with the next pairing: x versus y
	 */
	public synchronized Point consumeNextGame() {
		if (buffer.size() == 0) {
			throw new Error("No more rounds!");
		}

		return buffer.remove(buffer.size() - 1);
	}

	/** Create starting entries in table, create random seed -> buffer */
	private void initialize() {
		maintable = new ArrayList<SwissElement>(playerNumber);

		for (int i = 0; i < playerNumber; i++) {
			maintable.add(new SwissElement(i));
		}

		// reset buffer
		buffer.clear();

		// Fill buffer with ordered pairing (first with last, etc)
		final int hsize = playerNumber / 2;
		for (int i = 0; i < (hsize); i++) {
			Point newset = new Point(i, playerNumber - 1 - i);
			buffer.add(newset);
		}
		
		if (playerNumber % 2 != 0) {
			// lucky guy - he is already winner in this round
			Point byePlayer = new Point(hsize, playerNumber);
			buffer.add(byePlayer);
		}
	}

	/**
	 * Returns the number of rounds required to finish the tournament based on
	 * the number of players. It is the binary logarithm rounded up
	 * 
	 * @param player
	 * @return
	 */
	public static int calcRoundNumber(int player) {
		double rnumber = Math
				.ceil(Math.log10(player) / java.lang.Math.log10(2));

		return (int) rnumber;
	}

	@Override
	public AbstractRepresentation getBestCandidate() {
		return bestCandidate;
	}
}
