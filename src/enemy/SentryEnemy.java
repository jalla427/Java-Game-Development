package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import tmp.AudioPlayer;
import tmp.Game;
import tmp.GameObject;
import tmp.Handler;
import tmp.ID;
import tmp.SpriteSheet;

public class SentryEnemy extends GameObject {
	
	public Handler handler;
	private BufferedImage enemy_image;
	private SpriteSheet ss;
	private int spriteSet = 0;
	
	private int timer = 0;
	private int fireRate = 150;

	public SentryEnemy(float x, float y, int width, int height, ID id, Handler handler, int fireRate, int timerOffset) {
		super(x, y, width, height, id);
		
		this.handler = handler;
		this.velX = 0;
		this.velY = 0;
		this.fireRate = Game.clamp(fireRate, 20, 500);
		
		timer = Game.clamp(timerOffset, 0, fireRate - 10);
		
		ss = new SpriteSheet(Game.sprite_sheet_sentry);
		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 2; }
		enemy_image = ss.grabImage(1, 2 + spriteSet, width, height);
	}

	public void tick() {
		timer++;
		if(timer == fireRate - 10) {
			luminosity = width;
			enemy_image = ss.grabImage(1, 1 + spriteSet, width, height);
		}
		if(timer >= fireRate) {
			timer = 0;
			luminosity = 0;
			enemy_image = ss.grabImage(1, 2 + spriteSet, width, height);

			AudioPlayer.playSound("/bulletFire.wav");
			handler.addBullet(new Bullet(this.x + (width/2), this.y + (height/2), 10, 10, ID.Enemy, handler, Handler.playerX + 16, Handler.playerY + 16, 7, 1));
		}
	}

	public void render(Graphics g) {
		g.drawImage(enemy_image, (int) x, (int) y, null);
	}
	
	public Polygon getBounds() {
		
		return null;
	}

}
