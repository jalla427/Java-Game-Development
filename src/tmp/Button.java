package tmp;

import java.awt.*;

public abstract class Button {
    private Handler handler;
    private ID id = ID.Button;
    private String name;
    private int x;
    private int y;
    private int width;
    private int height;

    public Button(Handler handler, String name, int x, int y, int width, int height) {
        this.handler = handler;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void tick();

    public abstract void render(Graphics g);

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setName(String name) { this.name = name; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height;}

}
