/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import main.FrevoMain;
import net.jodk.lang.FastMath;

/** A class representing a collection of statistic data. */
public class StatKeeper implements Comparable<StatKeeper> {
	private double sum = 0.0;
	private double sum2 = 0.0;
	private int number_of_elements = 0;
	double min = Double.MAX_VALUE;
	double max = -Double.MAX_VALUE;
	int countHit0 = 0;
	private boolean recordValues = false;
	private String statName; // used for labeling column headings
	private String valuesName; // used to display the values
	boolean touched = false;

	ArrayList<Double> values = new ArrayList<Double>();

	public StatKeeper() {
		this(false, "untitled", "untitled"); // call the long constructor
	}

	/**
	 * Creates a new StatKeeper object.
	 * 
	 * @param recordValues
	 *            defines if values should be recorded, otherwise just a
	 *            statistic of min, max, mean, sdev is kept
	 * @param statName
	 *            gives the value a name which is used for labeling the columns
	 *            in the exported csv
	 * @param isDisplayed
	 *            if true, this stat is added to a list of notable stats which
	 *            can be exported to a csv together
	 */
	public StatKeeper(boolean recordValues, String statName, String valuename) {
		this.recordValues = recordValues;
		this.statName = statName;
		this.valuesName = valuename;
	}

	/** Returns the number of elements */
	public int elementNumber() {
		return values.size();
	}

	/** Returns the given element from the series */
	public double getElement(int e) {
		return values.get(e);
	}

	public int length() {
		return number_of_elements;
	}

	public void add(double x) {
		touched = true;
		if (x == 0.0)
			countHit0++;
		if (x < min)
			min = x;
		if (x > max)
			max = x;
		sum += x;
		sum2 += x * x;
		number_of_elements++;
		if (recordValues) {
			values.add(x);
		}
	}

	/** Returns the mean value of the contained data. */
	public double mean() {
		if (number_of_elements > 0)
			return sum / number_of_elements;

		return 0.0;
	}

	/** Returns the standard deviation of the contained data. */
	public double sdev() {
		if (number_of_elements > 0)
			return FastMath.sqrt((number_of_elements * sum2 - sum * sum)
					/ (number_of_elements * (number_of_elements - 1)));

		return 0.0;
	}

	/** Removes all data from this StatKeeper */
	public void clear() {
		sum = 0.0;
		sum2 = 0.0;
		number_of_elements = 0;
		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
		countHit0 = 0;
	}

	/** */
	public String result(boolean wHitrate) {
		DecimalFormat fm = new DecimalFormat("0.0000");
		String smin, smax;
		if (min == Double.MAX_VALUE)
			smin = "--";
		else
			smin = fm.format(min);
		if (max == -Double.MAX_VALUE)
			smax = "--";
		else
			smax = fm.format(max);
		if (wHitrate)
			return fm.format(mean()) + " " + number_of_elements + " " + smin
					+ " " + smax + " " + fm.format(sdev()) + " "
					+ fm.format(countHit0 / (double) number_of_elements);

		return fm.format(mean()) + " " + number_of_elements + " " + smin + " "
				+ smax + " " + fm.format(sdev());
	}

	/**
	 * Opens a file for writing and returns the <tt>FileOutputStream</tt> If the
	 * file exists and cannot be overwritten (locked by another application or
	 * read-only), an alternative filename with the same file extension
	 * containing "(x)", where x is the number of the trial starting at 2, is
	 * generated.
	 * 
	 * @param the
	 *            intendedFilename, which is tried first
	 * @return the FileOutputStream of the opened file
	 */
	private static FileOutputStream openFile(String intendedFilename) {
		String filename, filebody, extension;
		final int maxTrials = 3;
		FileOutputStream fos = null;

		int extPos = intendedFilename.lastIndexOf('.');

		if (extPos == -1) {
			extension = "";
			filebody = intendedFilename;
		} else {
			extension = intendedFilename.substring(extPos,
					intendedFilename.length());
			filebody = intendedFilename.substring(0, extPos);
		}

		filename = intendedFilename;

		for (int trial = 1; trial <= maxTrials + 1; trial++)
			try {
				fos = new FileOutputStream(filename);
				break;
			} catch (FileNotFoundException e) {
				// If the file cannot be overwritten, we get an exception,
				// adjust filename in this case
				if (trial < maxTrials) {
					filename = filebody + " (" + (trial + 1) + ")" + extension;
					System.err
							.println("Cannot write recorded values to disk, adjusting filename to '"
									+ filename + "'!");
				} else {
					System.err
							.println("Cannot write recorded values to disk - given up after "
									+ maxTrials + " trials!");
					e.printStackTrace();
					return null;
				}
			}
		return fos;
	}

