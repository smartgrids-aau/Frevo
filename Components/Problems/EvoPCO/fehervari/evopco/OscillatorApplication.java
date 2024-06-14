package fehervari.evopco;

import net.tinyos.prowler.Application;
import net.tinyos.prowler.Node;

public abstract class OscillatorApplication extends Application {
	
	/** Reference to the parent simulation */
	protected EvoPCO parent;
	
	protected FiringEvent nextFiringEvent;

	public OscillatorApplication(Node node, EvoPCO parent) {
		super(node);
		this.parent = parent;  
	}
	
	public abstract void fire();

	/** Returns the current phase of the oscillator. Returned value lies between 0..1 */
	public double getCurrentPhase() {
		//double phase = parent.SYNC_INTERVAL_ms - EvoPCO.convertTicksToMillisec(nextFiringEvent.getTime() - getNode().getNodeTime());
		long remainder_ms = EvoPCO.convertTicksToMillisec(nextFiringEvent.getTime()) - getNode().getNodeTimeInMillisec();
		double phase_ms = parent.SYNC_INTERVAL_ms - remainder_ms;
		
		double phase = phase_ms / parent.SYNC_INTERVAL_ms;
		
		/*if (phase > 1) {
			System.out.println (getNode().getId()+": firing time: "+EvoPCO.convertTicksToMillisec(nextFiringEvent.getTime())+" own time: "+getNode().getNodeTimeInMillisec()+" hash: "+nextFiringEvent.toString());
		}*/
		return Math.abs(phase);
	}
}
