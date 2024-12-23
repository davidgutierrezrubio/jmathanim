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
import com.jmathanim.mathobjects.Shape;
import com.jogamp.common.nio.Buffers;
//import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class send appropiate VAO buffers to shaders
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShaderDrawer {

    JOGLRenderQueue queue;

    //Shader to draw lines with different thickness and linecaps
    protected ShaderLoader thinLineShader;
    //Shader to fill concave polygons
    protected ShaderLoader fillShader;
//    private GL4 gles;
    private final GL4 gl4;
//    private GLUgl2 glu;

    private final int[] vao = new int[1];
    private final int[] vbo = new int[2];

    public ShaderDrawer(GL4 gl4) {
        this.gl4 = gl4;

        gl4.glGenVertexArrays(vao.length, vao, 0);
        gl4.glBindVertexArray(vao[0]);
        gl4.glGenBuffers(2, vbo, 0);
        gl4.glEnableVertexAttribArray(0);
        gl4.glEnableVertexAttribArray(1);

    }

    void drawFillNew(Shape s) {
        ArrayList<ArrayList<float[]>> pieces = s.getPath().getPolygonalPieces();
        if (pieces.isEmpty()) {
            s.computePolygonalPieces();
            pieces = s.getPath().getPolygonalPieces();  // Asegurarse de recalcular las piezas
        }

        if (s.getMp().getFillColor().getAlpha() == 0) {
            return;
        }

        final PaintStyle fillStylable = s.getMp().getFillColor();
        ShaderLoader shader = fillShader;
        defineColorParametersForFillShader(fillStylable, shader);

// Generar un array de triángulos
        Vec normal = s.getNormalVector();
        float nx = (float) normal.x;
        float ny = (float) normal.y;
        float nz = (float) normal.z;

// Verificar el tamaño total necesario
        int numVertices = 1; // Empieza en 1 por el primer punto común
        for (ArrayList<float[]> piece : pieces) {
            // Aquí contamos dos vértices por cada par (Q, R) en cada pieza
            numVertices += piece.size() * 2 - 2; // Ajustar para evitar sumar uno de más
        }
        int totalSize = 4 * numVertices;
        float[] points = new float[totalSize];
        float[] normals = new float[totalSize];

// Llenar arrays con los valores correspondientes
        int index = 0;

// Añadir el primer punto común
        float[] P = pieces.get(0).get(0);
        points[index] = P[0];
        points[index + 1] = P[1];
        points[index + 2] = P[2];
        points[index + 3] = 1f;

        normals[index] = nx;
        normals[index + 1] = ny;
        normals[index + 2] = nz;
        normals[index + 3] = 1f;

        index += 4;

// Iterar sobre todas las piezas conectadas
        for (int k = 0; k < pieces.size(); k++) {
            ArrayList<float[]> piece = pieces.get(k);
            for (int n = (k == 0 ? 1 : 0); n < piece.size() - 1; n++) {
                float[] Q = piece.get(n);
                float[] R = piece.get(n + 1);

                // Agregar Q
                points[index] = Q[0];
                points[index + 1] = Q[1];
                points[index + 2] = Q[2];
                points[index + 3] = 1f;

                normals[index] = nx;
                normals[index + 1] = ny;
                normals[index + 2] = nz;
                normals[index + 3] = 1f;

                index += 4;

                // Agregar R
                points[index] = R[0];
                points[index + 1] = R[1];
                points[index + 2] = R[2];
                points[index + 3] = 1f;

                normals[index] = nx;
                normals[index + 1] = ny;
                normals[index + 2] = nz;
                normals[index + 3] = 1f;

                index += 4;
            }
        }
//
//// Verificar el índice final
//        if (index != totalSize) {
//            System.out.println("Error: El índice final no coincide con el tamaño esperado.");
//            return;
//        }
//
//// Imprimir información de depuración
//        System.out.println("Total Size: " + totalSize);
//        System.out.println("Index: " + index);
//        System.out.println("points.length: " + points.length);
//        System.out.println("normals.length: " + normals.length);

// bind an array of triangle fan
        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(points);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        gl4.glBufferData(GL4.GL_ARRAY_BUFFER, fbVertices.limit() * 4L, fbVertices, GL4.GL_STATIC_DRAW);
        gl4.glVertexAttribPointer(0, 4, GL4.GL_FLOAT, false, 0, 0);

        FloatBuffer fbNormals = Buffers.newDirectFloatBuffer(normals);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
        gl4.glBufferData(GL4.GL_ARRAY_BUFFER, fbNormals.limit() * 4L, fbNormals, GL4.GL_STATIC_DRAW);
        gl4.glVertexAttribPointer(1, 4, GL4.GL_FLOAT, false, 0, 0);

// Enable Stencil buffer to draw concave polygons
        gl4.glStencilMask(0b00000001);  // Last bit for filling

// set stencil buffer to invert value on draw, 0 to 1 and 1 to 0
        gl4.glStencilFunc(GL4.GL_EQUAL, 0, 2);
        gl4.glStencilOp(GL4.GL_INVERT, GL4.GL_INVERT, GL4.GL_INVERT);

// disable writing to color buffer
        gl4.glColorMask(false, false, false, false);

// draw polygon into stencil buffer
        gl4.glDepthMask(false);
        gl4.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);
        gl4.glDepthMask(true);

// set stencil buffer to only keep pixels when value in buffer is 1
        gl4.glStencilFunc(GL4.GL_EQUAL, 1, 3);
        gl4.glStencilOp(GL4.GL_ZERO, GL4.GL_ZERO, GL4.GL_ZERO);

// enable color again
        gl4.glColorMask(true, true, true, true);
        gl4.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);

    }

    void drawFill(Shape s) {
        ArrayList<ArrayList<float[]>> pieces = s.getPath().getPolygonalPieces();
        if (pieces.isEmpty()) {
            s.computePolygonalPieces();
        }
        if (s.getMp().getFillColor().getAlpha() == 0) {
            return;
        }

        final PaintStyle fillStylable = s.getMp().getFillColor();
        ShaderLoader shader = fillShader;
        defineColorParametersForFillShader(fillStylable, shader);
//        System.out.println("Shader uniform color: "+shapeColors[2]);
        //Generates a triangle fan array
        Vec normal = s.getNormalVector();
        float nx = (float) normal.x;
        float ny = (float) normal.y;
        float nz = (float) normal.z;
        ArrayList<Float> coords = getFloats(pieces);

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
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        gl4.glBufferData(GL4.GL_ARRAY_BUFFER, fbVertices.limit() * 4L, fbVertices, GL4.GL_STATIC_DRAW);
        gl4.glVertexAttribPointer(0, 4, GL4.GL_FLOAT, false, 0, 0);

        FloatBuffer fbNormals = Buffers.newDirectFloatBuffer(normals);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
        gl4.glBufferData(GL4.GL_ARRAY_BUFFER, fbNormals.limit() * 4L, fbNormals, GL4.GL_STATIC_DRAW);
        gl4.glVertexAttribPointer(1, 4, GL4.GL_FLOAT, false, 0, 0);

        //Enable Stencil buffer to draw concave polygons
        gl4.glStencilMask(0b00000001);//Last bit for filling

        // set stencil buffer to invert value on draw, 0 to 1 and 1 to 0
        //Pass the stencil test if the pixel doesnt belong to the drawed contour
        gl4.glStencilFunc(GL4.GL_EQUAL, 0, 2);
//        if (!s.isIsConvex()) {
        gl4.glStencilOp(GL4.GL_INVERT, GL4.GL_INVERT, GL4.GL_INVERT);
        // disable writing to color buffer
        gl4.glColorMask(false, false, false, false);
        // draw polygon into stencil buffer
        //Has to disable depth mask in order to avoid z fightint for concave polygons
        gl4.glDepthMask(false);
        gl4.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);
        gl4.glDepthMask(true);
        // set stencil buffer to only keep pixels when value in buffer is 1
        //Bit 2 is set to 1 if the contour is drawed in that fragment, so stencil
        //mask has to be 01 in order to draw fill
        //00->No contour, no fill
        //10->Contour, no fill
        //11->Contour&fill (don't draw fill here!)
        //01->Fill only
        gl4.glStencilFunc(GL4.GL_EQUAL, 1, 3);
        gl4.glStencilOp(GL4.GL_ZERO, GL4.GL_ZERO, GL4.GL_ZERO);

        // enable color again
        gl4.glColorMask(true, true, true, true);
