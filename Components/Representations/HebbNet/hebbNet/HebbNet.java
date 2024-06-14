/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package hebbNet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import main.FrevoMain;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import core.FileType;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

import utils.NESRandom;

public class HebbNet extends AbstractRepresentation {

	protected NESRandom generator;
	protected float[][] weight;
	protected float[][] plasticity;
	protected float[][] currentweight;
	protected float[] bias;
	protected float[] output;
	protected float[] activation;
	protected float learningrate;
	protected int nodes;
	protected int input_nodes;
	protected int output_nodes;
	protected int hidden_nodes;
	protected float weight_range;
	protected float bias_range;
	protected int stepnumber;

	protected ArrayList<FileType> exportList = new ArrayList<FileType>();

	public HebbNet(int inputnumber, int outputnumber, NESRandom random,
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
		this.currentweight = new float[this.nodes][this.nodes];
		this.plasticity = new float[this.nodes][this.nodes];
		this.bias = new float[this.nodes];
		this.output = new float[this.nodes];
		this.activation = new float[this.nodes];
		learningrate = 0.0f;
		if (random != null)
			randomizeWB(); // this step is not needed for cloning, otherwise
							// random is not null
	}

	private void loadProperties() {
		XMLFieldEntry snumber = getProperties().get("stepNumber");
		stepnumber = Integer.parseInt(snumber.getValue());

		XMLFieldEntry wr = getProperties().get("weight_range");
		this.weight_range = Float.parseFloat(wr.getValue());
		XMLFieldEntry br = getProperties().get("bias_range");
		this.bias_range = Float.parseFloat(br.getValue());
		XMLFieldEntry hn = getProperties().get("hiddenNodes");
		hidden_nodes = Integer.parseInt(hn.getValue());

		nodes = input_nodes + output_nodes + hidden_nodes;
	}

	private void randomizeWB() {
		for (int i = input_nodes; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				this.weight[j][i] = rand_range(weight_range);
				this.plasticity[j][i] = generator.nextFloat();
			}
			this.output[i] = 0; // *f
			this.bias[i] = rand_range(bias_range);
		}
		learningrate = 0.01f * generator.nextFloat();

