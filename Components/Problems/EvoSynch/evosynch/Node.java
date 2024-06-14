package evosynch;

import java.util.ArrayList;
import java.util.HashSet;

import core.AbstractRepresentation;

/** A pulse coupled oscillator node. */
public class Node {
	
	/** Indicates the current position of the own timer */
	private float timer = 0;
	
	/** A list of other <tt>Nodes<tt> that can hear this node */
	ArrayList<Node> connectedNodes = new ArrayList<Node>();
	
	/** The "brain" of the node. */
	private AbstractRepresentation controller;
	
	private HashSet<Integer> receivedPulses = new HashSet<Integer>();
	
	/** List of input values for the jumping policy controller. */
	private ArrayList<Float> input = new ArrayList<Float>();
	
	/** List of output values for the jumping policy controller. */
	private ArrayList<Float> output;
	
	/** Maximum propagation delay in time steps */
	private int max_propagation_delay_step_length;
	
	private EvoSynch parent;
	
	/** Used for checking connectivity */
	boolean isMarked = false;
	
	/** Constructor method of a Node. Requires a representation to control jumping policy and an initial timer value. */
	public Node(AbstractRepresentation representation, float startTime, EvoSynch parent) {
		this.controller = representation;
		this.timer = startTime;
		this.parent = parent;
		
		max_propagation_delay_step_length = (int)(EvoSynch.PROPAGATION_DELAY_SEC / EvoSynch.SIMULATION_STEP_LENGTH_SEC);
	}
	
	public void incrementTimer() {
		if (timer >= 1.0) {
			//reset own timer
			timer = 0;
		} else
			// increments timer
			timer += EvoSynch.SIMULATION_STEP_LENGTH_SEC;
	}
	
	/** Checks the internal timer if it reached the threshold. Returns true if node fires. */
	public boolean checkTimer() {
		if (timer >= 1.0) {
			// Fire: notify all connected nodes
			for (Node other:connectedNodes) {
				other.sendPulse();
			}
			return true;
		}
		return false;
	}
	
	/** Updates own timer if a pulse has been detected */
	public void updateOwnTimer() {
		//check if we received a pulse
		Integer now = 0;
		if ((receivedPulses.contains(now)) && (!isInRefractory())){
			input.clear();
			// add own timer value
			input.add(timer);
			// get output
			output = controller.getOutput(input);
			// overwrite own timer state
			timer = output.get(0);
		}
		
		receivedPulses.remove(now);
		
		//decrement all other
		for (int i=1;i<=max_propagation_delay_step_length;i++) {
			if (receivedPulses.remove(Integer.valueOf(i)))
				receivedPulses.add(Integer.valueOf(i - 1));
		}
		
	}
	
	/** Adds the given node to the list of connected nodes. */
	public void connectNode(Node other) {
		connectedNodes.add(other);
	}
	
	/** Notifies this node on an incoming impulse */
	public void sendPulse() {
		// add random propagation delay between 1..max
		receivedPulses.add(parent.getRandom().nextInt(max_propagation_delay_step_length)+1);
	}
	
	/** Returns own timer state */
	public float getOwnTimerValue() {
		return timer;
	}
	
	private boolean isInRefractory() {
		return (timer < 0.2);
	}
	
	/** Removes all connections. */
	public void removeAllConnections() {
		connectedNodes.clear();
	}

}
