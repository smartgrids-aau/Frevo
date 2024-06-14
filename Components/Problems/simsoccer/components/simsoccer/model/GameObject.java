package components.simsoccer.model;

public class GameObject {
	
	public double distance;
	//public double direction;
	
	public double getDistance() {
		return this.distance;
	}
	
	
	/*public double getDirection() {
		return this.direction;
	}*/
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	/*public void setDirection(double direction) {
		this.direction = direction;
	}*/
	
	/**
	 * Returns the type of the object
	 * @return 0 if undefined, 1 if player, 2 if ball, 3 if line, 4 if goal
	 */
	public int getType() {
		return 0;
	}
}
