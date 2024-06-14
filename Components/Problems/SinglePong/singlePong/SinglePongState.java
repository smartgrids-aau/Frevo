package singlePong;

import core.AbstractRepresentation;

public class SinglePongState {
	
	private SinglePongParameters parameters;
	
 	private boolean withmonitor = false;
 	private boolean withpause = false;
 	
 	/**
 	 * Current step of the simulation
 	 */
 	private int actualStep;
 	
 	private AbstractRepresentation candidate;
 	
 	private Paddle[] team;
 	
 	private Ball[] balls;
	 	
 	private float distanceToBall;
 	
 	/** Contains how many goals have happened so far in the game. */
	int score;
	
	private double fitness;
	
	private int controllingPosition;
	
	public SinglePongState(SinglePongParameters parameters) {
 		this.parameters = parameters;
	}
	
	public boolean isWithMonitor() {
		return withmonitor;
	}
	
	public void setWithMonitor(boolean withmonitor) {
		this.withmonitor = withmonitor;
	}
	
	public boolean isWithPause() {
		return withpause;
	}
	
	public void setWithPause(boolean withpause) {
		this.withpause = withpause;
	}
	
	public int getActualStep() {
		return actualStep;
	}

	public void setActualStep(int actualStep) {
		this.actualStep = actualStep;
	}

	
	public void setCandidate(AbstractRepresentation candidate) {
		this.candidate = candidate;
	}
	
	public AbstractRepresentation getCandidate() {
		return candidate;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public Paddle[] getTeam() {
		return team;
	}
	
	public void goal()
	{
		this.score++;
		for (Ball ball : getBalls())
		{
			ball.reset();			
		}
		placePlayers();
	}
	
	public int getScore() {
		return this.score;
	}

	public void createTeam() {
		team = new Paddle[parameters.getPlayersPerTeam()];
		for (int i = 0; i < parameters.getPlayersPerTeam(); i++) {
			team[i] = new Paddle(parameters, new EvolvedController(candidate), i);
		}				
	}
	
	public void createBalls() {
		balls = new Ball[parameters.getBalls()];
		for(int i = 0; i < parameters.getBalls(); i++)
		{
			balls[i] = new Ball(parameters, i);
		}	
	}
	
	/** Places players to their starting position */
	public void placePlayers() {
		int step = parameters.getHeight() / (parameters.getPlayersPerTeam() + 1); 
		int p = 0;
		int y =  parameters.getHeight() - step; 
		while (p < parameters.getPlayersPerTeam()) {
			int s = parameters.getPlayersPerTeam() - p - 1;
			team[s].setPosition(0, y - parameters.getPaddleHeight()/2);
			team[s].setDirection(0);
			
			y -= step;
			p++;
		}
	}
	
	public void resetScores() {
		actualStep = 0;
		
		score = 0;
		
		fitness = 0;
		
		controllingPosition = 0;
		
		distanceToBall = 0;
	}
	
	public void resetSimulation(){
		createTeam();
		createBalls();
		placePlayers();
		placeBalls();
	}

	/**
	 * Resets the ball's position
	 */
	public void placeBalls() {
		for (int i=0; i<balls.length; i++)
		{
			balls[i].reset();			
		}
	}
	
	public Ball[] getBalls() {
		return balls;
	}

	public boolean isFinished() {
		return actualStep >= parameters.getMaximumSteps();
	}
	
	public boolean isPlaying() {
		return !isFinished();
	}

	public int getControllingPositions() {
		return controllingPosition;
	}

	public void incrementControllingPosition() {
		this.controllingPosition ++;
	}

	public float getDistanceToBall() {
		return distanceToBall;
	}

	public void addDistanceToBall(float distanceToBall) {
		this.distanceToBall = distanceToBall;
	}
}




