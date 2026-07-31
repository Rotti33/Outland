package package_main;

import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
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
	
	int[][] worldBuilding = new int[maxScreenCol][maxScreenRow];
	
	BufferedImage playerImage;
	Steuerung steuerung = new Steuerung();
	Thread gameThread;
	
	int fps = 0;
	int fpsCounter = 0;
	long timer = 0;
	
	boolean kannUmgraben = true;
	
	public Gamepanel() {
		
		try {
			playerImage = ImageIO.read(getClass().getResourceAsStream("/player.png"));
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
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

		for (int col = 0; col < maxScreenCol; col++) {
			for (int row = 0; row < maxScreenRow; row++) {
				worldBuilding[col][row] = 0;
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
		
		if (steuerung.interaktion == true) {
			
			if (kannUmgraben == true) {
				
				int spielerMitteX = playerx + (tileSize / 2);
				int spielerMitteY = playery + (tileSize / 2);
				
				int aktuelleSpalte = spielerMitteX / tileSize;
				int aktuelleZeile = spielerMitteY / tileSize;
				
				if (aktuelleSpalte >= 0 && aktuelleSpalte < maxScreenCol && aktuelleZeile >= 0 && aktuelleZeile < maxScreenRow) {		
					if (worldBuilding[aktuelleSpalte][aktuelleZeile] == 0) {
						worldBuilding[aktuelleSpalte][aktuelleZeile] = 1;
					}
				}
				
				kannUmgraben = false;
			}
			
		} else {
			kannUmgraben = true;
		}
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		for (int col = 0; col < maxScreenCol; col++) {
			for (int row = 0; row < maxScreenRow; row++) {
				
				int x = col * tileSize;
				int y = row * tileSize;
				
				if (worldBuilding[col][row] == 0) {
					g2.setColor(new Color(34, 139, 34));
				}
				else if (worldBuilding[col][row] == 1) {
					g2.setColor(new Color(139, 69, 19));
				}
				
				g2.fillRect(x, y, tileSize, tileSize);
			}
		}
		
		g2.setColor(Color.DARK_GRAY);
		
		for (int x = 0; x < screenWidth; x = x + tileSize) {
			g2.drawLine(x, 0, x, screenHeight);
		}
		
		for (int y = 0; y < screenHeight; y = y + tileSize) {
			g2.drawLine(0, y, screenWidth, y);
		}
		
		g2.setColor(Color.WHITE);
		if (playerImage != null) {
			g2.drawImage(playerImage, playerx, playery, tileSize, tileSize, null);
		}
		
		g2.setColor(Color.YELLOW);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
		g2.drawString("FPS: " + fps, 700, 20);
		
		g2.dispose();
	}
}