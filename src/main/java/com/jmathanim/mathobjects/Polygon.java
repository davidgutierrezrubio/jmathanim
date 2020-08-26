/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Polygon extends JMPathMathObject {

    public Polygon() {
        this(null);
    }

    public Polygon(MathObjectDrawingProperties mp) {

        this(new ArrayList<Point>(), true, mp);
    }

    public Polygon(ArrayList<Point> vertices, boolean close, MathObjectDrawingProperties mp) {
        super(mp);
        numInterpolationPoints = 1;//TODO: Make it adaptative
        this.addVertices(vertices);
        this.isClosed = close;
        if (!vertices.isEmpty()) {
            computeJMPathFromVertices();
        }
    }

    public final void addVertices(ArrayList<Point> vertices) {
        for (Point p : vertices) {
            JMPathPoint jmPathPoint = new JMPathPoint(p, true, JMPathPoint.TYPE_VERTEX);
            jmPathPoint.isCurved=false;
            this.vertices.add(jmPathPoint);
        }
    }

    public boolean addVertex(Point p) {
        needsRecalcControlPoints = true;
        return vertices.add(new JMPathPoint(p, true, JMPathPoint.TYPE_VERTEX));
    }

    public boolean addVertex(Double x, Double y) {
        needsRecalcControlPoints = true;
        return this.addVertex(new Point(x, y));
    }

    public boolean addVertex(Double x, Double y, Double z) {
        needsRecalcControlPoints = true;
        Point p=new Point(x, y, z);
        return this.addVertex(p);
    }

    public void close() {
        jmpath.close();
        needsRecalcControlPoints = true;
    }

    public void open() {
        jmpath.open();
        needsRecalcControlPoints = true;
    }

    @Override
    public void moveTo(Vec coords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MathObject copy() {
        Polygon resul = new Polygon();//FODO: FIX
        return resul;
    }

    @Override
    public void draw(Renderer r) {
        if (needsRecalcControlPoints) {
            System.out.println("Update path en draw");
            computeJMPathFromVertices();
        }
//        if (drawParam >= 1) {
//            drawParam = 1;
//        }

//        JMPath c = jmpath.getSlice(drawParam);
//
//        if (drawParam < 1) {
//            c.open();
//        } else {
//            c.close();
//        }
        r.setBorderColor(mp.drawColor);
        r.setStroke(mp.getThickness(r));
        r.drawPath(this,jmpath);
    }

//    @Override
//    public void computeJMPath() {
//        //TODO: ¿Compute intermediate points?
//        JMPath jmpath2 = new JMPath();
//        for (Point p : vertices) {
//            jmpath2.add(p);
//        }
//        if (isClosed) {
//            jmpath2.close();
//        } else {
//            jmpath2.open();
//        }
//        jmpath2.curveType = JMPath.STRAIGHT;
//        jmpath = jmpath2.interpolate(20);
//        jmpath.computeControlPoints(JMPath.STRAIGHT);
//        needsRecalcControlPoints = false;
//    }
    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        System.out.println("Update path en update");
        computeJMPathFromVertices();
        updateDependents();
    }

    public ArrayList<Point> getVertices() {
        ArrayList<Point> resul=new ArrayList<>();
        for (JMPathPoint jmp: vertices)
        {
            resul.add(jmp.p);
        }
        return resul;
    }

    @Override
    public void prepareForNonLinearAnimation() {
        JMPath jmpathTemp;
        //If the path is not interpolated, do it now
//        if (!jmpath.isInterpolated) {
//            jmpathTemp = jmpath.interpolate(numInterpolationPoints);
//            jmpath.clear();
//            jmpath.addPointsFrom(jmpathTemp);
//        }
        numInterpolationPoints = 20;
        update();

    }

    @Override
    public void processAfterNonLinearAnimation() {
//        jmpath.removeInterpolationPoints();//Remove interpolation points
        numInterpolationPoints = 1;
        update();
    }

   

}
