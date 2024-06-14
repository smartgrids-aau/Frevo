package fehervari.noveltyspecies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.jodk.lang.FastMath;
import utils.NESRandom;
import core.AbstractRepresentation;

class Species {

	// for gaussrand
	static int iset = 0;
	static double gset;
	// ---------

	int id;
	int age; // The age of the Species
	double ave_fitness; // The average fitness of the Species
	double max_fitness; // Max fitness of the Species
	double max_fitness_ever; // The max it ever had
	int expected_offspring;
	boolean novel;
	boolean checked;
	boolean obliterate; // Allows killing off in competitive coevolution
						// stagnation
	int age_of_last_improvement; // If this is too long ago, the Species will
									// goes extinct
	double average_est; // When playing real-time allows estimating average
						// fitness

	/** The organisms in the Species */
	ArrayList<Organism> organisms = new ArrayList<Organism>();

	NESRandom generator;

	public Species(int i, NESRandom random) {
		id = i;
		age = 1;
		ave_fitness = 0.0;
		expected_offspring = 0;
		novel = false;
		age_of_last_improvement = 0;
		max_fitness = 0;
		max_fitness_ever = 0;
		obliterate = false;

		average_est = 0;

		generator = random;
	}

	public Species(int i, boolean novel, NESRandom random) {
		id = i;
		age = 1;
		ave_fitness = 0.0;
		expected_offspring = 0;
		this.novel = novel;
		age_of_last_improvement = 0;
		max_fitness = 0;
		max_fitness_ever = 0;
		obliterate = false;

		average_est = 0;

		generator = random;
	}

	boolean add_organism(Organism o) {
		organisms.add(o);
		return true;
	}

	/**
	 * Returns the first organism in this species or <code>null</code> if there
	 * are no organism in this species.
	 */
	public Organism first() {
		if (organisms.size() != 0)
			return organisms.get(0);
		return null;
	}

	/** Returns true if this species has no organisms. */
	public boolean isEmpty() {
		return organisms.size() == 0;
	}

	void rank() {
		// rank reverse order (best goes first)
		Collections.sort(organisms, Organism.FITNESS_DESCENDING_ORDER);
	}

	public double estimate_average() {

		double total = 0.0; // running total of fitnesses

		// Note: Since evolution is happening in real-time, some organisms may
		// not
		// have been around long enough to count them in the fitness evaluation

		double num_orgs = 0; // counts number of orgs above the time_alive
								// threshold

		// enhanced loops are slower on arraylists! :(
		for (int i=0;i< organisms.size();i++) {
			Organism curorg = organisms.get(i);
			// New variable time_alive
			if (curorg.time_alive >= NoveltySpecies.TIME_ALIVE_MINIMUM) {
				total += curorg.fitness;
				++num_orgs;
			}
		}

		if (num_orgs > 0)
			average_est = total / num_orgs;
		else {
			average_est = 0;
		}

		return average_est;
	}

	/** Removes the given organism from the species. */
	void remove_org(Organism org) {
		if (!organisms.remove(org)) {
			System.err
					.println("ERROR: Trying to remove non-existent organism from species!");
		}
	}

