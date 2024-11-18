package tmp;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import enemy.*;
import Item.Coin;
import level.LevelCollection;
import level.TileMapBuilder;

public class Game extends Canvas implements Runnable {

	private Thread thread;
	private boolean running = false;
	protected static boolean clearButtons = false;
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
	private static int[] saveData = new int[33];
	
	//Variables primarily for level transition
	protected static boolean playerControl = true;
	protected static boolean levelEnd = false;
	protected static boolean transitioning = false;
	private static int transitionTimer = 0;
	private static String transitionMessage = "";
	public static int coinsLeft = 1;
	
	private static Handler handler;
	private final Menu menu;
	public static HUD hud;
	protected static TileMapBuilder tombTileMapBuilder;

	//Sprites
	public static BufferedImage backgroundImg;
	public static BufferedImage levelBackgroundImg;
	public static BufferedImage tombBackgroundImg;
	public static BufferedImage dungeonBackgroundImg;
	public static BufferedImage infernoBackgroundImg;
	public static BufferedImage finalBackgroundImg;
	public static BufferedImage sprite_sheet_menu_buttons;
	public static BufferedImage tomb_blocks_20x20;
	public static BufferedImage dungeon_blocks_20x20;
	public static BufferedImage burning_blocks_20x20;
	public static BufferedImage final_blocks_20x20;
	public static BufferedImage sprite_sheet;
	public static BufferedImage sprite_sheet_hawk;
	public static BufferedImage sprite_sheet_sentry;
	public static BufferedImage sprite_sheet_strider;
	public static BufferedImage sprite_sheet_thumper;
	public static BufferedImage sprite_sheet_wisp;
	public static BufferedImage sprite_sheet_golem;
	public static BufferedImage sprite_sheet_core;
	public static BufferedImage sprite_sheet_coin;
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
		Blitz,
		Game
	}

    public static STATE gameState = STATE.Menu;
	public static boolean debugMode = false;
	
	//Constructor
	public Game() {
		//Load assets
		BufferedImageLoader loader = new BufferedImageLoader();
		backgroundImg = loader.loadImage("/tombMainMenu.png");
		tombBackgroundImg = loader.loadImage("/tombBackground.png");
		dungeonBackgroundImg = loader.loadImage("/dungeonBackground.png");
		infernoBackgroundImg = loader.loadImage("/infernoBackground.png");
		finalBackgroundImg = loader.loadImage("/finalBackground.png");
		sprite_sheet_menu_buttons = loader.loadImage("/sprite_sheet_menu_buttons.png");
		tomb_blocks_20x20 = loader.loadImage("/tomb_blocks_20x20.png");
		dungeon_blocks_20x20 = loader.loadImage("/dungeon_blocks_20x20.png");
		burning_blocks_20x20 = loader.loadImage("/burning_blocks_20x20.png");
		final_blocks_20x20 = loader.loadImage("/final_blocks_20x20.png");
		sprite_sheet = loader.loadImage("/sprite_sheet.png");
		sprite_sheet_hawk = loader.loadImage("/sprite_sheet_hawk.png");
		sprite_sheet_sentry = loader.loadImage("/sprite_sheet_sentry.png");
		sprite_sheet_strider = loader.loadImage("/sprite_sheet_strider.png");
		sprite_sheet_thumper = loader.loadImage("/sprite_sheet_thumper.png");
		sprite_sheet_wisp = loader.loadImage("/sprite_sheet_wisp.png");
		sprite_sheet_golem = loader.loadImage("/sprite_sheet_golem.png");
		sprite_sheet_core = loader.loadImage("/sprite_sheet_core.png");
		sprite_sheet_coin = loader.loadImage("/sprite_sheet_coin.png");
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
		menu = new Menu(handler);
		hud = new HUD();
		this.addKeyListener(new KeyInput(handler, this));
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
		final double maxDelta = 10.0;
		long lastTime = System.nanoTime();
		long sleepTime;
		long timeTaken;
		double amountOfTicks = 60;
		double nsPerTick = 1000000000.0 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;

		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			//Cap the delta to avoid spiraling
			if (delta > maxDelta) {
				delta = maxDelta;
			}

			while(delta >= 1) {
				tick();
				delta--;
			}
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
			if((System.currentTimeMillis() - timer) >= 1000){
				timer = System.currentTimeMillis();
				if(debugMode) {
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
		else if(gameState == STATE.Menu || gameState == STATE.Settings || gameState == STATE.Statistics || gameState == STATE.LevelSelect) {
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

		//Game start, Level 1 Transition
		if(gameState == STATE.Menu && hud.getLevel() == 1) {
			levelBackgroundImg = tombBackgroundImg;
			gameState = STATE.Game;
			handler.clearButtons();
			startLevelTransition(tomb_blocks_20x20, 1, 3, sWidth/2-16, sHeight/2-32);
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
			if(coinsLeft == 0 && !gameOver && !transitioning && !levelEnd) {
				levelEnd = true;
				while(handler.areEnemies()) {
					handler.clearEnemies();
				}
				playerControl = false;
			}
			//Level 2 Transition
			if (coinsLeft == 0 && hud.getLevel() == 1) {
				startLevelTransition(tomb_blocks_20x20, 2, 4, sWidth / 2 - 16, sHeight / 2 + 232);
			}
			//Level 3 Transition
			if (coinsLeft == 0 && hud.getLevel() == 2) {
				startLevelTransition(tomb_blocks_20x20, 3, 6, sWidth / 2 - 16, sHeight / 2 + 232);
			}
			//Level 4 Transition
			if (coinsLeft == 0 && hud.getLevel() == 3) {
				startLevelTransition(tomb_blocks_20x20, 4, 8, sWidth / 2 - 16, sHeight - 60);
			}
			//Level 5 Transition
			if (coinsLeft == 0 && hud.getLevel() == 4) {
				startLevelTransition(tomb_blocks_20x20, 5, 9, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 6 Transition
			if (coinsLeft == 0 && hud.getLevel() == 5) {
				startLevelTransition(tomb_blocks_20x20, 6, 9, sWidth / 2 - 16, sHeight - 100);
			}
			//Level 7 Transition, start of section 2
			if (coinsLeft == 0 && hud.getLevel() == 6) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = dungeonBackgroundImg;
					unlockedSkins[1] = true;
				}
				startLevelTransition(dungeon_blocks_20x20, 7, 6, sWidth / 2 - 16, sHeight - 300);
			}
			//Level 8 Transition
			if (coinsLeft == 0 && hud.getLevel() == 7) {
				startLevelTransition(dungeon_blocks_20x20, 8, 8, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 9 Transition
			if (coinsLeft == 0 && hud.getLevel() == 8) {
				startLevelTransition(dungeon_blocks_20x20, 9, 9, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 10 Transition
			if (coinsLeft == 0 && hud.getLevel() == 9) {
				startLevelTransition(dungeon_blocks_20x20, 10, 8, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 11 Transition
			if (coinsLeft == 0 && hud.getLevel() == 10) {
				startLevelTransition(dungeon_blocks_20x20, 11, 8, sWidth / 2 - 16, sHeight - 180);
			}
			//Level 12 Transition
			if (coinsLeft == 0 && hud.getLevel() == 11) {
				startLevelTransition(dungeon_blocks_20x20, 12, 9, sWidth / 2 - 16, sHeight - 180);
			}
			//Level 13 Transition, start of section 3
			if (coinsLeft == 0 && hud.getLevel() == 12) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = infernoBackgroundImg;
					unlockedSkins[2] = true;
				}
				startLevelTransition(burning_blocks_20x20, 13, 8, sWidth / 2 - 16, sHeight/2);
			}
			//Level 14 Transition
			if (coinsLeft == 0 && hud.getLevel() == 13) {
				startLevelTransition(burning_blocks_20x20, 14, 9, sWidth / 2 - 16, sHeight - 350);
			}
			//Level 15 Transition
			if (coinsLeft == 0 && hud.getLevel() == 14) {
				startLevelTransition(burning_blocks_20x20, 15, 9, sWidth / 2 - 16, sHeight - 240);
			}
			//Level 16 Transition
			if (coinsLeft == 0 && hud.getLevel() == 15) {
				startLevelTransition(burning_blocks_20x20, 16, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 17 Transition
			if (coinsLeft == 0 && hud.getLevel() == 16) {
				startLevelTransition(burning_blocks_20x20, 17, 9, sWidth / 2 - 16, sHeight - 200);
			}
			//Level 18 Transition
			if (coinsLeft == 0 && hud.getLevel() == 17) {
				startLevelTransition(burning_blocks_20x20, 18, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 19 Transition
			if (coinsLeft == 0 && hud.getLevel() == 18) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = tombBackgroundImg;
					unlockedSkins[3] = true;
				}
				startLevelTransition(tomb_blocks_20x20, 19, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 20 Transition
			if (coinsLeft == 0 && hud.getLevel() == 19) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = dungeonBackgroundImg;
				}
				startLevelTransition(dungeon_blocks_20x20, 20, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 21 Transition
			if (coinsLeft == 0 && hud.getLevel() == 20) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = infernoBackgroundImg;
				}
				startLevelTransition(burning_blocks_20x20, 21, 9, sWidth / 2 - 16, sHeight - 250);
			}
			//Level 22 Transition
			if (coinsLeft == 0 && hud.getLevel() == 21) {
				if(!gameOver && !levelEnd) {
					levelBackgroundImg = finalBackgroundImg;
				}
				startLevelTransition(final_blocks_20x20, 22, 12, sWidth / 2 - 16, sHeight - 250);
			}
			//Game Won
			if (coinsLeft == 0 && hud.getLevel() == 22) {
				levelEnd = true;
				beginGameOver();
			}

			//Level Transition Timer
			if (transitioning && !escapeGame) {
				transitionTimer++;
				transitionMessage = "Level " + hud.getLevel();

				//Brief pause between levels before calling enemies
				if (hud.getLevel() == 1) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(100, 100, 32, 32, ID.Enemy, handler, 300));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 2) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(sWidth / 4, 100, 32, 32, ID.Enemy, handler, 50));
						handler.addObject(new HawkEnemy(3 * (sWidth / 4), 100, 32, 32, ID.Enemy, handler, 120));
						handler.addObject(new SentryEnemy(0, 200, 20, 20, ID.Enemy, handler, 150, 0));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 3) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(sWidth / 4, 100, 32, 32, ID.Enemy, handler, 200));
						handler.addObject(new SentryEnemy(120, 100, 20, 20, ID.Enemy, handler, 250, 30));
						handler.addObject(new SentryEnemy(440, 100, 20, 20, ID.Enemy, handler, 250, 60));
						handler.addObject(new SentryEnemy(760, 100, 20, 20, ID.Enemy, handler, 250, 0));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 4) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(200, 100,32, 32, ID.Enemy, handler, 100));
						handler.addObject(new SentryEnemy(40, 100, 20, 20, ID.Enemy, handler, 250, 50));
						handler.addObject(new SentryEnemy(sWidth - 60, 100, 20, 20, ID.Enemy, handler, 250, 150));
						handler.addObject(new SentryEnemy(40, sHeight - 50, 20, 20, ID.Enemy, handler, 250, 50));
						handler.addObject(new SentryEnemy(sWidth - 60, sHeight - 50, 20, 20, ID.Enemy, handler, 250, 150));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 5) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 200));
						handler.addObject(new HawkEnemy(200, 300, 32, 32, ID.Enemy, handler, 100));
						handler.addObject(new SentryEnemy(40, sHeight - 50, 20, 20, ID.Enemy, handler, 200, 50));
						handler.addObject(new SentryEnemy(sWidth - 60, sHeight - 50, 20, 20, ID.Enemy, handler, 200, 150));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 6) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 250));
						handler.addObject(new SentryEnemy(420, 120, 20, 20, ID.Enemy, handler, 200, 100));
						handler.addObject(new SentryEnemy(420, 160, 20, 20, ID.Enemy, handler, 200, 110));
						handler.addObject(new SentryEnemy(460, 120, 20, 20, ID.Enemy, handler, 200, 120));
						handler.addObject(new SentryEnemy(460, 160, 20, 20, ID.Enemy, handler, 200, 130));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 7) {
					if (transitionTimer >= 200) {
						handler.addObject(new StriderEnemy(100, 150, 32, 32, ID.Enemy, handler));
						handler.addObject(new StriderEnemy(sWidth - 100, 100, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 8) {
					if (transitionTimer >= 200) {
						handler.addObject(new StriderEnemy(100, 400, 32, 32, ID.Enemy, handler));
						handler.addObject(new StriderEnemy(sWidth - 132, 400, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(100, 150, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 9) {
					if (transitionTimer >= 200) {
						handler.addObject(new StriderEnemy(sWidth - 100, 400, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(100, 150, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(Game.sWidth - 100, 150, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 10) {
					if (transitionTimer >= 200) {
						handler.addObject(new StriderEnemy(50, sHeight - 75, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(160, 180, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(540, 180, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 11) {
					if (transitionTimer >= 200) {
						handler.addObject(new StriderEnemy(100, sHeight - 60, 32, 32, ID.Enemy, handler));
						handler.addObject(new StriderEnemy(200, sHeight - 60, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(180, 160, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 12) {
					if (transitionTimer >= 200) {
						handler.addObject(new StriderEnemy(Game.sWidth - 62, 220, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy((Game.sWidth/2) - 80, 300, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy((Game.sWidth/2) + 48, 300, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 13) {
					if (transitionTimer >= 200) {
						handler.addObject(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						handler.addObject(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 14) {
					if (transitionTimer >= 200) {
						handler.addObject(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 100));
						handler.addObject(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 15) {
					if (transitionTimer >= 200) {
						handler.addObject(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 50));
						handler.addObject(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 200));
						handler.addObject(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 16) {
					if (transitionTimer >= 200) {
						handler.addObject(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 75));
						handler.addObject(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						handler.addObject(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 17) {
					if (transitionTimer >= 200) {
						handler.addObject(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 50));
						handler.addObject(new GolemEnemy(80, Game.sHeight - 180, 40, 40, ID.Enemy, handler));
						handler.addObject(new GolemEnemy(Game.sWidth/2 + 20, 200, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 18) {
					if (transitionTimer >= 200) {
						handler.addObject(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 150));
						handler.addObject(new GolemEnemy(30, 220, 40, 40, ID.Enemy, handler));
						handler.addObject(new GolemEnemy(Game.sWidth - 70, 220, 40, 40, ID.Enemy, handler));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 19) {
					if (transitionTimer >= 200) {
						handler.addObject(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 50));
						handler.addObject(new WispEnemy(Game.sWidth - 240, 100, 26, 26, ID.Enemy, handler, 175));
						handler.addObject(new SentryEnemy(40, 100, 20, 20, ID.Enemy, handler, 300, 200));
						handler.addObject(new SentryEnemy(40, 220, 20, 20, ID.Enemy, handler, 200, 110));
						handler.addObject(new SentryEnemy(sWidth - 60, 100, 20, 20, ID.Enemy, handler, 300, 120));
						handler.addObject(new SentryEnemy(sWidth - 60, 220, 20, 20, ID.Enemy, handler, 200, 230));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 20) {
					if (transitionTimer >= 200) {
						handler.addObject(new GolemEnemy(Game.sWidth/2 + 20, 200, 40, 40, ID.Enemy, handler));
						handler.addObject(new StriderEnemy(30, 220, 32, 32, ID.Enemy, handler));
						handler.addObject(new HawkEnemy(200, 180, 32, 32, ID.Enemy, handler, 100));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 21) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 200));
						handler.addObject(new ThumperEnemy((Game.sWidth/2) - 80, 200, 32, 32, ID.Enemy, handler));
						handler.addObject(new WispEnemy(200, 100, 26, 26, ID.Enemy, handler, 100));
						endLevelTransition();
					}
				}
				if (hud.getLevel() == 22) {
					if (transitionTimer >= 200) {
						handler.addObject(new CoreEnemy(100, 100, 20, 20, ID.Enemy, handler));
						handler.addObject(new CoreEnemy(sWidth - 100, 100, 20, 20, ID.Enemy, handler));
						endLevelTransition();
					}
				}
			}

			//Handle coin collection during level
			if(!escapeGame) { coinSpawner(); }
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

		if(gameState == STATE.Menu || gameState == STATE.Settings || gameState == STATE.Statistics || gameState == STATE.LevelSelect) {
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
	
	//Start transitioning level
	private void startLevelTransition(BufferedImage tileMap, int nextLevel, int coins, int playerX, int playerY) {
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
				handler.addObject(new Player(playerX, playerY, 32, 32, playerSkin, ID.Player, handler));
				hud.setLevel(nextLevel);
				unlockedLevels[nextLevel - 1] = true;
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
	
	//Transition to gameover
	public static void beginGameOver() {
		escapeGame = true;
		playerControl = false;
		for(int i = 0;  i < KeyInput.keyDown.length; i++) {
			KeyInput.keyDown[i] = false;
		}
		transitioning = false;
		transitionTimer = 0;
		coinsLeft = 0;
		while(handler.areEnemies() || handler.areCoins()) {
			handler.clearEnemies();
			handler.clearItems();
		}
	}

	//Set level coin goal during level transition
	private void setLevelCoinGoal(int goal) {
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
				saveData = new int[] {50, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
				writeOutSaveData();
			}
		} catch (IOException e) {
			System.out.println("Encountered an error while attempting to load save data.");
		}

		//Set game values based on retrieved save data
		for (int i = 0; i < saveData.length; i++) {
			if(i == 0) {
				gameVolume = saveData[i];
			} else if(i == 1) {
				highScore = saveData[i];
			} else if(i == 2) {
				blitzHighScore = saveData[i];
			} else if(i <= 10) {
                unlockedSkins[i - 3] = saveData[i] == 1;
			} else {
                unlockedLevels[i - 11] = saveData[i] == 1;
			}
		}
	}

	protected static void writeOutSaveData() {
		String filePath = "./save/savedata.txt"; //Save location
		String fileDir = "./save"; //Save directory

		//Set save data values based on current game values
		for (int i = 0; i < saveData.length; i++) {
			if (i == 0) {
				 saveData[i] = gameVolume;
			} else if (i == 1) {
				 saveData[i] = highScore;
			} else if (i == 2) {
				saveData[i] = blitzHighScore;
			} else if (i <= 10) {
				saveData[i] = unlockedSkins[i - 3] ? 1 : 0;
			} else {
				saveData[i] = unlockedLevels[i - 11] ? 1 : 0;
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
					writer.write("50, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0");
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