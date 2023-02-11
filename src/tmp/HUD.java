package tmp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HUD {
	
	public static int HEALTH = 100;
	private int greenValue = 255;
	
	private int score = 0;
	private int scoreTimer = 0;
	private int level = 0;
	
	public void tick() {
		HEALTH = Game.clamp(HEALTH, 0, 100);
		greenValue = Game.clamp(greenValue, 0, 255);
		
		greenValue = HEALTH * 2;
		
		if(!Game.transitioning && !Game.escapeGame) {
			scoreTimer++;
			if(scoreTimer >= 20) {
				score++;
				scoreTimer = 0;
			}
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(24, 24, 200, 32);
		g.setColor(new Color(75, greenValue, 0));
		g.fillRect(24, 24, HEALTH * 2, 32);
		g.setColor(Color.white);
		g.drawRect(24, 24, 200, 32);
		
		g.setFont(new Font("Helvetica", Font.PLAIN, 12));
		g.drawString("Score: " + score, 240, 36);
		g.drawString("Level: " + level, 240, 52);
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
