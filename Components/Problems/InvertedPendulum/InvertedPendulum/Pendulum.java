package InvertedPendulum;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.jodk.lang.FastMath;

/**
 * The class Pendulum is a simulation of a pendulum. This simulation considers
 * the force which is imposed on the slide, the gravity, the centrifugal force
 * and the feedback of the pendulum
 * 
 * @author Thomas Dittrich
 * 
 */
public class Pendulum {
	/**
	 * x position of the slide
	 */
	private double position;

	/**
	 * returns the x position of the slide
	 * 
	 * @return
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * x velocity of the slide
	 */
	private double vs;

	/**
	 * returns the x velocity of the slide
	 * 
	 * @return
	 */
	public double getVs() {
		return vs;
	}

	/**
	 * angle of the pendulum.</br> 0 ...... pendulum is to the right of the
	 * slide</br> 90 .... pendulum is above of the slide</br> 180 .. pendulum is
	 * to the left of the slide</br> 270 .. pendulum is beneath the slide</br>
	 */
	private double phi;

	/**
	 * returns the angle of the pendulum.</br> 0 ...... pendulum is to the right
	 * of the slide</br> 90 .... pendulum is above of the slide</br> 180 ..
	 * pendulum is to the left of the slide</br> 270 .. pendulum is beneath the
	 * slide</br>
	 * 
	 * @return
	 */
	public double getPhi() {
		return phi;
	}

	/**
	 * tangential velocity of the pendulum-mass
	 */
	private double vp;

	/**
	 * returns the tangential velocity of the pendulum-mass
	 * 
	 * @return
	 */
	public double getVp() {
		return vp;
	}

	private double visibleslidewaylength;
	private double visibleslidewayposition;
	private double dt;
	private double ms;
	private double r;
	private double mp;
	private final double g = 9.81;
	private PendulumVisualization pendulumVisualization;
	private boolean visualizationEnabled = false;

	/**
	 * Create a pendulum
	 * 
	 * @param timestepinsec
	 *            time between two simulation-steps
	 * @param enableVisualization
	 *            defines if the simulation is done with or without
	 *            visualization
	 */
	public Pendulum(double timestepinsec, boolean enableVisualization) {
		this.dt = timestepinsec;
		visibleslidewaylength = 20;
		position = 0.0;
		visibleslidewayposition = position;
		ms = 0.1;
		vs = 0.0;
		phi = Math.PI * 3.0 / 2.0;
		r = 1.0;
		vp = 0.0;
		mp = 0.01;
		visualizationEnabled = enableVisualization;
		if (enableVisualization) {
			pendulumVisualization = new PendulumVisualization();
		}
	}

	/**
	 * Returns the visualization of the pendulum (only if the visualization has
	 * been enabled in the constuctor)
	 * 
	 * @return JPanel which does the visualization (null if the visualization
	 *         has not been enabled)
	 */
	public JPanel getVisualization() {
		if (visualizationEnabled) {
			return pendulumVisualization.getPendulumVisualizationDisplay();
		}
		return null;
	}

	/**
	 * does one simulation-step
	 * 
	 * @param forceX
	 *            the strength that moves the slide
	 */
	public void movePendulum(double forceX) {
		double sinphi = FastMath.sin(phi);
		double cosphi = FastMath.cos(phi);

		double Fgsx = mp * g * sinphi * cosphi;
		double Fzsx = mp * vp * vp / r;
		double a = (forceX - Fgsx + Fzsx * cosphi)
				/ (ms + mp * FastMath.abs(cosphi));

		vs = vs + a * dt;
		vp = vp + a * sinphi * dt - g * cosphi * dt;
		phi = phi + vp / r * dt;
		position = position + vs * dt;
		visibleslidewayposition = position;
		if (visualizationEnabled) {
			pendulumVisualization.refresh();
		}
	}

	/**
	 * The class PendulumVisualization does the asynchronous visualization of
	 * the pendulum
	 * 
	 * @author Thomas Dittrich
	 * 
	 */
	private class PendulumVisualization extends SwingWorker<Void, Integer> {

		private PendulumVisualizationDisplay pendulumVisualizationDisplay;

		public PendulumVisualizationDisplay getPendulumVisualizationDisplay() {
			return pendulumVisualizationDisplay;
		}

		public PendulumVisualization() {
			super();
			pendulumVisualizationDisplay = new PendulumVisualizationDisplay();
		}

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		public void refresh() {
			publish();
		}

		protected void process(List<Integer> results) {
			pendulumVisualizationDisplay.repaint();
		}

		private class PendulumVisualizationDisplay extends JPanel {
			private static final long serialVersionUID = -6003551128733144194L;

			int width = 1000;
			int height = 300;

			public PendulumVisualizationDisplay() {
				super();
				this.setPreferredSize(new Dimension(width, height));
			}

			public void paintComponent(Graphics g) {
				width = this.getSize().width;
				height = this.getSize().height;
				double position = Pendulum.this.position;
				double slidewayposition = Pendulum.this.visibleslidewayposition;
				double slidewaylength = Pendulum.this.visibleslidewaylength;
				double pendulumAngle = Pendulum.this.phi;
				double pendulumlength = Pendulum.this.r;
				double slidepositionx = (width / 2.0 + ((position - slidewayposition)
						/ slidewaylength * width));
				double slidepositiony = height / 2.0;
				double masspositionx = pendulumlength / slidewaylength
						* width * FastMath.cos(pendulumAngle)
						+ slidepositionx;
				double masspositiony = -pendulumlength / slidewaylength
						* width * FastMath.sin(pendulumAngle)
						+ slidepositiony;

				g.clearRect(0, 0, width, height);
				g.setColor(Color.green);
				g.drawLine((int) slidepositionx, (int) slidepositiony,
						(int) masspositionx, (int) masspositiony);
				g.setColor(Color.blue.darker());
				g.fillOval((int) slidepositionx - 10,
						(int) slidepositiony - 10, 20, 20);
				g.setColor(Color.red);
				g.fillOval((int) masspositionx - 5, (int) masspositiony - 5,
						10, 10);

				int xl = (int) FastMath.ceil(slidewayposition / 5);
				for (int i = 0; i < slidewaylength / 5; i++) {
					double xline = ((xl + i) * 5.0 - slidewayposition)
							/ slidewaylength * width;
					g.drawLine((int) xline, 0, (int) xline, height);
				}
			}
		}
	}
}
