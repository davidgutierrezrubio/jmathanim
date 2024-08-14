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

    private Dodecahedron() {
    }

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
        buildVerticesFromArray(arrayVertices);
        buildEdgesFromArray(arrayEdges);
        buildFacesFromArray(5, arrayFaces);
        this.faces.drawAlpha(0);
    }
    float phi = (1.0f + (float) Math.sqrt(5.0)) / 2.0f;  // Aproximadamente 1.618

    // Lista de puntos (vértices)
    float[] arrayVertices = {
        1, 1, 1, // 0
        1, 1, -1, // 1
        1, -1, 1, // 2
        1, -1, -1, // 3
        -1, 1, 1, // 4
        -1, 1, -1, // 5
        -1, -1, 1, // 6
        -1, -1, -1, // 7
        0, phi, 1 / phi, // 8
        0, phi, -1 / phi, // 9
        0, -phi, 1 / phi, // 10
        0, -phi, -1 / phi, // 11
        1 / phi, 0, phi, // 12
        1 / phi, 0, -phi, // 13
        -1 / phi, 0, phi, // 14
        -1 / phi, 0, -phi, // 15
        phi, 1 / phi, 0, // 16
        phi, -1 / phi, 0, // 17
        -phi, 1 / phi, 0, // 18
        -phi, -1 / phi, 0 // 19
    };

    // Lista de aristas
    int[] arrayEdges = {
        0, 8, // 0
        8, 4, // 1
        4, 14, // 2
        14, 12, // 3
        12, 0, // 4
        0, 16, // 5
        16, 17, // 6
        17, 2, // 7
        2, 12, // 8
        14, 6, // 9
        6, 10, // 10
        10, 2, // 11
        10, 11, // 12
        11, 3, // 13
        3, 17, // 14
        8, 9, // 15
        9, 1, // 16
        1, 16, // 17
        1, 13, // 18
        13, 3, // 19
        18, 19, // 20
        19, 7, // 21
        7, 15, // 22
        15, 5, // 23
        5, 18, // 24
        4, 18, // 25
        7, 11, // 26
        9, 5, // 27
        6, 19, // 28
        13, 15 // 29
    };

    // Lista de caras
    int[] arrayFaces = {
        1, 16, 0, 8, 9, // 0
        1, 9, 5, 15, 13, // 1
        15, 5, 18, 19, 7, // 2
        19, 7, 11, 10, 6, // 3
        10, 11, 3, 17, 2, // 4
        17, 2, 12, 0, 16, // 5
        18, 4, 8, 9, 5, // 6
        7, 11, 3, 13, 15, // 7
        4, 8, 0, 12, 14, // 8
        6, 19, 18, 4, 14, // 9
        1, 13, 3, 17, 16, // 10
        10, 6, 14, 12, 2 // 11
    };

}
