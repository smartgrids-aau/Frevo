package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalLookAndFeel;

import main.FrevoMain;

public class FrevoLoadingPanel extends JPanel {
	private static final long serialVersionUID = -4974063826877772885L;
	private ImageIcon loadingIcon;

	// should be between 0 and 1
	private float progress = 0;

	public FrevoLoadingPanel(ImageIcon loadingIcon) {
		super();
		this.loadingIcon = loadingIcon;
		this.setBackground(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getWhite());

		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		GridBagLayout loadpanellayout = new GridBagLayout();
		setLayout(loadpanellayout);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int imageX = getWidth() / 2 - loadingIcon.getIconWidth() / 2;
		int imageY = getHeight() / 2 - loadingIcon.getIconHeight() / 2;

		// paint percentage
		if(FrevoMain.launchInDarkMode)
			g.setColor(Color.BLUE.darker());
		else
			g.setColor(Color.GREEN.darker().darker().darker());
		g.fillArc(imageX, imageY, loadingIcon.getIconWidth(),
				loadingIcon.getIconHeight(), 90, -(int) (progress * 360));

		// paint loading image
		g.drawImage(loadingIcon.getImage(), imageX, imageY, null);
	}

	public void setProgress(float p) {
		this.progress = p;
		repaint();
	}
}
