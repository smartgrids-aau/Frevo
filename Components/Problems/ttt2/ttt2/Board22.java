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
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JFrame;


public class Board22 extends JFrame {

	private static final long serialVersionUID = -7096757846531908642L;
	private GCanvas canvas;
	private int [][] gamefield = new int [2][2];

	public Board22 (int game) {
		super ("Tic Tac Game "+game);
	    setBounds(0,0,300,320);// set frame
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    Container con = this.getContentPane(); // inherit main frame
	    con.setBounds(0, 0, 300, 300);
	    con.setBackground(Color.WHITE);     // paint background
	    canvas = new GCanvas();     		// create drawing canvas
	    con.add(canvas); setVisible(true);  // add to frame and show
	    this.setLocationRelativeTo(null);
	}
	
	public void redraw() {
		canvas.repaint();
	}
	
	public void addStep(int a, int b, int step) {
		gamefield[a][b] = step;
	}
	
	class GCanvas extends Canvas
	{
		private static final long serialVersionUID = -8748735749891520153L;
		
		public void paint(Graphics g) {
			//draw board lines
			g.setColor(Color.BLACK);
			g.fillRect(this.getWidth()/2, 0, 2, 300);
			g.fillRect(0, this.getHeight()/2, 300, 2);
		
			//draw steps
			for (int i=0;i<2;i++) {
				for (int k = 0; k<2; k++) {
					if (gamefield[i][k] != 0) {
						drawSimb(g,i,k,gamefield[i][k]);
					}
				}
			}
			
		}
		
		private void drawSimb(Graphics g, int i, int k, int res) {
			int x = ((k+1) * this.getWidth()/2) - (this.getWidth()/4);
			int y = ((i+1) * this.getHeight()/2) - (this.getHeight()/4);
			if (res == 1) {
				g.setColor(Color.BLUE);
				g.fillOval(x-20, y-20, 40, 40);
			}
			else if (res == -1) {
				g.setColor(Color.RED);
				g.fillOval(x-20, y-20, 40, 40);
			}
		}
	}
}
