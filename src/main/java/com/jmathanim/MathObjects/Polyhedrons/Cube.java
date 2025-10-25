/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmathanim.MathObjects.Polyhedrons;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Cube extends Polyhedron {

    public static Cube make() {
        return make(true, true);
    }

    public static Cube make(boolean attachEdges, boolean attachFaces) {
        Cube resul = new Cube();
        resul.attachEdges = attachEdges;
        resul.attachFaces = attachFaces;
        resul.build();
        return resul;
    }

    private Cube() {
    }

    private void build() {
        buildVerticesFromArray(arrayVertices);
        buildEdgesFromArray(arrayEdges);
        buildFacesFromArray(4, arrayFaces);
        this.faces.drawAlpha(0);
    }

    private final float[] arrayVertices = {
        // Vértices del cubo
        -0.5f, -0.5f, -0.5f, // Vértice 0
        0.5f, -0.5f, -0.5f, // Vértice 1
        0.5f, 0.5f, -0.5f, // Vértice 2
        -0.5f, 0.5f, -0.5f, // Vértice 3
        -0.5f, -0.5f, 0.5f, // Vértice 4
        0.5f, -0.5f, 0.5f, // Vértice 5
        0.5f, 0.5f, 0.5f, // Vértice 6
        -0.5f, 0.5f, 0.5f // Vértice 7
    };
    private final int[] arrayEdges = {
        // Aristas de la base inferior
        0, 1, // Arista 0-1
        1, 2, // Arista 1-2
        2, 3, // Arista 2-3
        3, 0, // Arista 3-0

        // Aristas de la base superior
        4, 5, // Arista 4-5
        5, 6, // Arista 5-6
        6, 7, // Arista 6-7
        7, 4, // Arista 7-4

        // Aristas verticales que conectan las bases
        0, 4, // Arista 0-4
        1, 5, // Arista 1-5
        2, 6, // Arista 2-6
        3, 7 // Arista 3-7
    };
    private final int[] arrayFaces = {
        // Cara trasera (z = -0.5)
        0, 1, 2, 3, // Cara 0

        // Cara delantera (z = 0.5)
        4, 5, 6, 7, // Cara 1

        // Cara izquierda (x = -0.5)
        0, 3, 7, 4, // Cara 2

        // Cara derecha (x = 0.5)
        1, 2, 6, 5, // Cara 3

        // Cara inferior (y = -0.5)
        0, 1, 5, 4, // Cara 4

        // Cara superior (y = 0.5)
        3, 2, 6, 7 // Cara 5
    };

}
