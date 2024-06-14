package pong;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a ball in the Pong game.
 * 
 * @author Sergii Zhevzhyk
 */
public class Ball extends FieldObject {
	/**
	 * Describes an interception between two lines.
	 * It also contains additional parameter that 
	 * shows direction of the ball. 
	 */
	public class InterceptionResult {
		// X coordinate of the interception
		public int x; 
		// Y coordinate of the interception
		public int y;
		// Direction of the ball
		public String direction;
	}
	
	/**
	 * Describes an acceleration of the ball.
	 */
	public class AccelerateResult {
		public double nx;
		public double ny;
		public int x;
		public int y;
		public double dx;
		public double dy;	
	}
	
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	
	private double speed;
	private double acceleration;	
	
	private double dx;
	private double dy;
	
	private int number;
	
	private ArrayList<Integer> prevX = new ArrayList<Integer>();
	private ArrayList<Integer> prevY = new ArrayList<Integer>();
	private int prevHistory = 3;
	
	public Ball(PongParameters parameters, int number)
	{
		super(parameters);
		this.number = number;
		this.minX = this.getHeight();
		this.maxX = parameters.getWidth() - this.getWidth();
		this.minY = parameters.getWallWidth() + this.getHeight();
		this.maxY = parameters.getHeight() - parameters.getWallWidth() - this.getHeight();
		this.speed = (this.maxX - this.minX) / parameters.getBallSpeed();
		this.acceleration = parameters.getBallAcceleration();
	}
	
	/* (non-Javadoc)
	 * @see pongNew.FieldObject#getWidth()
	 */
	@Override
	public int getWidth() {
		return getParameters().getBallRadius();
	}


	/* (non-Javadoc)
	 * @see pongNew.FieldObject#getHeight()
	 */
	@Override
	public int getHeight() {
		return getParameters().getBallRadius();
	}
	
	public int getNumber() {
		return this.number;
	}
	
	@Override
	public void setPosition(int x, int y)
	{
		prevX.add(getX());
		prevY.add(getY());
		if (prevX.size() > prevHistory) {
			prevX.remove(0);
			prevY.remove(0);
		}
		super.setPosition(x, y);
		setLeft(getX() - getWidth());
		setTop(getY() - getHeight());
		setRight(getX() + getWidth());
		setBottom(getY() + getHeight());		
	}
	
