/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import static java.lang.Math.sqrt;

/**
 * A vector in 3D
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Vec {

    public double x, y, z;

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

    public Vec multInSite(double r) {
        x *= r;
        y *= r;
        z *= r;
        return this;
    }
    public Vec mult(double r){
        return new Vec(x*r,y*r,z*r);
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
}
