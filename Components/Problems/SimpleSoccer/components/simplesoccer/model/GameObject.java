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

public class GameObject {
	
	public double distance;
	//public double direction;
	
	public double getDistance() {
		return this.distance;
	}
	
	
	/*public double getDirection() {
		return this.direction;
	}*/
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	/*public void setDirection(double direction) {
		this.direction = direction;
	}*/
	
	/**
	 * Returns the type of the object
	 * @return 0 if undefined, 1 if player, 2 if ball, 3 if line, 4 if goal
	 */
	public int getType() {
		return 0;
	}
}
