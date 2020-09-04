/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Vec;
import static java.lang.Math.PI;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Circle extends Arc {

    public Circle(Vec center, Vec point) {
        this(new Point(center), center.distanceTo(point));
    }

    public Circle(Point arcCenter, double radius) {
        super(arcCenter, radius, 2 * PI, true);
    }

    @Override
    public Circle copy() {
        Circle resul=new Circle(this.center.copy(), radiusx);
        resul.mp.copyFrom(mp);
        resul.jmpath.clear();
        resul.jmpath.addPointsFrom(jmpath.rawCopy());
        return resul;
    }

}
