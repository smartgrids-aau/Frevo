package pong;

/**
 * Base object for all objects on a pong field  
 * 
 * @author Sergii Zhevzhyk
 *
 */
public abstract class FieldObject {
	// position of the object
	private int x;
	private int y;
	
	// bound coordinates
	private int left;
	private int right;
	private int top;
	private int bottom;
	
	private PongParameters parameters;
	
	public FieldObject(PongParameters parameters){
		this.parameters = parameters;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	
	public int getRight() {
		return right;
	}
	public void setRight(int right) {
		this.right = right;
	}
	
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	
	public int getBottom() {
		return bottom;
	}
	public void setBottom(int bottom) {
		this.bottom = bottom;
	}
	
	public PongParameters getParameters() {
		return this.parameters;
	}	
	
	public abstract int getWidth(); 
	
	public abstract int getHeight();
	
	public void setPosition(int x, int y)
	{
		this.x = x;
	    this.y = y;
	}
}

