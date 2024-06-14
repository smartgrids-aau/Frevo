package components.simsoccer.model;

import java.awt.geom.Point2D;

import components.simsoccer.SimServer;

public class Ball extends MobileObject {
	public Ball(Point2D.Double pos) {
		super(pos);
	}
	
	public int getType() {
		return 2;
	}
	
	public void applydecay() {
		this.speedVector = new Point2D.Double (speedVector.x*SimServer.BALL_DECAY,speedVector.y*SimServer.BALL_DECAY);
		
	}

	/**
	 * Adds the acceleration vector to the speedvector then normalizes it
	 * @param accvect
	 */
	public void addAccVector(Point2D.Double accvect) {
		Point2D.Double speedvector = new Point2D.Double (this.speedVector.x+accvect.x,this.speedVector.y+accvect.y);
		//normalize
		double length = SimServer.getLength(speedvector); 
		if (length > SimServer.BALL_SPEED_MAX ) {
			double ratio = (SimServer.BALL_SPEED_MAX/length);
			speedvector = new Point2D.Double (speedvector.x*ratio,speedvector.y*ratio);
		}
		this.speedVector = speedvector;
	}
}
