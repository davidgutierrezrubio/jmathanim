/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderers;

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
    private final int width;
    private final int height;

    public Java2DRenderer(Properties cnf) {
        width=Integer.parseInt(cnf.getProperty("WIDTH"));
        height=Integer.parseInt(cnf.getProperty("HEIGHT"));
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
    public void drawArc() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawPolygon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawCircle(int x, int y, int radius) {
        g2d.setColor(color);
        g2d.drawOval(x, y, radius, radius);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        g2d.setColor(color);
        g2d.drawLine(x1, y1, x2, y2);
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

}
