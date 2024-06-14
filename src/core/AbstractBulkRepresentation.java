package core;

import java.util.Hashtable;

import utils.NESRandom;

public abstract class AbstractBulkRepresentation extends AbstractRepresentation {
	
	/** The core representation model */
	protected ComponentXMLData coreRepresentation;

	public AbstractBulkRepresentation(int numberOfInputs, int numberOfOutputs, NESRandom random, Hashtable<String, XMLFieldEntry> properties) {
		super(numberOfInputs, numberOfOutputs, random, properties);
	}
	
	/** Returns true if this is a bulk representation, false otherwise. */
	public boolean isBulkRepresentation() {
		return true;
	}
	
	/** Returns the representation at the given index */
	public abstract AbstractRepresentation getRepresentation(int index) throws IndexOutOfBoundsException;
	
	/** Returns the number of representations in this bulk representation. */
	public abstract int getGenotypeSize();
}
