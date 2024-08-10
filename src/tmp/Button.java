package tmp;

import java.awt.*;

public abstract class Button {
    private Handler handler;
    private ID id = ID.Button;
    private int x;
    private int y;
    private int width;
    private int height;

    public Button(Handler handler, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.handler = handler;
        this.width = width;
        this.height = height;
    }

    public abstract void tick();

    public abstract void render(Graphics g);

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height;}

}
