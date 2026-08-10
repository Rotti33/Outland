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
	
	String blickRichtung = "unten";	
	
	int[][] worldBuilding = new int[maxScreenCol][maxScreenRow];
	int[][] wachstumsTimer = new int[maxScreenCol][maxScreenRow];
	
	BufferedImage playerImage;
	Steuerung steuerung = new Steuerung();
	Thread gameThread;
	
	int fps = 0;
	int fpsCounter = 0;
	long timer = 0;
	
	boolean kannUmgraben = true;
	
	Tile[] kachelTypen = new Tile[10];
	
	int samenAnzahl = 5;
	int tomaten = 0;
	int gold = 0;
	
	public Gamepanel() {
		
		ladeKachelBilder();
		
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
			blickRichtung = "oben";
			if (playery < 0) {
				playery = 0;
			}
		}
		
		if (steuerung.unten == true) {
			playery = playery + figurSpeed;
			blickRichtung = "unten";
			if (playery > screenHeight - tileSize) {
				playery = screenHeight - tileSize;
			}
		}

		if (steuerung.links == true) {
			playerx = playerx - figurSpeed;
			blickRichtung = "links";
			if (playerx < 0) {
				playerx = 0;
			}
		}
		
		if (steuerung.rechts == true) {
			playerx = playerx + figurSpeed;
			blickRichtung = "rechts";
			if (playerx > screenWidth - tileSize) {
				playerx = screenWidth - tileSize;
			}
		}
		
		if (steuerung.interaktion == true) {			
			if (kannUmgraben == true) {
				
				int spielerSpalte = (playerx + (tileSize / 2)) / tileSize;
				int spielerZeile = (playery + (tileSize / 2)) / tileSize;
				
				if (spielerSpalte == 15 && spielerZeile == 0) {
					if (tomaten > 0) {
						gold = gold + (tomaten * 10);
						tomaten = 0;
					}
				} 
				else {
					int zielX = playerx + (tileSize / 2);
					int zielY = playery + (tileSize / 2);
					
					if(blickRichtung.equals("oben")) { zielY = zielY - tileSize; }
					else if(blickRichtung.equals("unten")) { zielY = zielY + tileSize; }
					else if(blickRichtung.equals("links")) { zielX = zielX - tileSize; }
					else if(blickRichtung.equals("rechts")) { zielX = zielX + tileSize; }
					
					int aktuelleSpalte = zielX / tileSize;
					int aktuelleZeile = zielY / tileSize;
					
					if (aktuelleSpalte >= 0 && aktuelleSpalte < maxScreenCol && aktuelleZeile >= 0 && aktuelleZeile < maxScreenRow) {		
						if (worldBuilding[aktuelleSpalte][aktuelleZeile] == 0) {
							worldBuilding[aktuelleSpalte][aktuelleZeile] = 1;
						}
						else if (worldBuilding[aktuelleSpalte][aktuelleZeile] == 1 && samenAnzahl > 0) {
							worldBuilding[aktuelleSpalte][aktuelleZeile] = 2;
							samenAnzahl = samenAnzahl - 1;
						}
						else if (worldBuilding[aktuelleSpalte][aktuelleZeile] == 3) {
							worldBuilding[aktuelleSpalte][aktuelleZeile] = 1;
							tomaten = tomaten + 1;
						}
					}
				}
				
				kannUmgraben = false;
			}
			
		} else {
			kannUmgraben = true;
		}
		
		for (int col = 0; col < maxScreenCol; col++) {
			for (int row = 0; row < maxScreenRow; row++) {
				
				if (worldBuilding[col][row] == 2) {
					
					wachstumsTimer[col][row]++;
					
					// 60 Runden = 1 Sekunde. 300 Runden = 5 Sekunden!
					if (wachstumsTimer[col][row] >= 300) {
						worldBuilding[col][row] = 3;
						wachstumsTimer[col][row] = 0;
					}
				}
			}
		}
	}
	
	public void ladeKachelBilder() {
		try {
			kachelTypen[0] = new Tile();
			kachelTypen[0].image = ImageIO.read(getClass().getResourceAsStream("/gras.Test.png"));
			
			kachelTypen[1] = new Tile();
			kachelTypen[1].image = ImageIO.read(getClass().getResourceAsStream("/erde.Test.png"));
			
			kachelTypen[2] = new Tile();
			kachelTypen[2].image = ImageIO.read(getClass().getResourceAsStream("/erde.Test.png"));
			
			kachelTypen[3] = new Tile();
			kachelTypen[3].image = ImageIO.read(getClass().getResourceAsStream("/erde.Test.png"));
			
		} catch (Exception e) {
			e.printStackTrace();
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
				
				int kachelNummer = worldBuilding[col][row];
				
				if (kachelTypen[kachelNummer] != null && kachelTypen[kachelNummer].image != null) {
					g2.drawImage(kachelTypen[kachelNummer].image, x, y, tileSize, tileSize, null);
				}
				
				if (kachelNummer == 2) {
					g2.setColor(Color.YELLOW);
					g2.fillOval(x + 18, y + 18, 12, 12); 
				}
				
				if (kachelNummer == 3) {
					g2.setColor(Color.GREEN);
					g2.fillRect(x + 14, y + 10, 20, 28); 
				}
				
				if (col == 15 && row == 0) {
					g2.setColor(Color.BLUE);
					g2.fillRect(x, y, tileSize, tileSize);
					
					g2.setColor(Color.WHITE);
					g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
					g2.drawString("MARKT", x + 6, y + 26);
				}
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
		
		int selectorX = playerx + (tileSize / 2);
		int selectorY = playery + (tileSize / 2);
		
		if (blickRichtung.equals("oben")) {
			selectorY = selectorY - tileSize;
		}
		else if (blickRichtung.equals("unten")) {
			selectorY = selectorY + tileSize;
		}
		else if (blickRichtung.equals("links")) {
			selectorX = selectorX - tileSize;
		}
		else if (blickRichtung.equals("rechts")) {
			selectorX = selectorX + tileSize;
		}
		
		int selectorCol = selectorX / tileSize;
		int selectorRow = selectorY / tileSize;
		
		if (selectorCol >= 0 && selectorCol < maxScreenCol && selectorRow >= 0 && selectorRow < maxScreenRow) {
			
			g2.setColor(Color.CYAN);
			g2.drawRect(selectorCol * tileSize, selectorRow * tileSize, tileSize, tileSize);
		}
		
		g2.setColor(Color.YELLOW);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
		g2.drawString("FPS: " + fps, 700, 20);
		
		g2.setColor(Color.ORANGE);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
		g2.drawString("Samen im Rucksack: " + samenAnzahl, 20, 410);
		
		g2.setColor(Color.RED);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
		g2.drawString("Geerntete Tomaten: " + tomaten, 20, 385);
		
		g2.setColor(Color.YELLOW);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
		g2.drawString("Gold: " + gold + "g", 20, 360);
		
		g2.dispose();
	}
}