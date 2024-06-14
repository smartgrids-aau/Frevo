package components.cdrones;

import graphics.JIntegerTextField;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.FrevoMain;
import net.jodk.lang.FastMath;

public class DronesDisplay extends JFrame {

	private static final long serialVersionUID = -3845311699398981320L;
	/** Scaling factor */
	private final Color backgroundColor = Color.DARK_GRAY;
	// public final Color gridlinesColor = Color.LIGHT_GRAY;
	private final Color stepnumbercolor = Color.WHITE;
	private final Color obstacleColor = Color.BLACK;
	private final Color dronesColor = Color.BLUE;
	// private final Color dronerangecolor = Color.YELLOW;
	//private final Color dronecommrange = Color.CYAN;
	private final Color directionarrowColor = Color.ORANGE;

	private final Color bumperInactiveColor;
	private final Color bumperActiveColor;
	
	private final Color radarInactiveColor;
	private final Color radarActiveColor;

	private JPanel menuPanel;
	private DrawPanel canvasPanel;
	public JIntegerTextField mapField;
	public JIntegerTextField dronesField;
	private JCheckBox dronenumberbox;

	public static final Color FIELDCOLOR = new Color(201, 196, 169);

	/** Width to draw calculated from simulation */
	// private Random generator = new NESRandom();

	private Icon playIcon;
	private Icon stopIcon;

	protected JButton startButton;
	protected JButton stopButton;

	private cdrones master;

