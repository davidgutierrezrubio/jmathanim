/*
 * Copyright (C) 2024 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Renderers.ProcessingRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Renderers.FXRenderer.FXPathUtils;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.MovieEncoders.VideoEncoder;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.CircularArrayList;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AbstractJMImage;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import processing.core.PApplet;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ProcessingRenderer extends Renderer {

    private ProcessingApplet prApplet;
    private static CountDownLatch latch = new CountDownLatch(1);

    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;

    private static final double MIN_THICKNESS = .2d;

    public final FXPathUtils fXPathUtils;
    public Camera3D camera;
    public Camera3D fixedCamera;
    public double correctionThickness;

    protected VideoEncoder videoEncoder;
    protected File saveFilePath;

    public ProcessingRenderer(JMathAnimScene parentScene) {
        super(parentScene);

        fXPathUtils = new FXPathUtils();
        camera = new Camera3D(scene, config.mediaW, config.mediaH);
        fixedCamera = new Camera3D(scene, config.mediaW, config.mediaH);
        correctionThickness = config.mediaW * 1d / 1066;//Correction factor for thickness
    }

    @Override
    public void initialize() {
        prApplet = new ProcessingApplet(this, config, new CountDownLatch(2));
        camera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fixedCamera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        Thread thread = new Thread(() -> PApplet.runSketch(new String[]{"ProcessingApplet"}, prApplet));
        thread.start();
        try {
            prApplet.setupLatch.await(); // Espera hasta que prAppletPrueba esté listo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            prepareEncoder();
        } catch (Exception ex) {
            Logger.getLogger(ProcessingRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void prepareEncoder() throws Exception {

        JMathAnimScene.logger.debug("Preparing encoder");

        if (config.isCreateMovie()) {
            videoEncoder = new XugglerVideoEncoder();
            File tempPath = new File(config.getOutputDir().getCanonicalPath());
            tempPath.mkdirs();
            saveFilePath = new File(config.getOutputDir().getCanonicalPath() + File.separator
                    + config.getOutputFileName() + "_" + config.mediaH + ".mp4");
            JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
            config.setSaveFilePath(saveFilePath);
            videoEncoder.createEncoder(config);
        }

    }
  @Override
    public double MathWidthToThickness(double w) {
//        return mathScalar * config.mediaW / (xmax - ymin);
//        return camera.mathToScreen(w) / 1.25 * camera.getMathView().getWidth() / 2d;
        return w * 1066;
    }

    @Override
    public double ThicknessToMathWidth(double th) {
        return th / 1066;
    }
    
    @Override
    public double ThicknessToMathWidth(MathObject obj) {
        Camera cam = (obj.getMp().isAbsoluteThickness() ? fixedCamera : camera);
        return obj.getMp().getThickness() / 1066 * 4 / cam.getMathView().getWidth();
    }

    @Override
    public void addSound(SoundItem soundItem) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public RendererEffects buildRendererEffects() {
        //nothing to do here...yet
        return null;
    }

    @Override
    public void clearAndPrepareCanvasForAnotherFrame() {
        prApplet.beginDraw();
    }

    @Override
    public Rect createImage(InputStream stream) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void debugText(String text, Vec loc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void drawAbsoluteCopy(Shape sh, Vec anchor) {
        Shape shape = sh.copy();
        Vec vFixed = defaultToFixedCamera(anchor);
        shape.shift(vFixed.minus(anchor));
        drawPath(shape, sh.getCamera());
    }

    /**
     * Returns equivalent position from default camera to fixed camera. For
     * example if you pass the Vec (1,1) to this method, it will return a new
     * set of coordinates so that, when rendered with the fixed camera, appears
     * in the same position as (1,1) with default camera.
     *
     * @param v Vector that marks the position
     * @return The coordinates to be used with the fixed camera
     */
    public Vec defaultToFixedCamera(Vec v) {
        double[] ms = camera.mathToScreenFX(v);
        double[] coords = fixedCamera.screenToMath(ms[0], ms[1]);
        return new Vec(coords[0], coords[1]);
        
    }
    @Override
    public void drawImage(AbstractJMImage obj, Camera cam) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void drawPath(Shape mobj) {
        drawPath(mobj, camera);
    }

     @Override
    public void drawPath(Shape mobj, Camera camera) {
        prApplet.drawPath(mobj, (Camera3D) camera);
    }
    
    
