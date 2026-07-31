package package_main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Steuerung implements KeyListener {
	
	public boolean oben, unten, links, rechts, interaktion;
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (code == KeyEvent.VK_W) {oben = true;}
		if (code == KeyEvent.VK_S) {unten = true;}
		if (code == KeyEvent.VK_A) {links = true;}
		if (code == KeyEvent.VK_D) {rechts = true;}
		if (code == KeyEvent.VK_E) {interaktion = true;}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (code == KeyEvent.VK_W) {oben = false;}
		if (code == KeyEvent.VK_S) {unten = false;}
		if (code == KeyEvent.VK_A) {links = false;}
		if (code == KeyEvent.VK_D) {rechts = false;}
		if (code == KeyEvent.VK_E) {interaktion = false;}
	}
}