	public DronesDisplay(final cdrones master) {
		super("2D Robot coverage simulator");
		this.master = master;
		setBounds(0, 0, 666, 600);// set frame
		setMinimumSize(new Dimension(555, 320));
		// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container con = this.getContentPane(); // inherit main frame
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));

		bumperInactiveColor = new Color(0.8f, 0.521f, .247f, 0.5f);
		bumperActiveColor = new Color(.698f, .133f, .133f, 1f);
		
		radarInactiveColor = new Color(.12f, .698f, .666f, 0.1f);
		radarActiveColor = new Color(.12f, .698f, .666f, 0.5f);

		// Create Menu frame
		menuPanel = new JPanel();
		menuPanel.setPreferredSize(new Dimension(600, 80));
		menuPanel.setMinimumSize(new Dimension(600, 80));
		menuPanel.setMaximumSize(new Dimension(600, 80));
		menuPanel.setBorder(new TitledBorder("Control"));
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));

		startButton = new JButton();
		startButton.setPreferredSize(new Dimension(30, 25));
		startButton.setMaximumSize(new Dimension(30, 25));
		startButton.setMinimumSize(new Dimension(30, 25));
		menuPanel.add(startButton);

		stopButton = new JButton();
		stopButton.setPreferredSize(new Dimension(30, 25));
		stopButton.setMaximumSize(new Dimension(30, 25));
		stopButton.setMinimumSize(new Dimension(30, 25));
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

				// update drone number percentage
				int dronenum = dronesField.getIntegerText();
				if ((dronenum > 0) && (dronenum <= 10)) {
					if (dronenum != DronesDisplay.this.master.DRONENUMBER) {
						DronesDisplay.this.master.DRONENUMBER = dronenum;
						// regenerate maps
						DronesDisplay.this.master.reCreateMaps();
					} else {
						DronesDisplay.this.master.DRONENUMBER = dronenum;
					}
				}

				int map = mapField.getIntegerText();
				if (map > cdrones.EVALUATIONNUMBER) {
					cdrones.EVALUATIONNUMBER = map;
					master.reCreateMaps();
				}

				DronesDisplay.this.master.runBackground(map - 1);
				startButton.setEnabled(false);
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (DronesDisplay.this.master.isRunning) {
					DronesDisplay.this.master.isRunning = false;
				}
			}
		});

		JLabel seedLabel = new JLabel("Map:");
		seedLabel.setSize(80, 30);
		menuPanel.add(seedLabel);

		mapField = new JIntegerTextField();
		mapField.setPreferredSize(new Dimension(33, 20));
		mapField.setMaximumSize(new Dimension(33, 20));
		mapField.setMinimumSize(new Dimension(33, 20));
		mapField.setText(Integer.toString(1));
		menuPanel.add(mapField);

		JLabel dronesLabel = new JLabel("Drones:");
		dronesLabel.setSize(80, 30);
		menuPanel.add(dronesLabel);

		dronesField = new JIntegerTextField();
		dronesField.setPreferredSize(new Dimension(25, 20));
		dronesField.setMaximumSize(new Dimension(25, 20));
		dronesField.setMinimumSize(new Dimension(25, 20));
		dronesField.setText(Integer.toString(master.DRONENUMBER));
		menuPanel.add(dronesField);

		JButton randomButton = new JButton("Random");
		randomButton.setSize(90, 25);
		randomButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapField.setText(Integer.toString(0));
			}
		});
		menuPanel.add(randomButton);

		dronenumberbox = new JCheckBox("Numbers");
		dronenumberbox.setSize(90, 30);
		dronenumberbox.setSelected(true);
		menuPanel.add(dronenumberbox);

		final int SPEED_MIN = -30;
		final int SPEED_REALTIME = -20;
		final int SPEED_MAX = -3;

		// get slider position for current speed setting
		int SPEED_INIT = (int) (Math.log10(cdrones.DISPLAYWAIT + 1) * -9.97 + 0.5);
		if (SPEED_INIT < SPEED_MIN)
			SPEED_INIT = SPEED_MIN;
		if (SPEED_INIT > SPEED_MAX)
			SPEED_INIT = SPEED_MAX;

		// System.out.println(SPEED_INIT+" -> is speed "+cdrones.DISPLAYWAIT);

		JSlider simSpeedSlider = new JSlider(JSlider.HORIZONTAL, SPEED_MIN,
				SPEED_MAX, SPEED_INIT);

		simSpeedSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();

				int newDisplayWait = (int) FastMath.pow(10,
						source.getValue() / -9.97) - 1;
				if (newDisplayWait <= 0)
					newDisplayWait = 1;

				cdrones.DISPLAYWAIT = newDisplayWait;
				// System.out.println(source.getValue()+" -> speed changed to "+cdrones.DISPLAYWAIT);
			}
		});
		simSpeedSlider.setMajorTickSpacing(10);
		simSpeedSlider.setPaintTicks(true);

		// Create the label table
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(Integer.valueOf(SPEED_REALTIME), new JLabel("realtime"));
		labelTable.put(Integer.valueOf(SPEED_MIN), new JLabel("slow"));
		labelTable.put(Integer.valueOf(SPEED_MAX), new JLabel("fast"));
		simSpeedSlider.setLabelTable(labelTable);

		simSpeedSlider.setPaintLabels(true);
		menuPanel.add(simSpeedSlider);

		canvasPanel = new DrawPanel();
		con.add(menuPanel);
		con.add(canvasPanel);

		setVisible(true); // add to frame and show
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
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				/*
				 * if (DronesDisplay.this.master.isRunning) {
				 * DronesDisplay.this.master.isRunning = false; }
				 */
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (DronesDisplay.this.master.isRunning) {
					DronesDisplay.this.master.isRunning = false;
				}
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}

		});
	}

	public void updateDisplay() {
		canvasPanel.repaint();
		this.repaint();
	}

	class DrawPanel extends JPanel {
		private static final long serialVersionUID = -8748735749891520153L;

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int dim = 1;
			Point fd = DronesServer.FIELDDIM;
			// calculate scaling property
			int w = getWidth() - 20;
			int h = getHeight() - 30;// leave some border, draw inside

			double scale = 0;
			if (((double) w / (double) fd.x) < ((double) h / (double) fd.y)) {
				scale = (double) w / (double) fd.x;
				h = (int) (scale * fd.y);
			} else {
				scale = (double) h / (double) fd.y;
				w = (int) (scale * fd.x);
			}

			// draw background
			g.setColor(backgroundColor);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setColor(Color.WHITE);
			g.fillRect(10, 10, w, h);

			// draw field grid
			/*
			 * g.setColor(gridlinesColor); int ax = (fd.x/dim); int ay =
			 * (fd.y/dim); for (int i=1;i<(ax);i++) {
			 * g.drawLine(10+(int)(dim*scale*(i)), 10, 10+(int)(dim*scale*(i)),
			 * h+9); //vertical lines } for (int i=1;i<(ay);i++) {
			 * g.drawLine(10, 10+(int)(dim*scale*(i)), w+9,
			 * 10+(int)(dim*scale*(i))); //vertical lines }
			 */

			// Draw rest only if parent object exists
			if (master.dronesserver != null) {

				int[][] vgrid = master.dronesserver.visitedgrid;
				// fill visited zones
				for (int x = 0; x < vgrid.length; x++) {
					for (int y = 0; y < vgrid[0].length; y++) {
						if (vgrid[x][y] > 0) {
							g.setColor(Color.GRAY);
							// g.setColor(makeColor(vgrid[x][y]));
							g.fillRect(
									(int) (((double) x * (double) dim * scale) + 10),
									(int) (((double) y * (double) dim * scale) + 10),
									(int) (dim * scale) + 1,
									(int) (dim * scale) + 1);
						}
					}
				}

				// draw obstacles
				int[][] ogrid = master.dronesserver.fieldgrid;
				for (int x = 0; x < ogrid.length; x++) {
					for (int y = 0; y < ogrid[0].length; y++) {
						if (ogrid[x][y] == DronesServer.BLOCKED) {
							g.setColor(obstacleColor);
							g.fillRect(
									(int) (((double) x * (double) dim * scale) + 10),
									(int) (((double) y * (double) dim * scale) + 10),
									(int) (dim * scale) + 1,
									(int) (dim * scale) + 1);
							// place red X
							/*
							 * g.setColor(Color.RED); g.drawLine( (int)
							 * (((double) x * (double) dim * scale) + 10), (int)
							 * (((double) y * (double) dim * scale) + 10), (int)
							 * (((double) (x + 1) * (double) dim * scale) + 10),
							 * (int) (((double) (y + 1) * (double) dim * scale)
							 * + 10)); g.drawLine( (int) (((double) (x + 1) *
							 * (double) dim * scale) + 10), (int) (((double) y *
							 * (double) dim * scale) + 10), (int) (((double) x *
							 * (double) dim * scale) + 10), (int) (((double) (y
							 * + 1) * (double) dim * scale) + 10));
							 */
						}
					}
				}

				// draw drones
				for (int d = 0; d < master.DRONENUMBER; d++) {
					Drone drone = master.drones[d];
					g.setColor(dronesColor);
					g.fillOval(
							(int) ((drone.getPosition().x - Drone.DRONE_SIZE / 2.0d) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.DRONE_SIZE / 2.0d) * scale) + 10,
							(int) (Drone.DRONE_SIZE * scale),
							(int) (Drone.DRONE_SIZE * scale));
					// draw detection circle
					/*
					 * g.setColor(dronerangecolor); g.drawOval( (int)
					 * ((master.drones[d].getPosition().x - (double)
					 * Drone.DETECTION_RANGE) * scale) + 10, (int)
					 * (((DronesServer
					 * .FIELDDIM.y-master.drones[d].getPosition().y) - (double)
					 * Drone.DETECTION_RANGE) * scale) + 10, (int)
					 * (Drone.DETECTION_RANGE * 2 * scale), (int)
					 * (Drone.DETECTION_RANGE * 2 * scale));
					 */

					// draw bumpers
					if (drone.BUMPER_TR_ON)
						g.setColor(bumperActiveColor);
					else
						g.setColor(bumperInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (Drone.DETECTION_RANGE * 2 * scale),
							(int) (Drone.DETECTION_RANGE * 2 * scale), 0, 90);

					if (drone.BUMPER_BR_ON)
						g.setColor(bumperActiveColor);
					else
						g.setColor(bumperInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (Drone.DETECTION_RANGE * 2 * scale),
							(int) (Drone.DETECTION_RANGE * 2 * scale), -90, 90);

					if (drone.BUMPER_TL_ON)
						g.setColor(bumperActiveColor);
					else
						g.setColor(bumperInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (Drone.DETECTION_RANGE * 2 * scale),
							(int) (Drone.DETECTION_RANGE * 2 * scale), 90, 90);

					if (drone.BUMPER_BL_ON)
						g.setColor(bumperActiveColor);
					else
						g.setColor(bumperInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.DETECTION_RANGE) * scale) + 10,
							(int) (Drone.DETECTION_RANGE * 2 * scale),
							(int) (Drone.DETECTION_RANGE * 2 * scale), 180, 90);

					// draw communication circle
					/*g.setColor(dronecommrange);
					for (int i = 0; i < 18; i++)
						g.drawArc(
								(int) ((drone.getPosition().x - (double) Drone.COMMUNICATION_RANGE) * scale) + 10,
								(int) (((DronesServer.FIELDDIM.y - drone
										.getPosition().y) - (double) Drone.COMMUNICATION_RANGE) * scale) + 10,
								(int) (Drone.COMMUNICATION_RANGE * 2 * scale),
								(int) (Drone.COMMUNICATION_RANGE * 2 * scale),
								i * 20, 10);*/
					
					if (drone.RADIO_TR_ON)
						g.setColor(radarActiveColor);
					else
						g.setColor(radarInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale),
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale), 0, 90);
					

					if (drone.RADIO_BR_ON)
						g.setColor(radarActiveColor);
					else
						g.setColor(radarInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale),
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale), -90, 90);

					if (drone.RADIO_TL_ON)
						g.setColor(radarActiveColor);
					else
						g.setColor(radarInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale),
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale), 90, 90);

					if (drone.RADIO_BL_ON)
						g.setColor(radarActiveColor);
					else
						g.setColor(radarInactiveColor);

					g.fillArc(
							(int) ((drone.getPosition().x - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) - Drone.COMMUNICATION_RANGE) * scale) + 10,
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale),
							(int) (Drone.COMMUNICATION_RANGE * 2 * scale), 180, 90);
								
					// draw drone direction
					g.setColor(directionarrowColor);
					double angle = Math.toRadians(drone.getBodyDirection());
					g.drawLine(
							(int) ((drone.getPosition().x * scale) + 10),
							(int) (((DronesServer.FIELDDIM.y - drone
									.getPosition().y) * scale) + 10),
							(int) (((drone.getPosition().x * scale) + 10) + Drone.DETECTION_RANGE
									* scale * FastMath.cos(angle)),
							(int) ((((DronesServer.FIELDDIM.y - drone
									.getPosition().y) * scale) + 10) - Drone.DETECTION_RANGE
									* scale * FastMath.sin(angle)));

					// draw drone number
					// g.setColor(Color.BLACK);
					// if (dronenumberbox.isSelected()) {
					// g.drawString(Integer.toString(master.drones[d].id_number),
					// (int)(master.drones[d].getPosition().x* multipl+55),
					// (int)(master.drones[d].getPosition().y* multipl+45));
					// }
					// System.out.println
					// (Integer.toString(master.drones[d].id_number)+": "+master.drones[d].getPosition().x+","+master.drones[d].getPosition().y);
				}
			}
			// draw the step number to lower left corner
			g.setColor(stepnumbercolor);
			g.drawString(
					"Step: " + master.stepnumber + "/"
							+ Integer.toString(master.aktStep), 10, h + 25);

		}

	}
}
