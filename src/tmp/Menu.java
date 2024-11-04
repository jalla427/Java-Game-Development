package tmp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import tmp.Game.STATE;

public class Menu extends MouseAdapter {
	
	private final Handler handler;
	SpriteSheet menu_buttons;
	SpriteSheet player_skins;

	private Random r;
	protected static int bWidth = 200;
	protected static int bHeight = 64;
	public int finalScore = 0;
	
	public Menu(Handler handler) {
		this.handler = handler;
		menu_buttons = new SpriteSheet(Game.sprite_sheet_menu_buttons);
		player_skins = new SpriteSheet(Game.sprite_sheet);
	}
	
	public void mousePressed(MouseEvent e) {

	}
	
	public void mouseReleased(MouseEvent e) {
		//Grab mouse coordinates and check for overlap with any button
		int mx = e.getX();
		int my = e.getY();
		Button buttonClicked = handler.getButtonAtLocation(mx, my);

		if(Game.gameState == STATE.Menu && buttonClicked != null) {
			//Quit
			if(buttonClicked.getName() == "Quit") {
				System.exit(1);
			}
			//Settings
			if(buttonClicked.getName() == "Settings") {
				Game.gameState = STATE.Settings;
				handler.clearButtons();
				AudioPlayer.playSound("/buttonClick.wav");
			}
			//Play
			if(buttonClicked.getName() == "Play") {
				Game.hud.setLevel(1);
				AudioPlayer.playSound("/buttonClick.wav");
			}
		}

		if(Game.gameState == STATE.Settings && buttonClicked != null) {
			//Volume
			if(buttonClicked.getName() == "LeftVolume") {
				Game.gameVolume = Game.clamp(Game.gameVolume - 10, 0, 100);
				AudioPlayer.playSound("/buttonClick.wav");
			}
			if(buttonClicked.getName() == "RightVolume") {
				Game.gameVolume = Game.clamp(Game.gameVolume + 10, 0, 100);
				AudioPlayer.playSound("/buttonClick.wav");
			}
			//Difficulty Mods
			if(buttonClicked.getName() == "Hard Mode: Off") {
				Game.hardMode = true;
				buttonClicked.setName("Hard Mode: On");
				AudioPlayer.playSound("/buttonClick.wav");
			} else if(buttonClicked.getName() == "Hard Mode: On") {
				Game.hardMode = false;
				buttonClicked.setName("Hard Mode: Off");
				AudioPlayer.playSound("/buttonClick.wav");
			}
			if(buttonClicked.getName() == "Dark Mode: Off") {
				Game.darkMode = true;
				buttonClicked.setName("Dark Mode: On");
				AudioPlayer.playSound("/buttonClick.wav");
			} else if(buttonClicked.getName() == "Dark Mode: On") {
				Game.darkMode = false;
				buttonClicked.setName("Dark Mode: Off");
				AudioPlayer.playSound("/buttonClick.wav");
			}
			if(buttonClicked.getName() == "Crazy Coins: Off") {
				Game.crazyCoins = true;
				buttonClicked.setName("Crazy Coins: On");
				AudioPlayer.playSound("/buttonClick.wav");
			} else if(buttonClicked.getName() == "Crazy Coins: On") {
				Game.crazyCoins = false;
				buttonClicked.setName("Crazy Coins: Off");
				AudioPlayer.playSound("/buttonClick.wav");
			}
			//Back
			if(buttonClicked.getName() == "Menu") {
				Game.gameState = STATE.Menu;
				handler.clearButtons();
				AudioPlayer.playSound("/buttonClick.wav");
			}
			if(buttonClicked.getName().contains("playerSkinOption")) {
				//Retrieve number at the end of button name (should be 1-4)
				int skinNum = Integer.valueOf(buttonClicked.getName().substring((buttonClicked.getName().length() - 1)));
				if(Game.playerSkin != skinNum && skinNum <= Game.unlockedSkins.length && skinNum >= 1 && Game.unlockedSkins[skinNum - 1]) {
					handler.getImageButtonByName(buttonClicked.getName()).setImage(player_skins.grabImage(skinNum, 1, 32, 32));
					handler.getImageButtonByName("playerSkinOption" + Game.playerSkin).setImage(player_skins.grabImage(Game.playerSkin, 3, 32, 32));
					Game.playerSkin = skinNum;
				}
			}
		}

		if(Game.gameState == STATE.Game && buttonClicked != null) {
			//Return to menu from gameover
			if(buttonClicked.getName() == "Quit") {
				AudioPlayer.playSound("/buttonClick.wav");
				HUD.HEALTH = 100;
				Game.hud.setScore(0);
				Game.hud.setLevel(0);
				Game.escapeGame = false;
				Game.gameOver = false;
				Game.gameState = STATE.Menu;
				handler.clearButtons();
				Game.paused = false;
				Game.quit = true;
			}
			if(buttonClicked.getName() == "Resume") {
				AudioPlayer.playSound("/buttonClick.wav");
				Handler.clearButtons();
				Game.paused = false;
			}
			if(buttonClicked.getName() == "Next Level") {
				AudioPlayer.playSound("/buttonClick.wav");
				Handler.clearButtons();
				Game.transitioning = true;
				Game.levelEnd = false;
			}
		}
	}
	
