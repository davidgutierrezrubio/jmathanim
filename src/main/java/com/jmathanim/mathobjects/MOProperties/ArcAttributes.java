/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.MOProperties;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ArcAttributes extends MathObjectAttributes {

    public Point center;
    public double radius, angle;
    public Shape arc;
    private double radiusState, angleState;

    public ArcAttributes(Point center, double radius, double angle, Shape arc) {
        super(arc);
        this.center = center;
        this.radius = radius;
        this.angle = angle;
        this.arc = arc;
    }

    @Override
    public void applyTransform(AffineJTransform tr) {
        tr.applyTransform(center);
        double sum = 0;
        for (Point p : arc.getPath().getPoints()) {
            sum += center.to(p).norm();
        }
        radius = sum / arc.getPath().size();
    }

    @Override
    public void saveState() {
        center.saveState();
        radiusState = radius;
        angleState = angle;

    }

    @Override
    public void restoreState() {
        center.restoreState();
        radius = radiusState;
        angle = angleState;
    }

    @Override
    public MathObjectAttributes copy() {
        return new ArcAttributes(center.copy(), radius, angle, null);
    }

    @Override
    public void setParent(MathObject parent) {
        arc = (Shape) parent;
    }
}
