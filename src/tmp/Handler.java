package tmp;

import java.awt.Graphics;
import java.util.LinkedList;


public class Handler {
	
	public LinkedList<GameObject> object = new LinkedList<GameObject>();
	public LinkedList<RectTextButton> buttonList = new LinkedList<RectTextButton>();
	
	public void tick() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			tempObject.tick();
		}
		for(int i = 0; i < buttonList.size(); i++) {
			RectTextButton tempObject = buttonList.get(i);
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

	public void renderHigherElements(Graphics g) {
		for(int i = 0; i < buttonList.size(); i++) {
			RectTextButton tempObject = buttonList.get(i);
			tempObject.render(g);
		}
	}
	
	public void addObject(GameObject object) {
		this.object.add(object);
	}
	public void removeObject(GameObject object) {
		this.object.remove(object);
	}

	public void addButton(RectTextButton button) { this.buttonList.add(button); }
	public void removeButton(RectTextButton button) { this.buttonList.remove(button); }
	
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

	public void clearItems() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Coin) {
				removeObject(object.get(i));
			}
		}
	}
	
	public void clearLevel() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Enemy || tempObject.getID() == ID.Player || tempObject.getID() == ID.Level || tempObject.getID() == ID.Coin) {
				removeObject(object.get(i));
			}
		}
	}

	public void clearButtons() {
		while(areButtons()) {
			buttonList.pop();
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

	public boolean areCoins() {
		boolean foundCoin = false;
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Coin) {
				foundCoin = true;
				break;
			}
		}
		return foundCoin;
	}
	
	public boolean areLevel() {
		boolean foundLevel = false;
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Enemy || tempObject.getID() == ID.Player || tempObject.getID() == ID.Level || tempObject.getID() == ID.Coin) {
				foundLevel = true;
				break;
			}
		}
		return foundLevel;
	}

	public boolean areButtons() {
		boolean foundButton = false;
		if(buttonList.size() > 0) {
			foundButton = true;
		}
		return foundButton;
	}

	public String getButtonAtLocation(int mx, int my) {
		String buttonName = "";
		for(int i = 0; i < buttonList.size(); i++) {
			RectTextButton tempObject = buttonList.get(i);
			if(Game.isPointInBounds(mx, my, (int) tempObject.getX(), (int) tempObject.getY(), tempObject.getWidth(), tempObject.getHeight())) {
				buttonName = tempObject.getText();
				break;
			}
		}
		return buttonName;
	}

	public String getButtonNames() {
		String buttonName = "";
		for(int i = 0; i < buttonList.size(); i++) {
			RectTextButton tempObject = buttonList.get(i);
			buttonName += tempObject.getText() + " / ";
		}
		return buttonName;
	}
}
