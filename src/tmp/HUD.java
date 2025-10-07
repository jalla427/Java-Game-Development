package tmp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HUD {
	
	public static float health = 100;
	private int greenValue = 255;
	protected int coinStart = 1;
	public static float shield = 0;
	public static boolean hasShield = false;
	
	protected static int score = 0;
	private int scoreTimer = 0;
	private int level = 0;
	
	public void tick() {
		health = Game.clamp(health, 0, 100);
		if(!Game.transitioning && !Game.paused && Game.coinsLeft > 0) shield -= 0.1 * Game.deltaTime;
		shield = Game.clamp(shield, 0, 100);
		greenValue = (int) (health * 2);
		greenValue = Game.clamp(greenValue, 0, 255);
	}
	
	public void render(Graphics g) {
		//Health bar
		g.setColor(Color.gray);
		g.fillRect(24, 24, 200, 32);
		g.setColor(new Color(75, greenValue, 0));
		g.fillRect(24, 24, (int) (health * 2), 32);
		g.drawImage(Game.meter_overlay, 24, 24, null);

		//Shield
		g.setColor(new Color(90, 210, 255));
		g.fillRect(24, 24, (int) (shield * 2), 32);
		g.drawImage(Game.meter_overlay, 24, 24, null);
		if(hasShield) { g.drawImage(Game.sprite_sheet_menu_buttons.grabImageFast(2, 4), 230, 24, null); }
		else { g.drawImage(Game.sprite_sheet_menu_buttons.grabImageFast(3, 4), 230, 24, null); }

		//Score + Level
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica", Font.PLAIN, 12));
		g.drawString("Score: " + score, 270, 36);
		if(getLevel() == 99) {
			g.drawString("Blitz Infinite Survival", 270, 52);
		}
		else {
			g.drawString("Level: " + level, 270, 52);
		}

		//Level progress bar
		if(getLevel() < 99) {
			g.setColor(new Color(250, 250, 0));
			g.fillRect(Game.sWidth - 224, 24, 200, 32);
			g.setColor(Color.gray);
			g.fillRect(Game.sWidth - 224, 24, (int)(((double)Game.coinsLeft/(double)coinStart) * 200), 32);
		}
		else {
			g.setColor(Color.MAGENTA);
			if(!Game.gameOver) { g.fillRect(Game.sWidth - 224, 24, 200, 32); }
			g.setColor(Color.gray);
			if(!Game.gameOver) {
				g.fillRect(Game.sWidth - 224, 24, (int) ((((double) Game.coinsLeft % 50) / 50) * 200), 32);
			}
			else {
				g.fillRect(Game.sWidth - 224, 24, 200, 32);
			}
		}
		g.drawImage(Game.meter_overlay, Game.sWidth - 224, 24, null);
	}

	private void handleScoreByTime() {
		if(!Game.transitioning && !Game.escapeGame) {
			scoreTimer++;
			if(scoreTimer >= 20) {
				score++;
				scoreTimer = 0;
			}
		}
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
}
