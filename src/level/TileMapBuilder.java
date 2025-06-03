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
	
	public void createLevel(SpriteSheet tileSheetName, int[][] tiles, Handler handler) {
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
	
	private void prepTileImgs9x9(SpriteSheet tileSheetName) {
		brickBlock = tileSheetName.grabImageFast(1, 1);
		plainBlock = tileSheetName.grabImageFast(1, 2);
		fadedBlock = tileSheetName.grabImageFast(1, 3);
		pillarBlock = tileSheetName.grabImageFast(2, 1);
		holeEmptyBlock = tileSheetName.grabImageFast(2, 2);
		edgelessBlock = tileSheetName.grabImageFast(2, 3);
		holeBlueBlock = tileSheetName.grabImageFast(3, 1);
		holeRedBlock = tileSheetName.grabImageFast(3, 2);
		holeGreenBlock = tileSheetName.grabImageFast(3, 3);
	}
	
}
