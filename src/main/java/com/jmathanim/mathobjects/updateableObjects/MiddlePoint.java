/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MiddlePoint extends Point {

    private Point p1, p2;
    private double lambda;

    public MiddlePoint(Point p1, Point p2) {
        this(p1, p2, .5);
    }

    public MiddlePoint(Point p1, Point p2, double lambda) {
        super();
        this.p1 = p1;
        this.p2 = p2;
        this.lambda = lambda;
    }

    @Override
    public void update() {
        this.v = p1.v.interpolate(p2.v, lambda);
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(p1.getUpdateLevel(),p2.getUpdateLevel())+1;
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        scene.registerObjectToBeUpdated(p1);
        scene.registerObjectToBeUpdated(p2);
    }

}
