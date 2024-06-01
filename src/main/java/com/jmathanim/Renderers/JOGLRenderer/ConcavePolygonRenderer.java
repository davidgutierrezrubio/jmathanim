/*
 * Copyright (C) 2024 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Renderers.JOGLRenderer;

import com.jmathanim.mathobjects.Point;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConcavePolygonRenderer {

    private static int stencilDepth = 0; // Profundidad del stencil

    public static void drawConcavePolygon(float[] points) {
        if (points.length % 3 != 0) {
            throw new IllegalArgumentException("La lista de puntos debe tener coordenadas (x, y, z) para cada vértice");
        }
        // Crea un buffer para los vértices del polígono
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(points.length);
        for (Float coord : points) {
            verticesBuffer.put(coord);
        }
        verticesBuffer.flip();

        // Habilita el modo de dibujo de polígono
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i < points.length; i += 3) {
            GL11.glVertex3f(points[i], points[i + 1], points[i + 2]);
        }

        GL11.glEnd();

        
        //Metodo alternativo que puede ser más eficiente. Probar cuando funcione
//        // Habilita el uso de los datos de los vértices
//        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//
//// Especifica el buffer de vértices
//        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, verticesBuffer);
//
//// Dibuja el polígono
//        GL11.glDrawArrays(GL11.GL_POLYGON, 0, points.length / 3);
//
//// Deshabilita el uso de los datos de los vértices
//        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);


if (false) {
        // Incrementa la profundidad del stencil
        stencilDepth++;

        // Dibuja el contorno del polígono en el stencil buffer
        GL11.glStencilMask(0xFF); // Habilita la escritura en el stencil buffer
        GL11.glStencilFunc(GL11.GL_ALWAYS, stencilDepth, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // Reemplaza el valor del stencil con la profundidad actual
        GL11.glBegin(GL11.GL_LINE_LOOP); // Dibuja el contorno del polígono
        for (int i = 0; i < points.length; i += 3) {
            GL11.glVertex3f(points[i], points[i + 1], points[i + 2]);
        }
        GL11.glEnd();

        // Configura el relleno del polígono utilizando el stencil buffer
        GL11.glStencilFunc(GL11.GL_EQUAL, stencilDepth, 0xFF);
        GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO); // Deshabilita la escritura en el stencil buffer

        // Dibuja el polígono relleno
        GL11.glColor3f(1.0f, 1.0f, 1.0f); // Color de relleno blanco
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i < points.length; i += 3) {
            GL11.glVertex3f(points[i], points[i + 1], points[i + 2]);
        }
        GL11.glEnd();
}
        // Libera el buffer de vértices
        MemoryUtil.memFree(verticesBuffer);
    }
}
