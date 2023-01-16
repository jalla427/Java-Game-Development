package tmp;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import tmp.Game.STATE;

public class KeyInput extends KeyAdapter {
	
	private Handler handler;
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
		
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			
			//Character Controls
			if(tempObject.getID() == ID.Player && Game.playerControl == true) {
				//WASD
				if(key == KeyEvent.VK_W && keyDown[1] != true && tempObject.isGrounded()) { 
					//Formerly jump, switched to space
					keyDown[0] = true; 
				}
				else if(key == KeyEvent.VK_W) { 
					keyDown[0] = true; 
				}
				if(key == KeyEvent.VK_S && keyDown[0] != true) { 
					//Formerly down, replaced with jumping mechanic
					keyDown[1] = true; 
				}
				else if(key == KeyEvent.VK_S) { 
					keyDown[1] = true; 
				}
				if(key == KeyEvent.VK_D && keyDown[3] != true) { 
					tempObject.setxDirection(1); 
					keyDown[2] = true; 
				}
				else if(key == KeyEvent.VK_D) { 
					tempObject.setxDirection(2); 
					keyDown[2] = true; 
				}
				if(key == KeyEvent.VK_A && keyDown[2] != true) { 
					tempObject.setxDirection(0); 
					keyDown[3] = true; 
				}
				else if(key == KeyEvent.VK_A) { 
					tempObject.setxDirection(2); 
					keyDown[3] = true; 
				}
				
				//Jump
				if(key == KeyEvent.VK_SPACE) { 
					keyDown[4] = true;
					if(tempObject.isGrounded()) {
						if(Game.debugMode == true) {
							System.out.println("Jumped!");
						}
						tempObject.velY -= 20; 
						tempObject.setGrounded(false);  
					}
				}
			}
		}
		
		//Pause
		if(key == KeyEvent.VK_P) {
			if(game.gameState == STATE.Game) {

			}
		}
		
		//Debug Mode
		if(key == KeyEvent.VK_F3) {
			if(Game.debugMode == false) {
				Game.debugMode = true;
				System.out.println("** Debug Mode On **");
			}
			else {
				Game.debugMode = false;
				System.out.println("** Debug Mode Off **");
			}
		}
		
		//Quit game
		if(key == KeyEvent.VK_ESCAPE) {
			Game.escapeGame = true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.getID() == ID.Player) {
				//Key events for player
				if(key == KeyEvent.VK_W) keyDown[0] = false; 
				if(key == KeyEvent.VK_S) keyDown[1] = false; 
				if(key == KeyEvent.VK_D) {
					if(keyDown[3]){
						tempObject.setxDirection(0);
					}
					keyDown[2] = false; 
				}
				if(key == KeyEvent.VK_A) {
					if(keyDown[2]){
						tempObject.setxDirection(1);
					}
					keyDown[3] = false;
				}
				if(key == KeyEvent.VK_SPACE) keyDown[4] = false;
				
				//Horizontal movement
				if(!keyDown[2] && !keyDown[3]) tempObject.setxDirection(2);
			}
		}
	}
}
