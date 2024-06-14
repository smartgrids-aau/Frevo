package fehervari.evopco;

import graphics.JChart2DComponent;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import net.tinyos.prowler.CrystalOscillatorModel;
import net.tinyos.prowler.Event;
import net.tinyos.prowler.GaussianRadioModel;
import net.tinyos.prowler.Mica2Node;
import net.tinyos.prowler.Node;
import net.tinyos.prowler.RadioModel;
import net.tinyos.prowler.RayleighRadioModel;
import net.tinyos.prowler.Simulator;
import utils.StatKeeper;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.XMLFieldEntry;

public class EvoPCO extends AbstractSingleProblem {

	/** Number of simulation runs */
	private int NUMBER_OF_SIMULATION_RUNS = 1;

	/** Type of radio model in the simulation */
	private fehervari.evopco.RadioModel RADIO_MODEL = fehervari.evopco.RadioModel.RAYLEIGH_RADIO_MODEL;

	/** Network topology */
	private NetworkTopology NETWORK_TOPOLOGY = NetworkTopology.REGULAR_GRID_TOPOLOGY;

	/** Number of nodes in the simulation */
	private int NUMBER_OF_NODES;
	
	/** Chance for a node to have false receptions */
	private float FALSE_POSITIVE_CHANCE = 0.00f;

	/**
	 * Defines the standard synchronization-interval in milliseconds.<br>
	 * (default value = 1000 ms)
	 */
	public long SYNC_INTERVAL_ms = 1000;
	
	public long SYNC_INTERVAL_tick = convertMillisecToTicks(SYNC_INTERVAL_ms);

	/** Length of the simulation in seconds */
	public int SIMULATION_LENGTH = 100;

	/** Length of the area. The experiments area is a square. */
	private float AREA_SIDE_LENGTH = 100;

	/**
	 * Initial clock drift for all clocks in ppm. The real clock drift is a
	 * random value between 0 and INITIAL_CLOCK_DRIFT_PPM Default value: 100000
	 * (with rate correction) Default value: 1000 (without rate correction) =
	 * \rho=0.001 Normal good clocks have about 100ppm, 0 = no drift
	 */
	private static long INITIAL_CLOCK_DRIFT_PPM = 100;// 100;

	/**
	 * If true, then the clock drift additionally depends on the actual
	 * temperature
	 */
	private static final boolean TEMPERATURE_DEPENDENT = false;

	private JumpingPolicy JUMPING_POLICY = JumpingPolicy.FIREFLY_POLICY;

	// keep track of nodes
	List<Node> nodes = new ArrayList<Node>();

	Simulator simulator;

	/** true for visualizing */
	private boolean isGraphics = false;

	private double current_fitness = 0;
	private int fitness_counter = 0;

	/** Chart to display time-series */
	private JChart2DComponent jChart;
	
	private OscillatorApplication[] applications;

	TimeMemory[] timestamps;

	private boolean measureNetwork = false;
	
	private AbstractRepresentation candidate;

	private HashSet<Integer>[] receivedFromNode;

