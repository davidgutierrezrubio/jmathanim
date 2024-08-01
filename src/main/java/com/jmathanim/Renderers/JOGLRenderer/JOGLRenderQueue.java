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
import com.jmathanim.mathobjects.surface.Surface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 * Manages the queue of objects to be rendered at every frame draw
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class JOGLRenderQueue implements GLEventListener {

    public boolean busy = false; //True if actually drawing
    private static final double MIN_THICKNESS = .2d;
    private int height;
    public BufferedImage savedImage;
    public boolean useCustomShaders = true;
    public boolean saveImageFlag = false;
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
        gl3 = drawable.getGL().getGL3();
        gl2 = drawable.getGL().getGL2();
        glu = new GLU();
        gl3.glEnable(GL.GL_DEPTH_TEST);
        gl3.glDepthFunc(GL.GL_LEQUAL);
        gl3.glEnable(GL3.GL_LINE_SMOOTH);
        gl3.glEnable(GL3.GL_POLYGON_SMOOTH);
        gl3.glHint(GL3.GL_POLYGON_SMOOTH_HINT, GL3.GL_NICEST);
        gl3.glHint(GL3.GL_LINE_SMOOTH_HINT, GL3.GL_NICEST);
        gl3.glEnable(GL3.GL_BLEND);
        gl3.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl3.glEnable(GL3.GL_MULTISAMPLE);
        gl3.glEnable(GL3.GL_SAMPLE_ALPHA_TO_COVERAGE);
        gl2.glEnable(GL2.GL_MULTISAMPLE);
        gl2.glEnable(GL2.GL_SAMPLE_ALPHA_TO_COVERAGE);

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

            Vec vcamera = camera.look.to(camera.eye);
            float vx = (float) (vcamera.x);
            float vy = (float) (vcamera.y);
            float vz = (float) (vcamera.z);

            Vec vcameraRoll = Vec.to(vcamera.y, vcamera.x);
            Vec vcameraYaw = Vec.to(vcamera.z, Math.sqrt(vcamera.y * vcamera.y + vcamera.x * vcamera.x));
            double yaw = vcameraYaw.getAngle();
            double roll = vcameraRoll.getAngle();

            for (MathObject obj : objectsToDraw) {
                if (obj instanceof Shape) {
                    drawShape(obj, roll, yaw);
                    if (!"".equals(obj.getDebugText())) {
                        gl3.glUseProgram(0);
                        textRenderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
                        textRenderer.setColor(java.awt.Color.red); // Color rojo
                        textRenderer.draw(obj.getDebugText(), 1, 1);
                        textRenderer.endRendering();
                    }
                }
                if (obj instanceof Surface) {
                    drawSurface((Surface) obj);
                }
            }

            objectsToDraw.clear();
            gl3.glFlush();

            if (saveImageFlag) {
                savedImage = screenshot(gl3, drawable);
                saveImageFlag = false;
            }
            if (config.isCreateMovie()) {
//            BufferedImage image = screenshot(drawable);
                BufferedImage image = screenshot(gl3, drawable);
                videoEncoder.writeFrame(image, frameCount);
            }
            busy = false;
            notify();
        }

    }

    /**
     * Draw a 2D generic Shape, with thickness and fill
     *
     */
    private void drawShape(MathObject obj, double roll, double yaw) {
        //Convex, filled-> Not 2º stencil buffer
        //Thickness=1, not filled-> No thin shader, no fill method, no stencil
        Shape s = (Shape) obj;
        gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl2.glLoadIdentity();
        if (s.getMp().isFaceToCamera()) {

            final Vec center = s.getMp().getFaceToCameraPivot();//s.getCenter();
            float cx = (float) center.x;
            float cy = (float) center.y;
            float cz = (float) center.z;

            //Compute model view matrix so that faces to the camera
            gl2.glTranslatef(cx, cy, cz);
            if (roll != 0) {
                gl2.glRotatef((float) (180 - roll * 180 / PI), 0, 0, 1);
            }
            gl2.glRotatef((float) (yaw * 180 / PI), 1, 0, 0);
            gl2.glTranslatef(-cx, -cy, -cz);
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
        shaderDrawer.drawFill(s, pieces);
//                    shaderDrawer.drawFillSlowButWorking(s, pieces);
        gl3.glDisable(GL.GL_STENCIL_TEST);
    }

    private void loadModelMatrixIntoShaders() {
        FloatBuffer modMat = FloatBuffer.allocate(16);
        gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modMat);
        gl2.glUseProgram(thinLinesShader.getShader());
        gl2.glUniformMatrix4fv(thinLinesShader.getUniformVariable("modelMatrix"), 1, false, modMat);
        gl2.glUseProgram(fillShader.getShader());
        gl2.glUniformMatrix4fv(fillShader.getUniformVariable("modelMatrix"), 1, false, modMat);
    }

    private void printModelMatrix() {//For debugging purposes
        FloatBuffer modMat = FloatBuffer.allocate(16);
        gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modMat);
        System.out.println("-----");
        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) {
                System.out.println("");
            }
            System.out.print(modMat.get(i) + " ");

        }
        System.out.println("----");
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
            float d = (float) camera.eye.to(camera.look).norm();
            glu.gluPerspective(camera.fov, (float) bb.getWidth() / bb.getHeight(), .1f, d * 1.5f);

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

    private void drawSurface(Surface s) {

    }
}
