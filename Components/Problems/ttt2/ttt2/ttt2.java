package ttt2;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import core.AbstractMultiProblem;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

/**
 * A very simple tic tac toe 2x2 implementation
 * @author IFeher
 *
 */
public class ttt2 extends AbstractMultiProblem {
	
	private AbstractRepresentation[] nets = new AbstractRepresentation[2];
	private int [][] gamefield = new int [2][2];
	private static final boolean playerblue = false;
	private static final boolean playerred = true;
	public int chres = -2;
	private Board22 board;
	private boolean monitorvisible = false;

	@Override
	public void replayWithVisualization(AbstractRepresentation[] candidates) {
		// TODO Auto-generated method stub
	}
	
	private void newBoard(int game) {
		if (monitorvisible) { //doesnt really work now
			java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	            	board = new Board22(1);
	            }
	    });	
			
		}
		gamefield[0][0] = 0; //TopLeft
		gamefield[0][1] = 0; //TopRight
		gamefield[1][0] = 0; //BottomLeft
		gamefield[1][1] = 0; //BottomRight
	}

	@Override
	public List<RepresentationWithScore> evaluateFitness(AbstractRepresentation[] candidates) {
		//Initialize
		if (candidates.length != 2) throw new Error ("This problem requires exactly 2 candidates to work");
		nets[0] = candidates[0];
		nets[1] = candidates[1];
		newBoard(1);
		
		int [] points = new int [2];
		
		points[0] = 0;
		points[1] = 0;
		
		for(int match=1;match<=2;match++) {	
		
			AbstractRepresentation net1,net2;
			boolean p1,p2;
			
			net1=nets[match-1];
			net2=nets[2-match];
					
			p1=playerblue;
			p2=playerred;
			
			if (match==2) newBoard(match);
			
			net1.reset();
			net2.reset();
			//First round, net1 starts - altogether 4 moves, win is possible after first step
	
			int res=0;
			
			for (int move=0; move<4;move++)
			{
				if (monitorvisible) {
					try {
						
						board.redraw();
						
						Thread.sleep(1000);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (move % 2 == 0)
					getStep(net1,p1);
				else
					getStep(net2,p2);
				if (move > 0) 
					if ((res = checkwinner()) != 0) break;
			}
			
			points[match-1] += res;
			points[2-match] -= res;
			if (board != null) board.dispose();
		}
					
		//we have the results now
		List<RepresentationWithScore> result = new LinkedList<RepresentationWithScore>();
		
		RepresentationWithScore player1 = new RepresentationWithScore(nets[0], 1);
		RepresentationWithScore player2 = new RepresentationWithScore(nets[1], 1);
		
		if (points[0] > points[1]) {
			player2.setScore(-1);
		}
		else if (points[0] < points[1]) {
			player1.setScore(-1);
		}
		
		result.add(player1);
		result.add(player2);
		return result;
	}

	/**
	 * Checks whether one player has won or not
	 * @return 1 of player 1 won, -1 if player 2 won, 0 if still undecided
	 */
	private int checkwinner() {
		int result=0;
		if ((gamefield[0][0] == gamefield[1][1]) && (gamefield[1][1] != 0  )) {
			if (gamefield[0][0] == 1 ) { 
				result = 1;
			}
			else {
				result = -1;
			}
		}
		if ((gamefield[1][0] == gamefield[0][1]) && (gamefield[0][1] != 0  )) {
			if (gamefield[0][1] == 1 ) { 
				result = 1;
			}
			else { 
				result = -1;
			}
		}
		return result;
	}

	private void getStep(AbstractRepresentation net, boolean player) {
		ArrayList<Float> input = createInput(player);
		ArrayList<Float> output = net.getOutput(input);		
		//find highest output
		ArrayList<Integer> steporder = getMaxPos(output);
				
		if (checkZone(steporder.get(0))) {
			stepOnField(steporder.get(0),player);
		}
		else if (checkZone(steporder.get(1))) {
			stepOnField(steporder.get(1),player);
		}
		else if (checkZone(steporder.get(2))) {
			stepOnField(steporder.get(2),player);
		}
		else if (checkZone(steporder.get(3))) {
			stepOnField(steporder.get(3),player);
		}
		else throw new Error ("Cannot find zone to step");				
	}
	
	private void stepOnField(int field,boolean player) {
		int step;
		if (player == playerblue) step = 1; else step = -1;
		
		if (field == 0) {
			addStep (0,0,step);
			//gamefield[0][0] = step;
		}
		else if (field == 1) {
			addStep (0,1,step);
			//gamefield[0][1] = step;
		}
		else if (field == 2) {
			addStep (1,0,step);
			//gamefield[1][0] = step;
		}
		else if (field == 3) {
			addStep (1,1,step);
			//gamefield[1][1] = step;
		}
		else throw new Error ("This shouldnt happpen!");
	}
	
	private void addStep(int a, int b, int step) {
		gamefield[a][b] = step;
		if (monitorvisible) board.addStep(a,b,step);
	}

	private ArrayList<Float> createInput(boolean player) {
		ArrayList<Float> input = new ArrayList<Float>();
		for (int i=0;i<4;i++) {
			if (player == playerblue)
				input.add( (float) getFieldRes(i));
			else
				input.add( (float) -getFieldRes(i));
		}
		return input;
	}
	
	private int getFieldRes (int f) {
		if (f == 0) return gamefield[0][0];
		else if (f == 1) return gamefield[0][1];
		else if (f == 2) return gamefield[1][0];
		else if (f == 3) return gamefield[1][1];
		else throw new Error("Unknown zone");
	}
	
	/**
	 * Returns a vector containing the positions in order
	 * @param v
	 * @return
	 */
	private static ArrayList<Integer> getMaxPos(ArrayList<Float> iv) {
		ArrayList <Integer> ov = new ArrayList<Integer>(); // new vector for the output 
		
		int maxpos = getMaxfromVector(iv);
		ov.add(maxpos);
		iv.set(maxpos, -1200f); //this ensures that this value can not be max again
		maxpos = getMaxfromVector(iv);
		ov.add(maxpos);
		iv.set(maxpos, -1200f); //this ensures that this value can not be max again
		maxpos = getMaxfromVector(iv);
		ov.add(maxpos);
		iv.set(maxpos, -1200f);
		maxpos = getMaxfromVector(iv);
		ov.add(maxpos);
		return ov; 
	}

	private static int getMaxfromVector(ArrayList<Float> iv) {		
		float max = -2000f;
		int maxpos = -1;
		for (int i=0;i<iv.size();i++) {
			if (iv.get(i) >= max  ) {
				max = iv.get(i);
				maxpos = i;
			}
			 
		}
		return maxpos;
	}
	
	/**
	 * Checks whether the given zone is free or not
	 * @param i
	 * @return true if free false otherwise
	 */
	private boolean checkZone(int i) {
		if (i == 0) {
			if (gamefield[0][0] == 0 ) return true;
			return false;
		}
		if (i == 1) {
			if (gamefield[0][1] == 0 ) return true;
			return false;
		}
		if (i == 2) {
			if (gamefield[1][0] == 0 ) return true;
			return false;
		}
		if (i == 3) {
			if (gamefield[1][1] == 0 ) return true;
			return false;
		}
		
		throw new Error("Unknown zone");
	}

	@Override
	public Hashtable<String, XMLFieldEntry> adjustRequirements(
			Hashtable<String, XMLFieldEntry> requirements,
			Hashtable<String, XMLFieldEntry> properties) {
		return requirements;
	}

}
