package CEA2D;

import java.util.ArrayList;

import core.AbstractRepresentation;
import core.ComponentXMLData;


public class Member {
	static long nextid = 0;
	long id;
 	double diff;
	AbstractRepresentation rep;
	ArrayList<Member> neighbors;
	private replaceFunction createdBy;
	
	public replaceFunction getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(replaceFunction createdBy) {
		this.createdBy = createdBy;
	}

	public enum replaceFunction{ELITE, MUTATE, XOVER, RENEW};
	
	/**
	 * Create a new member with a new unique ID
	 * 
	 * @param representation
	 *            ComponentXMLdata which is used to create the Members.
	 * @param parameters
	 *            Instance holds the properties for each member.
	 */
	public Member(ComponentXMLData representation, Parameters parameters, int inputnumber, int outputnumber) {
		id = nextid;
		nextid++;
		neighbors = new ArrayList<Member>();
		createdBy = replaceFunction.RENEW;
		try {
			rep = representation
					.getNewRepresentationInstance(inputnumber, outputnumber,
							parameters.getGenerator());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a new member with a new unique ID from loaded representation
	 * @param representation 
	 * 				Representation which has been loaded from file.
	 * @param parameters 
	 * 				Instance holds the properties for each member.
	 */
	public Member(AbstractRepresentation representation, Parameters parameters) {
		id = nextid;
		nextid++;
		neighbors = new ArrayList<Member>();
		createdBy = replaceFunction.RENEW;
		rep = representation;
		rep.setGenerator(parameters.getGenerator());
	}
	
	@Override
	public String toString() {
		return rep.getHash() + "\t" + rep.getFitness();
	}
}
