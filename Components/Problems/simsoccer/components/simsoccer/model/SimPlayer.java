package components.simsoccer.model;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import components.simsoccer.SimServer;

public class SimPlayer extends MobileObject  implements Player {
	
	public String teamName;
	public int side; //0: left, 1: right
	public int uniformNumber;
	public double bodyDirection;
	public double faceDirection;
	public double neckDirection;
	private Controller controller;
	public List<Intention> buffer = new ArrayList<Intention>();
	private double stamina;
	private double effort;
	private double recovery;
	
	public SimPlayer (String team, int side, int number, Controller controller) {
		super(new Point2D.Double(0,0));
		setTeam(team);
		setSide(side); // 0, 2
		setNumber(number);
		setController(controller);
		controller.setPlayer(this);
		speedVector = new Point2D.Double(0,0);
		accelerationVector = new Point2D.Double(0,0);
		stamina = SimServer.STAMINAMAX;
		effort = 1;
	}
	
	public int getType() {
		return 1;
	}
	
	public int getOppositeSide() {
		if (this.side == 0) return 1;
		return 0;
	}
	
	public Controller getController() {
		return this.controller;
	}
	
	public void setController(Controller c) {
		this.controller = c;
	}
	
	public void setTeam(String team) {
		this.teamName = team;
	}
	
	/**
	 * Returns 0 if the player is in the left team, 1 if in the right team
	 * @return
	 */
	public int getSide() {
		return this.side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	
	public int getNumber() {
		return this.uniformNumber;
	}
	public void setNumber(int number) {
		this.uniformNumber=number;
	}
	
	public double getBodyDirection() {
		return this.bodyDirection;
	}
	
	public void setBodyDirection (double bodydirection) {
		this.bodyDirection = bodydirection;
	}
	public double getFaceDirection() {
		return this.faceDirection;
	}
	
	public void setFaceDirection (double facedirection) {
		this.faceDirection = facedirection;
	}
	public double getNeckDirection() {
		return this.neckDirection;
	}
	
	public void setNeckDirection (double neckdirection) {
		this.neckDirection = neckdirection;
	}
	
	public void setStamina (int stamina) {
		this.stamina = stamina;
	}
	public double getStamina() {
		return this.stamina;
	}
	
	public double consumeStamina(double power) {
		double stam = stamina;
		if (this.stamina < power) {
			setStamina(0);
			return stam;
		}
		
		stamina = stamina-power;
		return power;		
	}
	
	public void setEffort (double effort) {
		this.effort = effort;
	}
	public double getEffort() {
		return effort;
	}
	
	public void setRecovery (double recovery) {
		this.recovery = recovery;
	}
	public double getRecovery() {
		return recovery;
	}

	@Override
	public void catchBall(double direction) {
		// useless		
	}

	@Override
	public void changeViewMode(int quality, int angle) {
		// TODO for changeviewmode		
	}

	@Override
	public void dash(int power) {
		buffer.add(new Intention(SimServer.DASH,power,0,null));	
	}

	@Override
	public String getTeamName() {
		return teamName;
	}

	@Override
	public boolean isTeamEast() {
		if (side == 1) return true;
		return false;
	}

	@Override
	public void isTeamEast(boolean is) {
		this.side = 1;
	}

	@Override
	public void kick(int power, double direction) {
		buffer.add(new Intention(SimServer.KICK,power,direction,null));		
	}

	@Override
	public void move(int x, int y) {
		buffer.add(new Intention(SimServer.MOVE,x,y,null));	
	}

	@Override
	public void say(String message) {
		buffer.add(new Intention(SimServer.SAY,0,0,message));
		
	}

	@Override
	public void score() {
		buffer.add(new Intention(SimServer.SCORE,0,0,null));
		
	}

	@Override
	public void senseBody() {
		// TODO sensebody		
	}

	@Override
	public void turn(double angle) {
		buffer.add(new Intention(SimServer.TURN,angle,0,null));		
	}

	@Override
	public void turnNeck(double angle) {
		buffer.add(new Intention(SimServer.TURNNECK,angle,0,null));
		//unimplemented
	}
	/**
	 * Adds the acceleration vector to the speedvector then normalizes it
	 * @param accvect
	 */
	public void addAccVector(Point2D.Double accvect) {
		Point2D.Double speedvector = new Point2D.Double (this.speedVector.x+accvect.x,this.speedVector.y+accvect.y);
		//normalize
		double length = SimServer.getLength(speedvector); 
		if (length > SimServer.PLAYER_SPEED_MAX ) {
			double ratio = (SimServer.PLAYER_SPEED_MAX/length); //! POSSIBLE NAN ERROR
			speedvector = new Point2D.Double (speedvector.x*ratio,speedvector.y*ratio);
		}
		this.speedVector = speedvector;
	}

	public void applydecay() {
		Point2D.Double newspeed = new Point2D.Double (speedVector.x*SimServer.PLAYER_DECAY,speedVector.y*SimServer.PLAYER_DECAY);
		this.speedVector = newspeed;
		
	}
}
