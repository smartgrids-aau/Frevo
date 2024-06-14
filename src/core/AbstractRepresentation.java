/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import main.FrevoMain;

import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.dom4j.Element;
import org.dom4j.Node;

import utils.NESRandom;

/** Abstract formulation of a candidate representation. Basically, it servers as a controller "brain" of an agent with its respective input and output interfaces.
 * <p>
 * Each representation must be <i>evolvable</i> meaning it has to implement the necessary mutation and recombination functions.
 * @author Istvan Fehervari*/
public abstract class AbstractRepresentation extends AbstractComponent implements Comparable <AbstractRepresentation>{

	/**
	 * Contains some information about this representation which should only be used by the optimization Method
	 */
	public AbstractEvolutionStatus EvolutionStatus;
	
	/** The fitness value achieved after being evaluated */
	private double fitness;
	
	/** Indicates if this representation has been already evaluated.*/
	private boolean evaluated;
	
	private static boolean classInitialized;
	
	/** Sets the class initialization */
	public static void setClassInitialized(boolean initialized) {
		classInitialized = initialized;
	}
	
	public static boolean isClassInitialized() {
		return classInitialized;
	}
	
	/** Initializes the class variables. Used to be overwritten for cross-instance information handling */
	public static void initialize(int inputnumber, int outputnumber,
			Hashtable<String, XMLFieldEntry> properties, Random rand) {
		if (!isClassInitialized())
			setClassInitialized(true);
	}
	
	/** Constructs a new representation with the given parameters.
	 * @param numberOfInputs Number of input connectors
	 * @param numberOfOutputs Number of output connectors
	 * @param random The random generator object used to construct this representation
	 * @param properties The set of properties as they are defined in the component's XML descriptor file*/
	public AbstractRepresentation(int numberOfInputs, int numberOfOutputs, NESRandom random, Hashtable<String, XMLFieldEntry> properties) {
		resetEval();
	}
	
	public AbstractRepresentation(File xmlfile) {
		// load properties from session file
		FrevoMain.loadInstallDirectory();
		FrevoMain.initComponentDirectories();
		FrevoMain.reLoadComponents(true);
		
		// load session data from file
		FrevoMain.loadSession(xmlfile);
		
		// set properties
		setProperties(FrevoMain.getSelectedComponent(ComponentType.FREVO_REPRESENTATION).getProperties());
	}
	
	/** Returns the state if this representation has been already evaluated.
	 * @return <tt>true<tt> if this representation is already evaluated, <tt>false<tt> otherwise.
	 */
	final public boolean isEvaluated() {
		return evaluated;
	}
	
	/** Sets the evaluated flag for the representation. Candidates with evaluated flag will not be exectued by the ranking instance. */
	final public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

	/** Returns the fitness value of this representation. Throws an exception if this representation has not been evaluated yet.
	 * @return the fitness value of this representation.
	 * @throws IllegalStateException if this representation has not been evaluated.
	 * @see {@link AbstractRepresentation#isEvaluated isEvaluated} to check if a fitness value is already calculated. */
	final public double getFitness() throws IllegalStateException {
		if (isEvaluated())
			return fitness;
		
		throw new IllegalStateException("ERROR: Representation has not yet been evaluated!");
	}

	/** Sets the fitness of this representation to the given value. Set the evaluated flag to true.
	 * @param fitness The new fitness value.*/
	final public void setFitness(double fitness) {
		this.fitness = fitness;
		evaluated = true;
	}
	
	/** Exports the content of this representation to a file. Format and type is custom defined.
	 * @param saveFile The file to be saved to*/
	public abstract void exportToFile(File saveFile);

	/**
	 * Initiates a mutation on this representation with the given parameters.
	 * @param severity Should be between 0..1, interpreted by the representation.
	 * @param probability Should be between 0..1, probability for one genome to be mutated.
	 * @param method Selected mutation method. Use {@link #getNumberofMutationFunctions()} to obtain the number of implemented methods.
	 */
	final public void mutate(float severity, float probability, int method) {
		resetEval();
		if (severity > 1) {
			System.err.print("Mutation severity should be between 0..1. Input mutation severity equals to " + severity + " and is changed to 1.");
			severity = 1;
		}
		if (severity < 0) {
			System.err.print("Mutation severity should be between 0..1. Input mutation severity equals to " + severity + " and is changed to 0.");
			severity = 0;
		}
		
		mutationFunction(severity, probability, method);
	}
	
	/** Initiates a mutation with the given name */
	public void mutate(String functionName, Object... args){
		// To be overwritten
		System.err.println("ERROR: Mutation function called ["+functionName+"] is not implemented in this class!");
	}
	
	/**
	 * Specific implementation of the mutation function that modifies this representation based on the provided parameters.
	 * @param severity Should be between 0..1, interpreted by the representation.
	 * @param probability Should be between 0..1, probability for one genome to be mutated.
	 * @param method Selected mutation method. Use {@link #getNumberofMutationFunctions()} to obtain the number of implemented methods.
	 */
	protected abstract void mutationFunction(float severity, float probability, int method);
	
