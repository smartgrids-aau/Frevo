package core;

import java.util.List;

public interface MultiObjectiveFitness {
	
	/** Evaluates the given representation by calculating its corresponding mulit-objective fitness value. A higher fitness means better performance.<br>
	 * @param candidate The candidate solution to be evaluated.
	 * @return the corresponding fitness value. */
	public List<Double> evaluateFitness(AbstractRepresentation candidate);
}
