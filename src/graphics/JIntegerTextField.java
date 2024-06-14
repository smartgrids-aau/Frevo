package graphics;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class JIntegerTextField extends JTextField {
	private static final long serialVersionUID = -848135456547986252L;
	final static String badchars = "`~!@#$%^&*()_-+=\\|\"':;?/>.<, ";

	public void processKeyEvent(KeyEvent ev) {

		char c = ev.getKeyChar();

		if ((Character.isLetter(c)) || badchars.indexOf(c) > -1) {
			ev.consume();
			return;
		}
		super.processKeyEvent(ev);
	}

	/** Returns the content as integer */
	public int getIntegerText() {
		return Integer.parseInt(this.getText());
	}
}
