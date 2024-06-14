package CEA2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;

import CEA2D.Member.replaceFunction;
import net.jodk.lang.FastMath;
import utils.NESRandom;
import core.AbstractRepresentation;
import core.ComponentXMLData;

/**
 * The class population represents the whole population for the evolutionary
 * algorithm SSEA2D. It contains all the representations and the function to
 * evolve a new generation.
 * 
 * @author Thomas Dittrich
 */

public class Population {

	Member[] members;
	Parameters parameters;
	long randomNeighborhoodSeed;
	private ComponentXMLData representation;
	private int inputnumber;
	private int outputnumber;

	private double numElite;
	private double numMutate;
	private double numXOver;
	private double numRenew;

	private double effectivityElite;
	private double effectivityMutate;
	private double effectivityXOver;
	private double effectivityRenew;

	public double getNumElite() {
		return numElite;
	}

	public double getNumMutate() {
		return numMutate;
	}

	public double getNumXOver() {
		return numXOver;
	}

	public double getNumRenew() {
		return numRenew;
	}

	public double getEffectivityElite() {
		return effectivityElite;
	}

	public double getEffectivityMutate() {
		return effectivityMutate;
	}

	public double getEffectivityXOver() {
		return effectivityXOver;
	}

	public double getEffectivityRenew() {
		return effectivityRenew;
	}

	int[][] obs_pattern;

	/**
	 * 
	 * @param representation ComponentXMLdata which is used to create the Members.
	 *                       If this constructor is called in a subclass of
	 *                       AbstractRepresentation the variable representation
	 *                       should be handed over
	 * @param parameters     Instance which holds the properties for each member.
	 */
	public Population(ComponentXMLData representation, Parameters parameters, int inputnumber, int outputnumber,
			CEA2D cea2d) {
		this.parameters = parameters;
		this.representation = representation;
		this.inputnumber = inputnumber;
		this.outputnumber = outputnumber;
		// this.cea2d = cea2d;
		cea2d.createObstaclePattern();

		obs_pattern = cea2d.getObstaclePattern(); 

		int nummembers = 0;

		for (int x = 0; x < parameters.POPULATIONFIELDSIZE_WIDTH; x++) {
			for (int y = 0; y < parameters.POPULATIONFIELDSIZE_HEIGHT; y++) {
				if (obs_pattern[x][y] != 1000) {
					obs_pattern[x][y] = nummembers++;
				}
			}
		}

		members = new Member[nummembers];

		for (int i = 0; i < members.length; i++) {
			members[i] = new Member(representation, parameters, inputnumber, outputnumber);
		}

		if (parameters.NEIGHBOURHOODMODE == 1) {
			SetGridneighborhood();
		} else if (parameters.NEIGHBOURHOODMODE == 2) {
			randomNeighborhoodSeed = parameters.getGenerator().getSeed();
			SetRandomneighborhood(8);
		} else {
			SetGridneighborhood();
		}
	}

	public Population(ComponentXMLData representation, Parameters parameters, int inputnumber, int outputnumber,
			ArrayList<AbstractRepresentation> population, Document doc) {
		this.parameters = parameters;
		this.inputnumber = inputnumber;
		this.outputnumber = outputnumber;

		// members = new Member[parameters.POPULATIONFIELDSIZE_HEIGHT *
		// parameters.POPULATIONFIELDSIZE_WIDTH];
		members = new Member[population.size()];
		for (int i = 0; i < members.length; i++) {
			members[i] = new Member(population.get(i), parameters);
		}

		// get population root node
		Node dpopulations = doc.selectSingleNode("/frevo/populations");
		// get population size
		List<?> npops = dpopulations.selectNodes(".//population");
		Iterator<?> it = npops.iterator();
		while (it.hasNext()) {
			Node pop = (Node) it.next();
			this.randomNeighborhoodSeed = pop.numberValueOf("./@randomNeighborhoodSeed").longValue();
		}

		if (parameters.NEIGHBOURHOODMODE == 1) {
			SetGridneighborhood();
		} else if (parameters.NEIGHBOURHOODMODE == 2) {
			SetRandomneighborhood(8);
		} else {
			SetGridneighborhood();
		}
	}

	/**
	 * Returns an ArrayList of IRepresentations which contains all the
	 * IRepresentations of the Members
	 * 
	 * @return ArrayList of IRepresentation
	 */

	public ArrayList<AbstractRepresentation> getMembers() {
		ArrayList<AbstractRepresentation> m = new ArrayList<AbstractRepresentation>();

		for (Member me : members) {
			m.add(me.rep);
		}

		return m;
	}

