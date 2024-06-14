package components.simsoccer.model;


import java.util.ArrayList;

import components.simsoccer.SimServer;
import components.simsoccer.SimSoccer;
import core.AbstractRepresentation;

/**
 * The controller of a player, basically this class contains the behavior of the soccer player
 */

public class NngaPlayer implements Controller {

  private boolean canSeeBall = false;
  private final double FARAWAY = 400.0;
  private double distanceBall = FARAWAY;
  private double directionBall;
  
  private SimPlayer skiiplayer;
  private SimSoccer master;
  private AbstractRepresentation net;
  private ArrayList<Float> input = new ArrayList<Float>();
  private ArrayList<Float> output = new ArrayList<Float>();
  private boolean switcher;
  public int kickCount = 0; 
  public double [] nearestteammatedist = new double[3];
  public double [] nearestOppdist = new double[3];
  public double borderdist;
  public int bordernum;
  public double goal_own_dist = FARAWAY;
  public double goal_own_direction;
  public double goal_opp_dist = FARAWAY;
  public double goal_opp_direction = FARAWAY;
  public ArrayList<VisibleObject> visiblePlayerBuffer = new ArrayList<VisibleObject>();
  //private Random generator = new Random();
  /**
   * Defined as follows:
   * 0: PLAY_MODE_BEFORE_KICK_OFF
   * 1: PLAY_MODE_KICK_OFF_OWN
   * 2: PLAY_MODE_KICK_OFF_Other
   * 3: PLAY_MODE_PLAY_ON
   */
  //private int gamemode;
  //private int marginBall = 5;
  
  public NngaPlayer(AbstractRepresentation net, SimSoccer master) {
	this.net = net.clone();   
	this.master = master;
  }
  
  public void setNearestTeamMateDist (int sec, double dist) {
	  this.nearestteammatedist[sec] = dist;
  }
  
  public void setNearestOppDist (int sec, double dist) {
	  this.nearestOppdist[sec] = dist;
  }
  
  public double getNearestTeamMateDist (int sec) {
	  return nearestteammatedist[sec];
  }
  
  public double getNearestOppDist (int sec) {
	  return nearestOppdist[sec];
  }
  
  public void setGoalLeft (double dist, double direction) {
	  if (getPlayer().isTeamEast()) { 
		  goal_opp_dist = dist;
		  goal_opp_direction = direction;
	  }
	  else { 
		  goal_own_dist = dist;
		  goal_own_direction = direction;
	  }
  }
   
  public double getBorderDist (int sec) {
	  return borderdist;
  }
  
  public void setGoalRight (double dist, double direction) {
	  if (getPlayer().isTeamEast()) {
		  goal_own_dist = dist;
		  goal_own_direction = direction;
	  }
	  else {
		  goal_opp_dist = dist;
		  goal_opp_direction = direction;
	  }
  }
  
  public void setKickNum (int kick) {
	  kickCount = kick;
	  master.kicknum[master.teamname2int(getPlayer().getTeamName())][getPlayer().getNumber()-1] = kickCount; //increase kick number for scoring
  }
  
  public SimSoccer getSession() {
	  return this.master;
  }

  public Player getPlayer (){
    return skiiplayer;
  }
  
  public void setPlayer (Player p){
	  skiiplayer = (SimPlayer) p;
  }
  
  /**
   * Reset the state of the controller.
   */
  public void preInfo () {
    canSeeBall = false;
    goal_opp_dist = FARAWAY;
    goal_own_dist = FARAWAY;
    goal_opp_direction = FARAWAY;
    //nullify team mates & opponents visible
    for (int i=0;i<3;i++) {
    	this.setNearestOppDist(i, FARAWAY);
    	this.setNearestTeamMateDist(i, FARAWAY);
    }
    borderdist = 0;
    bordernum = 0;
    visiblePlayerBuffer.clear();
  }
  
