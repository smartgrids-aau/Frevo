package components.controllers;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import components.cdrones.Drone;
import components.cdrones.DronesServer;
import components.cdrones.cdrones;
import core.AbstractRepresentation;
import net.jodk.lang.FastMath;

/** Controller which uses only the relative position of the drones within range */
public class AdvancedEvolvedController extends DroneController {
	
	/** Direction change period */
	private final int BASESTEP = cdrones.BASETEP;
	private int stepper;
	private double direction;
	private ArrayList<Float> input = new ArrayList<Float>();
	private ArrayList<Float> output = new ArrayList<Float>();
	private double[] sensordata;
	
	public static final int SENSOR_FRONT = 0;
	public static final int SENSOR_RIGHT = 1;
	public static final int SENSOR_BACK = 2;
	public static final int SENSOR_LEFT = 3;
	
	//all the drones share the same controller for repeatability
	static Random generator;
	
	/** Contains a list of coordinates of the other drones */
	private ArrayList<Drone> dronesinrange;
	
	public AdvancedEvolvedController (AbstractRepresentation rep, cdrones parent) {
		this.representation = rep;
		generator = parent.getRandom();
	}

	@Override
	public void postInfo() {
		stepper++;
		if (stepper == BASESTEP) {
			stepper = 0;
			direction = calculateNewDirection();
		}
		else direction = 0;
	
		this.drone.dashto( 100 , DronesServer.fixangle(drone.getBodyDirection()+direction));
	}

	@Override
	public void preInfo() {
		//direction = this.drone.getBodyDirection();
	}
	
	public void addDronesInRange( ArrayList<Drone> drones) {
		this.dronesinrange = drones;
	}
	
	private double calculateNewDirection() {
		double direction = this.drone.getBodyDirection();
		//Cartesian approach
		input.clear();
		/*for (int i=0;i<dronesinrange.size();i++) {
			if (dronesinrange.get(i) != null) {
				double dx = this.drone.getPosition().x - dronesinrange.get(i).getPosition().x;
				double dy = this.drone.getPosition().y - dronesinrange.get(i).getPosition().y;
				
				
				double distance2 = dx*dx + dy*dy; 
				//rotate dx, dy by robot orientation
				Point2D.Double transco = DronesServer.getRelativeCoordinates(this.drone.getBodyDirection(),new Point2D.Double(dx,dy));
				
				input.add((float)(transco.x/distance2));
				input.add((float)(transco.y/distance2));
			}
			else {
				input.add(0f);
				input.add(0f);
			}
		}*/
		float left = 0;
		float right = 0;
		float back = 0;
		float front = 0;
		for (int i=0;i<dronesinrange.size();i++) {
			if (dronesinrange.get(i) != null) {
				double dx = this.drone.getPosition().x - dronesinrange.get(i).getPosition().x;
				double dy = this.drone.getPosition().y - dronesinrange.get(i).getPosition().y;
				Point2D.Double transco = DronesServer.getRelativeCoordinates(this.drone.getBodyDirection(),new Point2D.Double(dx,dy));
				double distance2 = dx*dx + dy*dy;
				//determine which sensor it picks up
				if (transco.x < 0) back += (transco.x/distance2);
				else front += (transco.x/distance2);
				
				if (transco.y < 0) right += (transco.x/distance2);
				else left += (transco.x/distance2);
			}			
		}
		input.add(front);
		input.add(back);
		input.add(left);
		input.add(right);
		
		//add sensory input
		for (int i=0;i<sensordata.length;i++) {
			if (sensordata[i] >= 0) {
				//input.add((float)(1-sensordata[i]/Drone.SENSOR_RANGE) );
				input.add((float)( 1/ FastMath.pow2(sensordata[i])));
			}
			else {
				input.add(0f);
			}
		}
		
		output.clear();		
		//output.setSize(2);
		output = representation.getOutput(input);
			
		//convert output from 0..1 to -1..1
		double dx = output.get(0)*2-1;
		double dy = output.get(1)*2-1;
		
		//convert coords back to absolute
		Point2D.Double transco = DronesServer.getRelativeCoordinates(0,new Point2D.Double(dx,dy));
		
		direction = Math.toDegrees(FastMath.atan2(transco.y, transco.x));
		
		return direction;
	}
	
	/** 
	 * prints a nicely formatted comma-separated vector of floats
	 * auxiliary function for debugging purposes
	 * @param data a vector of float values
	 */
	void printVector(Vector<Float> data) {
		DecimalFormat fm = new DecimalFormat("0.00");
		String sep="";
		for(int i=0; i<input.size(); i++) {
			System.out.print(sep+fm.format(input.get(i)));
			sep="; ";
		}	
	}	
	
	/** Returns true if the given field is within sensor range (cone) */
	public boolean sensorSeeField(int sensor, Point f) {
		boolean sees = false;
		
		double res = 1.0;
		//own position
		Point2D.Double pos = drone.getPosition();
		double dangle = drone.getBodyDirection();
		//field's center
		Point2D.Double fc = DronesServer.getFieldCenterfromField(f);
		
		//all sensors are placed in the middle of the drone perfectly covering the whole spectrums!!
		
		Point2D.Double sensorcenter = pos;//new Point2D.Double (pos.x,pos.y-Drone.DRONE_SIZE/2);
		double sensorangle = 0;
		
		switch (sensor) {
		case (SENSOR_FRONT): sensorangle = dangle; break;
		case (SENSOR_BACK): sensorangle = DronesServer.fixangle(dangle+180); break;
		case (SENSOR_RIGHT): sensorangle = DronesServer.fixangle(dangle-90); break;
		case (SENSOR_LEFT): sensorangle = DronesServer.fixangle(dangle+90); break;
		}
		
		Point2D.Double[] corners = new Point2D.Double[]{new Point2D.Double(fc.x-res/2,fc.y-res/2),
				new Point2D.Double(fc.x-res/2,fc.y+res/2),
				new Point2D.Double(fc.x+res/2,fc.y-res/2),
				new Point2D.Double(fc.x+res/2,fc.y+res/2)};
		
		for (int i=0;i<4;i++) {
			//if corner is in range and cone then...
			double cornerdistance = DronesServer.getDistance(sensorcenter,corners[i]);
			double dx = corners[i].x - sensorcenter.x;
			double dy = corners[i].y - sensorcenter.y;			
			double cornerangle = Math.toDegrees(Math.atan2(dy,dx));
			cornerangle = DronesServer.fixangle(cornerangle);
			
			if ((cornerdistance < Drone.SENSOR_RANGE) && (Math.abs(DronesServer.angleDiffDegrees(sensorangle,cornerangle)) < Drone.SENSOR_ANGLE/2) ){
				sees = true;
				break;
			}
		}	
		return sees;
	}
	
	public void addSensorData(double[] distancelist) {
		this.sensordata = distancelist;
	}

}
