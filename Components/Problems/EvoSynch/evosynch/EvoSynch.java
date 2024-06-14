package evosynch;

import graphics.JChart2DComponent;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import utils.StatKeeper;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.XMLFieldEntry;

/** Simulates a number of PCO Nodes to reach synchrony */
public class EvoSynch extends AbstractSingleProblem {

	/** Number of different nodes in the simulation */
	private int NUMBER_OF_NODES = 5;

	/** Average number of connected nodes */
	private float AVERAGE_NODE_DEGREE = 3f;

	/** Length of the simulation in seconds */
	private int SIMULATION_LENGTH_SEC = 20;

	/** Length of one simulation step in seconds */
	public static float SIMULATION_STEP_LENGTH_SEC = 0.01f;

	/** Number of evaluation runs. */
	public static int NUMBER_OF_TRIALS = 1;

	/** The maximum propagation delay in seconds. */
	static float PROPAGATION_DELAY_SEC = 0.05f;

	/** List of all nodes */
	private ArrayList<Node> nodes = new ArrayList<Node>();

	private Random generator;

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		// create network of nodes
		loadParameters();
		// run simulation
		return runSimulation(candidate, null);
	}

	private double runSimulation(AbstractRepresentation candidate,
			JChart2DComponent chart) {
		//double connectionChance = (double) AVERAGE_NODE_DEGREE
		//		/ (double) NUMBER_OF_NODES;

		// get the total number of simulation steps
		long stepnumber = (long) Math.ceil(SIMULATION_LENGTH_SEC
				/ SIMULATION_STEP_LENGTH_SEC);

		int trialnum = NUMBER_OF_TRIALS;

		ArrayList<StatKeeper> timerdata = new ArrayList<StatKeeper>();

		if (chart != null) {
			trialnum = 1;

			// create statkeepers
			for (int i = 0; i < NUMBER_OF_NODES; i++) {
				StatKeeper sk = new StatKeeper(true, "time", "clock (sec)");
				timerdata.add(sk);
				chart.addstatkeeper(sk);
			}
		}

		float sum_fitness = 0;

		for (int trial = 0; trial < trialnum; trial++) {
			// generate nodes
			nodes.clear();
			for (int i = 0; i < NUMBER_OF_NODES; i++) {
				nodes.add(new Node(candidate.clone(), generator.nextFloat(),this));
			}

			NodeLinker.linkNodes(nodes, (int) AVERAGE_NODE_DEGREE, true);
//			// only connected networks are considered
//			boolean isNetworkConnected = false;
//			int attempt = 0;
//			while (!isNetworkConnected) {
//				attempt++;
//				if (attempt == 100) {
//					throw new Error("Cannot generate graph!");
//				}
//				// generate random topology
//				for (int i = 0; i < NUMBER_OF_NODES; i++) {
//					Node n = nodes.get(i);
//					// remove all connections
//					n.removeAllConnections();
//					// generate new connections
//					for (Node other : nodes) {
//						// no self connection
//						if (other == n)
//							continue;
//
//						if (generator.nextFloat() <= connectionChance) {
//							// connect nodes
//							n.connectNode(other);
//						}
//					}
//				}
//				
//				//check topology
//				//reset marks
//				for (Node n:nodes)
//					n.isMarked = false;
//				
//				Queue<Node> markingQueue = new LinkedList<Node>();
//				
//				markingQueue.add(nodes.get(0));
//				
//				while (!markingQueue.isEmpty()) {
//					Node n = markingQueue.poll();
//					//mark this node
//					n.isMarked = true;
//					//enqueue node's connections
//					for (Node on:n.connectedNodes) {
//						markingQueue.add(on);
//					}
//				}
//				
//				//check markings
//				isNetworkConnected = true;
//				for (Node n:nodes) {
//					if (!n.isMarked) {
//						isNetworkConnected = false;
//						break;
//					}
//				}				
//			}
			
			// run simulation
			// float sim_fitness = 0;
			// float lastMaxDistance = 0;
			long biggestgap = 0;
			long currentgap = 0;
			for (long simStep = 0; simStep < stepnumber; simStep++) {
				// register new time values
				if (chart != null) {
					for (int i = 0; i < NUMBER_OF_NODES; i++) {
						timerdata.get(i).add(nodes.get(i).getOwnTimerValue());
					}
				}

				for (Node n : nodes) {
					// nodes increment their timers
					n.incrementTimer();
				}

				boolean notFired = true;
				// check nodes if they fire
				for (Node n : nodes) {
					// nodes notify each other
					if (n.checkTimer())
						notFired = false;
				}

				if (notFired)
					currentgap++;
				else {
					if (currentgap > biggestgap) {
						biggestgap = currentgap;
					}
					currentgap = 0;
				}

				for (Node n : nodes) {
					// nodes update themselves if they received a pulse
					n.updateOwnTimer();
				}

				// calculate fitness by getting the maximum distance
				/*
				 * float maxDistance = 0; for (int an = 0; an < nodes.size() -
				 * 1; an++) { for (int bn = an + 1; bn < nodes.size(); bn++) {
				 * // get distance float timeA =
				 * nodes.get(an).getOwnTimerValue(); float timeB =
				 * nodes.get(bn).getOwnTimerValue();
				 * 
				 * float distanceNormal = Math.abs(timeA - timeB);
				 * 
				 * float distanceReverted = 1.0f - Math.max(timeA, timeB) +
				 * Math.min(timeA, timeB);
				 * 
				 * float distance = Math.min(distanceReverted, distanceNormal);
				 * 
				 * if (distance > maxDistance) maxDistance = distance; } }
				 * 
				 * sim_fitness += maxDistance; lastMaxDistance = maxDistance;
				 */

			}// end of one simulation
				// sim_fitness /= stepnumber;
				// sum_fitness += sim_fitness;
			// sum_fitness += lastMaxDistance;
			sum_fitness += biggestgap;

		}
		// return -sum_fitness/NUMBER_OF_TRIALS;
		// return 1 - (sum_fitness / NUMBER_OF_TRIALS);
		return sum_fitness / NUMBER_OF_TRIALS;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		loadParameters();

		// draw only the first 2 nodes
		JChart2DComponent chart = new JChart2DComponent(NUMBER_OF_NODES);

		double fitness = runSimulation(candidate, chart);
		
		System.out.println("Simulation finished with fitness: "+fitness);

		JFrame resultsframe = new JFrame("Synch Results");

		resultsframe.add(chart);
		chart.updateChart();
		resultsframe.setLocationRelativeTo(null);
		resultsframe.setMinimumSize(new Dimension(500, 300));
		resultsframe.setVisible(true);

	}

	/** Loads the necessary parameters. */
	private void loadParameters() {
		generator = getRandom();

		// get number of nodes
		XMLFieldEntry numberofnodesentry = getProperties().get("number_of_nodes");
		if (numberofnodesentry != null)
			NUMBER_OF_NODES = Integer.parseInt(numberofnodesentry.getValue());

		// get connection chance
		XMLFieldEntry averageNodeDegree = getProperties()
				.get("average_node_degree");
		if (averageNodeDegree != null)
			AVERAGE_NODE_DEGREE = Integer
					.parseInt(averageNodeDegree.getValue());

		// get the length of the simulation
		XMLFieldEntry simulationLength = getProperties().get(
				"simulation_length_sec");
		if (simulationLength != null)
			SIMULATION_LENGTH_SEC = Integer.parseInt(simulationLength
					.getValue());

		// get the length of one simulation step
		XMLFieldEntry simulationStepLength = getProperties().get(
				"simulation_step_sec");
		if (simulationStepLength != null)
			SIMULATION_STEP_LENGTH_SEC = Float.parseFloat(simulationStepLength
					.getValue());

		// get number of trials
		XMLFieldEntry numberOfTrials = getProperties().get("number_of_evaluations");
		if (numberOfTrials != null)
			NUMBER_OF_TRIALS = Integer.parseInt(numberOfTrials.getValue());

		// get the length of the propagation delay
		XMLFieldEntry propagationDelayLength = getProperties().get(
				"propagation_delay_sec");
		if (propagationDelayLength != null)
			PROPAGATION_DELAY_SEC = Float.parseFloat(propagationDelayLength
					.getValue());
	}
	
	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

}