		resetWeights();
	}

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
		this.learningrate += rand_range((int) Math
				.max(learningrate / 5.0, 0.01));
		resetWeights();
	}

	public void mutateBias(float severity, float p, int method) {
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

	public void mutateWeight(float severity, float p, int method) {

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
						this.weight[i][j] += rand_range((int) Math.max(
								this.weight[i][j] / 5.0, 10)); // 20% mutation
						break;
					default:
						this.weight[i][j] += rand_range(rate);
						break;
					}
				// mutate plasticity values

				if (generator.nextFloat() < p)
					switch (method) {
					case 1:
						this.plasticity[i][j] += rand_range(rate);
						break;
					case 2: // smart method, facilitates the mutation to
							// fine-tune small parameters
						// float
						// change=rand_range((int)Math.max(this.weight[i][j]/5.0,10));
						// // 20% mutation
						// System.out.print("ch("+i+","+j+"):"+change+" ");
						this.plasticity[i][j] += rand_range((int) Math.max(
								this.plasticity[i][j] / 5.0, 10)); // 20%
																	// mutation
						break;
					default:
						this.plasticity[i][j] += rand_range(rate);
						break;
					}

			}
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
				sum += this.weight[j][i] + this.plasticity[j][i];
			}
		}
		sum += learningrate;
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
		if (!(other instanceof HebbNet))
			throw new IllegalArgumentException(
					"Xover between different network classes not possible!");

		// modify this representation
		if (method == 1) {
			int noninodes = nodes - input_nodes;
			int count = rndIndex(noninodes - 1) + 1; // minimum 1, maximum all
														// but 1
			int start = input_nodes + rndIndex(noninodes);

			HebbNet offspring = this;
			HebbNet father = (HebbNet) other;

			for (int i = start; i < start + count; i++) {
				if (i >= nodes)
					break;

				for (int j = 0; j < nodes; j++) {
					offspring.weight[j][i] = father.weight[j][i];
					offspring.plasticity[j][i] = father.plasticity[j][i];
				}
				offspring.bias[i] = father.bias[i];
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
		if (x >= 1)
			return 1;
		else if (x <= 0)
			return 0;
		else
			return x;

	}

	public void reset() // resets also the network weights to default
	{
		for (int i = 0; i < nodes; i++)
			this.output[i] = 0;
		resetWeights();
	}

	private void resetWeights() // resets the network weights to default
	{
		for (int i = 0; i < bias.length; i++)
			for (int j = input_nodes; j < weight.length; j++)
				currentweight[j][i] = weight[j][i];

		for (int i = 0; i < nodes; i++)
			for (int j = 0; j < nodes; j++)
				currentweight[j][i] = weight[j][i];
	}

	public ArrayList<Float> getOutput(ArrayList<Float> input) {
		for (int i = 0; i < stepnumber - 1; i++) {
			getStep(input, false);
		}
		return getStep(input, true);
	}

	/** Provides the networks output for the given input */
	public ArrayList<Float> getStep(ArrayList<Float> input, boolean learn) {
		if (input.size() != this.input_nodes)
			throw new IllegalArgumentException(
					"Input vector size inappropriate!");

		for (int i = 0; i < input.size(); i++)
			this.output[i] = input.get(i);

		for (int i = input_nodes; i < nodes; i++) {
			float sum = 0;
			for (int j = 0; j < nodes; j++) {
				sum += this.currentweight[j][i] * this.output[j];
				// System.out.println("sum="+sum);
			}
			this.activation[i] = this.bias[i] + sum;
		}

		for (int i = input_nodes; i < nodes; i++) {
			this.output[i] = activate(this.activation[i]);
		}

		// Hebbian Learning
		if (learn) {
			float delta[][] = new float[nodes][nodes];
			for (int i = input_nodes; i < nodes; i++) {
				for (int j = 0; j < nodes; j++) {
					delta[j][i] = (float) (learningrate * (output[i] - 0.5) * (output[j] - 0.5));
				}
			}
			// normalize to an average value of 0.0
			float sum = 0.0f;
			int n = 0;
			for (int i = input_nodes; i < nodes; i++) {
				for (int j = 0; j < nodes; j++) {
					if (i != j) {
						sum += delta[j][i];
						n++;
					}
				}
			}
			float corr = sum / n;
			// apply learning
			for (int i = input_nodes; i < nodes; i++)
				for (int j = 0; j < nodes; j++)
					if (i != j)
						currentweight[j][i] += delta[j][i] - corr;
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

		if (!(o instanceof HebbNet))
			throw new IllegalArgumentException(
					"diffTo between different network classes not possible!");

		HebbNet network = (HebbNet) o;

		for (int i = 0; i < this.weight.length; i++) {
			for (int j = 0; j < this.weight[i].length; j++) {
				diff += Math.abs(network.weight[i][j] - this.weight[i][j]);
				diff += Math.abs(network.plasticity[i][j]
						- this.plasticity[i][j]);
			}
		}
		for (int i = 0; i < this.bias.length; i++)
			diff += Math.abs(network.bias[i] - this.bias[i]);

		return diff;
	}

	public AbstractRepresentation cloneFunction() {
		HebbNet c = new HebbNet(input_nodes, output_nodes, 
				generator, getProperties());

		for (int i = 0; i < this.weight.length; i++) {
			for (int j = 0; j < this.weight[0].length; j++) {
				c.weight[i][j] = this.weight[i][j];
				c.plasticity[i][j] = this.plasticity[i][j];
				c.currentweight[i][j] = this.currentweight[i][j];
			}
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
		c.stepnumber = this.stepnumber;
		c.learningrate = this.learningrate;

		String n1 = this.getHash();
		String n2 = c.getHash();

		if (!n1.equals(n2))
			System.out.println(this.getHash() + "cloned to"
					+ c.getHash());

		return c;
	}

	/** Method used for saving the properties of this component */
	public void exportToXmlElement(Element element) {
		Element nn = element.addElement("hebbnet");
		// TODO
		nn.addAttribute("UniqueName", getHash());
		nn.addAttribute("learningrate", String.valueOf(learningrate));
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
					weight.addText(String.valueOf(this.weight[j][i]));
					weight.addAttribute("plasticity",
							String.valueOf(plasticity[j][i]));
				}
			}
		}
	}

	@Override
	public AbstractRepresentation loadFromXML(Node nd) {
		try {
			String fitnessString = nd.valueOf("./@fitness");
			if (!fitnessString.isEmpty()) {			
				this.setFitness(Double.parseDouble(fitnessString));
			}
			// Add properties
			loadProperties();
			this.learningrate = Float.parseFloat(nd.valueOf("./@learningrate"));
			this.input_nodes = Integer.parseInt(nd.valueOf("./@input_nodes"));
			this.output_nodes = Integer.parseInt(nd.valueOf("./@output_nodes"));
			this.nodes = Integer.parseInt(nd.valueOf("./@nodes"));
			this.weight_range = Float.parseFloat(nd.valueOf("./@weight_range"));
			this.bias_range = Float.parseFloat(nd.valueOf("./@bias_range"));
			this.activation = new float[this.nodes];
			this.output = new float[this.nodes];
			this.bias = new float[this.nodes];
			this.weight = new float[this.nodes][this.nodes];
			this.plasticity = new float[this.nodes][this.nodes];
			this.currentweight = new float[this.nodes][this.nodes];
			Node dnodes = nd.selectSingleNode("./nodes");
			for (int nr = this.input_nodes; nr < this.nodes; nr++) {
				Node curnode = dnodes.selectSingleNode("./node[@nr='" + nr
						+ "']");
				if (curnode == null)
					throw new IllegalArgumentException(
							"HebbNet: node tags inconsistent!"
									+ "\ncheck 'nr' attributes and nodes count in nnetwork!");
				this.bias[nr] = Float.parseFloat(curnode.valueOf("./bias"));
				Node dweights = curnode.selectSingleNode("./weights");
				for (int from = 0; from < this.nodes; from++) {
					Node thisweight = dweights
							.selectSingleNode("./weight[@from='" + from + "']");
					float plast = Float.parseFloat(thisweight
							.valueOf("./@plasticity"));
					float weight = Float.parseFloat(thisweight.valueOf("."));
					this.weight[from][nr] = weight;
					this.plasticity[from][nr] = plast;
				}
			}
			resetWeights();
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"HebbNet: NumberFormatException! Check XML File");
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
						Float.toString(this.weight[from][n]) + " Plasticity:"
								+ Float.toString(plasticity[from][n]));
			}
		}

		return result;
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
	
	/**
	 * Sets the generator of random numbers for the current representation.
	 */
	@Override
	public void setGenerator(NESRandom generator) {
		this.generator = generator;
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
						// out.write((n+1)+" "+(from+1)+" "+plasticity[from][n]);
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
					"HebbNet",
					null,
					System.getProperty("user.dir")
							+ "//Components//Representations//HebbNet//src//HebbNet.dtd");
			Element cnetwork = doc.addElement("HebbNet");
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
		List<List<Float>> weights=new ArrayList<List<Float>>();
		for (int i=0;i<nodes;i++){
			List<Float> temp=new ArrayList<Float>();
			for (int j=0;j<nodes;j++){
				temp.add(this.currentweight[i][j]);
			}
			weights.add(temp);
		}
		StringTemplateGroup templates = this.getStringTemplate();
		StringTemplate claST = templates.getInstanceOf("getOutputDeclaration");
		claST.setAttribute("stepnumber", stepnumber);
		claST.setAttribute("learningrate", learningrate);
		claST.setAttribute("inputnodes", input_nodes);
		claST.setAttribute("outputsize", output.length);
		claST.setAttribute("nodes", nodes);
		claST.setAttribute("outputnodes", output_nodes);
		claST.setAttribute("biases", biases);
		claST.setAttribute("weights", weights);
				
		return claST.toString();
	}

}
