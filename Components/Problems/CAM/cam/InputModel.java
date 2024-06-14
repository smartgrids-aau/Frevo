package cam;

public enum InputModel {
	/**
	 * Input model where only the Von Neumann environment (=4 neighbors) is considered two
	 * times: first the color then the cell output
	 */
	COLOR_OUTPUT_V_NEUMANN,
	/**
	 * Input model where all 8 neighbors' outputs are considered and also their
	 * color
	 */
	COLOR_OUTPUT_MOORE,
	/** Input model where all 8 neighbors are considered but only their output */
	OUTPUT_MOORE,
	/** Input model where only the Von Neumann environment (=4 neighbors) is
	 * considered but only their output */
	OUTPUT_V_NEUMANN,
	/** Input model where all 8 neighbors are
	 * considered but only their colors */
	COLOR_MOORE,
	/** Input model where only the Von Neumann environment (=4 neighbors) is
	 * considered but only their color */
	COLOR_V_NEUMANN
}
