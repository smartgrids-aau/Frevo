package emergencyexit;

/*import java.awt.Color;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.ArrayList;
 import java.util.Random;
 import java.util.Vector;

 import javax.swing.JButton;
 import javax.swing.JFrame;
 import javax.swing.SwingWorker;

 import interfaces.IProblem;
 import interfaces.IRepresentation;

 import GridVisualization.*;

 public class EmergencyExit extends IProblem {
 int width;
 int height;
 int xPositionofEmergencyExit;
 int yPositionofEmergencyExit;
 int steps;
 IRepresentation[] candidates;
 int numberofAgents;
 WhiteBoard w;
 Display d;
 ArrayList<agent> startingAgents;

 @Override
 public double getResult(IRepresentation[] c) {

 // read config from xml file
 width = Integer.parseInt(getProperties().get("width").getValue());
 height = Integer.parseInt(getProperties().get("height").getValue());
 xPositionofEmergencyExit = Integer.parseInt(getProperties().get("xPositionofEmergencyExit").getValue());
 yPositionofEmergencyExit = Integer.parseInt(getProperties().get("yPositionofEmergencyExit").getValue());
 steps = Integer.parseInt(getProperties().get("steps").getValue());
 candidates = c;

 numberofAgents = 1;

 double Fitness = 0.0;
 for (int step = 0; step < steps; step++) {
 Fitness = simulateonestep(step == 0, this.candidates, width, height, xPositionofEmergencyExit,
 yPositionofEmergencyExit, numberofAgents);
 }

 return Fitness;
 }

 private int[][] field;
 private ArrayList<agent> agents;

 @Override
 public void replayWithVisualization(IRepresentation[] c) {
 // read config from xml file
 width = Integer.parseInt(getProperties().get("width").getValue());
 height = Integer.parseInt(getProperties().get("height").getValue());
 xPositionofEmergencyExit = Integer.parseInt(getProperties().get("xPositionofEmergencyExit").getValue());
 yPositionofEmergencyExit = Integer.parseInt(getProperties().get("yPositionofEmergencyExit").getValue());
 steps = Integer.parseInt(getProperties().get("steps").getValue());
 candidates = c;

 w = new WhiteBoard(400, 400, width, height, 1);
 d = new Display(440, 465, "EmergencyExit");
 d.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 d.add(w);
 d.setVisible(true);
 w.addColorToScale(-1, Color.WHITE);
 w.addColorToScale(1, Color.BLACK);
 w.addColorToScale(2, Color.GREEN);
 numberofAgents = 1;

 for (int step = 0; step < steps; step++) {
 simulateonestep(step == 0, this.candidates, width, height, xPositionofEmergencyExit, yPositionofEmergencyExit,
 numberofAgents);
 int data[][] = new int[width][height];
 for (int x = 0; x < width; x++) {
 for (int y = 0; y < height; y++) {
 if  (field[x][y] == 2) data[x][y] = 2;
 else if (field[x][y] == 1) data[x][y] = 1;
 else                   data[x][y] = 0;
 }
 }
 w.setData(data);

 d.setTitle("Emergency Exit    Step: " + step + "    number of Agents: " + agents.size());
 }
 JButton stepminusButton = new JButton("-");
 JButton stepplusButton = new JButton("+");
 d.add(stepminusButton);
 d.add(stepplusButton);
 stepminusButton.addActionListener(new ActionListener() {

 @SuppressWarnings("unchecked")
 @Override
 public void actionPerformed(ActionEvent e) {
 if(steps>1) steps--;
 agents.clear();
 for(agent a:startingAgents){
 agents.add(a);
 }

 for (int step = 0; step < steps; step++) {
 simulateonestep(false, candidates, width, height, xPositionofEmergencyExit, yPositionofEmergencyExit,
 numberofAgents);
 int data[][] = new int[width][height];
 for (int x = 0; x < width; x++) {
 for (int y = 0; y < height; y++) {
 if  (field[x][y] == 2) data[x][y] = 2;
 else if (field[x][y] == 1) data[x][y] = 1;
 else                   data[x][y] = 0;
 }
 }
 w.setData(data);

 d.setTitle("Emergency Exit    Step: " + step + "    number of Agents: " + agents.size());
 }
 w.repaint();
 }
 });
 stepplusButton.addActionListener(new ActionListener() {

 @SuppressWarnings("unchecked")
 @Override
 public void actionPerformed(ActionEvent e) {
 steps++;
 agents.clear();
 for(agent a:startingAgents){
 agents.add(a);
 }
 for (int step = 0; step < steps; step++) {
 simulateonestep(false, candidates, width, height, xPositionofEmergencyExit, yPositionofEmergencyExit,
 numberofAgents);
 int data[][] = new int[width][height];
 for (int x = 0; x < width; x++) {
 for (int y = 0; y < height; y++) {
 if  (field[x][y] == 2) data[x][y] = 2;
 else if (field[x][y] == 1) data[x][y] = 1;
 else                   data[x][y] = 0;
 }
 }
 w.setData(data);

 d.setTitle("Emergency Exit    Step: " + step + "    number of Agents: " + agents.size());
 }
 w.repaint();
 }
 });
 w.repaint();
 }

 @Override
 public String getClassName() {
 return "EmergencyExit";
 }

 @Override
 public String getDescription() {
 return "A Simulation of Agents who try to find the Emergency Exit";
 }

 @Override
 public String getName() {
 return "";
 }

 @SuppressWarnings("unchecked")
 private double simulateonestep(boolean reset, IRepresentation[] candidates, int width, int height,
 int xPositionofEmergencyExit, int yPositionofEmergencyExit, int numberofAgents) {

 if (reset) {
 field = new int[width][height];
 agents = new ArrayList<agent>();

 // initialize field and agents
 // set every element in field to Field.empty
 for (int x = 0; x < width; x++) {
 for (int y = 0; y < height; y++) {
 field[x][y] = 0;
 }
 }
 // set element at the position of Emergency Exit to Field.EmergencyExit
 field[xPositionofEmergencyExit][yPositionofEmergencyExit] = 2;
 // initialize agents and set elements at the position of the Agents to Fild.Agent
 Random r = new Random();
 while (agents.size() < numberofAgents) {
 for (int x = 0; x < width; x++) {
 for (int y = 0; y < height; y++) {
 if (agents.size() < numberofAgents && field[x][y] == 0 && r.nextBoolean()) {
 agents.add(new agent(candidates[0].clone(), x, y, xPositionofEmergencyExit - x, yPositionofEmergencyExit
 - y));
 field[x][y] = 1;
 }
 }
 }
 }
 startingAgents = new ArrayList<agent>();
 for(agent a:agents){
 startingAgents.add(a);
 }
 // end of initialize field and agents
 }

 field = new int[width][height];
 // initialize field and agents
 // set every element in field to Field.empty
 for (int x = 0; x < width; x++) {
 for (int y = 0; y < height; y++) {
 field[x][y] = 0;
 }
 }
 // set element at the position of Emergency Exit to Field.EmergencyExit
 field[xPositionofEmergencyExit][yPositionofEmergencyExit] = 2;
 // initialize agents and set elements at the position of the Agents to Fild.Agent
 for (agent a : agents) {
 field[a.xpos][a.ypos] = 1;
 }

 // simulate

 for (int i = agents.size() - 1; i >= 0; i--) {
 agent a = agents.get(i);
 // input[0] .. x distance to Emergency exit
 //      [1] .. y distance to Emergency exit
 //      [2] .. position north of the Agent is occupied
 //      [3] .. position east of the Agent is occupied
 //      [4] .. position south of the Agent is occupied
 //      [5] .. position west of the Agent is occupied
 Vector<Float> input = new Vector<Float>();
 input.add((float) a.xDistToExit);
 input.add((float) a.yDistToExit);

 float northOccupied = 0.0f; // 0.0 .. not occupied, 1.0 .. occupied
 if (a.ypos > 0 && field[a.xpos][a.ypos - 1] == 0) northOccupied = 0.0f;
 else                                                    northOccupied = 1.0f;
 input.add(northOccupied);

 float eastOccupied = 0.0f;
 if (a.xpos < width - 1 && field[a.xpos + 1][a.ypos] == 0) eastOccupied = 0.0f;
 else                                                            eastOccupied = 1.0f;
 input.add(eastOccupied);

 float southOccupied = 0.0f;
 if (a.ypos < height - 1 && field[a.xpos][a.ypos + 1] == 0) southOccupied = 0.0f;
 else                                                         southOccupied = 1.0f;
 input.add(southOccupied);

 float westOccupied = 0.0f;
 if (a.xpos > 0 && field[a.xpos - 1][a.ypos] == 0) westOccupied = 0.0f;
 else                                                    westOccupied = 1.0f;
 input.add(westOccupied);

 // output[0] .. x velocity.         xV >= 1.0 .. move 1 field east
 //                           1.0 >  xV > -1.0 .. stay
 //                          -1.0 >= xV        .. move 1 field west
 // output[1] .. y velocity.         yV >= 1.0 .. move 1 field south
 //                           1.0 >  yV > -1.0 .. stay
 //                          -1.0 >= yV        .. move 1 field north

 Vector<Float> output = a.representation.getOutput(input);

 float xVelocity = output.get(0).floatValue();
 float yVelocity = output.get(1).floatValue();

 if  (xVelocity >= 1.0 && a.xpos < width - 1 && eastOccupied == 0.0) a.xpos += 1;
 else if (xVelocity <= -1.0 && a.xpos > 0    && westOccupied == 0.0) a.xpos -= 1;
 else if (yVelocity >= 1.0 && a.ypos < height - 1 && southOccupied == 0.0) a.ypos += 1;
 else if (yVelocity <= -1.0 && a.ypos > 0     && northOccupied == 0.0) a.ypos -= 1;

 a.xDistToExit = xPositionofEmergencyExit - a.xpos;
 a.yDistToExit = yPositionofEmergencyExit - a.ypos;

 if (a.xDistToExit == 0 && a.yDistToExit == 0) {
 agents.remove(i);
 }
 }

 // set all elements to Field.empty
 for (int x = 0; x < width; x++) {
 for (int y = 0; y < height; y++) {
 field[x][y] = 0;
 }
 }

 // set element at the position of Emergency Exit to Field.EmergencyExit
 field[xPositionofEmergencyExit][yPositionofEmergencyExit] = 2;

 // set elements at the position of the Agents to Fild.Agent
 for (agent a : agents) {
 field[a.xpos][a.ypos] = 1;
 }

 return (-agents.size());
 }

 public class agent {
 public IRepresentation representation;
 public int xpos;
 public int ypos;
 public int xDistToExit;
 public int yDistToExit;

 *//**
 * @param r
 *          Representation for this Agent
 * @param x
 *          defines the x position of this Agent
 * @param y
 *          defines the y position of this Agent
 * @param xdist
 *          defines the x distance of this Agent to the Emergency Exit
 * @param ydist
 *          defines the y distance of this Agent to the Emergency Exit
 */
