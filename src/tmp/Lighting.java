package tmp;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Lighting {
    public static void render(Graphics2D g, Handler handler) {
        Color darkness = new Color(0, 0, 0, 150);
        Area darknessBounds = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        float luminosity = 0;
        float targetX = 0;
        float targetY = 0;

        //Loop through all objects in search of tiles
        for(int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);

            //Check for collision with tiles
            if (tempObject.getID() == ID.Player || tempObject.getID() == ID.Enemy || tempObject.getID() == ID.Coin) {
                targetX = tempObject.getX() + (tempObject.getWidth()/2);
                targetY = tempObject.getY() + (tempObject.getHeight()/2);
                luminosity = tempObject.getLuminosity();
                float variation = (float) Math.random();

                Shape lightCircle = new Ellipse2D.Double(targetX - (luminosity/2), targetY - (luminosity/2) + variation, luminosity, luminosity + variation);
                Area lightArea = new Area(lightCircle);

                darknessBounds.subtract(lightArea);
            }
        }

        g.setColor(darkness);
        g.fill(darknessBounds);
    }
}