	public Organism reproduce_one(int generation, Population pop,
			ArrayList<Species> sorted_species) {

		// This list contains the eligible organisms (KEN)
		ArrayList<Organism> elig_orgs = new ArrayList<Organism>();

		int poolsize; // The number of Organisms in the old generation

		int orgnum; // Random variable
		Organism mom; // Parent Organisms
		Organism dad;
		Organism baby; // The new Organism

		AbstractRepresentation new_genome; // For holding baby's genes

		Species newspecies; // For babies in new Species
		Organism comporg; // For Species determination through comparison

		Species randspecies; // For mating outside the Species
		double randmult;
		int randspeciesnum;

		boolean found; // When a Species is found

		int giveup; // For giving up finding a mate outside the species

		rank(); // Make sure organisms are ordered by rank

		// ADDED CODE (Ken)
		// Now transfer the list to elig_orgs without including the ones that
		// are too young (Ken)
		for (int ci=0;ci<organisms.size();ci++) {
			Organism curorg = organisms.get(ci);
			if (curorg.time_alive >= NoveltySpecies.TIME_ALIVE_MINIMUM)
				elig_orgs.add(curorg);
		}

		// Now elig_orgs should be an ordered list of mature organisms
		// Special case: if it's empty, then just include all the organisms (age
		// doesn't matter in this case) (Ken)
		if (elig_orgs.size() == 0) {
			elig_orgs.addAll(organisms);			
		}

		// Only choose from among the top ranked orgs
		poolsize = (int) ((elig_orgs.size() - 1) * NoveltySpecies.SURVIVAL_THRESHOLD);

		// First, decide whether to mate or mutate
		// If there is only one organism in the pool, then always mutate
		if ((generator.nextFloat() < NoveltySpecies.MUTATE_ONLY_PROBABILITY)
				|| poolsize == 0) {
			// Choose the random parent
			// RANDOM PARENT CHOOSER
			if (poolsize == 0)
				orgnum = 0;
			else
				orgnum = generator.nextInt(poolsize);
			
			Organism curorg = elig_orgs.get(orgnum);

			mom = curorg;

			new_genome = mom.genotype.clone();
			// Do the mutation
			new_genome.mutate(1.0f, 1.0f, 1);

			baby = new Organism(0.0, new_genome, generation);
		}

		// Otherwise we should mate
		else {

			// Choose the random mom
			mom = elig_orgs.get(generator.nextInt(poolsize));

			// Choose random dad
			if (generator.nextFloat() < NoveltySpecies.INTERSPECIES_MATE_RATE) {
				// Mate within Species
				dad = elig_orgs.get(generator.nextInt(poolsize));
			} else {

				// Mate outside Species
				randspecies = this;

				// Select a random species
				giveup = 0; // Give up if you cant find a different Species
				while ((randspecies == this) && (giveup < 5)) {
					// Choose a random species tending towards better species
					randmult = generator.nextGaussian() /4 ;
					if (randmult > 1.0)
						randmult = 1.0;
					if (randmult< 0.5)
						randmult = 0.5;
					// This tends to select better species
					double t = Math
							.floor((randmult * (sorted_species.size() - 1.0)) + 0.5);
					randspeciesnum = (int) t;

					if (randspeciesnum == -1) {
						System.out.println(sorted_species.size() + " " + t
								+ " " + randspeciesnum);
					}

					randspecies = sorted_species.get(randspeciesnum);

					++giveup;
				}

				// New way: Make dad be a champ from the random species
				dad = randspecies.first();
			}

			// Perform xover
			new_genome = mom.genotype.clone();
			new_genome.xOverWith(dad.genotype, 1);

			// Determine whether to mutate the baby's Genome
			// This is done randomly or if the mom and dad are the same organism
			if ((generator.nextFloat() > NoveltySpecies.MATE_ONLY_PROB)
					|| (mom.genotype.diffTo(dad.genotype) == 0.0)) {

				// Do the mutation depending on probabilities of
				// various mutations
				new_genome.mutate(1.0f, 1.0f, 1);

				// Create the baby
				baby = new Organism(0.0, new_genome, generation);

			} else {
				// Create the baby without mutating first
				baby = new Organism(0.0, new_genome, generation);
			}

		}

		// Add the baby to its proper Species
		// If it doesn't fit a Species, create a new one
		if (pop.species.size() == 0) {
			// Create the first species
			newspecies = new Species(++(pop.last_species_id), true, generator);
			(pop.species).add(newspecies);
			newspecies.add_organism(baby); // Add the baby
			baby.species = newspecies; // Point the baby to its species
		} else {
			found = false;

			// go through the species
			Iterator<Species> curit = pop.species.iterator();
			while (curit.hasNext()) {
				Species curspecies = curit.next();

				if (curspecies.isEmpty())
					continue;

				comporg = curspecies.first();

				if (baby.isCompatibleTo(comporg)) {
					curspecies.add_organism(baby);
					baby.species = curspecies;
					found = true;
					break;
				}
			}

			// If we didn't find a match, create a new species
			if (!found) {
				newspecies = new Species(++(pop.last_species_id), true,
						generator);
				(pop.species).add(newspecies);
				newspecies.add_organism(baby); // Add the baby
				baby.species = newspecies; // Point baby to its species
			}

		} // end else

		// Put the baby also in the master organism list
		pop.organisms.add(baby);

		return baby; // Return a pointer to the baby
	}

	public static double GaussRand(NESRandom generator) {

		double fac, rsq, v1, v2;

		if (iset == 0) {
			do {
				v1 = 2.0 * (generator.nextFloat()) - 1.0;
				v2 = 2.0 * (generator.nextFloat()) - 1.0;
				rsq = v1 * v1 + v2 * v2;
			} while (rsq >= 1.0 || rsq == 0.0);
			fac = FastMath.sqrt(-2.0 * FastMath.log(rsq) / rsq);
			gset = v1 * fac;
			iset = 1;
			return v2 * fac;
		} else {
			iset = 0;
			return gset;
		}
	}

	public int getSize() {
		return organisms.size();
	}

}
