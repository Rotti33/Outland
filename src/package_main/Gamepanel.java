package package_main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Gamepanel extends JPanel implements Runnable {
	
	// KACHEL-EINSTELLUNGEN
	final int originalTileSize = 16;
	final int scale = 3;
	final int tileSize = originalTileSize * scale;
	
	// 1. MONITOR-GRÖSSE (Das sichtbare Fenster auf dem Desktop)
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 9;
	public final int screenWidth = tileSize * maxScreenCol;   // 768 Pixel
	public final int screenHeight = tileSize * maxScreenRow;  // 432 Pixel
	
	// 2. WELT-GRÖSSE (Die riesige begehbare Map im Hintergrund)
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;
	public final int worldWidth = tileSize * maxWorldCol;   // 2400 Pixel
	public final int worldHeight = tileSize * maxWorldRow;  // 2400 Pixel
	
	// SPIELER-VARIABLEN (Startet genau im Zentrum der 50x50 Map)
	int playerx = tileSize * 23;
	int playery = tileSize * 23;
	int figurSpeed = 4;
	
	// KAMERA: Pinned die Spielfigur exakt in die Bildschirm-Mitte
	public final int screenX = (screenWidth / 2) - (tileSize / 2);
	public final int screenY = (screenHeight / 2) - (tileSize / 2);
	
	String blickRichtung = "unten";	
	
	// WELT-GEDÄCHTNIS (Bereit für 50x50 Felder)
	int[][] worldBuilding = new int[maxWorldCol][maxWorldRow];
	int[][] wachstumsTimer = new int[maxWorldCol][maxWorldRow];
	
	BufferedImage playerImage;
	Steuerung steuerung = new Steuerung();
	Thread gameThread;
	
	int fps = 0;
	int fpsCounter = 0;
	long timer = 0;
	
	boolean kannUmgraben = true;
	boolean kannEinkaufen = true;
	
	Tile[] kachelTypen = new Tile[10];
	
	int samenAnzahl = 5;
	int tomaten = 0;
	int gold = 0;
	
	public Gamepanel() {
		
		ladeKachelBilder();
		ladeKarte();
		
		try {
			playerImage = ImageIO.read(getClass().getResourceAsStream("/player.png"));
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		// REPARIERT: Das Fenster auf dem Desktop bleibt 768x432 Pixel groß!
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
			blickRichtung = "oben";
			if (playery < 0) {
				playery = 0;
			}
		}
		
		if (steuerung.unten == true) {
			playery = playery + figurSpeed;
			blickRichtung = "unten";
			if (playery > worldHeight - tileSize) {
				playery = worldHeight - tileSize;
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
			if (playerx > worldWidth - tileSize) {
				playerx = worldWidth - tileSize;
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
					
					if (aktuelleSpalte >= 0 && aktuelleSpalte < maxWorldCol && aktuelleZeile >= 0 && aktuelleZeile < maxWorldRow) {		
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
		
		if (steuerung.shop == true) {
			if (kannEinkaufen == true) {
				
				int spielerSpalte = (playerx + (tileSize / 2)) / tileSize;
				int spielerZeile = (playery + (tileSize / 2)) / tileSize;
				
				if (spielerSpalte == 15 && spielerZeile == 0) {
					if (gold >= 2) {
						gold = gold - 2;
						samenAnzahl = samenAnzahl + 1;
					}
				}
				kannEinkaufen = false;
			}
		} else {
			kannEinkaufen = true;
		}
		
		for (int col = 0; col < maxWorldCol; col++) {
			for (int row = 0; row < maxWorldRow; row++) {
				
				if (worldBuilding[col][row] == 2) {
					
					wachstumsTimer[col][row]++;
					
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
	
	public void ladeKarte() {
		try {
			InputStream is = getClass().getResourceAsStream("/map01.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;

			while(col < maxWorldCol && row < maxWorldRow) {
				
				String zeile = br.readLine();
				
				while(col < maxWorldCol) {
					String[] zahlen = zeile.split(" "); 
					int num = Integer.parseInt(zahlen[col]); 
					
					worldBuilding[col][row] = num; 
					col++;
				}
				
				if(col == maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();
			
		} catch (Exception e) {
			System.out.println("FEHLER BEIM KARTEN-LADEN!");
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		// 1. MAP MIT KAMERA-OFFSET ZEICHNEN
		for (int worldCol = 0; worldCol < maxWorldCol; worldCol++) {
			for (int worldRow = 0; worldRow < maxWorldRow; worldRow++) {
				
				int worldX = worldCol * tileSize;
				int worldY = worldRow * tileSize;
				
				int screenXPos = worldX - playerx + screenX;
				int screenYPos = worldY - playery + screenY;
				
				int kachelNummer = worldBuilding[worldCol][worldRow];
				
				if (kachelTypen[kachelNummer] != null && kachelTypen[kachelNummer].image != null) {
					g2.drawImage(kachelTypen[kachelNummer].image, screenXPos, screenYPos, tileSize, tileSize, null);
				}
				
				if (kachelNummer == 2) {
					g2.setColor(Color.YELLOW);
					g2.fillOval(screenXPos + 18, screenYPos + 18, 12, 12); 
				}
				if (kachelNummer == 3) {
					g2.setColor(Color.GREEN);
					g2.fillRect(screenXPos + 14, screenYPos + 10, 20, 28); 
				}
				
				if (worldCol == 15 && worldRow == 0) {
					g2.setColor(Color.BLUE);
					g2.fillRect(screenXPos, screenYPos, tileSize, tileSize);
					g2.setColor(Color.WHITE);
					g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
					g2.drawString("MARKT", screenXPos + 6, screenYPos + 26);
				}
			}
		}
		
		// 2. RASTER MIT KAMERA ZEICHNEN
		g2.setColor(Color.DARK_GRAY);
		for (int worldCol = 0; worldCol < maxWorldCol; worldCol++) {
			int worldX = worldCol * tileSize;
			int screenXPos = worldX - playerx + screenX;
			g2.drawLine(screenXPos, 0, screenXPos, screenHeight);
		}
		for (int worldRow = 0; worldRow < maxWorldRow; worldRow++) {
			int worldY = worldRow * tileSize;
			int screenYPos = worldY - playery + screenY;
			g2.drawLine(0, screenYPos, screenWidth, screenYPos);
		}
		
		// 3. SPIELER FEST IN DER MITTE ZEICHNEN
		g2.setColor(Color.WHITE);
		if (playerImage != null) {
			g2.drawImage(playerImage, screenX, screenY, tileSize, tileSize, null);
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
		
		if (selectorCol >= 0 && selectorCol < maxWorldCol && selectorRow >= 0 && selectorRow < maxWorldRow) {
			
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