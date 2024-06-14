package nnga;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import nnga.NNGAEvolutionStatus.evolutionFunction;

import org.dom4j.Element;

import utils.NESRandom;
import core.AbstractRanking;
import core.AbstractRepresentation;
import core.ComponentXMLData;
import core.ProblemXMLData;

public class SimplePopulation {

	private ArrayList<AbstractRepresentation> members;
	private ComponentXMLData representation;
	int input_number;
	int output_number;
	private AbstractRepresentation bestCandidate;
	NNGAParameters parameters;

	private int oldnumElite;
	private int oldnumMutation;
	private int oldnumXOver;
	private int oldnumRenew;
	private int oldnumRandom;
	
	private int numElite;
	private int numMutation;
	private int numXOver;
	private int numRenew;
	private int numRandom;
	
	private int numEliteElite;
	private int numMutationElite;
	private int numXOverElite;
	private int numRenewElite;
	private int numRandomElite;
	
	public int getOldnumElite() {
		return oldnumElite;
	}

	public int getOldnumMutation() {
		return oldnumMutation;
	}

	public int getOldnumXOver() {
		return oldnumXOver;
	}

	public int getOldnumRenew() {
		return oldnumRenew;
	}

	public int getOldnumRandom() {
		return oldnumRandom;
	}

	public int getNumElite() {
		return numElite;
	}

	public int getNumMutation() {
		return numMutation;
	}

	public int getNumXOver() {
		return numXOver;
	}

	public int getNumRenew() {
		return numRenew;
	}

	public int getNumRandom() {
		return numRandom;
	}
	
	public int getNumEliteElite() {
		return numEliteElite;
	}

	public int getNumMutationElite() {
		return numMutationElite;
	}

	public int getNumXOverElite() {
		return numXOverElite;
	}

	public int getNumRenewElite() {
		return numRenewElite;
	}

	public int getNumRandomElite() {
		return numRandomElite;
	}

	public SimplePopulation (ComponentXMLData representationData, NNGAParameters parameters, int inputnumber, int outputnumber) {
		this.parameters = parameters;
		this.representation = representationData;
		input_number = inputnumber;
		output_number = outputnumber;
		createPop();	
	}
	
	public SimplePopulation (ComponentXMLData representationData, NNGAParameters parameters, int inputnumber, int outputnumber, ArrayList<AbstractRepresentation> population) {
		this.parameters = parameters;		
		this.representation = representationData;
		input_number = inputnumber;
		output_number = outputnumber;
		members = population;
		for (AbstractRepresentation member : members) {
			member.setGenerator(parameters.getGenerator());
		}
		
	}
	
