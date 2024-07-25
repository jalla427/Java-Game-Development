package tmp;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Lighting {
    public static void render(Graphics2D g, Handler handler) {
        Color[] darkness = new Color[5];
        darkness[0] = new Color(0, 0, 0, 0);
        darkness[1] = new Color(0, 0, 0, 25);
        darkness[2] = new Color(0, 0, 0, 50);
        darkness[3] = new Color(0, 0, 0, 75);
        darkness[4] = new Color(0, 0, 0, 100);

        Area[] lightBounds = new Area[5];
        lightBounds[0] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[1] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[2] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[3] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[4] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));

        float luminosity = 0;
        float targetX = 0;
        float targetY = 0;

        //Loop through all objects
        for(int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);
            Shape[] lightCircles = new Shape[5];

            //Apply light to objects with luminosity
            if (tempObject.getLuminosity() > 0) {
                targetX = tempObject.getX() + (tempObject.getWidth()/2);
                targetY = tempObject.getY() + (tempObject.getHeight()/2);
                luminosity = tempObject.getLuminosity();
                float variation = (float) Math.random();

                //Create gradient circles
                for(int ii = 0; ii < 5; ii++) {
                    double lightMod = ((ii + 1.0) / 5.0);
                    Shape lightCircle = new Ellipse2D.Double((targetX - ((luminosity / 2) * lightMod)), (targetY - ((luminosity / 2) * lightMod)) + variation, luminosity * lightMod, (luminosity + variation) * lightMod);
                    lightCircles[ii] = lightCircle;

                    Area lightArea = new Area(lightCircles[ii]);
                    lightBounds[ii].subtract(lightArea);
                }
            }
        }

        //Apply darkness
        for(int i = 4; i >= 0; i--) {
            g.setColor(darkness[i]);
            g.fill(lightBounds[i]);
        }
    }
}
