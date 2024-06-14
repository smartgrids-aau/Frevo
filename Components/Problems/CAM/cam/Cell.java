package cam;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
import core.AbstractRepresentation;


public class Cell {

	public AbstractRepresentation representation;
	
	public int color; //current color
	public int nextcolor; //new color 
	public int targetcolor; //index of the intended color in Cam.colArr
	
	public float[] output; //current output info to neighboring cells
	public float[] nextoutput; //new output
	
	public int outputnumber;
	
	/**Constructs a new Cell object.
	 * @param representation Representation object used in this cell
	 * @param colorindex The intended target color
	 * @param outputnumber Number of outputs that this cell generates*/
	public Cell (AbstractRepresentation representation, int colorindex, int outputnumber) {
		this.representation = representation;
		this.targetcolor = colorindex;
		this.outputnumber = outputnumber;
		
		output = new float[outputnumber];		
		nextoutput = new float[outputnumber];
		
		for (int i=0;i<outputnumber;i++) {
			output[i] = -1;
		}
		
		color = -1;
		
	}
	
	public void update()
	{
		color=nextcolor;
		System.arraycopy(nextoutput, 0, output, 0, outputnumber);
	}
}
