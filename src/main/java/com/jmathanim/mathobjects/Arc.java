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
    public double step;

    public Arc(Point cen, double radius, double angle) {
        this(cen, radius, angle, false);
    }

    public Arc(Point cen, double radius, double angle, boolean isClosed) {
        super();
        step =Math.PI*2/40;//Default
        this.x = cen.v.x;
        this.y = cen.v.y;
        this.radiusx = radius;
        this.radiusy = radius;
        this.angle = angle;
        this.isClosed=isClosed;
        needsRecalcControlPoints = true;
        numInterpolationPoints = 1;//For now, don't interpolate
        computePoints();
        computeJMPathFromVertices();
    }

    @Override
    public Point getCenter() {
        return new Point(x, y);
    }

    @Override
    public void draw(Renderer r) {
        r.setBorderColor(mp.drawColor);
        double rad = mp.getThickness(r);
//        if (needsRecalcControlPoints) {
//            computeJMPathFromVertices();
//        }

//        JMPath c = jmpath.getSlice(drawParam);
//        if (drawParam < 1) {
//            c.open();
//        } else {
//            c.close();
//        }
        r.setBorderColor(mp.drawColor);
        r.setStroke(this);
        r.drawPath(this);
    }

    public void computePoints() {
        vertices.clear();
        double x0 = x + radiusx;
        double y0 = y;
        double x1, y1;
        for (double alphaC = 0; alphaC < angle; alphaC += step) {
            x1 = x + radiusx * Math.cos(alphaC);
            y1 = y + radiusy * Math.sin(alphaC);
            Point p = new Point(x1, y1);
            JMPathPoint po = new JMPathPoint(p,true,JMPathPoint.TYPE_VERTEX);
            po.isCurved=true;
            vertices.add(po);
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
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //TODO: Needs to recompute center around scaleCenter
//        radiusx*=sx;
//        radiusy*=sy;
//        needsRecalcControlPoints=true;
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
