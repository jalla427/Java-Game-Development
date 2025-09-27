package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class AnnihilatorEnemy extends GameObject {
	private BufferedImage enemy_image;
	private int animationFrame;
	private int animationDelay;
	private SpriteSheet ss;
	private int spriteSet = 0;
	private int firingOffset = 0;
	private int firingTimer = 0;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private int maxSpeed = 3;

	public AnnihilatorEnemy(int x, int y, int width, int height, ID id) {
		super(x, y, width, height, id);

		this.luminosity = 200;
		this.animationFrame = 1;
		this.animationDelay = 1;
		
		ss = Game.sprite_sheet_annihilator;
		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 2; }
		enemy_image = ss.grabImageFast(1 + spriteSet, 1);
		
		velX = 5;
		velY = 0;
	}

	public void tick() {
		//Fire bullets timer
		firingTimer++;
		if(firingTimer >= 120) {
			firingOffset = 1;
			if(firingTimer >= 140 && Handler.enemyList.size() < 15) {
				Handler.addBullet(new Bullet(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/4), 16, 16, ID.Enemy, Handler.playerX + 16, Handler.playerY - 16 - (float)(Math.random()*4), (int) (5 + (Math.random()*6)), true, 3));
				Handler.addBullet(new Bullet(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/4), 16, 16, ID.Enemy, Handler.playerX + 16, Handler.playerY + 45 + (float)(Math.random()*10), (int) (5 + (Math.random()*6)), true, 3));
				firingTimer = 0;
				firingOffset = 0;
			}
		}

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

		//Find area shared by enemy and by tile
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
			x -= Math.signum(this.getVelX());
			updateCollision();
			this.setVelX(-this.getVelX());
		}
		
		//Vertical Collision
		y += velY;
		updateCollision();
		
		//Set grounded to false in case enemy has walked over an edge
		this.setGrounded(false);

		//Find area shared by enemy and tile
		a1 = new Area(collision);
		a1.intersect(a2);
				
		//Determine if any area is shared by enemy and by tile
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
		}
	}

	public void render(Graphics g) {
		//Cycles animation frame
		this.animationDelay++;
		if(this.animationDelay >= 10) {
			this.animationDelay = 1;
			if(this.animationFrame < 6) {
				this.animationFrame++;
			}
			else {
				this.animationFrame = 1;
			}
		}

		this.enemy_image = ss.grabImageFast(1 + spriteSet + firingOffset, this.animationFrame);
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

	//Moves collision box with enemy
	protected void updateCollision() {
		xCollision = new int[] {(int) x, ((int) x) + width, ((int) x) + width, (int) x};
		yCollision = new int[] {(int) y, (int) y, ((int) y) + height, ((int) y) + height};
		
		collision = new Polygon();
		collision.xpoints = xCollision;
		collision.ypoints = yCollision;
		collision.npoints = xCollision.length;
	}

	protected void updateVelocity() {
		//Limit speed
		this.setVelX(Game.clamp(this.getVelX(), -maxSpeed, maxSpeed));
		this.setVelY(Game.clamp(this.getVelY(), -maxSpeed, maxSpeed));
		
		//Position
		this.setX(Game.clamp(x, 0, Game.sWidth - width));
		this.setY(Game.clamp(y, 0, Game.sHeight - height));
	}
}
