package components.simsoccer.model;

import java.awt.geom.Point2D;

import net.jodk.lang.FastMath;


public class Evaluator {

	private final Point2D.Double [] ControlPoints = new Point2D.Double[63];
	private int [] scoreTeam = new int [2];
	private int [] balldistscoreTeam = new int [2];
        
	public Evaluator() {
		int all = 0;
		for (int row = 0;row<7;row++) {
			for (int col = 0;col<9;col++) {
				ControlPoints[all] = new Point2D.Double();
				ControlPoints[all].x = -40 + (col*10);
				ControlPoints[all].y =  30 - (row*10);
				all++;
			}
		}
	}
	
	/**
	 * Returns the scores, must invoke calculate first!
	 * @param team 0: left, 1: right
	 * @return
	 */
	public int getScore(int team) {
		return scoreTeam[team];
	}
	
	public int getBalldistScore (int team) {
		return balldistscoreTeam[team];
	}
	public void calculate(infoObject object) {
            scoreTeam[0] = 0;
            scoreTeam[1] = 0;
            balldistscoreTeam [0] = 0;
            balldistscoreTeam [1] = 0;
            //calculate points according to allocation
            for (int i=0; i<63; i++) {
            	calculateScore (ControlPoints[i], object);
            }
            //calculate points according
            calculateBalldistScore(object); 
	}

	private void calculateBalldistScore(infoObject object) {
		if (minimumballdist(0,object) < minimumballdist(1,object)) balldistscoreTeam[0]++;
		else if (minimumballdist(0,object) > minimumballdist(1,object)) balldistscoreTeam[1]++;
	}
	
	private double minimumballdist (int team, infoObject object) {
		double d1 = distance(object.team[team][0],object.ball);
		double d2;
		for (int i = 1;i<11;i++) {
            d2 = distance(object.team[team][i],object.ball);
            if (d2 < d1) d1 = d2;
        }
		return d1;
	}
	
    private void calculateScore(Point2D.Double cpoint, infoObject object) {
        if ((minimumdistance (0,cpoint,object)) < (minimumdistance (1,cpoint,object))) scoreTeam[0]++;
        else if ((minimumdistance (0,cpoint,object)) > (minimumdistance (1,cpoint,object))) scoreTeam[1]++; 
    }
    
    private double minimumdistance (int team, Point2D.Double cpoint, infoObject object) {
            double d1 = distance(object.team[team][0],cpoint);
            double d2;
            for (int i = 1;i<11;i++) {
                d2 = distance(object.team[team][i],cpoint);
                if (d2 < d1) d1 = d2;
            }
            return d1;
    }
    public double distance (Point2D.Double p1, Point2D.Double p2) {
        return FastMath.hypot(p2.x-p1.x, p2.y-p1.y);
    }
}
