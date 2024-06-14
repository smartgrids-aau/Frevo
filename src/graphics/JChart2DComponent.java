package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.metal.MetalLookAndFeel;

import utils.StatKeeper;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.util.Range;

/**
 * JChart2DComponent V 0.1 2012/07/24
 * 
 * The JChart2DComponent class provides easy access to the Library JChart2D
 * which is used to display charts. You only have to define the number of traces
 * and add the StatKeeper objects which contain the data that should be
 * displayed. Neither the StatKeeper objects nor the data in the StatKeeper
 * objects have to be added all at once. They can be added by time. But you can
 * not add more StatKeeper objects than the number of traces
 * 
 * @author Thomas Dittrich
 */
public class JChart2DComponent extends Chart2D {
	private static final long serialVersionUID = 4400587971377717202L;
	private Trace2DSimple[] traces;
	private StatKeeper[] stats;
	private int[] ilastaddedpoint;
	private int numberoftraces;
	private int inext;
	private double mindisplayedval = Double.MAX_VALUE;
	private double maxdisplayedval = -Double.MAX_VALUE;

	/**
	 * Creates a new Chart which already contains all the traces that should be
	 * displayed. At the beginning none of the traces are visible because there
	 * is no data provided for them. The data for the traces should be added
	 * with the method addstatkeeper. For every trace there should be one
	 * StatKeeper object.
	 * 
	 * @param numberoftraces
	 *            the number of traces that should be displayed
	 */
	public JChart2DComponent(int numberoftraces) {
		super();
		this.numberoftraces = numberoftraces;
		inext = 0;
		traces = new Trace2DSimple[this.numberoftraces];
		stats = new StatKeeper[this.numberoftraces];
		ilastaddedpoint = new int[this.numberoftraces];
		for (int i = 0; i < traces.length; i++) {
			ilastaddedpoint[i] = -1;
			traces[i] = new Trace2DSimple("run " + i);
			traces[i].setStroke(new BasicStroke(3f));
			this.addTrace(traces[i]);
			int r = 0;
			int g = 0;
			int b = 0;
			if (i < Math.ceil(traces.length / 6.0)) {
				int ncolorsincase = (int) Math.ceil(traces.length / 6.0);
				r = 255;
				g = 255 / ncolorsincase * i;
				b = 0;
			} else if (i < Math.ceil(2.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(2.0 * traces.length / 6.0) - Math
						.ceil(traces.length / 6.0));
				r = (int) (255 - 255 / ncolorsincase
						* (i - Math.ceil(traces.length / 6.0)));
				g = 255;
				b = 0;
			} else if (i < Math.ceil(3.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(3.0 * traces.length / 6.0) - Math
						.ceil(2.0 * traces.length / 6.0));
				r = 0;
				g = 255;
				b = (int) (255 / ncolorsincase * (i - Math
						.ceil(2.0 * traces.length / 6.0)));
			} else if (i < Math.ceil(4.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(4.0 * traces.length / 6.0) - Math
						.ceil(3.0 * traces.length / 6.0));
				r = 0;
				g = (int) (255 - 255 / ncolorsincase
						* (i - Math.ceil(3.0 * traces.length / 6.0)));
				b = 255;
			} else if (i < Math.ceil(5.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(5.0 * traces.length / 6.0) - Math
						.ceil(4.0 * traces.length / 6.0));
				r = (int) (255 / ncolorsincase * (i - Math
						.ceil(4.0 * traces.length / 6.0)));
				g = 0;
				b = 255;
			} else {
				int ncolorsincase = (int) (Math.ceil(6.0 * traces.length / 6.0) - Math
						.ceil(5.0 * traces.length / 6.0));
				r = 255;
				g = 0;
				b = (int) (255 - 255 / ncolorsincase
						* (i - Math.ceil(5.0 * traces.length / 6.0)));
			}
			traces[i].setColor(new Color(r, g, b));
		}
		this.setBackground(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getWhite());
		this.setFont(new Font(null, Font.PLAIN, 9));
		this.getAxisY().setMinorTickSpacing(0.1);
		this.getAxisY().setMinorTickSpacing(1.0);
		this.getAxisY().setRangePolicy(
				new RangePolicyFixedViewport(new Range(-10, 10)));
		this.getAxisX().setMinorTickSpacing(10.0);
		this.getAxisX().setMajorTickSpacing(10.0);
	}

	/**
	 * Creates a new chart that does not contain any traces. You have to
	 * configure the Chart by means of the function configureChart and then add
	 * the data sources by means of the funktion addStatKeeper.
	 */
	public JChart2DComponent() {
		super();
		this.setBackground(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getWhite());
		this.setFont(new Font(null, Font.PLAIN, 9));
		this.getAxisY().setMinorTickSpacing(0.1);
		this.getAxisY().setMinorTickSpacing(1.0);
		this.getAxisY().setRangePolicy(
				new RangePolicyUnbounded(new Range(-10, 10)));
	}

