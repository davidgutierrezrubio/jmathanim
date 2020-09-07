/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

/**
 * This class represents middle point computed from 2 given ones. This class
 * implements the interface updateable, which automatically updates its
 * components.
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class MiddlePoint extends Point implements Updateable {

    private Point p1, p2;
    private double lambda;

    public MiddlePoint(Point p1, Point p2) {
        this(p1, p2, .5);
    }

    public MiddlePoint(Point p1, Point p2, double lambda) {
        super(p1.interpolate(p2, lambda));
        this.p1 = p1;
        this.p2 = p2;
        p1.addUpdateable(this);
        p2.addUpdateable(this);
        this.lambda = lambda;
        updateFromParents();
    }

    @Override
    public void updateFromParents() {
        this.v=p1.v.interpolate(p2.v, lambda);
    }

}
