package nnracer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.FrevoMain;

public class RacerDisplay extends JFrame {

	private static final long serialVersionUID = 3173908436083775602L;
	private static final int ROAD_WIDTH = 60;
	private static final int ROAD_HEIGHT = 75;
	private static final int PADDING = 30;
	
	private GCanvas canvas;
	private NNRacer parent;
	
	private final Point roadsize; 

	private final Color asphalt = new Color(30, 30, 30);
	
	private BufferedImage racercar;

	public RacerDisplay(NNRacer parent) {
		super("NNRacer simulation");

		this.parent = parent;
		
		roadsize = new Point(ROAD_WIDTH*parent.lanenum*2,ROAD_HEIGHT*(1+parent.lookahead)*2);
		
		try {
			racercar = ImageIO.read(new File(FrevoMain.getInstallDirectory()+"//Components//Problems//nnracer//nnracer//F1-car.png"));
		} catch (IOException e) {
			this.dispose();
			e.printStackTrace();
		}

		setBounds(0, 0, roadsize.x + PADDING,
				roadsize.y + PADDING);// set frame
		//setMinimumSize(new Dimension(roadsize.x,
		//		roadsize.y));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.setLocationRelativeTo(null);
		canvas = new GCanvas();
		add(canvas);
		setVisible(true);
	}

	public void updateDisplay() {
		canvas.repaint();
		this.repaint();
	}

	class GCanvas extends JPanel {
		public GCanvas() {
			setBackground(asphalt);
			setDoubleBuffered(true);
		}

		private static final long serialVersionUID = -8748735749891520154L;

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			
			int w = this.getWidth() - PADDING;
			int h = this.getHeight() - PADDING;
			
			float scale = 0;
			if (((float) w / (float) roadsize.x) < ((float) h / (float) roadsize.y)) {
				scale = (float) w / (float) roadsize.x;
				h = (int) (scale * roadsize.y);
			} else {
				scale = (float) h / (float) roadsize.y;
				w = (int) (scale * roadsize.x);
			}
			
			g.setColor(Color.GRAY);
			g2d.drawRect(PADDING/2, PADDING/2, (int)(scale*roadsize.x), (int)(scale*roadsize.y));
			
			// paint lanes
			g.setColor(Color.WHITE);
			float[] dashline = { 15f*scale, 10f*scale };
			BasicStroke bs1 = new BasicStroke((int)(5*scale), 
			        BasicStroke.CAP_BUTT, 
			        BasicStroke.JOIN_ROUND, 
			        1.0f, 
			        dashline,
			        2f);
			g2d.setStroke(bs1);

			int lanew = (int)(scale*roadsize.x)/parent.lanenum;
			int laneh = (int)(scale*roadsize.y)/(parent.lookahead+1);
			
			int carw = (int)(lanew*0.8f);
			int carh = (int)(laneh*0.8f);
			
			for (int l=1;l<parent.lanenum;l++)
				g2d.drawLine(PADDING/2+lanew*l, PADDING/2, PADDING/2+lanew*l, PADDING/2+(int)(scale*roadsize.y));

			//draw own car
			Point carlocation = getCarLocation(1, lanew, laneh, scale, carw, carh);
			g.drawImage(racercar, carlocation.x,  carlocation.y, carw, carh, null);
			
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}
		
		private Point getCarLocation (int pos, int lanew, int laneh, float scale, int carw, int carh) {
			Point res = new Point ();
			res.x = PADDING/2 + pos*lanew + ((lanew-carw)/2);
			res.y = PADDING/2 + (int)(scale*roadsize.y) / (parent.lookahead+1) * parent.lookahead + ((laneh-carh)/2);
			return res;
		}
	}

}
