package spiderinosim;

import core.AbstractSingleProblem;
import main.FrevoMain;
import spiderinosim.Simulation.Command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import core.AbstractRepresentation;


/**
 * Glue code to connect FREVO with simulator
 *
 */
public class SpiderinoSim extends AbstractSingleProblem {
	
	
	protected DisplayWorker worker;
	protected double replaySpeed = 1;
	protected int[] curve;
	
	
	public double evaluateCandidate(AbstractRepresentation candidate) {
		try {
			SimulationProperties properties = new SimulationProperties(getProperties());
			double fitnessTotal = 0;	
			curve = new int[properties.getMaximumSteps()];
			for	(int i = 0; i < properties.getEvaluationCount(); i++) {
				Simulation simulation = new Simulation(properties, getRandom());
				fitnessTotal += runSimulation(simulation, candidate);
			}
			return fitnessTotal / properties.getEvaluationCount();
		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	protected void writeCurve(AbstractRepresentation candidate)
	{
		try {
			SimulationProperties properties = new SimulationProperties(getProperties());	
			curve = new int[properties.getMaximumSteps()];
			for	(int i = 0; i < properties.getEvaluationCount(); i++) {
				Simulation simulation = new Simulation(properties, getRandom());
				runSimulation(simulation, candidate);
			}
			
			File curveFile = File.createTempFile("curve", ".csv", new File(FrevoMain.getActiveDirectory()));
			System.out.println("Creating curve file: " + curveFile.getAbsolutePath());
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(curveFile)));
				
			for (int i = 0; i < curve.length; i++) {
				writer.write(i + "," + curve[i] + "\n");
			}
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}

	
	public void replayWithVisualization(AbstractRepresentation candidate) {				
		try {
			SimulationProperties properties = new SimulationProperties(getProperties());
			curve = new int[properties.getMaximumSteps()];
			
			writeCurve(candidate);
			
			
			Simulation simulation = new Simulation(properties, getRandom());					
			worker = new DisplayWorker(candidate, simulation);
			worker.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	
	private class DisplayWorker extends SwingWorker<Void, Integer> implements SimulationDisplay.SpeedListener {

		protected Simulation simulation;
		protected SimulationDisplay display;
		protected AbstractRepresentation candidate;
		
		public DisplayWorker(AbstractRepresentation candidate, Simulation simulation) {
			this.simulation = simulation;
			display = new SimulationDisplay(simulation, this);
			this.candidate = candidate;			
		}
		
		
		@Override
		protected Void doInBackground() throws Exception {
			runSimulation(simulation, candidate);
			return null;
		}
		
		public void setProgressToPublish(int p) {
			publish(p);
		}

		protected void process(List<Integer> results) {
			display.updateDisplay();
		}


		@Override
		public void speedChanged(double speed) {
			setReplaySpeed(speed);
		}

	}

	protected synchronized double getReplaySpeed() {
		return replaySpeed;
	}
	
	protected synchronized void setReplaySpeed(double replaySpeed) {
		this.replaySpeed = replaySpeed;
	}
	
	
	
	protected double runSimulation(Simulation simulation, AbstractRepresentation candidate) throws SimulationException, InterruptedException {

		// clone the candidate for each spiderino
		for	(Spiderino spiderino : simulation.getSpiderinos()) {
			spiderino.setCandidate(candidate.clone());
		}
			
		
		SimulationProperties properties = simulation.getProperties();		
		for (int i = 0; i < properties.getMaximumSteps(); i++) {
			
			for	(Spiderino spiderino : simulation.getSpiderinos()) {				
				ArrayList<Float> output = spiderino.getCandidate().getOutput(simulation.getSensorValues(spiderino));
				
				if (output.size() != properties.getOutputCount())
					throw new SimulationException("OutputCount mismatch - fix you XML!");
				
				if (output.size() == 1) {
				
					// convert output to command
					Simulation.Command command = Command.DoNothing;
					int c = Math.min((int) (output.get(0) * 5), 4);
					switch (c) {
					case 1:
						command = Command.TurnLeft;
						break;
						
					case 2:
						command = Command.TurnRight;
						break;
						
					case 3:
						command = Command.GoForward;
						break;
						
					case 4:
						command = Command.GoBackward;
						break;
	
					default:
						break;
					}
				
					simulation.executeCommand(spiderino, command);
					
				} else if (output.size() == 2) {
					float turnOutput = output.get(0);
					Simulation.Command turnCommand = Command.DoNothing;
					if (turnOutput <= 0.4f) {
						turnCommand = Command.TurnLeft;
					} else if (turnOutput >= 0.6f) {
						turnCommand = Command.TurnRight;
					}
					simulation.executeCommand(spiderino, turnCommand);
					
					float motorOutput = output.get(1);
					Simulation.Command motorCommand = Command.DoNothing;
					if (motorOutput <= 0.4f) {
						motorCommand = Command.GoForward;
					} else if (motorOutput >= 0.6f) {
						motorCommand = Command.GoBackward;
					}
					simulation.executeCommand(spiderino, motorCommand);						
				} else {
					throw new SimulationException("Invalid OutputCount!");
				}
			}
			
			if (worker != null) {
				worker.setProgressToPublish(i);
				Thread.sleep((int)(properties.getStepTime() * getReplaySpeed()));
			}
			
			int spiderinoGoalCount = 0;
			for	(Spiderino spiderino : simulation.getSpiderinos()) {				
				if (simulation.isSpiderinoAtGoal(spiderino)) {
					spiderinoGoalCount++;
				}
			}
			curve[i] += spiderinoGoalCount;
		}
		
		return simulation.getFitness();
	}
	
	
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}
}