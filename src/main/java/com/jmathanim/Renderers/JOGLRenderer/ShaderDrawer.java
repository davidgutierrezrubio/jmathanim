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
    private int vbo[] = new int[2];

    public ShaderDrawer(ShaderLoader sl, GL3ES3 gles3, GL3 gl) {
        this.sl = sl;
        this.gles = gles3;
        this.gl = gl;

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(2, vbo, 0);

    }

    void drawFill(Shape s) {
        if (s.getMp().getFillColor().getAlpha() == 0) {
            return;
        }

    }

    void drawShape(Shape s) {
        int size = s.size() + 1;
        float vertices[] = new float[size * 3];
        float colors[] = new float[size * 4];
        PaintStyle st = s.getMp().getDrawColor();
        float r=0;
        float g=0;
        float b=0;
        float alpha=1;
        if (st instanceof JMColor) {
            JMColor col=(JMColor) st;
            r=(float) col.r;
            g=(float) col.g;
            b=(float) col.b;
            alpha=(float) col.getAlpha();
        }
        for (int n = 0; n < size; n++) {
            Vec v = s.get(n).p.v;
            vertices[3 * n] = (float) v.x;
            vertices[3 * n + 1] = (float) v.y;
            vertices[3 * n + 2] = (float) v.z;

            colors[4 * n] = r;
            colors[4 * n + 1] = g;
            colors[4 * n + 2] = b;
            colors[4 * n + 3] = alpha;

        }

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
        gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL2ES2.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL2ES2.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        FloatBuffer fbColors = Buffers.newDirectFloatBuffer(colors);
        gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vbo[1]);
        gl.glBufferData(GL2ES2.GL_ARRAY_BUFFER, fbColors.limit() * 4, fbColors, GL2ES2.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 4, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glDrawArrays(GL2ES2.GL_LINE_STRIP, 0, size); //Draw the vertices as triangle
        gl.glDisableVertexAttribArray(0); // Allow release of vertex position memory
        gl.glDisableVertexAttribArray(1); // Allow release of vertex color memory

//
//        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
//        int numBytes = vertices.length * 3;
//        gl.glBufferData(GL2ES2.GL_ARRAY_BUFFER, numBytes, fbVertices, GL2ES2.GL_STATIC_DRAW);
//        fbVertices = null; // It is OK to release CPU vertices memory after transfer to GPU
//        gl.glVertexAttribPointer(0, 3, GL2ES2.GL_FLOAT, false, 0, 0);
//
//        FloatBuffer fbColors = Buffers.newDirectFloatBuffer(colors);
//
//        // Select the VBO, GPU memory data, to use for colors
//        gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboHandles[COLOR_IDX]);
//
//        numBytes = colors.length * 4;
//        gl.glBufferData(GL2ES2.GL_ARRAY_BUFFER, numBytes, fbColors, GL2ES2.GL_STATIC_DRAW);
//        fbColors = null; // It is OK to release CPU color memory after transfer to GPU
//
//        // Associate Vertex attribute 1 with the last bound VBO
//        gl.glVertexAttribPointer(1 /* the vertex attribute */, 4 /* four possitions used for each vertex */,
//                GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                0 /* The bound VBO data offset */);
//
//        gl.glEnableVertexAttribArray(1);
//
//        gl.glDrawArrays(GL2ES2.GL_TRIANGLES, 0, 3); //Draw the vertices as triangle
//
//        gl.glDisableVertexAttribArray(0); // Allow release of vertex position memory
//        gl.glDisableVertexAttribArray(1); // Allow release of vertex color memory
    }

}
