package singlePong;

/**
 * Base class for the controller of the robot.  
 *   
 * @author Sergii Zhevzhyk
 *
 */
public abstract class Controller {
	protected Paddle robot;
	protected SinglePongParameters parameters;
	protected SinglePongState state;
	
	/** Sets the robot of this controller */
	public final void setRobot(Paddle robot) {
		this.robot = robot;
	}
	
	public void setParameters(SinglePongParameters parameters) {
		this.parameters = parameters;
	}
	
	public void setState(SinglePongState state) {
		this.state = state;
	}
	
	public boolean isDebugging() {
		if (parameters == null || state == null) {
			return false;
		}
		return parameters.isDebugging();
	}
		
	/** Processes the visual information and generates the next intention */
	public abstract void process();
}
