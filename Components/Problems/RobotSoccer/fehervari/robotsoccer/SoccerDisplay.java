package fehervari.robotsoccer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import net.jodk.lang.FastMath;

import main.FrevoMain;

public class SoccerDisplay extends JFrame implements WindowListener {

	/** Color of the field (grass) */
	private final Color FIELDCOLOR = new Color(68, 128, 24);
	private final Color LINECOLOR = Color.WHITE;
	private final Color TEAM_LEFT_COLOR = Color.RED.darker();
	private final Color TEAM_RIGHT_COLOR = Color.BLUE;
	private final Color BALL_COLOR = Color.BLACK;

	/** Width of the lines painted on the field */
	private final double LINE_WIDTH = 0.01;

	private static final long serialVersionUID = -3845311699398981320L;
	private JPanel menuPanel;
	private DrawPanel canvasPanel;

	protected JButton startButton;
	protected JButton stopButton;
	private Icon playIcon;
	private Icon stopIcon;

	private DisplayWorker workerThread;
	ParameterSet parameters;
	private SoccerServer server = null;
	private Random generator;

	SoccerDisplay(ParameterSet parameters, Random random) {
		super("Robot Soccer Simulation");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0, 0, 620, 550);// set initial frame
		Container con = this.getContentPane(); // inherit main frame
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));

		this.parameters = parameters;
		generator = random;

		// Create Menu frame
		menuPanel = new JPanel();
		menuPanel.setPreferredSize(new Dimension(300, 50));
		menuPanel.setMinimumSize(new Dimension(300, 50));
		menuPanel.setMaximumSize(new Dimension(300, 50));
		menuPanel.setBorder(new TitledBorder("Control"));
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));

		startButton = new JButton();
		startButton.setSize(30, 25);
		menuPanel.add(startButton);

		stopButton = new JButton();
		stopButton.setSize(30, 25);
		menuPanel.add(stopButton);

		try {
			playIcon = new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/media/Play24.gif"));
			stopIcon = new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/media/Stop24.gif"));
			startButton.setIcon(playIcon);
			stopButton.setIcon(stopIcon);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startButton.setEnabled(false);
				workerThread = new DisplayWorker();
				workerThread.execute();
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startButton.setEnabled(true);
				workerThread.stopSimulation();
			}
		});

		con.add(menuPanel);

		canvasPanel = new DrawPanel();

		con.add(canvasPanel);

		this.setVisible(true); // add to frame and show

		canvasPanel.repaint();
		canvasPanel.setBackground(FIELDCOLOR);

		canvasPanel.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				e.getComponent().repaint();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		this.setLocationRelativeTo(null);
	}

	class DrawPanel extends JPanel {
		private static final long serialVersionUID = -8748735749891520153L;
		private final Point2D.Double topleftcorner = new Point2D.Double(10, 10);
		private Point2D.Double centerpoint = new Point2D.Double();

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			// calculate scaling property
			int w = getWidth() - 20;
			int h = getHeight() - 20;// leave some border, draw inside

			double scale = 0;
			if (((double) w / SoccerServer.FIELD_WIDTH) < ((double) h / SoccerServer.FIELD_HEIGHT)) {
				scale = (double) w / SoccerServer.FIELD_WIDTH;
				h = (int) (scale * SoccerServer.FIELD_HEIGHT);
			} else {
				scale = (double) h / SoccerServer.FIELD_HEIGHT;
				w = (int) (scale * SoccerServer.FIELD_WIDTH);
			}

			double scaledwidth = scale * SoccerServer.FIELD_WIDTH;
			double scaledheight = scale * SoccerServer.FIELD_HEIGHT;

			centerpoint.x = topleftcorner.x + (scaledwidth / 2.0);
			centerpoint.y = topleftcorner.y + (scaledheight / 2.0);

			// draw field boundary
			g.setColor(LINECOLOR);
			g2.setStroke(new BasicStroke((int) (LINE_WIDTH * scale)));
			g2.draw(new Line2D.Double(topleftcorner, new Point2D.Double(
					topleftcorner.x + scaledwidth, topleftcorner.y)));
			g2.draw(new Line2D.Double(topleftcorner, new Point2D.Double(
					topleftcorner.x, topleftcorner.y + scaledheight)));
			g2.draw(new Line2D.Double(new Point2D.Double(topleftcorner.x,
					topleftcorner.y + scaledheight), new Point2D.Double(
					topleftcorner.x + scaledwidth, topleftcorner.y
							+ scaledheight)));
			g2.draw(new Line2D.Double(new Point2D.Double(topleftcorner.x
					+ scaledwidth, topleftcorner.y), new Point2D.Double(
					topleftcorner.x + scaledwidth, topleftcorner.y
							+ scaledheight)));

			// half line
			g2.draw(new Line2D.Double(new Point2D.Double(topleftcorner.x
					+ scaledwidth / 2.0, topleftcorner.y), new Point2D.Double(
					topleftcorner.x + scaledwidth / 2.0, topleftcorner.y
							+ scaledheight)));

			// center circle
			g2.draw(new Ellipse2D.Double(centerpoint.x - (0.915 * scale),
					centerpoint.y - (0.915 * scale), 1.83 * scale, 1.83 * scale));

			// penalty area
			g2.draw(new Rectangle2D.Double(topleftcorner.x, centerpoint.y
					- (2.0175 * scale), 1.65 * scale, 4.035 * scale));
			g2.draw(new Rectangle2D.Double(topleftcorner.x + scale * 8.85,
					centerpoint.y - (2.0175 * scale), 1.65 * scale,
					4.035 * scale));

			// goal area
			g2.draw(new Rectangle2D.Double(topleftcorner.x, centerpoint.y
					- (0.9175 * scale), 0.55 * scale, 1.835 * scale));
			g2.draw(new Rectangle2D.Double(topleftcorner.x + scale * 9.95,
					centerpoint.y - (0.9175 * scale), 0.55 * scale,
					1.835 * scale));

			// goal inside
			g2.draw(new Rectangle2D.Double(topleftcorner.x - scale
					* SoccerServer.GOAL_DEPTH, centerpoint.y
					- (SoccerServer.GOAL_WIDTH / 2 * scale),
					SoccerServer.GOAL_DEPTH * scale, SoccerServer.GOAL_WIDTH
							* scale));
			g2.draw(new Rectangle2D.Double(topleftcorner.x + scaledwidth,
					centerpoint.y - (SoccerServer.GOAL_WIDTH / 2 * scale),
					SoccerServer.GOAL_DEPTH * scale, SoccerServer.GOAL_WIDTH
							* scale));

			// penalty circles
			g2.draw(new Arc2D.Double(topleftcorner.x + scale * 0.185,
					centerpoint.y - scale * 0.915, scale * 1.83, scale * 1.83,
					53, -106, Arc2D.OPEN));
			g2.draw(new Arc2D.Double(topleftcorner.x + scale * 8.485,
					centerpoint.y - scale * 0.915, scale * 1.83, scale * 1.83,
					233, -106, Arc2D.OPEN));

			// center point
			g.setColor(new Color(255, 255, 255, 150));
			g.fillOval((int) (centerpoint.x - (0.08 * scale)),
					(int) (centerpoint.y - (0.08 * scale)),
					(int) (0.16 * scale), (int) (0.16 * scale));

			if (server != null) {
				// draw left team
				for (SoccerRobot robot : server.players[0]) {
					g.setColor(TEAM_LEFT_COLOR);
					Point2D pos = robot.getPosition();
					// body
					g.fillOval(
							(int) (centerpoint.x + (pos.getX() - SoccerRobot.ROBOT_DIAMETER / 2)
									* scale),
							(int) (centerpoint.y - (pos.getY() + SoccerRobot.ROBOT_DIAMETER / 2)
									* scale),
							(int) (SoccerRobot.ROBOT_DIAMETER * scale),
							(int) (SoccerRobot.ROBOT_DIAMETER * scale));

					// draw direction
					g.setColor(Color.WHITE);
					double dir = robot.getBodyDirection();
					g.drawLine((int) (centerpoint.x + pos.getX() * scale),
							(int) (centerpoint.y - pos.getY() * scale),
							(int) (centerpoint.x + ((pos.getX() + 0.25 *FastMath.cos(FastMath.toRadians(dir))) * scale)),
							(int) (centerpoint.y - ((pos.getY() + 0.25 *FastMath.sin(FastMath.toRadians(dir))) * scale)));
				}

				// draw right team
				for (SoccerRobot robot : server.players[1]) {
					g.setColor(TEAM_RIGHT_COLOR);
					Point2D pos = robot.getPosition();
					// body
					g.fillOval(
							(int) (centerpoint.x + (pos.getX() - SoccerRobot.ROBOT_DIAMETER / 2)
									* scale),
							(int) (centerpoint.y - (pos.getY() + SoccerRobot.ROBOT_DIAMETER / 2)
									* scale),
							(int) (SoccerRobot.ROBOT_DIAMETER * scale),
							(int) (SoccerRobot.ROBOT_DIAMETER * scale));
					// direction
					g.setColor(Color.WHITE);
					double dir = robot.getBodyDirection();
					g.drawLine((int) (centerpoint.x + pos.getX() * scale),
							(int) (centerpoint.y - pos.getY() * scale),
							(int) (centerpoint.x + ((pos.getX() + 0.25 *FastMath.cos(FastMath.toRadians(dir))) * scale)),
							(int) (centerpoint.y - ((pos.getY() + 0.25 *FastMath.sin(FastMath.toRadians(dir))) * scale)));					
				}

				// draw ball
				g.setColor(BALL_COLOR);
				g.fillOval((int) (centerpoint.x + (server.ball.getPosition()
						.getX() - server.ball.getDiameter() / 2) * scale),
						(int) (centerpoint.y - (server.ball.getPosition()
								.getY() + server.ball.getDiameter() / 2)
								* scale),
						(int) (server.ball.getDiameter() * scale),
						(int) (server.ball.getDiameter() * scale));

				// draw actual timestep
				g2.drawString("Time: " + server.actualStep + " / "
						+ server.stepNumber + "  Goals: [" + server.goals[0]
						+ " : " + server.goals[1] + "]", (int) topleftcorner.x,
						(int) (topleftcorner.y + scaledheight + 20));

				g.setColor(TEAM_LEFT_COLOR);
				g.fillRect((int) topleftcorner.x, (int) (topleftcorner.y
						+ scaledheight + 50), 20, 30);

				g.setColor(TEAM_RIGHT_COLOR);
				g.fillRect((int) (topleftcorner.x + scaledwidth - 20),
						(int) (topleftcorner.y + scaledheight + 50), 20, 30);
			}
		}
	}

	private class DisplayWorker extends SwingWorker<Void, Integer> {

		@Override
		protected Void doInBackground() throws Exception {

			// create new server
			server = new SoccerServer(parameters, generator);

			// run simulation without display
			server.runSimulation(SoccerDisplay.this.canvasPanel);
			return null;
		}

		public void stopSimulation() {
			server.stop();
		}

		@Override
		public void done() {
			// code to ensure that exceptions are handled and not swallowed
			try {
				get();
			} catch (final InterruptedException ex) {
				throw new RuntimeException(ex);
			} catch (final ExecutionException ex) {
				throw new RuntimeException(ex.getCause());
			}
		}

		protected void process(List<Integer> results) {
			canvasPanel.repaint();
		}

	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		workerThread.stopSimulation();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		workerThread.stopSimulation();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
}
