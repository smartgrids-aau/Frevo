package nnga;

import java.util.Hashtable;

import utils.NESRandom;
import core.XMLFieldEntry;

/**
 * Storage of parameters for {@link NNGA} method.
 * 
 * @author Sergii Zhevzhyk
 */
public class NNGAParameters {
	/** Number of parallel populations */
	private int POPULATIONNUMBER;
	/** Size of each population */
	private int POPULATIONSIZE;
	/** Number of generations to calculate */
	private int GENERATIONS;
	/** Severity of the mutations that occur */
	private float MUTATIONSEVERITY;
	/** Probability that a mutation occurs */
	private float MUTATIONPROBABILITY;
	/** Number of elite candidates */
	private int N_ELITE;
	/** Number of randomly selected candidates in the next population */
	private int N_RANDOM;
	/** Number of mutated candidates in the next population */
	private int N_MUTATE;
	/** Number of randomly generated candidates in the next population */
	private int N_RENEW;
	/** Number of new candidates created by recombination in the next population */
	private int N_XOVER;
	/** Number of generations till an inter-population crossover happens */
	private int N_INTERXOVER_FREQ;
	/** The index of the mutation method to be used */
	private int MUTATIONMETHOD;
	/** The index of the recombination method to be used */
	private int XOVERMETHOD;
	/** Indicates after how many generations should a saving occur */
	private int SAVEINTERVAL;

	/** Indicates if improvements should be saved */
	private boolean SAVEIMPROVEMENTS = false;
	
	/**
	 * The method which is using the current parameters
	 */
	private NNGA method;

	/**
	 * Constructs the instance of {@link NNGAParameters} class for the method 
	 * @param method the method which is using the current parameters
	 */
	public NNGAParameters(NNGA method) {
		if (method == null){
			throw new NullPointerException(); 
		}			
		
		this.method = method;
	}
	
	/**
	 * Initialize parameters from method's properties
	 * @param properties properties of the method
	 */
	public void initialize(Hashtable<String, XMLFieldEntry> properties) {
		// Load the number of populations
		XMLFieldEntry popnum = properties.get("populationnumber");
		POPULATIONNUMBER = Integer.parseInt(popnum.getValue());

		// Load population size
		XMLFieldEntry popsize = properties.get("populationsize");
		POPULATIONSIZE = Integer.parseInt(popsize.getValue());

		// Load the number of generations
		XMLFieldEntry gensize = properties.get("generations");
		GENERATIONS = Integer.parseInt(gensize.getValue());

		// Load save interval
		XMLFieldEntry saveint = properties.get("saveinterval");
		SAVEINTERVAL = Integer.parseInt(saveint.getValue());

		// Load improvement saving flag
		XMLFieldEntry saveimp = properties.get("saveImprovements");
		SAVEIMPROVEMENTS = Boolean.parseBoolean(saveimp.getValue());

		// Load the percentage of elite candidates in the whole population
		XMLFieldEntry p_elite = properties.get("percentage_elite");
		float P_ELITE = Float.parseFloat(p_elite.getValue());

		// Load the percentage of randomly selected candidates in the next population
		XMLFieldEntry p_random = properties.get("percentage_random");
		float P_RANDOM = Float.parseFloat(p_random.getValue());

		// Load the percentage of mutated candidates in the next population
		XMLFieldEntry p_mutate = properties.get("percentage_mutate");
		float P_MUTATE = Float.parseFloat(p_mutate.getValue());

		// Load the percentage of randomly generated candidates in the next population
		XMLFieldEntry p_renew = properties.get("percentage_renew");
		float P_RENEW = Float.parseFloat(p_renew.getValue());

		// Load the percentage of new candidates created by recombination in the next population
		XMLFieldEntry p_xover = properties.get("percentage_xover");
		float P_XOVER = Float.parseFloat(p_xover.getValue());

		// Load the frequency of inter-population crossover
		XMLFieldEntry n_inter = properties.get("interXover_frequency");
		N_INTERXOVER_FREQ = Integer.parseInt(n_inter.getValue());

		// Load the default mutation method
		XMLFieldEntry mm = properties.get("mutationMethod");
		MUTATIONMETHOD = Integer.parseInt(mm.getValue());

		// Load the default recombination method
		XMLFieldEntry xm = properties.get("xoverMethod");
		XOVERMETHOD = Integer.parseInt(xm.getValue());

		// Load mutation severity
		XMLFieldEntry ms = properties.get("mutationseverity");
		MUTATIONSEVERITY = Float.parseFloat(ms.getValue());
		
		// Load mutation severity
		XMLFieldEntry mp = properties.get("mutationprobability");
		MUTATIONPROBABILITY = Float.parseFloat(mp.getValue());

		// calculate numbers based on percentages
		N_ELITE = (int) (POPULATIONSIZE * P_ELITE);
		N_RANDOM = (int) (POPULATIONSIZE * P_RANDOM);
		N_MUTATE = (int) (POPULATIONSIZE * P_MUTATE);
		N_RENEW = (int) (POPULATIONSIZE * P_RENEW);
		N_XOVER = (int) (POPULATIONSIZE * P_XOVER);

		// fix rounding errors
		int roundingError = POPULATIONSIZE - N_ELITE - N_RANDOM - N_MUTATE
				- N_RENEW - N_XOVER;

		N_ELITE += roundingError;
	}

