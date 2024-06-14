/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package main;

import graphics.FrevoTheme;
import graphics.FrevoWindow;
import helper.CMinusLexer;
import helper.CMinusParser;

import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import utils.NESRandom;
import utils.SafeSAX;
import utils.StatKeeper;
import core.AbstractMethod;
import core.AbstractMultiProblem;
import core.AbstractMultiProblem.RepresentationWithScore;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;
import core.ComponentType;
import core.ComponentXMLData;
import core.FileType;
import core.ProblemXMLData;
import core.RepresentationComparator;
import core.XMLFieldEntry;
import core.XMLFieldType;

/**
 * This is the core class of FREVO. It handles the components and operates the
 * simulation run through static methods. FrevoMain hosts all components loaded
 * from XML files from the components directory. This is a static class thus,
 * not meant to be instantiated.
 * 
 * <p>
 * Components are placed within 4 main categories: Problems, Methods,
 * Representations, Rankings. Problems are further sorted into multi and single
 * problems.
 * 
 * <p>
 * <b>Problem:</b> Problems are encapsulations of optimization tasks, typically
 * including an agent-based simulation tool. The controller of each agent is
 * received in a form of an {@link AbstractRepresentation}.<br>
 * <br> 
 * <b>Method:</b> Methods are optimization algorithms meant to iteratively
 * improve a set of candidate solutions. Furthermore, they are responsible in
 * loading and saving these candidates.<br>
 * <br>
 * <b>Representation:</b> These classes are models of agent controllers
 * considered as candidate solutions. These controllers implement some kind of
 * mutation or recombination operators in order to be optimized by the
 * respective method.<br>
 * <br>
 * <b>Ranking</b>: Rankings are used to sort a list of candidates in a
 * decreasing order of fitness. {@link AbstractSingleProblem}s can only use the
 * {@link AbsoluteRanking} while {@link AbstractMultiProblem}s can use various
 * more sophisticated algorithms.
 * 
 * @author Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 */
public class FrevoMain {

	/** Indicates the current debug level */
	public static int DEBUGLEVEL = 0x00;

	/** Is true when FREVO is started in graphical mode */
	private static boolean graphics;

	/**
	 * Returns true if FREVO is launched with GUI enabled.
	 * 
	 * @return True if FREVO is launched with its GUI, false otherwise.
	 */
	public static boolean isFrevoWithGraphics() {
		return graphics;
	}

	/** This object holds the reference of the graphical component of FREVO */
	private static FrevoWindow mainWindow;

	/**
	 * Returns the instance of the main GUI of FREVO.
	 * 
	 * @return The main GUI window of FREVO.
	 */
	public static FrevoWindow getMainWindow() {
		return mainWindow;
	}

	/** Holds the current directory */
	private final static String currentDir = System.getProperty("user.dir");

	/**
	 * Returns the current active directory used when FREVO was launched.
	 * 
	 * @return The current active directory.
	 */
	public static String getActiveDirectory() {
		return currentDir;
	}

	/**
	 * Holds the root directory of the program <b>without</b> the file separator
	 * at the end
	 */
	private static String FREVO_INSTALL_DIRECTORY;

	/**
	 * Returns the directory where FREVO is installed. Path ends <b>without</b>
	 * a separator character.
	 * 
	 * @return The installation directory of FREVO.
	 */
	public static String getInstallDirectory() {
		return FREVO_INSTALL_DIRECTORY;
	}

	/** The current major version */
	private final static String MAJORVERSION = "1.5";

	/**
	 * Returns the major version of FREVO in a <code>String</code> format.
	 * 
	 * @return The major version of FREVO.
	 */
	public static String getMajorVersion() {
		return MAJORVERSION;
	}

	/**
	 * Returns the minor version of FREVO in a <code>String</code> format.
	 * 
	 * @return The minor version of FREVO.
	 */
	public static String getMinorVersion() {
		String minorversion = "$Rev: 2834 $";
		String justTheNumber = minorversion.substring(6,
				minorversion.length() - 1);
		return justTheNumber;
	}

	// ----------------------static fields for referencing-------------------

	/** File extension for FREVO session files */
	public static final String FREVO_SESSION_EXTENSION = "zse";

	/** File type for FREVO sessions. */
	public static final FileType FREVO_SESSION_FILE_TYPE = new FileType(
			FREVO_SESSION_EXTENSION, "FREVO session file (*."
					+ FREVO_SESSION_EXTENSION + ")");

	/** File extension for FREVO result files */
	public static final String FREVO_RESULT_EXTENSION = "zre";

	/** File type for FREVO results. */
	public static final FileType FREVO_RESULT_FILE_TYPE = new FileType(
			FREVO_RESULT_EXTENSION, "FREVO result file (*."
					+ FREVO_RESULT_EXTENSION + ")");

	/** File extension for FREVO component package files */
	public static final String FREVO_PACKAGE_EXTENSION = "zcp";

	/**
	 * If true console output will be written directly to a file. Only applies
	 * if FREVO is launched in command-line mode
	 */
	public static final boolean FREVO_REDIRECTCONSOLE_TO_FILE = false;

	/** A static object holding the results to keep them in memory */
	// private static Object results;

	/** The current simulation run out of <i>maxRun</i> */
	private static int currentRun = 0;

	/**
	 * Returns the currently active simulation run. It is 1 based.
	 * 
	 * @return The number of the active simulation run.
	 */
	public static int getCurrentRun() {
		return currentRun;
	}

	/**
	 * Sets the active simulation run to the given number.
	 * 
	 * @param currentrun
	 *            The new value of the current simulation run.
	 */
	public static void setCurrentRun(int currentrun) {
		FrevoMain.currentRun = currentrun;
	}

	/** The number of total simulation runs */
	private static int numberOfRuns = 1;

	/**
	 * Returns the number of simulation runs scheduled.
	 * 
	 * @return The number of simulation runs scheduled.
	 */
	public static int getNumberOfSimulationRuns() {
		return numberOfRuns;
	}

	/**
	 * Sets the number of simulation runs to the given value.
	 * 
	 * @param maxRun
	 *            the maxRun to set
	 */
	public static void setNumberOfSimulationRuns(int maxRun) {
		FrevoMain.numberOfRuns = maxRun;
	}

	/** Starting seed of the simulation run */
	private static long SEED = 12345;

	/**
	 * Returns the seed of the simulation.
	 * 
	 * @return The current seed of the simulation.
	 */
	public static long getSeed() {
		return getInitialSeed() + getCurrentRun();
	}
	
	/**
	 * Returns the initial seed of the simulation.
	 * 
	 * @return The initial seed of the simulation.
	 */
	public static long getInitialSeed() {
		return SEED;
	}
	
	/**
	 * Sets the initial seed of the simulation to the given value.
	 * 
	 * @param seed
	 *            The new seed value used for simulations.
	 */
	public static void setInitialSeed(long seed) {
		SEED = seed;
	}

	/** Custom name of the session, mainly used for saving */
	private static String customName = "DEFAULT";

	/**
	 * Returns the custom name of the current simulation run.
	 * 
	 * @return The custom name of the active simulation run.
	 */
	public static String getCustomName() {
		return customName;
	}

	/**
	 * Sets the custom name of the active simulation to the given
	 * <code>String</code>.
	 * 
	 * @param customName
	 *            The new custom name to be used.
	 */
	public static void setCustomName(String customName) {
		FrevoMain.customName = customName;
	}

	/** Statistics object. Usually, the first element should be the index */
	private static ArrayList<StatKeeper> STATISTICS;

	/** Statistics to be displayed in interactive mode */
	private static ArrayList<StatKeeper> DISPLAYSTATISTICS;

	/** Startup parameters for FREVO */
	private static String[] PARAMETERS;

	/**
	 * Returns the FREVO startup parameters.
	 * 
	 * @return The FREVO startup arguments (parameters)
	 */
	public static String[] getParameters() {
		return PARAMETERS;
	}

	/** This is the currently selected problem */
	private static ProblemXMLData SELECTED_PROBLEM = null;
	/** This is the currently selected method */
	private static ComponentXMLData SELECTED_METHOD = null;
	/** This is the currently selected representation */
	private static ComponentXMLData SELECTED_REPRESENTATION = null;
	/** This is the currently selected ranking */
	private static ComponentXMLData SELECTED_RANKING = null;

	/**
	 * A map containing the installation directories of all components. Filled
	 * up at startup.
	 */
	private static final HashMap<ComponentType, String> COMPONENTINSTALLDIR = new HashMap<ComponentType, String>();

	/**
	 * Holds a list of <code>Strings</code> referring to the keywords used for
	 * sorting <b>problem</b> components.
	 */
	private static ArrayList<KeywordCategory> problemKeywordList;
	/**
	 * Holds a list of <code>Strings</code> referring to the keywords used for
	 * sorting <b>method</b> components.
	 */
	private static ArrayList<KeywordCategory> methodKeywordList;
	/**
	 * Holds a list of <code>Strings</code> referring to the keywords used for
	 * sorting <b>representation</b> components.
	 */
	private static ArrayList<KeywordCategory> representationKeywordList;
	/**
	 * Holds a list of <code>Strings</code> referring to the keywords used for
	 * sorting <b>ranking</b> components.
	 */
	private static ArrayList<KeywordCategory> rankingKeywordList;

	/** Indicates the size of the ComponentBrowser window */
	public static int[] componentBrowserParameters = new int[] { 600, 600, 273,
			271 };
	/** Indicates the size of the main window */
	public static int[] mainWindowParameters = new int[] { 600, 600, (Toolkit.getDefaultToolkit().getScreenSize().width-600)/2,  (Toolkit.getDefaultToolkit().getScreenSize().height-600)/2};
	
	/**	Indicates whether the app is started in darkMode*/
	public static boolean launchInDarkMode = false;

