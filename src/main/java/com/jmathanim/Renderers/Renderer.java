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
import com.jmathanim.MathObjects.AbstractJMImage;
import com.jmathanim.MathObjects.AbstractShape;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.SVGExport;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jmathanim.jmathanim.JMathAnimScene.logger;

/**
 * Coordinates x,y,z are always given in (0,0) to (w,h), where (0,0) is upper
 * left corner.
 *
 * @author David
 */
public abstract class Renderer {

    protected final JMathAnimConfig config;
    protected final JMathAnimScene scene;

    private final Vec nullVector=Vec.to(0,0);

    public Renderer(JMathAnimScene parentScene) {
        this.scene = parentScene;
        this.config = parentScene.getConfig();
        config.setRenderer(this);
    }

    abstract public RendererEffects buildRendererEffects();
    
    abstract public void initialize();

    abstract public <T extends Camera> T getCamera();

    abstract public <T extends Camera> T getFixedCamera();

    /**
     * Returns the width of the media output, in pixels
     *
     * @return the width of media
     */
    public int getMediaWidth() {
        return config.getMediaWidth();
    }

    /**
     * Returns the height of the media output, in pixels
     *
     * @return the height of media
     */
    public int getMediaHeight() {
        return config.getMediaHeight();
    }

    /**
     * Saves the generated frame to movie or a set of still images, according to
     * config
     *
     * @param frameCount Frame number
     */
    abstract public void saveFrame(int frameCount);

    /**
     * Finish the render queue. Close the video stream if necessary and perform
     * other cleaning operations
     *
     * @param frameCount Frame number
     */
    abstract public void finish(int frameCount);

    /**
     * Gets the rendered frame
     *
     * @param frameCount Frame number. Neeed for superimposing it and in case of
     * saving a screenshot
     * @return A BufferedImage object with the drawn frame.
     */
    abstract protected BufferedImage getRenderedImage(int frameCount);

    public void saveImage() {
        int frameCount = config.getScene().getFrameCount();
        saveImage(config.getOutputFileName() + String.format("%06d", frameCount) + ".png", "png");
    }
    /**
     * Writes the current rendered image to a file in the specified image format.
     * <p>
     * This method handles both raster formats (e.g., PNG, JPG) and delegates
     * to a specialized method for vector formats (SVG).
     *
     * @param filename The name of the file (including the extension) where the image will be saved.
     * The file is stored within the configured output directory ({@code config.getOutputDir()}).
     * @param format The output image format, such as "png", "jpg", or "gif".
     * If "svg" is specified, the method delegates the saving process to
     * the {@code saveSVGImage} method.
     */
    public void saveImage(String filename, String format) {
       if (format.equals("svg")) {
           saveSVGImage(filename,true);
           return;
       }
       //Another bitmap format (usually png)
        int frameCount = config.getScene().getFrameCount();
        BufferedImage renderedImage = getRenderedImage(frameCount);
        writeImageToPNG(filename, renderedImage, format);
    }

    protected void writeImageToPNG(String filename, BufferedImage renderedImage, String format) {
        try {
            File file = new File(config.getOutputDir().getCanonicalPath() + File.separator + filename);
            ImageIO.write(renderedImage, format, file);
            logger.info("Saved image " + LogUtils.fileName(file.getPath()));
        } catch (IOException ex) {
            logger.error("Error saving png image "+filename);
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Saves the current view of the scene to an SVG file.
     * The SVG viewport (its coordinate system) is determined based on the
     * value of {@code useMathView}.
     * * @param filename The name of the SVG file (including the extension) to save the image to.
     * It is saved within the configured output directory ({@code config.getOutputDir()}).
     * @param useMathView If {@code true}, the SVG's bounding box is determined by
     * the limits of the current mathematical view. If {@code false},
     * the bounding box will be automatically computed to tightly fit all
     * visible objects in the scene.
     */
    public void saveSVGImage(String filename, boolean useMathView) {
        try {
            File file = new File(config.getOutputDir().getCanonicalPath() + File.separator + filename);
            SVGExport svgExport = new SVGExport(config.getScene());
            svgExport.setUseMathView(useMathView);
            String svgCode = svgExport.getSVGCode();
            PrintWriter pw = new PrintWriter(file);
            pw.print(svgCode);
            pw.close();
//            SVGUtils.saveSVGFile(scene,file);
            logger.info("Saved image " + LogUtils.fileName(file.getPath()));
        } catch (IOException ex) {
            logger.error("Error saving svg image "+filename);
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Clear current renderer, with the background color
     */
    public void clearAndPrepareCanvasForAnotherFrame() {
//        for (MathObject<?> obj: scene.getMathObjects()) {
//            DebugTools.setHasBeenUpdated(obj,false);
//        }
    };

    /**
     * Draws the path of a JMPathObject This method will draw most of the
     * objects in the screen
     *
     * @param mobj The JMPathObject
     */
    abstract protected void drawPath(AbstractShape<?> mobj);

    public void drawPath(AbstractShape<?> mobj, Camera camera) {
        drawPath(mobj, nullVector, camera);
    };
    abstract public void drawPath(AbstractShape<?> mobj, Vec shiftVector, Camera camera);

    abstract public void drawAbsoluteCopy(AbstractShape<?> sh, Vec anchor);

    abstract public Rect createImage(InputStream stream);

    abstract public void drawImage(AbstractJMImage<?> obj,Camera cam);

    abstract public void debugText(String text, Vec loc);

    abstract public double MathWidthToThickness(double w);

    abstract public double ThicknessToMathWidth(double th);

    abstract public double ThicknessToMathWidth(MathObject<?> obj);

    abstract public void addSound(SoundItem soundItem);
}
