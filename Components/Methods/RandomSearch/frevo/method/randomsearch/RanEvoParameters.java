package frevo.method.randomsearch;

import java.util.Hashtable;

import core.XMLFieldEntry;
import utils.NESRandom;

/**
 * Storage of parameters for {@link RandomSearch} method.
 * 
 * @author Sergii Zhevzhyk
 */
public class RanEvoParameters {
	
	/** 
	 * Number of parallel populations 
	 */
	private int POPULATIONNUMBER = 1;
	
	/**
	 * Number of representations in the population
	 */
	private int POPULATIONSIZE;
	
	/**
	 * Number of generations
	 */
	private int GENERATIONS;
	
	/** 
	 * Number of elite candidates 
	 */
	private int N_ELITE = 1;
	
	/**
	 * The method which is using the current parameters
	 */
	RandomSearch method;
	
	/**
	 * Constructs the instance of {@link RanEvoParameters} class for the method 
	 * @param method the method which is using the current parameters
	 */
	public RanEvoParameters(RandomSearch method) {		
		if (method == null){
			throw new NullPointerException(); 
		}			
		
		this.method = method;
	}
	
	/**
	 * Gets the number of populations
	 * @return the number of populations
	 */
	public int getPopulationNumber() {
		return POPULATIONNUMBER;
	}
	
	/**
	 * Gets the size of one population
	 * @return the size of one population
	 */
	public int getPopulationSize() {
		return POPULATIONSIZE;
	}

	/**
	 * Sets the size of one population
	 * @param populationSize the size of one population
	 */
	public void setPopulationSize(int populationSize) {
		POPULATIONSIZE = populationSize;
	}

	/**
	 * Gets the number of generations
	 * @return the number of generations
	 */
	public int getGenerations() {
		return GENERATIONS;
	}

	/**
	 * Sets the number of generations
	 * @param generations the number of generations 
	 */
	public void setGenerations(int generations) {
		GENERATIONS = generations;
	}
	
	/**
	 * Sets the number of elite candidates
	 * @param elite the number of elite candidates
	 */
	public void setElite(int elite) {
		N_ELITE = elite;
	}
	
	/**
	 * Gets the number of elite candidates
	 * @return the number of elite candidates
	 */
	public int getElite() {
		return N_ELITE;				
	}	
	
	/**
	 * Gets the generator of random numbers
	 * @return the instance of {@link NESRandom} class for generating of random numbers
	 */
	public NESRandom getGenerator() {
		NESRandom generator = method.getRandom();
		if (generator == null) {
			throw new NullPointerException();
		}			
		return generator;
	}

	/**
	 * Initialize parameters from method's properties
	 * @param properties properties of the method
	 */
	public void initialize(Hashtable<String, XMLFieldEntry> properties) {
		// Read the population size
		XMLFieldEntry popsize = properties.get("populationsize");
		POPULATIONSIZE = Integer.parseInt(popsize.getValue()); 
		
		// Read the number of generations
		XMLFieldEntry generations = properties.get("generations");		
		GENERATIONS = Integer.parseInt(generations.getValue());				
	}	
}
