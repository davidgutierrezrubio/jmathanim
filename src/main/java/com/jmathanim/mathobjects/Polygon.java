/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Polygon extends JMPathMathObject {

    protected ArrayList<Point> vertices;
    protected boolean isClosed;

    public Polygon() {
        this(new ArrayList<Point>(), true);
    }

    public Polygon(ArrayList<Point> vertices) {
        this(vertices, true);
    }

    public Polygon(ArrayList<Point> vertices, boolean isClosed) {
        super();
        this.vertices = vertices;
        this.isClosed = isClosed;
        if (!vertices.isEmpty()) {
            computeJMPath();
        }
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
        r.setColor(Color.GREEN);//TODO: Configs
        r.setStroke(.01);//TODO: COnfig stroke size
        r.setAlpha(alpha);
        r.drawPath(c);
    }

    @Override
    public void computeJMPath() {
        //TODO: ¿Compute intermediate points?
        JMPath jmpath2 = new JMPath();
        for (Point p : vertices) {
            jmpath2.add(p);
        }
        if (isClosed) {
            jmpath2.close();
        } else {
            jmpath2.open();
        }
        jmpath2.curveType = JMPath.STRAIGHT;
        jmpath = jmpath2.interpolate(20);
        jmpath.computeControlPoints(JMPath.STRAIGHT);
        needsRecalcControlPoints = false;
    }

    @Override
    public void scale(Vec scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        computeJMPath();
        updateDependents();
    }

    public ArrayList<Point> getVertices() {
        return vertices;
    }

   

}
