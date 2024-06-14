/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simsoccer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import net.jodk.lang.FastMath;

import components.simsoccer.model.SimPlayer;

public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int multipl = 5;
	private static final int width = 120;
	private static final int height = 80;
	public Point center;
	private static final int Relwidth = width*multipl;
	private static int Relheight = height*multipl;
	private final Color bgColor = new Color(20,210,10);
	private final Color leftColor = Color.RED;
	private final Color rightColor = Color.BLUE;
	private final Color ballColor = Color.BLACK;
	private final double ballsize = 1.3; //virtual size of the ball, only used for painting not for calculating
	private GCanvas canvas;
	private SimSoccer master;
	
	public Display(SimSoccer master) // constructor
	  {
	    super("Skiinet Simulator");
	    this.master = master;
	    center = new Point ((Relwidth/2)+10,(Relheight/2)+10);
	    setBounds(0,0,Relwidth+20,Relheight+50);// set frame
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    Container con = this.getContentPane(); // inherit main frame
	    con.setBounds(0, 0, Relwidth, Relheight);
	    con.setBackground(bgColor);          // paint background
	    canvas = new GCanvas();     // create drawing canvas
	    
	    addWindowListener(new WindowAdapter() {  // add listener to speed up
			@Override                            // sim once window is closed
			public void windowClosing(WindowEvent we) {
				setWithPause(false);
				super.windowClosing(we);
			}
		});
	    
	    con.add(canvas); setVisible(true);  // add to frame and show
	    this.setLocationRelativeTo(null);

	  }
	
	public void updateDisplay() {
		canvas.repaint();
	}
	
	class GCanvas extends Canvas
	{
		private static final long serialVersionUID = -8748735749891520153L;
		public Point innerOrigo = new Point (10+5*multipl,10+5*multipl); //TOP-LEFT corner of inner rect
		private int innerwidth = Relwidth-(2*(5*multipl));
		private int innerheight = Relheight-2*(5*multipl);
		public Point midpoint = new Point (innerOrigo.x+(innerwidth/2),innerOrigo.y+(innerheight/2)); //center of the field
		public double ballsiz = ballsize*multipl; 
		
		
		public void paint(Graphics g) {
			//draw field acc.
			g.setColor(Color.WHITE);
			g.drawRect(10, 10, Relwidth, Relheight); //outer bound
			g.drawLine(center.x, 10, center.x, Relheight+10); //half line
			g.drawRect(innerOrigo.x,innerOrigo.y,innerwidth,innerheight); //inner bound
			g.drawOval(center.x-8*multipl, center.y-8*multipl, 16*multipl, 16*multipl); //middle 10 circle
			g.drawRect(innerOrigo.x,innerOrigo.y+14*multipl,17*multipl,40*multipl); //left 20
			g.drawRect(innerOrigo.x+innerwidth-17*multipl,innerOrigo.y+70,17*multipl,40*multipl); //right 20
			g.drawRect(innerOrigo.x,innerOrigo.y+25*multipl, 5*multipl, 18*multipl); //goal area left
			g.drawRect(innerOrigo.x+innerwidth-5*multipl, innerOrigo.y+25*multipl, 5*multipl, 18*multipl);
			//draw players
			for (int i=0;i<11;i++) {
				paintObj(g,master.playersinteams[0][i],leftColor); 
				paintObj(g,master.playersinteams[1][i],rightColor);
			}
			
			//draw ball
			paintObj(g,master.simserver.ball.position,ballColor);
			//draw the step number to lower left corner
			g.setColor(Color.BLACK);
			g.drawString("Step: "+master.stepnumber+"/"+Integer.toString(master.aktStep), 15, 405);
			
		}
		/**
		 * Paints a circle to the given coordinates
		 * @param x
		 * @param y
		 * @param c - color of the object
		 */
		private void paintObj(Graphics g, double x, double y, Color c, boolean isPlayer, SimPlayer player) {
			Point correctMid = new Point (midpoint.x+(int)((x)*multipl),midpoint.y+(int)((y)*multipl));
			int a = correctMid.x-(int)(1.414*ballsiz);
			int b = correctMid.y-(int)(1.414*ballsiz);
			Color prev = g.getColor(); //saves actual color
			g.setColor(c);
			g.fillOval(a+2,b+1,(int)(2*ballsiz),(int)(2*ballsiz));
			//draw direction sign
			if (isPlayer) {
				//draw direction line
				g.setColor(Color.YELLOW);
				double direction = player.bodyDirection;
				g.drawLine(correctMid.x, correctMid.y-1, correctMid.x+(int)(ballsiz*FastMath.sin(FastMath.toRadians(direction))), correctMid.y-1+(int)(ballsiz*FastMath.cos(FastMath.toRadians(direction))));
				//draw number
				g.setColor(c);
				g.drawString(Integer.toString(player.getNumber()+1 ), a+2, b+1);
			}
			g.setColor(prev); //resets color
		}
		private void paintObj(Graphics g, Point2D.Double point, Color c) {
			paintObj(g,point.x,point.y,c,false,null);
		}
		private void paintObj(Graphics g, SimPlayer player, Color c) {
			paintObj(g,player.position.x,player.position.y,c,true,player);
		}
	}
	
	public void setWithPause(boolean bool) {
		master.withpause = bool;
	}
}