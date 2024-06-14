package frevo.method.randomsearch;

import java.util.ArrayList;

import org.dom4j.Element;

import core.AbstractRepresentation;
import core.ComponentXMLData;

/**
 * The population for {link RandomEvolution} method. The population consists of representations.
 * 
 * @author Sergii Zhevzhyk
 *
 */
public class RanEvoPopulation {

	/**
	 * Members of the current population
	 */
	ArrayList<AbstractRepresentation> members;

	/**
	 * Description of the representation for all members in this population
	 */
	ComponentXMLData representation;
	
	/**
	 * Parameters of the method
	 */
	RanEvoParameters parameters;
	
	/**
	 * Number of inputs
	 */
	int inputnumber;
	
	/**
	 * Number of outputs
	 */
	int outputnumber;
	
	/**
	 * Constructs the population for {link RandomEvolution} method
	 * @param representation description of the member representation for population
	 * @param parameters parameters of the method
	 * @param inputnumber number of inputs for representation
	 * @param outputnumber number of outputs for representation
	 * @throws Exception initialization of the population failed
	 */
	public RanEvoPopulation(ComponentXMLData representation, RanEvoParameters parameters, int inputnumber, int outputnumber) throws Exception {
		this.representation = representation;
		this.parameters = parameters;		
		this.inputnumber = inputnumber;
		this.outputnumber = outputnumber;
		
		createPopulation();		
	}	
	
	/**
	 * Constructs the population for {link RandomEvolution} method with initialized members
	 * @param representation description of the member representation for population
	 * @param parameters parameters of the method
	 * @param inputnumber number of inputs for representation
	 * @param outputnumber number of outputs for representation
	 * @param members externally loaded members for population 
	 * @throws Exception initialization of the population failed
	 */
	public RanEvoPopulation(ComponentXMLData representation, RanEvoParameters parameters, int inputnumber, int outputnumber, ArrayList<AbstractRepresentation> members) throws Exception {
		this.representation = representation;
		this.parameters = parameters;
		this.inputnumber = inputnumber;
		this.outputnumber = outputnumber;
		this.members = members;
		
		for (AbstractRepresentation member : members) {
			member.setGenerator(parameters.getGenerator());
		}
	}

	/**
	 * Returns the members from population
	 * @return the list of representations
	 */
	ArrayList<AbstractRepresentation> getMembers() {
		return this.members;
	}
	
	/**
	 * Evolves population by one step to the next generation
	 * @param generation the number of generation
	 * @throws Exception couldn't evolve population to the next generation
	 */
	public void evolve(int generation) throws Exception {
		if (generation >= parameters.getGenerations()) {
			return;
		}		
		
		// replacing all candidates which are not elite with new representation
		for (int i = parameters.getElite(); i < parameters.getPopulationSize(); i++) {
			parameters.getGenerator().nextLong();
			// create new candidate 
			AbstractRepresentation member = representation.getNewRepresentationInstance(inputnumber, outputnumber, parameters.getGenerator());
			// replace old candidate with created one
			members.set(i, member);			
		}	
	}
	
	/**
	 * Creates initial population
	 * @throws Exception the problem with creation of the members
	 */
	private void createPopulation() throws Exception {
		members = new ArrayList<AbstractRepresentation>();
		for (int i = 0; i<parameters.getPopulationSize(); i++) {
			AbstractRepresentation member = representation.getNewRepresentationInstance(inputnumber, outputnumber, parameters.getGenerator());
			members.add(member);			
		}		
	}
	
	/** Exports the population to the given Element. To save individual representations, use {@link AbstractRepresentation#exportToXmlElement(Element)}.
	 * @param element the element to be exported to
	 * @return the element with information about population
	 */	
	Element exportXml(Element element)
	{
		Element dpop = element.addElement("population");
		
		// add all members of the population
		for(AbstractRepresentation n: members)
			n.exportToXmlElement(dpop);
		
		return dpop;
	}
}
