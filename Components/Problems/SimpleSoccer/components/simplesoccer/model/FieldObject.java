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

import java.awt.geom.Point2D;


public class FieldObject extends GameObject {

	public Point2D.Double position;
	
	public FieldObject (Point2D.Double pos) {
		this.position = pos;
	}
	
	public Point2D.Double getPosition() {
		return this.position;
	}
	
	public void setPosition (Point2D.Double pos) {
		this.position = pos;
	}
	
}
