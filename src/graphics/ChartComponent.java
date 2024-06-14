package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import net.jodk.lang.FastMath;
import utils.StatKeeper;

/** A graphical component that displays 2d chart data */
public class ChartComponent extends JPanel {
	private static final long serialVersionUID = 2421509969118542059L;

	private static final Color LINECOLOR = Color.green.darker();
	private static final Color POINTCOLOR = Color.red;

	ArrayList<Double> data;
	final int PADDING = 20;

	private ArrayList<Point> displayeddatapoints = new ArrayList<Point>();
	
	public final String ordinate;
	public final String abscissa;

	public ChartComponent(String namex, String namey, ArrayList<Double> data) {
		ordinate = namey;
		abscissa = namex;
		this.data = data;
		setToolTipText("");
		ToolTipManager.sharedInstance().setInitialDelay(0);
	}

	public ChartComponent(StatKeeper statkeeper) {
		ordinate = statkeeper.getValuesName();
		abscissa = statkeeper.getStatName();
		data = statkeeper.getValues();
		setToolTipText("");
		ToolTipManager.sharedInstance().setInitialDelay(0);
	}
	
	public String getToolTipText(MouseEvent e)
    {
        //get nearest point
		if (displayeddatapoints.size() == 0) return null;
		
		Point npoint = displayeddatapoints.get(0);
		int nearest = 0;
		double nd = FastMath.sqrt( (npoint.x - e.getX())*(npoint.x - e.getX()) + ((npoint.y - e.getY())*(npoint.y - e.getY())) );
		
		for (int i=1;i<displayeddatapoints.size();i++ ) {
			Point p = displayeddatapoints.get(i);
			double dist = FastMath.sqrt( (p.x - e.getX())*(p.x - e.getX()) + ((p.y - e.getY())*(p.y - e.getY())) );
			if (dist < nd) {
				nearest = i;
				npoint = p;
				nd = dist;
			}
		}
		
		if (nd > 5) return null;
		return data.get(nearest).toString();
    }
	
	public Point getToolTipLocation(MouseEvent e)
    {
        Point p = e.getPoint();
        p.y += 20;
        p.x += 5;
        return p;
    }

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int w = getWidth();
		int h = getHeight();

		// Draw y axis.
		g2.draw(new Line2D.Double(PADDING, PADDING, PADDING, h - PADDING));

		// Draw x axis.
		g2.draw(new Line2D.Double(PADDING, h - PADDING, w - PADDING, h
				- PADDING));

		// Draw labels.
		Font font = g2.getFont();
		FontRenderContext frc = g2.getFontRenderContext();
		LineMetrics lm = font.getLineMetrics("0", frc);
		float sh = lm.getAscent() + lm.getDescent();

		// Y axis label.
		float sy = PADDING + ((h - 2 * PADDING) - abscissa.length() * sh) / 2
				+ lm.getAscent();
		for (int i = 0; i < abscissa.length(); i++) {
			String letter = String.valueOf(abscissa.charAt(i));
			float sw = (float) font.getStringBounds(letter, frc).getWidth();
			float sx = (PADDING - sw) / 2;
			g2.drawString(letter, sx, sy);
			sy += sh;
		}

		// Abscissa label.
		sy = h - PADDING + (PADDING - sh) / 2 + lm.getAscent();
		float sw = (float) font.getStringBounds(ordinate, frc).getWidth();
		float sx = (w - sw) / 2;
		g2.drawString(ordinate, sx, sy);

		if (data.size() != 0) {
			// Draw lines.
			double xInc = (double) (w - 2 * PADDING) / (data.size() - 1);
			double scale = (h - 2 * PADDING) / getMax();
			g2.setPaint(LINECOLOR);
			for (int i = 0; i < data.size() - 1; i++) {
				double x1 = PADDING + i * xInc;
				double y1 = h - PADDING - scale * data.get(i);
				double x2 = PADDING + (i + 1) * xInc;
				double y2 = h - PADDING - scale * data.get(i + 1);
				g2.draw(new Line2D.Double(x1, y1, x2, y2));
			}

			// Mark data points.
			displayeddatapoints.clear();
			g2.setPaint(POINTCOLOR);
			for (int i = 0; i < data.size(); i++) {
				double x = PADDING + i * xInc;
				double y = h - PADDING - scale * data.get(i);
				g2.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
				displayeddatapoints.add(new Point((int)x,(int)y));
			}

		}

	}

	private double getMax() {
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) > max)
				max = data.get(i);
		}
		return max;
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//simple test case
		double[] data = { 21, 14, 18, 03, 86, 88, 74, 87, 54, 77, 61, 55, 48, 60,
				49, 36, 38, 27, 20, 18 };
		
		//double[] data = { 10, 20 };
		
		ArrayList<Double> alist = new ArrayList<Double>(data.length);
		for (int i=0;i<data.length;i++)
			alist.add(data[i]);

		f.add(new ChartComponent("x", "y", alist));
		f.setSize(400, 400);
		f.setLocation(200, 200);
		f.setVisible(true);
	}

}