	/** Creates initial population*/
	private void createPop() {
		members = new ArrayList<AbstractRepresentation>();
		for (int i = 0;i<parameters.getPopulationSize();i++) {
			try {
				AbstractRepresentation member = representation.getNewRepresentationInstance(input_number, output_number, parameters.getGenerator());
				member.EvolutionStatus = new NNGAEvolutionStatus();
				members.add(member);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		bestCandidate = members.get(0);
		
	}
	
	/** Returns an array of representations within this population */
	public AbstractRepresentation[] getNetArray()
	{
		AbstractRepresentation[] n = new AbstractRepresentation[parameters.getPopulationSize()];
		return members.toArray(n);
	}
	
	/** Sorts the candidates in a decreasing order */
	void rank()
	{
		Collections.sort(members,Collections.reverseOrder());
	}

	ArrayList<AbstractRepresentation> getMembers() {
		return this.members;
	}
	
	/** Returns the best candidate of the population */
	AbstractRepresentation getBestCandidate() {
		return bestCandidate;		
	}

	AbstractRepresentation genNewNetwork() throws InstantiationException, IllegalAccessException, InvocationTargetException {
		AbstractRepresentation member = representation.getNewRepresentationInstance(input_number,output_number,parameters.getGenerator());
		return member;
	}

	public void readNetArray(AbstractRepresentation[] nets) {
		this.members = new ArrayList<AbstractRepresentation>(Arrays.asList(nets));
	}

	/** Exports the population to the given Element. To save individual representations, use {@link AbstractRepresentation#exportToXmlElement(Element)}.
	 * @param element the element to be exported to*/
	Element exportXml(Element element)
	{
		Element dpop = element.addElement("population");
		
		// add all members of the population
		for(AbstractRepresentation n: members)
			n.exportToXmlElement(dpop);
		
		return dpop;
	}
	
	public int sortCandidates(AbstractRanking ranking, ProblemXMLData problemData, NESRandom nesRandom) {
		int evals = ranking.sortCandidates(getMembers(), problemData, nesRandom);
		bestCandidate = ranking.getBestCandidate();
		return evals;
	}
	
	/**
	 * Evolves population by one step to the next generation
	 * @param generation the number of generation
	 * @throws Exception couldn't evolve population to the next generation
	 */
	public void evolve(int generation, LinkedList<SimplePopulation> populations) throws Exception {
		if (generation >= parameters.getGenerations()) {
			return;
		}
		oldnumElite = numElite;
		oldnumMutation = numMutation;
		oldnumXOver = numXOver;
		oldnumRandom = numRandom;
		oldnumRandom = numRandom;
		
		numElite = 0;
		numEliteElite = 0;
		numMutation = 0;
		numMutationElite = 0;
		numXOver = 0;
		numXOverElite = 0;
		numRenew = 0;
		numRenewElite = 0;
		numRandom = 0;
		numRandomElite = 0;
		
		// track current position in the new array
		int position = 0;

		// temporary copy array for new candidates
		AbstractRepresentation[] nets = getNetArray();

		position = parameters.getElite();

		for (int i = 0; i < position; i++) {
			switch(((NNGAEvolutionStatus) nets[i].EvolutionStatus).createdBy){
			case Elite:
				numEliteElite++;
				break;
			case Mutation:
				numMutationElite++;
				break;
			case Xover:
				numXOverElite++;
				break;
			case Renew:
				numRenewElite++;
				break;
			case Random:
				numRandomElite++;
				break;
			}
			nets[i].EvolutionStatus = new NNGAEvolutionStatus();
			((NNGAEvolutionStatus) nets[i].EvolutionStatus).createdBy = evolutionFunction.Elite;
			numElite++;
		}
		
		// select randomly, probability should be higher for better-ranked
		// individuals
		for (int i = position; i < position + parameters.getRandom(); i++) {
			int randrange;
			int startind;

			// number of nets that are considered based on maximum diversity to
			// individuals already considered so far

			// HINT: set this value to one to turn off this feature
			int diffsearch = 1;

			// TODO: calculation of starting bugged!
			// create a triangle distribution by adding two independent randoms
			randrange = parameters.getPopulationSize() - 1 - (i + diffsearch - 1);
			startind = rndIndex(randrange) + rndIndex(randrange - 1)
					- (randrange - 1);

			if (startind < 0)
				startind = -startind;

			startind += i;

			double bestdiff = -1;
			int srcind = startind;

			for (int j = startind; j < startind + diffsearch; j++) {
				double diff = 1;
				// calculate product of diffs of all nets selected so far (0
				// to i-1)
				for (int k = 0; k < i; k++)
					diff *= nets[j].diffTo(nets[k]);
				if (diff > bestdiff) {
					bestdiff = diff;
					srcind = j;
				}
			}

			AbstractRepresentation temp = nets[i];
			nets[i] = nets[srcind];
			nets[srcind] = temp;
		}
		int elitepos = position;
		position += parameters.getRandom();

		for(int i = elitepos; i < position; i++){
			nets[i].EvolutionStatus = new NNGAEvolutionStatus();
			((NNGAEvolutionStatus) nets[i].EvolutionStatus).createdBy = evolutionFunction.Random;
			numRandom++;
		}
		
		int survivors = position;

		// create mutated copies of selected nets
		for (int i = position; i < position + parameters.getMutate(); i++) {
			int srcind = rndIndex(survivors);
			nets[i] = nets[srcind].clone();
			nets[i].mutate(parameters.getMutationSeverity(), parameters.getMutationProbability(), parameters.getMutationMethod());
			nets[i].EvolutionStatus = new NNGAEvolutionStatus();
			((NNGAEvolutionStatus) nets[i].EvolutionStatus).createdBy = evolutionFunction.Mutation;
			numMutation++;
		}
		position += parameters.getMutate();

		int trials = 100; // used to avoid endless loops
		int ii;
		// create offsprings between two individuals
		for (ii = position; ii < position + parameters.getXOver(); ii++) {
			AbstractRepresentation mother, father;
			SimplePopulation fatherpop;
			int motherindex, fatherindex;// + type

			motherindex = rndIndex(parameters.getElite() + parameters.getRandom());
			mother = nets[motherindex];

			if (populations.size() > 1 && generation % parameters.getInterXOverFreq() == 0) {
				// Xover between different Populations
				fatherpop = populations.get(rndIndex(parameters.getPopulationNumber()));
			} else {
				// Xover within the same Population
				fatherpop = this;
			}

			// search for partner with highest diversity
			int candindex;
			double bestDiff, candDiff;
			AbstractRepresentation cand;

			fatherindex = rndIndex(parameters.getElite() + parameters.getRandom());
			father = fatherpop.getNetArray()[fatherindex];
			bestDiff = mother.diffTo(father);

			for (int j = 0; j < 10; j++) {
				candindex = rndIndex(parameters.getElite() + parameters.getRandom());
				cand = fatherpop.getNetArray()[candindex];
				candDiff = mother.diffTo(cand);

				if (candDiff > bestDiff) {
					fatherindex = candindex;
					father = cand;
					bestDiff = candDiff;
				}
			}

			nets[ii] = mother.clone();

			// create offsprings
			boolean isNew = true;
			nets[ii].xOverWith(father, parameters.getXOverMethod());
			
			// check if this net exist already in the pool
			for (int j = 0; j < ii; j++) {
				if (nets[ii].diffTo(nets[j]) == 0) {
					isNew = false;
					break;
				}
			}
			if (isNew == false) {
				if (trials-- == 0)
					break;
				ii--; // redo this net
			} 
		}
		int posXOver = position;
		position = ii;
		for(int i = posXOver; i < position; i++){
			nets[i].EvolutionStatus = new NNGAEvolutionStatus();
			((NNGAEvolutionStatus) nets[i].EvolutionStatus).createdBy = evolutionFunction.Xover;
			numXOver++;
		}

		// for the remaining slots create some new individuals with random
		// parameters
		if (position + parameters.getRenew() != nets.length)
			System.err.println("Warning! Filling "
					+ (nets.length - (position + parameters.getRenew()))
					+ " positions with random candidates.");
		for (int i = position; i < nets.length; i++) {
			try {
				nets[i] = genNewNetwork();
				nets[i].EvolutionStatus = new NNGAEvolutionStatus();
				((NNGAEvolutionStatus) nets[i].EvolutionStatus).createdBy = evolutionFunction.Renew;
				numRenew++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		position += parameters.getRenew();

		readNetArray(nets); // write back netarray to population
	}
	
	/** Returns a random index within the given range */
	private int rndIndex(int range) {
		if (range < 1)
			return 0;

		return parameters.getGenerator().nextInt(Integer.MAX_VALUE) % range;
	}

	public void setParameter(String key, String value) {
		for (AbstractRepresentation representation: members) {
			 representation.setProperty(key, value);			 
		}		
	}

}
