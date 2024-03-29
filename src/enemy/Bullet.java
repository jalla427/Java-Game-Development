package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import tmp.Game;
import tmp.GameObject;
import tmp.Handler;
import tmp.ID;

public class Bullet extends GameObject {

	private final Handler handler;
	
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	public Bullet(float x, float y, int width, int height, ID id, Handler handler, float targetX, float targetY) {
		super(x, y, width, height, id);
		
		double[] speeds = getSpeed(x, y, targetX, targetY);
		
		this.handler = handler;
		this.luminosity = 6;
		this.velX = (float) speeds[0];
		this.velY = (float) speeds[1];
	}

	public void tick() {
		//Update position
		x += velX;
		y += velY;
		
		updateCollision();
		
		//If bullet is offscreen, delete it
		if(x > Game.sWidth || x < -width || y > Game.sHeight || y < -height) {
			handler.object.remove(this);
		}
	}

	public void render(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect((int) x, (int) y, width, height);
		
		//Draw collision box
		if(Game.debugMode) {
			g.setColor(Color.YELLOW);
			g.drawPolygon(collision);
		}
	}
	
	//moves collision box with enemy
	protected void updateCollision() {
		xCollision = new int[] {(int) x, ((int) x) + width, ((int) x) + width, (int) width};
		yCollision = new int[] {(int) y, (int) y, ((int) y) + height, ((int) y) + height};
		
		collision = new Polygon();
		collision.xpoints = xCollision;
		collision.ypoints = yCollision;
		collision.npoints = xCollision.length;
	}

	public Polygon getBounds() {
		return collision;
	}
	
	public double[] getSpeed(float x, float y, float targetX, float targetY) {
		double[] speeds = new double[2];
		int bulletSpeed = 10;
		
		double dx = targetX - x;
		double dy = targetY - y;
		double angle = Math.atan2(dy, dx);
		
		speeds[0] = Math.cos(angle) * bulletSpeed;
		speeds[1] = Math.sin(angle) * bulletSpeed;
		
		return speeds;
	}

}
