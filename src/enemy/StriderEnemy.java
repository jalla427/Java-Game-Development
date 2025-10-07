package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class StriderEnemy extends GameObject {
	private int animationFrame;
	private float animationDelay = 200;
	private float animationDelayTimer;
	int animType;
	long walkAudioTimer = 0;
	private int enemySpriteNum = 3;
	private int spriteSet = 0;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int maxSpeed = 3;
	int sightRange = 295;
	float jumpTimer = 0;
	float wanderTimer = 0;
	boolean attacking = true;
	boolean jumping = false;
	boolean xCollided = false;

	public StriderEnemy(int x, int y, int width, int height, ID id) {
		super(x, y, width, height, id);

		this.luminosity = 100;
		this.setGrounded(false);
		this.animType = 1;
		this.animationFrame = 1;
		this.animationDelayTimer = 1;
		this.sightRange += (int) (Math.random() * 10);

		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 4; Game.unlockedSkins[8] = true; }
		
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
		Area a1;
		Area a2 = Handler.currentLevelArea;
		
	    //Horizontal Collision
		x += velX * Game.deltaTime;
		updateCollision();

		//Find area shared by enemy and by tiles
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

			//Update jumping AI
			if(!jumping) {
				this.xCollided = true;
			}
		}
		
		//Vertical Collision
		y += velY * Game.deltaTime;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);

		//Find area shared by enemy and tile
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

			//If jumping, end jump
			if(!this.isGrounded()) {
				this.setGrounded(true);
				if(jumping) {
					this.jumping = false;
					AudioPlayer.playSound("/striderLand.wav");
				}
			}
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
		}

		int currentAnimType = this.animType;

		if(velX == 0 && velY == 0) {
			this.animType = 1;
			if(this.animType != currentAnimType) {
				this.animationDelayTimer = 0;
				this.animationDelay = 10;
			}
		}
		else {
			if(Handler.playerX > this.x) {
				this.animType = 2;
				if(this.animType != currentAnimType) {
					this.animationDelayTimer = 0;
					this.animationDelay = 10;
				}
			}
			else {
				this.animType = 3;
				if(this.animType != currentAnimType) {
					this.animationDelayTimer = 0;
					this.animationDelay = 10;
				}
			}
		}
		if(jumping) {
			this.animType = 4;
			if(this.animType != currentAnimType) {
				this.animationDelayTimer = 0;
				this.animationDelay = 200;
			}
		}

		g.drawImage(Game.enemySpriteSheets[enemySpriteNum].grabImageFast(animType + spriteSet, this.animationFrame), (int) x, (int) y, null);

		if(getVelX() != 0 || getVelY() != 0) {
			this.setLuminosity(Game.clamp(this.getLuminosity() + (10 * Game.deltaTime), 10, 100));
		}
		else {
			this.setLuminosity(Game.clamp(this.getLuminosity() - (10 * Game.deltaTime), 0, 100));
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
		float playerDistance = Game.calculateDistance(this.x, this.y, Handler.playerX, Handler.playerY);

		if(!attacking) {
			if(playerDistance < this.sightRange) {
				this.attacking = true;
				this.wanderTimer = 0;
				AudioPlayer.playSound("/striderOnBeep.wav");
			}
			else {
				//Handle random wandering movements
				this.wanderTimer += 1 * Game.deltaTime;
				if(this.wanderTimer > 300) {
					if(this.getVelX() != 0) {
						this.setVelX(0);
					}
					else {
						//Randomly determine walk direction
						if(Math.random() > 0.5) {
							this.setVelX(1);
						}
						else {
							this.setVelX(-1);
						}
					}
					this.wanderTimer = (float) (Math.random() * 10);
				}
			}
		}
		
		if(attacking) {
			if(Handler.playerX > this.x) {
				velX += 1 * Game.deltaTime;
			}
			else {
				velX -= 1 * Game.deltaTime;
			}

			//Decide to jump
			if(!this.jumping && ((this.xCollided && this.isGrounded()) || (Handler.playerY < this.y && this.isGrounded() && this.jumpTimer >= 150))) {
				this.jumpTimer = (float) (20 * Math.random() * Game.deltaTime);
				velY -= 20;
				this.setGrounded(false);
				this.jumping = true;
				this.xCollided = false;
			}
			else {
				this.jumpTimer += 1 * Game.deltaTime;
			}

			if(playerDistance >= this.sightRange) {
				this.attacking = false;
				this.setVelX(0);
				AudioPlayer.playSound("/striderOffBeep.wav");
			}
		}

		if(!this.isGrounded()) {
			velY += 1 * Game.deltaTime;
			velY = Game.clamp(velY, -20, 10);
		}
		else {
			velY = 0;
		}
		
		//Limit speed
		velX = Game.clamp(velX, -maxSpeed, maxSpeed);

		//Stop adjusting velocity X if overlapping player on the X axis (prevents spinning in place)
		if(Game.calculateDistance(x, x, Handler.playerX, x) < (float) width / 2) {
			this.setVelX(0);
		}

		//Walking audio
		if(!this.jumping && velX != 0 && (System.currentTimeMillis() - walkAudioTimer) > 200) {
			walkAudioTimer = System.currentTimeMillis();
			AudioPlayer.playSound("/striderWalk.wav");
		}

		//Position
		x = Game.clamp(x, 0, Game.sWidth - width);
		y = Game.clamp(y, 0, Game.sHeight - height);
	}
}
