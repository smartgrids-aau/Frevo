/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package multiLayerNetwork;

import java.io.File;
import java.io.FileNotFoundException;
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
public class MultiLayerNetwork extends AbstractRepresentation {

	protected NESRandom generator;
	protected float[][][] hiddenWeight;	//weights inbetween the hidden nodes
	protected float[][] inputWeight;	//weights between the input and the first hidden layer
	protected float [][] outputWeight;	//weights between the las hidden layer and the output layer
	protected float[][] hiddenBias;	
	protected float [] outputBias;
	protected float[] output;
	protected float[][] hiddenOutput;
	protected int rank;
	protected int nodes;
	protected int input_nodes;
	protected int output_nodes;
	protected int hidden_layers;
	protected int nodes_per_layer;
	protected float weight_range;
	protected float bias_range;
	protected int stepnumber;

	protected ArrayList<FileType> exportList = new ArrayList<FileType>();

	public MultiLayerNetwork(int inputnumber, int outputnumber,
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
		XMLFieldEntry hl = getProperties().get("hidden_layers");
		hidden_layers = Integer.parseInt(hl.getValue());
		XMLFieldEntry npl = getProperties().get("nodes_per_layer");
		nodes_per_layer = Integer.parseInt(npl.getValue());

		nodes = input_nodes + output_nodes + hidden_layers * nodes_per_layer;

		// generate structure
		this.hiddenWeight = new float[hidden_layers-1][nodes_per_layer][nodes_per_layer];
		this.inputWeight = new float[input_nodes][nodes_per_layer];
		this.outputWeight = new float[nodes_per_layer][output_nodes];
		this.hiddenBias = new float[hidden_layers][nodes_per_layer];
		this.outputBias = new float[output_nodes];
		this.output = new float[output_nodes];
		this.hiddenOutput = new float[hidden_layers][nodes_per_layer];
		if (random != null)
			randomizeWB(); // this step is not needed for cloning, otherwise
							// random is not null
	}

