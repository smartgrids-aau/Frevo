package spiderinosim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;




/**
 * Displays the Simulation.
 * This class and the data it accesses probably isn't thread safe.
 * 
 * TODO: Fix thread safety issues!
 */
public class SimulationDisplay extends JFrame implements ChangeListener {

	
	public interface SpeedListener {
		void speedChanged(double speed);
	}
	
	private static final long serialVersionUID = -4526636674872509085L;

	static final double RADIANS_PER_DEGREE = Math.PI / 180;
	
	protected Simulation simulation;
	protected SimulationPanel panel;
	protected JPanel controlPanel;
	protected JButton saveButton;
	protected JSlider speedSlider;
	protected SpeedListener speedListener;
		
	public SimulationDisplay(Simulation simulation, SpeedListener speedListener) {
		this.simulation = simulation;
		this.speedListener = speedListener;
		Container container = getContentPane();
		panel = new SimulationPanel(); 
		container.add(panel, BorderLayout.CENTER);
		controlPanel = new JPanel();
		container.add(controlPanel, BorderLayout.SOUTH);
		saveButton = new JButton("Save");
		controlPanel.add(saveButton, BorderLayout.EAST);
		speedSlider = new JSlider(JSlider.HORIZONTAL, -4, 4, 0);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setMajorTickSpacing(4);
		speedSlider.addChangeListener(this);
		controlPanel.add(speedSlider, BorderLayout.WEST); 
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDisplay();
			}
		});
		
		setSize(400,400);
		setPreferredSize(new Dimension(400, 400));
		pack();
		setVisible(true); 
	}

	
	/**
	 * Template for exporting SVG file
	 */
	protected void saveDisplay() {
		try {
			World world = simulation.getWorld();
			double thickness = 2;
			File tempFile = File.createTempFile("world", ".svg");
			System.out.println("Save Display:" + tempFile.getAbsolutePath());
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));
			writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" + 
					"<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\">");
			
			// draw world
			writer.write("<rect x=\"0\" y=\"0\" width=\"" + world.getWidth() * 100 + "\" height=\""+ world.getHeight() * 100 + "\" stroke=\"#765373\" stroke-width=\"" + thickness + "\" fill=\"none\"/>");

			// draw objects
			for (WObject o : world.getObjects()) {
				double x = o.getX();
				double y = o.getY();
				double radius = o.getRadius();

				String color = "#7AA20D";
				if (o instanceof Spiderino) {
					color = "#7AA20D";
					
					// draw a line showing rotation
					double rotation = ((Spiderino)o).getRotation();				
					double dX = radius * Math.cos(rotation * RADIANS_PER_DEGREE);
					double dY = radius * Math.sin(rotation * RADIANS_PER_DEGREE);
					writer.write("<line x1=\"" + (x + dX) * 100 + "\" y1=\""+ + (y + dY) * 100 + "\" x2=\"" + (x + dX * 1.7) * 100 + "\" y2=\"" + (y + dY * 1.7) * 100 + "\" stroke=\"" + color + "\" stroke-width=\"" + thickness + "\"/>");

					dX = radius * Math.cos((rotation + 45) * RADIANS_PER_DEGREE);
					dY = radius * Math.sin((rotation + 45)* RADIANS_PER_DEGREE);
					writer.write("<line x1=\"" + (x + dX) * 100 + "\" y1=\""+ + (y + dY) * 100 + "\" x2=\"" + (x + dX * 1.3) * 100 + "\" y2=\"" + (y + dY * 1.3) * 100 + "\" stroke=\"" + color + "\" stroke-width=\"" + thickness + "\"/>");

					dX = radius * Math.cos((rotation - 45) * RADIANS_PER_DEGREE);
					dY = radius * Math.sin((rotation - 45)* RADIANS_PER_DEGREE);
					writer.write("<line x1=\"" + (x + dX) * 100 + "\" y1=\""+ + (y + dY) * 100 + "\" x2=\"" + (x + dX * 1.3) * 100 + "\" y2=\"" + (y + dY * 1.3) * 100 + "\" stroke=\"" + color + "\" stroke-width=\"" + thickness + "\"/>");

					
				} else if (o instanceof Light) {
					color = "#ef9235";
				}
				
				writer.write("<circle cx=\""+ x * 100 +"\" cy=\""+ y * 100 + "\" r=\""+ radius * 100 +"\" fill=\"" + color + "\" stroke=\"none\"/>");				
			}
		
			writer.write("</svg>");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void updateDisplay(){
		this.repaint();
		panel.repaint();
	}

	class SimulationPanel extends JPanel {

		private static final long serialVersionUID = -4107593896442925509L;

		protected double scale;
		
		public SimulationPanel() {
			addComponentListener(new ResizeListener());
		}
		
		
		
		public void paint(Graphics g) {
	
			World world = simulation.getWorld();
			
			// draw world
			double worldWidth = world.getWidth();
			double worldHeight = world.getHeight();
			g.setColor(Color.LIGHT_GRAY);			
			g.drawRect(0, 0, toPixels(worldWidth), toPixels(worldHeight));
			
			for (WObject o : world.getObjects()) {
				double x = o.getX();
				double y = o.getY();
				double radius = o.getRadius();

				if (o instanceof Spiderino) {
					g.setColor(Color.BLUE);
					
					// draw a line showing rotation
					double rotation = ((Spiderino)o).getRotation();				
					double dX = radius * Math.cos(rotation * RADIANS_PER_DEGREE );
					double dY = radius * Math.sin(rotation * RADIANS_PER_DEGREE);
					g.drawLine(toPixels(x + dX), toPixels(y + dY), toPixels(x + dX * 1.5), toPixels(y + dY * 1.5));
				} else if (o instanceof Light) {
					g.setColor(Color.YELLOW);
				}
				
				g.fillOval(toPixels(x - radius), toPixels(y - radius), toPixels(2 * radius), toPixels(2 * radius));
			}
		}
	
		
		protected int toPixels(double v) {
			return (int)(scale * v);
		}
		
		
		class ResizeListener extends ComponentAdapter {
	        public void componentResized(ComponentEvent e) {
	        	int width = getWidth();
				int height = getHeight();
				
				double worldWidth = simulation.getWorld().getWidth();
				double worldHeight = simulation.getWorld().getHeight();
				
				double windowRatio = ((double) width) / height;
				double worldRatio = worldWidth / worldHeight;							
				if (windowRatio > worldRatio) {
					scale = height / worldHeight;
				} else {
					scale = width / worldWidth;
				}											
	        }
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == speedSlider) {
			double speed = Math.pow(2, speedSlider.getValue());
			if (speedListener != null)
				speedListener.speedChanged(speed);
		}
	}	
}
