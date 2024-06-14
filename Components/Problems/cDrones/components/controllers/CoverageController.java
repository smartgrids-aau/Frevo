package components.controllers;

import java.util.ArrayList;
import java.util.Random;

import net.jodk.lang.FastMath;

import components.cdrones.Drone;

/** A controller based on Evsen's coverage-based algorithm (first version).
 * It changes directions with a given frequency. Drones repel eachother, at the border
 * mirrored angles are taking into account with higher probability */
public class CoverageController extends DroneController {

	private double direction;
	
	private ArrayList<Drone> dronesinrange;
	
	private final int DIRECTION_CHANGE_INTERVAL = 100;//50 is worse
	
	private int stepper = 0;


	public CoverageController (Random r) {
		this.generator = r;
		
		//initial direction is random
		direction = generator.nextInt(360);
	}
	
	/** The behavior of the drone */
	public void postInfo() {
		
		if (stepper == DIRECTION_CHANGE_INTERVAL) {
			calculateNewDirection();
			stepper = 0;
		}	
		this.drone.dashto( 100 , direction);
		
		stepper++;
	}
	
	private void calculateNewDirection() {
		
		if (dronesinrange.size() == 0) {
			//do nothing
			//direction = generator.nextInt(360);
		} else {
			double vecx = 1.0/Drone.COMMUNICATION_RANGE;
			double vecy = 1.0/Drone.COMMUNICATION_RANGE;
			
			for (Drone od : dronesinrange) {
				double xdiff = od.getPosition().x - this.drone.getPosition().x;
				double ydiff = od.getPosition().y - this.drone.getPosition().y;
				vecx += 1.0 / xdiff;
				vecy += 1.0 / ydiff;
			}
			
			//convert it to angle
			direction = FastMath.toDegrees(FastMath.atan2(vecy, vecx));
			
		}
	}
	
	public void reportFailedMove() {		
		boolean good = false;
		while (!good) {
			double newdir = generator.nextInt(360);
			if ((Math.abs(180-(newdir-direction)) % 360) > 45) {
				good = true;
				direction = newdir;
			}
		}
	}
	
	public void addDronesInRange( ArrayList<Drone> drones) {
		this.dronesinrange = drones;
	}
	
	public void preInfo() {
		//nothing to reset
	}
}
