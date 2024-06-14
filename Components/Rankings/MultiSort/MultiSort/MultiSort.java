package MultiSort;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import utils.NESRandom;
import core.AbstractComponent;
import core.AbstractMultiProblem;
import core.AbstractMultiProblem.RepresentationWithScore;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.ProblemXMLData;
import core.XMLFieldEntry;

public class MultiSort extends AbstractRanking {
	
	public MultiSort(Hashtable<String, XMLFieldEntry> properties) {
		super(properties);
	}

	private AbstractRepresentation bestCandidate;

//	public static int generation = 0;
//	public static boolean over80 = false;

	// TODO Make more general!!!
	@Override
	public int sortCandidates(final ArrayList<AbstractRepresentation> pop,
			ProblemXMLData problem, NESRandom random) {
		NESRandom generator = random;
		int number_of_evaluations = 0;
		// Retrieve properties
		XMLFieldEntry et = getProperties().get("evaluation_times");
		int evaltimes = Integer.parseInt(et.getValue());
		XMLFieldEntry gt = problem.getProperties().get("evaluation_time");	//changed from evaluation_times to evaluation_time
		int gametimes = Integer.parseInt(gt.getValue());

		int popnumber = pop.size();// number of entities
		int playernumber = Integer.parseInt(problem.getRequirements()
				.get("maximumCandidates").getValue());// number of players each
														// game
		// too simple check
		if (popnumber % playernumber != 0)
			throw new Error(
					"MultiSort will not work if the population number"
							+ " is not a multiple of the given player number. Current settings: population size "
							+ popnumber + " playernumber " + playernumber);

		int[] timesarray = new int[popnumber];// to store who played how many
												// times
		float[] pointsarray = new float[popnumber];

		ArrayList<Integer> players = new ArrayList<Integer>();// list to see
																// players
																// available for
																// games

		try {
			for (int i = 0; i < evaltimes; i++) { // each evaluation
				players.clear();
				for (int ik = 0; ik < popnumber; ik++) { // fill up list from
															// scratch
					players.add(ik);
				}

				// create random groups
				for (int g = 0; g < (popnumber / playernumber); g++) { // each
																		// group
					AbstractRepresentation[] members = new AbstractRepresentation[playernumber];
					int[] membernumber = new int[playernumber];
					for (int k = 0; k < playernumber; k++) {// each member
						membernumber[k] = players.remove(generator
								.nextInt(players.size()));
						members[k] = pop.get(membernumber[k]);
					}
					// evaluate them
					AbstractMultiProblem p;
					AbstractComponent comp = problem.getNewProblemInstance();
					if (comp instanceof AbstractMultiProblem) {
						p = (AbstractMultiProblem) comp;
					} else {
						throw new Error(
								"MultiSort requires an instance of AbstractMultiProblem");
					}

					p.setRandom(generator.clone());
					List<RepresentationWithScore> gameresult = p
							.evaluateFitness(members);
					number_of_evaluations++;
					// probably buggy
					for (int m = 0; m < playernumber; m++) {
						// store points
						pointsarray[membernumber[m]] += gameresult.get(m)
								.getScore();
						// register that they already played
						timesarray[membernumber[m]]++;
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		// we should have the points here, comes the sorting
		ArrayList<SortElement> playerArray = new ArrayList<SortElement>();
		for (int p = 0; p < popnumber; p++) {
			SortElement s = new SortElement(pop.get(p), pointsarray[p]);
			playerArray.add(s);
		}
		Collections.sort(playerArray, Collections.reverseOrder());
		for (int u = 0; u < pop.size(); u++) {
			pop.set(u, playerArray.get(u).player);
		}
		float average = 0;
		for (int p = 0; p < pop.size(); p++) {
			average = average + playerArray.get(p).points;
		}
		average = average / pop.size();

		System.out
				.println("Best gain: " + playerArray.get(0).points + " sum: "
						+ average + " Worst: "
						+ playerArray.get(pop.size() - 1).points);
		System.out
				.println((playerArray.get(0).points / (evaltimes * gametimes))
						+ ","
						+ (average / (evaltimes * gametimes))
						+ ","
						+ (playerArray.get(pop.size() - 1).points / (evaltimes * gametimes)));

//		if (playerArray.get(0).points / (evaltimes * gametimes) >= 80) {
//			System.out.println("Fitness of 80 reached in generation "
//					+ MultiSort.generation);
//			try {
//				BufferedWriter out = new BufferedWriter(new FileWriter(
//						"average_gen.txt", true));
//				out.write(Integer.toString(MultiSort.generation) + "\n");
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			// System.exit(0);
//		} else if (MultiSort.generation == 4999) {
//			try {
//				BufferedWriter out = new BufferedWriter(new FileWriter(
//						"average_gen.txt", true));
//				out.write("Could not reach 80 in 5000 generations+\n");
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			// System.exit(0);
//		}

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("best.txt",
					true));
			out.write(
//			Integer.toString(MultiSort.generation)
					 ","
					+ Float.toString(playerArray.get(0).points
							/ (evaltimes * gametimes))
					+ ","
					+ (average / (evaltimes * gametimes))
					+ ","
					+ (playerArray.get(pop.size() - 1).points / (evaltimes * gametimes))
					+ "\n");
			if (playerArray.get(0).points / (evaltimes * gametimes) >= 100) {
				System.out.println("Switching back to linear function");
				// out.write ("Switching back to linear function\n");
//				MultiSort.over80 = true; // TODO - this is CLUTTER!!!!
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		MultiSort.generation++;

		bestCandidate = pop.get(0);
		return number_of_evaluations;
	}

	private class SortElement implements Comparable<SortElement> {
		public float points = 0;
		public AbstractRepresentation player;

		public SortElement(AbstractRepresentation r, float p) {
			this.player = r.clone();
			this.points = p;
		}

		@Override
		public int compareTo(SortElement o) {
			if (this.points < o.points)
				return -1;
			else if (this.points > o.points)
				return 1;
			return 0;
		}
	}

	@Override
	public AbstractRepresentation getBestCandidate() {
		return bestCandidate;
	}

}
