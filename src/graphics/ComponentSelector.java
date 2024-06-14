package graphics;

import graphics.FrevoWindow.CustomFileFilter;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import main.FrevoMain;
import utils.ScreenCapture;
import core.ComponentXMLData;
import core.FileType;

public class ComponentSelector extends JFrame implements WindowListener,
		ActionListener {

	private static final long serialVersionUID = 7435531649424370040L;

	JButton confirmbutton;
	@SuppressWarnings({ "rawtypes"}) //to avoid warning in Java7
	JComboBox componentsBox;
	JCheckBox exportSources;

	private int function;

	public static final int COMPONENT_SELECTOR_EXPORT = 100;
	public static final int COMPONENT_SELECTOR_DELETE = 101;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	ComponentSelector(ArrayList<ComponentXMLData> components, String title,
			int function) {
		super(title);

		this.function = function;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// return focus to main window after this has been closed
		this.addWindowListener(this);

		this.setLocationRelativeTo(null);

		this.setMinimumSize(new Dimension(400, 150));
		this.setMaximumSize(new Dimension(400, 150));
		this.setResizable(false);

		// inherit main frame
		Container con = this.getContentPane();

		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));

		// add filler object
		con.add(Box.createRigidArea(new Dimension(10, 5)));

		// add dropdown list for components
		componentsBox = new JComboBox(components.toArray());
		componentsBox.setMinimumSize(new Dimension(300, 50));
		componentsBox.setMaximumSize(new Dimension(300, 50));
		componentsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		componentsBox.setRenderer(renderer);

		con.add(componentsBox);

		// add extra checkboxes
		if (function == COMPONENT_SELECTOR_EXPORT) {
			exportSources = new JCheckBox("Export source files");
			exportSources.setSelected(true);
			exportSources.setAlignmentX(Component.CENTER_ALIGNMENT);
			con.add(exportSources);
		} else if (function == COMPONENT_SELECTOR_DELETE) {
			con.add(Box.createRigidArea(new Dimension(10, 20)));
		}

		// Add filler object
		con.add(Box.createRigidArea(new Dimension(10, 10)));

		// add confirm button
		if (function == COMPONENT_SELECTOR_EXPORT) {
			confirmbutton = new JButton("Export");
		} else if (function == COMPONENT_SELECTOR_DELETE) {
			confirmbutton = new JButton("Delete");
		}
		confirmbutton.setPreferredSize(new Dimension(100, 25));
		confirmbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
		confirmbutton.addActionListener(this);

		con.add(confirmbutton);

		pack();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		FrevoMain.getMainWindow().setEnabled(true);
		e.getComponent().setVisible(false);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		FrevoMain.getMainWindow().setEnabled(true);
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == confirmbutton) {
			if (function == COMPONENT_SELECTOR_EXPORT)
				exportComponent();
			else if (function == COMPONENT_SELECTOR_DELETE) {
				ComponentXMLData cdata = (ComponentXMLData) componentsBox
						.getSelectedItem();
				
				// erase component
				FrevoMain.deleteComponent(cdata);
				
				// return control to main window
				FrevoMain.getMainWindow().setEnabled(true);
				
				// hide thid window
				ComponentSelector.this.setVisible(false);
			}
				
		}

	}

	private void exportComponent() {
		// open file dialog
		// save console content
		JFileChooser fc = new JFileChooser(FrevoMain.getActiveDirectory());
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new CustomFileFilter(new FileType(
				FrevoMain.FREVO_PACKAGE_EXTENSION,
				"FREVO component package (*."
						+ FrevoMain.FREVO_PACKAGE_EXTENSION + ")")));

		File saveFile = null;
		while (true) {
			int returnVal = fc.showDialog(ComponentSelector.this,
					"Export component");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File chosen = fc.getSelectedFile();
				if (!chosen.exists()) {
					// file does not exist yet, proceed with writing
					saveFile = chosen;
					ComponentXMLData comp = (ComponentXMLData) componentsBox
							.getSelectedItem();
					exportComponent(comp, saveFile, exportSources.isSelected());
					break;
				}
				int confirm = JOptionPane.showConfirmDialog(
						ComponentSelector.this,
						"Overwrite file? " + chosen.getName());
				if (confirm == JOptionPane.OK_OPTION) {
					saveFile = chosen;
					ComponentXMLData comp = (ComponentXMLData) componentsBox
							.getSelectedItem();
					exportComponent(comp, saveFile, exportSources.isSelected());
					break;
				} else if (confirm == JOptionPane.NO_OPTION) {
					// user clicked on NO
					continue;
				} else if (confirm == JOptionPane.CANCEL_OPTION) {
					// user canceled export
					break;
				}

			} else
				break;
		}
	}

	private void exportComponent(final ComponentXMLData comp,
			final File saveFile, final boolean withSources) {
		confirmbutton.setEnabled(false);
		confirmbutton.setText("Exporting...");

		Thread workerthread = new Thread(new Runnable() {

			@Override
			public void run() {
				FrevoMain.exportComponent(comp, saveFile, withSources);
				confirmbutton.setText("Export");
				confirmbutton.setEnabled(true);

				// hide this component
				FrevoMain.getMainWindow().setEnabled(true);
				ComponentSelector.this.setVisible(false);
			}
		});

		workerthread.start();

	}

	@SuppressWarnings("rawtypes")
	private class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 5521990526255048439L;

		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			ComponentXMLData cdata = (ComponentXMLData) value;

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			// Set the icon and text.
			String imagepath = (FrevoMain.getComponentDirectory(cdata
					.getComponentType()) + cdata.getClassDir() + File.separator + cdata
					.getImageLocation());
			ImageIcon componentIcon;
			componentIcon = FrevoWindow.getAdaptedImage(imagepath);
			componentIcon.setDescription(cdata.getName());
			setIcon(ScreenCapture.resizeImage(componentIcon.getImage(), 48, 48));
			
			//already caught by FrevoWindow.getAdaptedImage()
			/*if (componentIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
				setIcon(ScreenCapture.resizeImage(
						 FrevoWindow.nothingIcon.getImage(), 48, 48));
			} else 
			{
				
			}*/

			setText(cdata.toString());

			setHorizontalAlignment(JLabel.LEFT);

			return this;
		}

	}

}
