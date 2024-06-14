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

import java.util.List;

/**
 * Provides a general interface to formulate own problems that evaluates multiple representations at a time.
 * 
 * <p>
 * In order to provide a visualized evaluation the method  {@link AbstractSingleProblem#replayWithVisualization} must be overwritten.
 * 
 * <p>To support requirement changes based on different parameters override the following method:<br><br>
 * 
 * -{@link AbstractProblem#adjustRequirements(java.util.Hashtable, java.util.Hashtable)}
 * 
 * @author Istvan Fehervari
 *
 */
public abstract class AbstractMultiProblem extends AbstractProblem {
	
	/** Returns the fitness of the given representations. The returned list contains the representations with their achieved scores. A higher score usually means a better ranking.<br>
	 * However, the order of the candidates matters the most for ranking, the scores are secondary.
	 * @param candidates Array of candidates to be evaluated.
	 * @return a list of evaluated candidates with decreasing fitness. That means better candidates have lower index. */
	public abstract List<RepresentationWithScore> evaluateFitness(final AbstractRepresentation[] candidates);

	
	/** This function is called when the user request a visual replay from the problem.
	 * A total re-evaluation is not needed, only to provide a visual mean for
	 * the user to grasp the performance of the indicated candidate solutions.
	 * <p> 
	 * It is recommended to use advanced threading for display like {@link javax.swing.SwingWorker}.
	 * @param candidates Array of candidates whose replay has been requested. */
	public void replayWithVisualization(AbstractRepresentation[] candidates) {
		// Default implementation only prints the results of a single evaluation
		List<RepresentationWithScore> results = evaluateFitness(candidates);
		
		System.out.println ("Evaluation fininshed with the following results:");
		for (RepresentationWithScore rs : results) {
			System.out.println (rs.representation.getHash()+" : "+rs.score);
		}
	}
	
	/** A structure that contains a representation along with its score. Comparison of this object is based only one the score stored within. */
	public static class RepresentationWithScore  implements Comparable<RepresentationWithScore> {
		/** The contained representation */
		private AbstractRepresentation representation;
		/** The score of the representation stored within this class.*/
		private double score;
		
		private double hiddenFitness = 0f;
		
		/** General constructor of this class requiring the representation and its fitness score to be stored. The fitness of the representation will be overwritten
		 * @param representation The candidate representation of this class.
		 * @param score The score of this representation.*/
		public RepresentationWithScore(AbstractRepresentation representation, double score) {
			this.representation = representation;
			this.representation.setFitness(score);
			this.score = score;
			
		}
		
		/** Returns the candidate representation stored within this class.
		 * @return The representation stored within the class.*/
		public AbstractRepresentation getRepresentation() {
			return this.representation;
		}
		
		/** Returns the score of the representation stored within the class. 
		 * @return The score of the representation stored within the class.*/
		public double getScore() {
			return this.score;
		}
		
		/** Sets the score of the contained representation. 
		 * @param score The new score to be used.*/
		public void setScore(double score) {
			this.score = score;
		}
		
		public double getHiddenFitness() {
			return this.hiddenFitness;
		}
		
		public void setHiddenFitness(double fitness) {
			this.hiddenFitness = fitness;
		}

		@Override
		public int compareTo(RepresentationWithScore o) {
			if (this.score > o.score) return 1;
			else if (this.score < o.score) return -1;
			return 0;
		}
	}
}
