package components.ddrones;

import java.awt.Point;


public class Drone {
	private Point current; //current position in the grid
	private Point previous; //previous position in the grid
	private int bodyDirection;
	
	private DroneController controller;


	public Drone (DroneController c) {
		this.controller = c;
		this.controller.setDrone(this);
		this.previous = new Point(0,0);
		this.current = new Point(0,0);
	}
	
	public Point getPosition() {
		return this.current;
	}
	
	public void setPosition (Point pos) {
		this.current = pos;
	}
	
	public Point getPreviousPosition() {
		return this.previous;
	}
	
	public void setPreviousPosition(Point pos) {
		this.previous = pos;
	}
	
	public void setBodyDirection(int d) {
		this.bodyDirection = d;
	}

	public double getBodyDirection() {
		return this.bodyDirection;
	}
	
	public DroneController getController() {
		return this.controller;
	}
	
}
