package neat.activationfunction;


public class LinearFunction implements ActivationFunction {

	private static final long serialVersionUID = -4247260705166478250L;

	public double activate(double neuronIp) {
		return (neuronIp);
	}

	public double derivative(double neuronIp) {
		return (neuronIp);
	}
	
	public String toString() {
		return "LinearFunction";
	}

}
