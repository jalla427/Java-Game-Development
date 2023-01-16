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
	SpriteSheet ss;
	
	private int timer = 0;
	private int fireRate = 150;

	public SentryEnemy(float x, float y, ID id, Handler handler, int fireRate, int timerOffset) {
		super(x, y, id);
		
		this.handler = handler;
		this.velX = 0;
		this.velY = 0;
		this.fireRate = Game.clamp(fireRate, 20, 500);
		
		timer = Game.clamp(timerOffset, 0, fireRate - 10);
		
		ss = new SpriteSheet(Game.sprite_sheet_sentry);
		enemy_image = ss.grabImage(1, 2, 20, 20);
	}

	public void tick() {
		timer++;
		if(timer == fireRate - 10) {
			enemy_image = ss.grabImage(1, 1, 20, 20);
		}
		if(timer >= fireRate) {
			timer = 0;
			enemy_image = ss.grabImage(1, 2, 20, 20);
			for(int i = 0; i < handler.object.size(); i++) {
				GameObject tempObject = handler.object.get(i);
				
				if(tempObject.getID() == ID.Player) {
					AudioPlayer.playSound("res/bulletFire.wav", -20f);
					handler.addObject(new Bullet(this.x + 10, this.y + 10, ID.Enemy, handler, tempObject.getX() + 16, tempObject.getY() + 16));
				}
			}
		}
		
	}

	public void render(Graphics g) {
		//g.setColor(Color.RED);
		//g.fillRect((int) x, (int) y, 32, 32);
		g.drawImage(enemy_image, (int) x, (int) y, null);
	}
	
	public Polygon getBounds() {
		
		return null;
	}

}
