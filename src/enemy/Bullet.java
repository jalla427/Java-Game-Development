package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import tmp.*;

public class Bullet extends GameObject {
	private int animationFrame;
	private int animationDelay;
	private int enemySpriteNum = 2;
	private int sprite;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private float targetX;
	private float targetY;
	private int bulletSpeed;
	private boolean homing = false;
	private int homingTimer = 0;
	public boolean active = false;

	public Bullet(float x, float y, int width, int height, ID id, float targetX, float targetY, int speed, boolean homing, int sprite, boolean active) {
		super(x, y, width, height, id);

		this.x = x;
		this.y = y;
		this.targetX = targetX;
		this.targetY = targetY;
		this.luminosity = 20;
		this.animationFrame = 1;
		this.animationDelay = 1;
		this.sprite = sprite;
		this.homing = homing;
		this.active = active;
		this.bulletSpeed = speed;

		refreshSpeeds();
	}

	public void tick() {
		if(active) {
			//Update position
			if (this.homing) {
				updateVelocity();
			}
			this.setX(this.getX() + this.getVelX());
			this.setY(this.getY() + this.getVelY());

			updateCollision();

			//If bullet is offscreen, delete it
			if ((x > Game.sWidth || x < -this.getWidth() || y > Game.sHeight || y < -this.getHeight()) && this.isActive()) {
				Handler.bulletRemoveList.add(this);
			}
		}
	}

	public void render(Graphics g) {
		if(active) {
			//Cycles animation frame
			this.animationDelay++;
			if (this.animationDelay >= 5) {
				this.animationDelay = 1;
				if (this.animationFrame < 10) {
					this.animationFrame++;
				} else {
					this.animationFrame = 1;
				}
			}

			g.drawImage(Game.enemySpriteSheets[enemySpriteNum].grabImageFast(this.sprite, animationFrame), (int) x, (int) y, null);

			//Draw collision box
			if (Game.debugMode) {
				g.setColor(Color.YELLOW);
				try {
					g.drawPolygon(collision);
				} catch (NullPointerException npe) {
					System.out.println(npe);
				}
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

	public void refreshSpeeds() {
		double[] speeds = getSpeed(this.x, this.y, this.targetX, this.targetY);
		this.velX = (float) speeds[0];
		this.velY = (float) speeds[1];
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean getActive() {
		return this.active;
	}
	public boolean isActive() {
		return active;
	}
	public void setTargetX(float targetX) {
		this.targetX = targetX;
	}
	public float getTargetX() {
		return this.targetX;
	}
	public void setTargetY(float targetY) {
		this.targetY = targetY;
	}
	public float getTargetY() {
		return this.targetY;
	}
	public void setBulletSpeed(int speed) {
		this.bulletSpeed = speed;
	}
	public int getBulletSpeed() {
		return this.bulletSpeed;
	}
	public void setHoming(boolean homing) {
		this.homing = homing;
	}
	public void setSprite(int sprite) {
		this.sprite = sprite;
	}
}
