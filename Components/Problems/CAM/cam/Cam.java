package cam;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import main.FrevoMain;
import net.jodk.lang.FastMath;
import core.AbstractBulkRepresentation;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ProblemXMLData;
import core.XMLFieldEntry;

public class Cam extends AbstractSingleProblem {

	public boolean use1ofC = false;

	public static boolean USENEWFITNESS = false;

	/** Input model where all 8 neighbors are considered but only their colors */
	public static final int COLORMOOREINPUT = 2;
	/**
	 * Input model where all 8 neighbors' outputs are considered and also own
	 * previous as well
	 */
	public static final int LOTOFINPUTS = 4;

	/** Name of the image used for loading */
	private static String imageName = null;
	private static BufferedImage iconImg = null;
	/** Width of the image */
	private static int WIDTH;
	/** Height of the image */
	private static int HEIGHT;
	/** Array of colors found on the image */
	private static ArrayList<Integer> colArr = new ArrayList<Integer>();

	private static double[][] weights;

	// TODO calculate for the missing values
	private static double[] errorfactor = { 0.0, 0.0, 0.5, 1.0 / 6.0, -0.0,
			-0.0, -0.0, -0.0, 0.11 };

	private Cell[][] pool;
	public int SIMSTEPS;
	public boolean bestsolutionOutOfNSteps;
	public static boolean smartFitnessModel;
	public int fitness_steps;
	/**
	 * Plane is a torroid meaning that cells at the border are directly
	 * connected to the cells on the other side
	 */
	public boolean istorroid;
	/** Number of different colors */
	public static int COLORS;
	public static final int SCALE = 10;

	protected static int WORSTFITNESS = 0;
	private int bestStep = 0;

	/** Candidate solution used for evaluation */
	AbstractRepresentation candidate;
	/** Input model used for evaluation */
	private InputModel INPUTMODEL;

	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {

		// Adjust to inputmodel
		XMLFieldEntry inp = properties.get("inputmodel");
		XMLFieldEntry outputn = requirements.get("outputnumber");
		XMLFieldEntry inputn = requirements.get("inputnumber");

		XMLFieldEntry use1ofc = properties.get("1-of-C");
		boolean oneOfC = false;
		if (use1ofc != null)
			oneOfC = Boolean.parseBoolean(use1ofc.getValue());

		if (oneOfC) {
			// load image to know the number of colors
			String newImageName = properties.get("picture").getValue();
			String filename = getFileNameFromPath(newImageName);
			if (!filename.equals(imageName)) {
				boolean success = Cam.loadimage(newImageName);
				if (!success) {
					// try to load it at default place
					String fname = getFileNameFromPath(newImageName);

					// add default dir
					String pre = FrevoMain.getInstallDirectory()
							+ File.separator + "Components" + File.separator
							+ "Problems" + File.separator + "CAM"
							+ File.separator;

					// pre = pre.substring(1);

					pre = pre.replace('/', File.separatorChar);
					pre = pre.replace('\\', File.separatorChar);

					boolean succ = Cam.loadimage(pre + fname);
					if (!succ) {
						System.err.println("Failed to load image file at "
								+ newImageName);
						// Send signal to Frevo to stop executing
					} else {
						imageName = fname;
					}
				} else {
					imageName = filename;
				}
			}
		}

		INPUTMODEL = InputModel.valueOf(inp.getValue());

		switch (INPUTMODEL) {
		case COLOR_OUTPUT_V_NEUMANN:
			if (oneOfC) {
				// 4*C color, 4*C output
				inputn.setValue(Integer.toString(8 * COLORS));
			} else {
				// 4 color, 4 output
				inputn.setValue(Integer.toString(8));
			}
			// 4 output, 1 own color
			outputn.setValue(Integer.toString(5));
			break;
		case OUTPUT_MOORE:
			// 8 input
			inputn.setValue(Integer.toString(8));
			// 8 output, 1 own color
			outputn.setValue(Integer.toString(9));
			break;
		case COLOR_OUTPUT_MOORE:
			// 8 input, 8*C color
			if (oneOfC) {
				inputn.setValue(Integer.toString(8 + 8 * COLORS));
			} else {
				// 8 input, 8 color
				inputn.setValue(Integer.toString(16));
			}
			// 8 output + 1 own color
			outputn.setValue(Integer.toString(9));
			break;
		case OUTPUT_V_NEUMANN:
			// 4 input
			inputn.setValue(Integer.toString(4));
			// 4 output, 1 own color
			outputn.setValue(Integer.toString(5));
			break;
		case COLOR_MOORE:
			if (oneOfC) {
				// 8*C output
				inputn.setValue(Integer.toString(8 * COLORS));
			} else {
				// 8 output
				inputn.setValue(Integer.toString(8));
			}

			// 1 own color
			outputn.setValue(Integer.toString(1));
			break;
		case COLOR_V_NEUMANN:
			if (oneOfC) {
				// 4*C output
				inputn.setValue(Integer.toString(4 * COLORS));
			} else {
				// 4 output
				inputn.setValue(Integer.toString(4));
			}

			// 1 own color
			outputn.setValue(Integer.toString(1));
			break;
		}

		return requirements;
	}

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {

		return runCandidateEvaluation(candidate, null);
	}