//    @Override
//    public void drawPath(Shape mobj, Camera camera) {
//        prApplet.applyStyle(mobj, (MODrawProperties) mobj.getMp());
//        int numPoints = mobj.size(); //TODO improve if closed
//
//        boolean closed = (mobj.get(0).isThisSegmentVisible);
//
//        float[] xx1 = new float[numPoints];
//        float[] yy1 = new float[numPoints];
//        float[] zz1 = new float[numPoints];
//        float[] mx1 = new float[numPoints];
//        float[] my1 = new float[numPoints];
//        float[] mz1 = new float[numPoints];
//        float[] mx2 = new float[numPoints];
//        float[] my2 = new float[numPoints];
//        float[] mz2 = new float[numPoints];
//        float[] xx2 = new float[numPoints];
//        float[] yy2 = new float[numPoints];
//        float[] zz2 = new float[numPoints];
//        CircularArrayList<JMPathPoint> jmps = mobj.getPath().jmPathPoints;
//        for (int i = 0; i < numPoints - (closed ? 0 : 1); i++) {
//            Vec p1 = jmps.get(i).p.v;
//            Vec pm1 = jmps.get(i).cpExit.v;
//            Vec pm2 = jmps.get(i + 1).cpEnter.v;
//            Vec p2 = jmps.get(i + 1).p.v;
//            double[] vv1 = camera.mathToScreen(p1.x, p1.y);//TODO: To handle 3D, change this, use procesing camera
//            double[] vvm1 = camera.mathToScreen(pm1.x, pm1.y);
//            double[] vvm2 = camera.mathToScreen(pm2.x, pm2.y);
//            double[] vv2 = camera.mathToScreen(p2.x, p2.y);
//            xx1[i] = (float) vv1[0];//(int) Math.round(vv1[0]);
//            yy1[i] = (float) vv1[1];//(int) Math.round(vv1[1]);
//            zz1[i] = 0f;
//            mx1[i] = (float) vvm1[0];//(int) Math.round(vvm1[0]);
//            my1[i] = (float) vvm1[1];//(int) Math.round(vvm1[1]);
//            mz1[i] = 0f;
//            mx2[i] = (float) vvm2[0];//(int) Math.round(vvm2[0]);
//            my2[i] = (float) vvm2[1];//(int) Math.round(vvm2[1]);
//            mz2[i] = 0f;
//            xx2[i] = (float) vv2[0];//(int) Math.round(vv2[0]);
//            yy2[i] = (float) vv2[1];//(int) Math.round(vv2[1]);
//            zz2[i] = 0f;
//        }
//        prApplet.drawShape(
//                xx1, yy1, zz1,
//                mx1, my1, mz1,
//                mx2, my2, mz2,
//                xx2, yy2, zz2,
//                false);
//    }

    @Override
    public void finish(int frameCount) {
        JMathAnimScene.logger.info(
                String.format("%d frames created, %.2fs total time", frameCount, (1.f * frameCount) / config.fps));
        if (config.isCreateMovie()) {
            /**
             * Encoders, like decoders, sometimes cache pictures so it can do
             * the right key-frame optimizations. So, they need to be flushed as
             * well. As with the decoders, the convention is to pass in a null
             * input until the output is not complete.
             */
            JMathAnimScene.logger.info("Finishing movie...");
            videoEncoder.finish();
            if (videoEncoder.isFramesGenerated()) {
                JMathAnimScene.logger.info("Movie created at " + saveFilePath);
            }

        }
        prApplet.exitSketch();
    }

    @Override
    public <T extends Camera> T getCamera() {
        return (T) camera;
    }

    @Override
    public Camera getFixedCamera() {
        return fixedCamera;
    }

    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        return prApplet.getRenderedImage();
    }

    @Override
    public void saveFrame(int frameCount) {
        prApplet.endDraw(latch);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        prApplet.redrawAndWait(latch1, latch2); // Forzar redibujado y esperar
        try {
//            latch1.await(); // Wait for redraw to complete
            latch2.await(); // Wait for redraw to complete
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessingApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (config.isCreateMovie()) {
            BufferedImage renderedImage = prApplet.renderedImage;
            videoEncoder.writeFrame(renderedImage, frameCount);
        }
        if (config.isSaveToPNG()) {
            BufferedImage renderedImage = prApplet.renderedImage;
            String filename = config.getOutputFileName() + String.format("%06d", frameCount) + ".png";
            writeImageToPNG(filename, renderedImage, "png");
        }
//         try {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ProcessingApplet.class.getName()).log(Level.SEVERE, null, ex);
//            }

    }

}
