package fehervari.noveltyspecies;

import java.util.ArrayList;
import java.util.Collections;

public class NoveltyArchive {

	// counter to keep track of how many gens since we've added to the archive
	int time_out;
	// parameter for how many neighbors to look at for N-nearest neighbor
	// distance novelty
	int neighbors;

	// current generation
	int generation;

	// minimum threshold for a "novel item"
	float novelty_threshold;
	float novelty_floor;

	int this_gen_index;

	// hall of fame mode, add an item each generation regardless of threshold
	boolean hall_of_fame;
	// add new items according to threshold
	boolean threshold_add;

	ArrayList<NoveltyItem> novel_items = new ArrayList<NoveltyItem>();
	ArrayList<NoveltyItem> add_queue = new ArrayList<NoveltyItem>();
	ArrayList<NoveltyItem> fittest = new ArrayList<NoveltyItem>();

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

	void evaluate_population(Population pop, boolean fitness) {
		for (int i=0;i<pop.organisms.size();i++) {
			evaluate_individual(pop.organisms.get(i), pop, fitness);
		}
			

	}

	void evaluate_individual(Organism ind, Population pop, boolean fitness) {
		float result;
		if (fitness) // assign fitness according to average novelty
		{
			result = novelty_avg_nn(ind.noveltypoint, -1, false, pop);
			ind.fitness = result;
		} else // consider adding a point to archive based on dist to nearest
				// neighbor
		{
			result = novelty_avg_nn(ind.noveltypoint, 1, false, null);
			ind.noveltypoint.novelty = result;
			if (add_to_novelty_archive(result))
				add_novel_item(ind.noveltypoint, true);
		}
	}

	/** add novel item to archive (aq =true by defalt) */
	private void add_novel_item(NoveltyItem item, boolean aq) {
		item.added = true;
		item.generation = generation;
		novel_items.add(item);
		if (aq)
			add_queue.add(item);

	}

	boolean add_to_novelty_archive(float novelty) {
		// criteria for adding to the archive
		if (novelty > novelty_threshold)
			return true;

		return false;
	}

	// method too heavy!!
	private float novelty_avg_nn(NoveltyItem item, int neigh,
			boolean ageSmooth, Population pop) {
		// nearest neighbor novelty score calculation

		ArrayList<NoveltyPair> novelties;
		if (pop != null)
			novelties = map_novelty_pop(item, pop);
		else
			novelties = map_novelty(item);

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
			add_novel_item(item, true);
		} else {
			len = neigh;
			if ((int) novelties.size() < len)
				len = novelties.size();
			int i = 0;

			while (weight < neigh && i < (int) novelties.size()) {
				float term = novelties.get(i).first;
				float w = 1.0f;

				if (ageSmooth) {
					float age = (novelties.get(i).second).age;
					w = 1.0f - (float) (Math.pow(0.95, age));
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

	class NoveltyPair implements Comparable<NoveltyPair> {

		/** Novelty metric */
		float first;
		/** Novelty Item */
		NoveltyItem second;

		public NoveltyPair(float novelty_metric, NoveltyItem noveltyItem) {
			first = novelty_metric;
			second = noveltyItem;
		}

		@Override
		public int compareTo(NoveltyPair o) {
			if (this.first > o.first)
				return 1;

			if (this.first < o.first)
				return -1;

			return 0;
		}
	}

	/** map the novelty metric across the archive */
	private ArrayList<NoveltyPair> map_novelty(NoveltyItem newitem) {
		ArrayList<NoveltyPair> novelties = new ArrayList<NoveltyPair>();

		// across archive
		for (int i = 0; i < (int) novel_items.size(); i++) {
			novelties.add(new NoveltyPair(novelty_metric(novel_items.get(i),
					newitem), novel_items.get(i)));
		}
		return novelties;
	}

	/** map the novelty metric across the archive + current population */
	ArrayList<NoveltyPair> map_novelty_pop(NoveltyItem newitem, Population pop) {
		ArrayList<NoveltyPair> novelties = new ArrayList<NoveltyPair>();

		// across archive
		for (int i = 0; i < (int) novel_items.size(); i++) {
			novelties.add(new NoveltyPair(novelty_metric(novel_items.get(i),
					newitem), novel_items.get(i)));
		}

		// across population
		for (int i = 0; i < (int) pop.organisms.size(); i++) {
			novelties.add(new NoveltyPair(novelty_metric(
					pop.organisms.get(i).noveltypoint, newitem), pop.organisms
					.get(i).noveltypoint));

		}
		return novelties;
	}

	// simple novelty metric
	private float novelty_metric(NoveltyItem x, NoveltyItem y) {
		float diff = 0.0f;
		for (int k = 0; k < x.data.size(); k++) {
			diff += Math.abs(x.data.get(k) - y.data.get(k));
		}
		return diff;
	}

	public void end_of_gen_steady(Population pop) {
		generation++;

		add_pending();
	}

	// adjust dynamic novelty threshold depending on how many have been added to
	// archive recently
	void add_pending() {

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

	public void update_fittest(Population pop) {
		// sort in descending order
		Collections.sort(fittest, NoveltyItem.FITNESS_DESCENDING_COMPARATOR);
	}

	// maintain list of fittest organisms so far
	void update_fittest(Organism org) {
		int allowed_size = 5;
		if (fittest.size() < allowed_size) {
			if (org.noveltypoint != null) {
				NoveltyItem x = new NoveltyItem((org.noveltypoint));
				fittest.add(x);
				Collections.sort(fittest, NoveltyItem.FITNESS_DESCENDING_COMPARATOR);
			}
		} else {
			if (org.noveltypoint.fitness > fittest.get(fittest.size()-1).fitness) {
				NoveltyItem x = new NoveltyItem((org.noveltypoint));
				fittest.set(fittest.size()-1,x);

				Collections.sort(fittest, NoveltyItem.FITNESS_DESCENDING_COMPARATOR);
			}
		}
	}

	int get_set_size() {
		return novel_items.size();
	}
}
