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

/** Provides a list of component types 
 * @author Istvan Fehervari*/
public enum ComponentType {
	/** Indicates a problem with an absolute fitness.
	 * @see AbstractSingleProblem */
	FREVO_PROBLEM,
	/** Indicates a problem that evaluates many candidates at a time.
	 * @see AbstractMultiProblem*/
	FREVO_MULTIPROBLEM,
	/** Indicates an optimization method component.
	 * @see AbstractMethod*/
	FREVO_METHOD,
	/** Indicates a solution representation.
	 * @see AbstractRepresentation*/
	FREVO_REPRESENTATION,
	/** Indicates a set of solution representations.
	 * @see AbstractRepresentation*/
	FREVO_BULKREPRESENTATION,
	/** Indicates a ranking algorithm.
	 * @see AbstractRanking*/
	FREVO_RANKING
}
