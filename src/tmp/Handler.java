package tmp;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Objects;


public class Handler {
	
	public LinkedList<GameObject> object = new LinkedList<>();
	public LinkedList<Button> buttonList = new LinkedList<>();
	public LinkedList<ImageButton> imageButtonList = new LinkedList<>();
	
	public void tick() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			tempObject.tick();
		}
		for(int i = 0; i < buttonList.size(); i++) {
			Button tempObject = buttonList.get(i);
			tempObject.tick();
		}
	}
	
	public void render(Graphics g) {
		GameObject playerObject = null;

		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() != ID.Player) {
				tempObject.render(g);
			}
			else {
				playerObject = tempObject;
			}
		}
		if(playerObject != null) {
			playerObject.render(g);
		}
	}

	public void renderHigherElements(Graphics g) {
		for(int i = 0; i < buttonList.size(); i++) {
			Button tempObject = buttonList.get(i);
			tempObject.render(g);
		}
	}
	
	public void addObject(GameObject object) {
		this.object.add(object);
	}
	public void removeObject(GameObject object) {
		this.object.remove(object);
	}

	public void addButton(Button button) { this.buttonList.add(button); }
	public void addImageButton(ImageButton button) {
		this.buttonList.add(button);
		this.imageButtonList.add(button);
	}
	public void removeButton(Button button) { this.buttonList.remove(button); }
	public void removeImageButton(ImageButton button) {
		this.buttonList.remove(button);
		this.imageButtonList.remove(button);
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
		while(areImageButtons()) {
			imageButtonList.pop();
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

	public boolean areImageButtons() {
		boolean foundButton = false;
		if(imageButtonList.size() > 0) {
			foundButton = true;
		}
		return foundButton;
	}

	public Button getButtonAtLocation(int mx, int my) {
		for(int i = 0; i < buttonList.size(); i++) {
			Button tempObject = buttonList.get(i);
			if(Game.isPointInBounds(mx, my, tempObject.getX(), tempObject.getY(), tempObject.getWidth(), tempObject.getHeight())) {
				return tempObject;
			}
		}
		return null;
	}

	public ImageButton getImageButtonByName(String name) {
		for(int i = 0; i < imageButtonList.size(); i++) {
			ImageButton tempObject = imageButtonList.get(i);
			if(Objects.equals(tempObject.getName(), name)) {
				return tempObject;
			}
		}
		return null;
	}

	public String getButtonNames() {
		String buttonName = "";
		for(int i = 0; i < buttonList.size(); i++) {
			Button tempObject = buttonList.get(i);
			buttonName += tempObject.getName() + " / ";
		}
		return buttonName;
	}
}
