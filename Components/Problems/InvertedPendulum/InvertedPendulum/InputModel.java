package InvertedPendulum;

public enum InputModel {
	/** Angle, speed of the pendulum, speed of the cart and the position from the starting point.*/
    ANGLE_VPENDULUM_VSLIDE_POSITION,
    /** Angle, speed of the pendulum and the speed of the cart.*/
    ANGLE_VPENDULUM_VSLIDE,
    /** Angle and the speed of the pendulum.*/
    ANGLE_VPENDULUM,
    /** Only angle the angle of the pendulum.*/
    ANGLE
}
