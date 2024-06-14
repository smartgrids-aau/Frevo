package neat;


public class NEATNodeInnovation implements NEATInnovation {
	private int innovationId;
	private int nodeId;
	/** Innovation ID where this nodeinnovation has been placed upon */
	private int linkInnovationId;
	

	public NEATNodeInnovation(int innovationId) {
		this.innovationId = innovationId;
		this.linkInnovationId = -1;
	}

	public int innovationId() {
		return (this.innovationId);
	}

	public int type() {
		return NEATNodeGene.HIDDEN;
	}
	
	public void setLinkInnovationId(int id) {
		this.linkInnovationId = id;
	}

	public void setInnovationId(int id) {
		this.innovationId = id;
	}
	
	public void setNodeId(int id) {
		this.nodeId = id;
	}
	
	public int getNodeId() {
		return (this.nodeId);
	}
	
	public int getLinkInnovationId() {
		return (this.linkInnovationId);
	}

	public boolean equals(Object test) {
		boolean equals = false;
		if (test instanceof NEATNodeInnovation) {
			NEATNodeInnovation thisInnovation = (NEATNodeInnovation)test;
			equals = (this.linkInnovationId == thisInnovation.getLinkInnovationId());
		}
		
		return (equals);		
	}

	void setType(int t) {
		//this.type = t;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
