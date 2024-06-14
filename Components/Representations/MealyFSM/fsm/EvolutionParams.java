package fsm;

import java.util.ArrayList;
import java.util.Hashtable;
import core.XMLFieldEntry;

/**
 * Class collects parameters read from properties and default values for these
 * parameters.
 */
public class EvolutionParams {
	// constants
	final static int DEFAULT_NUMBER_OF_STATES = 6;

	final static float DEFAULT_MUTATE_ADD_STATE_PROB = 0.1f;
	final static float DEFAULT_MUTATE_MOD_TR_NEXTSTATE_PROB = 0.1f;
	final static float DEFAULT_MUTATE_MOD_TR_OUTPUT_PROB = 0.1f;
	final static float DEFAULT_MUTATE_DEL_STATE_PROB = 0.1f;
	final static float DEFAULT_MUTATE_DEL_TR_PROB = 0.1f;
	final static float DEFAULT_MUTATE_THRESHOLD_PROB = 0.1f;
	final static float DEFAULT_MUTATE_MOD_INIT_STATE_PROB = 0.1f;

	@SuppressWarnings("serial")
	final static ArrayList<Integer> DEFAULT_THRESHOLD_UNITS = new ArrayList<Integer>() {
		{
			add(100);
		}
	};
	final static int DEFAULT_OUTPUT_UNITS = 100;

	final static boolean DEFAULT_STATE_NUM_CHANGE_ALLOWED = true;
	final static GenerationMode DEFAULT_GENERATION_MODE = GenerationMode.RANDOM;
	final static DistanceCalc DEFAULT_DISTANCE_CALC = DistanceCalc.HAMMING_DISTANCE;
	final static int DEFAULT_VALUE_COUNT_ABOVE_TO_THRESHOLD = 4;

	// parameters
	public boolean state_num_change_allowed = DEFAULT_STATE_NUM_CHANGE_ALLOWED;
	public GenerationMode generation_mode = DEFAULT_GENERATION_MODE;
	public DistanceCalc distance_calc_method = DEFAULT_DISTANCE_CALC;

	public ArrayList<Integer> threshold_units = DEFAULT_THRESHOLD_UNITS;
	public int output_units = DEFAULT_OUTPUT_UNITS;

	public short nInput;
	public short nOutput;
	public int nState = DEFAULT_NUMBER_OF_STATES;

	public float mutate_add_state_prob = DEFAULT_MUTATE_ADD_STATE_PROB;
	public float mutate_mod_tr_nextstate_prob = DEFAULT_MUTATE_MOD_TR_NEXTSTATE_PROB;
	public float mutate_mod_tr_output_prob = DEFAULT_MUTATE_MOD_TR_OUTPUT_PROB;
	public float mutate_del_state_prob = DEFAULT_MUTATE_DEL_STATE_PROB;
	public float mutate_mod_init_state_prob = DEFAULT_MUTATE_MOD_INIT_STATE_PROB;
	public float mutate_del_tr_prob = DEFAULT_MUTATE_DEL_TR_PROB;
	public float mutate_threshold_prob = DEFAULT_MUTATE_THRESHOLD_PROB;

	public EvolutionParams(short inputnumber, short outputnumber,
			Hashtable<String, XMLFieldEntry> properties) {

		this.nInput = inputnumber;
		this.nOutput = outputnumber;
		String notLoaded = "";

		try {
			notLoaded = "num_of_states";
			this.nState = FSMXMLFieldParser.getIntProperty(properties,
					"num_of_states");

			notLoaded = "state_num_change_allowed";
			this.state_num_change_allowed = FSMXMLFieldParser
					.getBooleanProperty(properties, "state_num_change_allowed");
			notLoaded = "generation_mode";
			this.generation_mode = FSMXMLFieldParser
					.getGenerationModeFromProperty(properties,
							"generation_mode");
			notLoaded = "distance_calc_method";
			this.distance_calc_method = FSMXMLFieldParser
					.getDistanceCalcMethodFromProperty(properties,
							"distance_calc_method");
			notLoaded = "mutate_add_state_prob";
			this.mutate_add_state_prob = FSMXMLFieldParser.getFloatProperty(
					properties, "mutate_add_state_prob");
			notLoaded = "mutate_mod_tr_nextstate_prob";
			this.mutate_mod_tr_nextstate_prob = FSMXMLFieldParser
					.getFloatProperty(properties,
							"mutate_mod_tr_nextstate_prob");
			notLoaded = "mutate_mod_tr_output_prob";
			this.mutate_mod_tr_output_prob = FSMXMLFieldParser
					.getFloatProperty(properties, "mutate_mod_tr_output_prob");
			notLoaded = "mutate_mod_init_state_prob";
			this.mutate_mod_init_state_prob = FSMXMLFieldParser
					.getFloatProperty(properties, "mutate_mod_init_state_prob");
			notLoaded = "mutate_del_state_prob";
			this.mutate_del_state_prob = FSMXMLFieldParser.getFloatProperty(
					properties, "mutate_del_state_prob");
			notLoaded = "mutate_del_tr_prob";
			this.mutate_del_tr_prob = FSMXMLFieldParser.getFloatProperty(
					properties, "mutate_del_tr_prob");
			notLoaded = "mutate_threshold_prob";
			this.mutate_threshold_prob = FSMXMLFieldParser.getFloatProperty(
					properties, "mutate_threshold_prob");

			notLoaded = "threshold_units";
			this.threshold_units = FSMXMLFieldParser.getIntArrayProperty(
					properties, "threshold_units");
			notLoaded = "output_units";
			this.output_units = FSMXMLFieldParser.getIntProperty(properties,
					"output_units");
		} catch (Exception e) {
			System.err.println("Couldn't load evo properties (" + notLoaded
					+ "), using defaults.");
		}

	}

	public boolean isFixedStateFSM() {
		return !this.state_num_change_allowed;
	}

	public int getNumOfStates() {
		return this.nState;
	}

	/**
	 * Returns generation mode of MealyFSM.
	 * 
	 * @see GenerationMode
	 */
	public GenerationMode getGenerationMode() {
		return this.generation_mode;
	}

	/**
	 * Returns distance calculation method.
	 * 
	 * @see DistanceCalc
	 */
	public DistanceCalc getDistanceCalcMethod() {
		return this.distance_calc_method;
	}
}