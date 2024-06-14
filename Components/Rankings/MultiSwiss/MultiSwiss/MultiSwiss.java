package MultiSwiss;

/**
 * This class implements a ranking method for evaluating a candidate pool using competitive multi-player games
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import utils.NESRandom;
import core.AbstractComponent;
import core.AbstractMultiProblem;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.ProblemXMLData;
import core.XMLFieldEntry;

/** This ranking method is for evaluating a candidate pool using competitive multi-player games
 *  It applies a Swiss-style pairing system as described in 
 *  https://www.wizards.com/dci/downloads/RISK2210Swiss.pdf
 * 
 * @author wilfried
 */

public class MultiSwiss extends AbstractRanking {

	public MultiSwiss(Hashtable<String, XMLFieldEntry> properties) {
		super(properties);
	}

	private AbstractRepresentation bestcandidate;
	
	/**
	 * DEBUGLVL=0 no debug messages
	 * DEBUGLVL=1 list ranking after sorting is done
	 * DEBUGLVL=1 list ranking during sorting
	 */
	static int DEBUGLVL=0;
	
	/** Sorts the given array of representations in a descending order of fitness and returns the number of evaluation that was required for the ranking to finish.
	 * Evaluation of the candidates is done by the provided <i>problem</i> component.
	 * @param representations The population to be sorted.
	 * @param problem The problem descriptor to be used for evaluation.
	 * @param random The random generator object used for sorting.
	 * @return the number of evaluations that were needed to rank the given set of representations*/
	public int sortCandidates (final ArrayList<AbstractRepresentation> pop, final ProblemXMLData problem, NESRandom random) {
		//Random generator = new NESRandom(0x5EED);
		int playersPerGame;
		int popsize = pop.size();// number of entities
		int playernumbermin = Integer.parseInt(problem.getRequirements()
				.get("minimumCandidates").getValue()); //min number of players for a game
		int playernumbermax = Integer.parseInt(problem.getRequirements()
				.get("maximumCandidates").getValue()); //max number of players for a game
		
		if (playernumbermin>playernumbermax)
			throw new Error(			
				"Error in configuration of minimum and maximum players of this game. "+
			    "The minimum (currently "+playernumbermin+") should be less or equal " +
				"to the maximum (currently "+playernumbermax+")");
		
		if (popsize<playernumbermin)
			throw new Error(
				"Not enough canditates in the population for this game. Population size is"+
				popsize+", the game is for "+playernumbermin+" to "+playernumbermax+" players!");
		
		if (popsize > playernumbermax)
			playersPerGame = playernumbermax;
		else
			playersPerGame = popsize;
		
		MultiSortElement.average_score = (playernumbermax+1.0)/2;
		
		//estimate the number of rounds to play in order to get a reasonable ranking
		int roundsToPlay = (int)Math.ceil(Math.log(popsize)/Math.log(playersPerGame));
		
		//players can be paired randomly for the first round, therefore we just
		//select them one-by-one according to the initial sorting and copy them into
		//a sortable list
		ArrayList<MultiSortElement> candidates=new ArrayList<MultiSortElement>();
		for (AbstractRepresentation c: pop) {
			candidates.add(new MultiSortElement(c));
		}
		
		for(int round=0; round<roundsToPlay; round++) {
			if (DEBUGLVL >= 2) {
				System.out.println("Sorting round "+round);
				System.out.println("Rank\tName\tFitness");
				//list candidates
				for (int i=0; i<popsize;i++) {
					AbstractRepresentation player = candidates.get(i).getPlayer();
					System.out.println(i+"\t"+player.getHash()+"\t"+candidates.get(i).getScore()+" ("+candidates.get(i).totalscore+" / "+candidates.get(i).number_of_games+")");
				}
				pause();
			}
			
			//create and evaluate groups
			
			int index=0;
			while (index+playersPerGame < popsize) { //this loop could be parallelized
				//create the array of players for this game
				AbstractRepresentation[] members = new AbstractRepresentation[playersPerGame];
				for (int i=0; i<playersPerGame;i++) {
					members[i]=candidates.get(index+i).getPlayer();
					members[i].reset();
				}
				// evaluate them
				AbstractMultiProblem p;
				AbstractComponent comp;
				try {
					comp = problem.getNewProblemInstance();
					if (comp instanceof AbstractMultiProblem) {
						p = (AbstractMultiProblem) comp;
						p.setRandom(random.clone());
						p.evaluateFitness(members);
					} else {
						throw new Error(
								"MultiSort requires an instance of AbstractMultiProblem");
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				}

				//sort according to fitness score from this game
				Arrays.sort(members, Collections.reverseOrder());
				//overwrite score with reference points
				for (int i=0; i<playersPerGame;i++) {
					members[i].setFitness(playersPerGame-i);
				}
				//update scores
				for (int i=0; i<playersPerGame;i++) {
					AbstractRepresentation player =candidates.get(index+i).getPlayer();
					candidates.get(index+i).addResult(player.getFitness());
				}
				index+=playersPerGame;
			}
			//sort according to current score
			Collections.sort(candidates, Collections.reverseOrder());
			
		}
		//write back fitness values and copy back to population
		for (int i=0; i<popsize;i++) {
			AbstractRepresentation player = candidates.get(i).getPlayer();
			player.setFitness(candidates.get(i).getScore());
			pop.set(i, player);
		}
		
		if (DEBUGLVL >= 1) {
			//list population
			System.out.println("Final Raning\nRank\tName\tFitness");
			for (int i=0; i<popsize;i++) {
				AbstractRepresentation player = pop.get(i);
				System.out.println(i+"\t"+player.getHash()+"\t"+player.getFitness());
			}
			pause();
		}

		bestcandidate = pop.get(0);
		return 0;	
	}
	
	/** utility function to pause the program for debug purposes */
	private void pause() 
	{
		System.out.println("\n\nPress ENTER..");
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public AbstractRepresentation getBestCandidate() {
		return bestcandidate;
	}
}
