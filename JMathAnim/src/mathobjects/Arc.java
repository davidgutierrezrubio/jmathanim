/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathobjects;

import Renderers.Renderer;
import Utils.Vec;

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
        //Compute an optimal alpha, depending on the screen?
        for (double alpha=0;alpha<angle;alpha+=0.1)
        {
            x1=x+radius*Math.cos(alpha);
            y1=y+radius*Math.sin(alpha);
            r.drawLine(x0, y0, x1, y1);
            x0=x1;
            y0=y1;
        }
    }

}
