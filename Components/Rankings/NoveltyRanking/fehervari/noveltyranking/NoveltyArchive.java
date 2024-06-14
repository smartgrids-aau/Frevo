package fehervari.noveltyranking;

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
	
	int number_of_evaluations;
	
	int items_added_since_last_change;

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
		
		items_added_since_last_change = 0;
		number_of_evaluations = 0;
	}

	// evaluate the novelty of the whole population
	public void evaluate_population(ArrayList<NoveltyItem> pop, boolean fitness) {

		Iterator<NoveltyItem> it = pop.iterator();

		while (it.hasNext()) {
			evaluate_individual(it.next(), pop, fitness);
		}
	}

	// evaluate the novelty of a single individual
	private void evaluate_individual(NoveltyItem ind,
			ArrayList<NoveltyItem> pop, boolean fitness) {
		//float result;
		if (fitness) // assign fitness according to average novelty compared to population and archive
		{
			novelty_avg_nn(ind, -1, false, pop);
			number_of_evaluations++;
			
		} else // consider adding a point to archive based on dist to nearest
				// neighbor
		{
			// change from Istvan: do not calculate again to prevent overwrite of previous data
			//result = novelty_avg_nn(ind, 1, false, null);
			//ind.novelty = result;
			if (add_to_novelty_archive(ind.novelty)) {//result
				add_novel_item(ind);
				items_added_since_last_change++;
			}
		}
	}

	// criteria for adding to the archive
	private boolean add_to_novelty_archive(float novelty) {
		return novelty > novelty_threshold;
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
			if (novelties.size() < len)
				len = novelties.size();
			int i = 0;

			while (weight < neigh && i < novelties.size()) {
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
	
	int size() {
		return novel_items.size();
	}

	// map the novelty metric across the archive + current population
	private ArrayList<NoveltyPair> map_novelty_pop(NoveltyItem newitem,
			ArrayList<NoveltyItem> pop) {
		
		ArrayList<NoveltyPair> novelties;
		if (pop != null)
			novelties = new ArrayList<NoveltyPair>(novel_items.size()+pop.size());
		else
			novelties = new ArrayList<NoveltyPair>(novel_items.size());

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
}
