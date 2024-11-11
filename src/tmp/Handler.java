package tmp;

import java.awt.Graphics;
import java.awt.geom.Area;
import java.util.LinkedList;
import java.util.Objects;


public class Handler {
	
	public LinkedList<GameObject> object = new LinkedList<>();
	public static LinkedList<Button> buttonList = new LinkedList<>();
	public static LinkedList<ImageButton> imageButtonList = new LinkedList<>();
	GameObject playerObject;
	public static float playerX = 0;
	public static float playerY = 0;
	public static Area currentLevelArea = null;
	
	public void tick() {
		if(!Game.paused) {
			for(int i = 0; i < object.size(); i++) {
				GameObject tempObject = object.get(i);
                tempObject.tick();

                //While looping through all objects, retrieve current player cords for easy access
                if (tempObject.getID() == ID.Player) {
                    playerObject = tempObject;
                    playerX = tempObject.getX();
                    playerY = tempObject.getY();
                }
            }
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

	public void addButton(Button button) { buttonList.add(button); }
	public void addImageButton(ImageButton button) {
		buttonList.add(button);
		imageButtonList.add(button);
	}
	public void removeButton(Button button) { buttonList.remove(button); }
	public void removeImageButton(ImageButton button) {
		buttonList.remove(button);
		imageButtonList.remove(button);
	}
	
	public void clearPlayer() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
            if (tempObject.getID() == ID.Player) {
                removeObject(tempObject);
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

	public static void clearButtons() {
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

	public static boolean areButtons() {
		boolean foundButton = false;
		if(!buttonList.isEmpty()) {
			foundButton = true;
		}
		return foundButton;
	}

	public static boolean areImageButtons() {
		boolean foundButton = false;
		if(!imageButtonList.isEmpty()) {
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

	public void findTotalLevelArea() {
        Area combinedLevel = new Area();
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            if (tempObject.getID() == ID.Level) {
                combinedLevel.add(new Area(tempObject.getBounds()));
            }
        }
        currentLevelArea = combinedLevel;
    }
}
