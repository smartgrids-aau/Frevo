package fehervari.noveltysearch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import utils.NESRandom;
import utils.StatKeeper;
import core.AbstractMethod;
import core.AbstractProblem;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ComponentType;
import core.ComponentXMLData;
import core.ProblemXMLData;
import core.XMLFieldEntry;

public class NoveltySearch extends AbstractMethod {

	private int POPULATIONSIZE;

	private ArrayList<NoveltyItem> population;

	/** Entries contain the best fitness over all populations */
	private StatKeeper bestFitnessStats;

	/** archive that contains explored solutions that had high novelty */
	private NoveltyArchive archive;

	private float INITIAL_THRESHOLD;

	/** Number of generations to calculate */
	private int GENERATIONS;

	private float mutate_only_probablity;

	private float MUTATIONSEVERITY;

	private int MUTATIONMETHOD;

	/**
	 * Constructor for this optimization method
	 * 
	 * @param random
	 *            is used to create consistent results every time the
	 *            optimization is started
	 */
	public NoveltySearch(NESRandom random) {
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

		// load parameters
		loadparameters();

		// get possible maximum fitness if possible
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

		// register statistics
		// create statistics
		bestFitnessStats = new StatKeeper(true, "Best Fitness ("
				+ FrevoMain.getCurrentRun() + ")", "Generations");

		FrevoMain.addStatistics(bestFitnessStats, true);

		double bestFitnessSoFar = Double.MIN_VALUE;

		// obtain problem requirements
		int input_number = problemData.getRequiredNumberOfInputs();
		int output_number = problemData.getRequiredNumberOfOutputs();

		// create initial population and get their novelty vector

		population = new ArrayList<NoveltyItem>(POPULATIONSIZE);
		for (int i = 0; i < POPULATIONSIZE; i++) {
			try {
				AbstractRepresentation member = representationData
						.getNewRepresentationInstance(input_number,
								output_number, generator);

				population.add(new NoveltyItem(member,
						((AbstractSingleProblem) (problemData
								.getNewProblemInstance())).getNoveltyVector(
								member, true), i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Create archive
		archive = new NoveltyArchive(INITIAL_THRESHOLD);

		// assign fitness scores based on novelty
		archive.evaluate_population(population, true);
		// add to archive
		// archive.evaluate_population(population, false);

		// rank them according to their novelty
		Collections.sort(population, Collections.reverseOrder());

		int archive_size = 0;

		int generation = 0;
		// Create offspring one at a time, testing each offspring,
		// and replacing the worst with the new offspring if its better
		for (int offspring_count = 0; offspring_count < GENERATIONS
				* POPULATIONSIZE; offspring_count++) {

			// end of a generation
			if (offspring_count % (POPULATIONSIZE) == 0) {
				archive.end_of_gen_steady(population);
				System.out.println("Proceeding to generation " + generation++);

				// archive.add_randomly(pop);
				archive.evaluate_population(population, false);
				int as = archive.get_set_size();
				if (as > archive_size) {
					archive_size = as;
					System.out.println("ARCHIVE SIZE: " + archive_size
							+ " THRESHOLD: " + archive.novelty_threshold);
				}
				bestFitnessStats.add(bestFitnessSoFar);
			}

			// progress
			setProgress((float) offspring_count
					/ (float) (GENERATIONS * POPULATIONSIZE));

			// ------ reproduction -------
			// select random parents based on roulette selection

			NoveltyItem offspring;
			// First, decide whether to mate or mutate
			if (generator.nextFloat() < mutate_only_probablity) {
				NoveltyItem[] parents = selectParents(1,false);
				// create exactly one offspring
				offspring = mutate(parents[0], offspring_count);
			} else {
				// select 2 parents
				NoveltyItem[] parents = selectParents(2,false);
				// create exactly one offspring
				offspring = xover(parents, offspring_count);
			}

			// re-evaluate novelty points
			try {
				offspring.noveltypoints = ((AbstractSingleProblem) (problemData
						.getNewProblemInstance())).getNoveltyVector(
						offspring.genotype, true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// calculate novelty of new individual
			archive.evaluate_individual(offspring, population, true);

			// update fittest list
			archive.update_fittest(offspring);

			// report fittest candidate to statistics and to frevo
			double bestFitness = archive.fittest.get(0).genotype.getFitness();

			if (bestFitness > bestFitnessSoFar) {
				bestFitnessSoFar = bestFitness;

				// save generation
				String fitnessstring;
				if (problemData.getComponentType() == ComponentType.FREVO_PROBLEM) {
					fitnessstring = " (" + bestFitness + ")";
				} else {
					// multiproblem
					fitnessstring = "";
				}
				DecimalFormat fm = new DecimalFormat("000");
				FrevoMain.saveResult(
						problemData.getName() + "_g" + archive.generation
								+ "_i" + fm.format(offspring_count)
								+ fitnessstring,
						saveResults(offspring_count, archive.generation),
						this.seed, getRandom().getSeed());
			}

			// check if problem has been solved
			if (bestFitness >= maxfitness) {
				System.out.println("Solution has been found in generation "
						+ archive.generation);
				break;
			}
		}

		setProgress(1f);

	}

	private Element saveResults(int iteration, int generation) {
		Element dpop = DocumentFactory.getInstance()
				.createElement("population");

		dpop.addAttribute("iteration", String.valueOf(iteration));
		dpop.addAttribute("generation", String.valueOf(generation));
		dpop.addAttribute("randomseed", String.valueOf(this.getSeed()));

		// add all members of the population
		for (int r = 0; r < population.size(); r++)
			population.get(r).genotype.exportToXmlElement(dpop);

		return dpop;
	}

	/** Creates an offspring of the given parents */
	private NoveltyItem xover(NoveltyItem[] parents, int id) {
		NoveltyItem child = new NoveltyItem(parents[0].genotype.clone(),
				null, id);
		child.genotype.xOverWith(parents[1].genotype, 1);
		return child;
	}

	/** creates a mutated copy of the given candidate */
	private NoveltyItem mutate(NoveltyItem parent, int id) {
		NoveltyItem mutated = new NoveltyItem(parent.genotype.clone(), null, id);
		mutated.genotype.mutate(MUTATIONSEVERITY, 0.1f, MUTATIONMETHOD);
		return mutated;
	}

	/**
	 * Returns candidates from the population based on fitness-proportional
	 * selection.
	 */
	private NoveltyItem[] selectParents(int num_selected, boolean isRoulette) {

		HashSet<NoveltyItem> parents = new HashSet<NoveltyItem>();

		if (isRoulette) {

			// construct roulette wheel
			float totalfitness = 0.0f;

			for (int i = 0; i < population.size(); i++) {
				totalfitness += population.get(i).novelty;
			}

			float[] roulette = new float[population.size()];

			roulette[0] = 1.0f / totalfitness * population.get(0).novelty;
			for (int i = 1; i < population.size(); i++) {
				roulette[i] = roulette[i - 1]
						+ (1.0f / totalfitness * population.get(i).novelty);
			}

			// select parents
			while (parents.size() != num_selected) {
				float chance = generator.nextFloat();
				int selected = 0;
				while (roulette[selected] <= chance)
					selected++;

				NoveltyItem parent = population.get(selected);
				if (!parents.contains(parent)) {
					parents.add(parent);
				}
			}
		} else {
			// tournament selection
			int size = population.size();
			while (parents.size() != num_selected) {
				// select A and B
				NoveltyItem parentA = population.get(generator.nextInt(size));
				NoveltyItem parentB = population.get(generator.nextInt(size));
				
				while (parentB == parentA) {
					parentB = population.get(generator.nextInt(size));
				}

				parents.add(parentA.novelty > parentB.novelty ? parentA
						: parentB);
			}
		}

		return parents.toArray(new NoveltyItem[num_selected]);
	}

	private void loadparameters() {

		// Load population size
		XMLFieldEntry entry = getProperties().get("populationsize");
		POPULATIONSIZE = Integer.parseInt(entry.getValue());

		// Load the number of generations
		entry = getProperties().get("generations");
		GENERATIONS = Integer.parseInt(entry.getValue());

		entry = getProperties().get("initial_novelty_threshold");
		INITIAL_THRESHOLD = Float.parseFloat(entry.getValue());

		entry = getProperties().get("mutate_only_probablity");
		mutate_only_probablity = Float.parseFloat(entry.getValue());

		entry = getProperties().get("mutation_severity");
		MUTATIONSEVERITY = Float.parseFloat(entry.getValue());

		// MUTATIONMETHOD 1
		entry = getProperties().get("mutation_method");
		MUTATIONMETHOD = Integer.parseInt(entry.getValue());
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

}
