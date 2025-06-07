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
	private SpriteSheet ss;
	private int spriteSet = 0;
	int direction = 1;

	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;

	int maxSpeed = 2;
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
		
		ss = Game.sprite_sheet_wisp;
		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 4; }
		enemy_image = ss.grabImageFast(direction + spriteSet, this.animationFrame);
		
		velX = 0;
		velY = 0;
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
		//Cycles animation frame
		enemy_image = ss.grabImageFast(direction + spriteSet, this.animationFrame);
		this.animationDelay++;
		if(this.animationDelay >= 10) {
			this.animationDelay = 1;
			if(this.animationFrame < 9) {
				this.animationFrame++;
			}
			else {
				this.animationFrame = 1;
			}
		}

		findPlayerDirection();
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
			if((Math.random() * 2) >= 1) {
				AudioPlayer.playSound("/wisp_ambient_2.wav");
			} else {
				AudioPlayer.playSound("/wisp_ambient_1.wav");
			}

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
				velX = 0;
				velY = 0;
				if(retreatTimer <= -100) {
					attacking = true;
					retreatTimer = 0;

					//Fire barrage of fireballs
					AudioPlayer.playSound("/wisp_fire.wav");
					handler.addBullet(new Bullet(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/4), 10, 10, ID.Enemy, Handler.playerX + 16, Handler.playerY - 34, 7, false, 2));
					handler.addBullet(new Bullet(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/4), 10, 10, ID.Enemy, Handler.playerX + 16, Handler.playerY + 16, 7, false, 2));
					handler.addBullet(new Bullet(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/4), 10, 10, ID.Enemy, Handler.playerX + 16, Handler.playerY + 66, 7, false, 2));
				}
			}
		}
		else {
			retreatTimer++;
		}
		
		if(attacking) {
			if(homingTimer >= 10) {
				if(Handler.playerX > this.x) {
					velX = velX + 2;
				}
				else {
					velX = velX - 2;
				}
				if(Handler.playerY > this.y) {
					velY = velY + 2;
				}
				else {
					velY = velY - 2;
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

	private void findPlayerDirection() {
		//First two sprite sheet rows are right/left, last two rows are right/left while firing
		if(Handler.playerX >= this.getX()) {
			this.direction = 2;
		} else {
			this.direction = 1;
		}
		if(!attacking) {
			this.direction += 2;
		}
	}
}