/*
 public agent(IRepresentation r, int x, int y, int xdist, int ydist) {
 representation = r;
 xpos = x;
 ypos = y;
 xDistToExit = xdist;
 yDistToExit = ydist;
 }
 }
 }
 */

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.jodk.lang.FastMath;
import utils.NESRandom;
import GridVisualization.Display;
import GridVisualization.WhiteBoard;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import main.FrevoMain;

/**
 * A simulation where multiple Agents try to find the Emergency Exit
 * 
 * @author Thomas Dittrich
 * 
 */
public class EmergencyExit extends AbstractSingleProblem {

	int steps;
	int numberofAgents;
	int numberofExits;
	int numberofBlockades;
	int seed;
	int numberofEvaluations;
	agent[] agents;
	Exit[] EmergencyExits;
	blockade[] blockades;
	AbstractRepresentation c;
	JTextField seedTextField;
	Field f = new Field();
	FieldSource FIELDSOURCE;
	boolean runningSimulation = false;
	JButton playbutton;
	JButton resetbutton;
	DisplayWorker dw;

	// this function is called to simulate without visualization. It is used to
	// find the best Representation
	@Override
	public double evaluateCandidate(AbstractRepresentation candidate) {
		// read config from xml file
		steps = Integer.parseInt(getProperties().get("steps").getValue());
		f.width = Integer.parseInt(getProperties().get("width").getValue());
		f.height = Integer.parseInt(getProperties().get("height").getValue());
		seed = Integer.parseInt(getProperties().get("seedforEmergencyExits")
				.getValue());
		numberofEvaluations = Integer.parseInt(getProperties().get(
				"NumberofEvaluations").getValue());
		String filename = getProperties().get("Filename").getValue();
		FIELDSOURCE = FieldSource.valueOf(getProperties().get("fieldsource")
				.getValue());
		c = candidate;
		double Fitness = 0;
		if (FIELDSOURCE == FieldSource.FIELD_FROM_FILE) {
			setupField(filename);
			Fitness += calcSim();
		} else {
			for (int s = seed; s < seed + numberofEvaluations; s++) {
				setupField(s);
				Fitness += calcSim();
			}
		}

		return (Fitness);// / numberofEvaluations);
	}

