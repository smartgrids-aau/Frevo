/*
 * Copyright (C) 2009 Istvan Fehervari
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

package utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * A small class which creates a new FREVO component and adjusts the Eclipse
 * project settings. Can be called from the FREVO GUI or executed separately. 
 * @author Istvan Fehervari
 */
public class ComponentCreator extends JFrame {

	private static final long serialVersionUID = -8207181728458354833L;

	JLabel typelabel = new JLabel("Component Type:");
	JLabel namelabel = new JLabel("Component Name: (e.g. TICTACTOE)");
	JLabel packagelabel = new JLabel("Specify package (e.g. com.mycomponent)");
	JTextField namefield = new JTextField();
	JTextField packagefield = new JTextField();
	JLabel descriptionlabel = new JLabel("Description");
	JTextArea descriptionArea = new JTextArea();
	JButton createbutton = new JButton("Create");
	String[] typeStrings = { "Problem with absolute fitness",
			"Problem with tournament fitness", "Method", "Representation",
			"Ranking" };
	@SuppressWarnings({ "rawtypes", "unchecked" }) //to avoid warning in Java7
	JComboBox/* <String> */typebox = new JComboBox/* <String> */(typeStrings);

	int valid = 0;
	boolean isnamed = false;
	boolean ispackaged = false;
	boolean isdescribed = false;
	boolean istyped = false;

