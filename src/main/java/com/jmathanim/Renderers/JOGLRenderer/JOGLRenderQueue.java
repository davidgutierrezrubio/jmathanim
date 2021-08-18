/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.MovieEncoders.VideoEncoder;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jogamp.opengl.egl.EGLGLCapabilities;

/**
 * Manages the queue of objects to be rendered at every frame draw
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class JOGLRenderQueue implements GLEventListener {

    public boolean busy = false; //True if actually drawing
    private static final double MIN_THICKNESS = .2d;
    private int height;
    public boolean useCustomShaders = true;
    JMathAnimConfig config;
    ArrayList<MathObject> objectsToDraw;
    GLU glu;
//    GLUgl2 glu2;
    private int width;
    final float zNear = 0.1f, zFar = 7000f;
//    private GL3ES3 gles;
    private GL3 gl3;
    private GL2 gl2;
    private Camera3D camera;
    public Camera3D fixedCamera;
    public VideoEncoder videoEncoder;
    public File saveFilePath;
    private int newLineCounter = 0;
    public int frameCount;

    //Shaders and uniform variables
    ShaderLoader thinLinesShader;
    ShaderLoader fillShader;
    int unifProject;//Projection matrix
    int unifModelMat;//Model matrix
    int unifMiterLimit;//Miter limit (for rendering thin lines)
    int unifViewPort;//Viewport (for rendering thin lines)

    ShaderDrawer shaderDrawer;
    public JOGLRenderer renderer;

    public JOGLRenderQueue(JMathAnimConfig config) {

        this.config = config;
        objectsToDraw = new ArrayList<>();

        try {
            prepareEncoder();
        } catch (Exception ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setConfig(JMathAnimConfig config) {
        this.config = config;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
//        gles = drawable.getGL().getGL3ES3();
        gl3 = drawable.getGL().getGL3();
        gl2 = drawable.getGL().getGL2();
        glu = new GLU();
//        glu2 = new GLUgl2();

//        gles.setSwapInterval(1);
        gl3.glEnable(GL.GL_DEPTH_TEST);
//        gles.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
        gl3.glDepthFunc(GL.GL_LEQUAL);

        gl3.glEnable(GL3.GL_LINE_SMOOTH);
        gl3.glEnable(GL3.GL_POLYGON_SMOOTH);
//        gl3.glEnable(GL3.GL_POINT_SMOOTH);
//        gl3.glHint(GL3.GL_POINT_SMOOTH, GL3.GL_NICEST);
        gl3.glHint(GL3.GL_POLYGON_SMOOTH_HINT, GL3.GL_NICEST);
        gl3.glHint(GL3.GL_LINE_SMOOTH_HINT, GL3.GL_NICEST);
        gl3.glEnable(GL3.GL_BLEND);
        gl3.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl3.glEnable(GL3.GL_MULTISAMPLE);
        gl3.glEnable(GL3.GL_SAMPLE_ALPHA_TO_COVERAGE);

        //Drawer class, we have to pass it the created shaders
        shaderDrawer = new ShaderDrawer(gl3, gl2);
        if (useCustomShaders) {
            thinLinesShader = new ShaderLoader(gl3, "#thinLines/thinLines.vs", "#thinLines/thinLines.gs", "#thinLines/thinLines.fs");
            fillShader = new ShaderLoader(gl3, "#fill/fill.vs", "", "#fill/fill.fs");
            try {
                thinLinesShader.loadShaders();
                fillShader.loadShaders();
            } catch (IOException ex) {
                Logger.getLogger(JOGLRenderQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Uniform variables to be used in this class
            unifProject = thinLinesShader.getUniformVariable("projection");
            unifModelMat = thinLinesShader.getUniformVariable("modelMat");
            unifMiterLimit = thinLinesShader.getUniformVariable("MiterLimit");
            unifViewPort = thinLinesShader.getUniformVariable("Viewport");

            shaderDrawer.thinLineShader = thinLinesShader;
            shaderDrawer.fillShader = fillShader;
            shaderDrawer.unifColor = thinLinesShader.getUniformVariable("unifColor");
            shaderDrawer.unifThickness = thinLinesShader.getUniformVariable("Thickness");
        }
    }

    public final void prepareEncoder() throws Exception {

        JMathAnimScene.logger.info("Preparing encoder");

        if (config.isCreateMovie()) {
            videoEncoder = new XugglerVideoEncoder();
            File tempPath = new File(config.getOutputDir().getCanonicalPath());
            tempPath.mkdirs();
            saveFilePath = new File(config.getOutputDir().getCanonicalPath() + File.separator
                    + config.getOutputFileName() + "_" + config.mediaH + ".mp4");
            JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
            videoEncoder.createEncoder(saveFilePath, config);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    public void addToQueue(MathObject obj) {
        objectsToDraw.add(obj);
    }

    @Override
    public synchronized void display(GLAutoDrawable drawable) {
        synchronized (this) {
            busy = true;
            adjustCameraView(drawable);

            //Trying to get rid of the annoying z-fighting...
            Vec toEye = camera.look.to(camera.eye);

            // clear screen
            PaintStyle backgroundColor = config.getBackgroundColor();
            if (backgroundColor instanceof JMColor) {
                JMColor col = (JMColor) backgroundColor;
                gl3.glClearColor((float) col.r, (float) col.g, (float) col.b, (float) col.getAlpha());
            }
            gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            double zFightingStep = 0;//.001;
            double zFightingParameter = 0;
            for (MathObject obj : objectsToDraw) {
                if (obj instanceof Shape) {

                    //Convex, filled-> Not 2º stencil buffer
                    //Thickness=1, not filled-> No thin shader, no fill method, no stencil
                    Shape s = (Shape) obj;
                    gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
                    gl2.glLoadIdentity();
                    if (s.faceToCamera) {
                        //Compute model view matrix so that faces to the camera
                        printModelMatrix();
                        gl2.glRotatef((float) (PI / 4), 1, 1, 1);
                        printModelMatrix();
                        System.out.println("----");
                    }

                    loadModelMatrixIntoShaders();

                    ArrayList<ArrayList<Point>> pieces = s.getPath().computePolygonalPieces(camera);
                    float[] fc = getFillColor(s);
                    boolean noFill = (fc[3] == 0);//if true, shape is not filled

                    //First clear the Stencil buffer if the shape is filled
//                    if (!noFill) {
                    gl3.glEnable(GL3.GL_STENCIL_TEST);
                    gl3.glStencilMask(0xFF);
                    gl3.glClear(GL3.GL_STENCIL_BUFFER_BIT);
//                    } else {
//                        gl3.glDisable(GL3.GL_STENCIL_TEST);
//                    }

                    //Contour
                    gl2.glUseProgram(thinLinesShader.getShader());
                    if (s.getMp().getThickness() > 0) {
                        shaderDrawer.drawThinShape(s, pieces, noFill);
                    }

                    //Fill
                    gl2.glUseProgram(fillShader.getShader());

//                    zFightingParameter += zFightingStep;
                    shaderDrawer.drawFill(s, pieces, toEye.mult(zFightingParameter));
//                    shaderDrawer.drawFillSlowButWorking(s, pieces);
                    gl3.glDisable(GL.GL_STENCIL_TEST);

                }
            }

            objectsToDraw.clear();
            gl3.glFlush();

            if (config.isCreateMovie()) {
//            BufferedImage image = screenshot(drawable);
                BufferedImage image = screenshot(gl3, drawable);
                videoEncoder.writeFrame(image, frameCount);
            }
            busy = false;
            notify();
        }

    }

    private void loadModelMatrixIntoShaders() {
        FloatBuffer modMat = FloatBuffer.allocate(16);
        gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modMat);
        gl2.glUseProgram(thinLinesShader.getShader());
        gl2.glUniformMatrix4fv(thinLinesShader.getUniformVariable("modelMatrix"), 1, false, modMat);
        gl2.glUseProgram(fillShader.getShader());
        gl2.glUniformMatrix4fv(fillShader.getUniformVariable("modelMatrix"), 1, false, modMat);
    }

    private void printModelMatrix() {
        FloatBuffer modMat = FloatBuffer.allocate(16);
        gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modMat);
        for (int i = 0; i < 16; i++) {
             if (i % 4 == 0) {
                System.out.println("");
            }
            System.out.print(modMat.get(i) + " ");
           
        }
    }

    public BufferedImage screenshot(GL3 gl2, GLDrawable drawable) {
        AWTGLReadBufferUtil aa = new AWTGLReadBufferUtil(drawable.getGLProfile(), true);
        BufferedImage img = aa.readPixelsToBufferedImage(gl2, 0, 0, config.mediaW, config.mediaH, true);
        return img;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        this.width = w;
        this.height = h;

        adjustCameraView(drawable);
    }

    private void adjustCameraView(GLAutoDrawable drawable) throws GLException {
        final GL2 gl2 = drawable.getGL().getGL2();
        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl2.glLoadIdentity();
        Rect bb = camera.getMathView();
        if (!camera.perspective) {
            gl2.glOrtho(bb.xmin, bb.xmax, bb.ymin, bb.ymax, -5, 5);
        } else {
            glu.gluPerspective(camera.fov, (float) bb.getWidth() / bb.getHeight(), .1f, 10.0f);

            Vec up = camera.getUpVector();//Inefficient way. Improve this.
            glu.gluLookAt(
                    camera.eye.v.x, camera.eye.v.y, camera.eye.v.z,
                    camera.look.v.x, camera.look.v.y, camera.look.v.z,
                    up.x, up.y, up.z
            );
        }
        FloatBuffer projMat = FloatBuffer.allocate(16);

        if (useCustomShaders) {
//        Custom shader uniform attributes
            gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projMat);

            gl2.glUseProgram(thinLinesShader.getShader());
            gl2.glUniformMatrix4fv(unifProject, 1, false, projMat);
            gl2.glUniform1f(unifMiterLimit, .5f);
            gl2.glUniform2f(unifViewPort, this.width, this.height);

            gl2.glUseProgram(fillShader.getShader());
            gl2.glUniformMatrix4fv(fillShader.getUniformVariable("projection"), 1, false, projMat);
        }
    }

    public Camera3D getCamera() {
        return camera;
    }

    public void setCamera(Camera3D camera) {
        this.camera = camera;
    }

    public float computeThickness(MathObject mobj) {
        Camera cam = (mobj.getMp().isAbsoluteThickness() ? fixedCamera : camera);
        float value = (float) Math.max(mobj.getMp().getThickness() / cam.getMathView().getWidth() * 2.5d, MIN_THICKNESS);
        return value;
    }

    public float[] getFillColor(Shape s) {
        PaintStyle st = s.getMp().getFillColor();
        float r = 0;
        float g = 0;
        float b = 0;
        float alpha = 1;
        if (st instanceof JMColor) {
            JMColor col = (JMColor) st;
            r = (float) col.r;
            g = (float) col.g;
            b = (float) col.b;
            alpha = (float) col.getAlpha();
        }
        return new float[]{r, g, b, alpha};
    }

//    private void drawSurface(Surface surface) {
//
//        JMColor col = (JMColor) surface.getMp().getFillColor();
//        if (col.getAlpha() > 0) {//Draw the surface fill then
//            gl.glPushAttrib(GL2.GL_ENABLE_BIT);
//            gl.glColor4d(col.r, col.g, col.b, col.getAlpha());
//            for (Face f : surface.faces) {
//                gl.glBegin(GL2.GL_POLYGON);
//                for (Point p : f.points) {
//                    gl.glVertex3d(p.v.x, p.v.y, p.v.z);
//                }
//                gl.glEnd();
//            }
//
//            gl.glPopAttrib();
//        }
//
//        gl.glPushAttrib(GL2.GL_ENABLE_BIT);
//        processDrawingStyle(surface);
//        for (Face f : surface.faces) {
//            gl.glBegin(GL2.GL_LINE_LOOP);
//            for (Point p : f.points) {
//                gl.glVertex3d(p.v.x, p.v.y, p.v.z);
//            }
//            gl.glEnd();
//        }
//
//        gl.glPopAttrib();
//    }
}
