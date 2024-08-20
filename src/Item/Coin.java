package Item;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class Coin extends GameObject {

	private final Handler handler;
	private BufferedImage coin_image;
	private int animationFrame;
	private int animationDelay;
	private boolean animationForward = true;
	SpriteSheet ss;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private int coinValue = 50;
	private int coinHeal = 5;
	private int maxSpeed = 5;

	public Coin(float x, float y, int width, int height, float speedOne, float speedTwo, ID id, Handler handler) {
		super(x, y, width, height, id);
		
		double[] speeds = getSpeed(speedOne, speedTwo);
		
		this.handler = handler;
		this.animationFrame = 1;
		this.animationDelay = 1;

		ss = new SpriteSheet(Game.sprite_sheet_coin);
		coin_image = ss.grabImage(1, 1, width, height);

		this.luminosity = 50;
		this.velX = (float) speeds[0];
		this.velY = (float) speeds[1];
	}

	public void tick() {
		//Update position
		collision();
		updateCollision();
		
		//If coin is off-screen, delete it
		if(x > Game.sWidth || x < -this.getWidth() || y > Game.sHeight || y < -this.getHeight()) {
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
		this.setX(this.getX() + this.getVelX());
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
					AudioPlayer.playSound("res/coinGet.wav");
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
					this.setX(this.getX() - this.getVelX());
					updateCollision();
					a1.reset();
					a2.reset();
					a1 = new Area(collision);
					a2 = new Area(tempObject.getBounds());
					a1.intersect(a2);

					//Move coin to the wall slowly until overlapping by one pixel
					while(a1.isEmpty()) {
						x += Math.signum(this.getVelX());
						updateCollision();
						a1.reset();
						a2.reset();
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
						a1.intersect(a2);
					}

					//Position coin one pixel outside of wall
					x -= Math.signum(this.getVelX());
					updateCollision();

					//Flip velocity to bounce coin
					this.setVelX(-this.getVelX());
					this.setVelX((this.getVelX() * (float) ((1.5 * Math.random()) + 0.3)));

					//Play bounce sound
					AudioPlayer.playSound("res/coinBounce.wav");
				}
				a1.reset();
				a2.reset();
			}
		}

		//Vertical Collision
		this.setY(this.getY() + this.getVelY());
		updateCollision();

		//Set grounded to false in case coin has moved over an edge
		this.setGrounded(false);

		//Loop through all objects in search of tiles
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);

			//Check for collision with tiles
			if(tempObject.getID() == ID.Level) {
				//Find area shared by coin and tile
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
					this.setY(this.getY() - this.getVelY());
					updateCollision();
					a1.reset();
					a2.reset();
					a1 = new Area(collision);
					a2 = new Area(tempObject.getBounds());
					a1.intersect(a2);

					//Move coin to the wall slowly until overlapping by one pixel
					while(a1.isEmpty()) {
						y += Math.signum(this.getVelY());
						updateCollision();
						a1.reset();
						a2.reset();
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
						a1.intersect(a2);
					}

					//Position coin one pixel outside of wall
					y -= Math.signum(this.getVelY());
					updateCollision();

					//Flip velocity to bounce coin
					this.setVelY(-this.getVelY());
					this.setVelY((this.getVelY() * (float) ((2 * Math.random()) + 0.5)));

					//Play bounce sound
					AudioPlayer.playSound("res/coinBounce.wav");
				}
				a1.reset();
				a2.reset();
			}
		}
		this.setVelX(Game.clamp(this.getVelX(), -maxSpeed, maxSpeed));
		this.setVelY(Game.clamp(this.getVelY(), -maxSpeed, maxSpeed));
	}

	public void render(Graphics g) {
		coin_image = ss.grabImage(1, this.animationFrame, width, height);
		this.animationDelay++;
		if(this.animationDelay >= 15) {
			this.animationDelay = 1;
			if(this.animationForward) {
				if(this.animationFrame < 9) {
					this.animationFrame++;
				}
				else {
					this.animationForward = false;
					this.animationFrame--;
				}
			}
			else {
				if(this.animationFrame > 1) {
					this.animationFrame--;
				}
				else {
					this.animationForward = true;
					this.animationFrame++;
				}
			}
		}

		g.drawImage(coin_image, (int) x, (int) y, null);
		
		//Draw collision box
		if(Game.debugMode && collision != null) {
			g.setColor(Color.GREEN);
			g.drawPolygon(collision);

//			System.out.println("--------------");
//			System.out.println("velX: " + this.getVelX());
//			System.out.println("velY: " + this.getVelY());
		}
		
	}
	
	//moves collision box with coin
	protected void updateCollision() {
		int pointX = (int) this.getX();
		int pointY = (int) this.getY();
		int objWidth = (int) this.getWidth();
		int objHeight = (int) this.getHeight();

		xCollision = new int[] {pointX, (pointX) + objWidth, (pointX) + objWidth, pointX};
		yCollision = new int[] {pointY, pointY, (pointY) + objHeight, (pointY) + objHeight};
		
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
		
		return speeds;
	}
}