	public ComponentCreator() {
		super("FREVO Component Creator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(300, 300);
		this.setLocationRelativeTo(null);
		this.setResizable(false);

		typebox.setMaximumSize(new Dimension(250, 20));
		typebox.setSelectedIndex(-1);// select nothing
		typebox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				istyped = true;
				check();
			}
		});

		namefield.setMinimumSize(new Dimension(250, 23));
		namefield.setMaximumSize(new Dimension(250, 23));
		DocumentListener myListener = new CheckListener(2);

		namefield.getDocument().addDocumentListener(myListener);

		packagefield.setMinimumSize(new Dimension(250, 23));
		packagefield.setMaximumSize(new Dimension(250, 23));
		DocumentListener packageListener = new CheckListener(4);

		packagefield.getDocument().addDocumentListener(packageListener);

		descriptionArea.setMinimumSize(new Dimension(250, 100));
		descriptionArea.setMaximumSize(new Dimension(250, 100));
		descriptionArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		descriptionArea.setLineWrap(true);
		// descriptionArea.setText("Enter component description");
		DocumentListener descListener = new CheckListener(3);
		descriptionArea.getDocument().addDocumentListener(descListener);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// frame layout
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(typelabel)
				.addComponent(typebox)
				.addComponent(namelabel)
				.addComponent(namefield)
				.addComponent(packagelabel)
				.addComponent(packagefield)
				.addComponent(descriptionlabel)
				.addComponent(descriptionArea)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(createbutton)));

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(typelabel).addComponent(typebox)
								.addComponent(namelabel)
								.addComponent(namefield)
								.addComponent(packagelabel)
								.addComponent(packagefield)
								.addComponent(descriptionlabel)
								.addComponent(descriptionArea))
				.addGroup(
						layout.createSequentialGroup().addComponent(
								createbutton)));

		createbutton.setEnabled(false);
		pack();

		createbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// create files
				String compname = namefield.getText();
				String dirname, dir = "";
				String packagename = packagefield.getText();
				String packagenamedir = packagename.replace('.', '/');

				int type = typebox.getSelectedIndex();
				File xmlfile = null;
				File classfile = null;

				try {
					// create component directory
					switch (type) {
					case 0: {
						dir = "Components/Problems";
						dirname = "./" + dir + "/" + compname;
						// create component dir
						new File(dirname).mkdir();
						
						// create component package dir
						new File("./" + dir + "/" + compname + "/"
								+ packagenamedir).mkdirs();

						// copy xml file to component dir
						xmlfile = new File("./" + dir + "/" + compname + ".xml");
						copyFile(new File("./src/utils/problemxml.template"),
								xmlfile);

						// copy class file to package dir
						classfile = new File("./" + dir + "/" + compname + "/"
								+ packagenamedir + "/" + compname + ".java");
						copyFile(new File(
								"./src/utils/singleproblemclass.template"),
								classfile);
						break;
					}
					case 1: {
						dir = "Components/Problems";
						dirname = "./" + dir + "/" + compname;
						// create component dir
						new File(dirname).mkdir();
						// create component package dir
						new File("./" + dir + "/" + compname + "/"
								+ packagenamedir).mkdirs();

						// copy xml file to component dir
						xmlfile = new File("./" + dir + "/" + compname + ".xml");
						copyFile(new File("./src/utils/problemxml.template"),
								xmlfile);

						// copy class file to package dir
						classfile = new File("./" + dir + "/" + compname + "/"
								+ packagenamedir + "/" + compname + ".java");
						copyFile(new File(
								"./src/utils/multiproblemclass.template"),
								classfile);
						break;
					}
					case 2: {
						dir = "Components/Methods";
						dirname = "./" + dir + "/" + compname;
						// create component dir
						new File(dirname).mkdir();
						// create component package dir
						new File("./" + dir + "/" + compname + "/"
								+ packagenamedir).mkdirs();

						// copy xml file to component dir
						xmlfile = new File("./" + dir + "/" + compname + ".xml");
						copyFile(new File("./src/utils/methodxml.template"),
								xmlfile);

						// copy class file to package dir
						classfile = new File("./" + dir + "/" + compname + "/"
								+ packagenamedir + "/" + compname + ".java");
						copyFile(new File("./src/utils/methodclass.template"),
								classfile);
						break;
					}
					case 3: {
						dir = "Components/Representations";
						dirname = "./" + dir + "/" + compname;
						// create component dir
						new File(dirname).mkdir();
						// create component package dir
						new File("./" + dir + "/" + compname + "/"
								+ packagenamedir).mkdirs();

						// copy xml file to component dir
						xmlfile = new File("./" + dir + "/" + compname + ".xml");
						copyFile(new File(
								"./src/utils/representationxml.template"),
								xmlfile);

						// copy class file to package dir
						classfile = new File("./" + dir + "/" + compname + "/"
								+ packagenamedir + "/" + compname + ".java");
						copyFile(new File(
								"./src/utils/representationclass.template"),
								classfile);
						break;
					}
					case 4: {
						dir = "Components/Rankings";
						dirname = "./" + dir + "/" + compname;
						// create component dir
						new File(dirname).mkdir();
						// create component package dir
						new File("./" + dir + "/" + compname + "/"
								+ packagenamedir).mkdirs();

						// copy xml file to component dir
						xmlfile = new File("./" + dir + "/" + compname + ".xml");
						copyFile(new File("./src/utils/rankingxml.template"),
								xmlfile);

						// copy class file to package dir
						classfile = new File("./" + dir + "/" + compname + "/"
								+ packagenamedir + "/" + compname + ".java");
						copyFile(new File("./src/utils/rankingclass.template"),
								classfile);
						break;
					}
					}
					// replace TEMP in xml
					BufferedReader reader = new BufferedReader(new FileReader(
							xmlfile));
					String line = "", oldtext = "";
					while ((line = reader.readLine()) != null) {
						oldtext += line + "\r\n";
					}
					reader.close();

					String newtext = oldtext.replaceAll("TEMPCLASSDIR",
							compname + "/" + packagename);
					newtext = newtext.replaceAll("TEMP", compname);
					newtext = newtext.replaceAll("TDESC",
							descriptionArea.getText());
					newtext = newtext.replaceAll("PACKAGE", packagename);

					FileWriter writer = new FileWriter(xmlfile);
					writer.write(newtext);
					writer.close();

					// replace TEMP in class
					reader = new BufferedReader(new FileReader(classfile));
					line = "";
					oldtext = "";
					while ((line = reader.readLine()) != null) {
						oldtext += line + "\r\n";
					}
					reader.close();

					newtext = oldtext.replaceAll("TEMP", compname);
					newtext = newtext.replaceAll("PACKAGENAME", packagename);

					writer = new FileWriter(classfile);
					writer.write(newtext);
					writer.close();

					// Update classpath xml
					File classpath = new File(FrevoMain
							.getInstallDirectory()
							+ File.separator
							+ ".classpath");

					if (classpath.exists()) {
						SAXReader xmlReader = new SAXReader(false);
						Document doc = xmlReader.read(classpath);

						doc.getRootElement().addElement("classpathentry")
								.addAttribute("kind", "src")
								.addAttribute("output", dir + "/" + compname)
								.addAttribute("path", dir + "/" + compname);

						OutputFormat format = OutputFormat.createPrettyPrint();
						format.setLineSeparator(System
								.getProperty("line.separator"));

						FileWriter out = new FileWriter(classpath);
						BufferedWriter bw = new BufferedWriter(out);
						XMLWriter wr = new XMLWriter(bw, format);
						wr.write(doc);
						wr.close();
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// TODO compile newly created classfile

				JOptionPane
						.showMessageDialog(
								ComponentCreator.this,
								"Component "
										+ compname
										+ " has been created! Please refresh your project in Eclipse for the changes to take effect!",
								"Done!", JOptionPane.DEFAULT_OPTION);
				// add to classpath
				ComponentCreator.this.dispose();
			}
		});

	}

	private void check() {
		if ((istyped) && (isnamed) && (isdescribed) && (ispackaged)) {
			createbutton.setEnabled(true);
		} else
			createbutton.setEnabled(false);
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				new ComponentCreator().setVisible(true);
			}
		});
	}

	public void copyFile(File in, File out) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}

	private class CheckListener implements DocumentListener {

		int type;

		public CheckListener(int t) {
			this.type = t;
		}
		
		private void handleDocument(javax.swing.text.Document doc) {
			if (doc.getLength() > 0) {
				if (type == 2)
					isnamed = true;
				else if (type == 3)
					isdescribed = true;
				else if (type == 4)
					ispackaged = true;
				check();
			} else {
				if (type == 2)
					isnamed = false;
				else if (type == 3)
					isdescribed = false;
				else if (type == 4)
					ispackaged = false;
				check();
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			handleDocument(e.getDocument());
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			handleDocument(e.getDocument());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			handleDocument(e.getDocument());
		}

	}
}
