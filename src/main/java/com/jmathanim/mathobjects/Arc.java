/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arc extends JMPathMathObject {

    public double x, y, z;
    public double radiusx, radiusy, angle;
    protected boolean closePath;
    public boolean isCurved;
    public double step;

    public Arc(double x, double y, double radius, double angle) {
        super();
        isCurved = true;//Default
        step = .1;//Default
        this.x = x;
        this.y = y;
        this.radiusx = radius;
        this.radiusy = radius;
        this.angle = angle;
        setDrawParam(1);//Draw parameter to 1, draw the full arc
        closePath = false;
        needsRecalcControlPoints = true;
        computeJMPath();

    }

    @Override
    public Vec getCenter() {
        return new Vec(x, y);
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
        double x0 = x + radiusx;
        double y0 = y;
        double x1, y1;
        jmpath = new JMPath();
        jmpath.close();
        for (double alphaC = 0; alphaC < angle; alphaC += step) {
            x1 = x + radiusx * Math.cos(alphaC);
            y1 = y + radiusy * Math.sin(alphaC);
            jmpath.add(new Point(x1, y1));
        }
        if (isCurved) {
            jmpath.computeControlPoints(JMPath.CURVED);
        } else {
            jmpath.computeControlPoints(JMPath.STRAIGHT);
        }
    }

    @Override
    public void moveTo(Vec coords) {
        x = coords.x;
        y = coords.y;
        needsRecalcControlPoints = true;
    }

    @Override
    public void shift(Vec shiftVector) {
        x += shiftVector.x;
        y += shiftVector.y;
        needsRecalcControlPoints = true;

    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Vec scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //TODO: Needs to recompute center around scaleCenter
//        radiusx*=sx;
//        radiusy*=sy;
//        needsRecalcControlPoints=true;
    }

    @Override
    public void update() {
        computeJMPath();
        updateDependents();
    }

}
