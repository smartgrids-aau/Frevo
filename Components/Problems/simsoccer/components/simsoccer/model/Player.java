package components.simsoccer.model;


/**
 * Interface for an abstract soccer player. To be used by Controller.
 */

public interface Player {

  public void dash (int power);
  public void move (int x, int y);
  public void kick (int power, double direction);
  public void say (String message);
  public void senseBody ();
  public void turn (double angle);
  public void turnNeck (double angle);
  public void catchBall (double direction);
  public void changeViewMode(int quality, int angle);
  public void score();

  public String getTeamName();
  public boolean isTeamEast();
  public void isTeamEast(boolean is);
  public void setNumber(int num);
  public int getNumber();
  public int getOppositeSide();

}