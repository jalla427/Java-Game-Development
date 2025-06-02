package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import tmp.*;

public class Bullet extends GameObject {

	private final Handler handler;
	private BufferedImage bullet_image;
	private int animationFrame;
	private int animationDelay;
	SpriteSheet ss;
	int sprite;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int bulletSpeed;

	public Bullet(float x, float y, int width, int height, ID id, Handler handler, float targetX, float targetY, int speed, int sprite) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.luminosity = 6;
		this.animationFrame = 1;
		this.animationDelay = 1;
		this.sprite = sprite + 2;

		this.bulletSpeed = speed;
		double[] speeds = getSpeed(x, y, targetX, targetY);
		this.velX = (float) speeds[0];
		this.velY = (float) speeds[1];

		ss = new SpriteSheet(Game.sprite_sheet_sentry);
		bullet_image = ss.grabImage(this.sprite, 1, width, height);
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
		//Cycles animation frame
		bullet_image = ss.grabImage(this.sprite, animationFrame, (int) this.getWidth(), (int) this.getHeight());
		this.animationDelay++;
		if(this.animationDelay >= 5) {
			this.animationDelay = 1;
			if(this.animationFrame < 4) {
				this.animationFrame++;
			}
			else {
				this.animationFrame = 1;
			}
		}

		g.drawImage(bullet_image, (int) x, (int) y, null);
		
		//Draw collision box
		if(Game.debugMode) {
			g.setColor(Color.YELLOW);
			try{
				g.drawPolygon(collision);
			} catch(NullPointerException npe) {
				System.out.println(npe);
			}
		}
	}
	
	//moves collision box with enemy
	protected void updateCollision() {
		int pointX = (int) this.getX();
		int pointY = (int) this.getY();
		int objWidth = (int) this.getWidth();
		int objHeight = (int) this.getHeight();

		xCollision = new int[] {pointX, (pointX) + objWidth, (pointX) + objWidth, pointX};
		yCollision = new int[] {pointY, pointY, (pointY) + objHeight, (pointY) + objHeight};
		
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
		
		double dx = targetX - x;
		double dy = targetY - y;
		double angle = Math.atan2(dy, dx);
		
		speeds[0] = Math.cos(angle) * bulletSpeed;
		speeds[1] = Math.sin(angle) * bulletSpeed;
		
		return speeds;
	}

}
