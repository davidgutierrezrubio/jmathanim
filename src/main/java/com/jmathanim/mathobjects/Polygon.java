/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Polygon extends JMPathMathObject {

    private ArrayList<Point> vertices;
    private boolean isClosed;

    public Polygon() {
        this(new ArrayList<Point>(), true);
    }

    public Polygon(ArrayList<Point> vertices) {
        this(vertices, true);
    }

    public Polygon(ArrayList<Point> vertices, boolean isClosed) {
        this.vertices = vertices;
        this.isClosed = isClosed;
        computeJMPath();
    }

    public boolean add(Point e) {
        needsRecalcControlPoints = true;
        return vertices.add(e);
    }

    public boolean add(Double x, Double y) {
        needsRecalcControlPoints = true;
        return vertices.add(new Point(x, y));
    }

    public boolean add(Double x, Double y, Double z) {
        needsRecalcControlPoints = true;
        return vertices.add(new Point(x, y, z));
    }

    public void close() {
        needsRecalcControlPoints = true;
        isClosed = true;
    }

    public void open() {
        needsRecalcControlPoints = true;
        isClosed = false;
    }

    @Override
    public Vec getCenter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void moveTo(Vec coords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void shift(Vec shiftVector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Renderer r) {
        if (needsRecalcControlPoints) {
            computeJMPath();
        }
        if (drawParam >= 1) {
            drawParam = 1;
        }

        JMPath c = jmpath.getSlice(drawParam);
        if (drawParam < 1) {
            c.open();
        } else {
            c.close();
        }
        r.drawPath(c);
    }

@Override
        public void computeJMPath() {
        //TODO: ¿Compute intermediate points?
        jmpath = new JMPath();
        for (Point p : vertices) {
            jmpath.add(p.getCenter());
        }
        if (isClosed) {
            jmpath.close();
        } else {
            jmpath.open();
        }
        jmpath.computeControlPoints(JMPath.STRAIGHT);
        needsRecalcControlPoints=false;
    }

    @Override
    public void scale(Vec scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

}
