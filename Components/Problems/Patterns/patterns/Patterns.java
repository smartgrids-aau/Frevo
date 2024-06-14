package patterns;
import graphics.JIntegerTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import core.AbstractRepresentation;
import core.AbstractSingleProblem;


public class Patterns extends AbstractSingleProblem {
	
	protected int[] digits;
	protected int[] outputdigits;
	int numberSystem = 10;
	int teachtimes = 4;
	
	boolean HINTS=true;

	@Override
	protected double evaluateCandidate(AbstractRepresentation candidate) {
		AbstractRepresentation net = candidate;
		
		numberSystem = Integer.parseInt(this.getProperties().get("numbersystem").getValue());
		teachtimes = Integer.parseInt(this.getProperties().get("numberofteaching").getValue());
		
		//generate a random sequence
		int numberOfDigits = Integer.parseInt(this.getProperties().get("numberofdigits").getValue());
		int numberOfTrainingpatterns = Integer.parseInt(Patterns.this.getProperties().get("numberoftrainingpatterns").getValue());
		
		double sumerr=0.0;
		
		for(int j=0; j<numberOfTrainingpatterns; j++) {
		digits = new int[numberOfDigits];
		Random generator = getRandom(); //new Random
		
		for (int i=0;i<numberOfDigits;i++) {
			digits[i] = generator.nextInt(numberSystem); 
			//System.out.print(digits[i]);
		}
		sumerr += evaluate(net, digits);
		}
		
		return (int) (sumerr / numberOfTrainingpatterns);
	}
	
	private double evaluate(AbstractRepresentation net, int[] digits) {
		
		int lastoutput=0;
		outputdigits = new int[digits.length];
		net.reset();                       
		//teach network

		double error =0.0;
		for (int t =0;t<=teachtimes;t++) {
			//go through all digits
			for (int i=0; i<digits.length; i++) {
				int digit = digits[i];
				if (t==teachtimes) {//last round that counts?
					outputdigits[i]=lastoutput;
					if (HINTS) 
						error -= Math.abs(digit-lastoutput);
					else {
						//error -= (digit-lastoutput)*(digit-lastoutput);
						error -= Math.abs(digit-lastoutput);
						digit = lastoutput; //the net gets its own output as input
					}
				}
				ArrayList<Float> input = new ArrayList<Float>();
				input.add((float)digit);
				ArrayList<Float> output = net.getOutput(input);
				lastoutput = (int)(output.get(0)*numberSystem);
				if (lastoutput >= numberSystem) lastoutput=numberSystem-1;
			}			
		}	

		return error;
	}

	@Override
	public void replayWithVisualization(AbstractRepresentation candidate) {
		numberSystem = Integer.parseInt(this.getProperties().get("numbersystem").getValue());
		teachtimes = Integer.parseInt(this.getProperties().get("numberofteaching").getValue());
		
		PatternsPanel ppanel = new PatternsPanel(candidate);
		ppanel.setVisible(true);
	}
	
	private class PatternsPanel extends JFrame {

		private static final long serialVersionUID = -4049897269400061917L;
		private AbstractRepresentation representation;
		JLabel outputLabel;
		JIntegerTextField inputtextfield;

		public PatternsPanel(AbstractRepresentation net) {
			super ("Patterns");
			this.representation = net;
			this.setLocationRelativeTo(null);
			this.setSize(240, 180);
			this.setLayout(null);
			
			JLabel digitsLabel = new JLabel ("Digits:");
			digitsLabel.setSize(40, 25);
			digitsLabel.setLocation(5, 3);
			this.add(digitsLabel);
			
			inputtextfield = new JIntegerTextField();
			inputtextfield.setSize(110, 25);
			inputtextfield.setLocation(50, 3);
			this.add(inputtextfield);
			
			outputLabel = new JLabel ("");
			outputLabel.setSize(200, 25);
			outputLabel.setLocation(5, 35);
			
			JButton startButton = new JButton("Go");
			startButton.setSize(50,25);
			startButton.setLocation(165, 3);
			startButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					representation.reset();
					//teach network
					int number_of_digits = Integer.parseInt(Patterns.this.getProperties().get("numberofdigits").getValue());
					
					//read from field
					String inputdigits = inputtextfield.getText();
					for (int i=0;i<number_of_digits;i++) {
						if (i>=inputdigits.length()) {
							digits[i]=0;
							System.err.println("Given number has too few digits, padding with zeroes.");
						}
						digits[i] = Integer.parseInt((inputdigits.substring(i, i+1)));
					}
					if (inputdigits.length() > number_of_digits)
						System.err.println("Given number is too long, ignoring the last "+(inputdigits.length()-number_of_digits)+" digits.");
					
					double error = evaluate(representation, digits);
					
					String res = "";
					for(int digit:outputdigits)
						res += digit;
					
					outputLabel.setText(res);
					System.out.println(res+" : "+"error: "+error);
				}
			});
			this.add(startButton);
			
			//generate random input
			int number_of_digits = Integer.parseInt(Patterns.this.getProperties().get("numberofdigits").getValue());
			digits = new int[number_of_digits];
			Random generator = getRandom();
			StringBuilder digitstring = new StringBuilder(number_of_digits);
			for (int i=0;i<number_of_digits;i++) {
				digits[i] = generator.nextInt(numberSystem);
				digitstring.append(Integer.toString(digits[i]));
			}		

			inputtextfield.setText(digitstring.toString());
		}
	}
	
	@Override
	public double getMaximumFitness() {
		return Double.MAX_VALUE;
	}

}
