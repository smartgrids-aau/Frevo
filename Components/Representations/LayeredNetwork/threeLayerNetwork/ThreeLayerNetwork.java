/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package threeLayerNetwork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.jodk.lang.FastMath;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.dom4j.Element;
import org.dom4j.Node;

import core.FileType;
import core.AbstractRepresentation;
import core.XMLFieldEntry;
import utils.NESRandom;

/**
 * A not recursive layered neural network
 * 
 * @author Wil
 */
public class ThreeLayerNetwork extends AbstractRepresentation {

	protected NESRandom generator;
	protected float[][] weight;
	protected float[] bias;
	protected float[] output;
	protected float[] activation;
	protected int rank;
	protected int nodes;
	protected int input_nodes;
	protected int output_nodes;
	protected int hidden_nodes;
	protected float weight_range;
	protected float bias_range;
	protected int stepnumber;

	protected ArrayList<FileType> exportList = new ArrayList<FileType>();

	public ThreeLayerNetwork(int inputnumber, int outputnumber,
			NESRandom random, Hashtable<String, XMLFieldEntry> properties) {
		super(inputnumber, outputnumber, random, properties);
		this.setProperties(properties);

		generator = random;
		input_nodes = inputnumber;
		output_nodes = outputnumber;
		// load properties
		XMLFieldEntry snumber = getProperties().get("stepNumber");
		stepnumber = Integer.parseInt(snumber.getValue());

		XMLFieldEntry wr = getProperties().get("weight_range");
		this.weight_range = Float.parseFloat(wr.getValue());
		XMLFieldEntry br = getProperties().get("bias_range");
		this.bias_range = Float.parseFloat(br.getValue());
		XMLFieldEntry hn = getProperties().get("hiddenNodes");
		hidden_nodes = Integer.parseInt(hn.getValue());

		nodes = input_nodes + output_nodes + hidden_nodes;

		// generate structure
		this.weight = new float[this.nodes][this.nodes];
		this.bias = new float[this.nodes];
		this.output = new float[this.nodes];
		this.activation = new float[this.nodes];
		if (random != null)
			randomizeWB(); // this step is not needed for cloning, otherwise
							// random is not null
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

	private void randomizeWB() {
		// randomize connections between hidden nodes and input nodes
		for (int i = input_nodes; i < nodes - output_nodes; i++) // all hidden nodes
		{
			for (int j = 0; j < input_nodes; j++) // all input nodes
			{
				this.weight[j][i] = rand_range(weight_range);
			}
			this.bias[i] = rand_range(bias_range);
		}

		// randomize connections between hidden nodes and output nodes
		for (int i = nodes - output_nodes; i < nodes; i++) // all output nodes
		{
			for (int j = input_nodes; j < nodes - output_nodes; j++) // all hidden nodes
			{
				this.weight[j][i] = rand_range(weight_range);
			}
			this.bias[i] = rand_range(bias_range);
		}

		// no bias for input nodes
	}

	// *f
	private float rand_range(float border) {
		double val;
		val = generator.nextDouble() * 2 * border - border; // *f

		return (float) val;
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
		this.mutateBias(severity, probability, method);
		this.mutateWeight(severity, probability, method);
	}

	public void mutateBias(float severity, float p, int method) {
		float rate = severity * bias_range; 

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

	public void mutateWeight(float severity, float p, int method) {
		float rate = severity * weight_range; 
		
		for (int i = 0; i < bias.length; i++) {
			for (int j = input_nodes; j < weight.length; j++) {
				if (generator.nextFloat() < p)
					switch (method) {
					case 1:
						this.weight[i][j] += rand_range(rate);
						break;
					case 2: // smart method, facilitates the mutation to
							// fine-tune small parameters
						this.weight[i][j] += rand_range((int) Math.max(
								this.weight[i][j] / 5.0, 10)); // 20% mutation
						break;
					default:
						this.weight[i][j] += rand_range(rate);
						break;
					}

			}
		}
	}

	private int rndIndex(int range) {
		if (range < 1)
			return 0;

		return generator.nextInt(Integer.MAX_VALUE) % range;
	}

	@Override
	public void recombinationFunction(AbstractRepresentation other, int method) {
		if (!(other instanceof ThreeLayerNetwork))
			throw new IllegalArgumentException(
					"Xover between different network classes not possible!");

		int noninodes = nodes - input_nodes;
		int count = rndIndex(noninodes - 1) + 1; // minimum 1, maximum all
													// but 1
		int start = input_nodes + rndIndex(noninodes);

		ThreeLayerNetwork offspring = this;
		ThreeLayerNetwork father = (ThreeLayerNetwork) other;

		for (int i = start; i < start + count; i++) {
			if (i >= nodes)
				break;

			for (int j = 0; j < nodes; j++)
				offspring.weight[j][i] = father.weight[j][i];
			offspring.bias[i] = father.bias[i];
		}

	}

	public float activate(float x) {
		if (x >= 1)
			return 1;
		else if (x <= 0)
			return 0;
		else
			return (short) x;

	}
	
	public static float sigmoidActivate(float x) {
		return (float) (1.0f / (1.0f + FastMath.exp(-x)));
	}

	public void reset() {
		for (int i = 0; i < nodes; i++)
			this.output[i] = 0;
	}

	/** Provides the networks output for the given input */
	public ArrayList<Float> getOutput(ArrayList<Float> input) {
		for (int i = 0; i < stepnumber - 1; i++) {
			getStep(input);
		}
		return getStep(input);
	}

	/**
	 * Calculate an output vector for the given input vector
	 * @param input given input vector
	 * @return calculated output vector
	 */
	public ArrayList<Float> getStep(ArrayList<Float> input) {
		if (input.size() != this.input_nodes)
			throw new IllegalArgumentException(
					"Input vector size inappropriate!");

		for (int i = 0; i < input.size(); i++) {
			this.output[i] = input.get(i);
		}

		// propagate input to hidden layer
		for (int i = input_nodes; i < nodes - output_nodes; i++) // all hidden
																	// nodes
		{
			float sum = 0;
			for (int j = 0; j < input_nodes; j++) // all input nodes
			{
				sum += this.weight[j][i] * this.output[j];
			}
			this.activation[i] = this.bias[i] + sum;
			this.output[i] = sigmoidActivate(this.activation[i]);
		}

		// propagate information to output layer

		for (int i = nodes - output_nodes; i < nodes; i++) // all output nodes
		{
			float sum = 0;
			for (int j = input_nodes; j < nodes - output_nodes; j++) // all
																		// hidden
																		// nodes
			{
				sum += this.weight[j][i] * this.output[j];
			}
			this.activation[i] = this.bias[i] + sum;
			this.output[i] = sigmoidActivate(this.activation[i]);
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
		int n=0;

		if (!(o instanceof ThreeLayerNetwork))
			throw new IllegalArgumentException(
					"diffTo between different network classes not possible!");

		ThreeLayerNetwork network = (ThreeLayerNetwork) o;

		// add differences from input to hidden layer
		for (int i = input_nodes; i < nodes - output_nodes; i++) // all hidden
		// nodes
		{
			for (int j = 0; j < input_nodes; j++) // all input nodes
			{
				diff += Math.abs(network.weight[j][i] - this.weight[j][i]) / weight_range;
			}
		}
		n+=(nodes - output_nodes-input_nodes)*input_nodes;
		
		// add differences from hidden to output layer
		for (int i = nodes - output_nodes; i < nodes; i++) // all output nodes
		{
			for (int j = input_nodes; j < nodes - output_nodes; j++) // all hidden
			{
				diff += Math.abs(network.weight[j][i] - this.weight[j][i]) / weight_range;
			}
		}
		n+= output_nodes*(nodes - output_nodes-input_nodes);
		
		// bias of all hidden and output nodes 
		for (int i = input_nodes; i < this.bias.length; i++)
			diff += Math.abs(network.bias[i] - this.bias[i]) / bias_range;
		n+=this.bias.length-input_nodes;

		return diff/n;
	}

	public AbstractRepresentation cloneFunction() {
		ThreeLayerNetwork res = new ThreeLayerNetwork(input_nodes,
				output_nodes, generator, getProperties());

		for (int i = 0; i < weight.length; i++) {
			System.arraycopy(weight[i], 0, res.weight[i], 0, weight[i].length);
		}

		res.bias = this.bias.clone();
		res.output = this.output.clone();
		res.activation = this.activation.clone();
		res.rank = this.rank;
		return res;
	}

	public float getWeight(int nr, int from) {
		if (nr >= nodes)
			throw new IllegalArgumentException("Id " + nr + " is not a node!");
		{
			if (from >= nodes)
				throw new IllegalArgumentException("Id " + from
						+ " is not a node!");

			return this.weight[from][nr];
		}
	}

	/** Method used for saving the properties of this component */
	public void exportToXmlElement(Element element) {
		Element nn = element.addElement("ThreeLayerNetwork");
		nn.addAttribute("input_nodes", String.valueOf(this.input_nodes));
		nn.addAttribute("output_nodes", String.valueOf(this.output_nodes));
		nn.addAttribute("nodes", String.valueOf(this.nodes));
		nn.addAttribute("weight_range", String.valueOf(this.weight_range));
		nn.addAttribute("bias_range", String.valueOf(this.bias_range));
		if (this.isEvaluated()) {
			nn.addAttribute("fitness", String.valueOf(this.getFitness()));
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

				Element weights = node.addElement("weights");
				for (int j = 0; j < this.nodes; j++) {
					Element weight = weights.addElement("weight");
					weight.addAttribute("from", String.valueOf(j));
					weight.addText(String.valueOf(this.getWeight(i, j)));
				}
			}
		}
	}
	
	/**
	 * Sets the generator of random numbers for the current representation. 
	 */
	@Override
	public void setGenerator(NESRandom generator) {
		this.generator = generator;
	}

	@Override
	public AbstractRepresentation loadFromXML(Node nd) {
		try {
			String fitnessString = nd.valueOf("./@fitness");
			if (!fitnessString.isEmpty()) {			
				this.setFitness(Double.parseDouble(fitnessString));
			}
			
			this.input_nodes = Integer.parseInt(nd.valueOf("./@input_nodes"));
			this.output_nodes = Integer.parseInt(nd.valueOf("./@output_nodes"));
			this.nodes = Integer.parseInt(nd.valueOf("./@nodes"));
			this.weight_range = Float.parseFloat(nd.valueOf("./@weight_range"));
			this.bias_range = Float.parseFloat(nd.valueOf("./@bias_range"));
			// this.iteration =
			// Integer.parseInt(nd.valueOf("./simulation/@iteration"));
			// this.score = Integer.parseInt(nd.valueOf("./simulation/@score"));
			this.activation = new float[this.nodes];
			this.output = new float[this.nodes];
			this.bias = new float[this.nodes];
			this.weight = new float[this.nodes][this.nodes];
			Node dnodes = nd.selectSingleNode("./nodes");
			for (int nr = this.input_nodes; nr < this.nodes; nr++) {
				Node curnode = dnodes.selectSingleNode("./node[@nr='" + nr
						+ "']");
				if (curnode == null)
					throw new IllegalArgumentException(
							"ThreeLayerNetwork: node tags inconsistent!"
									+ "\ncheck 'nr' attributes and nodes count in nnetwork!");
				this.bias[nr] = Float.parseFloat(curnode.valueOf("./bias"));
				Node dweights = curnode.selectSingleNode("./weights");
				for (int from = 0; from < this.nodes; from++) {
					String ws = dweights.valueOf("./weight[@from='" + from
							+ "']");
					if (ws.length() == 0)
						throw new IllegalArgumentException(
								"ThreeLayerNetwork: weight tags inconsistent!"
										+ "\ncheck 'from' attributes and nodes count in nnetwork!");
					float val = Float.parseFloat(ws);
					this.weight[from][nr] = val;
				}
			}
			// this.gahist = new GAHistory(this.config, nd);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"ThreeLayerNetwork: NumberFormatException! Check XML File");
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
		for (int n = 0; n < nodes; n++) {
			result.put("bias node " + n, Float.toString(this.bias[n]));
			for (int from = 0; from < this.nodes; from++) {
				result.put("weight from " + from + " to " + n,
						Float.toString(this.weight[from][n]));
			}
		}

		return result;
	}

	@Override
	public void exportToFile(File saveFile) {
		System.out.println("Exporting Pajek network file to "
				+ saveFile.getName());
		try {
			// Create file
			FileWriter fstream = new FileWriter(saveFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("*Vertices " + this.nodes);
			out.newLine(); // use newline instead of \n to avoid system
							// dependency
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

			// add edges from hidden neurons to input neurons
			for (int n = input_nodes; n < (nodes - output_nodes); n++) { // take
																			// all
																			// hidden
																			// neurons
				for (int from = 0; from < input_nodes; from++) { // take all
																	// input
																	// neurons
					out.write((n + 1) + " " + (from + 1) + " "
							+ weight[from][n]);
					out.newLine();
				}
			}

			// add edges from output neurons to hidden neurons
			for (int n = (nodes - output_nodes); n < nodes; n++) { // take all
																	// output
																	// neurons
				for (int from = input_nodes; from < (nodes - output_nodes); from++) { // take
																						// all
																						// hidden
																						// neurons
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
	}
	@Override
	public String getC() throws FileNotFoundException {
		List<Float> biases=new ArrayList<Float>();
		for (float b:this.bias){
			biases.add(b);
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
		claST.setAttribute("stepnumber", stepnumber);
		claST.setAttribute("inputnodes", input_nodes);
		claST.setAttribute("outputsize", output.length);
		claST.setAttribute("nodes", nodes);
		claST.setAttribute("outputnodes", output_nodes);
		claST.setAttribute("biases", biases);
		claST.setAttribute("weights", weights);
				
		System.out.println(claST.toString());
		return claST.toString();
	}
}
