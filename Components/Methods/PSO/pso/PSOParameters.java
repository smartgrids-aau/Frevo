package pso;

import java.util.Hashtable;

import utils.NESRandom;
import core.XMLFieldEntry;

public class PSOParameters {
	
	/**
	 * Number of representations in the population
	 */
	public int POPULATIONSIZE = 100;
	
	/**
	 * Number of generations
	 */
	public int GENERATIONS = 400;	
	
	/**
	 * Weight of velocity
	 */
	public double W = 0.7;
	
	/**
	 * Cognitive learning rate >= 0
     * tendency to return to personal best position 
	 */
	public double C1 = 2.4;
	
	/**
	 * Social learning rate >= 0
     * tendency to move towards the swarm best position
	 */
	public double C2 = 2.1;
	
	/**
	 * The method which is using the current parameters
	 */
	private PSO method;

	public PSOParameters(PSO method) {	
		
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
		
		// Get properties
		XMLFieldEntry popsize = properties.get("populationsize");
		POPULATIONSIZE = Integer.parseInt(popsize.getValue());
		
		XMLFieldEntry generations = properties.get("generations");
		GENERATIONS = Integer.parseInt(generations.getValue());

		XMLFieldEntry c1 = properties.get("C1");
		C1 = Double.parseDouble(c1.getValue());
		
		XMLFieldEntry c2 = properties.get("C2");
		C2 = Double.parseDouble(c2.getValue());
		
		XMLFieldEntry w = properties.get("W");
		W = Double.parseDouble(w.getValue());			
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
