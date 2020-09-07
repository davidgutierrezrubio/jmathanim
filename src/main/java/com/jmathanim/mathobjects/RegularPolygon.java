/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class RegularPolygon extends Polygon {

    int numVertices;
    double side;
    private final Point firstPoint;
    private final ArrayList<Segment> radius;
    private final ArrayList<Segment> apothem;

    public RegularPolygon(int numVertices, double side) {
        super();
        this.numVertices = numVertices;
        this.side = side;
        firstPoint = new Point(side, 0);
        radius = new ArrayList<>();
        apothem = new ArrayList<>();
        isClosed=true;
        computeVertices();
        computeJMPathFromVertices();
        computeRadiusAndApothems();
        
    }

    private void computeVertices() {
        this.vertices.clear();

        Point newPoint = (Point) firstPoint.copy();
        for (int n = 0; n < numVertices; n++) {
            double alpha = 2 * n * Math.PI / numVertices;
            Vec moveVector = new Vec(side * Math.cos(alpha), side * Math.sin(alpha));
            newPoint = newPoint.add(moveVector);
//            dependsOn(newPoint);
//            cousins.add(newPoint);
//            addObjectToScene(newPoint);
            this.addVertex(newPoint);

        }
    }

//    @Override
//    public void computeJMPath() {
//        jmpath.clear();
//        for (Point p: vertices)
//        {
//          jmpath.add(p);  
//        }
//        
//        jmpath.close();
//        jmpath.curveType = JMPath.STRAIGHT;
//        jmpath = jmpath.interpolate(20);
//
//        jmpath.computeControlPoints();
//        
//    }
    public ArrayList<Segment> getRadius() {
        return radius;
    }

    public ArrayList<Segment> getApothem() {
        return apothem;
    }

    private void computeRadiusAndApothems() {
        radius.clear();
        for (JMPathPoint p : jmpath.jmPathPoints) {
            if (p.type == JMPathPoint.TYPE_VERTEX) {
                radius.add(new Segment(center, p.p));
            }
        }

        apothem.clear();
        Point q = null;
        for (JMPathPoint p : jmpath.jmPathPoints) {
            if (p.type == JMPathPoint.TYPE_VERTEX) {
                if (q != null) {
                    apothem.add(new Segment(center, new MiddlePoint(p.p, q)));
                }
                q = p.p;
            }
        }
//Now last apothem
        apothem.add(new Segment(center, vertices.get(0).p.interpolate(q, .5)));
    }

}
