package sched;

import java.util.ArrayList;

public class Stats<E extends Number> {

	ArrayList<E> data;
	int size;

	public Stats(ArrayList<E> data) {
		this.data = data;
		size = data.size();
	}

	public double getMean() {
		double sum = 0.0;
		for (E a : data)
			sum += a.doubleValue();
		return sum / size;
	}

	public double getVariance() {
		double mean = getMean();
		double temp = 0;
		for (E a : data)
			temp += (mean - a.doubleValue()) * (mean - a.doubleValue());
		return temp / size;
	}

	public double getStdDev() {
		return Math.sqrt(getVariance());
	}

	
}
