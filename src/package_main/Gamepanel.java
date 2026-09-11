package package_main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Gamepanel extends JPanel implements Runnable {
	
	final int originalTileSize = 16;
	final int scale = 3;
	final int tileSize = originalTileSize * scale;
	
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 9;
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	
	public final int screenX = (screenWidth / 2) - (tileSize / 2);
	public final int screenY = (screenHeight / 2) - (tileSize / 2);
	
	int[][] worldBuilding = new int[maxWorldCol][maxWorldRow];
	int[][] wachstumsTimer = new int[maxWorldCol][maxWorldRow];
	
	Steuerung steuerung = new Steuerung();
	Thread gameThread;

	TileManager tileM = new TileManager(this);
	Player spieler = new Player(this, steuerung);
	
	int fps = 0;
	int fpsCounter = 0;
	long timer = 0;
	
	boolean kannUmgraben = true;
	boolean kannEinkaufen = true;
	
	public int gameState;
	public final int titelState = 0;
	public final int spielState = 1;
	public int menuAuswahl = 0;
	
	public Gamepanel() {
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.addKeyListener(steuerung);
		this.setFocusable(true);
		
		gameThread = new Thread(this);
		gameState = titelState;
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
		
		// ========================================================
		// RAGIERUNGS-BEREICH 1: DAS LAUFENDE SPIEL (spielState)
		// ========================================================
		if (gameState == spielState) {
			
			spieler.update();
			
			if (steuerung.interaktion == true) {			
				if (kannUmgraben == true) {
					
					int spielerSpalte = (spieler.worldX + (tileSize / 2)) / tileSize;
					int spielerZeile = (spieler.worldY + (tileSize / 2)) / tileSize;
					
					if (spielerSpalte == 15 && spielerZeile == 0) {
						if (spieler.tomaten > 0) {
							spieler.gold = spieler.gold + (spieler.tomaten * 10);
							spieler.tomaten = 0;
						}
					} 
					else {
						int zielX = spieler.worldX + (tileSize / 2);
						int zielY = spieler.worldY + (tileSize / 2);
						
						if(spieler.blickRichtung.equals("oben")) { zielY = zielY - tileSize; }
						else if(spieler.blickRichtung.equals("unten")) { zielY = zielY + tileSize; }
						else if(spieler.blickRichtung.equals("links")) { zielX = zielX - tileSize; }
						else if(spieler.blickRichtung.equals("rechts")) { zielX = zielX + tileSize; }
						
						int aktuelleSpalte = zielX / tileSize;
						int aktuelleZeile = zielY / tileSize;
						
						if (aktuelleSpalte >= 0 && aktuelleSpalte < maxWorldCol && aktuelleZeile >= 0 && aktuelleZeile < maxWorldRow) {		
							if (worldBuilding[aktuelleSpalte][aktuelleZeile] == 0) {
								worldBuilding[aktuelleSpalte][aktuelleZeile] = 1;
							}
							else if (worldBuilding[aktuelleSpalte][aktuelleZeile] == 1 && spieler.samenAnzahl > 0) {
								worldBuilding[aktuelleSpalte][aktuelleZeile] = 2;
								spieler.samenAnzahl = spieler.samenAnzahl - 1;
							}
							else if (worldBuilding[aktuelleSpalte][aktuelleZeile] == 3) {
								worldBuilding[aktuelleSpalte][aktuelleZeile] = 1;
								spieler.tomaten = spieler.tomaten + 1;
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
					
					int spielerSpalte = (spieler.worldX + (tileSize / 2)) / tileSize;
					int spielerZeile = (spieler.worldY + (tileSize / 2)) / tileSize;
					
					if (spielerSpalte == 15 && spielerZeile == 0) {
						if (spieler.gold >= 2) {
							spieler.gold = spieler.gold - 2;
							spieler.samenAnzahl = spieler.samenAnzahl + 1;
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
		} // <- Hier schließt das spielState-If ganz sauber!
		
		// ========================================================
		// RAGIERUNGS-BEREICH 2: DAS HAUPTMENÜ (titelState)
		// ========================================================
		if (gameState == titelState) {
			
			// NACH OBEN DRÜCKEN (W)
			if (steuerung.oben == true) {
				if (kannUmgraben == true) { 
					menuAuswahl--;
					if (menuAuswahl < 0) {
						menuAuswahl = 1; 
					}
					kannUmgraben = false;
				}
			}
			
			// NACH UNTEN DRÜCKEN (S)
			if (steuerung.unten == true) {
				if (kannUmgraben == true) {
					menuAuswahl++;
					if (menuAuswahl > 1) {
						menuAuswahl = 0; 
					}
					kannUmgraben = false;
				}
			}
			
			// AUSWAHL BESTÄTIGEN (Aktionstaste E)
			if (steuerung.interaktion == true) {
				if (kannUmgraben == true) {
					if (menuAuswahl == 0) {
						gameState = spielState; 
					}
					else if (menuAuswahl == 1) {
						System.exit(0); 
					}
					kannUmgraben = false;
				}
			}
			
			// Wenn keine Taste gedrückt wird, schalten wir die Dauerfeuer-Bremse wieder frei
			if (steuerung.oben == false && steuerung.unten == false && steuerung.interaktion == false) {
				kannUmgraben = true;
			}
		} // <- Hier schließt das titelState-If ganz sauber!
		
	} // <- Hier schließt die gesamte update()-Methode
	
@Override
protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D)g;
	
	double fensterBreite = getWidth();
	double fensterHoehe = getHeight();
	double scaleX = fensterBreite / screenWidth;
	double scaleY = fensterHoehe / screenHeight;
	g2.scale(scaleX, scaleY);
	
	// ========================================================
	// ZUSTAND 0: HAUPTMENÜ (TITELBILD) ZEICHNEN
	// ========================================================
	if (gameState == titelState) {
		
		// 1. Hintergrund: Tiefschwarz einfärben
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, screenWidth, screenHeight);
		
		// 2. Spieletitel zeichnen
		g2.setColor(Color.WHITE);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
		String titelText = "OUTLAND";
		g2.drawString(titelText, 260, 150); // Zentriert auf der 768px Leinwand
		
		// 3. Option 1: Spiel starten
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
		String startText = "Spiel starten";
		g2.drawString(startText, 300, 260);
		// Wenn dieser Knopf aktiv ist, zeichnen wir einen Pfeil davor
		if (menuAuswahl == 0) {
			g2.drawString(">", 275, 260);
		}
		
		// 4. Option 2: Beenden
		String beendenText = "Beenden";
		g2.drawString(beendenText, 320, 320);
		if (menuAuswahl == 1) {
			g2.drawString(">", 295, 320);
		}
	}
	
	// ========================================================
	// ZUSTAND 1: DAS LAUFENDE SPIEL ZEICHNEN (Alte Logik verpackt)
	// ========================================================
	else if (gameState == spielState) {
		
		tileM.draw(g2);
		
		// RASTER ZEICHNEN
		g2.setColor(Color.DARK_GRAY);
		for (int worldCol = 0; worldCol < maxWorldCol; worldCol++) {
			int worldX = worldCol * tileSize;
			int screenXPos = worldX - spieler.worldX + screenX;
			g2.drawLine(screenXPos, 0, screenXPos, screenHeight);
		}
		for (int worldRow = 0; worldRow < maxWorldRow; worldRow++) {
			int worldY = worldRow * tileSize;
			int screenYPos = worldY - spieler.worldY + screenY;
			g2.drawLine(0, screenYPos, screenWidth, screenYPos);
		}
		
		spieler.draw(g2);
		
		// SQUIGGLY-SELECTOR
		int spielerWeltCol = (spieler.worldX + (tileSize / 2)) / tileSize;
		int spielerWeltRow = (spieler.worldY + (tileSize / 2)) / tileSize;
		
		if (spieler.blickRichtung.equals("oben")) { spielerWeltRow--; }
		else if (spieler.blickRichtung.equals("unten")) { spielerWeltRow++; }
		else if (spieler.blickRichtung.equals("links")) { spielerWeltCol--; }
		else if (spieler.blickRichtung.equals("rechts")) { spielerWeltCol++; }
		
		int selectorScreenX = (spielerWeltCol * tileSize) - spieler.worldX + screenX;
		int selectorScreenY = (spielerWeltRow * tileSize) - spieler.worldY + screenY;
		
		int checkCol = selectorScreenX / tileSize;
		int checkRow = selectorScreenY / tileSize;
		if (checkCol >= 0 && checkCol < maxScreenCol && checkRow >= 0 && checkRow < maxScreenRow) {
			g2.setColor(Color.CYAN);
			g2.drawRect(selectorScreenX, selectorScreenY, tileSize, tileSize);
		}
		
		// INVENTAR-TEXTE
		g2.setColor(Color.YELLOW);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
		g2.drawString("FPS: " + fps, 700, 20);
		
		g2.setColor(Color.ORANGE);
		g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
		g2.drawString("Samen im Rucksack: " + spieler.samenAnzahl, 20, 410);
		g2.setColor(Color.RED);
		g2.drawString("Geerntete Tomaten: " + spieler.tomaten, 20, 385);
		g2.setColor(Color.YELLOW);
		g2.drawString("Gold: " + spieler.gold + "g", 20, 360);
	}
	
	g2.dispose();
}
}