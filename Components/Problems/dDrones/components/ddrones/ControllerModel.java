package components.ddrones;

/**
 * method 1: neural network (evolved), cooperative with 4 sensors<br>
 * method 2: random walk<br>
 * method 3: random direction<br>
 * method 4: belief-based approach<br>
 * method 5: neural network (evolved), non-cooperative<br>
 * method 6: neural network (evolved), cooperative with 8 sensors<br>
 * method 7: neural network (evolved), cooperative with 24 sensors
 * method 8: neural network (evolved), with range sensors
 */
public enum ControllerModel {
	Random_Walk,
	Random_Direction,
	Belief_based,
	Evolved_non_cooperative,
	Evolved_cooperative_4_sensors,
	Evolved_cooperative_8_sensors,
	Evolved_cooperative_24_sensors,
	Evolved_range_sensors,
	Random_direction_border_avoiding
	
}
