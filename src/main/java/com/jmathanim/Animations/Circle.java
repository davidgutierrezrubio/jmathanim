/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Arc;
import static java.lang.Math.PI;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Circle extends Arc{
    
    public Circle(double x, double y, double radius) {
        super(x, y, radius, 2*PI);
        closePath=true;
    }
    public Circle (Vec center, Vec point)
    {
        this(center.x,center.y,center.distanceTo(point));
    }
    
}
