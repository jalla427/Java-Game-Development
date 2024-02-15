package tmp;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Lighting {
    public static void render(Graphics2D g) {
        Color darkness = new Color(0, 0, 0, 0);
        Area darknessBounds = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));

        g.setColor(darkness);
        g.fill(darknessBounds);
    }
}
