package pso;

import java.util.ArrayList;
import java.util.List;

import core.AbstractRepresentation;

/**
 * 
 * 
 * @author Sergii Zhevzhyk
 */
public class Sparcle {
	private AbstractRepresentation representation;
	
	private ArrayList<Double> velocities = new ArrayList<Double>(); //Current velocity
	
	private AbstractRepresentation localBestRepresentation;
	
	private double localBestFitness;
	
	public Sparcle(AbstractRepresentation representation) {
		this.representation = representation;
	}
		
	public double getLocalBestFitness() {
		return localBestFitness;
	}

	public void setLocalBestFitness(double localBestFitness) {
		this.localBestFitness = localBestFitness;
	}

	public AbstractRepresentation getRepresentation() {
		return representation;
	}
	
	public List<Double> getVelocities() {
		return velocities;
	}
	
	public AbstractRepresentation getLocalBestRepresentation() {
		return localBestRepresentation;
	}
	
	public void setLocalBestRepresentation(AbstractRepresentation representation) {
		this.localBestRepresentation = representation;
	}

	public double calculateFitness() {
		double fitness = representation.getFitness();
		if (fitness > localBestFitness) {
			localBestFitness = fitness;
			localBestRepresentation = representation.clone();
		}
		return fitness;
	}
	
	
	
		
}
