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

public class Intention {
	
	public int intId;
	public double param1,param2;
	public String param3s;
	
	public Intention (int id, double p1, double p2, String p3s) {
		this.intId = id;
		this.param1 = p1;
		this.param2 = p2;
		this.param3s = p3s;
	}
}
