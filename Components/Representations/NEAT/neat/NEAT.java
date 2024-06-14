package neat;
/*
 * Copyright (C) 2011 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import graphics.NeuralNetworkVisualisation;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import main.FrevoMain;
import neat.activationfunction.ActivationFunction;
import neat.activationfunction.LinearFunction;
import neat.activationfunction.SigmoidFunction;
import neat.activationfunction.TanhFunction;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import utils.NESRandom;
import utils.SafeSAX;
import core.AbstractRepresentation;
import core.XMLFieldEntry;

/**
 * <b>NeuroEvolution of Augmenting Topologies</b><br>
 * A neural network whose structure is also evolved.<br>
 * See http://en.wikipedia.org/wiki/Neuroevolution_of_augmenting_topologies<br>
 * <br>
 * Originally developed by Ken Stanley in 2002 while at The University of Texas
 * at Austin
 */
public class NEAT extends AbstractRepresentation {

	private final static int MAX_LINK_ATTEMPTS = 5;

	private NEATChromosome chromosome;

	private NESRandom generator;
	private int input_number;
	private int output_number;

	private Synapse[] connections;
	private Neuron[] neurons;
	private int level = 0;

	private float excessCoeff;
	private float disjointCoeff;
	private float weightCoeff;

	private float max_perturbation;
	private float max_bias_perturbation;

	private float add_link_prob;
	private float add_node_prob;
	private float mutate_node_weight_replaced_prob;
	private float mutate_node_sf_prob;
	private float mutate_link_toggle_prob;
	private float mutate_feature_prob;
	private float mutate_node_bias_prob;
	private float mutate_link_weight_prob;

	private boolean featureSelection;

	private boolean recurrent;

	public NEAT(int inputnumber, int outputnumber, NESRandom random,
			Hashtable<String, XMLFieldEntry> properties) {
		super(inputnumber, outputnumber, random, properties);

		this.setProperties(properties);
		this.generator = random;
		this.input_number = inputnumber;
		this.output_number = outputnumber;

		loadProperties();

		// construct from innovation base
		chromosome = InnovationDatabase.database().individualFromTemplate();

		updateNetStructure();
	}

	public NEAT(int inputnumber, int outputnumber, NESRandom random,
			Hashtable<String, XMLFieldEntry> properties,
			NEATChromosome chromosome) {

		super(inputnumber, outputnumber, random, properties);

		this.setProperties(properties);
		this.generator = random;
		this.input_number = inputnumber;
		this.output_number = outputnumber;

		loadProperties();

		// construct from innovation base
		this.chromosome = chromosome;

		updateNetStructure();
	}

	public NEAT(File xmlfile, int popid) {
		// loads properties
		super(xmlfile);

		// get population root node
		Document doc = SafeSAX.read(xmlfile, true);
		Node dpopulations = doc.selectSingleNode("/frevo/population");
		List<?> nets = dpopulations.selectNodes(".//NEAT");
	//	Node pop = (Node) npops.get(popid);

		//List<?> nets = pop.selectNodes("./*");

		// return the one with the highest fitness
		int bestID = 0;
		Node net = (Node) nets.get(bestID);
		String fitnessString = net.valueOf("./@fitness");
		double bestfitness = Double.parseDouble(fitnessString);

		for (int i = 1; i < nets.size(); i++) {
			net = (Node) nets.get(i);
			fitnessString = net.valueOf("./@fitness");
			double fitness = Double.parseDouble(fitnessString);
			if (fitness > bestfitness) {
				bestID = i;
				bestfitness = fitness;
			}
		}

		loadFromXML((Node) nets.get(bestID));

	}

	private void loadProperties() {
		Hashtable<String, XMLFieldEntry> properties = getProperties();
		// load properties
		XMLFieldEntry entry = properties.get("disjoint_coeff");
		disjointCoeff = Float.parseFloat(entry.getValue());

		entry = properties.get("excess_coeff");
		excessCoeff = Float.parseFloat(entry.getValue());

		entry = properties.get("weight_coeff");
		weightCoeff = Float.parseFloat(entry.getValue());

		entry = properties.get("recursive");
		recurrent = Boolean.parseBoolean(entry.getValue());

		entry = properties.get("max_perturbation");
		max_perturbation = Float.parseFloat(entry.getValue());

		entry = properties.get("feature_selection");
		featureSelection = Boolean.parseBoolean(entry.getValue());

		// probabilities

		entry = properties.get("add_link_prob");
		add_link_prob = Float.parseFloat(entry.getValue());

		entry = properties.get("add_node_prob");
		add_node_prob = Float.parseFloat(entry.getValue());

		entry = properties.get("mutate_node_weight_replaced_prob");
		mutate_node_weight_replaced_prob = Float.parseFloat(entry.getValue());

		entry = properties.get("mutate_link_toggle_prob");
		mutate_link_toggle_prob = Float.parseFloat(entry.getValue());

		entry = properties.get("mutate_node_sf_prob");
		mutate_node_sf_prob = Float.parseFloat(entry.getValue());

		entry = properties.get("mutate_feature_prob");
		mutate_feature_prob = Float.parseFloat(entry.getValue());

		entry = properties.get("mutate_node_bias_prob");
		mutate_node_bias_prob = Float.parseFloat(entry.getValue());

		entry = properties.get("mutate_link_weight_prob");
		mutate_link_weight_prob = Float.parseFloat(entry.getValue());
	}

