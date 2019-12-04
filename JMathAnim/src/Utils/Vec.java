/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import static java.lang.Math.sqrt;


/**
 * A vector in 3D
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Vec {
    public float x,y,z;
    
    public Vec(float x, float y) {
        this(x,y,0);
    }
    public Vec(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public float dot(Vec a){
        return x*a.x+y*a.y+z*a.z;
    }
    
    public Vec mult(float r){
        x*=r;
        y*=r;
        z*=r;
        return this;
    }
    public Vec mult(double r){
        x*=(float)r;
        y*=(float)r;
        z*=(float)r;
        return this;
    }
    public Vec add(Vec b){
        x+=b.x;
        y+=b.y;
        z+=b.z;
        return this;
    }
    public Vec minus(Vec b){
        x-=b.x;
        y-=b.y;
        z-=b.z;
        return this;
    }
    
    public float norm() {
        return (float) sqrt(x*x+y*y+z*z);
    }
    public int xi(){
        return (int)(x);
    }
    public int yi(){
        return (int)(y);
    }
}
