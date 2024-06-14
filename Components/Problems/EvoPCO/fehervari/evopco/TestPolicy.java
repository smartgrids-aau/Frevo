package fehervari.evopco;

import net.tinyos.prowler.Application;
import net.tinyos.prowler.Event;
import net.tinyos.prowler.Node;

public class TestPolicy extends Application {
	
	EvoPCO parent;

	public TestPolicy(Node node, long time, EvoPCO parent) {
		super(node);

		this.parent = parent;
		
		// schedule only firing
		FiringEvent firingEvent = new FiringEvent(node.getNodeTime() + time);
		node.addEvent(firingEvent);
	}
	
	public void receiveMessage(Object message, Node sender) {
		Message m = (Message) message;
		parent.registerReceive(getNode().getId(), Integer.parseInt(m.getContent()) );
		System.out.println ("TEST: Node "+getNode().getId()+" receives from "+Integer.parseInt(m.getContent()));
	}
	
	private class FiringEvent extends Event {
		
		/** Expected firing time in simulator-ticks */
		public FiringEvent(long time) {
			super(time);
		}
		
		public void execute() {
			// fire
			sendMessage(new Message(TestPolicy.this.getNode().getId()));
			System.out.println ("TEST: Node "+TestPolicy.this.getNode().getId()+" fires at "+parent.getSimulationTimeMs());
		}
	}

}