  /**
   * Controls the client by interpreting the state of the controller.
   */
  public void postInfo () {
	  
	  processVisibleObjects();
	  int borderin;
	  if (bordernum == 0) borderin = 5;
	  if (bordernum == 1) borderin = 0;
	  if (bordernum == 2) borderin = 3;
	  else borderin = 0;
	  
	  input.clear();
	  output.clear();
	  
	  //add input vales
	  if (distanceBall != 0) input.add( (float)(300/distanceBall) ); else input.add (300f); //Input 1 - ball distance
	  input.add((float)directionBall); //Input 2 - ball relative direction
	  if (nearestteammatedist[0] != 0) input.add((float)(FARAWAY/nearestteammatedist[0])); else input.add ((float)FARAWAY); //Input 3
	  if (nearestteammatedist[1] != 0) input.add((float)(FARAWAY/nearestteammatedist[1])); else input.add ((float)FARAWAY); //Input 4
	  if (nearestteammatedist[2] != 0) input.add((float)(FARAWAY/nearestteammatedist[2])); else input.add ((float)FARAWAY); //Input 5
	  if (nearestOppdist[0] != 0) input.add((float)(FARAWAY/nearestOppdist[0])); else input.add ((float)FARAWAY); //Input 6
	  if (nearestOppdist[1] != 0) input.add((float)(FARAWAY/nearestOppdist[1])); else input.add ((float)FARAWAY); //Input 7
	  if (nearestOppdist[2] != 0) input.add((float)(FARAWAY/nearestOppdist[2])); else input.add ((float)FARAWAY); //Input 8
	  input.add((float)borderdist); //input 9
	  input.add((float)borderin); //input 10
	  input.add((float)(FARAWAY/goal_own_dist)); //Input 11
	  input.add((float)(FARAWAY/goal_opp_dist)); //Input 12
	  input.add((float)goal_opp_direction); //Input 13
	  
	  //calculate outputs
	  //output.setSize(4);
	  output = net.getOutput(input);

	  //get game scores //never used
	/*  if (master.sendScores) {
		  getPlayer().score();
		  master.sendScores=false;
	  }*/
	  //get outputs according to game state
	 /* if (gamemode == 1) kickOff();
	  else if (gamemode == 2) kickOff();
	  else if (gamemode == 3) playOn();*/
	  playOn();
	    
  }
 
