package package_main;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;

public class Gamepanel extends JPanel implements Runnable{
	
	final int originalTileSize = 16;
	final int scale = 3;
	final int tileSize = originalTileSize * scale;
	
	final int maxScreenCol = 16;
	final int maxScreenRow = 9;
	final int screenWidth = tileSize * maxScreenCol;
	final int screenHeight = tileSize * maxScreenRow;
	
	int playerx = 100;
	int playery = 100;
	int figurSpeed = 4;
	Steuerung steuerung = new Steuerung();
	Thread gameThread;
	
	public Gamepanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.addKeyListener(steuerung);
		this.setFocusable(true);
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		double drawInterval = 1000000000 / 60;
		double nextDrawTime = System.nanoTime() + drawInterval;
		
		while (gameThread != null) {
			update();
			repaint();
			
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime / 1000000;
				
				if(remainingTime < 0) {
					remainingTime = 0;
				}

			Thread.sleep((long)remainingTime);
			nextDrawTime += drawInterval;
			}
		catch(InterruptedException e) {
			e.printStackTrace();
			}
		}
	}
	
	public void update() {
		if(steuerung.oben == true) {
			playery = playery - figurSpeed;
		}
		
		if(steuerung.unten == true) {
			playery = playery + figurSpeed;
		}
		
		if(steuerung.links == true) {
			playerx = playerx - figurSpeed;
		}
		
		if(steuerung.rechts == true) {
			playerx = playerx + figurSpeed;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(Color.WHITE);
		g2.fillRect(playerx, playery, tileSize, tileSize);
		
		g2.dispose();
	}
}