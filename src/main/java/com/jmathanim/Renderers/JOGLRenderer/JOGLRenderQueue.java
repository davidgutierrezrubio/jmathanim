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
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.MovieEncoders.VideoEncoder;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
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
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
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

    JMathAnimConfig config;
    ArrayList<Shape> shapes;
    GLU glu;
    GLUgl2 glu2;
    final float zNear = 0.1f, zFar = 7000f;
    GL2ES2 gles2;
    private GL2 gl2;
    private Camera camera;
    public VideoEncoder videoEncoder;
    public File saveFilePath;
    private int newLineCounter = 0;
    public int frameCount;

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
        gl2 = drawable.getGL().getGL2();
        glu = new GLU();
        glu2 = new GLUgl2();
        
        gles2.setSwapInterval(1);
        gl2.glEnable(GL.GL_DEPTH_TEST);
        gles2.glEnable(GL.GL_DEPTH_TEST);
        gles2.glEnable(GL.GL_BLEND);
        gles2.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
        gl2.glDepthFunc(GL.GL_LEQUAL);

        gles2.glEnable(GL2.GL_LINE_SMOOTH);
        gles2.glEnable(GL2.GL_POLYGON_SMOOTH);
        gles2.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
        gles2.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gles2.glEnable(GL2.GL_BLEND);
        gles2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gles2.glEnable(GL2.GL_MULTISAMPLE);

       

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
        gl2.glFlush();

        if (config.isCreateMovie()) {
//            BufferedImage image = screenshot(drawable);
            BufferedImage image = screenshot3(gl2,drawable);
            videoEncoder.writeFrame(image, frameCount);
        }

//        screenshot2(gl2);
    }
public BufferedImage screenshot3(GL2 gl2,GLDrawable drawable) {
        AWTGLReadBufferUtil aa = new AWTGLReadBufferUtil(drawable.getGLProfile(), true);
        BufferedImage img = aa.readPixelsToBufferedImage(gl2, 0,0,config.mediaW,config.mediaH,true);
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

        gl2.glReadPixels(0, 0, width, height, GL.GL_RGB, GL.GL_BYTE, buffer);

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
        PaintStyle drawColor = s.getMp().getDrawColor();
        if (drawColor instanceof JMColor) {
            JMColor col = (JMColor) drawColor;
            gl2.glColor4d(col.r, col.g, col.b, col.getAlpha());
        }

        JMPath path = s.getPath();
//        for (int n = 0; n < path.size()+1; n++) {
//            JMPathPoint p = path.getJMPoint(n);
//            gl2.glVertex3d(p.p.v.x, p.p.v.y, p.p.v.z);
//        }
        gl2.glBegin(GL2.GL_LINE_STRIP);

        int num = 30;
        for (int n = 0; n <= path.size(); n++) {//TODO: This needs to improve A LOT
            JMPathPoint p1 = path.getJMPoint(n);
            JMPathPoint p2 = path.getJMPoint(n + 1);
            if (p2.isThisSegmentVisible) {
//            if (true) {
//                drawBezierSegment(num, p1, p2);
//                    double[] po = new double[]{p.v.x, p.v.y, p.v.z};
                for (int k = 0; k <= num; k++) {
                    Point p = JMPath.getJMPointBetween(p1, p2, 1d * k / num).p;
//                Point p = p1.p;
                    gl2.glVertex3d(p.v.x, p.v.y, p.v.z);
                }
            } else {
                gl2.glEnd();
                gl2.glBegin(GL2.GL_LINE_STRIP);
            }

        }
        gl2.glEnd();

    }

    private void drawFill(Shape sh) {
        JMColor col = (JMColor) sh.getMp().getFillColor();
        if (col.getAlpha() == 0) {
            return;
        }
        //Fill
        gl2.glPolygonMode(com.jogamp.opengl.GL.GL_FRONT_AND_BACK, com.jogamp.opengl.GL.GL_POINTS);
//        gl2.glDisable(com.jogamp.opengl.GL.GL_DEPTH_TEST);
//        gl2.glDisable(com.jogamp.opengl.GL.GL_CULL_FACE);
        GLUtessellator tess = GLUtessellatorImpl.gluNewTess();
        GLUtessellatorCallback tessbegin = new Tessbegin(glu2, gl2);

        glu.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, tessbegin);
        glu.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, tessbegin);
        glu.gluTessCallback(tess, GLU.GLU_TESS_END, tessbegin);
        glu.gluTessCallback(tess, GLU.GLU_TESS_COMBINE, tessbegin);

        gl2.glColor4d(col.r, col.g, col.b, col.getAlpha());
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
                if (true) {
//                if (p2.isThisSegmentVisible) {
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

        gl2.glMap1d(GL2.GL_MAP1_VERTEX_3, 0f, 1f, 3, 4, ctrlpointBuf);
        gl2.glEnable(GL2.GL_MAP1_VERTEX_3);
        gl2.glBegin(GL2.GL_LINE_STRIP);
        for (int i = 0; i <= num; i++) {
            gl2.glEvalCoord1d(1f * i / num);
        }
        gl2.glEnd();
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
//        gl2.glViewport(x, y, w, h);
////        gl2.glOrtho(-1, 1, -1, 1, 0, 100);
//        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
//        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
////        gl2.glLoadIdentity();
////        glu.gluPerspective(60.0f , (float) w / h , 0.1f , 10000.0f);
        gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
////        gl2.glLoadIdentity();
//
        gl2.glLoadIdentity();
        Rect bb = camera.getMathView();
//        gl2.glOrtho(bb.xmin, bb.xmax, bb.ymin, bb.ymax, -5, 5);

        glu.gluPerspective(30.0f, (float) bb.getWidth() / bb.getHeight(), 0.1f, 10000.0f);
        glu.gluLookAt(0, -6, 2, 0, 0, 0, 0, 1, 0);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

}

class Tessbegin implements GLUtessellatorCallback {

    private final GL2 gl;
    private final GLUgl2 glu;

    public Tessbegin(GLUgl2 glu, GL2 gl) {
        this.glu = glu;
        this.gl = gl;
    }

    @Override
    public void begin(int type) {
        gl.glBegin(type);

    }

    @Override
    public void beginData(int type, Object polygonData) {
    }

    @Override
    public void edgeFlag(boolean boundaryEdge) {
    }

    @Override
    public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
    }

    @Override
    public void vertex(Object vertexData) {
        double[] data = (double[]) vertexData;
        gl.glVertex3d(data[0], data[1], data[2]);
    }

    @Override
    public void vertexData(Object vertexData, Object polygonData) {
    }

    @Override
    public void end() {
        gl.glEnd();
    }

    @Override
    public void endData(Object polygonData) {
        System.out.println("EndData " + polygonData);
    }

    @Override
    public void combine(double[] doubles, Object[] os, float[] weight, Object[] outData) {
//        double[] p0=(double[])os[0];
//        double[] p1=(double[])os[1];
//        double[] p2=(double[])os[2];
//        double[] p3=(double[])os[3];

//        double x=weight[0]*p0[0]+weight[1]*p1[0]+weight[2]*p2[0]+weight3*p3[0];
        outData[0] = new double[]{doubles[0], doubles[1], doubles[2]};
    }

    @Override
    public void combineData(double[] coords, Object[] data, float[] weight, Object[] outData, Object polygonData) {
    }

    @Override
    public void error(int errnum) {
    }

    @Override
    public void errorData(int errnum, Object polygonData) {
    }

}
