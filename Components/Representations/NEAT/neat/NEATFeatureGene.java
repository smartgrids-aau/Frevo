package neat;

import org.dom4j.Element;
import org.dom4j.Node;


public class NEATFeatureGene implements NEATGene {
	
	private static final long serialVersionUID = 3982459545230832111L;
	private Double featureValue;
	private int innovationNumber;
	
	public NEATFeatureGene(int innovationNumber, double value) {
		this.featureValue = Double.valueOf(value);
		this.innovationNumber = innovationNumber;
	}
	
	public NEATFeatureGene(Node n) {
		this.innovationNumber = Integer.parseInt(n.valueOf("./@innovationnumber"));
		this.featureValue = Double.valueOf(n.valueOf("./@featureValue"));
	}

	public int getInnovationNumber() {
		return (this.innovationNumber);
	}

	public Number geneAsNumber() {
		return (featureValue);
	}

	public String geneAsString() {
		return (this.featureValue.toString());
	}

	@Override
	public NEATFeatureGene cloneGene() {
		NEATFeatureGene clone = new NEATFeatureGene(this.innovationNumber, this.featureValue);
		return clone;
	}

	@Override
	public void exportToXMLElement(Element node) {
		node.setName("NEATFeatureGene");
		
		node.addAttribute("innovationnumber", String.valueOf(getInnovationNumber()));
		node.addAttribute("featureValue", String.valueOf(featureValue));
		
	}
}
