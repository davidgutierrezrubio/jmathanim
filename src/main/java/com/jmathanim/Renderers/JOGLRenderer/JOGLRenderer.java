/*
 * Copyright (C) 2024 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Renderers.JOGLRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AbstractJMImage;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.surface.Surface;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Development, unstable class for testing the JOGL API for rendering purposes
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JOGLRenderer extends Renderer {

    public Camera3D camera;
    public Camera3D fixedCamera;
    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;
    private GLWindow glWindow;
    JOGLRenderQueue queue;

    public JOGLRenderer(JMathAnimScene parentScene) {
        super(parentScene);
        camera = new Camera3D(parentScene, config.mediaW, config.mediaH);
        fixedCamera = new Camera3D(parentScene, config.mediaW, config.mediaH);
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
        return new RendererEffects();
    }

    @Override
    public Rect createImage(InputStream stream) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void drawPath(Shape mobj, Vec shiftVector,Camera camera) {
        queue.addToQueue(mobj);//TODO: SHiftvector is not passed trhough queue
    }



    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        queue.saveImageFlag = true;
        synchronized (queue) {
            glWindow.display();
        }
        return queue.savedImage;
    }

    @Override
    public void initialize() {
        queue = new JOGLRenderQueue(config);
        queue.width=this.getMediaWidth();
        queue.height=this.getMediaHeight();
        queue.renderer = this;
        queue.setCamera(camera);
        queue.fixedCamera = fixedCamera;
        camera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fixedCamera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL4));
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);
        caps.setAlphaBits(8);
        caps.setStencilBits(3);

        glWindow = GLWindow.create(caps);
        glWindow.setSize(config.mediaW, config.mediaH);
        glWindow.setTitle("JMathAnim - " + config.getOutputFileName());
        glWindow.addGLEventListener(queue);
//
//// Verifica soporte de anti-aliasing y establece un valor seguro
//        if (GLContext.getCurrent().isExtensionAvailable("GL_ARB_multisample")) {
//
//            int[] maxSamples = new int[1];
//            GLContext.getCurrent().getGL().glGetIntegerv(GL.GL_MAX_SAMPLES, maxSamples, 0);
//            caps.setNumSamples(Math.min(8, maxSamples[0])); // Usa 8 o el máximo permitido
//
//        }
//
//// Verifica soporte de bits alfa y stencil
//        int[] alphaBits = new int[1];
//        GLContext.getCurrent().getGL().glGetIntegerv(GL.GL_ALPHA_BITS, alphaBits, 0);
//        caps.setAlphaBits(Math.min(8, alphaBits[0]));
//
//        int[] stencilBits = new int[1];
//        GLContext.getCurrent().getGL().glGetIntegerv(GL.GL_STENCIL_BITS, stencilBits, 0);
//        caps.setStencilBits(Math.min(8, stencilBits[0]));

        glWindow.setVisible(true);//TODO: For now it needs to always show the window...

    }

    @Override
    public <T extends Camera> T getCamera() {
        return (T) camera;
    }

    @Override
    public Camera getFixedCamera() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void saveFrame(int frameCount) {
//        JMathAnimScene.logger.info("JOGLRenderer: Saving frame");
        queue.frameCount = frameCount;
        synchronized (queue) {
            glWindow.display();
        }
    }

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
            queue.videoEncoder.finish();
            if (queue.videoEncoder.isFramesGenerated()) {
                JMathAnimScene.logger.info("Movie created at " + queue.saveFilePath);
            }

        }
    }

    @Override
    public void clearAndPrepareCanvasForAnotherFrame() {
        super.clearAndPrepareCanvasForAnotherFrame();
        //This should delete the JOGL queue
//        JMathAnimScene.logger.info("JOGLRenderer: Clear frame");
    }

    public void drawSurface(Surface surface) {
        queue.addToQueue(surface);

    }

    @Override
    public void drawPath(Shape mobj) {
        //This should create JOGL objects and add them to the queue
//        JMathAnimScene.logger.info("JOGLRenderer: Drawing path");
//        queue.addShapeFill(mobj);
        queue.addToQueue(mobj);
    }

    @Override
    public void drawAbsoluteCopy(Shape sh, Vec anchor) {
        drawPath(sh);//TODO: Fix this
    }

    public Rect createImage(String fileName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawImage(AbstractJMImage obj, Camera cam) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void debugText(String text, Vec loc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double getThicknessForMathWidth(double w) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
