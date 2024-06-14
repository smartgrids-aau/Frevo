package XOR;


import java.util.ArrayList;

import core.AbstractRepresentation;
import core.AbstractSingleProblem;

/** A simple XOR problem to test evolutionary algorithms if they work properly. Fitness is determined by mean square error. */
public class XOR extends AbstractSingleProblem {
	
	private boolean printOutput = false;

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		//test all 4 cases and return negative mean square error
		AbstractRepresentation net = candidate;
		
		double sumerror = 0;
		
		ArrayList<Float> input = new ArrayList<Float>();
		ArrayList<Float> output = new ArrayList<Float>();
		
		// 0 - 0 : 0
		input.add(0f);
		input.add(0f);
		output = net.getOutput(input);
		
		float out = output.get(0);
		
		if (printOutput) {
			System.out.println ("In: 0-0 Out: "+out);
		}
		
		sumerror += Math.pow((out-0),2);
		
		// 0 - 1 : 1
		input.clear();
		output.clear();
		net.reset();
		
		input.add(0f);
		input.add(1f);
		output = net.getOutput(input);
		
		out = output.get(0);
		if (printOutput) {
			System.out.println ("In: 0-1 Out: "+out);
		}
		
		sumerror += Math.pow((out-1),2);
		
		// 1 - 0 : 1
		input.clear();
		output.clear();
		net.reset();
				
		input.add(1f);
		input.add(0f);
		output = net.getOutput(input);
				
		out = output.get(0);
		if (printOutput) {
			System.out.println ("In: 1-0 Out: "+out);
		}
				
		sumerror += Math.pow((out-1),2);
		
		// 1 - 1 : 0
		input.clear();
		output.clear();
		net.reset();
						
		input.add(1f);
		input.add(1f);
		output = net.getOutput(input);
						
		out = output.get(0);
		if (printOutput) {
			System.out.println ("In: 1-1 Out: "+out);
		}
						
		sumerror += Math.pow((out-0),2);			
		
		//return the sum
		sumerror = Math.sqrt(sumerror / 4.0);
		if (sumerror > 0)
			sumerror = - sumerror;
		
		return sumerror;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		printOutput = true;
		double res = evaluateCandidate(candidate);
		printOutput = false;
		System.out.println ("MSE: "+res);

	}
	
	@Override
	public double getMaximumFitness() {
		return 100;
	}

}
