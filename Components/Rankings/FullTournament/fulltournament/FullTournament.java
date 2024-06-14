package fulltournament;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
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


public class FullTournament extends AbstractRanking {
	
	public FullTournament(Hashtable<String, XMLFieldEntry> properties) {
		super(properties);
	}

	private AbstractRepresentation bestCandidate;
	
	//Vector for Thread safety
	Vector<Resultrecord> results = new Vector<Resultrecord>();
	
	private static int N_TREADS;
	
	class EvaluationRunnable implements Runnable {
		private ProblemXMLData problem;
		private AbstractRepresentation ireps[];
		int k, j;

		public EvaluationRunnable(ProblemXMLData problem, AbstractRepresentation ireps[], int k, int j) {
			this.problem = problem;
			this.ireps = ireps;
			this.k = k;
			this.j = j;
		}

		@Override
		public void run() {
			try {				
				AbstractMultiProblem p;
				AbstractComponent comp = problem.getNewProblemInstance();
				if (comp instanceof AbstractMultiProblem) {
					p = (AbstractMultiProblem)comp;
				} else {
					throw new Error("Full Tournament requires an instance of AbstractMultiProblem");
				}
					
				List<RepresentationWithScore> gameresult = p.evaluateFitness(new AbstractRepresentation[]{ireps[0],ireps[1]});
				
				double point1 = 0;
				double point2 = 0;
				if (gameresult.get(0).getRepresentation() == ireps[0]) {
					point1 = gameresult.get(0).getScore();
					point2 = gameresult.get(1).getScore();
				} else if (gameresult.get(1).getRepresentation() == ireps[1]) {
					point1 = gameresult.get(1).getScore();
					point2 = gameresult.get(0).getScore();
				} else {
					throw new Error("Passed representations are invalid");
				}
				
				if (point1 > point2) {
					results.get(k).add(2);
				} else if (point1 < point2) {
					results.get(j).add(2);
				} else {
					results.get(k).add(1);
					results.get(j).add(1);
				}
				
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	}	

	@Override
	public int sortCandidates(final ArrayList<AbstractRepresentation> pop, ProblemXMLData problem, NESRandom random) {
		XMLFieldEntry nthreads = getProperties().get("parallelthreads");
		N_TREADS = Integer.parseInt(nthreads.getValue());
		
		ArrayList<AbstractRepresentation> result = new ArrayList<AbstractRepresentation>();
		
		results.clear();
		for (int i=0; i< pop.size();i++) {
			results.add(new Resultrecord(i));
		}
		
		int number_of_evaluations = 0;
		
		//Single thread
		if (N_TREADS <= 1) {
		
			//Create scores
			for (int k=0;k<pop.size()-1;k++) {
				AbstractRepresentation p1 = pop.get(k);
				for (int j = k+1;j<pop.size();j++) {
					AbstractRepresentation p2 = pop.get(j);
					
					//play game
					try {
						AbstractMultiProblem p;
						AbstractComponent comp = problem.getNewProblemInstance();
						if (comp instanceof AbstractMultiProblem) {
							p = (AbstractMultiProblem)comp;
						} else {
							throw new Error("Full Tournament requires an instance of AbstractMultiProblem");
						}
						
						p.setRandom(random.clone());
						List<RepresentationWithScore> gameresult = p.evaluateFitness(new AbstractRepresentation[]{p1,p2});
						number_of_evaluations++;
						
						double point1 = 0;
						double point2 = 0;
						if (gameresult.get(0).getRepresentation() == p1) {
							point1 = gameresult.get(0).getScore();
							point2 = gameresult.get(1).getScore();
						} else if (gameresult.get(1).getRepresentation() == p1) {
							point1 = gameresult.get(1).getScore();
							point2 = gameresult.get(0).getScore();
						} else {
							throw new Error("Passed representations are invalid");
						}
						
						if (point1 > point2) {
							results.get(k).add(2);
						} else if (point1 < point2) {
							results.get(j).add(2);
						} else {
							results.get(k).add(1);
							results.get(j).add(1);
						}
						
					
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
		// Multiple threads
		} else {
			ExecutorService executor = Executors.newFixedThreadPool(N_TREADS);
			
			for (int k=0;k<pop.size()-1;k++) {
				AbstractRepresentation p1 = pop.get(k);
				for (int j = k+1;j<pop.size();j++) {
					AbstractRepresentation p2 = pop.get(j);
					
					Runnable worker = new EvaluationRunnable(problem, new AbstractRepresentation[]{p1, p2},k,j);
					executor.execute(worker);
					number_of_evaluations++;
				}
			}
			
			// This will make the executor accept no new threads
			// and finish all existing threads in the queue
			executor.shutdown();
			// Wait until all threads are finish
			while (!executor.isTerminated()) {
				try {
					executor.awaitTermination(300, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		Collections.sort(results,Collections.reverseOrder());
		
		for (int s=0;s<pop.size();s++) {
			AbstractRepresentation r = pop.get(results.get(s).number);
			r.setFitness(results.get(s).score);
			result.add(r);
		}
		
		//copy result back into pop
		for (int u=0; u<pop.size();u++) {
			pop.set(u, result.get(u));
		}
		//pop = result;
		
		bestCandidate = pop.get(0);
		return number_of_evaluations;
	}
	
	private class Resultrecord implements Comparable<Resultrecord>{
		public int number;
		public int score = 0;
		
		public Resultrecord(int number) {
			this.number = number;
		}
		
		public void add(int point) {
			score += point;
		}

		@Override
		public int compareTo(Resultrecord other) {
			if (this.score > other.score) return 1;
			else if (this.score < other.score) return -1;
			else return 0;
		}
	}

	@Override
	public AbstractRepresentation getBestCandidate() {
		return bestCandidate;
	}

}
