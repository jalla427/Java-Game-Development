package tmp;

import java.awt.*;
import java.util.LinkedList;

public class Button {
    private Handler handler;
    private ID id = ID.Button;
    private Font font;
    private Color colorOne;
    private Color colorTwo;
    private int x;
    private int y;
    private int width;
    private int height;
    private String text;

    public Button(Handler handler, Font font, Color colorOne, Color colorTwo, String text, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.handler = handler;
        this.font = font;
        this.colorOne = colorOne;
        this.colorTwo = colorTwo;
        this.text = text;
        this.width = width;
        this.height = height;
    }

    public void tick() {

    };
    public void render(Graphics g) {
        FontMetrics metrics = g.getFontMetrics(font);
        int textX = (int) x + (width - metrics.stringWidth(text)) / 2;
        int textY = (int) y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setColor(colorOne);
        g.fillRect((int) x, (int) y, width, height);

        g.setColor(colorTwo);
        g.drawRect((int) x, (int) y, width, height);

        g.setFont(font);
        g.drawString(text, textX, textY);
    }

    public Font getFont() { return font; }
    public Color getColorOne() { return colorOne; }
    public Color getColorTwo() { return colorTwo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getText() { return text; }

    public void setFont(Font font) { this.font = font; }
    public void setColorOne(Color colorOne) { this.colorOne = colorOne; }
    public void setColorTwo(Color colorTwo) { this.colorTwo = colorTwo; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height;}
    public void setText(String text) { this.text = text; }
}

