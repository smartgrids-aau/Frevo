package sched;

import java.util.Random;

import sched.Query.SubQuery;

public class Client {

	private uCloud entryPoint;
	private Query currentQuery;
	private int numQueriesLeft;
	private Random rand;

	enum QueryType {
		SIMPLE, JOIN
	}

	public Client(uCloud entrypoint, Random rand, int numQueriesLeft) {
		this.entryPoint = entrypoint;
		this.rand = rand;
		this.numQueriesLeft = numQueriesLeft;
		sendNewRequest();
	}

	public void sendNewRequest() {
//		System.out.println("Starting request");
		if (numQueriesLeft > 0) {
			QueryType type = rand.nextBoolean() ? QueryType.SIMPLE
					: QueryType.JOIN;
//			System.out.println("ext Querytype "+ type.toString() );
			this.currentQuery = generateNewQuery(type);
			numQueriesLeft--;
			entryPoint.startQuery(currentQuery);
			
		}
	}

	private Query generateNewQuery(QueryType type) {
		switch (type) {
		case SIMPLE:
			/*
			 * use a simple query from the samples: SELECT sentiment, URL from
			 * webpages where sentiment > 0, order by sentiment The sort
			 * operator is merging all results to a single ucloud. (should only
			 * move data if the initial ucloud is not the same as the client)
			 */
			SubQuery[] simplequery = { SubQuery.READ, SubQuery.PROJECT,
					SubQuery.SORT };

			return new Query(simplequery, Query.KEYSPACE.WEBPAGES, this, entryPoint.inputsize);
		case JOIN:
			/*
			 * SELECT domainname, AVG(pagerank) from webpages, JOIN entities ON
			 * url=webpageurl WHERE entities.name="EC", GROUP BY domainname,
			 * ORDERBY AVG(pagerank)
			 */
			SubQuery[] joinquery = { SubQuery.READLEFT, SubQuery.READRIGHT,
					SubQuery.JOIN, SubQuery.GROUPBYAVG, SubQuery.FILTER,
					SubQuery.PROJECT, SubQuery.SORT };
			return new Query(joinquery, Query.KEYSPACE.ALL, this, entryPoint.inputsize);
		}
		;
		return null;
	}

	public uCloud getEntryPoint() {
		return this.entryPoint;
	}

}
