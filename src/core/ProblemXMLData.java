package core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import utils.SafeSAX;


public class ProblemXMLData extends ComponentXMLData {

	/** A map containing the requirement entries of this component. */
	private Hashtable<String, XMLFieldEntry> requirements;

	private AbstractProblem problemPrototype;

	/**
	 * Constructor of this problem class. Basically, it loads up all the data
	 * from the given XML file and puts them in the appropriate variables.
	 * 
	 * @param ctype
	 *            The type of this component.
	 * @param xmlfile
	 *            The XML file used to load up data into this class.
	 * @throws InstantiationException
	 *             if the component type is not <i>problem</i> or <i>multi
	 *             problem</i>
	 */
	@SuppressWarnings("deprecation")
	public ProblemXMLData(ComponentType ctype, File xmlfile)
			throws InstantiationException {
		super(ctype, xmlfile);

		// Load requirements
		if ((ctype == ComponentType.FREVO_PROBLEM)
				|| (ctype == ComponentType.FREVO_MULTIPROBLEM)) {

			// load the XML file
			Document doc = SafeSAX.read(xmlfile,true);

			// get requirements node
			Node reqnode = doc.selectSingleNode("/icomponent/requirements");
			if (reqnode == null) {
				// throw exception if requirements node is missing
				throw new InstantiationException(
						"Cannot instantiate problem based on this XML description!");
			}
			
			// collect all nodes
			List<?> npops = reqnode.selectNodes(".//reqentry");
			Iterator<?> it = npops.iterator();

			// create requirement map
			Hashtable<String, XMLFieldEntry> reqtable = new Hashtable<String, XMLFieldEntry>();

			// collect informations
			while (it.hasNext()) {
				Element el = (Element) it.next();
				String key = el.valueOf("./@key");
				XMLFieldType type = XMLFieldType.valueOf(el.valueOf("./@type"));
				String value = el.valueOf("./@value");

				if (!FrevoMain.checkType(type, value))
					throw new IllegalArgumentException("Value \"" + value
							+ "\" for Key \"" + key + "\" is not of type "
							+ type + "!");
				reqtable.put(key, new XMLFieldEntry(value, type, null));

				if ((key.equals("minimumCandidates"))
						|| (key.equals("maximumCandidates"))) {
					int m = Integer.parseInt(value);
					// adjust problem type accordingly
					if (m != 1)
						this.componentType = ComponentType.FREVO_MULTIPROBLEM;
				}

			}
			// Add requirement map to the object
			setRequirements(reqtable);

		} else {
			throw new InstantiationException(
					"Cannot instantiate problem based on this XML description!");
		}

		try {
			problemPrototype = (AbstractProblem) componentClass.newInstance();
		} catch (IllegalAccessException e) {
			throw new InstantiationException(
					"Cannot instantiate problem based on this XML description!");
		}

	}

	public int getRequiredNumberOfInputs() {
		adjustRequirements();

		// return inputs
		XMLFieldEntry input = requirements.get("inputnumber");
		return Integer.parseInt(input.getValue());
	}

	public int getRequiredNumberOfOutputs() {
		adjustRequirements();

		// return outputs
		XMLFieldEntry output = requirements.get("outputnumber");
		return Integer.parseInt(output.getValue());
	}
	
	public int getMinimumNumberOfPlayers() {
		adjustRequirements();

		// return inputs
		XMLFieldEntry input = requirements.get("minimumCandidates");
		return Integer.parseInt(input.getValue());
	}

	public int getMaximumNumberOfPlayers() {
		adjustRequirements();

		// return inputs
		XMLFieldEntry input = requirements.get("maximumCandidates");
		return Integer.parseInt(input.getValue());
	}

	public void adjustRequirements() {
		problemPrototype.setProperties(properties);
		this.requirements = problemPrototype.adjustRequirements(requirements,
				properties);
	}

	/**
	 * Returns a map containing the loaded requirement pairs defined in the XML
	 * file. Since only single problem and multi problem components have
	 * requirements it will throw and IllegalAccessException
	 * 
	 * @return A map of requirements or null if the type of this component is
	 *         not single problem or multi problem.
	 */
	@SuppressWarnings("deprecation")
	public Hashtable<String, XMLFieldEntry> getRequirements() {
		try {
			AbstractProblem c = (AbstractProblem) componentClass.newInstance();
			c.setProperties(properties);
			this.requirements = c.adjustRequirements(requirements, properties);
			return requirements;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a new <i>problem</i> instance created from this source data.
	 * 
	 * @return A new <i>problem</i> instance
	 * @throws InstantiationException
	 *             if an error occurs while instantiating the encapsulated
	 *             problem object
	 */
	public AbstractProblem getNewProblemInstance()
			throws InstantiationException {

		// get class's constructor
		Constructor<?>[] c = getComponentClass().getConstructors();
		AbstractProblem result;
		try {
			// instantiate problem
			result = (AbstractProblem) c[0].newInstance();
			// add properties and set source XML to this object
			result.setProperties(properties);
			result.setXMLData(this);
			return result;
		} catch (Exception e) {
			throw new InstantiationException();
		}
	}

	/**
	 * Sets the map of requirements to the given value.
	 * 
	 * @param reqtable
	 *            The new map of requirements to be used.
	 */
	private void setRequirements(Hashtable<String, XMLFieldEntry> reqtable) {
		requirements = reqtable;
	}

}