	/**
	 * Configures the Chart. i.e. resets the chart if it already contains traces
	 * and adds a certain number of new traces. The data sources for the traces
	 * have to be added by means of the function addStatKeeper.
	 * 
	 * @param numberoftraces the number of traces that are added
	 */
	public void configureChart(int numberoftraces) {
		this.numberoftraces = numberoftraces;
		inext = 0;
		traces = new Trace2DSimple[this.numberoftraces];
		stats = new StatKeeper[this.numberoftraces];
		ilastaddedpoint = new int[this.numberoftraces];
		for (int i = 0; i < traces.length; i++) {
			ilastaddedpoint[i] = -1;
			traces[i] = new Trace2DSimple("run " + i);
			traces[i].setStroke(new BasicStroke(3f));
			this.addTrace(traces[i]);
			int r = 0;
			int g = 0;
			int b = 0;
			if (i < Math.ceil(traces.length / 6.0)) {
				int ncolorsincase = (int) Math.ceil(traces.length / 6.0);
				r = 255;
				g = 255 / ncolorsincase * i;
				b = 0;
			} else if (i < Math.ceil(2.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(2.0 * traces.length / 6.0) - Math
						.ceil(traces.length / 6.0));
				r = (int) (255 - 255 / ncolorsincase
						* (i - Math.ceil(traces.length / 6.0)));
				g = 255;
				b = 0;
			} else if (i < Math.ceil(3.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(3.0 * traces.length / 6.0) - Math
						.ceil(2.0 * traces.length / 6.0));
				r = 0;
				g = 255;
				b = (int) (255 / ncolorsincase * (i - Math
						.ceil(2.0 * traces.length / 6.0)));
			} else if (i < Math.ceil(4.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(4.0 * traces.length / 6.0) - Math
						.ceil(3.0 * traces.length / 6.0));
				r = 0;
				g = (int) (255 - 255 / ncolorsincase
						* (i - Math.ceil(3.0 * traces.length / 6.0)));
				b = 255;
			} else if (i < Math.ceil(5.0 * traces.length / 6.0)) {
				int ncolorsincase = (int) (Math.ceil(5.0 * traces.length / 6.0) - Math
						.ceil(4.0 * traces.length / 6.0));
				r = (int) (255 / ncolorsincase * (i - Math
						.ceil(4.0 * traces.length / 6.0)));
				g = 0;
				b = 255;
			} else {
				int ncolorsincase = (int) (Math.ceil(6.0 * traces.length / 6.0) - Math
						.ceil(5.0 * traces.length / 6.0));
				r = 255;
				g = 0;
				b = (int) (255 - 255 / ncolorsincase
						* (i - Math.ceil(5.0 * traces.length / 6.0)));
			}
			traces[i].setColor(new Color(r, g, b));
		}
	}
	
	/** Returns the number of traces that already have a StatKeeper object assigned to. */
	public int getNumberOfAddedTraces() {
		return inext;
	}

	/**
	 * Adds a new StatKeeper that provides data for the traces. You must not add
	 * more StatKeeper objects than the number of Traces you defined in the
	 * constructor.
	 * 
	 * @param sk
	 *            new StatKeeper
	 */
	public void addstatkeeper(StatKeeper sk) {
		if (inext < numberoftraces) {
			if (inext == 0) {
				AxisTitle titleX = new AxisTitle(sk.getValuesName());
				AxisTitle titleY = new AxisTitle(sk.getStatName().contains(".") ? sk
						.getStatName().substring(0,
								sk.getStatName().indexOf('.')) : sk
						.getStatName());
				titleX.setTitleColor(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getControlTextColor());
				titleY.setTitleColor(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getControlTextColor());
				this.getAxisX().setAxisTitle(titleX);
				this.getAxisY().setAxisTitle(titleY);
			}
			traces[inext].setName(sk.getStatName());
			stats[inext] = sk;
			inext++;
		} else {
			System.err.println ("WARNING: Unable to add more traces to chart!");
		}
	}
	
	/** Returns the assigned StatKeeper instace at the given index. */
	public StatKeeper getStatKeeper(int index) {
		return stats[index];
	}

	/**
	 * Updates the displayed chart. If a new StatKeeper has been added or the
	 * amount of data in the already displayed StatKeepers has changed since the
	 * last call of this method the new data will be added to the displayed
	 * chart
	 */
	public void updateChart() {
		for (int i = 0; i < numberoftraces; i++) {
			for (int n = ilastaddedpoint[i] + 1; stats[i] != null
					&& n < stats[i].getValues().size(); n++) {
				double val = stats[i].getValues().get(n);
				traces[i].addPoint(n, val);
				ilastaddedpoint[i] = n;
				if (val > maxdisplayedval) {
					maxdisplayedval = val;
				}
				if (val < mindisplayedval) {
					mindisplayedval = val;
				}
			}
		}
		double chartmax = maxdisplayedval == 0
				|| maxdisplayedval == -Double.MAX_VALUE ? 1
				: maxdisplayedval < 0 ? maxdisplayedval / 1.05
						: maxdisplayedval * 1.05;
		double chartmin = mindisplayedval == 0
				|| mindisplayedval == Double.MAX_VALUE ? -1
				: mindisplayedval < 0 ? mindisplayedval * 1.05
						: mindisplayedval / 1.05;
		// this.getAxisY().setRange(new Range(chartmin, chartmax));
		this.getAxisY().getRangePolicy()
				.setRange(new Range(chartmin, chartmax));
	}
}
