package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import tmp.*;

public class HawkEnemy extends GameObject {
	
	private final Handler handler;
	private BufferedImage enemy_image;
	private int animationFrame;
	private int animationDelay;
	SpriteSheet ss;
	
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int maxSpeed = 6;
	int homingTimer = 0;
	int retreatTimer;
	boolean attacking = true;
	
	public HawkEnemy(int x, int y, int width, int height, ID id, Handler handler, int retreatNum) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.luminosity = 100;
		this.retreatTimer = Game.clamp(retreatNum, 0, 300);
		this.animationFrame = 1;
		this.animationDelay = 1;
		
		ss = new SpriteSheet(Game.sprite_sheet_hawk);
		enemy_image = ss.grabImage(1, 1, width, height);
		
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
	    Area a2; 
		
	    //Horizontal Collision
		x += velX;
		updateCollision();
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			
			//Check for collision with tiles
			if(tempObject.getID() == ID.Level) {
				//Find area shared by enemy and by tile
				a1 = new Area(collision);
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
			
			//Check for collision with tiles
			if(tempObject.getID() == ID.Level) {
				//Find area shared by enemy and tile
				a1 = new Area(collision);
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
				}
				a1.reset();
				a2.reset();
			}
		}
	}

	public void render(Graphics g) {
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
		homingTimer++;
		if(retreatTimer >= 300 && attacking) {
			attacking = false;
			luminosity = 0;
			enemy_image = ss.grabImage(1, 4, width, height);
			AudioPlayer.playSound("res/hawkOffBeep.wav");
		}
		
		if(!attacking) {
			if(Handler.playerX > this.x) {
				velX = velX - 1;
				velX = Game.clamp(velX, -2, 2);
			}
			else {
				velX = velX + 1;
				velX = Game.clamp(velX, -2, 2);
			}
			velY -= 1;
			velY = Game.clamp(velY, -4, 4);
			
			retreatTimer -= 7;
			if(retreatTimer <= 0) {
				attacking = true;
				luminosity = 100;
				enemy_image = ss.grabImage(1, 1, width, height);
				AudioPlayer.playSound("res/hawkOnBeep.wav");
			}
		}
		else {
			retreatTimer++;
		}
		
		if(attacking) {
			//Cycles animation frame
			enemy_image = ss.grabImage(1, this.animationFrame, width, height);
			this.animationDelay++;
			if(this.animationDelay >= 15) {
				this.animationDelay = 1;
				if(this.animationFrame < 3) {
					this.animationFrame++;
				}
				else {
					this.animationFrame = 1;
				}
			}

			if(homingTimer >= 10) {
				if(Handler.playerX > this.x) {
					velX = velX + 1;
				}
				else {
					velX = velX - 1;
				}
				if(Handler.playerY > this.y) {
					velY = velY + 1;
				}
				else {
					velY = velY - 1;
				}
				homingTimer = 0;
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
