package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import tmp.*;

public class Bullet extends GameObject {

	private final Handler handler;
	private BufferedImage bullet_image;
	SpriteSheet ss;

	private int spriteTimer = 1;
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

		ss = new SpriteSheet(Game.sprite_sheet_sentry);
		bullet_image = ss.grabImage(6, 1, width, height);
	}

	public void tick() {
		//Update position
		x += velX;
		y += velY;
		
		updateCollision();
		
		//If bullet is offscreen, delete it
		if(x > Game.sWidth || x < -this.getWidth() || y > Game.sHeight || y < -this.getHeight()) {
			handler.object.remove(this);
		}
	}

	public void render(Graphics g) {
		//Update sprite
		spriteTimer++;
		bullet_image = ss.grabImage(6, spriteTimer, (int) this.getWidth(), (int) this.getHeight());
		if(spriteTimer >= 5) {
			spriteTimer = 0;
		}

		g.drawImage(bullet_image, (int) x, (int) y, null);
		
		//Draw collision box
		if(Game.debugMode) {
			g.setColor(Color.YELLOW);
			g.drawPolygon(collision);
		}
	}
	
	//moves collision box with enemy
	protected void updateCollision() {
		xCollision = new int[] {(int) this.getX(), ((int) this.getX()) + (int) this.getWidth(), ((int) this.getX()) + (int) this.getWidth(), (int) this.getWidth()};
		yCollision = new int[] {(int) this.getY(), (int) this.getY(), ((int) this.getY()) + (int) this.getHeight(), ((int) this.getY()) + (int) this.getHeight()};
		
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