	/** Initializes the innovation factory, should only run once */
	public static void initialize(int inputnumber, int outputnumber,
			Hashtable<String, XMLFieldEntry> properties, Random rand) {
		if (!isClassInitialized()) {

			InnovationDatabase.reset();

			// initialize the innovation database (single template right now)
			InnovationDatabase.database().createTemplate(inputnumber,
					outputnumber, properties, rand);

			setClassInitialized(true);
		}
	}

	/**
	 * Generates a neural network structure based on the chromosome
	 * 
	 */
	public void updateNetStructure() {

		ArrayList<NEATGene> genes = chromosome.genes();

		ArrayList<NEATNodeGene> nodes = new ArrayList<NEATNodeGene>();
		ArrayList<NEATLinkGene> links = new ArrayList<NEATLinkGene>();

		for (int i = 0; i < chromosome.size(); i++) {
			if (genes.get(i) instanceof NEATNodeGene) {
				nodes.add((NEATNodeGene) genes.get(i));
			} else if (genes.get(i) instanceof NEATLinkGene) {
				if (((NEATLinkGene) genes.get(i)).isEnabled()) {
					// only add enabled links to the net structure
					links.add((NEATLinkGene) genes.get(i));
				}
			}
		}

		this.connections = this.createLinks(links, this.createNeurons(nodes));
		this.assignNeuronDepth(this.outputNeurons(), 0);
	}

	public ArrayList<Neuron> outputNeurons() {
		ArrayList<Neuron> outputNeurons = new ArrayList<Neuron>();

		for (int i = 0; i < this.neurons.length; i++) {
			if (this.neurons[i].neuronType() == NEATNodeGene.OUTPUT) {
				outputNeurons.add(this.neurons[i]);
			}
		}
		return (outputNeurons);
	}

	public ArrayList<Neuron> inputNeurons() {
		ArrayList<Neuron> inputNeurons = new ArrayList<Neuron>();

		for (int i = 0; i < this.neurons.length; i++) {
			if (this.neurons[i].neuronType() == NEATNodeGene.INPUT) {
				inputNeurons.add(this.neurons[i]);
			}
		}
		return (inputNeurons);
	}

	public ArrayList<Neuron> hiddenNeurons() {
		ArrayList<Neuron> hiddenNeurons = new ArrayList<Neuron>();

		for (int i = 0; i < this.neurons.length; i++) {
			if (this.neurons[i].neuronType() == NEATNodeGene.HIDDEN) {
				hiddenNeurons.add(this.neurons[i]);
			}
		}
		return (hiddenNeurons);
	}

	/** Provided lists must be created. */
	public void getNeurons(ArrayList<Neuron> inputs, ArrayList<Neuron> hidden,
			ArrayList<Neuron> outputs) {
		if (inputs != null)
			inputs.clear();
		
		if (hidden != null)
			hidden.clear();
		
		if (outputs != null)
			outputs.clear();

		for (int i = 0; i < this.neurons.length; i++) {
			if (this.neurons[i].neuronType() == NEATNodeGene.INPUT) {
				if (inputs != null)
					inputs.add(this.neurons[i]);
			} else if (this.neurons[i].neuronType() == NEATNodeGene.OUTPUT) {
				if (outputs != null)
					outputs.add(this.neurons[i]);
			} else {
				if (hidden != null)
					hidden.add(this.neurons[i]);
			}
		}
	}

	private void assignNeuronDepth(ArrayList<Neuron> neurons, int depth) {
		int i;
		Neuron neuron;

		for (i = 0; i < neurons.size(); i++) {
			neuron = neurons.get(i);
			if (neuron.neuronType() == NEATNodeGene.OUTPUT) {
				if (neuron.neuronDepth() == -1) {
					neuron.setNeuronDepth(depth);
					this.assignNeuronDepth(neuron.sourceNeurons(), depth + 1);
				}
			} else if (neuron.neuronType() == NEATNodeGene.HIDDEN) {
				if (neuron.neuronDepth() == -1) {
					neuron.setNeuronDepth(depth);
					this.assignNeuronDepth(neuron.sourceNeurons(), depth + 1);
				}
			} else if (neuron.neuronType() == NEATNodeGene.INPUT) {
				neuron.setNeuronDepth(Integer.MAX_VALUE);
			}
		}
	}

