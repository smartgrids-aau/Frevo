package light;
import graphics.JIntegerTextField;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import main.FrevoMain;
import net.jodk.lang.FastMath;
import utils.NESRandom;

public class SimDisplay extends JFrame {

	private static final long serialVersionUID = -3845311699398981320L;
	private JPanel menuPanel;
	private DrawPanel canvasPanel;

	public JIntegerTextField seedField;
	
	private Random generator = new NESRandom();
	
	private Icon playIcon;
	private Icon stopIcon;
	
	protected JButton startButton;
	protected JButton stopButton;
	protected JCheckBox enableTrackingCheckBox;
	protected JCheckBox enableGridLinesCheckBox;
	
	public static final Color FIELDCOLOR = new Color (201,196,169);
	public static final int DEFAULT_DISPLAY_WIDTH = 320;
	public static final int DEFAULT_DISPLAY_HEIGHT = 320;
	
	
	private Light master;
	
	public SimDisplay(Light master) {
		super("2D Robot Light! Simulation");
	    this.master = master;
	    setBounds(0,0,620,688);// set initial frame
	    setMinimumSize(new Dimension(DEFAULT_DISPLAY_WIDTH, DEFAULT_DISPLAY_HEIGHT));
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    Container con = this.getContentPane(); // inherit main frame
	    con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
	    
	    //Create Menu frame
	    menuPanel = new JPanel();
	    menuPanel.setPreferredSize(new Dimension(300, 50));
	    menuPanel.setMinimumSize(new Dimension(300, 50));
	    menuPanel.setMaximumSize(new Dimension(300, 50));
	    menuPanel.setBorder(new TitledBorder("Control"));
	    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
	    
	    startButton = new JButton ();
	    startButton.setSize(30, 25);
	    menuPanel.add(startButton);
	    
	    stopButton = new JButton ();
		stopButton.setSize(30, 25); 
	    menuPanel.add(stopButton);
	    
	    try {
			playIcon = new ImageIcon(new URL("jar:file:" + FrevoMain.getInstallDirectory() + "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/" + "toolbarButtonGraphics/media/Play24.gif"));
			stopIcon = new ImageIcon(new URL("jar:file:" + FrevoMain.getInstallDirectory() + "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/" + "toolbarButtonGraphics/media/Stop24.gif"));
			startButton.setIcon(playIcon);
			startButton.setToolTipText("Start simulation");
			stopButton.setIcon(stopIcon);
			stopButton.setToolTipText("Stop simulation");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
				
	    startButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {							
				SimDisplay.this.master.runBackground();
				startButton.setEnabled(false);
			}
		});
	    
