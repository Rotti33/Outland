package package_main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {

	Gamepanel gp;
	Steuerung steuerung;
	
	public int worldX;
	public int worldY;
	public int figurSpeed;
	public String blickRichtung;
	
	public BufferedImage playerImage;
	
	public int samenAnzahl;
	public int tomaten;
	public int gold;
	
	public Player(Gamepanel gp, Steuerung steuerung) {
		this.gp = gp;
		this.steuerung = steuerung;
		
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		worldX = gp.tileSize * 23;
		worldY = gp.tileSize * 23;
		figurSpeed = 4;
		blickRichtung = "unten";
		
		samenAnzahl = 5;
		tomaten = 0;
		gold = 0;
	}
	
	public void getPlayerImage() {
		try {
			playerImage = ImageIO.read(getClass().getResourceAsStream("/player.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		
		if (steuerung.oben == true) {
			blickRichtung = "oben";
			int naechstesY = worldY - figurSpeed;
			
			if (hatKollision(worldX, naechstesY) == false) {
				worldY = naechstesY;
			}
			if (worldY < 0) { worldY = 0; }
		}
		
		if (steuerung.unten == true) {
			blickRichtung = "unten";
			int naechstesY = worldY + figurSpeed;
			
			if (hatKollision(worldX, naechstesY) == false) {
				worldY = naechstesY;
			}
			if (worldY > gp.worldHeight - gp.tileSize) {
				worldY = gp.worldHeight - gp.tileSize;
			}
		}
		
		if (steuerung.links == true) {
			blickRichtung = "links";
			int naechstesX = worldX - figurSpeed;
			
			if (hatKollision(naechstesX, worldY) == false) {
				worldX = naechstesX;
			}
			if (worldX < 0) { worldX = 0; }
		}
		
		if (steuerung.rechts == true) {
			blickRichtung = "rechts";
			int naechstesX = worldX + figurSpeed;
			
			if (hatKollision(naechstesX, worldY) == false) {
				worldX = naechstesX;
			}
			if (worldX > gp.worldWidth - gp.tileSize) {
				worldX = gp.worldWidth - gp.tileSize;
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		if (playerImage != null) {
			g2.drawImage(playerImage, gp.screenX, gp.screenY, gp.tileSize, gp.tileSize, null);
		}
	}
	
	public boolean hatKollision(int zukuenftigesX, int zukuenftigesY) {
		
		int linksEcke   = zukuenftigesX;
		int rechtsEcke  = zukuenftigesX + gp.tileSize - 1;
		int obenEcke    = zukuenftigesY;
		int untenEcke   = zukuenftigesY + gp.tileSize - 1;
		
		int colLinks   = linksEcke / gp.tileSize;
		int colRechts  = rechtsEcke / gp.tileSize;
		int rowOben    = obenEcke / gp.tileSize;
		int rowUnten   = untenEcke / gp.tileSize;
		
		if (colLinks >= 0 && colRechts < gp.maxWorldCol && rowOben >= 0 && rowUnten < gp.maxWorldRow) {
			
			int kachel1 = gp.worldBuilding[colLinks][rowOben];
			int kachel2 = gp.worldBuilding[colRechts][rowOben];
			int kachel3 = gp.worldBuilding[colLinks][rowUnten];
			int kachel4 = gp.worldBuilding[colRechts][rowUnten];
			
			if (gp.tileM.kachelTypen[kachel1].collision == true ||
				gp.tileM.kachelTypen[kachel2].collision == true ||
				gp.tileM.kachelTypen[kachel3].collision == true ||
				gp.tileM.kachelTypen[kachel4].collision == true) {
				
				return true;
			}
		}
		return false;
	}
}