	private Synapse[] createLinks(ArrayList<NEATLinkGene> links,
			Neuron[] neurons) {
		NEATLinkGene gene;
		Synapse[] synapses = new Synapse[links.size()];

		Neuron from;
		Neuron to;

		for (int i = 0; i < links.size(); i++) {
			gene = links.get(i);
			from = this.findNeuronById(neurons, gene.getFromId());
			to = this.findNeuronById(neurons, gene.getToId());
			to.addSourceNeuron(from);
			synapses[i] = new Synapse(from, to, gene.getWeight());
			synapses[i].setEnabled(gene.isEnabled());
			to.addIncomingSynapse(synapses[i]);
		}

		return (synapses);
	}

	private Neuron[] createNeurons(ArrayList<NEATNodeGene> nodes) {
		this.neurons = new Neuron[nodes.size()];
		NEATNodeGene gene;

		for (int i = 0; i < neurons.length; i++) {
			gene = nodes.get(i);
			this.neurons[i] = new Neuron(this.createActivationFunction(gene),
					gene.id(), gene.getType());
			this.neurons[i].modifyBias(gene.bias(), 0, true);
		}

		return (neurons);
	}

	private ActivationFunction createActivationFunction(NEATNodeGene gene) {
		ActivationFunction function = null;
		// inputs are passed through
		if (gene.getType() == NEATNodeGene.INPUT) {
			function = new LinearFunction();
		} else if (gene.getType() == NEATNodeGene.OUTPUT) {
			function = new SigmoidFunction(gene.sigmoidFactor());
		} else {
			function = new TanhFunction();
		}

		return (function);
	}

	private Neuron findNeuronById(Neuron[] neurons, int id) {
		boolean found = false;
		Neuron neuron = null;
		int i = 0;

		while (!found) {
			if (neurons[i].id() == id) {
				neuron = neurons[i];
				found = true;
			} else {
				i++;
			}
		}

		return (neuron);
	}

	@Override
	public void exportToFile(File saveFile) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void mutationFunction(float severity, float probability,
			int method) {

		boolean res = mutateAddNode();

		if (!res)
			res = mutateAddLink();

		if (!res) {
			// no structural changes has occurred then mutate the rest

			if (generator.nextFloat() < mutate_link_weight_prob)
				mutateLinkWeights();
			if (generator.nextFloat() < mutate_node_bias_prob)
				mutateNodeBias();
			if (generator.nextFloat() < mutate_node_sf_prob)
				mutateNodeSigmoidFactor();
			if ((generator.nextFloat() < mutate_link_toggle_prob)
					&& featureSelection) {
				mutateLinkToggle();
			}
			if ((generator.nextFloat() < mutate_feature_prob)
					&& (featureSelection)) {
				mutateFeatureNode();
			}
		}

		this.updateDepthInfo();
		// now update chrome for depth and recurrency legality

		chromosome.updateChromosome(this.ensureLegalLinks(chromosome.genes()));

		updateNetStructure();
	}

	private void mutateLinkToggle() {
		ArrayList<NEATLinkGene> links = new ArrayList<NEATLinkGene>();

		// collect all link trait
		for (NEATGene g : chromosome.genes()) {
			if (g instanceof NEATLinkGene)
				links.add((NEATLinkGene) g);
		}

		// select one randomly
		NEATLinkGene mutatee = links.get(generator.nextInt(links.size()));

		if (this.featureSelection) {
			mutatee.setEnabled(!mutatee.isEnabled());
		}
	}

	private void mutateNodeSigmoidFactor() {
		// select randomly
		ArrayList<NEATNodeGene> nodes = new ArrayList<NEATNodeGene>();

		// collect all nodes
		for (NEATGene g : chromosome.genes()) {
			if (g instanceof NEATNodeGene)
				nodes.add((NEATNodeGene) g);
		}

		// select one randomly
		NEATNodeGene mutatee = nodes.get(generator.nextInt(nodes.size()));

		double newSF = mutatee.sigmoidFactor();

		newSF = mutatee.sigmoidFactor()
				+ MathUtils.nextClampedDouble(-max_perturbation,
						max_perturbation, generator);
		mutatee = new NEATNodeGene(mutatee.getInnovationNumber(), mutatee.id(),
				newSF, mutatee.getType(), mutatee.bias());
		// mutatee.setSigmoidFactor(newSF);
	}

	private void mutateNodeBias() {
		ArrayList<NEATNodeGene> nodes = new ArrayList<NEATNodeGene>();

		// collect all nodes
		for (NEATGene g : chromosome.genes()) {
			if (g instanceof NEATNodeGene)
				nodes.add((NEATNodeGene) g);
		}

		// select one randomly
		NEATNodeGene mutatee = nodes.get(generator.nextInt(nodes.size()));

		double newBias = mutatee.bias();

		newBias += MathUtils.nextClampedDouble(-max_bias_perturbation,
				max_bias_perturbation, generator);
		// mutatee.setBias(newBias);
		mutatee = new NEATNodeGene(mutatee.getInnovationNumber(), mutatee.id(),
				mutatee.sigmoidFactor(), mutatee.getType(), newBias);

	}

