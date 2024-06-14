package fehervari.noveltysearch;

import net.jodk.lang.FastMath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class NoveltyArchive {

	// minimum threshold for a "novel item"
	float novelty_threshold;

	float novelty_floor;

	// current generation
	int generation;

	// parameter for how many neighbors to look at for N-nearest neighbor
	// distance novelty
	int neighbors;

	// counter to keep track of how many gens since we've added to the archive
	int time_out;

	// hall of fame mode, add an item each generation regardless of threshold
	boolean hall_of_fame;

	// add new items according to threshold
	boolean threshold_add;

	int this_gen_index;

	// all the novel items we have found so far
	ArrayList<NoveltyItem> novel_items = new ArrayList<NoveltyItem>();
	ArrayList<NoveltyItem> fittest = new ArrayList<NoveltyItem>();
	// novel items waiting addition to the set pending the end of the generation
	ArrayList<NoveltyItem> add_queue = new ArrayList<NoveltyItem>();

	public NoveltyArchive(float threshold) {

		// how many nearest neighbors to consider for calculating novelty score?
		neighbors = 15;
		generation = 0;
		time_out = 0; // used for adaptive threshold
		novelty_threshold = threshold;
		novelty_floor = 0.25f; // lowest threshold is allowed to get

		this_gen_index = 1;
		hall_of_fame = false;
		threshold_add = true;
	}

	// evaluate the novelty of the whole population
	void evaluate_population(ArrayList<NoveltyItem> pop, boolean fitness) {

		Iterator<NoveltyItem> it = pop.iterator();

		while (it.hasNext()) {
			evaluate_individual(it.next(), pop, fitness);
		}
	}

	// evaluate the novelty of a single individual
	void evaluate_individual(NoveltyItem ind, ArrayList<NoveltyItem> pop,
			boolean fitness) {
		//float result;
		if (fitness) // assign fitness according to average novelty
		{
			novelty_avg_nn(ind, -1, false, pop);
			
		} else // consider adding a point to archive based on dist to nearest
				// neighbor
		{
		//	result = novelty_avg_nn(ind, 1, false, null);
		//	ind.novelty = result;
			if (add_to_novelty_archive(ind.novelty))
				add_novel_item(ind);
		}
	}

	// criteria for adding to the archive
	private boolean add_to_novelty_archive(float novelty) {
		if (novelty > novelty_threshold)
			return true;
		else
			return false;
	}

	// nearest neighbor novelty score calculation
	private float novelty_avg_nn(NoveltyItem item, int neigh,
			boolean ageSmooth, ArrayList<NoveltyItem> pop) {
		ArrayList<NoveltyPair> novelties;

		if (pop == null)
			novelties = map_novelty_pop(item, null);
		else
			novelties = map_novelty_pop(item, pop);

		// sort in ascending order
		Collections.sort(novelties);

		float density = 0.0f;
		int len = novelties.size();
		float sum = 0.0f;
		float weight = 0.0f;

		if (neigh == -1) {
			neigh = neighbors;
		}

		if (len < 1) {
			item.age = 1.0f;
			add_novel_item(item);
		} else {
			len = neigh;
			if ((int) novelties.size() < len)
				len = novelties.size();
			int i = 0;

			while (weight < neigh && i < (int) novelties.size()) {
				float term = novelties.get(i).novelty;
				float w = 1.0f;

				if (ageSmooth) {
					float age = (novelties.get(i).noveltyItem).age;
					w = 1.0f - (float) FastMath.pow(0.95f, age);
				}

				sum += term * w;
				weight += w;
				i++;
			}

			if (weight != 0) {
				density = sum / weight;
			}
		}

		item.novelty = density;
		item.generation = generation;
		return density;
	}

	private void add_novel_item(NoveltyItem item) {
		this.add_novel_item(item, true);
	}

	// add novel item to archive
	private void add_novel_item(NoveltyItem item, boolean aq) {
		item.added = true;
		item.generation = generation;
		novel_items.add(item);
		if (aq)
			add_queue.add(item);
	}

	// map the novelty metric across the archive + current population
	private ArrayList<NoveltyPair> map_novelty_pop(NoveltyItem newitem,
			ArrayList<NoveltyItem> pop) {

		ArrayList<NoveltyPair> novelties = new ArrayList<NoveltyPair>();

		// compare to archive
		for (int i = 0; i < novel_items.size(); i++) {
			novelties.add(new NoveltyPair(novelty_metric(novel_items.get(i),
					newitem), novel_items.get(i)));
		}

		// compare to population
		if (pop != null) {
			for (int i = 0; i < pop.size(); i++) {
				novelties.add(new NoveltyPair(novelty_metric(pop.get(i),
						newitem), pop.get(i)));

			}
		}
		return novelties;
	}

	private class NoveltyPair implements Comparable<NoveltyPair> {
		float novelty;
		NoveltyItem noveltyItem;

		public NoveltyPair(float novelty_metric, NoveltyItem noveltyItem) {
			this.novelty = novelty_metric;
			this.noveltyItem = noveltyItem;
		}

		@Override
		public int compareTo(NoveltyPair other) {
			if (this.novelty > other.novelty)
				return 1;
			else if (this.novelty < other.novelty)
				return -1;

			return 0;
		}
	}

	// simple novelty metric
	private float novelty_metric(NoveltyItem x, NoveltyItem y) {
		float diff = 0.0f;
		for (int k = 0; k < x.noveltypoints.size(); k++) {
			diff += Math.abs(x.noveltypoints.get(k) - y.noveltypoints.get(k));
		}
		return diff;
	}

	// steady-state end of generation call (every so many individuals)
	void end_of_gen_steady(ArrayList<NoveltyItem> pop) {

		generation++;

		add_pending();
	}

	// adjust dynamic novelty threshold depending on how many have been added to
	// archive recently
	private void add_pending() {
		// if (record) {
		// (*datafile) << novelty_threshold << " " << add_queue.size() << endl;
		// }

		if (hall_of_fame) {
			if (add_queue.size() == 1)
				time_out++;
			else
				time_out = 0;
		} else {
			if (add_queue.size() == 0)
				time_out++;
			else
				time_out = 0;
		}

		// if no individuals have been added for 10 generations
		// lower threshold
		if (time_out == 10) {
			novelty_threshold *= 0.95;
			if (novelty_threshold < novelty_floor)
				novelty_threshold = novelty_floor;
			time_out = 0;
		}

		// if more than four individuals added this generation
		// raise threshold
		if (add_queue.size() > 4)
			novelty_threshold *= 1.2;

		add_queue.clear();

		this_gen_index = novel_items.size();
	}

	int get_set_size() {
		return (int) novel_items.size();
	}

	void update_fittest(NoveltyItem offspring) {
		int allowed_size = 5;
		if (fittest.size() < allowed_size) {
				NoveltyItem x = offspring.clone();
				fittest.add(x);
				// sort fitness list in a descending order
				Collections.sort(fittest, NoveltyItem.getReverseFitnessComparator());

		} else {
			// List is full, add offspring to the list only if it is better than the worst (last) element 
			if (offspring.genotype.getFitness() > fittest.get(fittest.size()-1).genotype.getFitness()) {
				NoveltyItem x = offspring.clone();
				fittest.add(x);

				// sort fitness list in a descending order
				Collections.sort(fittest, NoveltyItem.getReverseFitnessComparator());

				// remove last
				fittest.remove(fittest.size()-1);
			}
		}
	}
}
