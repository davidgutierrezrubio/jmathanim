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
public class Arc extends Shape {

    public double radiusx, radiusy, angle;
    public double step;
    public Point center;

    public Arc(Point cen, double radius, double angle) {
        this(cen, radius, angle, Math.PI*2/40,false);
    }

    public Arc(Point cen, double radius, double angle, double step, boolean isClosed) {
        super();
        this.step=step;
        this.center=cen;
        this.radiusx = radius;
        this.radiusy = radius;
        this.angle = angle;
        needsRecalcControlPoints = true;
        numInterpolationPoints = 1;//For now, don't interpolate
        computePoints();
        computeJMPathFromVertices(isClosed);
    }

    @Override
    public Point getCenter() {
        return center;
    }

//    @Override
//    public void draw(Renderer r) {
//        r.setBorderColor(mp.drawColor);
//        double rad = mp.getThickness(r);
////        if (needsRecalcControlPoints) {
////            computeJMPathFromVertices();
////        }
//
////        JMPath c = jmpath.getSlice(drawParam);
////        if (drawParam < 1) {
////            c.open();
////        } else {
////            c.close();
////        }
//        r.setBorderColor(mp.drawColor);
//        r.setStroke(this);
//        r.drawPath(this);
//    }

    public void computePoints() {
        vertices.clear();
        double x1, y1;
        for (double alphaC = 0; alphaC < angle; alphaC += step) {
            x1 = center.v.x + radiusx * Math.cos(alphaC);
            y1 = center.v.x + radiusy * Math.sin(alphaC);
            Point p = new Point(x1, y1);
            JMPathPoint po = new JMPathPoint(p,true,JMPathPoint.TYPE_VERTEX);
            po.isCurved=true;
            vertices.add(po);
        }

    }

    @Override
    public void moveTo(Vec coords) {
        super.moveTo(center);//TODO: This doens't work
        center.moveTo(center);
    }

    @Override
    public void shift(Vec shiftVector) {
        super.shift(shiftVector);
        center.shift(shiftVector);
    }

    

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        super.scale(scaleCenter, sx, sy, sz);
        center.scale(scaleCenter, sx, sy, sz);
        radiusx*=sx;
        radiusy*=sy;
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