	/** Modifies this representation based on the implemented recombination function and the provided parameters.
	 * @param other The other representation used for the recombination function.
	 * @param method Selected recombination method. Use {@link #getNumberOfRecombinationFunctions()} to obtain the number of implemented functions.*/
	final public void xOverWith(final AbstractRepresentation other, int method) {
		recombinationFunction(other, method);
		resetEval();
	}
	
	/** Specific implementation of the recombination function that initiates a crossover on this representation
	 * and with the given representation with the provided function.<br>
	 * Use {@link #getNumberOfRecombinationFunctions()} to obtain the number of implemented recombination functions. */
	protected abstract void recombinationFunction(AbstractRepresentation other, int method);
	
	/** Returns the number of implemented mutation functions.
	 * @return the number of mutation functions implemented by this class.*/
	public abstract int getNumberofMutationFunctions();
	
	/** Returns the number of implemented crossover functions.
	 * @return the number of recombination functions implemented by this class. */
	public abstract int getNumberOfRecombinationFunctions();	
		
	/** Processes the given list of inputs and returns the corresponding output values. The output values must be within the range of 0..1. 
	 * @param input The list of input values to be processed.
	 * @return a list of output values.*/
	public abstract ArrayList<Float> getOutput(ArrayList<Float> input);
		
	/** Resets the representation to its base state. Typically this is meant to erases cache for representations possessing some memory.*/
	public abstract void reset();

	/** Compares this representation to another one.
	 * Basically, this function should provide a value measuring the distance from another representation of the same type.
	 * @param representation The other representation to compare to.
	 * @return a value representing the distance between the two candidates.*/
	public abstract double diffTo(final AbstractRepresentation representation);
	
	/** Creates a full clone of this representation. The returned object must be a deep copy of this representation.
	 *  @return a deep copy of this representation. */
	protected abstract AbstractRepresentation cloneFunction();
	
	/** Returns a clone of this representation in a form of a deep copy.
	 * @return a deep copy of this representation.*/
	final public AbstractRepresentation clone() {
		AbstractRepresentation clone = this.cloneFunction();
		if(this.EvolutionStatus != null)
			clone.EvolutionStatus = this.EvolutionStatus.clone();
		if (this.isEvaluated()) {
			clone.evaluated = true;
			clone.fitness = this.fitness;
		} else {
			clone.evaluated = false;
		}
		return clone;
	}
	
	/** Exports this representation and all its necessary data to the provided XML element. The representation must be reconstructible from the saved data using {@link #loadFromXML(Node)}.
	 * @param element The root element to be used for saving. */
	public abstract void exportToXmlElement (Element element);
	
	/** Reconstructs this representation from the given XML node.
	 * @param node The root XML node to load the data from.
	 * @return a newly created representation built from the provided data. */
	public abstract AbstractRepresentation loadFromXML(Node node);
	
	/** Returns a table of key-value pairs that describes this representation. This data is mostly used to visualize this specific representation instance.
	 * @return a human-readable table about the details/configuration of this specific instance*/
	public abstract Hashtable<String,String> getDetails();
	
	/** Returns a hash of this representation instance calculated by its parameters/structure. 
	 * This is used to give the candidate a name for identification. The length should be 4-6 characters.
	 * @return a hash of this representation instance.*/
	public abstract String getHash();
	
	/** Compares this representation to another one based on their fitness values.
	 *  @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.*/
	final public int compareTo(AbstractRepresentation other) {
		if (this.getFitness() > other.getFitness()) return 1; //our score is larger
		else if (this.getFitness() < other.getFitness()) return -1; //other one is larger
		return 0;
	}
	
	/** Resets the evaluation flag to <tt>false</tt>.*/
	final private void resetEval() {
		evaluated = false;
	}
	
	/** Returns a string representation of the object. The returned text equals with the name of the class following the generated unique name in brackets.
	 * @return a textual representation of this object. */
	final public String toString() {
		return getClass().getName()+"("+this.getHash()+")";
	}
	
	/** Returns true if this is a bulk representation, false otherwise. */
	public boolean isBulkRepresentation() {
		return false;
	}
	
	/**
	 * Sets the generator for current representation
	 * @param generator the generator of random numbers
	 */
	public void setGenerator(NESRandom generator) {
	}
	
	/**
	 * Gets the C representation
	 * @throws FileNotFoundException 
	 */
	public abstract String getC() throws FileNotFoundException;
	
	
	/**
	 * Gets the StringTemplateGroup from a file with a name corresponding to class name and ".template" extension
	 * @return
	 * @throws FileNotFoundException
	 */
	protected StringTemplateGroup getStringTemplate() throws FileNotFoundException{
		String name=this.getClass().getSimpleName();
		String filename=name+".template";
		StringTemplateGroup templates = new StringTemplateGroup(new FileReader(filename),AngleBracketTemplateLexer.class);
		return templates;
	}
}
