package spiderinosim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main simulation class.
 */
public class Simulation {

	/**
	 * Spiderino commands.
	 */
	public enum Command {
		DoNothing,
		GoForward,
		GoBackward,
		TurnLeft,
		TurnRight			
	}
	
	static final double RADIANS_PER_DEGREE = Math.PI / 180;

	// Simulation parameters
	static final int TIMEOUT = 100;
		
	static final double SENSOR_SPREAD_ANGLE = 60;
	static final int SENSOR_SPREAD_COUNT = 19;
	
	static final double SENSOR_ANGLE_OFFSET = 45;
	
	protected Cny70Sensor cny70Sensor;
	protected Random random;
	protected SimulationProperties properties;
	protected World world;
	protected List<Spiderino> spiderinos;
	protected RayTracer rayTracer;
	protected double spiderinoWalkStep;
	protected double spiderinoTurnStep;	
	
	
	/**
	 * Creates a Simulation with the specified properties.
	 * @param properties
	 * @throws SimulationException
	 */
	public Simulation(SimulationProperties properties, Random random) throws SimulationException {
		this.random = random;
		this.properties = properties;
		cny70Sensor = new Cny70Sensor(); 
		world = new World(properties.getWorldWidth(), properties.getWorldHeight());
		rayTracer = new RayTracer(world);
		spiderinos = new ArrayList<Spiderino>();		
		spiderinoWalkStep = properties.getSpiderinoWalkSpeed() / (1000 / properties.getStepTime());
		spiderinoTurnStep = properties.getSpiderinoTurnSpeed() / (1000 / properties.getStepTime());
		
		populateWorld();
							
	}
	
	
	/**
	 * Adds Light and Spiderinos to the World. Here the light is placed at the specified position and the Spiderinos are placed randomly.
	 * @throws SimulationException
	 */
	protected void populateWorld() throws SimulationException {
		
		
		double lightRadius = properties.getLightRadius();
		int lightCount = properties.getLightCount();
		
		if (lightCount == -1) {
			// add single light to center of the world
			world.add(new Light(world.getWidth() / 2, world.getHeight() / 2, lightRadius));
		} else {
			// add Lights to world at random locations
			for (int id = 0; id < lightCount; id++) {
				Light light = new Light( 0, 0, lightRadius);
				
				// add a Light, keep trying until success or timeout
				for (int i = 0; i < TIMEOUT; i++) {				
					// ensure it is not too close to borders
					light.setX(getRandom(lightRadius, world.getWidth() - lightRadius));
					light.setY(getRandom(lightRadius, world.getHeight() - lightRadius));
					
					if (world.add(light)) {
						light = null;
						break;
					}
				}
				
				if (light != null)
					throw new SimulationException("Failed to add Light to World before TIMEOUT occured");				
			}
		}
		
		// add Spiderinos to world at random locations
		double spiderinoRadius = properties.getSpiderinoRadius();
		for (int id = 0; id < properties.getSpiderinoCount(); id++) {
			Spiderino spiderino = new Spiderino(id, getRandom(0, 360), 0, 0, spiderinoRadius);
			spiderinos.add(spiderino);
			
			// add a spiderino, keep trying until success or timeout
			for (int i = 0; i < TIMEOUT; i++) {				
				// ensure it is not too close to borders
				spiderino.setX(getRandom(spiderinoRadius, world.getWidth() - spiderinoRadius));
				spiderino.setY(getRandom(spiderinoRadius, world.getHeight() - spiderinoRadius));
				
				if (world.add(spiderino)) {
					spiderino = null;
					break;
				}
			}
			
			if (spiderino != null)
				throw new SimulationException("Failed to add Spiderino to World before TIMEOUT occured");				
		}
	}