	@SuppressWarnings("unchecked")
	@Override
	public double evaluateCandidate(AbstractRepresentation candidate) {
		// load parameters
		loadParameters();
		
		this.candidate = candidate;

		// construct new simulator
		simulator = new Simulator();

		// set random object for repeatability
		Simulator.random = generator;

		if (isGraphics) {
			// run only once
			NUMBER_OF_SIMULATION_RUNS = 1;

			// initialize chart
			jChart = new JChart2DComponent(NUMBER_OF_NODES);

			// create statkeepers
			for (int i = 0; i < NUMBER_OF_NODES; i++) {
				StatKeeper sk = new StatKeeper(true, "phase ("+i+")", "clock (ms)");
				jChart.addstatkeeper(sk);
			}
		}

		double fitness = 0;

		// measure nodes
		if (measureNetwork) {

			// erase cache
			simulator.clear();
			simulator.reset();
			nodes.clear();

			// allocate storage
			receivedFromNode = new HashSet[NUMBER_OF_NODES];
			for (int i = 0; i < NUMBER_OF_NODES; i++) {
				receivedFromNode[i] = new HashSet<Integer>();
			}

			// creating the desired radio model
			RadioModel radioModel = configureRadioModel();

			createNodesWithTopology(radioModel);

			// creating all nodes
			long timecounter = 0;
			Node tempNode = simulator.firstNode;
			while (tempNode != null) {
				// assign controller to each node

				new TestPolicy(tempNode, convertMillisecToTicks(timecounter*1000), this);
				timecounter++;

				// create random clock drift
				double clock_drift_ppm = 0;

				CrystalOscillatorModel crystalModel = new CrystalOscillatorModel(
						CrystalOscillatorModel.CRYSTAL_USER_DEFINED);

				crystalModel.setInitialClockDriftPPM(clock_drift_ppm);
				crystalModel.setInflectionTemperatureCelsius(-25);
				crystalModel.setTemperatureCoefficients(600, -2, 0);
				tempNode.applyCrystalOscillatorModel(crystalModel);
				tempNode.setTemperatureDependence(TEMPERATURE_DEPENDENT);

				// add to list
				nodes.add(tempNode);

				// step to next
				tempNode = tempNode.nextNode;
			}

			// this call is a must after configuring all nodes
			radioModel.updateNeighborhoods();
			
			simulator.run((timecounter+1)*1000);
			
			// calculate average node degree
			if (measureNetwork) {
				double sum = 0;
				for (int i = 0; i < NUMBER_OF_NODES; i++)
					sum += receivedFromNode[i].size();

				sum /= NUMBER_OF_NODES;

				System.out.println("Average node degree: " + sum);
			}
		}		

		// for each simulation run
		for (int sim_run = 0; sim_run < NUMBER_OF_SIMULATION_RUNS; sim_run++) {

			// reset simulator
			simulator.clear();

			// remove nodes
			nodes.clear();

			// reset fitness
			current_fitness = 0;
			fitness_counter = 0;

			// reset last firing data
			timestamps = new TimeMemory[NUMBER_OF_NODES];
			for (int i = 0; i < NUMBER_OF_NODES; i++)
				timestamps[i] = new TimeMemory(3);
			
			RadioModel radioModel = configureRadioModel();

			// create nodes with topology
			createNodesWithTopology(radioModel);

			// assign working sets to nodes
			setupNodes();

			// this call is a must after configuring all nodes
			radioModel.updateNeighborhoods();
			
			// schedule random false positive detections
			if (FALSE_POSITIVE_CHANCE > 0.0f) {
				// define repeating event class 
				class RepeatingEvent extends Event { 
					public RepeatingEvent(long time) {
						super(time);
					}
					public void execute() {
						// roll on all nodes
						for (Node node:nodes) {
							if (generator.nextFloat() < FALSE_POSITIVE_CHANCE) {
								FalseDetectionEvent f = new FalseDetectionEvent(generator
										.nextInt((int) SYNC_INTERVAL_tick), applications[node.getId()]);
								node.addEvent(f);
							}
						}
												
						// reschedule itself every period
						simulator.addEvent(new RepeatingEvent(simulator.getSimulationTime()+SYNC_INTERVAL_tick));
					}
				}
				// schedule first event with a little delay
				simulator.addEvent(new RepeatingEvent(simulator.getSimulationTime()+100));
			}

			// schedule timer event to record display data
			if (isGraphics) {
				class TimerEvent extends Event {

					public TimerEvent(long time) {
						super(time);
					}

					public void execute() {
						// record node data to chart
						for (int i = 0; i < nodes.size(); i++) {
							// Node node = nodes.get(i);
							StatKeeper sk = jChart.getStatKeeper(i);
							sk.add(applications[i].getCurrentPhase());

							/*sk.add(firingState[i] ? ((1000 / NUMBER_OF_NODES) * (nodes
									.get(i).getId() + 1)) : 0);
							// reset firing state
							firingState[i] = false;*/
						}

						// reschedule itself
						Event tEvent = new TimerEvent(
								simulator.getSimulationTime()
										+ (Simulator.ONE_SECOND / 1000));
						simulator.addEvent(tEvent);
					}
				}

				// schedule timer event
				Event tEvent = new TimerEvent(simulator.getSimulationTime());
				simulator.addEvent(tEvent);
			}

			// run simulation
			simulator.run(SIMULATION_LENGTH);

			// calculate fitness
			double norm = 2.0 - Math.pow(2, fitness_counter);
			fitness += (current_fitness / norm);

		}

		// return average of all runs
		return fitness / NUMBER_OF_SIMULATION_RUNS;
	}

