package sched;

public class Query {

	enum SubQuery {
		READ(1.0f, 1.0f, false), READLEFT(1.0f, 1.0f, false), READRIGHT(1f, 1f,
				false), PROJECT(0.002f, 0.0005f, false), FILTER(0.005f, 1.7f,
				false), JOIN(0.4f, 7f, true), SORT(1f, 1f, false), GROUPBYAVG(
				0.1f, 0.001f, true);

		private final float k; // the approximate number of VM hours needed to
								// process 1 GB of data (h/GB)
		private final float q; // the approx. transmitted data per VM hour [in
								// GB] (GB/h)

		private final boolean hasReduce;

		SubQuery(float k, float q, boolean hasReduce) {
			this.k = k;
			this.q = q;
			this.hasReduce = hasReduce;
		}

		public float costs() {
			return this.k;
		}

		public boolean hasReduce() {
			return this.hasReduce;
		}

	}

	enum KEYSPACE {
		WEBPAGES, ENTITIES, ALL
	}

	private int currentstage = 0;
	private SubQuery[] querytree = null;
	private KEYSPACE keyspace;
	private Client client;
	private double queryCosts = 0;
	private float inputsize; // 6.8MB - documents tiny setting

	public Query(SubQuery[] querytree, KEYSPACE keyspace, Client client,
			float inputsize) {
		this.querytree = querytree;
		this.keyspace = keyspace;
		this.client = client;
		this.inputsize = inputsize;
		for (SubQuery sub : this.querytree) {
			queryCosts += sub.costs() * inputsize; // processing costs are the
													// same for each machine
		}

	}

	public KEYSPACE getKeyspace() {
		return keyspace;
	}

	public SubQuery[] getQuerytree() {
		return querytree;
	}

	public int getCurrentStage() {
		return currentstage;
	}

	public void increaseCurrentStage() {
		this.currentstage++;
	}

	public Client getClient() {
		return this.client;
	}

	public double getTotalCost() {
		return queryCosts;
	}

	// add transport costs depending on output per hour times bandwidth
	public void addToTotalCosts(float bandwidth) {
		int stage = currentstage > 0 ? currentstage - 1 : currentstage;
		queryCosts += (this.querytree[stage].q * inputsize) / bandwidth;
	}

	public float calculateStageCost() {
		return (float) queryCosts / querytree.length;
	}

	public boolean isFinished() {
		return querytree.length <= currentstage;
	}

}
