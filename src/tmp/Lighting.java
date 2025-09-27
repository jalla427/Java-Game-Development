package tmp;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Lighting {
    private static Color[] darkness = new Color[5];
    private static Area[] lightBounds = new Area[5];

    public static void render(Graphics2D g) {
        Color[] darkness = new Color[5];
        darkness[0] = new Color(0, 0, 0, 0);
        darkness[1] = new Color(0, 0, 0, 25);
        darkness[2] = new Color(0, 0, 0, 50);
        darkness[3] = new Color(0, 0, 0, 75);

        //Account for dark mode
        if(Game.darkMode) {
            darkness[4] = new Color(0, 0, 0, 255);
        } else {
            darkness[4] = new Color(0, 0, 0, 100);
        }

        lightBounds[0] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[1] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[2] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[3] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));
        lightBounds[4] = new Area(new Rectangle.Double(0, 0, Game.sWidth, Game.sHeight));

        double[] gradientDistances = new double[]{0.5, 1, 1.75, 3, 5};

        //Loop through all objects
        for(int i = 0; i < Handler.object.size(); i++) {
            applyLighting(Handler.object.get(i), gradientDistances);
        }
        for(int i = 0; i < Handler.enemyList.size(); i++) {
            applyLighting(Handler.enemyList.get(i), gradientDistances);
        }
        applyLighting(Handler.playerObject, gradientDistances);

        //Apply darkness
        for(int i = 4; i >= 0; i--) {
            g.setColor(darkness[i]);
            g.fill(lightBounds[i]);
        }
    }

    private static void applyLighting(GameObject obj, double[] gradientDistances) {
        float luminosity = 0;
        float targetX = 0;
        float targetY = 0;
        Shape[] lightCircles = new Shape[5];

        //Apply light to objects with luminosity
        if (obj.getLuminosity() > 0) {
            targetX = obj.getX() + (obj.getWidth()/2);
            targetY = obj.getY() + (obj.getHeight()/2);
            luminosity = obj.getLuminosity();
            float variation = (float) Math.random() * 2;

            //Create gradient circles
            for(int ii = 0; ii < 5; ii++) {
                double lightMod = (gradientDistances[ii] / 5.0);
                Shape lightCircle = new Ellipse2D.Double((targetX - (((luminosity + variation) * lightMod) / 2)), (targetY - (((luminosity + variation) * lightMod) / 2)), (luminosity + variation) * lightMod, (luminosity + variation) * lightMod);
                lightCircles[ii] = lightCircle;

                Area lightArea = new Area(lightCircles[ii]);
                lightBounds[ii].subtract(lightArea);
            }
        }
    }
}
