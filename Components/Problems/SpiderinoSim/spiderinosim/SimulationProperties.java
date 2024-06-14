package spiderinosim;

import java.util.Hashtable;

import core.XMLFieldEntry;

/**
 * Holds simulation properties.
 */
public class SimulationProperties {
	
	protected int fitnessFunctionNumber;
	protected int outputCount;
	protected int evaluationCount;
	protected int maximumSteps;
	protected int stepTime;
	protected double worldWidth;
	protected double worldHeight;
	protected int spiderinoCount;
	protected double spiderinoRadius;
	protected double spiderinoWalkSpeed;
	protected double spiderinoTurnSpeed;
	protected double lightRadius;
	protected int lightCount;
	protected double distanceThreshold;
	protected double goalThreshold;
	protected double distanceFitnessWeight;
	protected double walkStepsFitnessWeight;
	protected double turnStepsFitnessWeight;


	public SimulationProperties(int fitnessFunctionNumber, int outputCount, int evaluationCount, int maximumSteps, int stepTime, double worldWidth, double worldHeight, int spiderinoCount, double spiderinoRadius, double spiderinoWalkSpeed, double spiderinoTurnSpeed, double lightRadius, int lightCount, double distanceThreshold, double goalThreshold, double distanceFitnessWeight, double walkStepsFitnessWeight, double turnStepsFitnessWeight) {
		this.fitnessFunctionNumber=fitnessFunctionNumber;
		this.outputCount = outputCount;
		this.evaluationCount = evaluationCount;
		this.maximumSteps = maximumSteps;
		this.stepTime = stepTime;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.spiderinoCount = spiderinoCount;
		this.spiderinoRadius = spiderinoRadius;
		this.spiderinoWalkSpeed = spiderinoWalkSpeed;
		this.spiderinoTurnSpeed = spiderinoTurnSpeed;
		this.lightRadius = lightRadius;
		this.lightCount = lightCount;
		this.distanceThreshold = distanceThreshold;
		this.goalThreshold = goalThreshold;
		this.distanceFitnessWeight = distanceFitnessWeight;
		this.walkStepsFitnessWeight = walkStepsFitnessWeight;
		this.turnStepsFitnessWeight = turnStepsFitnessWeight;
	}
	
	public SimulationProperties(Hashtable<String, XMLFieldEntry> properties) {	
		fitnessFunctionNumber = Integer.parseInt(properties.get("fitnessFunctionNumber").getValue());
		outputCount = Integer.parseInt(properties.get("outputCount").getValue());
		evaluationCount = Integer.parseInt(properties.get("evaluationCount").getValue());
		maximumSteps = Integer.parseInt(properties.get("maximumSteps").getValue());
		stepTime = Integer.parseInt(properties.get("stepTime").getValue());
		worldWidth = Double.parseDouble(properties.get("worldWidth").getValue());
		worldHeight = Double.parseDouble(properties.get("worldHeight").getValue());
		spiderinoCount = Integer.parseInt(properties.get("spiderinoCount").getValue());
		spiderinoRadius = Double.parseDouble(properties.get("spiderinoRadius").getValue());
		spiderinoWalkSpeed = Double.parseDouble(properties.get("spiderinoWalkSpeed").getValue());
		spiderinoTurnSpeed = Double.parseDouble(properties.get("spiderinoTurnSpeed").getValue());
		lightRadius = Double.parseDouble(properties.get("lightRadius").getValue());
		lightCount = Integer.parseInt(properties.get("lightCount").getValue());
		distanceThreshold = Double.parseDouble(properties.get("distanceThreshold").getValue());
		goalThreshold = Double.parseDouble(properties.get("goalThreshold").getValue());
		distanceFitnessWeight = Double.parseDouble(properties.get("distanceFitnessWeight").getValue());
		walkStepsFitnessWeight = Double.parseDouble(properties.get("walkStepsFitnessWeight").getValue());
		turnStepsFitnessWeight = Double.parseDouble(properties.get("turnStepsFitnessWeight").getValue());	
	}
	
	public int getOutputCount() {
		return outputCount;
	}
	
	public int getEvaluationCount() {
		return evaluationCount;
	}
	
	public int getMaximumSteps() {
		return maximumSteps;
	}


	public int getStepTime() {
		return stepTime;
	}


	public double getWorldWidth() {
		return worldWidth;
	}


	public double getWorldHeight() {
		return worldHeight;
	}


	public int getSpiderinoCount() {
		return spiderinoCount;
	}


	public double getSpiderinoRadius() {
		return spiderinoRadius;
	}


	public double getSpiderinoWalkSpeed() {
		return spiderinoWalkSpeed;
	}


	public double getSpiderinoTurnSpeed() {
		return spiderinoTurnSpeed;
	}


	public double getLightRadius() {
		return lightRadius;
	}


	public int getLightCount() {
		return lightCount;
	}

	public double getDistanceThreshold() {
		return distanceThreshold;
	}
	
	public double getGoalThreshold() {
		return goalThreshold;
	}
	
	public double getDistanceFitnessWeight() {
		return distanceFitnessWeight;
	}

	
	public double getWalkStepsFitnessWeight() {
		return walkStepsFitnessWeight;
	}

	
	public double getTurnStepsFitnessWeight() {
		return turnStepsFitnessWeight;
	}

}
