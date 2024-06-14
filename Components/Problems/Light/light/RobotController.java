package light;

import java.util.ArrayList;

import core.AbstractRepresentation;


public class RobotController {
	
	protected Robot robot;
	private AbstractRepresentation network;
	
	private ArrayList<Float> input = new ArrayList<Float>();
	private ArrayList<Float> output = new ArrayList<Float>();
	
	double[] sensorvalues;

	public RobotController (AbstractRepresentation rep, Light parent) {
		this.network = rep;
		rep.reset();
	}
	
	/** Sets the controller to the pre-info stage (before receiving sensor data) */
	public void preInfo() {
		//reset values here if necessary
		sensorvalues = new double[]{0,0};
	}
	
	/** The behavior of the drone */
	public void postInfo() {
		input.clear();
		//add sensor values here
		input.add((float)(1-sensorvalues[0]/Robot.LIGHT_SENSOR_RANGE) );
		input.add((float)(1-sensorvalues[1]/Robot.LIGHT_SENSOR_RANGE));		
		//calculate network output
		output = network.getOutput(input);
		//use output for control
		this.robot.setSpeed(output.get(0),output.get(1));
	}
	
	public void setRobot(Robot r) {
		this.robot = r;
	}
	
	public void setSensorValues(double[] svalues) {
		sensorvalues = svalues;
	}
}
