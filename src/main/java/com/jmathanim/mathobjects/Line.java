/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;

/**
 * Represents an infinite line, given by 2 points.
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Line extends Segment {

    final Point bp1, bp2;

    /**
     * Creates a line that passes through p with direction v
     * @param p
     * @param v
     */
    public Line(Point p, Vec v) {
        this(p, p.add(v));
    }

    /**
     * Creates a new line that passes through given points
     * @param p1
     * @param p2
     */
    public Line(Point p1, Point p2) {
        super(p1, p2);
        bp1 = new Point(0, 0);//trivial boundary points, just to initialize objects
        bp2 = new Point(0, 0);
    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
    }

    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Renderer r) {
        jmpath.clear();
        computeBoundPoints(r);
        jmpath.addPoint(bp1);
        jmpath.addPoint(bp2);
        r.setColor(mp.color);
        r.setStroke(mp.getThickness(r));
        r.setAlpha(mp.alpha);
//        r.drawLine(bp1.v.x, bp1.v.y, bp2.v.x, bp2.v.y);
        r.drawPath(jmpath);
        p1.draw(r);
        p2.draw(r);
        bp1.draw(r);
        bp2.draw(r);

    }

    public void computeBoundPoints(Renderer r) {
    Rect rect=r.getCamera().getMathBoundaries();
        double[] intersectLine = rect.intersectLine(p1.v.x, p1.v.y, p2.v.x, p2.v.y);
        
        if (intersectLine==null) {
            //If there are no intersect points, take p1 and p2 (workaround)
            bp1.v.x=p1.v.x;
            bp1.v.y=p1.v.y;
            bp2.v.x=p2.v.x;
            bp2.v.y=p2.v.y;
        }
        else
        {
            bp1.v.x=intersectLine[0];
            bp1.v.y=intersectLine[1];
            bp2.v.x=intersectLine[2];
            bp2.v.y=intersectLine[3];
        }
        
    }
    

    }
