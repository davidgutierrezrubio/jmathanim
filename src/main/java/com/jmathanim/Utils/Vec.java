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
public class Vec implements Stateable {

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

    public Vec cross(Vec a) {
        return new Vec(this.y * a.z - this.z * a.y, this.z * a.x - this.x * a.z, this.x * a.y - this.y * a.x);
    }

    public Vec multInSite(double lambda) {
        x *= lambda;
        y *= lambda;
        z *= lambda;
        return this;
    }

    /**
     * Returns a new vector representing this vector scaled by a factor. The
     * current vector is unaltered.
     *
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
        return new Vec((1-alpha)*x + alpha * v2.x, (1-alpha)*y + alpha * v2.y);

    }

    public Vec copy() {
        Vec resul = new Vec(x, y);
        return resul;
    }

    public void copyFrom(Vec v) {
        if (v != null) {
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
        }
    }

    @Override
    public void saveState() {
        xState = x;
        yState = y;
        zState = z;
    }

    @Override
    public void restoreState() {
        x = xState;
        y = yState;
        z = zState;

    }

    public double getAngle() {
        double angle = Math.atan2(this.y, this.x);
        return angle;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vec other = (Vec) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Vec(" + x + ", " + y + ')';
    }

}