	public String getHash() {
		double sum = 0;
		// add bias
		//hidden layers
		for(int i = 0; i < hidden_layers; i++)
		{
			for(int j = 0;  j < nodes_per_layer; j++)
			{
				sum += this.hiddenBias[i][j];
			}
		}
		//output
		for (int i = 0; i < outputBias.length; i++) {
			sum += outputBias[i];
		}
		// add weights
		//input to hidden
				for(int i = 0; i < input_nodes; i++)
				{
					for(int j = 0; j < nodes_per_layer; j++)
					{
						sum += this.inputWeight[i][j];
					}
				}
		//between hidden layers
		for(int i = 0; i < hidden_layers-1; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				for(int k = 0; k < nodes_per_layer; k++)
				{
					sum += this.hiddenWeight[i][k][k];
				}
			}
		}
		//hidden to output
		for(int i = 0; i < nodes_per_layer; i++)
		{
			for(int j = 0; j < output_nodes; j++)
			{
				sum += this.outputWeight[i][j];
			}
		}
		String res = Double.toString(sum);
		int resint = res.hashCode();
		return Integer.toHexString(resint & 0xFFFFF);
	}

	private void randomizeWB() {
		// randomize connections between hidden nodes and input nodes
		for(int i = 0; i < input_nodes; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				this.inputWeight[i][j] = rand_range(weight_range);
			}
		}
		//randomize connections between hidden layers
		for(int i = 0; i < hidden_layers-1; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				for(int k = 0; k < nodes_per_layer; k++)
				{
					this.hiddenWeight[i][k][j] = rand_range(weight_range);
				}
			}
		}
		// randomize connections between hidden nodes and output nodes
		for(int i = 0; i < nodes_per_layer; i++)
		{
			for(int j = 0; j < output_nodes; j++)
			{
				this.outputWeight[i][j] = rand_range(weight_range);
			}
		}
		//bias
		for(int i = 0; i < hidden_layers; i++)
			for(int j = 0; j < nodes_per_layer; j++)
				this.hiddenBias[i][j] = rand_range(bias_range);
		for(int i = 0; i < output_nodes; i++)
			this.outputBias[i] = rand_range(bias_range);
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

		//hidden layers
		for(int i = 0; i < hidden_layers; i++)
		{
			for (int j = 0; j < nodes_per_layer; j++) {
				if (generator.nextFloat() < p) {
					switch (method) {
					case 1:
						this.hiddenBias[i][j] += rand_range(rate);
						break;
					case 2: // smart method, facilitates the mutation to fine-tune
							// small parameters
						this.hiddenBias[i][j] += rand_range((int) Math.max(
								this.hiddenBias[i][j] / 5.0, 10)); // 20% mutation
						break;
					default:
						this.hiddenBias[i][j] += rand_range(rate);
						break;
					}
				}

			}
		}
		//outputs
		for (int i = 0; i < outputBias.length; i++) {
			if (generator.nextFloat() < p) {
				switch (method) {
				case 1:
					this.outputBias[i] += rand_range(rate);
					break;
				case 2: // smart method, facilitates the mutation to fine-tune
						// small parameters
					this.outputBias[i] += rand_range((int) Math.max(
							this.outputBias[i] / 5.0, 10)); // 20% mutation
					break;
				default:
					this.outputBias[i] += rand_range(rate);
					break;
				}
			}

		}
	}

	public void mutateWeight(float severity, float p, int method) {
		float rate = severity * weight_range;
		// mutate connections between hidden nodes and input nodes
		for(int i = 0; i < input_nodes; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				if (generator.nextFloat() < p)
					switch (method) {
					case 1:
						this.inputWeight[i][j] += rand_range(rate);
						break;
					case 2: // smart method, facilitates the mutation to
							// fine-tune small parameters
						this.inputWeight[i][j] += rand_range((int) Math.max(
								this.inputWeight[i][j] / 5.0, 10)); // 20% mutation
						break;
					default:
						this.inputWeight[i][j] += rand_range(rate);
						break;
					}
			}
		}
		//mutate connections between hidden layers
		for(int i = 0; i < hidden_layers-1; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				for(int k = 0; k < nodes_per_layer; k++)
				{
					if (generator.nextFloat() < p)
						switch (method) {
						case 1:
							this.hiddenWeight[i][j][k] += rand_range(rate);
							break;
						case 2: // smart method, facilitates the mutation to
								// fine-tune small parameters
							this.hiddenWeight[i][j][k] += rand_range((int) Math.max(
									this.hiddenWeight[i][j][k] / 5.0, 10)); // 20% mutation
							break;
						default:
							this.hiddenWeight[i][j][k] += rand_range(rate);
							break;
						}
				}						
			}
		}
		// mutate connections between hidden nodes and output nodes
		for(int i = 0; i < nodes_per_layer; i++)
		{
			for(int j = 0; j < output_nodes; j++)
			{
				if (generator.nextFloat() < p)
					switch (method) {
					case 1:
						this.outputWeight[i][j] += rand_range(rate);
						break;
					case 2: // smart method, facilitates the mutation to
							// fine-tune small parameters
						this.outputWeight[i][j] += rand_range((int) Math.max(
								this.outputWeight[i][j] / 5.0, 10)); // 20% mutation
						break;
					default:
						this.outputWeight[i][j] += rand_range(rate);
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
		if (!(other instanceof MultiLayerNetwork))
			throw new IllegalArgumentException(
					"Xover between different network classes not possible!");

		int noninodes = nodes - input_nodes;
		int count = rndIndex(noninodes - 1) + 1; // minimum 1, maximum all
													// but 1
		int start = rndIndex(noninodes);

		MultiLayerNetwork offspring = this;
		MultiLayerNetwork father = (MultiLayerNetwork) other;

		for (int i = start; i < start + count; i++) {
			if (i >= noninodes)
				break;
			if(i < hidden_layers*nodes_per_layer)	//hidden nodes
			{
				if(i < nodes_per_layer)	//first hidden layer
				{
					for(int j = 0; j < input_nodes; j++)
					{
						offspring.inputWeight[j][i%nodes_per_layer] = father.inputWeight[j][i%nodes_per_layer];
					}
				}
				else
				{
					for(int  j= 0; j < nodes_per_layer; j++)
					{
						offspring.hiddenWeight[i/nodes_per_layer-1][j][i%nodes_per_layer] = father.hiddenWeight[i/nodes_per_layer-1][j][i%nodes_per_layer];
					}
				}
				offspring.hiddenBias[i/nodes_per_layer][i%nodes_per_layer] = father.hiddenBias[i/nodes_per_layer][i%nodes_per_layer];
			}
			else	//output nodes
			{
				for(int j = 0; j < nodes_per_layer; j++)
				{
					offspring.outputWeight[j][i-hidden_layers*nodes_per_layer] = father.outputWeight[j][i-hidden_layers*nodes_per_layer];
				}
				offspring.outputBias[i-hidden_layers*nodes_per_layer] = father.outputBias[i-hidden_layers*nodes_per_layer];
			}
			
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
		for(int i = 0; i < hidden_layers; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				this.hiddenOutput[i][j] = 0;
			}
		}
		for (int i = 0; i < output_nodes; i++)
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

		// propagate input to hidden layer
		for (int i = 0; i < nodes_per_layer; i++) //first layer of hidden nodes
		{
			float sum = 0;
			for (int j = 0; j < input_nodes; j++) // all input nodes
			{
				sum += this.inputWeight[j][i] * input.get(j);
			}
			this.hiddenOutput[0][i] = sigmoidActivate(this.hiddenBias[0][i] + sum);
		}
		
		//propagate information between hidden layers
		for(int i = 1; i < hidden_layers; i++)	//all hidden layers except the first
		{
			for(int j = 0; j < nodes_per_layer; j++)	//all nodes of one hidden layer
			{
				float sum = 0;
				for(int k = 0; k < nodes_per_layer; k++)
				{
					sum += this.hiddenWeight[i-1][k][j] * this.hiddenOutput[i-1][k];
				}
				this.hiddenOutput[i][j] = sigmoidActivate(this.hiddenBias[i][j] + sum);
			}
		}

		// propagate information to output layer

		for (int i = 0; i < output_nodes; i++) // all output nodes
		{
			float sum = 0;
			for (int j = 0; j < nodes_per_layer; j++) 
			{
				sum += this.outputWeight[j][i] * this.hiddenOutput[hidden_layers-1][j];
			}
			this.output[i] = sigmoidActivate(this.outputBias[i] + sum);
		}

		ArrayList<Float> outputVector = new ArrayList<Float>();

		for (int i = 0; i < output_nodes; i++) {
			outputVector.add(this.output[i]);
		}
		return outputVector;
	}

	@Override
	public double diffTo(AbstractRepresentation o) {
		double diff = 0;
		int n=0;

		if (!(o instanceof MultiLayerNetwork))
			throw new IllegalArgumentException(
					"diffTo between different network classes not possible!");

		MultiLayerNetwork network = (MultiLayerNetwork) o;
		
		//connections between hidden nodes and input nodes
		for(int i = 0; i < input_nodes; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				diff += Math.abs(network.inputWeight[i][j] - this.inputWeight[i][j]) / weight_range;
			}
		}
		n += input_nodes*nodes_per_layer;
		//connections between hidden layers
		for(int i = 0; i < hidden_layers-1; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				for(int k = 0; k < nodes_per_layer; k++)
				{
					diff += Math.abs(network.hiddenWeight[i][k][j] - this.hiddenWeight[i][k][j]) / weight_range;
				}
				
			}
		}
		n += (hidden_layers-1)*nodes_per_layer*nodes_per_layer;
		//connections between hidden nodes and output nodes
		for(int i = 0; i < nodes_per_layer; i++)
		{
			for(int j = 0; j < output_nodes; j++)
			{
				diff += Math.abs(network.outputWeight[i][j] - this.outputWeight[i][j]) / weight_range;
			}
		}
		n+= nodes_per_layer*output_nodes;
		//hidden bias
		for(int i = 0; i < hidden_layers; i++)
			for(int j = 0; j < nodes_per_layer; j++)
				diff += Math.abs(network.hiddenBias[i][j] - this.hiddenBias[i][j]) / weight_range;
		n += hidden_layers*nodes_per_layer;
		//output bias
		for(int i = 0; i < output_nodes; i++)
			diff += Math.abs(network.outputBias[i] - this.outputBias[i]) / weight_range;
		n += output_nodes;

		return diff/n;
	}

	public AbstractRepresentation cloneFunction() {
		MultiLayerNetwork res = new MultiLayerNetwork(input_nodes,
				output_nodes, generator, getProperties());
		//hidden nodes
		for(int i = 0; i < hidden_layers; i++)
		{
			System.arraycopy(hiddenBias[i], 0, res.hiddenBias[i], 0, hiddenBias[i].length);
			System.arraycopy(hiddenOutput[i], 0, res.hiddenOutput[i], 0, hiddenBias[i].length);
		}
		//weights
		for(int i = 0; i < input_nodes; i++)
		{
			System.arraycopy(inputWeight[i], 0, res.inputWeight[i], 0, inputWeight[i].length);
		}
		for(int i = 0; i < hidden_layers-1; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				System.arraycopy(hiddenWeight[i][j], 0, res.hiddenWeight[i][j], 0, hiddenWeight[i][j].length);
			}
		}
		for(int i = 0; i < nodes_per_layer; i++)
		{
			System.arraycopy(outputWeight[i], 0, res.outputWeight[i], 0, outputWeight[i].length);
		}

		//output nodes
		res.outputBias = this.outputBias.clone();
		res.output = this.output.clone();
		res.rank = this.rank;
		return res;
	}

	/** Method used for saving the properties of this component */
	public void exportToXmlElement(Element element) {
		Element nn = element.addElement("ThreeLayerNetwork");
		nn.addAttribute("input_nodes", String.valueOf(this.input_nodes));
		nn.addAttribute("output_nodes", String.valueOf(this.output_nodes));
		nn.addAttribute("nodes", String.valueOf(this.nodes));
		nn.addAttribute("weight_range", String.valueOf(this.weight_range));
		nn.addAttribute("bias_range", String.valueOf(this.bias_range));
		nn.addAttribute("stepNumber", String.valueOf(this.stepnumber));
		nn.addAttribute("hidden_layers", String.valueOf(this.hidden_layers));
		nn.addAttribute("nodes_per_layer", String.valueOf(this.nodes_per_layer));
		if (this.isEvaluated()) {
			nn.addAttribute("fitness", String.valueOf(this.getFitness()));
		}

		Element hnodes = nn.addElement("hidden_nodes");
		for(int lr = 0; lr < hidden_layers; lr++)
		{
			Element layer = hnodes.addElement("layer");
			layer.addAttribute("lr", String.valueOf(lr));
			for(int nr = 0; nr < nodes_per_layer; nr++)
			{
				Element node = layer.addElement("node");
				node.addAttribute("nr", String.valueOf(nr));
				Element bias = node.addElement("bias");
				bias.addText(String.valueOf(this.hiddenBias[lr][nr]));
				Element weights = node.addElement("weights");
				if(lr == 0)
				{
					for (int from = 0; from < input_nodes; from++) {
						Element weight = weights.addElement("weight");
						weight.addAttribute("from", String.valueOf(from));
						weight.addText(String.valueOf(inputWeight[from][nr]));
					}
				}
				else
				{
					for (int from = 0; from < nodes_per_layer; from++) {
						Element weight = weights.addElement("weight");
						weight.addAttribute("from", String.valueOf(from));
						weight.addText(String.valueOf(hiddenWeight[lr-1][from][nr]));
					}
				}
			}
		}
		Element onodes = nn.addElement("output_nodes");
		for(int nr = 0; nr < output_nodes; nr++)
		{
			Element node = onodes.addElement("node");
			node.addAttribute("nr", String.valueOf(nr));
			Element bias = node.addElement("bias");
			bias.addText(String.valueOf(this.outputBias[nr]));

			Element weights = node.addElement("weights");
			for (int from = 0; from < nodes_per_layer; from++) {
				Element weight = weights.addElement("weight");
				weight.addAttribute("from", String.valueOf(from));
				weight.addText(String.valueOf(outputWeight[from][nr]));
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
			// load properties
			this.input_nodes = Integer.parseInt(nd.valueOf("./@input_nodes"));
			this.output_nodes = Integer.parseInt(nd.valueOf("./@output_nodes"));
			this.nodes = Integer.parseInt(nd.valueOf("./@nodes"));
			this.weight_range = Float.parseFloat(nd.valueOf("./@weight_range"));
			this.bias_range = Float.parseFloat(nd.valueOf("./@bias_range"));
			this.stepnumber = Integer.parseInt(nd.valueOf("./@stepNumber"));
			this.hidden_layers = Integer.parseInt(nd.valueOf("./@hidden_layers"));
			this.nodes_per_layer = Integer.parseInt(nd.valueOf("./@nodes_per_layer"));

			nodes = input_nodes + output_nodes + hidden_layers * nodes_per_layer;

			// generate structure
			this.hiddenWeight = new float[hidden_layers-1][nodes_per_layer][nodes_per_layer];
			this.inputWeight = new float[input_nodes][nodes_per_layer];
			this.outputWeight = new float[nodes_per_layer][output_nodes];
			this.hiddenBias = new float[hidden_layers][nodes_per_layer];
			this.outputBias = new float[output_nodes];
			this.output = new float[output_nodes];
			
			String fitnessString = nd.valueOf("./@fitness");
			if (!fitnessString.isEmpty()) {			
				this.setFitness(Double.parseDouble(fitnessString));
			}
			Node hnodes = nd.selectSingleNode("./hidden_nodes");
			for(int lr = 0; lr < hidden_layers; lr++)
			{
				Node layer = hnodes.selectSingleNode("./layer[@lr='" + lr + "']");
				for(int nr = 0; nr < nodes_per_layer; nr++)
				{
					Node curnode = layer.selectSingleNode("./node[@nr='" + nr + "']");
					if (curnode == null)
						throw new IllegalArgumentException(
								"MultiLayerNetwork: node tags inconsistent!"
										+ "\ncheck 'nr' attributes and nodes count in nnetwork!");
					
					this.hiddenBias[lr][nr] = Float.parseFloat(curnode.valueOf("./bias"));
					Node weights = curnode.selectSingleNode("./weights");
					if(lr == 0)
					{
						for (int from = 0; from < input_nodes; from++) {
							String ws = weights.valueOf("./weight[@from='" + from
									+ "']");
							if (ws.length() == 0)
								throw new IllegalArgumentException(
										"MultiLayerNetwork: weight tags inconsistent!"
												+ "\ncheck 'from' attributes and nodes count in nnetwork!");
							float val = Float.parseFloat(ws);
							this.inputWeight[from][nr] = val;
						}
					}
					else
					{
						for (int from = 0; from < nodes_per_layer; from++) {
							String ws = weights.valueOf("./weight[@from='" + from
									+ "']");
							if (ws.length() == 0)
								throw new IllegalArgumentException(
										"MultiLayerNetwork: weight tags inconsistent!"
												+ "\ncheck 'from' attributes and nodes count in nnetwork!");
							float val = Float.parseFloat(ws);
							this.hiddenWeight[lr-1][from][nr] = val;
						}
					}
				}
			}
			Node onodes = nd.selectSingleNode("./output_nodes");
			for(int nr = 0; nr < output_nodes; nr++)
			{
				Node curnode = onodes.selectSingleNode("./node[@nr='" + nr + "']");
				if (curnode == null)
					throw new IllegalArgumentException(
							"MultiLayerNetwork: node tags inconsistent!"
									+ "\ncheck 'nr' attributes and nodes count in nnetwork!");
				
				this.outputBias[nr] = Float.parseFloat(curnode.valueOf("./bias"));
				Node weights = curnode.selectSingleNode("./weights");
				for (int from = 0; from < nodes_per_layer; from++) {
					String ws = weights.valueOf("./weight[@from='" + from
							+ "']");
					if (ws.length() == 0)
						throw new IllegalArgumentException(
								"MultiLayerNetwork: weight tags inconsistent!"
										+ "\ncheck 'from' attributes and nodes count in nnetwork!");
					float val = Float.parseFloat(ws);
					this.outputWeight[from][nr] = val;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
					"MultiLayerNetwork: NumberFormatException! Check XML File");
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
		for(int i = 0; i < hidden_layers; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				result.put("bias hidden node " + i + "/" + j, Float.toString(hiddenBias[i][j]));
				if(i==0)
				{
					for(int from = 0; from < input_nodes; from++)
					{
						result.put("weight from input " + from + " to hidden " + i + "/" + j, Float.toString(inputWeight[from][j]));
					}
				}
				else
				{
					for(int from = 0; from < nodes_per_layer; from++)
					{
						result.put("weight from hidden " + (i-1) + "/" + from + " to hidden " + i + "/" + j, Float.toString(hiddenWeight[i-1][from][j]));
					}
				}
			}
		}
		for(int i = 0; i < output_nodes; i++)
		{
			result.put("bias output node " + i, Float.toString(outputBias[i]));
			for(int j = 0; j < nodes_per_layer; j++)
			{
				result.put("weight from hidden " + (hidden_layers) + "/" + j + " to output " + i, Float.toString(outputWeight[j][i]));
			}
		}
		return result;
	}
	
	//Uses the template of three layered network
	@Override
	public String getC() throws FileNotFoundException {
		List<Float> biases=new ArrayList<Float>();
		for(int i = 0; i < input_nodes; i++)
			biases.add(0f);
		for(float[] bs: this.hiddenBias)
			for(float b:bs)
				biases.add(b);
		for (float b:this.outputBias){
			biases.add(b);
		}
		/* Mapping of the weights to the list looks like this
		 * +-----------+
		 * | 0 i 0 0 0 |
		 * | 0 0 h 0 0 |
		 * | 0 0 0 h 0 |
		 * | 0 0 0 0 o |
		 * | 0 0 0 0 0 |
		 * +-----------+
		 * 
		 */
		List<List<Float>> weights=new ArrayList<List<Float>>();
		for(int i = 0; i < nodes; i++)
		{
			List<Float> temp = new ArrayList<Float>();
			for(int j = 0; j < nodes; j++)
			{
				temp.add(0f);
			}
			weights.add(temp);
		}
		for(int i = 0; i < input_nodes; i++)
		{
			List<Float> temp = weights.get(i);
			for(int j = 0; j < nodes_per_layer; j++)
			{
				temp.set(j+input_nodes, inputWeight[i][j]);
			}
		}
		for(int i = 0; i < hidden_layers-1; i++)
		{
			for(int j = 0; j < nodes_per_layer; j++)
			{
				List<Float> temp = weights.get(input_nodes + i*nodes_per_layer + j);
				for(int k = 0; k < nodes_per_layer; k++)
				{
					temp.set(k+ input_nodes + nodes_per_layer*(i+1), hiddenWeight[i][k][j]);
				}
			}
		}
		for(int i = 0; i < nodes_per_layer; i++)
		{
			List<Float> temp = weights.get(nodes-output_nodes-nodes_per_layer+i);
			for(int j = 0; j < output_nodes; j++)
			{
				temp.set(j+input_nodes+hidden_layers*nodes_per_layer, outputWeight[i][j]);
			}
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

	@Override
	public void exportToFile(File saveFile) {
		// TODO Auto-generated method stub
		
	}
}
