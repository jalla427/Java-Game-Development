package tmp;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import Item.BlitzOrb;
import Item.HealOrb;
import enemy.*;
import Item.Coin;
import level.LevelCollection;
import level.TileMapBuilder;

public class Game extends Canvas implements Runnable {

	private Thread thread;
	private boolean running = false;
	protected static boolean clearButtons = false;
	protected static int levelSelected = 0;
	public static boolean paused = false;
	public static boolean escapeGame = false;
	public static boolean gameOver = false;
	public static boolean quit = false;
	
	//Main frame dimensions
	public static int sWidth = 900;
	public static int sHeight = 670;

	//Global volume
	public static int gameVolume = 50;
	protected static int highScore = 0;
	protected static int blitzHighScore = 0;
	private static int[] saveData = new int[38];
	
	//Variables primarily for level transition
	protected static boolean playerControl = true;
	protected static boolean levelEnd = false;
	protected static boolean transitioning = false;
	private static int transitionTimer = 0;
	private static String transitionMessage = "";
	public static int coinsLeft = 1;
	public Random random = new Random();
	
	private static Handler handler;
	protected final Menu menu;
	public static HUD hud;
	protected static TileMapBuilder tombTileMapBuilder;

	//Sprites
	public static BufferedImage levelBackgroundImg; //Set dynamically
	public static BufferedImage backgroundImg;
	public static BufferedImage tombBackgroundImg;
	public static BufferedImage dungeonBackgroundImg;
	public static BufferedImage infernoBackgroundImg;
	public static BufferedImage finalBackgroundImg;
	public static BufferedImage blitzBackgroundImg_1;
	public static BufferedImage blitzBackgroundImg_2;
	public static BufferedImage blitzBackgroundImg_3;
	public static BufferedImage blitzBackgroundImg_4;
	public static BufferedImage blitzBackgroundImg_5;
	public static SpriteSheet sprite_sheet_menu_buttons;
	public static SpriteSheet tomb_blocks_20x20;
	public static SpriteSheet dungeon_blocks_20x20;
	public static SpriteSheet burning_blocks_20x20;
	public static SpriteSheet final_blocks_20x20;
	public static SpriteSheet blitz_blocks_1_20x20;
	public static SpriteSheet blitz_blocks_2_20x20;
	public static SpriteSheet blitz_blocks_3_20x20;
	public static SpriteSheet blitz_blocks_4_20x20;
	public static SpriteSheet blitz_blocks_5_20x20;
	public static SpriteSheet sprite_sheet;
	public static SpriteSheet sprite_sheet_hawk;
	public static SpriteSheet sprite_sheet_sentry;
	public static SpriteSheet sprite_sheet_bullet;
	public static SpriteSheet sprite_sheet_strider;
	public static SpriteSheet sprite_sheet_thumper;
	public static SpriteSheet sprite_sheet_wanderer;
	public static SpriteSheet sprite_sheet_wisp;
	public static SpriteSheet sprite_sheet_golem;
	public static SpriteSheet sprite_sheet_core;
	public static SpriteSheet sprite_sheet_keeper;
	public static SpriteSheet sprite_sheet_annihilator;
	public static SpriteSheet sprite_sheet_coin;
	public static BufferedImage meter_overlay;
	public static BufferedImage tombGameBanner;
	public static BufferedImage tombButton;
	public static BufferedImage tombButton_small;
	public static BufferedImage tombButton_long;
	public static BufferedImage dungeonButton;
	public static BufferedImage dungeonButton_small;
	public static BufferedImage burningButton;
	public static BufferedImage burningButton_small;
	public static BufferedImage brightRedButton;
	public static BufferedImage brightBlueButton_long;

	public static double altEnemySkinOdds = 0.005;
	public static int playerSkin = 1;
	public static boolean[] unlockedSkins = new boolean[] {true, false, false, false, false, false, false, false};
	public static boolean[] unlockedLevels = new boolean[] {true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};

	//Score modifiers
	public static boolean hardMode = false;
	public static boolean darkMode = false;
	public static boolean crazyCoins = false;
	
	//Used for determining the current scene
	public enum STATE {
		Menu,
		Settings,
		Statistics,
		LevelSelect,
		Controls,
		Game
	}

    public static STATE gameState = STATE.Menu;
	public static boolean debugMode = false;
	public static boolean logFPSMode = false;
	
