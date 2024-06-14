package neat;

import java.io.Serializable;

import org.dom4j.Element;

public interface NEATGene extends Serializable {
	public Number geneAsNumber();
	public String geneAsString();
	public int getInnovationNumber();
	public NEATGene cloneGene();
	public void exportToXMLElement(Element node);

}
