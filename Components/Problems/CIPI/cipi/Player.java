package cipi;

import core.AbstractRepresentation;

public class Player {

	AbstractRepresentation representation;
	float currentValue;
	

	Player(AbstractRepresentation representation, float startupTokens) {
		this.representation = representation;
		this.currentValue = startupTokens;
	}


}
