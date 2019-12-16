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
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Arc extends MathObject {

    public double x, y, z;
    public double radius, angle;

    public Arc(double x, double y, double radius, double angle) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.angle = angle;
        setDrawParam(1);//Draw parameter to 1, draw the full arc
    }

    
    @Override
    public Vec getCenter() {
        return new Vec(x, y);
    }

    @Override
    public void draw(Renderer r) {
        double x0=x+radius;
        double y0=y;
        double x1,y1;
        r.setStroke(.01);//TODO: COnfig stroke size
        r.createPath(x0,y0);
        //Compute an optimal alpha, depending on the screen?
        for (double alpha=0;alpha<angle*drawParam;alpha+=0.01)
        {
            x1=x+radius*Math.cos(alpha);
            y1=y+radius*Math.sin(alpha);
            
            r.addPointToPath(x1, y1);
//            r.drawLine(x0, y0, x1, y1); //TODO: Mejorar, crear poly en 2D
//            x0=x1;
//            y0=y1;
        }
//        r.closePath();
        r.drawPath();
    }

    @Override
    public void moveTo(Vec coords) {
        x=coords.x;
        y=coords.y;
    }

    @Override
    public void shift(Vec shiftVector) {
        x+=shiftVector.x;
        y+=shiftVector.y;
    }

}
