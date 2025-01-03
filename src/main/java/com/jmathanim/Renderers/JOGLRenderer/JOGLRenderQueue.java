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
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the queue of objects to be rendered at every frame draw
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class JOGLRenderQueue implements GLEventListener {

    private static final double MIN_THICKNESS = .2d;
    final float zNear = 0.1f, zFar = 7000f;
    private final int newLineCounter = 0;
    private final TextRenderer textRenderer;
    public boolean busy = false; //True if actually drawing
    public int height;
    public BufferedImage savedImage;
    public boolean useCustomShaders = true;
    public boolean saveImageFlag = false;
    public int width;
    public Camera3D camera;
    public Camera3D fixedCamera;
    public VideoEncoder videoEncoder;
    public File saveFilePath;
    public int frameCount;
    public JOGLRenderer renderer;
    JMathAnimConfig config;
    ArrayList<MathObject> objectsToDraw;
    //Shaders and uniform variables
    //Shader that draws thin, rounded cap lines
    ShaderLoader thinLinesShader;
    ShaderLoader fillShader;
    int unifProject;//Projection matrix
    int unifModelMat;//Model matrix
    int unifMiterLimit;//Miter limit (for rendering thin lines)
    int unifViewPort;//Viewport (for rendering thin lines)
    ShaderDrawer shaderDrawer;
    Matrix4f projectionMatrix;
    Matrix4f viewMatrix;
    private boolean hasToRectify = true;
    //    private GL3ES3 gles;
    private GL4 gl4;

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
        hasToRectify = false;
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
     */
    private void drawShape(MathObject obj) {
        Shape s = (Shape) obj;
        boolean needsFill = ((s.getMp().getFillColor().getAlpha() > 0) && (s.size() > 2));

        loadProjectionViewMatrixIntoShaders();

        if (needsFill) {//TODO: This method (a stencil for each shape is EXPENSIVE). Also: Only needed for CONCAVE shapes
            //If shape needs to be filled, enable stencil test
            //Mark second stencil bit for contour (will prevent filled area to overwrite this)
//            if (!s.isIsConvex()) {
                activateScissors(s);

                gl4.glEnable(GL4.GL_STENCIL_TEST);
                gl4.glStencilMask(0xFF);
                gl4.glClear(GL4.GL_STENCIL_BUFFER_BIT);
                gl4.glStencilFunc(GL.GL_NOTEQUAL, 0b10, 0b10);//Second bit for contour
                gl4.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
//            }
        }
        if ((s.getMp().getThickness() > 0) && (s.getMp().getDrawColor().getAlpha() > 0)) {
            gl4.glUseProgram(thinLinesShader.getShader());
            shaderDrawer.thinLineShader = thinLinesShader;
            shaderDrawer.drawContour(s);
        }

        if (needsFill) {
//            if (!s.isIsConvex()) {
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

            gl4.glDisable(GL4.GL_SCISSOR_TEST);
//            }
        }
    }

    private void activateScissors(Shape s) {
        Matrix4f mvpMatrix = new Matrix4f();
        projectionMatrix.mul(viewMatrix, mvpMatrix);
        int[] viewport = new int[]{0, 0, config.mediaW, config.mediaH};

        int[] coords = computeScissorRegion(mvpMatrix, viewport, s);

        gl4.glEnable(GL4.GL_SCISSOR_TEST);
        int gap = 0;
        gl4.glScissor(coords[0] - gap, coords[1] - gap, coords[2] + gap, coords[3] + gap);
    }

    private int[] computeScissorRegion(Matrix4f mvpMatrix, int[] viewport, Shape s) {
        Rect bb = s.getBoundingBox();//TODO: Ensure that this Box holds z information!!
        float[] p0 = new float[3];
        float[] p1 = new float[3];
        float[] p2 = new float[3];
        float[] p3 = new float[3];
        float[] p4 = new float[3];
        float[] p5 = new float[3];
        float[] p6 = new float[3];
        float[] p7 = new float[3];
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmin, bb.ymin, bb.zmin, p0);
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmin, bb.ymin, bb.zmax, p1);
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmin, bb.ymax, bb.zmin, p2);
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmin, bb.ymax, bb.zmax, p3);
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmax, bb.ymin, bb.zmin, p4);
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmax, bb.ymin, bb.zmax, p5);
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmax, bb.ymax, bb.zmin, p6);
        calculateWindowCoordinates(mvpMatrix, viewport, bb.xmax, bb.ymax, bb.zmax, p7);

        return calculateScissorParams(p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public boolean calculateWindowCoordinates(Matrix4f mvpMatrix, int[] viewport,
                                              double x, double y, double z, float[] windowCoordinates) {

        // Vector en espacio del modelo
        Vector4f modelCoords = new Vector4f((float) x, (float) y, (float) z, 1.0f);

        // Transformar a espacio recortado
        Vector4f clipCoords = new Vector4f();
        mvpMatrix.transform(modelCoords, clipCoords);

        // Normalizar las coordenadas homogéneas
        if (clipCoords.w == 0.0f) {
            return false; // No se puede dividir entre cero
        }
        clipCoords.div(clipCoords.w);

        // Transformar a espacio de ventana
        windowCoordinates[0] = ((clipCoords.x + 1.0f) * 0.5f) * viewport[2] + viewport[0];
        windowCoordinates[1] = ((clipCoords.y + 1.0f) * 0.5f) * viewport[3] + viewport[1];
        windowCoordinates[2] = (clipCoords.z + 1.0f) * 0.5f; // Profundidad normalizada

        return true;
    }

    public int[] calculateScissorParams(
            float[] p0, float[] p1, float[] p2, float[] p3,
            float[] p4, float[] p5, float[] p6, float[] p7
    ) {

        float minXa = Math.min(Math.min(p0[0], p1[0]), Math.min(p2[0], p3[0]));
        float minXb = Math.min(Math.min(p4[0], p5[0]), Math.min(p6[0], p7[0]));
        float minX = Math.min(minXa, minXb);

        float minYa = Math.min(Math.min(p0[1], p1[1]), Math.min(p2[1], p3[1]));
        float minYb = Math.min(Math.min(p4[1], p5[1]), Math.min(p6[1], p7[1]));
        float minY = Math.min(minYa, minYb);


        float maxXa = Math.max(Math.max(p0[0], p1[0]), Math.max(p2[0], p3[0]));
        float maxXb = Math.max(Math.max(p4[0], p5[0]), Math.max(p6[0], p7[0]));
        float maxX = Math.max(maxXa, maxXb);

        float maxYa = Math.max(Math.max(p0[1], p1[1]), Math.max(p2[1], p3[1]));
        float maxYb = Math.max(Math.max(p4[1], p5[1]), Math.max(p6[1], p7[1]));
        float maxY = Math.max(maxYa, maxYb);

        int scissorX = Math.round(minX);
        int scissorY = Math.round(minY);
        int scissorWidth = Math.round(maxX - minX);
        int scissorHeight = Math.round(maxY - minY);
        int gap=5;
        return new int[]{scissorX-gap, scissorY-gap, scissorWidth+gap, scissorHeight+gap};
    }

    private void loadProjectionViewMatrixIntoShaders() {

        Rect bb = camera.getMathView();
        float d = (float) camera.eye.to(camera.look).norm();
        float aspectRatio = (float) (bb.getWidth() / bb.getHeight());
        projectionMatrix = new Matrix4f().perspective((float) (1f * camera.fov),
                aspectRatio, // Relación de aspecto
                0.001f, // Cota de cerca
                d * 15f // Cota de lejanía
        );
        Vec up = camera.getUpVector();//Inefficient way. Improve this.
        viewMatrix = new Matrix4f().lookAt(
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

            gl4.glUniformMatrix4fv(projLoc, 1, false, projectionMatrix.get(new float[16]), 0);
            gl4.glUniformMatrix4fv(viewLoc, 1, false, viewMatrix.get(new float[16]), 0);

            gl4.glUniform2f(thinLinesShader.getUniformVariable("Viewport"), (float) this.width, (float) this.height);

            gl4.glUseProgram(fillShader.getShader());
            projLoc = gl4.glGetUniformLocation(fillShader.getShader(), "projection");
            viewLoc = gl4.glGetUniformLocation(fillShader.getShader(), "view");

            gl4.glUniformMatrix4fv(projLoc, 1, false, projectionMatrix.get(new float[16]), 0);
            gl4.glUniformMatrix4fv(viewLoc, 1, false, viewMatrix.get(new float[16]), 0);

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
