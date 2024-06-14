package components.controllers;

import java.util.Random;

/** A controller whose output is randomly changed after a given step */
public class RandomWalkController extends DroneController {
	
	/** Direction change period */
	private static final int BASESTEP = 100;
	private int stepper = 0;
	private Random generator;
	private int direction;

	public RandomWalkController(Random generator) {
		this.generator = generator;
		this.direction = generator.nextInt(360);
	}

	@Override
	public void postInfo() {
		stepper++;
		if (stepper == BASESTEP) {
			stepper = 0;
			direction = generator.nextInt(360);
		}
		this.drone.dashto( 100 , direction);		
	}
	
	public void reportFailedMove() {
		direction = generator.nextInt(360);
	}

	@Override
	public void preInfo() {
		// do nothing
	}

}
