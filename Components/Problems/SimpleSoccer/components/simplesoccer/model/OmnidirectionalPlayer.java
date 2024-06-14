package components.simplesoccer.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jodk.lang.FastMath;

import components.simplesoccer.SimpleServer;
import components.simplesoccer.SimpleSoccer;

import core.AbstractRepresentation;

/**
 * The controller of a player, basically this class contains the behavior of the
 * soccer player controlled by the passed representation component. The player
 * will make decision based on all players, distance to goal, distance to ball.
 */
public class OmnidirectionalPlayer extends NearestInfoPlayer {

	protected List<SimPlayer> teammates;
	protected List<SimPlayer> opponents;

	/** Number of sections used */
	public final static int SECTIONS = 6;

	@SuppressWarnings("unchecked")
	private ArrayList<SimPlayer>[] teammate_sections = new ArrayList[SECTIONS];
	@SuppressWarnings("unchecked")
	private ArrayList<SimPlayer>[] opponent_sections = new ArrayList[SECTIONS];

	public OmnidirectionalPlayer(AbstractRepresentation net, SimpleSoccer master) {
		super(net, master);

		for (int i = 0; i < teammate_sections.length; i++) {
			teammate_sections[i] = new ArrayList<SimPlayer>();
			opponent_sections[i] = new ArrayList<SimPlayer>();
		}
	}

