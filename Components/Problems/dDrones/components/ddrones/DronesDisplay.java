package components.ddrones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DronesDisplay extends JFrame {

	private static final long serialVersionUID = -3845311699398981321L;
	private static final int multipl = 2;
	private final Color bgColor = Color.WHITE;
	private GCanvas canvas;
	private int w;
	private int h;
	private static int Relwidth;
	private static int Relheight;
	
	private ddrones master;
	
	public DronesDisplay(ddrones master) {
		super("UAV grid simulator");
	    this.master = master;
	    w=ddrones.GRIDWIDTH;
	    h=ddrones.GRIDHEIGHT;
	    Relwidth=10*w*multipl;
	    Relheight=10*h*multipl;
	    setBounds(0,0,Relwidth+10,Relheight+50);// set frame
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    this.setLocationRelativeTo(null);
	    canvas = new GCanvas();
	    add(canvas);
	    setVisible(true);
	    
	}
	public void updateDisplay() {
		canvas.repaint();
	}
	
	class GCanvas extends JPanel
	{
		public GCanvas() {
			setBackground(bgColor);
			setDoubleBuffered(true);
		}
		private static final long serialVersionUID = -8748735749891520154L;
		
		public void paint(Graphics g) {
			super.paint(g);

			//draw field grid
			g.setColor(new Color(27,142,40));
			for (int i=0;i<w+1;i++) {
				g.drawLine(10*i*multipl, 0*multipl, 10*i*multipl, h*10*multipl);
			}
			for (int i=0;i<h+1;i++) {
				g.drawLine(0*multipl, 10*i*multipl, w*10*multipl, 10*i*multipl);
			}
			
			//draw obstacles
			int[][] ogrid = master.fieldgrid;
			
			if (ogrid != null) {
				for (int x = 0;x<ogrid.length;x++) {
					for (int y = 0;y<ogrid[0].length;y++) {
						if (ogrid[x][y] == ddrones.BLOCKED) {
							g.setColor(Color.BLACK);
							g.fillRect((x)*10*multipl,(y)*10*multipl, 10*multipl, 10*multipl);
						}
					}
				}
			}
			

			//fill visited zones			
			boolean[][] vgrid = master.visitedgrid;
			if (vgrid != null) {
				for (int x = 0;x<vgrid.length;x++) {
					for (int y = 0;y<vgrid[0].length;y++) {
						if (vgrid[x][y]) {
							g.setColor(Color.GRAY);
							g.fillRect((x)*10*multipl,(y)*10*multipl, 10*multipl, 10*multipl);
						}
					}
				}
			}
			
						
			//draw drones
			g.setColor(Color.BLUE);
			for (int d=0;d<ddrones.DRONENUMBER;d++) {
				if (master.drones[d] != null)
				g.fillOval((int)(master.drones[d].getPosition().getX())*multipl*10+5, (int)(master.drones[d].getPosition().getY())*multipl*10+5, 10, 10);
			}		
			
			//draw the step number to lower left corner
			g.setColor(Color.BLACK);
			g.drawString("Step: "+Integer.toString(master.aktStep+1)+"/"+master.stepnumber, 0, Relheight+15);
			
			Toolkit.getDefaultToolkit().sync();
	        g.dispose();
		}
	}
}
