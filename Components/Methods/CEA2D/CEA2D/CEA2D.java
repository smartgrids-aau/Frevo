package CEA2D;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import utils.NESRandom;
import utils.StatKeeper;
import core.AbstractMethod;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.ComponentType;
import core.ComponentXMLData;
import core.PopulationDiversity;
import core.ProblemXMLData;
import core.XMLFieldEntry;
import core.XMLMethodStep;
import frevoutils.JGridMap.Display;
import frevoutils.JGridMap.JGridMap;

/**
 * The class SSEA2D (Spatially Structured Evolutionary Algorithm 2D) is a
 * evolutionary algorithm that considers only the neighbors of every
 * representation to decide if the representation remains in the next
 * generation, mutates, creates an offspring with another representation or is
 * replaced by a totally new representation. The representations are arranged in
 * a two dimensional grid, where every representation has 8 neighbors.
 * 
 * @author Thomas Dittrich
 * 
 */
public class CEA2D extends AbstractMethod {

	/**
	 * Parameters of the method for current experiment
	 */
	private Parameters parameters;

	private StatKeeper bfitness;
	private StatKeeper numSimulations;

	// Statistics about population diversity
	private StatKeeper diversity;
	private StatKeeper maxDiversity;
	private StatKeeper minDiversity;
	private StatKeeper standardDeviation;

	private StatKeeper numElite;
	private StatKeeper numMutate;
	private StatKeeper numXOver;
	private StatKeeper numRenew;

	private StatKeeper effectivityElite;
	private StatKeeper effectivityMutate;
	private StatKeeper effectivityXOver;
	private StatKeeper effectivityRenew;

	private Population pop;

	private double minfitness;

	private boolean iniOK = false;

	Display gridFrame;
	JGridMap fitnessgrid;

	public final static Color gray = new Color(153, 153, 153); // gray color for obstacles
	public final static Color white = new Color(255, 255, 255); // define white color
	public final static Color black = new Color(0, 0, 0); // define black color

	public int[][] obstacle_array;

	/* Constructs a new SSEA2D object */
	public CEA2D(NESRandom random) {
		super(random);
		parameters = new Parameters(this);
	}

	public int[][] getObstaclePattern() {
		return obstacle_array;
	}

	public void createObstaclePattern() {
		// define patterns for obstacles (at least 10*10 grid for case 1, 2 and 3!)

		obstacle_array = new int[parameters.POPULATIONFIELDSIZE_WIDTH][parameters.POPULATIONFIELDSIZE_HEIGHT];

		switch (parameters.OBSTACLE_PATTERN) {

		case 1:
			if (parameters.POPULATIONFIELDSIZE_WIDTH >= 10 && parameters.POPULATIONFIELDSIZE_HEIGHT >= 10) {
				for (int i = 0; i < 7; i++) {
					obstacle_array[i][0] = 1000;
				}
				for (int i = 1; i < 6; i++) {
					obstacle_array[i][1] = 1000;
				}
				for (int i = 2; i < 5; i++) {
					obstacle_array[i][2] = 1000;
				}
				obstacle_array[3][3] = 1000;

				for (int i = 5; i < 10; i++) {
					obstacle_array[i][9] = 1000;
				}
				for (int i = 6; i < 9; i++) {
					obstacle_array[i][8] = 1000;
				}
				obstacle_array[7][7] = 1000;
			}

			else {
				for (int i = 0; i < parameters.POPULATIONFIELDSIZE_WIDTH; i++) {
					for (int j = 0; j < parameters.POPULATIONFIELDSIZE_HEIGHT; j++) {
						obstacle_array[i][j] = 0;
					}
				}
			}
			break;

		case 2:
			// define obstacle pattern 2
			if (parameters.POPULATIONFIELDSIZE_WIDTH >= 10 && parameters.POPULATIONFIELDSIZE_HEIGHT >= 10) {
				for (int i = 0; i < 10; i++) {
					obstacle_array[i][5] = 1000;
				}
			}

			else {
				for (int i = 0; i < parameters.POPULATIONFIELDSIZE_WIDTH; i++) {
					for (int j = 0; j < parameters.POPULATIONFIELDSIZE_HEIGHT; j++) {
						obstacle_array[i][j] = 0;
					}
				}
			}
			break;

		case 3:
			if (parameters.POPULATIONFIELDSIZE_WIDTH >= 10 && parameters.POPULATIONFIELDSIZE_HEIGHT >= 10) {
				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 4; j++) {
						obstacle_array[j][i] = 1000;
					}
				}

				for (int i = 6; i < 10; i++) {
					for (int j = 5; j < 10; j++) {
						obstacle_array[j][i] = 1000;
					}
				}
			} else {
				for (int i = 0; i < parameters.POPULATIONFIELDSIZE_WIDTH; i++) {
					for (int j = 0; j < parameters.POPULATIONFIELDSIZE_HEIGHT; j++) {
						obstacle_array[i][j] = 0;
					}
				}
			}

