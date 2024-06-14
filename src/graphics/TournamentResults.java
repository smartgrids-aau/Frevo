package graphics;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import core.AbstractRepresentation;
import core.AbstractMultiProblem.RepresentationWithScore;

public class TournamentResults extends JFrame implements ActionListener { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6470356894697685974L;
	
	ArrayList<RepresentationWithScore> results;
	JTable resultsTable;
	JButton saveButton;
	
	public TournamentResults(ArrayList<RepresentationWithScore> list) {
		super("Tournament Results");
		
		setBounds(0, 0, 100, 100);
		
		this.setLocationRelativeTo(null);
		
		setPreferredSize(new Dimension(260, 200));
		
		results = list;
		
		// data for the table
		Object[][] data = new Object[list.size()][3];
		
		for (int u = 0; u < list.size(); u++) {
			// order number of the representation in population
			data[u][0] = u;
			// hash of the candidate
			data[u][1] = list.get(u).getRepresentation().getHash();
			// scores 
			data[u][2] = list.get(u).getScore();
			list.get(u).setScore(u);			
		}
		
		// names of the columns for the table
		Object[] names = new Object[3];
		names[0] = "#";
		names[1] = "Hash";
		names[2] = "Score";

		resultsTable = new JTable(data, names);
		JPanel panel = new JPanel();
		
		resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		panel.add(scrollPane);
		GridLayout paneLayout = new GridLayout(2, 1);
		panel.setLayout(paneLayout);
		
		saveButton = new JButton("Save");
		panel.add(saveButton);
		saveButton.addActionListener(this);
		
		this.add(panel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (saveButton == source) {
			// get selected representations
			ArrayList<AbstractRepresentation> candidates = new ArrayList<AbstractRepresentation>();
			
			ListSelectionModel lsm = resultsTable.getSelectionModel();
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();
			for (int i = minIndex; i <= maxIndex; i++) {
				if (lsm.isSelectedIndex(i)) {
					candidates.add(results.get(i).getRepresentation());
				}
			}
			
			Document doc = DocumentHelper.createDocument();
			Element cnetwork = doc.addElement("CompleteNetwork");
			
			for (int i=0; i<candidates.size(); i++) {
				candidates.get(i).exportToXmlElement(cnetwork);
			}
			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setLineSeparator(System.getProperty("line.separator"));

			File saveFile = new File("C:\\hoho.xml");
			try {
				saveFile.createNewFile();
				FileWriter out = new FileWriter(saveFile);
				BufferedWriter bw = new BufferedWriter(out);
				XMLWriter wr = new XMLWriter(bw, format);
				wr.write(doc);
				wr.close();				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
