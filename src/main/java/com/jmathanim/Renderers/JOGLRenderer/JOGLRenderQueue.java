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
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
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

    private static final double MIN_THICKNESS = .2d;

    JMathAnimConfig config;
    ArrayList<Shape> shapes;
    GLU glu;
    GLUgl2 glu2;
    final float zNear = 0.1f, zFar = 7000f;
    GL2ES2 gles2;
    private GL2 gl;
    private Camera3D camera;
    public Camera3D fixedCamera;
    public VideoEncoder videoEncoder;
    public File saveFilePath;
    private int newLineCounter = 0;
    public int frameCount;
    private int unifProject;
    private int unifScal;
    private float scaVal;
    private int shaderprogram;
    private int unifModelMat;
    private int unifColor;

    public JOGLRenderQueue(JMathAnimConfig config) {
        this.config = config;
        shapes = new ArrayList<>();

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
        gles2 = drawable.getGL().getGL2ES2();
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        glu2 = new GLUgl2();

        gles2.setSwapInterval(1);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gles2.glEnable(GL.GL_DEPTH_TEST);
        gles2.glEnable(GL.GL_BLEND);
        gles2.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
        gl.glDepthFunc(GL.GL_LEQUAL);

        gles2.glEnable(GL2.GL_LINE_SMOOTH);
        gles2.glEnable(GL2.GL_POLYGON_SMOOTH);
        gles2.glEnable(GL2.GL_POINT_SMOOTH);
        gles2.glHint(GL2.GL_POINT_SMOOTH, GL2.GL_NICEST);
        gles2.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
        gles2.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gles2.glEnable(GL2.GL_BLEND);
        gles2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gles2.glEnable(GL2.GL_MULTISAMPLE);
        gles2.glEnable(GL2.GL_SAMPLE_ALPHA_TO_COVERAGE);
        try {
            loadShaders();
        } catch (IOException ex) {
            Logger.getLogger(JOGLRenderQueue.class.getName()).log(Level.SEVERE, null, ex);
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

    public void addShapeToQueue(Shape s) {
        shapes.add(s);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        adjustCameraView(drawable);
        // clear screen
        PaintStyle backgroundColor = config.getBackgroundColor();
        if (backgroundColor instanceof JMColor) {
            JMColor col = (JMColor) backgroundColor;
            gles2.glClearColor((float) col.r, (float) col.g, (float) col.b, (float) col.getAlpha());
        }
        gles2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        for (Shape s : shapes) {
            drawFill(s);
            drawShape(s);
        }
        shapes.clear();
        gl.glFlush();

        if (config.isCreateMovie()) {
//            BufferedImage image = screenshot(drawable);
            BufferedImage image = screenshot3(gl, drawable);
            videoEncoder.writeFrame(image, frameCount);
        }

//        screenshot2(gl2);
    }

    public BufferedImage screenshot3(GL2 gl2, GLDrawable drawable) {
        AWTGLReadBufferUtil aa = new AWTGLReadBufferUtil(drawable.getGLProfile(), true);
        BufferedImage img = aa.readPixelsToBufferedImage(gl2, 0, 0, config.mediaW, config.mediaH, true);
        return img;
    }

    public void screenshot2(GL2 gl2) {
        GLReadBufferUtil util = new GLReadBufferUtil(false, true);
        util.readPixels(gl2, false);
        Texture texture = util.getTexture();
    }
//Sloooooooooooooooooooooooooooooooooooooooooooooooooooooooooow

    public BufferedImage screenshot(GLDrawable drawable) {
        int width = config.mediaW;
        int height = config.mediaH;
        BufferedImage screenshot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = screenshot.getGraphics();
        ByteBuffer buffer = ByteBuffer.allocate(1919998);

        gl.glReadPixels(0, 0, width, height, GL.GL_RGB, GL.GL_BYTE, buffer);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                // The color are the three consecutive bytes, it's like referencing
                // to the next consecutive array elements, so we got red, green, blue..
                // red, green, blue, and so on..
                graphics.setColor(new java.awt.Color(buffer.get() * 2, buffer.get() * 2, buffer.get() * 2));
                graphics.drawRect(w, height - h, 1, 1); // height - h is for flipping the image
            }
        }
        return screenshot;
    }

    private void drawShape(Shape s) {
        gl.glPushAttrib(GL2.GL_ENABLE_BIT);
        processDrawingStyle(s);

        JMPath path = s.getPath();

        gl.glBegin(GL2.GL_LINE_STRIP);

        for (int n = 0; n <= path.size(); n++) {//TODO: This needs to improve A LOT
            JMPathPoint p1 = path.getJMPoint(n);
            JMPathPoint p2 = path.getJMPoint(n + 1);
            if (p2.isThisSegmentVisible) {
                int num = (p2.isCurved ? 30 : 1);
//            if (true) {
//                drawBezierSegment(num, p1, p2);
//                    double[] po = new double[]{p.v.x, p.v.y, p.v.z};
                for (int k = 0; k <= num; k++) {//TODO: Optimize this for straight segments
                    Point p = JMPath.getJMPointBetween(p1, p2, 1d * k / num).p;
//                Point p = p1.p;
                    gl.glVertex3d(p.v.x, p.v.y, p.v.z);
                }
            } else {
                gl.glEnd();
                gl.glBegin(GL2.GL_LINE_STRIP);
            }

        }
        gl.glEnd();
        gl.glPopAttrib();

//          gl2.glPointSize(thickness*.5f);
//            gl2.glBegin(GL2.GL_POINTS);
//        for (int n = 0; n <= path.size(); n++) {//TODO: This needs to improve A LOT
//            JMPathPoint p = path.getJMPoint(n);
//            if (p.isThisSegmentVisible) {
//                 gl2.glVertex3d(p.p.v.x, p.p.v.y, p.p.v.z);
//            }
//        }
//          gl2.glEnd();
    }

    private void processDrawingStyle(Shape s) {
        //Thickness...
        final float thickness = computeThickness(s);
        gl.glLineWidth(thickness);

        //Color... (TODO: implement gradients)
        PaintStyle drawColor = s.getMp().getDrawColor();
        if (drawColor instanceof JMColor) {
            JMColor col = (JMColor) drawColor;
            gl.glColor4d(col.r, col.g, col.b, col.getAlpha());
//           gl.glUniform4f(unifColor,(float)col.r, (float)col.g, (float)col.b, (float)col.getAlpha());
        }

        //Dash styles...
        MODrawProperties.DashStyle dash = s.getMp().getDashStyle();
        switch (dash) {//Default implementation is not good for thick lines!!!
            case SOLID:
                //Do nothing!
                break;
            case DASHED:
                gl.glLineStipple(1, (short) 0b1111111111000000);
                gl.glEnable(GL2.GL_LINE_STIPPLE);
                break;
            case DOTTED:
                gl.glLineStipple(1, (short) 0b0000011111100000);
                gl.glEnable(GL2.GL_LINE_STIPPLE);
                break;
            case DASHDOTTED:
                gl.glLineStipple(1, (short) 0b1111111000010000);
                gl.glEnable(GL2.GL_LINE_STIPPLE);
                break;
        }
    }

    private void drawFill(Shape sh) {
        JMColor col = (JMColor) sh.getMp().getFillColor();
        if (col.getAlpha() == 0) {
            return;
        }
        //Fill
        gl.glPolygonMode(com.jogamp.opengl.GL.GL_FRONT_AND_BACK, com.jogamp.opengl.GL.GL_POINTS);
//        gl2.glDisable(com.jogamp.opengl.GL.GL_DEPTH_TEST);
//        gl2.glDisable(com.jogamp.opengl.GL.GL_CULL_FACE);
        GLUtessellator tess = GLUtessellatorImpl.gluNewTess();
        GLUtessellatorCallback tessbegin = new TessellatorCallback(glu2, gl);

        glu.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, tessbegin);
        glu.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, tessbegin);
        glu.gluTessCallback(tess, GLU.GLU_TESS_END, tessbegin);
        glu.gluTessCallback(tess, GLU.GLU_TESS_COMBINE, tessbegin);

        gl.glColor4d(col.r, col.g, col.b, col.getAlpha());
        GLUgl2.gluTessBeginPolygon(tess, null);
        GLUgl2.gluTessBeginContour(tess);
        //This draws only vertices
