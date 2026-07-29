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
	
	int fps = 0;
	int fpsCounter = 0;
	long timer = 0;
	
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
			
			fpsCounter++;
			
			timer += drawInterval;
			
			if(timer > 1000000000) {
				fps = fpsCounter;
				fpsCounter = 0;
				timer = 0;
			}
		}
	}
	
	public void update() {
		
		if (steuerung.oben == true) {
			playery = playery - figurSpeed;
			
			if (playery < 0) {
				playery = 0;
			}
		}
		
		if (steuerung.unten == true) {
			playery = playery + figurSpeed;
			
			if (playery > screenHeight - tileSize) {
				playery = screenHeight - tileSize;
			}
		}

		if (steuerung.links == true) {
			playerx = playerx - figurSpeed;
			
			if (playerx < 0) {
				playerx = 0;
			}
		}
		
		if (steuerung.rechts == true) {
			playerx = playerx + figurSpeed;
			
			if (playerx > screenWidth - tileSize) {
				playerx = screenWidth - tileSize;
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(Color.DARK_GRAY);
		
		for (int x = 0; x < screenWidth; x = x + tileSize) {
			g2.drawLine(x, 0, x, screenHeight);
		}
		
		for (int y = 0; y < screenHeight; y = y + tileSize) {
			g2.drawLine(0, y, screenWidth, y);
		}
		
		g2.setColor(Color.WHITE);
		g2.fillRect(playerx, playery, tileSize, tileSize);
		
		g2.setColor(Color.YELLOW);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
		g2.drawString("FPS: " + fps, 700, 20);
		
		g2.dispose();
	}
}