	/**
	 * Saves the current statistic into a file
	 * 
	 * @param intendedFilename
	 *            , the filename might be changed if the file is busy
	 */
	public void saveResults(String intendedFilename) {
		FileOutputStream fos = openFile(intendedFilename);

		try {
			PrintStream ps = new PrintStream(fos);
			PrintStream out = System.out;
			System.setOut(ps);
			dumpValues();
			System.setOut(out);
			fos.close();
		} catch (IOException e) {
			System.err.println("Cannot write recorded values to disk!");
			e.printStackTrace();
		}
	}

	/**
	 * Prints all stored information to the standard output. If the object does
	 * not have the information on each single element then the average and the
	 * standard deviation will be printed only.
	 */
	private void dumpValues() {
		DecimalFormat fm = new DecimalFormat("0.0000");

		if (statName != null)
			System.out.println(";" + statName);

		if (recordValues == false) {
			System.out.println("mean;" + mean());
			System.out.println("n;" + number_of_elements);
			System.out.println("sdev;" + sdev());
		} else
			for (int i = 0; i < values.size(); i++)
				System.out.println(fm.format(values.get(i)));
	}

	/**
	 * Prints all information stored within the given list of Statkeepers to the
	 * standard output.
	 */
	private static void dumpMultiValues(ArrayList<StatKeeper> statList,
			boolean addStatColumns) {
		// make sure that the decimal separator is a dot
		DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
		mySymbols.setDecimalSeparator('.');

		DecimalFormat fm = new DecimalFormat("0.0000", mySymbols);

		// write header line
		String lastName = null, sep = "";
		int n = 0;
		
		for (StatKeeper s : statList) {
			int pos = s.statName.indexOf('.');
			if (pos == -1)
				pos = s.statName.length();
			String currentName = s.statName.substring(0, pos);
			if ((lastName != null) && addStatColumns)
				if (lastName.compareTo(currentName) != 0) {
					if (n > 1)
						System.out.print(",AVG." + lastName);
					n = 0;
				}
			lastName = currentName;
			System.out.print(sep + s.statName);
			sep = ",";
			n++;
		}
		if (addStatColumns && (n > 1))
			System.out.print(",AVG." + lastName);

		System.out.println();

		// we need maximum length
		int maxlength = 0;
		for (StatKeeper s : statList) {
			if (s.length() > maxlength)
				maxlength = s.length();
		}

		for (int i = 0; i < maxlength; i++) {

			for (int si = 0; si < statList.size(); si++) {
				StatKeeper s = statList.get(si);

				if (s.length() <= i) {
					// repeat last line
					System.out
							.print(fm.format(s.values.get(s.values.size() - 1)));
				} else {
					System.out.print (fm.format(s.values.get(i)));
				}
				
				// place separator if not last
				if (si != statList.size() - 1)
					System.out.print(sep);
				else {
					System.out.println();
				}

			}

		}

		// write columns BUGGY implementation, column length might be different!
		/*
		 * for (int i = 0; i < statList.get(0).number_of_elements; i++) {
		 * lastName = null; sep = ""; n = 0; sum = 0.0; for (StatKeeper s :
		 * statList) { int pos = s.statName.indexOf('.'); if (pos == -1) pos =
		 * s.statName.length(); String currentName = s.statName.substring(0,
		 * pos); if ((lastName != null) && addStatColumns) if
		 * (lastName.compareTo(currentName) != 0) { if (n > 1)
		 * System.out.print("," + fm.format(sum / n)); n = 0; sum = 0.0; }
		 * lastName = currentName; System.out.print(sep); sep = ","; if (i <
		 * s.values.size()) { System.out.print(fm.format(s.values.get(i))); n++;
		 * sum += s.values.get(i); }
		 * 
		 * } if (addStatColumns && (n > 1)) System.out.print("," + fm.format(sum
		 * / n)); System.out.println(); }
		 */
	}

	private static void dumpRBoxplotFile(ArrayList<StatKeeper> statList,
			int step) {

		// make sure that the decimal separator is a dot
		DecimalFormatSymbols mySymbols = new DecimalFormatSymbols();
		mySymbols.setDecimalSeparator('.');
		DecimalFormat fm = new DecimalFormat("0.0000", mySymbols);

		for (StatKeeper s : statList) {
			String sep = "";
			if (s.statName.contains("eneration"))
				continue;
			for (int i = 0; i < s.values.size(); i += step) {
				System.out.print(sep + fm.format(s.values.get(i)));
				sep = "\t";
			}
			System.out.println();
		}
	}

	public static void saveNotableStats(String filename,
			ArrayList<StatKeeper> statList) {
		saveNotableStats(filename, statList, false);
	}

