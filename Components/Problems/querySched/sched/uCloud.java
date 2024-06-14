package sched;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sched.Query.KEYSPACE;
import core.AbstractRepresentation;

public class uCloud {

	private List<Client> clients;
	private KEYSPACE keyspace;
	private List<Task> currentTasks;
	ArrayDeque<Query> currentQueries;
	AbstractRepresentation rep;
	private int id;
	private int queriesServed;
	private double currentCosts;
	private int totalTasks = 0;
	private int stagesServed = 0;
	ArrayList<Float> inputValues = new ArrayList<>();
	private int numberOfInputValues = 3;
	public final int inputsize;

	public uCloud(String keyspace, int numClients, AbstractRepresentation rep,
			int id, Random rand, int numQueriesperClient, int inputsize) {
		this.keyspace = KEYSPACE.valueOf(keyspace.toUpperCase());
		this.currentQueries = new ArrayDeque<>();
		this.currentTasks = new ArrayList<>();
		this.clients = new ArrayList<>(numClients);
		this.inputsize = inputsize;
		for (int i = 0; i < numClients; i++) {
			this.clients.add(new Client(this, rand, numQueriesperClient));
		}
		this.rep = rep.clone();
		this.id = id;
		for (int i = 0; i < numberOfInputValues; i++) {
			inputValues.add(0f);
		}
	}

	public int getId() {
		return id;
	}

	public List<Client> getClients() {
		return clients;
	}

	/**
	 * set all tasks to go to the next round (reduce task) and remove the ones
	 * that are done. Assumption 1 Tasks requires 1 step for fulfillment
	 */
	public void nextTaskRound() {
		for (Task t : currentTasks) {
			t.nextRound();
		}
		cleanupFinishedTasks();
	}

	public int getCurrentNumberofTasks() {
		return currentTasks.size();
	}

	public void addTask(Task t) {
		totalTasks++;
		this.currentTasks.add(t);
	}

	public int getTotalTasks() {
		return totalTasks;
	}

	public KEYSPACE getKeyspace() {
		return keyspace;
	}

	public void startQuery(Query currentQuery) {
		this.currentQueries.addLast(currentQuery);
	}

	public void addToQueryCost(double cost) {
		currentCosts += cost;
	}

	
	public float getCurrentCosts() {
		return (float) currentCosts;
	}

	public void increaseQueriesserved() {
		queriesServed++;
	}

	public int getQueriesServed() {
		return queriesServed;
	}

	public void increaseStagesServed() {
		stagesServed++;
	}

	public int getStagesServed() {
		return stagesServed;
	}

	private void cleanupFinishedTasks() {
		// unofortunately, you cannot use lamda expressions 
		//currentTasks.removeIf(t -> t.getnumRounds() == 0); // cleanup
	}

	public void nextRoundFinished() {
		currentTasks.get(0).nextRound();
		cleanupFinishedTasks();
	}

}