	// Component storage, hashed based on classname
	/**
	 * A hashmap containing all <code>problem</code> components based on their
	 * class name
	 */
	private static HashMap<String, ComponentXMLData> iProblems = new HashMap<String, ComponentXMLData>();
	/**
	 * A hashmap containing all <code>problem</code> components based on their
	 * class name
	 */
	private static HashMap<String, ComponentXMLData> iMultiProblems = new HashMap<String, ComponentXMLData>();
	/**
	 * A hashmap containing all <code>method</code> components based on their
	 * class name
	 */
	private static HashMap<String, ComponentXMLData> iMethods = new HashMap<String, ComponentXMLData>();
	/**
	 * A hashmap containing all <code>representation</code> components based on
	 * their class name
	 */
	private static HashMap<String, ComponentXMLData> iRepresentations = new HashMap<String, ComponentXMLData>();
	/**
	 * A hashmap containing all <code>bulk representation</code> components
	 * based on their class name
	 */
	private static HashMap<String, ComponentXMLData> iBulkRepresentations = new HashMap<String, ComponentXMLData>();
	/**
	 * A hashmap containing all <code>ranking</code> components based on their
	 * class name
	 */
	private static HashMap<String, ComponentXMLData> iRankings = new HashMap<String, ComponentXMLData>();

	// Flags for FREVO running state
	/**
	 * Is true if all necessary elements are loaded and the optimization method
	 * can be started.
	 */
	public static boolean isLaunchable = false;

	/** Indicates if a method is currently running or not. */
	public static boolean isRunning = false;
	
