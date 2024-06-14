/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import main.FrevoMain;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import utils.NESRandom;
import utils.SafeSAX;

/**
 * Class loading and keeping all data about a single component read from an XML
 * descriptor file. Use this class to load all data about a component before
 * instantiating it.
 * 
 * @author Istvan Fehervari
 */
public class ComponentXMLData {

	/** Holds the root class of this component. */
	protected Class<? extends AbstractComponent> componentClass;
	/** Refers to the name of the root class. */
	private String className;
	/** Path pointing to the directory of the root class. */
	private String classDir;
	/** A short description of this component. */
	private String description;
	/** Path pointing to the location of the reference image of this component. */
	private String imageLocation = null;
	/** Refers to the name of this component. */
	private String name;
	/** Refers to the author of this component. */
	private String author;
	/** Refers to the version of this component. */
	private String version;
	/**
	 * Holds a reference to the root XML file that was used to construct this
	 * object.
	 */
	private File sourceXMLFile;
	/** A list of keywords describing this component. */
	private ArrayList<String> keywords = new ArrayList<String>();

	/**
	 * Indicates if constructing this class (loading all data from XML) happened
	 * successfully. Only used in the constructor.
	 */
	private boolean loadedSuccessfully;
	/** Refers to the type of this component. E.g. problem, method, etc. */
	protected ComponentType componentType;

	/** A map containing the property entries of this component. */
	protected Hashtable<String, XMLFieldEntry> properties = new Hashtable<String, XMLFieldEntry>();

	/**
	 * Constructor of this class. Basically, it loads up all the data from the
	 * given XML file and puts them in the appropriate variables.
	 * 
	 * @param ctype
	 *            The type of this component.
	 * @param xmlfile
	 *            The XML file used to load up data into this class.
	 */
	public ComponentXMLData(ComponentType ctype, File xmlfile) {
		// monitor if loading encounters errors or not
		loadedSuccessfully = true;

		// set the type of this component
		this.componentType = ctype;

		// sets the source of this class
		setSourceXMLFile(xmlfile);

		// load the XML file
		Document doc = SafeSAX.read(xmlfile, true);

		try {
			// Load configuration section
			Node nd = doc.selectSingleNode("/icomponent/config");
			if (nd == null) {
				System.out
						.println("No config tag found - Excluding component at ");
				// configuration tag is required
				loadedSuccessfully = false;

			} else {
				List<?> npops = nd.selectNodes(".//entry");
				Iterator<?> it = npops.iterator();

				while (it.hasNext()) {
					Element el = (Element) it.next();
					String key = el.valueOf("./@key");
					String value = el.valueOf("./@value");

					if (key.equals("classdir")) {
						// get root class directory
						setClassDir(value);
					} else if (key.equals("classname")) {
						// get root class name
						setClassName(value);
					} else if (key.equals("name")) {
						// get component name
						setName(value);
					} else if (key.equals("description")) {
						// get description
						setDescription(value);
					} else if (key.equals("author")) {
						// get author
						setAuthor(value);
					} else if (key.equals("version")) {
						// get version
						setVersion(value);
					} else if (key.equals("image")) {
						// get reference image path
						setImageLocation(value);
					} else if (key.equals("tags")) {
						// read all tags
						ArrayList<String> keywordlist = new ArrayList<String>();
						Scanner sc = new Scanner(value);
						sc.useDelimiter(",");
						while (sc.hasNext())
							keywordlist.add(sc.next());
						setKeywords(keywordlist);
						sc.close();
					} else if (key.equals("isBulkRepresentation")) {
						if (Boolean.parseBoolean(value)) {
							this.componentType = ComponentType.FREVO_BULKREPRESENTATION;
						}
					}
				}

				// Load class file
				String classdir = getClassDir();

				String dir = FrevoMain.getInstallDirectory() + File.separator
						+ "Components" + File.separator
						+ FrevoMain.getComponentTypeAsString(ctype) + "s"
						+ File.separator + classdir + File.separator;

				try {
					// Create a new class loader with the directory
					ComponentClassLoader cl = new ComponentClassLoader(dir);

					// Load in the class
					Class<?> loadedclass = cl.loadClass(getClassName(), true);

					setComponentClass(loadedclass
							.asSubclass(AbstractComponent.class));

				} catch (ClassNotFoundException e) {
					System.err.println("Class not found: " + getClassName());
					// root class is required
					loadedSuccessfully = false;
				} catch (Exception e) {
					// for anything else
					System.err.println("Class unavailable to load: "
							+ getName());
					loadedSuccessfully = false;
				}
			}
			// Load properties section
			Node nd2 = doc.selectSingleNode("/icomponent/properties");
			if (nd2 == null) {
				System.out.println("No properties tag found for component "
						+ getName());

			} else {
				List<?> npops = nd2.selectNodes(".//propentry");
				Iterator<?> it = npops.iterator();

				// create hashmap for the properties
				Hashtable<String, XMLFieldEntry> proptable = new Hashtable<String, XMLFieldEntry>();

				while (it.hasNext()) {
					Element el = (Element) it.next();
					String key = el.valueOf("./@key");
					XMLFieldType type = XMLFieldType.valueOf(el
							.valueOf("./@type"));
					String value = el.valueOf("./@value");

					String description = el.valueOf("./@description");

					if (!FrevoMain.checkType(type, value))
						throw new IllegalArgumentException("Value \"" + value
								+ "\" for Key \"" + key + "\" is not of type "
								+ type + "!");

					if (type.equals(XMLFieldType.ENUM)) {
						String enumName = el.valueOf("./@enumName");
						proptable.put(key, new XMLFieldEntry(value, type,
								enumName, description));
					} else {
						proptable.put(key, new XMLFieldEntry(value, type,
								description));
					}

				}
				// Add hashmap to the object
				setProperties(proptable);
			}

		} catch (OutOfMemoryError mem) {
			System.err.println("Could not import! (Out of memory)");
			loadedSuccessfully = false;
		} catch (IllegalArgumentException e) {
			System.out.println("IllegalArgumentException\n" + e.getMessage());
			loadedSuccessfully = false;
		}

	}

