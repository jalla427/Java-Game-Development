package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class StriderEnemy extends GameObject {

	private final Handler handler;
	private BufferedImage enemy_image;
	private int animationFrame;
	private int animationDelay = 200;
	private int animationDelayTimer;
	int animType;
	long walkAudioTimer = 0;
	private SpriteSheet ss;
	private int spriteSet = 0;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int maxSpeed = 3;
	int sightRange = 300;
	int jumpTimer = 0;
	int wanderTimer = 0;
	boolean attacking = true;
	boolean jumping = false;
	boolean xCollided = false;

	public StriderEnemy(int x, int y, int width, int height, ID id, Handler handler) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.luminosity = 100;
		this.setGrounded(false);
		this.animType = 1;
		this.animationFrame = 1;
		this.animationDelayTimer = 1;
		this.sightRange += (int) (Math.random() * 5);
		
		ss = new SpriteSheet(Game.sprite_sheet_strider);
		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 4; }
		enemy_image = ss.grabImage(1 + spriteSet, 1, width, height);
		
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
		x += velX;
		updateCollision();

		//Find area shared by enemy and by tiles
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

			//Update jumping AI
			if(!jumping) {
				this.xCollided = true;
			}
		}
		
		//Vertical Collision
		y += velY;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);

		//Find area shared by enemy and tile
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

		if(this.getVelX() == 0) {
			this.animType = 1;
			if(this.animType != currentAnimType) {
				this.animationDelayTimer = 0;
				this.animationDelay = 200;
			}
		}
		else {
			if(Handler.playerX > this.x) {
				this.animType = 2;
				if(this.animType != currentAnimType) {
					this.animationDelayTimer = 0;
					this.animationDelay = 20;
				}
			}
			else {
				this.animType = 3;
				if(this.animType != currentAnimType) {
					this.animationDelayTimer = 0;
					this.animationDelay = 20;
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

		this.enemy_image = ss.grabImage(animType + spriteSet, this.animationFrame, width, height);
		g.drawImage(this.enemy_image, (int) x, (int) y, null);
		
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
				this.wanderTimer++;
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
					this.wanderTimer = 0 + (int) (Math.random() * 10);
				}
			}
		}
		
		if(attacking) {
			if(Handler.playerX > this.x) {
				this.setVelX(this.getVelX() + 1);
			}
			else {
				this.setVelX(this.getVelX() - 1);
			}

			//Decide to jump
			if(!this.jumping && ((this.xCollided && this.isGrounded()) || (Handler.playerY < this.y && this.isGrounded() && this.jumpTimer >= 150))) {
				this.jumpTimer = (int) (20 * Math.random());
				this.setVelY(this.getVelY() - 20);
				this.setGrounded(false);
				this.jumping = true;
				this.xCollided = false;
			}
			else {
				this.jumpTimer++;
			}

			if(playerDistance >= this.sightRange) {
				this.attacking = false;
				this.setVelX(0);
				AudioPlayer.playSound("/striderOffBeep.wav");
			}
		}

		if(!this.isGrounded()) {
			this.setVelY(this.getVelY() + 1);
			this.setVelY(Game.clamp(this.getVelY(), -20, 10));
		}
		else {
			this.setVelY(0);
		}
		
		//Limit speed
		this.setVelX(Game.clamp(this.getVelX(), -maxSpeed, maxSpeed));

		//Stop adjusting velocity X if overlapping player on the X axis (prevents spinning in place)
		if(Game.calculateDistance(this.getX(), this.getX(), Handler.playerX, this.getX()) < this.getWidth() / 2) {
			this.setVelX(0);
		}

		//Walking audio
		if(!this.jumping && this.getVelX() != 0 && (System.currentTimeMillis() - walkAudioTimer) > 200) {
			walkAudioTimer = System.currentTimeMillis();
			AudioPlayer.playSound("/striderWalk.wav");
		}

		//Position
		x = Game.clamp(x, 0, Game.sWidth - width);
		y = Game.clamp(y, 0, Game.sHeight - height);
	}
}
