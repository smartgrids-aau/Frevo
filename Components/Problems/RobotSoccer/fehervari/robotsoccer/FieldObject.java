package fehervari.robotsoccer;

import java.awt.geom.Point2D;

public abstract class FieldObject {
	
	/** Robot's actual position */
	private Point2D position;
	
	/** Returns the actual position of this robot. */
	public final Point2D getPosition() {
		return this.position;
	}

	/** Sets the actual position of this robot. */
	public final void setPosition(Point2D pos) {
		this.position = pos;
	}
	
	public abstract double getDiameter();
}
