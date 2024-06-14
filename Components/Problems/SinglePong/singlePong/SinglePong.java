package singlePong;

import java.util.Hashtable;

import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.XMLFieldEntry;

/**
 * Represents single player tennis game, which belongs to arcade genre.
 *
 * More information about this game http://en.wikipedia.org/wiki/Pong
 *   
 * @author Sergii Zhevzhyk
 *
 */
public class SinglePong extends AbstractSingleProblem {

	private SinglePongParameters parameters = new SinglePongParameters();
	
	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		// load parameters
		parameters.initialize(getProperties(), getRandom());
		
		// initialize the state of the game
		SinglePongState state = new SinglePongState(parameters);
		state.setCandidate(candidate);
		state.setWithMonitor(false);
		state.setWithPause(false);
		
		SinglePongServer server = new SinglePongServer(parameters, state);
		
		server.runSimulation(null);
		return server.getResult();
	}

		
	/* (non-Javadoc)
	 * @see core.AbstractSingleProblem#replayWithVisualization(core.AbstractRepresentation)
	 */
	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		// load parameters
		parameters.initialize(getProperties(), getRandom());
		
		// initialize the state of the game
		SinglePongState state = new SinglePongState(parameters);
		state.setCandidate(candidate);
		
		new SinglePongDisplay(parameters, state);		
	}

	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}
	
	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {

		XMLFieldEntry inputnumber = requirements.get("inputnumber");
		int input = 3;
		
		inputnumber.setValue(Integer.toString(input));

		return requirements;
	}
}
