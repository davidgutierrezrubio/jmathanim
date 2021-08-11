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
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.surface.Face;
import com.jmathanim.mathobjects.surface.Surface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.GLReadBufferUtil;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import jogamp.opengl.glu.tessellator.GLUtessellatorImpl;

/**
 * Manages the queue of objects to be rendered at every frame draw
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class JOGLRenderQueue implements GLEventListener {
    public boolean busy=false; //True if actually drawing
    private static final double MIN_THICKNESS = .2d;
    private int height;
    public boolean useCustomShaders = true;
    JMathAnimConfig config;
    ArrayList<MathObject> objectsToDraw;
    GLU glu;
    GLUgl2 glu2;
    private int width;
    final float zNear = 0.1f, zFar = 7000f;
    private GL3ES3 gles;
    private GL3 gl;
    private Camera3D camera;
    public Camera3D fixedCamera;
    public VideoEncoder videoEncoder;
    public File saveFilePath;
    private int newLineCounter = 0;
    public int frameCount;
    ShaderLoader shaderLoader;
    ShaderDrawer shaderDrawer;

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
        gles = drawable.getGL().getGL3ES3();
        gl = drawable.getGL().getGL3();
        glu = new GLU();
        glu2 = new GLUgl2();

        gles.setSwapInterval(1);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gles.glEnable(GL.GL_DEPTH_TEST);
        gles.glEnable(GL.GL_BLEND);
        gles.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
        gl.glDepthFunc(GL.GL_LEQUAL);

        gles.glEnable(GL2.GL_LINE_SMOOTH);
        gles.glEnable(GL2.GL_POLYGON_SMOOTH);
        gles.glEnable(GL2.GL_POINT_SMOOTH);
//        gles2.glHint(GL2.GL_POINT_SMOOTH, GL2.GL_NICEST);
        gles.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
        gles.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gles.glEnable(GL2.GL_BLEND);
        gles.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gles.glEnable(GL2.GL_MULTISAMPLE);
        gles.glEnable(GL2.GL_SAMPLE_ALPHA_TO_COVERAGE);
        if (useCustomShaders) {
            shaderLoader = new ShaderLoader(gles, gl);
            try {
                shaderLoader.loadShaders();
            } catch (IOException ex) {
                Logger.getLogger(JOGLRenderQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
            shaderDrawer = new ShaderDrawer(shaderLoader, gles, gl);
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
        busy=true;
        adjustCameraView(drawable);

        // clear screen
        PaintStyle backgroundColor = config.getBackgroundColor();
        if (backgroundColor instanceof JMColor) {
            JMColor col = (JMColor) backgroundColor;
            gles.glClearColor((float) col.r, (float) col.g, (float) col.b, (float) col.getAlpha());
        }
        gles.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (!useCustomShaders) {

//            for (MathObject obj : objectsToDraw) {
//
//                if (obj instanceof Shape) {
//                    Shape s = (Shape) obj;
//                    drawFill(s);
//                    drawShape(s);
//                }
//
//                if (obj instanceof Surface) {
//                    drawSurface((Surface) obj);
//                }
//
//            }
        }
        else
        {//Custom shaders for drawing MathObjects
            for (MathObject obj : objectsToDraw) {
                 if (obj instanceof Shape) {
                    Shape s = (Shape) obj;
                    shaderDrawer.drawFill(s);
                    shaderDrawer.drawShape(s);
                }
            }
            
            
            
        }

        objectsToDraw.clear();
        gl.glFlush();

        if (config.isCreateMovie()) {
//            BufferedImage image = screenshot(drawable);
            BufferedImage image = screenshot(gl, drawable);
            videoEncoder.writeFrame(image, frameCount);
        }
        busy=false;
        this.notifyAll();
    }

    public BufferedImage screenshot(GL3 gl2, GLDrawable drawable) {
        AWTGLReadBufferUtil aa = new AWTGLReadBufferUtil(drawable.getGLProfile(), true);
        BufferedImage img = aa.readPixelsToBufferedImage(gl2, 0, 0, config.mediaW, config.mediaH, true);
        return img;
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        this.width=w;
        this.height=h;
               
        adjustCameraView(drawable);
    }

    private void adjustCameraView(GLAutoDrawable drawable) throws GLException {
        final GL2 gl2 = drawable.getGL().getGL2();
        gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//
        gl2.glLoadIdentity();
        Rect bb = camera.getMathView();
        if (!camera.perspective) {
            gl2.glOrtho(bb.xmin, bb.xmax, bb.ymin, bb.ymax, -5, 5);
        } else {
            glu.gluPerspective(camera.fov, (float) bb.getWidth() / bb.getHeight(), 0.1f, 10000.0f);

            Vec up = camera.getUpVector();//Inefficient way. Improve this.
            glu.gluLookAt(
                    camera.eye.v.x, camera.eye.v.y, camera.eye.v.z,
                    camera.look.v.x, camera.look.v.y, camera.look.v.z,
                    up.x, up.y, up.z
            );
        }
        FloatBuffer projMat = FloatBuffer.allocate(16);
        FloatBuffer modMat = FloatBuffer.allocate(16);

        if (useCustomShaders) {
//        Custom shader uniform attributes
            gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projMat);
            gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modMat);
            gl2.glUniformMatrix4fv(shaderLoader.unifProject, 1, false, projMat);
            gl2.glUniformMatrix4fv(shaderLoader.unifModelMat, 1, false, modMat);
            gl2.glUniform1f(shaderLoader.unifMiterLimit, .5f);
            gl2.glUniform1f(shaderLoader.unifThickness, 60);
            gl2.glUniform2f(shaderLoader.unifViewPort, this.width, this.height);
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