	/**
	 * Indicates if the loading process encountered no errors. If true there
	 * might be required data missing, therefore using this class is not
	 * advised.
	 * 
	 * @return True if component is loaded successfully and ready to be used.
	 */
	public boolean isLoadedSuccessfully() {
		return loadedSuccessfully;
	}

	/**
	 * Returns a map containing the loaded property pairs defined in the XML
	 * file.
	 * 
	 * @return A map holding all properties of this component.
	 */
	public Hashtable<String, XMLFieldEntry> getProperties() {
		return properties;
	}

	/**
	 * Returns the type of this component.
	 * 
	 * @return The type of this component.
	 */
	public ComponentType getComponentType() {
		return this.componentType;
	}

	/**
	 * Sets the properties map to the given value.
	 * 
	 * @param properties
	 *            The new properties map to be used.
	 */
	private void setProperties(Hashtable<String, XMLFieldEntry> properties) {
		this.properties = properties;
	}

	/**
	 * Returns the root class of this component.
	 * 
	 * @return The root class of this component.
	 */
	public Class<? extends AbstractComponent> getComponentClass() {
		return componentClass;
	}

	/**
	 * Sets the root class of this component to the given class.
	 * 
	 * @param componentclass
	 *            The new root class to be used.
	 */
	private void setComponentClass(
			Class<? extends AbstractComponent> componentclass) {
		this.componentClass = componentclass;
	}

	/**
	 * Returns the root class name of this component as it is defined in the XML
	 * file. Typically, it includes the whole name of the class with the package
	 * definition included.
	 * 
	 * @return The name of the root class.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the root class name to the given value.
	 * 
	 * @param The
	 *            new class name to be used.
	 */
	private void setClassName(String classname) {
		this.className = classname;
	}

