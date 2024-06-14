package cipi;
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
import java.util.Hashtable;
import java.util.List;

import core.AbstractMultiProblem;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

/** Implementation of the Common interest vs. private interest problem */
public class CIPI extends AbstractMultiProblem {

	private boolean log = false;
	private BufferedWriter out;

	private void Log(String s) {
		try {
			out.write(s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<RepresentationWithScore> evaluateFitness(
			AbstractRepresentation[] candidates) {
		try {
			out = new BufferedWriter(new FileWriter("cipi-log.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// has to be ordered according to algorithm
		ArrayList<RepresentationWithScore> results = new ArrayList<RepresentationWithScore>();
		int evaltimes = Integer.parseInt(getProperties()
				.get("evaluation_times").getValue());
		int numTokens = Integer.parseInt(getProperties().get("startuptokens")
				.getValue());
		
		int playernumber = 6;
		float[] money = new float[playernumber];// array for storing temporary
		// money
		float[] gainedmoney = new float[playernumber];// array for storing the
		// gained money

		for (int i = 0; i < playernumber; i++) {
			gainedmoney[i] = 0;
		}

		ArrayList<Float> previousaction = new ArrayList<Float>();
		// handcraft previous action first
		previousaction.clear();
		//previousaction.add(1.0f); //indicates that this is a starting action
		for (int i1 = 0; i1 < playernumber; i1++) { // add zero as initial
													// previous action about
													// everyone
			previousaction.add(0f);
		}
		if (log)
			Log("--------------------------------------");
		for (int eval = 0; eval < evaltimes; eval++) {
			if (log)
				Log("Game " + (eval + 1));
			// reset player money
			for (int i = 0; i < playernumber; i++) {
				money[i] = numTokens;
			}

			ArrayList<Float> nextaction = new ArrayList<Float>();
			// feed input (action of players from the last round) and collect
			// output
			for (int i = 0; i < playernumber; i++) {
				// interpret previous action and put it into nowaction
				ArrayList<Float> nowaction = new ArrayList<Float>();
				// add starting indicator
				if (eval == 0)
					nowaction.add(1.0f);
				else
					nowaction.add(0.0f);
				// add own previous action
				nowaction.add(previousaction.get(i));
				// add previous action of opponents 
				for (int j = 0; j < playernumber; j++) {
					if (j != i)
						nowaction.add(previousaction.get(j));
				}
				candidates[i].reset();
				float action = candidates[i].getOutput(nowaction).get(0)*numTokens;

				if (action > money[i])
					action = money[i];
				else if (action < 0)
					action = 0;
				nextaction.add(action);
			}
			if (log)
				Log("Donations: " + nextaction.get(0) + " " + nextaction.get(1)
						+ " " + nextaction.get(2) + " " + nextaction.get(3)
						+ " " + nextaction.get(4) + " " + nextaction.get(5));
			// execute actions
			float pot = 0;
			for (int i = 0; i < playernumber; i++) {
				float donation = nextaction.get(i);
				money[i] = money[i] - donation; // deduct money
				pot = pot + donation; // add money to pot
			}
			
			// calculate real GNP
//			realIncome -= pot;
//			realIncome += pot*3;
						
			// pot function
//			apparentIncome -= pot;
			//pot = pot *3; // no cooperation
			pot = pot *6;
			//pot = pot*pot/numTokens*2f; // immediate cooperation
			//pot = pot*pot/numTokens; //cooperation after 120 
			//pot = pot*pot/numTokens/2f; // never cooperate
			
//			apparentIncome += pot;
			
			// redistribute money
			for (int i = 0; i < playernumber; i++) {
				money[i] += pot / playernumber;
			}
			if (log)
				Log("Gain: " + (money[0] - 20) + " " + (money[1] - 20) + " "
						+ (money[2] - 20) + " " + (money[3] - 20) + " "
						+ (money[4] - 20) + " " + (money[5] - 20));
			previousaction = nextaction;

			// save only the collected or lost collected money in this turn
			for (int i = 0; i < playernumber; i++) {
				gainedmoney[i] += money[i] - numTokens;
			}
		}
		if (log)
			Log("At the end: " + gainedmoney[0] + " " + gainedmoney[1] + " "
					+ gainedmoney[2] + " " + gainedmoney[3] + " "
					+ gainedmoney[4] + " " + gainedmoney[5]);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < candidates.length; i++) {
			results.add(new RepresentationWithScore(candidates[i],
					gainedmoney[i]/evaltimes));
		}

		return results;
	}

	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {
		// adjust number of inputs
		XMLFieldEntry inputn = requirements.get("inputnumber");
		// 1 starting indicator + 1 previous action + 1 previous action of your opponent 
		inputn.setValue(String.valueOf(1 + 1 + 5));
		return requirements;
	}
	
	
	

}
