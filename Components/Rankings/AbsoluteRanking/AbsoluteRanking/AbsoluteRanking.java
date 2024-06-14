package AbsoluteRanking;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.FrevoMain;
import utils.NESRandom;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ProblemXMLData;
import core.XMLFieldEntry;


public class AbsoluteRanking extends AbstractRanking {
	
	public AbsoluteRanking(Hashtable<String, XMLFieldEntry> properties) {
		super(properties);
	}

	AbstractRepresentation bestCandidate;

	class EvaluationRunnable implements Runnable {
		AbstractRepresentation cand;
		ProblemXMLData problem;
		private NESRandom generator;

		public EvaluationRunnable(AbstractRepresentation cand, ProblemXMLData problem, NESRandom random) {
			this.cand = cand;
			this.problem = problem;
			this.generator = random;
		}

		@Override
		public void run() {
			try {
				if (!FrevoMain.isRunning) {
					return;
				}
				AbstractSingleProblem p = (AbstractSingleProblem)(problem.getNewProblemInstance());
				p.setRandom(generator.clone());
				cand.reset(); //wipe clean state of the candidate 
				double fitness = p.evaluateFitness(cand);
				cand.setFitness(fitness);
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	}	

	@Override
	public int sortCandidates(final ArrayList<AbstractRepresentation> pop, ProblemXMLData problem, NESRandom random) {
		int nthreads=Integer.parseInt(getProperties().get("parallelthreads").getValue());
				

		int numberofevaluations = 0;
		
		if (nthreads<=1) {
			//evaluate using for loop
			for (AbstractRepresentation cand:pop) {
				if (!FrevoMain.isRunning) {
					return numberofevaluations;
				}
				
				if(!cand.isEvaluated()){
    				try {
    					AbstractSingleProblem p;
    					p = (AbstractSingleProblem)(problem.getNewProblemInstance());
    					p.setRandom(random.clone());
    					cand.reset(); //wipe clean state of the candidate 
    					double fitness = p.evaluateFitness(cand);
    					cand.setFitness(fitness);
    					numberofevaluations++;
    				} catch (InstantiationException e) {
    					e.printStackTrace();
    				}
				}
			}
		}
		else {
			// evaluate using thread pool
			ExecutorService executor = Executors.newFixedThreadPool(nthreads);
			for (AbstractRepresentation cand:pop) {
				if (!FrevoMain.isRunning) {
					return numberofevaluations;
				}
				if(!cand.isEvaluated()){
				    Runnable worker = new EvaluationRunnable(cand, problem, random);
				    executor.execute(worker);
				    numberofevaluations++;
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
			// close the pool and wait for all tasks to finish.
			
		}
		
		//sort with highest fitness first
		Collections.sort(pop, Collections.reverseOrder());
		
//		System.out.print("Best Agent: "+pop.get(0).getHash()+"\t");
//		System.out.println("Best Fitness: "+pop.get(0).getFitness());
//		
//		System.out.print("Worst Agent: "+pop.get(pop.size()-1).getHash()+"\t");
//		System.out.println("Worst Fitness: "+pop.get(pop.size()-1).getFitness());
		
		bestCandidate = pop.get(0);
		
		return numberofevaluations;
	}

	@Override
	public AbstractRepresentation getBestCandidate() {
		return bestCandidate;
	}


}