			break;

		case 4:
		default:
			for (int r = 0; r < parameters.OBSTACLES; r++) {
				NESRandom rand = parameters.getGenerator(); // define random numbers for randomly distributed obstacles
				int obs_x = 0; // random numbers for rows
				int obs_y = 0; // random numbers for columns

				do {
					obs_x = rand.nextInt(parameters.POPULATIONFIELDSIZE_WIDTH);
					obs_y = rand.nextInt(parameters.POPULATIONFIELDSIZE_HEIGHT);

				} while (obstacle_array[obs_x][obs_y] == 1000);

				obstacle_array[obs_x][obs_y] = 1000;
			}
			break;
		}
	}

	public void initialize() {
		parameters.initialize(getProperties());

		// show fitness grid if in GUI mode
		if (FrevoMain.isFrevoWithGraphics()) {
			// initialize fitness grid
			if (gridFrame == null) {
				gridFrame = new Display(1, 1, "Spatial Fitness");
				// gridFrame = new Display(parameters.POPULATIONFIELDSIZE_LENGTH,
				// parameters.POPULATIONFIELDSIZE_WIDTH, "Spatial Fitness");
				gridFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				gridFrame.setLocation(0, 0);
			}
			if (fitnessgrid == null) {
				fitnessgrid = new JGridMap(parameters.POPULATIONFIELDSIZE_WIDTH * 20,
						parameters.POPULATIONFIELDSIZE_HEIGHT * 20, parameters.POPULATIONFIELDSIZE_WIDTH,
						parameters.POPULATIONFIELDSIZE_HEIGHT, 2);

				// a condition, if we have more obstacles than in the area
				if (parameters.OBSTACLES >= parameters.POPULATIONFIELDSIZE_HEIGHT
						* parameters.POPULATIONFIELDSIZE_WIDTH) {
					System.out.println("Number of obstacles is too high for the defined grid!");
					System.out.println(
							"Please enter next time a lower number than populationsize_length * populationsize_width!");
					parameters.OBSTACLES = 0;
					System.out.println("Obstacles in the grid: " + parameters.OBSTACLES);
					return;
				}

				else {
					System.out.println("Obstacles in the grid: " + parameters.OBSTACLES);
				}

				// initialize color scale for fitness
				// 0...white
				// 1...red
				// 50...yellow
				// 99...green
				// 1000...gray

				for (int i = 0; i < 100; i++) {
					int r = 0;
					int g = 0;

					if (i < 50) {
						r = 255;
						g = i * 255 / 50;
					} else {
						r = 255 - (i - 50) * 255 / 50;
						g = 255;
					}

					int color = r * 65536 + g * 256;

					fitnessgrid.addColorToScale(i, new Color(color));
				}
				fitnessgrid.addColorToScale(1000, gray); // define gray color for obstacles

			}

			gridFrame.setLayout(new GridLayout(1, 1));
			gridFrame.add(fitnessgrid);
			gridFrame.pack();
			gridFrame.setVisible(true);
		}
	}

	@Override
	public void runOptimization(ProblemXMLData problemData, ComponentXMLData representationData,
			ComponentXMLData rankingData, Hashtable<String, XMLFieldEntry> properties) {

		// initialize evolution
		initialize();

		pop = new Population(representationData, parameters, problemData.getRequiredNumberOfInputs(),
				problemData.getRequiredNumberOfOutputs(), this);

		createStatistics();

		try {
			Step step = new Step(problemData, rankingData);

			// Iterate through generations
			for (int generation = 0; generation < parameters.GENERATIONS; generation++) {

				step.setGeneration(generation);

				if (!evolve(step)) {
					break;
				}
			}

		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// indicate final progress
		setProgress(100);

		// closes the window which holds the fitness grid
		if (FrevoMain.isFrevoWithGraphics()) {
			if (gridFrame != null) {
				gridFrame.dispose();
			}
			fitnessgrid = null;
			gridFrame = null;
		}
	}

	@Override
	public void continueOptimization(ProblemXMLData problemData, ComponentXMLData representationData,
			ComponentXMLData rankingData, Hashtable<String, XMLFieldEntry> properties, Document doc) {
		// initialize evolution
		initialize();

		// record the best fitness over the evolution
		Node dpopulations = doc.selectSingleNode("/frevo/populations");
		double best_fitness = Double.parseDouble(dpopulations.valueOf("./@best_fitness"));
		int lastGeneration = Integer.parseInt(dpopulations.valueOf("./@generation"));
		long randomseed = Long.parseLong(dpopulations.valueOf("./@randomseed"));
		getRandom().setSeed(randomseed);

		// load initial population(s)
		ArrayList<ArrayList<AbstractRepresentation>> loadedPops = loadFromXML(doc);
		if (loadedPops.size() != 1) {
			System.err.println("Couldn't restore population from XML file");
			return;
		}

		pop = new Population(representationData, parameters, problemData.getRequiredNumberOfInputs(),
				problemData.getRequiredNumberOfOutputs(), loadedPops.get(0), doc);

		createStatistics();

		try {
			// evolve the whole population
			Step step = new Step(problemData, rankingData);
			pop.evolve(step);
			step.setBestFitness(best_fitness);

			// Iterate through generations
			for (int generation = lastGeneration + 1; generation < parameters.GENERATIONS; generation++) {

				step.setGeneration(generation);

				if (!evolve(step)) {
					break;
				}
			}

		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// indicate final progress
		setProgress(100);

		// closes the window which holds the fitness grid
		if (FrevoMain.isFrevoWithGraphics()) {
			gridFrame.dispose();
			fitnessgrid = null;
			gridFrame = null;
		}
	}

	private boolean evolve(Step step) throws Exception {
		// set progress
		setProgress((float) step.getGeneration() / (float) parameters.GENERATIONS);

		boolean doSave = false;

		AbstractRanking ranking = step.getRanking();
		// evaluates all members and calculates the best fitness
		ArrayList<AbstractRepresentation> memberrepresentations = pop.getMembers();

		int numSims = ranking.sortCandidates(memberrepresentations, step.getProblemData(),
				new NESRandom(generator.getSeed()));

		bfitness.add(memberrepresentations.get(0).getFitness());

		if (memberrepresentations.get(0).getFitness() > step.getBestFitness()) {
			step.setBestFitness(memberrepresentations.get(0).getFitness());
			doSave = true;
		}

		numSimulations.add(numSims);

		PopulationDiversity diversityCalc = new PopulationDiversity(pop.getMembers());
		diversity.add(diversityCalc.getAverageDiversity());
		maxDiversity.add(diversityCalc.getMaxDiversity());
		minDiversity.add(diversityCalc.getMinDiversity());
		standardDeviation.add(diversityCalc.getStandardDeviation());

		numElite.add(pop.getNumElite());
		numMutate.add(pop.getNumMutate());
		numXOver.add(pop.getNumXOver());
		numRenew.add(pop.getNumRenew());

		effectivityElite.add(pop.getEffectivityElite());
		effectivityMutate.add(pop.getEffectivityMutate());
		effectivityXOver.add(pop.getEffectivityXOver());
		effectivityRenew.add(pop.getEffectivityRenew());

		if (FrevoMain.isFrevoWithGraphics()) {
			// shows the fitness of the whole population as a grid of
			// colors, where red means bad fitness and green means good
			// fitness
			updatefitnessgrid();
		}

		// save periodically
		if ((parameters.SAVEINTERVAL != 0) && (step.getGeneration() % parameters.SAVEINTERVAL == 0)) {
			doSave = true;
		}

		// save last generation
		if (step.getGeneration() == parameters.GENERATIONS - 1) {
			doSave = true;
		}

		String fitnessstring;
		if (step.getProblemData().getComponentType() == ComponentType.FREVO_PROBLEM) {
			fitnessstring = " (" + step.getBestFitness() + ")";
		} else {
			// multiproblem
			fitnessstring = "";
		}

		long currentActiveSeed = getRandom().getSeed();
		String fileName = getFileName(step.getProblemData(), step.getGeneration(), fitnessstring);
		Element xmlLastState = saveResults(step.getGeneration());
		xmlLastState.addAttribute("best_fitness", String.valueOf(step.getBestFitness()));
		// save the last state of evaluation
		XMLMethodStep state = new XMLMethodStep(fileName, xmlLastState, this.seed, currentActiveSeed);
		setLastResults(state);

		if (doSave) {
			FrevoMain.saveResult(fileName, xmlLastState, this.seed, currentActiveSeed);
		}

		if (step.getBestFitness() >= step.getMaxFitness()) {
			// fill up remaining space in statkeeper with last value
			if (bfitness.length() != parameters.GENERATIONS) {
				int dif = parameters.GENERATIONS - bfitness.length();
				double lastvalue = bfitness.getValues().get(bfitness.length() - 1);
				for (int i = 0; i < dif; i++) {
					bfitness.add(lastvalue);
				}
			}

			return false;
		}

		if (handlePause()) {
			// closes the window which holds the fitnessgrid
			if (gridFrame != null)
				gridFrame.dispose();
			fitnessgrid = null;
			gridFrame = null;
			return false;
		}

		// mutates all members of the population according to the
		// specified mutation rules (only if it's not the last
		// generation)
		if (step.getGeneration() != parameters.GENERATIONS - 1) {
			pop.evolve(step);
		}

		return true;
	}

	private String getFileName(ProblemXMLData problemData, int generation, String fitnessstring) {
		DecimalFormat fm = new DecimalFormat("000");

		return problemData.getName() + "_g" + fm.format(generation) + fitnessstring;
	}

	private static ArrayList<AbstractRepresentation> createList(Node nd) {
		ArrayList<AbstractRepresentation> result = new ArrayList<AbstractRepresentation>();

		ComponentXMLData representation = FrevoMain.getSelectedComponent(ComponentType.FREVO_REPRESENTATION);

		try {
			List<?> npops = nd.selectNodes("./*");
			Iterator<?> it = npops.iterator();
			int size = npops.size();
			int currentIndex = 0;
			while (it.hasNext()) {
				// set loading progress
				FrevoMain.setLoadingProgress((float) currentIndex / size);

				Node net = (Node) it.next();
				size--;
				if (size % 10 == 0)
					size = size + (2 * 2 - 4);
				AbstractRepresentation member = representation.getNewRepresentationInstance(0, 0, null);
				member.loadFromXML(net);
				result.add(member);

				currentIndex++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/** Saves all population data to a new XML element and returns it. */
	public Element saveResults(int generation) {
		Element dpopulations = DocumentFactory.getInstance().createElement("populations");

		dpopulations.addAttribute("count", String.valueOf(1));
		dpopulations.addAttribute("generation", String.valueOf(generation));
		dpopulations.addAttribute("randomseed", String.valueOf(this.getSeed()));

		Element dpop = dpopulations.addElement("population");
		dpop.addAttribute("randomNeighborhoodSeed", String.valueOf(pop.randomNeighborhoodSeed));

		// sort candidates with decreasing fitness
		ArrayList<AbstractRepresentation> members = pop.getMembers();

		for (AbstractRepresentation n : members) {
			n.exportToXmlElement(dpop);
		}

		return dpopulations;
	}

	@Override
	public ArrayList<ArrayList<AbstractRepresentation>> loadFromXML(Document doc) {
		// final list to be returned
		ArrayList<ArrayList<AbstractRepresentation>> populations = new ArrayList<ArrayList<AbstractRepresentation>>();

		// get population root node
		Node dpopulations = doc.selectSingleNode("/frevo/populations");

		// get number of current generation
		int currentGeneration = Integer.parseInt(dpopulations.valueOf("./@generation"));

		// get population size
		List<?> npops = dpopulations.selectNodes(".//population");
		Iterator<?> it = npops.iterator();
		while (it.hasNext()) {
			Node pop = (Node) it.next();
			ArrayList<AbstractRepresentation> pops = createList(pop);
			populations.add(pops);
		}
		// Load the number of generations
		XMLFieldEntry gensize = getProperties().get("generations");
		if (gensize != null) {
			int generations = Integer.parseInt(gensize.getValue());
			// TODO check max fitness also
			// set boolean value which shows possibility of continuation of experiment
			// if maximum number of generations hasn't been reached.
			setCanContinue(currentGeneration + 1 < generations);
		}

		return populations;
	}

	/**
	 * displays the fitness of the actual population in a Grid
	 */

	public void updatefitnessgrid() {
		// determine maximum and minimum fitness
		ArrayList<AbstractRepresentation> rep = pop.getMembers();
		double maxfitness = rep.get(0).getFitness();
		if (!iniOK) {
			minfitness = rep.get(0).getFitness();
		}
		for (AbstractRepresentation r : rep) {
			if (r.isEvaluated()) {
				if (r.getFitness() > maxfitness) {
					maxfitness = r.getFitness();
				} else if (r.getFitness() < minfitness) {
					minfitness = r.getFitness();
				}
			}
		}

		// normalize fitness between 0 and 100
		double k = 100.0 / (maxfitness - minfitness);
		double d = -(minfitness * k);
		// System.out.println("H: " + parameters.POPULATIONFIELDSIZE_HEIGHT + " W: " +
		// parameters.POPULATIONFIELDSIZE_WIDTH);
		// int[][] fitnessarray = new
		// int[parameters.POPULATIONFIELDSIZE_WIDTH][parameters.POPULATIONFIELDSIZE_HEIGHT];

		// 3-dimensional array for the fitness grid and obstacle grid
		int[][][] three_dim = new int[parameters.POPULATIONFIELDSIZE_WIDTH][parameters.POPULATIONFIELDSIZE_HEIGHT][2];

		for (int y = 0; y < parameters.POPULATIONFIELDSIZE_HEIGHT; y++) {
			for (int x = 0; x < parameters.POPULATIONFIELDSIZE_WIDTH; x++) {
				if (pop.obs_pattern[x][y] != 1000)
					if (rep.get(pop.obs_pattern[x][y]).isEvaluated()) {
						int normfitness = (int) (rep.get(pop.obs_pattern[x][y]).getFitness() * k + d);

						three_dim[x][y][0] = normfitness;
						fitnessgrid.repaint();
					}

					else {
						three_dim[x][y][0] = 0;
					}
				else
					three_dim[x][y][1] = pop.obs_pattern[x][y];
			}
		}
		// show normalized fitness in fitness grid
		fitnessgrid.setData(three_dim);
		fitnessgrid.repaint();
		iniOK = true;
	}

	private void createStatistics() {
		// bfitness = new StatKeeper(true, "Best Fitness ("+ FrevoMain.getCurrentRun() +
		// ")", "Generations");
		bfitness = new StatKeeper(true, "Best fitness", "Generations");

		numSimulations = new StatKeeper(true, "numSimulations" + FrevoMain.getCurrentRun(), "Generations");

		diversity = new StatKeeper(true, "Diversity", "Generations");
		maxDiversity = new StatKeeper(true, "Max. diversity", "Generations");
		minDiversity = new StatKeeper(true, "Min. diversity", "Generations");
		standardDeviation = new StatKeeper(true, "Deviation", "Generations");

		numElite = new StatKeeper(true, "number of Elite", "Generations");
		numMutate = new StatKeeper(true, "number of Mutation", "Generations");
		numXOver = new StatKeeper(true, "number of XOver", "Generations");
		numRenew = new StatKeeper(true, "number of Renew", "Generations");

		effectivityElite = new StatKeeper(true, "effektivity of Elite", "Generations");
		effectivityMutate = new StatKeeper(true, "effektivity of Mutation", "Generations");
		effectivityXOver = new StatKeeper(true, "effektivity of XOver", "Generations");
		effectivityRenew = new StatKeeper(true, "effektivity of Renew", "Generations");

		// Collect best fitness
		FrevoMain.addStatistics(bfitness, true);

		// Collect diversity
		FrevoMain.addStatistics(diversity, true);
	}
}
