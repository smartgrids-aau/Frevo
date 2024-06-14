package nnracer;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.swing.SwingWorker;

import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.XMLFieldEntry;

import utils.NESRandom;

/**
 * A simple problem where a race car driver needs to learn how to maneuver to
 * avoid slower cars
 */
public class NNRacer extends AbstractSingleProblem {
	
	private static final int FREE = 0;
	private static final int BLOCKED = 1;

	/** Array of Lookahead - Lane */
	public int[][] obstaclePattern;
	private Random generator = new NESRandom();
	private float carProbability;
	//public int[] baseline;
	int lanenum, lookahead;
	private int evalTimes, maxovertake;
	
	boolean allowTeleportation = true;
	
	private RacerDisplay display;
	protected DisplayWorker sw;

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {

		AbstractRepresentation driver = candidate;
		
		loadParameters();
		
		//run evaluation
		
		int sum = 0;
		for (int r = 0; r < evalTimes; r++) {	
			sum += getEval(driver);
		}
		
		double res = ((double)sum / evalTimes);
		return (res);
	}
	
	private void loadParameters() {
		// extract properties
		lanenum = Integer
				.parseInt(getProperties().get("laneNumber").getValue());
		lookahead = Integer.parseInt(getProperties().get("lookAhead")
				.getValue());

		carProbability = Float.parseFloat(getProperties().get("carProbability")
				.getValue());
		maxovertake = Integer.parseInt(getProperties().get("maxOvertakeNumber")
				.getValue());
		evalTimes = Integer.parseInt(getProperties().get("evaluationTimes")
				.getValue());

		generator = getRandom(); 
	}

	/** Returns the number of successful overtakes */
	private int getEval(AbstractRepresentation driver) {
		boolean isColloided = false;//is the driver still intact?
		
		int finishedCycles = 0;// number of successful overtakes
		
		int position = generator.nextInt(lanenum); // vertical position of the car picked randomly

		obstaclePattern = new int[lookahead][lanenum];
		
		ArrayList<Float> input = new ArrayList<Float>();
		ArrayList<Float> output = new ArrayList<Float>();

		// initialize obstacle pattern
		generateObstacleMap();

		//run till it crashes
		while ((!isColloided) && (finishedCycles <= maxovertake)) {
			
			// increase difficulty
			setDifficulity(finishedCycles);
			
			// add map data to input
			input.clear();
			
			for (int row = 0; row < lookahead;row++) {
				for (int lane = 0; lane < lanenum;lane++) {
					input.add((float)obstaclePattern[row][lane]);
				}
			}
			
			// add own position to input
			input.add((float) position);

			// get output
			output.clear();
			output = driver.getOutput(input);

			position = getPosition(output.get(0));

			// check collision
			if (obstaclePattern[0][position] == BLOCKED) {
				isColloided = true; // collision happened
			}				
			else { // no collision, propagate cars
				regenerateMap();
				finishedCycles++;
			}
		}
		return finishedCycles;
	}

	/** Increases car probability with higher succesfull overtakes */
	private void setDifficulity(int cyc) {
		if ((cyc > 50) && (cyc < 100))
			carProbability = 0.6f;
		else if ((cyc >= 100) && (cyc < 150))
			carProbability = 0.7f;
		else if ((cyc >= 150) && (cyc < 200))
			carProbability = 0.8f;
		else if (cyc >= 200)
			carProbability = 0.9f;
	}

	/** Converts the output of the representation to position */
	private int getPosition(float out) {
		// out is always in the range of 0 .. +1

		int pos = (int) (lanenum * out);
		
		if (pos > lanenum - 1)
			pos = lanenum - 1;
		
		return pos;
	}

	/**
	 * Generates binary input grid. Depends on the car probability
	 * 
	 * @param init
	 */
	private void generateObstacleMap() {
		for (int row = 0; row < lookahead;row++) {
			for (int lane = 0; lane < lanenum;lane++) {
				int occupiedspots = 0;
				if (generator.nextFloat() > carProbability) {
					obstaclePattern[row][lane] = BLOCKED;
					occupiedspots++;
				} else
					obstaclePattern[row][lane] = FREE;
				
				//check if there is a free spot
				if (occupiedspots == lanenum) {
					int freelane = generator.nextInt(lanenum);
					obstaclePattern[row][freelane] = FREE;
				}
			}
		}
	}
	
	
	private void regenerateMap() {
		// propagate map (backwards algorithm)
		for (int row = obstaclePattern.length-1;row >0;row--) {
			for (int lane =0;lane <lanenum;lane++) {
				obstaclePattern[row][lane] = obstaclePattern[row-1][lane];
			}
		}
		//generate new input line
		for (int lane = 0; lane < lanenum;lane++) {
			int occupiedspots = 0;
			if (generator.nextFloat() > carProbability) {
				obstaclePattern[0][lane] = BLOCKED;
				occupiedspots++;
			} else
				obstaclePattern[0][lane] = FREE;
			
			//check if there is a free spot
			if (occupiedspots == lanenum) {
				int freelane = generator.nextInt(lanenum);
				obstaclePattern[0][freelane] = FREE;
			}
		}
		
	}
	
	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		loadParameters();
		//System.out.println("Result: " + getResult(candidates));
		display = new RacerDisplay(this);
	}
	
	public void runBackground(int mapnum) {
		sw = new DisplayWorker();
		sw.execute();

	}

	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {
		XMLFieldEntry ln = properties.get("laneNumber");
		int laneNumber = Integer.parseInt(ln.getValue());
		XMLFieldEntry la = properties.get("lookAhead");
		int lookAhead = Integer.parseInt(la.getValue());

		// Required input is the lanenumber * visible rows + 1 for own position
		XMLFieldEntry inputn = requirements.get("inputnumber");
		inputn.setValue(Integer.toString((laneNumber * lookAhead) + 1));

		return requirements;
	}
	
	private class DisplayWorker extends SwingWorker<Void, Integer> {

		@Override
		protected Void doInBackground() throws Exception {
			//runSimulation(true);
			return null;
		}

		/*public void setProgressToPublish(int p) {
			publish(p);
		}*/

		protected void process(List<Integer> results) {
			display.updateDisplay();
		}

	}

}
