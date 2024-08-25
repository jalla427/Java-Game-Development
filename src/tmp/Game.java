package tmp;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import enemy.HawkEnemy;
import enemy.SentryEnemy;
import enemy.ThumperEnemy;
import enemy.StriderEnemy;
import Item.Coin;
import level.LevelCollection;
import level.TileMapBuilder;

public class Game extends Canvas implements Runnable {

	private Thread thread;
	private boolean running = false;
	public static boolean escapeGame = false;
	public static boolean gameOver = false;
	public static boolean quit = false;
	
	//Main frame dimensions
	public static int sWidth = 900;
	public static int sHeight = 670;

	//Global volume
	public static int gameVolume = 30;
	
	//Variables primarily for level transition
	protected static boolean playerControl = true;
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
	public static BufferedImage sprite_sheet_menu_buttons;
	public static BufferedImage tomb_blocks_20x20;
	public static BufferedImage dungeon_blocks_20x20;
	public static BufferedImage burning_blocks_20x20;
	public static BufferedImage sprite_sheet;
	public static BufferedImage sprite_sheet_hawk;
	public static BufferedImage sprite_sheet_sentry;
	public static BufferedImage sprite_sheet_strider;
	public static BufferedImage sprite_sheet_thumper;
	public static BufferedImage sprite_sheet_coin;
	public static int playerSkin = 1;
	
	//Used for determining the current scene
	public enum STATE {
		Menu,
		Settings,
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
		sprite_sheet_menu_buttons = loader.loadImage("/sprite_sheet_menu_buttons.png");
		tomb_blocks_20x20 = loader.loadImage("/tomb_blocks_20x20.png");
		dungeon_blocks_20x20 = loader.loadImage("/dungeon_blocks_20x20.png");
		burning_blocks_20x20 = loader.loadImage("/burning_blocks_20x20.png");
		sprite_sheet = loader.loadImage("/sprite_sheet.png");
		sprite_sheet_hawk = loader.loadImage("/sprite_sheet_hawk.png");
		sprite_sheet_sentry = loader.loadImage("/sprite_sheet_sentry.png");
		sprite_sheet_strider = loader.loadImage("/sprite_sheet_strider.png");
		sprite_sheet_thumper = loader.loadImage("/sprite_sheet_thumper.png");
		sprite_sheet_coin = loader.loadImage("/sprite_sheet_coin.png");

		//Create core objects
		handler = new Handler();
		tombTileMapBuilder = new TileMapBuilder();
		menu = new Menu(handler);
		hud = new HUD();
		this.addKeyListener(new KeyInput(handler, this));
		this.addMouseListener(menu);

		//Create game window
		new Main("Game Demo", sWidth, sHeight, this);
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
//				System.out.println(KeyInput.keyDown[4]);
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
		//Based on gamestate, determine what needs to tick
		if(gameState == STATE.Game) {
			handler.tick();
			hud.tick();
			if(escapeGame) {
				menu.tick();
			}
		}
		else if(gameState == STATE.Menu || gameState == STATE.Settings) {
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
			if (HUD.HEALTH <= 0 && !escapeGame) {
				escapeGame = true;
			}

			if (escapeGame) {
				if (!gameOver) {
					beginGameOver();
					gameOver = true;
				}
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
				startLevelTransition(tomb_blocks_20x20, 5, 10, sWidth / 2 - 16, sHeight - 200);
			}

			//Level 6 Transition
			if (coinsLeft == 0 && hud.getLevel() == 5) {
				startLevelTransition(tomb_blocks_20x20, 6, 10, sWidth / 2 - 16, sHeight - 100);
			}

			//Level 7 Transition, start of section 2
			if (coinsLeft == 0 && hud.getLevel() == 6) {
				if(!gameOver) {
					levelBackgroundImg = dungeonBackgroundImg;
				}
				startLevelTransition(dungeon_blocks_20x20, 7, 6, sWidth / 2 - 16, sHeight - 300);
			}

			//Level 8 Transition
			if (coinsLeft == 0 && hud.getLevel() == 7) {
				startLevelTransition(dungeon_blocks_20x20, 8, 8, sWidth / 2 - 16, sHeight - 200);
			}

			//Level 9 Transition
			if (coinsLeft == 0 && hud.getLevel() == 8) {
				startLevelTransition(dungeon_blocks_20x20, 9, 10, sWidth / 2 - 16, sHeight - 200);
			}

			//Level 10 Transition
			if (coinsLeft == 0 && hud.getLevel() == 9) {
				startLevelTransition(dungeon_blocks_20x20, 10, 12, sWidth / 2 - 16, sHeight - 200);
			}

			//Level 11 Transition
			if (coinsLeft == 0 && hud.getLevel() == 10) {
				startLevelTransition(dungeon_blocks_20x20, 11, 14, sWidth / 2 - 16, sHeight - 180);
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
						handler.addObject(new HawkEnemy(sWidth / 4, 100, 32, 32, ID.Enemy, handler, 0));
						handler.addObject(new HawkEnemy(3 * (sWidth / 4), 100, 32, 32, ID.Enemy, handler, 120));
						handler.addObject(new SentryEnemy(0, 200, 20, 20, ID.Enemy, handler, 150, 0));
						endLevelTransition();
					}
				}

