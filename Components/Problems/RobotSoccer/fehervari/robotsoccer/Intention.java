package fehervari.robotsoccer;

public class Intention {
	
	/** Intention ID for changing motor speeds */
	public static final int SETSPEED = 0;
	/** Intention ID for kicks */
	public static final int KICK = 1;
	
	public int intId;
	public double param1,param2;
	public String param3s;
	
	public Intention (int id, double p1, double p2, String p3s) {
		this.intId = id;
		this.param1 = p1;
		this.param2 = p2;
		this.param3s = p3s;
	}
}
