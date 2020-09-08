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

    /**
     * Default constructor, a circle with center (0,0) and radius 1
     */
    public Circle(){
        this(new Point(0,0),1);
    }
    public Circle(Vec center, Vec point) {
        this(new Point(center), center.distanceTo(point));
    }

    public Circle(Point arcCenter, double radius) {
        super(arcCenter, radius, 2 * PI,Math.PI*2/40, true);
    }

    @Override
    public Circle copy() {
        Circle resul=new Circle(new Point(x,y), radiusx);
        resul.mp.copyFrom(mp);
        resul.jmpath.clear();
        resul.jmpath.addPointsFrom(jmpath.rawCopy());
        return resul;
    }

}
