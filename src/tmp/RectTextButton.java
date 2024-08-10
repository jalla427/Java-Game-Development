package tmp;

import java.awt.*;

public class RectTextButton extends Button {
    private Font font;
    private Color colorOne;
    private Color colorTwo;
    private String text;

    public RectTextButton(Handler handler, Font font, Color colorOne, Color colorTwo, String text, int x, int y, int width, int height) {
        super(handler, x, y, width, height);
        this.font = font;
        this.colorOne = colorOne;
        this.colorTwo = colorTwo;
        this.text = text;
    }

    public void tick() {

    }

    public void render(Graphics g) {
        FontMetrics metrics = g.getFontMetrics(font);
        int textX = this.getX() + (this.getWidth() - metrics.stringWidth(text)) / 2;
        int textY = this.getY() + ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setColor(colorOne);
        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        g.setColor(colorTwo);
        g.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        g.setFont(font);
        g.drawString(text, textX, textY);
    }

    public String getText() { return text; }
    public Font getFont() { return font; }
    public Color getColorOne() { return colorOne; }
    public Color getColorTwo() { return colorTwo; }

    public void setFont(Font font) { this.font = font; }
    public void setColorOne(Color colorOne) { this.colorOne = colorOne; }
    public void setColorTwo(Color colorTwo) { this.colorTwo = colorTwo; }
    public void setText(String text) { this.text = text; }
}

