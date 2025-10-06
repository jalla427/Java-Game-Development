package tmp;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDoubleTextButton extends Button {
    private Font font;
    private Color color;
    private String textOne;
    private String textTwo;
    private final BufferedImage image;

    public ImageDoubleTextButton(Font font, Color textColor, String name, String textOne, String textTwo, BufferedImage image, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.font = font;
        this.color = textColor;
        this.image = image;
        this.textOne = textOne;
        this.textTwo = textTwo;
    }

    public void tick() {

    }

    public void render(Graphics g) {
        FontMetrics metrics = g.getFontMetrics(font);
        int textX = longestLine(textOne, textTwo, metrics);
        int textY = this.getY() + ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawImage(image, this.getX(), this.getY(), null);

        g.setColor(color);
        g.setFont(font);
        g.drawString(this.getTextOne(), textX, textY - 10);
        g.drawString(this.getTextTwo(), textX, textY + 10);
    }

    private int longestLine(String lineOne, String lineTwo, FontMetrics metrics) {
        int finalLength;

        int lengthOne = this.getX() + (this.getWidth() - metrics.stringWidth(this.getTextOne())) / 2;
        int lengthTwo = this.getX() + (this.getWidth() - metrics.stringWidth(this.getTextTwo())) / 2;

        if(lengthOne <= lengthTwo) { finalLength = lengthOne; }
        else { finalLength = lengthTwo; }

        return finalLength;
    }

    public Font getFont() { return font; }
    public Color getColorOne() { return color; }

    public void setFont(Font font) { this.font = font; }
    public void setColorOne(Color color) { this.color = color; }
    public void setTextOne(String textOne) { this.textOne = textOne; }
    public String getTextOne() { return textOne; }
    public void setTextTwo(String textTwo) { this.textTwo = textTwo; }
    public String getTextTwo() { return textTwo; }

}