//        gl3.glDepthMask(false);
//        drawWholeScreen();//Draw whole screen with current color
        gl4.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, fbVertices.limit() / 4);
//        gl3.glDepthMask(true);
    }

    private static @NotNull ArrayList<Float> getFloats(ArrayList<ArrayList<float[]>> pieces) {
        ArrayList<Float> coords = new ArrayList<>();

        //We are going to generate a triangle strip
        //This is the first and common point...
        float[] P = pieces.get(0).get(0);
        coords.add(P[0]);
        coords.add(P[1]);
        coords.add(P[2]);
        coords.add(1f);
        //Iterate over all connected components
        for (int k = 0; k < pieces.size(); k++) {
            ArrayList<float[]> piece = pieces.get(k);
            for (int n = (k == 0 ? 1 : 0); n < piece.size() - 1; n++) {
                float[] Q = piece.get(n);
                float[] R = piece.get(n + 1);
                coords.add(Q[0]);
                coords.add(Q[1]);
                coords.add(Q[2]);
                coords.add(1f);
                coords.add(R[0]);
                coords.add(R[1]);
                coords.add(R[2]);
                coords.add(1f);
            }
        }
        //It seems I need this triangles as well...
        for (int k = 0; k < pieces.size() - 1; k++) {
            float[] Q = pieces.get(k).get(0);
            float[] R = pieces.get(k + 1).get(0);
            coords.add(Q[0]);
            coords.add(Q[1]);
            coords.add(Q[2]);
            coords.add(1f);
            coords.add(R[0]);
            coords.add(R[1]);
            coords.add(R[2]);
            coords.add(1f);
        }
        return coords;
    }

    private void defineColorParametersForFillShader(final PaintStyle fillStylable, ShaderLoader shader) {
        gl4.glUniform2f(shader.getUniformVariable("resolution"), queue.width, queue.height);

        if (fillStylable instanceof JMColor) {
            float[] shapeColors = toColor(fillStylable);
            gl4.glUniform4f(shader.getUniformVariable("unifColor"), shapeColors[0], shapeColors[1], shapeColors[2], shapeColors[3]);
            gl4.glUniform1i(shader.getUniformVariable("fillType"), 0);//Solid color
        }
        if (fillStylable instanceof JMLinearGradient) {
            JMLinearGradient lg = (JMLinearGradient) fillStylable;
            lg.getStops();
            //TODO: Stuff to do here needed...
            gl4.glUniform1i(shader.getUniformVariable("fillType"), 1);//Linear Gradient
        }
        if (fillStylable instanceof JMRadialGradient) {
            JMRadialGradient rg = (JMRadialGradient) fillStylable;
            rg.getStops();
            //TODO: Stuff to do here needed...
            gl4.glUniform1i(shader.getUniformVariable("fillType"), 2);//Radial Gradient
        }

    }

    /**
     * Convert a JMColor to a float array {r,g,b,a}
     *
     * @param st Stylable. If not a JMColor, return black
     * @return The color components
     */
    private float[] toColor(PaintStyle st) {
        if (st instanceof JMColor) {
            JMColor col = (JMColor) st;
            float r = (float) col.r;
            float g = (float) col.g;
            float b = (float) col.b;
            float alpha = (float) col.getAlpha();
            return new float[]{r, g, b, alpha};
        } else {
            return new float[]{0f, 0f, 0f, 1f};
        }
    }

    /**
     * Draw a thin contour line
     *
     * @param s Shape to draw
     * @param pieces Rectified
     */
    void drawContour(Shape s) {
        ArrayList<ArrayList<float[]>> pieces = s.getPath().getPolygonalPieces();
        if (pieces.isEmpty()) {
            s.computePolygonalPieces();
        }
        if (s.getMp().getDrawColor().getAlpha() == 0) {
            return;
        }

        //Set uniform variables
        float[] shapeColors = toColor(s.getMp().getDrawColor());
        float thickness = s.getMp().getThickness().floatValue();
        float eyeX = (float) queue.camera.eye.v.x;
        float eyeY = (float) queue.camera.eye.v.y;
        float eyeZ = (float) queue.camera.eye.v.z;
        float lookX = (float) queue.camera.look.v.x;
        float lookY = (float) queue.camera.look.v.y;
        float lookZ = (float) queue.camera.look.v.z;
        gl4.glUniform2f(thinLineShader.getUniformVariable("resolution"), queue.width, queue.height);
        gl4.glUniform4f(thinLineShader.getUniformVariable("unifColor"), shapeColors[0], shapeColors[1], shapeColors[2], shapeColors[3]);
        gl4.glUniform3f(thinLineShader.getUniformVariable("eye"), eyeX, eyeY, eyeZ);
        gl4.glUniform3f(thinLineShader.getUniformVariable("lookAt"), lookX, lookY, lookZ);

        gl4.glUniform1f(thinLineShader.getUniformVariable("Thickness"), thickness);
        int size = 0;
        for (ArrayList<float[]> piece : pieces) {
            size += piece.size();//Each piece has these number of segments...
        }
        size *= 16;//and each segment is defined by 4 points with 4 coordinates each one
        float[] vertexArray = new float[size];
        AtomicInteger counter = new AtomicInteger(0);

        pieces.parallelStream().forEach(piece -> {
            int pieceSize = piece.size();
            if (!piece.isEmpty()) {
//                boolean isClosedLoop = piece.get(pieceSize - 1).isEquivalentTo(piece.get(0), 0.000001);
                boolean isClosedLoop = (dist(piece.get(pieceSize - 1), piece.get(0)) < 0.000001);

                for (int n = 0; n < pieceSize - 1; n++) {
                    float[] p = piece.get(n);
                    float[] q = piece.get(n + 1);
                    float[] r = (n > 0) ? piece.get(n - 1) : (isClosedLoop ? piece.get(pieceSize - 2) : p);
                    float[] t = (n < pieceSize - 2) ? piece.get(n + 2) : (isClosedLoop && n == pieceSize - 2 ? piece.get(1) : q);

                    int localCounter = counter.getAndAdd(16);
                    fillVertexArray(vertexArray, localCounter, r, p, q, t);
                }
            }
        });
        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertexArray);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        gl4.glBufferData(GL4.GL_ARRAY_BUFFER, fbVertices.limit() * 4L, fbVertices, GL4.GL_STATIC_DRAW);
        gl4.glVertexAttribPointer(0, 4, GL4.GL_FLOAT, false, 0, 0);
        gl4.glDrawArrays(GL4.GL_LINES_ADJACENCY_EXT, 0, vertexArray.length / 4);
    }

    private float dist(float[] v, float[] w) {
        return Math.abs(v[0] - w[0]) + Math.abs(v[1] - w[1]) + Math.abs(v[2] - w[2]);
    }

    private void fillVertexArray(float[] array, int startIndex, float[]... vectors) {
        int offset = 0;
        for (float[] v : vectors) {
            array[startIndex + offset] = v[0];
            array[startIndex + offset + 1] = v[1];
            array[startIndex + offset + 2] = v[2];
            array[startIndex + offset + 3] = 1f;
            offset += 4;
        }
    }

}
