package neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import core.XMLFieldEntry;

/**
 * Provides the NEAT innovation database as described in Kenneth Stanley's NEAT
 * papers The innovations are kept for the entire life of the run.
 * 
 * @author MSimmerson (NEAT4J)
 * 
 */
public class InnovationDatabase {

	static InnovationDatabase database = new InnovationDatabase();

	private Random generator;

	/** Maps linkinnovationID -> NodeInnovation */
	private HashMap<Integer, NEATNodeInnovation> nodeinnovations;
	private HashMap<Pair, NEATLinkInnovation> linkinnovations;

	private static int innovationId = 1;
	private static int neuronId = 1;

	/** Indicates how many time we try to add an existing innovation. */
	public static int hits = 0;
	public static int misses = 0;

	private NEATChromosome template;

	private InnovationDatabase() {
		this.nodeinnovations = new HashMap<Integer, NEATNodeInnovation>();
		this.linkinnovations = new HashMap<Pair, NEATLinkInnovation>();
		innovationId = 1;
		neuronId = 1;
		hits = 0;
		misses = 0;
	}

	/** Resets the innovation database. */
	static void reset() {
		database = new InnovationDatabase();
	}

	/**
	 * Singleton accessor
	 * 
	 * @return
	 */
	public static InnovationDatabase database() {
		return (database);
	}

	public NEATChromosome getTemplate() {
		return template;
	}

	/**
	 * @param featureSelection
	 *            - if true assigns one connection to each output to a random
	 *            input. If false, all inputs are connected to all outputs
	 * @param extraFeatureCount
	 *            - creates chromosome features that are non structural, ie they
	 *            have no bearing on the creation of the net they represent
	 *            evolving data inputs.
	 **/
	NEATChromosome createTemplate(int inputs, int outputs,
			Hashtable<String, XMLFieldEntry> properties, Random rand) {

		if (generator == null)
			generator = rand;

		int i;

		ArrayList<NEATNodeGene> nodes = new ArrayList<NEATNodeGene>(inputs
				+ outputs);

		ArrayList<NEATLinkGene> links;
		ArrayList<NEATFeatureGene> features = new ArrayList<NEATFeatureGene>(0);

		for (i = 0; i < inputs; i++) {
			nodes.add(this.createNewNodeGene(NEATNodeGene.INPUT));
		}

		for (i = inputs; i < (inputs + outputs); i++) {
			nodes.add(this.createNewNodeGene(NEATNodeGene.OUTPUT));
		}

		// start with each input to each output, doesn't allow feature
		// selection (DEFAULT)
		links = new ArrayList<NEATLinkGene>(inputs * outputs);
		for (int oi = 0; oi < outputs; oi++) {
			for (int ii = 0; ii < inputs; ii++) {
				NEATLinkGene lg = this.submitLinkInnovation(nodes.get(ii).id(),
						nodes.get(inputs + oi).id());

				lg.setWeight(MathUtils.nextPlusMinusOne(generator));
				links.add(lg);
			}
		}

		template = createNEATChromosome(nodes, links, features);

		return template;
	}

	/** Returns a random chromosome, structure based on the template */
	public NEATChromosome individualFromTemplate() {
		int i;
		ArrayList<NEATGene> templateGenes = template.genes();
		ArrayList<NEATGene> individualGenes = new ArrayList<NEATGene>(
				templateGenes.size());
		NEATNodeGene nodeGene;
		NEATLinkGene linkGene;
		NEATFeatureGene featureGene;

		for (i = 0; i < templateGenes.size(); i++) {
			if (templateGenes.get(i) instanceof NEATNodeGene) {
				nodeGene = (NEATNodeGene) templateGenes.get(i);
				individualGenes.add(new NEATNodeGene(nodeGene
						.getInnovationNumber(), nodeGene.id(), MathUtils
						.nextPlusMinusOne(generator), nodeGene.getType(),
						MathUtils.nextDouble(generator)));
			} else if (templateGenes.get(i) instanceof NEATLinkGene) {
				linkGene = (NEATLinkGene) templateGenes.get(i);
				individualGenes.add(new NEATLinkGene(linkGene
						.getInnovationNumber(), true, linkGene.getFromId(),
						linkGene.getToId(), MathUtils
								.nextPlusMinusOne(generator)));
			} else if (templateGenes.get(i) instanceof NEATFeatureGene) {
				featureGene = (NEATFeatureGene) templateGenes.get(i);
				individualGenes
						.add(new NEATFeatureGene(featureGene
								.getInnovationNumber(), MathUtils
								.nextDouble(generator)));
			}
		}

		return (new NEATChromosome(individualGenes));
	}

