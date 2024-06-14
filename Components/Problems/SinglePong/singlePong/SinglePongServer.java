package singlePong;

import net.jodk.lang.FastMath;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * The class which runs the simulation
 *   
 * @author Sergii Zhevzhyk
 */
public class SinglePongServer {

	private SinglePongParameters parameters;
	private SinglePongState state;
	private boolean isInterrupted;

	public SinglePongServer(SinglePongParameters parameters, SinglePongState state) {
		this.parameters = parameters;
		this.state = state;		
	}

	public void runSimulation(JPanel display) {
		int totalFitness = 0;
		
		for (int i=0; i < 3; i++)
		{
			// prepare the simulation for a new run
			state.resetScores();
			state.resetSimulation();
			
			int step = 0;
			while (state.isPlaying()) {
				if (isInterrupted) {
					break;
				}
	
				state.setActualStep(step);
				
				double dt = 40.0/ 1000;
	
				for (Paddle paddle : state.getTeam()) 
				{
					calculateSensors(paddle);
				}
				
				if (parameters.isControllingPosition())
				{
					contollingPosition();
				}
				
				// process robot intentions
				for (Paddle robot : state.getTeam()) {
					Controller controller = robot.getController();
					if (this.parameters.isDebugging()) {
						controller.setParameters(parameters);
						controller.setState(state);
					}
					
					controller.process();
				}
				
				for (Paddle paddle : state.getTeam()) 
				{
					paddle.update(dt);
				}
				
	        	if (this.state.isPlaying()) 
	        	{
	        		for(Ball ball : state.getBalls())
	        		{
	        			ball.update(dt, state);
	        			
	              		if (ball.getRight() < 0)
	              		{
	              			if (parameters.isControllingDistance())
	              			{
	              				Paddle paddle = getClosestPaddle(ball.getY());
	              				this.state.addDistanceToBall(
	              						Math.abs(paddle.getCentralY() - ball.getY()));
	              			}
	              			
	                		this.state.goal();                		
	              		}
	        		}
	        	}
	        	
	        	// update display
				if (display != null) {
					display.repaint();					
					pause(50);
				}
				
				step++;
			}
		
			state.setFitness(0);

			totalFitness += (state.getScore() == 0)? 4000 : 2000/state.getScore();
			
			if (parameters.isControllingPosition())
			{
				int positionPoints = state.getControllingPositions() / parameters.getPlayersPerTeam();
				totalFitness += positionPoints;
			}
			
			if (parameters.isControllingDistance())
			{
				float averageDistance = state.getDistanceToBall() / state.getScore();
				if (averageDistance < 50) 
				{
					totalFitness += 50 - averageDistance;
				}
			}
		}
		
		state.setFitness(totalFitness/3);

	}
	
	private Paddle getClosestPaddle(int y) {
		int minDistance = Integer.MAX_VALUE;
		Paddle closestPaddle = null;
		for (Paddle paddle : state.getTeam())
		{
			int distance = Math.abs(y - paddle.getCentralY());
			if (distance < minDistance)
			{
				minDistance = distance;
				closestPaddle = paddle;
			}
		}	
		return closestPaddle;
	}

	protected synchronized void pause(int ms) {
		try {
			this.wait(ms);
		} catch (InterruptedException ex) {
		}
	}

	private void calculateSensors(Paddle paddle) {		
		calculatePosition(paddle);
		calculateTeammate(paddle);
		calculateBall(paddle);		
	}
	
	/**
	 * Calculates a current position of a paddle for a controller.
	 * All coordinates should lay in the range (0, 1).
	 * @param paddle for which the current position is calculated.
	 */
	private void calculatePosition(Paddle paddle) {
		List<Float> positions = new ArrayList<Float>(); 
		
		float pos = ((float)(paddle.getCentralY())) / parameters.getHeight(); 
		positions.add(pos);
		
		paddle.setSensorPosition(positions);
	}
	
