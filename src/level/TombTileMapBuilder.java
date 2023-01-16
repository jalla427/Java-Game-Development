package level;

import java.awt.image.BufferedImage;

import tmp.Game;
import tmp.Handler;
import tmp.ID;
import tmp.SpriteSheet;

public class TombTileMapBuilder {
	
	private Handler handler;
	
	private BufferedImage tombBrickBlock;
	private BufferedImage tombPlainBlock;
	private BufferedImage tombFadedBlock;
	private BufferedImage tombPillarBlock;
	private BufferedImage tombHoleEmptyBlock;
	private BufferedImage tombEdgelessBlock;
	private BufferedImage tombHoleBlueBlock;
	private BufferedImage tombHoleRedBlock;
	private BufferedImage tombHoleGreenBlock;
	
	public void createTombLevel(int[][] tiles, Handler handler) {
		this.handler = handler;
		
		prepTombImgs();
		int current;
		
		//Expected array should be [34][45] with a window size of 900/670
		for (int row = 0; row < tiles.length; row++) { 
			for (int col = 0; col < tiles[row].length; col++) { 
				current = tiles[row][col]; 
				if(current == 1) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombBrickBlock));
				}
				if(current == 2) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombPlainBlock));
				}
				if(current == 3) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombFadedBlock));
				}
				if(current == 4) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombPillarBlock));
				}
				if(current == 5) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombHoleEmptyBlock));
				}
				if(current == 6) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombEdgelessBlock));
				}
				if(current == 7) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombHoleBlueBlock));
				}
				if(current == 8) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombHoleRedBlock));
				}
				if(current == 9) {
					handler.addObject(new Tile(col*20, row*20, ID.Level, handler, tombHoleGreenBlock));
				}
			} 
		}

	}
	
	private void prepTombImgs() {
		SpriteSheet ss = new SpriteSheet(Game.tomb_blocks_20x20);
		tombBrickBlock = ss.grabImage(1, 1, 20, 20);
		tombPlainBlock = ss.grabImage(1, 2, 20, 20);
		tombFadedBlock = ss.grabImage(1, 3, 20, 20);
		tombPillarBlock = ss.grabImage(2, 1, 20, 20);
		tombHoleEmptyBlock = ss.grabImage(2, 2, 20, 20);
		tombEdgelessBlock = ss.grabImage(2, 3, 20, 20);
		tombHoleBlueBlock = ss.grabImage(3, 1, 20, 20);
		tombHoleRedBlock = ss.grabImage(3, 2, 20, 20);
		tombHoleGreenBlock = ss.grabImage(3, 3, 20, 20);
	}
	
}
