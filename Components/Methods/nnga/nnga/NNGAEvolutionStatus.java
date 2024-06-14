package nnga;

import org.dom4j.Element;
import org.dom4j.Node;

import core.AbstractEvolutionStatus;
import core.AbstractRepresentation;

public class NNGAEvolutionStatus extends AbstractEvolutionStatus {

	public enum evolutionFunction{Elite, Mutation, Xover, Renew, Random};
	
	public evolutionFunction createdBy;
	
	public NNGAEvolutionStatus(){
		createdBy = evolutionFunction.Renew;
	}
	
	public NNGAEvolutionStatus(evolutionFunction evFunction){
		createdBy = evFunction;
	}
	
	@Override
	public void exportToXmlElement(Element element) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractRepresentation loadFromXML(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractEvolutionStatus clone() {
		return new NNGAEvolutionStatus(createdBy);
	}
}
