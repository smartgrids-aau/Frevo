package fehervari.noveltyspecies;

import java.util.Comparator;

import core.AbstractRepresentation;

class Organism {

	public static final Comparator<Organism> FITNESS_DESCENDING_ORDER = new FitnessDescendingComparator();

	AbstractRepresentation genotype;
	NoveltyItem noveltypoint; //The Organism's Novelty Point
	Species species = null;
	double fitness;  //A measure of fitness for the Organism
	double orig_fitness;  //A fitness measure that won't change during adjustments
	double error;  //Used just for reporting purposes
	boolean pop_champ;  //Marks the best in population
	boolean pop_champ_child; //Marks the duplicate child of a champion (for tracking purposes)
	double expected_offspring; //Number of children this Organism may have
	boolean eliminate;  //Marker for destruction of inferior Organisms
	boolean champion; //Marks the species champ
	int super_champ_offspring;  //Number of reserved offspring for a population leader
	boolean winner;  //Win marker (if needed for a particular task)
	double high_fit; //DEBUG variable- high fitness of champ
	
	// Track its origin- for debugging or analysis- we can tell how the organism was born
	boolean mut_struct_baby;
	boolean mate_baby;
	boolean modified;
	
	int time_alive; //When playing in real-time allows knowing the maturity of an individual
	
	int generation;  //Tells which generation this Organism is from

	
	
	public Organism (double fit, AbstractRepresentation genome, int gen) {
		genotype = genome;
		expected_offspring=0;
		generation=gen;
		fitness=fit;
		time_alive=0;
		
		eliminate=false;
		error=0;
		winner=false;
		champion=false;
		super_champ_offspring=0;
		
		//debug
		pop_champ=false;
		pop_champ_child=false;
		high_fit=0;
		mut_struct_baby=false;
		mate_baby=false;

		modified = true;
	}
	
	public boolean isCompatibleTo(Organism other) {
		if (this.genotype.diffTo(other.genotype) < NoveltySpecies.COMPATIBILITY_THRESHOLD)
			return true;
		
		return false;
	}
	
	static class FitnessDescendingComparator implements Comparator<Organism> {

		@Override
		public int compare(Organism o1, Organism o2) {
			if (o1.fitness > o2.fitness)
				return -1;
			
			if (o1.fitness < o2.fitness)
				return 1;
			
			return 0;
		}
		
	}

}
