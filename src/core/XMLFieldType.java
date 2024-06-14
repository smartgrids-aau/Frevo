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

/** Enumeration of possible types of variables defined in properties and requirements.
 * @author Istvan Fehervari*/
public enum XMLFieldType {

	/** Array of integers separated with the comma character. E.g. 1,2,3*/
	INTARRAY,
	/** String value*/
	STRING,
	/** Integer value*/
	INT,
	/** Long integer value*/
	LONG,
	/** Float value*/
	FLOAT,
	/** Boolean value*/
	BOOLEAN,
	/** Enumeration provided by a class*/
	ENUM,
	/** Path to a file*/
	FILE;
}
