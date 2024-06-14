package emergencyexit;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import GridVisualization.*;

/**
 * The Class FieldCreator is used to create Fields that can be used in the class
 * EmergencyExit. These Fields contain Emergency Exits, Blockades and Agents
 * that are placed with the mouse.
 * 
 * @author praktikant 9
 * 
 */
public class FieldCreator {
	enum Tool {
		agent, EmergencyExit, blockade, empty;
	}

	Field field = new Field();
	ArrayList<Point> agents = new ArrayList<Point>();
	ArrayList<Point> EmergencyExits = new ArrayList<Point>();
	ArrayList<Point> blockades = new ArrayList<Point>();
	int width = 20;
	int height = 20;
	WhiteBoard w;
	Display d;
	JButton selectAgentButton;
	JButton selectEmergencyExitButton;
	JButton selectBlockadeButton;
	JButton selectEmptyButton;
	JButton saveButton;
	JButton openButton;
	JPanel buttonPanel;
	Tool tool = Tool.agent;
	File FieldFile = new File("Default.ser");

	public FieldCreator() {
		d = new Display(740, 800, "Field Creator");
		d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		d.setVisible(true);
		selectAgentButton = new JButton("select agent");
		selectAgentButton.addActionListener(new selectAgentListener());
		selectEmergencyExitButton = new JButton("select Emergency Exit");
		selectEmergencyExitButton
				.addActionListener(new selectEmergencyExitListener());
		selectBlockadeButton = new JButton("select blockade");
		selectBlockadeButton.addActionListener(new selectBlockadeListener());
		selectEmptyButton = new JButton("select empty");
		selectEmptyButton.addActionListener(new selectEmptyListener());
		saveButton = new JButton(" save ");
		saveButton.addActionListener(new saveButtonListener());
		openButton = new JButton(" open ");
		openButton.addActionListener(new openButtonListener());
		d.setLayout(new BorderLayout());
		buttonPanel = new JPanel();
		buttonPanel.add(selectAgentButton);
		buttonPanel.add(selectEmergencyExitButton);
		buttonPanel.add(selectBlockadeButton);
		buttonPanel.add(selectEmptyButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(openButton);
		d.add(buttonPanel, BorderLayout.NORTH);
		w = new WhiteBoard(700, 700, width, height, 1);
		w.paintBoard.addMouseListener(new PaintBoardClickListener());
		w.addImageToScale(0,
				"Components\\Problems\\EmergencyExit\\emergencyexit\\agent.png");
		w.addImageToScale(1,
				"Components\\Problems\\EmergencyExit\\emergencyexit\\EmergencyExit.png");
		w.addImageToScale(2,
				"Components\\Problems\\EmergencyExit\\emergencyexit\\blockade.png");
		d.add(w);
		w.setVisible(true);
		w.setGridOn(true);
		d.pack();
		d.setVisible(true);
	}

	private void UpdateWhiteBoard() {

		int[][] data = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				data[x][y] = -1;
			}
		}
		for (Point pos : agents) {
			data[pos.x][pos.y] = 0;
		}
		for (Point pos : EmergencyExits) {
			data[pos.x][pos.y] = 1;
		}
		for (Point pos : blockades) {
			data[pos.x][pos.y] = 2;
		}
		w.setData(data);
		w.setGridOn(true);
		w.repaint();
	}

	private class PaintBoardClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			int MouseX = arg0.getX() / w.scale;
			int MouseY = arg0.getY() / w.scale;
			for (int i = agents.size() - 1; i >= 0; i--) {
				if (agents.get(i).x == MouseX && agents.get(i).y == MouseY)
					agents.remove(i);
			}
			for (int i = EmergencyExits.size() - 1; i >= 0; i--) {
				if (EmergencyExits.get(i).x == MouseX
						&& EmergencyExits.get(i).y == MouseY)
					EmergencyExits.remove(i);
			}
			for (int i = blockades.size() - 1; i >= 0; i--) {
				if (blockades.get(i).x == MouseX
						&& blockades.get(i).y == MouseY)
					blockades.remove(i);
			}
			Point p = new Point();
			p.x = MouseX;
			p.y = MouseY;
			if (tool == Tool.agent)
				agents.add(p);
			else if (tool == Tool.EmergencyExit)
				EmergencyExits.add(p);
			else if (tool == Tool.blockade)
				blockades.add(p);
			UpdateWhiteBoard();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class selectAgentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			tool = Tool.agent;
		}
	}

	private class selectEmergencyExitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			tool = Tool.EmergencyExit;
		}
	}

	private class selectBlockadeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			tool = Tool.blockade;
		}
	}

	private class selectEmptyListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			tool = Tool.empty;
		}
	}

	private class saveButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Field f = new Field();
			f.width = width;
			f.height = height;
			f.agents = new Point[agents.size()];
			for (int i = 0; i < f.agents.length; i++) {
				f.agents[i] = agents.get(i);
			}
			f.EmergencyExits = new Point[EmergencyExits.size()];
			for (int i = 0; i < f.EmergencyExits.length; i++) {
				f.EmergencyExits[i] = EmergencyExits.get(i);
			}
			f.blockades = new Point[blockades.size()];
			for (int i = 0; i < f.blockades.length; i++) {
				f.blockades[i] = blockades.get(i);
			}
			try {
				String path = FieldFile.getAbsolutePath();
				path = path.substring(0, path.lastIndexOf("\\"));
				JFileChooser fc = new JFileChooser(path);
				fc.showSaveDialog(null);
				FieldFile = fc.getSelectedFile();
				if (FieldFile != null) {
					FieldFile.createNewFile();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (FieldFile != null) {
				try {
					FileOutputStream fileout = new FileOutputStream(FieldFile);
					ObjectOutputStream objectout = new ObjectOutputStream(
							fileout);
					objectout.writeObject(f);
					fileout.close();
					objectout.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				FieldFile = new File("default.ser");
			}
		}
	}

	private class openButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Field f;
			try {
				String path = FieldFile.getAbsolutePath();
				path = path.substring(0, path.lastIndexOf("\\"));
				JFileChooser fc = new JFileChooser(path);
				fc.showOpenDialog(null);
				FieldFile = fc.getSelectedFile();
				if (FieldFile != null) {
					FieldFile.createNewFile();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if (FieldFile != null) {
				try {
					FileInputStream filein = new FileInputStream(FieldFile);
					ObjectInputStream objectin = new ObjectInputStream(filein);
					f = (Field) objectin.readObject();
					filein.close();
					objectin.close();
					agents.clear();
					Collections.addAll(agents, f.agents);
					EmergencyExits.clear();
					Collections.addAll(EmergencyExits, f.EmergencyExits);
					blockades.clear();
					Collections.addAll(blockades, f.blockades);
					UpdateWhiteBoard();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				FieldFile = new File("default.ser");
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String args[]) {
		FieldCreator fc = new FieldCreator();
		fc.d.repaint();
		fc.w.repaint();
	}

}
