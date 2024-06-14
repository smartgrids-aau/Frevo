package spiderinosim;


/**
 * Provides RayTracer for a World.
 */
public class RayTracer {

	static final double RADIANS_PER_DEGREE = Math.PI / 180;
	
	protected World world;

	protected int rayCount;

	// cached values

	protected double x0;
	protected double y0;
	protected double dX;
	protected double dY;

	protected WObject source;
	protected double angle;

	// results

	protected WObject hit;
	protected double distance;

	public RayTracer(World world) {
		this.world = world;
		rayCount = 0;
	}


	/**
	 * Send a ray into the World and calculate results.
	 * @param source
	 * @param angle
	 */
	public void sendRay(WObject source, double angle) throws SimulationException {
		rayCount++;
		this.source = source;
		this.angle = angle;

		// ray vector, normalized
		dX = Math.cos(RADIANS_PER_DEGREE * angle);
		dY = Math.sin(RADIANS_PER_DEGREE * angle);

		// work out source point of ray, i.e. point of origin on source circle
		x0 = source.getX() + source.getRadius() * dX;
		y0 = source.getY() + source.getRadius() * dY;
		
		hit = null;
		distance = calculateDistanceToEdge();

		for(WObject target : world.getObjects()) {
			if (target == source)
				continue;
			testTarget(target);
		}		
	}
	
	
	private void testTarget(WObject target) throws SimulationException  {

		// target to source vector, normalized
		double fX = x0 - target.getX();
		double fY = y0 - target.getY();
				
		// solve quadratic equation from intersecting line with circle
		double a = dX * dX + dY * dY;
		double b = 2 * (fX * dX + fY * dY);
		double c = (fX * fX + fY * fY) - target.getRadius() * target.getRadius();
		double discriminant = b * b - 4 * a * c;

		// check for no intersection
		if (discriminant < 0)
			return;

		// calculate closest intersection
		discriminant = Math.sqrt(discriminant);
		double d = (-b - discriminant) / (2 * a);

		// check for actual hit
		if (d < 0)
			return;
		
		// update if closer than last object
		if (d < distance) {
			distance = d;
			hit = target;
		}
	}

	
	/**
	 * Calculates the distance to edge of the world.	 
	 * @return distance to the edge of the world.
	 */
	private double calculateDistanceToEdge() throws SimulationException {
		double width = world.getWidth();
		double height = world.getHeight();

		// assume intersection with only one edge is possible, test them in a sequence
		
		// top and bottom edges only possible if line is not horizontal
		if (dY != 0) {
			// top edge
			double t = y0 / -dY;
			double x = dX * t + x0;
			if ((t >= 0) && (x >= 0) && (x <= width))
					return t;
			
			// bottom edge
			t = (height - y0) / dY;
			x = dX * t + x0;
			if ((t >= 0) && (x >= 0) && (x <= width))
					return t;
		}
		
		// left and right edges only possible if line is not vertical
		if (dX != 0) {
			// left edge
			double t = x0 / -dX;
			double y = dY * t + y0;
			if ((t >= 0) && (y >= 0) && (y <= height))
				return t;
			
			// right edge
			t = (width - x0) / dX;
			y = dY * t + y0;
			if ((t > 0) && (y >= 0) && (y <= height))
				return t;
		}
		
		throw new SimulationException("failed to calculate distance to edge of the world");		
	}

	
	public double getDistance() {
		return distance;
	}

	
	public WObject getHit() {
		return hit;
	}
}