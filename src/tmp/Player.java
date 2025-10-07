package tmp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class Player extends GameObject {

	private BufferedImage player_image;
	private final SpriteSheet ss;
	private int playerSkin = 1;
	private int animationFrame;
	private int animationDelay = 5;
	private float animationDelayTimer;
	private int direction = 1;
	private int animationOffset = 0;
	private boolean damaged = false;
	protected boolean jumping = false;
	
	private Polygon collision;
	private int[] xCollision;
	private int[] yCollision;
	
	public Player(int x, int y, int width, int height, int playerSkin, ID id) {
		super(x, y, width, height, id);
		this.luminosity = 300;
		this.playerSkin = playerSkin;
		this.animationFrame = 1;
		this.animationDelayTimer = 1;
		
		ss = Game.sprite_sheet;
		player_image = ss.grabImageFast(this.playerSkin, 1);
		
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
		damaged = false;
		Area a1;
		Area a2 = Handler.currentLevelArea;
		
	    //Horizontal Collision
		x += velX * Game.deltaTime;
		updateCollision();

		//Find area shared by player and tile
		a1 = new Area(collision);
		a1.intersect(a2);

		if(!a1.isEmpty()) {
			//Reverse bad movement
			x -= velX * Game.deltaTime;
			updateCollision();
			a1.reset();
			a1 = new Area(collision);
			a1.intersect(a2);
					
			//Move player to the wall slowly until overlapping by one pixel
			while(a1.isEmpty()) {
				x += Math.signum(velX);
				updateCollision();
				a1.reset();
				a1 = new Area(collision);
				a1.intersect(a2);
			}
					
			//Position player one pixel outside of wall
			x -= Math.signum(velX);
			updateCollision();
			velX = 0;
		}
		
		//Vertical Collision
		y += velY * Game.deltaTime;
		updateCollision();
		if(velY >= 0) { jumping = false; } //Player is now falling
		
		//Set grounded to false in case player has walked over an edge
		this.setGrounded(false);

		//Find area shared by player and tile
		a1 = new Area(collision);
		a1.intersect(a2);

		if(!a1.isEmpty()) {
			//Reverse bad movement
			y -= velY * Game.deltaTime;
			updateCollision();
			a1.reset();
			a1 = new Area(collision);
			a1.intersect(a2);
					
			//Move player to the wall slowly until overlapping by one pixel
			while(a1.isEmpty()) {
				y += Math.signum(velY);
				updateCollision();
				a1.reset();
				a1 = new Area(collision);
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
				
		//If jump button still held at the end of a jump, jump again
		if(KeyInput.keyDown[2] && this.isGrounded() && Game.playerControl) {
			this.velY -= 20;
			this.setGrounded(false);
			this.jumping = true;
		}

		//Check for enemy collisions
		for(int i = 0; i < Handler.enemyList.size(); i++) {
			GameObject tempObject = Handler.enemyList.get(i);

			if(tempObject.getBounds() != null) {
				//Find area shared by player and enemy
				a1 = new Area(collision);
				a2 = new Area(tempObject.getBounds());
				a1.intersect(a2);

				//Lower HUD health if player is touching an enemy
				if(!a1.isEmpty()) {
					if(HUD.shield <= 0) { HUD.health -= 1 * Game.deltaTime; }
					else { HUD.shield -= 1 * Game.deltaTime; }
					damaged = true;
				}
			}
		}
	}

	public void render(Graphics g) {
		this.animationDelayTimer += 1 * Game.deltaTime;
		animationOffset = 0;

		if(Game.gameOver) { //Dim player if gameover
			if(this.getLuminosity() > 0) {
				this.setLuminosity(this.getLuminosity() - (1 * Game.deltaTime));
				player_image = ss.grabImageFast(playerSkin, 1);
			}
			else {
				player_image = ss.grabImageFast(playerSkin, 14);
			}
		} else if(damaged) {
			player_image = ss.grabImageFast(playerSkin, 13);
		} else if(this.animationDelayTimer >= this.animationDelay) { //Cycles animation frame
			this.animationDelayTimer = 1;
			if(this.animationFrame < 4) {
				//If attacking, enemy is walking and frame progression is normal
				if(this.getVelX() == 0) {
					this.animationFrame = 1;
				} else {
					this.animationFrame++;
				}
			}
			else {
				this.animationFrame = 1;
			}
			if(jumping) { animationOffset = animationOffset + 4; }
			player_image = ss.grabImageFast(playerSkin, animationFrame + animationOffset);
		}

		//Draw player
		g.drawImage(player_image, (int) x, (int) y, null);
		
		//Draw collision box
		if(Game.debugMode) {
			g.setColor(Color.GREEN);
			g.drawPolygon(collision);
		}
	}

	public Polygon getBounds() {
		return collision;
	}

	//moves collision box with player.
	protected void updateCollision() {
		xCollision = new int[] {(int) x, ((int) x) + width, ((int) x) + width, (int) x};
		yCollision = new int[] {(int) y, (int) y, ((int) y) + height, ((int) y) + height};
		
		collision = new Polygon();
		collision.xpoints = xCollision;
		collision.ypoints = yCollision;
		collision.npoints = xCollision.length;
	}
	
	protected void updateVelocity() {
		//Y Direction
		if(!this.isGrounded()) {
			velY += 1 * Game.deltaTime;
			velY = Game.clamp(velY, -20, 10);
		}
		else {
			velY = 0;
		}

		//X Direction -- (0 = left), (1 = right), (2 = neutral)
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
		else if (this.isGrounded()) {
			velX = 0;
		}

		//Limit X velocity
		velX = Game.clamp(velX, -5, 5);

		if(Game.gameOver || Game.levelEnd) {
			this.setVelX(0);
			this.setxDirection(2);
		}
		
		//Position
		x = Game.clamp(x, 0, Game.sWidth - width);
		y = Game.clamp(y, 0, Game.sHeight - height);
	}
}
