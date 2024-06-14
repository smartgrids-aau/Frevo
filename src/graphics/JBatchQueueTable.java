package graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import utils.ScreenCapture;

public class JBatchQueueTable extends JTable {
	private static final long serialVersionUID = 1L;

	private BatchQueueTableModel tablemodel;

	public JBatchQueueTable(BatchQueueTableModel tablemodel) {
		super(tablemodel, null, null);
		this.tablemodel = tablemodel;

		setDefaultRenderer(BatchQueueElement.class, new BatchQueueCell());
		setDefaultEditor(BatchQueueElement.class, new BatchQueueCell());
		setRowHeight(70);
	}

	private class BatchQueueCell extends AbstractCellEditor implements
			TableCellEditor, TableCellRenderer {
		private static final long serialVersionUID = -6000854042679381941L;

		// GUI variables
		private JPanel panel;
		private JLabel propLabel;
		private JPanel fromPanel;
		private JPanel endPanel;
		private JPanel stepCenterPanel;
		private JIntegerTextField fromTextField;
		private JIntegerTextField endTextField;
		private JIntegerTextField stepTextField;
		
		private Color normalOddColor = Color.RED.darker().darker().darker();
		private Color selectedOddColor = Color.RED.darker();
		private Color normalEvenColor = Color.GREEN.darker().darker().darker();
		private Color selectedEvenColor = Color.GREEN.darker();

		private BatchQueueElement element;

		public BatchQueueCell() {
			Color titleColor = Color.WHITE.darker();
			Color textColor = Color.WHITE;
			
			propLabel = new JLabel();

			TitledBorder propBorder = BorderFactory
					.createTitledBorder("Property key");
			propBorder.setTitleColor(titleColor);
			propLabel.setBorder(propBorder);
			propLabel.setPreferredSize(new Dimension(120, 58));
			propLabel.setHorizontalAlignment(SwingConstants.CENTER);
			propLabel.setForeground(textColor);
			panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(propLabel);

			fromPanel = new JPanel();
			TitledBorder fromBorder = BorderFactory
					.createTitledBorder("Starting value");
			fromBorder.setTitleColor(titleColor);
			fromPanel.setBorder(fromBorder);
			fromPanel.setPreferredSize(new Dimension(100, 58));

			fromTextField = new JIntegerTextField();
			fromTextField.setPreferredSize(new Dimension(50, fromTextField
					.getPreferredSize().height));
			fromTextField.setMaximumSize(new Dimension(50, fromTextField
					.getPreferredSize().height));
			fromTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
			fromTextField.getDocument().addDocumentListener(
					new DocumentListener() {

						@Override
						public void removeUpdate(DocumentEvent e) {
							element.setFromValue(fromTextField.getText());
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							element.setFromValue(fromTextField.getText());
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							element.setFromValue(fromTextField.getText());
						}
					});

			fromPanel.add(fromTextField);

			endPanel = new JPanel();
			TitledBorder endBorder = BorderFactory
					.createTitledBorder("End value");
			endBorder.setTitleColor(titleColor);
			endPanel.setBorder(endBorder);
			endPanel.setPreferredSize(new Dimension(90, 58));

			endTextField = new JIntegerTextField();
			endTextField.setPreferredSize(new Dimension(50, endTextField
					.getPreferredSize().height));
			endTextField.setMaximumSize(new Dimension(50, endTextField
					.getPreferredSize().height));
			endTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
			endTextField.getDocument().addDocumentListener(
					new DocumentListener() {

						@Override
						public void removeUpdate(DocumentEvent e) {
							element.setEndValue(endTextField.getText());
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							element.setEndValue(endTextField.getText());
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							element.setEndValue(endTextField.getText());
						}
					});

			endPanel.add(endTextField);

			JPanel stepPanel = new JPanel();

			stepTextField = new JIntegerTextField();
			stepTextField.setPreferredSize(new Dimension(30, stepTextField
					.getPreferredSize().height));
			stepTextField.setMaximumSize(new Dimension(30, stepTextField
					.getPreferredSize().height));
			stepTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
			stepTextField.getDocument().addDocumentListener(
					new DocumentListener() {

						@Override
						public void removeUpdate(DocumentEvent e) {
							element.setStepValue(stepTextField.getText());
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							element.setStepValue(stepTextField.getText());
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							element.setStepValue(stepTextField.getText());
						}
					});

			JButton incButton = new JButton("+");
			incButton.setMinimumSize(new Dimension(
					incButton.getPreferredSize().width, 12));
			incButton.setMaximumSize(new Dimension(
					incButton.getPreferredSize().width, 12));
			incButton.setPreferredSize(new Dimension(incButton
					.getPreferredSize().width, 12));
			incButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			incButton.setFocusable(false);

			incButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int step = stepTextField.getIntegerText();
					step++;
					String stepString = Integer.toString(step);
					stepTextField.setText(stepString);
					element.setStepValue(stepString);
				}
			});

			JButton decButton = new JButton("-");
			decButton.setMinimumSize(new Dimension(
					incButton.getPreferredSize().width, 12));
			decButton.setMaximumSize(new Dimension(
					incButton.getPreferredSize().width, 12));
			decButton.setPreferredSize(new Dimension(incButton
					.getPreferredSize().width, 12));
			decButton.setFocusable(false);

			decButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int step = stepTextField.getIntegerText();
					step--;
					String stepString = Integer.toString(step);
					stepTextField.setText(stepString);
					element.setStepValue(stepString);
				}
			});

			GroupLayout stepLayout = new GroupLayout(stepPanel);
			stepLayout.setHorizontalGroup(stepLayout
					.createSequentialGroup()
					.addComponent(stepTextField)
					.addGroup(
							stepLayout
									.createParallelGroup(
											GroupLayout.Alignment.CENTER)
									.addComponent(incButton)
									.addComponent(decButton)));

			stepLayout.setVerticalGroup(stepLayout
					.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(stepTextField)
					.addGroup(
							stepLayout.createSequentialGroup()
									.addComponent(incButton)
									.addComponent(decButton)));

			stepPanel.setLayout(stepLayout);

			panel.add(fromPanel);
			panel.add(endPanel);

			stepCenterPanel = new JPanel();
			TitledBorder stepBorder = BorderFactory.createTitledBorder("Step");
			stepBorder.setTitleColor(titleColor);
			stepCenterPanel.setBorder(stepBorder);
			stepCenterPanel.add(stepPanel);
			stepCenterPanel.setPreferredSize(new Dimension(stepCenterPanel
					.getPreferredSize().width, 58));
			panel.add(stepCenterPanel);
			
			JButton removeButton = new JButton(ScreenCapture.resizeImage(FrevoWindow.nothingIcon.getImage(),20,20));
			removeButton.setPreferredSize(new Dimension(20,20));
			
			removeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
					tablemodel.removeElement(element);
				}
			});
			
			panel.add(removeButton);
		}

		private void updateData(BatchQueueElement element, boolean isSelected,
				JTable table) {
			this.element = element;
			String text = element.getPropertyKey();
			propLabel.setText(text);
			propLabel.setToolTipText("Property key: " + text);
			fromTextField.setText(element.getStartValue());
			endTextField.setText(element.getEndValue());
			stepTextField.setText(element.getStepValue());

			// re-color based on postion
			int row = tablemodel.getRowIndex(element);
			if (row != -1) {
				Color paintColor;
				if (row % 2 == 0) {
					// even
					if (isSelected)
						paintColor = selectedEvenColor;
					else
						paintColor = normalEvenColor;
				} else {
					// odd
					if (isSelected)
						paintColor = selectedOddColor;
					else
						paintColor = normalOddColor;
				}

				panel.setBackground(paintColor);
				fromPanel.setBackground(paintColor);
				endPanel.setBackground(paintColor);
				stepCenterPanel.setBackground(paintColor);
			}
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			BatchQueueElement elem = (BatchQueueElement) value;
			updateData(elem, true, table);

			return panel;
		}

		public Object getCellEditorValue() {
			return element;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			BatchQueueElement elem = (BatchQueueElement) value;
			updateData(elem, isSelected, table);
			return panel;
		}

	}

}