	private void mutateLinkWeights() {
		ArrayList<NEATLinkGene> links = new ArrayList<NEATLinkGene>();

		// collect all link trait
		for (NEATGene g : chromosome.genes()) {
			if (g instanceof NEATLinkGene)
				links.add((NEATLinkGene) g);
		}

		// select one randomly
		NEATLinkGene mutatee = links.get(generator.nextInt(links.size()));

		// change weight
		double newWeight;
		if (generator.nextDouble() < this.mutate_node_weight_replaced_prob) {
			newWeight = MathUtils.nextPlusMinusOne(generator);
		} else {
			newWeight = mutatee.getWeight()
					+ MathUtils.nextClampedDouble(-max_perturbation,
							max_perturbation, generator);
		}
		mutatee = new NEATLinkGene(mutatee.getInnovationNumber(),
				mutatee.isEnabled(), mutatee.getFromId(), mutatee.getToId(),
				newWeight);
	}

	/** Prunes out recurrent connections if they are not allowed */
	private ArrayList<NEATGene> ensureLegalLinks(ArrayList<NEATGene> genes) {
		NEATLinkGene link;
		NEATNodeGene from;
		NEATNodeGene to;

		if (this.recurrent)
			return genes;

		ArrayList<NEATGene> newGenes = new ArrayList<NEATGene>();

		// only need to prune if recurrency not allowed

		// only return enabled links
		for (int i = 0; i < genes.size(); i++) {
			if (genes.get(i) instanceof NEATLinkGene) {
				link = (NEATLinkGene) genes.get(i);
				from = this.findNode(link.getFromId(), genes);
				to = this.findNode(link.getToId(), genes);
				if (from.getDepth() > to.getDepth()) {
					// not recurrent - so keep
					newGenes.add(genes.get(i));
				}
			} else {
				// add nodes automatically
				newGenes.add(genes.get(i));
			}
		}

		return (newGenes);
	}

	private void updateDepthInfo() {
		// use descriptor's chromo to create net
		ArrayList<NEATNodeGene> nodes = new ArrayList<NEATNodeGene>();
		ArrayList<NEATLinkGene> links = new ArrayList<NEATLinkGene>();

		ArrayList<NEATGene> genes = chromosome.genes();

		for (int i = 0; i < genes.size(); i++) {
			if (genes.get(i) instanceof NEATNodeGene) {
				nodes.add((NEATNodeGene) genes.get(i));
			} else if (genes.get(i) instanceof NEATLinkGene) {
				if (((NEATLinkGene) genes.get(i)).isEnabled()) {
					// only add enabled links to the net structure
					links.add((NEATLinkGene) genes.get(i));
				}
			}
		}

		this.assignNeuronDepth(
				this.findOutputNodes(this.candidateNodes(genes)), 1, chromosome);
	}

	private void assignNeuronDepth(ArrayList<NEATNodeGene> nodeGenes,
			int depth, NEATChromosome mutated) {

		NEATNodeGene node;

		for (int i = 0; i < nodeGenes.size(); i++) {
			node = nodeGenes.get(i);
			if (node.getType() == NEATNodeGene.OUTPUT) {
				if (depth == 1) {
					node.setDepth(depth);
					this.assignNeuronDepth(
							this.findSourceNodes(node.id(), mutated.genes()),
							depth + 1, mutated);
				}
			} else if (node.getType() == NEATNodeGene.HIDDEN) {
				if (node.getDepth() == 0) {
					// we have an unassigned depth
					node.setDepth(depth);
					this.assignNeuronDepth(
							this.findSourceNodes(node.id(), mutated.genes()),
							depth + 1, mutated);
				}
			} else if (node.getType() == NEATNodeGene.INPUT) {
				node.setDepth(Integer.MAX_VALUE);
			}
		}
	}

	private ArrayList<NEATNodeGene> findOutputNodes(
			ArrayList<NEATNodeGene> nodes) {
		ArrayList<NEATNodeGene> outputNodes = new ArrayList<NEATNodeGene>();
		NEATNodeGene node;

		for (int i = 0; i < nodes.size(); i++) {
			node = (NEATNodeGene) nodes.get(i);
			if (node.getType() == NEATNodeGene.OUTPUT) {
				outputNodes.add(node);
			}
		}

		return outputNodes;
	}

	private ArrayList<NEATNodeGene> findSourceNodes(int nodeId,
			ArrayList<NEATGene> genes) {
		ArrayList<NEATLinkGene> links = this.candidateLinks(genes);
		NEATLinkGene link;
		ArrayList<NEATNodeGene> sources = new ArrayList<NEATNodeGene>();

		for (int i = 0; i < links.size(); i++) {
			link = (NEATLinkGene) links.get(i);
			if (nodeId == link.getToId()) {
				// add from Id
				sources.add(this.findNode(link.getFromId(), genes));
			}
		}

		return (sources);
	}