	private RadioModel configureRadioModel() {
		RadioModel radioModel = null;

		switch (RADIO_MODEL) {
		case GAUSSIAN_RADIO_MODEL:
			radioModel = new GaussianRadioModel(simulator);
			break;
		case RAYLEIGH_RADIO_MODEL:
			radioModel = new RayleighRadioModel(simulator);
			break;
		}

		return radioModel;
	}

	private void createNodesWithTopology(RadioModel radioModel) {

		// Configure network topology

		switch (NETWORK_TOPOLOGY) {
		case RANDOM_TOPOLOGY:
			// random topology over the area
			try {
				simulator.createNodes(Mica2Node.class, radioModel, 0,
						NUMBER_OF_NODES, AREA_SIDE_LENGTH, 0);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.err.println("ERROR: Failed to generate network!");
			}
			break;
		case REGULAR_GRID_TOPOLOGY:
			// calculate the number of nodes on each side
			int nodes_per_side = (int) Math.ceil(Math.sqrt(NUMBER_OF_NODES));
			int x = 0;
			int y = 0;
			// node ID counter
			int id = 0;
			// spacing comes from area size
			int spacing = (int) (AREA_SIDE_LENGTH / nodes_per_side);

			// allocate nodes in a square array
			for (int n = 0; n < NUMBER_OF_NODES; n++) {
				try {
					simulator.createNode(Mica2Node.class, radioModel, id++,
							(float) x, (float) y, 0);

				} catch (Exception e) {
					e.printStackTrace();
				}
				x += spacing;

				if (x == (spacing * nodes_per_side)) {
					x = 0;
					y += spacing;
				}
			}

			break;
		}// end switch
	}

	private void setupNodes() {
		// creating all nodes
		applications = new OscillatorApplication[NUMBER_OF_NODES];
		
		Node tempNode = simulator.firstNode;

		while (tempNode != null) {
			// assign controller to each node
			OscillatorApplication application = null;
			switch (JUMPING_POLICY) {
			case FIREFLY_POLICY:
				application = new FireFlyPolicy(
						tempNode,
						generator
								.nextInt((int) convertMillisecToTicks(SYNC_INTERVAL_ms)),
						this);
				break;
			case IE_POICY:
				application = new IEPolicy(
						tempNode,
						generator
								.nextInt((int) convertMillisecToTicks(SYNC_INTERVAL_ms)),
						this);
				break;
			case EVOLVED_POLICY:
				application = new EvolvedPolicy(
						tempNode,
						generator
								.nextInt((int) convertMillisecToTicks(SYNC_INTERVAL_ms)),
						this,candidate);
				break;
			}
			
			applications[tempNode.getId()] = application;


			// create random clock drift
			double clock_drift_ppm = (long) (generator.nextDouble() * 2
					* INITIAL_CLOCK_DRIFT_PPM - INITIAL_CLOCK_DRIFT_PPM);

			CrystalOscillatorModel crystalModel = new CrystalOscillatorModel(
					CrystalOscillatorModel.CRYSTAL_USER_DEFINED);
			// best temperature-drift approximation of atmega1281 internal
			// calibrated RC-oscillator (fabrication accuracy at 8MHZ =
			// +-10%)
			crystalModel.setInitialClockDriftPPM(clock_drift_ppm);
			crystalModel.setInflectionTemperatureCelsius(-25);
			crystalModel.setTemperatureCoefficients(600, -2, 0);
			tempNode.applyCrystalOscillatorModel(crystalModel);
			tempNode.setTemperatureDependence(TEMPERATURE_DEPENDENT);

			// add to list
			nodes.add(tempNode);

			// step to next
			tempNode = tempNode.nextNode;
		}
	}

