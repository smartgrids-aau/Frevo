package fsm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import core.XMLFieldEntry;

/***
 * This class handles general parsing of XMLField-s,
 * but also parsing of specific fields in the XML that are representation specific (enums).
 *  
 * @author Agnes Pinter-Bartha
 *
 */
public class FSMXMLFieldParser {
	// STATIC functions that extract different type of values from properties
	// loaded into memory.

	/**
	 * Returns a boolean property from properties.
	 * 
	 * @param properties
	 *            properties loaded into memory as a hashtable
	 * @param property
	 *            name of the property
	 */
	public static boolean getBooleanProperty(
			Hashtable<String, XMLFieldEntry> properties, String property)
			throws Exception {
		XMLFieldEntry entry = properties.get(property);
		String value = entry.getValue();

		if (value.toLowerCase().equals("true"))
			return true;
		else if (value.toLowerCase().equals("false"))
			return false;
		else
			throw new Exception("Value should be true or false!");
	}

	/**
	 * Returns the generation mode of MealyFSM from properties.
	 * @param properties
	 *            properties loaded into memory as a hashtable
	 * @param property
	 *            name of the property
	 */
	public static GenerationMode getGenerationModeFromProperty(
			Hashtable<String, XMLFieldEntry> properties, String property)
			throws Exception {
		XMLFieldEntry entry = properties.get(property);
		String value = entry.getValue();

		return GenerationMode.valueOf(value.toUpperCase());
	}

	/**
	 * Returns the distance calculation method of MealyFSM from properties.
	 * @param properties
	 *            properties loaded into memory as a hashtable
	 * @param property
	 *            name of the property
	 */
	public static DistanceCalc getDistanceCalcMethodFromProperty(
			Hashtable<String, XMLFieldEntry> properties, String property)
			throws Exception {
		XMLFieldEntry entry = properties.get(property);
		String value = entry.getValue();

		return DistanceCalc.valueOf(value.toUpperCase());
	}

	/**
	 * Returns a Float property from properties.
	 * @param properties
	 *            properties loaded into memory as a hashtable
	 * @param property
	 *            name of the property
	 */
	public static Float getFloatProperty(
			Hashtable<String, XMLFieldEntry> properties, String property) {
		XMLFieldEntry entry = properties.get(property);
		return Float.valueOf(entry.getValue());
	}

	/**
	 * Returns an Integer property from properties.
	 * @param properties
	 *            properties loaded into memory as a hashtable
	 * @param property
	 *            name of the property
	 */
	public static Integer getIntProperty(
			Hashtable<String, XMLFieldEntry> properties, String property) {
		XMLFieldEntry ns = properties.get(property);
		return Integer.valueOf(ns.getValue());
	}
	
	/**
	 * Transform string parameter into an array of integers
	 * 
	 * @param property
	 */
	public static ArrayList<Integer> getIntArrayProperty(
			Hashtable<String, XMLFieldEntry> properties, String property) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		XMLFieldEntry entry = properties.get(property);
		if (isNotEmpty(entry.getValue())) {
			StringTokenizer tokenizer = new StringTokenizer(entry.getValue(),
					",");
			while (tokenizer.hasMoreTokens()) {
				list.add(Integer.valueOf(tokenizer.nextToken()));
			}
		}
		return list;
	}
	
	private static boolean isNotEmpty(String source) {
		return (source != null && !("").equals(source));
	}

}

