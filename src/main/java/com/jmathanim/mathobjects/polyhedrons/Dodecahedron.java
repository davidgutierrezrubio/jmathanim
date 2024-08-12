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
package com.jmathanim.mathobjects.polyhedrons;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Dodecahedron extends Polyhedron {

    public static Dodecahedron make() {
        return make(true, true);
    }

    public static Dodecahedron make(boolean attachEdges, boolean attachFaces) {
        Dodecahedron resul = new Dodecahedron();
        resul.attachEdges = attachEdges;
        resul.attachFaces = attachFaces;
        resul.build();
        return resul;
    }

    private void build() {
        buildVerticesFromArray(arraYVertices);
        buildEdgesFromArray(arrayEdges);
        buildFacesFromArray(5, arrayFaces);
        this.faces.drawAlpha(0);
    }
    float phi = (1.0f + (float) Math.sqrt(5.0)) / 2.0f;  // Aproximadamente 1.618

    float[] arraYVertices = {
        // Permutaciones de (±1, ±1, ±1)
        -1.0f, -1.0f, -1.0f, // Vértice 0
        1.0f, -1.0f, -1.0f, // Vértice 1
        -1.0f, 1.0f, -1.0f, // Vértice 2
        1.0f, 1.0f, -1.0f, // Vértice 3
        -1.0f, -1.0f, 1.0f, // Vértice 4
        1.0f, -1.0f, 1.0f, // Vértice 5
        -1.0f, 1.0f, 1.0f, // Vértice 6
        1.0f, 1.0f, 1.0f, // Vértice 7

        // Permutaciones de (0, ±1/φ, ±φ)
        0.0f, -1.0f / phi, -phi, // Vértice 8
        0.0f, 1.0f / phi, -phi, // Vértice 9
        0.0f, -1.0f / phi, phi, // Vértice 10
        0.0f, 1.0f / phi, phi, // Vértice 11

        -1.0f / phi, -phi, 0.0f, // Vértice 12
        1.0f / phi, -phi, 0.0f, // Vértice 13
        -1.0f / phi, phi, 0.0f, // Vértice 14
        1.0f / phi, phi, 0.0f, // Vértice 15

        -phi, 0.0f, -1.0f / phi, // Vértice 16
        -phi, 0.0f, 1.0f / phi, // Vértice 17
        phi, 0.0f, -1.0f / phi, // Vértice 18
        phi, 0.0f, 1.0f / phi // Vértice 19
    };
    int[] arrayEdges = {
        0, 8, 0, 9, 0, 12, 0, 14, // Aristas del vértice 0
        1, 8, 1, 9, 1, 13, 1, 15, // Aristas del vértice 1
        2, 10, 2, 11, 2, 12, 2, 14, // Aristas del vértice 2
        3, 10, 3, 11, 3, 13, 3, 15, // Aristas del vértice 3
        4, 16, 4, 17, 4, 12, 4, 14, // Aristas del vértice 4
        5, 16, 5, 17, 5, 13, 5, 15, // Aristas del vértice 5
        6, 18, 6, 19, 6, 12, 6, 14, // Aristas del vértice 6
        7, 18, 7, 19, 7, 13, 7, 15, // Aristas del vértice 7
        8, 10, 8, 18, // Aristas entre vértices especiales
        9, 11, 9, 19, // Aristas entre vértices especiales
        16, 18, 17, 19 // Otras aristas
    };
    int[] arrayFaces = {
        0, 12, 14, 16, 8 // Cara 0
//        1, 8, 16, 18, 10, // Cara 1
//        2, 10, 18, 19, 11, // Cara 2
//        3, 11, 19, 17, 13, // Cara 3
//        4, 12, 16, 17, 13, // Cara 4
//        5, 17, 16, 18, 15, // Cara 5
//        6, 18, 14, 15, 12, // Cara 6
//        7, 19, 17, 15, 13, // Cara 7
//        8, 10, 14, 12, 16, // Cara 8
//        9, 11, 15, 13, 19, // Cara 9
//        10, 18, 16, 14, 12, // Cara 10
//        11, 19, 17, 15, 13 // Cara 11
    };

}