	private NEATChromosome createNEATChromosome(ArrayList<NEATNodeGene> nodes,
			ArrayList<NEATLinkGene> links, ArrayList<NEATFeatureGene> features) {
		
		ArrayList<NEATGene> genes = new ArrayList<NEATGene>(nodes.size() + links.size()
				+ features.size());
		
		genes.addAll(nodes);
		genes.addAll(links);
		genes.addAll(features);

		return (new NEATChromosome(genes));
	}

	/**
	 * Should be called only at templatecreation since this does not add
	 * nodeinnovation
	 */
	private NEATNodeGene createNewNodeGene(int type) {
		// get next innovation number
		int nodeID = this.nextNodeNumber();

		// create corresponding gene
		NEATNodeGene nodeGene = new NEATNodeGene(-1, nodeID,
				generator.nextDouble(), type,
				MathUtils.nextPlusMinusOne(generator));

		return (nodeGene);
	}

	/**
	 * Submits a link insertion mutation to the database. If it does not exist,
	 * it creates it and adds it to the database. It returns the database entry
	 * 
	 * @param from
	 *            - from node identifier
	 * @param to
	 *            - to node identifier
	 * @return - Created link gene
	 */
	public NEATLinkGene submitLinkInnovation(int fromId, int toId) {
		// find innovation in the database
		NEATLinkInnovation databaseEntry = this
				.findLinkInnovation(fromId, toId);

		if (databaseEntry == null) {
			misses++;
			// create new link innovation
			int innovationNumber = this.nextInnovationNumber();
			databaseEntry = new NEATLinkInnovation(fromId, toId,
					innovationNumber);
			this.linkinnovations.put(new Pair(fromId, toId),
					databaseEntry);
		} else {
			hits++;
		}
		// the 0 weight is a place holder
		NEATLinkGene gene = new NEATLinkGene(databaseEntry.innovationId(),
				true, fromId, toId, 0);

		return (gene);
	}

	/**
	 * Returns the link innovation corresponding to the give from-to pair or
	 * null if not found.
	 */
	private NEATLinkInnovation findLinkInnovation(int fromId, int toId) {
		Pair p = new Pair(fromId, toId);

		return linkinnovations.get(p);
	}

	private int nextInnovationNumber() {
		return (innovationId++);
	}

	private int nextNodeNumber() {
		return (neuronId++);
	}

	private NEATNodeInnovation findNodeInnovation(NEATLinkGene link) {
		return findNodeInnovation(link.getInnovationNumber());
	}

	/** Returns the nodeinnovation corresponding to the id or null if not found. */
	private NEATNodeInnovation findNodeInnovation(int linkInnovationId) {
		return nodeinnovations.get(linkInnovationId);
	}

	/**
	 * Requests a new node from the database if present on the given link.
	 * Returns a new one if not found.
	 */
	public NEATNodeGene submitNodeInnovation(NEATLinkGene linkGene) {

		// check if this innovation is present in the database
		NEATNodeInnovation databaseEntry = findNodeInnovation(linkGene);

		if (databaseEntry == null) {
			// create new innovation
			misses++;
			int innovationNumber = this.nextInnovationNumber();
			databaseEntry = new NEATNodeInnovation(innovationNumber);
			databaseEntry.setLinkInnovationId(linkGene.getInnovationNumber());
			databaseEntry.setNodeId(this.nextNodeNumber());

			nodeinnovations.put(linkGene.getInnovationNumber(), databaseEntry);
			databaseEntry.setType(NEATNodeGene.HIDDEN);
		} else {
			hits++;
		}

		// create a new gene based on the innovation
		NEATNodeGene gene = new NEATNodeGene(databaseEntry.innovationId(),
				databaseEntry.getNodeId(), MathUtils.nextDouble(generator),
				databaseEntry.type(), MathUtils.nextPlusMinusOne(generator));

		return (gene);		
	}

	/** Returns the size of the innovation lists (nodes+links) */
	public int getSize() {
		return nodeinnovations.size() + linkinnovations.size();
	}
}
