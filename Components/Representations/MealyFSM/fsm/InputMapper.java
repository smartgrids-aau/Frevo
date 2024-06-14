package fsm;

import fsm.ThresholdedIntegerInput.MutationType;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Element;
import org.dom4j.Node;

import core.XMLFieldEntry;

import utils.NESRandom;

/***
 * This class serves as a mapper between input values (sensor values) and values
 * forwarded to the finite state machine. The reason behind using a mapper is
 * that a deterministic finite state machine needs to have a transition for
 * every possible combination of input values, for each state. If the number of values
 * permitted for each input are high, there might be a huge number of
 * transitions required for each state. By introducing this class, we can set
 * for each input whether it should be thresholded (transformed into binary -
 * using one threshold) or not. Currently one can set for each input the
 * following parameters: min, max, number of units, number of thresholds.
 * 
 * The class handles mutation, crossover of thresholds, export / import of
 * inputs as well.
 * 
 * @author Agnes Pinter-Bartha
 * 
 */
public class InputMapper {

	enum Defaults {
		DEFAULT_MIN(0),
		DEFAULT_MAX(100),
		DEFAULT_NUNIT(100),
		DEFAULT_NTHRESHOLDS(1);

		int value;

		Defaults(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private NESRandom generator;
	private int nInput;
	private int[] thresholdIds;
	private IntegerInput[] inputs;
	private Hashtable<String, XMLFieldEntry> properties;

	private static boolean showLoadError = true;

	/**
	 * Constructor of InputMapper class. Creates an instance of the class by
	 * loading values from properties already loaded into memory or using
	 * defaults.
	 * 
	 * @param properties
	 *            Properties loaded from XML into a Hashtable
	 * @param generator
	 *            Random number generator
	 * @param nInput
	 *            Number of inputs
	 */
	public InputMapper(Hashtable<String, XMLFieldEntry> properties,
			NESRandom generator, int nInput) {
		this.nInput = nInput;
		this.inputs = new IntegerInput[nInput];
		this.generator = generator;
		this.properties = properties;

		try {
			loadProperties();
		} catch (Exception ex) {
			if (showLoadError)
				System.err
						.println("Couldn't load input related properties, using defaults (all input thresholded)!");
			loadDefaultProperties();
		}
	}

	/***
	 * Constructor for InputMapper class that only requires a random number
	 * generator and the number of inputs. Mainly used when cloning the object.
	 * 
	 * 
	 * @param generator
	 *            random number generator
	 * @param nInput
	 *            number of inputs
	 */
	private InputMapper(NESRandom generator, int nInput) {
		this.nInput = nInput;
		this.generator = generator;
		this.inputs = new IntegerInput[nInput];

		loadDefaultProperties();
	}

	/***
	 * Constructor for InputMapper class that only requires the number of
	 * inputs. Mainly used when "importing" object from xml.
	 * 
	 * @param nInput
	 *            number of inputs
	 */
	private InputMapper(int nInput) {
		this.nInput = nInput;
		showLoadError = false;
	}

	/***
	 * Returns a clone of the object.
	 */
	public InputMapper clone() {
		InputMapper clone = new InputMapper(this.generator, this.nInput);

		clone.inputs = new IntegerInput[this.inputs.length];
		clone.thresholdIds = new int[this.thresholdIds.length];

		// copy input informations
		for (int i = 0; i < this.inputs.length; i++) {
			clone.inputs[i] = this.inputs[i].clone();
		}
		// copy thresholds
		System.arraycopy(this.thresholdIds, 0, clone.thresholdIds, 0, this.thresholdIds.length);

		return clone;
	}

	/**
	 * Returns number of inputs.
	 * 
	 * @return number of inputs
	 */
	public int getNumberOfInputs() {
		return nInput;
	}

	/**
	 * Returns the number of thresholded inputs.
	 * 
	 * @return number of thresholded inputs
	 */
	public int getNumberOfThresholdedInputs() {
		return thresholdIds.length;
	}

	/**
	 * Retrieve an array of inputs (actually IntegerInput-s).
	 * 
	 * @return array of {@link IntegerInput}
	 */
	public IntegerInput[] getInputs() {
		return inputs;
	}

	/**
	 * Export the instance of the class into an xml element.
	 * 
	 * @param element
	 *            XML Element
	 * @return XML Element
	 */
	public Element exportXml(Element element) {

		Element mapper = element.addElement("input_mapper");
		mapper.addAttribute("num_of_thresholds",
				String.valueOf(this.thresholdIds.length));
		mapper.addAttribute("num_of_inputs", String.valueOf(this.nInput));

		Element thresholds = mapper.addElement("inputs");

		for (int i = 0; i < inputs.length; i++) {
			Element node = thresholds.addElement("input");

			node.addAttribute("min", Integer.toString(inputs[i].min));
			node.addAttribute("max", Integer.toString(inputs[i].max));
			node.addAttribute("nunit", Integer.toString(inputs[i].nUnit));

			if (inputs[i] instanceof ThresholdedIntegerInput) {
				node.addAttribute("thresholds",
						((ThresholdedIntegerInput) inputs[i])
								.exportThresholds(","));
			} else {
				node.addAttribute("thresholds", "null");
			}
		}
		return mapper;
	}

	/**
	 * Handles mutation of thresholds.
	 * 
	 * @param prob
	 *            probability of mutating a threshold
	 * @param type
	 *            type of mutation {@link MutationType}
	 */
	public void mutateThresholds(float prob, MutationType type) {
		for (int i = 0; i < thresholdIds.length; i++) {
			((ThresholdedIntegerInput) inputs[i]).mutateThresholds(prob, type);
		}
	}

	/***
	 * Retrieve the thresholds for all inputs as a string. A number specifying
	 * how many thresholds are used for the following input will be given before
	 * the list of thresholds for each input. Example: if you have two inputs,
	 * none thresholded, you will get back a list with "[],[]" as elements. If
	 * the first input is thresholded, using three thresholds (1 and 4), second
	 * input not thresholded, then the list will look like "[1;4],[]".
	 * 
	 * @return all thresholds used for the inputs (led by number of thresholds)
	 *         as String
	 */
	public String getThresholdsAsString() {
		StringBuilder thrStr = new StringBuilder();

		for (int i = 0; i < nInput; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput) {
				thrStr.append("[");
				thrStr.append(((ThresholdedIntegerInput) inputs[i])
						.exportThresholds(";"));
				thrStr.append("]");
			} else {
				thrStr.append("[]");
			}
			if (i < nInput - 1)
				thrStr.append(",");
		}

		return thrStr.toString();
	}

