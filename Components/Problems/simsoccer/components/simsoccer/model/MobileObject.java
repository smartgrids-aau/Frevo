package components.simsoccer.model;


import java.awt.geom.Point2D;


public class MobileObject extends FieldObject {

	public MobileObject(Point2D.Double pos) {
		super(pos);
	}

	public void applydecay() {
		
	}
	public double directionChange;
	public double distanceChange;
	
	public Point2D.Double speedVector = new Point2D.Double(0,0);
	public Point2D.Double accelerationVector = new Point2D.Double(0,0);
	
	public double getDirectionChange() {
		return this.directionChange;
	}
	
	public double getDistanceChange() {
		return this.distanceChange;
	}
	
	public Point2D.Double getSpeedVector() {
		return this.speedVector;
	}
	
	public void setSpeedVector(Point2D.Double speed) {
		this.speedVector = speed;
	}
	
	public Point2D.Double getAccelerationVector() {
		return this.accelerationVector;
	}
	
	public void setaccelerationVector(Point2D.Double acc) {
		this.accelerationVector = acc;
	}
}
