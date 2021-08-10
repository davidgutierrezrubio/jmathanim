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
import com.jmathanim.mathobjects.Shape;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;
import java.nio.FloatBuffer;

/**
 * This class send appropiate VAO buffers to shaders
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShaderDrawer {

    ShaderLoader sl;
    private GL3ES3 gles;
    private GL3 gl;

    private int vao[] = new int[1];
    private int vbo[] = new int[3];

    public ShaderDrawer(ShaderLoader sl, GL3ES3 gles3, GL3 gl) {
        this.sl = sl;
        this.gles = gles3;
        this.gl = gl;

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(3, vbo, 0);

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

    void drawShape(Shape s) {
        float[] shapeColors = getColor(s);
        int size = s.size();
        for (int n = 0; n < size; n++) {
            Vec p = s.get(n - 1).p.v;
            Vec q = s.get(n).p.v;
            Vec r = s.get(n + 1).p.v;
            Vec t = s.get(n + 2).p.v;
            float vertices[] = new float[12];
            vertices[0] = (float) p.x;
            vertices[1] = (float) p.y;
            vertices[2] = (float) p.z;
            vertices[3] = (float) q.x;
            vertices[4] = (float) q.y;
            vertices[5] = (float) q.z;
            vertices[6] = (float) r.x;
            vertices[7] = (float) r.y;
            vertices[8] = (float) r.z;
            vertices[9] = (float) t.x;
            vertices[10] = (float) t.y;
            vertices[11] = (float) t.z;

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
