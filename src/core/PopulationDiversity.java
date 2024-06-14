package core;

import java.util.ArrayList;

import net.jodk.lang.FastMath;

public class PopulationDiversity {
	 
	private double averageDiversity = 0;
	private double maxDiversity = 0;
	private double minDiversity = Double.MAX_VALUE;
	private ArrayList<AbstractRepresentation> population;
	
	public PopulationDiversity (ArrayList<AbstractRepresentation> population) {
		if (population.size() < 2) {
			throw new IllegalArgumentException();
		}
		
		this.population = population;		
		int size = population.size();
		
		for (int i = 0; i< size - 1; i++) {
			for (int j = i + 1; j < size; j++){
				AbstractRepresentation first = population.get(i);
				AbstractRepresentation second = population.get(j);
				// calculate diversity between two instances 
				double diversity = first.diffTo(second);
				// calculate average diversity
				averageDiversity  += diversity;
				// search for max diversity
				if (maxDiversity < diversity) {
					maxDiversity = diversity;
				}
				// search for min diversity 
				if (minDiversity > diversity) {
					minDiversity = diversity;
				}
			}
		}
		
		averageDiversity = 2* averageDiversity / (size * (size - 1));
	}
	
	public double getAverageDiversity() {
		return averageDiversity;
	}

	public double getMaxDiversity() {
		return maxDiversity;
	}

	public double getMinDiversity() {
		return minDiversity;
	}	
	
	public double getStandardDeviation() {	
		double standardDeviation = 0;
		int size = population.size();
		
		for (int i = 0; i< size - 1; i++) {
			for (int j = i + 1; j < size; j++){
				AbstractRepresentation first = population.get(i);
				AbstractRepresentation second = population.get(j);
				// calculate diversity between two instances 
				double diversity = first.diffTo(second);
				
				standardDeviation += FastMath.pow2(diversity - averageDiversity);								
			}
		}
		
		standardDeviation = FastMath.sqrt(2 * standardDeviation / (size * (size - 1))); 
		return standardDeviation;
	}
}
