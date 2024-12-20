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
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.surface.Surface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
//import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Manages the queue of objects to be rendered at every frame draw
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class JOGLRenderQueue implements GLEventListener {

    public boolean busy = false; //True if actually drawing
    private static final double MIN_THICKNESS = .2d;
    private boolean hasToRectify=true;
    public int height;
    public BufferedImage savedImage;
    public boolean useCustomShaders = true;
    public boolean saveImageFlag = false;
    JMathAnimConfig config;
    ArrayList<MathObject> objectsToDraw;
    public int width;
    final float zNear = 0.1f, zFar = 7000f;
//    private GL3ES3 gles;
    private GL4 gl4;
    public Camera3D camera;
    public Camera3D fixedCamera;
    public VideoEncoder videoEncoder;
    public File saveFilePath;
    private final int newLineCounter = 0;
    public int frameCount;

    private final TextRenderer textRenderer;

    //Shaders and uniform variables
    //Shader that draws thin, rounded cap lines
    ShaderLoader thinLinesShader;
    ShaderLoader fillShader;
    int unifProject;//Projection matrix
    int unifModelMat;//Model matrix
    int unifMiterLimit;//Miter limit (for rendering thin lines)
    int unifViewPort;//Viewport (for rendering thin lines)
    ShaderDrawer shaderDrawer;
    public JOGLRenderer renderer;

    public JOGLRenderQueue(JMathAnimConfig config) {
        textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
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
        // Establece el perfil de OpenGL
//        GLProfile glp = GLProfile.get(GLProfile.GL2);
//        GLCapabilities caps = new GLCapabilities(glp);
        // Habilita el multisampling (antialiasing)
//        caps.setSampleBuffers(true);
//        caps.setNumSamples(16); // Ejemplo con 4 muestras por píxel
//  GL2 gl = drawable.getGL().getGL2();

        // Habilita el multisampling en OpenGL
//        gl.glEnable(GL.GL_MULTISAMPLE);
//        gles = drawable.getGL().getGL3ES3();
        gl4 = drawable.getGL().getGL4();
        gl4.glDepthMask(true);
        gl4.glEnable(GL4.GL_DEPTH_TEST);
//        gl3.glDepthFunc(GL4.GL_LESS);
        gl4.glDepthFunc(GL4.GL_LEQUAL);
        gl4.glEnable(GL4.GL_LINE_SMOOTH);
        gl4.glEnable(GL4.GL_POLYGON_SMOOTH);
        gl4.glHint(GL4.GL_POLYGON_SMOOTH_HINT, GL4.GL_NICEST);
        gl4.glHint(GL4.GL_LINE_SMOOTH_HINT, GL4.GL_NICEST);
        gl4.glEnable(GL4.GL_BLEND);
        gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        gl4.glEnable(GL4.GL_MULTISAMPLE);
        gl4.glEnable(GL4.GL_SAMPLE_ALPHA_TO_COVERAGE);
        gl4.glDisable(GL4.GL_CULL_FACE);

        //Drawer class, we have to pass it the created shaders
        shaderDrawer = new ShaderDrawer(gl4);
        shaderDrawer.queue = this;
//        shaderDrawer.width = this.width;
//        shaderDrawer.height = this.height;
        if (useCustomShaders) {
            thinLinesShader = new ShaderLoader(gl4, "#thinLines/thinLines.vs", "#thinLines/thinLines.gs", "#thinLines/thinLines.fs");
            fillShader = new ShaderLoader(gl4, "#fill/fill.vs", "", "#fill/fill.fs");
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
            config.setSaveFilePath(saveFilePath);
            videoEncoder.createEncoder(config);
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

            // clear screen
            PaintStyle backgroundColor = config.getBackgroundColor();
            if (backgroundColor instanceof JMColor) {
                JMColor col = (JMColor) backgroundColor;
                gl4.glClearColor((float) col.r, (float) col.g, (float) col.b, (float) col.getAlpha());
            }
            gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_STENCIL_BUFFER_BIT);

            computeRectifiedVersionOfAllShapes();

            for (MathObject obj : objectsToDraw) {
                if (obj instanceof Shape) {
                    drawShape(obj);
                }
                if (obj instanceof Surface) {
                    drawSurface((Surface) obj);
                }
            }

            objectsToDraw.clear();
            gl4.glFlush();

            if (saveImageFlag) {
                savedImage = screenshot(gl4, drawable);
                saveImageFlag = false;
            }
            if (config.isCreateMovie()) {
//            BufferedImage image = screenshot(drawable);
                BufferedImage image = screenshot(gl4, drawable);
                videoEncoder.writeFrame(image, frameCount);
            }

            // Check for GL errors
            int error = gl4.glGetError();
            if (error != GL4.GL_NO_ERROR) {
                System.err.println("OpenGL Error: " + error);
            }
            busy = false;
            notify();
        }
        int error = gl4.glGetError();
        if (error != GL4.GL_NO_ERROR) {
            System.err.println("OpenGL Error: " + error);
        }
    }

    private void computeRectifiedVersionOfAllShapes() {
//        if (hasToRectify) {
            hasToRectify=false;
            objectsToDraw.parallelStream()
                    .filter(obj -> obj instanceof Shape)
                    .map(obj -> (Shape) obj)
                    .forEach(Shape::computePolygonalPieces); //Compute rectified path (need to optimize this!!)
//        }
//        int numberOfBezierCurves=0;//Number of Bezier Curves to interpolate TODO: consider straight segments case
//        for (MathObject obj : objectsToDraw) {
//            if (obj instanceof Shape) {
//                Shape shape = (Shape) obj;
//                numberOfBezierCurves+=shape.getPath().jmPathPoints.stream().filter(t->t.isThisSegmentVisible).count();
//            }
//        }

    }

    /**
     * Draw a 2D generic Shape, with thickness and fill
     *
     */
    private void drawShape(MathObject obj) {
        Shape s = (Shape) obj;
        boolean needsFill = false;//(s.getMp().getFillColor().getAlpha() > 0);

        loadProjectionViewMatrixIntoShaders();
        if (needsFill) {//TODO: This method (a stencil for each shape is EXPENSIVE)
            //If shape needs to be filled, enable stencil test
            //Mark second stencil bit for contour (will prevent filled area to overwrite this)
            gl4.glEnable(GL4.GL_STENCIL_TEST);
            gl4.glStencilMask(0xFF);
            gl4.glClear(GL4.GL_STENCIL_BUFFER_BIT);
            gl4.glStencilFunc(GL.GL_NOTEQUAL, 0b10, 0b10);//Second bit for contour
            gl4.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
        }
        if ((s.getMp().getThickness() > 0) && (s.getMp().getDrawColor().getAlpha() > 0)) {
            gl4.glUseProgram(thinLinesShader.getShader());
            shaderDrawer.thinLineShader = thinLinesShader;
            shaderDrawer.drawContour(s);
        }

        if (needsFill) {
//Draw fill
            gl4.glUseProgram(fillShader.getShader());
            shaderDrawer.fillShader = fillShader;
            shaderDrawer.drawFill(s);
            // Check for GL errors
            int error = gl4.glGetError();
            if (error != GL4.GL_NO_ERROR) {
                System.err.println("OpenGL Error: " + error);
            }
            gl4.glDisable(GL4.GL_STENCIL_TEST);
        }
    }

    private void loadProjectionViewMatrixIntoShaders() {
        Matrix4f projection;
        Matrix4f view;
        Rect bb = camera.getMathView();
        float d = (float) camera.eye.to(camera.look).norm();
        float aspectRatio = (float) (bb.getWidth() / bb.getHeight());
        projection = new Matrix4f().perspective((float) (1f * camera.fov),
                aspectRatio, // Relación de aspecto
                0.001f, // Cota de cerca
                d * 15f // Cota de lejanía
        );
        Vec up = camera.getUpVector();//Inefficient way. Improve this.
        view = new Matrix4f().lookAt(
                new Vector3f(
                        (float) camera.eye.v.x,
                        (float) camera.eye.v.y,
                        (float) camera.eye.v.z
                ), // Posición de la cámara
                new Vector3f(
                        (float) camera.look.v.x,
                        (float) camera.look.v.y,
                        (float) camera.look.v.z
                ), // Punto hacia el cual está mirando
                new Vector3f(
                        (float) up.x,
                        (float) up.y,
                        (float) up.z
                ) // Vector hacia arriba
        );
        if (useCustomShaders) {
//            projection = new Matrix4f().identity();
//            view = new Matrix4f().identity();

            gl4.glUseProgram(thinLinesShader.getShader());
            int projLoc = gl4.glGetUniformLocation(thinLinesShader.getShader(), "projection");
            int viewLoc = gl4.glGetUniformLocation(thinLinesShader.getShader(), "view");

            gl4.glUniformMatrix4fv(projLoc, 1, false, projection.get(new float[16]), 0);
            gl4.glUniformMatrix4fv(viewLoc, 1, false, view.get(new float[16]), 0);

            gl4.glUniform2f(thinLinesShader.getUniformVariable("Viewport"), (float) this.width, (float) this.height);

            gl4.glUseProgram(fillShader.getShader());
            projLoc = gl4.glGetUniformLocation(fillShader.getShader(), "projection");
            viewLoc = gl4.glGetUniformLocation(fillShader.getShader(), "view");

            gl4.glUniformMatrix4fv(projLoc, 1, false, projection.get(new float[16]), 0);
            gl4.glUniformMatrix4fv(viewLoc, 1, false, view.get(new float[16]), 0);

        }

//        
//        FloatBuffer modMat = FloatBuffer.allocate(16);
////        gl3.glGetFloatv(GL4.GL_MODELVIEW_MATRIX, modMat);
//        gl3.glUseProgram(thinLinesShader.getShader());
//        gl3.glUniformMatrix4fv(thinLinesShader.getUniformVariable("modelMatrix"), 1, false, modMat);
//        gl3.glUseProgram(fillShader.getShader());
//        gl3.glUniformMatrix4fv(fillShader.getUniformVariable("modelMatrix"), 1, false, modMat);
    }

    public BufferedImage screenshot(GL4 gl4, GLDrawable drawable) {
        AWTGLReadBufferUtil aa = new AWTGLReadBufferUtil(drawable.getGLProfile(), true);
        BufferedImage img = aa.readPixelsToBufferedImage(gl4, 0, 0, config.mediaW, config.mediaH, true);
        return img;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        this.width = w;
        this.height = h;
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

    private void drawSurface(Surface s) {

    }

}
