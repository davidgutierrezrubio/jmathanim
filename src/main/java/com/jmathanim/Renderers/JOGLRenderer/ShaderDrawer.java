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
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * This class send appropiate VAO buffers to shaders
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShaderDrawer {

    ShaderLoader shaderLoader;
    private GL3ES3 gles;
    private GL3 gl;

    private int vao[] = new int[1];
    private int vbo[] = new int[2];

    public ShaderDrawer(ShaderLoader sl, GL3ES3 gles3, GL3 gl) {
        this.shaderLoader = sl;
        this.gles = gles3;
        this.gl = gl;

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(2, vbo, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);

    }

    void drawFill(Shape s) {
        if (s.getMp().getFillColor().getAlpha() == 0) {
            return;
        }

    }

    public float[] getColor(Shape s) {
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

    void drawShape(Shape s) {
        float[] shapeColors = getColor(s);
        double thickness = s.getMp().getThickness();
        for (int n = 0; n <= s.size(); n++) {
            JMPathPoint p = s.get(n);
            JMPathPoint q = s.get(n + 1);
            drawGLAdjacency(p.cpExit.v, p.p.v, q.p.v, q.cpEnter.v, shapeColors, thickness);
        }
    }

    void drawShapeSlowButWorkingMethod(Shape s,ArrayList<ArrayList<Point>> pieces) {
        //TODO: Implement this in glsl in a geometry shader
        float[] shapeColors = getColor(s);
        
        for (ArrayList<Point> piece : pieces) {
//            removeDuplicates(piece);
            for (int n = 0; n < piece.size() - 1; n++) {
                Vec p = piece.get(n).v;
                Vec q = piece.get(n + 1).v;
                Vec d = q.minus(p);
                Vec r, t;
                if (n > 0) {
                    r = piece.get(n - 1).v;
                } else {
                    r = p.minus(d);
                }
                if (n < piece.size() - 2) {
                    t = piece.get(n + 2).v;
                } else {
                    t = q.add(d);
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

                drawGLAdjacency(r, p, q, t, shapeColors, s.getMp().getThickness());
//                drawSegment(Vec.to(0,0), p, q, Vec.to(0,0), shapeColors, s.getMp().getThickness());
            }
        }
    }

    private void drawGLAdjacency(Vec p, Vec q, Vec r, Vec t, float[] shapeColors, double thickness) {

        gl.glUniform1f(shaderLoader.unifThickness, (float) thickness);
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

        float[] colors = new float[16];
        colors[0] = shapeColors[0];//TODO: this should be more concise, no need to repeat!
        colors[1] = shapeColors[1];
        colors[2] = shapeColors[2];
        colors[3] = shapeColors[3];
        colors[4] = shapeColors[0];
        colors[5] = shapeColors[1];
        colors[6] = shapeColors[2];
        colors[7] = shapeColors[3];
        colors[8] = shapeColors[0];
        colors[9] = shapeColors[1];
        colors[10] = shapeColors[2];
        colors[11] = shapeColors[3];
        colors[12] = shapeColors[0];
        colors[13] = shapeColors[1];
        colors[14] = shapeColors[2];
        colors[15] = shapeColors[3];

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
//            System.out.println("Limit " + fbVertices.limit());
        gl.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 0, 0);
//            gl.glEnableVertexAttribArray(0);

        FloatBuffer fbColors = Buffers.newDirectFloatBuffer(colors);
        gl.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[1]);
        gl.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbColors.limit() * 4, fbColors, GL3ES3.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 4, GL.GL_FLOAT, false, 0, 0);
//            gl.glEnableVertexAttribArray(1);

        gl.glDrawArrays(GL3ES3.GL_LINES_ADJACENCY_EXT, 0, 4);
//            gl.glDisableVertexAttribArray(0);
//            gl.glDisableVertexAttribArray(1);
    }

    void drawShapeBezierOld(Shape s) {
        float[] shapeColors = getColor(s);
        int size = s.size();
        for (int n = 0; n < size; n++) {
            JMPathPoint p = s.get(n);
            JMPathPoint q = s.get(n + 1);
            if (q.isThisSegmentVisible) {
                //Draw Bezier curve
                float vertices[] = new float[12];
                vertices[0] = (float) p.p.v.x;
                vertices[1] = (float) p.p.v.y;
                vertices[2] = (float) p.p.v.z;
                vertices[3] = (float) q.p.v.x;
                vertices[4] = (float) q.p.v.y;
                vertices[5] = (float) q.p.v.z;

                vertices[6] = (float) p.cpExit.v.x;
                vertices[7] = (float) p.cpExit.v.y;
                vertices[8] = (float) p.cpExit.v.z;
                vertices[9] = (float) q.cpEnter.v.x;
                vertices[10] = (float) q.cpEnter.v.y;
                vertices[11] = (float) q.cpEnter.v.z;

                float[] colors = new float[16];
                colors[0] = shapeColors[0];
                colors[1] = shapeColors[1];
                colors[2] = shapeColors[2];
                colors[3] = shapeColors[3];
                colors[4] = shapeColors[0];
                colors[5] = shapeColors[1];
                colors[6] = shapeColors[2];
                colors[7] = shapeColors[3];
                colors[8] = shapeColors[0];
                colors[9] = shapeColors[1];
                colors[10] = shapeColors[2];
                colors[11] = shapeColors[3];
                colors[12] = shapeColors[0];
                colors[13] = shapeColors[1];
                colors[14] = shapeColors[2];
                colors[15] = shapeColors[3];
                gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

                FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
                gl.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
                gl.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
                gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
                gl.glEnableVertexAttribArray(0);

                FloatBuffer fbColors = Buffers.newDirectFloatBuffer(colors);
                gl.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[1]);
                gl.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbColors.limit() * 4, fbColors, GL3ES3.GL_STATIC_DRAW);
                gl.glVertexAttribPointer(1, 4, GL.GL_FLOAT, false, 0, 0);
                gl.glEnableVertexAttribArray(1);

                gl.glDrawArrays(GL3ES3.GL_LINES_ADJACENCY_EXT, 0, size);
                gl.glDisableVertexAttribArray(0);
                gl.glDisableVertexAttribArray(1);

            }
        }
    }

}
