/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package core;

import java.util.Hashtable;
import java.util.Random;

import utils.NESRandom;

/** Base abstract superclass for Problem components. Problems are encapsulations of optimization tasks,
 *  typically including an agent-based simulation tool. The agent controllers are received in a form of an
 * {@link AbstractRepresentation}.
 * <p>
 * After evaluating the given candidate representation it is highly recommended to
 * call the representation's {@link AbstractRepresentation#setFitness(double) setFitness(double fitness)} method. 
 * 
 * <p>
 * Currently, there are two types (subclasses) of problems:
 * <list>
 * <li>{@link AbstractSingleProblem}: a problem which can assign a definite fitness value to individual solution candidates.
 * <li>{@link AbstractMultiProblem}: a problem which can only establish a rank of candidates with decreasing performance.  
 * </list>
 * 
 * @author Istvan Fehervari
 * 
 * */
public abstract class AbstractProblem extends AbstractComponent {
	
	/** The random generator object used by this problem. */
	protected Random generator;
	
	/**
	 * Returns the random generator object of this problem. Using this random generator ensures repeatable evaluations.
	 * @return A reference to the random generator object of this class.
	 */
	public Random getRandom() {
		// Create new random generator if there is no one yet
		if (generator == null) {
			generator = new NESRandom();
		}
		return generator;
	}
	
	/** Sets the random generator of this class to the given value.
	 * @param generator The new random generator to be used.
	 */
	public void setRandom(Random generator) {
		this.generator = generator;
	}
	
	/** Adjusts and returns the requirements of this problem that is modified according to the properties.
	 * Override this method if the requirements are dependent on the properties of this problem.
	 * @param requirements The set of requirement keys and values read from the source XML file.
	 * @param properties The set of property keys and values.
	 * @return the new requirement set adjust according to the given set of parameters.*/
	public Hashtable<String, XMLFieldEntry> adjustRequirements(Hashtable<String, XMLFieldEntry> requirements, Hashtable<String, XMLFieldEntry> properties) {
		return requirements;
	}
}