	/**
	 * Evolves the IRepresentation of every member according to the evolution-rules
	 */
	public void evolve(Step step) throws Exception {
		NESRandom rand = parameters.getGenerator();

		// get diff to all neighbors
		for (int i = 0; i < members.length; i++) {
			members[i].diff = 0;
			int j = 0;
			double diff = 0.0;
			for (Member n : members[i].neighbors) {
				if (n.rep.getFitness() >= members[i].rep.getFitness()) {
					diff += members[i].rep.diffTo(n.rep);
					j++;
				}
			}
			members[i].diff = j > 0 ? diff / j : 0.0;
		}

		AbstractRepresentation[] newmembers = new AbstractRepresentation[members.length];
		numElite = 0;
		numMutate = 0;
		numXOver = 0;
		numRenew = 0;
		int numEliteElite = 0;
		int numMutateElite = 0;
		int numXOverElite = 0;
		int numRenewElite = 0;

		for (int i = 0; i < members.length; i++) {
			members[i].rep.setFitness(members[i].rep.getFitness() + ((double) i + 1) / 1e6);
		}

		for (int i = 0; i < members.length; i++) {
			switch (members[i].getCreatedBy()) {
			case ELITE:
				numElite++;
				break;
			case MUTATE:
				numMutate++;
				break;
			case XOVER:
				numXOver++;
				break;
			case RENEW:
				numRenew++;
				break;
			}

			ArrayList<AbstractRepresentation> neighborhood = new ArrayList<AbstractRepresentation>();

			for (Member n : members[i].neighbors) {
				neighborhood.add(n.rep);
			}
			neighborhood.add(members[i].rep);

			step.getRanking().sortCandidates(neighborhood, step.getProblemData(), rand);

			int rankneighborhood = neighborhood.indexOf(members[i].rep);
			float re = parameters.PERCENTELITE / 100.0f;
			int rankelite = (int) FastMath.rint(neighborhood.size() * re);

			AbstractRepresentation[] elite = new AbstractRepresentation[rankelite];

			for (int j = 0; j < rankelite; j++) {
				elite[j] = neighborhood.get(j);
			}

			if (rankelite > 0 && (rankneighborhood < rankelite
					|| members[i].rep.getFitness() == elite[rankelite - 1].getFitness())) {

				newmembers[i] = members[i].rep;

				switch (members[i].getCreatedBy()) {
				case ELITE:
					numEliteElite++;
					break;
				case MUTATE:
					numMutateElite++;
					break;
				case XOVER:
					numXOverElite++;
					break;
				case RENEW:
					numRenewElite++;
					break;
				}
				members[i].setCreatedBy(replaceFunction.ELITE);

			} else {
				int geneticoperationrand = rand.nextInt((int) (100 - parameters.PERCENTELITE));
				if (geneticoperationrand < parameters.PERCENTMUTATEELITE) {
					if (rankelite > 0)
						newmembers[i] = elite[rand.nextInt(rankelite)].clone();
					else
						newmembers[i] = members[i].rep;
					newmembers[i].mutate(parameters.MUTATIONSEVERITY, parameters.MUTATIONPROBABILITY, 1);
					members[i].setCreatedBy(replaceFunction.MUTATE);
				} else if (geneticoperationrand < parameters.PERCENTXOVERELITE + parameters.PERCENTMUTATEELITE) {
					newmembers[i] = members[i].rep.clone();
					if (rankelite > 0)
						newmembers[i].xOverWith(elite[rand.nextInt(rankelite)], 1);
					else
						newmembers[i].xOverWith(neighborhood.get(rand.nextInt(neighborhood.size())), 1);
					members[i].setCreatedBy(replaceFunction.XOVER);
				} else {
					newmembers[i] = representation.getNewRepresentationInstance(inputnumber, outputnumber,
							parameters.getGenerator());
					members[i].setCreatedBy(replaceFunction.RENEW);
				}
			}
		}

		// copy the new members into the population
		for (int i = 0; i < members.length; i++) {
			members[i].rep = newmembers[i];
		}
		for (int i = 0; i < members.length; i++) {
			// System.out.println("Zeile 400, i: " + i);
			if (members[i].rep.isEvaluated()) {
				members[i].rep.setFitness(members[i].rep.getFitness() - ((double) i + 1) / 1e6);

			}

		}
		effectivityElite = numElite == 0 ? 0 : ((double) numEliteElite) / ((double) numElite);
		effectivityMutate = numMutate == 0 ? 0 : ((double) numMutateElite) / ((double) numMutate);
		effectivityXOver = numXOver == 0 ? 0 : ((double) numXOverElite) / ((double) numXOver);
		effectivityRenew = numRenew == 0 ? 0 : ((double) numRenewElite) / ((double) numRenew);

	}

	/**
	 * Sets the neighbors for every member. The Neighbors of a member are those
	 * which are adjacent in the grid
	 */

	public void SetGridneighborhood() {
		// add width and height for rectangular grids
		int fieldheight = parameters.POPULATIONFIELDSIZE_HEIGHT;
		int fieldwidth = parameters.POPULATIONFIELDSIZE_WIDTH;

		for (int x0 = 0; x0 < fieldwidth; x0++) {
			for (int y0 = 0; y0 < fieldheight; y0++) {
				for (int x1 = -1; x1 <= 1; x1++) {
					for (int y1 = -1; y1 <= 1; y1++) {
						if (x1 != 0 || y1 != 0) {
							int x = (x0 + x1 + fieldwidth) % fieldwidth;
							int y = (y0 + y1 + fieldheight) % fieldheight;

							if (obs_pattern[x0][y0] != 1000 && obs_pattern[x][y] != 1000) {
								members[obs_pattern[x0][y0]].neighbors.add(members[obs_pattern[x][y]]);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the neighbors for every member. The Neighbors of a member are selected
	 * by random
	 */
	public void SetRandomneighborhood(int numberofneighbors) {
		int fieldheight = parameters.POPULATIONFIELDSIZE_HEIGHT;
		int fieldwidth = parameters.POPULATIONFIELDSIZE_WIDTH;

		NESRandom localRandom = new NESRandom(randomNeighborhoodSeed);
		for (int x0 = 0; x0 < fieldwidth; x0++) {
			for (int y0 = 0; y0 < fieldheight; y0++) {
				for (int i = 0; i < numberofneighbors; i++) {
					int randValue = localRandom.nextInt(members.length);

					if ((obs_pattern[x0][y0] != 1000) && (obs_pattern[x0][y0] != randValue))
						members[obs_pattern[x0][y0]].neighbors.add(members[randValue]);
				}
			}
		}
	}
}
