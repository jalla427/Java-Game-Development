package tmp;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageButton extends Button {
    private BufferedImage image;

    public ImageButton(Handler handler, String name, BufferedImage image, int x, int y, int width, int height) {
        super(handler, name, x, y, width, height);
        this.image = image;
    }

    public void tick() {

    }

    public void render(Graphics g) {
        g.drawImage(image, this.getX(), this.getY(), null);
    }

    public BufferedImage getImage() { return image; }
    public void setImage(BufferedImage image) { this.image = image; }
}
