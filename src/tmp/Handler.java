package tmp;

import java.awt.Graphics;
import java.util.LinkedList;


public class Handler {
	
	public LinkedList<GameObject> object = new LinkedList<GameObject>();
	
	public void tick() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			
			tempObject.tick();
		}
	}
	
	public void render(Graphics g) {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() != ID.Player) {
				tempObject.render(g);
			}
		}
		
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Player) {
				tempObject.render(g);
			}
		}
	}
	
	public void addObject(GameObject object) {
		this.object.add(object);
	}
	
	public void removeObject(GameObject object) {
		this.object.remove(object);
	}
	
	public void clearPlayer() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Player) {
				removeObject(object.get(i));
			}
		}
	}
	
	public void clearTiles() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Level) {
				removeObject(object.get(i));
			}
		}
	}
	
	public void clearEnemies() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Enemy) {
				removeObject(object.get(i));
			}
		}
	}
	
	public void clearLevel() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Enemy || tempObject.getID() == ID.Player || tempObject.getID() == ID.Level) {
				removeObject(object.get(i));
			}
		}
	}
	
	public boolean arePlayers() {
		boolean foundPlayer = false;
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Player) {
				foundPlayer = true;
				break;
			}
		}
		return foundPlayer;
	}
	
	public boolean areTiles() {
		boolean foundTile = false;
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Level) {
				foundTile = true;
				break;
			}
		}
		return foundTile;
	}
	
	public boolean areEnemies() {
		boolean foundEnemy = false;
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Enemy) {
				foundEnemy = true;
				break;
			}
		}
		return foundEnemy;
	}
	
	public boolean areLevel() {
		boolean foundLevel = false;
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Enemy || tempObject.getID() == ID.Player || tempObject.getID() == ID.Level) {
				foundLevel = true;
				break;
			}
		}
		return foundLevel;
	}
}
