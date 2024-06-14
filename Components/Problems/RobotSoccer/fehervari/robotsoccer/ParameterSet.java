package fehervari.robotsoccer;

import core.AbstractRepresentation;

/** Class holding the parameters specified for the actual simulation. */
public class ParameterSet {
	
	/** Default size of each team. */
	private static final int DEFAULT_PLAYERS_PER_TEAM = 11;

	/** Default simulation length in seconds. */
	private static final int DEFAULT_SIMULATION_LENGTH_SEC = 60;
	
	/** Default value for the number of simulation steps per real-time second. */
	private static final int DEFAULT_SIMULATION_STEP_PER_SECOND = 10;

	/** Controller representations for each team (homogeneous). */
	public AbstractRepresentation[] nets = new AbstractRepresentation[2];
	
	/** Defines the number of players per team. */
	private int players_per_team = DEFAULT_PLAYERS_PER_TEAM;
	
	private int simulation_time_sec = DEFAULT_SIMULATION_LENGTH_SEC;
	
	private int simulation_steps_per_second = DEFAULT_SIMULATION_STEP_PER_SECOND;

	public ParameterSet(AbstractRepresentation[] candidates) throws IllegalArgumentException {
		// verify correct number of candidates
		if (candidates.length != 2)
			throw new IllegalArgumentException ("ERROR: Simulation received "+candidates.length+" candidates instead of 2!");
		
		// assign candidates
		this.nets[0] = candidates[0];
		this.nets[1] = candidates[1];
	}
	
	/** Sets the number of players in the team. */
	public void setPlayersPerTeam(int players) {
		this.players_per_team = players;
	}
	
	/** Returns the number of players per team. */
	public int getPlayersPerTeam() {
		return this.players_per_team;
	}

	/** Sets the simulation length to the given value. */
	public void setSimulationTimeSec(int simulationLength) {
		this.simulation_time_sec = simulationLength;	
	}
	
	/** Returns the length of the simulation in seconds. */
	public int getSimulationTimeSec() {
		return this.simulation_time_sec;
	}

	/**
	 * @return the simulation_steps_per_second
	 */
	public int getSimulationStepsPerSecond() {
		return simulation_steps_per_second;
	}

	/**
	 * @param simulation_steps_per_second the simulation_steps_per_second to set
	 */
	public void setSimulationStepsPerSecond(int simulation_steps_per_second) {
		this.simulation_steps_per_second = simulation_steps_per_second;
	}
}