	protected void tick() {
		Font fnt = new Font("arial", 1, 50);
		Font fnt2 = new Font("arial", 1, 30);
		Font fnt3 = new Font("arial", 1, 18);
		Color tan = new Color(71, 45, 0);
		Color deepRed = new Color(100, 0, 0);
		Color gold = new Color(205, 165, 0);
		boolean buttonsFound = handler.areButtons();

		if(Game.gameState == STATE.Menu) {
			if(!buttonsFound) {
				handler.addButton(new ImageTextButton(handler, fnt2, Color.WHITE, "Play", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 250, bWidth, bHeight));
				handler.addButton(new ImageTextButton(handler, fnt2, Color.WHITE, "Settings", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 350, bWidth, bHeight));
				handler.addButton(new ImageTextButton(handler, fnt2, Color.WHITE, "Quit", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 450, bWidth, bHeight));
			}
		}

		if(Game.gameState == STATE.Settings) {
			if(!buttonsFound) {
				//Create volume buttons
				handler.addButton(new ImageTextButton(handler, fnt2, Color.WHITE, "Menu", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 450, bWidth, bHeight));
				handler.addImageButton(new ImageButton(handler,"LeftVolume", menu_buttons.grabImage(1, 1, 32, 32), ((Game.sWidth/2) - 16) - 100, 230, 32, 32));
				handler.addImageButton(new ImageButton(handler,"RightVolume", menu_buttons.grabImage(1, 2, 32, 32), ((Game.sWidth/2) - 16) + 100, 230, 32, 32));

				//Create difficulty mod buttons
				if(Game.hardMode) {
					handler.addButton(new RectTextButton(handler, fnt3, deepRed, Color.WHITE, "Hard Mode: On", (Game.sWidth/2 - 200) - 80, 330, 160, 32));
				} else {
					handler.addButton(new RectTextButton(handler, fnt3, deepRed, Color.WHITE, "Hard Mode: Off", (Game.sWidth/2 - 200) - 80, 330, 160, 32));
				}
				if(Game.darkMode) {
					handler.addButton(new RectTextButton(handler, fnt3, Color.BLACK, Color.WHITE, "Dark Mode: On", (Game.sWidth/2 - 200) - 80, 370, 160, 32));
				} else {
					handler.addButton(new RectTextButton(handler, fnt3, Color.BLACK, Color.WHITE, "Dark Mode: Off", (Game.sWidth/2 - 200) - 80, 370, 160, 32));
				}
				if(Game.crazyCoins) {
					handler.addButton(new RectTextButton(handler, fnt3, gold, Color.WHITE, "Crazy Coins: On", (Game.sWidth/2 - 200) - 80, 410, 160, 32));
				} else {
					handler.addButton(new RectTextButton(handler, fnt3, gold, Color.WHITE, "Crazy Coins: Off", (Game.sWidth/2 - 200) - 80, 410, 160, 32));
				}

				//Create player skin buttons
				for(int i = 1; i <= Game.unlockedSkins.length; i++) {
					int rowHeightMod = 0;
					if(i > Game.unlockedSkins.length / 2) { rowHeightMod = 1; }
					if(Game.playerSkin == i) {
						handler.addImageButton(new ImageButton(handler,"playerSkinOption" + i, player_skins.grabImage(i, 1, 32, 32), (Game.sWidth/2) + (i * 36) - 105 - ((Game.unlockedSkins.length/2) * 36 * rowHeightMod), 320 + (rowHeightMod * 36), 32, 32));
					}
					else if(Game.unlockedSkins[i - 1]) {
						handler.addImageButton(new ImageButton(handler,"playerSkinOption" + i, player_skins.grabImage(i, 3, 32, 32), (Game.sWidth/2) + (i * 36) - 105 - ((Game.unlockedSkins.length/2) * 36 * rowHeightMod), 320 + (rowHeightMod * 36), 32, 32));
					} else {
						handler.addImageButton(new ImageButton(handler,"playerSkinOption" + i, player_skins.grabImage(i, 4, 32, 32), (Game.sWidth/2) + (i * 36) - 105 - ((Game.unlockedSkins.length/2) * 36 * rowHeightMod), 320 + (rowHeightMod * 36), 32, 32));
					}
				}
			}
		}

		if(Game.gameState == STATE.Game) {
			if(Game.escapeGame || Game.paused || Game.levelEnd) {
				if(!buttonsFound) {
					//In game button appearance should match the level appearance
					BufferedImage currentBtnType = Game.tombButton;
					if(Game.hud.getLevel() < 7 || Game.hud.getLevel() == 19) { currentBtnType = Game.tombButton; }
					if((Game.hud.getLevel() < 13 && Game.hud.getLevel() > 6) || Game.hud.getLevel() == 20 || Game.hud.getLevel() == 22) { currentBtnType = Game.dungeonButton; }
					if((Game.hud.getLevel() < 19 && Game.hud.getLevel() > 12) || Game.hud.getLevel() == 21) { currentBtnType = Game.burningButton; }

					if(Game.paused) {
						handler.addButton(new ImageTextButton(handler, fnt2, Color.WHITE, "Resume", currentBtnType, (Game.sWidth/2) - 100, 275, bWidth, bHeight));
					}
					if(Game.levelEnd && Game.hud.getLevel() < 22) {
						handler.addButton(new ImageTextButton(handler, fnt2, Color.WHITE, "Next Level", currentBtnType, (Game.sWidth/2) - 100, 350, bWidth, bHeight));
					}
					if(Game.paused || Game.gameOver) {
						handler.addButton(new ImageTextButton(handler, fnt2, Color.WHITE, "Quit", currentBtnType, (Game.sWidth/2) - 100, 350, bWidth, bHeight));
					}
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
			drawBoxedText(g, fnt3, tan, Color.WHITE, "Volume: "+ Game.gameVolume + "%", (Game.sWidth/2) - 80, 230, 160, 32);
			drawBoxedText(g, fnt3, tan, Color.WHITE, "Player Skin", (Game.sWidth/2) - 80, 280, 160, 32);
			drawBoxedText(g, fnt3, tan, Color.WHITE, "Score Modifiers", (Game.sWidth/2 - 200) - 80, 280, 160, 32);
		}
		
		if(Game.gameState == STATE.Game) {
			if(Game.escapeGame) {
				drawBoxedText(g, fnt2, deepRed, Color.WHITE, "Gameover!", (Game.sWidth/2) - 100, 275, bWidth, bHeight);
			} else if(Game.levelEnd) {
				drawBoxedText(g, fnt2, deepRed, Color.WHITE, "Level Compelete!", (Game.sWidth/2) - bWidth, 275, bWidth * 2, bHeight);
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
