package components.simsoccer.model;



public class Line extends GameObject {

	public int line_id;
	
	/**
	 * Line object
	 * @param lineid 0: top, 1: right, 2: bottom, 3: left
	 */
	public Line(int lineid) {
		line_id = lineid;
	}
	
	public int getLineId() {
		return this.line_id;
	}
	
	public void setLineId(int id) {
		this.line_id = id;
	}
	
	/**
	 * Returns the type of the object
	 * @return 0 if undefined, 1 if player, 2 if ball, 3 if line, 4 if goal
	 */
	public int getType() {
		return 3;
	}
}
