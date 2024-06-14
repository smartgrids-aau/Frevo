package pong;

import java.util.ArrayList;

import core.AbstractRepresentation;

/**
 * The main objective of this class is to calculate the next step of the robot. 
 * 
 * @author Sergii Zhevzhyk
 */
public class EvolvedController extends Controller {
	
	/**
	 * The "brain" of the robot. Usually the controller is implemented as neural network. 
	 */
	private AbstractRepresentation representation;
	
	/**
	 * The controller of the robot which coordinates its movements.
	 * @param representation the representation of the controller (e.g. neural network).
	 */
	public EvolvedController(AbstractRepresentation representation) {
		this.representation = representation;
	}

	
	/**
	 * The decision step where controller should say what to do next.
	 */
	@Override
	public void process() {
		ArrayList<Float> inputs = new ArrayList<Float>();
		ArrayList<Float> outputs = new ArrayList<Float>();
		representation.reset();	

		// add inputs
		inputs.addAll(this.robot.getSensorPosition());
		inputs.addAll(this.robot.getSensorTeammate());
		inputs.addAll(this.robot.getSensorBall());
		
		outputs = representation.getOutput(inputs);
		
		float speed = (outputs.get(0) - 0.5f) * 2;
		if (robot.getTeam() == 0){
			if (speed >= 0){
				this.robot.setUp(speed);
				this.robot.setDown(0);
			} else {
				this.robot.setDown(-speed);
				this.robot.setUp(0);
			}			
		} else {
			if (speed < 0){
				this.robot.setUp(-speed);
				this.robot.setDown(0);
			} else {
				this.robot.setDown(speed);
				this.robot.setUp(0);
			}			
		}
	}
}
