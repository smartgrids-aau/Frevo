package neat;


public class NEATFeatureInnovation implements NEATInnovation {
	
	private int innvovationId;
	
	public void setInnovationId(int id) {
		this.innvovationId = id;
	}

	public int innovationId() {
		return (this.innvovationId);
	}

	public int type() {
		return 0;
	}
}