	/**
	 * Calculates a distance to the closest teammate 
	 * and store it for a controller.
	 * @param paddle for which a distance to the closest teammate is calculated.
	 */
	private void calculateTeammate(Paddle paddle) {
		List<Float> teammates = new ArrayList<Float>();
		
		// calculate the distance to a closest teammate 
		float closestDistance = Float.MAX_VALUE;
		Paddle closestPaddle = null;
		for (Paddle p: state.getTeam()) {
			if (p == paddle)
				continue;
			float distance = p.getY() - paddle.getY();
			if (Math.abs(closestDistance) > Math.abs(distance))
			{
				closestDistance = distance;
				closestPaddle = p;
			}
		}
		
		if (closestPaddle == null)
		{
			teammates.add(1f);				
		}
		else
		{
			teammates.add(closestDistance/parameters.getHeight());
		}			
		
		paddle.setSensorTeammate(teammates);
	}
	
	/**
	 * Finds the closest ball for the given paddle.
	 * @param paddle the closest ball will be found for the given paddle.
	 * @param otherBall the closest ball if you want to find the next closest ball. 
	 * @return the closest ball for the given paddle.
	 */
	private Ball getClosestBall(Paddle paddle, Ball otherBall) {
		double minDistance = Double.MAX_VALUE;
		Ball closestBall = null;
		boolean moveToPaddle = false;
		
		for (Ball ball : state.getBalls()) 
		{
			if (otherBall != null && ball == otherBall) 
			{
				continue;
			}
			
			double distance = FastMath.hypot(
					Math.abs(paddle.getCentralX() - ball.getX()),
					Math.abs(paddle.getCentralY() - ball.getY()));
			
			if (ball.getPrevX().size() > 0 && 
				ball.getX() > ball.getPrevX().get(0))
			{
				// the ball moves in opposite direction from a paddle
				if (moveToPaddle) 
					continue;
				
				if (distance > minDistance || minDistance == Double.MAX_VALUE)
				{
					minDistance = distance;
					closestBall = ball;
				}
			}
			else
			{
				// the ball moves in direction of a paddle
				
				if (distance < minDistance || !moveToPaddle)
				{
					minDistance = distance;
					closestBall = ball;
				}
				
				moveToPaddle = true;
			}				
		}
		
		return closestBall;		
	}
	
	/**
	 * Calculates a distance to the closest ball and
	 * stores this information for a controller.
	 * @param paddle for which a distance to the closest ball is calculated.
	 */
	private void calculateBall(Paddle paddle) {
		List<Float> ball = new ArrayList<Float>();
		
		Ball firstBall = getClosestBall(paddle, null);
		//Ball secondBall = getClosestBall(paddle, firstBall);
		
		//ball.add((firstBall.getX() - paddle.getCentralX())/((float)parameters.getWidth()));
		ball.add((firstBall.getY() - paddle.getCentralY())/((float)parameters.getHeight()));
		
		paddle.setSensorBall(ball);		
	}	
	
	private void contollingPosition() {
		int step = parameters.getMaximumSteps() / parameters.getControllingPositionProbes();
		if (state.getActualStep() % step != 0) {
			return;
		}
		int i = 0;
		int heightStep = parameters.getHeight() / parameters.getPlayersPerTeam();  
		while(i < parameters.getPlayersPerTeam())
		{
			int min = i * heightStep;
			int max = (i + 1) * heightStep;
			
			for (Paddle paddle : state.getTeam())
			{
				if (paddle.getCentralY() >= min &&
					paddle.getCentralY() <= max)
				{
					state.incrementControllingPosition();
					break;
				}				
			}
			
			i++;
		}
	}
	
	/**
	 * Gets the fitness as a result of the current experiment. 
	 * @return the fitness.
	 */
	public double getResult() {
		return state.getFitness();
	}

	public void stop() {
		this.isInterrupted = true;
	}

}