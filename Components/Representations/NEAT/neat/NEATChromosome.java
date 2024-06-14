package neat;

import java.io.Serializable;
import java.util.ArrayList;

public class NEATChromosome implements Comparable<NEATChromosome>, Serializable {

	private static final long serialVersionUID = -3198656132365271219L;
	private ArrayList<NEATGene> genes;
	private double fitness;

	public NEATChromosome(ArrayList<NEATGene> genes) {
		this.genes = genes;
	}

	public void updateChromosome(ArrayList<NEATGene> newGenes) {
		this.genes = newGenes;
	}
	
	public ArrayList<NEATGene> genes() {
		return (this.genes);
	}

	public int size() {
		return (this.genes.size());
	}

	public void updateFitness(double fitness) {
		this.fitness = fitness;
	}

	public double fitness() {
		return (this.fitness);
	}

	public int compareTo(NEATChromosome o) {
		int returnVal = 0;
		// sorts with highest first
		if (this.fitness() > o.fitness()) {
			returnVal = -1;
		} else if (this.fitness() < o.fitness()) {
				returnVal = 1;
		}
		
		return (returnVal);
	}

	/** returns a clone of this chromosome */
	public NEATChromosome cloneChromosome() {
		ArrayList<NEATGene> newgenes = new ArrayList<NEATGene>(genes.size());
		for (int i=0;i<genes.size();i++)
			newgenes.add(genes.get(i).cloneGene());
		
		NEATChromosome c = new NEATChromosome(genes);
		
		return c;
	}

	public void addGene(NEATGene newgene) {
		genes.add(newgene);
	}
}