	/**
	 * Returns the path of base directory of the root class as it is defined in
	 * the XML file.
	 * 
	 * @return The base directory of the root class.
	 */
	public String getClassDir() {
		return classDir;
	}

	/**
	 * Sets the path to the base directory to the given value.
	 * 
	 * @param classdir
	 *            The new path to the base directory to be used.
	 */
	private void setClassDir(String classdir) {
		this.classDir = classdir;
	}

	/**
	 * Returns the description of this problem as it is defined in the XML file.
	 * 
	 * @return A short description of this component.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this component to a new <code>String</code>
	 * value.
	 * 
	 * @param description
	 *            The new description to be used.
	 */
	private void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the author value defined in the source XML file.
	 * 
	 * @return The author of this component.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author of this component to a new <code>String</code> value.
	 * 
	 * @param author
	 *            The new author to be set.
	 */
	private void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns the version of this problem as it is defined in the XML file.
	 * 
	 * @return The version of this component as a <tt>String</tt> object.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version of this component to a new <code>String</code> value.
	 * 
	 * @param version
	 *            The new version to be set.
	 */
	private void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Returns the path to the reference image of this component as it is
	 * defined in the XML file.
	 * 
	 * @return The path to the reference image.
	 */
	public String getImageLocation() {
		return imageLocation;
	}

	/**
	 * Sets the path to the reference image of this component to the given
	 * value.
	 * 
	 * @param imagelocation
	 *            The new location of the reference image.
	 */
	private void setImageLocation(String imagelocation) {
		this.imageLocation = imagelocation;
	}

	/**
	 * Returns a human-readable name of this component as it is defined in the
	 * XML file.
	 * 
	 * @return The name of this component.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name if this component to the given value.
	 * 
	 * @param name
	 *            The name to be used.
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the reference of the source XML file to the given value.
	 * 
	 * @param sourcexml
	 *            The new reference to be used.
	 */
	private void setSourceXMLFile(File sourcexml) {
		this.sourceXMLFile = sourcexml;
	}

	/**
	 * Returns a reference to the source XML file used to construct this
	 * component.
	 * 
	 * @return A reference to the source XML file.
	 */
	public File getSourceXMLFile() {
		return this.sourceXMLFile;
	}

	/**
	 * Returns a new <i>method</i> instance created from this source data.
	 * Throws <code>InstantiationException</code> if this component is not a
	 * method description.
	 * 
	 * @return A new <i>method</i> instance
	 * @throws InstantiationException
	 *             if this component is not a method descriptor
	 */
	public AbstractMethod getNewMethodInstance(NESRandom random)
			throws InstantiationException {
		// check component type
		if (getComponentType() != ComponentType.FREVO_METHOD) {
			throw new InstantiationException();
		}

		// return a new instance
		Constructor<?>[] c = getComponentClass().getConstructors();
		AbstractMethod result;
		try {
			result = (AbstractMethod) c[0].newInstance(random);
			result.setProperties(properties);
			result.setXMLData(this);
			return result;
		} catch (Exception e) {
			throw new InstantiationException();
		}
	}

