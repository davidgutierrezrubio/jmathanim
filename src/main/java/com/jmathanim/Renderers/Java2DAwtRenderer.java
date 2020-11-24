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
import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Renderers.MovieEncoders.VideoEncoder;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.PreviewWindow;
import com.jmathanim.mathobjects.JMImage;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * This class uses Java2D to render the image. This class is deprecated. Use
 * JavaFXRenderer instead.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Java2DAwtRenderer extends Renderer {

    private static final boolean DEBUG_LABELS = false; //Draw label objects
    private static final boolean DEBUG_PATH_POINTS = false; //Draw control points and vertices
    private static final boolean PRINT_DEBUG = false; //Draw control points and vertices
    private static final boolean BOUNDING_BOX_DEBUG = false; //Draw bounding boxes

    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;

    private BufferedImage drawBufferImage;
    private BufferedImage finalImage;
    private BufferedImage debugImage;
    private Graphics2D g2draw;
    private Graphics2D g2debug;
    private final Graphics2D g2dFinalImage;
    public Camera2D camera;
    public Camera2D fixedCamera;

    private VideoEncoder videoEncoder;
    protected Path2D.Double path;

    private File saveFilePath;
    //To limit fps in preview window
    double interpolation = 0;
    final int TICKS_PER_SECOND = 5;
    final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    final int MAX_FRAMESKIP = 5;
    private Image img;
    private PreviewWindow previewWindow;
    private final RenderingHints rh;
    private ConvolveOp ConvolveShadowOp;
    private int scaleBufferedImage = 1;

    public Java2DAwtRenderer(JMathAnimScene parentScene) {
        super(parentScene);
        //Main Camera
        camera = new Camera2D(parentScene,cnf.mediaW * scaleBufferedImage, cnf.mediaH * scaleBufferedImage);
        //The Fixed camera it is not intended to change. It is used to display fixed-size objects
        //like heads of arrows, dot symbols or text
        fixedCamera = new Camera2D(parentScene,cnf.mediaW * scaleBufferedImage, cnf.mediaH * scaleBufferedImage);

        fixedCamera.setMathXY(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        camera.setMathXY(XMIN_DEFAULT, XMAX_DEFAULT, 0);

        drawBufferImage = new BufferedImage(cnf.mediaW * scaleBufferedImage, cnf.mediaH * scaleBufferedImage, BufferedImage.TYPE_INT_ARGB);
        finalImage = new BufferedImage(cnf.mediaW, cnf.mediaH, BufferedImage.TYPE_INT_RGB);
        debugImage = new BufferedImage(cnf.mediaW * scaleBufferedImage, cnf.mediaH * scaleBufferedImage, BufferedImage.TYPE_INT_ARGB);
        g2draw = drawBufferImage.createGraphics();
        g2debug = debugImage.createGraphics();
        g2dFinalImage = finalImage.createGraphics();
        rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2draw.setRenderingHints(rh);
        g2dFinalImage.setRenderingHints(rh);
        prepareEncoder();

        //Proofs
        if (cnf.getBackGroundImage() != null) {
            img = Toolkit.getDefaultToolkit().getImage(cnf.getBackGroundImage());
        }
//        This tracker waits for image to be fully loaded
        MediaTracker tracker = new MediaTracker(new JLabel());
        tracker.addImage(img, 1);
        try {
            tracker.waitForID(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(Java2DAwtRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void prepareEncoder() {

        JMathAnimScene.logger.info("Preparing encoder");

        if (cnf.isShowPreview()) {
            JMathAnimScene.logger.debug("Creating preview window");
            previewWindow = new PreviewWindow(this);
            previewWindow.buildGUI();

            try {
                SwingUtilities.invokeAndWait(() -> {
                    previewWindow.setVisible(true);
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(Java2DAwtRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (cnf.isCreateMovie()) {
//            videoEncoder=new JCodecVideoEncoder();
            videoEncoder = new XugglerVideoEncoder();
//            videoEncoder=new HumbleVideoEncoder();
            try {
                File tempPath = new File(cnf.getOutputDir().getCanonicalPath());
                tempPath.mkdirs();
                saveFilePath = new File(cnf.getOutputDir().getCanonicalPath() + File.separator + cnf.getOutputFileName() + "_" + cnf.mediaH + ".mp4");
                JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
//                muxer = Muxer.make(saveFilePath.getCanonicalPath(), null, "mp4");
                videoEncoder.createEncoder(saveFilePath, cnf);
            } catch (IOException ex) {
                Logger.getLogger(Java2DAwtRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (cnf.drawShadow) {
                computeShadowKernel();
            }

        }
    }

    @Override
    public void saveFrame(int frameCount) {
        //Draw all layers into finalimage
        if (cnf.drawShadow) {
            BufferedImage shadowImage = computeShadow(drawBufferImage);
            g2dFinalImage.drawImage(shadowImage, cnf.shadowOffsetX, cnf.shadowOffsetY, null);

        }

        //Draw the objects drawn into the drawBufferImage
        AffineTransform trBackup = g2dFinalImage.getTransform();

        if (scaleBufferedImage > 1) {//Scaled draw

            AffineTransform scaleTransformToFinal = AffineTransform.getScaleInstance(1d / scaleBufferedImage, 1d / scaleBufferedImage);
//            AffineTransformOp afop = new AffineTransformOp(tr, AffineTransformOp.TYPE_BILINEAR);
            g2dFinalImage.setTransform(scaleTransformToFinal);
//            BufferedImage draw=new BufferedImage(cnf.mediaW , cnf.mediaH, BufferedImage.TYPE_INT_ARGB);
//            draw=afop.filter(drawBufferImage, draw);
//            g2dFinalImage.drawImage(draw, 0, 0, null);
            g2dFinalImage.drawImage(drawBufferImage, 0, 0, null);
            //This layer is on top of everything, for debugging purposes
            g2dFinalImage.drawImage(debugImage, 0, 0, null);
            g2dFinalImage.setTransform(trBackup);

        } else {//Normal draw

            g2dFinalImage.drawImage(drawBufferImage, 0, 0, null);
            //This layer is on top of everything, for debugging purposes

            g2dFinalImage.drawImage(debugImage, 0, 0, null);
        }

        if (cnf.isShowPreview()) {

            //Draw into a window
            try {
                SwingUtilities.invokeAndWait(() -> {
                    Graphics gr = previewWindow.drawPanel.getGraphics();
                    gr.drawImage(finalImage, 0, 0, null);
                    long timeElapsedInNanoSeconds = scene.nanoTime - scene.previousNanoTime;
                    long fpsComputed;
                    if (timeElapsedInNanoSeconds > 0) {
                        fpsComputed = 1000000000 / (timeElapsedInNanoSeconds);
                    } else {
                        fpsComputed = 0;
                    }

                    String statusText = String.format("frame=%d   t=%.2fs    fps=%d", frameCount, (1f * frameCount) / cnf.fps, fpsComputed);
                    previewWindow.statusLabel.setText(statusText);

                    if (cnf.delay) {
                        double tiempo = (1.d / cnf.fps) * 1000;
                        try {
                            long tiempoPasado = timeElapsedInNanoSeconds / 1000000;
                            long delay = (long) (tiempo - tiempoPasado);
                            if (delay > 0) {
                                Thread.sleep(delay);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Java2DAwtRenderer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
            }
            while (previewWindow.pauseToggleButton.isSelected()) {
                try {
                    Thread.sleep(100);//TODO: Improve this
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java2DAwtRenderer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        if (cnf.isCreateMovie()) {
            videoEncoder.writeFrame(finalImage, frameCount);
        }
    }

    @Override
    public void finish(int frameCount
    ) {
        JMathAnimScene.logger.info(String.format("%d frames created, %.2fs total time", frameCount, (1.f * frameCount) / cnf.fps));
        if (cnf.isCreateMovie()) {
            /**
             * Encoders, like decoders, sometimes cache pictures so it can do
             * the right key-frame optimizations. So, they need to be flushed as
             * well. As with the decoders, the convention is to pass in a null
             * input until the output is not complete.
             */
            JMathAnimScene.logger.info("Finishing movie...");
            videoEncoder.finish();
            JMathAnimScene.logger.info("Movie created at " + saveFilePath);

        }
        if (cnf.isShowPreview()) {
            previewWindow.setVisible(false);
            previewWindow.dispose();
        }

    }

    @Override
    public void clear() {
        g2dFinalImage.setColor(scene.getConfig().getBackgroundColor().getAwtColor());
        g2dFinalImage.fillRect(0, 0, cnf.mediaW, cnf.mediaH);

        //Draw background image, if any
        if (img != null) {
//            drawScaledImage(img, g2dFinalImage);
            g2dFinalImage.drawImage(img, 0, 0, null);
        }
        drawBufferImage = new BufferedImage(cnf.mediaW * scaleBufferedImage, cnf.mediaH * scaleBufferedImage, BufferedImage.TYPE_INT_ARGB);
        debugImage = new BufferedImage(cnf.mediaW * scaleBufferedImage, cnf.mediaH * scaleBufferedImage, BufferedImage.TYPE_INT_ARGB);
        g2draw = drawBufferImage.createGraphics();
        g2debug = debugImage.createGraphics();
        g2draw.setRenderingHints(rh);

    }

    public void computeShadowKernel() {
        int dimension = cnf.shadowKernelSize;
        if (dimension > 0) {
            float valor = 1f / (dimension * dimension);
            float[] k = new float[dimension * dimension];
            for (int i = 0; i < dimension * dimension; i++) {
                k[i] = valor;
            }

            Kernel kernel = new Kernel(dimension, dimension, k);
            ConvolveShadowOp = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, rh);
        }
    }

    public BufferedImage computeShadow(BufferedImage img) {
        BufferedImage resul = new BufferedImage(cnf.mediaW * scaleBufferedImage, cnf.mediaH * scaleBufferedImage, BufferedImage.TRANSLUCENT);
////        ColorModel cm = img.getColorModel();
////        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
////        WritableRaster raster = img.copyData(null);
////        resul = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
//
//        int[] imagePixels = img.getRGB(0, 0, width, height, null, 0, width);
//        for (int i = 0; i < imagePixels.length; i++) {
//            int color = imagePixels[i];// & 0xff000000;
//            color = (int) ((color >> 56) * cnf.shadowAlpha) << 56;//TODO: Check this
//
//            imagePixels[i] = color;
//        }
//
//        resul.setRGB(0, 0, width, height, imagePixels, 0, width);
////        if (ConvolveShadowOp != null) {
////            resul = ConvolveShadowOp.filter(resul, null);
////        }
//        ShadowFilter fil = new ShadowFilter(cnf.shadowKernelSize, cnf.shadowOffsetX, cnf.shadowOffsetY, cnf.shadowAlpha);
//        fil.setShadowOnly(true);
////        GaussianFilter fil = new GaussianFilter(cnf.shadowKernelSize);
//        resul = fil.filter(img, null);
        return resul;
    }

    public void drawScaledImage(Image image, Graphics g) {
        int imgWidth = image.getWidth(null);
        int imgHeight = image.getHeight(null);

        double imgAspect = (double) imgHeight / imgWidth;

        int canvasWidth = cnf.mediaW * scaleBufferedImage;
        int canvasHeight = cnf.mediaH * scaleBufferedImage;

        double canvasAspect = (double) canvasHeight / canvasWidth;

        int x1 = 0; // top left X position
        int y1 = 0; // top left Y position
        int x2 = 0; // bottom right X position
        int y2 = 0; // bottom right Y position

        if (imgWidth < canvasWidth && imgHeight < canvasHeight) {
            // the image is smaller than the canvas
            x1 = (canvasWidth - imgWidth) / 2;
            y1 = (canvasHeight - imgHeight) / 2;
            x2 = imgWidth + x1;
            y2 = imgHeight + y1;
            g.drawImage(image, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
        } else {
            if (canvasAspect > imgAspect) {
                y1 = canvasHeight;
                // keep image aspect ratio
                canvasHeight = (int) (canvasWidth * imgAspect);
                y1 = (y1 - canvasHeight) / 2;
            } else {
                x1 = canvasWidth;
                // keep image aspect ratio
                canvasWidth = (int) (canvasHeight / imgAspect);
                x1 = (x1 - canvasWidth) / 2;
            }
            x2 = canvasWidth + x1;
            y2 = canvasHeight + y1;
//            g.drawImage(img, 9, 9, null);
            g.drawImage(image, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
        }

    }

    public void setStroke(Graphics2D g2d, MathObject obj) {

        double thickness = obj.mp.thickness;
        //Thickness 1 means 1% of screen width
        //Thickness 100 draws a line with whole screen width
        if (!obj.mp.absoluteThickness) {
            thickness *= cnf.mediaW * .005d;
        } else {
            thickness *= 4; //computed width for MediaW of 800
        }
        int strokeSize = (int) Math.round(thickness);

//        float strokeSize = (float) thickness;
        switch (obj.mp.dashStyle) {
            case SOLID:
                BasicStroke basicStroke = new BasicStroke(strokeSize, CAP_ROUND, JOIN_ROUND);
                g2d.setStroke(basicStroke);
                break;
            case DASHED:
                float[] dashedPattern = {5.0f * strokeSize, 2.0f * strokeSize};
                Stroke dashedStroke = new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dashedPattern, 1.0f);
                g2d.setStroke(dashedStroke);
                break;
            case DOTTED:
                float[] dottedPattern = {1f, 2.0f * strokeSize};
                Stroke dottedStroke = new BasicStroke(strokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10.0f, dottedPattern, 1.0f);
                g2d.setStroke(dottedStroke);
                break;
        }

    }

    @Override
    public void drawPath(Shape mobj) {
        drawPath(mobj, camera);
    }

    public void drawPath(Shape mobj, Camera2D cam) {

        JMPath c = mobj.getPath();
        int numPoints = c.size();

        if (numPoints >= 2) {
            path = createPathFromJMPath(mobj, c, cam);
//            path.setWindingRule(GeneralPath.WIND_NON_ZERO);

            if (mobj.mp.isFilled()) {
                //Filled paths are better drawn supposing all points are visible...
                //This works well on simply paths, but not path with multiple holes (like letter "B")
//                Path2D.Double pathToFill = createPathFromJMPath(mobj, mobj.getPath().allVisible(), cam);
//                g2draw.setColor(mobj.mp.fillColor.getColor());
//                g2draw.fill(pathToFill);
                g2draw.setColor(mobj.mp.getFillColor().getAwtColor());
                g2draw.fill(path);
            }
            //Border is always drawed
//            AffineTransform bTr = g2draw.getTransform();
//            final AffineTransform cameratoG2DTransform = getCameratoG2DTransform(cam);

            g2draw.setColor(mobj.mp.getDrawColor().getAwtColor());
            setStroke(g2draw, mobj);
//            g2draw.setTransform(cameratoG2DTransform);
            g2draw.draw(path);
//            g2draw.setTransform(bTr);

            if (BOUNDING_BOX_DEBUG) {
                debugBoundingBox(c.getBoundingBox());
            }
        }
    }

    public AffineTransform getCameratoG2DTransform(Camera cam) {
        Rect r = cam.getMathView();
        //First, move UL math corner to screen (0,0)
        AffineTransform tr = AffineTransform.getTranslateInstance(-r.xmin, -r.ymax);
        //Now, scale it so that (xmax-xmin, ymax-ymin) goes to (W,H)
        double w = cnf.mediaW * scaleBufferedImage / (r.xmax - r.xmin);
        double h = -cnf.mediaH * scaleBufferedImage / (r.ymax - r.ymin);
        AffineTransform sc = AffineTransform.getScaleInstance(w, h);
        sc.concatenate(tr);
        return sc;
    }

    public Path2D.Double createPathFromJMPath(Shape mobj, Camera2D cam) {
        return createPathFromJMPath(mobj, mobj.getPath(), cam);
    }

    public Path2D.Double createPathFromJMPath(Shape mobj, JMPath c, Camera2D cam) {
        Path2D.Double resul = new Path2D.Double();
        Vec p = c.getJMPoint(0).p.v;

        if (DEBUG_PATH_POINTS) {
            debugPathPoint(c.getJMPoint(0), c);
        }
        int[] scr = cam.mathToScreen(p.x, p.y);
        if (DEBUG_LABELS) {
            g2debug.setColor(Color.BLACK);
            g2debug.drawString(mobj.label, (float) scr[0], (float) scr[1]);
            g2debug.setColor(Color.WHITE);
            g2debug.drawString(mobj.label, (float) scr[0] + 2, (float) scr[1] + 2);
        }
        resul.moveTo(scr[0], scr[1]);
        //Now I iterate to get the next points
        int numPoints = c.size();
//        int prev[] = {scr[0], scr[1]};
//        int xy[] = camera.mathToScreen(scr[0], scr[1]);

        for (int n = 1; n < numPoints + 1; n++) {

            Vec point = c.getJMPoint(n).p.v;
            Vec cpoint1 = c.getJMPoint(n - 1).cp1.v;
            Vec cpoint2 = c.getJMPoint(n).cp2.v;
            int[] xy = cam.mathToScreen(point);

            int[] cxy1 = cam.mathToScreen(cpoint1);
            int[] cxy2 = cam.mathToScreen(cpoint2);
            if (DEBUG_PATH_POINTS) {
                debugPathPoint(c.getJMPoint(n), c);
            }
            if (c.getJMPoint(n).isThisSegmentVisible) {
                if (c.getJMPoint(n).isCurved) {
                    resul.curveTo(cxy1[0], cxy1[1], cxy2[0], cxy2[1], xy[0], xy[1]);
                } else {
                    resul.lineTo(xy[0], xy[1]);
                }
            } else {
                if (n < numPoints) {//If it is the last point, don't move (it creates a strange point at the beginning)
                    resul.moveTo(xy[0], xy[1]);
                    resul.closePath();
                }
            }

        }
//        if (c.getJMPoint(0).isThisSegmentVisible) {
//            if (xy[0] != scr[0] | xy[1] != scr[1]) {
//                resul.moveTo(scr[0], scr[1]);
//            }
//            resul.closePath();
//        }
        if (c.getNumberOfConnectedComponents() == 0) {
            resul.closePath();
        }
        return resul;
    }

    public void debugPathPoint(JMPathPoint p, JMPath path) {
        int[] x = camera.mathToScreen(p.p.v.x, p.p.v.y);
        debugCPoint(camera.mathToScreen(p.cp1.v.x, p.cp1.v.y));
        debugCPoint(camera.mathToScreen(p.cp2.v.x, p.cp2.v.y));
        if (p.type == JMPathPointType.VERTEX) {
            g2debug.setColor(Color.GREEN);

        }
        if (p.type == JMPathPointType.INTERPOLATION_POINT) {
            g2debug.setColor(Color.GRAY);

        }
        if (p.isCurved) {
            g2debug.drawOval(x[0] - 4, x[1] - 4, 8, 8);
        } else {
            g2debug.drawRect(x[0] - 2, x[1] - 2, 4, 4);
        }
        debugText(String.valueOf(path.jmPathPoints.indexOf(p)), x[0] + 5, x[1]);

    }

    public void debugPoint(int x, int y) {
        g2debug.drawOval(x - 2, y - 2, 4, 4);
    }

    public void debugPoint(int[] xy) {
        g2draw.setColor(Color.BLUE);
        g2draw.drawOval(xy[0] - 4, xy[1] - 4, 8, 8);
    }

    public void debugCPoint(int[] xy) {
        g2draw.setColor(Color.PINK);
        g2draw.drawRect(xy[0] - 4, xy[1] - 4, 8, 8);
    }

    /**
     * Returns equivalent position from default camera to fixed camera.
     *
     * @param v
     * @return
     */
    public Vec defaultToFixedCamera(Vec v) {
        double width1 = camera.getMathView().getWidth();
        int[] ms = camera.mathToScreen(v);
        double[] coords = fixedCamera.screenToMath(ms[0], ms[1]);
        return new Vec(coords[0], coords[1]);

    }

    @Override
    public void drawAbsoluteCopy(Shape sh, Vec v) {
        Shape shape = sh.copy();
        Vec vFixed = defaultToFixedCamera(v);
        shape.shift(vFixed.minus(v));
        drawPath(shape, fixedCamera);
    }

    @Override
    public Camera getFixedCamera() {
        return fixedCamera;
    }

    public PreviewWindow getPreviewWindow() {
        return previewWindow;
    }

    @Override
    public <T extends Camera> T getCamera() {
        return (T) camera;
    }

    @Override
    public Rect createImage(String fileName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawImage(JMImage obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void debugText(String text, Vec loc) {
        int[] xy = camera.mathToScreen(loc);
        debugText(text, xy[0], xy[1]);
    }

    public void debugText(String texto, int x, int y) {
        Font font = new Font("Serif", Font.PLAIN, 12);
        g2draw.setFont(font);
        g2draw.setColor(Color.WHITE);
        g2draw.drawString(texto, x, y);
    }

    public void debugBoundingBox(Rect r) {
        double[] ULCorner = {r.xmin, r.ymax};
        double[] DRCorner = {r.xmax, r.ymin};
        int[] scUL = camera.mathToScreen(ULCorner[0], ULCorner[1]);
        int[] scDR = camera.mathToScreen(DRCorner[0], DRCorner[1]);
        g2draw.setColor(Color.LIGHT_GRAY);
        g2draw.setStroke(new BasicStroke(1, CAP_ROUND, JOIN_ROUND));
        g2draw.drawRect(scUL[0], scUL[1], scDR[0] - scUL[0], scDR[1] - scUL[1]);
    }
}
