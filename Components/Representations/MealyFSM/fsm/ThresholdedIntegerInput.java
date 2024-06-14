package fsm;

import utils.NESRandom;

/***
 * Class for integer inputs that are thresholded. Thresholding is used to
 * transform the input into smaller intervals which are treated in the same way.
 * E.g. by using one threshold, we can transform the input into a binary input,
 * making difference between a value is either larger or equal to the threshold,
 * or smaller. Class handles mutation, initiation and export of thresholds.
 * 
 * @author Agnes Pinter-Bartha
 * 
 */
public class ThresholdedIntegerInput extends IntegerInput {
	public enum MutationType {
		SmallIncrement, SmallDecrement, Random
	}

	int[] thresholds;
	int nThreshold;
	NESRandom generator;

	/**
	 * Constructor for ThresholdedIntegerInput class.
	 * 
	 * @param min
	 *            minimum value for the input values
	 * @param max
	 *            maximum value for the input values
	 * @param nUnit
	 *            number of units dividing the interval [min..max]
	 * @param nThreshold
	 *            number of thresholds
	 * @param generator
	 *            random number generator {@link NESRandom}
	 */
	public ThresholdedIntegerInput(int min, int max, int nUnit, int nThreshold,
			NESRandom generator) {
		super(min, max, nUnit);
		this.generator = generator;
		this.nThreshold = nThreshold;

		initThresholds();

	}

	/***
	 * Constructor for ThresholdedIntegerInput class.
	 * 
	 * @param min
	 *            minimum value for the input values
	 * @param max
	 *            maximum value for the input values
	 * @param nUnit
	 *            number of units dividing the interval [min..max]
	 * @param nThreshold
	 *            number of thresholds
	 * @param thresholds
	 *            array of thresholds
	 */
	public ThresholdedIntegerInput(int min, int max, int nUnit, int nThreshold,
			int[] thresholds) {
		super(min, max, nUnit);
		this.generator = null;
		this.nThreshold = nThreshold;

		this.thresholds = new int[thresholds.length];
		System.arraycopy(thresholds, 0, this.thresholds, 0, thresholds.length);
	}

	/**
	 * Method for initiating threshold values.
	 */
	private void initThresholds() {
		if (generator == null) {
			// when fsm is loaded from xml..
			this.thresholds = new int[this.nThreshold];
			for (int i = 0; i < this.nThreshold; i++) {
				this.thresholds[i] = 0;
			}
		} else {
			// generate a random number between min and max
			this.thresholds = new int[this.nThreshold];
			for (int i = 0; i < this.nThreshold; i++) {
				this.thresholds[i] = min
						+ (((max - min) / nUnit) * generator.nextInt(nUnit));
			}
		}

		sortThresholds();
	}

	/***
	 * Method for cloning current instance.
	 */
	public ThresholdedIntegerInput clone() {
		ThresholdedIntegerInput clone = new ThresholdedIntegerInput(this.min,
				this.max, this.nUnit, this.nThreshold, this.thresholds);
		clone.generator = this.generator;
		return clone;
	}

	@Override
	public int getNumberOfInputValues() {
		// with n thresholds, we will have n+1 values
		return nThreshold + 1;
	}

	@Override
	public int getPosition(int value) {
		int pos = 0;

		while ((pos < nThreshold) && (value >= thresholds[pos])) {
			pos++;
		}

		return pos;
	}

	/**
	 * Retrieve array of thresholds.
	 * 
	 * @return array of thresholds
	 */
	public int[] getThresholds() {
		return this.thresholds;
	}

	/**
	 * Return number of thresholds.
	 * 
	 * @return number of thresholds
	 */
	public int getNThreshold() {
		return nThreshold;
	}

	/**
	 * Set the threshold at the given position.
	 * 
	 * @param pos
	 *            position of threshold in the array of thresholds
	 * @param value
	 *            new value of threshold
	 */
	public void setThreshold(int pos, int value) {
		this.thresholds[pos] = value;
	}

	/**
	 * Method for mutating thresholds.
	 * 
	 * @param prob
	 *            probability of mutation
	 * @param type
	 *            type of mutation, see {@link MutationType}
	 */
	public void mutateThresholds(float prob, MutationType type) {
		for (int i = 0; i < nThreshold; i++) {
			if (generator.nextFloat() < prob) {
				mutateThresholds(i, type);
				while (!allThresholdDifferent(i))
					mutateThresholds(i, type);
			}
		}

		// TODO: would it be better to use OrderedSet?
		sortThresholds();
	}

