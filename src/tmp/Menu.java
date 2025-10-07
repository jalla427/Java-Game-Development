package tmp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

import tmp.Game.STATE;

public class Menu extends MouseAdapter {
	SpriteSheet menu_buttons;
	SpriteSheet player_skins;
	ImageTextButton volumeDisplay = null;

	private Random r;
	protected static int bWidth = 200;
	protected static int bHeight = 64;
	protected static boolean isBinding = false;
	protected static int bindingTarget = -1;
	
	public Menu() {
		menu_buttons = Game.sprite_sheet_menu_buttons;
		player_skins = Game.sprite_sheet;
	}
	
	public void mouseReleased(MouseEvent e) {
		//Grab mouse coordinates and check for overlap with any button
		int mx = e.getX();
		int my = e.getY();
		Button buttonClicked = Handler.getButtonAtLocation(mx, my);

		//Main menu
		if(Game.gameState == STATE.Menu && buttonClicked != null) {
			//Quit
			if(buttonClicked.getName() == "Quit") {
				Game.writeOutSaveData();
				System.exit(1);
			}
			//Settings
			if(buttonClicked.getName() == "Settings") {
				Game.gameState = STATE.Settings;
				Game.clearButtons = true;
				AudioPlayer.playSound("/buttonClick.wav");
			}
			//Statistics
			if(buttonClicked.getName() == "Statistics") {
				Game.gameState = STATE.Statistics;
				Game.clearButtons = true;
				AudioPlayer.playSound("/buttonClick.wav");
			}
			//Level Select
			if(buttonClicked.getName() == "Level Select") {
				Game.gameState = STATE.LevelSelect;
				Game.clearButtons = true;
				AudioPlayer.playSound("/buttonClick.wav");
			}
			//Play Blitz
			if(buttonClicked.getName() == "Play Blitz") {
				Game.hud.setLevel(99);
				AudioPlayer.playSound("/buttonClick.wav");
			}
			//Play
			if(buttonClicked.getName() == "Play Game") {
				Game.hud.setLevel(1);
				AudioPlayer.playSound("/buttonClick.wav");
			}
		}

		//Settings menu
		if(Game.gameState == STATE.Settings && buttonClicked != null) {
			//Controls menu
			if(buttonClicked.getName() == "Controls") {
				Handler.buttonList.clear();
				Handler.imageButtonList.clear();
				Game.gameState = STATE.Controls;
			}
			//Volume
			if(buttonClicked.getName() == "LeftVolume") {
				Game.gameVolume = Game.clamp(Game.gameVolume - 10, 0, 100);
				AudioPlayer.playSound("/buttonClick.wav");
				Handler.buttonList.clear();
				Handler.imageButtonList.clear();
			}
			if(buttonClicked.getName() == "RightVolume") {
				Game.gameVolume = Game.clamp(Game.gameVolume + 10, 0, 100);
				AudioPlayer.playSound("/buttonClick.wav");
				Handler.buttonList.clear();
				Handler.imageButtonList.clear();
			}
			//Difficulty modes
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
			//Player skin select
			if(buttonClicked.getName().contains("playerSkinOption")) {
				//Retrieve number at the end of button name (should be 1-8)
				int skinNum = Integer.parseInt(buttonClicked.getName().substring((buttonClicked.getName().length() - 1)));
				if(Game.playerSkin != skinNum && skinNum <= Game.unlockedSkins.length && skinNum >= 1 && Game.unlockedSkins[skinNum - 1]) {
					Handler.getImageButtonByName(buttonClicked.getName()).setImage(player_skins.grabImageFast(skinNum, 1));
					Handler.getImageButtonByName("playerSkinOption" + Game.playerSkin).setImage(player_skins.grabImageFast(Game.playerSkin, 14));
					Game.playerSkin = skinNum;
				}
			}
		}

		//Controls menu
		if(Game.gameState == STATE.Controls && buttonClicked != null && !isBinding) {
			//Settings
			if(buttonClicked.getName() == "Settings") {
				Game.gameState = STATE.Settings;
				Game.clearButtons = true;
				AudioPlayer.playSound("/buttonClick.wav");
			}

			//Change keybindings
			if(Objects.equals(buttonClicked.getName(), KeyEvent.getKeyText(KeyInput.keyBinds[0]))) {
				isBinding = true;
				bindingTarget = 0;
				buttonClicked.setName("PRESS ANY KEY");
			}
			if(Objects.equals(buttonClicked.getName(), KeyEvent.getKeyText(KeyInput.keyBinds[1]))) {
				isBinding = true;
				bindingTarget = 1;
				buttonClicked.setName("PRESS ANY KEY");
			}
			if(Objects.equals(buttonClicked.getName(), KeyEvent.getKeyText(KeyInput.keyBinds[2]))) {
				isBinding = true;
				bindingTarget = 2;
				buttonClicked.setName("PRESS ANY KEY");
			}
			if(Objects.equals(buttonClicked.getName(), KeyEvent.getKeyText(KeyInput.keyBinds[3]))) {
				isBinding = true;
				bindingTarget = 3;
				buttonClicked.setName("PRESS ANY KEY");
			}
		}

		//Level select
		if(Game.gameState == STATE.LevelSelect && buttonClicked != null) {
			if(buttonClicked.getName().contains("Level ") && buttonClicked.getName() != "Level Select") {
				AudioPlayer.playSound("/buttonClick.wav");
				Game.levelSelected = Integer.parseInt(buttonClicked.getName().replaceAll("[^\\d]", ""));
			}
		}

		//Return to main menu
		if((Game.gameState == STATE.Settings || Game.gameState == STATE.LevelSelect || Game.gameState == STATE.Statistics) && buttonClicked != null) {
			//Back to main menu
			if(buttonClicked.getName() == "Menu") {
				Game.clearButtons = true;
				Game.gameState = STATE.Menu;
				AudioPlayer.playSound("/buttonClick.wav");
				Game.writeOutSaveData();
			}
		}

		if(Game.gameState == STATE.Game && buttonClicked != null) {
			//Return to menu from gameover
			if(buttonClicked.getName() == "Quit") {
				AudioPlayer.playSound("/buttonClick.wav");
				if(Game.highScore < Game.hud.getScore() && Game.hud.getLevel() <= 22) { Game.highScore = Game.hud.getScore(); }
				if(Game.blitzHighScore < Game.hud.getScore() && Game.hud.getLevel() == 99) { Game.blitzHighScore = Game.hud.getScore(); }
				HUD.health = 100;
				HUD.shield = 0;
				HUD.hasShield = false;
				Game.hud.setScore(0);
				Game.hud.setLevel(0);
				Game.escapeGame = false;
				Game.gameOver = false;
				Game.gameState = STATE.Menu;
				Game.levelEnd = false;
				Game.clearButtons = true;
				Game.paused = false;
				Game.quit = true;
				Game.writeOutSaveData();
			}
			if(buttonClicked.getName() == "Resume") {
				AudioPlayer.playSound("/buttonClick.wav");
				Game.clearButtons = true;
				Game.paused = false;
			}
			if(buttonClicked.getName() == "Next Level") {
				AudioPlayer.playSound("/buttonClick.wav");
				Game.clearButtons = true;
				Game.transitioning = true;
				Game.levelEnd = false;
			}
		}
	}
	
