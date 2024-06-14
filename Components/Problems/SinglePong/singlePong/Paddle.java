package singlePong;

import java.util.List;

public class Paddle extends FieldObject {
	
	private int minY;
	private int maxY;
	private double speed;
	
	private double up;
	private double down;
	
	private Controller controller;
	private int number;
		
	private List<Float> sensorPosition;
	private List<Float> sensorTeammate;
	private List<Float> sensorBall;
	
	public Paddle(SinglePongParameters parameters, Controller controller, int number) 
	{		
		super(parameters);
				
		this.number = number;
		this.controller = controller;
		
		controller.setRobot(this);
		
		this.minY = parameters.getWallWidth();
		this.maxY = parameters.getHeight() - parameters.getWallWidth() - this.getHeight();
		this.speed = (maxY - minY) / parameters.getPaddleSpeed();
	}
	
	/* (non-Javadoc)
	 * @see pongNew.FieldObject#getWidth()
	 */
	@Override
	public int getWidth() {
		return super.getParameters().getPaddleWidth();
	}

	/* (non-Javadoc)
	 * @see pongNew.FieldObject#getHeight()
	 */
	@Override
	public int getHeight() {
		return super.getParameters().getPaddleHeight();
	}
	
	/* (non-Javadoc)
	 * @see pongNew.FieldObject#setPosition(int, int)
	 */
	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
	    setLeft(getX());
	    setRight(getLeft() + getWidth());
	    setTop(getY());
	    setBottom(getY() + getHeight());
	}
	
	public Controller getController() {
		return controller;
	}

	public int getNumber() {
		return number;
	}

	public double getUp() {
		return up;
	}
	public void setUp(double up) {
		this.up = up;
	}

	public double getDown() {
		return down;
	}
	public void setDown(double down) {
		this.down = down;
	}
	
	public int getCentralX() {
		return (getLeft() + getRight())/2;
	}
	public int getCentralY() {
		return (getBottom() + getTop())/2;
	}

	public List<Float> getSensorPosition() {
		return sensorPosition;
	}
	public void setSensorPosition(List<Float> sensorPosition) {
		this.sensorPosition = sensorPosition;
	}

	public List<Float> getSensorTeammate() {
		return sensorTeammate;
	}
	public void setSensorTeammate(List<Float> sensorTeammate) {
		this.sensorTeammate = sensorTeammate;
	}

	public List<Float> getSensorBall() {
		return sensorBall;
	}
	public void setSensorBall(List<Float> sensorBall) {
		this.sensorBall = sensorBall;
	}

	public void setDirection(double dy)
	{
		this.up = dy < 0 ? -dy : 0;
		this.down = dy > 0 ? dy : 0;
	}
	
	public void update(double dt)
	{
		double amount = this.down - this.up;
	    if (amount != 0) 
	    {
	    	double newY = getY() + (amount * dt * this.speed);
	        if (newY < this.minY)
	        {
	        	newY = this.minY;
	        }
	        else if (newY > this.maxY)
	        {
	        	newY = this.maxY;
	        }
	        this.setPosition(this.getX(), (int)newY);
	    }	
	}
}