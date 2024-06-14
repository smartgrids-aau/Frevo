package fehervari.noveltyspecies;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import java.lang.UnsupportedOperationException;
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

public class NoveltySpecies extends AbstractMethod {

	/** Entries contain the best fitness over all populations */
	private StatKeeper bestFitnessStats;

	static double COMPATIBILITY_THRESHOLD;

	static int TIME_ALIVE_MINIMUM;

	static float SURVIVAL_THRESHOLD;

	private Population pop;

	private int POPULATIONSIZE;

	double highest_fitness = 0.0;

	private int GENERATIONS;

	private int SAVEINTERVAL;

	static float MUTATE_ONLY_PROBABILITY;

	static float INTERSPECIES_MATE_RATE;

	static float MATE_ONLY_PROB;

	/**
	 * Constructor for this optimization method
	 * 
	 * @param random
	 *            is used to create consistent results every time the
	 *            optimization is started
	 */
	public NoveltySpecies(NESRandom random) {
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
		loadParameters();

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

		FrevoMain.addStatistics(bestFitnessStats,true);

		double bestFitnessSoFar = Double.MIN_VALUE;

		// create new population
		pop = new Population(representationData, POPULATIONSIZE,
				problemData.getRequiredNumberOfInputs(),
				problemData.getRequiredNumberOfOutputs(), getRandom());

		// start evolution
		float archive_thresh = 6.0f; // initial novelty threshold

		// archive of novel behaviors
		NoveltyArchive archive = new NoveltyArchive(archive_thresh);

		// We try to keep the number of species constant at this number
		int num_species_target = POPULATIONSIZE / 15;

		// This is where we determine the frequency of compatibility threshold
		// adjustment
		int compat_adjust_frequency = POPULATIONSIZE / 20;
		if (compat_adjust_frequency < 1)
			compat_adjust_frequency = 1;

		// Initially, we evaluate the whole population
		// Evaluate each organism on a test
		int indiv_counter = 0;
		for (int ci=0;ci<pop.organisms.size();ci++) {
			Organism curorg = pop.organisms.get(ci);
			
			// evaluate each individual
			curorg.noveltypoint = getNovelty(curorg, problemData);
			curorg.noveltypoint.indiv_number = indiv_counter;
			indiv_counter++;
		}

		// assign fitness scores based on novelty
		archive.evaluate_population(pop, true);
		// add to archive
		archive.evaluate_population(pop, false);

		// Get ready for real-time loop
		// Rank all the organisms from best to worst in each species
		pop.rank_within_species();

		// Assign each species an average fitness
		// This average must be kept up-to-date by rtNEAT in order to select
		// species probabilistically for reproduction
		pop.estimate_all_averages();

		System.out.println("Entering evolutionary loop...");

		// archive size track for statistics only
		int archive_size = 0;

		int generation = 0;
		// Now create offspring one at a time, testing each offspring,
		// and replacing the worst with the new offspring if its better
		for (int offspring_count = 0; offspring_count < POPULATIONSIZE
				* GENERATIONS; offspring_count++) {
			
			if (handlePause())
				break;

			// end of a generation
			if (offspring_count % (POPULATIONSIZE) == 0) {
				System.out.print("Proceeding to generation " + generation++ +" species: "+pop.species.size()+" [");
				
				for (Species s:pop.species) {
					System.out.print(s.getSize()+", ");
				}
				System.out.println("]");

				archive.end_of_gen_steady(pop);

				archive.evaluate_population(pop, false);

				int as = archive.get_set_size();
				if (as > archive_size) {
					archive_size = as;
					System.out.println("ARCHIVE SIZE: " + archive_size);
				}
				bestFitnessStats.add(bestFitnessSoFar);
			}

			// progress
			setProgress((float) offspring_count
					/ (float) (GENERATIONS * POPULATIONSIZE));

			// Every pop_size reproductions, adjust the compat_thresh to better
			// match the num_species_targer and reassign the population to new
			// species

			if (offspring_count % compat_adjust_frequency == 0) {

				// update fittest individual list

				archive.update_fittest(pop);

				// refresh generation's novelty scores
				archive.evaluate_population(pop, true);

				int num_species = pop.species.size();
				double compat_mod = 0.1; // Modify compat thresh to control
											// speciation

				// This tinkers with the compatibility threshold
				if (num_species < num_species_target
						&& COMPATIBILITY_THRESHOLD > 0.3) {
					COMPATIBILITY_THRESHOLD -= compat_mod;
					/*System.out.println("New compatibility threshold (-): "
							+ COMPATIBILITY_THRESHOLD + " S:"+pop.species.size()+ " P:"+pop.organisms.size());*/
				} else if (num_species > num_species_target) {
					COMPATIBILITY_THRESHOLD += compat_mod;
					/*System.out.println("New compatibility threshold (+): "
							+ COMPATIBILITY_THRESHOLD+ " S:"+pop.species.size()+ " P:"+pop.organisms.size());*/
				}

				// Go through entire population, reassigning organisms to new
				// species
				for (int ci=0;ci<pop.organisms.size();ci++)
					pop.reassign_species(pop.organisms.get(ci));
			}

			// Here we call two rtNEAT calls:
			// 1) choose_parent_species() decides which species should produce
			// the next offspring
			// 2) reproduce_one(...) creates a single offspring from the chosen
			// species
			Organism new_org = (pop.choose_parent_species()).reproduce_one(
					offspring_count, pop, pop.species);

			// Now we evaluate the new individual
			new_org.noveltypoint = getNovelty(new_org, problemData);
			//System.out.println ("Time: "+(System.currentTimeMillis()-start));
			new_org.noveltypoint.indiv_number = indiv_counter;

			archive.evaluate_individual(new_org, pop, true);

			indiv_counter++;

			// update fittest list
			archive.update_fittest(new_org);

			// Now we reestimate the baby's species' fitness
			new_org.species.estimate_average();

			// check if problem is solved
			double bestFitness = archive.fittest.get(0).genotype.getFitness();

			boolean saveResults = false;
			
			if (bestFitness > bestFitnessSoFar) {
				bestFitnessSoFar = bestFitness;
				saveResults = true;
			}
			
			if (SAVEINTERVAL > 0 && generation % SAVEINTERVAL == 0) {
				saveResults = true;
			}
			
			if (saveResults) {
				// save generation
				String fitnessstring;
				if (problemData.getComponentType() == ComponentType.FREVO_PROBLEM) {
					fitnessstring = "_(" + bestFitness + ")";
				} else {
					// multiproblem
					fitnessstring = "";
				}
				DecimalFormat fm = new DecimalFormat("000");
				FrevoMain.saveResult(
						problemData.getName() + "_g"
								+ archive.generation +"_i"
								+ fm.format(offspring_count) + fitnessstring,
						saveResults(offspring_count, archive.generation),
						this.seed, getRandom().getSeed());
			}	

			// check termination
			if (bestFitness >= maxfitness) {
				System.out.println("Problem solved!");
				break;
			}

			// Remove the worst organism
			pop.remove_worst();

		} // end epoch loop

		setProgress(1.0f);

	}