//        for (int n = 0; n <= sh.getPath().size(); n++) {
//            Point p = sh.getPoint(n);//.getJMPoint(n).p;
//            double[] po = new double[]{p.v.x, p.v.y, p.v.z};
//            GLUgl2.gluTessVertex(tess, po, 0, po);
//        }
        int num = 20;
        JMPath path = sh.getPath();
        for (int n = 0; n < path.size(); n++) {
            for (int k = 0; k <= num; k++) {
                JMPathPoint p1 = path.getJMPoint(n);
                JMPathPoint p2 = path.getJMPoint(n + 1);
//                if (true) {
                if (p2.isThisSegmentVisible) {
                    Point p = JMPath.getJMPointBetween(p1, p2, 1d * k / num).p;
//                Point p = p1.p;

                    double[] po = new double[]{p.v.x, p.v.y, p.v.z};
                    GLUgl2.gluTessVertex(tess, po, 0, po);
                } else {
                    GLUgl2.gluTessEndContour(tess);
                    GLUgl2.gluTessBeginContour(tess);

                }
            }
        }

        GLUgl2.gluTessEndContour(tess);
        GLUgl2.gluTessEndPolygon(tess);
        GLUgl2.gluDeleteTess(tess);
    }

    private void drawBezierSegment(int num, JMPathPoint p1, JMPathPoint p2) {
//        for (int k = 0; k < num; k++) {
//            JMPathPoint p = JMPath.getJMPointBetween(p1, p2, 1d * k / num);
//            gl2.glVertex3d(p.p.v.x, p.p.v.y, p.p.v.z);
//        }
        //Prueba de evaluator
        double[][] punticos = new double[][]{
            {p1.p.v.x, p1.p.v.y, p1.p.v.z},
            {p1.cpExit.v.x, p1.cpExit.v.y, p1.cpExit.v.z},
            {p2.cpEnter.v.x, p2.cpEnter.v.y, p2.cpEnter.v.z},
            {p2.p.v.x, p2.p.v.y, p2.p.v.z}
        };
        DoubleBuffer ctrlpointBuf = GLBuffers.newDirectDoubleBuffer(punticos[0].length
                * punticos.length);
        for (int i = 0; i < punticos.length; i++) {
            ctrlpointBuf.put(punticos[i]);
        }
        ctrlpointBuf.rewind();

        gl.glMap1d(GL2.GL_MAP1_VERTEX_3, 0f, 1f, 3, 4, ctrlpointBuf);
        gl.glEnable(GL2.GL_MAP1_VERTEX_3);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for (int i = 0; i <= num; i++) {
            gl.glEvalCoord1d(1f * i / num);
        }
        gl.glEnd();
//        gl2.glPointSize(3);
//        gl2.glBegin(GL.GL_POINTS);
//        for (int i = 0; i < punticos.length; i += 3) {
//            gl2.glVertex3d(punticos[i][0], punticos[i][1], punticos[i][2]);
//        }
//        gl2.glEnd();
//        

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
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
//        for (int n = 0; n < 16; n++) {
//            mat.put(n, 0);
//        }
//        mat.put(0, 1f);
//        mat.put(5, 1f);
//        mat.put(10, 1f);
//        mat.put(15, 1f);

        gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projMat);
        gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modMat);
        gl2.glUniformMatrix4fv(unifProject, 1, false, projMat);
        gl2.glUniformMatrix4fv(unifModelMat, 1, false, modMat);
        
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

    public void loadShaders() throws IOException {
        int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        int f = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);

        ResourceLoader rl = new ResourceLoader();
        URL urlVS = rl.getResource("#default.vs", "shaders");
        BufferedReader brv = new BufferedReader(new FileReader(urlVS.getFile()));
        String vsrc = "";
        String line;
        while ((line = brv.readLine()) != null) {
            vsrc += line + "\n";
        }
        gl.glShaderSource(v, 1, new String[]{vsrc}, null);
        gl.glCompileShader(v);
        IntBuffer ib = IntBuffer.allocate(1);
        gl.glGetShaderiv(v, GL2.GL_COMPILE_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
            System.out.println("VAMOS A VER, QUE DA ERROR AL COMPILAR V");
        }

        URL urlFS = rl.getResource("#default.fs", "shaders");
        BufferedReader brf = new BufferedReader(new FileReader(urlFS.getFile()));
        String fsrc = "";
        while ((line = brf.readLine()) != null) {
            fsrc += line + "\n";
        }
        gl.glShaderSource(f, 1, new String[]{fsrc}, null);
        gl.glCompileShader(f);
        ib = IntBuffer.allocate(1);
        gl.glGetShaderiv(f, GL2.GL_COMPILE_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
            System.out.println("VAMOS A VER, QUE DA ERROR AL COMPILAR F");
        }
         shaderprogram = gl.glCreateProgram();
        gl.glAttachShader(shaderprogram, v);
        gl.glAttachShader(shaderprogram, f);

        gl.glLinkProgram(shaderprogram);

        ib = IntBuffer.allocate(1);
        gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
            System.out.println("ERROR AL LINKEAR");
        }

        gl.glValidateProgram(shaderprogram);
        //Uncomment this for tweaking shaders
        gl.glUseProgram(shaderprogram);
        unifProject = gl.glGetUniformLocation(shaderprogram, "projection");
        unifModelMat = gl.glGetUniformLocation(shaderprogram, "modelMat");
        unifColor = gl.glGetUniformLocation(shaderprogram, "currentColor");
        System.out.println("projection at " + unifProject);
        System.out.println("modelMat at " + unifModelMat);

        ib = IntBuffer.allocate(1);
        //Print attributes
        gl.glGetProgramiv(shaderprogram, GL2.GL_ACTIVE_ATTRIBUTES, ib);
        System.out.println("Hay " + ib.get(0) + " atributos");

        for (int n = 0; n < ib.get(0); n++) {
            IntBuffer ib1 = IntBuffer.allocate(16);
            IntBuffer ib2 = IntBuffer.allocate(16);
            IntBuffer ib3 = IntBuffer.allocate(16);
            ByteBuffer bb = ByteBuffer.allocate(16);
            gl.glGetActiveAttrib(shaderprogram, n, 16, ib1, ib2, ib3, bb);
            System.out.println("Attribute " + n + ": type " + ib3.get(0) + ", name: " + StandardCharsets.UTF_8.decode(bb).toString());
        }

    }
}