	/** Returns a node from the gene set with the give id */
	private NEATNodeGene findNode(int id, ArrayList<NEATGene> genes) {
		int i = 0;
		NEATGene gene;
		NEATNodeGene node = null;
		boolean found = false;

		while (i < genes.size() && !found) {
			gene = genes.get(i);
			if (gene instanceof NEATNodeGene) {
				node = (NEATNodeGene) genes.get(i);
				if (node.id() == id) {
					found = true;
				}
			}
			i++;
		}

		return (node);
	}

	/** Tries to add a node on an existing connection */
	private boolean mutateAddNode() {
		if (generator.nextDouble() > this.add_node_prob)
			return false;

		boolean mutated = false;

		ArrayList<NEATLinkGene> nodeLinks;

		NEATLinkGene chosenLink;
		NEATNodeGene newNode;
		NEATLinkGene newLowerLink;
		NEATLinkGene newUpperLink;

		int linkIdx;

		// add a node on an existing enabled connection
		// find an existing connection to intercept
		nodeLinks = this.candidateLinks(chromosome.genes());
		if (nodeLinks.size() > 0) {

			// pick a link randomly
			linkIdx = generator.nextInt(nodeLinks.size());
			chosenLink = (NEATLinkGene) nodeLinks.get(linkIdx);

			// disable old link
			chosenLink.setEnabled(false);
			newNode = InnovationDatabase.database().submitNodeInnovation(
					chosenLink);
			// newNode.setBias(MathUtils.nextPlusMinusOne());
			newLowerLink = InnovationDatabase.database().submitLinkInnovation(
					chosenLink.getFromId(), newNode.id());
			newUpperLink = InnovationDatabase.database().submitLinkInnovation(
					newNode.id(), chosenLink.getToId());

			// set weights according to Stanley et al's NEAT document
			newLowerLink.setWeight(1);
			newUpperLink.setWeight(chosenLink.getWeight());

			chromosome.addGene(newNode);
			chromosome.addGene(newLowerLink);
			chromosome.addGene(newUpperLink);

			mutated = true;
		}

		return mutated;
	}

	/** Returns true if a mutation was successful. */
	private boolean mutateAddLink() {
		boolean mutated = false;
		double linkRandVal = generator.nextDouble();
		NEATNodeGene from;
		NEATNodeGene to;
		int rIdx;
		int i = 0;
		ArrayList<NEATLinkGene> links;
		ArrayList<NEATNodeGene> nodes;

		NEATGene newLink = null;

		if (linkRandVal < this.add_link_prob) {
			nodes = this.candidateNodes(chromosome.genes());
			links = this.candidateLinks(chromosome.genes());
			// find a new available link
			while (newLink == null && i < MAX_LINK_ATTEMPTS) {
				rIdx = generator.nextInt(nodes.size());
				from = ((NEATNodeGene) nodes.get(rIdx));
				rIdx = generator.nextInt(nodes.size());
				to = ((NEATNodeGene) nodes.get(rIdx));

				if (!this.linkIllegal(from, to, links)) {
					// set it to a random value
					newLink = InnovationDatabase.database()
							.submitLinkInnovation(from.id(), to.id());
					((NEATLinkGene) newLink).setWeight(MathUtils
							.nextPlusMinusOne(generator));
					chromosome.addGene(newLink);
					mutated = true;
					break;
				}
				i++;
			}
		}

		return mutated;
	}

	private void mutateFeatureNode() {
		// select random feature gene
		ArrayList<NEATFeatureGene> featurenodes = new ArrayList<NEATFeatureGene>();

		// collect all nodes
		for (NEATGene g : chromosome.genes()) {
			if (g instanceof NEATFeatureGene)
				featurenodes.add((NEATFeatureGene) g);
		}

		// select one randomly
		NEATFeatureGene mutatee = featurenodes.get(generator
				.nextInt(featurenodes.size()));

		mutatee = new NEATFeatureGene(mutatee.getInnovationNumber(), mutatee
				.geneAsNumber().doubleValue()
				+ MathUtils.nextClampedDouble(-max_perturbation,
						max_perturbation, generator));

	}

	private boolean linkIllegal(NEATNodeGene from, NEATNodeGene to,
			ArrayList<NEATLinkGene> links) {
		boolean illegal = false;
		int idx = 0;
		NEATLinkGene linkGene;

		if ((to.getType() == NEATNodeGene.INPUT)) {
			illegal = true;
		} else {
			while (!illegal && (idx < links.size())) {
				linkGene = (NEATLinkGene) links.get(idx);

				if ((linkGene.getFromId() == from.id() && linkGene.getToId() == to
						.id())) {
					illegal = true;
				}
				idx++;
			}
		}

		return (illegal);
	}

