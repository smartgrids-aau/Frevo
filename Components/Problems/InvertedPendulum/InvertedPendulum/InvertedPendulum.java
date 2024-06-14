package InvertedPendulum;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import net.jodk.lang.FastMath;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.XMLFieldEntry;

/**
 * Simulation of an inverted pendulum. An inverted pendulum is a pendulum that
 * should be balanced above the slide by the representation. The representation
 * can only control the slide by imposing a force on it
 * 
 * @author Thomas Dittrich
 * 
 */
public class InvertedPendulum extends AbstractSingleProblem {

	private float TIMESTEPINMILLIS = 0.001f;
	private int NUMSTEPS = 100000;
	private float MAXSTRENGTH = 5.0f;
	private FitnessModel FITNESSMODEL;
	private InputModel INPUTMODEL;
	private Pendulum pendulum;
	private AbstractRepresentation visualizationcand;
	private JFrame f;
	private Timer timer;
	private double minx = 0.0;
	private double maxx = 0.0;
	private boolean moveRight = false;
	private boolean moveLeft = false;

	/**
	 * initializes a new simulation
	 */
	private void initialize() {
		XMLFieldEntry timestepinmillis = getProperties()
				.get("length_of_timestep_ms");
		TIMESTEPINMILLIS = Float.parseFloat(timestepinmillis.getValue());
		XMLFieldEntry numsteps = getProperties().get("number_of_timesteps");
		NUMSTEPS = Integer.parseInt(numsteps.getValue());
		XMLFieldEntry fitnessmodel = getProperties().get("fitnessmodel");
		FITNESSMODEL = FitnessModel.valueOf(fitnessmodel.getValue());
		XMLFieldEntry inputmodel = getProperties().get("inputmodel");
		INPUTMODEL = InputModel.valueOf(inputmodel.getValue());
		minx = 0.0;
		maxx = 0.0;
	}

