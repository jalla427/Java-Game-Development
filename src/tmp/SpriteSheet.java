package tmp;

import java.awt.image.BufferedImage;

public class SpriteSheet {
	
	private final BufferedImage sprite;
	
	public SpriteSheet(BufferedImage ss) {
		this.sprite = ss;
	}
	
	public BufferedImage grabImage(int row, int col, int width, int height) {
		BufferedImage img = sprite.getSubimage((col * width) - width, (row * height) - height, width, height);
		return img;
	}
}
