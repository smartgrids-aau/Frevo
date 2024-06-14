package fehervari.evopco;

import net.tinyos.prowler.Event;

public class FalseDetectionEvent extends Event {
	private OscillatorApplication application;
	
	public FalseDetectionEvent(long time, OscillatorApplication application) {
		super(time);
		this.application = application;
	}
	
	public void execute() {
		application.fire();
	}

}
