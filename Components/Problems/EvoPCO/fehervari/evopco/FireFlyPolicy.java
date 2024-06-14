package fehervari.evopco;

import net.tinyos.prowler.Node;


public class FireFlyPolicy extends OscillatorApplication {
	
	/**
	 * store period-data from max. 10 different nodes (default value = 10)<br>
	 * (default value = 10)
	 */
	private final int RATECAL_MAX_NODES = 10;
	
	/**
	 * Defines the amount of lost messages. If more than MAX_LOST_MESSAGES are lost,
	 * then it can be assumed that the node no longer exists => entry can be removed
	 */
	private int ratecal_lost_msg_cnt[] = new int[RATECAL_MAX_NODES];
	
	/**
	 * Defines the start-index of the ringbuffer for every node-entry.
	 */
	private int ratecal_start_index[] = new int[RATECAL_MAX_NODES];
	
	/**
	 * Defines the end-index of the ringbuffer for every node-entry.
	 */
	private int ratecal_end_index[] = new int[RATECAL_MAX_NODES];
	
	/**
	 * Stores the nodeId for every node-entry.
	 * Is needed to distinguish between the different nodes for clock-rate
	 * calibration.
	 */
	private int ratecal_nodeId[] = new int[RATECAL_MAX_NODES];
	
	/**
	 * Defines an empty entry in the ringbuffer.
	 * Is need for the clock-rate calibration.
	 * (default value = -1)
	 */
	private final int RATECAL_NO_NODEENTRY = -1;
	
	/**
	 * Clock-rate calibration should be done over 8 periods.<br>
	 * (default value = 8)
	 */
	private final int RATECAL_MAX_PERIODS = 8;
	
	/**
	 * Stores the absolute timestamp (phase) for every sync-message in the receiver's granularity.
	 * Is needed for clock-rate calibration.
	 */
	private long ratecal_ring_buffer_recvTimestamp[][] = new long[RATECAL_MAX_NODES][RATECAL_MAX_PERIODS];
	
	/**
	 * Stores the timestamp of the sender. Note that the ticks of the timestamp is based on the sender's
	 * granularity. Is needed for clock-rate calibration.
	 */
	private long ratecal_ring_buffer_senderTimestamp[][] = new long[RATECAL_MAX_NODES][RATECAL_MAX_PERIODS];
	
	/**
	 * Stores the latest static offset of the sender-nodes.
	 */
	private long ratecal_ring_buffer_latestStaticOffset[] = new long[RATECAL_MAX_NODES];
	
	/** Refractory period length coefficient */
	private final float REFRACTORY_COEFF = 0.2f;
	
	/** Coefficient on jumping */
	private final float JUMPING_COEFF = 1.2f;
	
	public FireFlyPolicy(Node node, long delayed_activation_time_ticks, EvoPCO parent) {
		super(node, parent); 

		// init variables for clock rate correction
		for (int i = 0; i < RATECAL_MAX_NODES; i++) {
			ratecal_lost_msg_cnt[i] = 0;
			ratecal_start_index[i] = 0;
			ratecal_end_index[i] = 0;
			ratecal_nodeId[i] = RATECAL_NO_NODEENTRY; // 
			for (int j = 0; j < RATECAL_MAX_PERIODS; j++) {
				ratecal_ring_buffer_recvTimestamp[i][j] = 0;
				ratecal_ring_buffer_senderTimestamp[i][j] = 0;
				ratecal_ring_buffer_latestStaticOffset[i] = 0;
			}
		}
		
		// Schedule next Firing
		nextFiringEvent = new FiringEvent(node.getNodeTime() + delayed_activation_time_ticks, this);
		node.addEvent(nextFiringEvent);
	}
	
	public void receiveMessage(Object message, Node sender) {
		// receive message
		//long ptime = parent.getTime();
		//System.out.println("Node "+getNode().getId()+" received at "+ptime);
		
		// get own expected firing time in ticks
		long oldfiringtime_tick = nextFiringEvent.getTime();
		
		long otime = getNode().getNodeTime();
		
		// get own phase time in ms (elapsed time since last beep)
		long phasetime_ms = parent.SYNC_INTERVAL_ms - EvoPCO.convertTicksToMillisec(oldfiringtime_tick - otime);
		
		// check refractory period, do not jump
		if (phasetime_ms < ((float)(parent.SYNC_INTERVAL_ms) * REFRACTORY_COEFF)) {
			// do nothing
			return;
		}
		
		// cancel previous message
		parent.cancelEvent(nextFiringEvent);
		
		// schedule new firing event
		long newfiringtime_ms = (long)(phasetime_ms * JUMPING_COEFF);
		
		if (newfiringtime_ms >= parent.SYNC_INTERVAL_ms) {
			//fire immediately
			fire();
			
			// next beep after a whole period (already in fire)
			//nextFiringEvent = new FiringEvent(getNode().getNodeTime() + EvoPCO.convertMillisecToTicks(parent.SYNC_INTERVAL_ms), this);
		} else {
			// next beep as calculated
			nextFiringEvent = new FiringEvent(getNode().getNodeTime() - EvoPCO.convertMillisecToTicks(phasetime_ms) + EvoPCO.convertMillisecToTicks(newfiringtime_ms), this);
		}
		getNode().addEvent(nextFiringEvent);
	}
	
	public synchronized void fire() {
		// send whatever message
		sendMessage(new Message("beep"));
		
		// register firing time
		parent.registerFire(this.getNode().getId());
		
		// schedule next firing
		nextFiringEvent = new FiringEvent(this.getNode().getNodeTime()
				+ EvoPCO.convertMillisecToTicks(parent.SYNC_INTERVAL_ms),this);
		this.getNode().addEvent(nextFiringEvent);
	}
}
