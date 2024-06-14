package pong;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import core.AbstractMultiProblem.RepresentationWithScore;
import net.jodk.lang.FastMath;

public class PongServer {

	private PongParameters parameters;
	private PongState state;
	private boolean isInterrupted;

	public PongServer(PongParameters parameters, PongState state) {
		this.parameters = parameters;
		this.state = state;
	}

	public void runSimulation(JPanel display) {
		int[] scores = new int[2];
		
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
	
				for (Paddle paddle : state.getFirstTeam()) 
				{
					calculateSensors(paddle);
				}
				for (Paddle paddle : state.getSecondTeam()) 
				{
					calculateSensors(paddle);
				}
				
				if (parameters.isControllingPosition())
				{
					controllingPosition(0);
					controllingPosition(1);
				}
				
				// process robot intentions
				for (Paddle robot : state.getFirstTeam()) {
					Controller controller = robot.getController();
					if (this.parameters.isDebugging()) {
						controller.setParameters(parameters);
						controller.setState(state);
					}
					
					controller.process();
				}
				for (Paddle robot : state.getSecondTeam()) {
					Controller controller = robot.getController();
					if (this.parameters.isDebugging()) {
						controller.setParameters(parameters);
						controller.setState(state);
					}
					
					controller.process();
				}			
				
				for (Paddle paddle : state.getFirstTeam()) 
				{
					paddle.update(dt);
				}
				for (Paddle paddle : state.getSecondTeam()) 
				{
					paddle.update(dt);
				}
	        	
	        	if (this.state.isPlaying()) 
	        	{
	        		for(Ball ball : state.getBalls())
	        		{
	        			ball.update(dt, state);
	        			
	              		if (ball.getLeft() > this.parameters.getWidth())
	                		this.state.goal(0);
	              		else if (ball.getRight() < 0)
	                		this.state.goal(1);
	        		}
	        	}
	        	
	        	// update display
				if (display != null) {
					display.repaint();					
					pause(50);
				}
				
				step++;
			}
			
			for (int t = 0; t < 2; t++) {
				scores[t] += state.getScore(t);
			}
		}
		
		// calculate final fitness for both teams
		for (int t = 0; t < 2; t++) {
			state.setFitness(t, 0);

			int totalFitness = 0;
			
			totalFitness += scores[t];
			
			state.setFitness(t, totalFitness);
		}

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
		float pos=  ((float)(paddle.getCentralY())) / parameters.getHeight();
		if (paddle.getTeam() == 0) {
			positions.add(pos);
		} else {
			positions.add(1-pos);
		}
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
		for (Paddle p: state.getTeam(paddle.getTeam())) {
			if (p == paddle)
				continue;
			float distance = p.getY() - paddle.getY();
			if (Math.abs(closestDistance) > Math.abs(distance))
			{
				closestDistance = distance;
				closestPaddle = p;
			}
		}
		
		if (closestPaddle == null) {
			teammates.add(1f);				
		}
		else if (paddle.getTeam() == 0) {
			teammates.add(closestDistance/parameters.getHeight());
		}
		else {
			teammates.add(-closestDistance/parameters.getHeight());	
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
			   ((ball.getX() > ball.getPrevX().get(0) && paddle.getTeam()==0) ||
			   (ball.getX() < ball.getPrevX().get(0) && paddle.getTeam()==1)))
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
		
		float distance_y = (firstBall.getY() - paddle.getCentralY())/((float)parameters.getHeight()); 
		if (paddle.getTeam() == 0) {
			ball.add(distance_y);
		}
		else {
			ball.add(-distance_y);			
		}
		
		paddle.setSensorBall(ball);		
	}	

	
	private void controllingPosition(int team) {
		int step = parameters.getMaximumSteps() / PongParameters.getControllingPositionProbes();
		if (state.getActualStep() % step != 0) {
			return;
		}
		int i = 0;
		int heightStep = parameters.getHeight() / parameters.getPlayersPerTeam();  
		while(i < parameters.getPlayersPerTeam())
		{
			int min = i * heightStep;
			int max = (i + 1) * heightStep;
			
			for (Paddle paddle : state.getTeam(team))
			{
				if (paddle.getCentralY() >= min &&
					paddle.getCentralY() <= max)
				{
					state.incrementControllingPosition(team);
					break;
				}				
			}
			
			i++;
		}		
	}

	
	public List<RepresentationWithScore> getResults() {
		// return real results
		List<RepresentationWithScore> results = new ArrayList<RepresentationWithScore>();

		if (state.getFitness(0) > state.getFitness(1)){
			results.add(new RepresentationWithScore(state.getCandidate(0), state
					.getFitness(0)));
			results.add(new RepresentationWithScore(state.getCandidate(1), state
					.getFitness(1)));	
		} else {
			results.add(new RepresentationWithScore(state.getCandidate(1), state
					.getFitness(1)));
			results.add(new RepresentationWithScore(state.getCandidate(0), state
					.getFitness(0)));
		}
		return results;
	}

	public void stop() {
		this.isInterrupted = true;
	}

}

