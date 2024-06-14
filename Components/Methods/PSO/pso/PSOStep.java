package pso;

import core.AbstractProblem;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ComponentXMLData;
import core.ProblemXMLData;

/**
 * 
 * @author Sergii Zhevzhyk
 *
 */
public class PSOStep {

	private ProblemXMLData problemData;
	private AbstractRanking ranking;
	
	private Sparcle bestSparcle;
	private AbstractRepresentation bestRepresentation;
	
	private double bestFitness;
	
	/**
	 * Max fitness which could be achieved
	 */
	private double maxFitness;
	
	/**
	 * The number of current generation
	 */
	private int generation;

	public PSOStep(ProblemXMLData problemData, ComponentXMLData rankingData) throws InstantiationException {
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
	 * Gets the max fitness which could be achieved
	 * @return max fitness
	 */
	public double getMaxFitness() {
		return maxFitness;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}
	
	public Sparcle getBestSparcle() {
		return bestSparcle;
	}

	public void setBestSparcle(Sparcle bestSparcle) {
		this.bestSparcle = bestSparcle;
		this.bestRepresentation = bestSparcle.getRepresentation().clone();
	}
	
	public AbstractRepresentation getBestRepresentation() {
		return bestRepresentation;
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