	private double runCandidateEvaluation(AbstractRepresentation candidate,
			ArrayList<Float> novelties) {
		// load parameters
		this.candidate = candidate;

		INPUTMODEL = InputModel.valueOf(getProperties().get("inputmodel")
				.getValue());

		SIMSTEPS = Integer.parseInt(getProperties().get("simulation_steps")
				.getValue());

		fitness_steps = Integer.parseInt(getProperties().get("fitness_steps")
				.getValue());

		if (fitness_steps < 0) {
			System.err
					.println("fitness_steps must be at least 1; changed to 1!");
			fitness_steps = 1;
		}
		if (fitness_steps > SIMSTEPS) {
			System.err
					.println("fitness_steps must not be larger than number of simulation steps; changed to "
							+ SIMSTEPS + "!");
			fitness_steps = SIMSTEPS;
		}

		bestsolutionOutOfNSteps = Boolean.parseBoolean(getProperties().get(
				"bestsolutionOutOfNSteps").getValue());

		smartFitnessModel = Boolean.parseBoolean(getProperties().get(
				"smartFitnessModel").getValue());

		istorroid = Boolean.parseBoolean(this.getProperties().get("istorroid")
				.getValue());

		XMLFieldEntry use1ofc = this.getProperties().get("1-of-C");

		if (use1ofc != null)
			use1ofC = Boolean.parseBoolean(use1ofc.getValue());

		// simulate and evaluate

		double fitness = calcSim(candidate, SIMSTEPS, false, novelties);

		return fitness;
	}

	private double calcFitness() {
		double fitness = 0;
		for (int x = 0; x < WIDTH; x++)
			for (int y = 0; y < HEIGHT; y++) {
				int diff = pool[x][y].color - pool[x][y].targetcolor;
				fitness += diff * diff;
			}
		return 1.0 - (fitness / WORSTFITNESS);
	}

