package bulkrepresentation.simplebulkrepresentation;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;

import main.FrevoMain;

import org.dom4j.Element;
import org.dom4j.Node;

import utils.NESRandom;
import core.AbstractBulkRepresentation;
import core.AbstractRepresentation;
import core.ComponentType;
import core.ComponentXMLData;
import core.XMLFieldEntry;

public class SimpleBulkRepresentation extends AbstractBulkRepresentation {
	
	/** List of the representations s*/
	ArrayList<AbstractRepresentation> representations;
	/** Fix size of the genotype*/
	final int genotypesize;
	final boolean is_genotype_positions_fixed;
	
	final NESRandom generator;
	final int number_of_inputs;
	final int number_of_outputs;

	public SimpleBulkRepresentation(int numberOfInputs, int numberOfOutputs,
			NESRandom random, Hashtable<String, XMLFieldEntry> properties) {
		super(numberOfInputs, numberOfOutputs, random, properties);
		
		representations = new ArrayList<AbstractRepresentation>();
		setProperties(properties);
		
		number_of_inputs = numberOfInputs;
		number_of_outputs = numberOfOutputs;
		generator = random;
		// load properties
		genotypesize = Integer.parseInt(properties.get("genotype_size").getValue());
		is_genotype_positions_fixed = Boolean.parseBoolean(properties.get("is_gentype_positions_fixed").getValue());
		
		ComponentXMLData coreRepresentation = FrevoMain.getComponent(ComponentType.FREVO_REPRESENTATION, properties.get("core_representation_component").getValue()); 
		// generate representations
		for (int i=0;i<genotypesize;i++) {
			try {
				representations.add(coreRepresentation.getNewRepresentationInstance(numberOfInputs, numberOfOutputs, random));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void exportToFile(File saveFile) {
		// TODO exportToFile
		
	}

	@Override
	protected void mutationFunction(float severity, float probability,
			int method) {
		// mutate each candidate
		for (AbstractRepresentation rep:representations) {
			rep.mutate(severity, probability, method);
		}
	}

	@Override
	protected void recombinationFunction(AbstractRepresentation other,
			int method) {
		// recombine with other bulk representation
		AbstractBulkRepresentation otherRep = (AbstractBulkRepresentation) other;
		
		// method here should mean the number of crossover points
		/*if (method != 1) {
			System.err.println ("Method > 1 is not yet implemented!");
		}*/
		
		// get random xover point
		int xoverpoint = generator.nextInt(genotypesize-1);
		// replace candidates from the other bulk representation
		for (int i = xoverpoint+1;i<genotypesize;i++) {
			representations.set(i, otherRep.getRepresentation(i).clone());
		}
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
	public ArrayList<Float> getOutput(ArrayList<Float> input) {
		System.err.println ("Bulk representations cannot process output directly!");
		return null;
	}

	@Override
	public void reset() {
		for (AbstractRepresentation rep:representations)
			rep.reset();
	}

	@Override
	public double diffTo(AbstractRepresentation representation) {
		AbstractBulkRepresentation other = (AbstractBulkRepresentation)representation;
		// returns the average distance between pairs
		double average = 0;
		for (int i=0;i<genotypesize;i++) {
			average += representations.get(i).diffTo(other.getRepresentation(i));
		}
		average /= genotypesize;
		return average;
	}

	@Override
	protected AbstractRepresentation cloneFunction() {
		SimpleBulkRepresentation result = new SimpleBulkRepresentation(number_of_inputs, number_of_outputs, generator, getProperties());
		
		// clone representations
		result.representations.clear();
		for (AbstractRepresentation rep:representations)
			result.representations.add(rep.clone());
		
		return result;
	}

	@Override
	public void exportToXmlElement(Element element) {
		Element root = element.addElement("SimpleBulkRepresentation");
		// save individually
		for (AbstractRepresentation rep:representations) {
			rep.exportToXmlElement(root);
		}
	}

	@Override
	public AbstractRepresentation loadFromXML(Node node) {
		// TODO loadFromXML
		return null;
	}

	@Override
	public Hashtable<String, String> getDetails() {
		// TODO getDetails
		return null;
	}

	@Override
	public String getHash() {
		// compose hash
		StringBuilder sb = new StringBuilder();
		
		for (AbstractRepresentation rep:representations) {
			sb.append(rep.getHash().substring(0, 3));
		}
		
		return sb.toString();
	}

	@Override
	public AbstractRepresentation getRepresentation(int index)
			throws IndexOutOfBoundsException {
		return representations.get(index);
	}

	@Override
	public int getGenotypeSize() {
		return this.genotypesize;
	}

	@Override
	public String getC() {
		System.err.println ("Bulk representations cannot process output directly!");
		return null;
	}

}
