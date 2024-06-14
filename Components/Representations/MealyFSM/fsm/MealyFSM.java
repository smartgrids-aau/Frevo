package fsm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import utils.NESRandom;
import core.AbstractRepresentation;
import core.XMLFieldEntry;
import fsm.ThresholdedIntegerInput.MutationType;

/**
 * Creates a Mealy finite state machine representation.
 * 
 * A Mealy machine is a special type of finite state machine, whose output
 * values are determined both by its current state and the current input values.
 * Thus, previous states will have an effect on the current outputs creating a
 * slight notion of memory.
 * 
 * @author Agnes Pinter-Bartha
 * 
 */
public class MealyFSM extends AbstractRepresentation {
	private EvolutionParams params;

	private NESRandom generator;
	private String origName;
	private int countMutation;
	private int countCrossover;
	private String name;

	// parameters for FSM encoding:
	private int encodedStateTableLength;

	// mutatable parts
	private int nState = 6;
	private int initState = 0;
	private ArrayList<ArrayList<Integer>> outputsList;
	private ArrayList<Integer> nextStates; // TODO: introduce State?
	private InputMapper inputMapper; // thresholds

	// for evaluating the automata:
	private int currentState;

	// FSM statistics - when FSM is evaluated
	private TreeSet<Integer> usedStates = new TreeSet<Integer>();
	private int[] stateHistogram;

	public MealyFSM(int nInput, int nOutput, NESRandom random,
			Hashtable<String, XMLFieldEntry> properties) {
		super(nInput, nOutput, random, properties);
		this.setProperties(properties);

		// load parameters from xml
		this.params = new EvolutionParams((short) nInput, (short) nOutput,
				getProperties());
		this.inputMapper = new InputMapper(getProperties(), random, nInput);
		this.nState = params.getNumOfStates();
		this.generator = random;
		name = "";
		if (generator != null) {
			countCrossover = 0;
			countMutation = 0;
			origName = RandomStringUtils.random(8, 0, 0, true, true, null,
					random);
			name = origName;
		}

		this.encodedStateTableLength = getEncodedStateTableLength();

		this.currentState = 0;
		this.initState = 0;
		this.stateHistogram = new int[nState];
		for (int i = 0; i < stateHistogram.length; i++) {
			stateHistogram[i] = 0;
		}

		initStateTransitionTable();

		// if generator is not null, use it for generating
		// the fsm's encoded state transition table
		// generator can be null e.g. when FSM is loaded from xml...
		if (generator != null) {
			if (params.getGenerationMode() == GenerationMode.SIMAO)
				this.genSimaoRandomFSM();
			else if (params.getGenerationMode() == GenerationMode.RANDOM)
				this.genRandomFSM();
			else {
				System.err
						.println("Generation mode not implemented, using random.");
				this.genRandomFSM();
			}
		}

	} // needed for loadfromxml and testing, so no data to be set

