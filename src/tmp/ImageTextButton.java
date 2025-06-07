package tmp;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageTextButton extends Button {
    private Font font;
    private Color color;
    private final BufferedImage image;

    public ImageTextButton(Font font, Color textColor, String name, BufferedImage image, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.font = font;
        this.color = textColor;
        this.image = image;
    }

    public void tick() {

    }

    public void render(Graphics g) {
        FontMetrics metrics = g.getFontMetrics(font);
        int textX = this.getX() + (this.getWidth() - metrics.stringWidth(this.getName())) / 2;
        int textY = this.getY() + ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawImage(image, this.getX(), this.getY(), null);

        g.setColor(color);
        g.setFont(font);
        g.drawString(this.getName(), textX, textY);
    }

    public Font getFont() { return font; }
    public Color getColorOne() { return color; }

    public void setFont(Font font) { this.font = font; }
    public void setColorOne(Color color) { this.color = color; }

}

