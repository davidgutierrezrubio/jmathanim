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
    public double radius, angle;
    protected boolean closePath;

    public Arc(double x, double y, double radius, double angle) {
        super();
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.angle = angle;
        setDrawParam(1);//Draw parameter to 1, draw the full arc
        closePath = false;
        computeJMPath();

    }

    @Override
    public Vec getCenter() {
        return new Vec(x, y);
    }

    @Override
    public void draw(Renderer r) {
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
        double x0 = x + radius;
        double y0 = y;
        double x1, y1;
        jmpath = new JMPath();
        jmpath.close();
        for (double alpha = 0; alpha < angle; alpha += 0.25) {
            x1 = x + radius * Math.cos(alpha);
            y1 = y + radius * Math.sin(alpha);
            jmpath.add(new Vec(x1, y1));
        }
        jmpath.computeControlPoints(JMPath.CURVED);
    }

    @Override
    public void moveTo(Vec coords) {
        x = coords.x;
        y = coords.y;
    }

    @Override
    public void shift(Vec shiftVector) {
        x += shiftVector.x;
        y += shiftVector.y;
    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
