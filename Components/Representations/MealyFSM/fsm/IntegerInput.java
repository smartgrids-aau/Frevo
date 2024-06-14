package fsm;

/**
 * Represents an input with min and max values.
 * The min..max interval is divided into nUnit parts.
 * 
 * @author Agnes Pinter-Bartha
 *
 */
public class IntegerInput{
	int min;
	int max;
	int nUnit;		
	
	/**
	 * Constructor for IntegerInput class.
	 * 
	 * @param min minimum value for the input values
	 * @param max maximum value for the input values
	 * @param nUnit number of units dividing the interval [min..max]
	 */
	public IntegerInput(int min, int max, int nUnit){
		this.min = min;
		this.max = max;
		this.nUnit = nUnit;		
	}
	
	/**
	 * Gives back a clone of the current IntegerInput instance.
	 */
	public IntegerInput clone(){
		IntegerInput clone = new IntegerInput(this.min, this.max, this.nUnit);
		return clone;
	}
	
	/**
	 * Return how many different values are in [min..max] interval.
	 * @return number of possible values
	 */
	public int getNumberOfInputValues(){
		return nUnit + 1;
	}
	
	/**
	 * Return minimum value for this input.
	 * @return minimum value
	 */
	public int getMin(){
		return this.min;
	}
	
	/**
	 * Return maximum value for this input.
	 * @return maximum value
	 */
	public int getMax(){
		return this.max;
	}
	
	/**
	 * Return number of units for this input.
	 * @return number of units in which [min..max] interval is diveded
	 */
	public int getUnits(){
		return this.nUnit;
	}
	
	/**
	 * Return unit's length for this input.
	 * @return length of unit (double value)
	 */
	public double getUnitLenght(){
		return (max - min) * 1.0f / nUnit;
	}
	
	/**
	 * Returns position of value in sorted input values.
	 * @param value Input value
	 * @return number of units
	 */
	public int getPosition(int value){
		if (max == min)
			return 0;
		return (int)((value - min) * nUnit * 1.0f / (max - min));
	}
}