package sg;

import java.util.ArrayList;

import core.AbstractRepresentation;
import core.AbstractSingleProblem;

/**
 * Simple smart grid simulator where households can buy energy from the market
 * Each household needs <neededEnergy> within 24 hours (every hour it's possible
 * to buy sth) Price model follows simple curve
 * 
 * 
 * @author Anita
 * 
 */
public class SmartGrid extends AbstractSingleProblem {

	private final int steps = 24; // hours per day

	private int numHouses; // number of houses participating the market
	private int neededEnergy; // how much each household has to get to cover
								// energy need
	// (target value at the end of the day).

	private int penalty; // penalty for buying too many energy units
	private House[] houses; // the candidates

	private boolean log = false; // get information on what has been bought
	float[] predictedConsumption = { 120, 110, 110, 110, 115, 140, 170, 190,
			190, 190, 200, 202, 202, 205, 220, 261, 290, 310, 320, 330, 320,
			270, 210, 170 }; // fixed consumption for each household per hour

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {

		numHouses = Integer.parseInt(getProperties().get("numberOfHouses")
				.getValue());
		neededEnergy = Integer.parseInt(getProperties().get("neededEnergy")
				.getValue());

		penalty = Integer.parseInt(getProperties().get("penalty").getValue());

		houses = new House[numHouses];

		// all houses have the "same" behavior
		for (int i = 0; i < numHouses; i++) {
			houses[i] = new House(candidate.clone());
		}

		float currentPrice; // current price of energy - follows cos

		ArrayList<Float> inputValues = new ArrayList<Float>(); // input for
																// neural
																// network
		ArrayList<Float> outputValues = new ArrayList<Float>();

		float amountToBuy = 0; // output value - how much a household should
								// currently buy

		float sumCompulsory = 0;
		for (float f : predictedConsumption) {
			sumCompulsory += f;
		}

		int s;

		for (s = 0; s < steps; s++) {

			// calculate current price
			currentPrice = (float) Math.cos(s) + 1f; // make positive
			// calculate current energy provided

			for (int i = 0; i < numHouses; i++) {

				House current = houses[i];

				inputValues.clear();

				inputValues.add(currentPrice);
				inputValues.add(predictedConsumption[s]);
				inputValues.add(current.currentMoneySpent);
				inputValues.add(current.currentEnergy);
				inputValues.add((float) neededEnergy);
				inputValues.add(current.currentCompulsory);
				inputValues.add(sumCompulsory);
				outputValues.clear();
				outputValues = current.r.getOutput(inputValues);

				// output is between 0 and 1 - percentage of needed energy
				amountToBuy = outputValues.get(0)
						* (neededEnergy - sumCompulsory)
						+ predictedConsumption[s];

				if (log) {
					System.out.println("House" + i + " bought " + amountToBuy
							+ " for " + currentPrice);
				}
				current.setCurrentCompulsory(current.currentCompulsory
						+ predictedConsumption[s]);
				current.setCurrentEnergy(current.currentEnergy + amountToBuy);
				current.setCurrentMoneySpent(current.currentMoneySpent
						+ amountToBuy * currentPrice);

			}
		}
		return evaluateOutput();
	}

	/**
	 * Based on the output of the neural network each house bought energy. In
	 * this function the payment and amount of energy units is evaluated and the
	 * fitness calculated
	 * 
	 * @return
	 */
	private double evaluateOutput() {
		double fitness;
		double moneyspent = 0;
		double energybought = 0;
		for (House h : houses) {
			moneyspent += h.currentMoneySpent;
			if (h.currentEnergy < neededEnergy) {
				moneyspent += penalty * (neededEnergy - h.currentEnergy);
			} else if (h.currentEnergy > neededEnergy) {
				moneyspent += penalty * (h.currentEnergy - neededEnergy);
			}
			energybought += h.currentEnergy;
		}

		fitness = energybought / moneyspent / numHouses;

		if (log)
			System.out.println("needed energy " + neededEnergy * numHouses
					+ " bought energy " + energybought + " money spent "
					+ moneyspent + " fitness " + fitness);
		return fitness;
	}

	@Override
	/**
	 * The evaluation is simply the rerun of the neural network with the evolved parameters. Logging is activated such that the process is visible.
	 */
	public void replayWithVisualization(AbstractRepresentation candidate) {
		log = true;
		evaluateCandidate(candidate);

	}
	
	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

}


/**
 * Candidate representation including custom parameters.
 * @author Anita Sobe
 *
 */
class House {

	AbstractRepresentation r;
	float currentEnergy;
	float currentMoneySpent;
	float currentCompulsory;

	public House(AbstractRepresentation candidate) {
		r = candidate;
	}

	public void setCurrentCompulsory(float currentCompulsory) {
		this.currentCompulsory = currentCompulsory;
	}

	public void setCurrentEnergy(float currentEnergy) {
		this.currentEnergy = currentEnergy;
	}

	public void setCurrentMoneySpent(float currentMoneySpent) {
		this.currentMoneySpent = currentMoneySpent;
	}
}