	    stopButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (SimDisplay.this.master.isRunning) {
					SimDisplay.this.master.isRunning = false;
				}
			}
		});
	    
	    JLabel seedLabel = new JLabel ("Seed:");
	    seedLabel.setPreferredSize(new Dimension(50, 30));
	    seedLabel.setMinimumSize(new Dimension(50, 30));
	    seedLabel.setMaximumSize(new Dimension(50, 30));
	    menuPanel.add(seedLabel);
	    
	    seedField = new JIntegerTextField();
	    seedField.setPreferredSize(new Dimension(40, 20));
	    seedField.setMinimumSize(new Dimension(40, 20));
	    seedField.setMaximumSize(new Dimension(40, 20));
	    seedField.setText(Integer.toString(1234));
	    menuPanel.add(seedField);
	    
	    JButton randomButton = new JButton ("Random");
	    randomButton.setSize(90, 25);
	    randomButton.setLocation(160, 18);
	    randomButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				seedField.setText(Integer.toString(generator.nextInt(10000)));
			}
		});
	    menuPanel.add(randomButton);
	    
	    con.add(menuPanel);
	    
	    
	    JPanel checkboxes = new JPanel();
	    checkboxes.setLayout(new BoxLayout(checkboxes, BoxLayout.X_AXIS));
	    con.add(checkboxes);
	    
	    enableTrackingCheckBox = new JCheckBox("Tracking");
	    enableTrackingCheckBox.setSelected(false);
	    checkboxes.add(enableTrackingCheckBox);
	    
	    enableGridLinesCheckBox = new JCheckBox("Grid");
	    enableGridLinesCheckBox.setSelected(false);
	    checkboxes.add(enableGridLinesCheckBox);
	    
	    canvasPanel = new DrawPanel();
	    
	    con.add(canvasPanel);
	    
	    addWindowListener(new WindowAdapter() {  // add listener to speed up
			@Override                            // sim once window is closed
			public void windowClosing(WindowEvent we) {
				setWithPause(false);
				super.windowClosing(we);
			}
		});
	    
	    setVisible(true);  // add to frame and show
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
	
	private double calcResizeProp(){
		int border = 20;		
		double prop = (double)(Math.min(getWidth() - border, getHeight() - border)) / (double)(SimServer.FIELDDIM);
		return prop;
	}
	
	public void updateDisplay() {
		canvasPanel.repaint();
		this.repaint();
	}
	
	class DrawPanel extends JPanel
	{
		private static final long serialVersionUID = -8748735749891520153L;
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//calculate scaling property
			int w = getWidth()-20;
			int h = getHeight()-20;//leave some border, draw inside
			double prop;
			Point origo;//middle point of inner
			int u =0;
			if (w<=h) u = w;
			else u = h;
			
			prop = (double)(u) / (double)(SimServer.FIELDDIM);
			origo = new Point (10+u/2,10+u/2);
			
			//draw field boundary
			g.setColor(Color.BLACK);
			g.drawLine(10,10, u, 10);
			g.drawLine(10, u, u, u);
			g.drawLine(10, 10, 10, u);
			g.drawLine(u, 10, u, u);
			
			//Draw rest only if parent object exists	
			Point robotcenter;
			if (master.simserver != null) {
				robotcenter = new Point ((int)(master.robot.getPosition().getX()),(int)(master.robot.getPosition().getY()));
				
				// draw previous moves of the robot
				if (enableTrackingCheckBox.isSelected()) {					
					g.setColor(Color.black);
					List<Point2D> robotMoves = master.simserver.getRobotMoves();
					Iterator<Point2D> it = robotMoves.iterator();
					Point2D A;
					Point2D B;					
					if (robotMoves.size() >= 2){
						A = it.next();
						while(it.hasNext())
						{
							B = it.next();
							g.drawLine(
									(int)(A.getX() * prop + origo.x), (int)(A.getY() * prop + origo.y),
									(int)(B.getX() * prop + origo.x), (int)(B.getY() * prop + origo.y));
							
							A = B;
						}
					}						
				}
				
				if (enableGridLinesCheckBox.isSelected())
					drawGridLines(g, prop);
				
				robotcenter = new Point ((int)(robotcenter.x* prop+origo.x),(int)(robotcenter.y * prop +origo.y ));
				
				//draw target range circle
				int rad = (int)(SimServer.LIGHTSOURCE_PLACEMENT_RADIUS*prop);
				g.setColor(Color.RED);

				for (int i=0;i<18;i++)	g.drawArc(origo.x-rad, origo.y-rad, rad*2, rad*2, i*20, 10);
				//draw target
				Point tloc = new Point (
						(int)(master.simserver.targetlocation.getX() * prop + origo.x),
						(int)(master.simserver.targetlocation.getY() * prop + origo.y) );
				int ldia = (int)(SimServer.LIGHTSOURCE_SIZE*prop);
				
				g.setColor(Color.ORANGE);
				g.fillOval(tloc.x-ldia/2, tloc.y-ldia/2, ldia, ldia);
				g.setColor(Color.YELLOW);
				g.drawOval(tloc.x-ldia/2, tloc.y-ldia/2, ldia, ldia);
				//draw robot
				g.setColor(Robot.BASECOLOR);
				
				//draw a point always
				g.drawLine(robotcenter.x, robotcenter.y, robotcenter.x, robotcenter.y);

				//draw body
				g.fillOval((int)(robotcenter.x-(Robot.ROBOT_DIAMETER*prop/2.0)), (int)(robotcenter.y-(Robot.ROBOT_DIAMETER*prop/2.0)), (int)(Robot.ROBOT_DIAMETER*prop), (int)(Robot.ROBOT_DIAMETER*prop));
				
				//draw robot orientation
				g.setColor(Color.RED);
				double dir = Math.toRadians(master.robot.getBodyDirection());		
				g.drawLine(robotcenter.x, robotcenter.y, (int)(robotcenter.x+FastMath.sin(dir)*(Robot.ROBOT_DIAMETER*prop/2.0)),(int)( robotcenter.y-FastMath.cos(dir)*(Robot.ROBOT_DIAMETER*prop/2.0)));
				
				//draw wheels
				g.setColor(Robot.WHEELCOLOR);
				
				Point leftwheelcenter = new Point ((int)(robotcenter.x+FastMath.sin(dir-1.57)*(Robot.ROBOT_DIAMETER*prop/2.0)),(int)( robotcenter.y-FastMath.cos(dir-1.57)*(Robot.ROBOT_DIAMETER*prop/2.0)));
				Point leftwheelcenter2 = new Point ((int)(robotcenter.x+FastMath.sin(dir-1.57)*((Robot.ROBOT_DIAMETER*prop/2.0)-1)),(int)( robotcenter.y-FastMath.cos(dir-1.57)*((Robot.ROBOT_DIAMETER*prop/2.0)-1)));
				
				g.drawLine((int)(leftwheelcenter.x+FastMath.sin(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( leftwheelcenter.y-FastMath.cos(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)),(int)(leftwheelcenter.x+FastMath.sin(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( leftwheelcenter.y-FastMath.cos(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)));				
				g.drawLine((int)(leftwheelcenter2.x+FastMath.sin(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( leftwheelcenter2.y-FastMath.cos(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)),(int)(leftwheelcenter2.x+FastMath.sin(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( leftwheelcenter2.y-FastMath.cos(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)));
				
				Point rightwheelcenter = new Point ((int)(robotcenter.x+FastMath.sin(dir+1.57)*(Robot.ROBOT_DIAMETER*prop/2.0)),(int)( robotcenter.y-FastMath.cos(dir+1.57)*(Robot.ROBOT_DIAMETER*prop/2.0)));
				Point rightwheelcenter2 = new Point ((int)(robotcenter.x+FastMath.sin(dir+1.57)*((Robot.ROBOT_DIAMETER*prop/2.0)-1)),(int)( robotcenter.y-FastMath.cos(dir+1.57)*((Robot.ROBOT_DIAMETER*prop/2.0)-1)));

				g.drawLine((int)(rightwheelcenter.x+FastMath.sin(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( rightwheelcenter.y-FastMath.cos(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)),(int)(rightwheelcenter.x+FastMath.sin(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( rightwheelcenter.y-FastMath.cos(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)));				
				g.drawLine((int)(rightwheelcenter2.x+FastMath.sin(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( rightwheelcenter2.y-FastMath.cos(dir)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)),(int)(rightwheelcenter2.x+FastMath.sin(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)), (int)( rightwheelcenter2.y-FastMath.cos(dir+3.14)*(Robot.ROBOT_WHEEL_DIAMETER*prop/2.0)));
				
				//draw light sensor range
				g.setColor(Color.ORANGE);
				
				g.drawLine (leftwheelcenter.x,leftwheelcenter.y,(int)(leftwheelcenter.x+FastMath.sin(dir-FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)),(int)( leftwheelcenter.y-FastMath.cos(dir-FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)));
				g.drawLine (leftwheelcenter.x,leftwheelcenter.y,(int)(leftwheelcenter.x+FastMath.sin(dir+FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)),(int)( leftwheelcenter.y-FastMath.cos(dir+FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)));
				g.drawArc((int)(leftwheelcenter.x-Robot.LIGHT_SENSOR_RANGE*prop), (int)(leftwheelcenter.y-Robot.LIGHT_SENSOR_RANGE*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(-master.robot.getBodyDirection()+90-(Robot.LIGHT_SENSOR_ANGLE/2)), (int)(Robot.LIGHT_SENSOR_ANGLE));
				
				g.drawLine (rightwheelcenter.x,rightwheelcenter.y,(int)(rightwheelcenter.x+FastMath.sin(dir+FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)),(int)( rightwheelcenter.y-FastMath.cos(dir+FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)));
				g.drawLine (rightwheelcenter.x,rightwheelcenter.y,(int)(rightwheelcenter.x+FastMath.sin(dir-FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)),(int)( rightwheelcenter.y-FastMath.cos(dir-FastMath.toRadians(Robot.LIGHT_SENSOR_ANGLE/2))*(Robot.LIGHT_SENSOR_RANGE*prop)));
				g.drawArc((int)(rightwheelcenter.x-Robot.LIGHT_SENSOR_RANGE*prop), (int)(rightwheelcenter.y-Robot.LIGHT_SENSOR_RANGE*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(-master.robot.getBodyDirection()+90-(Robot.LIGHT_SENSOR_ANGLE/2)), (int)(Robot.LIGHT_SENSOR_ANGLE));
				
				//highlight sensor if active
				if (master.robot.getController().sensorvalues[0] != 0)
				g.fillArc((int)(leftwheelcenter.x-Robot.LIGHT_SENSOR_RANGE*prop), (int)(leftwheelcenter.y-Robot.LIGHT_SENSOR_RANGE*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(-master.robot.getBodyDirection()+90-(Robot.LIGHT_SENSOR_ANGLE/2)), (int)(Robot.LIGHT_SENSOR_ANGLE));
				if (master.robot.getController().sensorvalues[1] != 0) {
					g.fillArc((int)(rightwheelcenter.x-Robot.LIGHT_SENSOR_RANGE*prop), (int)(rightwheelcenter.y-Robot.LIGHT_SENSOR_RANGE*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(Robot.LIGHT_SENSOR_RANGE*2*prop), (int)(-master.robot.getBodyDirection()+90-(Robot.LIGHT_SENSOR_ANGLE/2)), (int)(Robot.LIGHT_SENSOR_ANGLE));
				}
				
			}
			//draw the step number to lower left corner
			g.setColor(Color.BLACK);
			g.drawString("Step: "+master.maxsteps+"/"+Integer.toString(master.currentstep), 15, u-5);
			// TODO: write fitness specific information as well
		}
		
		public double getGridCellSize(){
			double prop = calcResizeProp();
			return master.getDisplayGridSize() * prop;
		}
		public void drawGridLines(Graphics g, double prop){
			g.setColor(Color.gray);
			
			for(int i = 0; i + 10 < SimServer.FIELDDIM * prop; i+= master.getDisplayGridSize() * prop)
			{
				g.drawLine(i + 10, 10, i +10, (int) (SimServer.FIELDDIM * prop));								
				g.drawLine(10, i + 10, (int) (SimServer.FIELDDIM * prop), i + 10);								
			}
		}
		
		// implements double buffering to avoid flickers
	    public void update(Graphics g) {
	    	Graphics offgc;
	    	Image offscreen = null;
	    	Dimension d = getSize();

	    	// create the offscreen buffer and associated Graphics
	    	offscreen = createImage(d.width, d.height);
	    	offgc = offscreen.getGraphics();
	    	// clear the exposed area
	    	offgc.setColor(getBackground());
	    	offgc.fillRect(0, 0, d.width, d.height);
	    	offgc.setColor(getForeground());
	    	// do normal redraw
	    	paint(offgc);
	    	// transfer offscreen to window
	    	g.drawImage(offscreen, 0, 0, this);
	    }
		 
	}
	
	public void setWithPause(boolean bool) {
		master.withpause = bool;
	}
	
}
