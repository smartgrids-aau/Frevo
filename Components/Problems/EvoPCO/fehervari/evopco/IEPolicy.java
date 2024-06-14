package fehervari.evopco;

import net.tinyos.prowler.Node;


public class IEPolicy extends OscillatorApplication {
	
	/**
	 * store period-data from max. 10 different nodes (default value = 10)<br>
	 * (default value = 10)
	 */
	private static final int RATECAL_MAX_NODES = 10;
	
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

	/** Maximum transmission delay in ms */
	private final float TAU_MAX_s = 0.035f;
	
	private double alpha;
	private double beta;
	private double kappa = 0.6;
	
	public IEPolicy(Node node, long delayed_activation_time_ticks, EvoPCO parent) {
		super(node, parent);

		//System.out.println("Will fire first at "+EvoPCO.convertTicksToMillisec(delayed_activation_time_ticks));
		
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
		
		// calculate jumping constants
		alpha = (kappa * (0.25 - TAU_MAX_s) - TAU_MAX_s) / (0.5 - TAU_MAX_s);
		beta = (1.0 - alpha) * TAU_MAX_s;
		
		// Schedule next Firing
		nextFiringEvent = new FiringEvent(node.getNodeTime() + delayed_activation_time_ticks, this);
		node.addEvent(nextFiringEvent);
		//if (getNode().getId() == 2)
		//	System.out.println ("New event registered at "+this.getNode().getNodeTimeInMillisec()+" for "+EvoPCO.convertTicksToMillisec(nextFiringEvent.getTime())+" hash: "+nextFiringEvent.toString());
	}
	
	public void receiveMessage(Object message, Node sender) {
		// receive message
		double phase = getCurrentPhase();
		
		// do not jump if it is below tau_max
		if (phase < (TAU_MAX_s))
			return;

		// cancel previous message
		parent.cancelEvent(nextFiringEvent);
		
		// calculate new phase
		double newphase;		
		if (phase <= 0.5) {
			// inhibitory jump
			newphase = alpha * phase + beta;
		} else {
			// exhibitory jump
			newphase = 1.0 - kappa*(0.5 - (2*TAU_MAX_s))*(1.0 - phase);
		}
		
		if (newphase > 1.0) {
			// fire immediately
			fire();
		} else {
			// convert phase to ms
			long phase_ms = (long)(parent.SYNC_INTERVAL_ms * newphase);		
			long remainder_ms = parent.SYNC_INTERVAL_ms - phase_ms;
			
			nextFiringEvent = new FiringEvent(getNode().getNodeTime() + EvoPCO.convertMillisecToTicks(remainder_ms),this);
			
			getNode().addEvent(nextFiringEvent);
		}		
	}
	
	public synchronized void fire() {
		// send message
		sendMessage(new Message(Integer.toString(this.getNode().getId())));
		
		// register firing time
		parent.registerFire(this.getNode().getId());
		
		//System.out.println("Node "+getNode().getId()+" is firing at "+parent.getTime());
		
		// cancel previous message
		parent.cancelEvent(nextFiringEvent);
		
		// schedule next firing
		nextFiringEvent = new FiringEvent(this.getNode().getNodeTime()
				+ EvoPCO.convertMillisecToTicks(parent.SYNC_INTERVAL_ms),this);
		this.getNode().addEvent(nextFiringEvent);
	}


}
