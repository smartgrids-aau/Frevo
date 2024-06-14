package neat.activationfunction;

import net.jodk.lang.FastMath;


public class TanhFunction implements ActivationFunction {

	private static final long serialVersionUID = 7327183018841168894L;

	/**
	 * @see org.neat4j.ailibrary.nn.core.ActivationFunction#activate(double)
	 */
	public double activate(double neuronIp) {
		double op;
		if (neuronIp < -20) {
			op = -1;
		} else if (neuronIp > 20) {
			op = 1;
		} else {
			op = (1 - FastMath.exp(-2 * neuronIp)) / (1 + FastMath.exp(-2 * neuronIp));
		}
		return (op);
	}

	public double derivative(double neuronIp) {
		double deriv = 0;
		deriv = (1 - Math.pow(neuronIp, 2));
		return (deriv);
	}
	
	public String toString() {
		return "TanhFunction";
	}
}