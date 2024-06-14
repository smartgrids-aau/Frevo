package fehervari.robotsoccer;

import java.util.Hashtable;
import java.util.List;

import core.AbstractMultiProblem;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

public class RobotSoccer extends AbstractMultiProblem {

	/** Runs the simulation */
	public List<RepresentationWithScore> evaluateFitness(
			AbstractRepresentation[] candidates) {

		// load parameters
		ParameterSet parameters = loadParameters(candidates);

		// create new server
		SoccerServer server = new SoccerServer(parameters,getRandom());

		// run simulation without display
		server.runSimulation(null);

		// return fitness
		return server.getResults();
	}

	/** Runs the simulation with graphical display */
	public void replayWithVisualization(AbstractRepresentation[] candidates) {
		// load parameters
		ParameterSet parameters = loadParameters(candidates);

		// create display which controls the simulation
		new SoccerDisplay(parameters,getRandom());
	}
	
	/** Loads the necessary parameters from the properties map. */
	private ParameterSet loadParameters(AbstractRepresentation[] candidates) {
		// create new set
		ParameterSet pset = new ParameterSet(candidates);
		
		// load players per team
		XMLFieldEntry playersPerTeamEntry = getProperties().get("playersPerTeam");
		
		if (playersPerTeamEntry != null) {
			pset.setPlayersPerTeam(Integer.parseInt(playersPerTeamEntry.getValue()));
		}
		
		// load evaluation time
		XMLFieldEntry evaltimeEntry = getProperties().get("evaluation_time");
		
		if (evaltimeEntry != null) {
			pset.setSimulationTimeSec(Integer.parseInt(evaltimeEntry.getValue()));
		}
		
		// load simulation steps per second
		XMLFieldEntry simstepspersecondEntry = getProperties().get("simulation_steps_per_second");
		
		if (simstepspersecondEntry != null) {
			pset.setSimulationStepsPerSecond(Integer.parseInt(simstepspersecondEntry.getValue()));
		}
			
		return pset;
	}
	
	public Hashtable<String, XMLFieldEntry> adjustRequirements(Hashtable<String, XMLFieldEntry> requirements, Hashtable<String, XMLFieldEntry> properties) {
		// adjust input size based on sensory settings
		XMLFieldEntry inputn = requirements.get("inputnumber");
		
		inputn.setValue(Integer.toString(SoccerRobot.CAMERA_RESOLUTION*5));
		
		return requirements;
	}

}