	/**
	 * Returns the distance to the closest spiderino robot
	 * @param me
	 * @return distance
	 * @throws SimulationException 
	 */
	public double getDistanceNearestRobot(Spiderino me) throws SimulationException{
		double mindist=Double.MAX_VALUE;
		for	(Spiderino spiderino : spiderinos) {
			double dist;
			if (spiderino==me)
				continue;
			dist = Math.pow(me.getX()-spiderino.getX(), 2)+Math.pow(me.getY()-spiderino.getY(), 2);
			if (dist < mindist)
				mindist=dist;
		}
		return Math.sqrt(mindist);
	}
	
	
	/**
	 * Calculates a fitness value (calculation depends on fitnessFunctionNumber)
	 * @param 
	 * @return fitness value
	 * @throws SimulationException 
	 */
	public double getFitness() throws SimulationException{	
		double worldDiagonal = Math.sqrt(world.getWidth() * world.getWidth() + world.getHeight() * world.getHeight());
		double fitness=0.0;
		
		switch (this.properties.fitnessFunctionNumber) {
		case 1:
			for	(Spiderino spiderino : spiderinos) {				
			
				// first step for fitness function: reward Spiderinos for getting close to a light
				// compute distance to closest Light
				double closestLightDistance = worldDiagonal;
				for	(WObject o : world.getObjects()) {
					if (o instanceof Light) {
						double dX = spiderino.getX() - o.getX();
						double dY = spiderino.getY() - o.getY();			
						double distance = Math.sqrt(dX*dX + dY*dY) - o.getRadius() - spiderino.getRadius();
	
						if (distance < 0)
							throw new SimulationException("Goal distance must be positive");
						
						if (distance < closestLightDistance) {
							closestLightDistance = distance;
						}
					}
				}
				
				double distanceFitness =  100 - Math.max(closestLightDistance - properties.getDistanceThreshold(), 0) / (worldDiagonal - properties.getDistanceThreshold()) * 100;
				double walkStepsFitness = 100 - (100.0 * spiderino.getWalkSteps() / properties.getMaximumSteps());
				double turnStepsFitness = 100 - (100.0 * spiderino.getTurnSteps() / properties.getMaximumSteps());
				
				fitness+=distanceFitness * properties.getDistanceFitnessWeight() + walkStepsFitness * properties.getWalkStepsFitnessWeight() + turnStepsFitness * properties.getTurnStepsFitnessWeight();
			}
			break;
		case 2:
			// we try to form a circle
			double centerOfWeightX=0.0,centerOfWeightY=0.0;
			double sumdistance=0.0,sumdistance2=0.0,sdev,avgdist;
			double mindistance2NextSpiderino=Double.MAX_VALUE;
			int n=spiderinos.size();
			for	(Spiderino spiderino : spiderinos) {
				centerOfWeightX += spiderino.getX();
				centerOfWeightY += spiderino.getY();
			}
			centerOfWeightX /= n;
			centerOfWeightY /= n;
			for	(Spiderino spiderino : spiderinos) {
				double dist2 = Math.pow(spiderino.getX()-centerOfWeightX,2) + Math.pow(spiderino.getY()-centerOfWeightY,2);
				sumdistance += Math.sqrt(dist2);
				sumdistance2 += dist2;
				double distNext=getDistanceNearestRobot(spiderino);
				if (distNext<mindistance2NextSpiderino)
					mindistance2NextSpiderino=distNext;
			}			
			sdev = Math.sqrt(1.0/(n-1) * (sumdistance2 - sumdistance*sumdistance/n));
			avgdist = sumdistance/n;
			
			double refRadialDistance=2*Math.PI*avgdist/n;
			fitness = avgdist/sdev * (mindistance2NextSpiderino/refRadialDistance);
			break;
		}

		return fitness;		
	}
	
	
	public boolean isSpiderinoAtGoal(Spiderino spiderino) throws SimulationException {
		
		for	(WObject o : world.getObjects()) {
			if (o instanceof Light) {
				double dX = spiderino.getX() - o.getX();
				double dY = spiderino.getY() - o.getY();			
				double distance = Math.sqrt(dX*dX + dY*dY) - o.getRadius() - spiderino.getRadius();

				if (distance < 0)
					throw new SimulationException("Goal distance must be positive");
				
				if (distance < properties.getGoalThreshold()) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Executes a single Spiderino command
	 * @param spiderino
	 * @param c
	 */
	public void executeCommand(Spiderino spiderino, Command c) {
		
		switch (c) {
		case DoNothing:			
			break;
			
		case GoForward:
			moveSpiderino(spiderino, true);
			spiderino.incrementWalkSteps();
			break;

		case GoBackward:			
			moveSpiderino(spiderino, false);
			spiderino.incrementWalkSteps();
			break;
			
		case TurnLeft:
			spiderino.setRotation(spiderino.getRotation() + spiderinoTurnStep);
			spiderino.incrementTurnSteps();
			break;
			
		case TurnRight:
			spiderino.setRotation(spiderino.getRotation() - spiderinoTurnStep);
			spiderino.incrementTurnSteps();
			break;
		} 
	}
	
	
	/**
	 * Moves a Spiderino forward or backward.
	 * @param spiderino
	 * @param forwards
	 */
	protected void moveSpiderino(Spiderino spiderino, boolean forwards) {
		double dX = Math.cos(spiderino.getRotation() * RADIANS_PER_DEGREE) * spiderinoWalkStep;
		double dY = Math.sin(spiderino.getRotation() * RADIANS_PER_DEGREE) * spiderinoWalkStep;
		if (forwards)
			world.move(spiderino, spiderino.getX() + dX, spiderino.getY() + dY);

		else
			world.move(spiderino, spiderino.getX() - dX , spiderino.getY() - dY);
	}
	
	
	/**
	 * Gets all 6 sensor values for a Spiderino.
	 * @param spiderino
	 * @return
	 * @throws SimulationException
	 */
	public ArrayList<Float> getSensorValues(Spiderino spiderino) throws SimulationException {
		ArrayList<Float> values = new ArrayList<Float>();
		
		values.add((float)readLightSensor(spiderino, 0));
		values.add((float)readProximitySensor(spiderino, 0));

		values.add((float)readLightSensor(spiderino, -SENSOR_ANGLE_OFFSET));
		values.add((float)readProximitySensor(spiderino, -SENSOR_ANGLE_OFFSET));

		values.add((float)readLightSensor(spiderino, SENSOR_ANGLE_OFFSET));
		values.add((float)readProximitySensor(spiderino, SENSOR_ANGLE_OFFSET));
		
		return values;
	}
	
	
	/**
	 * Reads light sensor value by sending several rays out into the world.
	 * @param spiderino
	 * @param angleOffset
	 * @return sensor value between 0 and 1.
	 * @throws SimulationException
	 */
	protected double readLightSensor(Spiderino spiderino, double angleOffset) throws SimulationException {
		double angle =  -SENSOR_SPREAD_ANGLE;
		double spreadDelta = SENSOR_SPREAD_ANGLE * 2 /  (SENSOR_SPREAD_COUNT - 1);
		double value = 1;
		for (int i = 0; i < SENSOR_SPREAD_COUNT; i++) {
			rayTracer.sendRay(spiderino, angle + spiderino.getRotation() + angleOffset);
			if (rayTracer.getHit() instanceof Light) {
				double v = (cny70Sensor.getAngleValue(angle) + 0.001) / cny70Sensor.getLightValue(rayTracer.getDistance());
				if (v < value)
					value = v;
			}
			angle += spreadDelta;
		}
		return value;
	}
	
	
	/**
	 * Reads proximity sensor value by sending a single ray out into the world.
	 * @param spiderino
	 * @param angleOffset
	 * @return sensor value between 0 and 1.
	 * @throws SimulationException
	 */
	protected double readProximitySensor(Spiderino spiderino, double angleOffset) throws SimulationException {
		double angle =  -SENSOR_SPREAD_ANGLE;
		double spreadDelta = SENSOR_SPREAD_ANGLE * 2 /  (SENSOR_SPREAD_COUNT - 1);
		double value = 1;
		for (int i = 0; i < SENSOR_SPREAD_COUNT; i++) {
			rayTracer.sendRay(spiderino, angle + spiderino.getRotation() + angleOffset);
			if (rayTracer.getHit() instanceof Light) {
				double v = (cny70Sensor.getAngleValue(angle) + 0.001) / cny70Sensor.getLightValue(rayTracer.getDistance());
				if (v < value)
					value = v;
			} else {
				double v = (cny70Sensor.getAngleValue(angle) + 0.001) / cny70Sensor.getObjectValue(rayTracer.getDistance());
				if (v < value)
					value = v;
			}	
			angle += spreadDelta;
		}
		return value;
	}
	
	
	/**
	 * Wrapper for random number generation.
	 * @param min
	 * @param max
	 * @return
	 */
	protected double getRandom(double min, double max) {
		return random.nextDouble() * (max - min) + min;
	}


	public List<Spiderino> getSpiderinos() {
		return spiderinos;
	}

	
	public Spiderino getSpiderino(int id) {
		return spiderinos.get(id);
	}
	
	public World getWorld() {
		return world;
	}
	
	
	public SimulationProperties getProperties() {
		return properties;
	}
	
}
