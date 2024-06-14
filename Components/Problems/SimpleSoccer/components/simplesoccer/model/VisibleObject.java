/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simplesoccer.model;

public class VisibleObject {

	/** Returns 0 if the team of this player equals with the team of the observer, 1 if not, and -1 if not player */
	public int team = -1;
	
	/** Returns the distance to this object */
	public double distance;
	/** Returns the relative direction to this object in degrees */
	public double direction;
	
	public VisibleObject() {}
}
