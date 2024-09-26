package tmp;

import java.awt.*;

public class RectTextButton extends Button {
    private Font font;
    private Color colorOne;
    private Color colorTwo;

    public RectTextButton(Handler handler, Font font, Color colorOne, Color colorTwo, String name, int x, int y, int width, int height) {
        super(handler, name, x, y, width, height);
        this.font = font;
        this.colorOne = colorOne;
        this.colorTwo = colorTwo;
    }

    public void tick() {

    }

    public void render(Graphics g) {
        FontMetrics metrics = g.getFontMetrics(font);
        int textX = this.getX() + (this.getWidth() - metrics.stringWidth(this.getName())) / 2;
        int textY = this.getY() + ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setColor(colorOne);
        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        g.setColor(colorTwo);
        g.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        g.setFont(font);
        g.drawString(this.getName(), textX, textY);
    }

    public Font getFont() { return font; }
    public Color getColorOne() { return colorOne; }
    public Color getColorTwo() { return colorTwo; }

    public void setFont(Font font) { this.font = font; }
    public void setColorOne(Color colorOne) { this.colorOne = colorOne; }
    public void setColorTwo(Color colorTwo) { this.colorTwo = colorTwo; }
}

