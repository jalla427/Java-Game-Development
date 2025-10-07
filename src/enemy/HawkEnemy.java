package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import tmp.*;

public class HawkEnemy extends GameObject {

	private int animationFrame;
	private float animationDelay;
	private int enemySpriteNum = 0;
	private int spriteSet = 0;
	
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int maxSpeed = 6;
	double homingTimer = 0;
	double retreatTimer;
	boolean attacking = true;
	
	public HawkEnemy(int x, int y, int width, int height, ID id, int retreatNum) {
		super(x, y, width, height, id);

		this.luminosity = 100;
		this.retreatTimer = Game.clamp(retreatNum, 0, 300);
		this.animationFrame = 1;
		this.animationDelay = 1;

		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 1; Game.unlockedSkins[8] = true; }
		
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

		//Find area shared by enemy and by tile
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
		}
		
		//Vertical Collision
		y += velY * Game.deltaTime;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);

		//Find area shared by enemy and tile
		a1 = new Area(collision);
		a1.intersect(a2);
				
		//Determine if any area is shared by enemy and by tile
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
		}
	}

	public void render(Graphics g) {

		//Cycles animation frame
		if(attacking) {
			this.animationDelay += 1 * Game.deltaTime;
			if(this.animationDelay >= 10) {
				this.animationDelay = 1;
				if(this.animationFrame < 6) {
					this.animationFrame++;
				}
				else {
					this.animationFrame = 1;
				}
			}
		} else {
			this.animationFrame = 7;
		}

		g.drawImage(Game.enemySpriteSheets[enemySpriteNum].grabImageFast(1 + spriteSet, this.animationFrame), (int) x, (int) y, null);
		
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
		homingTimer += 1 * Game.deltaTime;
		if(retreatTimer >= 300 && attacking) {
			attacking = false;
			luminosity = 0;
			AudioPlayer.playSound("/hawkOffBeep.wav");
		}
		
		if(!attacking) {
			if(Handler.playerX > this.x) {
				velX = velX - (1 * Game.deltaTime);
				velX = Game.clamp(velX, -2, 2);
			}
			else {
				velX = velX + (1 * Game.deltaTime);
				velX = Game.clamp(velX, -2, 2);
			}
			velY -= 1 * Game.deltaTime;
			velY = Game.clamp(velY, -4, 4);
			
			retreatTimer -= 7 * Game.deltaTime;
			if(retreatTimer <= 0) {
				attacking = true;
				luminosity = 100;
				AudioPlayer.playSound("/hawkOnBeep.wav");
			}
		}
		else {
			retreatTimer += 1 * Game.deltaTime;
		}
		
		if(attacking) {
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
