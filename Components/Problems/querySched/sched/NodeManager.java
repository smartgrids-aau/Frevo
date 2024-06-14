package sched;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

import main.FrevoMain;
import sched.Query.KEYSPACE;
import sched.Query.SubQuery;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;

public class NodeManager extends AbstractSingleProblem {

	private ArrayList<uCloud> uClouds;
	// private float[][] bandwidthmap;

	private float bandwidth = 4.518f;// GB/h (app. 10 mbit/s)

	private double minInterval;

	private Random rand = new Random();

	private boolean log = false;

	private int queriesleft;

	private int numberOfQueriesMoved = 0;

	private boolean isRandomScheduler = false;

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {

		initialize(candidate);

		runSimulation(candidate);

		return evaluateOutput();
	}

	private double evaluateOutput() {
		double fitness;
		ArrayList<Integer> stages = new ArrayList<>();
		double costs = 0;
		double stagesServed = 0;
		for (uCloud u : uClouds) {
			stages.add(u.getStagesServed());
			costs += u.getCurrentCosts();
			stagesServed += u.getStagesServed();
		}

		double stagesCost = costs / stagesServed; // avg cost per stage per
		// cloud

		Stats<Integer> stats = new Stats<Integer>(stages);
		double variance = stats.getVariance();
		double avg = stats.getMean();
		double stdev = stats.getStdDev();
		System.out.println("total costs " + costs + " stages costs: "
				+ stagesCost + " stages per cloud variance " + variance
				+ " avg stages " + avg + " stdev stages " + stdev
				+ " stages served " + stagesServed + " number of stages moved "
				+ numberOfQueriesMoved);

		System.out.println(stages);

		// fitness = 1 / stdev;
		// fitness = 1 / stagesCost;
		// fitness = stagesCost / stdev;

		// fitness = 1 / (Math.log(stdev) * stagesCost);
		fitness = 1 / (stdev * stagesCost);
		System.out.println("Fitness: " + fitness);
		return fitness;
	}

	private void runSimulation(AbstractRepresentation candidate) {
		ArrayList<Float> result;
		while (this.queriesleft > 0) {

			for (uCloud u : uClouds) {

				ArrayDeque<Query> currentQueriesCopy = u.currentQueries.clone();

				query: for (Query q : currentQueriesCopy) {

					// we have to execute query stages and tasks -- whereas
					// tasks > query stages . TODO define finished by number of
					// executed tasks. or redesign map/reduce jobs

					while (!q.isFinished()) {

						u.nextTaskRound();

						if (q.getCurrentStage() == 0) {
							// check keyspace
							if (q.getKeyspace() != KEYSPACE.ALL
									&& q.getKeyspace() != u.getKeyspace()) {

								uCloud target;
								do {
									target = uClouds.get(rand.nextInt(uClouds
											.size() - 1));
								} while (target.getKeyspace() != q
										.getKeyspace());
								// move query
								if (log) {
									System.out
											.println("keyspace not matching move from "
													+ u.getId()
													+ " to "
													+ target.getId());
								}
								numberOfQueriesMoved++;
								q.addToTotalCosts(bandwidth);
								target.currentQueries.add(q); // go to next
																// query
								u.currentQueries.remove(q);
								continue query;
							}
						}

						// if (log)
						// System.out.println("Execute query at stage: "
						// + q.getCurrentStage() + " num stages: "
						// + q.getQuerytree().length);

						Task t = executeQueryStage(q);

						result = this.isRandomScheduler ? getPlacementRandom(u,
								q) : getPlacementNeural(u, q);

						int x = (int) (result.get(0) / minInterval);
						x = x > uClouds.size() - 1 ? x - 1 : x; // correct size
																// if result is
																// 1
						// output
						// to
						// node

						if (q.isFinished()) {
							if (log) {
								System.out.println("query finished: ");
							}
							// transfer to client cloud
							uCloud clientCloud = q.getClient().getEntryPoint();
							int entrypoint = clientCloud.getId();
							if (u.getId() != entrypoint) {
								q.addToTotalCosts(bandwidth);
								clientCloud.addToQueryCost(q.getTotalCost());
								if (log) {
									System.out
											.println("move query to entry point Cloud"
													+ entrypoint
													+ " query cost "
													+ q.getTotalCost());
								}
								numberOfQueriesMoved++;
								clientCloud.increaseQueriesserved();

							} else {
								u.addToQueryCost(q.getTotalCost());
								u.increaseQueriesserved();
								if (log) {
									System.out
											.println("query at entry point Cloud "
													+ u.getId()
													+ " query cost: "
													+ q.getTotalCost());
								}
							}
							q.getClient().sendNewRequest();
							this.queriesleft--;
							u.currentQueries.remove(q);

							// there are no more stages left
						} else {
							if (x == u.getId()) {
								u.addTask(t);
								if (log) {
									System.out
											.println("local execution with cost: "
													+ q.getTotalCost());
								}
								u.increaseStagesServed();
							} else {
								// move!
								uCloud target = uClouds.get(x);
								target.addTask(t);
								q.addToTotalCosts(bandwidth);
								target.currentQueries.add(q); // move
																// entire
																// query
								u.currentQueries.remove(q);
								if (log) {
									System.out.println("move query from "
											+ u.getId() + " to Cloud " + x
											+ " at cost " + q.getTotalCost());
								}
								target.increaseStagesServed();
								numberOfQueriesMoved++;
								// TODO differentiate between map-only and
								// map-reduce
								// stages
								continue query; // go to next query
							}
						}
					}

				}

				// if (log)
				// System.out.println("Cloud" + u.getId()
				// + ": current query size: "
				// + u.currentQueries.size()
				// + " current number of tasks "
				// + u.getCurrentNumberofTasks()
				// + " current avg costs per stage "
				// + u.getAvgStagesCost());

			}
		}
	}

