package enemy;

import tmp.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class WispEnemy extends GameObject {

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

	public WispEnemy(int x, int y, int width, int height, ID id, Handler handler, int retreatNum) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.luminosity = 100;
		this.retreatTimer = Game.clamp(retreatNum, 0, 300);
		this.animationFrame = 1;
		this.animationDelay = 1;
		
		ss = new SpriteSheet(Game.sprite_sheet_wisp);
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
		x += velX;
		y += velY;
		updateCollision();
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
			AudioPlayer.playSound("/wisp_ambient_2.wav");
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
				AudioPlayer.playSound("/wisp_ambient_1.wav");
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
