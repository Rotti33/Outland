package package_main;

import javax.swing.JFrame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class Main {

	public static void main(String[] args) {
		
		JFrame bildschirm = new JFrame();
		bildschirm.setTitle("Outland");
		bildschirm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bildschirm.setUndecorated(true);
		
		Gamepanel gamepanel = new Gamepanel();
		bildschirm.add(gamepanel);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		
		gd.setFullScreenWindow(bildschirm);
		
		bildschirm.setVisible(true);
		bildschirm.setIconImage(new javax.swing.ImageIcon("ima/Testicon.png").getImage());
	}
}