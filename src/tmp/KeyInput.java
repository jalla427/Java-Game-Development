package tmp;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import tmp.Game.STATE;

public class KeyInput extends KeyAdapter {
	
	private final Handler handler;
	protected static boolean[] keyDown = new boolean[5];
	
	Game game;
	
	public KeyInput(Handler handler, Game game) {
		this.handler = handler;
		this.game = game;
		
		keyDown[0] = false;
		keyDown[1] = false;
		keyDown[2] = false;
		keyDown[3] = false;
		keyDown[4] = false;
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		//Character Controls
		if(Game.playerControl) {
			//Directional movement
			if(key == KeyEvent.VK_D && !keyDown[3]) {
				handler.playerObject.setxDirection(1);
				keyDown[2] = true;
			}
			else if(key == KeyEvent.VK_D) {
				handler.playerObject.setxDirection(2);
				keyDown[2] = true;
			}
			if(key == KeyEvent.VK_A && !keyDown[2]) {
				handler.playerObject.setxDirection(0);
				keyDown[3] = true;
			}
			else if(key == KeyEvent.VK_A) {
				handler.playerObject.setxDirection(2);
				keyDown[3] = true;
			}
				
			//Jump
			if(key == KeyEvent.VK_SPACE) {
				keyDown[4] = true;
				if(handler.playerObject.isGrounded()) {
					handler.playerObject.velY -= 20;
					handler.playerObject.setGrounded(false);
				}
			}
		}
		
		//Debug Mode
		if(key == KeyEvent.VK_F3) {
			if(!Game.debugMode) {
				Game.debugMode = true;
				System.out.println("** Debug Mode On **");
			}
			else {
				Game.debugMode = false;
				System.out.println("** Debug Mode Off **");
			}
		}
		
		//Quit game
		if(key == KeyEvent.VK_ESCAPE && Game.gameState == STATE.Game && !Game.transitioning && !Game.levelEnd) {
			if(Game.paused == false) {
				Game.paused = true;
			} else {
				Handler.clearButtons();
				Game.paused = false;
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if(Game.playerControl) {
			//Key events for player
			if(key == KeyEvent.VK_W) keyDown[0] = false;
			if(key == KeyEvent.VK_S) keyDown[1] = false;
			if(key == KeyEvent.VK_D) {
				if(keyDown[3]){
					handler.playerObject.setxDirection(0);
				}
				keyDown[2] = false;
			}
			if(key == KeyEvent.VK_A) {
				if(keyDown[2]){
					handler.playerObject.setxDirection(1);
				}
				keyDown[3] = false;
			}
			if(key == KeyEvent.VK_SPACE) {
				keyDown[4] = false;
				if(!handler.playerObject.isGrounded() && handler.playerObject.velY < -5) {
					handler.playerObject.velY = -5;
				}
			}
				
			//Horizontal movement
			if(!keyDown[2] && !keyDown[3]) handler.playerObject.setxDirection(2);
		}
	}
}
