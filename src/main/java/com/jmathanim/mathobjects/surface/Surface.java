/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects.surface;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.JOGLRenderer.JOGLRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

import java.util.ArrayList;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Surface extends MathObject {

    public final MathObjectGroup vertices;
    public final ArrayList<HalfEdge> halfEdges;
    public final ArrayList<Face> faces;

    public Surface() {
        vertices = new MathObjectGroup();
        halfEdges = new ArrayList<>();
        faces = new ArrayList<>();
    }

    public Surface(ArrayList<Point> vertices, ArrayList<HalfEdge> halfEdges, ArrayList<Face> faces) {
        this.vertices = new MathObjectGroup();
        this.vertices.getObjects().addAll(vertices);
        this.halfEdges = halfEdges;
        this.faces = faces;
    }

    @Override
    protected Rect computeBoundingBox() {
        return this.vertices.getBoundingBox();
    }

    @Override
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int createFace() {
        this.faces.add(new Face());
        return this.faces.size() - 1;
    }

//    public Shape faceToShape(int face) {
//        Face f=this.faces.get(face);
//        for (int i = 0; i < f.faceHalfEdges.size(); i++) {
//            HalfEdge hedge = f.faceHalfEdges.get(i);
//            this.vertices.get(hedge.toVertex);
//            
//        }
//    }
    public HalfEdge addHalfEdge(int toVertex, int face, int oppositeEdge, int nextEdge) {
        HalfEdge e = new HalfEdge(toVertex, face, oppositeEdge, nextEdge);
        halfEdges.add(e);
        e.thisEdge = halfEdges.size() - 1;
        this.faces.get(face).faceHalfEdges.add(e);
        return e;
    }

    @Override
    public <T extends MathObject> T copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (r instanceof JOGLRenderer) {
            this.vertices.draw(scene, r, cam);
            JOGLRenderer jr = (JOGLRenderer) r;
            jr.drawSurface(this);
        }
    }
}
