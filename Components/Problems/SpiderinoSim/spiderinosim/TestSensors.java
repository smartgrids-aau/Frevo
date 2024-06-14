package spiderinosim;

import java.util.ArrayList;
import java.util.Random;

public class TestSensors {

	public TestSensors() {
		// TODO Auto-generated method stub
		
		
		
		
		//double[] lut = Cny70Sensor.createLut(new int[]{0, 10, 20, 30, 40, 50, 60}, new double[]{1, 0.96, 0.84, 0.69, 0.55, 0.35, 0.1});

		try {
			SimulationProperties properties = new SimulationProperties(1, 0, 0, 100, 50, 2, 3, 1, .06, 0.06, 120, 10, 0, 0, 0, 0, 0, 0);
			CustomSimulation simulation = new CustomSimulation(properties, null);
			Spiderino spiderino = simulation.getSpiderino(0);
			ArrayList<Float> value = simulation.getSensorValues(spiderino);
			for(float f : value) {
				System.out.println(f + " ");
			}
			
			SimulationDisplay d = new SimulationDisplay(simulation, null);
			d.updateDisplay();
			
			
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new TestSensors();
	}
	
	public class CustomSimulation extends Simulation {

		public CustomSimulation(SimulationProperties properties, Random random) throws SimulationException {
			super(properties, random);			
		}

		@Override
		protected void populateWorld() throws SimulationException {			
			Spiderino spiderino = new Spiderino(0, 0, 0.06, 0.06, properties.getSpiderinoRadius());
			spiderinos.add(spiderino);
			if (!world.add(spiderino))
				System.out.println("Failed to add spiderino");
			
			spiderino = new Spiderino(0, 0, 1, 0.06, properties.getSpiderinoRadius());
			spiderinos.add(spiderino);
			if (!world.add(spiderino))
				System.out.println("Failed to add spiderino");
		}
				
	}

}
