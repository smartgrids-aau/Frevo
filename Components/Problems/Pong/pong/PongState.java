package pong;

import core.AbstractRepresentation;

public class PongState {
	
	private PongParameters parameters;
	
 	private boolean withmonitor = false;
 	private boolean withpause = false;
 	
 	/**
 	 * Current step of the simulation
 	 */
 	private int actualStep;
 	
 	private AbstractRepresentation[] candidates;
 	
 	private Paddle[][] teams;
 	
 	private Ball[] balls;
	 	
 	/** Contains how many goals have happened so far in the game. */
	int[] scores = new int[2];
	private double[] fitness = new double[2];
	
	private int controllingPosition[] = new int[2];
	
	private int ballDirection = 0;
	
	public PongState(PongParameters parameters) {
 		this.parameters = parameters;
	}
	
	private void changeBallDirection(){
		this.ballDirection = 1 - ballDirection;
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

	
	public void setCandidates(AbstractRepresentation[] candidates) {
		this.candidates = candidates;
	}
	
	public AbstractRepresentation[] getCandidates() {
		return candidates;
	}
	
	public AbstractRepresentation getCandidate(int i) {
		return candidates[i];
	}
	
	public void setFitness(int team, double fitness) {
		this.fitness[team] = fitness;
	}
	
	public double getFitness(int team) {
		return fitness[team];
	}
	
	public Paddle[] getFirstTeam() {
		return teams[0];
	}
	
	public Paddle[] getSecondTeam() {
		return teams[1];
	}
	
	public Paddle[] getTeam(int team) {
		return teams[team];
	}	
	
	public void goal(int team)
	{
		this.scores[team]++;
		for (Ball ball : getBalls())
		{
			ball.reset(ballDirection);			
		}					
		changeBallDirection();
	}
	
	public int getScore(int team) {
		return this.scores[team];
	}
	
	public void createTeams() {
		teams = new Paddle[2][parameters.getPlayersPerTeam()];
		for (int i = 0; i < parameters.getPlayersPerTeam(); i++) {
			teams[0][i] = new Paddle(parameters, new EvolvedController(candidates[0]), 0, i);
		}
		for (int i = 0; i < parameters.getPlayersPerTeam(); i++) {
			teams[1][i] = new Paddle(parameters, new EvolvedController(candidates[1]), 1, i);
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
			teams[0][s].setPosition(0, y - parameters.getPaddleHeight()/2);
			teams[1][p].setPosition(
					parameters.getWidth() - teams[1][p].getWidth(), y - parameters.getPaddleHeight()/2);
			teams[0][s].setDirection(0);
			teams[1][p].setDirection(0);
			
			y -= step;
			p++;
		}
	}
	
	public void resetScores() {
		actualStep = 0;
		
		scores[0] = 0;
		scores[1] = 0;

		fitness[0] = 0;
		fitness[1] = 0;
		
		controllingPosition[0] = 0;
		controllingPosition[1] = 0;
	}
	
	public void resetSimulation(){
		createTeams();
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
			balls[i].reset(ballDirection);
			changeBallDirection();
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
	
	public int getControllingPositions(int team) {
		return controllingPosition[team];
	}

	public void incrementControllingPosition(int team) {
		this.controllingPosition[team] ++;
	}
	
}
