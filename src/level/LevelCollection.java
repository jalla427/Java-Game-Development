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
		if(num == 12) {
			level = arrayTo2DArray(DungeonLevels.getLevelSix());
		}
		if(num == 13) {
			level = arrayTo2DArray(BurningLevels.getLevelOne());
		}
		if(num == 14) {
			level = arrayTo2DArray(BurningLevels.getLevelTwo());
		}
		if(num == 15) {
			level = arrayTo2DArray(BurningLevels.getLevelThree());
		}
		if(num == 16) {
			level = arrayTo2DArray(BurningLevels.getLevelFour());
		}
		if(num == 17) {
			level = arrayTo2DArray(BurningLevels.getLevelFive());
		}
		if(num == 18) {
			level = arrayTo2DArray(BurningLevels.getLevelSix());
		}
		if(num == 19) {
			level = arrayTo2DArray(FinalLevels.getLevelOne());
		}
		if(num == 20) {
			level = arrayTo2DArray(FinalLevels.getLevelTwo());
		}
		if(num == 21) {
			level = arrayTo2DArray(FinalLevels.getLevelThree());
		}
		if(num == 22) {
			level = arrayTo2DArray(FinalLevels.getLevelFour());
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
