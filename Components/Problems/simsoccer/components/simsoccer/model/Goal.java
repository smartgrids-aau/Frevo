package components.simsoccer.model;


import java.awt.geom.Point2D;


public class Goal extends FieldObject {

	public int side;
	
	public Goal (Point2D.Double pos, int side) {
		super(pos);
		this.side = side;
	}
	
	public int getType() {
		return 4;
	}
	
	public int getSide() {
		return side;
	}
}
