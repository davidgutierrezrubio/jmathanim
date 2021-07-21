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
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jogamp.graph.curve.OutlineShape;
import com.jogamp.graph.curve.Region;
import com.jogamp.graph.curve.opengl.GLRegion;
import com.jogamp.graph.curve.opengl.RegionRenderer;
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.geom.SVertex;
import com.jogamp.graph.geom.Vertex;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUnurbs;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.PMVMatrix;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import jogamp.opengl.glu.gl2.nurbs.GLUgl2nurbsImpl;
import jogamp.opengl.glu.tessellator.GLUtessellatorImpl;

/**
 * Manages the queue of objects to be rendered at every frame draw
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class JOGLRenderQueue implements GLEventListener {

    JMathAnimConfig config;
    // these will define a shape that is defined once at init
    RenderState renderState;
    RegionRenderer regionRenderer;
    GLRegion glRegion;
    private ArrayList<GLRegion> regions;
    ArrayList<Shape> shapes;
    volatile float weight = 1.0f;
    GLU glu;
    GLUgl2 glu2;
    final float zNear = 0.1f, zFar = 7000f;

    /* 2nd pass texture size antialias SampleCount
           4 is usually enough */
    private final int[] sampleCount = new int[]{2};

    /* variables used to update the PMVMatrix before rendering */
    private float xTranslate = -40f;
    private float yTranslate = 0f;
    private float zTranslate = -100f;

    private final int renderModes = Region.VARWEIGHT_RENDERING_BIT;
    GL2ES2 gles2;
    private double alpha;
    private GL2 gl2;
    private Camera camera;

    public void setConfig(JMathAnimConfig config) {
        this.config = config;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        alpha = 0;
        gles2 = drawable.getGL().getGL2ES2();
        gl2 = drawable.getGL().getGL2();
        glu = new GLU();
        glu2 = new GLUgl2();
        regions = new ArrayList<>();
        shapes = new ArrayList<>();

        gles2.setSwapInterval(1);
        gles2.glEnable(GL.GL_DEPTH_TEST);
        gles2.glEnable(GL.GL_BLEND);
        gles2.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);

        gles2.glEnable(GL2.GL_LINE_SMOOTH);
        gles2.glEnable(GL2.GL_POLYGON_SMOOTH);
        gles2.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
        gles2.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gles2.glEnable(GL2.GL_BLEND);
        gles2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gles2.glEnable(GL2.GL_MULTISAMPLE);
        /* initialize OpenGL specific classes that know how to render the graph API shapes */
        renderState = RenderState.createRenderState(SVertex.factory());
        // define a colour to render our shape with
        renderState.setColorStatic(1.0f, 1.0f, 1.0f, .5f);
        renderState.setHintMask(RenderState.BITHINT_GLOBAL_DEPTH_TEST_ENABLED);

        regionRenderer = RegionRenderer.create(renderState, /* GLCallback */ RegionRenderer.defaultBlendEnable, /* GLCallback */ RegionRenderer.defaultBlendDisable);

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        final GL2ES2 gl = drawable.getGL().getGL2ES2();
        regionRenderer.destroy(gl);
        glRegion.destroy(gl);
    }

    public void addShapeToQueue(Shape s) {
//        final JMPath path = s.getPath();
//        float[] buffer = new float[path.size() * 9];
//        int k = 0;
//        for (int n = 0; n < path.size(); n++) {
//            JMPathPoint p = path.getJMPoint(n);
//            buffer[k] = (float) p.p.v.x;
//            buffer[k + 1] = (float) p.p.v.y;
//            buffer[k + 2] = (float) p.p.v.z;
//            k += 3;
//            buffer[k] = (float) p.cpExit.v.x;
//            buffer[k + 1] = (float) p.cpExit.v.y;
//            buffer[k + 2] = (float) p.cpExit.v.z;
//            k += 3;
//
//            buffer[k] = (float) p.cpEnter.v.x;
//            buffer[k + 1] = (float) p.cpEnter.v.y;
//            buffer[k + 2] = (float) p.cpEnter.v.z;
//            k += 3;
//
////         OutlineShape outlineShape = createOutlineShapeFromShape(s);
//            gles2.getGL2().glMap1f(GL2.GL_MAP1_VERTEX_3, 0f, 1f, 3, 4, buffer, 0);
//        }
        shapes.add(s);
//        addShapeFill(s);

    }

