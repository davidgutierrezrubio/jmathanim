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
import com.jogamp.opengl.util.PMVMatrix;
import java.util.ArrayList;

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
    final float zNear = 0.1f, zFar = 7000f;

    /* 2nd pass texture size antialias SampleCount
           4 is usually enough */
    private final int[] sampleCount = new int[]{2};

    /* variables used to update the PMVMatrix before rendering */
    private float xTranslate = -40f;
    private float yTranslate = 0f;
    private float zTranslate = -100f;

    private final int renderModes = Region.VARWEIGHT_RENDERING_BIT;
    GL2ES2 gl;
    private double alpha;

    public void setConfig(JMathAnimConfig config) {
        this.config = config;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        alpha = 0;
        gl = drawable.getGL().getGL2ES2();
        glu = new GLU();
        regions = new ArrayList<>();
        shapes = new ArrayList<>();

        gl.setSwapInterval(1);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);

        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_MULTISAMPLE);
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

    public void addShapeContour(Shape s) {
        final JMPath path = s.getPath();
        float[] buffer = new float[path.size() * 9];
        int k = 0;
        for (int n = 0; n < path.size(); n++) {
            JMPathPoint p = path.getJMPoint(n);
            buffer[k] = (float) p.p.v.x;
            buffer[k + 1] = (float) p.p.v.y;
            buffer[k + 2] = (float) p.p.v.z;
            k += 3;
            buffer[k] = (float) p.cpExit.v.x;
            buffer[k + 1] = (float) p.cpExit.v.y;
            buffer[k + 2] = (float) p.cpExit.v.z;
            k += 3;

            buffer[k] = (float) p.cpEnter.v.x;
            buffer[k + 1] = (float) p.cpEnter.v.y;
            buffer[k + 2] = (float) p.cpEnter.v.z;
            k += 3;

//         OutlineShape outlineShape = createOutlineShapeFromShape(s);
            gl.getGL2().glMap1f(GL2.GL_MAP1_VERTEX_3, 0f, 1f, 3, 4, buffer, 0);
        }
        shapes.add(s);

    }

    public void addShapeFill(Shape s) {
        OutlineShape outlineShape = createOutlineShapeFromShape(s);
        GLRegion reg = GLRegion.create(/* RenderModes */renderModes, /* TextureSequence */ null);
        reg.addOutlineShape(outlineShape, null, reg.hasColorChannel() ? renderState.getColorStatic(new float[4]) : null);
        regions.add(reg);

//           regionRenderer.enable(gl, true);
//        reg.draw(gl, regionRenderer, sampleCount);
//   regionRenderer.enable(gl, false);
    }

    private OutlineShape createOutlineShapeFromShape(Shape s) {
        OutlineShape outlineShape = new OutlineShape(renderState.getVertexFactory());
        double L = .75;
        JMPath path = s.getPath();
        for (int n = 0; n < path.size(); n++) {
            JMPathPoint p = path.getJMPoint(n);
            outlineShape.addVertex((float) ((1 - L) * p.p.v.x + L * p.cpEnter.v.x), (float) ((1 - L) * p.p.v.y + L * p.cpEnter.v.y), false);
            outlineShape.addVertex((float) p.p.v.x, (float) p.p.v.y, true);
//            outlineShape.addVertex((float) p.cpExit.v.x, (float) p.cpExit.v.y, false);
            outlineShape.addVertex((float) ((1 - L) * p.p.v.x + L * p.cpExit.v.x), (float) ((1 - L) * p.p.v.y + L * p.cpExit.v.y), false);

        }
        return outlineShape;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2ES2 gl = drawable.getGL().getGL2ES2();
        final GL2 gl2 = drawable.getGL().getGL2();
        // clear screen
        gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

//        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        // the RegionRenderer PMVMatrix define where we want to render our shape
        final PMVMatrix pmv = regionRenderer.getMatrix();
        pmv.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//        pmv.glLoadIdentity();
//        pmv.glTranslatef(xTranslate, yTranslate, zTranslate);

        if (weight != regionRenderer.getRenderState().getWeight()) {
            regionRenderer.getRenderState().setWeight(weight);
        }
        regionRenderer.enable(gl, true);
//        glRegion.draw(gl, regionRenderer, sampleCount);
        for (GLRegion r : regions) {
            r.draw(gl, regionRenderer, sampleCount);
        }
        regionRenderer.enable(gl, false);

        //Pruebas para intentar dibujar un contorno...
        gl2.glBegin(GL2.GL_LINE_STRIP);//static field
//        gl2.glVertex3f(0.50f, -0.50f, 0);
//        gl2.glVertex3f(-0.50f, 0.50f, 0);
        gl2.glColor3f(1, 1, 1);
        for (Shape s : shapes) {
            drawShape(gl2, s);
        }
//        gl2.glVertex3f(0.50f, -0.50f, 0);
//        gl2.glVertex3f(-0.50f, 0.50f, 0);
        gl2.glEnd();
        regions.clear();
        shapes.clear();

//        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//        gl2.glLoadIdentity();
//       gl2.glRotated(alpha, 0, 0, 1);
//       gl2.glRotated(45, 1, 1, 0);
//        alpha+=1;
        gl2.glLoadIdentity();
        Rect bb = config.getCamera().getMathView();
        gl2.glOrtho(bb.xmin, bb.xmax, bb.ymin, bb.ymax, -5, 5);
//        alpha+=.02;
        gl2.glFlush();
    }

    private void drawShape(GL2 gl2, Shape s) {
        JMPath path = s.getPath();
//        for (int n = 0; n < path.size()+1; n++) {
//            JMPathPoint p = path.getJMPoint(n);
//            gl2.glVertex3d(p.p.v.x, p.p.v.y, p.p.v.z);
//        }
        int num = 500;
        for (int k = 0; k < num; k++) {
            JMPathPoint p = path.getPointAt(1d * k / num);
            gl2.glVertex3d(p.p.v.x, p.p.v.y, p.p.v.z);
        }

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        final PMVMatrix pmv = regionRenderer.getMatrix();
        regionRenderer.reshapePerspective(45.0f, w, h, zNear, zFar);
        pmv.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        pmv.glLoadIdentity();
        final GL2 gl2 = drawable.getGL().getGL2();
        gl2.glViewport(x, y, w, h);
//        gl2.glOrtho(-1, 1, -1, 1, 0, 100);
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//        gl2.glLoadIdentity();
//        glu.gluPerspective(60.0f , (float) w / h , 0.1f , 10000.0f);
//        gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//        gl2.glLoadIdentity();
//        gl2.glRotated(45, 1, 0, 1);

    }
}
