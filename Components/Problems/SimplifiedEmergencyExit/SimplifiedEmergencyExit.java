
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.jodk.lang.FastMath;
import GridVisualization.Display;
import GridVisualization.WhiteBoard;
import core.AbstractRepresentation;
import core.AbstractSingleProblem;

public class SimplifiedEmergencyExit extends AbstractSingleProblem {

  int steps;
  int xpositionofEmergencyExit = 0;
  int ypositionofEmergencyExit = 0;
  int width;
  int height;
  int xpositionofAgent;
  int ypositionofAgent;
  AbstractRepresentation c;

  @Override
protected double evaluateCandidate(AbstractRepresentation candidate) {
    steps = Integer.parseInt(getProperties().get("steps").getValue());
    width = Integer.parseInt(getProperties().get("width").getValue());
    height = Integer.parseInt(getProperties().get("height").getValue());
    c = candidate;
    
    calcSim();
    
    return -FastMath.hypot(xpositionofEmergencyExit - xpositionofAgent, ypositionofEmergencyExit - ypositionofAgent);
  }
  void calcSim(){
    
    xpositionofEmergencyExit = Integer.parseInt(getProperties().get("xpositionofEmergencyExit").getValue());
    ypositionofEmergencyExit = Integer.parseInt(getProperties().get("ypositionofEmergencyExit").getValue());
    xpositionofAgent = Integer.parseInt(getProperties().get("xpositionofAgent").getValue());
    ypositionofAgent = Integer.parseInt(getProperties().get("ypositionofAgent").getValue());
    
    for (int step = 0; step < steps; step++) {
    	ArrayList<Float> input = new ArrayList<Float>();
      input.add((float) (xpositionofEmergencyExit - xpositionofAgent));
      input.add((float) (ypositionofEmergencyExit - ypositionofAgent));

      ArrayList<Float> output = c.getOutput(input);

      float xVelocity = output.get(0).floatValue()*2.0f-1.0f;
      float yVelocity = output.get(1).floatValue()*2.0f-1.0f;

      if /* */(xVelocity >= 1.0 && xpositionofAgent < width - 1) xpositionofAgent += 1;
      else if (xVelocity <= -1.0 && xpositionofAgent > 0 /*   */) xpositionofAgent -= 1;
      if /* */(yVelocity >= 1.0 && ypositionofAgent < height - 1) ypositionofAgent += 1;
      else if (yVelocity <= -1.0 && ypositionofAgent > 0 /*    */) ypositionofAgent -= 1;
    }
  }

  WhiteBoard whiteboard;
  Display display;

  @Override
  public void replayWithVisualization(AbstractRepresentation candidate) {
    steps = 0;
    c = candidate;
    width = Integer.parseInt(getProperties().get("width").getValue());
    height = Integer.parseInt(getProperties().get("height").getValue());
    display = new Display(440, 495, "SimplifiedEmergencyExit");
    display.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    whiteboard = new WhiteBoard(400, 400, width, height, 1);
    whiteboard.addColorToScale(0, Color.WHITE);
    whiteboard.addColorToScale(1, Color.BLACK);
    whiteboard.addColorToScale(2, Color.GREEN);
    JButton minusbutton = new JButton("-");
    JButton plusbutton = new JButton("+");
    display.add(whiteboard);
    display.add(minusbutton);
    display.add(plusbutton);
    minusbutton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (steps > 0) steps--;
        calcSim();
        displayResult();
        display.setTitle("Simplified Emergency Exit    Step: " + steps);
      }
    });
    plusbutton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        steps++;
        calcSim();
        displayResult();
        display.setTitle("Simplified Emergency Exit    Step: " + steps);
      }
    });
    display.setVisible(true);
    calcSim();
    displayResult();
    display.setTitle("Simplified Emergency Exit    Step: " + steps);
  }

  private void displayResult() {
    int[][] data = new int[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if /* */(x == xpositionofEmergencyExit && y == ypositionofEmergencyExit) data[x][y] = 2;
        else if (x == xpositionofAgent /*    */&& y == ypositionofAgent) /*    */data[x][y] = 1;
        else /*                                                                */data[x][y] = 0;
      }
    }
    whiteboard.setData(data);
    whiteboard.repaint();
  }
  
  @Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}


}
