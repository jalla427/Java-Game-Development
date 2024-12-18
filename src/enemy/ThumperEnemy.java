package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class ThumperEnemy extends GameObject {

	private final Handler handler;
	private BufferedImage enemy_image;
	private int animationFrame;
	private int animationDelay = 200;
	private int animationDelayTimer;
	int animType;
	private SpriteSheet ss;
	private int spriteSet = 0;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int maxSpeed = 5;
	int restingTimer = 0;

	boolean attacking = false;
	boolean collided = false;
	boolean motionLocked = false;

	public ThumperEnemy(int x, int y, int width, int height, ID id, Handler handler) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.luminosity = 100;
		this.setGrounded(false);
		this.animType = 1;
		this.animationFrame = 1;
		this.animationDelayTimer = 1;
		
		ss = new SpriteSheet(Game.sprite_sheet_thumper);
		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 2; }
		enemy_image = ss.grabImage(1 + spriteSet, 1, width, height);
		
		this.velX = 0;
		this.velY = 0;
	}

	public void tick() {
		updateVelocity();
		collision();
		updateCollision();
		collision.invalidate();
	}

	//Updates position and adjusts if the enemy is colliding with any tiles
	private void collision() {
		Area a1;
	    Area a2 = Handler.currentLevelArea;
		
	    //Horizontal Collision
		x += velX;
		updateCollision();

		//Find area shared by enemy and tiles
		a1 = new Area(collision);
		a1.intersect(a2);

		if(!a1.isEmpty()) {
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

			//End attack after collision
			collided = true;
		}

		//Vertical Collision
		y += velY;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);

		//Find area shared by enemy and tiles
		a1 = new Area(collision);
		a1.intersect(a2);

		if(!a1.isEmpty()) {
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

			//After resolving collision, grounded is now true
			if(!this.isGrounded()) {
				this.setGrounded(true);
			}

			//End attack after collision
			collided = true;
		}
	}

	public void render(Graphics g) {
		//Cycles animation frame
		this.animationDelayTimer++;
		if(this.animationDelayTimer >= this.animationDelay) {
			this.animationDelayTimer = 1;
			if(this.animationFrame < 8) {
				this.animationFrame++;
			}
			else {
				this.animationFrame = 1;
			}
		}

		int currentAnimType = this.animType;

		if(this.getVelX() == 0 && this.getVelY() == 0) {
			this.animType = 1;
			if(this.animType != currentAnimType) {
				this.animationDelayTimer = 0;
			}
		}
		else {
			this.animType = 2;
			if(this.animType != currentAnimType) {
				this.animationDelayTimer = 0;
			}
		}

		this.enemy_image = ss.grabImage(animType + spriteSet, this.animationFrame, width, height);
		g.drawImage(this.enemy_image, (int) x, (int) y, null);

		if(attacking) {
			this.setLuminosity(Game.clamp(this.getLuminosity() + 7, 0, 100));
		}
		else {
			this.setLuminosity(0);
		}
		
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
		//Find x and y distance to player separately for comparison
		float playerXDistance = (int) Game.calculateDistance(this.getX(), this.getY(), Handler.playerX, this.getY());
		float playerYDistance = (int) Game.calculateDistance(this.getX(), this.getY(), this.getX(), Handler.playerY);

		if(!attacking) {
			restingTimer++;
			if(restingTimer >= 20) {
				restingTimer = 0;

				//Out of four directions, go the direction that is the shortest distance to the player
				if(Handler.playerX >= (this.getX() + (this.getWidth()/2)) && playerXDistance >= playerYDistance) {
					this.velX = maxSpeed;
				}
				else if(Handler.playerX < (this.getX() + (this.getWidth()/2)) && playerXDistance >= playerYDistance) {
					this.velX = -maxSpeed;
				}
				else if(Handler.playerY >= (this.getY() + (this.getWidth()/2)) && playerYDistance > playerXDistance) {
					this.velY = maxSpeed;
				}
				else if(Handler.playerY < (this.getY() + (this.getWidth()/2)) && playerYDistance > playerXDistance) {
					this.velY = -maxSpeed;
				}
				this.attacking = true;
			}
		}
		
		if(attacking) {
			if(collided) {
				//Play noise only if full motion occurred (avoids repeated slamming noises)
				if(motionLocked) {
					AudioPlayer.playSound("/thumperSlam.wav");
				}

				//Reset tracking variables
				attacking = false;
				collided = false;
				motionLocked = false;
				this.setVelX(0);
				this.setVelY(0);
			}

			//Change direction once during each attack
			if(this.getVelX() != 0 && playerXDistance <= 16 && !motionLocked) {
				this.setVelX(0);
				if(Handler.playerY >= this.getY()) {
					this.setVelY(Game.clamp((int) (maxSpeed * Math.random()), 3, maxSpeed));
				}
				else {
					this.setVelY(Game.clamp((int) (-maxSpeed * Math.random()), -maxSpeed, -3));
				}
				AudioPlayer.playSound("/thumperOn.wav");
				motionLocked = true;
			}
			else if(this.getVelY() != 0 && playerYDistance <= 16 && !motionLocked) {
				this.setVelY(0);
				if(Handler.playerX >= this.getX()) {
					this.setVelX(Game.clamp((int) (maxSpeed * Math.random()), 3, maxSpeed));
				}
				else {
					this.setVelX(Game.clamp((int) (-maxSpeed * Math.random()), -maxSpeed, -3));
				}
				AudioPlayer.playSound("/thumperOn.wav");
				motionLocked = true;
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
