package CEA2D;

import java.util.Hashtable;

// import net.jodk.lang.FastMath;
import core.XMLFieldEntry;
import utils.NESRandom;

/**
 * Storage of parameters for {@link CEA2D} method.
 * 
 * @author Sergii Zhevzhyk
 */
public class Parameters {

	/**
	 * number of representations in the population
	 */
	public int POPULATIONSIZE;
	/**
	 * mode how the neighborhood of a member is defined
	 */
	public int NEIGHBOURHOODMODE;
	/**
	 * number of generations
	 */
	public int GENERATIONS;
	/**
	 * interval between two intermediate saves
	 */
	public int SAVEINTERVAL;

	/**
	 * defines the shape of the curve which represents the correlation between the
	 * rank of the fitness in the Neighborhood and the severity of the mutation.
	 * This curve has the formula f=100*r^a. Where f is the severity of mutation, a
	 * is MUTATIONSEVERITYCURVE and r is the rank of the fitness in the neighborhood
	 * divided by the number of neighbors
	 */
	public int NUMBEROFNEIGHBORS;

	/**
	 * defines how many Members are elite. The percentage of elite-members is
	 * probably not that high because if a member is elite is not calculated over
	 * the whole field but only in his neighborhood. And so it is possible, that a
	 * member is above this percentage in the neighborhood of one of his neighbours
	 * but not in his own
	 */
	public float PERCENTELITE;

	/**
	 * defines how many Generations an elite-member must exist
	 */
	public int MINIMUMLIFETIMEELITE;

	/**
	 * defines the severity of the mutation</br>
	 * 0 ...... representation does not change</br>
	 * 100 .. a totally new representation is generated
	 */
	public float MUTATIONSEVERITY;

	/**
	 * defines the probability of the mutation</br>
	 * 0 ...... representation does not change</br>
	 * 1 .. everything changes
	 */
	public float MUTATIONPROBABILITY;

	/**
	 * defines how many representations that are not elite create a mutation of a
	 * random elite-neighbor
	 */
	public int PERCENTMUTATEELITE;

	/**
	 * defines how many representations that are not elite create an offspring with
	 * a random elite-neighbor
	 */
	public int PERCENTXOVERELITE;

	/**
	 * The method which is using the current parameters
	 */
	private CEA2D method;

	public Parameters(CEA2D method) {
		if (method == null) {
			throw new NullPointerException();
		}

		this.method = method;
	}

	// rectangular grid for representation
	public int POPULATIONFIELDSIZE_HEIGHT;
	public int POPULATIONFIELDSIZE_WIDTH;

	// obstacles in the grid for harder evolution
	public int OBSTACLE_PATTERN;
	public int OBSTACLES;

	/**
	 * Initialize parameters from method's properties
	 * 
	 * @param properties properties of the method
	 */
	public void initialize(Hashtable<String, XMLFieldEntry> properties) {
		// Get properties

		// define the length of the population grid
		XMLFieldEntry pop_height = properties.get("populationsize_height");
		POPULATIONFIELDSIZE_HEIGHT = Integer.parseInt(pop_height.getValue());

		// define the width of the population grid
		XMLFieldEntry pop_width = properties.get("populationsize_width");
		POPULATIONFIELDSIZE_WIDTH = Integer.parseInt(pop_width.getValue());

		XMLFieldEntry neighborhoodmode = properties.get("neighbourhoodmode");
		NEIGHBOURHOODMODE = Integer.parseInt(neighborhoodmode.getValue());

		XMLFieldEntry generations = properties.get("generations");
		GENERATIONS = Integer.parseInt(generations.getValue());

		XMLFieldEntry saveint = properties.get("saveinterval");
		SAVEINTERVAL = Integer.parseInt(saveint.getValue());

		XMLFieldEntry percentelite = properties.get("percentelite");
		PERCENTELITE = Integer.parseInt(percentelite.getValue());

		XMLFieldEntry mutationseverity = properties.get("mutationseverity");
		MUTATIONSEVERITY = Float.parseFloat(mutationseverity.getValue());

		XMLFieldEntry mutationprobability = properties.get("mutationprobability");
		MUTATIONPROBABILITY = Float.parseFloat(mutationprobability.getValue());

		XMLFieldEntry percentmutateelite = properties.get("percentmutateelite");
		PERCENTMUTATEELITE = Integer.parseInt(percentmutateelite.getValue());

		XMLFieldEntry percentxoverelite = properties.get("percentxoverelite");
		PERCENTXOVERELITE = Integer.parseInt(percentxoverelite.getValue());

		// predefined obstacle-patterns
		XMLFieldEntry obstacle_pattern = properties.get("obstacle-pattern");
		OBSTACLE_PATTERN = Integer.parseInt(obstacle_pattern.getValue());

		// number of obstacles
		XMLFieldEntry random_obstacles = properties.get("random obstacles");
		OBSTACLES = Integer.parseInt(random_obstacles.getValue());

		// calculate the area of the grid
		POPULATIONSIZE = (int) POPULATIONFIELDSIZE_HEIGHT * POPULATIONFIELDSIZE_WIDTH;

	}

	/**
	 * Gets the generator of random numbers
	 * 
	 * @return the instance of {@link NESRandom} class for generating of random
	 *         numbers
	 */
	public NESRandom getGenerator() {
		NESRandom generator = method.getRandom();
		if (generator == null) {
			throw new NullPointerException();
		}
		return generator;
	}
}
