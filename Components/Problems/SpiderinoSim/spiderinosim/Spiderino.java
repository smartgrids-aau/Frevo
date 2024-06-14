package spiderinosim;

import core.AbstractRepresentation;

public class Spiderino extends WObject {

	
	protected int id;	
	protected double rotation;
	protected int walkSteps;
	protected int turnSteps;
	protected AbstractRepresentation candidate;

	/**
	 * Creates a Spiderino.
	 * @param id
	 * @param rotation
	 * @param x
	 * @param y
	 * @param radius
	 */
	public Spiderino(int id, double rotation, double x, double y, double radius) {
		super(x, y, radius);
		this.id = id;
		this.rotation = rotation;
		this.walkSteps = 0;
		this.turnSteps = 0;
	}
	
	
	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public double getRotation() {
		return rotation;
	}

	
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	
	
	public int getWalkSteps() {
		return walkSteps;
	}
	
	
	public void incrementWalkSteps() {
		walkSteps++;
	}
	
	
	public int getTurnSteps() {
		return turnSteps;
	}
	
	
	public void incrementTurnSteps() {
		turnSteps++;
	}


	public AbstractRepresentation getCandidate() {
		return candidate;
	}


	public void setCandidate(AbstractRepresentation candidate) {
		this.candidate = candidate;
	}
	
	
}
