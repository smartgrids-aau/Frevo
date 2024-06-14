package pong;

import java.util.List;

import core.AbstractMultiProblem;
import core.AbstractRepresentation;

/**
 * Represents two-dimensional tennis game and belongs to arcade genre. 
 * This implementation includes multiple players which should be organized 
 * using self-organizing principles.
 * 
 * More information about this game http://en.wikipedia.org/wiki/Pong  
 *   
 * @author Sergii Zhevzhyk
 */
public class Pong extends AbstractMultiProblem {

	private PongParameters parameters = new PongParameters();
	
	/* (non-Javadoc)
	 * @see core.AbstractMultiProblem#evaluateFitness(core.AbstractRepresentation[])
	 */
	@Override
	public List<RepresentationWithScore> evaluateFitness(
			AbstractRepresentation[] candidates) {
		// load parameters
		parameters.initialize(getProperties(), getRandom());
	
		PongState state = new PongState(parameters);
		state.setCandidates(candidates);
		state.setWithMonitor(false);
		state.setWithPause(false);
		
		PongServer server = new PongServer(parameters, state);
		
		server.runSimulation(null);
		
		return server.getResults();
	}
	
	/** Runs the simulation with graphical display */
	@Override
	public void replayWithVisualization(AbstractRepresentation[] candidates) {
		// load parameters
		parameters.initialize(getProperties(), getRandom());
		
		// inititalize the state of the game
		PongState state = new PongState(parameters);
		state.setCandidates(candidates);
		// create display which controls the simulation
		new PongDisplay(parameters, state);
	}
}
