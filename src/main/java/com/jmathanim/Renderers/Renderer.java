/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Renderers;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.logger;
import com.jmathanim.mathobjects.AbstractJMImage;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Coordinates x,y,z are always given in (0,0) to (w,h), where (0,0) is upper
 * left corner.
 *
 * @author David
 */
public abstract class Renderer {

    protected final JMathAnimConfig config;
    protected final JMathAnimScene scene;

    public Renderer(JMathAnimScene parentScene) {
        this.scene = parentScene;
        this.config = parentScene.getConfig();
    }

    abstract public void initialize();

    abstract public <T extends Camera> T getCamera();

    abstract public Camera getFixedCamera();

    /**
     * Returns the width of the media output, in pixels
     *
     * @return the width of media
     */
    public int getMediaWidth() {
        return config.mediaW;
    }

    /**
     * Returns the height of the media output, in pixels
     *
     * @return the height of media
     */
    public int getMediaHeight() {
        return config.mediaH;
    }

    abstract public void saveFrame(int frameCount);

    abstract public void finish(int frameCount);

    abstract protected BufferedImage getRenderedImage(int frameCount);

    public void saveImage() {
        int frameCount = config.getScene().getFrameCount();
        saveImage(config.getOutputFileName() + String.format("%06d", frameCount) + ".png", "png");
    }

    public void saveImage(String filename, String format) {
        int frameCount = config.getScene().getFrameCount();
        BufferedImage renderedImage = getRenderedImage(frameCount);
        writeImageToPNG(filename, renderedImage, format);
        logger.info("Saved image "+filename);
    }

    protected void writeImageToPNG(String filename, BufferedImage renderedImage, String format) {
        try {
            File file = new File(config.getOutputDir().getCanonicalPath() + File.separator + filename);
            ImageIO.write(renderedImage, format, file);
        } catch (IOException ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Clear current renderer, with the background color
     */
    abstract public void clear();

    /**
     * Draws the path of a JMPathObject This method will draw most of the
     * objects in the screen
     *
     * @param mobj The JMPathObject
     */
    abstract public void drawPath(Shape mobj);

    abstract public void drawAbsoluteCopy(Shape sh, Vec anchor);

    abstract public Rect createImage(InputStream stream);

    abstract public void drawImage(AbstractJMImage obj);

    abstract public void debugText(String text, Vec loc);

    abstract public double MathWidthToThickness(double w);

    abstract public double ThicknessToMathWidth(double th);

    abstract public double ThicknessToMathWidth(MathObject obj);

    abstract public void addSound(SoundItem soundItem);
}
