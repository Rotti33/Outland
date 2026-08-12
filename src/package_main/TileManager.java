package package_main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public class TileManager {

	Gamepanel gp;
	public Tile[] kachelTypen;

	public int[][] worldBuilding;

	public TileManager(Gamepanel gp) {
		this.gp = gp;
		
		kachelTypen = new Tile[10];
		worldBuilding = gp.worldBuilding;
		
		ladeKachelBilder();
		ladeKarte();
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

			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				String zeile = br.readLine();
				
				while(col < gp.maxWorldCol) {
					String[] zahlen = zeile.split(" "); 
					int num = Integer.parseInt(zahlen[col]); 
					
					worldBuilding[col][row] = num; 
					col++;
				}
				if(col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();
			
		} catch (Exception e) {
			System.out.println("FEHLER BEIM KARTEN-LADEN IM TILEMANAGER!");
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g2) {
		
		for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
			for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
				
				int worldX = worldCol * gp.tileSize;
				int worldY = worldRow * gp.tileSize;
				
				int screenXPos = worldX - gp.spieler.worldX + gp.screenX;
				int screenYPos = worldY - gp.spieler.worldY + gp.screenY;
				
				if (worldX + gp.tileSize > gp.spieler.worldX - gp.screenX &&
					worldX - gp.tileSize < gp.spieler.worldX + gp.screenX &&
					worldY + gp.tileSize > gp.spieler.worldY - gp.screenY &&
					worldY - gp.tileSize < gp.spieler.worldY + gp.screenY) {
					
					int kachelNummer = worldBuilding[worldCol][worldRow];
					
					if (kachelTypen[kachelNummer] != null && kachelTypen[kachelNummer].image != null) {
						g2.drawImage(kachelTypen[kachelNummer].image, screenXPos, screenYPos, gp.tileSize, gp.tileSize, null);
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
						g2.fillRect(screenXPos, screenYPos, gp.tileSize, gp.tileSize);
						g2.setColor(Color.WHITE);
						g2.setFont(new Font("Arial", Font.PLAIN, 10));
						g2.drawString("MARKT", screenXPos + 6, screenYPos + 26);
					}
				}
			}
		}
	}
}