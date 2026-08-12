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
			worldY = worldY - figurSpeed;
			blickRichtung = "oben";
			if (worldY < 0) { worldY = 0; }
		}
		if (steuerung.unten == true) {
			worldY = worldY + figurSpeed;
			blickRichtung = "unten";
			if (worldY > gp.worldHeight - gp.tileSize) {
				worldY = gp.worldHeight - gp.tileSize;
			}
		}
		if (steuerung.links == true) {
			worldX = worldX - figurSpeed;
			blickRichtung = "links";
			if (worldX < 0) { worldX = 0; }
		}
		if (steuerung.rechts == true) {
			worldX = worldX + figurSpeed;
			blickRichtung = "rechts";
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
}
