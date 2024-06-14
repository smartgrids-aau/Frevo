/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package core;

import graphics.FrevoWindow.SimulationWorkerThread;

import java.util.ArrayList;
import java.util.Hashtable;

import main.FrevoMain;

import org.dom4j.Document;

import utils.NESRandom;

/**
 * Abstract superclass for Method components. Methods are used to iteratively
 * optimize a given set of {@link AbstractRepresentation}s. They implement the
 * {@link Runnable} interface meaning the algorithm will be executed on a
 * separate thread.
 * 
 * <p>
 * Methods can save obtained results any time by calling the
 * {@link FrevoMain#saveResult(String, AbstractMethod)} static method.
 * 
 * @author Istvan Fehervari
 */
public abstract class AbstractMethod extends AbstractComponent {

	/** A seed used for tracking the random generator object. */
	protected long seed;

	/** Holds a reference for the random generator object. */
	protected NESRandom generator;

	/** Indicates the progress of this method. */
	protected float progress = 0;

	/** Indicates if an interrupt signal has arrived. */
	protected boolean isPaused = false;

	/** Contains information about last executed generation. */ 
	private XMLMethodStep lastState;
	
	/** Shows the possibility of continuation of the experiment. */
	private boolean canContinue = true;
	
	/** Sends an interrupt flag to the method. */
	final public synchronized void pause() {
		isPaused = true;
	}

	final public synchronized void wakeUp() {
		isPaused = false;
		this.notify();
	}

	/** check for pause flag */
	final protected synchronized boolean handlePause() {
		while (isPaused) {
			// wait here...
			FrevoMain.isRunning = false;
			try {
				wait();
			} catch (InterruptedException e) {
				// ignore this exception since it shows
				// that the execution has been interrupted 
			}
		}
		if (Thread.interrupted()) {
			// We've been interrupted
			return true;
		}
		return false;
	}

	public AbstractMethod(NESRandom random) {
		setRandom(random);
	}

	/**
	 * Runs the optimization method
	 * 
	 * @param problemData
	 *            defines the problem class and its configuration
	 * @param representationData
	 *            defines the representation class and its configuration
	 * @param rankingData
	 *            defines the ranking class and its configuration
	 * @param properties
	 *            the configuration of the optimizer
	 */
	public abstract void runOptimization(ProblemXMLData problemData,
			ComponentXMLData representationData, ComponentXMLData rankingData,
			Hashtable<String, XMLFieldEntry> properties);

	/**
	 * Continues the experiment from the last saved state. 
	 * 
	 * @param problemData
	 *            defines the problem class and its configuration
	 * @param representationData
	 *            defines the representation class and its configuration
	 * @param rankingData
	 *            defines the ranking class and its configuration
	 * @param properties
	 *            the configuration of the optimizer
	 * @param doc 
	 * 			  information in XML format about last completed step of evolutionary process
	 */
	public void continueOptimization(ProblemXMLData problemData,
			ComponentXMLData representationData, ComponentXMLData rankingData,
			Hashtable<String, XMLFieldEntry> properties, 
			Document doc){		
	}
	
	/**
	 * Returns the current progress of this method mostly for display purposes.
	 * 
	 * @return a float value between 0 and 1
	 */
	final public float getProgress() {
		return progress;
	}

	/**
	 * Sets the progress of this method the given value. This value is used by
	 * the assigned worker thread mostly for display purposed and it is between
	 * 0 and 1.
	 * 
	 * @param p
	 *            The new progress value to be used.
	 */
	final static public void setProgress(float p) {
		if (FrevoMain.isFrevoWithGraphics()) {
			SimulationWorkerThread sworker = FrevoMain.getMainWindow()
					.getWorkerThread();
			if (sworker != null) {
				sworker.setProgressToPublish(p);
			}
		}
	}

	/**
	 * Returns the assigned random generator object of this method.
	 * 
	 * @return a reference of the assigned random generator object.
	 */
	final public NESRandom getRandom() {
		if (generator == null) {
			// create new random generator
			NESRandom g = new NESRandom();
			this.seed = g.nextLong();
			generator = new NESRandom(seed);
		}
		return generator;
	}

	/**
	 * Sets the random generator object to the given value.
	 * 
	 * @param generator
	 *            The new random generator object to be used.
	 */
	final public void setRandom(NESRandom generator) {
		this.generator = generator;
		this.seed = generator.getSeed();
	}

	/**
	 * Sets the random seed for this method.
	 * 
	 * @param seed
	 *            The new random seed to be used.
	 */
	final public void setRandom(long seed) {
		this.seed = seed;
		generator = new NESRandom(seed);
	}

	/**
	 * Returns the current random seed associated with the random object of this
	 * method.
	 * 
	 * @return the current random seed of this method.
	 */
	final public long getSeed() {
		return this.generator.getSeed();
	}

	/**
	 * Load representations from a results XML doc object. The representations
	 * are arranged as a list of populations where each population is
	 * represented as a list of representations (or solutions).
	 * <p>
	 * If the loading takes considerable amount of time it is advised to provide
	 * visual feedback to the user. This can be done by calling the
	 * {@link main.FrevoMain#setLoadingProgress(float)} method.
	 * 
	 * @param doc
	 *            The source {@link org.dom4j.Document} to be used for loading
	 *            the representations.
	 * @return A 2D array of <tt>AbstractRepresentations</tt> loaded from the
	 *         source document.
	 */
	public abstract ArrayList<ArrayList<AbstractRepresentation>> loadFromXML(
			Document doc);
	
	/**
	 * Gets the latest results from Method evaluation.
	 * This method is important for the function which allows to save the last state,
	 * because it's possible to continue evaluation from the last saved state. 
	 * 
	 * @return object which contains results of evaluation for last generation. 
	 * If Method doesn't support saving it should return null.  
	 */
	final public XMLMethodStep getLastResults()	{
		return lastState;		
	}
	
	/**
	 * Sets the latest results of Method evaluation.
	 * @param methodStep information about last executed generation
	 */
	final protected void setLastResults(XMLMethodStep methodStep) {
		lastState = methodStep;
		FrevoMain.methodStateChanged(this);
	}
	
	/**
	 * Sets the possibility of continuation of the experiment
	 * 
	 * @param canContinue true if experiment can be continued, otherwise - false
	 */
	final protected void setCanContinue(boolean canContinue){
		this.canContinue = canContinue;
		FrevoMain.changeContinueState(this);
	}
	
	/**
	 * Gets the possibility of continuation of the experiment
	 * 
	 * @return true if experiment can be continued, otherwise - false
	 */
	final public boolean canContinue() {
		return canContinue;
	}	
}