				if (hud.getLevel() == 3) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(sWidth / 4, 100, 32, 32, ID.Enemy, handler, 0));
						handler.addObject(new SentryEnemy(120, 100, 20, 20, ID.Enemy, handler, 250, 30));
						handler.addObject(new SentryEnemy(440, 100, 20, 20, ID.Enemy, handler, 250, 60));
						handler.addObject(new SentryEnemy(760, 100, 20, 20, ID.Enemy, handler, 250, 0));
						endLevelTransition();
					}
				}

				if (hud.getLevel() == 4) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(200, 100,32, 32, ID.Enemy, handler, 0));
						handler.addObject(new SentryEnemy(40, 100, 20, 20, ID.Enemy, handler, 250, 50));
						handler.addObject(new SentryEnemy(sWidth - 60, 100, 20, 20, ID.Enemy, handler, 250, 150));
						handler.addObject(new SentryEnemy(40, sHeight - 50, 20, 20, ID.Enemy, handler, 250, 50));
						handler.addObject(new SentryEnemy(sWidth - 60, sHeight - 50, 20, 20, ID.Enemy, handler, 250, 150));
						endLevelTransition();
					}
				}

				if (hud.getLevel() == 5) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 0));
						handler.addObject(new HawkEnemy(200, 300, 32, 32, ID.Enemy, handler, 0));
						handler.addObject(new SentryEnemy(40, sHeight - 50, 20, 20, ID.Enemy, handler, 200, 50));
						handler.addObject(new SentryEnemy(sWidth - 60, sHeight - 50, 20, 20, ID.Enemy, handler, 200, 150));
						endLevelTransition();
					}
				}

				if (hud.getLevel() == 6) {
					if (transitionTimer >= 200) {
						handler.addObject(new HawkEnemy(200, 100, 32, 32, ID.Enemy, handler, 0));
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
						handler.addObject(new StriderEnemy(100, 400, 32, 32, ID.Enemy, handler));
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
						handler.addObject(new ThumperEnemy(340, 180, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(540, 180, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}

				if (hud.getLevel() == 11) {
					if (transitionTimer >= 200) {
						handler.addObject(new StriderEnemy(100, sHeight - 60, 32, 32, ID.Enemy, handler));
						handler.addObject(new StriderEnemy(200, sHeight - 60, 32, 32, ID.Enemy, handler));
						handler.addObject(new StriderEnemy(sWidth - 200, sHeight - 165, 32, 32, ID.Enemy, handler));
						handler.addObject(new ThumperEnemy(180, 160, 32, 32, ID.Enemy, handler));
						endLevelTransition();
					}
				}
			}

			//Handle coin collection during level
			coinSpawner();
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

		if(gameState == STATE.Menu || gameState == STATE.Settings) {
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
			transitioning = true;
			playerControl = false;
			for(int i = 0;  i < KeyInput.keyDown.length; i++) {
				KeyInput.keyDown[i] = false;
			}
			while(handler.areLevel()) {
				handler.clearLevel();
			}
			tombTileMapBuilder.createLevel(tileMap, LevelCollection.getLevel(nextLevel), handler);
			handler.addObject(new Player(playerX, playerY, 32, 32, playerSkin, ID.Player, handler));
			hud.setLevel(nextLevel);
			setLevelCoinGoal(coins);
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
	
	//Main method
	public static void main(String[] args) {
		new Game();
	}
}