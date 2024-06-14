package fullyMeshedNet;
/*
 * Copyright (C) 2011 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import main.FrevoMain;
import net.jodk.lang.FastMath;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import utils.NESRandom;
import utils.SafeSAX;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

public class FullyMeshedNet extends AbstractRepresentation {

	public static final ActivationFunction DEFAULT_ACTIVATION_FUNCTION = ActivationFunction.LINEAR;

	protected NESRandom generator;
	protected float[][] weight;
	protected float[] bias;
	protected float[] randombias;
	protected float[] output;
	protected float[] activation;
	protected int nodes;
	protected int input_nodes;
	protected int output_nodes;
	protected int hidden_nodes;
	protected float weight_range;
	protected float bias_range;
	protected float random_bias_range;
	protected int iterations;
	protected boolean random_source; // if true, each neuron has a second bias
										// which is subject to random input
	protected boolean variable_mutation_rate; // if true, the mutation rate
												// itself can be mutated and
												// inherited
	protected float mutationRate; // this variable is only effective when
									// variable_mutation_rate is active
	final float initialMutationRate = 1.0f;

	protected ActivationFunction activationFunction;

	public FullyMeshedNet(int inputnumber, int outputnumber, NESRandom random,
			Hashtable<String, XMLFieldEntry> properties) {
		super(inputnumber, outputnumber, random, properties);
		this.setProperties(properties);

		generator = random;
		input_nodes = inputnumber;
		output_nodes = outputnumber;
		// load properties from properties
		loadProperties();
		// generate structure
		this.weight = new float[this.nodes][this.nodes];
		this.bias = new float[this.nodes];
		this.randombias = new float[this.nodes];
		this.output = new float[this.nodes];
		this.activation = new float[this.nodes];
		mutationRate = initialMutationRate;
		if (random != null)
			randomizeWB(); // this step is not needed for cloning, otherwise
							// random is not null
	}

	private void loadProperties() {
		XMLFieldEntry pIterations = getProperties().get("iterations");
		iterations = Integer.parseInt(pIterations.getValue());

		XMLFieldEntry wr = getProperties().get("weight_range");
		this.weight_range = Float.parseFloat(wr.getValue());
		XMLFieldEntry br = getProperties().get("bias_range");
		this.bias_range = Float.parseFloat(br.getValue());
		XMLFieldEntry hn = getProperties().get("hiddenNodes");
		hidden_nodes = Integer.parseInt(hn.getValue());
		XMLFieldEntry rs = getProperties().get("random_source");
		random_source = Boolean.parseBoolean(rs.getValue());
		XMLFieldEntry rbr = getProperties().get("random_bias_range");
		this.random_bias_range = Float.parseFloat(rbr.getValue());
		XMLFieldEntry vmr = getProperties().get("variable_mutation_rate");
		variable_mutation_rate = Boolean.parseBoolean(vmr.getValue());

		nodes = input_nodes + output_nodes + hidden_nodes;

		// load activation function
		XMLFieldEntry afprop = getProperties().get("activationFunction");
		if (afprop == null) {
			activationFunction = DEFAULT_ACTIVATION_FUNCTION;
		} else {
			activationFunction = ActivationFunction.valueOf(afprop.getValue());
		}

	}

	private void randomizeWB() {
		for (int i = input_nodes; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				this.weight[j][i] = rand_range(weight_range);
			}
			this.output[i] = 0; // *f
			this.bias[i] = rand_range(bias_range);

			if (random_source)
				this.randombias[i] = (float) (1 - generator.nextDouble() * 2)
						* random_bias_range;
		}
	}

	// *f
	private float rand_range(float border) {
		double val;
		val = generator.nextDouble() * 2 * border - border; // *f

		return (float) val;
	}
	
	private double checkRanges(double value, double range) {
		if (value > range) {
			return range;
		}
		if (value < -range){
			return -range;
		}
		return value;
	}

	@Override
	public int getNumberofMutationFunctions() {
		return 2;
	}

	@Override
	public int getNumberOfRecombinationFunctions() {
		return 1;
	}

	@Override
	protected void mutationFunction(float severity, float probability,
			int method) {
		float usedMutationrate;

		if (variable_mutation_rate) {
			// use local muationrate
			usedMutationrate = mutationRate;
		} else {
			// use global mutationrate (severity)
			usedMutationrate = severity;
		}

		this.mutateBias(usedMutationrate, probability, method);
		this.mutateWeight(usedMutationrate, probability, method);
		if (random_source)
			this.mutateRandomBias(100, (int) (usedMutationrate * 100), method);

		if (variable_mutation_rate) {
			mutateMutationrate();
		}
	}

	private void mutateBias(float severity, float p, int method) {
		float rate = severity * bias_range; // use bias_range as 100%

		for (int i = input_nodes; i < bias.length; i++) {
			if (generator.nextFloat() < p) {
				switch (method) {
				case 1:
					this.bias[i] += rand_range(rate);
					break;
				case 2: // smart method, facilitates the mutation to fine-tune
						// small parameters
					this.bias[i] += rand_range((int) Math.max(
							this.bias[i] / 5.0, 10)); // 20% mutation
					break;
				default:
					this.bias[i] += rand_range(rate);
					break;
				}
			}
		}
	}

	private void mutateRandomBias(float severity, int p, int method) {
		float rate = severity * random_bias_range;
		float probability = p;

		for (int i = input_nodes; i < bias.length; i++) {
			if (generator.nextInt(100) < probability) {
				switch (method) {
				case 1:
					this.randombias[i] += rand_range(rate);
					break;
				case 2: // smart method, facilitates the mutation to fine-tune
						// small parameters
					this.randombias[i] += rand_range((int) Math.max(
							this.randombias[i] / 5.0, 10)); // 20% mutation
					break;
				default:
					this.randombias[i] += rand_range(rate);
					break;
				}
			}
		}
	}

	private void mutateWeight(float severity, float p, int method) {

		float rate = severity * weight_range; // use weight_range as 100%

		// System.out.println ("mutating weight rate:"+rate+" prob:"+p);

		for (int i = 0; i < bias.length; i++) {
			for (int j = input_nodes; j < weight.length; j++) {
				if (generator.nextFloat() < p)
					switch (method) {
					case 1:
						this.weight[i][j] += rand_range(rate);
						break;
					case 2: // smart method, facilitates the mutation to
							// fine-tune small parameters
						// float
						// change=rand_range((int)Math.max(this.weight[i][j]/5.0,10));
						// // 20% mutation
						// System.out.print("ch("+i+","+j+"):"+change+" ");
						this.weight[i][j] += rand_range((float) Math.max(
								this.weight[i][j] / 5.0, 0.1)); // 20% mutation
																// or at least
																// range of 0.1
						break;
					default:
						this.weight[i][j] += rand_range(rate);
						break;
					}
			}
		}
	}

	private void mutateMutationrate() {
		mutationRate += rand_range((float) Math.max(mutationRate / 5.0, 0.1)); // 20%
																				// mutation
																				// or
																				// at
																				// least
																				// range
																				// of
																				// 0.1
	}
	
	/** Initiates a mutation with the given name */
	public void mutate(String functionName, Object... args) {
		// Levy-flight mutation with sigma
		if (functionName.equals("Levy-flight-discovery")) {
			FullyMeshedNet p1 = (FullyMeshedNet) args[0];
			FullyMeshedNet p2 = (FullyMeshedNet) args[1];

			float rand = generator.nextFloat();

			for (int i = input_nodes; i < nodes; i++) {
				this.bias[i] += rand * (p1.getBias(i) - p2.getBias(i));
				
				for (int j = 0; j < nodes; j++) {
					this.weight[j][i] += rand * (p1.getWeight(i,j) - p2.getWeight(i,j));					
				}
				
			}

		} else if (functionName.equals("Levy-flight")) {
			double sigma = (Double) args[0];
			double beta = (Double) args[1];
			float scaling = (Float) args[2];
			FullyMeshedNet best = (FullyMeshedNet) args[3];
			
			
			float u,v, step, stepsize;
			for (int i = input_nodes; i < nodes; i++) {
				// bias first
				
				u = (float)(generator.nextGaussian()*sigma);
				v = (float)(generator.nextGaussian());
				step = u / (float)Math.pow(Math.abs(v),1.0/beta) ;
				stepsize = scaling * step * (bias[i]+best.bias[i]);//around 0.04
				
				bias[i] += stepsize * generator.nextGaussian();
				
				// weights
				for (int j = 0; j < nodes; j++) {
					
					u = (float)(generator.nextGaussian()*sigma);
					v = (float)(generator.nextGaussian());
					step = u / (float)Math.pow(Math.abs(v),1.0/beta) ;
					stepsize = scaling * step * (weight[j][i]+best.weight[j][i]);
					
					weight[j][i] += stepsize * generator.nextGaussian();
				}
			}
		} else if (functionName.equals("PSO")) {
			double w = (Double) args[0];
			// Cognitive learning rate. Tendency to return to personal best position
			double c1 = (Double) args[1];
			// Social learning rate. Tendency to move towards the swarm best position
			double c2 = (Double) args[2];
			
			@SuppressWarnings("unchecked")
			List<Double> velocities = (List<Double>) args[3];
			 
			FullyMeshedNet localBest = (FullyMeshedNet) args[4];
			FullyMeshedNet best = (FullyMeshedNet) args[5];
			
			if (velocities.size() == 0) {
				for (int i = input_nodes; i < nodes; i++) {
					velocities.add(generator.nextDouble());
					for (int j = 0; j < nodes; j++) {
						velocities.add(generator.nextDouble());
					}
				}
			}
			
			int v_ind = 0;
			for (int i = input_nodes; i < nodes; i++) {
				
				double weightedVelocity = w * velocities.get(v_ind);
				double locDiff  = localBest.bias[i] - bias[i];
				double globDiff = best.bias[i] - bias[i];
				double velocity = weightedVelocity + 
						c1 * generator.nextDouble() * locDiff +
						c2 * generator.nextDouble() * globDiff;
				velocity = checkRanges(velocity, 2);
				velocities.set(v_ind, velocity);
				this.bias[i] += velocities.get(v_ind);
				this.bias[i] = (float)checkRanges(this.bias[i], bias_range);
				v_ind++;
				
				for (int j = 0; j < nodes; j++) {
					weightedVelocity = w * velocities.get(v_ind);
					locDiff = localBest.weight[j][i] - weight[j][i];
					globDiff = best.weight[j][i] - weight[j][i];
					velocity = weightedVelocity + 
							c1 * generator.nextDouble() * locDiff +
							c2 * generator.nextDouble() * globDiff;
					velocity = checkRanges(velocity, 2);
					velocities.set(v_ind, velocity);
					this.weight[j][i] += velocities.get(v_ind);
					this.weight[j][i] = (float)checkRanges(weight[j][i], weight_range);
					v_ind++;					
				}
			}
		}		
		
		else {
			System.err.println("ERROR: Not implemented!");
		}
	}
	
	public String getHash() {
		double sum = 0;
		// add bias
		for (int i = 0; i < bias.length; i++) {
			sum += bias[i];
		}
		// add weights
		for (int i = input_nodes; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				sum += this.weight[j][i];
			}
		}
		String res = Double.toString(sum);
		int resint = res.hashCode();
		return Integer.toHexString(resint & 0xFFFFF);
	}

	private int rndIndex(int range) {
		if (range < 1)
			return 0;

		return generator.nextInt(Integer.MAX_VALUE) % range;
	}

	@Override
	public void recombinationFunction(AbstractRepresentation other, int method) {
		if (!(other instanceof FullyMeshedNet))
			throw new IllegalArgumentException(
					"Xover between different network classes not possible!");
		// modify this representation
		if (method == 1) {
			int noninodes = nodes - input_nodes;
			int count = rndIndex(noninodes - 1) + 1; // minimum 1, maximum
														// all but 1
			int start = input_nodes + rndIndex(noninodes);

			FullyMeshedNet offspring = this;
			FullyMeshedNet father = (FullyMeshedNet) other;

			for (int i = start; i < start + count; i++) {
				if (i >= nodes)
					break;

				for (int j = 0; j < nodes; j++)
					offspring.weight[j][i] = father.weight[j][i];
				offspring.bias[i] = father.bias[i];
				if (random_source)
					offspring.randombias[i] = father.randombias[i];
			}
			// return offspring;
		}
		/*
		 * else if (method == 2) { not yet implemented }
		 */
		else
			this.recombinationFunction(other, method);

	}

	public float activate(float x) {
		switch (activationFunction) {
		case LINEAR:
			return linearActivate(x);
		case SIGMOID:
			return sigmoidActivate(x);
		case RELU:
			return reluActivate(x);
		default:
			return linearActivate(x);
		}

	}

	public static float reluActivate(float x){
		return Math.max(0, x);
	}
	
	
	public static float sigmoidActivate(float x) {
		return (float) (1.0f / (1.0f + FastMath.exp(-x)));
	}

	public static float linearActivate(float x) {
		if (x >= 1)
			return 1;
		else if (x <= 0)
			return 0;
		else
			return x;
	}

	public void reset() {
		for (int i = 0; i < nodes; i++)
			this.output[i] = 0;
	}

	public ArrayList<Float> getOutput(ArrayList<Float> input) {
		for (int i = 0; i < iterations - 1; i++) {
			getStep(input);
		}
		return getStep(input);
	}

	/** Provides the networks output for the given input */
	public ArrayList<Float> getStep(ArrayList<Float> input) {
		if (input.size() != this.input_nodes)
			throw new IllegalArgumentException(
					"Input vector size inappropriate!\ninput size: "
							+ input.size() + " while input nodes are "
							+ this.input_nodes);

		for (int i = 0; i < input.size(); i++)
			this.output[i] = input.get(i);

		for (int i = input_nodes; i < nodes; i++) {
			float sum = 0;
			for (int j = 0; j < nodes; j++) {
				sum += this.weight[j][i] * this.output[j];
				// System.out.println("sum="+sum);
			}
			this.activation[i] = this.bias[i] + sum;
			if (random_source)
				this.activation[i] += rand_range(randombias[i]);
		}

		for (int i = input_nodes; i < nodes; i++) {
			this.output[i] = activate(this.activation[i]);
		}

		ArrayList<Float> outputVector = new ArrayList<Float>();

		for (int i = nodes - output_nodes; i < nodes; i++) {
			outputVector.add(this.output[i]);
		}
		return outputVector;
	}

	@Override
	public double diffTo(AbstractRepresentation o) {
		double diff = 0;

		if (!(o instanceof FullyMeshedNet))
			throw new IllegalArgumentException(
					"diffTo between different network classes not possible!");

		FullyMeshedNet network = (FullyMeshedNet) o;

		for (int i = 0; i < this.weight.length; i++) {
			for (int j = 0; j < this.weight[i].length; j++) {
				diff += Math.abs(network.weight[i][j] - this.weight[i][j]);
			}
		}
		for (int i = 0; i < this.bias.length; i++)
			diff += Math.abs(network.bias[i] - this.bias[i]);
		if (random_source)
			for (int i = 0; i < this.randombias.length; i++)
				diff += Math.abs(network.randombias[i] - this.randombias[i]);

		return diff;
	}

	protected AbstractRepresentation cloneFunction() {
		FullyMeshedNet c = new FullyMeshedNet(input_nodes, output_nodes,
				generator, getProperties());


		for (int i = 0; i < weight.length; i++) {
			System.arraycopy(weight[i], 0, c.weight[i], 0, weight[i].length);
		}

		c.bias = this.bias.clone();
		c.output = this.output.clone();
		c.activation = this.activation.clone();
		c.nodes = this.nodes;
		c.input_nodes = this.input_nodes;
		c.output_nodes = this.output_nodes;
		c.hidden_nodes = this.hidden_nodes;
		c.weight_range = this.weight_range;
		c.bias_range = this.bias_range;
		c.iterations = this.iterations;
		c.random_source = this.random_source;
		if (random_source) {
			c.randombias = this.randombias.clone();
			c.random_bias_range = this.random_bias_range;
		}

		// res.addProperties(getProperties());

		String n1 = this.getHash();
		String n2 = c.getHash();

		if (!n1.equals(n2))
			System.out.println(this.getHash() + "cloned to"
					+ c.getHash());

		return c;
	}

	public float getWeight(int nr, int from) {
		if (nr >= nodes)
			throw new IllegalArgumentException("Id " + nr + " is not an node!");
		{
			if (from >= nodes)
				throw new IllegalArgumentException("Id " + from
						+ " is not an node!");
			
			return this.weight[from][nr];
		}
	}

	/** Method used for saving the properties of this component */
	public void exportToXmlElement(Element element) {
		Element nn = element.addElement("FullyMeshedNet");
		nn.addAttribute("input_nodes", String.valueOf(this.input_nodes));
		nn.addAttribute("output_nodes", String.valueOf(this.output_nodes));
		nn.addAttribute("nodes", String.valueOf(this.nodes));
		nn.addAttribute("weight_range", String.valueOf(this.weight_range));
		nn.addAttribute("bias_range", String.valueOf(this.bias_range));
		nn.addAttribute("random_bias_range",
				String.valueOf(this.random_bias_range));
		nn.addAttribute("random_source", String.valueOf(this.random_source));
		nn.addAttribute("variable_mutation_rate",
				String.valueOf(this.variable_mutation_rate));
		
		nn.addAttribute("activation_function",String.valueOf(this.activationFunction));

		if (variable_mutation_rate)
			nn.addAttribute("mutation_rate", String.valueOf(this.mutationRate));
		
		if (this.isEvaluated()) {
			nn.addAttribute("fitness", String.valueOf(this.getFitness()));
		} else {
			nn.addAttribute("fitness", String.valueOf(-1));
		}

		Element dnodes = nn.addElement("nodes");

		for (int i = 0; i < this.nodes; i++) {
			Element node;
			if (i < input_nodes) {
				node = dnodes.addElement("node");
				node.addAttribute("nr", String.valueOf(i));
				node.addAttribute("type", "inputNode");
			} else {
				node = dnodes.addElement("node");
				node.addAttribute("nr", String.valueOf(i));
				node.addAttribute("type", "node");
				Element bias = node.addElement("bias");
				bias.addText(String.valueOf(this.bias[i]));

				if (random_source) {
					Element randombias = node.addElement("randombias");
					randombias.addText(String.valueOf(this.randombias[i]));
				}

				Element weights = node.addElement("weights");
				for (int j = 0; j < this.nodes; j++) {
					Element weight = weights.addElement("weight");
					weight.addAttribute("from", String.valueOf(j));
					weight.addText(String.valueOf(this.getWeight(i, j)));
				}
			}
		}
	}
	
	public FullyMeshedNet(File xmlfile, int popid) {
		// loads properties
		super(xmlfile);
		
		// load actual data (from CEA2D now)
		
		// get population root node
		Document doc = SafeSAX.read(xmlfile, true);
		Node dpopulations = doc.selectSingleNode("/frevo/populations");
		List<?> npops = dpopulations.selectNodes(".//population");
		Node pop = (Node) npops.get(popid);
		
		List<?> nets = pop.selectNodes("./*");
		
		// return the one with the highest fitness
		int bestID = 0;
		Node net = (Node) nets.get(bestID);
		String fitnessString = net.valueOf("./@fitness");
		double bestfitness = Double.parseDouble(fitnessString);
		
		for (int i=1;i<nets.size();i++) {
			net = (Node) nets.get(i);
			fitnessString = net.valueOf("./@fitness");
			double fitness = Double.parseDouble(fitnessString);
			if (fitness > bestfitness) {
				bestID = i;
				bestfitness = fitness;
			}
		}
		
		//loadFromXML((Node)nets.get(0));
		loadFromXML((Node)nets.get(bestID));
	}

	public FullyMeshedNet loadFromXML(Node nd) {
		try {
			// Add properties
			loadProperties();
			
			// set generator
			generator = new NESRandom(FrevoMain.getSeed());
			
			String fitnessString = nd.valueOf("./@fitness");
			if (!fitnessString.isEmpty()) {			
				this.setFitness(Double.parseDouble(fitnessString));
			}

			this.input_nodes = Integer.parseInt(nd.valueOf("./@input_nodes"));
			this.output_nodes = Integer.parseInt(nd.valueOf("./@output_nodes"));
			this.nodes = Integer.parseInt(nd.valueOf("./@nodes"));
			this.weight_range = Float.parseFloat(nd.valueOf("./@weight_range"));
			this.bias_range = Float.parseFloat(nd.valueOf("./@bias_range"));
			this.random_bias_range = Float.parseFloat(nd
					.valueOf("./@random_bias_range"));
			this.random_source = Boolean.parseBoolean(nd
					.valueOf("./@random_source"));

			this.variable_mutation_rate = Boolean.parseBoolean(nd
					.valueOf("./@variable_mutation_rate"));
			if (variable_mutation_rate)
				this.mutationRate = Float.parseFloat(nd
						.valueOf("./@mutation_rate"));
			
			this.activationFunction = ActivationFunction.valueOf(nd.valueOf("./@activation_function"));

			this.activation = new float[this.nodes];
			this.output = new float[this.nodes];
			this.bias = new float[this.nodes];
			this.randombias = new float[this.nodes];
			this.weight = new float[this.nodes][this.nodes];

			Node dnodes = nd.selectSingleNode("./nodes");
			for (int nr = this.input_nodes; nr < this.nodes; nr++) {
				Node curnode = dnodes.selectSingleNode("./node[@nr='" + nr
						+ "']");
				if (curnode == null)
					throw new IllegalArgumentException(
							"CompleteNetwork: node tags inconsistent!"
									+ "\ncheck 'nr' attributes and nodes count in nnetwork!");
				this.bias[nr] = Float.parseFloat(curnode.valueOf("./bias"));
				if (random_source) {
					this.randombias[nr] = Float.parseFloat(curnode
							.valueOf("./randombias"));
				}

				Node dweights = curnode.selectSingleNode("./weights");
				for (int from = 0; from < this.nodes; from++) {
					String ws = dweights.valueOf("./weight[@from='" + from
							+ "']");
					if (ws.length() == 0)
						throw new IllegalArgumentException(
								"CompleteNetwork: weight tags inconsistent!"
										+ "\ncheck 'from' attributes and nodes count in nnetwork!");
					float val = Float.parseFloat(ws);
					this.weight[from][nr] = val;
				}
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"CompleteNetwork: NumberFormatException! Check XML File");
		}
		return this;
	}

	@Override
	public Hashtable<String, String> getDetails() {
		Hashtable<String, String> result = new Hashtable<String, String>();
		result.put("input neurons", Integer.toString(this.input_nodes));
		result.put("output neurons", Integer.toString(this.output_nodes));
		result.put("hidden neurons",
				Integer.toString(nodes - input_nodes - output_nodes));
		result.put("weight range", Float.toString(this.weight_range));
		result.put("bias range", Float.toString(this.bias_range));
		if (random_source)
			result.put("random_bias range",
					Float.toString(this.random_bias_range));
		for (int n = 0; n < nodes; n++) {
			result.put("bias node " + n, Float.toString(this.bias[n]));
			if (random_source)
				result.put("random bias node " + n,
						Float.toString(this.randombias[n]));
			for (int from = 0; from < this.nodes; from++) {
				result.put("weight from " + from + " to " + n,
						Float.toString(this.weight[from][n]));
			}
		}

		return result;
	}
	
	/**
	 * Sets the generator of random numbers for the current representation.
	 */
	@Override
	public void setGenerator(NESRandom generator) {
		this.generator = generator;
	}

	/**
	 * @return the bias
	 */
	public float getBias(int i) {
		return bias[i];
	}

	public int getNumberofNodes() {
		return nodes;
	}

	@Override
	public void exportToFile(File saveFile) {
		String extension = FrevoMain.getExtension(saveFile);
		if (extension.equals("net")) {
			System.out.println("Exporting Pajek network file to "
					+ saveFile.getName());
			try {
				// Create file
				FileWriter fstream = new FileWriter(saveFile);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("*Vertices " + this.nodes);
				out.newLine();
				// input neurons
				for (int i = 1; i < input_nodes + 1; i++) {
					out.write(i + " \"I" + i + "\"");
					out.newLine();
				}
				for (int h = input_nodes + 1; h < nodes - output_nodes + 1; h++) {
					out.write(h + " \"H" + (h - input_nodes) + "\"");
					out.newLine();
				}
				int a = 1;
				for (int o = nodes - output_nodes + 1; o < nodes + 1; o++) {
					out.write(o + " \"O" + a + "\"");
					out.newLine();
					a++;
				}

				// Edges
				out.write("*Edges");
				out.newLine();
				for (int n = 0; n < input_nodes - 1; n++) {
					for (int p = n + 1; p < input_nodes; p++) {
						if (n != p) {
							out.write((n + 1) + " " + (p + 1) + " " + 0);
							out.newLine();
						}
					}
				}

				for (int n = input_nodes; n < nodes; n++) {
					for (int from = 0; from < nodes; from++) {
						out.write((n + 1) + " " + (from + 1) + " "
								+ weight[from][n]);
						out.newLine();
					}
				}

				// Close the output stream
				out.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		} else if (extension.equals("xml")) {
			System.out.println("Saving to XML");

			Document doc = DocumentHelper.createDocument();
			doc.addDocType(
					"CompleteNetwork",
					null,
					System.getProperty("user.dir")
							+ "//Components//Representations//CompleteNetwork//src//CompleteNetwork.dtd");
			Element cnetwork = doc.addElement("CompleteNetwork");
			this.exportToXmlElement(cnetwork);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setLineSeparator(System.getProperty("line.separator"));

			try {
				saveFile.createNewFile();
				FileWriter out = new FileWriter(saveFile);
				BufferedWriter bw = new BufferedWriter(out);
				XMLWriter wr = new XMLWriter(bw, format);
				wr.write(doc);
				wr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public String getC() throws FileNotFoundException {
		List<Float> biases=new ArrayList<Float>();
		for (float b:this.bias){
			biases.add(b);
		}
		List<Float> randombiases=new ArrayList<Float>();
		for (float b:this.randombias){
			randombiases.add(b);
		}
		List<List<Float>> weights=new ArrayList<List<Float>>();
		for (int i=0;i<nodes;i++){
			List<Float> temp=new ArrayList<Float>();
			for (int j=0;j<nodes;j++){
				temp.add(this.weight[i][j]);
			}
			weights.add(temp);
		}
		StringTemplateGroup templates = this.getStringTemplate();
		StringTemplate claST = templates.getInstanceOf("getOutputDeclaration");
		claST.setAttribute("seed", FrevoMain.getSeed());
		claST.setAttribute("iterations", iterations);
		claST.setAttribute("randomsource", random_source);
		claST.setAttribute("inputnodes", input_nodes);
		claST.setAttribute("outputsize", output.length);
		claST.setAttribute("nodes", nodes);
		claST.setAttribute("outputnodes", output_nodes);
		claST.setAttribute("biases", biases);
		claST.setAttribute("randombiases", randombiases);
		claST.setAttribute("weights", weights);
		switch (activationFunction) {
		case LINEAR:
			claST.setAttribute("activationmode", "linearActivate");
			break;
		case SIGMOID:
			claST.setAttribute("activationmode", "sigmoidActivate");
			break;
		default:
			claST.setAttribute("activationmode", "linearActivate");
			break;
		}
		
		return claST.toString();
	}
	
}
