package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class KeeperEnemy extends GameObject {
	private int animationFrame;
	private int animationDelay;
	private int enemySpriteNum = 9;
	private int spriteSet = 0;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	private int maxSpeed = 4;
	private int homingTimer = 0;
	private boolean coinLock = false;
	private int coinIndex;

	public KeeperEnemy(int x, int y, int width, int height, ID id) {
		super(x, y, width, height, id);

		this.luminosity = 100;
		this.animationFrame = 1;
		this.animationDelay = 1;

		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 1; }
		
		velX = 4;
		velY = 4;
	}

	public void tick() {
		coinLock = targetIsCoin();
		if(!coinLock) { findTargetCoin(); }
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
			x -= Math.signum(velX);
			updateCollision();
			velX = 0;
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
		if(this.animationDelay >= 5) {
			this.animationDelay = 1;
			if(this.animationFrame < 22) {
				this.animationFrame++;
			}
			else {
				this.animationFrame = 1;
			}
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
		homingTimer++;
		GameObject playerTargetObj = Handler.playerObject;
		if((Math.abs(playerTargetObj.getX() - this.getX()) < 80) && (Math.abs(playerTargetObj.getY() - this.getY()) < 80)) {
			if(homingTimer >= 10) {
				if(playerTargetObj.getX() > this.x) {
					velX = velX + 1;
				}
				else {
					velX = velX - 1;
				}
				if(playerTargetObj.getY() > this.y) {
					velY = velY + 1;
				}
				else {
					velY = velY - 1;
				}
				homingTimer = 0;
			}
		}
		else if(coinLock) {
			GameObject coinObject = Handler.object.get(coinIndex);
			if(homingTimer >= 10) {
				if(coinObject.getX() > this.x) {
					velX = velX + 1;
				}
				else {
					velX = velX - 1;
				}
				if(coinObject.getY() > this.y) {
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

	private void findTargetCoin() {
		for(int i = 0; i < Handler.object.size(); i++) {
			GameObject tempObject = Handler.object.get(i);
			if(tempObject.getID() == ID.Coin) {
				coinIndex = i;
				coinLock = true;
				break;
			}
		}
	}

	private boolean targetIsCoin() {
		if(Handler.object.size() > coinIndex) {
			if(Handler.object.get(coinIndex).getID() == ID.Coin) {
				return true;
			}
		}
		return false;
	}
}