	/** Returns a lost of link genes */
	private ArrayList<NEATLinkGene> candidateLinks(ArrayList<NEATGene> genes) {
		ArrayList<NEATLinkGene> nodeLinks = new ArrayList<NEATLinkGene>();
		NEATGene gene;

		for (int i = 0; i < genes.size(); i++) {
			gene = genes.get(i);
			if (gene instanceof NEATLinkGene) {
				nodeLinks.add((NEATLinkGene) gene);
			}
		}

		return (nodeLinks);
	}

	private ArrayList<NEATNodeGene> candidateNodes(ArrayList<NEATGene> genes) {
		ArrayList<NEATNodeGene> nodes = new ArrayList<NEATNodeGene>();
		NEATGene gene;

		for (int i = 0; i < genes.size(); i++) {
			gene = genes.get(i);
			if (gene instanceof NEATNodeGene) {
				nodes.add((NEATNodeGene) gene);
			}
		}

		return (nodes);
	}

	@Override
	protected void recombinationFunction(AbstractRepresentation other,
			int method) {
		NEATChromosome best = this.chromosome;
		NEATChromosome worst = ((NEAT) other).chromosome;

		// if fitness equal, shortest chromosome is be best (Cannot check it
		// here)
		if (getFitness() == other.getFitness()) {
			if (((NEAT) other).chromosome.size() < this.chromosome.size()) {
				best = worst;
				worst = this.chromosome;
			}
		}

		ArrayList<NEATGene> bestGenes = best.genes();
		ArrayList<NEATGene> worstGenes = worst.genes();

		boolean childBorn = false;
		ArrayList<NEATGene> childGenes = new ArrayList<NEATGene>();
		int bestIdx = 0;
		int worstIdx = 0;

		while (!childBorn) {
			if (worstIdx >= worstGenes.size()) {
				// copy rest of best
				while (bestIdx < bestGenes.size()) {
					childGenes.add(bestGenes.get(bestIdx++).cloneGene());
				}
				childBorn = true;
			} else if (bestIdx >= bestGenes.size()) {
				childBorn = true;
			} else if (bestGenes.get(bestIdx).getInnovationNumber() == worstGenes
					.get(worstIdx).getInnovationNumber()) {
				// innovations are the same, pick one gene at random
				childGenes.add(generator.nextBoolean() ? bestGenes.get(bestIdx)
						.cloneGene() : worstGenes.get(worstIdx).cloneGene());
				bestIdx++;
				worstIdx++;
			} else if (bestGenes.get(bestIdx).getInnovationNumber() > worstGenes
					.get(worstIdx).getInnovationNumber()) {
				// skip disjoint/excess
				worstIdx++;
			} else if (bestGenes.get(bestIdx).getInnovationNumber() < worstGenes
					.get(worstIdx).getInnovationNumber()) {
				// add best disjoint/excess
				childGenes.add(bestGenes.get(bestIdx).cloneGene());
				bestIdx++;
			}
		}

		// assign new chromosome
		this.chromosome = createChromosome(childGenes);

		// update this structure
		this.updateNetStructure();

	}

	/** Creates a new chromosome from the genes provided. */
	private NEATChromosome createChromosome(ArrayList<NEATGene> genes) {
		NEATChromosome chromo = new NEATChromosome(genes);

		return chromo;
	}

	@Override
	public int getNumberofMutationFunctions() {
		return 1;
	}

	@Override
	public int getNumberOfRecombinationFunctions() {
		return 1;
	}

	static final boolean useFixedIteratedOutput = true;

	@Override
	public ArrayList<Float> getOutput(ArrayList<Float> input) {

		if (!useFixedIteratedOutput) {

			this.level = 0;
			int i;
			// travel through the graph backwards from each output node
			ArrayList<Neuron> outputNeurons = this.outputNeurons();
			if (outputNeurons.size() == 0) {
				System.err
						.println("WARNING: No output neurons, no output will be generated.");
			}

			ArrayList<Float> outputs = new ArrayList<Float>(
					outputNeurons.size());

			this.level = 0;
			for (i = 0; i < outputNeurons.size(); i++) {
				outputs.add(threshold((this.neuronOutput(outputNeurons.get(i),
						input))));
			}

			return outputs;

		} else {
			// get neurons
			ArrayList<Neuron> inputneurons = new ArrayList<Neuron>();
			ArrayList<Neuron> outputneurons = new ArrayList<Neuron>();
			ArrayList<Neuron> hiddenneurons = new ArrayList<Neuron>();
			getNeurons(inputneurons, hiddenneurons, outputneurons);

			// experimental value
			int ITERATION_NUMBER = 3;

			// calculate all input
			for (int in = 0; in < inputneurons.size(); in++) {
				Neuron neuron = inputneurons.get(in);
				neuron.activate(input.get(neuron.id() - 1));
			}

			for (int i = 0; i < ITERATION_NUMBER; i++) {

				// calculate all hidden
				for (int in = 0; in < hiddenneurons.size(); in++) {
					Neuron neuron = hiddenneurons.get(in);
					neuron.activate();
				}

				// calculate all output
				for (int in = 0; in < outputneurons.size(); in++) {
					Neuron neuron = outputneurons.get(in);
					neuron.activate();
				}
			}

			ArrayList<Float> outputs = new ArrayList<Float>(
					outputneurons.size());

			for (int i = 0; i < outputneurons.size(); i++) {
				outputs.add((float) (outputneurons.get(i).lastActivation()));
			}

			return outputs;
		}
	}

