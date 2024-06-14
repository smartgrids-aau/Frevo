package spiderinosim;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents the Spiderino world, holds it's contents.
 */
public class World {
	protected double width;
	protected double height;
	protected List<WObject> objects;
	
	
	/**
	 * Creates a new world of the specified dimensions.
	 * @param width
	 * @param height
	 */
	public World(double width, double height) {
		this.width = width;
		this.height = height;
		objects = new ArrayList<WObject>();
	}
	
	
	/**
	 * Moves o to the specified new position iff it is valid.
	 * @param o
	 * @param x
	 * @param y
	 * @return true if o could be moved, otherwise false.
	 */
	public boolean move(WObject o, double x, double y) {
		
		if (!isValidPosition(o, x, y))		
			return false;		
		o.setX(x);
		o.setY(y);
		return true;
	}
	
	
	/**
	 * Adds o to the world iff it's position is valid.
	 * @param o
	 * @return true if o could be added, otherwise false.
	 */
	public boolean add(WObject o) {
		if (!isValidPosition(o, o.getX(), o.getY()))		
			return false;
		objects.add(o);
		return true;
	}
	
	
	/**
	 * Checks if the object could exist in the world at the specified location. Note: the specified x and y are used, not o's values.
	 * @param o
	 * @param x
	 * @param y
	 * @return true if o could exist in the world at the specified position, otherwise false.
	 */
	protected boolean isValidPosition(WObject o, double x, double y) {		
		// check world bounds
		if (x - o.getRadius() < 0)
			return false;
		if (y - o.getRadius() < 0)
			return false;
		if (x + o.getRadius() > width)
			return false;
		if (y + o.getRadius() > height)
			return false;

		// check for intersection with other objects
		for (WObject w : objects) {
			if (w == o)
				continue;
			double deltaX = x - w.getX();
			double deltaY = y - w.getY();
			double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if (distance < (w.getRadius() + o.getRadius()))
				return false;
		}
				
		return true;
	}

	
	/**
	 * Gets the width of the World.
	 * @return
	 */
	public double getWidth() {
		return width;
	}


	/**
	 * Gets the height of the World.
	 * @return
	 */
	public double getHeight() {
		return height;
	}


	/**
	 * Gets objects in the World. Don't modify this, use add and move methods instead.
	 * @return
	 */
	public List<WObject> getObjects() {
		return objects;
	}
}
