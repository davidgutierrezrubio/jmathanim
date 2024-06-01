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
import java.nio.FloatBuffer;
import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;

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


    public ShaderDrawer() {
    }

    void drawFill(Shape s, ArrayList<ArrayList<Point>> pieces) {
        if (s.getMp().getFillColor().getAlpha() == 0) {
            return;
        }

        final PaintStyle fillStylable = s.getMp().getFillColor();
        ShaderLoader shader = fillShader;
//        defineColorParametersForShader(fillStylable, shader);

        for (ArrayList<Point> piece : pieces) {
            float[] coords = new float[piece.size() * 3];
            int k = 0;
            for (Point point : piece) {
                coords[k++] = (float) point.v.x;
                coords[k++] = (float) point.v.y;
                coords[k++] = (float) point.v.z;
            }
            ConcavePolygonRenderer.drawConcavePolygon(coords);
        }

    }

    private void drawWholeScreen() {
//        FloatBuffer fbVertices;
//        // redraw polygon again, this time into color buffer.
//        //Set projection matrices to identity
//        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//        gl2.glPushMatrix();
//        gl2.glLoadIdentity();
//        FloatBuffer projMat = FloatBuffer.allocate(16);
//        gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projMat);
//        gl2.glUniformMatrix4fv(fillShader.getUniformVariable("projection"), 1, false, projMat);
//        float[] pp = new float[]{
//            -1f, 1f, 0f, 1f,
//            1f, 1f, 0f, 1f,
//            1f, -1f, 0f, 1f,
//            1f, -1f, 0f, 1f,
//            -1f, -1f, 0f, 1f
//        };
//        //bind an array of triangle fan
//        fbVertices = Buffers.newDirectFloatBuffer(pp);
//        gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
//        gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
//        gl3.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 0, 0);
//        gl3.glDrawArrays(GL3ES3.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);
//        gl2.glPopMatrix();
//        gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projMat);
//        gl2.glUniformMatrix4fv(fillShader.getUniformVariable("projection"), 1, false, projMat);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


    }

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

//    void drawThinShape(Shape s, ArrayList<ArrayList<Point>> pieces, boolean noFill) {
//        if (s.getMp().getDrawColor().getAlpha() == 0) {
//            return;
//        }
////        if (!noFill) {//If the shape is not filled, no need to do this
//        gl3.glStencilMask(0xFF);//Second bit for contour
//        gl3.glStencilFunc(GL.GL_ALWAYS, 2, 2);
//        gl3.glStencilOp(GL.GL_ZERO, GL.GL_ZERO, GL.GL_REPLACE);
////        }
//
//        //TODO: Implement this in glsl in a geometry shader
//        float[] shapeColors = getDrawColor(s);
//        gl2.glUniform4f(thinLineShader.getUniformVariable("unifColor"), shapeColors[0], shapeColors[1], shapeColors[2], shapeColors[3]);
//        for (ArrayList<Point> piece : pieces) {
//            for (int n = 0; n < piece.size() - 1; n++) {
//                Vec p = piece.get(n).v;
//                Vec q = piece.get(n + 1).v;
//                Vec d = q.minus(p);
//                Vec r, t;
//                if (n > 0) {
//                    r = piece.get(n - 1).v;
//                } else {
//                    r = p.copy();
//                }
//                if (n < piece.size() - 2) {
//                    t = piece.get(n + 2).v;
//                } else {
//                    t = q.copy();
//                }
//                if (n == piece.size() - 2) {
//                    if (piece.get(n + 1).isEquivalentTo(piece.get(0), .000001)) {
//                        t = piece.get(1).v;
//                    }
//                }
//                if (n == 0) {
//                    if (piece.get(piece.size() - 1).isEquivalentTo(piece.get(0), .000001)) {
//                        r = piece.get(piece.size() - 2).v;
//                    }
//                }
//
//                drawGLAdjacency(r, p, q, t, s.getMp().getThickness());
//            }
//        }
//    }
//
//    private void drawGLAdjacency(Vec p, Vec q, Vec r, Vec t, double thickness) {
//
//        gl3.glUniform1f(thinLineShader.getUniformVariable("Thickness"), (float) thickness);
//        float vertices[] = new float[16];
//        vertices[0] = (float) p.x;
//        vertices[1] = (float) p.y;
//        vertices[2] = (float) p.z;
//        vertices[3] = 1f;
//        vertices[4] = (float) q.x;
//        vertices[5] = (float) q.y;
//        vertices[6] = (float) q.z;
//        vertices[7] = 1f;
//        vertices[8] = (float) r.x;
//        vertices[9] = (float) r.y;
//        vertices[10] = (float) r.z;
//        vertices[11] = 1f;
//        vertices[12] = (float) t.x;
//        vertices[13] = (float) t.y;
//        vertices[14] = (float) t.z;
//        vertices[15] = 1f;
//        gl3.glEnableClientState(GL2.GL_VERTEX_ARRAY);
//
//        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
//        gl3.glBindBuffer(GL3ES3.GL_ARRAY_BUFFER, vbo[0]);
//        gl3.glBufferData(GL3ES3.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL3ES3.GL_STATIC_DRAW);
//        gl3.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 0, 0);
//        gl3.glDrawArrays(GL3ES3.GL_LINES_ADJACENCY_EXT, 0, 4);
//    }

}
