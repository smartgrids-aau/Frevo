/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich, Tobias Ibounig
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simplesoccer;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import utils.ScreenCapture;
import main.FrevoMain;
import net.jodk.lang.FastMath;
import components.simplesoccer.model.SimPlayer;

public class SimpleDisplay extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int multipl = 9;
	private static final int width = 120;
	private static final int height = 80;
	public Point center;
	private static final int Relwidth = width * multipl;
	private static int Relheight = height * multipl;
	// the coefficient we use to scale our images (flags, ball) 
	private static final float imageScale = 227.027f;
	private final Color bgColor = new Color(20, 210, 10);
	private final Color bgColor2 = new Color(30, 220, 20);
	private final Color leftColor = Color.RED;
	private final Color rightColor = Color.BLUE;
	private final double player_virt_size = 1.3; // virtual size of the player, only used
											// for painting not for calculating
	private final double ball_virt_size = 0.7; // virtual size of the ball, only used
											// for painting not for calculating
	private GCanvas canvas;
	private SimpleSoccer master;
	public boolean saving;	
	
	private JPanel menuPanel;
	protected JButton startButton;
	protected JButton stopButton;
	private Icon playIcon;
	private Icon stopIcon;
	protected JCheckBox saveFramesCheckbox;
	protected JComboBox<String> leftTeamComboBox;
	protected JComboBox<String> rightTeamComboBox; 
	
	private static final String RED_THEME = "Red";
	private static final String BLUE_THEME = "Blue";
	private static final String GERMANY_THEME = "Germany";
	private static final String BRAZIL_THEME = "Brazil";
	private static final String SPAIN_THEME = "Spain";
	private static final String ITALY_THEME = "Italy";
	private static final String ENGLAND_THEME = "England";
	private static final String ARGENTINA_THEME = "Argentina";
	private static final String GREECE_THEME = "Greece";
	private static final String FRANCE_THEME = "France";
	private static final String PORTUGAL_THEME = "Portugal";
	private static final String CROATIA_THEME = "Croatia";
	private static final String MEXICO_THEME = "Mexico";
	private static final String CAMEROON_THEME = "Cameroon";
	private static final String NETHERLANDS_THEME = "Netherlands";
	private static final String CHILE_THEME = "Chile";
	private static final String AUSTRALIA_THEME = "Australia";
	private static final String COLOMBIA_THEME = "Colombia";
	private static final String COSTA_RICA_THEME = "Costa Rica";
	private static final String IVORY_COAST_THEME = "Ivory Coast";
	private static final String RUSSIA_THEME = "Russia";
	private static final String KOREA_THEME = "South Korea";
	private static final String URUGUAY_THEME = "Uruguay";
	private static final String SWITZERLAND_THEME = "Switzerland";
	private static final String GHANA_THEME = "Ghana";
	private static final String USA_THEME = "USA";
	private static final String HONDURAS_THEME = "Honduras";
	private static final String BELGIUM_THEME = "Belgium";
	private static final String ALGERIA_THEME = "Algeria";
	private static final String BOSNIA_THEME = "Bosnia-Herzegovina";
	private static final String JAPAN_THEME = "Japan";
	private static final String EQUADOR_THEME = "Equador";
	private static final String IRAN_THEME = "Iran";
	private static final String NIGERIA_THEME = "Nigeria";


	private Image ballImage;
	private Image germanyImage;
	private Image brazilImage;
	private Image spainImage;
	private Image italyImage;
	private Image englandImage;
	private Image argentinaImage;
	private Image greeceImage;
	private Image franceImage;
	private Image portugalImage;
	private Image croatiaImage;
	private Image mexicoImage;
	private Image cameroonImage;
	private Image netherlandsImage;
	private Image chileImage;
	private Image australiaImage;
	private Image colombiaImage;
	private Image costaRicaImage;
	private Image ivoryCoastImage;
	private Image russiaImage;
	private Image koreaImage;
	private Image uruguayImage;
	private Image switzerlandImage;
	private Image ghanaImage;
	private Image usaImage;
	private Image hondurasImage;
	private Image belgiumImage;
	private Image algeriaImage;
	private Image bosniaImage;
	private Image equadorImage;
	private Image japanImage;
	private Image iranImage;
	private Image nigeriaImage;
	
	private DecimalFormat format = new DecimalFormat("##.00");
	
	public SimpleDisplay(SimpleSoccer master) // constructor
	{
		super("Skiinet Simulator");
		this.master = master;
		center = new Point((Relwidth / 2) + 10, (Relheight / 2) + 10);
		setBounds(0, 0, Relwidth + 50, Relheight + 140);// set frame
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container con = this.getContentPane(); // inherit main frame
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
		
		// Create Menu frame
		menuPanel = new JPanel();
		//menuPanel.setPreferredSize(new Dimension(300, 50));
		menuPanel.setMinimumSize(new Dimension(300, 50));
		//menuPanel.setMaximumSize(new Dimension(300, 50));
		menuPanel.setBorder(new TitledBorder("Control"));
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
		
		startButton = new JButton();
		startButton.setSize(30, 25);
		menuPanel.add(startButton);

		stopButton = new JButton();
		stopButton.setSize(30, 25);
		stopButton.setEnabled(false);
		menuPanel.add(stopButton);
		
		saveFramesCheckbox = new JCheckBox("Save frames");
		saveFramesCheckbox.setEnabled(true);
		menuPanel.add(saveFramesCheckbox);

		String[] leftThemes = { RED_THEME, GERMANY_THEME, BRAZIL_THEME, SPAIN_THEME,
				ITALY_THEME, ENGLAND_THEME, ARGENTINA_THEME, FRANCE_THEME, PORTUGAL_THEME, GREECE_THEME,
				CROATIA_THEME, MEXICO_THEME, CAMEROON_THEME,
				NETHERLANDS_THEME, CHILE_THEME, AUSTRALIA_THEME, COLOMBIA_THEME, COSTA_RICA_THEME, IVORY_COAST_THEME,
				RUSSIA_THEME, KOREA_THEME, URUGUAY_THEME, SWITZERLAND_THEME, GHANA_THEME, USA_THEME, HONDURAS_THEME,
				BELGIUM_THEME, ALGERIA_THEME, BOSNIA_THEME,
				EQUADOR_THEME, JAPAN_THEME, IRAN_THEME, NIGERIA_THEME};
		leftTeamComboBox = new JComboBox<String>(leftThemes);
		leftTeamComboBox.setMaximumSize(new Dimension(400, 20));
		menuPanel.add(leftTeamComboBox);
		
		String[] rightThemes = { BLUE_THEME, GERMANY_THEME, BRAZIL_THEME, SPAIN_THEME, 
				ITALY_THEME, ENGLAND_THEME, ARGENTINA_THEME, FRANCE_THEME, PORTUGAL_THEME, GREECE_THEME,
				CROATIA_THEME, MEXICO_THEME, CAMEROON_THEME,
				NETHERLANDS_THEME, CHILE_THEME, AUSTRALIA_THEME, COLOMBIA_THEME, COSTA_RICA_THEME, IVORY_COAST_THEME,
				RUSSIA_THEME, KOREA_THEME, URUGUAY_THEME, SWITZERLAND_THEME, GHANA_THEME, USA_THEME, HONDURAS_THEME,
				BELGIUM_THEME, ALGERIA_THEME, BOSNIA_THEME,
				EQUADOR_THEME, JAPAN_THEME, IRAN_THEME, NIGERIA_THEME};
		rightTeamComboBox = new JComboBox<String>(rightThemes);
		rightTeamComboBox.setMaximumSize(new Dimension(400, 20));
		menuPanel.add(rightTeamComboBox);
		
		try {
			playIcon = new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/media/Play24.gif"));
			stopIcon = new ImageIcon(new URL("jar:file:"
					+ FrevoMain.getInstallDirectory()
					+ "/Libraries/jlfgr/jlfgr-1_0.jar/!" + "/"
					+ "toolbarButtonGraphics/media/Stop24.gif"));
			startButton.setIcon(playIcon);
			stopButton.setIcon(stopIcon);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		setRun(false);
		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				saveFramesCheckbox.setEnabled(false);				
				setRun(true);		
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				saveFramesCheckbox.setEnabled(true);
				setRun(false);
			}
		});
		
		addWindowListener(new WindowAdapter() {  // add listener to speed up
			@Override                            // sim once window is closed
			public void windowClosing(WindowEvent we) {
				setWithPause(false);
				super.windowClosing(we);
			}
		});
		
		con.add(menuPanel);

		canvas = new GCanvas(); // create drawing canvas
		canvas.setBackground(bgColor);
		con.add(canvas); // add to frame
		
		String pathToLibrary = "jar:file:" + FrevoMain.getInstallDirectory() + "/Libraries/Categories/Problem/SimpleSoccer.jar/!" + "/";
		
		try {
				// flag of Germany
				BufferedImage img = ImageIO.read(new URL(pathToLibrary + "germany.png"));
				int width =  (int)(multipl * img.getWidth() / imageScale);
				int height = (int)(multipl * img.getHeight() / imageScale);
				germanyImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of Brazil
				img = ImageIO.read(new URL(pathToLibrary + "brazil.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				brazilImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of Spain
				img = ImageIO.read(new URL(pathToLibrary + "spain.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				spainImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of Italy
				img = ImageIO.read(new URL(pathToLibrary + "italy.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				italyImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of England
				img = ImageIO.read(new URL(pathToLibrary + "england.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				englandImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of argentina
				img = ImageIO.read(new URL(pathToLibrary + "argentina.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				argentinaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of greece
				img = ImageIO.read(new URL(pathToLibrary + "greece.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				greeceImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of france
				img = ImageIO.read(new URL(pathToLibrary + "france.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				franceImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of portugal
				img = ImageIO.read(new URL(pathToLibrary + "portugal.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				portugalImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);

				
				// flag of croatia 
				img = ImageIO.read(new URL(pathToLibrary + "croatia.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				croatiaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of mexico 
				img = ImageIO.read(new URL(pathToLibrary + "mexico.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				mexicoImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);

				// flag of cameroon 
				img = ImageIO.read(new URL(pathToLibrary + "cameroon.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				cameroonImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of netherlands 
				img = ImageIO.read(new URL(pathToLibrary + "netherlands.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				netherlandsImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of chile 
				img = ImageIO.read(new URL(pathToLibrary + "chile.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				chileImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of australia 
				img = ImageIO.read(new URL(pathToLibrary + "australia.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				australiaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of colombia
				img = ImageIO.read(new URL(pathToLibrary + "columbia.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				colombiaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of costa rica
				img = ImageIO.read(new URL(pathToLibrary + "costarica.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				costaRicaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of ivory coast
				img = ImageIO.read(new URL(pathToLibrary + "ivorycoast.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				ivoryCoastImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of russia
				img = ImageIO.read(new URL(pathToLibrary + "russia.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				russiaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);

				// flag of korea
				img = ImageIO.read(new URL(pathToLibrary + "korea.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				koreaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);

				// flag of uruguay
				img = ImageIO.read(new URL(pathToLibrary + "uruguay.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				uruguayImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of switzerland
				img = ImageIO.read(new URL(pathToLibrary + "switzerland.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				switzerlandImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);

				// flag of ghana
				img = ImageIO.read(new URL(pathToLibrary + "ghana.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				ghanaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);


				// flag of usa
				img = ImageIO.read(new URL(pathToLibrary + "united_states.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				usaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of honduras
				img = ImageIO.read(new URL(pathToLibrary + "honduras.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				hondurasImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);

				// flag of belgium
				img = ImageIO.read(new URL(pathToLibrary + "belgium.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				belgiumImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of algeria
				img = ImageIO.read(new URL(pathToLibrary + "algeria.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				algeriaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of bosnia
				img = ImageIO.read(new URL(pathToLibrary + "bosnia.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				bosniaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of japan
				img = ImageIO.read(new URL(pathToLibrary + "japan.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				japanImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of equador
				img = ImageIO.read(new URL(pathToLibrary + "ecuador.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				equadorImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of iran
				img = ImageIO.read(new URL(pathToLibrary + "iran.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				iranImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// flag of nigeria
				img = ImageIO.read(new URL(pathToLibrary + "nigeria.png"));
				width =  (int)(multipl * img.getWidth() / imageScale);
				height = (int)(multipl * img.getHeight() / imageScale);
				nigeriaImage = img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);
				
				// image of the ball
				img = ImageIO.read(new URL(pathToLibrary + "soccer_ball_high.png"));
				width =  (int)(multipl * ball_virt_size * img.getWidth()  / imageScale);
				height = (int)(multipl * ball_virt_size * img.getHeight() / imageScale);
				ballImage =  img.getScaledInstance( width , height, BufferedImage.SCALE_DEFAULT);

		 	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		setVisible(true); 
		this.setLocationRelativeTo(null);
	}

	private void setRun(boolean pause) {
		master.runSimulation = pause;
	}

	public void updateDisplay() {
		canvas.repaint();				
	}
	
	private void saveFrame() {
		try {
			ScreenCapture.createImage(canvas, "./Images/Frame_" + String.format("%04d", master.aktStep)  + ".bmp");
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/*
	 // unfortunately, saves a sequence of images with gaps
	 private void saveFramePNG() {
		try {
			BufferedImage img = ScreenCapture.createImage(canvas, null);
			boolean haha = ImageIO.write(img, "PNG", new File("./Images/Frame_" + String.format("%04d", master.aktStep)  + ".png"));
			System.out.println("./Images/Frame_" + String.format("%04d", master.aktStep)  + ".png " + Boolean.toString(haha) );
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}*/

	class GCanvas extends Canvas {
		private static final long serialVersionUID = -8748735749891520153L;
		public Point innerOrigo = new Point(10 + 5 * multipl, 10 + 5 * multipl); // TOP-LEFT
																					// corner
																					// of
																					// inner
																					// rect
		private int innerwidth = Relwidth - (2 * (5 * multipl));
		private int innerheight = Relheight - 2 * (5 * multipl);
		public Point midpoint = new Point(innerOrigo.x + (innerwidth / 2),
				innerOrigo.y + (innerheight / 2)); // center of the field
		public double player_size = player_virt_size * multipl;
		public double ball_size = ball_virt_size * multipl;

		// implements double buffering to avoid flickers
		public void update(Graphics g) {
			Graphics offgc;
			Image offscreen = null;
			Dimension d = getSize();

			// create the offscreen buffer and associated Graphics
			offscreen = createImage(d.width, d.height);
			offgc = offscreen.getGraphics();
			// clear the exposed area
			offgc.setColor(getBackground());
			offgc.fillRect(0, 0, d.width, d.height);
			offgc.setColor(getForeground());
			// do normal redraw
			paint(offgc);
			// transfer offscreen to window
			g.drawImage(offscreen, 0, 0, this);
			
			master.withsave = saveFramesCheckbox.isSelected();
			if (master.withsave) {
				//saveFrame();
				saving = true;
				saveFrame();
				saving = false;
			}
		}

		public void paint(Graphics g) {
	
			g.setColor(bgColor2);
			int currentStrip = 10;
			int stripWidth = Relwidth/20;
			for (int i=0; i< 10; i++) {
				g.fillRect( currentStrip, 10, stripWidth, Relheight);
				currentStrip += 2* stripWidth;
			}
			
			// draw field acc.
			g.setColor(Color.WHITE);
			
			g.drawRect(10, 10, Relwidth, Relheight); // outer bound
			g.drawLine(center.x, 10, center.x, Relheight + 10); // half line
			g.drawRect(innerOrigo.x, innerOrigo.y, innerwidth, innerheight); // inner
																				// bound
			g.drawOval(center.x - 8 * multipl, center.y - 8 * multipl,
					16 * multipl, 16 * multipl); // middle 10 circle
			g.drawRect(innerOrigo.x, innerOrigo.y + 14 * multipl, 17 * multipl,
					40 * multipl); // left 20
			g.drawRect(innerOrigo.x + innerwidth - 17 * multipl,
					innerOrigo.y + 14 * multipl, 17 * multipl, 40 * multipl); // right 20
			g.drawRect(innerOrigo.x, innerOrigo.y + 25 * multipl, 5 * multipl,
					18 * multipl); // goal area left
			g.drawRect(innerOrigo.x + innerwidth - 5 * multipl, innerOrigo.y
					+ 25 * multipl, 5 * multipl, 18 * multipl);
			// draw players
			for (int i = 0; i < SimpleSoccer.PLAYERS_PER_TEAM; i++) {
				paintPlayer(g, master.playersinteams[0][i]);
				paintPlayer(g, master.playersinteams[1][i]);
			}

			// draw ball
			paintBall(g, master.simpleserver.ball.position);
			//paintObj(g, master.simpleserver.ball.position, ballColor);
			// draw the step number to lower left corner
			g.setColor(Color.BLACK);
			/*g.drawString(
					"Step: " + master.stepnumber + "/"
							+ Integer.toString(master.aktStep), 15, Relheight + 30);*/
			g.drawString(
					"Time: " + format.format((float)master.aktStep/10), 15, Relheight + 30);
		}

		private void paintBall(Graphics g, Point2D.Double point) {
			Point correctMid = new Point(midpoint.x + (int) ((point.x) * multipl),
					midpoint.y + (int) ((point.y) * multipl));
			int a = correctMid.x - (int) ( ball_size);
			int b = correctMid.y - (int) ( ball_size);
			Color prev = g.getColor(); // saves actual color
			
			g.drawImage(ballImage,a, b,null);
			
			//g.setColor(Color.GRAY);
			//g.drawOval(a, b, (int) (2 * ball_size), (int) (2 * ball_size));
						
			g.setColor(prev); // resets color	
		}
		
		private void paintPlayer(Graphics g, SimPlayer player) {
			Point correctMid = new Point(midpoint.x + (int) ((player.position.x) * multipl),
					midpoint.y + (int) ((player.position.y) * multipl));
			int a = correctMid.x - (int) (player_size);
			int b = correctMid.y - (int) (player_size);
			Color prev = g.getColor(); // saves actual color
			
			int team = player.side;
			
			// get theme from an appropriate combobox  
			String selectedTheme = null;
			if (team == 0) {
				selectedTheme = (String) leftTeamComboBox.getSelectedItem();	
			} else {
				selectedTheme = (String) rightTeamComboBox.getSelectedItem();
			}			
			
			if (selectedTheme == null) {
				return;
			} else if (selectedTheme.equals(RED_THEME)) {
				g.setColor(leftColor);
				g.fillOval(a, b, (int) (2 * player_size), (int) (2 * player_size));			
			} else if (selectedTheme.equals(BLUE_THEME)) {
				g.setColor(rightColor);
			    g.fillOval(a, b, (int) (2 * player_size), (int) (2 * player_size));
			} else if (selectedTheme.equals(GERMANY_THEME)) { 
				g.drawImage(germanyImage,a, b,null);
			} else if (selectedTheme.equals(SPAIN_THEME)) { 
				g.drawImage(spainImage,a, b,null);
			} else if (selectedTheme.equals(BRAZIL_THEME)) { 
				g.drawImage(brazilImage,a, b,null);
			} else if (selectedTheme.equals(ITALY_THEME)) { 
				g.drawImage(italyImage,a, b,null);
			} else if (selectedTheme.equals(ENGLAND_THEME)) { 
				g.drawImage(englandImage,a, b,null);
			} else if (selectedTheme.equals(GREECE_THEME)) { 
				g.drawImage(greeceImage,a, b,null);
			} else if (selectedTheme.equals(ARGENTINA_THEME)) { 
				g.drawImage(argentinaImage,a, b,null);
			} else if (selectedTheme.equals(FRANCE_THEME)) { 
				g.drawImage(franceImage,a, b,null);
			} else if (selectedTheme.equals(CROATIA_THEME)) { 
				g.drawImage(croatiaImage,a, b,null);
			} else if (selectedTheme.equals(MEXICO_THEME)) {
				g.drawImage(mexicoImage,a, b,null);
			} else if (selectedTheme.equals(CAMEROON_THEME)) {
				g.drawImage(cameroonImage,a, b,null);
			} else if (selectedTheme.equals(NETHERLANDS_THEME)) {
				g.drawImage(netherlandsImage,a, b,null);
			} else if (selectedTheme.equals(CHILE_THEME)) {
				g.drawImage(chileImage,a, b,null);
			} else if (selectedTheme.equals(AUSTRALIA_THEME)) {
				g.drawImage(australiaImage,a, b,null);
			} else if (selectedTheme.equals(COLOMBIA_THEME)) {
				g.drawImage(colombiaImage,a, b,null);
			} else if (selectedTheme.equals(COSTA_RICA_THEME)) {
				g.drawImage(costaRicaImage,a, b,null);
			} else if (selectedTheme.equals(IVORY_COAST_THEME)) {
				g.drawImage(ivoryCoastImage,a, b,null);
			} else if (selectedTheme.equals(PORTUGAL_THEME)) {
				g.drawImage(portugalImage,a, b,null);
			}  else if (selectedTheme.equals(RUSSIA_THEME)) {
				g.drawImage(russiaImage,a, b,null);
			} else if (selectedTheme.equals(KOREA_THEME)) {
				g.drawImage(koreaImage,a, b,null);
			} else if (selectedTheme.equals(URUGUAY_THEME)) {
				g.drawImage(uruguayImage,a, b,null);
			} else if (selectedTheme.equals(SWITZERLAND_THEME)) {
				g.drawImage(switzerlandImage,a, b,null);
			} else if (selectedTheme.equals(GHANA_THEME)) {
				g.drawImage(ghanaImage,a, b,null);
			} else if (selectedTheme.equals(USA_THEME)) {
				g.drawImage(usaImage,a, b,null);
			}  else if (selectedTheme.equals(HONDURAS_THEME)) {
				g.drawImage(hondurasImage,a, b,null);
			} else if (selectedTheme.equals(BELGIUM_THEME)) {
				g.drawImage(belgiumImage,a, b,null);
			} else if (selectedTheme.equals(ALGERIA_THEME)) {
				g.drawImage(algeriaImage,a, b,null);
			} else if (selectedTheme.equals(BOSNIA_THEME)) {
				g.drawImage(bosniaImage,a, b,null);
			} else if (selectedTheme.equals(JAPAN_THEME)) {
				g.drawImage(japanImage,a, b,null);
			} else if (selectedTheme.equals(EQUADOR_THEME)) {
				g.drawImage(equadorImage,a, b,null);
			} else if (selectedTheme.equals(IRAN_THEME)) {
				g.drawImage(iranImage,a, b,null);
			} else if (selectedTheme.equals(NIGERIA_THEME)) {
				g.drawImage(nigeriaImage,a, b,null);
			} 			
			
			g.setColor(Color.GRAY);
			g.drawOval(a, b, (int) (2 * player_size), (int) (2 * player_size));
			
			// draw direction line
			g.setColor(Color.YELLOW);
			double direction = player.getBodyDirection();
			g.drawLine(
						correctMid.x,
						correctMid.y,
						correctMid.x
								+ (int) (player_size * FastMath.sin(
								FastMath.toRadians(direction))),
						correctMid.y
								- (int) (player_size * FastMath.cos(
							FastMath.toRadians(direction))));

			// draw number near player
			if (player.side == 0) {
				g.setColor(leftColor);	
			} else {
				g.setColor(rightColor);
			}
			
			/*g.drawString(Integer.toString(player.getNumber() + 1), a + 2,
					b + 1);*/

			g.setColor(prev); // resets color
		
		}
	}

	public void setWithPause(boolean bool) {
		master.withpause = bool;
	}
}
