package fehervari.robotsoccer;

import java.awt.geom.Point2D;

import net.jodk.lang.FastMath;

public class Ball extends FieldObject {

	public Point2D.Double speedVector = new Point2D.Double(0, 0);
	public Point2D.Double accelerationVector = new Point2D.Double(0, 0);

	public static final double BALL_DECAY = 0.97;
	//public static final double BALL_ACC_MAX = 2.7;
	public static final double BALL_SPEED_MAX = 0.12;
	public static final double BALL_DIAMETER = 0.125;

	private SoccerServer server;

	public Ball(Point2D.Double pos, SoccerServer server) {
		setPosition(pos);
		this.server = server;
	}

	public Ball(SoccerServer server) {
		this(new Point2D.Double(0, 0), server);
	}

	public void applydecay() {
		double newx = speedVector.x * BALL_DECAY;
		double newy = speedVector.y * BALL_DECAY;

		if (accelerationVector.x == 0 && accelerationVector.y == 0) {
			// stop the ball from infinite drifting
			if ((((newx < 0.005) && (newx > 0))
					|| ((newx > -0.005) && (newx < 0))) && (((newy < 0.005) && (newy > 0))
							|| ((newy > -0.005) && (newy < 0)))) {
				newx = 0;
				server.evaluateBallDistance();
			}
		}
		this.speedVector = new Point2D.Double(newx, newy);
	}

	/**
	 * Adds the acceleration vector to the speedvector then normalizes it
	 * 
	 * @param accvect
	 */
	public void addAccVector(Point2D.Double accvect) {
		setAccelerationVector(accvect);
		Point2D.Double speedvector = new Point2D.Double(this.speedVector.x
				+ accvect.x, this.speedVector.y + accvect.y);
		// normalize
		double length = FastMath.hypot(speedvector.x, speedvector.y);
		if (length > BALL_SPEED_MAX) {
			double ratio = (BALL_SPEED_MAX / length);
			speedvector = new Point2D.Double(speedvector.x * ratio,
					speedvector.y * ratio);
		}
		this.speedVector = speedvector;
	}

	public Point2D.Double getSpeedVector() {
		return this.speedVector;
	}

	public void setSpeedVector(Point2D.Double speed) {
		this.speedVector = speed;
	}

	public Point2D.Double getAccelerationVector() {
		return this.accelerationVector;
	}

	public void setAccelerationVector(Point2D.Double acc) {
		this.accelerationVector = acc;
	}

	public double getDiameter() {
		return BALL_DIAMETER;
	}
}
