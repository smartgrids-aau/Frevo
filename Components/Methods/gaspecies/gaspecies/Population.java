package gaspecies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import utils.NESRandom;
import core.AbstractRepresentation;
import core.ComponentXMLData;

class Population {

	// ******* When do we need to delta code? *******
	/** Stagnation detector */
	double highest_fitness;
	/** If too high, leads to delta coding */
	int highest_last_changed;

	/** The highest species ID */
	int last_species_id;

	/** An integer that when above zero tells when the first winner appeared */
	int winnergen;

	ArrayList<Organism> organisms;

	private int input_size;
	private int output_size;

	private NESRandom generator;

	ArrayList<Species> species;

	public Population(ComponentXMLData representationData, int pop_size,
			int input_size, int output_size, NESRandom random) {
		winnergen = 0;
		highest_fitness = 0.0;
		highest_last_changed = 0;
		generator = random;
		species = new ArrayList<Species>();
		this.input_size = input_size;
		this.output_size = output_size;

		spawn(representationData, pop_size);
	}

	private boolean spawn(ComponentXMLData representationData, int size) {

		// reset representation database
		@SuppressWarnings("unchecked")
		Class<? extends AbstractRepresentation> repclass = (Class<? extends AbstractRepresentation>) representationData
				.getComponentClass();
		try {

			Method uninitmethod = repclass.getMethod("setClassInitialized",
					boolean.class);

			uninitmethod.setAccessible(true); // if security settings allow this
			uninitmethod.invoke(null, Boolean.FALSE); // use null if the method is
												// static

			Method reinitmethod = repclass.getDeclaredMethod("initialize",
					int.class, int.class, Hashtable.class, Random.class);

			reinitmethod.setAccessible(true);
			reinitmethod.invoke(null, input_size, output_size,
					representationData.getProperties(), generator);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println("Method doesn't support initialization.");
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		organisms = new ArrayList<Organism>(size);

		for (int i = 0; i < size; i++) {
			try {
				AbstractRepresentation candidate = representationData
						.getNewRepresentationInstance(input_size, output_size,
								generator);
				organisms.add(new Organism(0.0, candidate, 1));

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}

		// Separate the new Population into species
		speciate();

		return true;
	}

	private boolean speciate() {
		// For stepping through Population
		Iterator<Organism> curorg = organisms.iterator();

		int counter = 0; // Species counter

		// For each organism, search for a species it is compatible to
		while (curorg.hasNext()) {
			Organism corg = curorg.next();

			// Create the first species and add this to it
			if (species.size() == 0) {

				Species newspecies = new Species(++counter, generator);
				species.add(newspecies);

				newspecies.add_organism(corg); // Add the current organism
				corg.species = newspecies; // Point organism to its species

			} else {

				boolean added = false;
				for (Species curspecies : species) {
					if (curspecies.isEmpty())
						continue;

					Organism comporg = curspecies.first();

					if (corg.isCompatibleTo(comporg)) {
						// Found compatible species, so add this organism to it
						curspecies.add_organism(corg);
						corg.species = curspecies;

						added = true;
						break;
					}
				}

				if (!added) {
					// create new species
					Species newspecies = new Species(++counter, generator);
					species.add(newspecies);

					// add organism
					newspecies.add_organism(corg);
					corg.species = newspecies;
				}

			}

		}

		last_species_id = counter; // Keep track of highest species

		return true;
	}

	boolean rank_within_species() {
		// Add each Species in this generation to the snapshot
		for (Species s : species)
			s.rank();

		return true;
	}

	public void estimate_all_averages() {
		for (Species s : species)
			s.estimate_average();
	}

	// This method takes an Organism and reassigns what Species it belongs to
	// It is meant to be used so that we can reasses where Organisms should
	// belong as the speciation threshold changes.
	public void reassign_species(Organism org) {
		
		boolean found = false; // Note we don't really need this flag but it
								// might be useful if we change how this
								// function works

		// go through species
		for (Species curspecies : species) {

			if (curspecies == org.species) {
				continue;
			}

			Organism comporg;
			if (!curspecies.isEmpty()) {
				comporg = curspecies.first();
			} else {
				continue;
			}

			// we have a valid comparison
			if (org.isCompatibleTo(comporg)) {

				// Found a more compatible species

				switch_species(org, org.species, curspecies);
				found = true; // Note the search is over
				break;

			}

		}

		// If we didn't find a match, create a new species, move the org to
		// that species, check if the old species is empty,
		// re-estimate averages, and return 0
		if (!found) {

			// Create a new species for the org
			Species newspecies = new Species(++(last_species_id), true,
					generator);
			species.add(newspecies);

			switch_species(org, org.species, newspecies);
		}
	}

	/** Move an Organism from one Species to another */
	private void switch_species(Organism org, Species orig_species,
			Species new_species) {

		// Remove organism from the species we want to remove it from
		orig_species.remove_org(org);

		// Add the organism to the new species, it is being moved to
		new_species.add_organism(org);
		org.species = new_species;

		// KEN: Delete orig_species if empty, and remove it from pop
		if ((orig_species.organisms.size()) == 0) {

			remove_species(orig_species);

			// Re-estimate the average of the species that now has a new member
			new_species.estimate_average();
		}
		// If not, re-estimate the species average after removing the organism
		// AND the new species with the new member
		else {
			orig_species.estimate_average();
			new_species.estimate_average();
		}
	}

	void remove_species(Species s) {
		if (!species.remove(s)) {
			System.err
					.println("ERROR: Trying to remove non-existent species from species list!");
		}
	}

	public Species choose_parent_species() {

		double total_fitness = 0;
		double marble; // The roulett marble
		double spin; // Spins until the marble reaches its chosen point

		// Use the roulette method to choose the species

		// Sum all the average fitness estimates of the different species
		// for the purposes of the roulette
		for (Species curspecies : species) {
			total_fitness += curspecies.average_est;
		}

		marble = generator.nextFloat() * total_fitness;

		Iterator<Species> it = species.iterator();
		Species curspecies = it.next();
		spin = curspecies.average_est;
		while (spin < marble && it.hasNext()) {
			curspecies = it.next();

			// Keep the wheel spinning
			spin += curspecies.average_est;
		}

		// Return the chosen species
		return curspecies;

	}

	Organism remove_worst() {
		
		double adjusted_fitness;
		double min_fitness = Double.MAX_VALUE;
		Iterator<Organism> curorgit = organisms.iterator();
		Organism curorg;
		Organism org_to_kill = null;
		Species orgs_species = null; // The species of the dead organism

		// Make sure the organism is deleted from its species and the population

		// First find the organism with minimum *adjusted* fitness
		while (curorgit.hasNext()) {
			curorg = curorgit.next();
			adjusted_fitness = curorg.fitness / curorg.species.organisms.size();
			if ((adjusted_fitness < min_fitness)
					&& (curorg.time_alive >= GASpecies.TIME_ALIVE_MINIMUM)) {
				min_fitness = adjusted_fitness;
				org_to_kill = curorg;

				orgs_species = curorg.species;
			}

		}

		if (org_to_kill != null) {

			// Remove the organism from its species and the population
			orgs_species.remove_org(org_to_kill); // Remove from species
			organisms.remove(org_to_kill); // Remove from population list

			// Did the species become empty?
			if ((orgs_species.organisms.size()) == 0) {

				remove_species(orgs_species);
			}
			// If not, re-estimate the species average after removing the
			// organism
			else {
				orgs_species.estimate_average();
			}
		}
		
		return org_to_kill;
	}
	
	ArrayList<AbstractRepresentation> getMembers() {
		ArrayList<AbstractRepresentation> members = new ArrayList<AbstractRepresentation>();
		for (Organism organism : organisms) {
			members.add(organism.genotype); 
		}		
		return members;				
	}
}