	/**
	 * simulates one step of the simulation. This contains the simulation of the
	 * representation and the pendulum
	 * 
	 * @param candidate
	 *            the representation that controls the slide
	 * @param noiseF
	 *            a disturbance force that is imposed on the slide
	 */
	private void simstep(AbstractRepresentation candidate, double noiseF) {
		ArrayList<Float> input;

		switch (INPUTMODEL) {
		case ANGLE_VPENDULUM_VSLIDE_POSITION:
			input = new ArrayList<Float>(4);
			input.add((float) ((pendulum.getPhi() * 180.0 / Math.PI) + 360.0) % 360 - 90);
			input.add((float) pendulum.getVp());
			input.add((float) pendulum.getVs());
			input.add((float) pendulum.getPosition());
			break;
		case ANGLE_VPENDULUM_VSLIDE:
			input = new ArrayList<Float>(3);
			input.add((float) ((pendulum.getPhi() * 180.0 / FastMath.PI) + 360.0) % 360 - 90);
			input.add((float) pendulum.getVp());
			input.add((float) pendulum.getVs());
			break;
		case ANGLE_VPENDULUM:
			input = new ArrayList<Float>(2);
			input.add((float) ((pendulum.getPhi() * 180.0 / FastMath.PI) + 360.0) % 360 - 90);
			input.add((float) pendulum.getVp());
			break;
		case ANGLE:
			input = new ArrayList<Float>(1);
			input.add((float) ((pendulum.getPhi() * 180.0 / FastMath.PI) + 360.0) % 360 - 90);
			break;
		default:
			input = new ArrayList<Float>(4);
			input.add((float) ((pendulum.getPhi() * 180.0 / FastMath.PI) + 360.0) % 360 - 90);
			input.add((float) pendulum.getVp());
			input.add((float) pendulum.getVs());
			input.add((float) pendulum.getPosition());
			break;
		}

		ArrayList<Float> output = candidate.getOutput(input);

		double f = output.get(0) * 2.0 - 1.0;

		pendulum.movePendulum(f * MAXSTRENGTH + noiseF);
	}

	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {

		XMLFieldEntry inp = properties.get("inputmodel");
		XMLFieldEntry inputn = requirements.get("inputnumber");

		INPUTMODEL = InputModel.valueOf(inp.getValue());

		// change the number of inputs for the representation
		switch (INPUTMODEL) {
		case ANGLE_VPENDULUM_VSLIDE_POSITION:
			inputn.setValue(Integer.toString(4));
			break;
		case ANGLE_VPENDULUM_VSLIDE:
			inputn.setValue(Integer.toString(3));
			break;
		case ANGLE_VPENDULUM:
			inputn.setValue(Integer.toString(2));
			break;
		case ANGLE:
			inputn.setValue(Integer.toString(1));
			break;
		}

		return requirements;
	}

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		initialize();
		pendulum = new Pendulum(TIMESTEPINMILLIS / 1000.0, false);
		// do the simulation
		double fitness = 0.0;
		double sumphi = 0.0;
		double sumphi2 = 0.0;
		double sumdist = 0.0;
		double sumdist2 = 0.0;
		double sumdistabs = 0.0;
		for (int i = 0; i < NUMSTEPS; i++) {
			simstep(candidate, 0);

			if (pendulum.getPosition() > maxx) {
				maxx = pendulum.getPosition();
			}
			if (pendulum.getPosition() < minx) {
				minx = pendulum.getPosition();
			}

			double phi = FastMath
					.abs(((pendulum.getPhi() * 180.0 / FastMath.PI) + 360.0) % 360 - 90);
			double phifit = phi / 180;
			sumphi += Math.abs(phifit);
			sumphi2 += phifit * phifit;
			sumdist += pendulum.getPosition();
			sumdist2 += pendulum.getPosition() * pendulum.getPosition();
			sumdistabs += Math.abs(pendulum.getPosition());
		}
		// calculate the fitness
		if (FITNESSMODEL == FitnessModel.ANGLE_POW_2) {
			fitness = (1 - sumphi2 / NUMSTEPS);
		} else if (FITNESSMODEL == FitnessModel.ANGLE_POW_2_PLUS_USED_WAY) {
			fitness = ((1 - sumphi2 / NUMSTEPS) * 2 / FastMath
					.abs(Math.max(maxx - minx, 2)));
		} else if (FITNESSMODEL == FitnessModel.ANGLE_PLUS_DIST_ORIGIN) {
			fitness = (-sumphi - Math.abs(sumdist));
		} else if (FITNESSMODEL == FitnessModel.ANGLE_PLUS_DIST_ORIGIN_POW_2) {
			fitness = (-sumphi - Math.abs(sumdist2));
		} else if (FITNESSMODEL == FitnessModel.ANGLE_PLUS_ABS_DIST_ORIGIN) {
			fitness = (-sumphi - sumdistabs/1000);
		} else {
			fitness = (1 - sumphi2 / NUMSTEPS);
		}
		if (fitness == 0) {
			fitness = 0;
		}
		return fitness;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		initialize();
		visualizationcand = candidate;
		pendulum = new Pendulum(TIMESTEPINMILLIS / 1000.0, true);
		timer = new Timer();
		f = new JFrame();
		f.add(pendulum.getVisualization());
		f.pack();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
		timer.scheduleAtFixedRate(new TimerTick(), 0, (long) TIMESTEPINMILLIS);

		f.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				timer.cancel();
				timer.purge();

			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent arg0) {

			}
		});
		// the arrow keys control the disturbance force
		f.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent k) {
			}

			@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
					moveRight = false;
				} else if (k.getKeyCode() == KeyEvent.VK_LEFT) {
					moveLeft = false;
				}
			}

			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
					moveRight = true;
				} else if (k.getKeyCode() == KeyEvent.VK_LEFT) {
					moveLeft = true;
				}

			}
		});
	}

	@Override
	public double getMaximumFitness() {
		return 1.0;
	}

	private class TimerTick extends TimerTask {

		long msystime = 0;

		@Override
		public void run() {
			// get the disturbance force
			double strengthX = 0.0;
			if (moveRight && !moveLeft) {
				strengthX = 10.0;
			} else if (!moveRight && moveLeft) {
				strengthX = -10.0;
			}

			// calculate the simulation
			simstep(visualizationcand, strengthX);

			// display some values in the title of the JFrame
			long systime = System.nanoTime();
			double dt = (systime - msystime) / 1000000.0;
			String s = String.format("%5.2f  v1 = %5.2f  v2 = %5.2f  x = %.2f",
					dt, InvertedPendulum.this.pendulum.getVs(),
					InvertedPendulum.this.pendulum.getVp(),
					InvertedPendulum.this.pendulum.getPosition());
			InvertedPendulum.this.f.setTitle(s);
			msystime = systime;
		}
	}
}
