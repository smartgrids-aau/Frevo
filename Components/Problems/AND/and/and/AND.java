package and.and;

import java.util.ArrayList;

import core.AbstractSingleProblem;
import core.AbstractRepresentation;

public class AND extends AbstractSingleProblem {

	@Override
	/** Evaluates the given representation by calculating its corresponding fitness value. A higher fitness means better performance.<br>
	 * @param candidate The candidate solution to be evaluated.
	 * @return the corresponding fitness value. */
	public double evaluateCandidate(AbstractRepresentation candidate) {
		
		double error = 0;
		
		ArrayList<Float> inputs = new ArrayList<Float>();
		ArrayList<Float> outputs;
		/*
		for(int i=0; i<=1; i++){
			for(int j=0; j<=1; j++){
				inputs.clear();
				
				inputs.add((float) i);
				inputs.add((float) j);
				outputs = candidate.getOutput(inputs);
				
				if(i != j | i == 0) error += outputs.get(0) ;		// should be 0
				else 				error += (1.0-outputs.get(0)) ;	// should be 1 iff i==j==1
			}
		}
		*/
		
		// test an average function
		for(int a=0; a<1000; a++){
			inputs.clear();
			for(int i=0; i<=1; i++){
				inputs.add((float) Math.random());
			}
			double expected = (inputs.get(0) + inputs.get(1)) / 2.0;
			double predicted = candidate.getOutput(inputs).get(0);
			error += Math.sqrt((predicted-expected)*(predicted-expected));
		}
		
		return -error;//-(error/100.0);
	}
	
	public void replayWithVisualization(AbstractRepresentation candidate){
		ArrayList<Float> inputs = new ArrayList<Float>();
		ArrayList<Float> outputs;
		
		/*
		for(int i=0; i<=1; i++){
			for(int j=0; j<=1; j++){
				inputs.clear();
				
				inputs.add((float) i);
				inputs.add((float) j);
				outputs = candidate.getOutput(inputs);
				
				System.out.println("A:"+i+", B:"+j+" --> "+outputs.get(0));
			}
		}*/
		
		// test an average function
		for(int i=0; i<=1; i++){
			inputs.add((float) Math.random());
		}
		double expected = (inputs.get(0) + inputs.get(1)) / 2.0;
		double predicted = candidate.getOutput(inputs).get(0);
		
		System.out.println("i:"+inputs.get(0)+", j:"+inputs.get(1)+" Expected: "+expected+" while predicted "+predicted);
	}
	
	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

}
