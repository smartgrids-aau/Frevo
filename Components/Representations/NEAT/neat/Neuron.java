package neat;

import java.util.ArrayList;

import neat.activationfunction.ActivationFunction;

public class Neuron {
	private double lastActivation;
	private double bias;
	private double[] weights;
	private ActivationFunction activationFunction;
	private int id;
	private int type;
	private int depth;
	private ArrayList<Neuron> sourceNeurons;
	private ArrayList<Synapse> incomingSynapses;
	private boolean isInput = false;

	public Neuron(ActivationFunction function, int id, int type) {		
		this.activationFunction = function;
		this.id = id;
		this.type = type;
		this.sourceNeurons = new ArrayList<Neuron>();
		this.incomingSynapses = new ArrayList<Synapse>();
		this.isInput = (type == NEATNodeGene.INPUT);
		this.depth = -1;
	}
	
	public void addSourceNeuron(Neuron neuron) {
		this.sourceNeurons.add(neuron);
	}
	
	public void addIncomingSynapse(Synapse synapse) {
		this.incomingSynapses.add(synapse);
	}
	
	public ArrayList<Synapse> incomingSynapses() {
		return (this.incomingSynapses);
	}
	
	public ArrayList<Neuron> sourceNeurons() {
		return (this.sourceNeurons);
	}
	
	public double lastActivation() {
		return (this.lastActivation);
	}
	
	public void activate(Float input) {
		if (!this.isInput)
			return;
		
		this.lastActivation = input;
	}
	
	/**
	 * If it is an input neuron, returns the input, else will run through the specified activation function.
	 * 
	 */
	public double activate(double[] nInputs) {
		double neuronIp = 0;
		int i = 0;
		double weight;
		double input;
		Synapse synapse;

		// acting as a bias neuron
		this.lastActivation = -1;

		if (!this.isInput) {			
			if (nInputs.length > 0) {
				for (i = 0; i < nInputs.length; i++) {
					input = nInputs[i];
					synapse = incomingSynapses.get(i);
					if (synapse.isEnabled()) {
						weight = synapse.getWeight();
						neuronIp += (input * weight);
					}
				}
				neuronIp += (-1 * this.bias);
				this.lastActivation = this.activationFunction.activate(neuronIp);
			}
		} else {
			//neuronIp = nInputs[0];
			this.lastActivation = nInputs[0];
		}
		
		return (this.lastActivation);
	}
	
	public double activate() {
		if (this.isInput)
			return lastActivation;
		
		double neuronIp = 0;
		double weight;
		
		for (int i=0;i<incomingSynapses.size();i++) {
			Synapse synapse = incomingSynapses.get(i);
			
			if (synapse.isEnabled()) {
				weight = synapse.getWeight();
				neuronIp += (synapse.getFrom().lastActivation * weight);
			}
		}
		
		neuronIp += (-1 * this.bias);
		this.lastActivation = this.activationFunction.activate(neuronIp);
		
		return lastActivation;
	}

	public ActivationFunction function() {
		return (this.activationFunction);
	}

	public void modifyWeights(double[] weightMods, double[] momentum, boolean mode) {
		System.arraycopy(weightMods, 0, this.weights, 0, this.weights.length);

	}

	public void modifyBias(double biasMod, double momentum, boolean mode) {
		this.bias = biasMod;
	}

	public double[] weights() {
		return (this.weights);
	}

	public double bias() {
		return (this.bias);
	}

	public double[] lastWeightDeltas() {
		return null;
	}

	public double lastBiasDelta() {
		return 0;
	}
	
	public int id() {
		return (this.id);
	}
	
	public int neuronType() {
		return (this.type);
	}
	
	public int neuronDepth() {
		return (this.depth);
	}

	public void setNeuronDepth(int depth) {
		this.depth = depth;
	}
	
	public void reset() {
		this.lastActivation = 0;
	}

	public ActivationFunction activationFunction() {
		return activationFunction;
	}
}
