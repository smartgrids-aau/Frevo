package pso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
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
import core.AbstractSingleProblem;
import core.ComponentType;
import core.ComponentXMLData;
import core.PopulationDiversity;
import core.ProblemXMLData;
import core.XMLFieldEntry;
import core.XMLMethodStep;

/**
 * Particle Swarm Optimization (PSO)
 * 
 * @author Sergii Zhevzhyk
 *
 */
public class PSO extends AbstractMethod {

	ArrayList<Sparcle> swarm = new ArrayList<Sparcle>();
	
	PSOParameters parameters;
	
	/** Entries contain the best fitness over all populations */
	private StatKeeper bestFitnessStats;
	
	private StatKeeper numEvaluations;
	
	// Statistics about population diversity
	private StatKeeper diversity;
	private StatKeeper maxDiversity;
	private StatKeeper minDiversity;
	private StatKeeper standardDeviation;
	
	public PSO(NESRandom random) {
		super(random);
		parameters = new PSOParameters(this);
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
			PSOStep step = new PSOStep(problemData, rankingData);
			
			// Iterate through generations
			for (int generation = 0; generation < parameters.GENERATIONS; generation++) {
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
			Hashtable<String, XMLFieldEntry> properties, Document doc) {
		// load and calculate parameters
		parameters.initialize(getProperties());
		
		createStatistics();
	}

	@Override
	public ArrayList<ArrayList<AbstractRepresentation>> loadFromXML(Document doc) {
		// final list to be returned
		ArrayList<ArrayList<AbstractRepresentation>> populations = new ArrayList<ArrayList<AbstractRepresentation>>();

		// get population root node
		Node dpopulations = doc.selectSingleNode("/frevo/populations");

		// get number of populations
		/*int populationCount = Integer
				.parseInt(dpopulations.valueOf("./@count"));*/
		// get number of current generation
		/*int currentGeneration = Integer
				.parseInt(dpopulations.valueOf("./@generation"));*/

		// get population size
		List<? extends Node> populationsNode = dpopulations
				.selectNodes(".//population");
		int populationSize = populationsNode.get(0).selectNodes("*").size();

		// calculate total representations
		int totalRepresentations = /*populationCount **/ populationSize;
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
		/*XMLFieldEntry gensize = getProperties().get("generations");
		if (gensize != null){
			int generations = Integer.parseInt(gensize.getValue());
			// TODO check max fitness also
			// set boolean value which shows possibility of continuation of experiment
			// if maximum number of generations hasn't been reached.
			setCanContinue(currentGeneration + 1 < generations);
		}*/
		
		return populations;
	}
	
	
	private boolean evolve(PSOStep step) {
		
		// sets our current progress
		setProgress((float) step.getGeneration() / (float) parameters.GENERATIONS);
		
		long currentSeed = getRandom().getSeed();
		
		//int evaluations = sortCandidates(step.getRanking(), step.getProblemData(), new NESRandom(generator.getSeed()));
		//numEvaluations.add(evaluations);
		
		boolean newFitness = false;
		for (Sparcle sparcle:swarm) {
			double fitness;
			try {
				fitness = evaluateCandidate(sparcle, step.getProblemData());
				if (fitness > step.getBestFitness()) {
					step.setBestFitness(fitness);
					step.setBestSparcle(sparcle);
					newFitness = true;
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
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
	
		if (newFitness) {	
			FrevoMain.saveResult(
						fileName , xmlLastState, this.seed, currentSeed
						);			
		}
		
		// add the best fitness of this generation to statistics
		bestFitnessStats.add(step.getBestFitness());
		
	
		ArrayList<AbstractRepresentation> representations = new ArrayList<AbstractRepresentation>();
		for (Sparcle sparcle:swarm) {
			representations.add(sparcle.getRepresentation());
		}
		PopulationDiversity diversityCalc = new PopulationDiversity(representations); 
		diversity.add(diversityCalc.getAverageDiversity());
		maxDiversity.add(diversityCalc.getMaxDiversity());
		minDiversity.add(diversityCalc.getMinDiversity());
		standardDeviation.add(diversityCalc.getStandardDeviation());

		
		for (Sparcle sparcle:swarm) {
			// check for pause flag
			if (handlePause()) {
				return false;
			}
			
			AbstractRepresentation representation = sparcle.getRepresentation();
			representation.mutate("PSO", parameters.W, parameters.C1, parameters.C2, sparcle.getVelocities(), sparcle.getLocalBestRepresentation(),
					step.getBestRepresentation());
			representation.setEvaluated(false);
		}
		
		// check for pause flag
		if (handlePause()) {
			return false;
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

	private double evaluateCandidate(Sparcle sparcle,
			ProblemXMLData problem) throws InstantiationException {

		AbstractRepresentation candidate = sparcle.getRepresentation();
		
		double fitness;
		if (!candidate.isEvaluated()) {
			AbstractSingleProblem p;
			p = (AbstractSingleProblem) (problem.getNewProblemInstance());
			p.setRandom(getRandom().clone());
			candidate.reset(); // wipe clean state of the candidate
			fitness = p.evaluateFitness(candidate);
			candidate.setFitness(fitness);
		} else {
			fitness = candidate.getFitness();
		}
		
		if (sparcle.getLocalBestFitness() < fitness) {
			sparcle.setLocalBestFitness(fitness);
			sparcle.setLocalBestRepresentation(candidate);
		}
		return fitness;

	}
	
	/*
	private int sortCandidates(AbstractRanking ranking, ProblemXMLData problemData, NESRandom nesRandom) {
		ArrayList<AbstractRepresentation> representations = new ArrayList<AbstractRepresentation>();
		for (Sparcle sparcle:swarm) {
			representations.add(sparcle.getRepresentation());
		}
		int evals = ranking.sortCandidates(representations, problemData, nesRandom);
		return evals;
	}
	*/

	private boolean createPopulations(ProblemXMLData problemData,
			ComponentXMLData representationData) {
		// obtain problem requirements
		int inputnumber = problemData.getRequiredNumberOfInputs();
		int outputnumber = problemData.getRequiredNumberOfOutputs();
		
		for (int i=0; i<parameters.POPULATIONSIZE; i++) {
			try {
								
				AbstractRepresentation member = representationData.getNewRepresentationInstance(inputnumber, outputnumber, parameters.getGenerator());
				Sparcle sparcle = new Sparcle(member);
				swarm.add(sparcle);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;		
	}
	
	/**
	 * Saves all population data to a new XML element and returns it.
	 * @param generation number of current generation
	 * @return information about population in XML format  
	 */
	public Element saveResults(int generation) {
		Element dpopulations = DocumentFactory.getInstance().createElement(
				"populations");

		//dpopulations.addAttribute("count", String.valueOf(parameters.getPopulationNumber()));
		dpopulations.addAttribute("generation", String.valueOf(generation));
		dpopulations.addAttribute("randomseed", String.valueOf(this.getSeed()));

		Element dpop = dpopulations.addElement("population");
		
		// add all members of the population
		for(Sparcle sparcle : swarm) {
			AbstractRepresentation rep = sparcle.getRepresentation();
			rep.exportToXmlElement(dpop);
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
		
		// register statistics
		FrevoMain.addStatistics(bestFitnessStats,true);
		FrevoMain.addStatistics(diversity,true);

		FrevoMain.addStatistics(numEvaluations,false);
		FrevoMain.addStatistics(maxDiversity,false);
		FrevoMain.addStatistics(minDiversity,false);
		FrevoMain.addStatistics(standardDeviation,false);		
	}
}
