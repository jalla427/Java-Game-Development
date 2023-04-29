package tmp;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import enemy.HawkEnemy;
import enemy.SentryEnemy;
import level.LevelCollection;
import level.TombTileMapBuilder;
import tmp.HUD;
import tmp.KeyInput;
import tmp.Menu;
import tmp.Game.STATE;
import tmp.BufferedImageLoader;

public class Game extends Canvas implements Runnable {

	private Thread thread;
	private boolean running = false;
	public static boolean escapeGame = false;
	public static boolean gameOver = false;
	public static boolean quit = false;
	
	//Main frame dimensions
	public static int sWidth = 900;
	public static int sHeight = 670;
	
	//Variables primarily for level transition
	protected static boolean playerControl = true;
	protected static boolean transitioning = false;
	private static int transitionTimer = 0;
	private static String transitionMessage = "";
	
	private static Handler handler;
	private Menu menu;
	protected static HUD hud;
	protected static TombTileMapBuilder tombTileMapBuilder;
	
	public static BufferedImage backgroundImg;
	public static BufferedImage tomb_blocks_20x20;
	public static BufferedImage sprite_sheet;
	public static BufferedImage sprite_sheet_hawk;
	public static BufferedImage sprite_sheet_sentry;
	
	//Used for determining the current scene
	public enum STATE {
		Menu,
		Settings,
		Game
	};
	public static STATE gameState = STATE.Menu;
	public static boolean debugMode = false;
	
	//Constructor
	public Game() {
		handler = new Handler();
		tombTileMapBuilder = new TombTileMapBuilder();
		menu = new Menu(handler);
		hud = new HUD();
		this.addKeyListener(new KeyInput(handler, this));
		this.addMouseListener(menu);
		
		BufferedImageLoader loader = new BufferedImageLoader();
		backgroundImg = loader.loadImage("/tombMainMenu.png");
		tomb_blocks_20x20 = loader.loadImage("/tomb_blocks_20x20.png");
		sprite_sheet = loader.loadImage("/sprite_sheet.png");
		sprite_sheet_hawk = loader.loadImage("/sprite_sheet_hawk.png");
		sprite_sheet_sentry = loader.loadImage("/sprite_sheet_sentry.png");
		
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
		long lastTime = System.nanoTime();
		double amountOfTicks = 60;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				delta--;
			}
			if(running)
				render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				//System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}
	
	//Update method
	private void tick() {
		if(gameState == STATE.Game) {
			handler.tick();
			hud.tick();
		}
		else if(gameState == STATE.Menu || gameState == STATE.Settings) {
			handler.tick();
			menu.tick();
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
		
		handler.render(g);
		
		//Quit was selected
		if(quit) {
			while(handler.areLevel()) {
				handler.clearLevel();
			}
			quit = false;
		}
		
		//Game start, Level 1 Transition
		if(gameState == STATE.Menu && hud.getLevel() == 1) {
			gameState = STATE.Game;
			startLevelTransition(1, sWidth/2-16, sHeight/2-32);
		}
		
		if(gameState == STATE.Game) {
			//Game Over
			if(HUD.HEALTH <= 0 && !escapeGame) {
				escapeGame = true;
			}
			
			if(escapeGame) {
				if(!gameOver) {
					beginGameOver();
					gameOver = true;
				}
			}
			
			//Level 2 Transition
			if(hud.getScore() == 150 && hud.getLevel() == 1) {
				startLevelTransition(2, sWidth/2-16, sHeight/2+232);
			}
			
			//Level 3 Transition
			if(hud.getScore() == 300 && hud.getLevel() == 2) {
				startLevelTransition(3, sWidth/2-16, sHeight/2+232);
			}
			
			//Level 3 Transition
			if(hud.getScore() == 450 && hud.getLevel() == 3) {
				startLevelTransition(4, sWidth/2-16, sHeight-60);
			}
			
			//Level Transition Timer
			if(transitioning && !escapeGame) {
				transitionTimer++;
				
				//Next level banner
				transitionMessage = "Level " + hud.getLevel();
				g.setColor(Color.white);
				g.setFont(new Font("Helvetica", Font.PLAIN, 36));
				g.drawString(transitionMessage, sWidth/2-50, sHeight/2);
				
				//Brief pause between levels before calling enemies
				if(hud.getLevel() == 1) {
					if(transitionTimer >= 7000) {
						handler.addObject(new HawkEnemy(100, 100, ID.Enemy, handler, 300));
						endLevelTransition();
					}
				}
				
				if(hud.getLevel() == 2) {
					if(transitionTimer >= 7000) {
						handler.addObject(new HawkEnemy(sWidth/4, 100, ID.Enemy, handler, 0));
						handler.addObject(new HawkEnemy(3*(sWidth/4), 100, ID.Enemy, handler, 120));
						handler.addObject(new SentryEnemy(0, 200, ID.Enemy, handler, 150, 0));
						endLevelTransition();
					}
				}
				
				if(hud.getLevel() == 3) {
					if(transitionTimer >= 7000) {
						handler.addObject(new HawkEnemy(sWidth/4, 100, ID.Enemy, handler, 0));
						handler.addObject(new SentryEnemy(120, 100, ID.Enemy, handler, 120, 30));
						handler.addObject(new SentryEnemy(440, 100, ID.Enemy, handler, 120, 60));
						handler.addObject(new SentryEnemy(760, 100, ID.Enemy, handler, 120, 0));
						endLevelTransition();
					}
				}
				
				if(hud.getLevel() == 4) {
					if(transitionTimer >= 7000) {
						handler.addObject(new HawkEnemy(200, 100, ID.Enemy, handler, 0));
						handler.addObject(new SentryEnemy(40, 100, ID.Enemy, handler, 200, 50));
						handler.addObject(new SentryEnemy(sWidth-60, 100, ID.Enemy, handler, 200, 150));
						handler.addObject(new SentryEnemy(40, sHeight-50, ID.Enemy, handler, 200, 100));
						handler.addObject(new SentryEnemy(sWidth-60, sHeight-50, ID.Enemy, handler, 200, 0));
						endLevelTransition();
					}
				}
			}
			
			hud.render(g);
		}
		else if(gameState == STATE.Menu || gameState == STATE.Settings) {
			g.drawImage(backgroundImg, 0, 0, null);
		}

		menu.render(g);
		g.dispose();
		bs.show();
	}
	
	//Start transitioning level
	private static void startLevelTransition(int nextLevel, int playerX, int playerY) {
		transitioning = true;
		playerControl = false;
		while(handler.areLevel()) {
			handler.clearLevel();
		}
		tombTileMapBuilder.createTombLevel(LevelCollection.getLevel(nextLevel), handler);
		handler.addObject(new Player(playerX, playerY, ID.Player, handler));
		hud.setLevel(nextLevel);
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
		transitioning = false;
		transitionTimer = 0;
		while(handler.areEnemies()) {
			handler.clearEnemies();
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
	
	//Main method
	public static void main(String args[]) {
		new Game();
	}
}