/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.JMLinearGradient;
import com.jmathanim.Styling.JMRadialGradient;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * This class send appropiate VAO buffers to shaders
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShaderDrawer {

    //Shader to draw lines with different thickness and linecaps
    protected ShaderLoader thinLineShader;
    //Shader to fill concave polygons
    protected ShaderLoader fillShader;
//    private GL3ES3 gles;
    private final GL3 gl3;
    private final GL2 gl2;
//    private GLUgl2 glu;

    private final int vao[] = new int[1];
    private final int vbo[] = new int[2];

    public ShaderDrawer(GL3 gl3, GL2 gl2) {
        this.gl3 = gl3;
        this.gl2 = gl2;

        gl3.glGenVertexArrays(vao.length, vao, 0);
        gl3.glBindVertexArray(vao[0]);
        gl3.glGenBuffers(2, vbo, 0);
        gl3.glEnableVertexAttribArray(0);
        gl3.glEnableVertexAttribArray(1);

    }

    void drawFill(Shape s, ArrayList<ArrayList<Point>> pieces) {
        if (s.getMp().getFillColor().getAlpha() == 0) {
            return;
        }

        final PaintStyle fillStylable = s.getMp().getFillColor();
        ShaderLoader shader = fillShader;
        defineColorParametersForShader(fillStylable, shader);
//        System.out.println("Shader uniform color: "+shapeColors[2]);
        //Generates a triangle fan array
        Vec normal = s.getNormalVector();
        float nx = (float) normal.x;
        float ny = (float) normal.y;
        float nz = (float) normal.z;
        ArrayList<Float> coords = new ArrayList<>();

        //We are going to generate a triangle strip
        //This is the first and common point...
        Point P = pieces.get(0).get(0);
        coords.add((float) P.v.x);
        coords.add((float) P.v.y);
        coords.add((float) P.v.z);
        coords.add(1f);
        //Iterate over all connected components
        for (int k = 0; k < pieces.size(); k++) {
            ArrayList<Point> piece = pieces.get(k);
            for (int n = (k == 0 ? 1 : 0); n < piece.size() - 1; n++) {
                Point Q = piece.get(n);
                Point R = piece.get(n + 1);
                coords.add((float) Q.v.x);
                coords.add((float) Q.v.y);
                coords.add((float) Q.v.z);
                coords.add(1f);
                coords.add((float) R.v.x);
                coords.add((float) R.v.y);
                coords.add((float) R.v.z);
                coords.add(1f);
            }
        }
        //It seems I need this triangles as well...
        for (int k = 0; k < pieces.size() - 1; k++) {
            Point Q = pieces.get(k).get(0);
            Point R = pieces.get(k + 1).get(0);
            coords.add((float) Q.v.x);
            coords.add((float) Q.v.y);
            coords.add((float) Q.v.z);
            coords.add(1f);
            coords.add((float) R.v.x);
            coords.add((float) R.v.y);
            coords.add((float) R.v.z);
            coords.add(1f);
        }

        float[] points = new float[coords.size()];//TODO: Optimize this
        float[] normals = new float[coords.size()];//TODO: Optimize this
        for (int i = 0; i < coords.size(); i += 4) {
            points[i] = coords.get(i);
            points[i + 1] = coords.get(i + 1);
            points[i + 2] = coords.get(i + 2);
            points[i + 3] = coords.get(i + 3);
            normals[i] = nx;
            normals[i + 1] = ny;
            normals[i + 2] = nz;
            normals[i + 3] = 1f;
        }

        //bind an array of triangle fan
        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(points);
        gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
        gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 0, 0);

        FloatBuffer fbNormals = Buffers.newDirectFloatBuffer(normals);
        gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[1]);
        gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbNormals.limit() * 4, fbNormals, GL3ES3.GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(1, 4, GL.GL_FLOAT, false, 0, 0);

        //Enable Stencil buffer to draw concave polygons
        gl3.glStencilMask(0b00000001);//Last bit for filling

        // set stencil buffer to invert value on draw, 0 to 1 and 1 to 0
        //Pass the stencil test if the pixel doesnt belong to the drawed contour
        gl3.glStencilFunc(GL.GL_EQUAL, 0, 2);
//        if (!s.isIsConvex()) {
        gl3.glStencilOp(GL.GL_INVERT, GL.GL_INVERT, GL.GL_INVERT);
        // disable writing to color buffer
        gl3.glColorMask(false, false, false, false);
        // draw polygon into stencil buffer
        //Has to disable depth mask in order to avoid z fightint for concave polygons
        gl3.glDepthMask(false);
        gl3.glDrawArrays(GL3ES3.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);
        gl3.glDepthMask(true);
        // set stencil buffer to only keep pixels when value in buffer is 1
        //Bit 2 is set to 1 if the contour is drawed in that fragment, so stencil
        //mask has to be 01 in order to draw fill
        //00->No fill, no contour
        //10->Contour, no fill
        //11->Contour&fill (don't draw fill here!)
        //01->Fill only
        gl3.glStencilFunc(GL.GL_EQUAL, 1, 3);
        gl3.glStencilOp(GL.GL_ZERO, GL.GL_ZERO, GL.GL_ZERO);

        // enable color again
        gl3.glColorMask(true, true, true, true);
