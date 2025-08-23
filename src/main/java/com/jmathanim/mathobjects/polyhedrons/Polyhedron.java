/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmathanim.mathobjects.polyhedrons;

import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

import java.util.Arrays;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Polyhedron extends MathObjectGroup {

    boolean attachEdges = true;
    boolean attachFaces = true;
    public final MultiShapeObject edges;
    public final MathObjectGroup vertices;
    public final MultiShapeObject faces;

    public Polyhedron() {
        edges = MultiShapeObject.make();
        vertices = MathObjectGroup.make();
        faces = MultiShapeObject.make();
        this.add(edges);
        this.add(vertices);
        this.add(faces);
    }

    
    
    protected void buildVerticesFromArray(float[] coords) {
        for (int i = 0; i < coords.length; i += 3) {
            vertices.add(Point.at(coords[i], coords[i + 1], coords[i + 2]));
        }
    }

    protected void buildEdgesFromArray(int[] indices) {
        for (int i = 0; i < indices.length; i += 2) {
            buildEdge(indices[i], indices[i + 1]);
        }
    }

    protected void buildFacesFromArray(int numSides, int[] indices) {
        for (int i = 0; i < indices.length; i += numSides) {
            buildFace(Arrays.copyOfRange(indices, i, i + numSides));
        }
    }

    public Shape buildEdge(int a, int b) {
        Shape edge;
        if (attachEdges) {
            edge = Shape.segment(
                    (Point) vertices.get(a),
                    (Point) vertices.get(b));
        } else {
            edge = Shape.segment(
                    ((Point) vertices.get(a)).copy(),
                    ((Point) vertices.get(b)).copy());
        }

        edges.add(edge);
        return edge;
    }

    public Shape buildFace(int[] indices) {
        Point[] points = new Point[indices.length];
        if (attachFaces) {
            for (int i = 0; i < indices.length; i++) {
                points[i] = (Point) vertices.get(indices[i]);
            }
        }
        else
        {
             for (int i = 0; i < indices.length; i++) {
                points[i] = ((Point) vertices.get(indices[i])).copy();
            }
        }
        Shape face = Shape.polygon(points);
        faces.add(face);
        return face;
    }
}