	private double calcWeightedFitness() {
		double fitness = 0;

		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				double owndiff = Math.abs(pool[x][y].color
						- pool[x][y].targetcolor);
				owndiff = owndiff * owndiff;

				fitness += (owndiff * weights[x][y]);
			}
		}

		// old fitness, that works but shows too high values
		if (USENEWFITNESS) {
			// new fitness that often goes below 0
			double trivial = WORSTFITNESS * errorfactor[COLORS];
			fitness /= trivial;
			return 1.0 - fitness;
		}

		return 1.0 - fitness / (double) WORSTFITNESS;
	}

	public static synchronized boolean loadimage(String newImageName) {
		// Load image only if it is new

		try {
			colArr.clear();
			System.out.println("Trying to load image at " + newImageName
					+ "... ");
			File imagefile = FrevoMain.loadSystemIndependentFile(newImageName);

			iconImg = ImageIO.read(imagefile);
			WIDTH = iconImg.getWidth();
			HEIGHT = iconImg.getHeight();

			// Build color list
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					int shortColor = rgbToShortColor(iconImg.getRGB(x, y));
					if (!colArr.contains(shortColor)) {
						colArr.add(shortColor);
					}
				}
			}

			COLORS = colArr.size();
			Collections.sort(colArr);

			// calculate weights matrix
			weights = new double[WIDTH][HEIGHT];
			double sumweight = 0;

			double colMinusOneSqared = FastMath.pow(COLORS - 1, 2);

			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					double weight = 0;
					int counter = 0;
					// neighbors, von neumann
					if (x != 0) {
						weight += FastMath.pow(Math.abs(colArr
								.indexOf(rgbToShortColor(iconImg.getRGB(x, y)))
								- colArr.indexOf(rgbToShortColor(iconImg
										.getRGB(x - 1, y)))), 2);
						counter++;
					}
					if (x != WIDTH - 1) {
						weight += FastMath.pow(Math.abs(colArr
								.indexOf(rgbToShortColor(iconImg.getRGB(x, y)))
								- colArr.indexOf(rgbToShortColor(iconImg
										.getRGB(x + 1, y)))), 2);
						counter++;
					}
					if (y != 0) {
						weight += FastMath.pow(Math.abs(colArr
								.indexOf(rgbToShortColor(iconImg.getRGB(x, y)))
								- colArr.indexOf(rgbToShortColor(iconImg
										.getRGB(x, y - 1)))), 2);
						counter++;
					}
					if (y != HEIGHT - 1) {
						weight += FastMath.pow(Math.abs(colArr
								.indexOf(rgbToShortColor(iconImg.getRGB(x, y)))
								- colArr.indexOf(rgbToShortColor(iconImg
										.getRGB(x, y + 1)))), 2);
						counter++;
					}

					weight = (weight / (counter * colMinusOneSqared));
					weight++;

					weights[x][y] = weight;
					sumweight += weight;
				}
			}

			// normalize weights
			sumweight /= (double) (WIDTH * HEIGHT);

			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					weights[x][y] /= sumweight;
				}
			}

			WORSTFITNESS = (COLORS - 1) * (COLORS - 1) * WIDTH * HEIGHT;

			/*
			 * WORSTFITNESS = 0; for (int x = 0; x < WIDTH; x++) { for (int y =
			 * 0; y < HEIGHT; y++) { int diff =
			 * Math.max(colArr.indexOf(rgbToShortColor(iconImg.getRGB(x, y))),
			 * COLORS -1 - colArr.indexOf(rgbToShortColor(iconImg.getRGB(x,
			 * y)))); WORSTFITNESS += diff * diff; } }
			 */

			System.out
					.println("Image (" + WIDTH + "x" + HEIGHT
							+ ") successfully loaded with " + COLORS
							+ " colors. Maximum theoretical deviation: "
							+ WORSTFITNESS);
		} catch (IOException e) {
			// System.err.println("Failed to load image file at " +
			// newImageName);
			// e.printStackTrace();
			return false;
		}

		if (COLORS == 0) {
			System.err.println("Error no colors defined!");
			return false;
		}

		return true;
	}

	private static String getFileNameFromPath(String filepath) {
		filepath = filepath.replace('/', File.separatorChar);
		filepath = filepath.replace('\\', File.separatorChar);
		int lastseparator = filepath.lastIndexOf(File.separator);

		if (lastseparator != -1) {
			filepath = filepath.substring(lastseparator + 1);
		}

		return filepath;
	}

	private int step;

	private double calcSim(AbstractRepresentation candidate, int steps,
			boolean printResult, ArrayList<Float> novelties) {

		// Load image only if it is new
		String newImageName = getProperties().get("picture").getValue();
		String filename = getFileNameFromPath(newImageName);
		if (!filename.equals(imageName)) {
			boolean success = Cam.loadimage(newImageName);
			if (!success) {
				// try to load it at default place
				String fname = getFileNameFromPath(newImageName);

				// add default dir
				String pre = FrevoMain.getInstallDirectory() + File.separator
						+ "Components" + File.separator + "Problems"
						+ File.separator + "CAM" + File.separator;

				// pre = pre.substring(1);

				pre = pre.replace('/', File.separatorChar);
				pre = pre.replace('\\', File.separatorChar);

				boolean succ = Cam.loadimage(pre + fname);
				if (!succ) {
					System.err.println("Failed to load image file at "
							+ newImageName);
					// Send signal to Frevo to stop executing
				} else {
					imageName = fname;
				}
			} else {
				imageName = filename;
			}
		}

		// create pool
		pool = new Cell[WIDTH][HEIGHT];
		candidate.reset();

		ProblemXMLData problemData = (ProblemXMLData) getXMLData();
		int outputnumber = problemData.getRequiredNumberOfOutputs();

		if (!candidate.isBulkRepresentation()) {
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					pool[x][y] = new Cell(
							candidate.clone(),
							colArr.indexOf(rgbToShortColor(iconImg.getRGB(x, y))),
							outputnumber - 1);
				}
			}
		} else {
			AbstractBulkRepresentation candidateSet = (AbstractBulkRepresentation) candidate;
			// bulk representation, checkerboard style
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					AbstractRepresentation cand;

					if ((x + y) % 2 == 1)
						cand = candidateSet.getRepresentation(0);
					else
						cand = candidateSet.getRepresentation(1);
					pool[x][y] = new Cell(
							cand.clone(),
							colArr.indexOf(rgbToShortColor(iconImg.getRGB(x, y))),
							outputnumber - 1);
				}
			}

		}

		double fitness = 0;
		bestStep = 0;
		if (bestsolutionOutOfNSteps)
			fitness = Integer.MIN_VALUE;

		// int NOVELTY__VECTOR_SIZE = steps;
		int[][] colors_novelty = new int[WIDTH][HEIGHT];

		// Run simulation
		for (step = 0; step < steps; step++) {

			// calculate the new output of all cells
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					calcCell(x, y);
				}
			}

			// update cells to their new output values
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					pool[x][y].update();
				}
			}

			double stepFitness;
			if (smartFitnessModel)
				stepFitness = calcWeightedFitness();
			else
				stepFitness = calcFitness();

			// record novelty if provided
			/*
			 * if (novelties != null) { if (novelties.size() ==
			 * NOVELTY__VECTOR_SIZE) novelties.remove(0);
			 * 
			 * novelties.add((float) stepFitness); }
			 */

			if (bestsolutionOutOfNSteps) {
				// the best solution within the defined fitness steps defines
				// the fitness
				if (stepFitness > fitness) {
					fitness = stepFitness;
					bestStep = step;

					// record novelty
					for (int x = 0; x < WIDTH; x++) {
						for (int y = 0; y < HEIGHT; y++) {
							colors_novelty[x][y] = pool[x][y].color;
						}
					}

				}
			} else {
				// fitness is based on the last step
				// if fitness_steps > 1 it is a weighted fitness
				if (steps - step <= fitness_steps) {
					fitness = fitness / 2 + stepFitness;
				}
			}
		}

		if (!bestsolutionOutOfNSteps) {
			// record novelty in the last step
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					colors_novelty[x][y] = pool[x][y].color;
				}
			}
		}

		if (novelties != null) {
			novelties.clear();
			// add to novelty
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					// TODO normalize?
					novelties.add((float) colors_novelty[x][y]);
				}
			}
		}
		if (printResult)
			System.out.println("Fitness: " + fitness);

		return fitness;
	}

	/** Calculates the cell's output at the given coordinates */
	private void calcCell(int x, int y) {
		Cell c = pool[x][y];
		// array for the inputs
		ArrayList<Float> input = new ArrayList<Float>();

		switch (INPUTMODEL) {
		case COLOR_OUTPUT_V_NEUMANN:
			addNeighborColors(input, x, y, false);
			addNeighborOutputs(input, x, y, false);
			break;
		case COLOR_V_NEUMANN:
			addNeighborColors(input, x, y, false);
			break;
		case OUTPUT_V_NEUMANN:
			addNeighborOutputs(input, x, y, false);
			break;
		case COLOR_OUTPUT_MOORE:
			addNeighborOutputs(input, x, y, true);
			addNeighborColors(input, x, y, true);
			break;
		case OUTPUT_MOORE:
			addNeighborOutputs(input, x, y, true);
			break;
		case COLOR_MOORE:
			addNeighborColors(input, x, y, true);
			break;
		}

		// TODO need?
		// c.representation.reset();

		ArrayList<Float> output;

		output = c.representation.getOutput(input);

		// first output is color!
		c.nextcolor = (int) (output.get(0) * COLORS);

		if (c.nextcolor < 0)
			c.nextcolor = 0;
		if (c.nextcolor >= COLORS)
			c.nextcolor = COLORS - 1;

		// copy outputs
		for (int i = 0; i < c.nextoutput.length; i++)
			c.nextoutput[i] = output.get(i + 1);

	}

	/**
	 * Adds the Von Neumann neighborhood's colors to the input array of the cell
	 * at (x,y)
	 */
	private void addNeighborOutputs(ArrayList<Float> input, int x, int y,
			boolean isMoore) {
		if (x > 0) // west
			input.add(pool[x - 1][y].output[3]);
		else {
			if (istorroid)
				input.add(pool[WIDTH - 1][y].output[3]);
			else
				input.add(-1.0f);
		}

		if (x < WIDTH - 1) // east
			input.add(pool[x + 1][y].output[1]);
		else {
			if (istorroid)
				input.add(pool[0][y].output[1]);
			else
				input.add(-1.0f);
		}

		if (y > 0) // north
			input.add(pool[x][y - 1].output[2]);
		else {
			if (istorroid)
				input.add(pool[x][HEIGHT - 1].output[2]);
			else
				input.add(-1.0f);
		}

		if (y < HEIGHT - 1) // south
			input.add(pool[x][y + 1].output[0]);
		else {
			if (istorroid)
				input.add(pool[x][0].output[0]);
			else
				input.add(-1.0f);
		}

		if (isMoore) {
			if ((x > 0) && (y > 0)) { // northwest
				input.add(pool[x - 1][y - 1].output[4]);
			} else {
				if (istorroid) {
					if (x > 0)
						input.add(pool[x - 1][HEIGHT - 1].output[4]);
					else if (y > 0)
						input.add(pool[WIDTH - 1][y - 1].output[4]);
					else
						input.add(pool[WIDTH - 1][HEIGHT - 1].output[4]);
				} else
					input.add(-1.0f);
			}

			if ((x < WIDTH - 1) && (y > 0)) { // northeast
				input.add(pool[x + 1][y - 1].output[5]);
			} else {
				if (istorroid) {
					if (x < WIDTH - 1)
						input.add(pool[x + 1][HEIGHT - 1].output[5]);
					else if (y > 0)
						input.add(pool[0][y - 1].output[5]);
					else
						input.add(pool[0][HEIGHT - 1].output[5]);
				} else
					input.add(-1.0f);
			}

			if ((x > 0) && (y < HEIGHT - 1)) { // southwest
				input.add(pool[x - 1][y + 1].output[6]);
			} else {
				if (istorroid) {
					if (x > 0)
						input.add(pool[x - 1][0].output[6]);
					else if (y < HEIGHT - 1)
						input.add(pool[WIDTH - 1][y + 1].output[6]);
					else
						input.add(pool[WIDTH - 1][0].output[6]);
				} else
					input.add(-1.0f);
			}

			if ((x < WIDTH - 1) && (y < HEIGHT - 1)) { // southeast
				input.add(pool[x + 1][y + 1].output[7]);
			} else {
				if (istorroid) {
					if (x < WIDTH - 1)
						input.add(pool[x + 1][0].output[7]);
					else if (y < HEIGHT - 1)
						input.add(pool[0][y + 1].output[7]);
					else
						input.add(pool[0][0].output[7]);
				} else
					input.add(-1.0f);
			}
		}
	}

	/**
	 * Adds the Von Neumann neighborhood's outputs to the input array of the
	 * cell at (x,y)
	 */
	private void addNeighborColors(ArrayList<Float> input, int x, int y,
			boolean isMoore) {
		if (x > 0) {// west
			if (use1ofC) {
				for (int i = 0; i < COLORS; i++) {
					if (i == pool[x - 1][y].color) {
						input.add(1f);
					} else
						input.add(0f);
				}
			} else
				input.add((float) pool[x - 1][y].color / COLORS);
		} else {
			if (istorroid) {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[WIDTH - 1][y].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[WIDTH - 1][y].color / COLORS);
			} else {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						input.add(-1.0f);
					}
				} else
					input.add(-1.0f);
			}
		}

		if (x < WIDTH - 1) {// east
			if (use1ofC) {
				for (int i = 0; i < COLORS; i++) {
					if (i == pool[x + 1][y].color) {
						input.add(1f);
					} else
						input.add(0f);
				}
			} else
				input.add((float) pool[x + 1][y].color / COLORS);
		} else {
			if (istorroid) {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[0][y].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[0][y].color / COLORS);
			} else {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++)
						input.add(-1.0f);
				} else
					input.add(-1.0f);
			}
		}

		if (y > 0) // north
			if (use1ofC) {
				for (int i = 0; i < COLORS; i++) {
					if (i == pool[x][y - 1].color) {
						input.add(1f);
					} else
						input.add(0f);
				}
			} else
				input.add((float) pool[x][y - 1].color / COLORS);
		else {
			if (istorroid) {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[x][HEIGHT - 1].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[x][HEIGHT - 1].color / COLORS);
			} else {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++)
						input.add(-1.0f);
				} else
					input.add(-1.0f);
			}
		}

		if (y < HEIGHT - 1) {// south
			if (use1ofC) {
				for (int i = 0; i < COLORS; i++) {
					if (i == pool[x][y + 1].color) {
						input.add(1f);
					} else
						input.add(0f);
				}
			} else
				input.add((float) pool[x][y + 1].color / COLORS);
		} else {
			if (istorroid) {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[x][0].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[x][0].color / COLORS);
			} else {
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++)
						input.add(-1.0f);
				} else
					input.add(-1.0f);
			}
		}

		if (isMoore) {
			if ((x > 0) && (y > 0)) { // northwest
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[x - 1][y - 1].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[x - 1][y - 1].color / COLORS);
			} else {
				if (istorroid) {
					if (x > 0) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[x - 1][HEIGHT - 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[x - 1][HEIGHT - 1].color
									/ COLORS);
					} else if (y > 0) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[WIDTH - 1][y - 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[WIDTH - 1][y - 1].color
									/ COLORS);
					} else {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[WIDTH - 1][HEIGHT - 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[WIDTH - 1][HEIGHT - 1].color
									/ COLORS);
					}
				} else {
					if (use1ofC) {
						for (int i = 0; i < COLORS; i++)
							input.add(-1.0f);
					} else
						input.add(-1.0f);
				}
			}

			if ((x < WIDTH - 1) && (y > 0)) { // northeast
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[x + 1][y - 1].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[x + 1][y - 1].color / COLORS);
			} else {
				if (istorroid) {
					if (x < WIDTH - 1) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[x + 1][HEIGHT - 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[x + 1][HEIGHT - 1].color
									/ COLORS);
					} else if (y > 0) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[0][y - 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[0][y - 1].color / COLORS);
					} else {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[0][HEIGHT - 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[0][HEIGHT - 1].color
									/ COLORS);
					}
				} else {
					if (use1ofC) {
						for (int i = 0; i < COLORS; i++)
							input.add(-1.0f);
					} else
						input.add(-1.0f);
				}
			}

			if ((x > 0) && (y < HEIGHT - 1)) { // southwest
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[x - 1][y + 1].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[x - 1][y + 1].color / COLORS);
			} else {
				if (istorroid) {
					if (x > 0) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[x - 1][0].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[x - 1][0].color / COLORS);
					} else if (y < HEIGHT - 1) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[WIDTH - 1][y + 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[WIDTH - 1][y + 1].color
									/ COLORS);
					} else {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[WIDTH - 1][0].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[WIDTH - 1][0].color / COLORS);
					}
				} else {
					if (use1ofC) {
						for (int i = 0; i < COLORS; i++)
							input.add(-1.0f);
					} else
						input.add(-1.0f);
				}
			}

			if ((x < WIDTH - 1) && (y < HEIGHT - 1)) { // southeast
				if (use1ofC) {
					for (int i = 0; i < COLORS; i++) {
						if (i == pool[x + 1][y + 1].color) {
							input.add(1f);
						} else
							input.add(0f);
					}
				} else
					input.add((float) pool[x + 1][y + 1].color / COLORS);
			} else {
				if (istorroid) {
					if (x < WIDTH - 1) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[x + 1][0].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[x + 1][0].color / COLORS);
					} else if (y < HEIGHT - 1) {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[0][y + 1].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[0][y + 1].color / COLORS);
					} else {
						if (use1ofC) {
							for (int i = 0; i < COLORS; i++) {
								if (i == pool[0][0].color) {
									input.add(1f);
								} else
									input.add(0f);
							}
						} else
							input.add((float) pool[0][0].color / COLORS);
					}
				} else {
					if (use1ofC) {
						for (int i = 0; i < COLORS; i++)
							input.add(-1.0f);
					} else
						input.add(-1.0f);
				}
			}
		}
	}

	public static int rgbToShortColor(int rgbcolor) {
		int[] rgbs = new int[3];
		int colorDepth = 24;

		rgbs[0] = (rgbcolor & 0xFF0000) >> 16; // get red
		rgbs[1] = (rgbcolor & 0x00FF00) >> 8; // get green
		rgbs[2] = (rgbcolor & 0x0000FF); // get blue
		int bitpos = 0x100; // start with bit 8
		int shortColor = 0;

		for (int i = 0; i < colorDepth; i++) {
			if (i % 3 == 0)
				bitpos >>= 1;
			if ((rgbs[i % 3] & bitpos) > 0)
				shortColor = (shortColor << 1) + 1;
			else
				shortColor <<= 1;
		}
		// System.out.printf("color was %x, converted to %x (%d)\n",rgbcolor,shortColor,shortColor);
		return shortColor;
	}

	public static Color shortColorToRGB(int shortColor) {
		int colorDepth = 24;
		Color rgbcolor = Color.black;
		int[] rgbs = new int[3];
		int bitpos = 1 << (colorDepth - 1);
		int bitpos2 = 0x100; // start with bit 8

		for (int i = 0; i < colorDepth; i++) {
			if (i % 3 == 0)
				bitpos2 >>= 1;
			if ((shortColor & bitpos) > 0)
				rgbs[i % 3] = rgbs[i % 3] | bitpos2;
			bitpos >>= 1;
		}
		rgbcolor = new Color(rgbs[0], rgbs[1], rgbs[2]);

		return rgbcolor;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		Cam.this.evaluateCandidate(candidate);
		Picturepanel picturepanel = new Picturepanel(iconImg, candidate);
		picturepanel.setVisible(true);
	}

	private class Picturepanel extends JFrame {
		private static final long serialVersionUID = -4380283761101925684L;
		private BufferedImage referenceimage;
		private WhiteBoard whiteboard;
		JRadioButton computedButton;
		JRadioButton referenceButton;
		IntegerTextField phaseTextField;

		public Picturepanel(BufferedImage ref, AbstractRepresentation rep) {
			super("CAM");
			referenceimage = ref;
			this.setLocationRelativeTo(null);
			int width = this.referenceimage.getWidth() * SCALE + 140;
			if (width < 250)
				width = 250;
			this.setSize(width, this.referenceimage.getHeight() * SCALE + 80);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setLayout(null);
			whiteboard = new WhiteBoard(referenceimage);
			whiteboard.setSize(referenceimage.getWidth() * SCALE,
					referenceimage.getHeight() * SCALE);
			whiteboard.setLocation(0, 30);
			whiteboard.setBackground(Color.WHITE);
			this.add(whiteboard);

			// add radio buttons
			referenceButton = new JRadioButton("Reference");
			referenceButton.setSelected(false);
			referenceButton.setLocation(0, 0);
			referenceButton.setSize(90, 20);
			referenceButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					whiteboard.repaint();
				}
			});
			this.add(referenceButton);

			computedButton = new JRadioButton("Evolved");
			computedButton.setSelected(true);
			computedButton.setLocation(90, 0);
			computedButton.setSize(70, 20);
			computedButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					whiteboard.repaint();
				}
			});
			this.add(computedButton);

			// add radiogroup
			ButtonGroup group = new ButtonGroup();
			group.add(referenceButton);
			group.add(computedButton);

			// phase button backwards
			JButton minusButton = new JButton();
			minusButton.setLocation(164, 1);
			minusButton.setSize(20, 19);
			minusButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					SIMSTEPS = Integer.parseInt(phaseTextField.getText());
					if (SIMSTEPS > 1) {
						SIMSTEPS--;
						Cam.this.calcSim(candidate, SIMSTEPS, true, null);
						whiteboard.repaint();
						phaseTextField.setText(Integer.toString(SIMSTEPS));
						if (bestsolutionOutOfNSteps)
							System.out.println("Maximum after "
									+ Cam.this.bestStep + " step(s).");
					}
				}
			});
			this.add(minusButton);

			// phase field
			phaseTextField = new IntegerTextField();
			phaseTextField.setSize(30, 20);
			phaseTextField.setLocation(185, 1);
			phaseTextField.setText(Integer.toString(SIMSTEPS));
			phaseTextField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					SIMSTEPS = Integer.parseInt(phaseTextField.getText());
					double fitness = Cam.this.calcSim(candidate, SIMSTEPS,
							true, null);
					System.out.println("Weighted fitness :" + fitness
							+ " unweighted fintess: " + calcFitness());
					whiteboard.repaint();
				}
			});

			this.add(phaseTextField);
			// phase buttons
			JButton plusButton = new JButton();
			plusButton.setLocation(215, 1);
			plusButton.setSize(20, 19);
			plusButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					SIMSTEPS = Integer.parseInt(phaseTextField.getText());
					SIMSTEPS++;
					Cam.this.calcSim(candidate, SIMSTEPS, true, null);
					whiteboard.repaint();
					phaseTextField.setText(Integer.toString(SIMSTEPS));
					if (bestsolutionOutOfNSteps) {
						System.out.println("Maximum in step: "
								+ Cam.this.bestStep);
					}
				}
			});
			this.add(plusButton);
		}

		private class WhiteBoard extends JPanel {
			private static final long serialVersionUID = 5587457222315758555L;
			private BufferedImage referenceimage;

			public WhiteBoard(BufferedImage ref) {
				this.referenceimage = ref;
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (referenceButton.isSelected()) {
					// draw reference image
					for (int x = 0; x < this.referenceimage.getWidth(); x++) {
						for (int y = 0; y < this.referenceimage.getHeight(); y++) {
							g.setColor(new Color(referenceimage.getRGB(x, y)));
							g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
						}
					}
				} else {
					// draw computed image
					for (int x = 0; x < this.referenceimage.getWidth(); x++) {
						for (int y = 0; y < this.referenceimage.getHeight(); y++) {
							g.setColor(shortColorToRGB(colArr
									.get(pool[x][y].color)));
							g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);

							// System.out.print (pool[x][y].color+" ");
						}
						// System.out.println();
					}
				}
			}
		}
	}

	class IntegerTextField extends JTextField {
		private static final long serialVersionUID = -6925002221857070184L;
		final static String badchars = "`~!@#$%^&*()_+=\\|\"':;?/>.<, ";

		public void processKeyEvent(KeyEvent ev) {

			char c = ev.getKeyChar();

			if ((Character.isLetter(c) && !ev.isAltDown())
					|| badchars.indexOf(c) > -1) {
				ev.consume();
				return;
			}
			if (c == '-' && getDocument().getLength() > 0)
				ev.consume();
			else
				super.processKeyEvent(ev);

		}
	}

	@Override
	public double getMaximumFitness() {
		return 1.0;
	}

	/** Returns the novelty vector for the given candidate representation. */
	public ArrayList<Float> getNoveltyVector(AbstractRepresentation candidate,
			boolean assignfitness) {
		// calculate last 20 states
		ArrayList<Float> novelties = new ArrayList<Float>(20);
		double fitness = runCandidateEvaluation(candidate, novelties);
		if (assignfitness)
			candidate.setFitness(fitness);

		return novelties;
	}
