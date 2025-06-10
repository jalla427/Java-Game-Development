package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import tmp.*;

public class Bullet extends GameObject {
	private BufferedImage bullet_image;
	private int animationFrame;
	private int animationDelay;
	SpriteSheet ss;
	int sprite;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private int bulletSpeed;
	private boolean homing = false;
	private int homingTimer = 0;

	public Bullet(float x, float y, int width, int height, ID id, float targetX, float targetY, int speed, boolean homing, int sprite) {
		super(x, y, width, height, id);

		this.luminosity = 20;
		this.animationFrame = 1;
		this.animationDelay = 1;
		this.sprite = sprite;
		this.homing = homing;

		this.bulletSpeed = speed;
		double[] speeds = getSpeed(x, y, targetX, targetY);
		this.velX = (float) speeds[0];
		this.velY = (float) speeds[1];

		this.ss = Game.sprite_sheet_bullet;
		this.bullet_image = ss.grabImageFast(this.sprite, 1);
	}

	public void tick() {
		//Update position
		if(this.homing) { updateVelocity(); }
		this.setX(this.getX() + this.getVelX());
		this.setY(this.getY() + this.getVelY());
		
		updateCollision();
		
		//If bullet is offscreen, delete it
		if(x > Game.sWidth || x < -this.getWidth() || y > Game.sHeight || y < -this.getHeight()) {
			Handler.bulletRemoveList.add(this);
		}
	}

	public void render(Graphics g) {
		//Cycles animation frame
		bullet_image = ss.grabImageFast(this.sprite, animationFrame);
		this.animationDelay++;
		if(this.animationDelay >= 5) {
			this.animationDelay = 1;
			if(this.animationFrame < 10) {
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
		
		this.collision = new Polygon();
		this.collision.xpoints = xCollision;
		this.collision.ypoints = yCollision;
		this.collision.npoints = xCollision.length;
	}

	private void updateVelocity() {
		this.homingTimer++;
		if(this.homingTimer >= 10) {
			if(Handler.playerX > this.getX()) {
				this.setVelX(this.getVelX() + 1);
			}
			else {
				this.setVelX(this.getVelX() - 1);
			}
			if(Handler.playerY > this.getY()) {
				this.setVelY(this.getVelY() + 1);
			}
			else {
				this.setVelY(this.getVelY() - 1);
			}
			this.homingTimer = 0;
		}
		this.setVelX(Game.clamp(this.getVelX(), -this.bulletSpeed, this.bulletSpeed));
		this.setVelY(Game.clamp(this.getVelY(), -this.bulletSpeed, this.bulletSpeed));
	}

	public Polygon getBounds() {
		return collision;
	}
	
	public double[] getSpeed(float x, float y, float targetX, float targetY) {
		double[] speeds = new double[2];
		
		double dx = targetX - x;
		double dy = targetY - y;
		double angle = Math.atan2(dy, dx);
		
		speeds[0] = Math.cos(angle) * this.bulletSpeed;
		speeds[1] = Math.sin(angle) * this.bulletSpeed;
		
		return speeds;
	}

}
