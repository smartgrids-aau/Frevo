package light;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum FitnessCalculationMethod {
	TIME_POSITION("Using Time and Distance"), 
	TIME_POSITION_TRACK("Using Time, Distance and Count of GridCells Visited"),
	TIME_POSITION_WEIGHTEDTRACK("Using Time, Distance and weighted area");
	
	public String toString(){
		return getName();
	}
		
	private final String name;
	private static final Map<String, FitnessCalculationMethod> map = 
            new HashMap<String, FitnessCalculationMethod>();
	
	static {
		   for (FitnessCalculationMethod type : FitnessCalculationMethod.values()) {
		     map.put(type.name, type);
		   }
		 }
	
	private FitnessCalculationMethod(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}	
	
	public static FitnessCalculationMethod fromString(String name) {
		   if (map.containsKey(name)) {
		     return map.get(name);
		   }
		   throw new NoSuchElementException(name + "not found");
		 }
}

