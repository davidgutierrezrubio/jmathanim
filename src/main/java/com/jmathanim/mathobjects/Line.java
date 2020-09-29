/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;

/**
 * Represents an infinite line, given by 2 points.
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Line extends Shape {

    final JMPathPoint bp1, bp2;
    final Point p1,p2;
    
    
    /**
     * Creates a line that passes through p with direction v
     *
     * @param p
     * @param v
     */
    public Line(Point p, Vec v) {
        this(p, p.add(v));
    }

    public Line(Point p1, Point p2) {
        this(p1, p2, null);
    }

    /**
     * Creates a new line that passes through given points
     *
     * @param p1
     * @param p2
     */
    public Line(Point p1, Point p2, MathObjectDrawingProperties mp) {
        super(mp);
        this.p1=p1;
        this.p2=p2;
        jmpath.clear(); //Super constructor adds p1, p2. Delete them
        bp1 = new JMPathPoint(new Point(0, 0), true, JMPathPoint.TYPE_VERTEX);//trivial boundary points, just to initialize objects
        bp2 = new JMPathPoint(new Point(0, 0), true, JMPathPoint.TYPE_VERTEX);//trivial boundary points, just to initialize objects
        jmpath.addJMPoint(bp1);
        jmpath.addJMPoint(bp2);
    }

    @Override
    public Line copy() {
        Line resul = new Line(p1.copy(), p2.copy());
        resul.mp.copyFrom(mp);
        return resul;
    }


    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Renderer r) {
        computeBoundPoints(r);
        super.draw(r);
    }

    public void computeBoundPoints(Renderer r) {
        Rect rect = r.getCamera().getMathView();
        double[] intersectLine = rect.intersectLine(p1.v.x, p1.v.y, p2.v.x, p2.v.y);

        if (intersectLine == null) {
            //If there are no intersect points, take p1 and p2 (workaround)
            bp1.p.v.x = p1.v.x;
            bp1.p.v.y = p1.v.y;
            bp2.p.v.x = p2.v.x;
            bp2.p.v.y = p2.v.y;
        } else {
            bp1.p.v.x = intersectLine[0];
            bp1.p.v.y = intersectLine[1];
            bp2.p.v.x = intersectLine[2];
            bp2.p.v.y = intersectLine[3];
        }
        bp1.cp1.v.x = bp1.p.v.x;
        bp1.cp1.v.y = bp1.p.v.y;
        bp1.cp2.v.x = bp1.p.v.x;
        bp1.cp2.v.y = bp1.p.v.y;
        bp2.cp1.v.x = bp2.p.v.x;
        bp2.cp1.v.y = bp2.p.v.y;
        bp2.cp2.v.x = bp2.p.v.x;
        bp2.cp2.v.y = bp2.p.v.y;

    }

}