	/**
	 * Export FSM as a graph's edge-list into a csv (comma separated) file.
	 * Please mind that for being able to use it with yED, you still have to
	 * transform the csv file into an xls file (e.g. open in Excel and save it
	 * as xls).
	 * 
	 * @param name
	 *            name of the file to which to export
	 */
	public void exportToYEDxmlFormat(String name, boolean test) {
		// TODO: make it possible to export into an xls file (maybe use a lib
		// like Apache POI or ..?)
		IntegerInput[] inputs = this.inputMapper.getInputs();

		int[] counts = new int[inputMapper.getNumberOfInputs()];
		for (int j = 0; j < counts.length; j++) {
			counts[j] = inputs[j].getNumberOfInputValues();
		}
		int[] currentInputs = new int[inputMapper.getNumberOfInputs()];

		try {
			FileWriter writer = new FileWriter(name);
			writer.append("EdgeList");
			writer.append('\n');
			writer.append('\n');

			writer.append("Source");
			writer.append(',');
			writer.append("Target");
			writer.append(',');
			writer.append("Weight");
			writer.append(',');
			writer.append("Labels");
			writer.append('\n');

			String row = "";
			for (int i = 0; i < nState; i++) {
				for (int j = 0; j < inputMapper.getInputValueCombinations(); j++) {
					int pos = i + j * nState;
					row = i + "," + nextStates.get(pos) + ",1.0,";

					// we use trim and replace so that label will not contain
					// any space reason is that an edge label cannot be too long.. 
					// so let's compress in some sense. - not optimized..
					row += getInputListString(currentInputs, ";").trim()
							.replaceAll("\\s+", "").replaceAll("val", "v")
							+ "/"
							+ getOutputsAsString(outputsList.get(pos), ";");
					writer.append(row);
					writer.append('\n');

					// increment current positions..
					int pos1 = currentInputs.length - 1;
					currentInputs[pos1]++;
					while (pos1 >= 0 && currentInputs[pos1] == counts[pos1]) {
						currentInputs[pos1] = 0;
						pos1--;
						if (pos1 >= 0)
							currentInputs[pos1]++;
					}
					if (test)
						System.out.println(row);
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the length of state transition table for the FSM.
	 * 
	 * @return length of state transition table.
	 */
	private int getEncodedStateTableLength() {
		if (inputMapper == null)
			System.out.println("inputMapper null");
		int inputCombinations = inputMapper.getInputValueCombinations();
		return inputCombinations * this.nState;
	}

	@Override
	public AbstractRepresentation cloneFunction() {
		MealyFSM clone = new MealyFSM(this.params.nInput, this.params.nOutput,
				generator, getProperties());
		clone.nState = this.nState;

		clone.inputMapper = this.inputMapper.clone();
		clone.encodedStateTableLength = this.encodedStateTableLength;

		clone.currentState = this.initState;
		clone.usedStates = new TreeSet<Integer>(); // null

		clone.initState = this.initState;
		clone.stateHistogram = new int[this.nState];

		clone.outputsList = new ArrayList<ArrayList<Integer>>();

		for (Iterator<ArrayList<Integer>> it = this.getOutputsList().iterator(); it
				.hasNext();) {
			// add outputs
			ArrayList<Integer> outputs = new ArrayList<Integer>();
			outputs.addAll(it.next());
			clone.getOutputsList().add(outputs);
		}

		clone.nextStates = new ArrayList<Integer>();
		clone.nextStates.addAll(this.getNextStates());

		clone.name = this.origName + "_m" + countMutation + "_x"
				+ countCrossover;

		return clone;
	}

	@Override
	public double diffTo(AbstractRepresentation representation) {
		// we will use distance calculations also used by M.Spichakova
		MealyFSM other = (MealyFSM) representation;
		int dist = 0;

		DistanceCalc distanceCalcMode = params.getDistanceCalcMethod();
		switch (distanceCalcMode) {
		case MAX_EQUAL_PREFIX:
			dist = calcMaxEqualPrefix(this, other);
			break;
		case HAMMING_DISTANCE:
			dist = calcHammingDistance(this, other);
			break;
		default:
			System.err
					.println("Unknown distance calculation method. Using default: Hamming.");
			dist = calcHammingDistance(this, other);
			break;
		}
		return dist;
	}

	/**
	 * Calculates Hamming distance between two Mealy FSMs. Currently compares
	 * the string representation of the two FSM.
	 * 
	 * @param fsm1
	 *            first Mealy FSM
	 * @param fsm2
	 *            second Mealy FSM
	 * @return Hamming distance of the two FSM
	 */
	public static int calcHammingDistance(MealyFSM fsm1, MealyFSM fsm2) {
		String chrom1 = fsm1.genStringRepr();
		String chrom2 = fsm2.genStringRepr();
		int length1 = chrom1.length();
		int length2 = chrom2.length();
		int length = Math.min(length1, length2);

		int dist = 0;
		boolean isInside = false;
		int innerDist = 0;

		for (int i = 0, j = 0; i < length && j < length; i++, j++) {
			// check if it is beginning of inner special comparison
			if (chrom1.charAt(i) == '[' && chrom2.charAt(j) == '[') {
				isInside = true;
				innerDist = 0;
				continue;
			}
			// check if it is ending of inner special comparison
			else if (chrom1.charAt(i) == ']' && chrom2.charAt(j) == ']') {
				isInside = false;
				dist += innerDist;
				innerDist = 0;
				continue;
			} else if (chrom1.charAt(i) == ']') {
				isInside = false;
				while (chrom2.charAt(j) == ']' || j < length) {
					j++;
					innerDist++;
				}
				dist += innerDist;
				innerDist = 0;
				continue;
			} else if (chrom1.charAt(j) == ']') {
				isInside = false;
				while (chrom2.charAt(i) == ']' || i < length) {
					i++;
					innerDist++;
				}
				dist += innerDist;
				innerDist = 0;
				continue;
			}

			// if we are inside a special inner comparison:
			// an inner comparison is used for the threshold lists for an input
			// this is needed so that later two differently thresholded fsms
			// could be compared
			// TODO: maybe add an if, so not to go through all this is fsms
			// similar
			if (isInside) {
				// in this case let's first compare the inside of [..]
				// then continue
				if (chrom1.charAt(i) != chrom2.charAt(j))
					innerDist++;
			} else if (chrom1.charAt(i) != chrom2.charAt(j))
				dist++;
			
			// TODO: maybe handle somehow if somebody forgets e.g. ']' ?
		}

		// if one chromosome longer than the other, consider as difference!
		dist += Math.max(length1, length2) - length;

		return dist;
	}

	/**
	 * Calculates maximal equal prefix for two Mealy FSMs. Currently compares
	 * the string representation of the two FSM.
	 * 
	 * @param fsm1
	 *            first Mealy FSM
	 * @param fsm2
	 *            second Mealy FSM
	 * 
	 * @return maximal equal prefix of the two FSM
	 */
	public static int calcMaxEqualPrefix(MealyFSM fsm1, MealyFSM fsm2) {
		String chrom1 = fsm1.genStringRepr();
		String chrom2 = fsm2.genStringRepr();
		int length = Math.min(chrom1.length(), chrom2.length());

		int dist = 0;

		// analysis stopped at first difference between the strings
		for (int i = 0; i < length; i++) {
			if (chrom1.charAt(i) == chrom2.charAt(i))
				dist++;
			else
				break;
		}

		return length - dist;
	}

	/**
	 * Apply mutation add state with a given probability. New transition's
	 * target source is chosen randomly.
	 * 
	 * @param prob
	 *            probability of applying the mutation
	 */
	public void mutationAddState(float prob) {
		if (generator.nextFloat() < prob) {

			ArrayList<Integer> outputs;
			Integer next;
			// add output-list or state from left to right
			for (int k = 1; k <= inputMapper.getInputValueCombinations(); k++) {
				// add outputs
				outputs = new ArrayList<Integer>();
				for (int j = 0; j < params.nOutput; j++) {
					outputs.add(generator.nextInt(params.output_units + 1));
				}
				outputsList.add(k * nState + 1, outputs);
				next = generator.nextInt(nState);
				nextStates.add(k * nState + 1, next);
			}
			nState++;
			stateHistogram = new int[nState];

			this.encodedStateTableLength = getEncodedStateTableLength();
		}

	}

	/**
	 * Remove a random state with a given probability
	 * 
	 * @param prob
	 *            probability of applying the mutation
	 */
	public void mutationRemoveState(float prob) {
		if (generator.nextFloat() < prob) {
			int rnd_state = getRandomState();

			// remove from right to left, so the index does not need to be
			// modified
			for (int k = inputMapper.getInputValueCombinations() - 1; k >= 0; k--) {
				getOutputsList().remove(k * nState + rnd_state);
				getNextStates().remove(k * nState + rnd_state);
			}

			for (Iterator<Integer> it = this.getNextStates().iterator(); it
					.hasNext();) {
				Integer next = it.next();

				// assign to states having transition to deleted state random
				// new state
				if (next == rnd_state)
					next = getRandomState();

				// rename states above rnd_state (now they are numbered with a
				// smaller int)
				if (next > rnd_state) {
					next--;
				}
			}
			nState--;
			this.stateHistogram = new int[nState];

			this.encodedStateTableLength = getEncodedStateTableLength();
		}
	}

	/**
	 * Apply mutation change initial state with a given probability
	 * 
	 * @param prob
	 *            probability of applying the mutation
	 */
	public void mutationChangeInitState(float prob) {
		if (generator.nextFloat() < prob) {
			// generate random, different from current
			int init = this.initState;
			while (init == this.initState) {
				this.initState = generator.nextInt(nState - 1) + 1;
			}
			resetToZeroInit(this.initState);
		}
	}

	/**
	 * Resets initial state to 0.
	 * 
	 * @param newInitState
	 *            previously set new initial state
	 */
	private void resetToZeroInit(int newInitState) {
		if (0 == newInitState)
			return;
		ArrayList<Integer> zeroInitStateOutput = new ArrayList<Integer>();
		ArrayList<Integer> newInitStateOutput = new ArrayList<Integer>();
		int nextState1;
		int nextState2;

		// swap the two state transition table row content - 
		// outputs and next state
		for (int k = 0; k < params.nInput; k++) {
			zeroInitStateOutput = outputsList.get(k * params.nState);
			newInitStateOutput = outputsList.get(k * params.nState
					+ newInitState);

			outputsList.set(k * params.nState, newInitStateOutput);
			outputsList.set(k * params.nState + newInitState,
					zeroInitStateOutput);

			nextState1 = nextStates.get(k * params.nState);
			nextState2 = nextStates.get(k * params.nState + newInitState);

			nextStates.set(k * params.nState, nextState2);
			nextStates.set(k * params.nState + newInitState, nextState1);
		}

		// swap next state names everywhere they appeared
		for (ListIterator<Integer> it = nextStates.listIterator(); it.hasNext();) {
			nextState1 = it.next();
			if (nextState1 == 0) {
				it.set(newInitState);
			} else if (nextState1 == newInitState) {
				it.set(0);
			}
		}

		this.initState = 0;
	}

	/**
	 * Apply mutation to outputs with a given probability
	 * 
	 * @param prob
	 *            probability of applying the mutation
	 * */
	public void mutationChangeOutputs(float prob) {
		ArrayList<Integer> outputs;

		for (int i = 0; i < encodedStateTableLength; i++) {
			outputs = outputsList.get(i);
			for (int j = 0; j < params.nOutput; j++) {
				if (generator.nextFloat() < prob) {
					outputs.set(j, generator.nextInt(params.output_units + 1));
				}
			}
			outputsList.set(i, outputs);
		}

	}

	/**
	 * Apply mutation to next states with a given probability
	 * 
	 * @param prob
	 *            probability of applying the mutation
	 * */
	public void mutationChangeNextStates(float prob) {
		int nextState;

		for (int i = 0; i < encodedStateTableLength; i++) {
			if (generator.nextFloat() < prob) {
				// set random, different target state
				nextState = nextStates.get(i);
				while (nextState == nextStates.get(i))
					nextState = generator.nextInt(nState);
				nextStates.set(i, nextState);
			}
		}
	}

	/**
	 * Apply mutation to thresholds with a given probability
	 * 
	 * @param prob
	 *            probability of applying the mutation
	 * */
	public void mutateThresholds(float prob) {
		inputMapper.mutateThresholds(prob, MutationType.Random);
	}

	/**
	 * Returns the number of states of the Mealy FSM.
	 * 
	 * @return number of states for this Mealy FSM
	 * */
	public int getNStates() {
		return this.nState;
	}

	/**
	 * Performes uniform-crossover between this Mealy FSM and another one.
	 * 
	 * @param other
	 *            The other parent (Mealy FSM) used to perform crossover.
	 * 
	 */
	public void uniformCrossover(MealyFSM other) {
		// we perform everything on this object for convenience
		// child = this, + we modify parts if from father genome

		// set init state: if true, select from the other parent
		if (generator.nextBoolean() == true) {
			this.initState = other.initState;
		}

		// crossover thresholds
		// we have to be careful as if inputs thresholded change,
		// than the length of the state transition table changes as well!!
		inputMapper.uniformCrossover(other.inputMapper);

		int pos = Math.min(this.encodedStateTableLength,
				other.encodedStateTableLength);
		int maxpos = Math.max(this.encodedStateTableLength,
				other.encodedStateTableLength);

		boolean otherLongerThanThis = other.encodedStateTableLength > pos;

		// set nextstate and outputlists - if true, select from other parent
		for (int i = 0; i < pos; i++) {
			// outputlist elements (array of outputs) can be mixed as well..
			ArrayList<Integer> outputs = outputsList.get(i);

			for (int j = 0; j < outputs.size(); j++) {
				if (generator.nextBoolean() == true) {
					outputs.set(j, other.outputsList.get(i).get(j));
				}
			}
			this.outputsList.set(i, outputs);

			if (generator.nextBoolean() == true) {
				this.nextStates.set(i, other.nextStates.get(i));
			}

		}

		// if the other parent has longer encoding, then longer part is taken
		// from the appropriate parent
		if (otherLongerThanThis) {
			for (int i = pos; i < maxpos; i++) {
				// if other is bigger, this will gets its part too
				this.outputsList.add(other.outputsList.get(i));
				this.nextStates.add(other.nextStates.get(i));
			}
		}

		// the other case is not needed to handle, since we give back this
		// anyway...

		// some other variable sets:
		this.encodedStateTableLength = maxpos;
		this.nState = Math.max(this.nState, other.nState);

		// reset
		this.currentState = this.initState;
		this.stateHistogram = new int[this.nState];
		this.usedStates.clear();
	}

	/**
	 * Performs one-point crossover between this Mealy FSM and another one.
	 * 
	 * @param other
	 *            The other parent (Mealy FSM) used to perform crossover.
	 */
	public void onepointCrossover(MealyFSM other) {
		int minpos = Math.min(this.encodedStateTableLength,
				other.encodedStateTableLength);

		// preparing for crossing FSMs with different encodedStateTableLength:
		// crossing point has to be inside both FSMs
		// chromosome: init state, thresholds, encoded state tr table
		int chromosome_len = 1
				+ this.inputMapper.getNumberOfThresholdedInputs() + minpos;
		int xover_pos = generator.nextInt(chromosome_len);

		// until xover point, chromosome is taken from this,
		// rest is filled from the other.

		if (xover_pos < 1) {
			// take only input state from this, all others from other
			copyThresholdsFromOther(other);
			copyEncodedStateTableFromOther(other);

		} else if (xover_pos < 1 + this.inputMapper
				.getNumberOfThresholdedInputs()) {
			// take input from this, and part from thresholds until xoverpoint
			copyPartofThresholdsFromOtherFromPos(other, xover_pos - 1);
			copyEncodedStateTableFromOther(other);

		} else {
			// xover is in the encodedtable..
			copyPartofEncodedStateTableFromOther(other, xover_pos
					- this.inputMapper.getNumberOfThresholdedInputs() - 1);
		}

		// reset
		this.currentState = this.initState;
		this.stateHistogram = new int[this.nState];
		this.usedStates.clear();
	}

	/**
	 * Helper function used to copy thresholds from another Mealy FSM to this.
	 * 
	 * @param other
	 *            Mealy FSM from which we copy thresholds
	 * 
	 */
	private void copyThresholdsFromOther(MealyFSM other) {
		this.inputMapper.copyThresholdsFromOther(other.inputMapper);
	}

	/**
	 * Helper function used to copy thresholds from another Mealy FSM, starting
	 * from a position, to this.
	 * 
	 * @param other
	 *            Mealy FSM from which we copy thresholds
	 * @param pos
	 *            position from which copy is started
	 */
	private void copyPartofThresholdsFromOtherFromPos(MealyFSM other, int pos) {
		// calculate position of input! and position of threshold!
		this.inputMapper.copyThresholdsFromOtherFromPos(other.inputMapper, pos);
	}

	/**
	 * Helper function used to copy the encoded state table from another Mealy
	 * FSM to this.
	 * 
	 * @param other
	 *            Mealy FSM from which we copy thresholds
	 * 
	 */
	private void copyEncodedStateTableFromOther(MealyFSM other) {
		this.outputsList = new ArrayList<ArrayList<Integer>>();

		for (Iterator<ArrayList<Integer>> it = other.getOutputsList()
				.iterator(); it.hasNext();) {
			// add outputs
			ArrayList<Integer> outputs = new ArrayList<Integer>();
			outputs.addAll(it.next());
			this.getOutputsList().add(outputs);
		}

		this.nextStates = new ArrayList<Integer>();
		this.nextStates.addAll(other.getNextStates());

		this.encodedStateTableLength = other.encodedStateTableLength;

	}

	/**
	 * Helper function used to copy the encoded state table from another Mealy
	 * FSM, starting from a position, to this.
	 * 
	 * @param other
	 *            Mealy FSM from which we copy thresholds
	 * @param pos
	 *            position from which copy is started
	 */
	private void copyPartofEncodedStateTableFromOther(MealyFSM other, int pos) {
		int tmppos = 0;

		Iterator<ArrayList<Integer>> it = other.getOutputsList().iterator();
		Iterator<Integer> it2 = other.getNextStates().iterator();

		for (; it.hasNext() && it2.hasNext();) {
			if (tmppos > pos) {
				// get output & nextstate from iterators:

				ArrayList<Integer> outputs = new ArrayList<Integer>();
				outputs.addAll(it.next());

				Integer nextState = it2.next();

				// depending on if other is longer or not: add or set values
				if (tmppos < this.encodedStateTableLength) {
					this.getOutputsList().set(tmppos, outputs);
					this.getNextStates().set(tmppos, nextState);
				} else {
					this.getOutputsList().add(outputs);
					this.nextStates.add(nextState);
				}
			} else {
				it.next();
				it2.next();
			}
			tmppos++;
		}

		this.encodedStateTableLength = other.encodedStateTableLength;

		// if other's table length is smaller, we need to delete some elements..
		Iterator<ArrayList<Integer>> tIt = this.getOutputsList().iterator();
		Iterator<Integer> tIt2 = this.getNextStates().iterator();

		int tmp = 0;
		for (; tIt.hasNext() && tIt2.hasNext();) {
			tIt.next();
			tIt2.next();

			if (tmp >= this.encodedStateTableLength) {
				tIt.remove();
				tIt2.remove();
			}
			tmp++;
		}

		if (this.encodedStateTableLength != this.getOutputsList().size()
				|| this.encodedStateTableLength != this.getNextStates().size())
			System.err
					.println("Problem in one-point crossover, size of encoded state table not correct!");

	}

	/** Return a random state number. */
	private int getRandomState() {
		return generator.nextInt(nState);
	}

	/** Generate a random output. */
	private ArrayList<Integer> generateRandomOutput() {
		ArrayList<Integer> outputs = new ArrayList<Integer>();
		for (int j = 0; j < params.nOutput; j++) {
			outputs.add(generator.nextInt(params.output_units + 1));
		}
		return outputs;
	}

	/**
	 * Generates a Mealy FSM so that all of the states are reachable. Based on
	 * the following article: Simao, A., Petrenko, A. and Maldonado, J. C.:
	 * Comparing finite state machine test coverage criteria, IET Software, 3
	 * (2) April 2009 : 91-105.
	 */
	public MealyFSM genSimaoRandomFSM() {
		// Simao, A., Petrenko, A. and Maldonado, J. C.: Comparing finite state
		// machine test coverage criteria, IET Software, 3 (2) April 2009 :
		// 91-105.
		//
		// 1. generate sets of states, inputs and outputs as required
		// 2. Phase 1: Create an initially connected FSM
		// - select a state as initial state and mark it as 'reached'.
		// - for each state s not marked as 'reached':
		// add transition from a randomly selected 'reached' s state,
		// with random input, output; mark s as 'reached'
		//
		// 3. Phase 2: Add more transitions if needed (ps. we want to set all
		// transitions!)
		// - by randomly selecting two states, an input and an output

		LinkedList<Integer> reachedList = new LinkedList<Integer>();
		LinkedList<Integer> unreachedStateList = new LinkedList<Integer>();

		for (int i = 0; i < nState; i++)
			unreachedStateList.add(i);

		initStateTransitionTable();
		// this.nTransition = 0;

		int inputs = inputMapper.getInputValueCombinations();

		this.initState = getRandomState();
		reachedList.add(this.initState);

		// build an array with positions in the state tr table that are null
		ArrayList<Integer> nullPositions = new ArrayList<Integer>();
		for (int i = 0; i < inputs; i++) {
			int pos = i * nState + initState;
			nullPositions.add(pos);
			// if (this.getOutputsList().get(pos) != null)
			// System.out.println("init state has already connections??");
		}
		// System.out.println("null positions: " + nullPositions);
		unreachedStateList.remove(this.initState);

		int rnd_unmarked_pos;

		// for not marked states:
		while (!unreachedStateList.isEmpty()) {
			rnd_unmarked_pos = 0; // always get first state
			int ustate = unreachedStateList.get(rnd_unmarked_pos);

			// selects random reached state and input as well
			int rnd_pos = this.generator.nextInt(nullPositions.size());

			int position = nullPositions.get(rnd_pos);
			this.outputsList.set(position, generateRandomOutput());
			this.nextStates.set(position, ustate);

			// mark state reached:
			reachedList.add(ustate);
			// remove selected random connection!
			nullPositions.remove(rnd_pos);

			// add new connections..
			for (int i1 = 0; i1 < inputs; i1++) {
				int pos = i1 * nState + ustate;
				nullPositions.add(pos);
				// if (this.getOutputsList().get(pos) != null)
				// System.out.println("newly reached state has already connections??");
			}
			// System.out.println("null positions: " + nullPositions);

			unreachedStateList.remove(rnd_unmarked_pos);

		}

		// System.out.println("FSM Phase 1: " + this.genStringRepr());

		// Phase2: add further transitions
		// PS. we need transitions for all inputs, so we will not limit number
		// of transitions
		for (int i = 0; i < this.encodedStateTableLength; i++) {
			if ((this.getOutputsList().get(i) == null)) {
				getOutputsList().set(i, generateRandomOutput());
				getNextStates().set(i, generator.nextInt(nState));
			}
		}
		// System.out.println("FSM Phase 2: " + this.genStringRepr());

		// reset so that generated fsm has init state 0
		resetToZeroInit(this.initState);

		return this;
	}

	/** Generate Mealy FSM with random generation. */
	public void genRandomFSM() {
		initStateTransitionTable();

		for (int j = 0; j < inputMapper.getInputValueCombinations(); j++) {
			for (int i = 0; i < this.nState; i++) {

				// generate next state and action (output) randomly
				try {
					outputsList
							.set(i + j * this.nState, generateRandomOutput());
					nextStates.set(i + j * this.nState,
							generator.nextInt(nState));
				} catch (Exception e) {
					if (generator == null)
						System.out.println("Random generator NULL");
					System.out.println("Problem encountered: i=" + i + ", j="
							+ j);
					break;
				}
			}
		}
	}

	@Override
	public void exportToFile(File saveFile) {
		String extension = FrevoMain.getExtension(saveFile);
		if (extension.equals("xml")) {
			System.out.println("Saving to XML");

			Document doc = DocumentHelper.createDocument();
			doc.addDocType(
					"MealyFSM",
					null,
					System.getProperty("user.dir")
							+ "//Components//Representations//MealyFSM//mealyfsm//MealyFSM.dtd");
			Element cnetwork = doc.addElement("MealyFSM");
			this.exportToXmlElement(cnetwork);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setLineSeparator(System.getProperty("line.separator"));

			try {
				saveFile.createNewFile();
				FileWriter out = new FileWriter(saveFile);
				BufferedWriter bw = new BufferedWriter(out);
				XMLWriter wr = new XMLWriter(bw, format);
				wr.write(doc);
				wr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void exportToXmlElement(Element element) {
		Element fsm = element.addElement("fsm");
		// in order to init enumparams to load their properties..
		fsm.addAttribute("name", String.valueOf(this.name));
		if (this.isEvaluated()) {
			fsm.addAttribute("fitness", String.valueOf(this.getFitness()));
		}

		fsm.addAttribute("num_of_states", String.valueOf(this.nState));
		fsm.addAttribute("num_of_inputs", String.valueOf(this.params.nInput));
		fsm.addAttribute("num_of_outputs", String.valueOf(this.params.nOutput));

		fsm.addAttribute("init_state", String.valueOf(this.initState));
		this.inputMapper.exportXml(fsm);

		Element fsm_states = fsm.addElement("fsm_states");
		Iterator<ArrayList<Integer>> ouputIt = this.getOutputsList().iterator();
		Iterator<Integer> stateIt = this.getNextStates().iterator();

		while (ouputIt.hasNext() && stateIt.hasNext()) {
			Element node;

			node = fsm_states.addElement("fsm_state");
			node.addAttribute("next_state", Integer.toString(stateIt.next()));
			node.addAttribute("nunit", Integer.toString(params.output_units));
			node.addAttribute("outputs",
					getOutputsAsString(ouputIt.next(), ","));
		}
	}

	/** Initiate state transition table. */
	private void initStateTransitionTable() {
		outputsList = new ArrayList<ArrayList<Integer>>();
		nextStates = new ArrayList<Integer>();
		for (int i = 0; i < encodedStateTableLength; i++) {
			ArrayList<Integer> outputs = null;
			getOutputsList().add(outputs);
			getNextStates().add(0);
		}
	}

	@Override
	public Hashtable<String, String> getDetails() {
		Hashtable<String, String> result = new Hashtable<String, String>();
		result.put("name", this.name);
		result.put("number of states", Integer.toString(this.nState));
		result.put("init state", Integer.toString(this.initState));
		result.put("generation mode", params.getGenerationMode().toString());

		// show details of inputs - thresholds
		IntegerInput[] inputs = this.inputMapper.getInputs();

		result.put("number of inputs",
				Integer.toString(this.inputMapper.getNumberOfInputs()));

		// if input is thresholded, print threshold information
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput)
				result.put("thresholds for input " + i,
						((ThresholdedIntegerInput) inputs[i])
								.exportThresholds(","));
			else
				result.put("threshold for input " + i, "no threshold");

		}

		// show details about transitions:
		Iterator<ArrayList<Integer>> outputIt = this.getOutputsList()
				.iterator();
		Iterator<Integer> stateIt = this.getNextStates().iterator();
		ArrayList<Integer> elem = new ArrayList<Integer>();

		// for each transition...
		int i = 0;
		StringBuilder repr = new StringBuilder();

		int[] counts = new int[inputMapper.getNumberOfInputs()];
		for (int j = 0; j < counts.length; j++) {
			counts[j] = inputs[j].getNumberOfInputValues();
		}

		// we will put current input values in an array and
		// increment it during the while cycle
		// TODO: maybe later on we should replace this with an iterator?
		int[] currentInputs = new int[inputMapper.getNumberOfInputs()];
		while (outputIt.hasNext() && stateIt.hasNext()) {
			elem = outputIt.next();

			if (elem == null) {
				repr.append("null");
				stateIt.next();
			} else {
				repr.append(padded(stateIt.next(), nState))
					.append("(")
					.append(getOutputsAsString(elem, ","))
					.append(")");
			}

			if (outputIt.hasNext() && (i % nState != (nState - 1)))
				repr.append(",");

			i++;

			if ((i % nState == 0 && outputIt.hasNext()) || !outputIt.hasNext()) {
				result.put(
						"transitions for input "
								+ getInputListString(currentInputs, ",") + ":",
						repr.toString());
				repr = new StringBuilder();
				i = 0;

				// increment currentpositions..
				int pos = currentInputs.length - 1;
				currentInputs[pos]++;
				while (pos >= 0 && currentInputs[pos] == counts[pos]) {
					currentInputs[pos] = 0;
					pos--;
					if (pos >= 0)
						currentInputs[pos]++;
				}
			}

		}
		result.put("string representation with thresholds",
				this.genStringReprWithThresholds());

		return result;
	}

	/**
	 * Return string representation of input values. For thresholded values,
	 * "val > .." or " val <= .." will be given.
	 * 
	 * @param values
	 *            list of input positions
	 * @param separator
	 *            separator of values
	 */
	private String getInputListString(int[] values, String separator) {
		StringBuilder list = new StringBuilder("(");
		IntegerInput[] inputs = inputMapper.getInputs();

		for (int i = 0; i < values.length; i++) {
			int th = (int) (inputs[i].getMin() + inputs[i].getUnitLenght()
					* values[i]);
			// for just one threshold:
			// format value as val > <threshold> for 1
			// and val <= threshold for 0
			if (inputs[i] instanceof ThresholdedIntegerInput) {
				list.append(((ThresholdedIntegerInput) inputs[i])
						.getLabel(values[i]));
			} else
				list.append(String.valueOf(th));

			if (i < values.length - 1)
				list.append(separator).append(" ");

		}
		list.append(")");

		return list.toString();
	}

	@Override
	public int getNumberofMutationFunctions() {
		return 1;
	}

	@Override
	public int getNumberOfRecombinationFunctions() {
		return 2;
	}

	@Override
	public String getHash() {
		return name;

	}

	@Override
	public AbstractRepresentation loadFromXML(Node nd) {
		// method called when results are loaded..
		try {
			String fitnessString = nd.valueOf("./@fitness");
			if (!fitnessString.isEmpty()) {			
				this.setFitness(Double.parseDouble(fitnessString));
			}
			if (this.generator == null)
				this.generator = new NESRandom();
			this.name = nd.valueOf("./@name");

			this.nState = Integer.parseInt(nd.valueOf("./@num_of_states"));
			int nInput = Integer.parseInt(nd.valueOf("./@num_of_inputs"));
			int nOutput = Integer.parseInt(nd.valueOf("./@num_of_outputs"));

			this.initState = Integer.parseInt(nd.valueOf("./@init_state"));
			this.currentState = this.initState;

			this.inputMapper = InputMapper.loadFromXML(nd);

			this.params = new EvolutionParams((short) nInput, (short) nOutput,
					getProperties());

			this.encodedStateTableLength = getEncodedStateTableLength();
			Node inodes = nd.selectSingleNode("./fsm_states");

			List<Node> nodeList = inodes.selectNodes("./fsm_state");

			this.outputsList = new ArrayList<ArrayList<Integer>>();
			this.nextStates = new ArrayList<Integer>();

			ArrayList<Integer> outputs;
			for (Node node : nodeList) {
				int next_state = Integer
						.parseInt(node.valueOf("./@next_state"));
				String outputstr = node.valueOf("./@outputs");

				outputs = new ArrayList<Integer>();

				StringTokenizer tokenizer = new StringTokenizer(outputstr, ",;");
				int i = 0;

				while (tokenizer.hasMoreTokens() && i < nOutput) {
					outputs.add(Integer.valueOf(tokenizer.nextToken()));
					i++;
				}
				getOutputsList().add(outputs);
				getNextStates().add(next_state);
			}

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"MealyFSM: NumberFormatException! Check XML File");
		}
		return this;
	}

	@Override
	public void reset() {
		this.currentState = this.initState;
		this.usedStates = new TreeSet<Integer>(); // null
		this.stateHistogram = new int[this.nState];
		for (int i = 0; i < stateHistogram.length; i++) {
			stateHistogram[i] = 0;
		}
	}

	@Override
	public void recombinationFunction(AbstractRepresentation other, int method) {
		try {
			switch (method) {
			case 1:
				uniformCrossover((MealyFSM) other);
				break;
			case 2:
			default:
				onepointCrossover((MealyFSM) other);
			}

			countCrossover++;
			this.name = origName + "_m" + countMutation + "_x" + countCrossover;
		} catch (Exception ex) {
			System.err.println("Crossover failed.");
			ex.printStackTrace();
		}

		// TODO: xover of FSMs with different length (not with fixed size)
		// is only prepared - further state encoding check must be performed
		// e.g. if next state is > than max state -> replace it with random!
	}

	@Override
	protected void mutationFunction(float severity, float probability,
			int method) {
		if (false == params.isFixedStateFSM()) {
			mutationAddState(params.mutate_add_state_prob);
			mutationRemoveState(params.mutate_del_state_prob);
		}

		mutateThresholds(params.mutate_threshold_prob);
		mutationChangeInitState(params.mutate_mod_init_state_prob);
		mutationChangeOutputs(params.mutate_mod_tr_output_prob);
		mutationChangeNextStates(params.mutate_mod_tr_nextstate_prob);

		countMutation++;
		this.name = origName + "_m" + countMutation + "_x" + countCrossover;
	}

	/**
	 * Generate String representation of Mealy FSM. String representation
	 * contains the initial state, number of thresholds used for each input and
	 * the encoded state transition table (having format of
	 * "next state (outputs)").
	 * <P>
	 * A part of the Mealy FSM's string representation is the following (the FSM
	 * has 6 states, its initial state is 0 and it has one threshold for each
	 * input. Then the string representation looks like this:
	 * <P>
	 * 000,001,001,1(072,020),4(053,066),3(044,067),3(040,021), ...1(063,087).
	 * 
	 * As mainly not the threshold's value will matter, but rather the
	 * associated transitions, threshold values are not included in the String
	 * representation.
	 * 
	 * @return String representation of Mealy FSM
	 */

	public String genStringRepr() {
		StringBuilder repr = new StringBuilder();

		// add init state
		repr.append(padded(this.initState, 100));
		repr.append(",");

		// add how many thresholds are used for the inputs
		repr.append(inputMapper.getNThresholdsForInputsAsString())
				.append(",");

		Iterator<ArrayList<Integer>> outputsIt = this.getOutputsList()
				.iterator();
		Iterator<Integer> stateIt = this.getNextStates().iterator();
		int i = 0;
		ArrayList<Integer> elem = new ArrayList<Integer>();
		while (outputsIt.hasNext() && stateIt.hasNext()) {
			elem = outputsIt.next();

			if (elem == null) {
				repr.append("null");
				stateIt.next();
			} else {
				repr.append(padded(stateIt.next(), nState))
						.append("(")
						.append(getOutputsAsString(elem, ","))
						.append(")");
			}

			if (outputsIt.hasNext() && (i % nState != nState - 1))
				repr.append(",");

			i++;
			if (i % nState == 0 && outputsIt.hasNext())
				repr.append("-");
		}
		return repr.toString();

	}

	/**
	 * Generate a String representation of Mealy FSM that includes threshold
	 * values as well. String representation contains the initial state, list of
	 * thresholds for each input and the encoded state transition table (having
	 * format of "next state (outputs)").
	 * <P>
	 * A part of the Mealy FSM's string representation is the following (the FSM
	 * has 6 states, its initial state is 0 and it has one threshold for each
	 * input: 6 and 28. Then the string representation looks like this:
	 * <P>
	 * 000,06,28,1(072,020),4(053,066),3(044,067),3(040,021), ...1(063,087).
	 * <P>
	 * If it has two thresholds for the first input (2 and 3), and none for the
	 * second, then the state transition table would be longer and the
	 * representation would start differently:
	 * <P>
	 * 000,[02;03],[],1(072,020),4(053,066),3(044,067),3(040,021),
	 * ...1(063,087).
	 * 
	 * @return String representation of Mealy FSM
	 */

	public String genStringReprWithThresholds() {
		StringBuilder repr = new StringBuilder();

		// add init state
		repr.append(padded(this.initState, 100));
		repr.append(",");

		// add thresholds too!
		repr.append(inputMapper.getThresholdsAsString())
				.append(",");

		Iterator<ArrayList<Integer>> outputsIt = this.getOutputsList()
				.iterator();
		Iterator<Integer> stateIt = this.getNextStates().iterator();
		int i = 0;
		ArrayList<Integer> elem = new ArrayList<Integer>();
		while (outputsIt.hasNext() && stateIt.hasNext()) {
			elem = outputsIt.next();

			if (elem == null) {
				repr.append("null");
				stateIt.next();
			} else {
				repr.append(padded(stateIt.next(), nState));
				repr.append("(");
				repr.append(getOutputsAsString(elem, ","));
				repr.append(")");
			}

			if (outputsIt.hasNext() && (i % nState != nState - 1))
				repr.append(",");

			i++;
			if (i % nState == 0 && outputsIt.hasNext())
				repr.append("-");
		}
		return repr.toString();
	}

	/**
	 * Returns output list as a string.
	 * 
	 * @param outputs
	 *            output list associated with a transition
	 * @param separater
	 *            String separator used to separate output values
	 * 
	 * @return formatted output or <i>null</i> in case output is not set
	 */
	public String getOutputsAsString(ArrayList<Integer> outputs,
			String separator) {
		StringBuilder str = new StringBuilder();
		if (outputs == null)
			return "null";
		Iterator<Integer> it = outputs.iterator();
		for (; it.hasNext();) {
			str.append(padded(it.next(), params.output_units));
			if (it.hasNext()) {
				str.append(separator);
			}
		}
		return str.toString();
	}

	/**
	 * Return how many states were used during evaluation of FSM.
	 * 
	 * @return count of used states
	 * */
	public int getCountOfUsedStates() {
		return usedStates.size();
	}

	/**
	 * Return state histogram as an array of integer, each item specifying how
	 * many times was the FSM in a specific state. First value is for state 0,
	 * second is for state 1 etc.
	 * 
	 * @return array of integer representing histogram of used states
	 */
	public int[] getStateHistogram() {
		return stateHistogram;
	}

	/**
	 * Return list of used states.
	 * 
	 * @return a list of used states
	 * */
	public ArrayList<Integer> getUsedStateList() {
		return new ArrayList<Integer>(usedStates);
	}

	/**
	 * Return outputs associated with a transition as FREVO expects: a list of
	 * floats.
	 * 
	 * @return a list of output values
	 */
	public ArrayList<Float> getFloatOutputs(ArrayList<Integer> outputs) {
		ArrayList<Float> result = new ArrayList<Float>();
		for (int i = 0; i < params.nOutput; i++) {
			result.add(1.0f * outputs.get(i) / params.output_units);
		}

		return result;
	}

	@Override
	public ArrayList<Float> getOutput(ArrayList<Float> input) {
		int position = inputMapper.getPositionInSortedInputList(input);

		int oldstate = this.getCurrentState();
		this.usedStates.add(oldstate);
		this.stateHistogram[oldstate]++;

		try {
			this.currentState = (getNextStates().get(position * this.nState
					+ oldstate));
			// System.out.println("curr state: " + oldstate + ", next state: " +
			// this.currentState);
		} catch (Exception ex) {
			System.out.println("Error occured");
			ex.printStackTrace();
		}

		ArrayList<Float> res = new ArrayList<Float>();
		res = getFloatOutputs(getOutputsList().get(
				position * this.nState + oldstate));

		return res;
	}

	/**
	 * Get outputs associated with all transitions (part of the encoding of
	 * state transition table).
	 * 
	 * @return list of outputs
	 */
	public ArrayList<ArrayList<Integer>> getOutputsList() {
		return outputsList;
	}

	/**
	 * Get next states associated with all transitions (part of the encoding of
	 * state transition table).
	 * 
	 * @return list of next states
	 */
	public ArrayList<Integer> getNextStates() {
		return nextStates;
	}

	/**
	 * Set next state in the encoded state transition table.
	 * 
	 * @param pos
	 *            position of the transition in the state transition table
	 * @param nextState
	 *            next state to be set for a transition
	 * */
	public void setNextState(int pos, int nextState) {
		this.nextStates.set(pos, nextState);
	}

	/**
	 * Returns position of an input value combination in a sorted list, where
	 * all input value combinations are listed.
	 * 
	 * @return position of input in the sorted input list
	 */
	public int getPositionInSortedInputList(ArrayList<Float> input) {
		return inputMapper.getPositionInSortedInputList(input);
	}

	/**
	 * Return current state of the FSM.
	 * 
	 * @return current state
	 * */
	public int getCurrentState() {
		return currentState;
	}

	/**
	 * Convert an integer value into a String by padding with 0. The length of
	 * the String depends on the Max value permitted for the integer.
	 * 
	 * @param value
	 *            integer value
	 * @param maxValue
	 *            maximum of the values permitted
	 * 
	 * @return formatted integer value as String
	 */
	private static String padded(int value, int maxValue) {
		StringBuilder padded = new StringBuilder();
		for (int i = 0; i < Integer.toString(maxValue).length()
				- Integer.toString(value).length(); i++) {
			padded.append("0");
		}
		padded.append(Integer.toString(value));
		return padded.toString();
	}

	@Override
	public String getC() throws FileNotFoundException {
		System.err.println ("MealyFSM not implemented!");
		return null;
	}

}
