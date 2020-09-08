/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class TransformedPoint extends Point{

    private AffineTransform transform;
    private final Point dstPoint;
    
    public TransformedPoint(Point p,AffineTransform tr) {
        super();
        this.dstPoint=p;
        this.transform=tr;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    @Override
    public void update() {
        Point tempPoint = transform.getTransformedPoint(this.dstPoint);
        this.v.x=tempPoint.v.x;
        this.v.y=tempPoint.v.y;
        this.v.z=tempPoint.v.z;
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        scene.registerObjectToBeUpdated(this.dstPoint);
    }
    
    
}
