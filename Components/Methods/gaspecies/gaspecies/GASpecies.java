package gaspecies;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import core.PopulationDiversity;
import core.ProblemXMLData;
import core.XMLFieldEntry;
import core.XMLMethodStep;

/**
 * Evolutionary method based on speciation as described in <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">NEAT </a>.
 */
public class GASpecies extends AbstractMethod {
	
	static final double COMPATIBILITY_THREHOLD_MIN = 0.01;

	/** Entries contain the best fitness over all populations */
	private StatKeeper bestFitnessStats;
	
	// Statistics about population diversity
	private StatKeeper diversity;
	private StatKeeper maxDiversity;
	private StatKeeper minDiversity;
	private StatKeeper standardDeviation;

	public static double COMPATIBILITY_THRESHOLD;

	static int TIME_ALIVE_MINIMUM;

	static float SURVIVAL_THRESHOLD;

	private Population pop;

	private int POPULATIONSIZE;

	double highest_fitness = 0.0;

	private int GENERATIONS;

	//private int SAVEINTERVAL;

	static float MUTATE_ONLY_PROBABILITY;

	static float INTERSPECIES_MATE_RATE;

	static float MATE_ONLY_PROB;

	public GASpecies(NESRandom random) {
		super(random);
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

		/*entry = getProperties().get("save_interval");
		SAVEINTERVAL = Integer.parseInt(entry.getValue());*/

		entry = getProperties().get("survival_threshold");
		SURVIVAL_THRESHOLD = Float.parseFloat(entry.getValue());

		entry = getProperties().get("mutate_only_probablity");
		MUTATE_ONLY_PROBABILITY = Float.parseFloat(entry.getValue());

		entry = getProperties().get("interspecies_mate_rate");
		INTERSPECIES_MATE_RATE = Float.parseFloat(entry.getValue());

		entry = getProperties().get("mate_only_prob");
		MATE_ONLY_PROB = Float.parseFloat(entry.getValue());

	}

	/**
	 * Creates instances for statistics purposes
	 */
	private void createStatistics() {
		// create statistics
		bestFitnessStats = new StatKeeper(true, "Best Fitness ("
				+ FrevoMain.getCurrentRun() + ")", "Generations");

		diversity = new StatKeeper(true, "Diversity", "Generations");
		maxDiversity = new StatKeeper(true, "Max. diversity", "Generations");
		minDiversity = new StatKeeper(true, "Min. diversity", "Generations");
		standardDeviation = new StatKeeper(true, "Deviation", "Generations");

		// register statistics
		FrevoMain.addStatistics(bestFitnessStats, true);
		FrevoMain.addStatistics(diversity, false);
		FrevoMain.addStatistics(maxDiversity, false);
		FrevoMain.addStatistics(minDiversity, false);
		FrevoMain.addStatistics(standardDeviation, false);

	}

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

		createStatistics();
		
		double bestFitnessSoFar = Double.MIN_VALUE;

		// create initial population
		// create new population
		pop = new Population(representationData, POPULATIONSIZE,
				problemData.getRequiredNumberOfInputs(),
				problemData.getRequiredNumberOfOutputs(), getRandom());

		// We try to keep the number of species constant at this number
		int num_species_target = POPULATIONSIZE / 10;

		// This is where we determine the frequency of compatibility threshold
		// adjustment
		int compat_adjust_frequency = POPULATIONSIZE / 20;
		if (compat_adjust_frequency < 1)
			compat_adjust_frequency = 1;

		// Initially, we evaluate the whole population
		// Evaluate each organism on a test
		for (int ci = 0; ci < pop.organisms.size(); ci++) {
			Organism curorg = pop.organisms.get(ci);

			// evaluate each individual
			curorg.fitness = getFitness(curorg, problemData);

			if (curorg.fitness > bestFitnessSoFar)
				bestFitnessSoFar = curorg.fitness;
		}

		// Get ready for real-time loop
		// Rank all the organisms from best to worst in each species
		pop.rank_within_species();

		// Assign each species an average fitness
		// This average must be kept up-to-date by rtNEAT in order to select
		// species probabilistically for reproduction
		pop.estimate_all_averages();

		System.out.println("Entering evolutionary loop...");

		int generation = 0;

