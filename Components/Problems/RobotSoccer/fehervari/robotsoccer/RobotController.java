package fehervari.robotsoccer;

public abstract class RobotController {

	protected SoccerRobot robot;
	
	/** Sets the robot of this controller */
	public final void setRobot(SoccerRobot soccerRobot) {
		this.robot = soccerRobot;
	}
	
	/** Processes the visual information and generates the next intention */
	public abstract void process();

}
