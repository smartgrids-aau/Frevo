package spiderino;

import core.AbstractSingleProblem;

import java.util.SplittableRandom;

import core.AbstractRepresentation;

public class Spiderino extends AbstractSingleProblem {

	@Override
	/** Evaluates the given representation by calculating its corresponding fitness value. A higher fitness means better performance.<br>
	 * @param candidate The candidate solution to be evaluated.
	 * @return the corresponding fitness value. */
	public double evaluateCandidate(AbstractRepresentation candidate) {
		PaperSimulation simulation = new PaperSimulation(candidate, new SplittableRandom(0x1234));
		return simulation.run();
	}
	
	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		PaperSimulationView view = new PaperSimulationView(candidate, 0x1234);
		view.start();
	}
	
	/** Returns the achievable maximum fitness of this problem. A representations with this fitness value cannot be improved any further. */
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

}