//        gl3.glDepthMask(false);
//        drawWholeScreen();//Draw whole screen with current color
        gl3.glDrawArrays(GL3ES3.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);
//        gl3.glDepthMask(true);
    }

    private void defineColorParametersForShader(final PaintStyle fillStylable, ShaderLoader shader) {
        if (fillStylable instanceof JMColor) {
            float[] shapeColors = toColor((JMColor) fillStylable);
            gl2.glUniform4f(shader.getUniformVariable("unifColor"), shapeColors[0], shapeColors[1], shapeColors[2], shapeColors[3]);
            gl2.glUniform1i(shader.getUniformVariable("fillType"), 0);//Solid color
        }
        if (fillStylable instanceof JMLinearGradient) {
            JMLinearGradient lg = (JMLinearGradient) fillStylable;
            lg.getStops();
            //TODO: Stuff to do here needed...
            gl2.glUniform1i(shader.getUniformVariable("fillType"), 1);//Linear Gradient
        }
        if (fillStylable instanceof JMRadialGradient) {
            JMRadialGradient rg = (JMRadialGradient) fillStylable;
            rg.getStops();
            //TODO: Stuff to do here needed...
            gl2.glUniform1i(shader.getUniformVariable("fillType"), 2);//Radial Gradient
        }

    }

    private void drawWholeScreen() {
        FloatBuffer fbVertices;
        // redraw polygon again, this time into color buffer.
        //Set projection matrices to identity
        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl2.glPushMatrix();
        gl2.glLoadIdentity();
        FloatBuffer projMat = FloatBuffer.allocate(16);
        gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projMat);
        gl2.glUniformMatrix4fv(fillShader.getUniformVariable("projection"), 1, false, projMat);
        float[] pp = new float[]{
            -1f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f,
            1f, -1f, 0f, 1f,
            1f, -1f, 0f, 1f,
            -1f, -1f, 0f, 1f
        };
        //bind an array of triangle fan
        fbVertices = Buffers.newDirectFloatBuffer(pp);
        gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
        gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 0, 0);
        gl3.glDrawArrays(GL3ES3.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);
        gl2.glPopMatrix();
        gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projMat);
        gl2.glUniformMatrix4fv(fillShader.getUniformVariable("projection"), 1, false, projMat);
    }

//    void drawFillSlowButWorking(Shape s, ArrayList<ArrayList<Point>> pieces) {
//        if (s.getMp().getFillColor().getAlpha() == 0) {
//            return;
//        }
//        float[] shapeColors = getFillColor(s);
//        gl2.glUniform4f(fillShader.getUniformVariable("unifColor"), shapeColors[0], shapeColors[1], shapeColors[2], shapeColors[3]);
//////        System.out.println("Shader uniform color: "+shapeColors[2]);
//        GLUtessellator tess = GLUtessellatorImpl.gluNewTess();
//        TessellatorCallback cb = new TessellatorCallback(glu, gl2);
//        GLU.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, cb);
//        GLU.gluTessCallback(tess, GLU.GLU_TESS_END, cb);
//        GLU.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, cb);
//        GLU.gluTessCallback(tess, GLU.GLU_TESS_COMBINE, cb);
//        GLU.gluTessCallback(tess, GLU.GLU_TESS_ERROR, cb);
//
//        GLU.gluTessBeginPolygon(tess, null);
//        for (ArrayList<Point> piece : pieces) {
//            GLU.gluTessBeginContour(tess);
//            for (Point p : piece) {
//                double[] coords = new double[]{p.v.x, p.v.y, p.v.z};
//                GLU.gluTessVertex(tess, coords, 0, coords);
//            }
//            GLU.gluTessEndContour(tess);
//        }
//        GLU.gluEndPolygon(tess);
//        GLU.gluDeleteTess(tess);
//    }
    public float[] getDrawColor(Shape s) {
        PaintStyle st = s.getMp().getDrawColor();
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

    public float[] toColor(JMColor col) {
        float r = (float) col.r;
        float g = (float) col.g;
        float b = (float) col.b;
        float alpha = (float) col.getAlpha();
        return new float[]{r, g, b, alpha};
    }

    void removeDuplicates(ArrayList<Point> piece) {
        int n = 0;
        while (n < piece.size() - 1) {
            if (piece.get(n).isEquivalentTo(piece.get(n + 1), .0000001)) {
                piece.remove(n + 1);
                n = 0;
            } else {
                n++;
            }
        }
    }

    void drawThinShape(Shape s, ArrayList<ArrayList<Point>> pieces, boolean noFill) {
        if (s.getMp().getDrawColor().getAlpha() == 0) {
            return;
        }
//        if (!noFill) {//If the shape is not filled, no need to do this
        gl3.glStencilMask(0xFF);//Second bit for contour
        gl3.glStencilFunc(GL.GL_ALWAYS, 2, 2);
        gl3.glStencilOp(GL.GL_ZERO, GL.GL_ZERO, GL.GL_REPLACE);
//        }

        //TODO: Implement this in glsl in a geometry shader
        float[] shapeColors = getDrawColor(s);
        gl2.glUniform4f(thinLineShader.getUniformVariable("unifColor"), shapeColors[0], shapeColors[1], shapeColors[2], shapeColors[3]);
        for (ArrayList<Point> piece : pieces) {
            for (int n = 0; n < piece.size() - 1; n++) {
                Vec p = piece.get(n).v;
                Vec q = piece.get(n + 1).v;
                Vec d = q.minus(p);
                Vec r, t;
                if (n > 0) {
                    r = piece.get(n - 1).v;
                } else {
                    r = p.copy();
                }
                if (n < piece.size() - 2) {
                    t = piece.get(n + 2).v;
                } else {
                    t = q.copy();
                }
                if (n == piece.size() - 2) {
                    if (piece.get(n + 1).isEquivalentTo(piece.get(0), .000001)) {
                        t = piece.get(1).v;
                    }
                }
                if (n == 0) {
                    if (piece.get(piece.size() - 1).isEquivalentTo(piece.get(0), .000001)) {
                        r = piece.get(piece.size() - 2).v;
                    }
                }

                drawGLAdjacency(r, p, q, t, s.getMp().getThickness());
            }
        }
    }

    private void drawGLAdjacency(Vec p, Vec q, Vec r, Vec t, double thickness) {

        gl3.glUniform1f(thinLineShader.getUniformVariable("Thickness"), (float) thickness);
        float vertices[] = new float[16];
        vertices[0] = (float) p.x;
        vertices[1] = (float) p.y;
        vertices[2] = (float) p.z;
        vertices[3] = 1f;
        vertices[4] = (float) q.x;
        vertices[5] = (float) q.y;
        vertices[6] = (float) q.z;
        vertices[7] = 1f;
        vertices[8] = (float) r.x;
        vertices[9] = (float) r.y;
        vertices[10] = (float) r.z;
        vertices[11] = 1f;
        vertices[12] = (float) t.x;
        vertices[13] = (float) t.y;
        vertices[14] = (float) t.z;
        vertices[15] = 1f;
        gl3.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
        gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
        gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 0, 0);
        gl3.glDrawArrays(GL3ES3.GL_LINES_ADJACENCY_EXT, 0, 4);
    }