	/**
	 * Check if all thresholds are different. In case of mutation for e.g., some
	 * thresholds might become equal. This method is used to check this.
	 * 
	 * @param i
	 *            index of threshold in the array of thresholds
	 * @return boolean value specifying if all thresholds are different or not
	 */
	private boolean allThresholdDifferent(int i) {
		boolean different = true;
		for (int j = 0; j < thresholds.length; j++) {
			if (thresholds[i] == thresholds[j] && i != j) {
				different = false;
				break;
			}
		}
		return different;
	}

	/***
	 * Method to sort thresholds. We would like the threshold array to contain
	 * strictly increasing series of thresholds.
	 */
	private void sortThresholds() {
		int tmp;
		for (int i = 0; i < thresholds.length; i++) {
			for (int j = i; j < thresholds.length; j++) {
				if (thresholds[i] > thresholds[j]) {
					tmp = thresholds[i];
					thresholds[i] = thresholds[j];
					thresholds[j] = tmp;
				}
			}
		}

	}

	/**
	 * Method for mutating thresholds.
	 * 
	 * @param pos
	 *            position of threshold in the array of thresholds
	 * @param type
	 *            type of mutation, see {@link MutationType}
	 */
	public void mutateThresholds(int pos, MutationType type) {
		switch (type) {
		case SmallDecrement:
			decValue(pos);
			break;
		case SmallIncrement:
			incValue(pos);
			break;
		case Random:
			setRandomValue(pos, generator);
			break;
		}
	}

	/**
	 * Increment a specific threshold value by one unit.
	 * 
	 * @param pos
	 *            position of threshold in the array of thresholds
	 */
	private void incValue(int pos) {
		thresholds[pos] = Math.min(++thresholds[pos], super.nUnit);
	}

	/**
	 * Decrement threshold value by one unit.
	 * 
	 * @param pos
	 *            position of threshold in the array of thresholds
	 */
	private void decValue(int pos) {
		thresholds[pos] = Math.max(--thresholds[pos], 0);
	}

	/**
	 * Sets a random value between 0 and number of units (excluding limits) for
	 * the threshold. Generated value will be different from current value.
	 * 
	 * @param pos
	 *            position of threshold in the array of thresholds
	 * @param generator
	 *            random number generator, see {@link NESRandom}
	 */
	private void setRandomValue(int pos, NESRandom generator) {
		int tmp = thresholds[pos];
		while (thresholds[pos] == tmp || thresholds[pos] == 0) {
			thresholds[pos] = generator.nextInt(super.nUnit);
		}
	}

	/**
	 * Converts thresholds into a formatted String.
	 * 
	 * @param sep
	 *            separator used for formatting the String
	 * @return formatted String
	 */
	public String exportThresholds(String sep) {
		StringBuilder export = new StringBuilder();

		for (int i = 0; i < this.nThreshold - 1; i++) {
			export.append(padded(thresholds[i], max)).append(sep);
		}
		// add last value too
		if (nThreshold > 0)
			export.append(padded(this.thresholds[this.nThreshold - 1], max));

		return export.toString();
	}

	/**
	 * Return label specifying which interval of values should be considered.
	 * 
	 * @param pos
	 *            position of threshold in the array of thresholds
	 * @return formatted string specifying an interval
	 * 
	 */
	public String getLabel(int pos) {
		String label = "";
		if (pos == 0)
			label = "val <= " + padded(thresholds[pos], max);
		else if (pos == thresholds.length)
			label = padded(thresholds[pos - 1], max) + " < val";
		else if (pos < thresholds.length)
			label = padded(thresholds[pos - 1], max) + " < val <= "
					+ padded(thresholds[pos], max);
		return label;
	}

	/**
	 * Retrieve number of thresholds.
	 * 
	 * @return number of thresholds
	 */
	public int getNumberOfThreshold() {
		return nThreshold;
	}

	/**
	 * Return sum of all thresholds for this input.
	 * 
	 * @return sum of thresholds
	 */
	public int getSumOfThresholds() {
		int sum = 0;
		for (int i = 0; i < this.nThreshold; i++) {
			sum += this.thresholds[i];
		}
		return sum;
	}

	/**
	 * Return threshold at the given position.
	 * 
	 * @param pos
	 *            position of threshold in the array of thresholds
	 * @return threshold
	 */
	public int getThreshold(int pos) {
		return thresholds[pos];
	}

	/**
	 * Convert an integer value into a String by padding with 0. The length of
	 * the String depends on the Max value permitted for the integer.
	 * 
	 * @param value
	 *            integer value
	 * @param maxValue
	 *            maximum of the values permitted
	 * 
	 * @return formatted integer value as String
	 */
	private static String padded(int value, int maxValue) {
		StringBuilder padded = new StringBuilder();
		for (int i = 0; i < Integer.toString(maxValue).length()
				- Integer.toString(value).length(); i++) {
			padded.append("0");
		}
		padded.append(Integer.toString(value));
		return padded.toString();
	}

}
