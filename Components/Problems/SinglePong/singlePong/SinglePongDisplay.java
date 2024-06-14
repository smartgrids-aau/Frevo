package singlePong;

import java.awt.AWTException;
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
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import utils.ScreenCapture;
import main.FrevoMain;

public class SinglePongDisplay extends JFrame implements WindowListener {
	
	/** Color of the field (grass) */
	private final Color FIELDCOLOR = Color.BLACK; 
	private final Color LINECOLOR = Color.WHITE;
	
	/** Width of the lines painted on the field */
	private final double LINE_WIDTH = 0.01;
	
	private static final long serialVersionUID = 4887770953016314765L;
	
	private JPanel menuPanel;
	private DrawPanel canvasPanel;

	protected JButton startButton;
	protected JButton stopButton;
	private Icon playIcon;
	private Icon stopIcon;
	protected JCheckBox saveFramesCheckbox;
	protected JCheckBox debugCheckbox;

	private DisplayWorker workerThread;
	private SinglePongParameters parameters;
	private SinglePongState state;
	private SinglePongServer server = null;
	

	SinglePongDisplay(SinglePongParameters parameters, SinglePongState state) {
		super("Pong Game");
		
		this.parameters = parameters;
		this.state = state;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0, 0, 620, 550);// set initial frame
		Container con = this.getContentPane(); // inherit main frame
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));

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
		stopButton.setEnabled(false);
		menuPanel.add(stopButton);
		
		saveFramesCheckbox = new JCheckBox("Save frames");
		menuPanel.add(saveFramesCheckbox);
		
		debugCheckbox = new JCheckBox("Debug");
		menuPanel.add(debugCheckbox);
		

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
				stopButton.setEnabled(true);
				debugCheckbox.setEnabled(false);
				saveFramesCheckbox.setEnabled(false);
				SinglePongParameters parameters = canvasPanel.parameters;
				if (parameters != null) {
					parameters.setDebugging(debugCheckbox.isSelected());
					parameters.setSaveFrames(saveFramesCheckbox.isSelected());
				}
				workerThread = new DisplayWorker();
				workerThread.execute();				
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				debugCheckbox.setEnabled(true);
				saveFramesCheckbox.setEnabled(true);
				workerThread.stopSimulation();
			}
		});

		con.add(menuPanel);

		canvasPanel = new DrawPanel(parameters, state);

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
		private Point2D.Double bottomrightcorner = new Point2D.Double();
		private SinglePongParameters parameters;
		private SinglePongState state;
		
		private int[][] DIGITS = new int[][] {
				 	{1, 1, 1, 0, 1, 1, 1}, // 0
				 	{0, 0, 1, 0, 0, 1, 0}, // 1
				 	{1, 0, 1, 1, 1, 0, 1}, // 2
				 	{1, 0, 1, 1, 0, 1, 1}, // 3
				 	{0, 1, 1, 1, 0, 1, 0}, // 4
				 	{1, 1, 0, 1, 0, 1, 1}, // 5
				 	{1, 1, 0, 1, 1, 1, 1}, // 6
				 	{1, 0, 1, 0, 0, 1, 0}, // 7
				 	{1, 1, 1, 1, 1, 1, 1}, // 8
				 	{1, 1, 1, 1, 0, 1, 0}  // 9
				};
		
		DrawPanel(SinglePongParameters parameters, SinglePongState state){
			this.parameters = parameters;
			this.state = state;
		}

		
		private String fix(int num) {
			String ret = Integer.toString(num);
			if (num < 10) ret = "00"+ret;
			else if (num < 100) ret = "0"+ret;
			return ret;
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			
			// calculate scaling property
			int w = getWidth() - 20;
			int h = getHeight() - 20;// leave some border, draw inside

			double scale = 0;
			if (((double) w / parameters.getWidth()) < ((double) h / parameters.getHeight())) {
				scale = (double) w / parameters.getWidth();
				h = (int) (scale * parameters.getHeight());
			} else {
				scale = (double) h / parameters.getHeight();
				w = (int) (scale * parameters.getWidth());
			}

			double scaledwidth = scale * parameters.getWidth();
			double scaledheight = scale * parameters.getHeight();

			bottomrightcorner.x = topleftcorner.x + scaledwidth;
			bottomrightcorner.y = topleftcorner.y + scaledheight;

			// draw field boundary
			g.setColor(LINECOLOR);
			g2.setStroke(new BasicStroke((int) (LINE_WIDTH * scale)));
			
			if (server == null)
				return;
				
			// draw top wall
			int scaledWallWidth = (int)  (parameters.getWallWidth()*scale);
			int x = (int) (topleftcorner.x);
			int y = (int) (topleftcorner.y);
			int dx = (int) (parameters.getWidth() * scale );
			int dy = scaledWallWidth;
			g2.fillRect(x ,	y, dx, dy);
		
			// draw bottom wall
			x = (int) (topleftcorner.x);
			y = (int) (bottomrightcorner.y - scaledWallWidth);
			g2.fillRect(x ,	y, dx, dy);
			
			x = (int) (topleftcorner.x + parameters.getWidth() * scale); 
			g2.drawLine(x, (int) (topleftcorner.y), x, (int) (bottomrightcorner.y));
			
			// draw score
			int sw = (int)(3*scaledWallWidth);
			int sh = (int)(4*scaledWallWidth);
			int dh = (int)(scaledWallWidth*4/5);
			
		    drawDigits(g2, state.getScore(), 
		    	(int)(topleftcorner.x + (0.5 + (parameters.getWidth()/2) - 1.5*parameters.getWallWidth())* scale - sw) , 
		    	(int)(topleftcorner.y+ 2*scaledWallWidth), 
		    	sw, sh, dh);			    
			
			//draw time left
			int lineX = (int)(topleftcorner.x + (bottomrightcorner.x - topleftcorner.x )/2 - scaledWallWidth/4);
			int lineY = (int)topleftcorner.y + scaledWallWidth;
			int lineWidth =(int)(scaledWallWidth/2);
			int lineHeight = (int)(( bottomrightcorner.y - topleftcorner.y - 2*scaledWallWidth) * 
					(parameters.getMaximumSteps() - state.getActualStep())/parameters.getMaximumSteps());				 
			g2.fillRect(lineX, lineY,
					lineWidth, lineHeight);
		    
			// draw paddles
			for (Paddle paddle : state.getTeam())
			{
				x = (int)(topleftcorner.x + paddle.getX()*scale);
				y = (int)(topleftcorner.y + paddle.getY()*scale);
				dx = (int) (parameters.getPaddleWidth() * scale );
				dy = (int) ( parameters.getPaddleHeight()*scale);
				g2.fillRect(x ,	y, dx, dy);		
			}
			
			// draw balls
			for (Ball ball : state.getBalls())
			{
				x = (int)(topleftcorner.x + (ball.getX() - parameters.getBallRadius())*scale);
				y = (int)(topleftcorner.y + (ball.getY() - parameters.getBallRadius())*scale);
				dx = (int) (2*parameters.getBallRadius() * scale );
				dy = (int) (2*parameters.getBallRadius() * scale);
				g2.fillRect(x,	y, dx, dy);
				
				int hist = ball.getPrevX().size();
				for (int i=0; i<ball.getPrevX().size(); i++) {
					x = (int)(topleftcorner.x + (ball.getPrevX().get(i) - parameters.getBallRadius()/(hist-i + 1))*scale);
					y = (int)(topleftcorner.y + (ball.getPrevY().get(i) - parameters.getBallRadius()/(hist-i + 1))*scale);
					dx = (int) (2*parameters.getBallRadius() * scale / (hist-i + 1) );
					dy = (int) (2*parameters.getBallRadius() * scale / (hist-i + 1));
					g2.drawRect(x,	y, dx, dy);
				}
			}
			
			if (parameters.isDebugging()) 
			{
				for (Paddle paddle : state.getTeam())
				{
					ArrayList<String> text = new ArrayList<String>(); 
					//paddle.getSensorPosition()
					text.add("Y: " + paddle.getSensorPosition().get(0));

					//paddle.getSensorTeammate()
					text.add("Teammate: " + paddle.getSensorTeammate().get(0));
					
					//paddle.getSensorBall()
					//text.add("Ball X: " + paddle.getSensorBall().get(0));
					text.add("Ball Y: " + paddle.getSensorBall().get(0));
					text.add("---------------------");
					
					//paddle.getUp()
					text.add("Up: " + paddle.getUp());
					//paddle.getDown()
					text.add("Down: " + paddle.getDown());
											
					for (int i=1; i<= text.size(); i++)
					{
						g2.drawString(text.get(i-1), (float)(topleftcorner.x + 20), 
								(float)(topleftcorner.y + scaledWallWidth + i*20));
					}
					
					break;
				}
			}					
			
			if (parameters.isSaveFrames()) {
				try {
					ScreenCapture.createImage(this, parameters.SAVE_PATH +fix(state.getActualStep())+".bmp");
				} catch (AWTException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}				
		}

		void drawDigits(Graphics2D g, int n, int x, int y, int w, int h, int dh) {
			if (n>9)
				return;
			int dw = dh;
			int[] blocks = DIGITS[n];
			if (blocks[0] == 1)
		        g.fillRect(x, y, w, dh);
		    if (blocks[1] == 1)
		        g.fillRect(x, y, dw, h/2);
		    if (blocks[2] == 1)
		        g.fillRect(x+w-dw, y, dw, h/2);
		    if (blocks[3] == 1)
		        g.fillRect(x, y + h/2 - dh/2, w, dh);
		    if (blocks[4] == 1)
		        g.fillRect(x, y + h/2, dw, h/2);
		    if (blocks[5] == 1)
		        g.fillRect(x+w-dw, y + h/2, dw, h/2);
		    if (blocks[6] == 1)
		        g.fillRect(x, y+h-dh, w, dh);
		}
	}
	
	private class DisplayWorker extends SwingWorker<Void, Integer> {

		@Override
		protected Void doInBackground() throws Exception {

			// create new server
			server = new SinglePongServer(parameters,state);

			// run simulation without display
			server.runSimulation(canvasPanel);
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
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}