//    void drawShapeBezierOld(Shape s) {
//        float[] shapeColors = getDrawColor(s);
//        int size = s.size();
//        for (int n = 0; n < size; n++) {
//            JMPathPoint p = s.get(n);
//            JMPathPoint q = s.get(n + 1);
//            if (q.isThisSegmentVisible) {
//                //Draw Bezier curve
//                float vertices[] = new float[12];
//                vertices[0] = (float) p.p.v.x;
//                vertices[1] = (float) p.p.v.y;
//                vertices[2] = (float) p.p.v.z;
//                vertices[3] = (float) q.p.v.x;
//                vertices[4] = (float) q.p.v.y;
//                vertices[5] = (float) q.p.v.z;
//
//                vertices[6] = (float) p.cpExit.v.x;
//                vertices[7] = (float) p.cpExit.v.y;
//                vertices[8] = (float) p.cpExit.v.z;
//                vertices[9] = (float) q.cpEnter.v.x;
//                vertices[10] = (float) q.cpEnter.v.y;
//                vertices[11] = (float) q.cpEnter.v.z;
//
//                float[] colors = new float[16];
//                colors[0] = shapeColors[0];
//                colors[1] = shapeColors[1];
//                colors[2] = shapeColors[2];
//                colors[3] = shapeColors[3];
//                colors[4] = shapeColors[0];
//                colors[5] = shapeColors[1];
//                colors[6] = shapeColors[2];
//                colors[7] = shapeColors[3];
//                colors[8] = shapeColors[0];
//                colors[9] = shapeColors[1];
//                colors[10] = shapeColors[2];
//                colors[11] = shapeColors[3];
//                colors[12] = shapeColors[0];
//                colors[13] = shapeColors[1];
//                colors[14] = shapeColors[2];
//                colors[15] = shapeColors[3];
//                gl3.glEnableClientState(GL2.GL_VERTEX_ARRAY);
//
//                FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
//                gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
//                gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
//                gl3.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
//                gl3.glEnableVertexAttribArray(0);
//
//                FloatBuffer fbColors = Buffers.newDirectFloatBuffer(colors);
//                gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[1]);
//                gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbColors.limit() * 4, fbColors, GL3ES3.GL_STATIC_DRAW);
//                gl3.glVertexAttribPointer(1, 4, GL.GL_FLOAT, false, 0, 0);
//                gl3.glEnableVertexAttribArray(1);
//
//                gl3.glDrawArrays(GL3ES3.GL_LINES_ADJACENCY_EXT, 0, size);
//                gl3.glDisableVertexAttribArray(0);
//                gl3.glDisableVertexAttribArray(1);
//
//            }
//        }
//    }
}