	private float threshold(double value) {
		if (value > 1.0)
			return 1.0f;
		else if (value < 0.0) {
			return 0.0f;
		}

		return (float) value;
	}

	public void setGenerator(NESRandom generator) {
		this.generator = generator;
	}

	static int maxlevel = 0;

	private double neuronOutput(Neuron neuron, ArrayList<Float> netInput) {

		double output = 0;
		double[] inputPattern;
		// find its inputs
		ArrayList<Neuron> sourceNodes = neuron.sourceNeurons();
		int i;

		if (this.level > maxlevel) {
			maxlevel = this.level;
			System.out.println("Max recursion level: " + maxlevel);
		}
		this.level++;
		if (neuron.neuronType() == NEATNodeGene.INPUT) {
			inputPattern = new double[1];
			// match the input column to the input node, id's start from 1
			inputPattern[0] = netInput.get(neuron.id() - 1);
		} else {
			inputPattern = new double[sourceNodes.size()];
			for (i = 0; i < sourceNodes.size(); i++) {
				if (neuron.id() == ((Neuron) sourceNodes.get(i)).id()) {
					// Self Recurrent
					inputPattern[i] = neuron.lastActivation();
				} else if (neuron.neuronDepth() > ((Neuron) sourceNodes.get(i))
						.neuronDepth()) {
					// Recurrent
					inputPattern[i] = ((Neuron) sourceNodes.get(i))
							.lastActivation();
				} else {
					inputPattern[i] = this.neuronOutput(
							(Neuron) sourceNodes.get(i), netInput);
				}
			}
		}
		output = neuron.activate(inputPattern);
		this.level--;
		return (output);
	}

