package fehervari.robotsoccer;

import java.util.ArrayList;

import core.AbstractRepresentation;

public class EvolvedController extends RobotController {

	ArrayList<Float> inputs = new ArrayList<Float>(
			5 * SoccerRobot.CAMERA_RESOLUTION);
	ArrayList<Float> outputs = new ArrayList<Float>(4);

	/** Representation used by this controller */
	private AbstractRepresentation representation;

	public EvolvedController(AbstractRepresentation representation) {
		this.representation = representation;
	}

	public void process() {

		// clear cache
		inputs.clear();
		outputs.clear();

		// add inputs
		inputs.addAll(this.robot.getSensorValuesWall());
		inputs.addAll(this.robot.getSensorValuesTeamMates());
		inputs.addAll(this.robot.getSensorValuesOpponents());
		inputs.addAll(this.robot.getSensorValuesBall());
		inputs.addAll(this.robot.getSensorValuesGoal());

		outputs = representation.getOutput(inputs);

		if (robot.canKick()) {
			// re-scale kick direction
			double kickdirection = outputs.get(3) * 2.0 - 1.0;
			robot.kick(outputs.get(2), kickdirection);
		} else {
			// set motor speed
			this.robot.setSpeed(outputs.get(0), outputs.get(1));
		}

	}
}
