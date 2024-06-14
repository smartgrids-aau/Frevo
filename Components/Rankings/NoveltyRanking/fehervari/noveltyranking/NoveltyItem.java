package fehervari.noveltyranking;

import java.util.ArrayList;

import core.AbstractRepresentation;

public class NoveltyItem implements Comparable<NoveltyItem>{
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
}
