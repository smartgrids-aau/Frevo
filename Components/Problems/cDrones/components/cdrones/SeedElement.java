package components.cdrones;

public class SeedElement implements Comparable<SeedElement>{
	public long seed;
	public int succeeded;
	
	public SeedElement (long s,int suc) {
		seed = s;
		succeeded = suc;
	}

	public int compareTo(SeedElement other) {
		if (this.succeeded < other.succeeded) return -1;
		else if (this.succeeded > other.succeeded) return 1;
		else return 0;
	}
}
