package nnga;

/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import utils.NESRandom;
import utils.StatKeeper;
import core.AbstractMethod;
import core.AbstractRepresentation;
import core.ComponentType;
import core.ComponentXMLData;
import core.PopulationDiversity;
import core.ProblemXMLData;
import core.XMLFieldEntry;
import core.XMLMethodStep;

/**
 * A genetic algorithm designed to evolve any kind of representation. Supports
 * multiple populations and several ranking systems.
 * 
 * @author Istvan Fehervari, Andreas Pfandler
 */
public class NNGA extends AbstractMethod {

	// Evolution parameters
	NNGAParameters parameters;
	
	/** List of lists containing the candidates sorted in populations */
	private LinkedList<SimplePopulation> pops = new LinkedList<SimplePopulation>();

	/** Entries contain the best fitness over all populations */
	private StatKeeper bestFitnessStats;
	
	private StatKeeper numEvaluations;
	
	// Statistics about population diversity
	private StatKeeper diversity;
	private StatKeeper maxDiversity;
	private StatKeeper minDiversity;
	private StatKeeper standardDeviation;
	
	private StatKeeper numElite;
	private StatKeeper numMutation;
	private StatKeeper numXOver;
	private StatKeeper numRenew;
	private StatKeeper numRandom;
	
	private StatKeeper effectivityElite;
	private StatKeeper effectivityMutation;
	private StatKeeper effectivityXOver;
	private StatKeeper effectivityRenew;
	private StatKeeper effectivityRandom;
	
	
	/** Constructs a new NNGA class */
	public NNGA(NESRandom random) {
		super(random);
		parameters = new NNGAParameters(this);
	}

