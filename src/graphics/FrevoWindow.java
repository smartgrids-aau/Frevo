package graphics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;

import utils.ComponentCreator;
import utils.NESRandom;
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
import core.XMLMethodStep;

/**
 * The main GUI for FREVO. It only serves as a graphical access to FREVO's
 * abilities, displays progress
 */
public class FrevoWindow extends JFrame implements MouseListener,
		ActionListener, WindowListener, DocumentListener, TableModelListener{

	private static final long serialVersionUID = -8505581652138492581L;

	/**
	 * Defines if the standard output is redirected to the FREVO graphical
	 * console
	 */
	private final boolean REDIRECTCONSOLE = true;

	/** Defines the starting time of the simulation */
	public static long startTime = 0;
	
	/**Determines how much brighter images should get in darkMode */
	public static final float DARK_MODE_BRIGHTNESS = 1.15f;

	// Image files
	static ImageIcon nothingIcon = null;
	private ImageIcon problemIcon = null;
	private ImageIcon problem_hIcon = null;
	private ImageIcon methodIcon = null;
	private ImageIcon method_hIcon = null;
	private ImageIcon representationIcon = null;
	private ImageIcon representation_hIcon = null;
	private ImageIcon rankingIcon = null;
	private ImageIcon ranking_hIcon = null;
	private ImageIcon advancedIcon = null;
	private ImageIcon advanced_hIcon = null;
	private ImageIcon loadingIcon = null;
	private ImageIcon frevoIcon = null;

	// ---- Main Layout ----
	private GroupLayout mainlayout;
	private GroupLayout loadLayout;
	private GroupLayout topLayout;
	private FrevoLoadingPanel loadingPanel;
	private Container mainContainer;
	private JSplitPane splitPane;
	private JPanel topPanel;
	private JTabbedPane statisticPanel;
	private JTabbedPane populationsTabbedPane;
	private JProgressBar progressBar;
	private JButton consolesaveButton;
	private JButton consoleclearButton;

	// ---- Batch Eval Layout ----
	private JSplitPane batchEvalSplitPane;
	private GroupLayout batchEvalLayout;
	private JPanel batchQueuePanel;
	private BatchQueueTableModel batchQueueTableModel;
	private JBatchQueueTable batchQueueTable;
	private JButton addToQueueButton;
	private JButton eraseQueueButton;
	private JButton backFromEvaluationButton;
	private JButton startBatchEvaluationButton;
	@SuppressWarnings("rawtypes")
	private JList representationsList;
	private JScrollPane selectedEvalElementPropertiesTablePane;
	private JPropertiesTable selectedEvalElementPropertiesTable;
	private PropertiesTableModel selectedEvalElementPropertiesTableModel;
	private AddTaskToQueueWindow addTaskWindow;

	// ---- Config Panel ----
	private JPanel configPanel;

	private JLabel problemButton;
	private JLabel methodButton;
	private JLabel representationButton;
	private JLabel rankingButton;
	private JLabel problemLabel;
	private JLabel methodLabel;
	private JLabel representationLabel;
	private JLabel rankingLabel;
	private JLabel advancedButton;
	private JTextPane advancedLabel;

	// ---- Advanced Panel ----
	private JPanel advancedPanel;

	private JLabel numberofrunsLabel;
	private JIntegerTextField numberofrunsTextField;
	private JLabel seedLabel;
	private JIntegerTextField seedTextField;
	private JLabel simnameLabel;
	private JTextField simnameTextField;
	private JButton advancedSettingsBackButton;

	// ---- Control Panel ----
	private JPanel simcontrolPanel;
	private JPanel lastGenerationPanel;

	private JButton startButton;
	private JButton stopButton;
	private JButton resetButton;
	private JButton saveCurrentButton;
	private JButton replayCurrentButton;

	// ---- Results Panel ----
	private JPanel resultsPanel;
	private JPanel evaluationPanel;
	private JButton replayButton;
	private JButton detailsButton;
	private JButton closeresButton;
	private JButton continueButton;
	private JButton tournamentButton;
	private JLabel seedresLabel;
	private JIntegerTextField seedresTextField;
	private JPropertiesTable evalTable;
	private JPopupMenu representationMenu;

	// ---- Evaluate Control Panel
	private JPanel evalControlPanel;

	private JButton startEvalButton;
	private JButton batchEvalButton;

	// Panes
	private JTextPane consoleTextPane;
	private JScrollPane consoleScrollPane;
	private JSplitPane resultsSplitPane;
	private JScrollPane evalScrollPane;

	// Menu items
	private JMenuItem aboutItem;
	private JMenuItem loadResultsItem;
	private JMenuItem loadSessionItem;
	private JMenuItem saveasItem;
	private JMenuItem saveItem;
	private JMenuItem exitItem;
	private JMenuItem ccreatorItem;
	private JMenuItem simconnectorItem;
	private JMenuItem exportComponentItem;
	private JMenuItem importComponentItem;
	private JMenuItem eraseComponentItem;
	private JMenuItem reloadComponentsItem;
	private JMenuItem toggleDarkModeItem;
	private JMenu loadMenu;

	// Component selectors
	ComponentBrowser pBrowser;
	ComponentBrowser mBrowser;
	ComponentBrowser reBrowser;
	ComponentBrowser raBrowser;

	ComponentSelector exportSelector;
	ComponentSelector deleteSelector;

	/** MAC ONLY: pointer to the application. */
	Application application = null;
	/** MAC ONLY: pointer to the OSX icon. */
	BufferedImage macIcon = null;

	private static final int RECENT_LIST_LENGTH = 3;

	/** List containing the latest saved sessions */
	private LinkedList<File> recentSessions = new LinkedList<File>();

	/** List containing the latest saved results */
	private LinkedList<File> recentResults = new LinkedList<File>();

	/** Worker Thread that runs the <code>method</code> behind the graphics */
	private SimulationWorkerThread simulationWorkerThread;

	/** Indicates if a session is currently in progress */
	boolean sessionIsPaused = false;
	
	/** Show tournament button */
	boolean showTournamentButton = false;

	private long activeSeed = FrevoMain.getInitialSeed();

	private ArrayList<AbstractRepresentation> candidatesToEvaluate = new ArrayList<AbstractRepresentation>();

	/** Loaded populations */
	private ArrayList<ArrayList<AbstractRepresentation>> populations;
	
	ArrayList<JTable> jTables = new ArrayList<JTable>();

	// streams for console redirection
	private final PipedInputStream pin = new PipedInputStream();
	private final PipedInputStream pin2 = new PipedInputStream();

	private Thread stdOutReader;
	private Thread stdErrReader;
	StyledDocument consoleDoc;
	Style LabelStyle;	//style for advancedLabel
	Style OutStyle;
	Style ErrStyle;

	private File activesessionfile;

	//private File loadedFile;	//repetitive, as it's only use is saving the currently selected component
								//however the components are already saved through the window anyway

	// Constants
	public final static Dimension iconDim = new Dimension(48, 48);

	@SuppressWarnings("rawtypes")
	public FrevoWindow() {
		super("FREVO - Framework for Evolutionary Design " + "v"
				+ FrevoMain.getMajorVersion() + " Rev:"
				+ FrevoMain.getMinorVersion());
		// sets default close operation
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// set initial size
		setBounds(FrevoMain.mainWindowParameters[2], FrevoMain.mainWindowParameters[3], FrevoMain.mainWindowParameters[0],
				FrevoMain.mainWindowParameters[1]);

		// Inherit main frame
		mainContainer = this.getContentPane();

		// Creates the window in the center of the screen
		//this.setLocationRelativeTo(null);	//replaced by FrevoMain.mainWindowParameters[2-3]

		// Defines minimum size
		setMinimumSize(new Dimension(700, 550));

		// Set tooltips to appear almost immediately
		ToolTipManager.sharedInstance().setInitialDelay(10);
		
		this.addMouseListener(this);

		// Change the color of the progressbar
		// color of the background
		UIManager.put("ProgressBar.background", ((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getWhite());

		if(FrevoMain.launchInDarkMode)
		{
			// color of progress bar
			UIManager.put("ProgressBar.foreground", Color.BLUE.darker().darker());
			// color of percentage counter on the base background
			UIManager.put("ProgressBar.selectionBackground", new Color(0x7777FF));
		}
		else
		{
			// color of progress bar
			UIManager.put("ProgressBar.foreground", Color.GREEN.darker().darker()
					.darker());
			// color of percentage counter on the base background
			UIManager.put("ProgressBar.selectionBackground", Color.GREEN.darker()
					.darker().darker());
		}
		
		// color of percentage counter on the bar
		UIManager.put("ProgressBar.selectionForeground", Color.WHITE);

		// TODO change color of other UI components (buttons, borders)
		

		// Load images
		try {
			// Load icon set
			List<BufferedImage> iconList = new LinkedList<BufferedImage>();
			iconList.add(ImageIO.read(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/" + "frevo_icon_32.png")));
			iconList.add(ImageIO.read(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/" + "frevo_icon_64.png")));
			iconList.add(ImageIO.read(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/" + "frevo_icon_128.png")));
			iconList.add(ImageIO.read(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/" + "frevo_icon_256.png")));
			iconList.add(ImageIO.read(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/" + "frevo_icon_512.png")));
			if (nothingIcon == null)
				nothingIcon = getAdaptedImage(
					ClassLoader.getSystemResource("noimage.png"));
			problemIcon = getAdaptedImage(
					ClassLoader.getSystemResource("problem.png"));
			problem_hIcon = getAdaptedImage(
					ClassLoader.getSystemResource("problem_h.png"));
			methodIcon = getAdaptedImage(
					ClassLoader.getSystemResource("method.png"));
			method_hIcon = getAdaptedImage(
					ClassLoader.getSystemResource("method_h.png"));
			representationIcon = getAdaptedImage(
					ClassLoader.getSystemResource("representation.png"));
			representation_hIcon = getAdaptedImage(
					ClassLoader.getSystemResource("representation_h.png"));
			rankingIcon = getAdaptedImage(
					ClassLoader.getSystemResource("ranking.png"));
			ranking_hIcon = getAdaptedImage(
					ClassLoader.getSystemResource("ranking_h.png"));
			advancedIcon = getAdaptedImage(
					ClassLoader.getSystemResource("gear.png"));
			advanced_hIcon = getAdaptedImage(
					ClassLoader.getSystemResource("gear_h.png"));
			if(FrevoMain.launchInDarkMode)
				loadingIcon = new ImageIcon(
						ClassLoader.getSystemResource("frevo_loading_d.png"));
			else
				loadingIcon = new ImageIcon(
						ClassLoader.getSystemResource("frevo_loading.png"));
			frevoIcon = new ImageIcon(
					ClassLoader.getSystemResource("frevo_icon_128.png"));

			// Set FREVO application icon
			this.setIconImages(iconList);

			// ------ OS-X things -------
			application = new DefaultApplication();
			if (application.isMac()) {
				macIcon = iconList.get(4);
				application.setApplicationIconImage(macIcon);
			} else {
				application = null;
			}
		} catch (IOException e) {
			System.err.println("Image loading error!");
			e.printStackTrace();
			System.exit(0);
		}

		// **************MENU+ITEMS************************************
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
				"Access Frevo settings");
		menuBar.add(menu);

		// Load menu
		loadMenu = new JMenu("Open");
		loadMenu.setMnemonic(KeyEvent.VK_L);
		menu.add(loadMenu);

		// Load menu item
		loadResultsItem = new JMenuItem("Results", KeyEvent.VK_R);
		loadResultsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.CTRL_MASK));
		loadResultsItem.getAccessibleContext().setAccessibleDescription(
				"Load FREVO results");

		loadResultsItem.addActionListener(this);

		loadSessionItem = new JMenuItem("Session", KeyEvent.VK_E);
		loadSessionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.CTRL_MASK));
		loadSessionItem.getAccessibleContext().setAccessibleDescription(
				"Load FREVO session");

		loadSessionItem.addActionListener(this);

		// build load menu
		buildLoadMenu();

		// Save as menu item
		saveasItem = new JMenuItem("Save as...", KeyEvent.VK_S);
		saveasItem.getAccessibleContext().setAccessibleDescription(
				"Save FREVO session");
		saveasItem.addActionListener(this);
		menu.add(saveasItem);

		// Save menu item
		saveItem = new JMenuItem("Save...", KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		saveItem.getAccessibleContext().setAccessibleDescription(
				"Save FREVO session");
		saveItem.setEnabled(false);
		saveItem.addActionListener(this);
		menu.add(saveItem);

		// Configuration
		JMenuItem propertiesItem = new JMenuItem("Properties", KeyEvent.VK_P);
		propertiesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				ActionEvent.CTRL_MASK));
		propertiesItem.getAccessibleContext().setAccessibleDescription(
				"FREVO Settings and properties");
		propertiesItem.setEnabled(false);
		menu.add(propertiesItem);

		menu.addSeparator();

		// Exit
		exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		exitItem.getAccessibleContext().setAccessibleDescription("Exit FREVO");
		exitItem.addActionListener(this);
		menu.add(exitItem);

		// About
		aboutItem = new JMenuItem("About FREVO");
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));
		aboutItem.addActionListener(this);

		// Components menu
		JMenu compmenu = new JMenu("Components");
		compmenu.setMnemonic(KeyEvent.VK_C);
		compmenu.getAccessibleContext().setAccessibleDescription(
				"Access Frevo components");
		menuBar.add(compmenu);

		// component creator
		ccreatorItem = new JMenuItem("Create new component...", KeyEvent.VK_N);
		ccreatorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.ALT_MASK));
		ccreatorItem.addActionListener(this);
		ccreatorItem.getAccessibleContext().setAccessibleDescription(
				"Create new component template");
		compmenu.add(ccreatorItem);

		exportComponentItem = new JMenuItem("Export component...");
		exportComponentItem.addActionListener(this);
		compmenu.add(exportComponentItem);

		importComponentItem = new JMenuItem("Import component...");
		importComponentItem.addActionListener(this);
		compmenu.add(importComponentItem);

		eraseComponentItem = new JMenuItem("Remove component...");
		eraseComponentItem.addActionListener(this);
		compmenu.add(eraseComponentItem);

		simconnectorItem = new JMenuItem("Connect to external simulator...");
		simconnectorItem.addActionListener(this);
		compmenu.add(simconnectorItem);
		simconnectorItem.setEnabled(false);

		reloadComponentsItem = new JMenuItem("Reload components");
		reloadComponentsItem.addActionListener(this);
		compmenu.add(reloadComponentsItem);
		
		// Options menu
		JMenu options = new JMenu("Options");
		options.setMnemonic(KeyEvent.VK_C);
		options.getAccessibleContext().setAccessibleDescription(
				"App-Settings");
		menuBar.add(options);
		
		toggleDarkModeItem = new JMenuItem();
		if(((FrevoTheme) MetalLookAndFeel.getCurrentTheme()).getDarkMode())
			toggleDarkModeItem.setText("Light mode");
		else
			toggleDarkModeItem.setText("Dark mode");
		toggleDarkModeItem.addActionListener(this);
		options.add(toggleDarkModeItem);

		// Help menu
		JMenu helpmenu = new JMenu("Help");
		helpmenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpmenu);
		helpmenu.add(aboutItem);

		this.setJMenuBar(menuBar);
		

		// **************PANELS*******************************************

		// configuration panel------------------------
		configPanel = new JPanel();
		Dimension leftDim = new Dimension(250, 266);
		configPanel.setMinimumSize(leftDim);
		configPanel.setMaximumSize(leftDim);

		TitledBorder configtitle = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2),
				"Configure Session");
		configtitle.setTitlePosition(TitledBorder.CENTER);
		configtitle.setTitleJustification(TitledBorder.CENTER);
		configPanel.setBorder(configtitle);

		problemButton = new JLabel(problemIcon);
		problemButton.setPreferredSize(iconDim);
		problemButton.setMinimumSize(iconDim);
		problemButton.setMaximumSize(iconDim);
		problemButton.setToolTipText("Select and configure Problem component");
		problemButton.addMouseListener(this);

		methodButton = new JLabel(methodIcon);
		methodButton.setPreferredSize(iconDim);
		methodButton.setMinimumSize(iconDim);
		methodButton.setMaximumSize(iconDim);
		methodButton.setEnabled(false);
		methodButton.addMouseListener(this);

		representationButton = new JLabel(representationIcon);
		representationButton.setPreferredSize(iconDim);
		representationButton.setMinimumSize(iconDim);
		representationButton.setMaximumSize(iconDim);
		representationButton.setEnabled(false);
		representationButton.addMouseListener(this);

		rankingButton = new JLabel(rankingIcon);
		rankingButton.setPreferredSize(iconDim);
		rankingButton.setMinimumSize(iconDim);
		rankingButton.setMaximumSize(iconDim);
		rankingButton.setEnabled(false);
		rankingButton.addMouseListener(this);

		problemLabel = new JLabel("Select Problem");
		problemLabel.setMinimumSize(iconDim);
		methodLabel = new JLabel("Select Method");
		methodLabel.setMinimumSize(iconDim);
		representationLabel = new JLabel("Select Representation");
		representationLabel.setMinimumSize(iconDim);
		rankingLabel = new JLabel("Select Ranking");
		rankingLabel.setMinimumSize(iconDim);

		Dimension prefdim = new Dimension(32, 32);
		advancedButton = new JLabel(advancedIcon);
		advancedButton.setMinimumSize(prefdim);
		advancedButton.setPreferredSize(prefdim);
		advancedButton.setMaximumSize(prefdim);
		advancedButton.setEnabled(true);
		advancedButton.addMouseListener(this);

		advancedLabel = new JTextPane();
		Dimension advdim = new Dimension(170, 32);
		advancedLabel.setMinimumSize(advdim);
		advancedLabel.setPreferredSize(advdim);
		advancedLabel.setMaximumSize(advdim);
		advancedLabel.setEditable(false);
		advancedLabel.setContentType("text/html");
		advancedLabel.setOpaque(false);
		updateAdvancedLabels();

		GroupLayout configlayout = new GroupLayout(configPanel);
		configPanel.setLayout(configlayout);
		int gap = 5;
		// horizontal
		configlayout.setHorizontalGroup(configlayout
				.createSequentialGroup()
				.addGap(5)
				.addGroup(
						configlayout
								.createParallelGroup(
										GroupLayout.Alignment.LEADING)
								.addGroup(
										configlayout.createSequentialGroup()
												.addComponent(problemButton)
												.addGap(gap)
												.addComponent(problemLabel))
								.addGroup(
										configlayout.createSequentialGroup()
												.addComponent(methodButton)
												.addGap(gap)
												.addComponent(methodLabel))
								.addGroup(
										configlayout
												.createSequentialGroup()
												.addComponent(
														representationButton)
												.addGap(gap)
												.addComponent(
														representationLabel))
								.addGroup(
										configlayout.createSequentialGroup()
												.addComponent(rankingButton)
												.addGap(gap)
												.addComponent(rankingLabel))
								.addGap(5)
								.addGroup(
										configlayout.createSequentialGroup()
												.addGap(9)
												.addComponent(advancedButton)
												.addGap(12)
												.addComponent(advancedLabel)))
				.addGap(5));

		// vertical
		configlayout
				.setVerticalGroup(configlayout
						.createParallelGroup()
						.addGap(5)
						.addGroup(
								configlayout
										.createSequentialGroup()
										.addGroup(
												configlayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																problemButton)
														.addGap(gap)
														.addComponent(
																problemLabel))
										.addGroup(
												configlayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																methodButton)
														.addGap(gap)
														.addComponent(
																methodLabel))
										.addGroup(
												configlayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																representationButton)
														.addGap(gap)
														.addComponent(
																representationLabel))
										.addGroup(
												configlayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																rankingButton)
														.addGap(gap)
														.addComponent(
																rankingLabel))
										.addGap(5)
										.addGroup(
												configlayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGap(9)
														.addComponent(
																advancedButton)
														.addGap(12)
														.addComponent(
																advancedLabel)))
						.addGap(5));

		// Panel for additional settings
		advancedPanel = new JPanel();
		Dimension advPanelDim = new Dimension(250, 266);
		advancedPanel.setMinimumSize(advPanelDim);
		advancedPanel.setMaximumSize(advPanelDim);

		TitledBorder advancedtitle = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2),
				"Simulation settings");
		advancedtitle.setTitlePosition(TitledBorder.CENTER);
		advancedtitle.setTitleJustification(TitledBorder.CENTER);
		advancedPanel.setBorder(advancedtitle);

		JPanel advcontenPanel = new JPanel();

		numberofrunsLabel = new JLabel("Number of runs:");
		numberofrunsTextField = new JIntegerTextField();
		numberofrunsTextField.setPreferredSize(new Dimension(100, 24));
		numberofrunsTextField.setText(Integer.toString(FrevoMain
				.getNumberOfSimulationRuns()));
		numberofrunsTextField.getDocument().addDocumentListener(this);

		seedLabel = new JLabel("Starting seed:");
		seedTextField = new JIntegerTextField();
		seedTextField.setPreferredSize(new Dimension(100, 24));
		seedTextField.setText(Long.toString(FrevoMain.getInitialSeed()));
		seedTextField.getDocument().addDocumentListener(this);

		simnameLabel = new JLabel("Simulation name:");
		simnameTextField = new JTextField();
		simnameTextField.setPreferredSize(new Dimension(100, 24));
		simnameTextField.setText(FrevoMain.getCustomName());
		simnameTextField.getDocument().addDocumentListener(this);

		GroupLayout advcontLayout = new GroupLayout(advcontenPanel);
		advcontenPanel.setLayout(advcontLayout);

		advcontLayout.setHorizontalGroup(advcontLayout
				.createParallelGroup()
				.addGroup(
						advcontLayout.createSequentialGroup()
								.addComponent(numberofrunsLabel).addGap(15)
								.addComponent(numberofrunsTextField))
				.addGap(10)
				.addGroup(
						advcontLayout.createSequentialGroup()
								.addComponent(seedLabel).addGap(15)
								.addComponent(seedTextField))
				.addGap(10)
				.addGroup(
						advcontLayout.createSequentialGroup()
								.addComponent(simnameLabel).addGap(15)
								.addComponent(simnameTextField)).addGap(10));

		advcontLayout.setVerticalGroup(advcontLayout
				.createSequentialGroup()
				.addGroup(
						advcontLayout.createParallelGroup()
								.addComponent(numberofrunsLabel).addGap(15)
								.addComponent(numberofrunsTextField))
				.addGap(10)
				.addGroup(
						advcontLayout.createParallelGroup()
								.addComponent(seedLabel).addGap(15)
								.addComponent(seedTextField))
				.addGap(10)
				.addGroup(
						advcontLayout.createParallelGroup()
								.addComponent(simnameLabel).addGap(15)
								.addComponent(simnameTextField)).addGap(10));

		advancedSettingsBackButton = new JButton("Confirm");
		advancedSettingsBackButton.addActionListener(this);

		JScrollPane advscrollPane = new JScrollPane(advcontenPanel);
		advscrollPane.setBorder(BorderFactory.createEmptyBorder());
		advscrollPane.setMinimumSize(new Dimension(240, 206));

		advancedPanel.setLayout(new GridBagLayout());
		GridBagConstraints constrains = new GridBagConstraints();

		constrains.gridx = 0;
		constrains.gridy = 0;
		constrains.fill = GridBagConstraints.BOTH;
		advancedPanel.add(advscrollPane, constrains);

		constrains.gridx = 0;
		constrains.gridy = 1;
		advancedPanel.add(advancedSettingsBackButton, constrains);

		// Panel for statistics and showing results
		statisticPanel = new JTabbedPane();

		TitledBorder restitle = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2), "Statistics");
		restitle.setTitlePosition(TitledBorder.CENTER);
		restitle.setTitleJustification(TitledBorder.CENTER);
		statisticPanel.setBorder(restitle);

		// Panel for simulation control------------------------------
		simcontrolPanel = new JPanel();
		TitledBorder simcontitle = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2), "Control");
		simcontitle.setTitlePosition(TitledBorder.CENTER);
		simcontitle.setTitleJustification(TitledBorder.CENTER);
		simcontrolPanel.setBorder(simcontitle);

		simcontrolPanel.setPreferredSize(new Dimension(250, 70));
		simcontrolPanel.setMinimumSize(new Dimension(250, 70));
		simcontrolPanel.setMaximumSize(new Dimension(250, 70));

		startButton = new JButton();
		startButton.setToolTipText("Start simulation");
		startButton.setEnabled(false);
		startButton.addActionListener(this);

		stopButton = new JButton();
		stopButton.setToolTipText("Pause simulation");
		stopButton.setEnabled(false);
		stopButton.addActionListener(this);

		resetButton = new JButton();
		resetButton.setToolTipText("Reset simulation");
		resetButton.setEnabled(false);
		resetButton.addActionListener(this);

		try {
			startButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/" + "play24.png")));
			/*
			 * stopIcon = new ImageIcon(new URL("jar:file:" +
			 * FrevoMain.getInstallDirectory() + "/Libraries/resources.jar/!" +
			 * "/" + "stop24.png"));
			 */
			stopButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/media/Pause24.gif")));
			resetButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/resources.jar/!" + "/" + "reset24.png")));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		simcontrolPanel.add(startButton);
		simcontrolPanel.add(stopButton);
		simcontrolPanel.add(resetButton);

		// Panel for last generation control------------------------------
		lastGenerationPanel = new JPanel();
		TitledBorder lastGenTitle = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2),
				"Last generation");
		lastGenTitle.setTitlePosition(TitledBorder.CENTER);
		lastGenTitle.setTitleJustification(TitledBorder.CENTER);
		lastGenerationPanel.setBorder(lastGenTitle);

		lastGenerationPanel.setPreferredSize(new Dimension(250, 60));
		lastGenerationPanel.setMinimumSize(new Dimension(250, 60));
		lastGenerationPanel.setMaximumSize(new Dimension(250, 60));

		saveCurrentButton = new JButton("Save");
		saveCurrentButton.setToolTipText("Save last generation");
		saveCurrentButton.setEnabled(false);
		saveCurrentButton.addActionListener(this);

		replayCurrentButton = new JButton("Replay");
		replayCurrentButton.setToolTipText("Replay last generation");
		replayCurrentButton.setEnabled(false);
		replayCurrentButton.addActionListener(this);

		lastGenerationPanel.add(saveCurrentButton);
		lastGenerationPanel.add(replayCurrentButton);

		// control panel for evaluation ---------------------
		evalControlPanel = new JPanel();
		evalControlPanel.setMaximumSize(new Dimension(1000, 30));

		startEvalButton = new JButton("Run evaluation");
		// startEvalButton.setToolTipText("Run evaluation");
		startEvalButton.setEnabled(true);
		startEvalButton.addActionListener(this);

		evalControlPanel.add(startEvalButton);

		// batch evaluation button
		batchEvalButton = new JButton("Batch evaluation...");
		batchEvalButton.addActionListener(this);
		evalControlPanel.add(batchEvalButton);

		// panel for showing results
		resultsPanel = new JPanel();
		resultsPanel.setMinimumSize(new Dimension(455, 200));
		populationsTabbedPane = new JTabbedPane();
		replayButton = new JButton("Replay");
		replayButton.addActionListener(this);
		detailsButton = new JButton("Details");
		detailsButton.addActionListener(this);
		closeresButton = new JButton("Close");
		closeresButton.addActionListener(this);
		continueButton = new JButton("Continue");
		continueButton.addActionListener(this);
		continueButton.setEnabled(false);
		continueButton
				.setToolTipText("It's impossible to continue the experiment which has been finished.");
		if (showTournamentButton){
			// tournament mode allows to run competition between candidates
			tournamentButton = new JButton("Tournament");
			tournamentButton.addActionListener(this);
		}		
		seedresLabel = new JLabel("Seed:");
		seedresTextField = new JIntegerTextField();
		seedresTextField.setPreferredSize(new Dimension(65, 24));
		seedresTextField.setText(Long.toString(FrevoMain.getInitialSeed()));
		seedresTextField.getDocument().addDocumentListener(this);

		JPanel resbtnPanel = new JPanel();// panel for buttons
		resbtnPanel.setMaximumSize(new Dimension(1000, 30));
		resbtnPanel.add(replayButton);
		resbtnPanel.add(detailsButton);
		resbtnPanel.add(seedresLabel);
		resbtnPanel.add(seedresTextField);
		resbtnPanel.add(closeresButton);
		resbtnPanel.add(continueButton);
		if (showTournamentButton){
			// tournament mode allows to run competition between candidates
			resbtnPanel.add(tournamentButton);
		}

		GroupLayout resbtnLayout = new GroupLayout(resultsPanel);
		resultsPanel.setLayout(resbtnLayout);

		resbtnLayout.setHorizontalGroup(resbtnLayout.createParallelGroup()
				.addComponent(populationsTabbedPane).addComponent(resbtnPanel));
		resbtnLayout.setVerticalGroup(resbtnLayout.createSequentialGroup()
				.addComponent(populationsTabbedPane).addComponent(resbtnPanel));

		// panel for console
		JPanel consolePanel = new JPanel();

		TitledBorder consoletitle = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2), "Console");
		consoletitle.setTitlePosition(TitledBorder.CENTER);
		consoletitle.setTitleJustification(TitledBorder.CENTER);
		consolePanel.setBorder(consoletitle);

		consoleTextPane = new JTextPane();
		consoleTextPane.setEditable(false);

		consoleScrollPane = new JScrollPane(consoleTextPane);

		// panel showing details
		evaluationPanel = new JPanel();
		evaluationPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2),
				"Parameters for evaluation"));
		evaluationPanel.setMinimumSize(leftDim);

		resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				resultsPanel, evaluationPanel);

		// styles for different colors
		consoleDoc = (StyledDocument) consoleTextPane.getDocument();
		OutStyle = consoleDoc.addStyle("OutStyle", null);
		StyleConstants.setForeground(OutStyle, ((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getControlTextColor());
		ErrStyle = consoleDoc.addStyle("ErrStyle", null);
		StyleConstants.setForeground(ErrStyle, Color.RED);

		JPanel consolecontrolPanel = new JPanel();
		Dimension consoleControlDim = new Dimension(30, 80);
		consolecontrolPanel.setMinimumSize(consoleControlDim);
		consolecontrolPanel.setMaximumSize(consoleControlDim);

		Dimension buttondim = new Dimension(25, 25);

		consolesaveButton = new JButton();
		consolesaveButton.setPreferredSize(buttondim);
		consolesaveButton.setToolTipText("Save console content to file");
		consolesaveButton.addActionListener(this);

		consoleclearButton = new JButton();
		consoleclearButton.setPreferredSize(buttondim);
		consoleclearButton.setToolTipText("Erase console");
		consoleclearButton.addActionListener(this);

		try {
			consolesaveButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/general/Save24.gif")));
			consoleclearButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/general/Delete24.gif")));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		consolecontrolPanel.add(consolesaveButton);
		consolecontrolPanel.add(consoleclearButton);

		GroupLayout consoleLayout = new GroupLayout(consolePanel);
		consolePanel.setLayout(consoleLayout);

		consoleLayout.setHorizontalGroup(consoleLayout.createSequentialGroup()
				.addComponent(consoleScrollPane)
				.addComponent(consolecontrolPanel));

		consoleLayout.setVerticalGroup(consoleLayout.createParallelGroup()
				.addComponent(consoleScrollPane)
				.addComponent(consolecontrolPanel));

		if (REDIRECTCONSOLE) {
			// redirect console
			try {
				PipedOutputStream pout = new PipedOutputStream(this.pin);
				System.setOut(new PrintStream(pout, true));
			} catch (java.io.IOException io) {
				try {
					consoleDoc.insertString(
							consoleDoc.getLength(),
							"Couldn't redirect STDOUT to console\n"
									+ io.getMessage(), ErrStyle);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} catch (SecurityException se) {
				try {
					consoleDoc.insertString(
							consoleDoc.getLength(),
							"Couldn't redirect STDOUT to console\n"
									+ se.getMessage(), ErrStyle);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			try {
				PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
				System.setErr(new PrintStream(pout2, true));
			} catch (java.io.IOException io) {
				try {
					consoleDoc.insertString(
							consoleDoc.getLength(),
							"Couldn't redirect STDERR to console\n"
									+ io.getMessage(), ErrStyle);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} catch (SecurityException se) {
				try {
					consoleDoc.insertString(
							consoleDoc.getLength(),
							"Couldn't redirect STDERR to console\n"
									+ se.getMessage(), ErrStyle);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

			final Runnable threadRunnable = new Runnable() {
				public synchronized void run() {
					try {
						while (Thread.currentThread() == stdOutReader) {
							try {
								this.wait(100);
							} catch (InterruptedException ie) {
							}
							if (pin.available() != 0) {
								String input = readLine(pin);
								consoleDoc.insertString(consoleDoc.getLength(),
										input, OutStyle);
								scrollConsolePaneToBottom();
							}
						}

						while (Thread.currentThread() == stdErrReader) {
							try {
								this.wait(100);
							} catch (InterruptedException ie) {
							}
							if (pin2.available() != 0) {
								String input = readLine(pin2);
								consoleDoc.insertString(consoleDoc.getLength(),
										input, ErrStyle);
								scrollConsolePaneToBottom();
							}
						}

					} catch (Exception e) {
						try {
							consoleDoc
									.insertString(consoleDoc.getLength(),
											"\nConsole reports an Internal error: "
													+ e, ErrStyle);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
				}
			};
			stdOutReader = new Thread(threadRunnable);
			stdOutReader.setDaemon(true);
			stdOutReader.start();
			stdErrReader = new Thread(threadRunnable);
			stdErrReader.setDaemon(true);
			stdErrReader.start();
		}

		// Progressbar
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(920, 35));
		progressBar.setMinimumSize(new Dimension(50, 35));
		progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		topPanel = new JPanel();
		topLayout = new GroupLayout(topPanel);
		setNormalTopLayout();

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel,
				consolePanel);

		// ******LAYOUT ARRANGEMENT******
		// create batch layout
		batchEvalLayout = new GroupLayout(topPanel);

		batchQueuePanel = new JPanel();
		batchQueuePanel.setMinimumSize(new Dimension(450, 266));

		// create empty table model
		batchQueueTableModel = new BatchQueueTableModel();

		batchQueueTable = new JBatchQueueTable(batchQueueTableModel);
		JScrollPane batchQueueTablePane = new JScrollPane(batchQueueTable);

		batchQueueTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					private int lastSelectedRow = -1;

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (lastSelectedRow != batchQueueTable.getSelectedRow()) {
							lastSelectedRow = batchQueueTable.getSelectedRow();
							if ((lastSelectedRow >= 0)
									&& (lastSelectedRow < batchQueueTable
											.getRowCount())) {
								// load selected row's properties
								BatchQueueElement selectedBatchElement = (BatchQueueElement) batchQueueTableModel
										.getValueAt(lastSelectedRow, 0);
								Hashtable<String, XMLFieldEntry> properties = selectedBatchElement
										.getProperties();

								String headers[] = { "Keys", "Values" };

								selectedEvalElementPropertiesTableModel = new PropertiesTableModel(
										headers, properties.size());
								selectedEvalElementPropertiesTableModel
										.addTableModelListener(new TableModelListener() {

											@Override
											public void tableChanged(
													TableModelEvent e) {
												// Save table content to
												// selected object
												for (int i = 0; i < selectedEvalElementPropertiesTableModel
														.getRowCount(); i++) {
													String key = (String) selectedEvalElementPropertiesTableModel
															.getValueAt(i, 0);
													String value = (String) selectedEvalElementPropertiesTableModel
															.getValueAt(i, 1);

													XMLFieldEntry entr = selectedEvalElementPropertiesTable
															.getProperties()
															.get(key);
													XMLFieldType type = entr
															.getType();

													if (FrevoMain.checkType(
															type, value))
														entr.setValue(value);
												}
											}
										});

								selectedEvalElementPropertiesTable = new JPropertiesTable(
										selectedEvalElementPropertiesTableModel,
										properties);
								selectedEvalElementPropertiesTable
										.setSurrendersFocusOnKeystroke(true);

								// Load
								Vector<String> v = new Vector<String>(
										properties.keySet());
								Collections.sort(v);

								Iterator<String> it = v.iterator();
								int i = 0;
								while (it.hasNext()) {
									String element = it.next();
									XMLFieldEntry entry = properties
											.get(element);

									selectedEvalElementPropertiesTableModel
											.setValueAt(element, i, 0);
									selectedEvalElementPropertiesTableModel
											.setValueAt(entry.getValue(), i, 1);
									i++;
								}
								selectedEvalElementPropertiesTableModel.isLoaded = true;
								selectedEvalElementPropertiesTablePane
										.setViewportView(selectedEvalElementPropertiesTable);
							}
						}

					}
				});

		JPanel batchQueueSettingsPanel = new JPanel();
		batchQueueSettingsPanel.setBorder(BorderFactory.createEtchedBorder());
		batchQueueSettingsPanel.setMinimumSize(new Dimension(300, 40));
		batchQueueSettingsPanel.setMaximumSize(new Dimension(1000, 40));

		addToQueueButton = new JButton("Add new run configuration");
		addToQueueButton.addActionListener(this);
		eraseQueueButton = new JButton("Remove all");
		eraseQueueButton.addActionListener(this);

		// add icon
		try {
			addToQueueButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/general/Add16.gif")));

			eraseQueueButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/general/Remove16.gif")));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		batchQueueSettingsPanel.add(addToQueueButton);
		batchQueueSettingsPanel.add(eraseQueueButton);

		GroupLayout batchQueueLayout = new GroupLayout(batchQueuePanel);
		batchQueuePanel.setLayout(batchQueueLayout);

		batchQueueLayout.setHorizontalGroup(batchQueueLayout
				.createParallelGroup().addComponent(batchQueueTablePane)
				.addComponent(batchQueueSettingsPanel));

		batchQueueLayout.setVerticalGroup(batchQueueLayout
				.createSequentialGroup().addComponent(batchQueueTablePane)
				.addComponent(batchQueueSettingsPanel));

		JPanel batchEvalParameterPanel = new JPanel();
		batchEvalParameterPanel.setMinimumSize(new Dimension(200, 266));
		batchEvalParameterPanel.setLayout(new BoxLayout(
				batchEvalParameterPanel, BoxLayout.Y_AXIS));

		JPanel representationNamePanel = new JPanel();
		TitledBorder evaluatedRepresentationsBorder = BorderFactory
				.createTitledBorder("Representation(s)");
		representationNamePanel.setBorder(evaluatedRepresentationsBorder);
		representationNamePanel.setLayout(new GridLayout(1, 1));

		representationsList = new JList();
		JScrollPane representationsListPane = new JScrollPane(
				representationsList);

		representationNamePanel.add(representationsListPane);
		representationNamePanel.setPreferredSize(new Dimension(
				representationNamePanel.getPreferredSize().width, 80));

		batchEvalParameterPanel.add(representationNamePanel);

		selectedEvalElementPropertiesTablePane = new JScrollPane();
		selectedEvalElementPropertiesTablePane.setMinimumSize(new Dimension(
				400, 100));
		selectedEvalElementPropertiesTablePane.setPreferredSize(new Dimension(
				1000, 1000));
		selectedEvalElementPropertiesTablePane.setMaximumSize(new Dimension(
				1000, 1000));

		batchEvalParameterPanel.add(selectedEvalElementPropertiesTablePane);

		JPanel batchEvalControlPanel = new JPanel();
		Border batchEvalControlPanelBorder = BorderFactory.createEtchedBorder();
		batchEvalControlPanel.setBorder(batchEvalControlPanelBorder);
		batchEvalControlPanel.setMinimumSize(new Dimension(100, 40));
		batchEvalControlPanel.setPreferredSize(new Dimension(100, 40));
		batchEvalControlPanel.setMaximumSize(new Dimension(1000, 40));

		startBatchEvaluationButton = new JButton("Start batch evaluation");
		startBatchEvaluationButton.addActionListener(this);

		backFromEvaluationButton = new JButton("Back");
		backFromEvaluationButton.addActionListener(this);

		try {
			startBatchEvaluationButton.setIcon(new ImageIcon(new URL(
					"jar:file:" + FrevoMain.getInstallDirectory()
							+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
							+ "toolbarButtonGraphics/media/Play16.gif")));

			backFromEvaluationButton.setIcon(new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/navigation/Back16.gif")));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		batchEvalControlPanel.add(startBatchEvaluationButton);
		batchEvalControlPanel.add(backFromEvaluationButton);

		batchEvalParameterPanel.add(batchEvalControlPanel);

		batchEvalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				batchQueuePanel, batchEvalParameterPanel);

		// create loading layout
		loadingPanel = new FrevoLoadingPanel(loadingIcon);
		loadLayout = new GroupLayout(mainContainer);

		// Create main group layout
		mainlayout = new GroupLayout(mainContainer);

		setMainLayout();

		this.addWindowListener(this);

		System.out.println("Welcome to FREVO " + "v"
				+ FrevoMain.getMajorVersion() + " Rev:"
				+ FrevoMain.getMinorVersion());
	}
	
	/** Reads an Image and brightens it slightly if in dark mode
	 * 	Returns the nothingIcon, if an image is not found
	 */
	public static ImageIcon getAdaptedImage(URL location)
	{
		try {
			BufferedImage img = ImageIO.read(location);
			if(FrevoMain.launchInDarkMode)
				new RescaleOp(DARK_MODE_BRIGHTNESS,0,null).filter(img, img);
			return new ImageIcon(img);
		} catch (IOException e) {
			return FrevoWindow.nothingIcon;
		}
	}
	
	/** Reads an Image and brightens it slightly if in dark mode
	 * 	Returns the nothingIcon, if an image is not found
	 */
	public static ImageIcon getAdaptedImage(String location)
	{
		try {
			BufferedImage img = ImageIO.read(new File(location));
			if(FrevoMain.launchInDarkMode)
				new RescaleOp(DARK_MODE_BRIGHTNESS,0,null).filter(img, img);
			return new ImageIcon(img);
		} catch (IOException e) {
			return FrevoWindow.nothingIcon;
		}	
	}

	/** Constructs the load menu with the recently loaded menu items */
	private void buildLoadMenu() {
		// remove all previous components
		loadMenu.removeAll();

		loadMenu.add(loadResultsItem);
		loadMenu.add(loadSessionItem);

		// add latest results
		if (recentResults.size() > 0) {
			loadMenu.addSeparator();

			Iterator<File> it = recentResults.descendingIterator();
			while (it.hasNext()) {
				final File f = it.next();
				JMenuItem item = new JMenuItem(f.getName());
				item.setToolTipText(f.getAbsolutePath());

				// add item listener
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						loadFile(f);
					}
				});
				loadMenu.add(item);
			}
		}

		// add latest sessions
		if (recentSessions.size() > 0) {
			loadMenu.addSeparator();

			Iterator<File> it = recentSessions.descendingIterator();
			while (it.hasNext()) {
				final File f = it.next();
				JMenuItem item = new JMenuItem(f.getName());
				item.setToolTipText(f.getAbsolutePath());

				// add item listener
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						loadFile(f);
					}
				});
				loadMenu.add(item);
			}
		}

	}

	private void setMainLayout() {
		mainContainer.removeAll();
		mainContainer.setLayout(mainlayout);

		mainlayout.setHorizontalGroup(mainlayout.createParallelGroup()
				.addComponent(splitPane).addComponent(progressBar));

		mainlayout.setVerticalGroup(mainlayout.createSequentialGroup()
				.addComponent(splitPane).addComponent(progressBar));
	}

	/** Sets the main screen to indicate loading */
	private void setLoadingLayout() {
		mainContainer.removeAll();
		mainContainer.setLayout(loadLayout);

		loadLayout.setHorizontalGroup(loadLayout.createParallelGroup()
				.addComponent(loadingPanel));

		loadLayout.setVerticalGroup(loadLayout.createSequentialGroup()
				.addComponent(loadingPanel));
	}

	private void setBatchEvalLayout() {
		// reset data
		batchQueueTableModel.removeAllElements();

		topPanel.removeAll();
		topPanel.setLayout(batchEvalLayout);

		batchEvalLayout.setHorizontalGroup(batchEvalLayout
				.createParallelGroup().addComponent(batchEvalSplitPane));

		batchEvalLayout.setVerticalGroup(batchEvalLayout
				.createSequentialGroup().addComponent(batchEvalSplitPane));
	}

	private void setNormalTopLayout() {
		topPanel.removeAll();
		topPanel.setLayout(topLayout);

		topLayout.setHorizontalGroup(topLayout
				.createSequentialGroup()
				.addGroup(
						topLayout.createParallelGroup()
								.addComponent(configPanel)
								.addComponent(simcontrolPanel)
								.addComponent(lastGenerationPanel))
				.addComponent(statisticPanel));

		topLayout.setVerticalGroup(topLayout
				.createParallelGroup()
				.addGroup(
						topLayout.createSequentialGroup()
								.addComponent(configPanel)
								.addComponent(simcontrolPanel)
								.addComponent(lastGenerationPanel))
				.addComponent(statisticPanel));
	}

	private void setResultsTopLayout() {
		topPanel.removeAll();
		GroupLayout topLayout = new GroupLayout(topPanel);

		topLayout.setHorizontalGroup(topLayout.createSequentialGroup()
				.addComponent(resultsSplitPane));
		topLayout.setVerticalGroup(topLayout.createParallelGroup()
				.addComponent(resultsSplitPane));
		topPanel.setLayout(topLayout);
	}

	void addPropertyToQueue(String propertyKey) {
		// adds the selected property to the current queue
		// deep clone problem properties
		Hashtable<String, XMLFieldEntry> sourceProperties = FrevoMain
				.getSelectedComponent(ComponentType.FREVO_PROBLEM)
				.getProperties();

		Hashtable<String, XMLFieldEntry> properties = new Hashtable<String, XMLFieldEntry>();

		Iterator<String> keyit = sourceProperties.keySet().iterator();

		XMLFieldEntry ownFieldEntryCopy = null;
		while (keyit.hasNext()) {
			String key = keyit.next();
			// copy all keys and values except batched key
			XMLFieldEntry sourceField = sourceProperties.get(key);

			XMLFieldEntry newField = sourceField.clone();

			if (key.equals(propertyKey)) {
				ownFieldEntryCopy = newField;
			} else {
				properties.put(key, newField);
			}
		}

		BatchQueueElement element = new BatchQueueElement(propertyKey,
				ownFieldEntryCopy, properties);
		batchQueueTableModel.addNewElement(element);
	}

	/**
	 * Updates the label of the preferences button
	 */
	public void updateAdvancedLabels() {
		String text = "";
		if (FrevoMain.getNumberOfSimulationRuns() > 1)
			text = FrevoMain.getNumberOfSimulationRuns() + " runs of <i>"
					+ FrevoMain.getCustomName()
					+ "</i><br> with starting seed of " + FrevoMain.getInitialSeed();
		else
			text = FrevoMain.getNumberOfSimulationRuns() + " run of <i>"
					+ FrevoMain.getCustomName()
					+ "</i><br> with starting seed of " + FrevoMain.getInitialSeed();
		advancedLabel.setText("<font color=\"#" + Integer.toHexString(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getControlTextColor().getRGB()).substring(2)+ "\"face=\"ARIAL\" size=\"3\">" + text
				+ "</font>");
		// update custom name editor box
		if (simnameTextField != null)
			simnameTextField.setText(FrevoMain.getCustomName());

		if (seedTextField != null)
			seedTextField.setText(Long.toString(FrevoMain.getInitialSeed()));

		if (numberofrunsTextField != null)
			numberofrunsTextField.setText(Integer.toString(FrevoMain
					.getNumberOfSimulationRuns()));
	}

	private class LoadingWorkerThread extends SwingWorker<Void, Float> {

		private File loadFile;

		private LoadingWorkerThread(File fileToLoad) {
			this.loadFile = fileToLoad;
		}

		@Override
		protected Void doInBackground() throws Exception {
			publish(0f);
			try {
				// Load session part
				Document doc = FrevoMain.loadSession(loadFile);

				if (FrevoMain.getExtension(loadFile).equals(
						FrevoMain.FREVO_RESULT_EXTENSION)) {
					// Load populations -> Call method's own loader
					try {
						// Instantiate components
						ComponentXMLData method = FrevoMain
								.getSelectedComponent(ComponentType.FREVO_METHOD);

						AbstractMethod m = method
								.getNewMethodInstance(new NESRandom(FrevoMain
										.getSeed()));

						populations = m.loadFromXML(doc);
						// sort all representations before visualizing
						for (ArrayList<AbstractRepresentation> representations : populations) {
							Collections.sort(representations,
									new RepresentationComparator());
						}
						refreshResultList();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				setMainLayout();
			}

			return null;
		}

		@Override
		public void done() {
			if (FrevoMain.getExtension(loadFile).equals(
					FrevoMain.FREVO_RESULT_EXTENSION)) {
				// adjust GUI
				resultsPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(Color.BLACK, 2),
						"Results of " + loadFile.getName()));

				evaluationPanel.removeAll();
				ComponentXMLData problemdata = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_PROBLEM);
				Hashtable<String, XMLFieldEntry> properties = problemdata
						.getProperties();
				String headers[] = { "Property", "Type", "Value" };
				PropertiesTableModel tm = new PropertiesTableModel(headers,
						properties.size());
				tm.filtered = true;
				tm.isLoaded = true;
				tm.addTableModelListener(FrevoWindow.this);
				evalTable = new JPropertiesTable(tm, properties);
				evalTable.setSurrendersFocusOnKeystroke(true);
				evalTable.addMouseListener(FrevoWindow.this);
				Vector<String> v = new Vector<String>(properties.keySet());
				Collections.sort(v);
				Iterator<String> it = v.iterator();
				int i = 0;
				while (it.hasNext()) {
					String element = it.next();
					XMLFieldEntry e = properties.get(element);
					tm.setValueAt(element, i, 0);
					tm.setValueAt(e.getType(), i, 1);
					tm.setValueAt(e.getValue(), i, 2);
					i++;
				}
				evalScrollPane = new JScrollPane(evalTable);
				GroupLayout evalLayout = new GroupLayout(evaluationPanel);
				evaluationPanel.setLayout(evalLayout);
				evalLayout.setHorizontalGroup(evalLayout.createParallelGroup()
						.addComponent(evalScrollPane)
						.addComponent(evalControlPanel));
				evalLayout.setVerticalGroup(evalLayout.createSequentialGroup()
						.addComponent(evalScrollPane)
						.addComponent(evalControlPanel));

				setResultsTopLayout();
			}

			// update main GUI
			simnameTextField.setText(FrevoMain.getCustomName());
			seedTextField.setText(Long.toString(FrevoMain.getInitialSeed()));
			numberofrunsTextField.setText(Integer.toString(FrevoMain
					.getNumberOfSimulationRuns()));

			updateAdvancedLabels();

			if (pBrowser == null) {
				pBrowser = new ComponentBrowser(ComponentType.FREVO_PROBLEM);
			}

			if (mBrowser == null) {
				mBrowser = new ComponentBrowser(ComponentType.FREVO_METHOD);
			}

			if (reBrowser == null) {
				reBrowser = new ComponentBrowser(
						ComponentType.FREVO_REPRESENTATION);
			}

			if (raBrowser == null) {
				raBrowser = new ComponentBrowser(ComponentType.FREVO_RANKING);
			}
			if(!FrevoMain.isRunning && !sessionIsPaused)
				problemButton.setEnabled(true);
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM) != null) {	//The problem can only be null if the loading failed
				problemLabel.setText(FrevoMain.getSelectedComponent(
						ComponentType.FREVO_PROBLEM).getName());
				pBrowser.setSelected(FrevoMain
						.getSelectedComponent(ComponentType.FREVO_PROBLEM));
				if(!FrevoMain.isRunning && !sessionIsPaused)
					methodButton.setEnabled(true);
			} else {
				problemLabel.setText("Select Problem");
				methodButton.setEnabled(false);
			}
			
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_METHOD) != null) {
				methodLabel.setText(FrevoMain.getSelectedComponent(
						ComponentType.FREVO_METHOD).getName());
				mBrowser.setSelected(FrevoMain
						.getSelectedComponent(ComponentType.FREVO_METHOD));
				if(!FrevoMain.isRunning && !sessionIsPaused)
					representationButton.setEnabled(true);
			} else {
				methodLabel.setText("Select Method");
				representationButton.setEnabled(false);
			}
			
			if (FrevoMain
					.getSelectedComponent(ComponentType.FREVO_REPRESENTATION) != null) {
				representationLabel.setText(FrevoMain.getSelectedComponent(
						ComponentType.FREVO_REPRESENTATION).getName());
				reBrowser.setSelected(FrevoMain
						.getSelectedComponent(ComponentType.FREVO_REPRESENTATION));
				if(!FrevoMain.isRunning && !sessionIsPaused)
					rankingButton.setEnabled(true);
			} else {
				representationLabel.setText("Select Representation");
				rankingButton.setEnabled(false);
			}

			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_RANKING) != null) {
				rankingLabel.setText(FrevoMain.getSelectedComponent(
						ComponentType.FREVO_RANKING).getName());
				raBrowser.setSelected(FrevoMain
						.getSelectedComponent(ComponentType.FREVO_RANKING));
			} else
				rankingLabel.setText("Select Ranking");
			
			if (FrevoMain.checkState()) {
				if(!FrevoMain.isRunning)
				{
					FrevoMain.isLaunchable = true;
					startButton.setEnabled(true);
				}
			} else {
				FrevoMain.isLaunchable = false;
				startButton.setEnabled(false);
			}


			setMainLayout();
		}

		protected void process(List<Float> results) {
			loadingPanel.setProgress(results.get(results.size() - 1));
		}

	}

	public void startSim(Document doc) {

		List<JChart2DComponent> statChartComponents = new ArrayList<JChart2DComponent>();

		// Start a new worker thread
		simulationWorkerThread = new SimulationWorkerThread(
				statChartComponents, FrevoMain.getSeed());
		simulationWorkerThread.setLoadedFile(doc);
		simulationWorkerThread.execute();
	}

	public class SimulationWorkerThread extends SwingWorker<Void, Float> {

		private AbstractMethod method;
		private List<JChart2DComponent> statisticChartComponents;
		private Document loadedFile;

		private SimulationWorkerThread(List<JChart2DComponent> statisticCharts,
				long seed) {
			try {
				method = FrevoMain.getSelectedComponent(
						ComponentType.FREVO_METHOD).getNewMethodInstance(
						new NESRandom(seed));
				// add chart component;
				this.statisticChartComponents = statisticCharts;
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Void doInBackground() throws Exception {
			try {
				FrevoMain.isRunning = true;
				// adjust GUI to simulation mode
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				resetButton.setEnabled(false);

				problemButton.setEnabled(false);
				methodButton.setEnabled(false);
				representationButton.setEnabled(false);
				rankingButton.setEnabled(false);

				// reset statistics to display
				FrevoMain.getStatisticsToDisplay().clear();
				
				if (loadedFile == null) {
					// run method
					method.runOptimization(
							(ProblemXMLData) FrevoMain
									.getSelectedComponent(ComponentType.FREVO_PROBLEM),
							FrevoMain
									.getSelectedComponent(ComponentType.FREVO_REPRESENTATION),
							FrevoMain
									.getSelectedComponent(ComponentType.FREVO_RANKING),
							method.getProperties());
				} else {
					method.continueOptimization(
							(ProblemXMLData) FrevoMain
									.getSelectedComponent(ComponentType.FREVO_PROBLEM),
							FrevoMain
									.getSelectedComponent(ComponentType.FREVO_REPRESENTATION),
							FrevoMain
									.getSelectedComponent(ComponentType.FREVO_RANKING),
							method.getProperties(), loadedFile);
				}
			}			
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void done() {
			// code to ensure that exceptions are handled and not swallowed
			try {
				if (!isCancelled())
					get();
			} catch (final InterruptedException ex) {
				throw new RuntimeException(ex);
			} catch (final ExecutionException ex) {
				throw new RuntimeException(ex.getCause());
			} catch (final Exception ex) {
				System.err.println(ex.getCause());
				return;
			}

			// When finished with calculations
			FrevoMain.isRunning = false;

			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			resetButton.setEnabled(true);

			problemButton.setEnabled(true);
			methodButton.setEnabled(true);
			representationButton.setEnabled(true);
			rankingButton.setEnabled(true);

			if (this.isCancelled()) {
				System.out.println("Simulation has been reset");
				return;
			}
			// step simulation run index
			FrevoMain.setCurrentRun(FrevoMain.getCurrentRun() + 1);

			// schedule next run if needed
			if (FrevoMain.getCurrentRun() < FrevoMain
					.getNumberOfSimulationRuns()) {
				System.out.println("Proceeding to the next run...");
				
				simulationWorkerThread = new SimulationWorkerThread(
						statisticChartComponents, FrevoMain.getSeed());
				simulationWorkerThread.execute();
			} else {
				setApplicationIconProgress(-1);
				// finish, show statistics
				StatKeeper.showValuation();
				System.out.println("Simulation finished in "
						+ (System.currentTimeMillis() - startTime) + "ms.");

				// TODO write statistics if needed
				// dump statistics
				FrevoMain.writeStatisticsToDisk();
			}

		}

		public AbstractMethod getMethod() {
			return method;
		}

		public void setProgressToPublish(float p) {
			publish(p);
		}

		public Document getLoadedFile() {
			return loadedFile;
		}

		public void setLoadedFile(Document loadedFile) {
			this.loadedFile = loadedFile;
		}

		protected void process(List<Float> results) {
			// obtain last progress information
			Float res = results.get(results.size() - 1);

			// adjust progress bar accordingly
			if (progressBar != null)
				progressBar.setValue((int) (res * 100));

			setApplicationIconProgress(res);

			// create new chart components if needed
			for (int i = 0; i < FrevoMain.getStatisticsToDisplay().size(); i++) {
				try {
					if (statisticChartComponents.size() <= i) {
						// chart does not exist yet, create it
						JChart2DComponent chart = new JChart2DComponent(
								FrevoMain.getNumberOfSimulationRuns());
						// add current statistics
						chart.addstatkeeper(FrevoMain.getStatisticsToDisplay()
								.get(i));

						// add to list
						statisticChartComponents.add(chart);

						// add to display
						statisticPanel.addTab(FrevoMain.getCustomName(), chart);
						// set up custom tab
						int index = statisticPanel.getTabCount() - 1;
						statisticPanel.setTabComponentAt(index,
								new ButtonTabComponent(statisticPanel,
										FrevoMain.getCustomName()));
					} else {

						JChart2DComponent chart = statisticChartComponents
								.get(i);
						// check if chart needs the current trace or not
						if (chart.getNumberOfAddedTraces() <= FrevoMain
								.getCurrentRun()) {
							chart.addstatkeeper(FrevoMain
									.getStatisticsToDisplay().get(i));
						}
						// update
						chart.updateChart();
					}
				} catch (Exception e) {
					System.err
							.println("Error has been occurred while updating statistic: "
									+ e.getMessage());
				}
			}
		}

		/**
		 * Indicates progress on the application icon. Currently OSX only.
		 * 
		 * @param progress
		 *            The progress value between 0 and 1. Pass -1 to make the
		 *            stat bars disappear.
		 */
		private void setApplicationIconProgress(float progress) {

			// MAC version
			if (application != null) {

				BufferedImage newIcon = new BufferedImage(macIcon.getWidth(),
						macIcon.getHeight(), BufferedImage.TYPE_INT_ARGB);

				Graphics2D graphics = (Graphics2D) newIcon.getGraphics();

				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				// draw original icon (512x512)
				graphics.drawImage(macIcon, 0, 0, null);

				if (progress != -1) {
					// draw progress background
					graphics.setColor(Color.decode("#494949"));
					graphics.fillRoundRect(40, 400, 432, 84, 6, 6);

					// draw progress foreground
					graphics.setColor(Color.decode("#1dee2c"));// offset = 6
					graphics.fillRoundRect(46, 403, (int) (420.0 * progress),
							72, 6, 6);

					// draw current run runs are more than 1
					if (FrevoMain.getNumberOfSimulationRuns() > 1) {
						// draw progress background
						graphics.setColor(Color.decode("#494949"));
						graphics.fillRoundRect(40, 30, 432, 84, 6, 6);

						// draw progress foreground
						float runp = (float) FrevoMain.getCurrentRun()
								/ (float) FrevoMain.getNumberOfSimulationRuns();
						graphics.setColor(Color.decode("#c61414"));// offset = 6
						graphics.fillRoundRect(46, 33, (int) (runp * 420.0), 72, 6, 6);
					}
				}

				graphics.dispose();

				application.setApplicationIconImage(newIcon);
			}
		}
	}

	public static synchronized String readLine(PipedInputStream in)
			throws IOException {
		String input = "";
		do {
			int available = in.available();
			if (available == 0)
				break;
			byte b[] = new byte[available];
			in.read(b);
			input = input + new String(b, 0, b.length);
		} while (!input.endsWith("\n") && !input.endsWith("\r\n") /* && !quit */);
		return input;
	}

	private void scrollConsolePaneToBottom() {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				consoleScrollPane.getVerticalScrollBar().setValue(
						consoleScrollPane.getVerticalScrollBar().getMaximum());
			}
		});
	}

	/**
	 * Resets any simulation progress and adjusts the GUI accordingly. Does not
	 * reset component configurations.
	 */
	private void reset() {
		FrevoMain.reset();
		// reset ongoing swing worker thread!
		FrevoMain.isRunning = false;
		if (sessionIsPaused) {
			System.out
					.println("Simulation has been canceled, cleaning up the cache...");
			simulationWorkerThread.cancel(true);
			sessionIsPaused = false;
		}
		sessionIsPaused = false;
		progressBar.setValue(0);
		methodStateChanged(null);
	}

	// Mouse events

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == problemButton && problemButton.isEnabled()) {
			if (pBrowser == null) {
				pBrowser = new ComponentBrowser(ComponentType.FREVO_PROBLEM);
			}
			FrevoWindow.this.setEnabled(false);
			pBrowser.setVisible(true);
		} else if ((e.getSource() == methodButton)
				&& (methodButton.isEnabled())) {
			if (mBrowser == null) {
				mBrowser = new ComponentBrowser(ComponentType.FREVO_METHOD);
			}
			FrevoWindow.this.setEnabled(false);
			mBrowser.setVisible(true);
		} else if ((e.getSource() == representationButton)
				&& (representationButton.isEnabled())) {
			if (reBrowser == null) {
				reBrowser = new ComponentBrowser(
						ComponentType.FREVO_REPRESENTATION);
			}
			FrevoWindow.this.setEnabled(false);
			reBrowser.setVisible(true);
		} else if ((e.getSource() == rankingButton)
				&& (rankingButton.isEnabled())) {
			if (raBrowser == null) {
				raBrowser = new ComponentBrowser(ComponentType.FREVO_RANKING);
			}
			FrevoWindow.this.setEnabled(false);
			raBrowser.setVisible(true);
		} else if ((e.getSource() == advancedButton)
				&& (advancedButton.isEnabled())) {
			// change topPanel's layout
			topPanel.removeAll();
			GroupLayout topLayout = new GroupLayout(topPanel);

			topLayout.setHorizontalGroup(topLayout
					.createSequentialGroup()
					.addGroup(
							topLayout.createParallelGroup()
									.addComponent(advancedPanel)
									.addComponent(simcontrolPanel)
									.addComponent(lastGenerationPanel))
					.addComponent(statisticPanel));

			topLayout.setVerticalGroup(topLayout
					.createParallelGroup()
					.addGroup(
							topLayout.createSequentialGroup()
									.addComponent(advancedPanel)
									.addComponent(simcontrolPanel)
									.addComponent(lastGenerationPanel))
					.addComponent(statisticPanel));

			topPanel.setLayout(topLayout);
			topPanel.validate();
		} else if (e.getSource() == evalTable) {
			int row = evalTable.rowAtPoint(e.getPoint());
			int column = evalTable.columnAtPoint(e.getPoint());
			if ((column == 2) && (evalTable.isCellEditable(row, column))) {
				PropertiesTableModel model = (PropertiesTableModel) evalTable
						.getModel();
				String key = (String) model.getValueAt(row, 0);
				XMLFieldEntry entr = evalTable.getProperties().get(key);
				XMLFieldType type = entr.getType();
				if (type == XMLFieldType.FILE) {
					// open file dialog
					String entry = (String) model.getValueAt(row, 2);
					File file = new File(entry);
					if (!file.exists())
						entry = FrevoMain.getInstallDirectory();
					JFileChooser fc = new JFileChooser(/*
														 * FrevoMain.
														 * getActiveDirectory()
														 */entry);
					int returnVal = fc.showDialog(FrevoWindow.this,
							"Select file");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						// change the table instead of the underlying data (??)
						evalTable.setValueAt(fc.getSelectedFile()
								.getAbsolutePath(), row, column);

						// reload
						model.fireTableCellUpdated(row, column);
					}

				}
			}

		} else if(jTables.contains(e.getSource()) && SwingUtilities.isRightMouseButton(e)) {
			JTable table = jTables.get(jTables.indexOf(e.getSource()));
			int row = table.rowAtPoint(e.getPoint());
			if(row >= 0 && row < table.getRowCount())
			{
				table.addRowSelectionInterval(row, row);
				representationMenu = new JPopupMenu();
				JMenuItem replayItem = new JMenuItem("Replay");
				replayItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {replayButton.doClick();}
				});
				JMenuItem detailsItem = new JMenuItem("Details");
				detailsItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {detailsButton.doClick();}
				});
				JMenuItem startEvalItem = new JMenuItem("Run evalutaion");
				startEvalItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {startEvalButton.doClick();}
				});	
				JMenuItem batchEvalItem = new JMenuItem("Batch evaluation");
				batchEvalItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {batchEvalButton.doClick();}
				});
				representationMenu.add(replayItem);
				representationMenu.add(detailsItem);
				representationMenu.add(startEvalItem);
				representationMenu.add(batchEvalItem);
				representationMenu.show(table, e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if ((e.getSource() == problemButton) && (problemButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(problem_hIcon);
		} else if ((e.getSource() == methodButton)
				&& (methodButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(method_hIcon);
		} else if ((e.getSource() == representationButton)
				&& (representationButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(representation_hIcon);
		} else if ((e.getSource() == rankingButton)
				&& (rankingButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(ranking_hIcon);
		} else if ((e.getSource() == advancedButton)
				&& (advancedButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(advanced_hIcon);
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if ((e.getSource() == problemButton) && (problemButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(problemIcon);
		} else if ((e.getSource() == methodButton)
				&& (methodButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(methodIcon);
		} else if ((e.getSource() == representationButton)
				&& (representationButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(representationIcon);
		} else if ((e.getSource() == rankingButton)
				&& (rankingButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(rankingIcon);
		} else if ((e.getSource() == advancedButton)
				&& (advancedButton.isEnabled())) {
			JLabel label = (JLabel) (e.getSource());
			label.setIcon(advancedIcon);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	// Actionlistener mostly for mouseclicks
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		// about menu item
		if (source == aboutItem) {
			// Display about window
			JOptionPane
					.showMessageDialog(
							FrevoWindow.this,
							"FREVO by Istvan Fehervari and Wilfried Elmenreich\nThis software is released under the GPL v3\nhttp://sourceforge.net/p/frevo",
							"About FREVO", JOptionPane.PLAIN_MESSAGE, frevoIcon);
		} // console save button
		else if (source == consolesaveButton) {
			// save console content
			JFileChooser fc = new JFileChooser(FrevoMain.getActiveDirectory());
			fc.setAcceptAllFileFilterUsed(false);
			fc.addChoosableFileFilter(new CustomFileFilter(new FileType("log",
					"FREVO log file (*.log)")));

			File saveFile = null;
			while (true) {
				int returnVal = fc.showDialog(FrevoWindow.this, "Save console");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File chosen = fc.getSelectedFile();
					if (!chosen.exists()) {
						// file does not exist yet, proceed with writing
						saveFile = chosen;
						saveConsoleToFile(saveFile);
						break;
					}
					int confirm = JOptionPane.showConfirmDialog(
							FrevoWindow.this,
							"Overwrite file? " + chosen.getName());
					if (confirm == JOptionPane.OK_OPTION) {
						saveFile = chosen;
						saveConsoleToFile(saveFile);
						break;
					} else if (confirm == JOptionPane.NO_OPTION) {
						// user clicked on NO
						continue;
					} else if (confirm == JOptionPane.CANCEL_OPTION) {
						// user canceled
						break;
					}

				} else
					break;
			}
		} else if (source == consoleclearButton) {
			// console clear button
			consoleTextPane.setText("");
		} else if (source == reloadComponentsItem) {
			int confirm = JOptionPane
					.showConfirmDialog(
							FrevoWindow.this,
							"Are you sure you want to reload all components? This will reset FREVO, all unsaved data will be lost.",
							"Are you sure?", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				// reload components
				FrevoMain.reLoadComponents(true);
			}
		} else if (source == importComponentItem) {
			int confirm = JOptionPane
					.showConfirmDialog(
							FrevoWindow.this,
							"Importing a component will reset all unsaved FREVO data. Are you sure you want to continue?",
							"Warning!", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				JFileChooser fc = new JFileChooser(
						FrevoMain.getActiveDirectory());
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new CustomFileFilter(new FileType(
						FrevoMain.FREVO_PACKAGE_EXTENSION,
						"FREVO component package (*."
								+ FrevoMain.FREVO_PACKAGE_EXTENSION + ")")));

				int returnVal = fc.showDialog(FrevoWindow.this,
						"Import FREVO component");

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					reset();
					System.out.println("Importing component...");
					FrevoMain.importComponent(fc.getSelectedFile());

					// reload components to reset GUI
					// FrevoMain.reLoadComponents();
					System.out
							.println("Component has been successfully imported! Please compile the sources if needed then restart FREVO.");
				}
			}
		} else if (source == exportComponentItem) {
			// show selector window
			if (exportSelector == null) {
				ArrayList<ComponentXMLData> allcomponents = new ArrayList<ComponentXMLData>();
				// add all single problems
				allcomponents.addAll(FrevoMain.getComponentList(
						ComponentType.FREVO_PROBLEM).values());
				// add all multi problems
				allcomponents.addAll(FrevoMain.getComponentList(
						ComponentType.FREVO_MULTIPROBLEM).values());
				// add all methods
				allcomponents.addAll(FrevoMain.getComponentList(
						ComponentType.FREVO_METHOD).values());
				// add all representations
				allcomponents.addAll(FrevoMain.getComponentList(
						ComponentType.FREVO_REPRESENTATION).values());
				// add all ranking
				allcomponents.addAll(FrevoMain.getComponentList(
						ComponentType.FREVO_RANKING).values());

				if (allcomponents.size() == 0)
					return;
				exportSelector = new ComponentSelector(allcomponents,
						"Select a component to export",
						ComponentSelector.COMPONENT_SELECTOR_EXPORT);
			}
			FrevoWindow.this.setEnabled(false);
			exportSelector.setVisible(true);
		} // remove component
		else if (source == eraseComponentItem) {
			// show warning
			int confirm = JOptionPane
					.showConfirmDialog(
							FrevoWindow.this,
							"Removing a component will reset all unsaved FREVO data. Are you sure you want to continue?",
							"Warning!", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				// show selector window
				if (deleteSelector == null) {
					ArrayList<ComponentXMLData> allcomponents = new ArrayList<ComponentXMLData>();
					// add all single problems
					allcomponents.addAll(FrevoMain.getComponentList(
							ComponentType.FREVO_PROBLEM).values());
					// add all multi problems
					allcomponents.addAll(FrevoMain.getComponentList(
							ComponentType.FREVO_MULTIPROBLEM).values());
					// add all methods
					allcomponents.addAll(FrevoMain.getComponentList(
							ComponentType.FREVO_METHOD).values());
					// add all representations
					allcomponents.addAll(FrevoMain.getComponentList(
							ComponentType.FREVO_REPRESENTATION).values());
					// add all ranking
					allcomponents.addAll(FrevoMain.getComponentList(
							ComponentType.FREVO_RANKING).values());

					if (allcomponents.size() == 0)
						return;
					deleteSelector = new ComponentSelector(allcomponents,
							"Select a component to be removed",
							ComponentSelector.COMPONENT_SELECTOR_DELETE);
				}
				FrevoWindow.this.setEnabled(false);
				deleteSelector.setVisible(true);
			}
		}
		else if (source == toggleDarkModeItem)
		{
			int confirm = JOptionPane
					.showConfirmDialog(
							FrevoWindow.this,
							"This will reset FREVO, all unsaved simulation data will be lost.",
							"Are you sure?", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				FrevoMain.launchInDarkMode = !FrevoMain.launchInDarkMode;
				FrevoMain.reLoadGraphics();
			}
		}
		// simulation start button
		else if (source == startButton) {
			if (FrevoMain.isLaunchable && !FrevoMain.isRunning) {
				stopButton.setEnabled(true);
				startButton.setEnabled(false);
				resetButton.setEnabled(false);

				// new simulation or continue
				if (sessionIsPaused) {
					simulationWorkerThread.getMethod().wakeUp();
					FrevoMain.isRunning = true;
					sessionIsPaused = false;
				} else {
					methodStateChanged(null);
					FrevoMain.isRunning = true;
					sessionIsPaused = false;

					FrevoMain.setNumberOfSimulationRuns(numberofrunsTextField
							.getIntegerText());
					FrevoMain.runSimulation(FrevoMain.getCustomName(), null);
				}
			} else
				System.out
						.println("ERROR: Session setup is incomplete or simulation is already running!");
		}
		// simulation stop button
		else if (source == stopButton) {
			simulationWorkerThread.getMethod().pause();

			sessionIsPaused = true;

			// FrevoMain.isRunning = false;

			System.out
					.println("Simulation has been stopped by the user, please wait while the method stops");
			stopButton.setEnabled(false);
			startButton.setEnabled(true);
			resetButton.setEnabled(true);
		}
		// simulation reset button
		else if (source == resetButton) {
			int confirm = JOptionPane
					.showConfirmDialog(FrevoWindow.this,
							"Are you sure you want to reset? Simulation progress will be lost!");
			if (confirm == JOptionPane.OK_OPTION) {
				// reset FREVO
				reset();
			}
		} else if (source == saveCurrentButton) {
			saveCurrentState();
		} else if (source == replayCurrentButton) {
			File savedFile = saveCurrentState();
			if (savedFile != null) {
				setLoadingLayout();
				LoadingWorkerThread loadingThread = new LoadingWorkerThread(
						savedFile);
				loadingThread.execute();
			}
		}
		// config backbutton (back from extra settings)
		else if (source == advancedSettingsBackButton) {
			updateAdvancedLabels();

			// return config layout
			setNormalTopLayout();
			advancedButton.setIcon(advancedIcon);
			topPanel.validate();
		}
		// load menu
		else if (source == loadResultsItem) {
			JFileChooser fc = new JFileChooser(FrevoMain.getActiveDirectory());
			fc.setAcceptAllFileFilterUsed(false);
			// fc.addChoosableFileFilter(new ZREFilter());
			fc.addChoosableFileFilter(new CustomFileFilter(
					FrevoMain.FREVO_RESULT_FILE_TYPE));

			int returnVal = fc.showDialog(FrevoWindow.this,
					"Open FREVO results");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				loadFile(fc.getSelectedFile());
			}
		} else if (source == loadSessionItem) { 
			JFileChooser fc = new JFileChooser(FrevoMain.getActiveDirectory());
			fc.setAcceptAllFileFilterUsed(false);
			fc.addChoosableFileFilter(new CustomFileFilter(
					FrevoMain.FREVO_SESSION_FILE_TYPE));

			int returnVal = fc.showDialog(FrevoWindow.this,
					"Open FREVO session");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				//pause before resetting
				if(FrevoMain.isRunning)
				{
					simulationWorkerThread.getMethod().pause();
					sessionIsPaused = true;
				}
				activesessionfile = fc.getSelectedFile();
				saveItem.setEnabled(true);
				loadFile(activesessionfile);
				reset();
				stopButton.setEnabled(false);
				resetButton.setEnabled(false);
			}
		}
		// save session as
		else if (source == saveasItem) {
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM) != null) {
				JFileChooser fc = new JFileChooser(
						FrevoMain.getActiveDirectory());
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new CustomFileFilter(new FileType(
						"zse", "FREVO session files (*.zse)")));
				File saveFile = null;
				while (true) {
					int returnVal = fc.showDialog(FrevoWindow.this,
							"Save session");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File chosen = fc.getSelectedFile();
						if (!chosen.exists()) {
							saveFile = chosen;
							saveSessionToFile(saveFile);
							activesessionfile = saveFile;
							saveItem.setEnabled(true);
							break;
						}
						int confirm = JOptionPane.showConfirmDialog(
								FrevoWindow.this,
								"Overwrite file? " + chosen.getName());
						if (confirm == JOptionPane.OK_OPTION) {
							saveFile = chosen;
							saveSessionToFile(saveFile);
							activesessionfile = saveFile;
							saveItem.setEnabled(true);
							break;
						} else if (confirm == JOptionPane.NO_OPTION) {
							continue;
						}
						break;

					}
					break;
				}

			} else {
				System.err.println("Cannot save empty session!");
			}
		}
		// save session
		else if (source == saveItem) {
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM) != null
					&& activesessionfile != null) {
				File saveFile = null;
				while (true) {
					File chosen = activesessionfile;
					if (!chosen.exists()) {
						saveFile = chosen;
						saveSessionToFile(saveFile);
						break;
					}
					int confirm = JOptionPane.showConfirmDialog(
							FrevoWindow.this,
							"Overwrite file? " + chosen.getName());
					if (confirm == JOptionPane.OK_OPTION) {
						saveFile = chosen;
						saveSessionToFile(saveFile);
						break;
					} else if (confirm == JOptionPane.NO_OPTION) {
						continue;
					}
					break;
				}

			} else {
				System.err.println("Cannot save empty session!");
			}
		}
		// Exit FREVO
		else if (source == exitItem) {
			int confirm = JOptionPane.showConfirmDialog(FrevoWindow.this,
					"Are you sure?", "Exit FREVO", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.OK_OPTION)
				System.exit(0);
		}
		// replay result
		else if (source == replayButton) {
			startReplay();
		}
		else if (source == tournamentButton) {
			tournamentPlay();
		}		
		// Show details panel
		else if (source == detailsButton) {
			// get number of selected entries
			ArrayList<AbstractRepresentation> candidates = new ArrayList<AbstractRepresentation>();
			for (int l = 0; l < jTables.size(); l++) {
				JTable r = jTables.get(l);
				ListSelectionModel lsm = r.getSelectionModel();
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				for (int i = minIndex; i <= maxIndex; i++) {
					if (lsm.isSelectedIndex(i)) {
						int selected = i;
						AbstractRepresentation rep = populations.get(l).get(
								selected);
						candidates.add(rep);
					}
				}
			}

			if (candidates.size() != 1)
				JOptionPane.showMessageDialog(this,
						"Please select exactly one candidate!");
			else {
				JFrame detailsFrame = new DetailsFrame(candidates.get(0));
				detailsFrame.setVisible(true);
			}
		}
		// go back
		else if (source == closeresButton) {
			// revert GUI
			setNormalTopLayout();
			topPanel.validate();
		} else if (source == continueButton) {
			if (FrevoMain.isRunning) {
				System.err.println("ERROR: Simulation is already running!");
			} else {
				stopButton.setEnabled(true);
				startButton.setEnabled(false);
				resetButton.setEnabled(false);

				// new simulation or continue
				if (sessionIsPaused) {
					simulationWorkerThread.getMethod().wakeUp();
					FrevoMain.isRunning = true;
					sessionIsPaused = false;
				} else {
					methodStateChanged(null);
					FrevoMain.isRunning = true;
					sessionIsPaused = false;

					FrevoMain.setNumberOfSimulationRuns(numberofrunsTextField
							.getIntegerText());
					FrevoMain.runSimulation(FrevoMain.getCustomName(), null);
				}
				//old code, doesn't support continouing
				/*stopButton.setEnabled(true);
				startButton.setEnabled(false);
				resetButton.setEnabled(false);

				methodStateChanged(null);
				FrevoMain.isRunning = true;
				sessionIsPaused = false;

				Document doc = loadedFile!=null ? FrevoMain.loadSession(loadedFile) : null;	//checks whether there currently is a file loaded, and if yes loads it into the simulation
				FrevoMain.setNumberOfSimulationRuns(numberofrunsTextField
						.getIntegerText());
				FrevoMain.runSimulation(FrevoMain.getCustomName(), doc);*/
				setNormalTopLayout();
				topPanel.validate();
			}
		}
		// Start evaluation
		else if (source == startEvalButton) {
			ArrayList<AbstractRepresentation> candidates = new ArrayList<AbstractRepresentation>();
			for (int l = 0; l < jTables.size(); l++) {
				JTable r = jTables.get(l);
				ListSelectionModel lsm = r.getSelectionModel();
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				for (int i = minIndex; i <= maxIndex; i++) {
					if (lsm.isSelectedIndex(i)) {
						int selected = i;
						AbstractRepresentation rep = populations.get(l).get(
								selected);
						candidates.add(rep);
					}
				}
			}

			// Get loaded problem configuration
			ProblemXMLData problem = (ProblemXMLData) FrevoMain
					.getSelectedComponent(ComponentType.FREVO_PROBLEM);
			if (problem == null)
				throw new IllegalStateException(
						"ERROR: Problem configuration is not loaded!");

			System.out.println("Evaluating candidate...");
			FrevoMain.evaluateCandidates(candidates, problem, null,
					this.activeSeed);
			System.out.println("Evaluation finished");

		}
		// Show batch evaluation settings window
		else if (source == batchEvalButton) {
			// get selected representations
			ArrayList<AbstractRepresentation> candidates = new ArrayList<AbstractRepresentation>();
			for (int l = 0; l < jTables.size(); l++) {
				JTable r = jTables.get(l);
				ListSelectionModel lsm = r.getSelectionModel();
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				for (int i = minIndex; i <= maxIndex; i++) {
					if (lsm.isSelectedIndex(i)) {
						int selected = i;
						AbstractRepresentation rep = populations.get(l).get(
								selected);
						candidates.add(rep);
					}
				}
			}
			// check problem requirements for the range of allowed number of
			// candidates
			ProblemXMLData problem = (ProblemXMLData) FrevoMain
					.getSelectedComponent(ComponentType.FREVO_PROBLEM);

			int minimumplayers = problem.getMinimumNumberOfPlayers();
			int maximumplayers = problem.getMaximumNumberOfPlayers();

			int selectednumber = candidates.size();
			if ((selectednumber >= minimumplayers)
					&& (selectednumber <= maximumplayers)) {
				// Proper number of candidates are selected
				candidatesToEvaluate = candidates;
				representationsList.setListData(candidatesToEvaluate.toArray());
				setBatchEvalLayout();
			} else {
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
				JOptionPane.showMessageDialog(this,
						"The number of selected candidates are invalid. "
								+ help, "Selection error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// Component Creator
		else if (source == ccreatorItem) {
			new ComponentCreator().setVisible(true);
		}
		// Add to batch queue
		else if (source == addToQueueButton) {
			// create new properties picker window based on the selected
			// (loaded) problem
			addTaskWindow = new AddTaskToQueueWindow(
					FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM));
			addTaskWindow.setVisible(true);
			FrevoWindow.this.setEnabled(false);
		}
		// remove all items from the batch queue
		else if (source == eraseQueueButton) {
			batchQueueTableModel.removeAllElements();
		}
		// returns from evaluation settings
		else if (source == backFromEvaluationButton) {
			setResultsTopLayout();
		}
		// start batch evaluation
		else if (source == startBatchEvaluationButton) {
			// return if there is nothing to be evaluated
			int numberOfBatchElements = batchQueueTableModel.getRowCount();
			if (numberOfBatchElements == 0)
				return;

			// lockEvalWindow();
			FrevoWindow.this.setEnabled(false);
			ExecutorService executor = Executors.newFixedThreadPool(1);

			for (int i = 0; i < numberOfBatchElements; i++) {
				final BatchQueueElement batchElement = batchQueueTableModel
						.removeElement(0);
				final boolean isLastThread = (i == (numberOfBatchElements - 1));

				Runnable evalWorker = new Runnable() {
					public void run() {
						System.out
								.println("Starting evaluation of next queue element...");
						// problem to be evaluated
						ProblemXMLData problem = (ProblemXMLData) FrevoMain
								.getSelectedComponent(ComponentType.FREVO_PROBLEM);
						// property entry that will be changed during batch
						XMLFieldEntry stepEntry = problem.getProperties()
								.get(batchElement.getPropertyKey()).clone();

						// check step feasibility
						int startValue = Integer.parseInt(batchElement
								.getStartValue());
						int endValue = Integer.parseInt(batchElement
								.getEndValue());
						int stepValue = Integer.parseInt(batchElement
								.getStepValue());
						if (((startValue > endValue) && (stepValue > 0))
								|| ((startValue < endValue) && (stepValue < 0))
								|| (stepValue == 0)) {
							System.err
									.println("Element in queue skipped due to unfeasible conditions!");
						}
						else
						{

							// check completeness
							if ((endValue - startValue) % stepValue != 0)
								System.err
										.println("Start-end difference is not evenly divisable by the step value!");
	
							// step entry in while loop (with warnings)
							while (startValue <= endValue) {
								// setup properties
								String currentValue = Integer.toString(startValue);
								stepEntry.setValue(currentValue);
								Hashtable<String, XMLFieldEntry> properties = batchElement
										.getProperties();
								properties.put(batchElement.getPropertyKey(),
										stepEntry);
	
								System.out.println(batchElement.getPropertyKey()
										+ ": " + currentValue);
								FrevoMain.evaluateCandidates(candidatesToEvaluate,
										problem, properties, activeSeed);
								startValue += stepValue;
							}
	
							System.out
									.println("Evaluation of queue element is finished!");
						}
						// finalize
						if (isLastThread) {
							// unLockEvalWindow();
							FrevoWindow.this.setEnabled(true);
						}
					}
				};

				executor.execute(evalWorker);
			}

		}
	}

	private File saveCurrentState() {
		if (getWorkerThread() == null || getWorkerThread().getMethod() == null) {
			methodStateChanged(null);
		} else {
			AbstractMethod method = getWorkerThread().getMethod();
			XMLMethodStep step = method.getLastResults();
			return FrevoMain.saveResult(step.getName(), step.getData(),
					step.getStartSeed(), step.getCurrentSeed());
		}
		return null;
	}

	private void startReplay() {
		// get selected representations
		ArrayList<AbstractRepresentation> candidates = new ArrayList<AbstractRepresentation>();
		for (int l = 0; l < jTables.size(); l++) {
			JTable r = jTables.get(l);
			ListSelectionModel lsm = r.getSelectionModel();
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();
			for (int i = minIndex; i <= maxIndex; i++) {
				if (lsm.isSelectedIndex(i)) {
					AbstractRepresentation rep = populations.get(l).get(i);
					candidates.add(rep);
				}
			}
		}

		// problem to be replayed
		ProblemXMLData problem = (ProblemXMLData) FrevoMain
				.getSelectedComponent(ComponentType.FREVO_PROBLEM);

		// Minimum number of players that needs to be selected
		int minimumplayers = problem.getMinimumNumberOfPlayers();
		// Maximum number of players that needs to be selected
		int maximumplayers = problem.getMaximumNumberOfPlayers();

		// number of candidates currently selected
		int selectednumber = candidates.size();

		if (((selectednumber < minimumplayers) || (selectednumber > maximumplayers))
				&& (selectednumber != 0)) {
			// show error
			String help = "";
			if ((minimumplayers == 1) && (maximumplayers == 1))
				help = "Please select exactly one candidate!";
			else {
				if (minimumplayers != maximumplayers)
					help = "Please use the SHIFT and CTRL keys to select min. "
							+ minimumplayers + ", max. " + maximumplayers
							+ " representations.";
				else
					help = "Please use the SHIFT and CTRL keys to select exactly "
							+ minimumplayers + " representations.";
			}

			JOptionPane.showMessageDialog(this,
					"The number of selected candidates are invalid. " + help,
					"Selection error", JOptionPane.ERROR_MESSAGE);
		} else {
			// fix null selections
			if (selectednumber == 0) {
				// if nothing is selected select first ones automatically
				if (maximumplayers == 1) {
					// add first entry
					candidates.add(populations.get(0).get(0));
					// select it visually
					jTables.get(0).getSelectionModel()
							.setSelectionInterval(0, 0);
				} else {
					// select minimum number of players
					candidates.addAll(populations.get(0).subList(0,
							minimumplayers));
					jTables.get(0).getSelectionModel()
							.setSelectionInterval(0, minimumplayers - 1);
				}
			}

			// refresh number of selected entries
			selectednumber = candidates.size();

			try {
				if (maximumplayers == 1) {
					AbstractSingleProblem ip = (AbstractSingleProblem) problem
							.getNewProblemInstance();
					ip.setRandom(new NESRandom(this.activeSeed));
					ip.replayWithVisualization(candidates.get(0));
				} else {
					// IMultiProblem
					AbstractMultiProblem imp = (AbstractMultiProblem) problem
							.getNewProblemInstance();
					imp.setRandom(new NESRandom(this.activeSeed));
					imp.replayWithVisualization(candidates
							.toArray(new AbstractRepresentation[selectednumber]));
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Run tournament between selected candidates
	 */
	private void tournamentPlay() {
		// get selected representations
		ArrayList<AbstractRepresentation> candidates = new ArrayList<AbstractRepresentation>();
		for (int l = 0; l < jTables.size(); l++) {
			JTable r = jTables.get(l);
			ListSelectionModel lsm = r.getSelectionModel();
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();
			for (int i = minIndex; i <= maxIndex; i++) {
				if (lsm.isSelectedIndex(i)) {
					AbstractRepresentation rep = populations.get(l).get(i);
					candidates.add(rep);
				}
			}
		}
		
		if (candidates.size() < 1) {
			return;
		}
		
		int[] points = new int[candidates.size()];
		
		// problem to be replayed
		ProblemXMLData problem = (ProblemXMLData) FrevoMain
			.getSelectedComponent(ComponentType.FREVO_PROBLEM);
		
		for (int i = 0; i<candidates.size()-1; i++) {
			for (int j = i+1; j<candidates.size(); j++) {
				AbstractRepresentation first = candidates.get(i);
				AbstractRepresentation second = candidates.get(j);
				
				try {
					AbstractMultiProblem imp;
					imp = (AbstractMultiProblem) problem
								.getNewProblemInstance();
								
					imp.setRandom(new NESRandom(this.activeSeed));
					AbstractRepresentation[] reps = new AbstractRepresentation[2];
					reps[0] = first;
					reps[1] = second; 
							
					List<RepresentationWithScore> results = imp.evaluateFitness(reps);
					if (results.size() > 1) {
						RepresentationWithScore firstResult = results.get(0);
						RepresentationWithScore secondResult = results.get(1);
						
						if (firstResult.getHiddenFitness() > 40000000) {
							if (firstResult.getRepresentation() == first) {
								points[i] += 1; 
							} else {
								points[j] += 1;
							}
						}
						
						if (secondResult.getHiddenFitness() > 40000000) {
							if (secondResult.getRepresentation() == second) {
								points[j] += 1;
							} else {
								points[i] += 1;
							}
						}					
					}
					
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			}
		}
		
		ArrayList<RepresentationWithScore> list = new ArrayList<RepresentationWithScore>(candidates.size()); 
		for (int i = 0; i<candidates.size(); i++) {
			RepresentationWithScore repWithScore = new RepresentationWithScore(candidates.get(i), points[i]);
			list.add(repWithScore);							
		}
		
		Collections.sort(list, new Comparator<Object>() 
		{
		    public int compare(Object o1, Object o2) 
		    {
		       if(o1 instanceof RepresentationWithScore && o2 instanceof RepresentationWithScore) 
		       {
		          RepresentationWithScore s_1 = (RepresentationWithScore)o1;
		          RepresentationWithScore s_2 = (RepresentationWithScore)o2;
		          
		          return Double.compare(s_2.getScore(), s_1.getScore());
		       } 
		       return 0;    
		    }
		});
		
		TournamentResults resultsFrame = new TournamentResults(list);
		resultsFrame.setVisible(true);				
	}


	/**
	 * Loads session/results data from file, uses a dedicated thread for that
	 */
	public void loadFile(File loadFile) {
		setLoadingLayout();
		//loadedFile = loadFile;
		LoadingWorkerThread loadingThread = new LoadingWorkerThread(loadFile);
		loadingThread.execute();
	}

	public void setLoadingProgress(float progress) {
		if ((progress >= 0) && (progress <= 1)) {
			loadingPanel.setProgress(progress);
		}
	}

	/** Reloads populations into the result display (on the left) */
	private void refreshResultList() {
		jTables = new ArrayList<JTable>();
		
		populationsTabbedPane.removeAll();

		for (int i = 0; i < populations.size(); i++) {
			// to visualize the population we have to create a table
			
			// data for the table
			Object[][] data = new Object[populations.get(i).size()][4];
			DecimalFormat formatter = new DecimalFormat("0.###"); 
						
			for (int u = 0; u < populations.get(i).size(); u++) {
				// order number of the representation in population
				data[u][0] = u;
				// the name of the population
				data[u][1] = populations.get(i).get(0).getXMLData().getClassName();
				// hash of the candidate
				data[u][2] = populations.get(i).get(u).getHash();
				// fitness value for current representation applied for selected problem
				
				data[u][3] = formatter.format(populations.get(i).get(u).getFitness());
			}
			
			// names of the columns for the table
			Object[] names = new Object[4];
			names[0] = "#";
			names[1] = "Controller";
			names[2] = "Hash";
			names[3] = "Fitness";
			
			JTable table = new JTable(data, names);
			JPanel panel = new JPanel();
			
			ProblemXMLData problem = (ProblemXMLData) FrevoMain
					.getSelectedComponent(ComponentType.FREVO_PROBLEM);

			int minimumplayers = problem.getMinimumNumberOfPlayers();
			if (minimumplayers > 1) {
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			} else {
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			}
			table.addMouseListener(this);
			jTables.add(table);
			JScrollPane scrollPane = new JScrollPane(table);
			GridLayout paneLayout = new GridLayout(1, 1);
			panel.setLayout(paneLayout);
			panel.add(scrollPane);

			populationsTabbedPane.addTab(String.format("Population %d", i), panel);
		}
	}

	/** Saves the active session to the provided file */
	private void saveSessionToFile(File saveFile) {
		if (!FrevoMain.getExtension(saveFile).equals("zse")) {
			saveFile = new File(saveFile.getAbsolutePath() + ".zse");
		}

		try {
			// Create file to save
			Document doc = DocumentHelper.createDocument();
			doc.addDocType("frevo", null, ".//Components//ISaveSession.dtd");
			Element dfrevo = doc.addElement("frevo");

			// export sessionconfig
			Element sessionconfig = dfrevo.addElement("sessionconfig");
			// custom name
			Element configentry = sessionconfig.addElement("configentry");
			configentry.addAttribute("key", "CustomName");
			configentry.addAttribute("type", "STRING");
			configentry.addAttribute("value", simnameTextField.getText());
			// number of runs
			Element runentry = sessionconfig.addElement("configentry");
			runentry.addAttribute("key", "NumberofRuns");
			runentry.addAttribute("type", "INT");
			runentry.addAttribute("value", numberofrunsTextField.getText());
			// starting seed
			Element seedentry = sessionconfig.addElement("configentry");
			seedentry.addAttribute("key", "StartingSeed");
			seedentry.addAttribute("type", "LONG");
			seedentry.addAttribute("value", seedTextField.getText());

			// export problem
			Element problemsettings = dfrevo.addElement("problem");
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM) != null) {
				ComponentXMLData problem = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_PROBLEM);
				Vector<String> keys = new Vector<String>(problem
						.getProperties().keySet());
				problemsettings.addAttribute("class", problem.getClassName());
				for (String k : keys) {
					Element entry = problemsettings.addElement("problementry");
					entry.addAttribute("key", k);
					entry.addAttribute("type", problem.getTypeOfProperty(k)
							.toString());
					entry.addAttribute("value", problem.getValueOfProperty(k));
				}
			}

			// export method
			Element methodsettings = dfrevo.addElement("method");
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_METHOD) != null) {
				ComponentXMLData method = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_METHOD);
				Vector<String> keys = new Vector<String>(method.getProperties()
						.keySet());
				methodsettings.addAttribute("class", method.getClassName());
				for (String k : keys) {
					Element entry = methodsettings.addElement("methodentry");
					entry.addAttribute("key", k);
					entry.addAttribute("type", method.getTypeOfProperty(k)
							.toString());
					entry.addAttribute("value", method.getValueOfProperty(k));
				}
			}

			// export representation
			Element representationsettings = dfrevo
					.addElement("representation");
			if (FrevoMain
					.getSelectedComponent(ComponentType.FREVO_REPRESENTATION) != null) {
				ComponentXMLData representation = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_REPRESENTATION);

				// export representation data
				Vector<String> keys = new Vector<String>(representation
						.getProperties().keySet());
				representationsettings.addAttribute("class",
						representation.getClassName());
				for (String k : keys) {
					Element entry = representationsettings
							.addElement("representationentry");
					entry.addAttribute("key", k);
					entry.addAttribute("type", representation
							.getTypeOfProperty(k).toString());
					entry.addAttribute("value",
							representation.getValueOfProperty(k));
				}

				if (representation.getComponentType() == ComponentType.FREVO_BULKREPRESENTATION) {
					// export core representation data into bulkrepresentation
					String corerepresentationclassname = representation
							.getProperties()
							.get("core_representation_component").getValue();
					ComponentXMLData corerepresentation = FrevoMain
							.getComponent(ComponentType.FREVO_REPRESENTATION,
									corerepresentationclassname);
					if (corerepresentation == null)
						System.err
								.println("ERROR: Could not load specified representation: "
										+ corerepresentationclassname);
					else {
						keys = new Vector<String>(corerepresentation
								.getProperties().keySet());
						for (String k : keys) {
							Element entry = representationsettings
									.addElement("corerepresentationentry");
							entry.addAttribute("key", k);
							entry.addAttribute("type", corerepresentation
									.getTypeOfProperty(k).toString());
							entry.addAttribute("value",
									corerepresentation.getValueOfProperty(k));
						}

					}

				}
			}

			// export ranking
			Element rankingsettings = dfrevo.addElement("ranking");
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_RANKING) != null) {
				ComponentXMLData ranking = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_RANKING);
				Vector<String> keys = new Vector<String>(ranking
						.getProperties().keySet());
				rankingsettings.addAttribute("class", ranking.getClassName());
				for (String k : keys) {
					Element entry = rankingsettings.addElement("rankingentry");
					entry.addAttribute("key", k);
					entry.addAttribute("type", ranking.getTypeOfProperty(k)
							.toString());
					entry.addAttribute("value", ranking.getValueOfProperty(k));
				}
			}

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setLineSeparator(System.getProperty("line.separator"));

			FileWriter out = new FileWriter(saveFile);
			BufferedWriter bw = new BufferedWriter(out);
			XMLWriter wr = new XMLWriter(bw, format);
			wr.write(doc);
			wr.close();
			System.out.println("Session saved to " + saveFile.getName());

			// add to recently save list
			addRecentSession(saveFile);
		} catch (IOException e) {
			System.err.println("Error saving session");
		}
	}

	private void saveConsoleToFile(File saveFile) {
		if (!FrevoMain.getExtension(saveFile).equals("log")) {
			saveFile = new File(saveFile.getAbsolutePath() + ".log");
		}
		try {
			FileWriter out = new FileWriter(saveFile);
			BufferedWriter output = new BufferedWriter(out);
			for (int i = 0; i < consoleDoc.getLength(); i++) {
				output.write(consoleDoc.getText(i, 1));
			}
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	// WindowListener methods
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// save preferred width
		FrevoMain.mainWindowParameters[0] = FrevoWindow.this.getBounds().width;
		FrevoMain.mainWindowParameters[1] = FrevoWindow.this.getBounds().height;
		FrevoMain.mainWindowParameters[2] = FrevoWindow.this.getBounds().x;
		FrevoMain.mainWindowParameters[3] = FrevoWindow.this.getBounds().y;
		FrevoMain.saveSettings();
		// ask for confirmation
		if (FrevoMain.isRunning) {
			int confirm = JOptionPane.showConfirmDialog(FrevoWindow.this,
					"Are you sure you want to exit FREVO?", "Are you sure?",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.OK_OPTION) {
				System.exit(0);
			}
		} else
			System.exit(0);

	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// Refresh labels
		if (FrevoMain.getSelectedComponent(ComponentType.FREVO_PROBLEM) != null) {
			problemLabel.setText(FrevoMain.getSelectedComponent(
					ComponentType.FREVO_PROBLEM).getName());
			if(!FrevoMain.isRunning && !sessionIsPaused)
				methodButton.setEnabled(true);
		} else {
			problemLabel.setText("Select Problem");
		}
		if (FrevoMain.getSelectedComponent(ComponentType.FREVO_METHOD) != null) {
			methodLabel.setText(FrevoMain.getSelectedComponent(
					ComponentType.FREVO_METHOD).getName());
			if(!FrevoMain.isRunning && !sessionIsPaused)
				representationButton.setEnabled(true);
		} else {
			methodLabel.setText("Select Method");
		}
		if (FrevoMain.getSelectedComponent(ComponentType.FREVO_REPRESENTATION) != null) {
			representationLabel.setText(FrevoMain.getSelectedComponent(
					ComponentType.FREVO_REPRESENTATION).getName());
			if(!FrevoMain.isRunning && !sessionIsPaused)
				rankingButton.setEnabled(true);
		} else {
			representationLabel.setText("Select Representation");
		}
		if (FrevoMain.getSelectedComponent(ComponentType.FREVO_RANKING) != null) {
			rankingLabel.setText(FrevoMain.getSelectedComponent(
					ComponentType.FREVO_RANKING).getName());
		} else {
			rankingLabel.setText("Select Ranking");
		}
		// check if FREVO is launchable
		if (FrevoMain.checkState()) {
			FrevoMain.isLaunchable = true;
			if (!FrevoMain.isRunning)
				startButton.setEnabled(true);
		} else {
			FrevoMain.isLaunchable = false;
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument() == simnameTextField.getDocument()) {
			FrevoMain.setCustomName(simnameTextField.getText());
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (e.getDocument() == simnameTextField.getDocument()) {
			FrevoMain.setCustomName(simnameTextField.getText());
		} else if (e.getDocument() == seedresTextField.getDocument()) {
			try {
				FrevoWindow.this.activeSeed = Long.parseLong(seedresTextField
						.getText());
			} catch (NumberFormatException e1) {
				FrevoWindow.this.activeSeed = 12345;
			}
		} else if (e.getDocument() == seedTextField.getDocument()) {
			try {
				FrevoMain.setInitialSeed(Long.parseLong(seedTextField.getText()));
			} catch (NumberFormatException e1) {
				FrevoMain.setInitialSeed(12345);
			}
		} else if (e.getDocument() == numberofrunsTextField.getDocument()) {
			try {
				FrevoMain.setNumberOfSimulationRuns(Integer
						.parseInt(numberofrunsTextField.getText()));
			} catch (NumberFormatException e1) {
				FrevoMain.setNumberOfSimulationRuns(1);
			}
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument() == simnameTextField.getDocument()) {
			FrevoMain.setCustomName(simnameTextField.getText());
		} else if (e.getDocument() == seedresTextField.getDocument()) {
			try {
				FrevoWindow.this.activeSeed = Long.parseLong(seedresTextField
						.getText());
			} catch (NumberFormatException e1) {
				FrevoWindow.this.activeSeed = 12345;
			}
		} else if (e.getDocument() == seedTextField.getDocument()) {
			try {
				FrevoMain.setInitialSeed(Long.parseLong(seedTextField.getText()));
			} catch (NumberFormatException e1) {
				FrevoMain.setInitialSeed(12345);
			}
		} else if (e.getDocument() == numberofrunsTextField.getDocument()) {
			try {
				FrevoMain.setNumberOfSimulationRuns(Integer
						.parseInt(numberofrunsTextField.getText()));
			} catch (NumberFormatException e1) {
				FrevoMain.setNumberOfSimulationRuns(1);
			}
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// saveTable(model, properties);
		if (e.getSource() == evalTable.getModel()) {
			TableModel model = evalTable.getModel();
			Hashtable<String, XMLFieldEntry> properties = evalTable
					.getProperties();

			// Save table content to selected object
			for (int i = 0; i < model.getRowCount(); i++) {
				String key = (String) model.getValueAt(i, 0);
				String value = (String) model.getValueAt(i, 2);

				if (value == null)
					return;

				XMLFieldEntry entr = properties.get(key);
				XMLFieldType type = entr.getType();

				if (FrevoMain.checkType(type, value))
					entr.setValue(value);
			}

		}

	}

	/** Filter to filter files */
	public static class CustomFileFilter extends FileFilter {

		private FileType sfile;

		public CustomFileFilter(FileType savefile) {
			this.sfile = savefile;
		}

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = FrevoMain.getExtension(f);
			if (extension != null) {
				if (extension.equals(sfile.getExtension())) {
					return true;
				}
				return false;
			}

			return false;
		}

		public String getExtension() {
			return sfile.getExtension();
		}

		// The description of this filter
		public String getDescription() {
			return sfile.getDescription();
		}
	}

	/**
	 * Stores data in a Map by columns. The keys are columns' names, while each
	 * value is a array of Objects. This implementation maintains a default
	 * ordering based on keys.
	 */
	static class HashTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1930986740986471854L;
		Hashtable<String, String> htable;

		public HashTableModel(Hashtable<String, String> hash) {
			this.htable = hash;
		}

		public void setTableModel(Hashtable<String, String> hash) {
			this.htable = hash;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			if (htable != null)
				return htable.size();

			return 0;
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (column == 0) {
				return getKey(row);
			}
			return htable.get(getKey(row));
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		private String getKey(int a_index) {
			String retval = "";
			Enumeration<String> e = htable.keys();
			for (int i = 0; i < a_index + 1; i++) {
				retval = e.nextElement();
			} // for

			return retval;
		}
	}

	static class PropertiesTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 8047616004117865346L;

		private Hashtable<Object, Object> lookup;

		private int rows;
		private final int columns;
		private final String headers[];
		public boolean isLoaded = false;
		public boolean filtered = false;

		public XMLFieldType[] FILTEREDCHANGES = {/*
												 * XMLFieldType.FILE,
												 * XMLFieldType.STRING
												 */};

		public PropertiesTableModel(String columnHeaders[], int rows) {
			this.columns = columnHeaders.length;
			headers = columnHeaders;
			lookup = new Hashtable<Object, Object>();
			this.rows = rows;
		}

		public PropertiesTableModel(String columnHeaders[],
				Hashtable<String, XMLFieldEntry> properties) {
			this.columns = columnHeaders.length;
			headers = columnHeaders;
			lookup = new Hashtable<Object, Object>();
			this.rows = properties.size();
			loadProperties(properties);
		}

		public int getColumnCount() {
			return columns;
		}

		public int getRowCount() {
			return rows;
		}

		public String getColumnName(int column) {
			return headers[column];
		}

		public Object getValueAt(int row, int column) {
			return lookup.get(new Point(row, column));
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			if ((rows < 0) || (columns < 0)) {
				throw new IllegalArgumentException("Invalid row/column setting");
			}
			if ((row < rows) && (column < columns)) {
				lookup.put(new Point(row, column), value);
				if (isLoaded)
					fireTableCellUpdated(row, column);
			} else
				throw new Error("Wrong values");
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == columns - 1) {
				if (getValueAt(rowIndex, columnIndex - 1).equals(
						"core_representation_component"))
					return false;
				// last column
				if (!filtered) {
					// check for core_representation
					return true;
				}
				XMLFieldType pt = (XMLFieldType) getValueAt(rowIndex,
						columnIndex - 1);
				for (XMLFieldType p : FILTEREDCHANGES) {
					if (p == pt)
						return false;
				}
				return true;

			}
			return false;
		}

		public void loadProperties(Hashtable<String, XMLFieldEntry> properties) {
			isLoaded = false;
			lookup.clear();
			rows = properties.size();
			Vector<String> v = new Vector<String>(properties.keySet());
			Collections.sort(v);

			Iterator<String> it = v.iterator();
			int i = 0;
			while (it.hasNext()) {
				String element = it.next();
				XMLFieldEntry e = properties.get(element);
				setValueAt(element, i, 0);
				setValueAt(e.getValue(), i, 1);
				i++;
			}
			fireTableDataChanged();
			isLoaded = true;
		}
	}

	public SimulationWorkerThread getWorkerThread() {
		return simulationWorkerThread;
	}

	/** Adds the session file to the latest list */
	private void addRecentSession(File sessionFile) {
		if (recentSessions.size() == RECENT_LIST_LENGTH) {
			// remove the oldest
			recentSessions.remove(0);
		}
		// add new to the list
		recentSessions.add(sessionFile);

		buildLoadMenu();
	}

	/** Adds the result file to the latest list (menu). */
	public void addRecentResult(File resultFile) {
		if (recentResults.size() == RECENT_LIST_LENGTH) {
			// remove the oldest
			recentResults.remove(0);
		}
		// add new to the list
		recentResults.add(resultFile);

		buildLoadMenu();
	}

	/**
	 * Changes some visual objects if the Method's last state has been changed.
	 * 
	 * @param method
	 *            information about the Method which is important for checking
	 *            of its state.
	 */
	public void methodStateChanged(AbstractMethod method) {
		if (saveCurrentButton != null) {
			if (method == null || method.getLastResults() == null) {
				saveCurrentButton.setEnabled(false);
				saveCurrentButton.setToolTipText("Save current state");
				replayCurrentButton.setEnabled(false);
			} else {
				XMLMethodStep step = method.getLastResults();
				saveCurrentButton.setEnabled(true);
				saveCurrentButton.setToolTipText("Save current state: "
						+ step.getName());
				replayCurrentButton.setEnabled(true);
			}
		}
	}

	/**
	 * Changes some visual objects if the possibility of the continuation has
	 * been changed.
	 * 
	 * @param method
	 *            information about the Method which is important for checking
	 *            of its state.
	 */
	public void changeContinueState(AbstractMethod method) {
		if (continueButton != null) {
			if (method == null || method.canContinue() == false) {
				continueButton.setEnabled(false);
				continueButton
						.setToolTipText("Unable to continue an experiment");
			} else {
				continueButton.setEnabled(true);
				continueButton.setToolTipText("Continue of an experiment");
			}
		}
	}
}
