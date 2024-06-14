/*
 * Copyright (C) 2009 Istvan Fehervari
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** FREVO Component Class loader that is used to load components being outside of the normal classpath using their <a href="#name">binary names</a>.
* <h4> <a name="name">Binary names</a> </h4>
*
* <p> Any class name provided as a {@link String} parameter to methods in
* <tt>ClassLoader</tt> must be a binary name as defined by the <a
* href="http://java.sun.com/docs/books/jls/">Java Language Specification</a>.
* 
* @author Istvan Fehervari*/
public class ComponentClassLoader extends ClassLoader {

	/** Reference to the root directory location where the classes to be loaded are. */
	private String root;

	/** Constructs a new <code>ComponentClassLoader</code> that is used to load FREVO components outside of the classpath. 
	 * @param rootdir The root directory where the classes are situated.
	 * @throws IllegalArgumentException if the given <code>rootdir</code> is null. */
	ComponentClassLoader(String rootdir) throws IllegalArgumentException{
		if (rootdir == null)
			throw new IllegalArgumentException("Null root directory");
		root = rootdir;
	}

	/** Loads the class with the specified <a href="#name">binary name</a>.
	 * This method searches for classes in the same manner as the {@link ClassLoader#loadClass(String)} method.
	 *  It is invoked by the Java virtual machine to resolve class references.
	 *  @param name The binary name of the class.
	 *  @param resolve If <tt>true</tt> then resolve the class
	 *  @return The resulting <tt>Class</tt> object.
	 *  @throws ClassNotFoundException If the class was not found*/
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {

		// Is the class loaded yet?
		Class<?> c = findLoadedClass(name);

		if (c == null) {
			// class is not loaded yet
			try {
				// try system class
				c = findSystemClass(name);
				if (c != null) {
					// class has been found
					return c;
				}
			} catch (ClassNotFoundException e) {
				// this is not an error, it just indicates we need to try load it
				//System.err.println("Class not found in system: "+name);
			}

			try {
				// adjust name first
				String[] fullname = name.split("\\.");
				String filename = fullname[fullname.length-1] + ".class";
				byte data[] = loadClassData(filename);

				//first argument is 0 if name is unknown
				c = defineClass(name, data, 0, data.length);
				if (c == null)
					throw new ClassNotFoundException(name);
				
				if (resolve)
					resolveClass (c);
				return c;
				
			} catch (IOException e) {
				System.err.println("No such file found to load class: " + root
						+ name);
				return null;
			}

		} 
		
		// Return loaded class
		return c;
		
	}

	/** Loads the byte data from the given class file.
	 * @return A byte array containing the source data of the give class.
	 * @throws IOException if I/O error occurs. */
	private byte[] loadClassData(String filename) throws IOException {
		// Create a file object relative to directory provided
		File f = new File(root, filename);

		// Get size of class file
		int size = (int) f.length();

		// Reserve space to read
		byte buff[] = new byte[size];

		// Get stream to read from
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);

		// Read in data
		dis.readFully(buff);

		// close stream
		dis.close();

		// return data
		return buff;
	}
}