//    public void addShapeFill(Shape s) {
////        OutlineShape outlineShape = createOutlineShapeFromShape(s);
////        GLRegion reg = GLRegion.create(/* RenderModes */renderModes, /* TextureSequence */ null);
////        reg.addOutlineShape(outlineShape, null, reg.hasColorChannel() ? renderState.getColorStatic(new float[4]) : null);
////        regions.add(reg);
////
//////           regionRenderer.enable(gl, true);
//////        reg.draw(gl, regionRenderer, sampleCount);
//////   regionRenderer.enable(gl, false);
//    }
//    private OutlineShape createOutlineShapeFromShape(Shape s) {
//        OutlineShape outlineShape = new OutlineShape(renderState.getVertexFactory());
//        double L = .75;
//        JMPath path = s.getPath();
//        for (int n = 0; n < path.size(); n++) {
//            JMPathPoint p = path.getJMPoint(n);
//            outlineShape.addVertex((float) ((1 - L) * p.p.v.x + L * p.cpEnter.v.x), (float) ((1 - L) * p.p.v.y + L * p.cpEnter.v.y), false);
//            outlineShape.addVertex((float) p.p.v.x, (float) p.p.v.y, true);
////            outlineShape.addVertex((float) p.cpExit.v.x, (float) p.cpExit.v.y, false);
//            outlineShape.addVertex((float) ((1 - L) * p.p.v.x + L * p.cpExit.v.x), (float) ((1 - L) * p.p.v.y + L * p.cpExit.v.y), false);
//
//        }
//        return outlineShape;
//    }
    @Override
    public void display(GLAutoDrawable drawable) {

        // clear screen
        PaintStyle backgroundColor = config.getBackgroundColor();
        if (backgroundColor instanceof JMColor) {
            JMColor col = (JMColor) backgroundColor;
            gles2.glClearColor((float) col.r, (float) col.g, (float) col.b, (float) col.getAlpha());
        }
        gles2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

//        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        // the RegionRenderer PMVMatrix define where we want to render our shape
//        pmv.glTranslatef(xTranslate, yTranslate, zTranslate);
//        if (weight != regionRenderer.getRenderState().getWeight()) {
//            regionRenderer.getRenderState().setWeight(weight);
//        }
//        regionRenderer.enable(gles2, true);
//        for (GLRegion r : regions) {
//            r.draw(gles2, regionRenderer, sampleCount);
//        }
//        regionRenderer.enable(gles2, false);
        //Pruebas para intentar dibujar un contorno...
        for (Shape s : shapes) {
            drawFill(s);
            drawShape(s);
        }

//        regions.clear();
        shapes.clear();

//        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//        gl2.glLoadIdentity();
//       gl2.glRotated(alpha, 0, 0, 1);
//       gl2.glRotated(45, 1, 1, 0);
//        alpha+=1;
       
//        alpha+=.02;
        final GLUgl2nurbsImpl glUgl2nurbsImpl = new GLUgl2nurbsImpl();

//        glu2.gluBeginCurve(glUgl2nurbsImpl);
//        int nknots = 6;
//        float[] knots = new float[]{0, 0, 0, 1, 1, 1};
//        int stride = 4;
//        float[] points = new float[]{0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0};
//        int order = 4;
//        int type = 0;
//        glu2.gluNurbsCurve(glUgl2nurbsImpl, nknots, knots, stride, points, order, type);
//
//        glu2.gluEndCurve(glUgl2nurbsImpl);
        gl2.glFlush();
    }

    private void drawShape(Shape s) {
        PaintStyle drawColor = s.getMp().getDrawColor();
        if (drawColor instanceof JMColor) {
            JMColor col = (JMColor) drawColor;
            gl2.glColor4d(col.r, col.g, col.b, col.getAlpha());
        }
        gl2.glBegin(GL2.GL_LINE_STRIP);
        JMPath path = s.getPath();
//        for (int n = 0; n < path.size()+1; n++) {
//            JMPathPoint p = path.getJMPoint(n);
//            gl2.glVertex3d(p.p.v.x, p.p.v.y, p.p.v.z);
//        }
        int num = 30;
        for (int n = 0; n <= path.size(); n++) {//TODO: This needs to improve A LOT
            JMPathPoint p1 = path.getJMPoint(n);
            JMPathPoint p2 = path.getJMPoint(n + 1);
            if (p2.isThisSegmentVisible) {
//            if (true) {
                drawBezierSegment(num, p1, p2);
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
        gl2.glDisable(com.jogamp.opengl.GL.GL_DEPTH_TEST);
        gl2.glDisable(com.jogamp.opengl.GL.GL_CULL_FACE);
        gl2.glColor3d(1, 1, 1);
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
            for (int k = 0; k < num; k++) {
                JMPathPoint p1 = path.getJMPoint(n);
                JMPathPoint p2 = path.getJMPoint(n + 1);
                Point p = JMPath.getJMPointBetween(p1, p2, 1d * k / num).p;
//                Point p = p1.p;

                double[] po = new double[]{p.v.x, p.v.y, p.v.z};
                GLUgl2.gluTessVertex(tess, po, 0, po);
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
        final PMVMatrix pmv = regionRenderer.getMatrix();
//        regionRenderer.reshapePerspective(45.0f, w, h, zNear, zFar);
//        pmv.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//        pmv.glLoadIdentity();
//        
//        pmv.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//        pmv.glLoadIdentity();
        pmv.glScalef(2f / (2 + 2), 2f / (1.125f + 1.125f), 1);

        final GL2 gl2 = drawable.getGL().getGL2();
        gl2.glViewport(x, y, w, h);
//        gl2.glOrtho(-1, 1, -1, 1, 0, 100);
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//        gl2.glLoadIdentity();
//        glu.gluPerspective(60.0f , (float) w / h , 0.1f , 10000.0f);
//        gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//        gl2.glLoadIdentity();
//       
         gl2.glLoadIdentity();
        Rect bb = config.getCamera().getMathView();
        gl2.glOrtho(bb.xmin, bb.xmax, bb.ymin, bb.ymax, -5, 5);
//         gl2.glRotated(45, 1, 0, 1);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edgeFlag(boolean boundaryEdge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertex(Object vertexData) {
        double[] data = (double[]) vertexData;
        gl.glVertex3d(data[0], data[1], data[2]);
    }

    @Override
    public void vertexData(Object vertexData, Object polygonData) {
        System.out.println("Vertexdata " + vertexData);
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
    public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
    }

    @Override
    public void combineData(double[] coords, Object[] data, float[] weight, Object[] outData, Object polygonData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void error(int errnum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void errorData(int errnum, Object polygonData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