	@Override
	public void runOptimization(ProblemXMLData problemData,
			ComponentXMLData representationData, ComponentXMLData rankingData,
			Hashtable<String, XMLFieldEntry> properties) {
		// load and calculate parameters
		parameters.initialize(getProperties());

		createPopulations(problemData, representationData);
		
		createStatistics();
		
		try {
			
			NNGAStep step = new NNGAStep(problemData, rankingData);
			
			// Iterate through generations
			for (int generation = 0; generation < parameters.getGenerations(); generation++) {
				
				step.setGeneration(generation);
				
				if (!evolve(step)) {
					break;
				}
			}
			
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (Exception e) {		
			e.printStackTrace();
		}

		// finalize progress
		setProgress(1f);
	}

	@Override
	public void continueOptimization(ProblemXMLData problemData,
			ComponentXMLData representationData, ComponentXMLData rankingData,
			Hashtable<String, XMLFieldEntry> properties,
			Document doc) {
				
		// load and calculate parameters
		parameters.initialize(getProperties());

		if (!loadFromDoc(problemData, representationData, doc)) {
			return;
		}	
		
		// record the best fitness over the evolution
		Node dpopulations = doc.selectSingleNode("/frevo/populations");
		double best_fitness = Double.parseDouble(dpopulations.valueOf("./@best_fitness"));
		int lastGeneration  = Integer.parseInt(dpopulations.valueOf("./@generation"));
		
		createStatistics();
		
		// Mutate all populations except when this is the last generation
		for (SimplePopulation population : pops) {
			try {
				population.evolve(lastGeneration, pops);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		try {
			
			NNGAStep step = new NNGAStep(problemData, rankingData);
			
			step.setBestFitness(best_fitness);
			
			// Iterate through generations
			for (int generation = lastGeneration + 1; generation < parameters.getGenerations(); generation++) {
				
				step.setGeneration(generation);
				
				if (!evolve(step)) {
					break;
				}
			}
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// finalize progress
		setProgress(1f);
	}
	
	@Override
	public ArrayList<ArrayList<AbstractRepresentation>> loadFromXML(Document doc) {
		// final list to be returned
		ArrayList<ArrayList<AbstractRepresentation>> populations = new ArrayList<ArrayList<AbstractRepresentation>>();

		// get population root node
		Node dpopulations = doc.selectSingleNode("/frevo/populations");

		// get number of populations
		int populationCount = Integer
				.parseInt(dpopulations.valueOf("./@count"));
		// get number of current generation
		int currentGeneration = Integer
				.parseInt(dpopulations.valueOf("./@generation"));

		// get population size
		List<? extends Node> populationsNode = dpopulations
				.selectNodes(".//population");
		int populationSize = populationsNode.get(0).selectNodes("*").size();

		// calculate total representations
		int totalRepresentations = populationCount * populationSize;
		int currentPopulation = 0;
		int currentRepresentation = 0;

		// Iterate through the population nodes
		Iterator<?> populationIterator = populationsNode.iterator();
		while (populationIterator.hasNext()) {
			Node populationNode = (Node) populationIterator.next();

			// create list of candidate representations for this population
			ArrayList<AbstractRepresentation> result = new ArrayList<AbstractRepresentation>();

			// track current progress
			currentRepresentation = 0;
			try {
				// Obtain an iterator over the representations
				List<?> representations = populationNode.selectNodes("./*");
				Iterator<?> representationsIterator = representations
						.iterator();

				while (representationsIterator.hasNext()) {
					// calculate current position for progress reporting
					int currentItem = currentPopulation * populationSize
							+ currentRepresentation + 1;

					// report loading state
					FrevoMain.setLoadingProgress((float) currentItem
							/ totalRepresentations);

					// step to next node
					Node net = (Node) representationsIterator.next();

					// construct representation based on loaded representation
					// data
					ComponentXMLData representation = FrevoMain
							.getSelectedComponent(ComponentType.FREVO_REPRESENTATION);
					AbstractRepresentation member = representation
							.getNewRepresentationInstance(0, 0, null);
					
					// load representation data from the XML into the instance
					member.loadFromXML(net);
					
					// add data to current population list
					result.add(member);

					// increment tracker
					currentRepresentation++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			populations.add(result);

			currentPopulation++;
		}
		
		// Load the number of generations
		XMLFieldEntry gensize = getProperties().get("generations");
		if (gensize != null){
			int generations = Integer.parseInt(gensize.getValue());
			// TODO check max fitness also
			// set boolean value which shows possibility of continuation of experiment
			// if maximum number of generations hasn't been reached.
			setCanContinue(currentGeneration + 1 < generations);
		}
		
		return populations;
	}
		
	/**
	 * Evolve population by one generation
	 * 
	 * @param step contains information about current step and some global variables for execution of an experiment 
	 * @return <i>true</i> if evolution has been finished successfully, <i>false</i> - otherwise
	 * @throws Exception error has been occurred during evolution process
	 */
	private boolean evolve(NNGAStep step) throws Exception {
		// check for pause flag
		if (handlePause()) {
			return false;
		}

		// sets our current progress
		setProgress((float) step.getGeneration() / (float) parameters.getGenerations());
		
		long currentSeed = getRandom().getSeed();
		long tempSeed = currentSeed;

		// indicates if we have to save this generation
		boolean saveThisGeneration = false;

		// Rank each population using the provided ranking

		// record the best fitness over all populations
		double best_fitness_pop = -Double.MAX_VALUE;//Double.MIN_VALUE;
		String hash_of_best_pop = pops.get(0).getBestCandidate().getHash();

		int sumOldNumElite = 0;
		int sumOldNumMutation = 0;
		int sumOldNumXOver = 0;
		int sumOldNumRenew = 0;
		int sumOldNumRandom = 0;

		int sumNumElite = 0;
		int sumNumMutation = 0;
		int sumNumXOver = 0;
		int sumNumRenew = 0;
		int sumNumRandom = 0;

		int sumNumEliteElite = 0;
		int sumNumMutationElite = 0;
		int sumNumXOverElite = 0;
		int sumNumRenewElite = 0;
		int sumNumRandomElite = 0;
		
		for (SimplePopulation pop : pops) {

			// check for pause flag
			if (handlePause()) {
				return false;
			}

			pop.setParameter("generation", String.valueOf(step.getGeneration()));
			
			// sort population
			int evaluations = pop.sortCandidates(step.getRanking(), step.getProblemData(), new NESRandom(tempSeed));
			
			numEvaluations.add(evaluations);

			// increment seed
			tempSeed++;

			if (pop.getBestCandidate().getFitness() > best_fitness_pop)
				best_fitness_pop = pop.getBestCandidate().getFitness();
			
			if (!pop.getBestCandidate().getHash().equals(hash_of_best_pop)) {
				hash_of_best_pop = pop.getBestCandidate().getHash();
			}
			
			sumOldNumElite += pop.getOldnumElite();
			sumOldNumMutation += pop.getOldnumMutation();
			sumOldNumXOver += pop.getOldnumXOver();
			sumOldNumRenew += pop.getOldnumRenew();
			sumOldNumRandom += pop.getOldnumRandom();
			
			sumNumElite += pop.getNumElite();
			sumNumMutation += pop.getNumMutation();
			sumNumXOver += pop.getNumXOver();
			sumNumRenew += pop.getNumRenew();
			sumNumRandom += pop.getNumRandom();

			sumNumEliteElite += pop.getNumEliteElite();
			sumNumMutationElite += pop.getNumMutationElite();
			sumNumXOverElite += pop.getNumXOverElite();
			sumNumRenewElite += pop.getNumRenewElite();
			sumNumRandomElite += pop.getNumRandomElite();

		}
		

		double effElite = sumOldNumElite == 0? 0 : ((double)sumNumEliteElite)/((double)sumOldNumElite);
		double effMutation = sumOldNumMutation == 0? 0 : ((double)sumNumMutationElite)/((double)sumOldNumMutation);
		double effXOver = sumOldNumXOver == 0? 0 : ((double)sumNumXOverElite)/((double)sumOldNumXOver);
		double effRenew = sumOldNumRenew == 0? 0 : ((double)sumNumRenewElite)/((double)sumOldNumRenew);
		double effRandom = sumOldNumRandom == 0? 0 : ((double)sumNumRandomElite)/((double)sumOldNumRandom);

		// add the best fitness of this generation to statistics
		bestFitnessStats.add(best_fitness_pop);

		PopulationDiversity diversityCalc = new PopulationDiversity(pops.get(0).getMembers()); 
		diversity.add(diversityCalc.getAverageDiversity());
		maxDiversity.add(diversityCalc.getMaxDiversity());
		minDiversity.add(diversityCalc.getMinDiversity());
		standardDeviation.add(diversityCalc.getStandardDeviation());
		
		effectivityElite.add(effElite);
		effectivityMutation.add(effMutation);
		effectivityXOver.add(effXOver);
		effectivityRenew.add(effRenew);
		effectivityRandom.add(effRandom);
		
		numElite.add(sumNumElite);
		numMutation.add(sumNumMutation);
		numXOver.add(sumNumXOver);
		numRenew.add(sumNumRenew);
		numRandom.add(sumNumRandom);
		
		// note, if there was an improvement (includes 0th
		// generation)
		if (best_fitness_pop > step.getBestFitness()) {
			step.setBestFitness(best_fitness_pop);
			// indicate saving
			if (parameters.getSaveImprovements())
				saveThisGeneration = true;
		}
		
		if (!hash_of_best_pop.equals(step.getHashOfBest()) && step.isMultiProblem) {
			step.setHashOfBest(hash_of_best_pop);
			
			if (parameters.getSaveImprovements())
				saveThisGeneration = true;
		}

		// check periodic save
		if ((parameters.getSaveInterval() != 0) && (step.getGeneration() % parameters.getSaveInterval() == 0)) {
			saveThisGeneration = true;
		}

		// check last generation
		if (step.getGeneration() == parameters.getGenerations() - 1) {
			saveThisGeneration = true;
		}

		String fitnessstring;
		if (step.getProblemData().getComponentType() == ComponentType.FREVO_PROBLEM) {
			fitnessstring = " (" + step.getBestFitness() + ")";
		} else {
			// multiproblem
			fitnessstring = "";
		}
						
		String fileName = getFileName(step.getProblemData(), step.getGeneration(), fitnessstring);
		Element xmlLastState = saveResults(step.getGeneration());
		xmlLastState.addAttribute("best_fitness", String.valueOf(step.getBestFitness()));
		
		// save the last state of evaluation
		XMLMethodStep state = new XMLMethodStep(fileName, xmlLastState, this.seed, currentSeed);
		setLastResults(state);
		// save generation
		if (saveThisGeneration) {
			FrevoMain.saveResult(
					fileName , xmlLastState, this.seed, currentSeed
					);
		}				

		// Stop if maximum fitness has been achieved
		if (step.getBestFitness() >= step.getMaxFitness()) {
			System.out.println("Maximum fitness of (" + step.getMaxFitness()
					+ ") has been achieved.");
			return false;
		}

		// Mutate all populations except when this is the last generation
		for (SimplePopulation population : pops) {
			population.evolve(step.getGeneration(), pops);
		}
		
		return true;
	}

	/**
	 * Creates new population(s)
	 * @param problemData information about problem
	 * @param representationData information about representation
	 * @return <i>true</i> if population has been initialized successfully, <i>false</i> - otherwise
	 */
	private boolean createPopulations(ProblemXMLData problemData,
			ComponentXMLData representationData) {
		// obtain problem requirements
		int inputnumber = problemData.getRequiredNumberOfInputs();
		int outputnumber = problemData.getRequiredNumberOfOutputs();
		
		// generate initial population(s)
		for (int i = 0; i < parameters.getPopulationNumber(); i++) {
			try {
				pops.add(new SimplePopulation(representationData, parameters, inputnumber, outputnumber));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Initializes population(s) from XML document
	 * @param problemData information about problem
	 * @param representationData information about representation
	 * @param doc XML document which contains data about population 
	 * @return <i>true</i> if population has been loaded successfully, <i>false</i> - otherwise
	 */
	private boolean loadFromDoc(ProblemXMLData problemData, ComponentXMLData representationData, Document doc) {
		// obtain problem requirements
		int inputnumber = problemData.getRequiredNumberOfInputs();
		int outputnumber = problemData.getRequiredNumberOfOutputs();

		// record the best fitness over the evolution
		Node dpopulations = doc.selectSingleNode("/frevo/populations");
		long randomseed = Long.parseLong(dpopulations.valueOf("./@randomseed"));
		getRandom().setSeed(randomseed);
										
		// load initial population(s)
		ArrayList<ArrayList<AbstractRepresentation>> loadedPops = loadFromXML(doc);
		for (int i = 0; i < parameters.getPopulationNumber(); i++) {
			try {
				pops.add(new SimplePopulation(representationData, parameters, inputnumber, outputnumber, loadedPops.get(i)));
			} catch (Exception e) {				
				e.printStackTrace();
				return false;
			}
		}			
		return true;		
	}

	/**
	 * Gets the file name for saving of current results
	 * @param problemData description of the problem
	 * @param generation number of current generation 
	 * @param fitnessstring max fitness for this generation
	 * @return file name
	 */
	private String getFileName(ProblemXMLData problemData, int generation,
			String fitnessstring) {
		
		DecimalFormat fm = new DecimalFormat("000");
		return problemData.getName() + "_g"
				+ fm.format(generation) + fitnessstring;
	}

	/**
	 * Saves all population data to a new XML element and returns it.
	 * @param generation number of current generation
	 * @return information about population in XML format  
	 */
	public Element saveResults(int generation) {
		Element dpopulations = DocumentFactory.getInstance().createElement(
				"populations");

		dpopulations.addAttribute("count", String.valueOf(parameters.getPopulationNumber()));
		dpopulations.addAttribute("generation", String.valueOf(generation));
		dpopulations.addAttribute("randomseed", String.valueOf(this.getSeed()));

		for (SimplePopulation pop : pops) {
			pop.exportXml(dpopulations);
		}
		
		return dpopulations;
	}
	
	
	/**
	 * Creates instances for statistics purposes
	 */
	private void createStatistics() {
		// create statistics
		bestFitnessStats = new StatKeeper(true, "Best Fitness ("
				+ FrevoMain.getCurrentRun() + ")", "Generations");

		numEvaluations = new StatKeeper(true, "numSimulations", "Generations");
		
		diversity = new StatKeeper(true, "Diversity", "Generations");
		maxDiversity = new StatKeeper(true, "Max. diversity", "Generations");
		minDiversity = new StatKeeper(true, "Min. diversity", "Generations");
		standardDeviation = new StatKeeper(true, "Deviation", "Generations");
		
		numElite = new StatKeeper(true, "number of Elite", "Generations");
		numMutation = new StatKeeper(true, "number of Mutation", "Generations");
		numXOver = new StatKeeper(true, "number of XOver", "Generations");
		numRenew = new StatKeeper(true, "number of Renew", "Generations");
		numRandom = new StatKeeper(true, "number of Random", "Generations");
		
		effectivityElite = new StatKeeper(true, "effectivity of Elite", "Generations");
		effectivityMutation = new StatKeeper(true, "effectivity of Mutation", "Generations");
		effectivityXOver = new StatKeeper(true, "effectivity of XOver", "Generations");
		effectivityRenew = new StatKeeper(true, "effectivity of Renew", "Generations");
		effectivityRandom = new StatKeeper(true, "effectivity of Random", "Generations");
		
		// register statistics
		FrevoMain.addStatistics(bestFitnessStats,true);
		FrevoMain.addStatistics(diversity,true);

		FrevoMain.addStatistics(numEvaluations,false);
		FrevoMain.addStatistics(maxDiversity,false);
		FrevoMain.addStatistics(minDiversity,false);
		FrevoMain.addStatistics(standardDeviation,false);
		
		FrevoMain.addStatistics(numElite,false);
		FrevoMain.addStatistics(numMutation,false);
		FrevoMain.addStatistics(numXOver,false);
		FrevoMain.addStatistics(numRenew,false);
		FrevoMain.addStatistics(numRandom,false);

		FrevoMain.addStatistics(effectivityElite,false);
		FrevoMain.addStatistics(effectivityMutation,false);
		FrevoMain.addStatistics(effectivityXOver,false);
		FrevoMain.addStatistics(effectivityRenew,false);
		FrevoMain.addStatistics(effectivityRandom,false);
	}
	
	
}