	/**
	 * Gets the number of parallel populations
	 * @return Number of parallel populations
	 */
	public int getPopulationNumber() {
		return POPULATIONNUMBER;
	}

	/**
	 * Gets the size of each population
	 * @return Size of each population
	 */
	public int getPopulationSize() {
		return POPULATIONSIZE;
	}

	/**
	 * Gets the number of generations to calculate
	 * @return Number of generations to calculate
	 */
	public int getGenerations() {
		return GENERATIONS;
	}

	/**
	 * Gets the severity of the mutations that occur
	 * @return Severity of the mutations that occur
	 */
	public float getMutationSeverity() {
		return MUTATIONSEVERITY;
	}

	/**
	 * Gets the probability that a mutation occurs
	 * @return Probability that a mutation occurs
	 */
	public float getMutationProbability() {
		return MUTATIONPROBABILITY;
	}

	/**
	 * Gets the number of elite candidates
	 * @return Number of elite candidates
	 */
	public int getElite() {
		return N_ELITE;
	}

	/**
	 * Gets the number of randomly selected candidates in the next population
	 * @return Number of randomly selected candidates in the next population
	 */
	public int getRandom() {
		return N_RANDOM;
	}

	/**
	 * Gets the number of mutated candidates in the next population
	 * @return Number of mutated candidates in the next population
	 */
	public int getMutate() {
		return N_MUTATE;
	}

	/**
	 * Gets the number of randomly generated candidates in the next population
	 * @return Number of randomly generated candidates in the next population
	 */
	public int getRenew() {
		return N_RENEW;
	}

	/**
	 * Gets the number of new candidates created by recombination in the next population
	 * @return Number of new candidates created by recombination in the next population
	 */
	public int getXOver() {
		return N_XOVER;
	}

	/**
	 * Gets the number of generations till an inter-population crossover happens
	 * @return Number of generations till an inter-population crossover happens
	 */
	public int getInterXOverFreq() {
		return N_INTERXOVER_FREQ;
	}

	/**
	 * Gets the index of the mutation method to be used
	 * @return The index of the mutation method to be used
	 */
	public int getMutationMethod() {
		return MUTATIONMETHOD;
	}

	/**
	 * Gets the index of the recombination method to be used
	 * @return The index of the recombination method to be used
	 */
	public int getXOverMethod() {
		return XOVERMETHOD;
	}

	/**
	 * Gets the value that indicates after how many generations should a saving occur
	 * @return
	 */
	public int getSaveInterval() {
		return SAVEINTERVAL;
	}

	/**
	 * Gets the boolean value which indicates if improvements should be saved
	 * @return
	 */
	public boolean getSaveImprovements() {
		return SAVEIMPROVEMENTS;
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

}
