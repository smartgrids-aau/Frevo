package neat;

import org.dom4j.Element;
import org.dom4j.Node;


public class NEATLinkGene implements NEATGene {

	private static final long serialVersionUID = 2775400577363455114L;
	private int innovationNumber;
	private boolean enabled;
	private int fromId;
	private int toId;
	private double weight;
	private boolean selfRecurrent = false;
	private boolean recurrent = false;
	
	/**
	 * @return Returns the recurrent.
	 */
	public boolean isRecurrent() {
		return recurrent;
	}
	/**
	 * @param recurrent The recurrent to set.
	 */
	public void setRecurrent(boolean recurrent) {
		this.recurrent = recurrent;
	}
	/**
	 * @return Returns the selfRecurrent.
	 */
	public boolean isSelfRecurrent() {
		return selfRecurrent;
	}
	/**
	 * @param selfRecurrent The selfRecurrent to set.
	 */
	public void setSelfRecurrent(boolean selfRecurrent) {
		this.selfRecurrent = selfRecurrent;
	}
	/**
	 * @param enabled The enabled to set.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (this.enabled == false) {			
			//int i = 0; wtf?
		}
	}
	/**
	 * @param fromId The fromId to set.
	 */
	public void setFromId(int fromId) {
		this.fromId = fromId;
	}
	/**
	 * @param toId The toId to set.
	 */
	public void setToId(int toId) {
		this.toId = toId;
	}
	/**
	 * @param weight The weight to set.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	/**
	 * @return Returns the enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @return Returns the fromId.
	 */
	public int getFromId() {
		return fromId;
	}
	/**
	 * @return Returns the innovationNumber.
	 */
	public int getInnovationNumber() {
		return innovationNumber;
	}
	/**
	 * @return Returns the toId.
	 */
	public int getToId() {
		return toId;
	}
	/**
	 * @return Returns the weight.
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * Creates the gene based on the params
	 * @param innovationNumber
	 * @param enabled
	 * @param fromId
	 * @param toId
	 * @param weight
	 */
	public NEATLinkGene(int innovationNumber, boolean enabled, int fromId, int toId, double weight) {
		this.innovationNumber = innovationNumber;
		this.setEnabled(enabled);
		this.fromId = fromId;
		this.toId = toId;
		this.weight = weight;
	}
	
	public NEATLinkGene(Node n) {
		this.innovationNumber = Integer.parseInt(n.valueOf("./@innovationnumber"));
		this.enabled = Boolean.parseBoolean(n.valueOf("./@enabled"));
		this.fromId = Integer.parseInt(n.valueOf("./@fromID"));
		this.toId = Integer.parseInt(n.valueOf("./@toID"));
		this.weight = Double.parseDouble(n.valueOf("./@weight"));
		this.recurrent = Boolean.parseBoolean(n.valueOf("./@recurrent"));
		this.selfRecurrent = Boolean.parseBoolean(n.valueOf("./@selfrecurrent"));
	}
	/**
	 * Not used within NEAT.  
	 */
	public Number geneAsNumber() {
		return (Integer.valueOf(this.innovationNumber));
	}

	public String geneAsString() {
		return (this.innovationNumber + ":" + 
				this.enabled + ":" + 
				this.fromId + ":" +
				this.toId + ":" + 
				this.weight);
	}
	@Override
	public NEATLinkGene cloneGene() {
		// returns a clone
		NEATLinkGene clone = new NEATLinkGene(this.innovationNumber,this.enabled,this.fromId,this.toId,this.weight);
		clone.recurrent = this.recurrent;
		clone.selfRecurrent = this.selfRecurrent;
		
		return clone;
	}
	@Override
	public void exportToXMLElement(Element node) {
		node.setName("NEATlinkGene");
		
		node.addAttribute("innovationnumber", String.valueOf(getInnovationNumber()));
		node.addAttribute("enabled", String.valueOf(isEnabled()));
		node.addAttribute("fromID", String.valueOf(getFromId()));
		node.addAttribute("toID", String.valueOf(getToId()));
		node.addAttribute("weight", String.valueOf(getWeight()));
		node.addAttribute("recurrent", String.valueOf(isRecurrent()));
		node.addAttribute("selfrecurrent", String.valueOf(isSelfRecurrent()));
	}
	
	@Override
	public String toString() {
		return
		// first we define connection of neurons: source and destination
		"{[" + fromId + "," + toId +
		// innovation id of the link
		"], IN:" + innovationNumber +
		// weight of the link
		", W:" + weight + "}";
	}
}
