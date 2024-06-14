package components.cdrones;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import components.controllers.DroneController;

public class Drone {
	
	private Point2D.Double position;
	public Point2D.Double speedVector = new Point2D.Double(0,0);
	public Point2D.Double accelerationVector = new Point2D.Double(0,0);
	private double bodyDirection;
	public List<Intention> buffer = new ArrayList<Intention>();
	public int id_number;
	
	/** Size of the drone */
	public static final double DRONE_SIZE = 1;
	
	private DroneController controller;
	
	/** A range within observation is possible */
	public static final int DETECTION_RANGE = 5;
	/** A range within communication is possible */
	public static final int COMMUNICATION_RANGE = 40;
	/** Range of the distance sensors */
	public static final int SENSOR_RANGE = 5;
	public static final double SENSOR_ANGLE = 45;
	
	public boolean BUMPER_TL_ON = false;
	public boolean BUMPER_TR_ON = false;
	public boolean BUMPER_BL_ON = false;
	public boolean BUMPER_BR_ON = false;
	
	public boolean RADIO_TL_ON = false;
	public boolean RADIO_TR_ON = false;
	public boolean RADIO_BL_ON = false;
	public boolean RADIO_BR_ON = false;
	
	public Point2D.Double getPosition() {
		return this.position;
	}
	
	public void setPosition (Point2D.Double pos) {
		this.position = pos;
	}

	public Drone (DroneController c, int id) {
		this.controller = c;
		this.controller.setDrone(this);
		speedVector = new Point2D.Double(0,0);
		accelerationVector = new Point2D.Double(0,0);
		this.id_number = id;
	}
	
	public DroneController getController() {
		return this.controller;
	}
	
	public void dashto(int power, double direction) {
		buffer.add(new Intention(DronesServer.DASHTO,power,direction,null));
	}
	
	/**
	 * Adds the acceleration vector to the speedvector then normalizes it
	 * @param accvect
	 */
	public void addAccVector(Point2D.Double accvect) {
		Point2D.Double speedvector = new Point2D.Double (this.speedVector.x+accvect.x,this.speedVector.y+accvect.y);
		//normalize
		double length = DronesServer.getLength(speedvector); 
		if (length > DronesServer.DRONE_SPEED_MAX ) {
			double ratio = (DronesServer.DRONE_SPEED_MAX/length); //! POSSIBLE NAN ERROR
			speedvector = new Point2D.Double (speedvector.x*ratio,speedvector.y*ratio);
		}
		this.speedVector = speedvector;
	}

	public void applydecay() {
		Point2D.Double newspeed = new Point2D.Double (speedVector.x*DronesServer.DRONE_DECAY,speedVector.y*DronesServer.DRONE_DECAY);
		this.speedVector = newspeed;
		
	}

	/** Set body facing direction in degrees */
	public void setBodyDirection(double angle) {
		if (angle >= 0) {
			this.bodyDirection = angle % 360;
		}
		else {
			angle = angle % 360;
			this.bodyDirection = 360+angle;
		}
	}

	public double getBodyDirection() {
		return this.bodyDirection;
	}

}
