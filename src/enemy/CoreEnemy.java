package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class CoreEnemy extends GameObject {

	private final Handler handler;
	private BufferedImage enemy_image;
	private int animationFrame;
	private int attackMode;
	private int animationDelay;
	private SpriteSheet ss;
	private int spriteSet = 0;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;
	private boolean colliding = false;

	private int maxSpeed = 6;
	private int modeTimer = 0;
	private int homingTimer = 0;

	public CoreEnemy(int x, int y, int width, int height, ID id, Handler handler) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.luminosity = 150;
		this.animationFrame = 1;
		this.attackMode = 1;
		this.animationDelay = 1;
		
		ss = new SpriteSheet(Game.sprite_sheet_core);
		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 3; }
		
		velX = 5;
		velY = 5;
	}

	public void tick() {
		updateVelocity();
		collision();
		updateCollision();
		collision.invalidate();
	}

	//Updates position and adjusts if the enemy is colliding with any tiles
	private void collision() {
		colliding = false;
		Area a1;
		Area a2 = Handler.currentLevelArea;
		
	    //Horizontal Collision
		x += velX;
		updateCollision();

		//Find area shared by enemy and by tile
		a1 = new Area(collision);
		a1.intersect(a2);

		//Determine if any area is shared by enemy and by tile
		if(!a1.isEmpty()) { colliding = true; }
		if(!a1.isEmpty() && this.attackMode != 2) {
			//Reverse bad movement
			x -= velX;
			updateCollision();
			a1.reset();
			a1 = new Area(collision);
			a1.intersect(a2);
					
			//Move enemy to the wall slowly until overlapping by one pixel
			while(a1.isEmpty()) {
				x += Math.signum(velX);
				updateCollision();
				a1.reset();
				a1 = new Area(collision);
				a1.intersect(a2);
			}
					
			//Position enemy one pixel outside of wall
			x -= Math.signum(velX);
			updateCollision();
			velX = 0;
		}
		
		//Vertical Collision
		y += velY;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);

		//Find area shared by enemy and tile
		a1 = new Area(collision);
		a1.intersect(a2);
				
		//Determine if any area is shared by enemy and by tile
		if(!a1.isEmpty()) { colliding = true; }
		if(!a1.isEmpty() && this.attackMode != 2) {
			//Reverse bad movement
			y -= velY;
			updateCollision();
			a1.reset();
			a1 = new Area(collision);
			a1.intersect(a2);
					
			//Move enemy to the wall slowly until overlapping by one pixel
			while(a1.isEmpty()) {
				y += Math.signum(velY);
				updateCollision();
				a1.reset();
				a1 = new Area(collision);
				a1.intersect(a2);
			}
					
			//Position enemy one pixel outside of wall
			y -= Math.signum(velY);
			updateCollision();
			velY = 0;
		}
	}

	public void render(Graphics g) {
		//Cycles animation frame
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
		enemy_image = ss.grabImage(this.attackMode + spriteSet, this.animationFrame, width, height);
		g.drawImage(enemy_image, (int) x, (int) y, null);
		
		//Draw collision box
		if(Game.debugMode) {
			try {
				g.setColor(Color.RED);
				g.drawPolygon(getBounds());
			}
			catch (NullPointerException e) {
				System.out.println(e);
			}
		}
	}

	public Polygon getBounds() {
		return collision;
	}

	//moves collision box with enemy
	protected void updateCollision() {
		xCollision = new int[] {(int) x, ((int) x) + width, ((int) x) + width, (int) x};
		yCollision = new int[] {(int) y, (int) y, ((int) y) + height, ((int) y) + height};
		
		collision = new Polygon();
		collision.xpoints = xCollision;
		collision.ypoints = yCollision;
		collision.npoints = xCollision.length;
	}
	
	protected void updateVelocity() {
		modeTimer++;
		homingTimer++;
		//Pursuit with collisions
		if(attackMode == 1) {
			if(homingTimer >= 8) {
				if(Handler.playerX > this.x) {
					this.setVelX(velX + 1);
				}
				else {
					this.setVelX(velX - 1);
				}
				if(Handler.playerY > this.y) {
					this.setVelY(velY + 1);
				}
				else {
					this.setVelY(velY - 1);
				}
				homingTimer = 0;
			}
			if(modeTimer >= 300) {
				if(Math.random() > 0.5) {
					attackMode = 2;
					homingTimer = 0;
					this.maxSpeed = 4;
				} else {
					attackMode = 3;
					homingTimer = 0;
					this.setVelX(0);
					this.setVelY(0);
				}
				modeTimer = -1;
			}
		}
		//Pursuit without collisions
		if(attackMode == 2 && modeTimer != -1) {
			if(homingTimer >= 5) {
				if(Handler.playerX > this.x) {
					this.setVelX(velX + 1);
				}
				else {
					this.setVelX(velX - 1);
				}
				if(Handler.playerY > this.y) {
					this.setVelY(velY + 1);
				}
				else {
					this.setVelY(velY - 1);
				}
				homingTimer = 0;
			}
			if(modeTimer >= 200 && !colliding) {
				if(Math.random() > 0.5) {
					attackMode = 1;
					homingTimer = 0;
				} else {
					attackMode = 3;
					homingTimer = 0;
					this.setVelX(0);
					this.setVelY(0);
				}
				modeTimer = -1;
				this.maxSpeed = 6;
			}
		}
		//Sentry ranged attack
		if(attackMode == 3 && modeTimer != -1) {
			if(homingTimer >= 20) {
				handler.addObject(new Bullet(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/4), 10, 10, ID.Enemy, handler, Handler.playerX + 16, Handler.playerY - 24, (int) (4 + (Math.random()*5)), 2));
				handler.addObject(new Bullet(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/4), 10, 10, ID.Enemy, handler, Handler.playerX + 16, Handler.playerY + 56, (int) (4 + (Math.random()*5)), 2));
				homingTimer = 0;
			}
			if(modeTimer >= 200) {
				if(Math.random() > 0.5) {
					attackMode = 1;
					homingTimer = 0;
				} else {
					attackMode = 2;
					homingTimer = 0;
					this.maxSpeed = 4;
				}
				modeTimer = -1;
			}
		}
		
		//Limit speed
		this.setVelX(Game.clamp(this.getVelX(), -maxSpeed, maxSpeed));
		this.setVelY(Game.clamp(this.getVelY(), -maxSpeed, maxSpeed));
		
		//Position
		x = Game.clamp(x, 0, Game.sWidth - width);
		y = Game.clamp(y, 0, Game.sHeight - height);
	}
}
