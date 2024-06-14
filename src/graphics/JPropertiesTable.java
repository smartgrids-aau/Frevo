package graphics;

import graphics.FrevoWindow.PropertiesTableModel;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import main.FrevoMain;
import core.XMLFieldEntry;
import core.XMLFieldType;

public class JPropertiesTable extends JTable {
	private static final long serialVersionUID = -3347614965645084482L;

	private HashMap<Integer, TableCellEditor> editors = new HashMap<Integer, TableCellEditor>();
	private PropertiesTableModel tablemodel;
	private Hashtable<String, XMLFieldEntry> properties;

	public JPropertiesTable(PropertiesTableModel dm,
			Hashtable<String, XMLFieldEntry> properties) {
		super(dm, null, null);
		this.tablemodel = dm;
		this.properties = properties;
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
	
	public void setModel(TableModel model, Hashtable<String, XMLFieldEntry> properties) {
		super.setModel(model);
		this.properties = properties;
	}
	
	public void saveTable() {
		// Save table content to selected object
		for (int i = 0; i < tablemodel.getRowCount(); i++) {
			String key = (String) tablemodel.getValueAt(i, 0);
			String value = (String) tablemodel.getValueAt(i, 1);

			XMLFieldEntry entr = properties.get(key);
			XMLFieldType type = entr.getType();

			if (FrevoMain.checkType(type, value))
				entr.setValue(value);
		}
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

	public TableCellEditor getCellEditor(int row, int column) {
		// get the type of the given cell data
		if (column == tablemodel.getColumnCount() - 1) {
			Object key = tablemodel.getValueAt(row, 0);
			XMLFieldEntry celldata = properties.get(key);
			// Enums get a dropdown list
			if (celldata.getType() == XMLFieldType.ENUM) {
				// get combobox
				TableCellEditor editor = editors.get(row);
				if (editor == null) {
					// create new
					String enumname = celldata.getEnumName();
					try {
						Class<?> enumc = Class.forName(enumname);
						List<?> list = Arrays.asList(enumc
								.getEnumConstants());
						ArrayList<String> result = new ArrayList<String>();
						for (Object o : list) {
							result.add(o.toString());
						}

						@SuppressWarnings({ "rawtypes", "unchecked" })
						JComboBox jcbox = new JComboBox(result.toArray());
						editor = new DefaultCellEditor(jcbox);
						editors.put(row, editor);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						editor = super.getCellEditor(row, column);
						editors.put(row, editor);
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}

				}
				return editor;
			} else if (celldata.getType() == XMLFieldType.BOOLEAN) {
				// get combobox
				TableCellEditor editor = editors.get(row);
				if (editor == null) {
					// create new
					String[] elements = { "true", "false" };

					@SuppressWarnings({ "rawtypes", "unchecked" })
					JComboBox jcbox = new JComboBox(elements);
					editor = new DefaultCellEditor(jcbox);
					editors.put(row, editor);

				}
				return editor;
			} else {
				return super.getCellEditor(row, column);
			}

		}
		return super.getCellEditor(row, column);
	}
}
