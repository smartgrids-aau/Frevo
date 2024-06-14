package fehervari.evopco;

public class Message {
	
	private String content;
	
	public Message(String content) {
		this.content = content;
	}
	
	public Message(int nodeid) {
		this.content = Integer.toString(nodeid);
	}
	
	public String getContent() {
		return this.content;
	}
}
