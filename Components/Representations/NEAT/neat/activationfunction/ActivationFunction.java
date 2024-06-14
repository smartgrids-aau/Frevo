package neat.activationfunction;

import java.io.Serializable;

public interface ActivationFunction extends Serializable {
	public double activate(double neuronIp);

	public double derivative(double neuronIp);
	
	public String toString();
}