	public void setDirection(double dx, double dy) 
	{
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * Accelerates the speed of the ball in order to have some challenge
	 * @param dt time interval
	 * @return position of the ball for the next step
	 */
	private AccelerateResult accelerate(double dt)
	{
		 double x = getX();
		 double y = getY(); 
		 double x2  = x + (dt * dx) + (acceleration * dt * dt * 0.5);
		 double y2  = y + (dt * dy) + (acceleration * dt * dt * 0.5);
	     double dx2 = dx + (acceleration * dt) * (dx > 0 ? 1 : -1);
	     double dy2 = dy + (acceleration * dt) * (dy > 0 ? 1 : -1);
	     AccelerateResult results = new AccelerateResult();
	     results.nx = (x2-x);
	     results.ny = (y2-y);
	     results.x = (int)x2;
	     results.y = (int)y2;
	     results.dx = dx2;
	     results.dy = dy2;
	     return results;
	}
	
	/**
	 * Calculates an interception between two lines.    
	 * More information: http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect/565282#565282
	 * @param x1 coordinate X of the first point of a first line.
	 * @param y1 coordinate Y of the first point of a first line.
	 * @param x2 coordinate X of the second point of a first line.
	 * @param y2 coordinate Y of the second point of a first line.
	 * @param x3 coordinate X of the first point of a second line.
	 * @param y3 coordinate Y of the first point of a second line.
	 * @param x4 coordinate X of the second point of a second line.
	 * @param y4 coordinate Y of the second point of a second line.
	 * @param direction defines a direction of the ball.
	 * @return Returns a point of interception between two lines. Returns null when two lines don't intercept. 
	 */
	private InterceptionResult intercept(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, String direction)
	{
		double denom = ((y4-y3) * (x2-x1)) - ((x4-x3) * (y2-y1));
	    if (denom != 0) 
	    {
	    	double ua = (((x4-x3) * (y1-y3)) - ((y4-y3) * (x1-x3))) / denom;
	        if ((ua >= 0) && (ua <= 1)) 
	        {
	        	double ub = (((x2-x1) * (y1-y3)) - ((y2-y1) * (x1-x3))) / denom;
	        	if ((ub >= 0) && (ub <= 1)) {
	        		double x = x1 + (ua * (x2-x1));
	        		double y = y1 + (ua * (y2-y1));
	        		InterceptionResult result = new InterceptionResult();
	        		result.x = (int)x;
	        		result.y = (int)y;
	        		result.direction = direction;
	        		return result;
	        	}
	        }
	    }
	    return null;		
	}
	
	/**
	 * Checks all possible interceptions between the ball and the paddle.
	 * @param paddle given paddle.
	 * @param nx difference between new and old positions of the ball.
	 * @param ny difference between new and old positions of the ball.
	 * @return parameters of possible interception.
	 */
	private InterceptionResult ballIntercept (Paddle paddle, double nx, double ny)
	{
		InterceptionResult pt = null;
	    if (nx < 0) 
	    {
	    	pt = intercept(getX(), getY(), getX() + nx, getY() + ny,
	    			paddle.getRight()  + getWidth(), 
                    paddle.getTop()    - getHeight(), 
                    paddle.getRight()  + getWidth(), 
                    paddle.getBottom() + getHeight(),    			
	                "right");
	    }
	    else if (nx > 0) 
	    {
	    	pt = intercept(getX(), getY(), getX() + nx, getY() + ny,
	    			paddle.getLeft()   - getWidth(), 
	                paddle.getTop()    - getHeight(), 
	                paddle.getLeft()   - getWidth(), 
	                paddle.getBottom() + getHeight(),
	                "left");
	    }
	    if (pt == null) 
	    {
	    	if (ny < 0) 
	    	{
	    		pt = intercept(getX(), getY(), getX() + nx, getY() + ny,
	    			paddle.getLeft()   - getWidth(), 
		            paddle.getBottom() + getHeight(), 
		            paddle.getRight()  + getWidth(), 
		            paddle.getBottom() + getHeight(),
	                "bottom");
	        }
	        else if (ny > 0) 
	        {
	        	pt = intercept(getX(), getY(), getX() + nx, getY() + ny,
	        		paddle.getLeft()   - getWidth(), 
		            paddle.getTop()    - getHeight(), 
		            paddle.getRight()  + getWidth(), 
		            paddle.getTop()    - getHeight(),
	                "top");
	        }
	    }
	    return pt;	
	}
	
	/**
	 * Calculates a position of the ball for the next step
	 * @param dt time interval
	 * @param state current state of the simulation 
	 */
	public void update(double dt, PongState state)
	{
		AccelerateResult pos = accelerate(dt);
		if ((pos.dy > 0) && (pos.y > this.maxY)) 
		{
			pos.y = this.maxY;
		    pos.dy = -pos.dy;
		}
		else if ((pos.dy < 0) && (pos.y < this.minY)) 
		{
			pos.y = this.minY;
		    pos.dy = -pos.dy;
		}

		
		for (int i=0; i<state.getFirstTeam().length; i++)
		{
			Paddle leftPaddle =  state.getFirstTeam()[i];
			Paddle rightPaddle = state.getSecondTeam()[i];
			
			Paddle paddle = (pos.dx < 0) ? leftPaddle : rightPaddle;
		    InterceptionResult pt = ballIntercept(paddle, pos.nx, pos.ny);
		    
		    if (pt != null) 
		    {
		    	switch(pt.direction) 
		    	{
		          case "left":
		          case "right":
		            pos.x = pt.x;
		            pos.dx = -pos.dx;
		            break;
		          case "top":
		          case "bottom":
		            pos.y = pt.y;
		            pos.dy = -pos.dy;
		            break;
		        }
	
		        // add/remove spin based on paddle direction
		        if (paddle.getUp() != 0)
		        {
		          pos.dy = pos.dy * (pos.dy < 0 ? 0.5 : 1.5);
		        }
		        else if (paddle.getDown() != 0)
		        {
		          pos.dy = pos.dy * (pos.dy > 0 ? 0.5 : 1.5);
		        }
		        break;		        
		    }
		}
	    this.setPosition(pos.x,  pos.y);
	    this.setDirection(pos.dx, pos.dy);	    
	}

	/**
	 * Resets the position and the direction of a ball.
	 * The position is defined based on a number of the ball.
	 */
	public void reset(int team) {
		int halfDistance = (this.maxX - this.minX)/2;
		// Calculate an Y position of the ball. 
		// Random generator is used in order to make the game more interesting
		// and exclude bias. 
		int y = minY + getParameters().getRandom().nextInt((int)(this.maxY - this.minY));
		if (this.number == 0)
		{
			this.setPosition(team == 1 ? this.maxX : this.minX, y);
		    this.setDirection(team == 1 ? -this.speed : this.speed, this.speed);
		}
		else if (this.number == 1)
		{
			this.setPosition(team == 1 ? this.minX : this.maxX , y);
		    this.setDirection(team == 1 ? this.speed : -this.speed, this.speed);
		}
		else if (this.number == 2)
		{			
			this.setPosition(this.minX + halfDistance, y);
		    this.setDirection(team == 1 ? -this.speed : this.speed, this.speed);
		}
		else if (this.number == 3)
		{
			this.setPosition(this.minX + halfDistance, y);
		    this.setDirection(team == 1 ? this.speed : -this.speed, this.speed);
		}
		else if (this.number == 4) 
		{
			this.setPosition(team == 1? this.minX + halfDistance/2 :  this.maxX - halfDistance/2, y);
		    this.setDirection(team == 1 ? this.speed : -this.speed, this.speed);			
		}
		else if (this.number == 5) 
		{
			this.setPosition(team == 1? this.maxX - halfDistance/2 : this.minX + halfDistance/2, y);
		    this.setDirection(team == 1 ? -this.speed : this.speed, this.speed);
		}
		// Clear a history of previous positions.
	    prevX.clear();
	    prevY.clear();
	}
	
	public List<Integer> getPrevX() {
		return this.prevX;
	}

	public List<Integer> getPrevY() {
		return this.prevY;
	}
}
