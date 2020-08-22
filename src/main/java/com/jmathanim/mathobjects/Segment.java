/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Vec;
import java.awt.Color;
import java.util.Properties;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Segment extends JMPathMathObject {//TODO:Should extend Polygon class

    String[] DEFAULT_CONFIG = {
        "THICKNESS", ".01",
        "STROKEJOIN", "ROUND"
    };
    Point p1, p2;
    JMPath curve;

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
        computeCurve();
    }

    @Override
    public Point getCenter() {
        return p1.interpolate(p2, .5);
    }

    public final void computeCurve() {
        curve = new JMPath();
        JMPathPoint jmp1 = new JMPathPoint(p1, true, JMPathPoint.TYPE_VERTEX);
        jmp1.isCurved=false;
        vertices.add(jmp1);
        JMPathPoint jmp2 = new JMPathPoint(p2, true, JMPathPoint.TYPE_VERTEX);
        jmp2.isCurved=false;
        vertices.add(jmp2);
        curve.computeControlPoints();
    }

    @Override
    public void draw(Renderer r) {
        Vec v1 = p1.v;
        Vec v2 = p2.v;
        Vec vd = v2.minus(v1);
        Vec v3 = v1.add(vd.mult(1));//TODO: FIX THIS
        r.setColor(mp.color);
        r.setStroke(mp.getThickness(r));
        r.setAlpha(mp.alpha);
        r.drawLine(v1.x, v1.y, v3.x, v3.y);

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
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        computeCurve();
        updateDependents();
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