	public static void saveNotableStats(String filename,
			ArrayList<StatKeeper> statList, boolean addStatColumns) {
		// sort according to names

		if (statList.size() == 0)
			return;

		FileOutputStream fos = openFile(filename);

		try {
			PrintStream ps = new PrintStream(fos);
			PrintStream out = System.out;
			System.setOut(ps);
			dumpMultiValues(statList, addStatColumns);
			System.setOut(out);
			fos.close();
		} catch (IOException e) {
			System.err.println("Cannot write recorded values to disk!");
			e.printStackTrace();
		}
	}

	/**
	 * Creates a text file in a format to be read by the statistical program R
	 * the stat with the generation count is omitted
	 * 
	 * @param filename
	 *            filename and path of the file to be created
	 * @param statList
	 *            an ArrayList of statkeeper objects
	 * @param step
	 *            only every step-th generation is saved, this is useful to keep
	 *            a comprehensible boxplot
	 */
	public static void saveRboxFile(String filename,
			ArrayList<StatKeeper> statList, int step) {
		FileOutputStream fos = openFile(filename);

		try {
			PrintStream ps = new PrintStream(fos);
			PrintStream out = System.out;
			System.setOut(ps);
			dumpRBoxplotFile(statList, 5); // !!!! Every 5th! //TODO make
											// configurable!
			System.setOut(out);
			fos.close();
		} catch (IOException e) {
			System.err.println("Cannot write recorded values to disk!");
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the valuation of a simulation (which may consist of more than
	 * one run) and outputs it on the console.
	 */
	public static void showValuation() {
		ArrayList<StatKeeper> bfitness = new ArrayList<StatKeeper>();
		ArrayList<StatKeeper> numSimulations = new ArrayList<StatKeeper>();

		for (int i = 0; i < FrevoMain.getStatistics().size(); i++) {
			StatKeeper sk = FrevoMain.getStatistics().get(i);
			if (sk.statName.startsWith("Best Fitness")) {
				bfitness.add(sk);
			} else if (sk.statName.startsWith("numSimulations")) {
				numSimulations.add(sk);
			}
		}

		int[] maxlevelgenerations = new int[bfitness.size()];
		int[] maxgenerations = new int[bfitness.size()];
		double[] maxfitnesses = new double[bfitness.size()];

		for (int j = 0; j < bfitness.size(); j++) {

			double minfitness = bfitness.get(j).getValues().get(0);
			maxfitnesses[j] = bfitness.get(j).getValues()
					.get(bfitness.get(j).getValues().size() - 1);
			double maxlevel = minfitness + (maxfitnesses[j] - minfitness) * 0.9;
			int i = 0;
			for (i = 0; i < bfitness.get(j).getValues().size(); i++) {
				if (bfitness.get(j).getValues().get(i) < maxlevel) {
					maxlevelgenerations[j] = i;
				}
				if (bfitness.get(j).getValues().get(i) < maxfitnesses[j]) {
					maxgenerations[j] = i;
				}
			}
			maxlevelgenerations[j]++;
			maxgenerations[j]++;
		}

		int maxlevelgeneration = 0;
		for (int i = 0; i < maxlevelgenerations.length; i++) {
			maxlevelgeneration += maxlevelgenerations[i];
		}
		maxlevelgeneration = Math.round((float) maxlevelgeneration
				/ (float) maxlevelgenerations.length);

		int maxgeneration = 0;
		for (int i = 0; i < maxgenerations.length; i++) {
			maxgeneration += maxgenerations[i];
		}
		maxgeneration = Math.round((float) maxgeneration
				/ (float) maxgenerations.length);

		double maxfitness = 0;
		for (int i = 0; i < maxfitnesses.length; i++) {
			maxfitness += maxfitnesses[i];
		}
		maxfitness = maxfitness / maxlevelgenerations.length;

		int numSim = 0;
		int numGen = 0;

		for (StatKeeper sk : numSimulations) {
			for (int i = 0; i < sk.getValues().size(); i++) {
				numSim += sk.getValues().get(i);
				numGen++;
			}
		}
		numSim = Math.round((float) numSim / (float) numGen);

		System.out.println("Average maximum fitness: " + maxfitness
				+ "\n90 percent of maximum reached in generation "
				+ (maxlevelgeneration)
				+ " (average)\nMaximum reached in generation "
				+ (maxgeneration)
				+ " (average)\nAverage Number of evaluations per generation: "
				+ numSim);
	}

	public int compareTo(StatKeeper o) {
		return statName.compareTo(o.getStatName());
	}

	public String getStatName() {
		return statName;
	}

	public String getValuesName() {
		return valuesName;
	}

	/**
	 * @return the minimum of the series
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @return the maximum of the series
	 */
	public double getMax() {
		return max;
	}

	public ArrayList<Double> getValues() {
		return values;
	}

}