	protected void tick() {
		Font fnt = new Font("arial", 1, 50);
		Font fnt2 = new Font("arial", 1, 30);
		Font fnt3 = new Font("arial", 1, 18);
		Font fnt4 = new Font("arial", 1, 14);
		Color tan = new Color(71, 45, 0);
		Color deepRed = new Color(100, 0, 0);
		Color gold = new Color(205, 165, 0);
		boolean buttonsFound = Handler.areButtons();

		if(Game.gameState == STATE.Menu) {
			if(!buttonsFound) {
				Handler.addButton(new ImageButton("Tomb Game Banner", Game.tombGameBanner, (Game.sWidth/2) - 150, 115, 300, bHeight));
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Play Game", Game.tombButton_long, (Game.sWidth/2) - 150, 286, 300, bHeight));
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Play Blitz", Game.tombButton, (Game.sWidth/2) - bWidth - 10, 355, bWidth, bHeight));
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Level Select", Game.tombButton, (Game.sWidth/2) + 10, 355, bWidth, bHeight));
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Settings", Game.tombButton, (Game.sWidth/2) - bWidth - 10, 425, bWidth, bHeight));
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Statistics", Game.tombButton, (Game.sWidth/2) + 10, 425, bWidth, bHeight));
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Quit", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 494, bWidth, bHeight));
			}
		}

		if(Game.gameState == STATE.Settings) {
			if(!buttonsFound) {
				//Create volume buttons
				Handler.addImageButton(new ImageButton("LeftVolume", menu_buttons.grabImageFast(1, 1), ((Game.sWidth/2) - 16) - 100, 230, 32, 32));
				Handler.addImageButton(new ImageButton("RightVolume", menu_buttons.grabImageFast(1, 2), ((Game.sWidth/2) - 16) + 100, 230, 32, 32));

				//Create difficulty mod buttons
				if(Game.hardMode) {
					Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Hard Mode: On", Game.burningButton_small, (Game.sWidth/2 - 200) - 80, 330, 160, 32));
				} else {
					Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Hard Mode: Off", Game.burningButton_small, (Game.sWidth/2 - 200) - 80, 330, 160, 32));
				}
				if(Game.darkMode) {
					Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Dark Mode: On", Game.dungeonButton_small, (Game.sWidth/2 - 200) - 80, 370, 160, 32));
				} else {
					Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Dark Mode: Off", Game.dungeonButton_small, (Game.sWidth/2 - 200) - 80, 370, 160, 32));
				}
				if(Game.crazyCoins) {
					Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Crazy Coins: On", Game.tombButton_small, (Game.sWidth/2 - 200) - 80, 410, 160, 32));
				} else {
					Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Crazy Coins: Off", Game.tombButton_small, (Game.sWidth/2 - 200) - 80, 410, 160, 32));
				}

				//Create player skin buttons
				for(int i = 1; i <= Game.unlockedSkins.length; i++) {
					int rowHeightMod = 0;
					if(i > Game.unlockedSkins.length / 2) { rowHeightMod = 1; }
					if(Game.playerSkin == i) {
						Handler.addImageButton(new ImageButton("playerSkinOption" + i, player_skins.grabImageFast(i, 1), (Game.sWidth/2) + (i * 36) - 105 - ((Game.unlockedSkins.length/2) * 36 * rowHeightMod), 320 + (rowHeightMod * 36), 32, 32));
					}
					else if(Game.unlockedSkins[i - 1]) {
						Handler.addImageButton(new ImageButton("playerSkinOption" + i, player_skins.grabImageFast(i, 14), (Game.sWidth/2) + (i * 36) - 105 - ((Game.unlockedSkins.length/2) * 36 * rowHeightMod), 320 + (rowHeightMod * 36), 32, 32));
					} else {
						Handler.addImageButton(new ImageButton("playerSkinOption" + i, player_skins.grabImageFast(i, 15), (Game.sWidth/2) + (i * 36) - 105 - ((Game.unlockedSkins.length/2) * 36 * rowHeightMod), 320 + (rowHeightMod * 36), 32, 32));
					}
				}

				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Player Skin", Game.tombButton_small, (Game.sWidth/2) - 80, 280, 160, 32));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Score Modifiers", Game.tombButton_small, (Game.sWidth/2) - 280, 280, 160, 32));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Volume: " + Game.gameVolume + "%", Game.tombButton_small, (Game.sWidth/2) - 80, 230, 160, 32));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Controls", Game.tombButton_small, (Game.sWidth/2) - 80, 410, 160, 32));
			}
		}

		if(Game.gameState == STATE.Controls) {
			if(!buttonsFound) {
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, KeyEvent.getKeyText(KeyInput.keyBinds[0]), Game.tombButton_small, (Game.sWidth/2) - 50, 220, 160, 32));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, KeyEvent.getKeyText(KeyInput.keyBinds[1]), Game.tombButton_small, (Game.sWidth/2) - 50, 260, 160, 32));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, KeyEvent.getKeyText(KeyInput.keyBinds[2]), Game.tombButton_small, (Game.sWidth/2) - 50, 300, 160, 32));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, KeyEvent.getKeyText(KeyInput.keyBinds[3]), Game.tombButton_small, (Game.sWidth/2) - 50, 340, 160, 32));

				Handler.addButton(new ImageButton("Right Label", Game.sprite_sheet_menu_buttons.grabImageFast(2, 3), (Game.sWidth/2) - 90, 220, 32, 32));
				Handler.addButton(new ImageButton("Left Label", Game.sprite_sheet_menu_buttons.grabImageFast(2, 2), (Game.sWidth/2) - 90, 260, 32, 32));
				Handler.addButton(new ImageButton("Jump Label", Game.sprite_sheet_menu_buttons.grabImageFast(2, 1), (Game.sWidth/2) - 90, 300, 32, 32));
				Handler.addButton(new ImageButton("Shield Label", Game.sprite_sheet_menu_buttons.grabImageFast(2, 4), (Game.sWidth/2) - 90, 340, 32, 32));

				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Settings", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 475, bWidth, bHeight));
			}
		}

		if(Game.gameState == STATE.LevelSelect) {
			if(!buttonsFound) {
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Select Level", Game.brightBlueButton_long, (Game.sWidth/2) - 150, 110, 300, bHeight));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 1", Game.tombButton_small, (Game.sWidth/2) - 330, 190, 160, 32));
				if(Game.unlockedLevels[1]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 2", Game.tombButton_small, (Game.sWidth/2) - 165, 190, 160, 32)); }
				if(Game.unlockedLevels[2]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 3", Game.tombButton_small, (Game.sWidth/2) + 5, 190, 160, 32)); }
				if(Game.unlockedLevels[3]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 4", Game.tombButton_small, (Game.sWidth/2) + 170, 190, 160, 32)); }
				if(Game.unlockedLevels[4]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 5", Game.tombButton_small, (Game.sWidth/2) - 330, 230, 160, 32)); }
				if(Game.unlockedLevels[5]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 6", Game.tombButton_small, (Game.sWidth/2) - 165, 230, 160, 32)); }
				if(Game.unlockedLevels[6]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 7", Game.dungeonButton_small, (Game.sWidth/2) + 5, 230, 160, 32)); }
				if(Game.unlockedLevels[7]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 8", Game.dungeonButton_small, (Game.sWidth/2) + 170, 230, 160, 32)); }
				if(Game.unlockedLevels[8]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 9", Game.dungeonButton_small, (Game.sWidth/2) - 330, 270, 160, 32)); }
				if(Game.unlockedLevels[9]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 10", Game.dungeonButton_small, (Game.sWidth/2) - 165, 270, 160, 32)); }
				if(Game.unlockedLevels[10]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 11", Game.dungeonButton_small, (Game.sWidth/2) + 5, 270, 160, 32)); }
				if(Game.unlockedLevels[11]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 12", Game.dungeonButton_small, (Game.sWidth/2) + 170, 270, 160, 32)); }
				if(Game.unlockedLevels[12]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 13", Game.burningButton_small, (Game.sWidth/2) - 330, 310, 160, 32)); }
				if(Game.unlockedLevels[13]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 14", Game.burningButton_small, (Game.sWidth/2) - 165, 310, 160, 32)); }
				if(Game.unlockedLevels[14]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 15", Game.burningButton_small, (Game.sWidth/2) + 5, 310, 160, 32)); }
				if(Game.unlockedLevels[15]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 16", Game.burningButton_small, (Game.sWidth/2) + 170, 310, 160, 32)); }
				if(Game.unlockedLevels[16]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 17", Game.burningButton_small, (Game.sWidth/2) - 330, 350, 160, 32)); }
				if(Game.unlockedLevels[17]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 18", Game.burningButton_small, (Game.sWidth/2) - 165, 350, 160, 32)); }
				if(Game.unlockedLevels[18]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 19", Game.tombButton_small, (Game.sWidth/2) + 5, 350, 160, 32)); }
				if(Game.unlockedLevels[19]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 20", Game.dungeonButton_small, (Game.sWidth/2) + 170, 350, 160, 32)); }
				if(Game.unlockedLevels[20]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 21", Game.burningButton_small, (Game.sWidth/2) - 165, 390, 160, 32)); }
				if(Game.unlockedLevels[21]) { Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Level 22", Game.dungeonButton_small, (Game.sWidth/2) + 5, 390, 160, 32)); }
			}
		}

		if(Game.gameState == STATE.Statistics) {
			if(!buttonsFound) {
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "High Score: " + Game.highScore, Game.brightBlueButton_long, (Game.sWidth/2) - 310, 110, 300, bHeight));
				Handler.addButton(new ImageTextButton(fnt3, Color.WHITE, "Blitz High Score: " + Game.blitzHighScore, Game.brightBlueButton_long, (Game.sWidth/2) + 10, 110, 300, bHeight));

				Handler.addButton(new ImageDoubleTextButton(fnt4, Color.WHITE, "Reach the cavern levels", "Reach the campaign's", "cavern levels", Game.brightRedButton, (Game.sWidth/2) - 310, 179, bWidth, bHeight));
				Handler.addButton(new ImageDoubleTextButton(fnt4, Color.WHITE, "Reach the burning levels", "Reach the campaign's", "burning levels", Game.brightRedButton, (Game.sWidth/2) + 10, 179, bWidth, bHeight));
				Handler.addButton(new ImageDoubleTextButton(fnt4, Color.WHITE, "Reach the final levels", "Reach the campaign's", "final levels", Game.brightRedButton, (Game.sWidth/2) - 310, 248, bWidth, bHeight));
				Handler.addButton(new ImageDoubleTextButton(fnt4, Color.WHITE, "Score 6000 points", "Score 6000 points in the", "campaign", Game.brightRedButton, (Game.sWidth/2) + 10, 248, bWidth, bHeight));
				Handler.addButton(new ImageDoubleTextButton(fnt4, Color.WHITE, "Score 7000 in hard mode", "Score 7000 points on", "hard mode in campaign", Game.brightRedButton, (Game.sWidth/2) - 310, 317, bWidth, bHeight));
				Handler.addButton(new ImageDoubleTextButton(fnt4, Color.WHITE, "Score 7000 on crazy coins", "Score 7000 points on", "crazy coins in campaign", Game.brightRedButton, (Game.sWidth/2) + 10, 317, bWidth, bHeight));
				Handler.addButton(new ImageDoubleTextButton(fnt4, Color.WHITE, "Score 7000 in dark mode", "Score 7000 points on", "dark mode in campaign", Game.brightRedButton, (Game.sWidth/2) - 310, 386, bWidth, bHeight));

				for(int i = 1; i <= Game.unlockedSkins.length; i++) {
					int skinButtonX;
					int skinButtonY;

					if(i % 2 == 0) { skinButtonX = (Game.sWidth/2) - 71; }
					else { skinButtonX = (Game.sWidth/2) + bWidth + 50; }

					if(i / 2 <= 1) { skinButtonY = 195; }
					else if(i / 2 <= 2) { skinButtonY = 264; }
					else if(i / 2 <= 3) { skinButtonY = 333; }
					else { skinButtonY = 402; }

					if(Game.unlockedSkins[i - 1]) {
						Handler.addImageButton(new ImageButton("unlockedSkin" + i, player_skins.grabImageFast(i, 1), skinButtonX, skinButtonY, 32, 32));
					} else {
						Handler.addImageButton(new ImageButton("unlockedSkin" + i, player_skins.grabImageFast(i, 15), skinButtonX, skinButtonY, 32, 32));
					}
				}

				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Menu", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 475, bWidth, bHeight));
			}
		}

		if(Game.gameState == STATE.Settings || Game.gameState == STATE.LevelSelect) {
			if(!buttonsFound) {
				Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Menu", Game.tombButton, (Game.sWidth/2) - (bWidth/2), 450, bWidth, bHeight));
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
						Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Resume", currentBtnType, (Game.sWidth/2) - 100, 275, bWidth, bHeight));
					}
					if(Game.levelEnd) {
						Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Level Compelete!", Game.brightBlueButton_long, (Game.sWidth/2) - (150), 275, 300, bHeight));
						if(Game.hud.getLevel() < 22) { Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Next Level", currentBtnType, (Game.sWidth/2) - 100, 350, bWidth, bHeight)); }
					}
					if(Game.paused || Game.gameOver || Game.escapeGame || (Game.levelEnd && Game.hud.getLevel() == 22)) {
						Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Quit", currentBtnType, (Game.sWidth/2) - 100, 350, bWidth, bHeight));
					}
					if(Game.escapeGame && !(Game.coinsLeft == 0 && Game.hud.getLevel() == 22)) {
						Handler.addButton(new ImageTextButton(fnt2, Color.WHITE, "Gameover!", Game.brightRedButton, (Game.sWidth/2) - 100, 275, bWidth, bHeight));
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
		}
		
		if(Game.gameState == STATE.Settings) {
			g.setFont(fnt);
			g.setColor(Color.WHITE);
			g.drawString("Settings", (Game.sWidth/2) - 95, 200);
		}

		if(Game.gameState == STATE.Controls) {
			g.setFont(fnt);
			g.setColor(Color.WHITE);
			g.drawString("Controls", (Game.sWidth/2) - 95, 200);
		}

		if(Game.gameState == STATE.Game) {
			if(Game.paused) {
				g.setFont(fnt);
				g.setColor(Color.WHITE);
				g.drawString("Paused", (Game.sWidth/2) - 95, 200);
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
