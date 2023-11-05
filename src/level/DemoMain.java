package level;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import tmp.BufferedImageLoader;
import tmp.Game;
import tmp.GameObject;
import tmp.ID;
import tmp.Handler;

public class DemoMain extends GameObject {
	private final Handler handler;
	private final BufferedImage terrainImg;
	
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;
	
	public DemoMain(float x, float y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
		
		BufferedImageLoader loader = new BufferedImageLoader();
		terrainImg = loader.loadImage("/tombMainMenu.png");
		
		updateCollision();
	}
		
	public void tick() {
		collision.invalidate();
		updateCollision();
	}

	public void render(Graphics g) {
		g.drawImage(terrainImg, 0, 0, null);
		
		g.setColor(Color.BLUE);
		g.drawPolygon(collision);
	}

	public Polygon getBounds() {
		return collision;
	}
	
	protected void updateCollision() {
		xCollision = new int[] {(int) x+1, (int) (x + 900), (int) (x + 900), (int) x, (int) x, 
				(int) (x + 101), (int) (x + 101), (int) (x + 800), (int) (x + 800), (int) x};
		yCollision = new int[] {(int) y+1, (int) y, (int) (y + 670), (int) (y + 670), (int) (y + 101), 
				(int) (y + 101), (int) (y + 569), (int) (y + 569), (int) (y + 101), (int) (y + 101)};
		
		collision = new Polygon();
		collision.xpoints = xCollision;
		collision.ypoints = yCollision;
		collision.npoints = xCollision.length;
	}
}

