package tmp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import tmp.GameObject;
import tmp.Game;
import tmp.ID;

public class Player extends GameObject {

	Handler handler;
	BufferedImage player_image;
	SpriteSheet ss;
	
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;
	
	public Player(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
		
		ss = new SpriteSheet(Game.sprite_sheet);
		player_image = ss.grabImage(1, 1, 32, 32);
		
		updateCollision();
	}

	public void tick() {
		updateVelocity();
		collision();
		updateCollision();
		collision.invalidate();
	}
	
	//Updates position and adjusts if the player is colliding with any tiles
	private void collision() {
		Area a1;
	    Area a2; 
		
	    //Horizontal Collision, enemy collision check
		x += velX;
		player_image = ss.grabImage(1, 1, 32, 32);
		updateCollision();
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			
			//Check for enemy collisions
			if(tempObject.getID() == ID.Enemy && tempObject.getBounds() != null) {
				//Find area shared by player and enemy
				a1 = new Area(collision);
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);
				
				//Lower HUD health if player is touching an enemy
				if(!a1.isEmpty()) {
					HUD.HEALTH--;
					player_image = ss.grabImage(1, 2, 32, 32);
				}
			}
			
			//Check for collision with tiles
			if(tempObject.getID() == ID.Level) {
				//Find area shared by player and tile
				a1 = new Area(collision);
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);
				
				//Determine if area is shared by player and tile
				if(!a1.isEmpty()) {
					//Log
					if(Game.debugMode) {
						//System.out.println("Collision!");
					}
					
					//Reverse bad movement
					x -= velX;
					updateCollision();
					a1.reset();
					a2.reset();
					a1 = new Area(collision);
					a2 = new Area(tempObject.getBounds());
					a1.intersect(a2);
					
					//Move player to the wall slowly until overlapping by one pixel
					while(a1.isEmpty()) {
						x += Math.signum(velX);
						updateCollision();
						a1.reset();
						a2.reset();
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
						a1.intersect(a2);
					}
					
					//Position player one pixel outside of wall
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
		
		//Set grounded to false in case player has walked over an edge
		this.setGrounded(false);
		
		//Loop through all objects in search of tiles
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			
			//Check for collision with tiles
			if(tempObject.getID() == ID.Level) {
				//Find area shared by player and tile
				a1 = new Area(collision);
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);
				
				//Determine if any area is shared by player and tile
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
					
					//Move player to the wall slowly until overlapping by one pixel
					while(a1.isEmpty()) {
						y += Math.signum(velY);
						updateCollision();
						a1.reset();
						a2.reset();
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
						a1.intersect(a2);
					}
					
					//Position player one pixel outside of wall
					y -= Math.signum(velY);
					updateCollision();
					
					if(Math.signum(velY) == 1) {
						this.setGrounded(true);
					}
					velY = 0;
				}
				a1.reset();
				a2.reset();
				
				//If jump button still held at the end of a jump, jump again
				if(KeyInput.keyDown[4] && this.isGrounded()) {
					if(Game.debugMode == true) {
						System.out.println("Jumped again!");
					}
					this.velY -= 20; 
					this.setGrounded(false); 
				}
			}
		}
	}

	public void render(Graphics g) {
		//Draw player
		g.drawImage(player_image, (int) x, (int) y, null);
		
		//Draw collision box
		if(Game.debugMode == true) {
			g.setColor(Color.GREEN);
			g.drawPolygon(collision);
		}
	}

	public Polygon getBounds() {
		return collision;
	}

	//moves collision box with player.
	protected void updateCollision() {
		xCollision = new int[] {(int) x, ((int) x) + 32, ((int) x) + 32, (int) x};
		yCollision = new int[] {(int) y, (int) y, ((int) y) + 32, ((int) y) + 32};
		
		collision = new Polygon();
		collision.xpoints = xCollision;
		collision.ypoints = yCollision;
		collision.npoints = xCollision.length;
	}
	
	protected void updateVelocity() {
		//Velocity -- (0 = left), (1 = right), (2 = neutral)
		if(!this.isGrounded()) {
			if(Game.debugMode == true) {
				//System.out.println("Airborne!");
			}
			velY += 1; 
			velY = Game.clamp(velY, -20, 10);
		}
		else {
			velY = 0;
		}
		
		if(this.xDirection == 0) { 
			if(this.isGrounded()) {
				velX = -5; 
			} 
			else {
				velX -= 1;
			}
		}
		else if(this.xDirection == 1) { 
			if(this.isGrounded()) {
				velX = 5; 
			} 
			else {
				velX += 1;
			} 
		}
		else if (this.isGrounded()) { velX = 0; }
		
		velX = Game.clamp(velX, -5, 5);
		
		//Position
		x = Game.clamp(x, 0, Game.sWidth - 32);
		y = Game.clamp(y, 0, Game.sHeight - 32);
	}
}
