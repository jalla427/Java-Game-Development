package tmp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import enemy.HawkEnemy;
import level.DemoMain;
import level.LevelCollection;
import level.TombTileMapBuilder;
import tmp.AudioPlayer;
import tmp.Game.STATE;

public class Menu extends MouseAdapter {
	
	private Game game;
	private Handler handler;
	
	private Random r;
	private int bWidth = 200;
	private int bHeight = 64;
	public int finalScore = 0;
	
	public Menu(Game game, Handler handler) {
		this.game = game;
		this.handler = handler;
	}
	
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
			
		if(game.gameState == STATE.Menu) {
			//Quit
			if(mouseOver(mx, my, (game.sWidth/2) - 100, 450, bWidth, bHeight)) {
				System.exit(1);
			}
			//Settings
			if(mouseOver(mx, my, (game.sWidth/2) - 100, 350, bWidth, bHeight)) {
				game.gameState = STATE.Settings;
				AudioPlayer.playSound("res/buttonClick.wav", -20f);
			}
			//Play
			if(mouseOver(mx, my, (game.sWidth/2) - 100, 250, bWidth, bHeight)) {
				Game.hud.setLevel(1);
				AudioPlayer.playSound("res/buttonClick.wav", -20f);
			}
		}
		
		if(game.gameState == STATE.Settings) {
			//Back
			if(mouseOver(mx, my, (game.sWidth/2) - 100, 450, bWidth, bHeight)) {
				game.gameState = STATE.Menu;
				AudioPlayer.playSound("res/buttonClick.wav", -20f);
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}
	
	private boolean mouseOver(int mx, int my, int x, int y, int width, int height) {
		if(mx > x && mx < x + width && my > y && my < y + height) {
			return true;
		}
		else return false;
	}
	
	public void tick() {
		
	}
	
	public void render(Graphics g) {
		Font fnt = new Font("arial", 1, 50);
		Font fnt2 = new Font("arial", 1, 30);
		Font fnt3 = new Font("arial", 1, 18);
		
		if(game.gameState == STATE.Menu) {
			g.setFont(fnt);
			g.setColor(Color.WHITE);
			g.drawString("Menu", (game.sWidth/2) - 65, 200);
			
			g.setFont(fnt2);
			g.drawRect((game.sWidth/2) - 100, 250, bWidth, bHeight);
			g.drawString("Play", (game.sWidth/2) - 30, 295);
			
			g.drawRect((game.sWidth/2) - 100, 350, bWidth, bHeight);
			g.drawString("Settings", (game.sWidth/2) - 55, 395);
			
			g.drawRect((game.sWidth/2) - 100, 450, bWidth, bHeight);
			g.drawString("Quit", (game.sWidth/2) - 30, 495);
		}
		
		if(game.gameState == STATE.Settings) {
			g.setFont(fnt);
			g.setColor(Color.WHITE);
			g.drawString("Settings", (game.sWidth/2) - 95, 200);
			
			g.setFont(fnt2);
			g.drawRect((game.sWidth/2) - (bWidth/2), 450, bWidth, bHeight);
			g.drawString("Menu", (game.sWidth/2) - 40, 495);
		}
	}
}