  /** Function to create input from  team and opposing players */
  private void processVisibleObjects() {
	  ArrayList<VisibleObject> teammates1 = new ArrayList<VisibleObject>();
	  ArrayList<VisibleObject> teammates2 = new ArrayList<VisibleObject>();
	  ArrayList<VisibleObject> teammates3 = new ArrayList<VisibleObject>();
	  ArrayList<VisibleObject> opponents1 = new ArrayList<VisibleObject>();
	  ArrayList<VisibleObject> opponents2 = new ArrayList<VisibleObject>();
	  ArrayList<VisibleObject> opponents3 = new ArrayList<VisibleObject>();
	  for (int i = 0; i<visiblePlayerBuffer.size();i++) {
		  VisibleObject obj = visiblePlayerBuffer.get(i);
		  if (obj.team == 0) { //teammate
			  if ((obj.direction >= -45) && (obj.direction < -15)) {
				  teammates1.add(obj);
			  }
			  else if ((obj.direction >= -15) && (obj.direction < 15)) {
				  teammates2.add(obj);
			  }
			  else if ((obj.direction >= 15) && (obj.direction <= 45)) {
				  teammates3.add(obj);
			  }
		  }
		  if (obj.team == 1) { //teammate
			  if ((obj.direction >= -45) && (obj.direction < -15)) {
				  opponents1.add(obj);
			  }
			  else if ((obj.direction >= -15) && (obj.direction < 15)) {
				  opponents2.add(obj);
			  }
			  else if ((obj.direction >= 15) && (obj.direction <= 45)) {
				  opponents3.add(obj);
			  }
		  }
	  }
	  //create input for nn
	  setNearestTeamMateDist(0, getMinimumDistance (teammates1));
	  setNearestTeamMateDist(1, getMinimumDistance (teammates2));
	  setNearestTeamMateDist(2, getMinimumDistance (teammates3));
	  
	  setNearestOppDist(0, getMinimumDistance (opponents1));
	  setNearestOppDist(1, getMinimumDistance (opponents2));
	  setNearestOppDist(2, getMinimumDistance (opponents3));
}

private double getMinimumDistance(ArrayList<VisibleObject> list) {
	double distance = FARAWAY;
	for (int i =0;i < list.size();i++) {
		if (list.get(i).distance < distance) distance = list.get(i).distance;
	}
	return distance;
}

private void playOn () {
	  if (canSeeBall)
	  {
		  if (distanceBall < 0.7) {
			  //turnTowardBall();
			  double direction = (output.get(3)); //Now kick is not preprogrammed
			  
		/*	  if (this.visiblePlayerBuffer.size()>0) {
				  direction = visiblePlayerBuffer.get(generator.nextInt(visiblePlayerBuffer.size())).direction;
			  }
			  if (goal_opp_direction != 400) {
				  direction = goal_opp_direction;
			  }*/
			  
			  getPlayer().kick( (int) (output.get(2)*1)/*power*/, (int)direction) /*direction*/;	
			  //System.out.println(getPlayer().getNumber()+" kick");
		  }
		  else {
			  if (switcher)
			  {
				  getPlayer().dash((int)(output.get(0)*SimServer.MAXPOWER)); //output 1 (dash)
				  switcher = false;
			  }
			  else
			  {
				  getPlayer().turn(output.get(1)*SimServer.MAXMOMENT); //output 2 (turn)
				  switcher = true;
			  }	
		  }		  
	  }
	  else {
		  if (switcher)
		  {
			  getPlayer().dash((int)(output.get(0)*1)); //output 1 (dash)
			  switcher = false;
		  }
		  else
		  {
			  ((SimPlayer)getPlayer()).turn(output.get(1)*1); //output 2 (turn)
			  switcher = true;
		  }
	  }
  }
  
//  private void kickOff () {
//	  if (canSeeBall)
//	  {
//		  //turnTowardBall();
//		  if (distanceBall < 0.6) {
//			  
//			  double direction = generator.nextInt(360); 
//			  if (goal_opp_direction != 400) {
//				  direction = goal_opp_direction;
//			  }
//			  if (this.visiblePlayerBuffer.size()>0) {
//				  direction = visiblePlayerBuffer.get(generator.nextInt(visiblePlayerBuffer.size())).direction;
//			  }
//			  //System.out.println("kick");
//			  if (getPlayer().getNumber()==1) System.err.println("I see the ball, it is close, I kick it to someone"+" ("+direction+")");
//			  getPlayer().kick(generator.nextInt(500)/*power*/, (int)direction)/*direction*/;
//		  }
//		  else {
//			  turnTowardBall();
//			  if (distanceBall < marginBall) {
//				  if (getPlayer().getNumber()==1) System.out.println("I see the ball, it is close, I will dash with "+distanceBall);
//				  getPlayer().dash((int)(distanceBall));
//			  }
//			  else {
//				  if (getPlayer().getNumber()==1) System.out.println("I see the ball but it is far away, I will dash with full power");
//				  getPlayer().dash(300);
//			  }
//		  }
//	  }
//	  else {
//		  if (switcher)
//		  {
//			  getPlayer().dash((int)(output.get(0)*1)); //output 1 (dash)
//			  switcher = false;
//			  if (getPlayer().getNumber()==1) System.out.println("I cant see the ball so I dash");
//		  }
//		  else
//		  {
//			  getPlayer().turn(output.get(1)*1); //output 2 (turn)
//			  switcher = true;
//			  if (getPlayer().getNumber()==1) System.out.println("I cant see the ball so I turn");
//		  }
//	  }
//  }
  