	WhiteBoard whiteboard;
	Display display;

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		steps = 0;
		c = candidate;
		// read config from xml file
		f.width = Integer.parseInt(getProperties().get("width").getValue());
		f.height = Integer.parseInt(getProperties().get("height").getValue());
		seed = Integer.parseInt(getProperties().get("seedforEmergencyExits")
				.getValue());
		String filename = getProperties().get("Filename").getValue();
		FIELDSOURCE = FieldSource.valueOf(getProperties().get("fieldsource")
				.getValue());
		if (FIELDSOURCE == FieldSource.FIELD_FROM_FILE) {
			setupField(filename);
		} else {
			setupField(seed);
		}
		display = new Display(840, 695, "EmergencyExit");
		display.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		whiteboard = new WhiteBoard(600, 600, f.width, f.height, 1);
		whiteboard.addColorToScale(0, Color.WHITE);
		// you can decide whether to take images or colors to represent things
		// on the whiteboard
		// whiteboard.addColorToScale(1, Color.BLACK);
		whiteboard
				.addImageToScale(1,
						"Components/Problems/EmergencyExit/emergencyexit/agent.png");
		// whiteboard.addColorToScale(2, Color.GREEN);
		whiteboard
				.addImageToScale(2,
						"Components/Problems/EmergencyExit/emergencyexit/EmergencyExit.png");
		whiteboard
				.addImageToScale(3,
						"Components/Problems/EmergencyExit/emergencyexit/blockade.png");
		JButton minusbutton = new JButton("<--");
		JButton plusbutton = new JButton("-->");
		