	/** Calculates fitness based on the stored timestamp values */
	private double calculateFitness() {
		// merge node time data
		ArrayList<Long> alldata = new ArrayList<Long>();
		for (int i = 0; i < NUMBER_OF_NODES; i++) {
			alldata.addAll(timestamps[i].getData());
		}

		// sort data
		Collections.sort(alldata);

		// place them on a circle 3 times
		long origin = alldata.get(0);

		for (int i = 0; i < alldata.size(); i++) {
			long num = alldata.get(i);
			num = (num - origin) % SYNC_INTERVAL_ms;
			alldata.set(i, num);
		}

		// find longest silence
		long longestDistance = (SYNC_INTERVAL_ms - alldata
				.get(alldata.size() - 1)) + alldata.get(0);
		for (int i = 1; i < alldata.size(); i++) {
			long d = alldata.get(i) - alldata.get(i - 1);
			if (d > longestDistance) {
				longestDistance = d;
			}
		}

		double result = (double) longestDistance / SYNC_INTERVAL_ms;
		return result;
	}

	public void replayWithVisualization(AbstractRepresentation candidate) {
		// turn on graphics
		isGraphics = true;

		// run simulation
		double fitness = evaluateCandidate(candidate);

		// display chart
		JFrame resultsframe = new JFrame("Synch Results");

		resultsframe.add(jChart);
		jChart.updateChart();
		resultsframe.setLocationRelativeTo(null);
		resultsframe.setMinimumSize(new Dimension(500, 300));
		resultsframe.setVisible(true);

		// print fitness to standard out
		System.out.println("Fitness : " + fitness);
	}

	/** Loads parameters from the property set */
	private void loadParameters() {
		// read number of simulation runs
		XMLFieldEntry numberOfRuns = getProperties().get(
				"number_of_evaluations");
		if (numberOfRuns != null)
			NUMBER_OF_SIMULATION_RUNS = Integer.parseInt(numberOfRuns
					.getValue());

		// load radio model
		XMLFieldEntry radioModel = getProperties().get("radio_model");
		if (radioModel != null)
			RADIO_MODEL = fehervari.evopco.RadioModel.valueOf(radioModel
					.getValue());

		// load jumping policy
		XMLFieldEntry jumpingPolicy = getProperties().get("jumping_policy");
		if (jumpingPolicy != null)
			JUMPING_POLICY = fehervari.evopco.JumpingPolicy
					.valueOf(jumpingPolicy.getValue());

		// load network topology
		XMLFieldEntry topology = getProperties().get("network_topology");
		if (topology != null)
			NETWORK_TOPOLOGY = NetworkTopology.valueOf(topology.getValue());

		// load number of nodes
		XMLFieldEntry nodenumber = getProperties().get("number_of_nodes");
		if (nodenumber != null)
			NUMBER_OF_NODES = Integer.parseInt(nodenumber.getValue());

		// load simulation length
		XMLFieldEntry simulationLength = getProperties().get(
				"simulation_length_s");
		if (simulationLength != null)
			SIMULATION_LENGTH = Integer.parseInt(simulationLength.getValue());

		// load clock drift
		XMLFieldEntry clockdrift = getProperties().get("clock_drift");
		if (clockdrift != null)
			INITIAL_CLOCK_DRIFT_PPM = Integer.parseInt(clockdrift.getValue());

		// load area side length
		XMLFieldEntry areasidelength = getProperties()
				.get("area_side_length_m");
		if (areasidelength != null)
			AREA_SIDE_LENGTH = Float.parseFloat(areasidelength.getValue());
		
		// load false positive reception rate
		XMLFieldEntry falsepositiveEntry = getProperties().get("random_false_positive");
		if (falsepositiveEntry != null)
			FALSE_POSITIVE_CHANCE = Float.parseFloat(falsepositiveEntry.getValue());

	}

