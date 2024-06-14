/*
 * Copyright (C) 2009 Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

package utils;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * This class is an extension of the original {@link org.dom4j.io.SAXReader}
 * with a safe <tt>dtd</tt> resolver. Silences missing dtd definition errors
 * during document loading.
 * 
 * The SafeSAX class serves as a "serial access" protocol for XML by
 * providing a static <b>read/1</b> method to load a given XML document.
 * If XML could not be loaded, it tries to load the XML document
 * with a generic resolver.
 * <p>
 * About SAX protocol:<br>
 * {@link http://www2.informatik.hu-berlin.de/~xing/Lib/Docs/jaxp/docs/tutorial/sax/index.html}
 * <p>
 * For further information about Java XML API, please visit:<br>
 * {@link http://www2.informatik.hu-berlin.de/~xing/Lib/Docs/jaxp/docs/tutorial/TOC.html#intro}
 * 
 * @author Wilfried Elmenreich
 */
public class SafeSAX {

	/**
	 * Ignorant implementation of a dtd resolver, does not try to read a dtd but
	 * delivers a generic xml dtd description instead
	 */
	static EntityResolver resolver = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new ByteArrayInputStream(
					"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
		}
	};

	/**
	 * Creates a new SAXReader and tries to load the given file into an XML
	 * document. In case the dtd could not be loaded, the process is restarted
	 * using a generic resolver
	 * 
	 * @param loadFile
	 *            the file to be loaded from
	 * @param reportError if false, errors will be swallowed
	 * @return the XML document
	 */
	public static Document read(File loadFile, boolean reportError) {

		Document doc = null;
		boolean error = false;

		try {
			SAXReader xmlReader = new SAXReader(false);
			doc = xmlReader.read(loadFile);
		} catch (OutOfMemoryError mem) {
			System.err.println("Could not import! (Out of memory)");
		} catch (DocumentException e) {
			// System.err.println("Document type description could not be loaded (probably wrong path) - don't worry, using a generic dtd...");
			error = true;
		}

		if (error) {
			try {
				SAXReader xmlReader = new SAXReader(false);
				xmlReader.setEntityResolver(resolver);
				doc = xmlReader.read(loadFile);
			} catch (OutOfMemoryError mem) {
				System.err.println("Could not import! (Out of memory)");
			} catch (DocumentException e) {
				if (reportError) {
					String msg = e.getMessage();
					System.err
							.println("Error while import: DocumentException\n"
									+ msg);	
				}
				error = true;
			}
		}

		return doc;
	}
}
