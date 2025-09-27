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

	private BufferedImage enemy_image;
	private SpriteSheet ss;
	private int spriteSet = 0;
	
	private int timer = 0;
	private int fireRate = 150;

	public SentryEnemy(float x, float y, int width, int height, ID id, int fireRate, int timerOffset) {
		super(x, y, width, height, id);

		this.velX = 0;
		this.velY = 0;
		this.fireRate = Game.clamp(fireRate, 20, 500);
		
		timer = Game.clamp(timerOffset, 0, fireRate - 10);
		
		ss = Game.sprite_sheet_sentry;
		if(Math.random() <= Game.altEnemySkinOdds) { spriteSet = 2; }
		enemy_image = ss.grabImageFast(1, 2 + spriteSet);
	}

	public void tick() {
		timer++;
		if(timer == fireRate - 10) {
			luminosity = width;
			enemy_image = ss.grabImageFast(1, 1 + spriteSet);
		}
		if(timer >= fireRate) {
			timer = 0;
			luminosity = 0;
			enemy_image = ss.grabImageFast(1, 2 + spriteSet);

			AudioPlayer.playSound("/bulletFire.wav");
			Handler.addBullet(new Bullet(this.x + (width/2), this.y + (height/2), 16, 16, ID.Enemy, Handler.playerX + 16, Handler.playerY + 16, 7, false, 1));
		}
	}

	public void render(Graphics g) {
		g.drawImage(enemy_image, (int) x, (int) y, null);
	}
	
	public Polygon getBounds() {
		
		return null;
	}

}
