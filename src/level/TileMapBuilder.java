package level;

import java.awt.image.BufferedImage;

import tmp.Handler;
import tmp.ID;
import tmp.SpriteSheet;

public class TileMapBuilder {
	protected static int tileWidth = 20;
	protected static int tileHeight = 20;
	protected static BufferedImage[] tileTypeList = new BufferedImage[9];
	
	public void createLevel(SpriteSheet tileSheetName, int[][] tiles) {
		prepTileImgs9x9(tileSheetName);
		int current;
		
		//Expected array should be [34][45] with a window size of 900/670
		for (int row = 0; row < tiles.length; row++) { 
			for (int col = 0; col < tiles[row].length; col++) { 
				current = tiles[row][col];
				if(current > 0) { Handler.addObject(new Tile(col*20, row*20, current - 1)); }
			}
		}
	}
	
	private void prepTileImgs9x9(SpriteSheet tileSheetName) {
		tileTypeList[0] = tileSheetName.grabImageFast(1, 1); //brickBlock
		tileTypeList[1] = tileSheetName.grabImageFast(1, 2); //plainBlock
		tileTypeList[2] = tileSheetName.grabImageFast(1, 3); //fadedBlock
		tileTypeList[3] = tileSheetName.grabImageFast(2, 1); //pillarBlock
		tileTypeList[4] = tileSheetName.grabImageFast(2, 2); //holeEmptyBlock
		tileTypeList[5] = tileSheetName.grabImageFast(2, 3); //edgelessBlock
		tileTypeList[6] = tileSheetName.grabImageFast(3, 1); //holeBlueBlock
		tileTypeList[7] = tileSheetName.grabImageFast(3, 2); //holeRedBlock
		tileTypeList[8] = tileSheetName.grabImageFast(3, 3); //holeGreenBlock
	}
	
}
