package components.simsoccer.model;

public class VisibleObject {

	/** Returns 0 if the team of this player equals with the team of the observer, 1 if not, and -1 if not player */
	public int team = -1;
	
	/** Returns the distance to this object */
	public double distance;
	/** Returns the relative direction to this object in degrees */
	public double direction;
	
	public VisibleObject() {}
}
