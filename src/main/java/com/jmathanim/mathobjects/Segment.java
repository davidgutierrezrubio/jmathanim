/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Vec;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Segment extends JMPathMathObject {

    Point p1, p2;


    public Segment(Vec v1, Vec v2) {
        this(new Point(v1), new Point(v2), null);
    }

    public Segment(Point p1, Point p2) {
        this(p1, p2, null);
    }

    public Segment(Point p1, Point p2, MathObjectDrawingProperties mp) {
        super(mp);
        this.p1 = p1;
        this.p2 = p2;
        if (p1 != null && p2 != null) {
            computeCurve();
        }
    }

    @Override
    public Point getCenter() {
        return p1.interpolate(p2, .5);
    }

    public final void computeCurve() {
        JMPathPoint jmp1 = new JMPathPoint(p1, true, JMPathPoint.TYPE_VERTEX);
        jmp1.isCurved = false;
        jmp1.isVisible=false;
        jmpath.addPoint(jmp1);
        JMPathPoint jmp2 = new JMPathPoint(p2, true, JMPathPoint.TYPE_VERTEX);
        jmp2.isCurved = false;
        jmpath.addPoint(jmp2);
    }

    @Override
    public void moveTo(Vec coords) {
        Vec v1 = p1.v;
        Vec shiftVector = coords.minus(v1);
        shift(shiftVector);

    }

    @Override
    public void shift(Vec shiftVector) {
        p1.shift(shiftVector);
        p2.shift(shiftVector);

    }

    @Override
    public Segment copy() {
        return new Segment(p1.copy(), p2.copy(),mp.copy());
    }

    @Override
    public void prepareForNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
