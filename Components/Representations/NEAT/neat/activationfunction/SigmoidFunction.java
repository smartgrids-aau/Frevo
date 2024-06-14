package neat.activationfunction;

import net.jodk.lang.FastMath;

public class SigmoidFunction implements ActivationFunction {

	private static final long serialVersionUID = -400651150346165504L;
	private double factor;

	public SigmoidFunction() {
		this(-4.9);
	}

	public SigmoidFunction(double factor) {
		this.factor = factor;
	}

	/**
	 * Returns +/- 1
	 * 
	 * @see org.neat4j.ailibrary.nn.core.ActivationFunction#activate(double)
	 */
	public double activate(double neuronIp) {
		return (1.0 / (1.0 + FastMath.exp(this.factor * neuronIp)));
	}

	public double derivative(double neuronIp) {
		return (neuronIp * (1 - neuronIp));
	}

	public void setFactor(double mod) {
		this.factor = mod;
	}
	
	public String toString() {
		return "SigmoidFunction";
	}
}
