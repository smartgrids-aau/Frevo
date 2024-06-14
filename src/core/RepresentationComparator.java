package core;

import java.util.Comparator;

/**
 * Comparator for representations by fitness values: representation with
 * higher fitness should have the lower index
 */
public class RepresentationComparator implements Comparator<AbstractRepresentation> {
	@Override
	public int compare(AbstractRepresentation arg0, AbstractRepresentation arg1) {
		return Double.compare(arg1.getFitness(), arg0.getFitness());
	}
}