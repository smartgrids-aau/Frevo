package nnga;

import core.AbstractProblem;
import core.AbstractRanking;
import core.AbstractSingleProblem;
import core.ComponentXMLData;
import core.ProblemXMLData;

/**
 * Defines one evolutionary step for {@link NNGA} method. 
 * 
 * @author Sergii Zhevzhyk
 */
public class NNGAStep {
	/**
	 * The number of current generation
	 */
	private int generation;
	
	/**
	 * Best fitness that has been already achieved
	 */
	private double bestFitness;
	
	/**
	 * Hash of best candidate
	 */
	private String hashOfBest;
	
	/**
	 * Max fitness which could be achieved
	 */
	private double maxFitness;
	
	/**
	 * This instance allows to rank the whole population
	 */
	private AbstractRanking ranking;
	
	/**
	 * Defines information about problem
	 */
	private ProblemXMLData problemData;
	
	/** Indicates if the passed problem component is an instance of AbstractMultiProblem */
	boolean isMultiProblem;
	
	/**
	 * Initializes new instance of {@link NNGAStep} class
	 * @param problemData defines information about problem
	 * @param rankingData contains information about ranking
	 * @throws InstantiationException couldn't initialize the instance for ranking
	 */
	public NNGAStep(ProblemXMLData problemData, ComponentXMLData rankingData) throws InstantiationException {
		this.problemData = problemData;
		loadMaxFitness(problemData);
		this.hashOfBest = "";
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
	 * Returns the hash for the best candidate
	 * @return
	 */
	public String getHashOfBest() {
		return hashOfBest;
	}
	
	/**
	 * Sets the hash for the best candidate
	 * @param hashOfBest
	 */
	public void setHashOfBest(String hashOfBest) {
		this.hashOfBest = hashOfBest;
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
				isMultiProblem = false;
			} else {
				isMultiProblem = true;
			}
		} 
		catch (InstantiationException e1) {
		}				
	}	
}
