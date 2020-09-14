/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.mathobjects.Stateable;
import static java.lang.Math.sqrt;

/**
 * A vector in 3D
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Vec implements Stateable{

    
    public double x, y, z;
    public double xState, yState, zState;

    public Vec(double x, double y) {
        this(x, y, 0);
    }

    public Vec(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        
    }

    public double dot(Vec a) {
        return x * a.x + y * a.y + z * a.z;
    }

    public Vec cross(Vec a)
    {
        return new Vec(this.y*a.z-this.z*a.y, this.z*a.x-this.x*a.z, this.x*a.y-this.y*a.x);
    }
    public Vec multInSite(double lambda) {
        x *= lambda;
        y *= lambda;
        z *= lambda;
        return this;
    }
    /**
     * Returns a new vector representing this vector scaled by a factor. The current vector is unaltered.
     * @param lambda The factor
     * @return The new vector
     */
    public Vec mult(double lambda) {
        return new Vec(x * lambda, y * lambda, z * lambda);
    }

    public Vec addInSite(Vec b) {
        x += b.x;
        y += b.y;
        z += b.z;
        return this;
    }

    public Vec minusInSite(Vec b) {
        x -= b.x;
        y -= b.y;
        z -= b.z;
        return this;
    }

    public Vec minus(Vec b) {
        return new Vec(x - b.x, y - b.y, z - b.z);
    }

    public Vec add(Vec b) {
        return new Vec(x + b.x, y + b.y, z + b.z);
    }

    public double norm() {
        return (double) sqrt(x * x + y * y + z * z);
    }

    public double distanceTo(Vec point) {
        Vec c = this.minus(point);
        return c.norm();
    }

    /**
     * Returns a new point between this and v2, given by the parameter
     *
     * @param v2 The other point to interpolate
     * @param alpha Parameter of interpolation. 0 gives this point. 1 gives v2.
     * 0.5 returns the middle point
     * @return The interpolated point
     */
    public Vec interpolate(Vec v2, double alpha) {
        return new Vec(x + alpha * (v2.x - x), y + alpha * (v2.y - y));

    }

    public Vec copy() {
        Vec resul=new Vec(x, y);
        return resul;
    }

    @Override
    public void saveState() {
        xState=x;
        yState=y;
        zState=z;
    }

    @Override
    public void restoreState() {
        x=xState;
        y=yState;
        z=zState;
    
    }

    public double getAngle() {
        double angle=Math.atan2(this.y, this.x);
        return angle;
    }
}
