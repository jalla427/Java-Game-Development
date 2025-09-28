package level;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import tmp.Game;
import tmp.GameObject;
import tmp.Handler;
import tmp.ID;

public class Tile extends GameObject {
	
	int tileType;
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;
	
	public Tile(int x, int y, int tileType) {
		super(x, y, TileMapBuilder.tileWidth, TileMapBuilder.tileHeight, ID.Level);
		this.tileType = tileType;
		updateCollision();
	}

	public void tick() {
		
	}

	public void render(Graphics g) {
		g.drawImage(TileMapBuilder.tileTypeList[tileType], (int) x, (int) y, null);
		
		//Draw collision box
		if(Game.debugMode) {
			g.setColor(Color.BLUE);
			g.drawPolygon(collision);
		}
	}

	public Polygon getBounds() {
		return collision;
	}
	
	private void updateCollision() {
		xCollision = new int[] {(int) x, (int) x + 19, (int) x + 19, (int) x};
		yCollision = new int[] {(int) y, (int) y, (int) y + 19, (int) y + 19};
		
		collision = new Polygon();
		collision.xpoints = xCollision;
		collision.ypoints = yCollision;
		collision.npoints = xCollision.length;
		
	}

}
