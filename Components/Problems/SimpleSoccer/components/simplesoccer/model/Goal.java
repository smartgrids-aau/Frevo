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


public class Goal extends FieldObject {

	public int side;
	
	public Goal (Point2D.Double pos, int side) {
		super(pos);
		this.side = side;
	}
	
	public int getType() {
		return 4;
	}
	
	public int getSide() {
		return side;
	}
}