	/***
	 * Retrieve the number of thresholds for each input as a string. Numbers are
	 * padded (length is set based on the number of units) and separated by
	 * comma (","). Example: if you have two inputs, none thresholded, you will
	 * get back a list with "000,000" as elements.
	 * 
	 * @return number of thresholds used for each input as a comma separated
	 *         value string
	 */
	public String getNThresholdsForInputsAsString() {
		StringBuilder thrStr = new StringBuilder();

		for (int i = 0; i < nInput; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput) {
				thrStr.append(padded(
						((ThresholdedIntegerInput) inputs[i]).getNThreshold(),
						((ThresholdedIntegerInput) inputs[i]).nUnit));
			} else {
				thrStr.append(padded(0, inputs[i].nUnit));
			}
			if (i < nInput - 1)
				thrStr.append(",");
		}

		return thrStr.toString();
	}

	/**
	 * Retrieve how many thresholds are used overall for all inputs.
	 * 
	 * @return count of all thresholds
	 */
	public int getCountOfAllThresholds() {
		int count = 0;

		for (int i = 0; i < nInput; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput)
				count += ((ThresholdedIntegerInput) inputs[i]).getNThreshold();
		}
		return count;
	}

	/**
	 * Set a threshold's value.
	 * 
	 * @param index
	 *            index of input for which threshold value is set
	 * @param pos
	 *            position of threshold for the specific input
	 * @param value
	 *            value of the threshold
	 */
	public void setThreshold(int index, int pos, Integer value) {
		if (doesArrayContainElement(thresholdIds, index)) {
			((ThresholdedIntegerInput) inputs[index]).setThreshold(pos, value);
		} else
			// TODO: maybe also raise an exception...
			System.err
					.println("Cannot modify threshold. No input with this id to threshold.");
	}

	/***
	 * Returns true if id is contained by the array of id. Otherwise returns
	 * false.
	 * 
	 * @param ids
	 *            array of ids
	 * @param id
	 *            id
	 * @return boolean value specifying whether the id is contained in the array
	 *         of ids
	 */
	private static boolean doesArrayContainElement(int[] ids, int id) {
		boolean isElement = false;

		search: for (int i = 0; i < ids.length; i++) {
			if (id == ids[i]) {
				isElement = true;
				break search;
			}
		}

		return isElement;
	}

	/**
	 * Calculates sum of all thresholds.
	 * 
	 * @return sum of all threhsolds
	 */
	public int getSumOfThresholds() {
		int sum = 0;
		for (int i = 0; i < nInput; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput)
				sum += ((ThresholdedIntegerInput) inputs[i])
						.getSumOfThresholds();
		}
		return sum;
	}

	/**
	 * Returns threshold for a specific input and at a specific position.
	 * 
	 * @param i
	 *            index of input
	 * @param pos
	 *            position of threshold in the input's threshold list
	 * @return
	 */
	public int getThreshold(int i, int pos) {
		return ((ThresholdedIntegerInput) getInput(i)).getThreshold(pos);
	}

	/**
	 * Set a given input's threshold to a given value.
	 * 
	 * @param i
	 *            index of input
	 * @param pos
	 *            position of threshold in the list of thresholds
	 * @param value
	 *            value to be set for the threshold
	 */
	public void setThresholdId(int i, int pos, int value) {
		if (doesArrayContainElement(thresholdIds, i)) {
			((ThresholdedIntegerInput) inputs[i]).setThreshold(pos, value);
		}
	}

	/**
	 * Method to create an InputMapper instance from an XML node.
	 * 
	 * @param n
	 *            XML node
	 * @return an instance of the class
	 */
	public static InputMapper loadFromXML(Node n) {
		InputMapper inputMapper;// = new InputMapper();

		try {
			Node nd = n.selectSingleNode("./input_mapper");

			int nThreshold = Integer.parseInt(nd
					.valueOf("./@num_of_thresholds"));
			int nInput = Integer.parseInt(nd.valueOf("./@num_of_inputs"));

			inputMapper = new InputMapper(nInput);
			inputMapper.thresholdIds = new int[nThreshold];

			Node inodes = nd.selectSingleNode("./inputs");
			List<Node> nodeList = inodes.selectNodes("./input");

			inputMapper.inputs = new IntegerInput[inputMapper.nInput];

			int i = 0;
			int nTh = 0;
			for (Node node : nodeList) {
				int min = Integer.parseInt(node.valueOf("./@min"));
				int max = Integer.parseInt(node.valueOf("./@max"));
				int nunit = Integer.parseInt(node.valueOf("./@nunit"));

				String thresholds = node.valueOf("./@thresholds"); // version
																	// 2.1
				// v1.0: one threshold
				if (node.valueOf("./@thresholds") == null
						|| node.valueOf("./@thresholds").equals("")) {
					
					String threshold = node.valueOf("./@threshold");
					if (threshold.equals("null") || threshold == null
							|| threshold.equals("no threshold")) {
						// add integer input
						inputMapper.inputs[i] = new IntegerInput(min, max,
								nunit);
					} else {
						// add thresholded input
						int[] values = new int[1];
						values[0] = (int) Integer.parseInt(threshold);
						inputMapper.inputs[i] = new ThresholdedIntegerInput(
								min, max, nunit, 1, values);
						nTh++;
					}
				}

				// v2.0: multiple thresholds
				else {
					if (thresholds.equals("null")) {
						inputMapper.inputs[i] = new IntegerInput(min, max,
								nunit);
						nTh++;
					} else {
						StringTokenizer tokenizer = new StringTokenizer(
								thresholds, ",");
						ArrayList<Integer> tokens = new ArrayList<Integer>();
						while (tokenizer.hasMoreTokens()) {
							tokens.add(Integer.valueOf(tokenizer.nextToken()));
						}

						int[] values = new int[tokens.size()];
						for (int j = 0; j < values.length; j++) {
							values[j] = tokens.get(j);
						}
						inputMapper.inputs[i] = new ThresholdedIntegerInput(
								min, max, nunit, values.length, values);
						nTh++;
					}
				}
				i++;
			}

			int k = 0;
			if (nTh > 0) {
				inputMapper.thresholdIds = new int[nTh];
				for (int j = 0; j < inputMapper.inputs.length; j++) {
					if (inputMapper.inputs[j] instanceof ThresholdedIntegerInput) {
						inputMapper.thresholdIds[k] = j;
						k++;
					}
				}
			}

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"MealyFSM: NumberFormatException! Check XML File");
		}

		return inputMapper;
	}

	/**
	 * Convert the given ArrayList parameter into a String.
	 * 
	 * @param list
	 *            ArrayList of Integers
	 * @return a formatted String, or "null" if parameter is null
	 */
	public static String getIntegerArrayListAsString(ArrayList<Integer> list) {
		StringBuilder str = new StringBuilder();
		if (list == null)
			return "null";
		Iterator<Integer> it = list.iterator();
		for (; it.hasNext();) {
			str.append(it.next().toString());
			if (it.hasNext())
				str.append(",");
		}
		return str.toString();
	}

	/**
	 * Method to mutate thresholds.
	 * 
	 * @param id
	 *            index of thresholded input
	 * @param pos
	 *            position of threshold for the selected input
	 * @param mutationType
	 *            type of mutation to be applied, see {@link MutationType}
	 */
	public void mutateThresholds(int id, int pos, MutationType mutationType) {
		int index = thresholdIds[id];
		ThresholdedIntegerInput input = (ThresholdedIntegerInput) inputs[index];

		input.mutateThresholds(pos, mutationType);
	}

	/**
	 * Converts the inputs to their position between min and max. Min beeing 0.
	 * For counting the positions, we count basically how many times is needed
	 * to add unit to min to reach the sensor value.
	 * 
	 * @param sensorInputs
	 *            ArrayList of input values
	 * @return ArrayList of positions in the [min.. max] interval
	 */
	public ArrayList<Integer> getEncodedInputValues(
			ArrayList<Float> sensorInputs) {
		ArrayList<Integer> currValues = new ArrayList<Integer>();
		for (int i = 0; i < sensorInputs.size(); i++) {
			currValues.add(inputs[i].getPosition((int) ((float) sensorInputs
					.get(i))));
		}
		return currValues;
	}

	/**
	 * Return position of sensor values in the list of possible input value
	 * combinations. In the sorted list, input is "incremented" starting from
	 * the last value. E.g. in case of two binary inputs, the sorted list would
	 * look like: {00, 01, 10, 11}.
	 * 
	 * @param sensorInputs
	 *            ArrayList of Floats describing the sensor values
	 * @return position of sensor values in the list of all possible input
	 *         values.
	 */
	public int getPositionInSortedInputList(ArrayList<Float> sensorInputs) {
		int count = 1;
		for (int i = 0; i < nInput; i++) {
			count = count
					* (inputs[i]
							.getPosition((int) ((float) sensorInputs.get(i))) + 1);
		}

		return count - 1;
	}

	/**
	 * Return how many input value combinations are.
	 * 
	 * @return number of input value combinations
	 */
	public int getInputValueCombinations() {
		int count = 1;
		for (int i = 0; i < nInput; i++) {
			count = count * (inputs[i].getNumberOfInputValues());
		}
		return count;
	}

	/**
	 * Loads default settings. Inputs are thresholded if number of thresholds is
	 * 0.
	 */
	public void loadDefaultProperties() {
		int min = Defaults.DEFAULT_MIN.getValue();
		int max = Defaults.DEFAULT_MAX.getValue();
		int nUnit = Defaults.DEFAULT_NUNIT.getValue();
		int nThreshold = Defaults.DEFAULT_NTHRESHOLDS.getValue();

		thresholdIds = new int[nInput];
		for (int i = 0; i < nInput; i++) {
			thresholdIds[i] = i;
			if (nThreshold == 0)
				inputs[i] = new IntegerInput(min, max, nUnit);
			else
				inputs[i] = new ThresholdedIntegerInput(min, max, nUnit,
						nThreshold, generator);
		}
	}

	/**
	 * Print basic information about InputMapper. Method used mainly for
	 * testing.
	 */
	public void printInfos() {
		System.out.println("Min\t| Max\t| Units\t| Thresholded");
		System.out.println("======================================");
		for (int i = 0; i < nInput; i++) {
			System.out.print(inputs[i].min + "\t| " + inputs[i].max
					+ "\t| " + inputs[i].nUnit + "\t| ");
			if (inputs[i] instanceof ThresholdedIntegerInput)
				System.out.println("yes ("
						+ ((ThresholdedIntegerInput) inputs[i])
								.exportThresholds(",") + ")");
			else
				System.out.println("no ");
		}
		System.out.println("Possible input combinations: "
				+ getInputValueCombinations());
		System.out.println("Number of thresholded values: "
				+ getNumberOfThresholdedInputs());
		System.out.println();
	}

	/**
	 * Load properties from a Hashtable (data structure to which the xml is
	 * transformed when loaded). In case not all min, max, unit information is
	 * given for the inputs, an exception is thrown.
	 * 
	 * @throws IncorrectPropertiesException
	 */
	private void loadProperties() throws IncorrectPropertiesException {
		// read infos about input values (min, max and how many units are
		// betweens min..max)
		ArrayList<Integer> minInputValues = FSMXMLFieldParser
				.getIntArrayProperty(properties, "min_input_values");
		ArrayList<Integer> maxInputValues = FSMXMLFieldParser
				.getIntArrayProperty(properties, "max_input_values");
		ArrayList<Integer> unitInputValues = FSMXMLFieldParser
				.getIntArrayProperty(properties, "unit_input_values");
		// check first two arrays to have the same size
		if (minInputValues.size() != nInput || maxInputValues.size() != nInput
				|| unitInputValues.size() != nInput) {
			if (showLoadError) {
				System.err
						.println("Size of min_input_values, max_input_values and unit_input_values "
								+ "must be equal to "
								+ nInput
								+ " (number of inputs)!"
								+ "If there are 3 input values expected, set three comma separated values for min, max and unit (e.g. min_input_values: 0,0,0)!");
				showLoadError = false;
			}
			throw (new IncorrectPropertiesException());
		}

		int min = 0;
		int max = 0;
		int nUnit = 0;

		ArrayList<Integer> nThresholdsForInputs = new ArrayList<Integer>();
		int aboveToThr = EvolutionParams.DEFAULT_VALUE_COUNT_ABOVE_TO_THRESHOLD;

		int nTh = 0;
		int thIntCount = 0;

		try {
			nThresholdsForInputs = FSMXMLFieldParser.getIntArrayProperty(
					properties, "nthresholds_for_inputs");
			// if no problem with parsing:
			for (int i = 0; i < nInput; i++) {
				min = minInputValues.get(i);
				max = maxInputValues.get(i);
				nUnit = unitInputValues.get(i);

				nTh = nThresholdsForInputs.get(i);
				if (nTh == 0) {
					inputs[i] = new IntegerInput(min, max, nUnit);
				} else {
					thIntCount++;
					inputs[i] = new ThresholdedIntegerInput(min, max, nUnit,
							nTh, generator.clone());
				}
			}

		} catch (Exception ex) {
			// try to read v1 from file..
			try {
				aboveToThr = FSMXMLFieldParser.getIntProperty(properties,
						"value_count_above_to_threshold");
			} catch (Exception ex2) {
				// use the initial defualt value..
			}
			System.out.println("InputMapper: loading v1 properties.. "
					+ "value_count_above_to_threshold: " + aboveToThr);

			// for all inputs, check if input should be thresholded..
			for (int i = 0; i < nInput; i++) {
				min = minInputValues.get(i);
				max = maxInputValues.get(i);
				nUnit = unitInputValues.get(i);

				if (nUnit + 1 <= aboveToThr) {
					inputs[i] = new IntegerInput(min, max, nUnit);
				} else {
					thIntCount++;
					inputs[i] = new ThresholdedIntegerInput(min, max, nUnit,
							nTh, generator.clone());
				}

			}
		}

		// init threshold ids
		thresholdIds = new int[thIntCount];
		int j = 0;

		for (int i = 0; i < nInput; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput) {
				thresholdIds[j] = i;
				j++;
			}
		}

	}

	/**
	 * Return the IntegerInput for the input at the index position.
	 * 
	 * @param index
	 *            index of input
	 * @return a representation of a specific input
	 */
	public IntegerInput getInput(int index) {
		return inputs[index];
	}

	/**
	 * Exception thrown when not all parameters are set for each input.
	 * 
	 */
	class IncorrectPropertiesException extends Exception {
		private static final long serialVersionUID = 310411032648720780L;

		public IncorrectPropertiesException() {
		}

		public IncorrectPropertiesException(String msg) {
			super(msg);
		}
	}

	/**
	 * Method to copy thresholds from an instance of InputMapper to the current
	 * instance.
	 * 
	 * @param other InputMapper instance to copy from
	 */
	public void copyThresholdsFromOther(InputMapper other) {
		int countTh = 0;
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = other.inputs[i].clone();
			if (inputs[i] instanceof ThresholdedIntegerInput)
				countTh++;
		}

		// make sure thresholded ids are correct
		// (if number of thresholds for inputs does not change,
		// this might be unnecessary)
		thresholdIds = new int[countTh];
		int k = 0;
		for (int i = 0; i < nInput; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput) {
				thresholdIds[k] = i;
				k++;
			}
		}

	}

	/***
	 * Method to copy thresholds from an instance of InputMapper to the current
	 * instance, starting from a position. 
	 * @param other InputMapper instance to copy from
	 * @param pos index of input 
	 */
	public void copyThresholdsFromOtherFromPos(InputMapper other, int pos) {
		int countTh = 0;
		for (int i = 0; i < inputs.length; i++) {
			if (i >= pos)
				inputs[i] = other.inputs[i].clone();

			if (inputs[i] instanceof ThresholdedIntegerInput)
				countTh++;
		}

		// make sure thresholded ids are correct
		// (if number of thresholds for inputs does not change,
		// this might be unnecessary)
		thresholdIds = new int[countTh];
		int k = 0;
		for (int i = 0; i < nInput; i++) {
			if (inputs[i] instanceof ThresholdedIntegerInput) {
				thresholdIds[k] = i;
				k++;
			}
		}

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
		padded.append(value);
		return padded.toString();
	}

	/**
	 * Perform uniform crossover with another InputMapper instance.
	 * 
	 * @param other InputMapper instance with which crossover is made
	 */
	public void uniformCrossover(InputMapper other) {
		// for all inputs: generate a bool to decide which inputmapper to take
		// input from.
		int countThresholded = 0;
		for (int i = 0; i < getNumberOfInputs(); i++) {
			if (generator.nextBoolean() == true) {
				// take input from other
				inputs[i] = other.inputs[i].clone();
				if (inputs[i] instanceof ThresholdedIntegerInput)
					countThresholded++;
			}
		}

		// if we are ready, we should fix one more thing: the thresholdIds..
		// this is necessary basically only if the two mappers discretize inputs
		// differently (e.g. one thresholds an input, the other not)
		thresholdIds = new int[countThresholded];

		int k = 0;
		for (int j = 0; j < getNumberOfInputs(); j++) {
			if (inputs[j] instanceof ThresholdedIntegerInput
					&& k < countThresholded) {
				thresholdIds[k] = j;
				k++;
			}
		}
	}

}
