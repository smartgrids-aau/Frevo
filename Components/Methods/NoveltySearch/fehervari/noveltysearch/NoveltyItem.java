package fehervari.noveltysearch;

import java.util.ArrayList;
import java.util.Comparator;

import core.AbstractRepresentation;

public class NoveltyItem implements Comparable<NoveltyItem>{
	private static final FitnessReverseComparator FITNESS_REVERSE_COMPARATOR = new FitnessReverseComparator();
	private static final FitnessComparator FITNESS_COMPARATOR = new FitnessComparator();
	
	AbstractRepresentation genotype;
	ArrayList<Float> noveltypoints;
	float novelty;
	int indiv_number;
	float age = 0.0f;
	float generation = 0.0f;
	boolean added;

	public NoveltyItem(AbstractRepresentation rep,
			ArrayList<Float> noveltyVector, int id) {
		this.genotype = rep;
		this.noveltypoints = noveltyVector;
		this.indiv_number = id;
	}

	@Override
	public int compareTo(NoveltyItem o) {
		if (this.novelty > o.novelty)
			return 1;
		if (this.novelty < o.novelty)
			return -1;
		
		return 0;
	}
	
	public NoveltyItem clone() {
		NoveltyItem c = new NoveltyItem(this.genotype.clone(),null,this.indiv_number);
		ArrayList<Float> nv = new ArrayList<Float>(noveltypoints.size());
		
		for (int i=0;i<noveltypoints.size();i++)
			nv.add(noveltypoints.get(i));
		
		c.noveltypoints = nv;
			
		return c;
	}
	
	public static FitnessReverseComparator getReverseFitnessComparator() {
		return FITNESS_REVERSE_COMPARATOR;
	}
	
	public static FitnessComparator getFitnessComparator() {
		return FITNESS_COMPARATOR;
	}
	
	private static final class FitnessReverseComparator implements Comparator<NoveltyItem>{

		@Override
		public int compare(NoveltyItem o1, NoveltyItem o2) {
			if (o1.genotype.getFitness() > o2.genotype.getFitness())
				return -1;
			else if (o1.genotype.getFitness() < o2.genotype.getFitness())
				return 1;
			return 0;
		}
	}
	
	private static final class FitnessComparator implements Comparator<NoveltyItem>{

		@Override
		public int compare(NoveltyItem o1, NoveltyItem o2) {
			if (o1.genotype.getFitness() < o2.genotype.getFitness())
				return -1;
			else if (o1.genotype.getFitness() > o2.genotype.getFitness())
				return 1;
			return 0;
		}
	}
}
