package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import main.FrevoMain;
import core.ComponentXMLData;
import core.XMLFieldEntry;
import core.XMLFieldType;

public class AddTaskToQueueWindow extends JFrame implements WindowListener{

	private static final long serialVersionUID = 8899201396859403396L;
	private StaticPropertiesTableModel model;
	private static JTable dataTable;
	private static Hashtable<String, XMLFieldEntry> properties;

	private JButton addButton;

	String[] columnNames = { "Property name", "Type", "Default value" };

	public static XMLFieldType[] ALLOWED_TYPES = { XMLFieldType.INT };

	AddTaskToQueueWindow(ComponentXMLData cdata) {
		super("Add new evaluation parameter");

		this.setLocationRelativeTo(null);
		
		this.addWindowListener(this);

		this.setMinimumSize(new Dimension(300, 300));

		properties = cdata.getProperties();

		model = new StaticPropertiesTableModel(columnNames, properties.size());

		dataTable = new JPropertiesTable(model, properties);
		dataTable.setSurrendersFocusOnKeystroke(true);
		dataTable.setFocusable(false);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// load data
		Vector<String> v = new Vector<String>(properties.keySet());
		Collections.sort(v);

		Iterator<String> it = v.iterator();
		int i = 0;
		while (it.hasNext()) {
			String element = it.next();
			XMLFieldEntry e = properties.get(element);
			model.setValueAt(element, i, 0);
			model.setValueAt(e.getType(), i, 1);
			model.setValueAt(e.getValue(), i, 2);
			i++;
		}

		dataTable.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = -5734651537476922720L;

			@Override
			public boolean isSelectedIndex(final int index) {
				boolean isSelected;
				XMLFieldType type = (XMLFieldType) model.getValueAt(index, 1);

				if (!Arrays.asList(ALLOWED_TYPES).contains(type)) {
					isSelected = false;
				} else {
					isSelected = super.isSelectedIndex(index);
				}
				return isSelected;
			}

			public int getSelectionMode() {
				return SINGLE_SELECTION;
			}
		});

		JScrollPane tablePane = new JScrollPane(dataTable);

		dataTable.repaint();

		addButton = new JButton("Add selected");

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addSelectedRow();
			}
		});

		// layout
		Container con = getContentPane();
		GroupLayout layout = new GroupLayout(con);

		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(tablePane).addComponent(addButton));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(tablePane).addComponent(addButton));

		con.setLayout(layout);
	}

	private void addSelectedRow() {
		int selectedrow = dataTable.getSelectedRow();
		
		if (selectedrow != -1) {
			// check if it is not selected
			XMLFieldType type = (XMLFieldType) model.getValueAt(selectedrow, 1);

			if (Arrays.asList(ALLOWED_TYPES).contains(type)) {
				String key = (String)(model.getValueAt(selectedrow, 0));
				FrevoMain.getMainWindow().addPropertyToQueue(key);
				
				// close this window
				this.dispose();
				FrevoMain.getMainWindow().setEnabled(true);
			}		
		}

	}

	private class StaticPropertiesTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 8047616004117865346L;

		private Hashtable<Object, Object> lookup;

		private final int rows;
		private final int columns;
		private final String headers[];
		public boolean isLoaded = false;

		public StaticPropertiesTableModel(String columnHeaders[], int rows) {
			this.columns = columnHeaders.length;
			headers = columnHeaders;
			lookup = new Hashtable<Object, Object>();
			this.rows = rows;
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
			return false;
		}

	}

	static class JPropertiesTable extends JTable {
		private static final long serialVersionUID = -3347614965645084482L;

		private StaticPropertiesTableModel tablemodel;
		private Hashtable<String, XMLFieldEntry> properties;

		public JPropertiesTable(StaticPropertiesTableModel dm,
				Hashtable<String, XMLFieldEntry> p) {
			super(dm, null, null);
			this.tablemodel = dm;
			this.properties = p;
		}

		public Component prepareRenderer(final TableCellRenderer renderer,
				final int row, final int column) {
			final Component prepareRenderer = super.prepareRenderer(renderer,
					row, column);
			final TableColumn tableColumn = getColumnModel().getColumn(column);

			tableColumn.setPreferredWidth(Math.max(
					prepareRenderer.getPreferredSize().width,
					tableColumn.getPreferredWidth()));

			return prepareRenderer;
		}

		public Hashtable<String, XMLFieldEntry> getProperties() {
			return this.properties;
		}

		public String getToolTipText(MouseEvent e) {
			Point p = e.getPoint();
			int column = this.columnAtPoint(p);
			// process only the first column
			if (column != 0)
				return null;

			int row = this.rowAtPoint(p);
			String key = (String) tablemodel.getValueAt(row, column);

			XMLFieldEntry descprop = this.properties.get(key);
			String description = descprop.getDescription();
			if (description.equals(""))
				return null;

			return description;
		}

		public void paintComponent(Graphics g) {
			// First super
			super.paintComponent(g);
			int size = tablemodel.lookup.size();
			if (size > 0) {
				for (int row = 0; row < size; row++) {
					XMLFieldType type = (XMLFieldType) tablemodel.getValueAt(
							row, 1);
					if (!Arrays.asList(ALLOWED_TYPES).contains(type)) {
						Graphics2D g2 = (Graphics2D) g;
						g2.setColor(Color.RED);
						g2.setStroke(new BasicStroke(3));
						int y = dataTable.getRowHeight() * row
								+ (dataTable.getRowHeight() / 2);
						g2.drawLine(0, y, dataTable.getWidth(), y);
					}
				}
			}

		}

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
}
