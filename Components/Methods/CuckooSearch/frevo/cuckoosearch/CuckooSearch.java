package frevo.cuckoosearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import main.FrevoMain;

import net.jodk.lang.FastMath;
import org.dom4j.Document;

import utils.NESRandom;
import utils.StatKeeper;
import core.AbstractMethod;
import core.AbstractProblem;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ComponentXMLData;
import core.ProblemXMLData;
import core.XMLFieldEntry;

public class CuckooSearch extends AbstractMethod {

	private static final double SQRT_OF_2_TIMES_PI = FastMath.sqrt(2 * Math.PI);

	/** Discovery rate of alien eggs/solutions */
	double Pa = 0.1;
	double Pa_max = 0.5;
	double Pa_min = 0.05;
	
	float scaling = 1f;
	float scaling_max = 100f;
	float scaling_min = 0.1f;

	double best_fitness;
	AbstractRepresentation bestnest;
	ArrayList<AbstractRepresentation> nests;// = population

	double beta = 3.0 / 2.0;
	
	// TODO: Can extract static parts of Formula, like: FastMath.sin(FastMath.PI * beta / 2.0)
	double sigma = FastMath
			.pow((gamma(1.0 + beta) * FastMath.sin(FastMath.PI * beta / 2.0) / (gamma((1.0 + beta) / 2.0)
					* beta * FastMath.pow(2, (beta - 1.0) / 2.0))), (1.0 / beta));
	
	static final boolean ADAPTIVE_PARAMETERS = true;
	
	

	/**
	 * Constructor for this optimization method
	 * 
	 * @param random
	 *            is used to create consistent results every time the
	 *            optimization is started
	 */
	public CuckooSearch(NESRandom random) {
		super(random);
	}

