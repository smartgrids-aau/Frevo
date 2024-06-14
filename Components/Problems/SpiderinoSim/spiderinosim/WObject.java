package spiderinosim;


/**
 * Base class for all objects in the World.
 */
public class WObject {

	protected double x;
	protected double y;
	protected double radius;

	
	/**
	 * Creates a WObject of the specified size at the specified position.
	 * @param x
	 * @param y
	 * @param radius
	 */
	public WObject(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	
	public double getX() {
		return x;
	}

	
	public void setX(double x) {
		this.x = x;
	}

	
	public double getY() {
		return y;
	}

	
	public void setY(double y) {
		this.y = y;
	}

	
	public double getRadius() {
		return radius;
	}	
}
