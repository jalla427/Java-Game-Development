package tmp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import enemy.HawkEnemy;
import level.DemoMain;
import level.LevelCollection;
import level.TileMapBuilder;
import tmp.AudioPlayer;
import tmp.Game.STATE;

public class Menu extends MouseAdapter {
	
	private final Handler handler;
	
	private Random r;
	protected static int bWidth = 200;
	protected static int bHeight = 64;
	public int finalScore = 0;
	
	public Menu(Handler handler) {
		this.handler = handler;
	}
	
	public void mousePressed(MouseEvent e) {

	}
	
	public void mouseReleased(MouseEvent e) {
		//Grab mouse coordinates and check for overlap with any button
		int mx = e.getX();
		int my = e.getY();
		String buttonClicked = handler.getButtonAtLocation(mx, my);

		if(Game.gameState == STATE.Menu) {
			//Quit
			if(buttonClicked == "Quit") {
				System.exit(1);
			}
			//Settings
			if(buttonClicked == "Settings") {
				Game.gameState = STATE.Settings;
				handler.clearButtons();
				AudioPlayer.playSound("res/buttonClick.wav", -20);
			}
			//Play
			if(buttonClicked == "Play") {
				Game.hud.setLevel(1);
				AudioPlayer.playSound("res/buttonClick.wav", -20);
			}
		}

		if(Game.gameState == STATE.Settings) {
			//Back
			if(buttonClicked == "Menu") {
				Game.gameState = STATE.Menu;
				handler.clearButtons();
				AudioPlayer.playSound("res/buttonClick.wav", -20);
			}
		}

		if(Game.gameState == STATE.Game) {
			//Return to menu from gameover
			if(buttonClicked == "Quit") {
				AudioPlayer.playSound("res/buttonClick.wav", -20);
				HUD.HEALTH = 100;
				Game.hud.setScore(0);
				Game.hud.setLevel(0);
				Game.escapeGame = false;
				Game.gameOver = false;
				Game.gameState = STATE.Menu;
				handler.clearButtons();
				Game.quit = true;
			}
		}
	}
	
	protected void tick() {
		Font fnt = new Font("arial", 1, 50);
		Font fnt2 = new Font("arial", 1, 30);
		Font fnt3 = new Font("arial", 1, 18);
		Color deepRed = new Color(100, 0, 0);
		Color tan = new Color(71, 45, 0);
		boolean buttonsFound = handler.areButtons();

		if(Game.gameState == STATE.Menu) {
			if(!buttonsFound) {
				handler.addButton(new Button(handler, fnt2, tan, Color.WHITE, "Play", (Game.sWidth/2) - (bWidth/2), 250, bWidth, bHeight));
				handler.addButton(new Button(handler, fnt2, tan, Color.WHITE, "Settings", (Game.sWidth/2) - (bWidth/2), 350, bWidth, bHeight));
				handler.addButton(new Button(handler, fnt2, tan, Color.WHITE, "Quit", (Game.sWidth/2) - (bWidth/2), 450, bWidth, bHeight));
			}
		}

		if(Game.gameState == STATE.Settings) {
			if(!buttonsFound) {
				handler.addButton(new Button(handler, fnt2, tan, Color.WHITE, "Menu", (Game.sWidth/2) - (bWidth/2), 450, bWidth, bHeight));
			}
		}

		if(Game.gameState == STATE.Game) {
			if(Game.escapeGame) {
				if(!buttonsFound) {
					handler.addButton(new Button(handler, fnt2, tan, Color.WHITE, "Quit", (Game.sWidth/2) - 100, 350, bWidth, bHeight));
				}
			}
		}
	}
	
	protected void render(Graphics g) {
		Font fnt = new Font("arial", 1, 50);
		Font fnt2 = new Font("arial", 1, 30);
		Font fnt3 = new Font("arial", 1, 18);
		Color deepRed = new Color(100, 0, 0);
		Color tan = new Color(71, 45, 0);
		
		if(Game.gameState == STATE.Menu) {
			g.setFont(fnt);
			g.setColor(Color.WHITE);
			g.drawString("Menu", (Game.sWidth/2) - 65, 200);
		}
		
		if(Game.gameState == STATE.Settings) {
			g.setFont(fnt);
			g.setColor(Color.WHITE);
			g.drawString("Settings", (Game.sWidth/2) - 95, 200);
		}
		
		if(Game.gameState == STATE.Game) {
			if(Game.escapeGame) {
				drawBoxedText(g, fnt2, deepRed, Color.WHITE, "Gameover!", (Game.sWidth/2) - 100, 275, bWidth, bHeight);
			}
		}
	}
	
	private void drawBoxedText(Graphics g, Font font, Color colorOne, Color colorTwo, String text, int x, int y, int width, int height) {
		FontMetrics metrics = g.getFontMetrics(font);
		int textX = x + (width - metrics.stringWidth(text)) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setColor(colorOne);
		g.fillRect(x, y, width, height);
		
		g.setColor(colorTwo);
		g.drawRect(x, y, width, height);
		
		g.setFont(font);
		g.drawString(text, textX, textY);
	}
}
