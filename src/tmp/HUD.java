package tmp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HUD {
	
	public static int HEALTH = 100;
	private int greenValue = 255;
	protected int coinStart = 1;
	
	protected static int score = 0;
	private int scoreTimer = 0;
	private int level = 0;
	
	public void tick() {
		HEALTH = Game.clamp(HEALTH, 0, 100);
		greenValue = Game.clamp(greenValue, 0, 255);
		
		greenValue = HEALTH * 2;
	}
	
	public void render(Graphics g) {
		//Health bar
		g.setColor(Color.gray);
		g.fillRect(24, 24, 200, 32);
		g.setColor(new Color(75, greenValue, 0));
		g.fillRect(24, 24, HEALTH * 2, 32);
		g.drawImage(Game.meter_overlay, 24, 24, null);

		//Score + Level
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica", Font.PLAIN, 12));
		g.drawString("Score: " + score, 240, 36);
		if(getLevel() == 99) {
			g.drawString("Blitz Infinite Survival", 240, 52);
		}
		else {
			g.drawString("Level: " + level, 240, 52);
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
