package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class GolemEnemy extends GameObject {

	private final Handler handler;
	private BufferedImage enemy_image;
	private int animationFrame;
	private int animationDelay = 5;
	private int animationDelayTimer;
	private int direction = 1;
	long walkAudioTimer = 0;
	SpriteSheet ss;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int maxSpeed = 3;
	int jumpTimer = 0;
	int waitTimer = 0;
	boolean attacking = false;
	boolean rising = false;
	boolean jumping = false;
	boolean xCollided = false;

	public GolemEnemy(int x, int y, int width, int height, ID id, Handler handler) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.luminosity = 100;
		this.setGrounded(false);
		this.direction = 1;
		this.animationFrame = 1;
		this.animationDelayTimer = 1;
		
		ss = new SpriteSheet(Game.sprite_sheet_golem);
		enemy_image = ss.grabImage(direction, this.animationFrame, width, height);
		
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
		//Determine which row of sprites to use
		findPlayerDirection();

		//Cycles animation frame
		this.animationDelayTimer++;
		if(this.animationDelayTimer >= this.animationDelay) {
			this.animationDelayTimer = 1;
			if(this.animationFrame <= 9) {
				//If attacking, enemy is walking and frame progression is normal
				if(!attacking) {
					if(this.getVelX() == 0) {
						this.animationFrame = 1;
					} else {
						this.animationFrame++;
					}
				} else {
					//If not attacking, enemy reveals spikes and holds position
					//When rising begins, play spike animation in reverse until fully risen
					if(rising) {
						animationFrame--;
						if(animationFrame == 1) {
							this.attacking = false;
							this.rising = false;
						}
					} else if(this.animationFrame < 9) {
						this.animationFrame++;
					}
				}
			}
			else {
				this.animationFrame = 1;
			}
		}

		//Draws correct sprite
		this.enemy_image = ss.grabImage(direction, this.animationFrame, width, height);
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

		//Stopping and revealing spikes is 'attacking'
		if(attacking) {
			this.setVelX(0);
			this.waitTimer++;
			if(!this.rising && (this.waitTimer > 300 || playerDistance >= 200)) {
				AudioPlayer.playSound("/golem_rise.wav");
				this.waitTimer = 0;
				this.rising = true;
			}
		}

		//Walking at the player is 'not attacking'
		if(!attacking) {
			if(Handler.playerX > this.x) {
				this.setVelX(this.getVelX() + 1);
			}
			else {
				this.setVelX(this.getVelX() - 1);
			}

			if(playerDistance <= 70) {
				this.attacking = true;
				this.setVelX(0);
				this.animationFrame = 1;
				AudioPlayer.playSound("/golem_stab.wav");
			}
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
			AudioPlayer.playSound("/golem_walk.wav");
		}

		//Position
		x = Game.clamp(x, 0, Game.sWidth - width);
		y = Game.clamp(y, 0, Game.sHeight - height);
	}

	private void findPlayerDirection() {
		//First two sprite sheet rows are right/left, last two rows are right/left while firing
		if(Handler.playerX >= this.getX()) {
			this.direction = 1;
		} else {
			this.direction = 2;
		}
		if(attacking) {
			this.direction += 2;
		}
	}
}