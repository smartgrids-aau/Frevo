package neat;

import org.dom4j.Element;
import org.dom4j.Node;


public class NEATNodeGene implements NEATGene {

	private static final long serialVersionUID = 5031497806420468763L;
	private int innovationNumber;
	private int id;
	private double sigmoidFactor = -1.0;
	private int type;
	private double depth;
	private double bias;
	public static final int HIDDEN = 0;
	public static final int OUTPUT = 1;
	public static final int INPUT = 2;
	
	public NEATNodeGene(int innovationNumber, int id, double sigmoidF, int type, double bias) {
		this.innovationNumber = innovationNumber;
		this.id = id;
		this.sigmoidFactor = sigmoidF;
		this.type = type;
		this.bias = bias;
		this.initialiseDepth();
	}
	
	public NEATNodeGene(Node n) {
		this.innovationNumber = Integer.parseInt(n.valueOf("./@innovationnumber"));
		this.id = Integer.parseInt(n.valueOf("./@id"));
		this.sigmoidFactor = Double.parseDouble(n.valueOf("./@sigmoidfactor"));
		this.type = Integer.parseInt(n.valueOf("./@type"));
		this.bias = Double.parseDouble(n.valueOf("./@bias"));
		
		this.initialiseDepth();
	}

	private void initialiseDepth() {
		if (this.type == INPUT) {
			this.depth = 0;
		} else if (this.type == OUTPUT) {
			this.depth = 1;
		}
	}
	
	/**
	 * @return Returns the depth.
	 */
	public double getDepth() {
		return depth;
	}
	/**
	 * @param depth The depth to set.
	 */
	public void setDepth(double depth) {
		this.depth = depth;
	}

	public void setSigmoidFactor(double bias) {
		this.sigmoidFactor = bias;
	}
	
	public int getType() {
		return type;
	}

	public int getInnovationNumber() {
		return (this.innovationNumber);
	}
	
	public int id() {
		return (this.id);
	}

	public double sigmoidFactor() {
		return (this.sigmoidFactor);
	}
	
	public Number geneAsNumber() {
		return (Integer.valueOf(this.innovationNumber));
	}

	public String geneAsString() {
		return (this.innovationNumber + ":" + this.id + ":" + this.sigmoidFactor);
	}

	public double bias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

	@Override
	public NEATGene cloneGene() {
		NEATNodeGene clone = new NEATNodeGene(this.innovationNumber, this.id, this.sigmoidFactor, this.type, this.bias);
		return clone;
	}

	@Override
	public void exportToXMLElement(Element node) {
		node.setName("NEATNodeGene");
		
		node.addAttribute("id", String.valueOf(id));
		node.addAttribute("innovationnumber", String.valueOf(getInnovationNumber()));
		node.addAttribute("sigmoidfactor", String.valueOf(sigmoidFactor()));
		node.addAttribute("type", String.valueOf(getType()));
		node.addAttribute("bias", String.valueOf(bias()));
		
	}

	@Override
	public String toString() {
		return "{ID:" + id + "; IN:" + innovationNumber + "}";
	}	
}
