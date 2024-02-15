package level;

import java.awt.image.BufferedImage;

import tmp.Game;
import tmp.Handler;
import tmp.ID;
import tmp.SpriteSheet;

public class TileMapBuilder {
	
	private Handler handler;
	
	private BufferedImage brickBlock;
	private BufferedImage plainBlock;
	private BufferedImage fadedBlock;
	private BufferedImage pillarBlock;
	private BufferedImage holeEmptyBlock;
	private BufferedImage edgelessBlock;
	private BufferedImage holeBlueBlock;
	private BufferedImage holeRedBlock;
	private BufferedImage holeGreenBlock;
	
	public void createLevel(BufferedImage tileSheetName, int[][] tiles, Handler handler) {
		this.handler = handler;
		
		prepTileImgs9x9(tileSheetName);
		int current;
		
		//Expected array should be [34][45] with a window size of 900/670
		for (int row = 0; row < tiles.length; row++) { 
			for (int col = 0; col < tiles[row].length; col++) { 
				current = tiles[row][col]; 
				if(current == 1) {
					handler.addObject(new Tile(col*20, row*20, 20, 20, ID.Level, handler, brickBlock));
				}
				if(current == 2) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, plainBlock));
				}
				if(current == 3) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, fadedBlock));
				}
				if(current == 4) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, pillarBlock));
				}
				if(current == 5) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, holeEmptyBlock));
				}
				if(current == 6) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, edgelessBlock));
				}
				if(current == 7) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, holeBlueBlock));
				}
				if(current == 8) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, holeRedBlock));
				}
				if(current == 9) {
					handler.addObject(new Tile(col*20, row*20,20, 20, ID.Level, handler, holeGreenBlock));
				}
			} 
		}

	}
	
	private void prepTileImgs9x9(BufferedImage tileSheetName) {
		SpriteSheet ss = new SpriteSheet(tileSheetName);
		brickBlock = ss.grabImage(1, 1, 20, 20);
		plainBlock = ss.grabImage(1, 2, 20, 20);
		fadedBlock = ss.grabImage(1, 3, 20, 20);
		pillarBlock = ss.grabImage(2, 1, 20, 20);
		holeEmptyBlock = ss.grabImage(2, 2, 20, 20);
		edgelessBlock = ss.grabImage(2, 3, 20, 20);
		holeBlueBlock = ss.grabImage(3, 1, 20, 20);
		holeRedBlock = ss.grabImage(3, 2, 20, 20);
		holeGreenBlock = ss.grabImage(3, 3, 20, 20);
	}
	
}
