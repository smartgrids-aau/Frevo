package graphics;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class BatchQueueTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<BatchQueueElement> elements = new ArrayList<BatchQueueElement>();
	
	@Override
	public int getRowCount() {
		return elements.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}
	
	public String getColumnName(int column) {
		return "Batch Queue";
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return BatchQueueElement.class;
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		if (row >= elements.size()) {
			//add new
			elements.add((BatchQueueElement)value);
		} else {
			elements.set(row, (BatchQueueElement)value);
		}
		fireTableCellUpdated(row, column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return elements.get(rowIndex);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void addNewElement(BatchQueueElement element) {
		setValueAt(element, elements.size(), 0);
		fireTableRowsInserted(0, elements.size());
	}
	
	public int getRowIndex(BatchQueueElement element) {
		return elements.indexOf(element);
	}

	public void removeElement(BatchQueueElement element) {
		elements.remove(element);
		fireTableDataChanged();
	}
	
	public BatchQueueElement removeElement(int index) {
		BatchQueueElement element = elements.get(index);
		removeElement(element);
		return element;
	}
	
	public void removeAllElements() {
		elements.clear();
		fireTableDataChanged();
	}

}
