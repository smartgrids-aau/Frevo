package components.controllers;


import java.util.Random;

import components.cdrones.Drone;
import core.AbstractRepresentation;

/** Abstract class for drone mobility controller */
public abstract class DroneController {
	
	public Random generator;
	
	/** The drone assigned to this controller */
	protected Drone drone;
	
	/** The representation "driving" this controller */
	protected AbstractRepresentation representation;
	
	/** Behavior of the controller <i>before</i> the inputs have been set */
	public abstract void preInfo();
	
	/** Behavior of the controller <i>after</i> the inputs have been set */
	public abstract void postInfo();
	

	public void setDrone(Drone d) {
		this.drone = d;
	}
	
	/** Fired when the drone's next movement would result in a forbidden zone */
	public void reportFailedMove() {
	}
}
