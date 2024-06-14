package Max_Traveler;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import utils.ARGoSNetworkInterface;
import core.AbstractSingleProblem;
import core.AbstractRepresentation;

/**
 * Example problem for the communication with ARGoS wich is the implementation
 * of the tutorial.
 * 
 * In this problem seven robots try to travel as far as possible. The whole
 * simulation is done by the robot simulator ARGoS while FREVO is used to
 * control the robots.
 * 
 * 
 * @author Thomas Dittrich
 * 
 */
public class Max_Traveler extends AbstractSingleProblem {

	private String address;
	private int numSteps;
	private ARGoSNetworkInterface ARGoSInterface;

	/**
	 * establish the network connection to ARGoS
	 */
	private void Init() {
		address = getPropertyValue("serverAddress");
		numSteps = Integer.parseInt(getPropertyValue("numSteps"));
		try {
			ARGoSInterface = new ARGoSNetworkInterface(7, address, 10010);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	/** Evaluates the given representation by calculating its corresponding fitness value. A higher fitness means better performance.<br>
	 * @param candidate The candidate solution to be evaluated.
	 * @return the corresponding fitness value. */
	public double evaluateCandidate(AbstractRepresentation candidate) {
		try {
			Init();
			AbstractRepresentation[] cand = new AbstractRepresentation[7];
			for (int i = 0; i < 7; i++) {
				cand[i] = candidate.clone();
			}
			double wallbump = 0.0;
			for (int i = 0; i < numSteps; i++) {
				/* Interact with Loop Function */
				ARGoSInterface.handleLoopFunction();
				for (int j = 0; j < 7; j++) {
					ArrayList<Float> sensorData = new ArrayList<Float>();
					String sensorIn = ARGoSInterface.readSensorData(j);
					double maxl = 0.0;
					/* convert input to ArrayList<Float> */
					String[] sensorInArr = sensorIn.split(" : ");
					for (int n = 0; n < sensorInArr.length; n++) {
						String[] v = sensorInArr[n].split(" ; ");
						float l = Float.parseFloat(v[0]);
						sensorData.add(l);
						maxl = Math.pow(Math.max(maxl, l), 4);
					}
					/* sum up the maximal sensor values */
					wallbump += maxl;
					// sensorData.add(va);
					// sensorData.add(vb);
					ArrayList<Float> out = cand[j].getOutput(sensorData);
					double a = out.get(0) * 2 - 1;
					double b = out.get(1) * 2 - 1;
					String outString = a + ";" + b;
					/* sum up the velocity */
					ARGoSInterface.writeActuatorData(j, outString);
				}
			}
			double[] fitnessArr = ARGoSInterface.getFitness();
			for (int i = 0; i < 7; i++) {
				ARGoSInterface.readSensorData(i);
				ARGoSInterface.writeActuatorData(i, "0.00 ; 0.00");
			}
			ARGoSInterface.reset();
			// double f1 = (((double)(7*numSteps)-numWallBumps)/(7*numSteps));
			double f1 = (((double) (7 * numSteps) - wallbump) / (7 * numSteps));
			// double f2 = (velocity/(7*numSteps));
			double f2 = fitnessArr[0];
			// double fitness = f1*f2;
			double fitness = f1 * f2;
			System.out
					.println(/* f2 + " : " + fitnessArr[0] + " : " + */fitness);
			return fitness;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Returns the achievable maximum fitness of this problem. A representations
	 * with this fitness value cannot be improved any further.
	 */
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		try {
			Init();
			AbstractRepresentation[] cand = new AbstractRepresentation[7];
			for (int i = 0; i < 7; i++) {
				cand[i] = candidate.clone();
			}
			while (!ARGoSInterface.handleLoopFunction().equals("End")) {
				for (int j = 0; j < 7; j++) {
					ArrayList<Float> sensorData = new ArrayList<Float>();
					String sensorIn = ARGoSInterface.readSensorData(j);
					/* convert input to ArrayList<Float> */
					String[] sensorInArr = sensorIn.split(" : ");
					for (int n = 0; n < sensorInArr.length; n++) {
						String[] v = sensorInArr[n].split(" ; ");
						float l = Float.parseFloat(v[0]);
						sensorData.add(l);
					}
					ArrayList<Float> out = cand[j].getOutput(sensorData);
					double a = out.get(0) * 2 - 1;
					double b = out.get(1) * 2 - 1;
					String outString = a + ";" + b;
					ARGoSInterface.writeActuatorData(j, outString);
				}
			}
			ARGoSInterface.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
