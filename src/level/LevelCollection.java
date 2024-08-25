package level;

//Class to store all level tilemaps as arrays for cleaner retrieval elsewhere
public class LevelCollection {
	public static int[][] getLevel(int num) {
		int[][] level = new int[34][45];
		
		if(num == 1) { //Start of tomb levels
			level = arrayTo2DArray(TombLevels.getLevelOne());
		}
		if(num == 2) {
			level = arrayTo2DArray(TombLevels.getLevelTwo());
		}
		if(num == 3) {
			level = arrayTo2DArray(TombLevels.getLevelThree());
		}
		if(num == 4) {
			level = arrayTo2DArray(TombLevels.getLevelFour());
		}
		if(num == 5) {
			level = arrayTo2DArray(TombLevels.getLevelFive());
		}
		if(num == 6) {
			level = arrayTo2DArray(TombLevels.getLevelSix());
		}
		if(num == 7) { //Start of dungeon levels
			level = arrayTo2DArray(DungeonLevels.getLevelOne());
		}
		if(num == 8) {
			level = arrayTo2DArray(DungeonLevels.getLevelTwo());
		}
		if(num == 9) {
			level = arrayTo2DArray(DungeonLevels.getLevelThree());
		}
		if(num == 10) {
			level = arrayTo2DArray(DungeonLevels.getLevelFour());
		}
		if(num == 11) {
			level = arrayTo2DArray(DungeonLevels.getLevelFive());
		}

		return level;
	}
	
	public static int[][] arrayTo2DArray(int[] levelArray) {
		int[][] level2D = new int[34][45];
		int current = 0;
		
		for (int row = 0; row < level2D.length; row++) { 
			for (int col = 0; col < level2D[row].length; col++) {
				level2D[row][col] = levelArray[current];
				current++;
			}
		}
		
		return level2D;
	}
}
