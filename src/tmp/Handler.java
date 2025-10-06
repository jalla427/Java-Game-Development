package tmp;

import enemy.Bullet;

import java.awt.Graphics;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;


public class Handler {
	public static ArrayList<GameObject> object = new ArrayList<>(500);
	public static ArrayList<GameObject> enemyList = new ArrayList<>(50);
	public static ArrayList<Bullet> bulletAddList = new ArrayList<>(10);
	public static ArrayList<Bullet> bulletRemoveList = new ArrayList<>(10);
	public static LinkedList<Bullet> bulletReserveList = new LinkedList<>();
	public static ArrayList<Button> buttonList = new ArrayList<>(30);
	public static ArrayList<ImageButton> imageButtonList = new ArrayList<>(30);
	public static Player playerObject = null;
	public static float playerX = 0;
	public static float playerY = 0;
	public static Area currentLevelArea = null;
	
	public static void tick() {
		if(!Game.paused) {
			for(int i = 0; i < object.size(); i++) {
				GameObject tempObject = object.get(i);
                tempObject.tick();
            }

			//Tick enemies
			//Newly generated bullets are added to enemy list, removes oob bullets
			enemyList.removeAll(bulletRemoveList);
			bulletReserveList.addAll(bulletRemoveList);
			bulletRemoveList.clear();
			enemyList.addAll(bulletAddList);
			bulletAddList.clear();
			enemyList.parallelStream().forEach(GameObject::tick);

			//Retrieve current player cords for easy access
			if(playerObject != null) {
				playerObject.tick();
				playerX = playerObject.getX();
				playerY = playerObject.getY();
			}
		}

		for(int i = 0; i < buttonList.size(); i++) {
			try {
				Button tempObject = buttonList.get(i);
				tempObject.tick();
			}
			catch(java.lang.NullPointerException e) {
				System.out.println("Error: Failed to tick button, skipping");
			}
        }
	}
	
	public static void render(Graphics g) {
		object.parallelStream().forEach(obj -> obj.render(g));
		for(int i = 0; i < enemyList.size(); i++) {
			GameObject tempEnemyObject = enemyList.get(i);
			tempEnemyObject.render(g);
		}
		if(playerObject != null) {
			playerObject.render(g);
		}
	}

	public static void renderHigherElements(Graphics g) {
		for(int i = 0; i < buttonList.size(); i++) {
			try {
				Button tempObject = buttonList.get(i);
				tempObject.render(g);
			} catch(java.lang.NullPointerException e)	{
				System.out.println("Error: Failed to render element, skipping");
			}
		}
	}
	
	public static void addObject(GameObject object) {
		Handler.object.add(object);
	}
	public static void removeObject(GameObject object) {
		Handler.object.remove(object);
	}
	public static void addEnemy(GameObject object) {
		Handler.enemyList.add(object);
	}
	public static void removeEnemy(GameObject object) {
		Handler.enemyList.remove(object);
	}

	public static void addBullet(float x, float y, float targetX, float targetY, float speed, boolean homing, int sprite) {
		if(!bulletReserveList.isEmpty()) {
			Bullet tempBullet = bulletReserveList.getFirst();
			bulletReserveList.remove(tempBullet);

			tempBullet.setX(x);
			tempBullet.setY(y);
			tempBullet.setTargetX(targetX);
			tempBullet.setTargetY(targetY);
			tempBullet.setBulletSpeed(speed);
			tempBullet.setHoming(homing);
			tempBullet.setSprite(sprite);
			tempBullet.refreshSpeeds();

			bulletAddList.add(tempBullet);
			tempBullet.setActive(true);
		}
	}

	public static void removeBullet(Bullet bullet) {
		bullet.setActive(false);
		bullet.setX(0);
		bullet.setY(0);
		bulletReserveList.add(bullet);
		enemyList.remove(bullet);
	}

	public static void initializeBulletReserve() {
		while(!bulletReserveList.isEmpty()) {
			bulletReserveList.pop();
		}
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
		bulletReserveList.add(new Bullet(0, 0, 16, 16, ID.Enemy, 0, 0, 0, false, 3, false));
	}

	public static void addButton(Button button) { buttonList.add(button); }
	public static void addImageButton(ImageButton button) {
		buttonList.add(button);
		imageButtonList.add(button);
	}
	public void removeButton(Button button) { buttonList.remove(button); }
	public void removeImageButton(ImageButton button) {
		buttonList.remove(button);
		imageButtonList.remove(button);
	}
	
	public static void clearPlayer() {
		Handler.playerObject = null;
	}
	
	public void clearTiles() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Level) {
				removeObject(object.get(i));
			}
		}
	}
	
	public static void clearEnemies() {
		while(areEnemies()) {
			for(int i = 0; i < enemyList.size(); i++) {
				GameObject tempObject = enemyList.get(i);
				if (tempObject.getID() == ID.Enemy) {
					Handler.removeEnemy(enemyList.get(i));
				}
			}
		}
	}

	public static void clearItems() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Coin || tempObject.getID() == ID.Orb) {
				removeObject(object.get(i));
			}
		}
	}
	
	public static void clearLevel() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Level || tempObject.getID() == ID.Coin || tempObject.getID() == ID.Orb) {
				removeObject(object.get(i));
			}
		}
		clearEnemies();
		clearPlayer();
	}

	public static void clearButtons() {
		while(areButtons()) {
			buttonList.remove(0);
		}
		while(areImageButtons()) {
			imageButtonList.remove(0);
		}
	}
	
	public boolean arePlayers() {
		if(playerObject != null) {
			return true;
		} else {
			return false;
		}
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
	
	public static boolean areEnemies() {
		if(!Handler.enemyList.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean areCoins() {
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

	public boolean areOrbs() {
		boolean foundOrb = false;
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getID() == ID.Orb) {
				foundOrb = true;
				break;
			}
		}
		return foundOrb;
	}
	
	public static boolean areLevel() {
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

	public static Button getButtonAtLocation(int mx, int my) {
		for(int i = 0; i < buttonList.size(); i++) {
			Button tempObject = buttonList.get(i);
			if(Game.isPointInBounds(mx, my, tempObject.getX(), tempObject.getY(), tempObject.getWidth(), tempObject.getHeight())) {
				return tempObject;
			}
		}
		return null;
	}

	public static ImageButton getImageButtonByName(String name) {
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

	public static void findTotalLevelArea() {
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
