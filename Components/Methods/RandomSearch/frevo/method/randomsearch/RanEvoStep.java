package frevo.method.randomsearch;

import core.AbstractProblem;
import core.AbstractRanking;
import core.AbstractSingleProblem;
import core.ComponentXMLData;
import core.ProblemXMLData;

/**
 * Defines one evolutionary step for {@link RandomSearch} method. 
 * 
 * @author Sergii Zhevzhyk
 */
public class RanEvoStep {
	
	/**
	 * The number of current generation
	 */
	int generation;
	
	/**
	 * Best fitness that has been already achieved
	 */
	double bestFitness;
	
	/**
	 * Max fitness which could be achieved
	 */
	double maxFitness;
	
	/**
	 * This instance allows to rank the whole population
	 */
	AbstractRanking ranking;
	
	/**
	 * Defines information about problem
	 */
	ProblemXMLData problemData;
	
	/**
	 * Initializes new instance of {@link RanEvoStep} class
	 * @param problemData defines information about problem
	 * @param rankingData contains information about ranking
	 * @throws InstantiationException couldn't initialize the instance for ranking
	 */
	public RanEvoStep(ProblemXMLData problemData, ComponentXMLData rankingData) throws InstantiationException {
		this.problemData = problemData;
		loadMaxFitness(problemData);
		ranking = rankingData.getNewRankingInstance();
	}

	/**
	 * Gets the number of current generation
	 * @return the number of current generation 
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Sets the number of current generation
	 * @param generation the number of current generation
	 */
	public void setGeneration(int generation) {
		this.generation = generation;
	}

	/**
	 * Gets the best fitness which has been achieved
	 * @return best fitness
	 */
	public double getBestFitness() {
		return bestFitness;
	}

	/**
	 * Sets the best fitness which has been achieved
	 * @param bestFitness best fitness
	 */
	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}
	
	/**
	 * Gets the max fitness which could be achieved
	 * @return max fitness
	 */
	public double getMaxFitness() {
		return maxFitness;
	}

	/**
	 * Gets the ranking instance
	 * @return ranking instance 
	 */
	public AbstractRanking getRanking() {
		return ranking;
	}
	
	/**
	 * Gets information about problem
	 * @return instance which contains information about problem
	 */
	public ProblemXMLData getProblemData() {
		return problemData;
	}

	/**
	 * Loads the max fitness from description of the problem
	 * @param problemData contains information about the problem
	 */
	private void loadMaxFitness(ProblemXMLData problemData) {
		maxFitness = Double.MAX_VALUE;
		try {
			AbstractProblem problem = problemData.getNewProblemInstance();
			if (problem instanceof AbstractSingleProblem) {
				AbstractSingleProblem sproblem = (AbstractSingleProblem) problem;
				maxFitness = sproblem.getMaximumFitness();
			}
		} 
		catch (InstantiationException e1) {
		}				
	}
}
