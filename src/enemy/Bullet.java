package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import tmp.*;

public class Bullet extends GameObject {
	private int animationFrame;
	private float animationDelay;
	private int enemySpriteNum = 2;
	private int sprite;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private float targetX;
	private float targetY;
	private float bulletSpeed;
	private boolean homing = false;
	private float homingTimer = 0;
	public boolean active = false;

	public Bullet(float x, float y, int width, int height, ID id, float targetX, float targetY, float speed, boolean homing, int sprite, boolean active) {
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
			x += velX * Game.deltaTime;
			y += velY * Game.deltaTime;

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
			this.animationDelay += 1 * Game.deltaTime;
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
		this.homingTimer += 1 * Game.deltaTime;
		if(this.homingTimer >= 10) {
			if(Handler.playerX > this.getX()) {
				velX++;
			}
			else {
				velX--;
			}
			if(Handler.playerY > this.getY()) {
				velY++;
			}
			else {
				velY--;
			}
			this.homingTimer = 0;
		}
		velX = Game.clamp(velX, (int) -this.bulletSpeed, (int) this.bulletSpeed);
		velY = Game.clamp(velY, (int) -this.bulletSpeed, (int) this.bulletSpeed);
	}

	public Polygon getBounds() {
		return collision;
	}
	
	public float[] getSpeed(float x, float y, float targetX, float targetY) {
		float[] speeds = new float[2];
		
		double dx = targetX - x;
		double dy = targetY - y;
		double angle = Math.atan2(dy, dx);
		
		speeds[0] = (float) (Math.cos(angle) * this.bulletSpeed);
		speeds[1] = (float) (Math.sin(angle) * this.bulletSpeed);
		
		return speeds;
	}

	public void refreshSpeeds() {
		float[] speeds = getSpeed(this.x, this.y, this.targetX, this.targetY);
		this.velX = speeds[0];
		this.velY = speeds[1];
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
	public void setBulletSpeed(float speed) {
		this.bulletSpeed = speed;
	}
	public float getBulletSpeed() {
		return this.bulletSpeed;
	}
	public void setHoming(boolean homing) {
		this.homing = homing;
	}
	public void setSprite(int sprite) {
		this.sprite = sprite;
	}
}