	private NoveltyItem getNovelty(Organism org, ProblemXMLData problemData) {

		NoveltyItem new_item = new NoveltyItem();
		new_item.genotype = org.genotype;

		// calculate fitness and novelty vector
		ArrayList<Float> nvector;
		try {
			nvector = ((AbstractSingleProblem) (problemData
					.getNewProblemInstance())).getNoveltyVector(org.genotype,
					true);

			double fitness = org.genotype.getFitness();

			if (fitness > highest_fitness)
				highest_fitness = fitness;

			// push back novelty characterization
			new_item.data = nvector;

			// set fitness (this is 'real' objective-based fitness, not novelty)
			new_item.fitness = fitness;
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return new_item;
	}

	private void loadParameters() {
		// Load population size
		XMLFieldEntry entry = getProperties().get("populationsize");
		POPULATIONSIZE = Integer.parseInt(entry.getValue());

		// initial compatibility threshold
		entry = getProperties().get("initial_compatibility_threshold");
		COMPATIBILITY_THRESHOLD = Float.parseFloat(entry.getValue());

		entry = getProperties().get("time_alive_minimum");
		TIME_ALIVE_MINIMUM = Integer.parseInt(entry.getValue());

		entry = getProperties().get("generations");
		GENERATIONS = Integer.parseInt(entry.getValue());

		entry = getProperties().get("save_interval");
		SAVEINTERVAL = Integer.parseInt(entry.getValue());

		entry = getProperties().get("survival_threshold");
		SURVIVAL_THRESHOLD = Float.parseFloat(entry.getValue());

		entry = getProperties().get("mutate_only_probablity");
		MUTATE_ONLY_PROBABILITY = Float.parseFloat(entry.getValue());

		entry = getProperties().get("interspecies_mate_rate");
		INTERSPECIES_MATE_RATE = Float.parseFloat(entry.getValue());

		entry = getProperties().get("mate_only_prob");
		MATE_ONLY_PROB = Float.parseFloat(entry.getValue());

	}

	private Element saveResults(int iteration, int generation) {
		Element dpop = DocumentFactory.getInstance()
				.createElement("population");
	
		dpop.addAttribute("iteration", String.valueOf(iteration));
		dpop.addAttribute("generation", String.valueOf(generation));
		dpop.addAttribute("randomseed", String.valueOf(this.getSeed()));
	
		Collections.sort(pop.organisms,Organism.FITNESS_DESCENDING_ORDER);
		
		// add all members of the population
		for (int r = 0; r < pop.organisms.size(); r++)
			pop.organisms.get(r).genotype.exportToXmlElement(dpop);
	
		return dpop;
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
		// final list to be returned
		ArrayList<ArrayList<AbstractRepresentation>> populations = new ArrayList<ArrayList<AbstractRepresentation>>();
		
		// get population root node
		Node dpopulations = doc.selectSingleNode("/frevo/population");
		
		generator.setSeed(Long.parseLong(dpopulations.valueOf("./@randomseed")));
				
		ArrayList<AbstractRepresentation> result = new ArrayList<AbstractRepresentation>();
		List<?> representations = dpopulations.selectNodes("./*");
		Iterator<?> representationsIterator = representations
				.iterator();
		
		int currentItem = 0;
		int totalRepresentations = representations.size();
		
		while (representationsIterator.hasNext()) {
			// report loading state
			FrevoMain.setLoadingProgress((float) currentItem
					/ totalRepresentations);
			
			try {
				// step to next node
				Node net = (Node) representationsIterator.next();
				
				// construct representation based on loaded representation
				// data
				ComponentXMLData representation = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_REPRESENTATION);
				AbstractRepresentation member = representation
						.getNewRepresentationInstance(0, 0, generator.clone());

				// load representation data from the XML into the instance
				member.loadFromXML(net);

				// add data to current population list
				result.add(member);
				
				currentItem++;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			
		}
		
		populations.add(result);
		
		return populations;
	}

}
