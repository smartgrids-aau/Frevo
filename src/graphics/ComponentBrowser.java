package graphics;

import graphics.FrevoWindow.PropertiesTableModel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import main.FrevoMain;
import main.FrevoMain.KeywordCategory;
import utils.ScreenCapture;
import core.ComponentType;
import core.ComponentXMLData;
import core.ProblemXMLData;
import core.XMLFieldEntry;
import core.XMLFieldType;

public class ComponentBrowser extends JFrame implements WindowListener,
		MouseListener, TableModelListener, ActionListener {

	private static final long serialVersionUID = 590310985776157487L;

	/** Tree containing icons on the left. */
	private JTree tree;

	private JScrollPane propTableScrollPane;
	private JPropertiesTable propTable;
	private PropertiesTableModel propertiesTableModel;

	private GroupLayout bulkRepresentationPanelLayout;
	private JScrollPane coreRepTableScrollPane;
	private JPropertiesTable coreRepTable;
	private PropertiesTableModel coreRepTableModel;
	private JLabel coreRepresentationLabel;
	private JLabel bulkRepresentationTitleLabel;
	private JComboBox<ComponentXMLData> coreRepresentationBox;

	private JButton confirmButton;
	private JButton cancelButton;

	/** Scroll pane for description field */
	private JScrollPane descview;
	/** TextPane to display formatted description text */
	private JTextPane descrPane;

	JSplitPane topsplitPane;
	JSplitPane bigsplitPane;

	JPanel bulkRepresentationPanel;

	private ArrayList<ComponentXMLData> componentList = new ArrayList<ComponentXMLData>();
	private Hashtable<String, XMLFieldEntry> properties;
	private Hashtable<String, XMLFieldEntry> coreRepProperties;

	private ComponentType componentType;

	private DefaultMutableTreeNode root;

	/** Reference to the image shown when loading the custom image fails. */
	private ImageIcon noimage;

	/** Holds a reference to the currently selected component */
	ComponentXMLData SELECTEDITEM = null;

	/** Enumeration containing only true and false fields. */
	enum TrueFalse {
		TRUE, FALSE
	}

	private Dimension iconSize = new Dimension(36, 36);

	ArrayList<FrevoMain.KeywordCategory> categories;
	ArrayList<DefaultMutableTreeNode> categorynodes = new ArrayList<DefaultMutableTreeNode>();

	public ComponentBrowser(ComponentType componenttype) {
		super("Select " + FrevoMain.getComponentTypeAsString(componenttype)
				+ " Component");
		componentType = componenttype;
		categories = FrevoMain.getCategories(componenttype);

		setBounds(0, 0, FrevoMain.componentBrowserParameters[0],
				FrevoMain.componentBrowserParameters[1]);// set initial size

		// inherit main frame
		Container con = this.getContentPane();

		// make this appear in the middle of the screen
		this.setLocationRelativeTo(null);

		// set minimum size
		setMinimumSize(new Dimension(500, 550));

		// load noimage and resize
		noimage = FrevoWindow.nothingIcon;
		noimage = ScreenCapture.resizeImage(noimage.getImage(), iconSize.width,
				iconSize.height);

		// return focus to main window after this has been closed
		this.addWindowListener(this);

		// Get all components from FrevoMain
		componentList.clear();

		Iterator<ComponentXMLData> it = FrevoMain
				.getComponentList(componenttype).values().iterator();

		while (it.hasNext()) {
			componentList.add(it.next());
		}

		// Add multi problems
		if (componenttype == ComponentType.FREVO_PROBLEM) {
			it = FrevoMain.getComponentList(ComponentType.FREVO_MULTIPROBLEM)
					.values().iterator();

			while (it.hasNext()) {
				componentList.add(it.next());
			}
		}

		// Add bulk representations
		if (componenttype == ComponentType.FREVO_REPRESENTATION) {
			// add bulkrepresentations to the browser's component list
			it = FrevoMain
					.getComponentList(ComponentType.FREVO_BULKREPRESENTATION)
					.values().iterator();

			while (it.hasNext()) {
				componentList.add(it.next());
			}
		}

		// filter components based on other selected components
		filterComponentList();

		root = new DefaultMutableTreeNode(
				FrevoMain.getComponentTypeAsString(componenttype) + "s");
		createTree(root);
		tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addMouseListener(this);

		tree.setShowsRootHandles(false);

		tree.expandRow(tree.getRowCount() - 1);

		// add tree renderer
		tree.setCellRenderer(new IconTreeCellRenderer());

		JScrollPane treeview = new JScrollPane(tree);
		treeview.setMinimumSize(new Dimension(270, 200));

		descrPane = new JTextPane();
		descrPane.setEditable(false);
		descrPane.setFont(new Font("Monospaced", 1, 13));
		descrPane.setCaretPosition(0);
		descrPane.setContentType("text/html");

		StyledDocument doc = descrPane.getStyledDocument();
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attr, -0.2f);
		doc.setParagraphAttributes(0, doc.getLength() - 10, attr, false);

		// add hyperlink listener
		descrPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						java.awt.Desktop desktop = java.awt.Desktop
								.getDesktop();
						desktop.browse(event.getURL().toURI());
					} catch (Exception ioe) {
						// swallow exception
						// ioe.printStackTrace();
					}
				}
			}
		});

		descview = new JScrollPane(descrPane);
		descview.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		topsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeview,
				descview);
		topsplitPane.setPreferredSize(new Dimension(500, 270));
		topsplitPane
				.setDividerLocation(FrevoMain.componentBrowserParameters[2]);

		propTableScrollPane = new JScrollPane();
		propTableScrollPane.setPreferredSize(new Dimension(500, 170));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setMaximumSize(new Dimension(5000, 40));

		confirmButton = new JButton("Confirm");
		confirmButton.setEnabled(false);
		confirmButton.addActionListener(this);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		buttonPanel.add(confirmButton);
		buttonPanel.add(cancelButton);

		bigsplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topsplitPane,
				propTableScrollPane);
		bigsplitPane
				.setDividerLocation(FrevoMain.componentBrowserParameters[3]);

		// define layout
		GroupLayout mainlayout = new GroupLayout(con);
		con.setLayout(mainlayout);

		mainlayout.setHorizontalGroup(mainlayout.createParallelGroup()
				.addComponent(bigsplitPane).addComponent(buttonPanel));
		mainlayout.setVerticalGroup(mainlayout.createSequentialGroup()
				.addComponent(bigsplitPane).addComponent(buttonPanel));

		// Setup bulk representation panel
		if (componenttype == ComponentType.FREVO_REPRESENTATION) {

			// Construct bulkRepresentationPanel's grouplayout
			coreRepresentationLabel = new JLabel("Base Representation: ");
			bulkRepresentationTitleLabel = new JLabel(
					"Bulk representation's properties");

			ArrayList<ComponentXMLData> coreRepresentationList = new ArrayList<ComponentXMLData>(
					componentList);

			ComponentXMLData[] objectarray = (ComponentXMLData[]) coreRepresentationList
					.toArray(new ComponentXMLData[coreRepresentationList.size()]);
			coreRepresentationBox = new JComboBox<ComponentXMLData>(objectarray);
			coreRepresentationBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED)
						loadCoreRepresentation((ComponentXMLData) coreRepresentationBox
								.getSelectedItem());
				}
			});

			bulkRepresentationPanel = new JPanel();

			coreRepTableScrollPane = new JScrollPane();
			coreRepTableScrollPane.setPreferredSize(new Dimension(500, 170));
		}

	}

	/** Filters the list based on several criterias */
	private void filterComponentList() {
		if (this.componentType == ComponentType.FREVO_RANKING) {
			// filter rankings based on selected problem
			ProblemXMLData selectedproblem = (ProblemXMLData) FrevoMain
					.getSelectedComponent(ComponentType.FREVO_PROBLEM);

			XMLFieldEntry maxplayersEntry = selectedproblem.getRequirements()
					.get("maximumCandidates");
			XMLFieldEntry minplayersEntry = selectedproblem.getRequirements()
					.get("minimumCandidates");
			int maxplayers = Integer.parseInt(maxplayersEntry.getValue());
			int minplayers = Integer.parseInt(minplayersEntry.getValue());

			if (minplayers > 1) {
				// disable absoluterank
				Iterator<ComponentXMLData> itr = componentList.iterator();
				while (itr.hasNext()) {
					ComponentXMLData cdata = itr.next();
					if (cdata.getKeywords().contains("absolute")) {
						itr.remove();
					}
				}
			} else if (maxplayers < 2) {
				// disable all tournaments
				Iterator<ComponentXMLData> itr = componentList.iterator();
				while (itr.hasNext()) {
					ComponentXMLData cdata = itr.next();
					if (!cdata.getKeywords().contains("absolute")) {
						itr.remove();
					}
				}
			}

		}
	}

	/** Saves the changes of this window */
	private void saveChanges() {
		// save preferences
		FrevoMain.componentBrowserParameters[0] = ComponentBrowser.this
				.getBounds().width;
		FrevoMain.componentBrowserParameters[1] = ComponentBrowser.this
				.getBounds().height;
		FrevoMain.componentBrowserParameters[2] = topsplitPane
				.getDividerLocation();
		FrevoMain.componentBrowserParameters[3] = bigsplitPane
				.getDividerLocation();
	}

	/**
	 * finishes componentbrowser session
	 */
	private void submitChanges() {
		ComponentXMLData oldSelectedComponent = FrevoMain
				.getSelectedComponent(componentType);
		FrevoMain.setSelectedComponent(componentType, SELECTEDITEM);

		saveChanges();

		// new problem selected
		if ((oldSelectedComponent != SELECTEDITEM)
				&& (componentType == ComponentType.FREVO_PROBLEM)) {
			if (FrevoMain.getSelectedComponent(ComponentType.FREVO_RANKING) != null) {
				// check compatibility with ranking
				ProblemXMLData selectedproblem = (ProblemXMLData) FrevoMain
						.getSelectedComponent(ComponentType.FREVO_PROBLEM);
				XMLFieldEntry maxplayersEntry = selectedproblem
						.getRequirements().get("maximumCandidates");
				XMLFieldEntry minplayersEntry = selectedproblem
						.getRequirements().get("minimumCandidates");
				int maxplayers = Integer.parseInt(maxplayersEntry.getValue());
				int minplayers = Integer.parseInt(minplayersEntry.getValue());
				ComponentXMLData selectedranking = FrevoMain
						.getSelectedComponent(ComponentType.FREVO_RANKING);
				if (selectedranking.getClassName().equals(
						"AbsoluteRanking.AbsoluteRanking")) {
					if (minplayers > 1) {
						// invalid ranking
						FrevoMain.getMainWindow().raBrowser = null;
						FrevoMain.setSelectedComponent(
								ComponentType.FREVO_RANKING, null);

					}
				} else {
					if (maxplayers < 2) {
						// invalid ranking
						FrevoMain.getMainWindow().raBrowser = null;
						FrevoMain.setSelectedComponent(
								ComponentType.FREVO_RANKING, null);
					}
				}
			} else {
				FrevoMain.getMainWindow().raBrowser = null;
			}
			FrevoMain.setCustomName(FrevoMain.getSelectedComponent(
					ComponentType.FREVO_PROBLEM).getName());
			FrevoMain.getMainWindow().updateAdvancedLabels();
		}

		FrevoMain.getMainWindow().setEnabled(true);
		ComponentBrowser.this.setVisible(false);
	}

	private class IconTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -6635134544981447344L;

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			// actual component
			if ((leaf) && (node.getUserObject() instanceof ComponentXMLData)) {
				ComponentXMLData cdata = (ComponentXMLData) (node
						.getUserObject());
				String imagepath = (FrevoMain
						.getComponentDirectory(componentType)
						+ cdata.getClassDir() + File.separator + cdata
						.getImageLocation());

				ImageIcon componentIcon = FrevoWindow.getAdaptedImage(imagepath);
				componentIcon.setDescription(cdata.getName());
				setIcon(ScreenCapture.resizeImage(componentIcon.getImage(), 48, 48));

				//already caught by FrevoWindow.getAdaptedImage()
				/*if (componentIcon.getImageLoadStatus() != java.awt.MediaTracker.ERRORED) {
					setIcon(ScreenCapture.resizeImage(componentIcon.getImage(),
							iconSize.width, iconSize.height));
				} else
					setIcon(noimage);*/
			} else if (node.getUserObject() instanceof KeywordCategory) {
				KeywordCategory nodeInfo = (KeywordCategory) node
						.getUserObject();
				ImageIcon img = FrevoWindow.getAdaptedImage(
						ClassLoader.getSystemResource("Categories/"
								+ FrevoMain
										.getComponentTypeAsString(componentType)
								+ "/" + nodeInfo.getImagePath()));
				setIcon(ScreenCapture.resizeImage(img.getImage(),
						iconSize.width, iconSize.height));
			} else {
				// The root icon
				ImageIcon img = FrevoWindow.getAdaptedImage(
						ClassLoader.getSystemResource("Categories/"
								+ FrevoMain
										.getComponentTypeAsString(componentType)
								+ "/" + "empty-folder.png"));
				setIcon(ScreenCapture.resizeImage(img.getImage(),
						iconSize.width, iconSize.height));
			}

			return this;
		}
	}

	private void createTree(DefaultMutableTreeNode top) {
		// List containing the tags for this component type

		int[] elementnumber = new int[categories.size()];
		for (int i = 0; i < categories.size(); i++)
			elementnumber[i] = 0;

		int unsortednumber = 0;

		// add categories
		for (int i = 0; i < categories.size(); i++) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(
					categories.get(i));
			top.add(node);
			categorynodes.add(node);
		}

		DefaultMutableTreeNode categoryUnsorted = new DefaultMutableTreeNode(
				new KeywordCategory("Unsorted", null, "unsorted.png"));

		DefaultMutableTreeNode categoryAll = new DefaultMutableTreeNode(
				new KeywordCategory("All", null, "all.png"));

		// sort components into categories
		for (int i = 0; i < componentList.size(); i++) {// iterate through
														// components
			ComponentXMLData cdata = componentList.get(i);
			ArrayList<String> ctags = cdata.getKeywords();
			boolean sorted = false;

			for (int k = 0; k < categories.size(); k++) {// iterate through all
															// categories
				boolean placed = false;
				for (int j = 0; j < ctags.size(); j++) {// iterate through this
														// components tags
					for (int l = 0; l < categories.get(k).getKeywords().size(); l++) {// iterate
																						// through
																						// all
																						// tags
																						// of
																						// this
																						// category
						if (ctags.get(j).contains(
								categories.get(k).getKeywords().get(l))) {
							placed = true;
							sorted = true;
						}
					}
				}
				if (placed) {
					// add
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) top
							.getChildAt(k);
					child.add(new DefaultMutableTreeNode(cdata));
					// adjust category counter
					elementnumber[k]++;
				}
			}
			if (!sorted) {
				categoryUnsorted.add(new DefaultMutableTreeNode(cdata));
				unsortednumber++;
			}
			// add to "All" category as well
			categoryAll.add(new DefaultMutableTreeNode(cdata));
		}

		if (unsortednumber > 0)
			top.add(categoryUnsorted);

		top.add(categoryAll);

		// adjust names to represent number of items
		for (int i = 0; i < categories.size(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) top
					.getChildAt(i);
			KeywordCategory nInfo = (KeywordCategory) (node.getUserObject());
			nInfo.setElements(elementnumber[i]);
		}

		KeywordCategory nodeInfo = (KeywordCategory) (categoryUnsorted
				.getUserObject());
		nodeInfo.setElements(unsortednumber);

		nodeInfo = (KeywordCategory) (categoryAll.getUserObject());
		nodeInfo.setElements(componentList.size());
	}

	public void setSelected(ComponentXMLData cdata) {
		loadSelected(cdata);
		// select component
		SELECTEDITEM = cdata;
		confirmButton.setEnabled(true);
		// select on tree		
		Enumeration<?> enu = root.preorderEnumeration();
		boolean found = false;
		while (enu.hasMoreElements() && !found) {
			DefaultMutableTreeNode elem = (DefaultMutableTreeNode)enu.nextElement();
			Object o = elem.getUserObject();
			if (o instanceof ComponentXMLData) {
				String name = o.toString();
				if (name.equals(cdata.toString())) {
					found = true;
					tree.setSelectionPath(new TreePath(elem.getPath()));
				}
			}
		}
	}

	private void loadCoreRepresentation(ComponentXMLData cdata) {
		coreRepProperties = cdata.getProperties();

		String headers[] = { "Keys", "Values" };
		coreRepTableModel = new PropertiesTableModel(headers,
				coreRepProperties.size());
		coreRepTableModel.addTableModelListener(this);

		coreRepTable = new JPropertiesTable(coreRepTableModel,
				coreRepProperties);
		coreRepTable.setSurrendersFocusOnKeystroke(true);

		// Load
		Vector<String> v = new Vector<String>(coreRepProperties.keySet());
		Collections.sort(v);

		Iterator<String> it = v.iterator();
		int i = 0;
		while (it.hasNext()) {
			String element = it.next();
			XMLFieldEntry e = coreRepProperties.get(element);
			coreRepTableModel.setValueAt(element, i, 0);
			coreRepTableModel.setValueAt(e.getValue(), i, 1);
			i++;
		}

		coreRepTable.addMouseListener(this);

		coreRepTableScrollPane.setViewportView(coreRepTable);
		coreRepTableScrollPane.setVisible(true);
		coreRepTableModel.isLoaded = true;

		// set property table
		if (properties != null) {
			XMLFieldEntry entry = properties
					.get("core_representation_component");
			if (entry != null) {
				entry.setValue(cdata.getClassName());

				properties.put("core_representation_component", entry);
				propertiesTableModel.loadProperties(properties);
			}
		}
	}

	private void loadSelected(ComponentXMLData cdata) {
		// Load properties
		properties = cdata.getProperties();

		// Load and format description text
		String description = cdata.getDescription();
		String author = cdata.getAuthor();
		String version = cdata.getVersion();

		StringBuilder fulltext = new StringBuilder();
		//text color
		fulltext.append("<div color=\"#" + Integer.toHexString(((FrevoTheme)MetalLookAndFeel.getCurrentTheme()).getControlTextColor().getRGB()).substring(2)+ "\">");
		// add title
		fulltext.append("<center><b><font size=\"5\">")
				.append(cdata.getName())
				.append("</font></b></center><br>");

		// add author
		if (author != null)
			fulltext.append("Created by ")
					.append(author)
					.append("<br><br>");

		// add version
		if (version != null)
			fulltext.append("Version: ")
					.append(version)
					.append("<br><br>");

		// add description
		fulltext.append("<FONT FACE=\"Arial\">")
				.append(description)
				.append("</font><br><br>");

		// add properties description
		Iterator<String> propit = properties.keySet().iterator();
		while (propit.hasNext()) {
			String key = propit.next();
			XMLFieldEntry p = properties.get(key);
			String propdesc = p.getDescription();
			if (!propdesc.isEmpty())
				fulltext.append("<b>")
						.append(key)
						.append(":</b> ")
						.append(propdesc)
						.append("<br><br>");
		}
		fulltext.append("</div>");

		descrPane.setText(fulltext.toString());
		// scroll it back up
		descrPane.setCaretPosition(0);

		// Load table for data
		// Setup Table
		String headers[] = { "Keys", "Values" };
		propertiesTableModel = new PropertiesTableModel(headers, properties);
		propertiesTableModel.addTableModelListener(this);

		propTable = new JPropertiesTable(propertiesTableModel, properties);
		propTable.setSurrendersFocusOnKeystroke(true);
		propTable.addMouseListener(this);

		propTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		propTableScrollPane.setViewportView(propTable);

		propTable.setModel(propertiesTableModel, properties);

		// load core representation's properties
		if (cdata.getComponentType() == ComponentType.FREVO_BULKREPRESENTATION) {
			// rename properties label
			bulkRepresentationTitleLabel.setText(cdata.getName()
					+ "'s properties");

			// load core representation's properties

			// try first from properties
			ComponentXMLData coreRepresentation;
			String coreRepClassName = properties.get(
					"core_representation_component").getValue();
			if (!coreRepClassName.equals("NULL")) {
				coreRepresentation = FrevoMain.getComponent(ComponentType.FREVO_REPRESENTATION, coreRepClassName);
				if (coreRepresentation != null) {
					coreRepresentationBox.setSelectedItem(coreRepresentation);
				}
			} else {
				// try from drop-down list
				coreRepresentation = (ComponentXMLData) coreRepresentationBox
						.getSelectedItem();
			}

			loadCoreRepresentation(coreRepresentation);

			// set property table
			XMLFieldEntry entry = properties
					.get("core_representation_component");
			if (entry != null) {
				entry.setValue(coreRepresentation.getClassName());

				properties.put("core_representation_component", entry);
				propertiesTableModel.loadProperties(properties);
			}

			// TODO since it seems that grouplayout cannot be updated (?)
			// the whole has to be regenerated each time the user clicks.
			// Not the best solution, you are free to provide a better one!

			// generate layout
			bulkRepresentationPanelLayout = new GroupLayout(
					bulkRepresentationPanel);
			bulkRepresentationPanel.setLayout(bulkRepresentationPanelLayout);

			// horizontal
			bulkRepresentationPanelLayout
					.setHorizontalGroup(bulkRepresentationPanelLayout
							.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addGap(10)
							.addGroup(
									bulkRepresentationPanelLayout
											.createSequentialGroup()
											.addGap(10)
											.addComponent(
													coreRepresentationLabel,
													GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE,
													GroupLayout.PREFERRED_SIZE)
											.addComponent(
													coreRepresentationBox,
													GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE,
													GroupLayout.PREFERRED_SIZE))
							.addComponent(coreRepTableScrollPane)
							.addComponent(bulkRepresentationTitleLabel,
									GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
							.addComponent(propTableScrollPane));

			// vertical
			bulkRepresentationPanelLayout
					.setVerticalGroup(bulkRepresentationPanelLayout
							.createSequentialGroup()
							.addGap(10)
							.addGroup(
									bulkRepresentationPanelLayout
											.createParallelGroup(
													GroupLayout.Alignment.CENTER)
											.addGap(10)
											.addComponent(
													coreRepresentationLabel,
													GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE,
													GroupLayout.PREFERRED_SIZE)
											.addComponent(
													coreRepresentationBox,
													GroupLayout.PREFERRED_SIZE,
													GroupLayout.DEFAULT_SIZE,
													GroupLayout.PREFERRED_SIZE))
							.addComponent(coreRepTableScrollPane)
							.addComponent(bulkRepresentationTitleLabel,
									GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
							.addComponent(propTableScrollPane));

			bigsplitPane.setBottomComponent(bulkRepresentationPanel);

		} else {
			bigsplitPane.setBottomComponent(propTableScrollPane);
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		e.getComponent().setVisible(false);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// save preferred width
		FrevoMain.componentBrowserParameters[0] = ComponentBrowser.this
				.getBounds().width;
		FrevoMain.componentBrowserParameters[1] = ComponentBrowser.this
				.getBounds().height;
		FrevoMain.componentBrowserParameters[2] = topsplitPane
				.getDividerLocation();
		FrevoMain.componentBrowserParameters[3] = bigsplitPane
				.getDividerLocation();
		FrevoMain.getMainWindow().setEnabled(true);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// select table
		JPropertiesTable table;
		if (e.getSource() == propTable)
			table = propTable;
		else if (e.getSource() == coreRepTable) {
			table = coreRepTable;
		} else
			return;

		// handle file selection dialog
		int row = table.rowAtPoint(e.getPoint());
		int column = table.columnAtPoint(e.getPoint());
		if (column == 1) {
			PropertiesTableModel tableModel = (PropertiesTableModel) table
					.getModel();
			String key = (String) tableModel.getValueAt(row, 0);
			XMLFieldEntry entr = table.getProperties().get(key);
			XMLFieldType type = entr.getType();
			if (type == XMLFieldType.FILE) {
				// open file dialog
				String entry = (String) tableModel.getValueAt(row, 1);
				File file = new File(entry);
				if (!file.exists())
					entry = FrevoMain.getInstallDirectory();
				JFileChooser fc = new JFileChooser(entry);
				int returnVal = fc.showDialog(ComponentBrowser.this,
						"Select file");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// change the table instead of the underlying data (??)
					table.setValueAt(fc.getSelectedFile().getAbsolutePath(),
							row, column);

					// reload
					tableModel.fireTableCellUpdated(row, column);
				}

			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == tree) {
			int selRow = tree.getRowForLocation(e.getX(), e.getY());

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (node == null)// Nothing is selected.
				return;

			if (selRow != -1) {
				if (e.getClickCount() == 1) {
					// load
					Object nodeInfo = node.getUserObject();
					if (node.isLeaf()) {// it is a component and not an empty
										// category
						if (nodeInfo instanceof ComponentXMLData) {
							ComponentXMLData cdata = (ComponentXMLData) nodeInfo;
							loadSelected(cdata);
							// select component
							SELECTEDITEM = cdata;
							confirmButton.setEnabled(true);
						}
					} else {
						// do not change the display
					}
				} else if (e.getClickCount() == 2) {
					if (node.isLeaf())
						submitChanges();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getSource() == propertiesTableModel)
			propTable.saveTable();
		else if (e.getSource() == coreRepTableModel)
			coreRepTable.saveTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == confirmButton) {
			submitChanges();
		} else if (e.getSource() == cancelButton) {
			saveChanges();
			FrevoMain.getMainWindow().setEnabled(true);
			ComponentBrowser.this.setVisible(false);
		}
	}

}