	/**
	 * The main function for starting FREVO.
	 * 
	 * @param args
	 *            Command line arguments entered when starting FREVO
	 */
	public static void main(String[] args) {
		loadInstallDirectory();

		// get launch arguments
		PARAMETERS = args;
		// calculate minor version number based on repository revision.
		System.out.println("Launching FREVO " + "v" + MAJORVERSION + " Rev:"
				+ getMinorVersion());

		// Initialize new statistics object
		STATISTICS = new ArrayList<StatKeeper>();
		DISPLAYSTATISTICS = new ArrayList<StatKeeper>();

		initComponentDirectories();

		// Run FREVO
		// Load components to the proper array
		loadComponents(false);
		// Load FREVO properties
		loadProperties();
		
		// no arguments, run graphical version
		if (args.length == 0) {
			launchFrevoGraphics(null);
		} else if ((args.length == 1) && (args[0].charAt(0) != '-')) {
			// load results file in argument
			launchFrevoGraphics(args[0]);
		} else {
			System.out.println("Args length: "+args.length);
				for (int i=0;i<args.length;i++){
						System.out.println("argument: "+args[i]);
		}
			if (args[0].equals("-s")) { // Load session and run
				if (FREVO_REDIRECTCONSOLE_TO_FILE) {
					try {
						System.out
								.println("Console output is written to log.txt");
						File logfile = new File("FREVO_log.txt");
						OutputStream output = new FileOutputStream(logfile);
						PrintStream printOut = new PrintStream(output);
						System.setOut(printOut);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}

				// Deselect components
				SELECTED_PROBLEM = null;
				SELECTED_METHOD = null;
				SELECTED_REPRESENTATION = null;
				SELECTED_RANKING = null;

				runSessionFromFile(args[1]);

			} else if( args[0].equals("-c") && args.length >= 2){	// replay result file without visualization
				
				// --- Glue all remaining arguments into a single path (some results might have spaces in their name)"
				String filepath = "";
				for(int a=1; a<args.length; a++){
					filepath += args[a];
					if(a < args.length-1) filepath+=" ";
				}
				System.out.println("Filepath: "+filepath);
				// ----
				
				File resultFile = new File(filepath);
				
				// load session part
				Document doc = loadSession(resultFile);
				
				if(getExtension(resultFile).equals(FREVO_RESULT_EXTENSION)){
					
					// Load populations -> Call method's own loader
					try {
						// Instantiate components
						ComponentXMLData method = FrevoMain.getSelectedComponent(ComponentType.FREVO_METHOD);

						AbstractMethod m = method.getNewMethodInstance(new NESRandom(getSeed()));
						
						// Loaded populations
						ArrayList<ArrayList<AbstractRepresentation>> populations = m.loadFromXML(doc);
						// sort all representations before visualizing
						for (ArrayList<AbstractRepresentation> representations : populations) {
							Collections.sort(representations, new RepresentationComparator());
						}
						
						// ---
						ProblemXMLData problem = (ProblemXMLData) FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM);
						AbstractSingleProblem ip = (AbstractSingleProblem) problem.getNewProblemInstance();
						ip.setRandom(new NESRandom(getSeed()));
						
						// get candidate with highest fitness and replay it in the market
						(ip).replayWithoutVisualization(resultFile, populations.get(0).get(0));
						;
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					}
					
				}
				
			} else if ((args[0].equals("-r")) && (args.length == 2)) {
				// remove a component
				// find the first component with the given name
				ComponentXMLData victim = null;
				String victimName = args[1];

				// search in single problems
				Iterator<ComponentXMLData> it = iProblems.values().iterator();
				while (it.hasNext()) {
					ComponentXMLData cdata = it.next();
					if (cdata.getClassName().equals(victimName)) {
						victim = cdata;
						break;
					}
				}

				// search in multi problems
				if (victim == null) {
					it = iMultiProblems.values().iterator();
					while (it.hasNext()) {
						ComponentXMLData cdata = it.next();
						if (cdata.getClassName().equals(victimName)) {
							victim = cdata;
							break;
						}
					}
				}

				// search in methods
				if (victim == null) {
					it = iMethods.values().iterator();
					while (it.hasNext()) {
						ComponentXMLData cdata = it.next();
						if (cdata.getClassName().equals(victimName)) {
							victim = cdata;
							break;
						}
					}
				}

				// search in representations
				if (victim == null) {
					it = iRepresentations.values().iterator();
					while (it.hasNext()) {
						ComponentXMLData cdata = it.next();
						if (cdata.getClassName().equals(victimName)) {
							victim = cdata;
							break;
						}
					}
				}

				// search in bulkrepresentations
				if (victim == null) {
					it = iBulkRepresentations.values().iterator();
					while (it.hasNext()) {
						ComponentXMLData cdata = it.next();
						if (cdata.getClassName().equals(victimName)) {
							victim = cdata;
							break;
						}
					}
				}

				// search in rankings
				if (victim == null) {
					it = iRankings.values().iterator();
					while (it.hasNext()) {
						ComponentXMLData cdata = it.next();
						if (cdata.getClassName().equals(victimName)) {
							victim = cdata;
							break;
						}
					}
				}

				if (victim != null) {
					// erase if found
					deleteComponent(victim);
					System.out
							.println("Component has been successfully removed: "
									+ victimName);
				} else {
					// indicate error
					System.out.println("No such component found to be erased: "
							+ victimName);
					System.out
							.println("Try listing the installed components with main.FrevoMain -l");
				}

			} else if (args[0].equals("-l")) {
				// list all loaded components
				System.out.println("Listing installed components:");

				// list single problems
				System.out.println();
				System.out.println("-- Problems --");
				Iterator<ComponentXMLData> it = iProblems.values().iterator();
				while (it.hasNext()) {
					ComponentXMLData cdata = it.next();
					System.out.println(cdata.getName() + "("
							+ cdata.getClassName() + ")");
				}

				// list multi problems
				System.out.println();
				System.out.println("-- Multi Problems --");
				it = iMultiProblems.values().iterator();
				while (it.hasNext()) {
					ComponentXMLData cdata = it.next();
					System.out.println(cdata.getName() + "("
							+ cdata.getClassName() + ")");
				}

				// list methods
				System.out.println();
				System.out.println("-- Methods --");
				it = iMethods.values().iterator();
				while (it.hasNext()) {
					ComponentXMLData cdata = it.next();
					System.out.println(cdata.getName() + "("
							+ cdata.getClassName() + ")");
				}

				// list representations
				System.out.println();
				System.out.println("-- Representations --");
				it = iRepresentations.values().iterator();
				while (it.hasNext()) {
					ComponentXMLData cdata = it.next();
					System.out.println(cdata.getName() + "("
							+ cdata.getClassName() + ")");
				}

				// list rankings
				System.out.println();
				System.out.println("-- Rankings --");
				it = iRankings.values().iterator();
				while (it.hasNext()) {
					ComponentXMLData cdata = it.next();
					System.out.println(cdata.getName() + "("
							+ cdata.getClassName() + ")");
				}

			} else if (args[0].equals("-?") || args[0].equals("?")
					|| args[0].equals("/?")) {
				printUsage();
			} else if ((args.length >= 2) && (args[0].equals("-e"))) {
				//glue all the other arguments together
				//in case a filepath includes a space or the arguments are seperated by spaces
				String argString = "";
				for(int a=1; a<args.length; a++){
					argString += args[a];
					if(a < args.length-1) argString+=" ";
				}
				String[] arguments=argString.split(",|;");
				int population=0;
				int representation=0;
				String language="C";
				String filename=null;
				String outputfile=null;
				for (String s:arguments){
					if (s.contains("=")){
						String[] parts=s.split("=");
						if (parts.length!=2){
							String message="ERROR: wrong parameter!\n"+s+"\nIn a command line argument: "+argString;
							System.out.println(message);
							printUsage();
							throw new IllegalArgumentException(message);
						}
						if (parts[0].contains("filename")){
							filename=parts[1];
						}
						if (parts[0].contains("out")){
							outputfile=parts[1];
						}
						if (parts[0].contains("language")){
							language=parts[1];
						}
						if (parts[0].contains("population")){
							population=Integer.valueOf(parts[1]);
						}
						if (parts[0].contains("representation")){
							representation=Integer.valueOf(parts[1]);
						}
					}
				}
				if (filename==null){
					throw new IllegalArgumentException("No representation file specified!");
				}
				if (outputfile==null){
					throw new IllegalArgumentException("No output file specified!");
				}
				File loadFile=new File(filename);
				Document doc = FrevoMain.loadSession(loadFile);
				ComponentXMLData method = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_METHOD);

				AbstractMethod m;
				try {
					m = method
							.getNewMethodInstance(new NESRandom(FrevoMain
									.getSeed()));
					ArrayList<ArrayList<AbstractRepresentation>> populations = m.loadFromXML(doc);
					AbstractRepresentation net=populations.get(population).get(representation);
					String stgName=null;
					if (!language.equals("C") && !language.equals("c")){
						stgName=language+".stg";
					}
					String content=net.getC();
				    if (content==null){
				    	printUsage();
				    	throw new IllegalArgumentException("Unable to get C code for given representation");
				    }
				    String output=content;
				    if (stgName!=null){
						StringTemplateGroup templates = new StringTemplateGroup(new FileReader(stgName),
							    AngleBracketTemplateLexer.class);
					    
					    InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
					    CMinusLexer lexer = new CMinusLexer(new ANTLRInputStream(stream));
						CommonTokenStream tokens = new CommonTokenStream(lexer);
						CMinusParser parser = new CMinusParser(tokens);
						parser.setTemplateLib(templates);
						RuleReturnScope r = parser.program();
						String result=r.getTemplate().toString();
						output=result;
					}
					PrintWriter out;
					out = new PrintWriter(outputfile);
					out.print(output);
				    out.close();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RecognitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
			 else {
				System.out.println("ERROR: wrong parameter set!");
				printUsage();
			}
		}
	}

	public static void initComponentDirectories() {
		// Initialize component directories
		COMPONENTINSTALLDIR.put(ComponentType.FREVO_PROBLEM,
				FREVO_INSTALL_DIRECTORY + "//Components//Problems");
		COMPONENTINSTALLDIR.put(ComponentType.FREVO_MULTIPROBLEM,
				FREVO_INSTALL_DIRECTORY + "//Components//Problems");
		COMPONENTINSTALLDIR.put(ComponentType.FREVO_METHOD,
				FREVO_INSTALL_DIRECTORY + "//Components//Methods");
		COMPONENTINSTALLDIR.put(ComponentType.FREVO_REPRESENTATION,
				FREVO_INSTALL_DIRECTORY + "//Components//Representations");
		COMPONENTINSTALLDIR.put(ComponentType.FREVO_BULKREPRESENTATION,
				FREVO_INSTALL_DIRECTORY + "//Components//Representations");
		COMPONENTINSTALLDIR.put(ComponentType.FREVO_RANKING,
				FREVO_INSTALL_DIRECTORY + "//Components//Rankings");
	}

	public static void loadInstallDirectory() {
		// get FREVO install directory from system
		String s = FrevoMain.class.getResource("FrevoMain.class").getFile();
		s = s.replaceAll("%20", " ");
		FREVO_INSTALL_DIRECTORY = s.substring(0, s.length() - 25);
	}

	/**
	 * Loads all parameters and components then launches FREVO with GUI. Loads
	 * the file at the given path right after launch or not if parameter is
	 * <code>null</code>.
	 * 
	 * @param loadfile
	 *            Path to a FREVO session or results file to load immediately
	 *            after launching. Passing <code>null</code> will omit this
	 *            operation.
	 */
	private static void launchFrevoGraphics(final String loadfile) {
		FrevoMain.graphics = true;

		// Deselect components
		SELECTED_PROBLEM = null;
		SELECTED_METHOD = null;
		SELECTED_REPRESENTATION = null;
		SELECTED_RANKING = null;

		// launch GUI window
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// use standard look and feel
					FrevoTheme theme = new FrevoTheme();
					theme.setDarkMode(launchInDarkMode);
					MetalLookAndFeel.setCurrentTheme(theme);

					UIManager.setLookAndFeel(new MetalLookAndFeel());

					// Create new FREVO GUI
					mainWindow = new FrevoWindow();
					mainWindow.setVisible(true);

					// load results from file
					if (loadfile != null) {
						System.out.println("Trying to open results file at "
								+ loadfile);
						loadFile(new File(loadfile));
					}

				} catch (Exception e) {
					e.printStackTrace();
					// error: no graphics available!
					System.out
							.println("FREVO: graphics are missing!\nTry \'main.FrevoMain -?\' for more information");
				}
			}
		});
	}

	/** Prints the usage parameters to the console */
	private static void printUsage() {
		System.out.println("USAGE:");
		System.out.println("Run main.FrevoMain to start FREVO with GUI.");
		System.out
				.println("Adding a file name right after it will order FREVO load the file right after the GUI has been displayed.");
		System.out
				.println("FREVO supports the following command-line arguments without GUI:");
		System.out
				.println("-c FILENAME : loads and executes the result file, e.g. -c test.zre");
		System.out
				.println("-s FILENAME : loads and executes the session file, e.g. -s test.zse");
		System.out
				.println("-r COMPONENT_CLASS_NAME : permanently removes the given installed component, e.g. -r nnga.NNGA");
		System.out.println("-l : prints a list of all loaded components");
		System.out.println("-e : exports a representations to a programm, e.g. -e filename=test.zre;output=test.c;language=c");
	}

	/** Reloads all components from the XML files. */
	public static void reLoadComponents(boolean silent) {
		// empty cache
		iProblems.clear();
		iMultiProblems.clear();
		iMethods.clear();
		iRepresentations.clear();
		iBulkRepresentations.clear();
		iRankings.clear();

		// remove stored results
		// results = null;

		// load components
		loadComponents(silent);

		// handle graphics
		if (graphics) {
			// dispose old window
			mainWindow.dispose();
			// create a new one
			launchFrevoGraphics(null);
		}
	}
	
	/** Restarts the graphics
	 * @param method the currently running method
	 * */
	public static void reLoadGraphics() {
		// handle graphics
		if (graphics) {
			//save components
			ProblemXMLData selectedProblem = SELECTED_PROBLEM;
			ComponentXMLData selectedMethod = SELECTED_METHOD;
			ComponentXMLData selectedRepresentation = SELECTED_REPRESENTATION;
			ComponentXMLData selectedRanking = SELECTED_RANKING;
			//save settings
			mainWindowParameters[0] = mainWindow.getBounds().width;
			mainWindowParameters[1] = mainWindow.getBounds().height;
			mainWindowParameters[2] = mainWindow.getBounds().x;
			mainWindowParameters[3] = mainWindow.getBounds().y;
			// dispose old window
			mainWindow.dispose();
			// create a new one
			launchFrevoGraphics(null);
			
			//reaload components
			SELECTED_PROBLEM = selectedProblem;
			SELECTED_METHOD = selectedMethod;
			SELECTED_REPRESENTATION = selectedRepresentation;
			SELECTED_RANKING = selectedRanking;
		}
	}

	/** Loads component data from all configuration files. */
	//private 
	public
	static void loadComponents(boolean silent) {

		for (ComponentType ctype : ComponentType.values()) {
			// Do not load multiproblems, (=problems) or bulkrepresentations
			// (=representation)
			if (ctype == ComponentType.FREVO_MULTIPROBLEM
					|| ctype == ComponentType.FREVO_BULKREPRESENTATION)
				continue;

			// get component directory
			File iComponentdir = new File(COMPONENTINSTALLDIR.get(ctype));

			// get a list of files within the directory
			File[] files = iComponentdir.listFiles();

			// set up a file filter for XML files
			FilenameFilter fileFilter = new XMLFileFilter();

			// get all XML files in that directory
			files = iComponentdir.listFiles(fileFilter);

			// load files one by one and put them in the proper map
			for (int i = 0; i < files.length; i++) {
				if (files.length != 0)
					try {
						// Load data from XML file
						ComponentXMLData cdata = readIComponentFromXml(
								files[i], ctype, false);

						// Sort component
						if (cdata != null) {
							// multiproblems go to their place
							if (cdata.getComponentType() == ComponentType.FREVO_MULTIPROBLEM) {
								getComponentList(
										ComponentType.FREVO_MULTIPROBLEM).put(
										cdata.getClassName(), cdata);
							} else if (cdata.getComponentType() == ComponentType.FREVO_BULKREPRESENTATION) {
								getComponentList(
										ComponentType.FREVO_BULKREPRESENTATION)
										.put(cdata.getClassName(), cdata);
							}

							else
								getComponentList(ctype).put(
										cdata.getClassName(), cdata);
						}

					} catch (RemoteException e) {
						e.printStackTrace();
					}
			}
		}

		// sum up loaded components
		int total = (iProblems.size() + iMultiProblems.size() + iMethods.size()
				+ iRepresentations.size() + iBulkRepresentations.size() + iRankings
				.size());

		if (!silent)
			System.out.println("Total components loaded: " + total + " (P:"
					+ (iProblems.size() + iMultiProblems.size()) + ",M:"
					+ iMethods.size() + ",Re:"
					+ (iRepresentations.size() + iBulkRepresentations.size())
					+ ",Ra:" + iRankings.size() + ")");
	}

	/**
	 * Loads FREVO properties from the configuration XML file. If the XML file
	 * is not present then a default configuration will be loaded creating a new
	 * configuration file.
	 */
	//private 
	public
	static void loadProperties() {
		try {
			Document doc = null;
			// Access XML file
			File documentFile = new File(FREVO_INSTALL_DIRECTORY
					+ "/frevo_config.xml");

			doc = SafeSAX.read(documentFile, false);

			// load keywords
			problemKeywordList = new ArrayList<KeywordCategory>();
			methodKeywordList = new ArrayList<KeywordCategory>();
			representationKeywordList = new ArrayList<KeywordCategory>();
			rankingKeywordList = new ArrayList<KeywordCategory>();

			Node keywordcategories = doc.selectSingleNode("/frevo/tags");

			// Collect problem keywords
			List<?> items = keywordcategories.selectNodes(".//problemtag");
			Iterator<?> it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");
				String keywords = el.valueOf("./@tags");
				String imagename = el.valueOf("./@image");

				ArrayList<String> keywordlist = new ArrayList<String>();
				Scanner sc = new Scanner(keywords);
				sc.useDelimiter(",");
				while (sc.hasNext())
					keywordlist.add(sc.next());

				problemKeywordList.add(new KeywordCategory(name, keywordlist,
						imagename));

				sc.close();
			}

			// Collect method keywords
			items = keywordcategories.selectNodes(".//methodtag");
			it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");
				String keywords = el.valueOf("./@tags");
				String imagename = el.valueOf("./@image");

				ArrayList<String> keywordlist = new ArrayList<String>();
				Scanner sc = new Scanner(keywords);
				sc.useDelimiter(",");
				while (sc.hasNext())
					keywordlist.add(sc.next());

				methodKeywordList.add(new KeywordCategory(name, keywordlist,
						imagename));

				sc.close();
			}

			// representation keywords
			items = keywordcategories.selectNodes(".//representationtag");
			it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");
				String keywords = el.valueOf("./@tags");
				String imagename = el.valueOf("./@image");

				ArrayList<String> keywordlist = new ArrayList<String>();
				Scanner sc = new Scanner(keywords);
				sc.useDelimiter(",");
				while (sc.hasNext())
					keywordlist.add(sc.next());

				representationKeywordList.add(new KeywordCategory(name,
						keywordlist, imagename));

				sc.close();
			}

			// ranking keywords
			items = keywordcategories.selectNodes(".//rankingtag");
			it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");
				String keywords = el.valueOf("./@tags");
				String imagename = el.valueOf("./@image");

				ArrayList<String> keywordlist = new ArrayList<String>();
				Scanner sc = new Scanner(keywords);
				sc.useDelimiter(",");
				while (sc.hasNext())
					keywordlist.add(sc.next());

				rankingKeywordList.add(new KeywordCategory(name, keywordlist,
						imagename));

				sc.close();
			}

			// load preferred window size
			Node windowsizes = doc.selectSingleNode("/frevo/window-sizes");

			items = windowsizes.selectNodes(".//window");
			it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");

				if (name.equals("main")) {
					String width = el.valueOf("./@width");
					String height = el.valueOf("./@height");
					String posX = el.valueOf("./@posX");
					String posY = el.valueOf("./@posY");
					mainWindowParameters[0] = Integer.parseInt(width);
					mainWindowParameters[1] = Integer.parseInt(height);
					mainWindowParameters[2] = Integer.parseInt(posX);
					mainWindowParameters[3] = Integer.parseInt(posY);
				} else if (name.equals("componentbrowser")) {
					String width = el.valueOf("./@width");
					String height = el.valueOf("./@height");
					String topsplit = el.valueOf("./@topsplit");
					String bigsplit = el.valueOf("./@bigsplit");
					componentBrowserParameters[0] = Integer.parseInt(width);
					componentBrowserParameters[1] = Integer.parseInt(height);
					componentBrowserParameters[2] = Integer.parseInt(topsplit);
					componentBrowserParameters[3] = Integer.parseInt(bigsplit);
				}
			}
			// load general appearance settings
			Node appearances = doc.selectSingleNode("/frevo/appearances");

			items = appearances.selectNodes(".//theme");
			it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");

				if (name.equals("frevoTheme")) {
					String darkMode = el.valueOf("./@darkMode");
					launchInDarkMode = Boolean.parseBoolean(darkMode);
				}
			}
		} catch (Exception e) {
			System.err.println("No config file found, using defaults");
		}

	}

	/**
	 * Loads the selected FREVO session or results file. Results will only be
	 * loaded if FREVO is running in GUI mode.
	 * 
	 * @param loadFile
	 *            The file to be loaded.
	 */
	protected static void loadFile(File loadFile) {
		if (getExtension(loadFile).equals(FREVO_SESSION_EXTENSION))
			loadSession(loadFile);
		else if ((graphics)
				&& (getExtension(loadFile).equals(FREVO_RESULT_EXTENSION))) {
			// results file will only be loaded in GUI mode
			mainWindow.loadFile(loadFile);
		} else {
			System.err.println("Cannot load file: " + loadFile.getName());
			System.err.println("Make sure to you use FREVO file extensions (."
					+ FREVO_SESSION_EXTENSION + "/." + FREVO_RESULT_EXTENSION
					+ ")");
		}

	}

	/**
	 * Loads the give session file to FREVO. In case of error the components
	 * will not be selected in FREVO.
	 * 
	 * @param loadFile
	 *            The session file to load.
	 */
	public static Document loadSession(File loadFile) {
		// loading session
		Document doc = null;
		// indicate if error occurs
		boolean error = false;

		// load XML tree from file
		doc = SafeSAX.read(loadFile, true);

		ProblemXMLData problem = null;
		ComponentXMLData method = null;
		ComponentXMLData representation = null;
		ComponentXMLData ranking = null;
		try {
			// Load configuration section
			Node nd = doc.selectSingleNode("/frevo/sessionconfig");
			if (nd == null) {
				System.err.println("Warning: No config keywords found!");
			} else { // load configuration
				List<?> npops = nd.selectNodes(".//configentry");
				Iterator<?> it = npops.iterator();

				// load all keys and values
				while (it.hasNext()) {
					Element el = (Element) it.next();
					String key = el.valueOf("./@key");
					String value = el.valueOf("./@value");

					if (key.equals("CustomName")) {
						FrevoMain.customName = value;
					} else if (key.equals("NumberofRuns")) {
						FrevoMain.setNumberOfSimulationRuns(Integer.parseInt(value));
					} else if (key.equals("StartingSeed")) {
						FrevoMain.setInitialSeed(Long.parseLong(value)); 
					}
				}
			}

			// Load problem
			Node problemnode = doc.selectSingleNode("/frevo/problem");

			// select appropriate component
			String classname = problemnode.valueOf("./@class");
			problem = (ProblemXMLData) getComponent(
					ComponentType.FREVO_PROBLEM, classname);
			if (problem == null) {
				error = true;
				System.err.println("ERROR: No such problem component found: "
						+ classname);
			}

			if (!error) {
				// load configuration
				List<?> pentries = problemnode.selectNodes(".//problementry");
				Iterator<?> it = pentries.iterator();

				// set problem properties
				while (it.hasNext()) {
					Element el = (Element) it.next();
					String key = el.valueOf("./@key");
					String value = el.valueOf("./@value");
					XMLFieldEntry pe = problem.getProperties().get(key);
					if (pe != null)
						pe.setValue(value);
				}
			}

			// Load method
			Node methodnode = doc.selectSingleNode("/frevo/method");

			// select appropriate component
			classname = methodnode.valueOf("./@class");
			method = getComponent(ComponentType.FREVO_METHOD, classname);
			if (method == null) {
				if (classname.equals("")) {
					System.err
							.println("WARNING: No class entry found to load the method component!");
					//error = true;
				} else {
					error = true;
					System.err
							.println("ERROR: No such method component found: "
									+ classname);
				}
			} else {
				// load configuration
				List<?> mentries = methodnode.selectNodes(".//methodentry");
				Iterator<?> mit = mentries.iterator();

				while (mit.hasNext()) {
					Element el = (Element) mit.next();
					String key = el.valueOf("./@key");
					String value = el.valueOf("./@value");
					XMLFieldEntry pe = method.getProperties().get(key);
					if (pe != null)
						pe.setValue(value);
				}
			}

			// Load representation
			Node representationnode = doc
					.selectSingleNode("/frevo/representation");
			// select appropriate component
			classname = representationnode.valueOf("./@class");
			representation = getComponent(ComponentType.FREVO_REPRESENTATION,
					classname);
			if (representation == null) {
				if (classname.equals("")) {
					System.err
							.println("WARNING: No class entry found to load the representation component!");
					//error = true;
				} else {
					error = true;
					System.err
							.println("ERROR: No such representation component found: "
									+ classname);
				}
			} else {
				// load configuration
				List<?> rentries = representationnode
						.selectNodes(".//representationentry");
				Iterator<?> rit = rentries.iterator();

				while (rit.hasNext()) {
					Element el = (Element) rit.next();
					String key = el.valueOf("./@key");
					String value = el.valueOf("./@value");
					XMLFieldEntry pe = representation.getProperties().get(key);
					if (pe != null)
						pe.setValue(value);
				}

				// load core representation
				if (representation.getComponentType() == ComponentType.FREVO_BULKREPRESENTATION) {
					String corerepresentationclassname = representation
							.getProperties()
							.get("core_representation_component").getValue();
					ComponentXMLData coreRepresentation = FrevoMain
							.getComponent(ComponentType.FREVO_REPRESENTATION,
									corerepresentationclassname);
					if (coreRepresentation == null) {
						System.err
								.println("ERROR: Could not load specified representation: "
										+ corerepresentationclassname);
					} else {
						rentries = representationnode
								.selectNodes(".//corerepresentationentry");
						rit = rentries.iterator();

						while (rit.hasNext()) {
							Element el = (Element) rit.next();
							String key = el.valueOf("./@key");
							String value = el.valueOf("./@value");
							XMLFieldEntry pe = coreRepresentation
									.getProperties().get(key);
							if (pe != null)
								pe.setValue(value);
						}
					}

				}
			}

			// Load ranking
			Node rankingnode = doc.selectSingleNode("/frevo/ranking");
			// select appropriate component
			classname = rankingnode.valueOf("./@class");
			ranking = getComponent(ComponentType.FREVO_RANKING, classname);
			if (ranking == null) {
				if (classname.equals("")) {
					System.err
							.println("WARNING: No class entry found to load the ranking component!");
					//error = true;
				} else {
					System.err
							.println("ERROR: No such ranking component found: "
									+ classname);
					error = true;
				}
			} else {
				// load configuration
				List<?> raentries = rankingnode.selectNodes(".//rankingentry");
				Iterator<?> rait = raentries.iterator();

				while (rait.hasNext()) {
					Element el = (Element) rait.next();
					String key = el.valueOf("./@key");
					String value = el.valueOf("./@value");
					XMLFieldEntry pe = ranking.getProperties().get(key);
					if (pe != null)
						pe.setValue(value);
				}
			}

			// adjust GUI
			if (isFrevoWithGraphics()) {
				mainWindow.updateAdvancedLabels();
			}

		} catch (OutOfMemoryError mem) {
			System.err.println("ERROR: Could not import! (Out of memory)");
			//error = true;
		} catch (IllegalArgumentException e) {
			System.out.println("IllegalArgumentException\n" + e.getMessage());
			//error = true;
		}
		if (error) {
			System.err.println("Error while importing!");
		} else {
			SELECTED_PROBLEM = problem;
			SELECTED_METHOD = method;
			SELECTED_REPRESENTATION = representation;
			SELECTED_RANKING = ranking;
		}

		return doc;
	}

	/**
	 * Loads the class with all properties and requirements stored in the
	 * appropriate XML file. Returns null if loading fails.
	 * 
	 * @param file
	 *            The XML file to load.
	 * @param ctype
	 *            Type of this component. E.g. problem, method, etc. Check
	 *            <code>ComponentType</code> enum for more.
	 * @param validateXML
	 *            Validates the XML while loading if true.
	 */
	public static ComponentXMLData readIComponentFromXml(File file,
			ComponentType ctype, boolean validateXML) throws RemoteException {
		// construct new xmldata object based on type
		ComponentXMLData result;

		try {
			result = new ProblemXMLData(ctype, file);
		} catch (InstantiationException e) {
			result = new ComponentXMLData(ctype, file);
		} catch (Exception e) {
			// skip loading component in case of ANY error
			return null;
		}

		// Return if loaded successfully
		if (result.isLoadedSuccessfully())
			return result;
		return null;
	}

	/**
	 * Starts a simulation with the given name and starting seed.
	 * 
	 * @param outputname
	 *            The output name used by the method. This is mostly to
	 *            formulate result files' names.
	 * @param seed
	 *            The starting random seed of this simulation.
	 */
	public static void runSimulation(String outputname, Document doc) {
		// Check if FREVO is ready to be launched
		if (!checkState()) {
			System.err
					.println("FREVO is not ready to be launched. Please select one component for problem, method, representation and ranking!");
			return;
		}

		try {
			System.out.println("Starting simulation");

			// start clock
			FrevoWindow.startTime = System.currentTimeMillis();

			// set running flag
			FrevoMain.isRunning = true;

			// reset current run
			FrevoMain.setCurrentRun(0);

			// erase temporary statistics
			FrevoMain.eraseStatistics();

			if (graphics) {
				mainWindow.startSim(doc);
			} else {
				// run with different seed
				for (int run = 0; run < FrevoMain.getNumberOfSimulationRuns(); run++) {
					// set current run to actual value
					FrevoMain.setCurrentRun(run);

					// Instantiate components
					final AbstractMethod method = SELECTED_METHOD
							.getNewMethodInstance(new NESRandom(FrevoMain.getSeed()));

					// execute without GUI
					method.runOptimization(SELECTED_PROBLEM,
							SELECTED_REPRESENTATION, SELECTED_RANKING,
							method.getProperties());
				}

				// finish
				FrevoMain.isRunning = false;

				// dump statistics
				FrevoMain.writeStatisticsToDisk();
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the corresponding category of keywords to the given component
	 * type.
	 * 
	 * @param ctype
	 *            The type of component whose keyword category is requested.
	 * @return The category of keywords for the given component type.
	 */
	public static ArrayList<KeywordCategory> getCategories(
			final ComponentType ctype) {
		switch (ctype) {
		case FREVO_PROBLEM:
			return problemKeywordList;
		case FREVO_MULTIPROBLEM:
			return problemKeywordList;
		case FREVO_METHOD:
			return methodKeywordList;
		case FREVO_REPRESENTATION:
			return representationKeywordList;
		case FREVO_BULKREPRESENTATION:
			return representationKeywordList;
		case FREVO_RANKING:
			return rankingKeywordList;
		default:
			break;
		}
		System.err.println("Undefined component type given: " + ctype.name());
		return null;
	}

	/**
	 * Starts a saving procedure calling the method's own saveResults function.
	 * Run-specific data will be added to the end of the file.
	 * 
	 * @param fileName
	 *            Name of the saved file without the extension. (E.g.
	 *            solution_generation_13)
	 * @param method
	 *            The corresponding method instance to be called for saving.
	 */
	public static File saveResult(String fileName,
			final Element representationRootElement, long startSeed,
			long currentActiveSeed) {
		// create new document for output
		Document doc = DocumentHelper.createDocument();
		doc.addDocType("frevo", null, System.getProperty("user.dir")
				+ "//Components//ISave.dtd");
		Element dfrevo = doc.addElement("frevo");

		String fileLocation = "Undefined";

		// export sessionconfig
		Element sessionconfig = dfrevo.addElement("sessionconfig");

		// custom name
		Element configentry = sessionconfig.addElement("configentry");
		configentry.addAttribute("key", "CustomName");
		configentry.addAttribute("type", "STRING");
		configentry.addAttribute("value", customName);

		// number of runs
		Element runentry = sessionconfig.addElement("configentry");
		runentry.addAttribute("key", "NumberofRuns");
		runentry.addAttribute("type", "INT");
		runentry.addAttribute("value",
				Integer.toString(getNumberOfSimulationRuns()));

		// starting seed
		Element seedentry = sessionconfig.addElement("configentry");
		seedentry.addAttribute("key", "StartingSeed");
		seedentry.addAttribute("type", "LONG");
		// seedentry.addAttribute("value", Long.toString(getSeed()));
		seedentry.addAttribute("value", Long.toString(startSeed));

		// active seed
		Element aseedentry = sessionconfig.addElement("configentry");
		aseedentry.addAttribute("key", "CurrentSeed");
		aseedentry.addAttribute("type", "LONG");
		aseedentry.addAttribute("value", Long.toString(currentActiveSeed));

		try {
			// export problem
			Element problemsettings = dfrevo.addElement("problem");
			ComponentXMLData problem = FrevoMain.SELECTED_PROBLEM;
			problemsettings.addAttribute("class", problem.getClassName());
			Vector<String> keys = new Vector<String>(problem.getProperties()
					.keySet());
			for (String k : keys) {
				Element entry = problemsettings.addElement("problementry");
				entry.addAttribute("key", k);
				entry.addAttribute("type", problem.getTypeOfProperty(k)
						.toString());
				entry.addAttribute("value", problem.getValueOfProperty(k));
			}

			// export method
			Element methodsettings = dfrevo.addElement("method");
			ComponentXMLData method = FrevoMain.SELECTED_METHOD;
			methodsettings.addAttribute("class", method.getClassName());
			keys = new Vector<String>(method.getProperties().keySet());
			for (String k : keys) {
				Element entry = methodsettings.addElement("methodentry");
				entry.addAttribute("key", k);
				entry.addAttribute("type", method.getTypeOfProperty(k)
						.toString());
				entry.addAttribute("value", method.getValueOfProperty(k));
			}

			// export ranking
			Element rankingsettings = dfrevo.addElement("ranking");
			ComponentXMLData ranking = FrevoMain.SELECTED_RANKING;
			rankingsettings.addAttribute("class", ranking.getClassName());
			keys = new Vector<String>(ranking.getProperties().keySet());
			for (String k : keys) {
				Element entry = rankingsettings.addElement("rankingentry");
				entry.addAttribute("key", k);
				entry.addAttribute("type", ranking.getTypeOfProperty(k)
						.toString());
				entry.addAttribute("value", ranking.getValueOfProperty(k));
			}

			// export representation
			Element repsettings = dfrevo.addElement("representation");
			ComponentXMLData representation = FrevoMain.SELECTED_REPRESENTATION;
			repsettings.addAttribute("class", representation.getClassName());
			keys = new Vector<String>(representation.getProperties().keySet());
			for (String k : keys) {
				Element entry = repsettings.addElement("representationentry");
				entry.addAttribute("key", k);
				entry.addAttribute("type", representation.getTypeOfProperty(k)
						.toString());
				entry.addAttribute("value",
						representation.getValueOfProperty(k));
			}

			// call method's own save solution
			dfrevo.add(representationRootElement);

			// save contents to file

			String location = FREVO_INSTALL_DIRECTORY + File.separator
					+ "Results" + File.separator + customName;
			File rootSaveDir = new File(location);

			// remove spaces from filename
			fileName.replaceAll(" ", "_");

			// create save directory based on given custom name
			rootSaveDir.mkdirs();

			// create sub-directories for different seeds
			if (FrevoMain.getNumberOfSimulationRuns() > 1) {
				// create seed directory if there are more than one run
				File seedDir = new File(location + File.separator + "seed_"
						+ startSeed);
				seedDir.mkdir();
				fileLocation = seedDir + File.separator + fileName + ".zre";
			} else {
				// save it the root location
				fileLocation = rootSaveDir + File.separator + fileName + ".zre";
			}

			File saveFile = new File(fileLocation);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setLineSeparator(System.getProperty("line.separator"));

			saveFile.createNewFile();
			FileWriter out = new FileWriter(saveFile);
			BufferedWriter bw = new BufferedWriter(out);
			XMLWriter wr = new XMLWriter(bw, format);
			wr.write(doc);
			wr.close();
			System.out.println("XML Writing Completed: " + fileLocation);

			if (isFrevoWithGraphics()) {
				mainWindow.addRecentResult(saveFile);
			}
			return saveFile;
		} catch (OutOfMemoryError mem) {
			System.err.println("Could not export! (Out of memory)");
		} catch (IOException e) {
			System.err
					.println("IOException while writing to XML! Check path at: "
							+ fileLocation);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Publishes the given results object in FREVO. This method is needed for
	 * simulations to be continued.
	 * 
	 * @param results
	 *            The result object to be published.
	 */
	/*
	 * public static void publishResults(final Object results) {
	 * FrevoMain.results = results; }
	 */

	/**
	 * Sets the progress of any results or session file loading operation. If
	 * FREVO is running in GUI mode, it will be displayed in a progress bar.
	 * Passed progress value must be within the range of 0..1
	 * 
	 * @param progress
	 *            The new progress value between 0 and 1.
	 */
	public static void setLoadingProgress(float progress) {
		if (graphics)
			mainWindow.setLoadingProgress(progress);
	}

	/**
	 * Returns the results object currently stored in FREVO.
	 * 
	 * @return Results currently saved in FREVO
	 */
	/*
	 * public static Object getResults() { return FrevoMain.results; }
	 */

	/**
	 * Returns the selected component of the given type.
	 * <tt>AbstractSingleProblem</tt> and <tt>AbstractMultiProblem</tt> will
	 * return the same component.
	 * 
	 * @return The currently selected component of the given type.
	 * @param ctype
	 *            The type of the component requested.
	 */
	public static ComponentXMLData getSelectedComponent(ComponentType ctype) {
		switch (ctype) {
		case FREVO_PROBLEM:
			return SELECTED_PROBLEM;
		case FREVO_MULTIPROBLEM:
			return SELECTED_PROBLEM;
		case FREVO_METHOD:
			return SELECTED_METHOD;
		case FREVO_REPRESENTATION:
			return SELECTED_REPRESENTATION;
		case FREVO_BULKREPRESENTATION:
			return SELECTED_REPRESENTATION;
		case FREVO_RANKING:
			return SELECTED_RANKING;
		}
		throw new Error("Invalid component type given");
	}

	/**
	 * Selects the given component of the given type.
	 * 
	 * @param ctype
	 *            The type of the component to be selected.
	 * @param cdata
	 *            The XML data to be selected.
	 */
	public static void setSelectedComponent(ComponentType ctype,
			ComponentXMLData cdata) {
		switch (ctype) {
		case FREVO_PROBLEM:
			SELECTED_PROBLEM = (ProblemXMLData) cdata;
			break;
		case FREVO_MULTIPROBLEM:
			SELECTED_PROBLEM = (ProblemXMLData) cdata;
			break;
		case FREVO_METHOD:
			SELECTED_METHOD = cdata;
			break;
		case FREVO_REPRESENTATION:
			SELECTED_REPRESENTATION = cdata;
			break;
		case FREVO_BULKREPRESENTATION:
			SELECTED_REPRESENTATION = cdata;
			break;
		case FREVO_RANKING:
			SELECTED_RANKING = cdata;
			break;
		default:
			break;
		}
	}

	/**
	 * Returns the data of the component with the given name or
	 * <code>null</code> if not found.
	 * 
	 * @param ctype
	 *            The type of the component to be returned.
	 * @param componentName
	 *            The name of the component to be returned.
	 * @return The component with the given name and type.
	 */
	public static ComponentXMLData getComponent(ComponentType ctype,
			String componentName) {
		switch (ctype) {
		// case FREVO_PROBLEM:
		// return iProblems.get(componentName);
		// case FREVO_MULTIPROBLEM:
		// return iMultiProblems.get(componentName);
		case FREVO_METHOD:
			return iMethods.get(componentName);
			/*
			 * case FREVO_REPRESENTATION: return
			 * iRepresentations.get(componentName);
			 */
		case FREVO_RANKING:
			return iRankings.get(componentName);
		default:
			break;
		}

		if ((ctype == ComponentType.FREVO_REPRESENTATION)
				|| (ctype == ComponentType.FREVO_BULKREPRESENTATION)) {
			// try representation array
			ComponentXMLData result = iRepresentations.get(componentName);

			if (result == null)
				result = iBulkRepresentations.get(componentName);

			return result;
		}

		if ((ctype == ComponentType.FREVO_PROBLEM)
				|| (ctype == ComponentType.FREVO_MULTIPROBLEM)) {
			// try problems array
			ComponentXMLData result = iProblems.get(componentName);

			// try multi problems array
			if (result == null)
				result = iMultiProblems.get(componentName);

			return result;
			// throw new Error("ERROR: Problem component not loaded!");
		}

		// should not reach this far
		throw new IllegalArgumentException("Invalid component type!");
	}

	/**
	 * Returns a list of components of the given type.
	 * 
	 * @param componentType
	 *            The type of the components requested.
	 * @return A list of all loaded components of the given type.
	 */
	public static HashMap<String, ComponentXMLData> getComponentList(
			ComponentType componentType) {
		if (componentType == ComponentType.FREVO_PROBLEM)
			return iProblems;
		else if (componentType == ComponentType.FREVO_MULTIPROBLEM)
			return iMultiProblems;
		else if (componentType == ComponentType.FREVO_METHOD)
			return iMethods;
		else if (componentType == ComponentType.FREVO_REPRESENTATION)
			return iRepresentations;
		else if (componentType == ComponentType.FREVO_BULKREPRESENTATION)
			return iBulkRepresentations;
		else if (componentType == ComponentType.FREVO_RANKING)
			return iRankings;
		else
			throw new Error("Invalid component type!");
	}

	/**
	 * Returns the absolute path to the base component directory ending
	 * <b>with</b> a file separator
	 * 
	 * @param componentType
	 *            The type of the components whose base directory is requested.
	 * @return The absolute path to the base component directory.
	 */
	public static String getComponentDirectory(final ComponentType componentType) {
		return COMPONENTINSTALLDIR.get(componentType) + File.separator;
	}
 
	/**
	 * Returns true if all required types of components (problem, method,
	 * representation, ranking) are selected.
	 * 
	 * @return True if one component of each type is selected.
	 */
	public static boolean checkState() {
		if ((SELECTED_PROBLEM != null) && (SELECTED_METHOD != null)
				&& (SELECTED_RANKING != null)
				&& (SELECTED_REPRESENTATION != null)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the value properly conforms with the given property type.
	 * 
	 * @param type
	 *            The property type against the value check is performed.
	 * @param value
	 *            The value to be used for the check.
	 * @return True if the value conforms the given property type, false
	 *         otherwise.
	 */
	public static boolean checkType(final XMLFieldType type, final String value) {
		try {
			switch (type) {
			case INT:
				Integer.parseInt(value);
				break;
			case LONG:
				Long.parseLong(value);
				break;
			case FLOAT:
				Float.parseFloat(value);
				break;
			case BOOLEAN:
				if ((value.equalsIgnoreCase("true"))
						|| (value.equalsIgnoreCase("false")))
					return true;

				return false;
			default:
				break;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the extension of the given <code>File</code>.
	 * 
	 * @param file
	 *            The file whose extension is requested.
	 * @return The extension of this file as it is stored in the file system.
	 */
	public static String getExtension(final File file) {
		String fileName = file.getName();
		// get the last period separator
		int mid = fileName.lastIndexOf(".");
		return (fileName.substring(mid + 1, fileName.length()));
	}

	/**
	 * Launches an optimization based on the parameters loaded from the given
	 * XML session file.
	 * <p>
	 * It is basically a combination of <code>loadSession</code> and then a
	 * <code>runSimulation</code> right after it. The starting seed is set to a
	 * static variable if not defined by the XML.
	 * 
	 * @param xmlfile
	 *            The XML session file that contains the simulation parameters.
	 */
	private static void runSessionFromFile(String xmlfile) {
		loadSession(new File(xmlfile));
		FrevoMain.setCurrentRun(0);
		runSimulation(FrevoMain.customName, null);
	}

	/**
	 * Runs a single evaluation session on the provided candidates with the
	 * given problem configuration. If the number of candidates in the array is
	 * not within the range specified in the <i>problem</i>'s requirements then
	 * an <code>IllegalStateException</code> will be thrown.
	 * 
	 * @param candidates
	 *            The array of candidates to be evaluated.
	 * @param problemData
	 *            The source XML descriptor of the problem component to be used.
	 * @param properties
	 *            The properties map used for configuring the problem component.
	 *            If <tt>null</tt> is passed then the problem's default
	 *            properties will be used.
	 * @param seed
	 *            The random seed used for the session
	 * @throws IllegalStateException
	 *             if the number of candidates is not within the range defined
	 *             by the problem.
	 */
	public static void evaluateCandidates(
			ArrayList<AbstractRepresentation> candidates,
			ProblemXMLData problemData,
			Hashtable<String, XMLFieldEntry> properties, long seed)
			throws IllegalStateException {

		// Get the number of required minimum and maximum players
		Hashtable<String, XMLFieldEntry> req = problemData.getRequirements();

		XMLFieldEntry minc = req.get("minimumCandidates");
		int minimumplayers = Integer.parseInt(minc.getValue());
		XMLFieldEntry maxc = req.get("maximumCandidates");
		int maximumplayers = Integer.parseInt(maxc.getValue());
		int selectednumber = candidates.size();
		if ((selectednumber >= minimumplayers)
				&& (selectednumber <= maximumplayers)) {
			try {
				if (maximumplayers == 1) {
					AbstractSingleProblem ip = (AbstractSingleProblem) problemData
							.getNewProblemInstance();
					if (properties != null)
						ip.setProperties(properties);

					ip.setRandom(new NESRandom(seed));

					// System.out.println("Evaluating candidate...");
					System.out.println("Fitness: "
							+ ip.evaluateFitness(candidates.get(0)));
					// System.out.println("Evaluation finished");
				} else {
					// evaluate AbstractMultiProblem
					AbstractMultiProblem imp = (AbstractMultiProblem) problemData
							.getNewProblemInstance();
					if (properties != null)
						imp.setProperties(properties);

					// set random
					imp.setRandom(new NESRandom(seed));

					// convert list to array
					AbstractRepresentation[] representations = new AbstractRepresentation[candidates
							.size()];
					candidates.toArray(representations);

					// evaluate candidates
					List<RepresentationWithScore> results = imp
							.evaluateFitness(representations);

					// display results
					for (int i = 0; i < results.size(); i++) {
						RepresentationWithScore res = results.get(i);
						System.out.println(i + ". "
								+ res.getRepresentation().getHash() + " : "
								+ res.getScore());
					}
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			// number of candidates in the array is not in the required range
			String help = "";
			if ((minimumplayers == 1) && (maximumplayers == 1))
				help = "Please select exactly one candidate!";
			else
				if (minimumplayers != maximumplayers)
					help = "Please use the SHIFT and CTRL keys to select min. "
							+ minimumplayers + ", max. " + maximumplayers
							+ " representations.";
				else
					help = "Please use the SHIFT and CTRL keys to select exactly "
							+ minimumplayers + " representations.";
			if (graphics) {
				JOptionPane.showMessageDialog(mainWindow,
						"The number of selected candidates are invalid. "
								+ help, "Selection error",
						JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Returns the representation at the given rank loaded from an results XML
	 * file.
	 * 
	 * @param loadfile
	 *            The XML results file to be loaded.
	 * @param rank
	 *            The position of the requested representation in the XML
	 *            results file.
	 * @return A representation at the given position from the XML results file.
	 */
	public static AbstractRepresentation getRepresentation(File loadfile,
			int rank) {
		// load session
		loadSession(loadfile);

		ComponentXMLData representation = SELECTED_REPRESENTATION;
		AbstractRepresentation net2h = null;

		Document doc = SafeSAX.read(loadfile, true);

		try {
			Node dpopulations = doc.selectSingleNode("/frevo/populations");
			List<?> npops = dpopulations.selectNodes(".//population");
			Iterator<?> it = npops.iterator();
			npops.get(rank);
			while (it.hasNext()) {
				Node nd = (Node) it.next();
				List<?> npops1 = nd.selectNodes("./*");
				Iterator<?> it1 = npops1.iterator();

				Node net = (Node) it1.next();
				net2h = representation.getNewRepresentationInstance(0, 0, null);
				net2h.loadFromXML(net);

				return net2h.clone();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Saves all statistics to the default directory (/Results/session) */
	public static void writeStatisticsToDisk() {
		writeStatisticsToDisk(FREVO_INSTALL_DIRECTORY + File.separator
				+ "Results" + File.separator + customName);
	}

	/**
	 * Saves the statistics data to the target directory.
	 * 
	 * @param pathToSaveDirectory
	 *            Path to the directory for saving the statistics data to.
	 */
	public static void writeStatisticsToDisk(String pathToSaveDirectory)
			throws IllegalAccessError {
		File saveDir = new File(pathToSaveDirectory);

		if (!saveDir.exists()) {
			saveDir.mkdir();
		}

		if (saveDir.exists() && saveDir.isDirectory()) {
			// save all data to this directory

			StatKeeper.saveNotableStats(pathToSaveDirectory + "/stats.csv",
					FrevoMain.STATISTICS, true);
			StatKeeper.saveRboxFile(pathToSaveDirectory + "/rboxplot.txt",
					FrevoMain.STATISTICS, 1);
		} else {
			throw new IllegalAccessError(
					"The provided directory does not exist: "
							+ pathToSaveDirectory);
		}

	}

	/**
	 * Returns a human readable name of the given component type.
	 * <p>
	 * E.g. <code>FREVO_IPROBLEM</code> : <i>Problem</i>
	 * 
	 * @return A human readable name of the given component type.
	 */
	public static String getComponentTypeAsString(ComponentType ctype) {
		switch (ctype) {
		case FREVO_PROBLEM:
			return "Problem";
		case FREVO_MULTIPROBLEM:
			return "MultiProblem";
		case FREVO_METHOD:
			return "Method";
		case FREVO_REPRESENTATION:
			return "Representation";
		case FREVO_BULKREPRESENTATION:
			return "BulkRepresentation";
		case FREVO_RANKING:
			return "Ranking";
		}
		return null;
	}

	/**
	 * Saves FREVO settings to the configuration file. Initiates file writing to
	 * store data.
	 */
	public static void saveSettings() {
		Document doc;
		File configfile = new File(FREVO_INSTALL_DIRECTORY
				+ "/frevo_config.xml");
		if (configfile.exists()) {
			// load data if it already exists
			doc = SafeSAX.read(configfile, true);

			Node windowsizes = doc.selectSingleNode("/frevo/window-sizes");

			List<?> items = windowsizes.selectNodes(".//window");
			Iterator<?> it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");

				// save main data
				if (name.equals("main")) {
					el.addAttribute("width",
							Integer.toString(FrevoMain.mainWindowParameters[0]));
					el.addAttribute("height",
							Integer.toString(FrevoMain.mainWindowParameters[1]));
					el.addAttribute("posX",
							Integer.toString(FrevoMain.mainWindowParameters[2]));
					el.addAttribute("posY",
							Integer.toString(FrevoMain.mainWindowParameters[3]));
				}
				// save component browser data
				if (name.equals("componentbrowser")) {
					el.addAttribute("width", Integer
							.toString(FrevoMain.componentBrowserParameters[0]));
					el.addAttribute("height", Integer
							.toString(FrevoMain.componentBrowserParameters[1]));
					el.addAttribute("topsplit", Integer
							.toString(FrevoMain.componentBrowserParameters[2]));
					el.addAttribute("bigsplit", Integer
							.toString(FrevoMain.componentBrowserParameters[3]));
				}
			}
			Node appearances = doc.selectSingleNode("/frevo/appearances");

			items = appearances.selectNodes(".//theme");
			it = items.iterator();

			while (it.hasNext()) {
				Element el = (Element) it.next();
				String name = el.valueOf("./@name");

				// save frevo theme data
				if (name.equals("frevoTheme")) {
					el.addAttribute("darkMode",
							Boolean.toString(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getDarkMode()));
				}
			}
		} else {
			try {
				configfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// file does not exist
			doc = DocumentHelper.createDocument();
			doc.addDocType("frevo", null, "config.dtd");
			Element dfrevo = doc.addElement("frevo");

			dfrevo.addElement("tags");

			Element windowsizes = dfrevo.addElement("window-sizes");
			Element mainwindow = windowsizes.addElement("window");
			mainwindow.addAttribute("name", "main");
			mainwindow.addAttribute("width",
					Integer.toString(FrevoMain.mainWindowParameters[0]));
			mainwindow.addAttribute("height",
					Integer.toString(FrevoMain.mainWindowParameters[1]));
			mainwindow.addAttribute("posX",
					Integer.toString(FrevoMain.mainWindowParameters[2]));
			mainwindow.addAttribute("posY",
					Integer.toString(FrevoMain.mainWindowParameters[3]));

			Element compwindow = windowsizes.addElement("window");
			compwindow.addAttribute("name", "componentbrowser");
			compwindow.addAttribute("width",
					Integer.toString(FrevoMain.componentBrowserParameters[0]));
			compwindow.addAttribute("height",
					Integer.toString(FrevoMain.componentBrowserParameters[1]));
			compwindow.addAttribute("topsplit",
					Integer.toString(FrevoMain.componentBrowserParameters[2]));
			compwindow.addAttribute("bigsplit",
					Integer.toString(FrevoMain.componentBrowserParameters[3]));
			
			Element appearances = dfrevo.addElement("appearances");
			Element frevoTheme = appearances.addElement("theme");
			frevoTheme.addAttribute("name", "frevoTheme");
			frevoTheme.addAttribute("darkMode", Boolean.toString(launchInDarkMode));
		}

		// write to file
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setLineSeparator(System.getProperty("line.separator"));

			configfile.createNewFile();
			FileWriter out = new FileWriter(configfile);
			BufferedWriter bw = new BufferedWriter(out);
			XMLWriter wr = new XMLWriter(bw, format);
			wr.write(doc);
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates a new <code>File</code> instance by converting the given pathname
	 * string into an abstract pathname. If the given string point to a file
	 * that does not exist then it replaces file separators and tries again.
	 * 
	 * @param filepath
	 *            A pathname string
	 * @return A new <code>File</code> instance with the given file path.
	 */
	public static File loadSystemIndependentFile(String filepath) {
		File file = new File(filepath);

		if (file.exists()) {
			return file;
		}
		// try with system dependent slashes
		filepath.replace('/', File.separatorChar);
		filepath.replace('\\', File.separatorChar);

		file = new File(filepath);
		return file;
	}

	/**
	 * This class is a container for a category used for sorting different
	 * components. It contains a list of <code>String</code> objects (keywords)
	 * and a corresponding image.
	 */
	public static class KeywordCategory {
		/** The name of this category. */
		private String name;
		/** List containing the keywords. */
		private ArrayList<String> keywords;
		/** The path to the image file representing this category. */
		private String imagepath;
		/**
		 * Number of elements associated to this category. -1 means it is
		 * undefined yet.
		 */
		private int elements = -1;

		/**
		 * Constructs a new category with the given name, list of keywords and
		 * the path to an image <code>File</code>.
		 * 
		 * @param name
		 *            The name of this category.
		 * @param keywords
		 *            A list of <code>String</code> objects for the
		 *            corresponding keywords.
		 * @param imagepath
		 *            A path to an image representing this category.
		 */
		public KeywordCategory(String name, ArrayList<String> keywords,
				String imagepath) {
			this.name = name;
			this.keywords = keywords;
			this.imagepath = imagepath;
			this.elements = -1;
		}

		/**
		 * Returns the name of this category.
		 * 
		 * @return The name of this category.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns a list of keywords in <code>String</code> objects.
		 * 
		 * @return A list of keywords.
		 */
		public ArrayList<String> getKeywords() {
			return keywords;
		}

		/**
		 * Returns the path to the representative image of this category. The
		 * path is not validated if it points to an existing image file.
		 * 
		 * @return The path to the representative image of this category.
		 */
		public String getImagePath() {
			return imagepath;
		}

		/**
		 * Sets the number of elements that are associated to this category.
		 * 
		 * @param elements
		 *            The new number of elements associated to this category.
		 */
		public void setElements(int elements) {
			this.elements = elements;
		}

		/**
		 * Returns a human-readable name of this category with the number of
		 * elements written in brackets.
		 * 
		 * @return The display name of this category with the number of elements
		 *         written in brackets.
		 */
		@Override
		public String toString() {
			if (elements != -1)
				return name + " (" + elements + ")";

			return name;
		}

	}

	/** FileFilter class that returns only .xml files. */
	public static class XMLFileFilter implements FilenameFilter {
		@Override
		public boolean accept(File file, String name) {
			// filter for XML files
			if (name.length() < 4)
				return false;
			if ((name.substring(name.length() - 4, name.length()))
					.equals(".xml"))
				return true;

			return false;
		}
	}

	/**
	 * Returns the <code>StatKeeper</code> element with the given index. If
	 * "skipInvisible" is set to true then non-displayed statistics will not
	 * count.
	 * 
	 * @param index
	 *            Index of the requested statistics.
	 * @return The stored statistics data at the given index.
	 * @throws IndexOutOfBoundsException
	 *             if index is invalid
	 */
	public static ArrayList<StatKeeper> getStatistics() {
		return STATISTICS;
	}

	/**
	 * Returns the array list with the statistics to be displayed
	 */
	public static ArrayList<StatKeeper> getStatisticsToDisplay() {
		return DISPLAYSTATISTICS;
	}

	/**
	 * Returns <code>true</code> if this <code>StatKeeper</code> object is
	 * currently loaded by FREVO. More formally, returns <tt>true</tt> if and
	 * only if the FREVO statistics contain at least one element <tt>e</tt> such
	 * that <tt>(sk==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;sk.equals(e))</tt>.
	 * 
	 * @param statkeeper
	 *            element whose presence is to be tested
	 * @return true if this <code>StatKeeper</code> object is currently loaded
	 *         by FREVO
	 */
	public static boolean isStatKeeperLoaded(StatKeeper statkeeper) {
		return STATISTICS.contains(statkeeper);
	}

	/**
	 * Adds the given <code>StatKeeper</code> object to FREVO. Objects added
	 * will be monitored and saved if needed.
	 * 
	 * @param statkeeper
	 *            StatKeeper to be added
	 */
	public static void addStatistics(StatKeeper statkeeper, boolean todisplay) {
		STATISTICS.add(statkeeper);
		if (todisplay == true)
			DISPLAYSTATISTICS.add(statkeeper);
	}

	/** Removes all previously added <code>StatKeeper</code> objects from FREVO. */
	public static void eraseStatistics() {
		STATISTICS.clear();
	}

	/**
	 * Exports the selected component into the given file.
	 * <p>
	 * All data found in the component directory will be added to the exported
	 * package that will be compressed by a ZIP algorithm. Adding the source
	 * .java files can be prohibited with the <code>addSources</code> flag.
	 * 
	 * @param component
	 *            The component to be exported.
	 * @param savefile
	 *            The output file that will contain the data of the component.
	 * @param addsources
	 *            Flag indicating if the corresponding .java sources are also
	 *            included or not.
	 */
	public static void exportComponent(ComponentXMLData component,
			File savefile, boolean addsources) {
		// correct file extension if missing
		if (!FrevoMain.getExtension(savefile).equals(FREVO_PACKAGE_EXTENSION)) {
			savefile = new File(savefile.getAbsolutePath() + "."
					+ FREVO_PACKAGE_EXTENSION);
		}

		// add root xml file to the package
		File rootXMLFile = component.getSourceXMLFile();

		// get class root directory
		String comprootdirname = component.getClassDir().split("/")[0];

		// get component root directory
		String comprootdir = getComponentDirectory(component.getComponentType())
				+ comprootdirname;

		// root directory for relative paths for addressing data within the
		// archive
		File comproot = new File(FREVO_INSTALL_DIRECTORY + File.separator
				+ "Components");

		File directory = new File(comprootdir);

		URI base = comproot.toURI();
		Deque<File> queue = new LinkedList<File>();

		// add base xml to the archive
		queue.push(rootXMLFile);

		// add component directory
		queue.push(directory);
		OutputStream out;
		try {
			out = new FileOutputStream(savefile);
		} catch (FileNotFoundException e) {
			System.err.println("Cannot create file for output!");
			return;
		}
		Closeable res;// = out;

		ZipOutputStream zout = new ZipOutputStream(out);
		// out.close();
		res = zout;
		try {
			while (!queue.isEmpty()) {
				directory = queue.pop();
				if (directory.isDirectory()) {
					for (File kid : directory.listFiles()) {
						String name = base.relativize(kid.toURI()).getPath();
						if (kid.isDirectory()) {
							// do not export svn data if present
							if (!kid.getName().equals(".svn")) {
								queue.push(kid);
								name = name.endsWith("/") ? name : name + "/";
								zout.putNextEntry(new ZipEntry(name));
							}
						} else {
							if (!addsources
									&& (getExtension(new File(name))
											.equals("java"))) {
								// do not add source if not wanted by user
							} else {
								System.out.println("Adding " + name);
								zout.putNextEntry(new ZipEntry(name));
								copy(kid, zout);
								zout.closeEntry();
							}
						}
					}
				} else {
					String name = base.relativize(directory.toURI()).getPath();
					System.out.println("Adding " + name);

					zout.putNextEntry(new ZipEntry(name));
					copy(directory, zout);
					zout.closeEntry();
				}
			}
			// report to user
			System.out.println("Component " + component
					+ " has been exported succesfully!");
			System.out.println();
			res.close();
		} catch (IOException e) {
			System.err.println("Cannot write output file!");
		}

	}

	/**
	 * Copies data directly from one <code>InputStream</code> to another
	 * <code>OutputStream</code>.
	 * 
	 * @param in
	 *            The <code>InputStream</code> to copy data from.
	 * @param out
	 *            The <code>OutputStream</code> to copy data to.
	 * @throws IOException
	 *             if I/O operation fails.
	 */
	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	/**
	 * Copies data from a <code>File</code> to a given <code>OutputStream</code>
	 * object.
	 * 
	 * @param file
	 *            The <code>File</code> to read from.
	 * @param out
	 *            The <code>OutputStream</code> to copy data to.
	 * @throws IOException
	 *             if I/O operation fails.
	 */
	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

	/**
	 * Copies data from an <code>InputStream</code> to a given <code>File</code>
	 * .
	 * 
	 * @param in
	 *            The <code>InputStream</code> to copy data from.
	 * @param file
	 *            The <code>File</code> to write data to.
	 * @throws IOException
	 *             if I/O operation fails.
	 */
	private static void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);
		} finally {
			out.close();
		}
	}

	/**
	 * Imports a component packaged in the FREVO package format (zcp). Copied
	 * files will still persist even if operation fails or the component is bad.
	 * 
	 * @param importfile
	 *            The ZCP file to be imported.
	 */
	public static void importComponent(File importfile) {
		// unzip to the given location
		String outputdir = null;
		URI frevo_install_base = new File(FrevoMain.getInstallDirectory())
				.toURI();
		try {
			ZipFile zfile = new ZipFile(importfile);
			File directory = new File(FREVO_INSTALL_DIRECTORY + File.separator
					+ "Components");

			Enumeration<? extends ZipEntry> entries = zfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File file = new File(directory, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					file.getParentFile().mkdirs();
					InputStream in = zfile.getInputStream(entry);
					try {
						copy(in, file);
						// copy data from root XML
						String extension = FrevoMain.getExtension(file);
						if (extension.equals("xml")) {
							URI fileURI = frevo_install_base.relativize(file
									.toURI());
							String uristring = fileURI.toString();
							String opath = uristring
									.substring(0, uristring.length()
											- extension.length() - 1);
							if ((outputdir == null)
									|| (opath.length() < outputdir.length())) {
								outputdir = opath;
							}
						}
					} finally {
						in.close();
					}
				}
			}
			zfile.close();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// component successfully installed, add to Eclipse classpath
		File classpath = new File(FREVO_INSTALL_DIRECTORY + File.separator
				+ ".classpath");
		if (classpath.exists()) {
			System.out.println("Adjusting Eclipse classpath...");
			Document doc = SafeSAX.read(classpath, true);

			Element root = doc.getRootElement();

			Element componentElement = root.addElement("classpathentry");
			componentElement.addAttribute("kind", "src");
			componentElement.addAttribute("output", outputdir);
			componentElement.addAttribute("path", outputdir);

			// save file
			try {
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setLineSeparator(System.getProperty("line.separator"));
				FileWriter out = new FileWriter(classpath);
				BufferedWriter bw = new BufferedWriter(out);
				XMLWriter wr = new XMLWriter(bw, format);
				wr.write(doc);
				wr.close();
			} catch (IOException e) {
				System.err
						.println("ERROR: Could not write Eclipse classpath file!");
				e.printStackTrace();
			}
		}

	}

	/**
	 * Resets FREVO to its initial state without reloading the components. Any
	 * ongoing simulations will be erased.
	 */
	public static void reset() {
		FrevoMain.setCurrentRun(0);
		FrevoMain.isRunning = false;
		FrevoMain.eraseStatistics();
	}

	/**
	 * Removes the component from the installed components. This function also
	 * removes all files within the component's directory. Forces FREVO to
	 * reload all components afterwards.
	 * 
	 * @param cdata
	 *            The component to be removed
	 */
	public static void deleteComponent(ComponentXMLData cdata) {

		// remove base XML
		File baseXML = cdata.getSourceXMLFile();
		baseXML.delete();

		// erase all files within component directory
		// get class root directory
		String classDir = cdata.getClassDir();

		// extract directory name
		// construct copy
		int i = 0;
		while (i < classDir.length()) {
			char c = classDir.charAt(i);
			if ((c == '/') || (c == '\\')) {
				i++;
			} else {
				break;
			}
		}

		int end = i + 1;
		while (end < classDir.length()) {
			char c = classDir.charAt(end);
			if ((c == '/') || (c == '\\')) {
				break;
			} else {
				end++;
			}
		}

		classDir = classDir.substring(i, end);

		String comprootdirname = classDir.split("/")[0];

		// get component root directory
		String comprootdir = FrevoMain.getComponentDirectory(cdata
				.getComponentType()) + comprootdirname;

		File rootdir = new File(comprootdir);

		eraseDirectory(rootdir);

		// Remove entry from classpath
		File classpath = new File(FrevoMain.getInstallDirectory()
				+ File.separator + ".classpath");
		if (classpath.exists()) {
			Document doc = SafeSAX.read(classpath, true);

			Element root = doc.getRootElement();
			String output; // the string to match the "output" field in
			// classpath xml

			// correct pathname for multiproblems
			if (cdata.getComponentType() == ComponentType.FREVO_MULTIPROBLEM)
				output = "Components/" + "Problems/" + comprootdirname;
			else if (cdata.getComponentType() == ComponentType.FREVO_BULKREPRESENTATION)
				output = "Components/" + "Representations/" + comprootdirname;
			else
				output = "Components/"
						+ FrevoMain.getComponentTypeAsString(cdata
								.getComponentType()) + "s/" + comprootdirname;
			// System.out.println("removing "+output);
			Node node = root.selectSingleNode("classpathentry[@output='"
					+ output + "']");
			if (node != null)
				node.detach();

			// save XML
			try {
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setLineSeparator(System.getProperty("line.separator"));
				FileWriter out = new FileWriter(classpath);
				BufferedWriter bw = new BufferedWriter(out);
				XMLWriter wr = new XMLWriter(bw, format);
				wr.write(doc);
				wr.close();
			} catch (IOException e) {
				System.err
						.println("ERROR: Could not write Eclipse classpath file!");
				e.printStackTrace();
			}

		}

		// force reloading components
		FrevoMain.reLoadComponents(true);
	}

	/**
	 * Recursively erases a directory with all data within. Probably will fail
	 * on symbolic links.
	 * 
	 * @param directory
	 *            The directory to be erased
	 */
	private static void eraseDirectory(File directory) {
		File[] currList;
		Stack<File> stack = new Stack<File>();
		stack.push(directory);
		while (!stack.isEmpty()) {
			if (stack.lastElement().isDirectory()) {
				currList = stack.lastElement().listFiles();
				if (currList.length > 0) {
					for (File curr : currList) {
						stack.push(curr);
					}
				} else {
					stack.pop().delete();
				}
			} else {
				stack.pop().delete();
			}
		}
	}

	/**
	 * Notifies FREVO window about changing of last generation
	 * 
	 * @param method
	 *            method whose last state has been changed. If method contains
	 *            information about last generation it could be saved
	 */
	public static void methodStateChanged(AbstractMethod method) {
		FrevoWindow window = getMainWindow();
		if (window != null) {
			window.methodStateChanged(method);
		}
	}
	

	/**
	 * Notifies FREVO window about changing of continuation possibility.
	 * 
	 * @param method
	 *            method whose continuation possibility has been changed.
	 */
	public static void changeContinueState(AbstractMethod method) {
		FrevoWindow window = getMainWindow();
		if (window != null) {
			window.changeContinueState(method);
		}
	}
}
