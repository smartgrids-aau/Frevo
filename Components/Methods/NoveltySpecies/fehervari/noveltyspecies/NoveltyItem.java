package fehervari.noveltyspecies;

import java.util.ArrayList;
import java.util.Comparator;

import core.AbstractRepresentation;

public class NoveltyItem {

	static final FitnessDescendingComparator FITNESS_DESCENDING_COMPARATOR = new FitnessDescendingComparator();

	AbstractRepresentation genotype;

	double fitness;

	ArrayList<Float> data;

	int indiv_number;

	float novelty;

	public boolean added;

	float age;

	float generation;

	public NoveltyItem() {
		added = false;
		genotype = null;
		age = 0.0f;
		generation = 0.0f;
		indiv_number = (-1);
	}

	public NoveltyItem(NoveltyItem item) {
		this.added = item.added;
		this.genotype = item.genotype.clone();

		this.age = item.age;
		this.fitness = item.fitness;
		this.novelty = item.novelty;
		this.generation = item.generation;
		this.indiv_number = item.indiv_number;

		this.data = new ArrayList<Float>(item.data);
	}

	static class FitnessDescendingComparator implements Comparator<NoveltyItem> {

		@Override
		public int compare(NoveltyItem o1, NoveltyItem o2) {
			if (o1.fitness < o2.fitness)
				return 1;

			if (o1.fitness > o2.fitness)
				return -1;

			return 0;
		}

	}
}
