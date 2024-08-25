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
	SpriteSheet ss;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	float playerX = 0;
	float playerY = 0;
	int maxSpeed = 6;
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
		enemy_image = ss.grabImage(1, 1, width, height);
		
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
	    Area a2; 
		
	    //Horizontal Collision
		x += velX;
		updateCollision();
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			a1 = new Area(collision);

			//While looping through all objects, retrieve player cords for later
			if(tempObject.getID() == ID.Player) {
				playerX = tempObject.getX();
				playerY = tempObject.getY();
			}

			//Check for collision with tiles
			else if(tempObject.getID() == ID.Level) {
				//Find area shared by enemy and by tile
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);
				
				//Determine if area is shared by enemy and tile
				if(!a1.isEmpty()) {
					//Reverse bad movement
					x -= velX;
					updateCollision();
					a1.reset();
					a2.reset();
					a1 = new Area(collision);
					a2 = new Area(tempObject.getBounds());
					a1.intersect(a2);
					
					//Move enemy to the wall slowly until overlapping by one pixel
					while(a1.isEmpty()) {
						x += Math.signum(velX);
						updateCollision();
						a1.reset();
						a2.reset();
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
						a1.intersect(a2);
					}
					
					//Position enemy one pixel outside of wall
					x -= Math.signum(velX);
					updateCollision();
					velX = 0;

					//End attack after collision
					collided = true;
				}
				a1.reset();
				a2.reset();
			}
		}
		
		//Vertical Collision
		y += velY;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);
		
		//Loop through all objects in search of tiles
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			a1 = new Area(collision);
			
			//Check for collision with tiles
			if(tempObject.getID() == ID.Level) {
				//Find area shared by enemy and tile
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);
				
				//Determine if any area is shared by enemy and by tile
				if(!a1.isEmpty()) {
					//Log
					if(Game.debugMode) {
						//System.out.println("Collision!");
					}
					
					//Reverse bad movement
					y -= velY;
					updateCollision();
					a1.reset();
					a2.reset();
					a1 = new Area(collision);
					a2 = new Area(tempObject.getBounds());
					a1.intersect(a2);
					
					//Move enemy to the wall slowly until overlapping by one pixel
					while(a1.isEmpty()) {
						y += Math.signum(velY);
						updateCollision();
						a1.reset();
						a2.reset();
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
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
				a1.reset();
				a2.reset();
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

		this.enemy_image = ss.grabImage(animType, this.animationFrame, width, height);
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
		//Find x and y distance to player separately for comparison
		float playerXDistance = (int) Game.calculateDistance(this.getX(), this.getY(), playerX, this.getY());
		float playerYDistance = (int) Game.calculateDistance(this.getX(), this.getY(), this.getX(), playerY);

		if(!attacking) {
			restingTimer++;
			if(restingTimer >= 20) {
				restingTimer = 0;

				//Out of four directions, go the direction that is the shortest distance to the player
				if(playerX >= (this.getX() + (this.getWidth()/2)) && playerXDistance >= playerYDistance) {
					this.velX = maxSpeed;
				}
				else if(playerX < (this.getX() + (this.getWidth()/2)) && playerXDistance >= playerYDistance) {
					this.velX = -maxSpeed;
				}
				else if(playerY >= (this.getY() + (this.getWidth()/2)) && playerYDistance > playerXDistance) {
					this.velY = maxSpeed;
				}
				else if(playerY < (this.getY() + (this.getWidth()/2)) && playerYDistance > playerXDistance) {
					this.velY = -maxSpeed;
				}
				this.attacking = true;
			}
		}
		
		if(attacking) {
			if(collided) {
				//Play noise only if full motion occurred (avoids repeated slamming noises)
				if(motionLocked) {
					AudioPlayer.playSound("res/thumperSlam.wav");
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
				if(playerY >= this.getY()) {
					this.setVelY(Game.clamp((int) (maxSpeed * Math.random()), 3, maxSpeed));
				}
				else {
					this.setVelY(Game.clamp((int) (-maxSpeed * Math.random()), -maxSpeed, -3));
				}
				AudioPlayer.playSound("res/thumperOn.wav");
				motionLocked = true;
			}
			else if(this.getVelY() != 0 && playerYDistance <= 16 && !motionLocked) {
				this.setVelY(0);
				if(playerX >= this.getX()) {
					this.setVelX(Game.clamp((int) (maxSpeed * Math.random()), 3, maxSpeed));
				}
				else {
					this.setVelX(Game.clamp((int) (-maxSpeed * Math.random()), -maxSpeed, -3));
				}
				AudioPlayer.playSound("res/thumperOn.wav");
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
