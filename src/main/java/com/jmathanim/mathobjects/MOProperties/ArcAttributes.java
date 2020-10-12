/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.MOProperties;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ArcAttributes extends MathObjectAttributes{
    
    public Point center;
    public double radius,radiusState;
    public Shape arc;

    public ArcAttributes(Point center, double radius,Shape arc) {
        this.center = center;
        this.radius = radius;
        this.arc=arc;
    }

    
    @Override
    public void applyTransform(AffineJTransform tr) {
        tr.applyTransform(center);
        this.radius=center.to(arc.getPoint(0)).norm();
    }

    @Override
    public void saveState() {
        center.saveState();
        radiusState=radius;
        
    }

    @Override
    public void restoreState() {
        center.restoreState();
        radius=radiusState;
    }
}
