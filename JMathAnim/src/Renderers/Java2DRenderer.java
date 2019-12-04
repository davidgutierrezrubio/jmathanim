/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Java2DRenderer extends Renderer {

    private final BufferedImage bufferedImage;
    private final Graphics2D g2d;

    public Java2DRenderer(Properties cnf) {
        int w = Integer.parseInt(cnf.getProperty("WIDTH"));
        int h = Integer.parseInt(cnf.getProperty("HEIGHT"));
        setSize(w, h);

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = bufferedImage.createGraphics();
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setRenderingHints(rh);
    }

    @Override
    public void drawPolygon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawCircle(double x, double y, double radius) {
        g2d.setColor(color);
        int[] screenx = camera.mathToScreen(x, y);
        int screenRadius = camera.mathToScreen(radius);
        g2d.drawOval(screenx[0], screenx[1], screenRadius, screenRadius);
    }

    @Override
    public void drawLine(double x1, double y1, double x2, double y2) {
        g2d.setColor(color);
        int[] screenx1 = camera.mathToScreen(x1, y1);
        int[] screenx2 = camera.mathToScreen(x2, y2);
        g2d.drawLine(screenx1[0], screenx1[1], screenx2[0], screenx2[1]);
    }

    @Override
    public void saveFrame(int frameCount) {
        String fname = "c:\\media\\screen-" + String.format("%05d", frameCount) + ".png";
        File file = new File(fname);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException ex) {
            Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void drawArc(double x, double y, double radius, double angle) {
    }

    @Override
    public void clear() {
        g2d.setColor(Color.BLACK);//TODO: Poner en opciones
        g2d.fillRect(0, 0, width, height);
    }

}
