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

/** Represents an entry loaded from component XML descriptor files. Each <tt>XMLFieldEntry</tt> contains a variable type, the variable value stored in <tt>String</tt> objects.
 * <p>
 * If the type is an enumeration (<tt>Enum</tt>), a base enum class is also required.
 * @author Istvan Fehervari*/
public class XMLFieldEntry implements Cloneable{

	/** Value of the entry */
	private String value;
	/** Type of the entry */
	private XMLFieldType type;
	/** Name of the base enumeration class. Only needed if type is enum */
	private String enumName = null;
	/** Short description of the entry, used for documentations and tooltip. */
	private String description = null;

	/** Constructs a new entry with the given parameters.
	 * @param value The value of this entry
	 * @param type The type of this entry
	 * @param description A short description of this entry */
	public XMLFieldEntry(String value, XMLFieldType type, String description) {
		this.value = value;
		this.type = type;
		this.description = description;
	}

	/** Constructs a new entry with the given parameters. The entry must be of type enum or and exception is thrown.
	 * @param value The value of this entry
	 * @param type The type of this entry
	 * @param description A short description of this entry
	 * @throws IllegalArgumentException if the type is not enum*/
	public XMLFieldEntry(String value, XMLFieldType type, String enumName, String description) throws IllegalArgumentException {
		// check if type matches
		if (type != XMLFieldType.ENUM) {
			IllegalArgumentException exception = new IllegalArgumentException("XMLFieldEntry instantiated with enumname but not enum type!");
			throw exception;
		}
			
		this.value = value;
		this.type = type;
		this.enumName = enumName;
		this.description = description;
	}

	/** Returns the type of this entry.
	 * @return The type of this entry. */
	public XMLFieldType getType() {
		return type;
	}

	/** Returns the value associated to this entry.
	 * @return the value of this entry. */
	public String getValue() {
		return value;
	}

	/** Sets the value of this entry to a new value.
	 * @param value The value to be used.*/
	public void setValue(String value) {
		this.value = value;
	}

	/** Returns the base enum name used for enumeration types. Throws and exception if this entry is not of type enum.
	 * @return The name of the base enum class of this entry.
	 * @throws IllegalStateException if this entry is not of type enum*/
	public String getEnumName() throws IllegalStateException{
		if (type != XMLFieldType.ENUM) {
			IllegalArgumentException exception = new IllegalArgumentException("This XMLFieldEntry is not of type enum!");
			throw exception;
		}
		return enumName;
	}
	
	/** Returns a short description of this entry.
	 * @return A short description of the entry.*/
	public String getDescription() {
		return description;
	}
	
	/** Creates a deep copy of this object. All fields within will be re-instantiated and their values will be set according to the source object. 
	 * @return a deep copy of this object. */
	public XMLFieldEntry clone() {
		XMLFieldType t = XMLFieldType.valueOf(type.toString());
		XMLFieldEntry clone = new XMLFieldEntry(value, t, description);
		if (this.enumName != null)
			clone.enumName = this.enumName;
		else
			clone.enumName = null;
		
		return clone;
	}
}