	/** Controls the client by interpreting the state of the controller. */
	public void postInfo() {
		input.clear();
		output.clear();

		net.reset(); // TODO reset nn STRANGE: somehow reset helps in evolution

		// Input neurons for ball (4)
		double balldist = SimpleServer.getLength(relativeBallPos);
		addInput(balldist, relativeBallPos);

		// Input neurons for lines (4)
		input.add((float) (1 / (1 + border_top)));
		input.add((float) (1 / (1 + border_bottom)));
		input.add((float) (1 / (1 + border_left)));
		input.add((float) (1 / (1 + border_right)));

		if (getPlayer().getSide() == 1) {
			// team is playing from right-to-left side,
			// input needs to be switched

			for (int i = 0; i < input.size(); i += 2) {
				float h;
				h = input.get(i);
				input.set(i, input.get(i + 1));
				input.set(i + 1, h);
			}
		}
		
		// Input for goal (1)
		input.add((float) (1 / (1 + SimpleServer.getLength(relativeOppGoalPos))));

		// add inputs for teammates, opponents

		// teammates and opponents together

		for (int sec = 0; sec < SECTIONS; sec++) {
			// erase the respective group
			teammate_sections[sec].clear();
			opponent_sections[sec].clear();

		}

		SimPlayer player = (SimPlayer) this.getPlayer();

		// iterate through all teammates/opponents and categorize them based on
		// angle

		Iterator<SimPlayer> tm_it = teammates.iterator();
		Iterator<SimPlayer> op_it = opponents.iterator();

		/** size of one section */
		double sect = Math.PI * 2 / (double) SECTIONS;

		// teammates
		while (tm_it.hasNext()) {
			SimPlayer tm = tm_it.next();

			// vector
			Point2D.Double vector = new Point2D.Double(tm.position.x
					- player.position.x, tm.position.y - player.position.y);
			double L = vector.distance(0, 0);
			// determine angle to x axis (1,0)
			double angle = FastMath.acos(vector.x / L);

			if (vector.y < 0)
				angle = (Math.PI + Math.PI) - angle;

			// System.out.println(FastMath.toDegrees(angle)
			// +" "+tm.position+" ("+tm.getNumber()+")");
			for (int i = 1; i <= SECTIONS; i++) {
				if (angle <= i * sect) {
					teammate_sections[i - 1].add(tm);
					// System.out.println (tm.getNumber()
					// +" is added to section "+(i-1));
					break;
				}
			}
		}

		// opponents
		while (op_it.hasNext()) {
			SimPlayer opp = op_it.next();

			// vector
			Point2D.Double vector = new Point2D.Double(opp.position.x
					- player.position.x, opp.position.y - player.position.y);
			double L = vector.distance(0, 0);
			// determine angle to x axis (1,0)
			double angle = FastMath.acos(vector.x / L);

			if (vector.y < 0)
				angle = (Math.PI + Math.PI) - angle;

			// System.out.println(FastMath.toDegrees(angle)
			// +" "+tm.position+" ("+tm.getNumber()+")");
			for (int i = 1; i <= SECTIONS; i++) {
				if (angle <= i * sect) {
					opponent_sections[i - 1].add(opp);
					// System.out.println (tm.getNumber()
					// +" is added to section "+(i-1));
					break;
				}
			}
		}

		// iterate categories to form inputs
		float[] t_inputs = new float[SECTIONS];
		float[] o_inputs = new float[SECTIONS];

		for (int i = 0; i < SECTIONS; i++) {
			t_inputs[i] = 0f;
			o_inputs[i] = 0f;
			for (int k = 0; k < teammate_sections[i].size(); k++) {
				// higher input for closer
				double distance = teammate_sections[i].get(k).position
						.distance(player.position);

				if (distance < 0.7)
					distance = 0.7;

				t_inputs[i] += 0.7 / (distance * distance);

			}

			for (int k = 0; k < opponent_sections[i].size(); k++) {
				// higher input for closer
				double distance = opponent_sections[i].get(k).position
						.distance(player.position);

				if (distance < 0.7)
					distance = 0.7;

				o_inputs[i] += 0.7 / (distance * distance);

			}
		}

		// add inputs (SECTIONS * 2) mirror when needed

		if (getPlayer().getSide() == 0) {
			for (int t = 0; t < t_inputs.length; t++)
				input.add((float) t_inputs[t]);

			for (int t = 0; t < o_inputs.length; t++)
				input.add((float) o_inputs[t]);
		} else {
			for (int t = t_inputs.length - 1; t >= 0; t--) {
				input.add((float) t_inputs[t]);
			}
			for (int t = o_inputs.length - 1; t >= 0; t--) {
				input.add((float) o_inputs[t]);
			}
		}

		if (SimpleSoccer.APPLY_STAMINA_MODEL) {
			addStaminaInput();
		}

		// calculate outputs
		output = net.getOutput(input);

		final boolean useCartesianOutput = SimpleSoccer.CARTESIANOUTPUT;

		// get ball speed
		double ballspeed = SimpleServer
				.getLength(this.getSession().simpleserver.ball.speedVector);

		if (useCartesianOutput) // output is given in cartesian form
		{
			double direction, power;

			double x[] = new double[4];
			for (int i = 0; i < 4; i++) {
				// scale up values to [-1.0,1.0]
				x[i] = output.get(i) * 2.0 - 1.0;

				if (getPlayer().getSide() == 1) {
					// team is playing from right-to-left side,
					// input needs to be switched
					x[i] = -x[i];
				}
			}

			if ((balldist < 0.7) && (ballspeed < 1.5)) { // we can kick the ball
				direction = FastMath.atan2(x[2], x[3]) * 180.0 / Math.PI;
				power = 100 * FastMath.sqrt(x[2] * x[2] + x[3] * x[3]);
				getPlayer().kick((int) power, direction); // scale to -1..0..1
			} else {
				direction = FastMath.atan2(x[0], x[1]) * 180.0 / Math.PI;
				power = 100 * FastMath.hypot(x[0], x[1]);
				getPlayer().dashto((int) power, direction);
			}
		} else {
			double direction;
			// output is given in polar coordinate form
			// this is more difficult for the NN
			if ((balldist < 0.7) && (ballspeed < 1.5)) { // we can kick the ball
				direction = output.get(3) * 360;
				if (getPlayer().getSide() == 1)
					direction += 180;
				getPlayer().kick((int) (output.get(2) * 100), direction); // scale
																			// to
																			// -1..0..1
			} else {
				direction = output.get(1) * 360;
				if (getPlayer().getSide() == 1)
					direction += 180;

				getPlayer().dashto((int) (output.get(0) * 100), direction);
			}
		}

	}

	public void setTeamPlayers(List<SimPlayer> teammates) {
		this.teammates = teammates;
	}

	public void setOpponents(List<SimPlayer> opponents) {
		this.opponents = opponents;
	}
}
