package fehervari.robotsoccer;

import java.awt.geom.Point2D;

public class Goal extends FieldObject {
	
	public int team;
	public Point2D bottompoint;
	public Point2D toppoint;
	
	public Goal(int team) {
		this.team=team;
		if (team == 0) {
			//left goal
			setPosition(new Point2D.Double(-SoccerServer.FIELD_WIDTH/2, 0));
			toppoint = new Point2D.Double(-SoccerServer.FIELD_WIDTH/2, SoccerServer.GOAL_WIDTH/2);
			bottompoint = new Point2D.Double(-SoccerServer.FIELD_WIDTH/2, -SoccerServer.GOAL_WIDTH/2);
		} else if (team == 1) {
			//right goal
			setPosition(new Point2D.Double(SoccerServer.FIELD_WIDTH/2, 0));
			toppoint = new Point2D.Double(-SoccerServer.FIELD_WIDTH/2, SoccerServer.GOAL_WIDTH/2);
			bottompoint = new Point2D.Double(-SoccerServer.FIELD_WIDTH/2, -SoccerServer.GOAL_WIDTH/2);
		} else {
			System.err.println("ERROR: Wrong argument provided while creating goal object. Please use 0 for left, 1 for right instead of "+team);
		}
		
	}
	@Override
	public double getDiameter() {
		return SoccerServer.GOAL_WIDTH;
	}

}