	/**
	 * Returns a new <i>representation</i> instance created from this source
	 * data. Throws <code>InstantiationException</code> if this component is not
	 * a representation description.
	 * 
	 * @return A new <i>representation</i> instance
	 * @throws InstantiationException
	 *             if this component is not a representation descriptor
	 */
	public AbstractRepresentation getNewRepresentationInstance(int inputnumber,
			int outputnumber, NESRandom random) throws IllegalAccessException,
			InvocationTargetException, InstantiationException {
		// check component type
		if ((getComponentType() != ComponentType.FREVO_REPRESENTATION)
				&& (getComponentType() != ComponentType.FREVO_BULKREPRESENTATION)) {
			throw new InstantiationException();
		}

		// try to run static initializer
		try {
			Method initializer = getComponentClass().getDeclaredMethod(
					"initialize", Integer.TYPE, Integer.TYPE, Hashtable.class,
					Random.class);
			initializer.setAccessible(true);
			initializer.invoke(null, inputnumber, outputnumber, properties,
					random);
		} catch (SecurityException e1) {
			System.err
					.println("Warning: Cannot run static initializer for class "
							+ getClassName());
		} catch (NoSuchMethodException e1) {
			// ignore
		}

		// return a new instance
		Constructor<?>[] c = getComponentClass().getConstructors();
		AbstractRepresentation result;

		try {
			result = null;
			if (getComponentType() == ComponentType.FREVO_REPRESENTATION) {
				for (Constructor<?> constructor : c) {
					try {
						result = (AbstractRepresentation) constructor
								.newInstance(inputnumber, outputnumber, random,
										getProperties());
						break;
					} catch (Exception ex) {
						// nothing to do here because we have to check out all
						// constructors
					}
				}
			} else {
				for (Constructor<?> constructor : c) {
					try {
						result = (AbstractBulkRepresentation) constructor
								.newInstance(inputnumber, outputnumber, random,
										getProperties());
						break;
					} catch (Exception ex) {
						// nothing to do here because we have to check out all
						// constructors
					}
				}
			}

			if (result == null) {
				throw new IllegalArgumentException(
						"There is no suitable constructor to create representation");
			}

			result.setProperties(properties);
			result.setXMLData(this);
			return result;
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}

	/**
	 * Returns a new <i>ranking</i> instance created from this source data.
	 * Throws <code>InstantiationException</code> if this component is not a
	 * ranking description.
	 * 
	 * @return A new <i>ranking</i> instance
	 * @throws InstantiationException
	 *             if this component is not a ranking descriptor
	 */
	public AbstractRanking getNewRankingInstance()
			throws InstantiationException {
		// check component type
		if (getComponentType() != ComponentType.FREVO_RANKING) {
			throw new InstantiationException();
		}

		// return a new instance
		Constructor<?> c;
		AbstractRanking result;
		try {
			c = getComponentClass().getDeclaredConstructor(Hashtable.class);
			result = (AbstractRanking) c.newInstance(properties);
			result.setXMLData(this);

		} catch (Exception itx) {
			throw new InstantiationException(itx.getMessage());
		}

		return result;
	}

	/**
	 * Returns the type of the given property key.
	 * 
	 * @param key
	 *            The property key whose type is requested.
	 * @return A reference to the property type stored within this component.
	 */
	public XMLFieldType getTypeOfProperty(String key) {
		XMLFieldEntry entr = this.properties.get(key);
		if (entr == null)
			throw new IllegalArgumentException("Key " + key + " not found!");
		return entr.getType();
	}

	/**
	 * Returns the value assigned to the given property key.
	 * 
	 * @param key
	 *            The property key whose assigned value is requested.
	 * @return A reference to the <code>String</code> value assigned to the
	 *         given property key.
	 */
	public String getValueOfProperty(String key) {
		XMLFieldEntry entr = this.properties.get(key);
		if (entr == null)
			throw new IllegalArgumentException("Key " + key + " not found!");
		return entr.getValue();
	}

	/**
	 * Returns a reference to a list containing the keywords as defined in the
	 * source XML file.
	 * 
	 * @return A list of keywords.
	 */
	public ArrayList<String> getKeywords() {
		return keywords;
	}

	/**
	 * Sets the list of keywords to the given reference.
	 * 
	 * @param keywords
	 *            The new list of keywords to be used.
	 */
	private void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * Returns a <code>String</code> representation of this component.
	 * 
	 * @return A <code>String</code> representation of this component.
	 */
	public String toString() {
		return getName();
	}
}