		// Now create offspring one at a time, testing each offspring,
		// and replacing the worst with the new offspring if its better
		for (int offspring_count = 0; offspring_count < POPULATIONSIZE
				* GENERATIONS; offspring_count++) {
			
			if (handlePause())
				break;

			// end of a generation
			if (offspring_count % (POPULATIONSIZE) == 0) {
				// we increase alive time of each individual in each generation
				for (Organism individual: pop.organisms) {
					individual.time_alive++;
				}
				
				System.out.print("Proceeding to generation " + generation++
						+ " species: " + pop.species.size() + " [");
				for (Species s : pop.species) {
					System.out.print(s.getSize() + ", ");
				}
				System.out.println("] CT= "+COMPATIBILITY_THRESHOLD);

				bestFitnessStats.add(bestFitnessSoFar);
				PopulationDiversity diversityCalc = new PopulationDiversity(pop.getMembers()); 
				diversity.add(diversityCalc.getAverageDiversity());
				maxDiversity.add(diversityCalc.getMaxDiversity());
				minDiversity.add(diversityCalc.getMinDiversity());
				standardDeviation.add(diversityCalc.getStandardDeviation());
				
			}

			// progress
			setProgress((float) offspring_count
					/ (float) (GENERATIONS * POPULATIONSIZE));

			// Every pop_size reproductions, adjust the compat_thresh to better
			// match the num_species_target and reassign the population to new
			// species
			if (offspring_count % compat_adjust_frequency == 0) {

				int num_species = pop.species.size();
				//double compat_mod = 0.1; // Modify compat thresh to control
											// speciation

				// This tinkers with the compatibility threshold
				if (num_species < num_species_target
						&& COMPATIBILITY_THRESHOLD > COMPATIBILITY_THREHOLD_MIN) {
					//COMPATIBILITY_THRESHOLD -= compat_mod;
					
					COMPATIBILITY_THRESHOLD *= 0.9;
					
				} else if (num_species > num_species_target) {
					//COMPATIBILITY_THRESHOLD += compat_mod;
					COMPATIBILITY_THRESHOLD *= 1.1;
				}

				// Go through entire population, reassigning organisms to new
				// species
				for (int ci = 0; ci < pop.organisms.size(); ci++)
					pop.reassign_species(pop.organisms.get(ci));

			}

			// Here we call two rtNEAT calls:
			// 1) choose_parent_species() decides which species should produce
			// the next offspring
			// 2) reproduce_one(...) creates a single offspring from the chosen
			// species
			Species parentSpecies = pop.choose_parent_species(); 
			Organism new_org = parentSpecies.reproduce_one(
					offspring_count, pop, pop.species);

			new_org.fitness = getFitness(new_org, problemData);
			
			// save new achievement or last generation
			if (new_org.fitness > bestFitnessSoFar || offspring_count + 1 == POPULATIONSIZE * GENERATIONS) {
				
				if (new_org.fitness > bestFitnessSoFar)
					bestFitnessSoFar = new_org.fitness;

				// save generation
				String fitnessstring;
				if (problemData.getComponentType() == ComponentType.FREVO_PROBLEM) {
					fitnessstring = "_(" + bestFitnessSoFar + ")";
				} else {
					// multiproblem
					fitnessstring = "";
				}
				
				DecimalFormat fm = new DecimalFormat("000");
				String fileName = problemData.getName() + "_g"
						+ generation + "_i"
						+ fm.format(offspring_count) + fitnessstring;
				Element xmlLastState = saveResults(offspring_count, generation);
				// save the last state of evaluation
				XMLMethodStep state = new XMLMethodStep(fileName, xmlLastState, this.seed, getRandom().getSeed());
				setLastResults(state);
				FrevoMain.saveResult(
						fileName,
						xmlLastState, this.seed,
						getRandom().getSeed());

			}

			// Now we reestimate the baby's species' fitness
			new_org.species.estimate_average();

			// Remove the worst organism
			pop.remove_worst();

			// check if problem is solved
			if (bestFitnessSoFar >= maxfitness) {
				System.out.println("Problem has been solved!");
				break;
			}

		}

		// finalize progress
		setProgress(100f);
	}

	/** Evaluates the provided organism and returns its fitness value. */
	private double getFitness(Organism org, ProblemXMLData problemData) {

		// calculate fitness
		double fitness = Double.MIN_VALUE;
		try {
			AbstractSingleProblem problem = ((AbstractSingleProblem) (problemData
					.getNewProblemInstance()));
			problem.setRandom(generator.clone());
			fitness = problem.evaluateFitness(org.genotype);
			
			if (fitness > highest_fitness)
				highest_fitness = fitness;

		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return fitness;
	}

	private Element saveResults(int iteration, int generation) {
		Element dpop = DocumentFactory.getInstance()
				.createElement("population");

		dpop.addAttribute("iteration", String.valueOf(iteration));
		dpop.addAttribute("generation", String.valueOf(generation));
		dpop.addAttribute("randomseed", String.valueOf(this.getSeed()));

		// add all members of the population
		for (int r = 0; r < pop.organisms.size(); r++)
			pop.organisms.get(r).genotype.exportToXmlElement(dpop);

		return dpop;
	}

	@Override
	public ArrayList<ArrayList<AbstractRepresentation>> loadFromXML(Document doc) {
		// final list to be returned
		ArrayList<ArrayList<AbstractRepresentation>> populations = new ArrayList<ArrayList<AbstractRepresentation>>();

		// get population root node
		Node dpopulations = doc.selectSingleNode("/frevo/population");

		generator
				.setSeed(Long.parseLong(dpopulations.valueOf("./@randomseed")));

		ArrayList<AbstractRepresentation> result = new ArrayList<AbstractRepresentation>();
		List<?> representations = dpopulations.selectNodes("./*");
		Iterator<?> representationsIterator = representations.iterator();

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
						.getNewRepresentationInstance(0, 0, null);

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
