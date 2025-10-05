package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class WandererEnemy extends GameObject {
	private int animationFrame;
	private int animationDelay = 10;
	private float animationDelayTimer;
	int animType;
	private int enemySpriteNum = 5;
	private int spriteSet = 0;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private int maxSpeed = 5;
	private float restingTimer = 0;
	private int direction = 1; //down=1, left=2, up=3, right=4

	private boolean attacking = false;
	private boolean collided = false;
	private boolean motionLocked = false;

	public WandererEnemy(int x, int y, int width, int height, ID id) {
		super(x, y, width, height, id);

		this.luminosity = 100;
		this.setGrounded(false);
		this.animType = 1;
		this.animationFrame = 1;
		this.animationDelayTimer = 1;

		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 4; }
		
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
		x += velX * Game.deltaTime;
		updateCollision();

		//Find area shared by enemy and tiles
		a1 = new Area(collision);
		a1.intersect(a2);

		if(!a1.isEmpty()) {
			//Reverse bad movement
			x -= velX * Game.deltaTime;
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
		y += velY * Game.deltaTime;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);

		//Find area shared by enemy and tiles
		a1 = new Area(collision);
		a1.intersect(a2);

		if(!a1.isEmpty()) {
			//Reverse bad movement
			y -= velY * Game.deltaTime;
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
		this.animationDelayTimer += 1 * Game.deltaTime;
		if(this.animationDelayTimer >= this.animationDelay) {
			this.animationDelayTimer = 1;
			if(this.animationFrame < 8) {
				this.animationFrame++;
			}
			else {
				this.animationFrame = 1;
			}
			//Spin direction light if immobile
			if(this.getVelX() == 0 && this.getVelY() == 0) {
				this.animType++;
				if(this.animType > 4) {
					this.animType = 1;
				}
			}
		}

		g.drawImage(Game.enemySpriteSheets[enemySpriteNum].grabImageFast(animType + spriteSet, this.animationFrame), (int) x, (int) y, null);
		
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
			restingTimer += 1 * Game.deltaTime;
			if(restingTimer >= 20) {
				restingTimer = 0;

				//Out of four directions, go the direction that is the shortest distance to the player
				//Direction: down=1, left=2, up=3, right=4
				if(this.direction == 3) {
					this.direction = 4;
					this.velX = this.maxSpeed;
					this.animType = 1;
				}
				else if(this.direction == 1) {
					this.direction = 2;
					this.velX = -this.maxSpeed;
					this.animType = 3;
				}
				else if(this.direction == 2) {
					this.direction = 3;
					this.velY = this.maxSpeed;
					this.animType = 2;
				}
				else if(this.direction == 4) {
					this.direction = 1;
					this.velY = -this.maxSpeed;
					this.animType = 4;
				}
				if(Math.random() > 0.8) { this.motionLocked = true; }
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
				velX = 0;
				velY = 0;
			}

			//Possibly change direction once during each attack
			//Direction: down=1, left=2, up=3, right=4
			if(velX != 0 && playerXDistance <= 16 && !motionLocked) {
				velX = 0;
				if(Handler.playerY >= y) {
					velY = maxSpeed;
					this.direction = 3;
					this.animType = 2;
				}
				else {
					velY = -maxSpeed;
					this.direction = 1;
					this.animType = 4;
				}
				AudioPlayer.playSound("/thumperOn.wav");
				motionLocked = true;
			}
			else if(velY != 0 && playerYDistance <= 16 && !motionLocked) {
				velY = 0;
				if(Handler.playerX >= x) {
					velX = maxSpeed;
					this.direction = 4;
					this.animType = 1;
				}
				else {
					velX = -maxSpeed;
					this.direction = 2;
					this.animType = 3;
				}
				AudioPlayer.playSound("/thumperOn.wav");
				motionLocked = true;
			}
		}
		
		//Limit speed
		velX = Game.clamp(velX, -maxSpeed, maxSpeed);
		velY = Game.clamp(velY, -maxSpeed, maxSpeed);

		//Position
		x = Game.clamp(x, 0, Game.sWidth - width);
		y = Game.clamp(y, 0, Game.sHeight - height);
	}
}
