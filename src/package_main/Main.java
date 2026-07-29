package package_main;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		
		JFrame bildschirm = new JFrame();
		bildschirm.setTitle("Outland");
		bildschirm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bildschirm.setResizable(false);
		
		Gamepanel gamepanel = new Gamepanel();
		bildschirm.add(gamepanel);
		bildschirm.pack();
		
		bildschirm.setLocationRelativeTo(null);
		bildschirm.setVisible(true);
		bildschirm.setIconImage(new javax.swing.ImageIcon("ima/Testicon.png").getImage());
	}
}