	/**
	 * Returns the achievable maximum fitness of this problem. A representations
	 * with this fitness value cannot be improved any further.
	 */
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

	/**
	 * Converts a time in milliseconds to the number of simulator-ticks
	 * 
	 * @param time_ms
	 *            time in ms
	 * @return the corresponding number of simulator-ticks
	 */
	static long convertMillisecToTicks(double time_ms) {
		return Math.round(time_ms * Simulator.ONE_SECOND / 1000);
	}

	/**
	 * Converts a number of simulator-ticks to a time-representation in
	 * milliseconds
	 * 
	 * @param ticks
	 *            number of simulator-ticks
	 * @return the corresponding time in ms
	 */
	static long convertTicksToMillisec(long ticks) {
		return Math.round(((double) ticks) * 1000.0 / Simulator.ONE_SECOND);
	}

	/** Cancels the given event */
	public void cancelEvent(Event event) {
		this.simulator.eventQueue.removeEvent(event);
	}

	/** Returns the current simulation time in ms */
	long getSimulationTimeMs() {
		return simulator.getSimulationTimeInMillisec();
	}
	
	/** Returns the current simulation time in simulator ticks */
	long getSimulationTimeTicks() {
		return simulator.getSimulationTime();
	}

	/** Registers the firing event coming from the provided node */
	public void registerFire(int id) {
		timestamps[id].addTimeStamp(simulator.getSimulationTimeInMillisec());

		// check if there is a need to calculate fitness
		int sum = 0;
		for (int i = 0; i < NUMBER_OF_NODES; i++) {
			sum += timestamps[i].getSize();
		}

		// store is filled
		if (sum == (3 * NUMBER_OF_NODES)) {
			// calculate fitness
			double measured = calculateFitness();

			// update fitness
			// double norm = 2.0 - Math.pow(2, fitness_counter);

			current_fitness = ((current_fitness / 2.0) + measured);
			// current_fitness /= norm;

			// System.out.println
			// ("Current fitness #"+(-1*fitness_counter)+" : "+current_fitness+" ("+f+") norm: "+norm+" time= "+simulator.getSimulationTimeInMillisec());
			 //System.out.println
			 //("measured="+measured+" fitness=");

			fitness_counter--;

			// shift memories
			for (int i = 0; i < NUMBER_OF_NODES; i++) {
				timestamps[i].shift();
			}
		}
	}

	/** Class storing the past time stamps */
	private class TimeMemory {
		private List<Long> timestamps;
		private List<Long> storage;
		private int m_size;

		public TimeMemory(int memory_size) {
			this.m_size = memory_size;
			timestamps = new ArrayList<Long>(memory_size);
			storage = new LinkedList<Long>();
		}

		public Collection<? extends Long> getData() {
			return timestamps;
		}

		/**
		 * Adds the given timestamp to the list. Returns true if the buffer is
		 * now full.
		 */
		public void addTimeStamp(long t) {
			if (timestamps.size() == m_size) {
				// add timestamp to buffer instead
				storage.add(t);
			} else {
				timestamps.add(t);
			}
		}

		/** Returns the size of the memorys */
		int getSize() {
			return timestamps.size();
		}

		/** Removes all time stamps from the memory */
		void reset() {
			timestamps.clear();
		}

		/** Removes all time stamps and fills up with the ones in the storage */
		void shift() {
			// remove processed times
			reset();

			// move top 3 elements from storage to memory
			int counter = 0;
			Iterator<Long> it = storage.iterator();
			while (it.hasNext() && (counter < 3)) {
				long num = it.next();
				it.remove();
				timestamps.add(num);
				counter++;
			}
		}
	}

	/** Notifies about a received pulse */
	public void registerReceive(int whoId, int fromId) {
		if (!measureNetwork)
			return;

		receivedFromNode[whoId].add(fromId);

	}

}
