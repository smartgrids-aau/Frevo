package sched;

public class Task {

	private int numRounds = 0;
	private boolean hasReduce;

	public Task(boolean hasReduce) {
		this.hasReduce = hasReduce;
		numRounds++;
		if (hasReduce) {
			numRounds++;
		}
	}

	public boolean hasReduce() {
		return hasReduce;
	}

	public void nextRound() {
		this.numRounds--;
	}

	public int getnumRounds() {
		return this.numRounds;
	}
	
	

}
