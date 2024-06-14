package core;

import org.dom4j.Element;

/**
 * Represents a step of the method evaluation. It is used for saving of the current state.
 * 
 * @author Sergii Zhevzhyk
 */
public class XMLMethodStep {

	private Element data;
	private String name;
	
	private long startSeed;
	private long currentSeed;
	
	/**
	 * Constructs a new <code>XMLMethodStep</code> which keeps information about a step of Method evaluation.
	 * 
	 * @param name name of a evaluation step
	 * @param data information about evaluation step
	 */
	public XMLMethodStep(String name, Element data, long startSeed, long currentSeed) {
		super();
		this.data = data.createCopy();
		this.name = name;
		this.startSeed = startSeed;
		this.currentSeed = currentSeed;
	}
	
	/**
	 * Gets information about last evaluation in XML format.
	 * 
	 * @return information about last evaluation in XML format
	 */
	public Element getData() {
		return data.createCopy();
	}
	
	/**
	 * Gets the name of the file. 
	 * 
	 * @return name of the file which best approaches to the data.
	 */
	public String getName() {
		return name;
	}	
	
	/**
	 * Gets the start seed
	 * 
	 * @return start seed
	 */
	public long getStartSeed() {
		return startSeed;
	}

	/**
	 * Gets the current seed
	 * 
	 * @return the current seed
	 */
	public long getCurrentSeed() {
		return currentSeed;
	}
}
