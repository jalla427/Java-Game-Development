package tmp;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import tmp.Game.STATE;

public class KeyInput extends KeyAdapter {
	protected static int[] keyBinds = new int[] {KeyEvent.VK_D, KeyEvent.VK_A, KeyEvent.VK_SPACE};
	protected static boolean[] keyDown = new boolean[5];
	
	public KeyInput() {
		keyDown[0] = false; //Right
		keyDown[1] = false; //Left
		keyDown[2] = false; //Jump
		keyDown[3] = false;
		keyDown[4] = false;
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		//Character Controls
		if(Game.playerControl) {
			//Directional movement
			if(key == keyBinds[0] && !keyDown[1]) {
				Handler.playerObject.setxDirection(1);
				keyDown[0] = true;
			}
			else if(key == keyBinds[0]) {
				Handler.playerObject.setxDirection(2);
				keyDown[0] = true;
			}
			if(key == keyBinds[1] && !keyDown[0]) {
				Handler.playerObject.setxDirection(0);
				keyDown[1] = true;
			}
			else if(key == keyBinds[1]) {
				Handler.playerObject.setxDirection(2);
				keyDown[1] = true;
			}
				
			//Jump
			if(key == keyBinds[2]) {
				keyDown[2] = true;
				if(Handler.playerObject.isGrounded()) {
					Handler.playerObject.velY -= 20;
					Handler.playerObject.setGrounded(false);
					Handler.playerObject.jumping = true;
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
			if(!Game.paused) {
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
			if(key == keyBinds[0]) {
				if(keyDown[1]){ //Opposite direction key still pressed, move that way from rest
					Handler.playerObject.setxDirection(0);
				} else { //Both direction keys are released, lower player X velocity
					Handler.playerObject.setxDirection(2);
					Handler.playerObject.setVelX(Game.clamp(Handler.playerObject.getVelX(), -2, 2));
				}
				keyDown[0] = false;
			}
			if(key == keyBinds[1]) {
				if(keyDown[0]){ //Opposite direction key still pressed, move that way from rest
					Handler.playerObject.setxDirection(1);
				} else { //Both direction keys are released, lower player X velocity
					Handler.playerObject.setxDirection(2);
					Handler.playerObject.setVelX(Game.clamp(Handler.playerObject.getVelX(), -2, 2));
				}
				keyDown[1] = false;
			}
			if(key == keyBinds[2]) {
				keyDown[2] = false;
				if(!Handler.playerObject.isGrounded() && Handler.playerObject.velY < -5) {
					Handler.playerObject.velY = -5;
				}
			}
				
			//Horizontal movement
			if(!keyDown[0] && !keyDown[1]) Handler.playerObject.setxDirection(2);
		}
	}
}