	/**
	 * Runs the optimization method
	 * 
	 * @param problemData
	 *            defines the problem class and its configuration
	 * @param representationData
	 *            defines the representation class and its configuration
	 * @param rankingData
	 *            defines the ranking class and its configuration
	 * @param properties
	 *            the configuration of the optimizer
	 */
	@Override
	public void runOptimization(ProblemXMLData problemData,
			ComponentXMLData representationData, ComponentXMLData rankingData,
			Hashtable<String, XMLFieldEntry> properties) {

		// Load population size
		XMLFieldEntry popsize = getProperties().get("number_of_nests");
		int population_size = Integer.parseInt(popsize.getValue());

		// Load the number of generations
		XMLFieldEntry gensize = getProperties().get("generations");
		int max_generations = Integer.parseInt(gensize.getValue());

		// obtain problem requirements
		int input_number = problemData.getRequiredNumberOfInputs();
		int output_number = problemData.getRequiredNumberOfOutputs();

		// Load discovery rate
		XMLFieldEntry disc_rate = getProperties().get("discovery_rate");
		Pa = Float.parseFloat(disc_rate.getValue());

		// create statistics
		StatKeeper bestFitnessStats = new StatKeeper(true, "Best Fitness ("
				+ FrevoMain.getCurrentRun() + ")", "Generations");

		// register statistics
		FrevoMain.addStatistics(bestFitnessStats,true);

		// record the best fitness over the evolution
		best_fitness = Double.MIN_VALUE;
		bestnest = null;

		// get possible maximum fitness if available in the problem component
		double maxfitness = Double.MAX_VALUE;
		try {
			AbstractProblem problem = problemData.getNewProblemInstance();
			if (problem instanceof AbstractSingleProblem) {
				AbstractSingleProblem sproblem = (AbstractSingleProblem) problem;
				maxfitness = sproblem.getMaximumFitness();
			}
		} catch (InstantiationException e1) {
			// do nothing
		}
		System.out.println("Maximum fitness is set to " + maxfitness);

		// initialize population
		nests = new ArrayList<AbstractRepresentation>(population_size);

		for (int i = 0; i < population_size; i++) {
			try {
				AbstractRepresentation member = representationData
						.getNewRepresentationInstance(input_number,
								output_number, getRandom());
				nests.add(member);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		getBestNest(nests, nests, problemData);

		for (int generation = 0; generation < max_generations; generation++) {
			
			if (ADAPTIVE_PARAMETERS) {
				double progress = (double)generation / (double)max_generations;
				// adjust Pa
				Pa = Pa_max - (progress) * (Pa_max-Pa_max);
				
				scaling = scaling_max * (float)Math.exp(Math.log(scaling_min/scaling_max) / (float)generation ); 
			}
			

			// check for pause flag
			if (handlePause())
				return;

			// sets our current progress
			setProgress((float) generation / (float) max_generations);

			// Generate new solutions (but keep the current best)
			ArrayList<AbstractRepresentation> new_nest = getCuckoos(nests);

			getBestNest(nests, new_nest, problemData);

			// Discovery and randomization

			new_nest = empty_nests(nests);

			// Evaluate this set of solutions
			nests = getBestNest(nests, new_nest, problemData);

			// add the best fitness of this generation to statistics
			bestFitnessStats.add(best_fitness);
			System.out.println("Generation " + generation + ": best fitness: "
					+ best_fitness);

			// TODO save generation

			// check termination criteria
			if (best_fitness >= maxfitness)
				break;

		}

		// finalize progress
		setProgress(100f);

	}

	private ArrayList<AbstractRepresentation> empty_nests(
			ArrayList<AbstractRepresentation> nests) {
		ArrayList<AbstractRepresentation> newnests = new ArrayList<AbstractRepresentation>(
				nests.size());

		// A fraction of worse nests are discovered with a probability pa

		// In the real world, if a cuckoo's egg is very similar to a host's
		// eggs, then
		// this cuckoo's egg is less likely to be discovered, thus the fitness
		// should
		// be related to the difference in solutions. Therefore, it is a good
		// idea
		// to do a random walk in a biased way with some random step sizes.
		// New solution by biased/selective random walks

		ArrayList<AbstractRepresentation> perm1 = new ArrayList<AbstractRepresentation>(
				nests);
		ArrayList<AbstractRepresentation> perm2 = new ArrayList<AbstractRepresentation>(
				nests);

		Collections.shuffle(perm1);
		Collections.shuffle(perm2);

		for (int i = 0; i < nests.size(); i++) {
			if (generator.nextDouble() > Pa) {
				// replace nest with a new nest
				AbstractRepresentation s = nests.get(i).clone();
				s.mutate("Levy-flight-discovery", perm1.get(i), perm2.get(i));
				s.setEvaluated(false);
				newnests.add(s);
			} else {
				// add old
				newnests.add(nests.get(i));
			}

		}

		return newnests;
	}

	// get cuckoos by random walk
	@SuppressWarnings("deprecation")
	private ArrayList<AbstractRepresentation> getCuckoos(
			ArrayList<AbstractRepresentation> nests) {

		for (int j = 0; j < nests.size(); j++) {

			// System.out.print ("before: "+nests.get(j).getHash());

			nests.get(j).mutate("Levy-flight", new Double(sigma),
					new Double(beta), new Float (scaling), bestnest);
			nests.get(j).setEvaluated(false);
			// System.out.println (" after: "+nests.get(j).getHash());
		}

		return nests;
	}

	private ArrayList<AbstractRepresentation> getBestNest(
			ArrayList<AbstractRepresentation> nests,
			ArrayList<AbstractRepresentation> newnests, ProblemXMLData problem) {

		for (int j = 0; j < newnests.size(); j++) {

			try {
				double fnew = evaluateCandidate(newnests.get(j), problem);

				// replace if better
				if (fnew >= nests.get(j).getFitness()) {
					nests.set(j, newnests.get(j));
				}

				if (fnew > best_fitness) {
					best_fitness = fnew;
					bestnest = newnests.get(j);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}

		return nests;
	}

	private double evaluateCandidate(AbstractRepresentation candidate,
			ProblemXMLData problem) throws InstantiationException {

		if (!candidate.isEvaluated()) {
			AbstractSingleProblem p;
			p = (AbstractSingleProblem) (problem.getNewProblemInstance());
			p.setRandom(getRandom().clone());
			candidate.reset(); // wipe clean state of the candidate
			double fitness = p.evaluateFitness(candidate);
			candidate.setFitness(fitness);

			return fitness;
		} else {
			return candidate.getFitness();
		}

	}

	/**
	 * Load representations from a results XML doc object. The representations
	 * are arranged as a list of populations where each population is
	 * represented as a list of representations (or solutions).
	 * <p>
	 * If the loading takes considerable amount of time it is advised to provide
	 * visual feedback to the user. This can be done by calling the
	 * {@link main.FrevoMain#setLoadingProgress(float)} method.
	 * 
	 * @param doc
	 *            The source {@link org.dom4j.Document} to be used for loading
	 *            the representations.
	 * @return A 2D array of <tt>AbstractRepresentations</tt> loaded from the
	 *         source document.
	 */
	@Override
	public ArrayList<ArrayList<AbstractRepresentation>> loadFromXML(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public static double logGamma(double x) {
		double tmp = (x - 0.5) * FastMath.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
				+ 24.01409822 / (x + 2) - 1.231739516 / (x + 3) + 0.00120858003
				/ (x + 4) - 0.00000536382 / (x + 5);
		return tmp + FastMath.log(ser * SQRT_OF_2_TIMES_PI);
	}

	public static double gamma(double x) {
		return FastMath.exp(logGamma(x));
	}

}