/*
 * Sorry istvan :)
	public static void main(String[] args) throws InstantiationException {

		boolean rename = false;
		for (int seed = 1; seed <= 20; seed++) {
			// for (int seed = 12345; seed < 12346; seed++) {

			String path = "/Users/ifeherva/Works/demesos/Sourcecode/Frevo/Results/CAM_maple_15x16_output_moore/seed_"
					+ seed + "/";
			File folder = new File(path);
			File[] files = folder.listFiles();

			// rename files

			if (rename) {
				for (File f : files) {
					String oldname = f.getName();

					if (oldname.equals("temp"))
						continue;

					oldname = oldname.replaceAll("\\s+", "_");

					Scanner sc = new Scanner(oldname);
					sc.useDelimiter("_");

					String name = sc.next();

					String iter = sc.next();
					String gen = sc.next();

					gen = gen.substring(1);
					if (gen.length() < 4) {
						StringBuilder sb = new StringBuilder();
						sb.append("g");
						for (int i = 0; i < 4 - gen.length(); i++) {
							sb.append("0");
						}
						sb.append(gen);
						gen = sb.toString();
					}

					String fitness = sc.next();

					f.renameTo(new File(path + name + "_" + gen + "_" + iter
							+ "_" + fitness));
					sc.close();
				}

			} else {
				// sort alphabetically
				Arrays.sort(files);

				File loadFile = files[files.length - 1];

				// get best representation
				FullyMeshedNet net = new FullyMeshedNet(loadFile, 0);
				// NEAT net = new NEAT(loadFile, 0);

				// create problem
				ProblemXMLData problemData = (ProblemXMLData) FrevoMain
						.getSelectedComponent(ComponentType.FREVO_PROBLEM);

				Cam cam = (Cam) problemData.getNewProblemInstance();
				problemData.adjustRequirements();

				USENEWFITNESS = true;

				// System.out.println(seed + " Fitness: " +
				// cam.evaluateCandidate(net)
				// + " Step: " + (cam.bestStep + 1));
				double new_f = cam.evaluateCandidate(net);

				USENEWFITNESS = false;
				// double old_f = cam.evaluateCandidate(net);
				// System.out.println(seed+" - " +old_f+" "+new_f);

				System.out.println(new_f);

				// System.out.println (cam.bestStep);
				// ArrayList<Neuron> hidden = new ArrayList<Neuron>();
				// net.getNeurons(null, hidden, null);
				// System.out.println (hidden.size());
			}
		}

	}
*/
}