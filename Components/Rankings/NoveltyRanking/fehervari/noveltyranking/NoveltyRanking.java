package fehervari.noveltyranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

import utils.NESRandom;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ProblemXMLData;
import core.XMLFieldEntry;

public class NoveltyRanking extends AbstractRanking {

	private NoveltyArchive archive;
	private AbstractRepresentation bestcandidate;

	private int THRESHOLD_ADJUST_LIMIT = 2500;

	public NoveltyRanking(Hashtable<String, XMLFieldEntry> properties) {
		super(properties);
		
		// load parameters
		
		// default threshold
		float initial_threshold = 6.0f;
		try {
			initial_threshold = Float.parseFloat(getPropertyValue("initial_threshold"));
		} catch (Exception e) {
			// initial_threshold = 6.0f;
		}

		// Create archive
		archive = new NoveltyArchive(initial_threshold);
	}

	/**
	 * Sorts the given array of representations in a descending order of fitness
	 * and returns the number of evaluation that was required for the ranking to
	 * finish. Evaluation of the candidates is done by the provided
	 * <i>problem</i> component.
	 * 
	 * @param representations
	 *            The population to be sorted.
	 * @param problem
	 *            The problem descriptor to be used for evaluation.
	 * @param random
	 *            The random generator object used for sorting.
	 * @return the number of evaluations that were needed to rank the given set
	 *         of representations
	 */
	public int sortCandidates(
			final ArrayList<AbstractRepresentation> representations,
			ProblemXMLData problem, NESRandom random) {

		int indiv_counter = 0;

		// calculate the novelty metrics for each representation in the
		// population
		ArrayList<NoveltyItem> novelty_pop = new ArrayList<NoveltyItem>(
				representations.size());
		Iterator<AbstractRepresentation> it = representations.iterator();

		// TODO introduce threading here
		while (it.hasNext()) {
			AbstractRepresentation rep = it.next();
			try {
				novelty_pop.add(new NoveltyItem(rep,
						((AbstractSingleProblem) (problem
								.getNewProblemInstance())).getNoveltyVector(
								rep, true), indiv_counter++));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// assign fitness scores based on novelty
		archive.evaluate_population(novelty_pop, true);
		// add to archive
		archive.evaluate_population(novelty_pop, false);

		// sort based on novelty
		Collections.sort(novelty_pop, Collections.reverseOrder());

		// find best fitness
		double bestfitness = Double.MIN_VALUE;
		for (int i = 0; i < novelty_pop.size(); i++) {
			// replace individuals based on sorted novelty
			representations.set(i, novelty_pop.get(i).genotype);

			if (novelty_pop.get(i).genotype.getFitness() > bestfitness) {
				bestfitness = novelty_pop.get(i).genotype.getFitness();
				bestcandidate = novelty_pop.get(i).genotype;
			}
		}

		// sort based on real fitness
		// Collections.sort(representations, Collections.reverseOrder());

		// adjust novelty threshold
		// If 2,500 evaluations pass and no new individuals have been added to
		// the archive, the threshold is lowered by 5%. If over four are added
		// in the same amount of evaluations, it is raised by 20%. (by Lehman)

		if (archive.number_of_evaluations >= THRESHOLD_ADJUST_LIMIT) {
			// reset counter
			archive.number_of_evaluations = 0;

			if (archive.items_added_since_last_change >= 4) {
				archive.novelty_threshold *= 1.2f;
			} else if (archive.items_added_since_last_change == 0) {
				archive.novelty_threshold *= 0.95f;
			}
			
			archive.items_added_since_last_change = 0;
		}

		System.out.println("Novelty archive size: " + archive.size()
				+ " threshold: " + archive.novelty_threshold);
		return representations.size();
	}

	public AbstractRepresentation getBestCandidate() {
		return bestcandidate;
	}

}
