package graphics;

import graphics.FrevoWindow.HashTableModel;
import helper.CMinusLexer;
import helper.CMinusParser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

import core.AbstractRepresentation;

/** Displays the details of the given representation */
public class DetailsFrame extends JFrame {

	private static final long serialVersionUID = -2449990889264554319L;
	
	private JPanel detailsPanel;
	private JTable detailsTable;
	private JScrollPane detailsScrollPane;
	JMenuBar menuBar;
	JMenu menu;
	
	public DetailsFrame(AbstractRepresentation representation) {
		super(representation.getXMLData().getClassName() + " ("
				+ representation.getHash() + ")");
		
		setBounds(0, 0, 100, 300);
		
		this.setLocationRelativeTo(null);
		
		setPreferredSize(new Dimension(500, 500));
		
		detailsPanel = new JPanel();
		detailsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2), "Details of "
						+ representation.getHash()));
		
		HashTableModel tm = new FrevoWindow.HashTableModel(null);
		tm.setTableModel(representation.getDetails());
		tm.fireTableDataChanged();
		
		detailsTable = new JTable(tm);
		detailsScrollPane = new JScrollPane(detailsTable);
		detailsTable.setTableHeader(null);
		
		// sort jtable based on the first column
		detailsTable.setAutoCreateRowSorter(true);
		RowSorter<?> sorter = detailsTable.getRowSorter();
		List<SortKey> sortKeys = new ArrayList<SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		
		//GridLayout detailsLayout = new GridLayout(1, 1);
		//detailsPanel.setLayout(detailsLayout);
		//detailsPanel.add(detailsScrollPane);
		
		//add Thomas his function to translate code into another language
		BorderLayout detailsLayout = new BorderLayout();
		detailsPanel.setLayout(detailsLayout);
		detailsPanel.add(BorderLayout.CENTER,detailsScrollPane);
		
		menuBar = new JMenuBar();
		menu = new JMenu("Export the source code");
		JMenuItem menuItemC = new JMenuItem("C");
		menuItemC.addActionListener(new ActionListener()
		{
			  public void actionPerformed(ActionEvent e)
			  {
				try {
					String content=representation.getC();
			    	if (content==null){
			    		throw new IllegalArgumentException("Failed to generate C source code!");
			    	}
			    	String name=DetailsFrame.getSavePlace();
			    	if (name==null){
			    		return;
			    	}
			    	content=getCPreamble()+content;
			    	PrintWriter	out = new PrintWriter(name);
					out.print(content);
				    out.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		    
			  }
			});
		menu.add(menuItemC);
		for (String language:this.getLanguages()){
			JMenuItem item=new JMenuItem(language);
			item.addActionListener(new ActionListener()
			{
				  public void actionPerformed(ActionEvent e)
				  {
				    String stgName=((JMenuItem)e.getSource()).getText()+".stg";
					PrintWriter out;
					try {
						StringTemplateGroup templates = new StringTemplateGroup(new FileReader(stgName),
							    AngleBracketTemplateLexer.class);
					    String content=representation.getC();
					    if (content==null){
					    	throw new IllegalArgumentException("Failed to generate C source code!");
					    }
					    InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
					    CMinusLexer lexer = new CMinusLexer(new ANTLRInputStream(stream));
						CommonTokenStream tokens = new CommonTokenStream(lexer);
						CMinusParser parser = new CMinusParser(tokens);
						parser.setTemplateLib(templates);
						RuleReturnScope r = parser.program();
						String result=r.getTemplate().toString();
						String name=DetailsFrame.getSavePlace();
						if (name==null){
				    		return;
				    	}
						out = new PrintWriter(name);
						out.print(result);
					    out.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RecognitionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				  }
				});
			menu.add(item);
		}
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		
		this.add(detailsPanel);
		
		this.pack();
	}
	
	private String getCPreamble(){
		String activate="#include <math.h>\n\nclass Result{\npublic:\n  float output[];\n  Result(float outp[], long outputsize){\n    long i;\n	for (i=0L;i<outputsize; i=i+1){\n	  output[i]=outp[i];\n	}\n  }\n};\n\n";
		return activate;
	}
	
	private List<String> getLanguages(){
		List<String> results=new ArrayList<String>();
		File[] files = new File(".").listFiles();
		for (File file : files) {
		    if (file.isFile() && file.getName().endsWith(".stg")) {
		        results.add(file.getName().split("\\.")[0]);
		    }
		}
		return results;
	}
	
	private static String getSavePlace(){
		JFileChooser c = new JFileChooser();
	      // Demonstrate "Open" dialog:
	      int rVal = c.showSaveDialog(null);
	      if (rVal == JFileChooser.APPROVE_OPTION) {
	        return c.getSelectedFile().getAbsolutePath();
	      }
	      else{
	    	  return null;
	      }
	}
}
