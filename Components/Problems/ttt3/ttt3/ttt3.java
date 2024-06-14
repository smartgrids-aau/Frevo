package ttt3;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.util.Hashtable;
import java.util.List;

import core.AbstractMultiProblem;
import core.AbstractRepresentation;
import core.XMLFieldEntry;


public class ttt3 extends AbstractMultiProblem {

	@Override
	public void replayWithVisualization(AbstractRepresentation[] candidates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<RepresentationWithScore> evaluateFitness(AbstractRepresentation[] candidates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {
		return requirements;
	}

}