	//Constructor
	public Game() {
		//Load assets
		BufferedImageLoader loader = new BufferedImageLoader();
		backgroundImg = loader.loadImage("/tombMainMenu.png");
		tombBackgroundImg = loader.loadImage("/tombBackground.png");
		dungeonBackgroundImg = loader.loadImage("/dungeonBackground.png");
		infernoBackgroundImg = loader.loadImage("/infernoBackground.png");
		finalBackgroundImg = loader.loadImage("/finalBackground.png");
		blitzBackgroundImg_1 = loader.loadImage("/blitzBackground_1.png");
		blitzBackgroundImg_2 = loader.loadImage("/blitzBackground_2.png");
		blitzBackgroundImg_3 = loader.loadImage("/blitzBackground_3.png");
		blitzBackgroundImg_4 = loader.loadImage("/blitzBackground_4.png");
		blitzBackgroundImg_5 = loader.loadImage("/blitzBackground_5.png");
		sprite_sheet_menu_buttons = new SpriteSheet(loader.loadImage("/sprite_sheet_menu_buttons.png"), 4, 4, 32, 32);
		tomb_blocks_20x20 = new SpriteSheet(loader.loadImage("/tomb_blocks_20x20.png"), 3, 3, 20, 20);
		dungeon_blocks_20x20 = new SpriteSheet(loader.loadImage("/dungeon_blocks_20x20.png"), 3, 3, 20, 20);
		burning_blocks_20x20 = new SpriteSheet(loader.loadImage("/burning_blocks_20x20.png"), 3, 3, 20, 20);
		final_blocks_20x20 = new SpriteSheet(loader.loadImage("/final_blocks_20x20.png"), 3, 3, 20, 20);
		blitz_blocks_1_20x20 = new SpriteSheet(loader.loadImage("/blitz_blocks_1_20x20.png"), 3, 3, 20, 20);
		blitz_blocks_2_20x20 = new SpriteSheet(loader.loadImage("/blitz_blocks_2_20x20.png"), 3, 3, 20, 20);
		blitz_blocks_3_20x20 = new SpriteSheet(loader.loadImage("/blitz_blocks_3_20x20.png"), 3, 3, 20, 20);
		blitz_blocks_4_20x20 = new SpriteSheet(loader.loadImage("/blitz_blocks_4_20x20.png"), 3, 3, 20, 20);
		blitz_blocks_5_20x20 = new SpriteSheet(loader.loadImage("/blitz_blocks_5_20x20.png"), 3, 3, 20, 20);
		sprite_sheet = new SpriteSheet(loader.loadImage("/sprite_sheet.png"), 8, 15, 32, 32);
		sprite_sheet_hawk = new SpriteSheet(loader.loadImage("/sprite_sheet_hawk.png"), 2, 7, 32, 32);
		sprite_sheet_sentry = new SpriteSheet(loader.loadImage("/sprite_sheet_sentry.png"), 4, 4, 20, 20);
		sprite_sheet_bullet = new SpriteSheet(loader.loadImage("/sprite_sheet_bullet.png"), 3, 10, 16, 16);
		sprite_sheet_strider = new SpriteSheet(loader.loadImage("/sprite_sheet_strider.png"), 8, 8, 32, 32);
		sprite_sheet_thumper = new SpriteSheet(loader.loadImage("/sprite_sheet_thumper.png"), 4, 8, 32, 32);
		sprite_sheet_wanderer = new SpriteSheet(loader.loadImage("/sprite_sheet_wanderer.png"), 8, 8, 32, 32);
		sprite_sheet_wisp = new SpriteSheet(loader.loadImage("/sprite_sheet_wisp.png"), 8, 10, 26, 26);
		sprite_sheet_golem = new SpriteSheet(loader.loadImage("/sprite_sheet_golem.png"), 8, 10, 40, 40);
		sprite_sheet_core = new SpriteSheet(loader.loadImage("/sprite_sheet_core.png"), 6, 10, 20, 20);
		sprite_sheet_keeper = new SpriteSheet(loader.loadImage("/sprite_sheet_keeper.png"), 2, 22, 32, 32);
		sprite_sheet_annihilator = new SpriteSheet(loader.loadImage("/sprite_sheet_annihilator.png"), 4, 6, 64, 48);
		sprite_sheet_coin = new SpriteSheet(loader.loadImage("/sprite_sheet_coin.png"), 3, 17, 10, 10);
		meter_overlay = loader.loadImage("/meter_overlay.png");
		tombGameBanner = loader.loadImage("/tombGameBanner.png");
		tombButton = loader.loadImage("/tombButton.png");
		tombButton_small = loader.loadImage("/tombButton_small.png");
		tombButton_long = loader.loadImage("/tombButton_long.png");
		dungeonButton = loader.loadImage("/dungeonButton.png");
		dungeonButton_small = loader.loadImage("/dungeonButton_small.png");
		burningButton = loader.loadImage("/burningButton.png");
		burningButton_small = loader.loadImage("/burningButton_small.png");
		brightRedButton = loader.loadImage("/brightRedButton.png");
		brightBlueButton_long = loader.loadImage("/brightBlueButton_long.png");

		//Create core objects
		handler = new Handler();
		tombTileMapBuilder = new TileMapBuilder();
		menu = new Menu();
		hud = new HUD();
		this.addKeyListener(new KeyInput());
		this.addMouseListener(menu);
		loadInSaveData();

		//Create game window
		new Main("Tomb Game", sWidth, sHeight, this);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Main game loop method
	public void run() {
		this.requestFocus();
		long sleepTime;
		long timeTaken;
		double fps = 60;
		double nsPerTick = 1000000000.0 / fps;
		long timer = System.currentTimeMillis();
		int frames = 0;

		while(running){
			long now = System.nanoTime();

			tick();
			render();
			frames++;

			//Sleep to maintain the target FPS
			timeTaken = System.nanoTime() - now;
			sleepTime = (long)(nsPerTick - timeTaken) / 1000000;  //Convert to milliseconds
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			//Output current FPS if debug mode is active
			if((System.currentTimeMillis() - timer) >= 1000) {
				timer = System.currentTimeMillis();
				if(logFPSMode) {
					System.out.println("FPS: " + frames);
				}
				frames = 0;
			}
		}
		stop();
	}
	
	//Update method
	private void tick() {
		//Buttons are set to be cleared
		if(clearButtons) {
			Handler.clearButtons();
			clearButtons = false;
		}

		//Based on gamestate, determine what needs to tick
		if(gameState == STATE.Game) {
			handler.tick();
			hud.tick();
			if(escapeGame || Game.paused || Game.levelEnd) {
				menu.tick();
			}
		}
		else if(gameState == STATE.Menu || gameState == STATE.Settings || gameState == STATE.Statistics || gameState == STATE.LevelSelect || gameState == STATE.Controls) {
			handler.tick();
			menu.tick();
		}

		//Quit was selected
		if(quit) {
			while(handler.areLevel()) {
				handler.clearLevel();
			}
			quit = false;
		}

		//Game starts regularly, transition to level 1
		if((gameState == STATE.Menu && hud.getLevel() == 1) || (gameState == STATE.LevelSelect && levelSelected == 1)) {
			levelBackgroundImg = tombBackgroundImg;
			gameState = STATE.Game;
			clearButtons = true;
			startLevelTransition(tomb_blocks_20x20, 1, 3, sWidth/2-16, sHeight/2-32);
		}

		//Game starts in blitz game mode
		if((gameState == STATE.Menu && hud.getLevel() == 99)) {
			levelBackgroundImg = tombBackgroundImg;
			gameState = STATE.Game;
			clearButtons = true;
			blitzLevelRandomizer();
		}

		//Game start by level selection
		if(gameState == STATE.LevelSelect && levelSelected > 0) {
			beginGame(levelSelected);
			if((levelSelected >= 1 && levelSelected <= 6) || levelSelected == 19) {
				levelBackgroundImg = tombBackgroundImg;
			}
			if((levelSelected >= 7 && levelSelected <= 12) || levelSelected == 20) {
				levelBackgroundImg = dungeonBackgroundImg;
			}
			if((levelSelected >= 13 && levelSelected <= 18) || levelSelected == 21) {
				levelBackgroundImg = infernoBackgroundImg;
			}
			if(levelSelected == 22) {
				levelBackgroundImg = finalBackgroundImg;
			}
		}

		//In game
		if(gameState == STATE.Game) {
			//Game Over
			if(HUD.HEALTH <= 0 && !escapeGame) {
				escapeGame = true;
			}
			if(escapeGame) {
				if (!gameOver) {
					beginGameOver();
					gameOver = true;
				}
			}

			//Score-based skin unlocks
			if(HUD.score >= 6000) {
				if(!unlockedSkins[4]) { unlockedSkins[4] = true; }
				if(!unlockedSkins[5] && hardMode && HUD.score >= 7000) { unlockedSkins[5] = true; }
				if(!unlockedSkins[6] && crazyCoins && HUD.score >= 7000) { unlockedSkins[6] = true; }
				if(!unlockedSkins[7] && darkMode && HUD.score >= 7000) { unlockedSkins[7] = true; }
			}

			//Detect level completion
			if(coinsLeft == 0 && !gameOver && !transitioning && !levelEnd && levelSelected == 0) {
				levelEnd = true;
				while(handler.areEnemies()) {
					handler.clearEnemies();
				}
				playerControl = false;
			}
			//Level 2 Transition
			if((coinsLeft == 0 && hud.getLevel() == 1) || levelSelected == 2) {
				startLevelTransition(tomb_blocks_20x20, 2, 4, sWidth / 2 - 16, sHeight / 2 + 232);
			}
			//Level 3 Transition
			if((coinsLeft == 0 && hud.getLevel() == 2) || levelSelected == 3) {
				startLevelTransition(tomb_blocks_20x20, 3, 6, sWidth / 2 - 16, sHeight / 2 + 232);
			}
			//Level 4 Transition
			if((coinsLeft == 0 && hud.getLevel() == 3) || levelSelected == 4) {
				startLevelTransition(tomb_blocks_20x20, 4, 8, sWidth / 2 - 16, sHeight - 60);
			}
			//Level 5 Transition
			if((coinsLeft == 0 && hud.getLevel() == 4) || levelSelected == 5) {
				startLevelTransition(tomb_blocks_20x20, 5, 9, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 6 Transition
			if((coinsLeft == 0 && hud.getLevel() == 5) || levelSelected == 6) {
				startLevelTransition(tomb_blocks_20x20, 6, 9, sWidth / 2 - 16, sHeight - 100);
			}
			//Level 7 Transition, start of section 2
			if((coinsLeft == 0 && hud.getLevel() == 6) || levelSelected == 7) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = dungeonBackgroundImg;
					unlockedSkins[1] = true;
				}
				startLevelTransition(dungeon_blocks_20x20, 7, 6, sWidth / 2 - 16, sHeight - 300);
			}
			//Level 8 Transition
			if((coinsLeft == 0 && hud.getLevel() == 7) || levelSelected == 8) {
				startLevelTransition(dungeon_blocks_20x20, 8, 8, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 9 Transition
			if((coinsLeft == 0 && hud.getLevel() == 8) || levelSelected == 9) {
				startLevelTransition(dungeon_blocks_20x20, 9, 9, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 10 Transition
			if((coinsLeft == 0 && hud.getLevel() == 9) || levelSelected == 10) {
				startLevelTransition(dungeon_blocks_20x20, 10, 8, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 11 Transition
			if((coinsLeft == 0 && hud.getLevel() == 10) || levelSelected == 11) {
				startLevelTransition(dungeon_blocks_20x20, 11, 8, sWidth / 2 - 16, sHeight - 180);
			}
			//Level 12 Transition
			if((coinsLeft == 0 && hud.getLevel() == 11) || levelSelected == 12) {
				startLevelTransition(dungeon_blocks_20x20, 12, 9, sWidth / 2 - 16, sHeight - 180);
			}
			//Level 13 Transition, start of section 3
			if((coinsLeft == 0 && hud.getLevel() == 12) || levelSelected == 13) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = infernoBackgroundImg;
					unlockedSkins[2] = true;
				}
				startLevelTransition(burning_blocks_20x20, 13, 8, sWidth / 2 - 16, sHeight/2);
			}
			//Level 14 Transition
			if((coinsLeft == 0 && hud.getLevel() == 13) || levelSelected == 14) {
				startLevelTransition(burning_blocks_20x20, 14, 9, sWidth / 2 - 16, sHeight - 350);
			}
			//Level 15 Transition
			if((coinsLeft == 0 && hud.getLevel() == 14) || levelSelected == 15) {
				startLevelTransition(burning_blocks_20x20, 15, 9, sWidth / 2 - 16, sHeight - 240);
			}
			//Level 16 Transition
			if((coinsLeft == 0 && hud.getLevel() == 15) || levelSelected == 16) {
				startLevelTransition(burning_blocks_20x20, 16, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 17 Transition
			if((coinsLeft == 0 && hud.getLevel() == 16) || levelSelected == 17) {
				startLevelTransition(burning_blocks_20x20, 17, 9, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 18 Transition
			if((coinsLeft == 0 && hud.getLevel() == 17) || levelSelected == 18) {
				startLevelTransition(burning_blocks_20x20, 18, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 19 Transition
			if((coinsLeft == 0 && hud.getLevel() == 18) || levelSelected == 19) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = tombBackgroundImg;
					unlockedSkins[3] = true;
				}
				startLevelTransition(tomb_blocks_20x20, 19, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 20 Transition
			if((coinsLeft == 0 && hud.getLevel() == 19) || levelSelected == 20) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = dungeonBackgroundImg;
				}
				startLevelTransition(dungeon_blocks_20x20, 20, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 21 Transition
			if((coinsLeft == 0 && hud.getLevel() == 20) || levelSelected == 21) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = infernoBackgroundImg;
				}
				startLevelTransition(burning_blocks_20x20, 21, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 22 Transition
			if((coinsLeft == 0 && hud.getLevel() == 21) || levelSelected == 22) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = finalBackgroundImg;
				}
				startLevelTransition(final_blocks_20x20, 22, 12, sWidth / 2 - 16, sHeight - 250);
			}
			//Game Won
			if(coinsLeft == 0 && hud.getLevel() == 22 && !levelEnd) {
					levelEnd = true;
					beginGameOver();
			}
			levelSelected = 0; //Level select should have occurred, clear value

			//Level Transition Timer
			if(transitioning && !escapeGame) {
				transitionTimer++;
				if(hud.getLevel() == 99) {
					transitionMessage = "Blitz Infinite Survival"; //Blitz
				}
				else {
					transitionMessage = "Level " + hud.getLevel(); //Regular levels
				}

				//Brief pause between levels before calling enemies
				if(hud.getLevel() == 1) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new HawkEnemy(100, 100, 32, 32, ID.Enemy, handler, 300));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 2) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new HawkEnemy(sWidth / 4, 100, 32, 32, ID.Enemy, handler, 50));
						handler.addEnemy(new HawkEnemy(3 * (sWidth / 4), 100, 32, 32, ID.Enemy, handler, 120));
						handler.addEnemy(new SentryEnemy(0, 200, 20, 20, ID.Enemy, handler, 150, 0));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 3) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new HawkEnemy(sWidth / 4, 100, 32, 32, ID.Enemy, handler, 200));
						handler.addEnemy(new SentryEnemy(120, 100, 20, 20, ID.Enemy, handler, 250, 30));
						handler.addEnemy(new SentryEnemy(440, 100, 20, 20, ID.Enemy, handler, 250, 60));
						handler.addEnemy(new SentryEnemy(760, 100, 20, 20, ID.Enemy, handler, 250, 0));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 4) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new HawkEnemy(200, 100,32, 32, ID.Enemy, handler, 100));
						handler.addEnemy(new SentryEnemy(40, 100, 20, 20, ID.Enemy, handler, 250, 50));
						handler.addEnemy(new SentryEnemy(sWidth - 60, 100, 20, 20, ID.Enemy, handler, 250, 150));
						handler.addEnemy(new SentryEnemy(40, sHeight - 50, 20, 20, ID.Enemy, handler, 250, 50));
						handler.addEnemy(new SentryEnemy(sWidth - 60, sHeight - 50, 20, 20, ID.Enemy, handler, 250, 150));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 5) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 200));
						handler.addEnemy(new HawkEnemy(200, 300, 32, 32, ID.Enemy, handler, 100));
						handler.addEnemy(new SentryEnemy(40, sHeight - 50, 20, 20, ID.Enemy, handler, 200, 50));
						handler.addEnemy(new SentryEnemy(sWidth - 60, sHeight - 50, 20, 20, ID.Enemy, handler, 200, 150));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 6) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 250));
						handler.addEnemy(new SentryEnemy(420, 120, 20, 20, ID.Enemy, handler, 200, 100));
						handler.addEnemy(new SentryEnemy(420, 160, 20, 20, ID.Enemy, handler, 200, 110));
						handler.addEnemy(new SentryEnemy(460, 120, 20, 20, ID.Enemy, handler, 200, 120));
						handler.addEnemy(new SentryEnemy(460, 160, 20, 20, ID.Enemy, handler, 200, 130));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 7) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new StriderEnemy(100, 150, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new StriderEnemy(sWidth - 100, 100, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 8) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new StriderEnemy(100, 400, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new StriderEnemy(sWidth - 132, 400, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy(100, 150, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 9) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new StriderEnemy(sWidth - 100, 400, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy(100, 150, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy(Game.sWidth - 100, 150, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 10) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new StriderEnemy(50, sHeight - 75, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy(160, 180, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy(540, 180, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 11) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new StriderEnemy(100, sHeight - 60, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new StriderEnemy(200, sHeight - 60, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy(180, 160, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 12) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new StriderEnemy(Game.sWidth - 62, 220, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy((Game.sWidth/2) - 80, 300, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new ThumperEnemy((Game.sWidth/2) + 48, 300, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 13) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						handler.addEnemy(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 14) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 100));
						handler.addEnemy(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 15) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 50));
						handler.addEnemy(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 200));
						handler.addEnemy(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 16) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 75));
						handler.addEnemy(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						handler.addEnemy(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 17) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 50));
						handler.addEnemy(new GolemEnemy(80, Game.sHeight - 180, 40, 40, ID.Enemy, handler));
						handler.addEnemy(new GolemEnemy(Game.sWidth/2 + 20, 200, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 18) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 150));
						handler.addEnemy(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						handler.addEnemy(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 19) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 50));
						handler.addEnemy(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 175));
						handler.addEnemy(new SentryEnemy(40, 100, 20, 20, ID.Enemy, handler, 300, 200));
						handler.addEnemy(new SentryEnemy(40, 220, 20, 20, ID.Enemy, handler, 200, 110));
						handler.addEnemy(new SentryEnemy(sWidth - 60, 100, 20, 20, ID.Enemy, handler, 300, 120));
						handler.addEnemy(new SentryEnemy(sWidth - 60, 220, 20, 20, ID.Enemy, handler, 200, 230));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 20) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new GolemEnemy(Game.sWidth/2 + 20, 200, 40, 40, ID.Enemy, handler));
						handler.addEnemy(new StriderEnemy(30, 220, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new HawkEnemy(200, 180, 32, 32, ID.Enemy, handler, 100));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 21) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 200));
						handler.addEnemy(new ThumperEnemy((Game.sWidth/2) - 80, 200, 32, 32, ID.Enemy, handler));
						handler.addEnemy(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 100));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 22) {
					if (transitionTimer >= 200) {
						handler.addEnemy(new CoreEnemy(100, 100, 20, 20, ID.Enemy, handler));
						handler.addEnemy(new CoreEnemy(sWidth - 100, 100, 20, 20, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if(hud.getLevel() == 99) {
					if (transitionTimer >= 200) {
						endLevelTransition();
					}
				}
			}

			//Handle coin collection during level
			if(!escapeGame && !transitioning) { coinSpawner(); }
		}
	}
	
	//Draw method
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		//Background
		g.setColor(Color.black);
		g.fillRect(0, 0, sWidth, sHeight);

		if(gameState == STATE.Menu || gameState == STATE.Settings || gameState == STATE.Statistics || gameState == STATE.LevelSelect || gameState == STATE.Controls) {
			g.drawImage(backgroundImg, 0, 0, null);
		}
		if(gameState == STATE.Game) {
			g.drawImage(levelBackgroundImg, 0, 0, null);
		}

		handler.render(g);

		if(gameState == STATE.Game) {
			Lighting.render((Graphics2D) g, handler);
			hud.render(g);
			if(transitioning && !escapeGame) {
				//Next level banner
				g.setColor(Color.white);
				g.setFont(new Font("Helvetica", Font.PLAIN, 36));
				int width = g.getFontMetrics().stringWidth(transitionMessage);
				g.drawString(transitionMessage, (sWidth / 2) - (width / 2), sHeight / 2);

				//Loading bar
				g.setColor(Color.gray);
				g.fillRect(sWidth / 2 - 50, (sHeight / 2) + 35, 100, 10);
				g.setColor(Color.blue);
				g.fillRect(sWidth / 2 - 50, (sHeight / 2) + 35, transitionTimer/2, 10);
				g.setColor(Color.white);
				g.drawRect(sWidth / 2 - 50, (sHeight / 2) + 35, 100, 10);
			}
		}

		handler.renderHigherElements(g);
		menu.render(g);
		g.dispose();
		bs.show();
	}

	protected static void beginGame(int level) {
		gameState = STATE.Game;
		clearButtons = true;
		hud.setLevel(level);
	}
	
	//Start transitioning level
	private void startLevelTransition(SpriteSheet tileMap, int nextLevel, int coins, int playerX, int playerY) {
		if(!gameOver) {
			if(!levelEnd) {
				transitioning = true;
				for(int i = 0;  i < KeyInput.keyDown.length; i++) {
					KeyInput.keyDown[i] = false;
				}
				while(handler.areLevel()) {
					handler.clearLevel();
				}
				tombTileMapBuilder.createLevel(tileMap, LevelCollection.getLevel(nextLevel), handler);
				handler.findTotalLevelArea();
				handler.playerObject = new Player(playerX, playerY, 32, 32, playerSkin, ID.Player, handler);
				hud.setLevel(nextLevel);
				if(nextLevel <= 22) { unlockedLevels[nextLevel - 1] = true; }
				setLevelCoinGoal(coins);
			}
		}
	}
	
	//Finish transitioning level
	private static void endLevelTransition() {
		transitionTimer = 0;
		transitionMessage = "";
		transitioning = false;
		playerControl = true;
	}

	//Randomize blitz level creation
	private void blitzLevelRandomizer() {
		SpriteSheet randomBlockChoice;
		double randomChoice = random.nextDouble();
		if(randomChoice <= 0.13) { randomBlockChoice =  tomb_blocks_20x20; levelBackgroundImg = tombBackgroundImg; }
		else if(randomChoice <= 0.26) { randomBlockChoice =  dungeon_blocks_20x20; levelBackgroundImg = dungeonBackgroundImg; }
		else if(randomChoice <= 0.32) { randomBlockChoice =  burning_blocks_20x20; levelBackgroundImg = infernoBackgroundImg; }
		else if(randomChoice <= 0.39) { randomBlockChoice =  blitz_blocks_1_20x20; levelBackgroundImg = blitzBackgroundImg_1; }
		else if(randomChoice <= 0.52) { randomBlockChoice =  blitz_blocks_2_20x20; levelBackgroundImg = blitzBackgroundImg_2; }
		else if(randomChoice <= 0.65) { randomBlockChoice =  blitz_blocks_3_20x20; levelBackgroundImg = blitzBackgroundImg_3; }
		else if(randomChoice <= 0.75) { randomBlockChoice =  blitz_blocks_4_20x20; levelBackgroundImg = blitzBackgroundImg_4; }
		else if(randomChoice <= 0.85) { randomBlockChoice =  blitz_blocks_5_20x20; levelBackgroundImg = blitzBackgroundImg_5; }
		else { randomBlockChoice =  final_blocks_20x20; levelBackgroundImg = finalBackgroundImg; }

		startLevelTransition(randomBlockChoice, 99, 999999, sWidth/2-16, sHeight/2-32);
	}

	//Randomize blitz enemy spawning
	private void blitzRandomEnemySpawner() {
		double randomChoice = random.nextDouble();
		if(randomChoice <= 0.24) {
			Handler.addEnemy(new HawkEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 32, 32, ID.Enemy, handler, 200));
		}
		else if(randomChoice <= 0.46) {
			Handler.addEnemy(new StriderEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 32, 32, ID.Enemy, handler));
		}
		else if(randomChoice <= 0.52 && crazyCoins) {
			Handler.addEnemy(new KeeperEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 32, 32, ID.Enemy));
		}
		else if(randomChoice <= 0.65) {
			Handler.addEnemy(new ThumperEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 32, 32, ID.Enemy, handler));
		}
		else if(randomChoice <= 0.76) {
			Handler.addEnemy(new WandererEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 32, 32, ID.Enemy));
		}
		else if(randomChoice <= 0.87) {
			Handler.addEnemy(new GolemEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 40, 40, ID.Enemy, handler));
		}
		else if(randomChoice <= 0.91 && hardMode) {
			Handler.addEnemy(new AnnihilatorEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 100, 64, 48, ID.Enemy));
		}
		else if(randomChoice <= 0.98) {
			Handler.addEnemy(new WispEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 26, 26, ID.Enemy, handler, 100));
		}
		else {
			Handler.addEnemy(new CoreEnemy(clamp((int) (Math.random() * sWidth), 100, sWidth - 100), 50, 20, 20, ID.Enemy, handler));
		}
	}
	
	//Transition to gameover
	public static void beginGameOver() {
		playerControl = false;
		for(int i = 0;  i < KeyInput.keyDown.length; i++) {
			KeyInput.keyDown[i] = false;
		}
		transitioning = false;
		transitionTimer = 0;
		setLevelCoinGoal(1);
		levelSelected = 0;
		while(handler.areEnemies() || handler.areCoins()) {
			handler.clearEnemies();
			handler.clearItems();
		}
	}

	//Set level coin goal during level transition
	private static void setLevelCoinGoal(int goal) {
		coinsLeft = goal;
		hud.coinStart = coinsLeft;
	}

	//Coin spawning during active gameplay
	private void coinSpawner() {
		if (!handler.areCoins() && coinsLeft > 0 && !transitioning && !gameOver) {
			boolean obstructed;
			float attemptX;
			float attemptY;

			do {
				obstructed = false;

				attemptX = (float) (sWidth * Math.random());
				attemptY = (float) (sHeight * Math.random());
				int[] xCollision = new int[]{(int) attemptX, ((int) attemptX) + 10, ((int) attemptX) + 10, (int) attemptX};
				int[] yCollision = new int[]{(int) attemptY, (int) attemptY, ((int) attemptY) + 10, ((int) attemptY) + 10};
				Polygon collision = new Polygon();
				collision.xpoints = xCollision;
				collision.ypoints = yCollision;
				collision.npoints = xCollision.length;

				for (int i = 0; i < handler.object.size(); i++) {
					GameObject tempObject = handler.object.get(i);
					Area a1;
					Area a2;

					//Check to make sure coin attempt is not near player
					if(tempObject.getID() == ID.Player) {
						if(calculateDistance(attemptX, attemptY, tempObject.getX(), tempObject.getY()) < 300) {
							obstructed = true;
						}
					}

					//Check for player/tile collision
					if (tempObject.getID() == ID.Player || tempObject.getID() == ID.Level) {
						//Find area shared by player/tile
						a1 = new Area(collision);
						a2 = new Area(tempObject.getBounds());
						a1.intersect(a2);

						//Determine if area is shared by player/tile
						if (!a1.isEmpty()) {
							obstructed = true;
						}
					}
				}
			} while (obstructed);

			handler.addObject(new Coin(attemptX, attemptY, 10, 10, (float) (5 * (Math.random() + 0.4)), (float) (5 * (Math.random() + 0.4)), ID.Coin, handler));

			//If in blitz game mode, spawn an enemy every 8 coins, heal orb every 15 coins, and blitz orb every 50 coins
			if(hud.getLevel() == 99) {
				if((coinsLeft) % 50 != 1) handler.addObject(new Coin(attemptX, attemptY, 10, 10, (float) (5 * (Math.random() + 0.4)), (float) (5 * (Math.random() + 0.4)), ID.Coin, handler));
				if(((coinsLeft) % 5 == 0 && Handler.enemyList.size() <= 6) || !Handler.areEnemies()) blitzRandomEnemySpawner();
				if((coinsLeft) % 15 == 0) handler.addObject(new HealOrb(attemptX, attemptY, 10, 10, (float) (5 * (Math.random() + 0.4)), (float) (6 * (Math.random() + 0.4)), ID.Orb, handler));
				if((coinsLeft) % 50 == 0) handler.addObject(new BlitzOrb(attemptX, attemptY, 10, 10, (float) (5 * (Math.random() + 0.4)), (float) (6 * (Math.random() + 0.4)), ID.Orb, handler));
			}
		}
	}
	
	//Restricts an int value between a given minimum and maximum value
	public static int clamp(int input, int min, int max) {
		int output = input;
		
		if(input < min) output = min;
		if(input > max) output = max;
		
		return output;
	}

	//Restricts a float value between a given minimum and maximum value
	public static float clamp(float input, int min, int max) {
		float output = input;
		
		if(input < min) output = min;
		if(input > max) output = max;
		
		return output;
	}

	//Finds the distance between two points
	public static float calculateDistance(float x1, float y1, float x2, float y2) {
		float deltaX = x2 - x1;
		float deltaY = y2 - y1;

        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

	public static boolean isPointInBounds(int mx, int my, int x, int y, int width, int height) {
		return mx > x && mx < x + width && my > y && my < y + height;
	}

	protected static void loadInSaveData() {
		String filePath = "./save/savedata.txt"; //Save location
		String fileDir = "./save"; //Save directory

		ensureSaveFilesExist(fileDir, filePath);
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line = reader.readLine(); //Ensure file is not empty
			if (line != null) {
				String[] parts = line.split(",\\s*"); //Split by comma and optional spaces
				for (int i = 0; i < parts.length && i < saveData.length; i++) {
					saveData[i] = Integer.parseInt(parts[i]);
				}
			} else {
				saveData = new int[] {50, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
				writeOutSaveData();
			}
		} catch (IOException e) {
			System.out.println("Encountered an error while attempting to load save data.");
		}

		//Set game values based on retrieved save data
		for (int i = 0; i < saveData.length; i++) {
			if (i == 0) {
				gameVolume = saveData[i];
			} else if (i == 1) {
				highScore = saveData[i];
			} else if (i == 2) {
				blitzHighScore = saveData[i];
			} else if (i <= 10) {
				unlockedSkins[i - 3] = saveData[i] == 1;
			} else if (i <= 32) {
				unlockedLevels[i - 11] = saveData[i] == 1;
			} else if (i == 33) {
				playerSkin = saveData[i];
			} else if (i <= 34) {
				KeyInput.keyBinds[0] = saveData[i];
			} else if (i <= 35) {
				KeyInput.keyBinds[1] = saveData[i];
			} else if (i <= 36) {
				KeyInput.keyBinds[2] = saveData[i];
			} else if (i <= 37) {
				KeyInput.keyBinds[3] = saveData[i];
			}
		}
	}

	protected static void writeOutSaveData() {
		String filePath = "./save/savedata.txt"; //Save location
		String fileDir = "./save"; //Save directory

		//Set save data values based on current game values
		for(int i = 0; i < saveData.length; i++) {
			if(i == 0) {
				 saveData[i] = gameVolume;
			} else if(i == 1) {
				 saveData[i] = highScore;
			} else if(i == 2) {
				saveData[i] = blitzHighScore;
			} else if(i <= 10) {
				saveData[i] = unlockedSkins[i - 3] ? 1 : 0;
			} else if(i <= 32) {
				saveData[i] = unlockedLevels[i - 11] ? 1 : 0;
			} else if(i <= 33) {
				saveData[i] = playerSkin;
			} else if(i <= 34) {
				saveData[i] = KeyInput.keyBinds[0];
			} else if(i <= 35) {
				saveData[i] = KeyInput.keyBinds[1];
			} else if(i <= 36) {
				saveData[i] = KeyInput.keyBinds[2];
			} else if(i <= 37) {
				saveData[i] = KeyInput.keyBinds[3];
			}
		}

		ensureSaveFilesExist(fileDir, filePath);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			//Convert array to string format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < saveData.length; i++) {
				sb.append(saveData[i]);
				if (i < saveData.length - 1) {
					sb.append(", ");
				}
			}
			writer.write(sb.toString());
		} catch (IOException e) {
			System.out.println("Encountered an error while attempting to save data.");
		}
	}

	protected static void ensureSaveFilesExist(String dirPath, String filePath) {
		try {
			//Create directory if it doesn't exist
			Files.createDirectories(Paths.get(dirPath));

			//Create file if it doesn't exist
			File saveFile = new File(filePath);
			if (!saveFile.exists()) {
				saveFile.createNewFile();

				//Write out default data
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
					writer.write("50, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 68, 65, 32, 83");
				}
			}
		} catch (IOException e) {
			System.out.println("Encountered an error while looking for save data.");
		}
	}
	
	//Main method
	public static void main(String[] args) {
		new Game();
	}
}