	private ArrayList<Float> getPlacementNeural(uCloud u, Query q) {
		ArrayList<Float> result;

		u.inputValues.set(0, u.getCurrentCosts() / u.getStagesServed());
		// u.inputValues.set(1, (float) u.getStagesServed());
		// u.inputValues.set(2, (float) q.getCurrentStage());
		u.inputValues.set(1, (float) u.getCurrentNumberofTasks());
		u.inputValues.set(2,
				(float) q.getQuerytree()[q.getCurrentStage() - 1].costs());

		result = u.rep.getOutput(u.inputValues);
		return result;
	}

	private ArrayList<Float> getPlacementRandom(uCloud u, Query q) {
		ArrayList<Float> result = new ArrayList<Float>();
		result.add(rand.nextFloat());
		return result;
	}

	private void initialize(AbstractRepresentation candidate) {

		log = Integer.parseInt(getPropertyValue("log")) == 1;

		int numNodes = Integer.parseInt(getPropertyValue("numberOfClouds"));
		uClouds = new ArrayList<>(numNodes);
		this.minInterval = 1f / (numNodes);
		int numClientspernode = Integer
				.parseInt(getPropertyValue("numberOfClientsperCloud"));

		int numQueriesperClient = Integer
				.parseInt(getPropertyValue("numberofQueriesperClient"));
		this.queriesleft = numQueriesperClient * numClientspernode * numNodes;
		int webPagesRatio = Integer.parseInt(getPropertyValue("webpagesRatio"));

		this.isRandomScheduler = Integer
				.parseInt(getPropertyValue("useRandomScheduler")) == 1;

		long seed = FrevoMain.getSeed();
		System.out.println("seed: " + seed);

		rand.setSeed(seed);
		super.setRandom(rand);
		int inputsize = Integer.parseInt(getPropertyValue("inputSizeGB"));

		// create number of nodes, clients set up everything
		for (int i = 0; i < numNodes; i++) {
			String keyspace = rand.nextInt(100) <= webPagesRatio ? "webpages"
					: "entities";
			uClouds.add(new uCloud(keyspace, numClientspernode, candidate, i,
					rand, numQueriesperClient, inputsize));

		}

		// TODO use external bandwidth map

		// this.bandwidthmap = new float[][] { { 10040752f, 1034872f, 1034872f
		// },
		// { 1034872f, 1040752f, 1048577f },
		// { 1034872f, 1048577f, 1048577f } };

	}

	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

	public Task executeQueryStage(Query q) {

		SubQuery current = q.getQuerytree()[q.getCurrentStage()];

		q.increaseCurrentStage();

		// create task to be executed
		return new Task(current.hasReduce());
	}
}
