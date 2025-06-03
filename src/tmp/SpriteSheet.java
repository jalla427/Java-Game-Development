package tmp;

import java.awt.image.BufferedImage;

public class SpriteSheet {
	
	private final BufferedImage sprite;
	private BufferedImage[][] tiles;
	
	public SpriteSheet(BufferedImage ss, int rows, int columns, int frameWidth, int frameHeight) {
		this.sprite = ss;
		defineTiles(rows, columns, frameWidth, frameHeight);
	}

	//Only called externally when height/width need to be specified as something other than what was initially provided
	public BufferedImage grabImage(int row, int col, int width, int height) {
		return this.sprite.getSubimage((col * width) - width, (row * height) - height, width, height);
	}

	private void defineTiles(int rows, int columns, int frameWidth, int frameHeight) {
		tiles = new BufferedImage[columns][rows];
		for(int ir = 0; ir < rows; ir++) {
			for(int ic = 0; ic < columns; ic++) {
				tiles[ic][ir] = grabImage(ir + 1, ic + 1, frameWidth, frameHeight);
			}
		}
	}

	public BufferedImage grabImageFast(int row, int col) {
		return tiles[col - 1][row - 1];
	}
}
