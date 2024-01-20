package Item;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;

public class Coin extends GameObject {

	private final Handler handler;
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private int coinValue = 50;
	private int coinHeal = 5;
	private int maxSpeed = 5;

	public Coin(float x, float y, float speedOne, float speedTwo, ID id, Handler handler) {
		super(x, y, id);
		
		double[] speeds = getSpeed(speedOne, speedTwo);
		
		this.handler = handler;
		this.width = 10;
		this.height = 10;
		this.velX = (float) speeds[0];
		this.velY = (float) speeds[1];
	}

	public void tick() {
		//Update position
		collision();
		updateCollision();
		
		//If coin is off screen, delete it
		if(x > Game.sWidth || x < -this.width || y > Game.sHeight || y < -this.height) {
			handler.object.remove(this);
			if(Game.debugMode) {
				System.out.println("*** Coin Out of Bounds! ***");
			}
		}
	}

	private void collision() {
		Area a1;
		Area a2;

		//Horizontal Collision
		x += velX;
		updateCollision();
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);

			//Check for player collision
			if(tempObject.getID() == ID.Player) {
				//Find area shared by coin and player
				a1 = new Area(collision);
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);

				//Determine if area is shared by coin and player
				if(!a1.isEmpty()) {
					Game.coinsLeft--;
					Game.hud.setScore(Game.hud.getScore() + coinValue);
					HUD.HEALTH += coinHeal;
					handler.object.remove(this);
				}
			}

			//Check for collision with tiles
			if(tempObject.getID() == ID.Level) {
				//Find area shared by coin and tile
				a1 = new Area(collision);
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);

				//Determine if area is shared by coin and tile
				if(!a1.isEmpty()) {
					//Log
					if(Game.debugMode) {
						//System.out.println("Collision! - X: " + velX);
					}

					//Reverse bad movement
					x -= velX;
					updateCollision();
					a1.reset();
					a2.reset();
					a1 = new Area(collision);
					a2 = new Area(tempObject.getBounds());
					a1.intersect(a2);

					//Move coin to the wall slowly until overlapping by one pixel
					while(a1.isEmpty()) {
						x += Math.signum(velX);
						updateCollision();
						a1.reset();
						a2.reset();
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
						a1.intersect(a2);
					}

					//Position coin one pixel outside of wall
					x -= Math.signum(velX);
					updateCollision();

					//Flip velocity to bounce coin
					velX = -velX;
					velX *= (float)((1.5 * Math.random()) + 0.3);
				}
				a1.reset();
				a2.reset();
			}
		}

		//Vertical Collision
		y += velY;
		updateCollision();

		//Set grounded to false in case coin has moved over an edge
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

				//Determine if any area is shared by coin and tile
				if(!a1.isEmpty()) {
					//Log
					if(Game.debugMode) {
						//System.out.println("Collision! - Y: " + velY);
					}

					//Reverse bad movement
					y -= velY;
					updateCollision();
					a1.reset();
					a2.reset();
					a1 = new Area(collision);
					a2 = new Area(tempObject.getBounds());
					a1.intersect(a2);

					//Move coin to the wall slowly until overlapping by one pixel
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

					//Flip velocity to bounce coin
					velY = -velY;
					velY *= (float)((2 * Math.random()) + 0.5);
				}
				a1.reset();
				a2.reset();
			}
		}
		velX = Game.clamp(velX, -maxSpeed, maxSpeed);
		velY = Game.clamp(velY, -maxSpeed, maxSpeed);
	}

	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect((int) x, (int) y, 10, 10);
		
		//Draw collision box
		if(Game.debugMode && collision != null) {
			g.setColor(Color.GREEN);
			g.drawPolygon(collision);
		}
		
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

	public Polygon getBounds() {
		return collision;
	}
	
	public double[] getSpeed(float x, float y) {
		double[] speeds = new double[2];
		int coinSpeed = 2;

		double dx = x;
		double dy = y;
		double angle = Math.atan2(dy, dx);
		
		speeds[0] = Math.cos(angle) * coinSpeed;
		speeds[1] = Math.sin(angle) * coinSpeed;
		
//		System.out.println("--------------");
//		System.out.println("velX: " + speeds[0]);
//		System.out.println("velY: " + speeds[1]);
		
		return speeds;
	}
}
