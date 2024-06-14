/**
 * 
 */
package singlePong;

import java.util.Hashtable;
import java.util.Random;

import core.XMLFieldEntry;

/**
 * Storage of parameters for single Pong game
 * 
 * @author Sergii Zhevzhyk
 */
public class SinglePongParameters {
	private Random random;
	
	// field
	private int width = 640;
	private int height = 480;
	private int wallWidth = 12;
	
	// paddle
	private int playersPerTeam = 3;
	private int paddleWidth = 12;
	private int paddleHeight = 60;
	private double paddleSpeed = 2;	// should be able to cross court vertically   in 2 seconds
	
	// ball
	private int numberOfBalls = 1;
	private double ballSpeed = 4;   // should be able to cross court horizontally in 4 seconds, at starting speed ...
	private double ballAccel = 8;
	private int ballRadius = 5;	
	
	private int maximumSteps = 3000;
	private int controllingPositionProbes = 100;
	
	private int maxScore = 9;
	
	// Parameters for evaluation from problem definition
	public int SCORE_WEIGHT = 10000;
	
	private boolean saveFrames = false;
	
	private boolean debugging = false;
	
	public String SAVE_PATH = "./Images/"; 
	
	// the need to teach representations how to control the field 
	private boolean controllingPosition = false;
	
	// encode the distance to ball if case of goal
	private boolean controllingDistance = false;
	
	public void initialize(Hashtable<String, XMLFieldEntry> parameters, Random random){
		this.random = random;
		
		// Read the parameters from a configuration file
		
		// paddle
		this.playersPerTeam = Integer.parseInt(parameters.get(
				"playersPerTeam").getValue());
		this.paddleHeight = Integer.parseInt(parameters.get(
				"paddleHeight").getValue());
		this.paddleSpeed = Double.parseDouble(parameters.get(
				"paddleSpeed").getValue());

		// ball
		this.numberOfBalls = Integer.parseInt(parameters.get(
				"numberOfBalls").getValue());
		
		// parameters of the controller
		XMLFieldEntry controllingPositionEntry = parameters.get("controllingPosition");
		if (controllingPositionEntry != null) {
			this.controllingPosition = Boolean.parseBoolean(controllingPositionEntry
					.getValue());
		}
		
		XMLFieldEntry controllingDistanceEntry = parameters.get("controllingDistance");
		if (controllingDistanceEntry != null) {
			this.controllingDistance = Boolean.parseBoolean(controllingDistanceEntry
					.getValue());
		}
		
		XMLFieldEntry maximumStepsEntry = parameters.get("maximumSteps");
		if (maximumStepsEntry != null) {
			this.maximumSteps = Integer.parseInt(maximumStepsEntry
					.getValue());
		}
	}
	
	public Random getRandom() {
		return random;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getWallWidth() {
		return wallWidth;
	}

	public int getPaddleWidth() {
		return paddleWidth;
	}
	
	public int getPaddleHeight() {
		return paddleHeight;
	}

	public double getPaddleSpeed() {
		return paddleSpeed;
	}

	public double getBallSpeed() {
		return ballSpeed;
	}

	public double getBallAcceleration() {
		return ballAccel;
	}

	public int getBallRadius() {
		return ballRadius;
	}
	
	/** Returns the number of players per team. */
	public int getPlayersPerTeam() {
		return playersPerTeam;
	}
	
	public int getBalls(){
		return numberOfBalls;
	}
	
	public int getMaximumSteps() {
		return this.maximumSteps;
	}	
	
	public boolean isSaveFrames() {
		return saveFrames;
	}

	public void setSaveFrames(boolean saveFrames) {
		this.saveFrames = saveFrames;
	}
	
	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}
	
	public boolean isDebugging() {
		return debugging;
	}

	public boolean isControllingPosition() {
		return controllingPosition;
	}

	public boolean isControllingDistance() {
		return this.controllingDistance;
	}
	
	public int getControllingPositionProbes() {
		return this.controllingPositionProbes;
	}
	
	public int getMaxScore() {
		return this.maxScore;
	}
}
