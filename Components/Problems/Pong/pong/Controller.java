package pong;


public abstract class Controller {
	protected Paddle robot;
	protected PongParameters parameters;
	protected PongState state;
	
	/** Sets the robot of this controller */
	public final void setRobot(Paddle robot) {
		this.robot = robot;
	}
	
	public void setParameters(PongParameters parameters) {
		this.parameters = parameters;
	}
	
	public void setState(PongState state) {
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
