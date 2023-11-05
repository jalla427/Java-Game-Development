package tmp;

import java.awt.image.BufferedImage;

public class SpriteSheet {
	
	private final BufferedImage sprite;
	
	public SpriteSheet(BufferedImage ss) {
		this.sprite = ss;
	}
	
	public BufferedImage grabImage(int col, int row, int width, int height) {
		BufferedImage img = sprite.getSubimage((row * width) - width, (col * height) - height, width, height);
		return img;
	}
}
