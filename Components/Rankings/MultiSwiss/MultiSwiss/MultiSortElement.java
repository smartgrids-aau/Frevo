package MultiSwiss;

import core.AbstractRepresentation;

/**
 * This inner class is a container for representation and scores that can be used to sort
 * the candidates
 * @author wilfried
 *
 */
public class MultiSortElement implements Comparable<MultiSortElement> {
	double totalscore = 0;
	int number_of_games = 0;
	static double average_score=3.0;
			
	public AbstractRepresentation player;

	public AbstractRepresentation getPlayer() {
		return player;
	}

	public MultiSortElement(AbstractRepresentation r) {
		this.player = r; //only shallow copy here 
	}

	public void addResult(double score) {
		totalscore+=score;
		number_of_games++;
	}
	
	public double getScore() {
		if (number_of_games==0)
			return average_score;
		else
			return totalscore/number_of_games;
	}
	
	@Override
	public int compareTo(MultiSortElement o) {
		
		if (this.getScore() < o.getScore())
			return -1;
		else if (this.getScore() > o.getScore())
			return 1;
		return 0;
	}
}