	@Override
	public void reset() {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].reset();
		}
	}

	@Override
	public double diffTo(AbstractRepresentation o) {
		int disjoints = 0;
		int excess = 0;
		boolean genesToProcess = true;
		int applicantIdx = 0;
		boolean applicantIdxEnded = false;
		int repIdx = 0;
		boolean repIdxEnded = false;
		double avWeightDiff = 0;
		double weightDiffTotal = 0;
		int N;
		double compatabilityScore = Integer.MAX_VALUE;

		NEAT other = (NEAT) o;

		ArrayList<NEATGene> applicantGenes = chromosome.genes();
		ArrayList<NEATGene> repGenes = other.chromosome.genes();

		N = chromosome.size() > other.chromosome.size() ? chromosome.size()
				: other.chromosome.size();

		int matchinggenes = 0;

		while (genesToProcess) {
			// find disjoints and excess
			if ((applicantGenes.get(applicantIdx)).getInnovationNumber() == ((NEATGene) repGenes
					.get(repIdx)).getInnovationNumber()) {
				// find average weight diff
				if (applicantGenes.get(applicantIdx) instanceof NEATLinkGene) {
					weightDiffTotal += Math
							.abs((((NEATLinkGene) applicantGenes
									.get(applicantIdx)).getWeight() - ((NEATLinkGene) repGenes
									.get(repIdx)).getWeight()));
					matchinggenes++;
				}
				applicantIdx++;
				repIdx++;
			} else if (((NEATGene) applicantGenes.get(applicantIdx))
					.getInnovationNumber() > ((NEATGene) repGenes.get(repIdx))
					.getInnovationNumber()) {
				if (repIdx < repGenes.size() && !repIdxEnded) {
					repIdx++;
					disjoints++;
				} else {
					applicantIdx++;
					excess++;
				}
			} else if (((NEATGene) applicantGenes.get(applicantIdx))
					.getInnovationNumber() < ((NEATGene) repGenes.get(repIdx))
					.getInnovationNumber()) {
				if (applicantIdx < applicantGenes.size() && !applicantIdxEnded) {
					applicantIdx++;
					disjoints++;
				} else {
					repIdx++;
					excess++;
				}
			}

			if (applicantIdx == N || repIdx == N) {
				genesToProcess = false;
			}

			// ensure we don't go out of range
			if (applicantIdx == applicantGenes.size()) {
				applicantIdx %= applicantGenes.size();
				applicantIdxEnded = true;
			} else if (repIdx == repGenes.size()) {
				repIdx %= repGenes.size();
				repIdxEnded = true;
			}
		}

		if (matchinggenes != 0)
			avWeightDiff = Math.abs(weightDiffTotal / matchinggenes);

		compatabilityScore = ((excessCoeff * excess) / N)
				+ ((disjointCoeff * disjoints) / N) + weightCoeff
				* avWeightDiff;

		return (compatabilityScore);
	}

	@Override
	protected AbstractRepresentation cloneFunction() {
		NEAT c = new NEAT(input_number, output_number, new NESRandom(
				generator.nextLong()), getProperties(),
				this.chromosome.cloneChromosome());

		if (this.isEvaluated()) {
			c.setFitness(getFitness());
		} else {
			c.setEvaluated(false);
		}

		return c;
	}

	@Override
	public void exportToXmlElement(Element element) {
		Element nn = element.addElement("NEAT");

		ArrayList<Neuron> inputneurons = new ArrayList<Neuron>();
		ArrayList<Neuron> outputneurons = new ArrayList<Neuron>();
		ArrayList<Neuron> hiddenneurons = new ArrayList<Neuron>();
		getNeurons(inputneurons, hiddenneurons, outputneurons);

		// export nodes

		nn.addAttribute("input_nodes", String.valueOf(inputneurons.size()));
		nn.addAttribute("output_nodes", String.valueOf(outputneurons.size()));
		nn.addAttribute("hidden_nodes", String.valueOf(hiddenneurons.size()));

		nn.addAttribute("recurrent", String.valueOf(recurrent));

		nn.addAttribute("randomseed", String.valueOf(generator.getSeed()));

		if (this.isEvaluated()) {
			nn.addAttribute("fitness", String.valueOf(this.getFitness()));
		}

		Element genes = nn.addElement("genes");

		Element node;
		for (int i = 0; i < chromosome.genes().size(); i++) {
			NEATGene gene = chromosome.genes().get(i);

			node = genes.addElement("NEATGene");
			gene.exportToXMLElement(node);
		}

	}

	@Override
	public NEAT loadFromXML(Node nd) {
		// Add properties
		loadProperties();
		
		// set generator
		generator = new NESRandom(FrevoMain.getSeed());
		
		this.input_number = Integer.parseInt(nd.valueOf("./@input_nodes"));
		this.output_number = Integer.parseInt(nd.valueOf("./@output_nodes"));

		this.recurrent = Boolean.parseBoolean(nd.valueOf("./@recurrent"));

		String fitnessString = nd.valueOf("./@fitness");
		if (!fitnessString.isEmpty()) {
			this.setFitness(Double.parseDouble(fitnessString));
		}

		// load genes
		ArrayList<NEATGene> genes = new ArrayList<NEATGene>();
		Node dgenes = nd.selectSingleNode("./genes");

		List<? extends Node> gs = dgenes.selectNodes("./*");
		Iterator<? extends Node> it = gs.iterator();

		while (it.hasNext()) {
			Node n = it.next();
			String name = n.getName();

			if (name.equals("NEATlinkGene"))
				genes.add(new NEATLinkGene(n));
			else if (name.equals("NEATNodeGene"))
				genes.add(new NEATNodeGene(n));
			else
				genes.add(new NEATFeatureGene(n));
		}

		this.chromosome = new NEATChromosome(genes);

		updateNetStructure();

		return this;
	}

	@Override
	public Hashtable<String, String> getDetails() {
		Hashtable<String, String> result = new Hashtable<String, String>();
		visualizeTopology();
		return result;
	}
	
	@Override
	public String getHash() {
		double sum = 0;
		// add bias
		for (int i = 0; i < neurons.length; i++) {
			sum += neurons[i].bias();
		}
		// add weights
		for (int i = 0; i < connections.length; i++) {
			sum += connections[i].getWeight();
		}

		String res = Double.toString(sum);
		int resint = res.hashCode();
		return Integer.toHexString(resint & 0xFFFFF);
	}
	
	/**
	 * Shows a frame with a visualized structure of the neural network. 
	 */
	private void visualizeTopology() {
		NeuralNetworkVisualisation visualizer = new NeuralNetworkVisualisation();
		
		for (NEATGene gene: chromosome.genes()) {
    		if (gene instanceof NEATNodeGene) {
    			NEATNodeGene node = (NEATNodeGene) gene;
    			if (node.getType() == NEATNodeGene.INPUT) {
    				visualizer.addToInputLayer(node.id());
    			} else if (node.getType() == NEATNodeGene.HIDDEN) {
    				visualizer.addToHiddenLayer(node.id());
    			} else {
    				visualizer.addToOutputLayer(node.id());
    			}    			
    		}
    	}
		
		for (NEATGene gene: chromosome.genes()) {
    		if (gene instanceof NEATLinkGene) {
    			NEATLinkGene link = (NEATLinkGene) gene;
    			if (!link.isEnabled()) { 
    				continue;
    			}
    			
    			try {
					visualizer.addLink(link.getFromId(), link.getToId(), link.getWeight());
				} catch (Exception e) {
					e.printStackTrace();
				}    			
    		}
    	}
		
		visualizer.visualize();
	}
	
	@Override
	public String getC() {
		System.err.println ("NEAT not implemented!");
		return null;
	}
}
