package fehervari.evopco;

import net.tinyos.prowler.Event;

public class FiringEvent extends Event {
	
	/** Expected firing time in simulator-ticks */
	private long time;
	private OscillatorApplication application;
	
	/** Constructs a new firing event.
	 * @param time The time of the event in simulator ticks */
	public FiringEvent(long time, OscillatorApplication application) {
		super(time);
		this.time = time;
		this.application = application;
	}

	public void execute() {
		// fire
		application.fire();
	}
	
    public String toString(){
        return Long.toString(time);        
    }
	
	/** Returns the time in simulator ticks*/
	public long getTime() {
		return time;
	}
}
