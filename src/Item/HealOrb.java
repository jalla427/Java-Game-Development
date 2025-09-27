package Item;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class HealOrb extends GameObject {

	private BufferedImage coin_image;
	private int animationFrame;
	private int animationDelay;
	private boolean animationForward = true;
	SpriteSheet ss;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private int orbHeal = 20;
	private int maxSpeed = 6;

	public HealOrb(float x, float y, int width, int height, float speedOne, float speedTwo, ID id) {
		super(x, y, width, height, id);
		
		double[] speeds = getSpeed(speedOne, speedTwo);

		this.animationFrame = 1;
		this.animationDelay = 1;

		ss = Game.sprite_sheet_coin;
		coin_image = ss.grabImageFast(2, 1);

		this.luminosity = 50;
		this.velX = (float) speeds[0];
		this.velY = (float) speeds[1];

		if(Game.hardMode) {
			this.orbHeal = 10;
		}
	}

	public void tick() {
		//Update position
		collision();
		updateCollision();
		
		//If coin is off-screen, delete it
		if(x > Game.sWidth || x < -this.getWidth() || y > Game.sHeight || y < -this.getHeight()) {
			Handler.object.remove(this);
			if(Game.debugMode) {
				System.out.println("*** Heal Orb Out of Bounds! ***");
			}
		}
	}

	private void collision() {
		Area a1;
		Area a2 = Handler.currentLevelArea;

		//Horizontal Collision
		this.setX(this.getX() + this.getVelX());
		updateCollision();

		//Find area shared by coin and tile
		a1 = new Area(collision);
		a1.intersect(a2);

		//Determine if area is shared by coin and tile
		if(!a1.isEmpty()) {
			//Reverse bad movement
			this.setX(this.getX() - this.getVelX());
			updateCollision();
			a1.reset();
			a1 = new Area(collision);
			a1.intersect(a2);

			//Move coin to the wall slowly until overlapping by one pixel
			while(a1.isEmpty()) {
				x += Math.signum(this.getVelX());
				updateCollision();
				a1.reset();
				a1 = new Area(collision);
				a1.intersect(a2);
			}

			//Position coin one pixel outside of wall
			x -= Math.signum(this.getVelX());
			updateCollision();

			//Flip velocity to bounce coin
			this.setVelX(-this.getVelX());
			this.setVelX((this.getVelX() * (float) ((1.5 * Math.random()) + 0.3)));

			//Play bounce sound
			AudioPlayer.playSound("/coinBounce.wav");
		}

		//Vertical Collision
		this.setY(this.getY() + this.getVelY());
		updateCollision();

		//Set grounded to false in case coin has moved over an edge
		this.setGrounded(false);

		//Find area shared by coin and tile
		a1 = new Area(collision);
		a1.intersect(a2);

		//Determine if any area is shared by coin and tile
		if(!a1.isEmpty()) {
			//Reverse bad movement
			this.setY(this.getY() - this.getVelY());
			updateCollision();
			a1.reset();
			a1 = new Area(collision);
			a1.intersect(a2);

			//Move coin to the wall slowly until overlapping by one pixel
			while(a1.isEmpty()) {
				y += Math.signum(this.getVelY());
				updateCollision();
				a1.reset();
				a1 = new Area(collision);
				a1.intersect(a2);
			}

			//Position coin one pixel outside of wall
			y -= Math.signum(this.getVelY());
			updateCollision();

			//Flip velocity to bounce coin
			this.setVelY(-this.getVelY());
			this.setVelY((this.getVelY() * (float) ((2 * Math.random()) + 0.5)));

			//Play bounce sound
			AudioPlayer.playSound("/coinBounce.wav");
		}

		//Check for player collision, find area shared by coin and player
		a1 = new Area(collision);
		a2 = new Area(Handler.playerObject.getBounds());
		a1.intersect(a2);

		//Determine if area is shared by coin and player
		if (!a1.isEmpty()) {
			Game.coinsLeft--;
			HUD.HEALTH += orbHeal;
			AudioPlayer.playSound("/coinGet.wav");
			Handler.object.remove(this);
		}

		this.setVelX(Game.clamp(this.getVelX(), -maxSpeed, maxSpeed));
		this.setVelY(Game.clamp(this.getVelY(), -maxSpeed, maxSpeed));
	}

	public void render(Graphics g) {
		coin_image = ss.grabImageFast(2, this.animationFrame);
		this.animationDelay++;
		if(this.animationDelay >= 3) {
			this.animationDelay = 1;
			if(this.animationForward) {
				if(this.animationFrame < 17) {
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

		if(Game.crazyCoins) { coinSpeed = coinSpeed * 2; }

		double dx = x;
		double dy = y;
		double angle = Math.atan2(dy, dx);
		
		speeds[0] = Math.cos(angle) * coinSpeed;
		speeds[1] = Math.sin(angle) * coinSpeed;
		
		return speeds;
	}
}