  public void infoSeeLine (double linedist) {
	  this.bordernum++;
	  if (linedist > borderdist) borderdist = linedist;
  }
  
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagRight (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagLeft (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagOwn (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagOther (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagCenter (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagCornerOther (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagCornerOwn (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */// TODO Auto-generated method stub
  public void infoSeeFlagPenaltyOwn (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagPenaltyOther (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that the own goal is in sight and about its
   * distance and direction. This information is stored in the
   * controllers state to be interpreted in postInfo().
   */
  public void infoSeeFlagGoalOwn (int id, double distance, double direction){
    /*CanSeeNothing = false;
    if (id==Controller.FLAG_CENTER) {
      this.canSeeOwnGoal = true;
      this.distanceOwnGoal = distance;
      this.directionOwnGoal = direction;
    }*/
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeFlagGoalOther (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeeLine (int id, double distance, double direction){
    //canSeeNothing = false;
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeePlayerOther (int number, double distance, double direction){
  }
  /**
   * The controller is informed that any object is in sight.
   */
  public void infoSeePlayerOwn (int number, double distance, double direction){
  }
  /**
   * The controller is informed that the ball is in sight and about its
   * distance and direction. This information is stored in the
   * controllers state to be interpreted in postInfo().
   */
  public void infoSeeBall (double distance, double direction){
    this.canSeeBall = true;
    this.distanceBall = distance;
    this.directionBall = direction;
  }
  public void infoHearReferee (int refereeMessage){
	  //System.out.println (refereeMessage);	
  }
  /**
   * If the controller hears that the server is in before kick off mode
   * it moves to a position that depends on the clients nummber.
   */
  public void infoHearPlayMode (int playMode){
    if (playMode==Controller.PLAY_MODE_BEFORE_KICK_OFF){
    	//this.gamemode = 0;
    	this.pause(1000);
      switch (this.getPlayer().getNumber()) {
        case 1: {this.getPlayer().move(-10, 0); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 2: {this.getPlayer().move(-10, 10); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 3: {this.getPlayer().move(-10, -10); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 4: {this.getPlayer().move(-20, 0); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 5: {this.getPlayer().move(-20, 10); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 6: {this.getPlayer().move(-20, -10); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 7: {this.getPlayer().move(-20, 20); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 8: {this.getPlayer().move(-20, -20); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 9: {this.getPlayer().move(-30, 0); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 10: {this.getPlayer().move(-40, 10); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        case 11: {this.getPlayer().move(-40, -10); if (getPlayer().isTeamEast()) getPlayer().turn(180); break;}
        default: throw new Error("number must be initialized before move");
      }
    }
    else if (playMode==Controller.PLAY_MODE_KICK_OFF_OWN){
    	//this.gamemode = 1;
    }
   else if (playMode==Controller.PLAY_MODE_KICK_OFF_OTHER){
    	//if (getPlayer().isTeamEast()) getPlayer().turn(180);
    	//this.gamemode = 2;
    }
    else if (playMode==Controller.PLAY_MODE_PLAY_ON){
    	//this.gamemode = 3;
    	//System.err.println ("PLAY ON");
    }
  }
  public void infoHear (double direction, String message){}
  public void infoSenseBody
    (int viewQuality, int viewAngle,
    double stamina, double speed, double headAngle, int kickCount,
    int dashCount, int turnCount, int sayCount, int turnNeckCount){
	  setKickNum(kickCount);
  }

/*  private void turnTowardBall() {
    getPlayer().turn(directionBall);
  }/*
  private void turnTowardOwnGoal() {
    getPlayer().turn(directionOwnGoal);
  }*/
  private synchronized void pause (int ms) {
    try {
      this.wait(ms);
    } catch (InterruptedException ex) {
    }
  }

public infoObject getInfoObject() {
	// Useless here
	return null;
}

public void addItem(VisibleObject object) {
	visiblePlayerBuffer.add(object);
}

public void clearItembuffer() {
	this.visiblePlayerBuffer.clear();
}

}