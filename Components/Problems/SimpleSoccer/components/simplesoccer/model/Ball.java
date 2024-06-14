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

import components.simplesoccer.SimpleServer;

public class Ball extends MobileObject {
	public Ball(Point2D.Double pos) {
		super(pos);
	}
	
	public int getType() {
		return 2;
	}
	
	public void applydecay() {
		this.speedVector = new Point2D.Double (speedVector.x*SimpleServer.BALL_DECAY,speedVector.y*SimpleServer.BALL_DECAY);
	}

	/**
	 * Adds the acceleration vector to the speedvector then normalizes it
	 * @param accvect
	 */
	public void addAccVector(Point2D.Double accvect) {
		Point2D.Double speedvector = new Point2D.Double (this.speedVector.x+accvect.x,this.speedVector.y+accvect.y);
		//normalize
		double length = SimpleServer.getLength(speedvector); 
		if (length > SimpleServer.BALL_SPEED_MAX ) {
			double ratio = (SimpleServer.BALL_SPEED_MAX/length);
			speedvector = new Point2D.Double (speedvector.x*ratio,speedvector.y*ratio);
		}
		this.speedVector = speedvector;
	}
}