		try {
			playbutton = new JButton(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/media/Play24.gif")));
			resetbutton = new JButton(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/reset24.png")));
		} catch (MalformedURLException e1) {
			playbutton = new JButton("Start");
			resetbutton = new JButton("Reset");
		}
		
		seedTextField = new JTextField("" + seed);
		seedTextField.setPreferredSize(new Dimension(50, 20));
		JButton changeSeedButton = new JButton("Change seed");
		JPanel stepMenu = new JPanel();
		stepMenu.add(minusbutton);
		stepMenu.add(plusbutton);
		JPanel playMenu = new JPanel();
		playMenu.add(playbutton);
		playMenu.add(resetbutton);
		JPanel seedMenu = new JPanel();
		seedMenu.add(seedTextField);
		seedMenu.add(changeSeedButton);
		
		//layout
		JPanel menu = new JPanel();
		GroupLayout menuLayout = new GroupLayout(menu);
		menu.setLayout(menuLayout);
		menu.add(stepMenu);
		menu.add(playMenu);
		menu.add(seedMenu);
		menuLayout.setHorizontalGroup(menuLayout.createParallelGroup()
				.addComponent(stepMenu).addGap(10).addComponent(playMenu).addGap(10).addComponent(seedMenu));
		menuLayout.setVerticalGroup(menuLayout.createSequentialGroup()
				.addComponent(stepMenu).addGap(10).addComponent(playMenu).addGap(10).addComponent(seedMenu));
		
		display.add(whiteboard);
		display.add(menu);
		Container displayContainer = display.getContentPane();
		GroupLayout displayLayout = new GroupLayout(displayContainer);
		displayContainer.setLayout(displayLayout);
		displayLayout.setHorizontalGroup(displayLayout.createSequentialGroup()
				.addComponent(whiteboard).addComponent(menu));
		displayLayout.setVerticalGroup(displayLayout.createParallelGroup()
				.addComponent(whiteboard).addComponent(menu));
		
		minusbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (steps > 0)
					steps--;
				calculateAndDisplay();
			}
		});
		plusbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				steps++;
				calculateAndDisplay();
			}
		});
		changeSeedButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				steps = 0;
				seed = Integer.parseInt(seedTextField.getText());
				FIELDSOURCE = FieldSource.RANDOM_FIELD;
				setupField(seed);
				calculateAndDisplay();
			}
		});
		playbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				runningSimulation = !runningSimulation;
				if(runningSimulation)
				{
					try {
						playbutton.setIcon(new ImageIcon(new URL("jar:file:"
								+ FrevoMain.getInstallDirectory()
								+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
								+ "toolbarButtonGraphics/media/Stop24.gif")));
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					dw = new DisplayWorker();
					dw.execute();
				}
				else
				{
					try {
						playbutton.setIcon(new ImageIcon(new URL("jar:file:"
								+ FrevoMain.getInstallDirectory()
								+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
								+ "toolbarButtonGraphics/media/Play24.gif")));
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		resetbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				steps = 0;
				calculateAndDisplay();
			}
		});
		display.setVisible(true);
		calculateAndDisplay();
	}
	
	/**
	 * load the field, calculate the fitness and display the results
	 */
	private void calculateAndDisplay()
	{
		String filename = getProperties().get("Filename").getValue();

		if (FIELDSOURCE == FieldSource.FIELD_FROM_FILE) {
			setupField(filename);
		} else {
			setupField(seed);
		}
		double Fitness = calcSim();
		displayResult();
		int agentsleft = 0;
		for (agent a : agents) {
			if (!a.hasReachedExit)
				agentsleft++;
		}
		String FitnessString = String.format("%.4f", Fitness);
		display.setTitle("Emergency Exit    Step: " + steps
				+ "   Fitness: " + FitnessString
				+ "  Number of Agents left: " + agentsleft);
	}
	
	private class DisplayWorker extends SwingWorker<Void, Integer> {

		@Override
		protected Void doInBackground() throws Exception {
			while(runningSimulation)
			{
				steps++;
				calculateAndDisplay();
				Thread.sleep(Integer.parseInt(getProperties().get("length_of_timestep_ms").getValue()));
			}
			return null;
		}

	}

	/**
	 * Displays the result of the last Simulation
	 */
	private void displayResult() {
		int[][] data = new int[f.width][f.height];
		for (int x = 0; x < f.width; x++) {
			for (int y = 0; y < f.height; y++) {
				data[x][y] = 0;
			}
		}
		for (agent a : agents) {
			data[a.xpos][a.ypos] = 1;
		}
		for (Exit e : EmergencyExits) {
			data[e.xpos][e.ypos] = 2;
		}
		for (blockade b : blockades) {
			data[b.xpos][b.ypos] = 3;
		}

		whiteboard.setData(data);
		whiteboard.repaint();
	}

	void setupField(String Filename) {
		Field f = new Field();
		File FieldFile = new File(Filename);
		try {
			FileInputStream filein = new FileInputStream(FieldFile);
			ObjectInputStream objectin = new ObjectInputStream(filein);
			f = (Field) objectin.readObject();
			filein.close();
			objectin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		numberofAgents = f.agents.length;
		numberofExits = f.EmergencyExits.length;
		numberofBlockades = f.blockades.length;
		agents /*        */= new agent[numberofAgents];
		EmergencyExits /**/= new Exit[numberofExits];
		blockades /*     */= new blockade[numberofBlockades];
		// it is important to reset the Representation. Otherwise there would
		// sometimes be simulation mistakes because the Representation wouldn't
		// start from the same seed
		c.reset();
		// create the agents and place them in a straight line from the upper
		// left to the lower right corner

		for (int i = 0; i < agents.length; i++) {
			agents[i] = new agent(c.clone(), f.agents[i].x, f.agents[i].y,
					false);
		}
		for (int i = 0; i < EmergencyExits.length; i++) {
			EmergencyExits[i] = new Exit();
			EmergencyExits[i].xpos = f.EmergencyExits[i].x;
			EmergencyExits[i].ypos = f.EmergencyExits[i].y;
		}
		for (int i = 0; i < blockades.length; i++) {
			blockades[i] = new blockade();
			blockades[i].xpos = f.blockades[i].x;
			blockades[i].ypos = f.blockades[i].y;
		}

	}

	void setupField(int s) {
		// read config from xml file
		numberofAgents /*   */= Integer.parseInt(getProperties().get(
				"NumberofAgents").getValue());
		numberofExits /*    */= Integer.parseInt(getProperties().get(
				"NumberofEmergencyExits").getValue());
		numberofBlockades /**/= Integer.parseInt(getProperties().get(
				"NumberofBlockades").getValue());

		numberofAgents /**/= Math.min(numberofAgents,
				Math.max(f.width, f.height));
		numberofExits /* */= Math.min(numberofExits, f.width * f.height);
		agents /*        */= new agent[numberofAgents];
		EmergencyExits /**/= new Exit[numberofExits];
		blockades /*     */= new blockade[numberofBlockades];
		// it is important to reset the Representation. Otherwise there would
		// sometimes be simulation mistakes because the Representation wouldn't
		// start from the same seed
		c.reset();
		// create the agents and place them in a straight line from the upper
		// left to the lower right corner

		Random positionGenerator = new NESRandom(s);
		for (int i = 0; i < agents.length; i++) {
			agents[i] = new agent(c.clone(), false);
			boolean PositionExistsAlready = false;
			do {
				agents[i].xpos = positionGenerator.nextInt(f.width);
				agents[i].ypos = positionGenerator.nextInt(f.width);
				PositionExistsAlready = false;
				for (int j = 0; j < i; j++) {
					if (agents[i].xpos == agents[j].xpos
							&& agents[i].ypos == agents[j].ypos)
						PositionExistsAlready = true;
				}
			} while (PositionExistsAlready);
		}
		for (int i = 0; i < EmergencyExits.length; i++) {
			EmergencyExits[i] = new Exit();
			boolean PositionExistsAlready = false;
			do {
				EmergencyExits[i].xpos = positionGenerator.nextInt(f.width);
				EmergencyExits[i].ypos = positionGenerator.nextInt(f.width);
				PositionExistsAlready = false;
				for (int j = 0; j < i; j++) {
					if (EmergencyExits[i].xpos == EmergencyExits[j].xpos
							&& EmergencyExits[i].ypos == EmergencyExits[j].ypos)
						PositionExistsAlready = true;
				}
				for (int j = 0; j < agents.length; j++) {
					if (EmergencyExits[i].xpos == agents[j].xpos
							&& EmergencyExits[i].ypos == agents[j].ypos)
						PositionExistsAlready = true;
				}
			} while (PositionExistsAlready);
		}
		for (int i = 0; i < blockades.length; i++) {
			blockades[i] = new blockade();
			boolean PositionExistsAlready = false;
			do {
				blockades[i].xpos = positionGenerator.nextInt(f.width);
				blockades[i].ypos = positionGenerator.nextInt(f.width);
				PositionExistsAlready = false;
				for (int j = 0; j < i; j++) {
					if (blockades[i].xpos == blockades[j].xpos
							&& blockades[i].ypos == blockades[j].ypos)
						PositionExistsAlready = true;
				}
				for (int j = 0; j < EmergencyExits.length; j++) {
					if (blockades[i].xpos == EmergencyExits[j].xpos
							&& blockades[i].ypos == EmergencyExits[j].ypos)
						PositionExistsAlready = true;
				}
				for (int j = 0; j < agents.length; j++) {
					if (blockades[i].xpos == agents[j].xpos
							&& blockades[i].ypos == agents[j].ypos)
						PositionExistsAlready = true;
				}
			} while (PositionExistsAlready);
		}
	}

	/**
	 * Calculates one Simulation whit a certain amount of steps, which has to be
	 * defined before calling this method
	 * 
	 * @return Returns the negative Sum of the distances between the agents and
	 *         the Emergency Exit
	 */
	double calcSim() {
		int stepfinished = steps;
		for (int step = 0; step < steps; step++) {
			for (int i = 0; i < agents.length; i++) {
				agent a = agents[i];
				if (!a.hasReachedExit) {

					Exit nearestExit = EmergencyExits[0];
					double minimumDistance = FastMath.hypot(
							EmergencyExits[0].xpos - a.xpos,
							EmergencyExits[0].ypos - a.ypos);
					for (int e = 0; e < EmergencyExits.length; e++) {
						double Distance = FastMath.hypot(EmergencyExits[e].xpos
								- a.xpos, EmergencyExits[e].ypos - a.ypos);
						if (Distance < minimumDistance) {
							minimumDistance = Distance;
							nearestExit = EmergencyExits[e];
						}
					}

					// input[0] .. horizontal distance between the agent and the
					// nearest Emergency Exit
					// input[1] .. vertical distance between the agent and the
					// nearest Emergency Exit
					// input[2] .. field north of the agent is occupied
					// input[3] .. field north-east of the agent is occupied
					// input[4] .. field east of the agent is occupied
					// input[5] .. field south-east of the agent is occupied
					// input[6] .. field south of the agent is occupied
					// input[7] .. field south-west of the agent is occupied
					// input[8] .. field west of the agent is occupied
					// input[9] .. field north-west of the agent is occupied

					// determine which fields around the agent are occupied by
					// another agent
					boolean northfree /**/= true;
					boolean northeastfree = true;
					boolean eastfree /* */= true;
					boolean southeastfree = true;
					boolean southfree /**/= true;
					boolean southwestfree = true;
					boolean westfree /* */= true;
					boolean northwestfree = true;

					if (a.ypos <= 0) {
						northeastfree = false;
						northfree = false;
						northwestfree = false;
					} else if (a.ypos >= f.height - 1) {
						southeastfree = false;
						southfree = false;
						southwestfree = false;
					}

					if (a.xpos <= 0) {
						northwestfree = false;
						westfree = false;
						southwestfree = false;
					} else if (a.xpos >= f.width - 1) {
						northeastfree = false;
						eastfree = false;
						southeastfree = false;
					}

					for (int j = 0; j < agents.length; j++) {
						agent ag = agents[j];
						if (!ag.hasReachedExit) { // If a agent has reached the
													// Emergency Exit he cannot
													// occupy a field
							if (a.xpos /**/== ag.xpos
									&& a.ypos - 1 == ag.ypos)
								northfree /**/= false;
							if (a.xpos + 1 == ag.xpos && a.ypos - 1 == ag.ypos)
								northeastfree = false;
							if (a.xpos + 1 == ag.xpos && a.ypos /**/== ag.ypos)
								eastfree /* */= false;
							if (a.xpos + 1 == ag.xpos && a.ypos + 1 == ag.ypos)
								southeastfree = false;
							if (a.xpos /**/== ag.xpos
									&& a.ypos + 1 == ag.ypos)
								southfree /**/= false;
							if (a.xpos - 1 == ag.xpos && a.ypos + 1 == ag.ypos)
								southwestfree = false;
							if (a.xpos - 1 == ag.xpos && a.ypos /**/== ag.ypos)
								westfree /* */= false;
							if (a.xpos - 1 == ag.xpos && a.ypos - 1 == ag.ypos)
								northwestfree = false;
						}
					}
					for (int j = 0; j < blockades.length; j++) {
						blockade b = blockades[j];
						if (a.xpos /**/== b.xpos && a.ypos - 1 == b.ypos)
							northfree /**/= false;
						if (a.xpos + 1 == b.xpos && a.ypos - 1 == b.ypos)
							northeastfree = false;
						if (a.xpos + 1 == b.xpos && a.ypos /**/== b.ypos)
							eastfree /* */= false;
						if (a.xpos + 1 == b.xpos && a.ypos + 1 == b.ypos)
							southeastfree = false;
						if (a.xpos /**/== b.xpos && a.ypos + 1 == b.ypos)
							southfree /**/= false;
						if (a.xpos - 1 == b.xpos && a.ypos + 1 == b.ypos)
							southwestfree = false;
						if (a.xpos - 1 == b.xpos && a.ypos /**/== b.ypos)
							westfree /* */= false;
						if (a.xpos - 1 == b.xpos && a.ypos - 1 == b.ypos)
							northwestfree = false;
					}

					ArrayList<Float> input = new ArrayList<Float>();
					input.add((float) (nearestExit.xpos - a.xpos));
					input.add((float) (nearestExit.ypos - a.ypos));
					input.add(northfree /**/? 1.0f : 0.0f);
					input.add(northeastfree ? 1.0f : 0.0f);
					input.add(eastfree /* */? 1.0f : 0.0f);
					input.add(southeastfree ? 1.0f : 0.0f);
					input.add(southfree /**/? 1.0f : 0.0f);
					input.add(southwestfree ? 1.0f : 0.0f);
					input.add(westfree /* */? 1.0f : 0.0f);
					input.add(northwestfree ? 1.0f : 0.0f);

					// output[0] .. horizontal velocity of the agent
					// output[1] .. vertical velocity of the agent
					ArrayList<Float> output = a.representation.getOutput(input);

					// the elements of output are float values between 0.0 and
					// 1.0
					// for the simulation it is useful to format these values so
					// that you can see what each value means
					float xVfloat = output.get(0).floatValue() * 2.0f - 1.0f;
					float yVfloat = output.get(1).floatValue() * 2.0f - 1.0f;

					int xVelocity = Math.round(xVfloat); // -1 .. move one field
															// in negative
															// horizontal
															// direction
															// 0 .. do not move
															// in any horizontal
															// direction
															// 1 .. move one
															// field in positive
															// horizontal
															// direction
					int yVelocity = Math.round(yVfloat); // -1 .. move one field
															// in negative
															// vertical
															// direction
															// 0 .. do not move
															// in any vertical
															// direction
															// 1 .. move one
															// field in positive
															// vertical
															// direction

					// move the agent (only if the place, that he wants to move
					// is not occupied by another agent)
					if /*   */(xVelocity == 0/* */&& yVelocity == -1/**/
							&& northfree /**/&& /*                    */a.ypos > 0) {
						a.xpos += 0;
						a.ypos += -1;
					} else if (xVelocity == 1/* */&& yVelocity == -1/**/
							&& northeastfree && a.xpos < f.width - 1
							&& a.ypos > 0) {
						a.xpos += 1;
						a.ypos += -1;
					} else if (xVelocity == 1/* */&& yVelocity == 0/* */
							&& eastfree /* */&& a.xpos < f.width - 1) {
						a.xpos += 1;
						a.ypos += 0;
					} else if (xVelocity == 1/* */&& yVelocity == 1/* */
							&& southeastfree && a.xpos < f.width - 1
							&& a.ypos < f.height - 1) {
						a.xpos += 1;
						a.ypos += 1;
					} else if (xVelocity == 0/* */&& yVelocity == 1/* */
							&& southfree /**/&& /*                    */a.ypos < f.height - 1) {
						a.xpos += 0;
						a.ypos += 1;
					} else if (xVelocity == -1/**/&& yVelocity == 1/* */
							&& southwestfree && a.xpos > 0 /*      */
							&& a.ypos < f.height - 1) {
						a.xpos += -1;
						a.ypos += 1;
					} else if (xVelocity == -1/**/&& yVelocity == 0/* */
							&& westfree /* */&& a.xpos > 0/*      */) {
						a.xpos += -1;
						a.ypos += 0;
					} else if (xVelocity == -1/**/&& yVelocity == -1/**/
							&& northwestfree && a.xpos > 0 /*      */&& a.ypos > 0) {
						a.xpos += -1;
						a.ypos += -1;
					}

					// if /* */(xVelocity >= 0.9 && a.xpos < width - 1 ) a.xpos
					// += 1;
					// else if (xVelocity <= -0.9 && a.xpos > 0 /* */) a.xpos -=
					// 1;
					// if /* */(yVelocity >= 0.9 && a.ypos < height - 1) a.ypos
					// += 1;
					// else if (yVelocity <= -0.9 && a.ypos > 0 /* */) a.ypos -=
					// 1;
					for (int n = 0; n < EmergencyExits.length
							&& !a.hasReachedExit; n++) {
						if (a.xpos == EmergencyExits[n].xpos
								&& a.ypos == EmergencyExits[n].ypos)
							a.hasReachedExit = true;
						else
							/*                                                                 */a.hasReachedExit = false;
					}
				}
			}
			boolean finished = true;

			for (agent a : agents) {
				if (!a.hasReachedExit)
					finished = false;
			}
			if (finished && stepfinished > step)
				stepfinished = step;
		}
		double Fitness = (double) (steps - stepfinished) / (double) steps;// *
																			// numberofAgents;
		for (agent a : agents) {
			double minimumDistance = FastMath.hypot(EmergencyExits[0].xpos
					- a.xpos, EmergencyExits[0].ypos - a.ypos);
			for (int e = 0; e < EmergencyExits.length; e++) {
				double Distance = FastMath.hypot(EmergencyExits[e].xpos
						- a.xpos, EmergencyExits[e].ypos - a.ypos);
				if (Distance < minimumDistance) {
					minimumDistance = Distance;
				}
			}
			Fitness += -minimumDistance;// / FastMath.hypot(f.width, f.height);
		}
		return (Fitness);// / numberofAgents);
	}

	public class agent {
		public AbstractRepresentation representation;
		public int xpos;
		public int ypos;
		public boolean hasReachedExit;

		/**
		 * @param r
		 *            Representation for this Agent
		 * @param x
		 *            defines the x position of this Agent
		 * @param y
		 *            defines the y position of this Agent
		 */
		public agent(AbstractRepresentation r, int x, int y, boolean reachedExit) {
			representation = r;
			xpos = x;
			ypos = y;
			hasReachedExit = reachedExit;
		}

		public agent(AbstractRepresentation r, boolean reachedExit) {
			representation = r;
			hasReachedExit = reachedExit;
		}
	}

	public class Exit {
		public int xpos;
		public int ypos;
	}

	public class blockade {
		public int xpos;
		public int ypos;
	}

	@Override
	public double getMaximumFitness() {
		// TODO Auto-generated method stub
		return Double.MAX_VALUE;
	}
}
