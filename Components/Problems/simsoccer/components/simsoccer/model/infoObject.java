package components.simsoccer.model;

import java.awt.geom.Point2D;

public class infoObject {
	
	/**
	 * This class contains the parsed information, used for the coach
	 */
	public Point2D.Double [] goals = new Point2D.Double[2]; //0: right, 1: left
	public Point2D.Double ball;
	public Point2D.Double [][] team = new Point2D.Double[2][11];
	public String teamname1;
    public String teamname2;
        
    public infoObject() {
    this("","");	
    }
    
    public infoObject(String teamname1, String teamname2)
	{
        this.teamname1 = teamname1;
        this.teamname2 = teamname2;
        goals [0] = new Point2D.Double(); 
        goals [1] = new Point2D.Double();
        ball = new Point2D.Double();
        
        for (int i = 0; i<11; i++) {
		team[0][i] = new Point2D.Double();
		team[1][i] = new Point2D.Double();
        }

	}
    /**
     * @param team 0 or 1
     * @param player 0..10
     * @param x
     * @param y
     */
    public void setPlayer (int team, int player, double x, double y) { 
        this.team[team][player].x = x;
        this.team[team][player].y = y;
    }
    public void setBall (double x, double y) {
    	this.ball.x = x;
    	this.ball.y = y;
    }
/**
 * Sets the goal position
 * NOTE: side 0: right goal, side 1 left goal
 **/
    public void setGoal (int side, double x, double y) {
        this.goals[side].x = x;
        this.goals[side].y = y;
    
    }
}
