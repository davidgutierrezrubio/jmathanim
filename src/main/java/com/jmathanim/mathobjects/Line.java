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
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Line extends Shape {

    final JMPathPoint bp1, bp2;
    private final Shape visiblePiece;
    private Point p1, p2;

    /**
     * Creates a line that passes through p with direction v
     *
     * @param p Point
     * @param v Direction vector
     */
    public Line(Point p, Vec v) {
        this(p, p.add(v));
    }

    /**
     * Creates a line that passes through p1 and p2
     *
     * @param p1 First point
     * @param p2 Second point
     */
    public Line(Point p1, Point p2) {
        this(p1, p2, null);
    }

    /**
     * Creates a new line that passes through given points, with specified
     * MathDrawingProperties
     *
     * @param p1 First point
     * @param p2 Second point
     * @param mp MathDrawingProperties
     */
    public Line(Point p1, Point p2, MathObjectDrawingProperties mp) {
        super(mp);
        this.p1 = p1;
        this.p2 = p2;
        jmpath.clear(); //Super constructor adds p1, p2. Delete them
        bp1 = new JMPathPoint(new Point(0, 0), true, JMPathPoint.TYPE_VERTEX);//trivial boundary points, just to initialize objects
        bp2 = new JMPathPoint(new Point(0, 0), true, JMPathPoint.TYPE_VERTEX);//trivial boundary points, just to initialize objects
        visiblePiece = new Shape();
        visiblePiece.jmpath.addJMPoint(bp1, bp2);
        visiblePiece.mp = this.mp;
        jmpath.addPoint(p1, p2);
        jmpath.getJMPoint(0).isThisSegmentVisible=false;
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
        visiblePiece.draw(r);

    }

    /**
     * Compute border points in the view area of the given renderer. This is
     * need in order to draw an "infinite" line which always extend to the whole
     * visible area. The border points are stored in bp1 and bp2
     *
     * @param r The renderer
     */
    public final void computeBoundPoints(Renderer r) {
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

//    @Override
//    public void saveState() {
//        super.saveState(); 
//        p1.saveState(); 
//        p2.saveState(); 
//    }
//
//    @Override
//    public void restoreState() {
//        super.restoreState();
//        p1.restoreState();
//        p2.restoreState();
//    }
    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    @Override
    public Point getCenter() {
        //Center of an infinite line doesn't exists. Take first point instead.
        return p1;
    }

    public static Line XAxis() {
        return new Line(new Point(0, 0), new Point(1, 0));
    }

    public static Line YAxis() {
        return new Line(new Point(0, 0), new Point(1, 0));
    }
    public static Line XYBisector()
{
    return new Line(new Point(0,0),new Point(1,1));
}
}
