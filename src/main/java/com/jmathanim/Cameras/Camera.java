/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Cameras;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class Camera {

    public int screenWidth;//Screen size 800x600, 1920x1280, etc.
    public int screenHeight;

    public void setSize(int w, int h) {
        screenWidth = w;
        screenHeight = h;
        reset();

    }
   
    /**
     * Do the necessary recalculations  (xmin, xmax, etc) when needed to.
     * It depends on the type of camera, so this method is abstract to be 
     * implemented in the subclass
     */
    public abstract void reset();
    public abstract void setCenter(double x,double y);
    public abstract void setCenter(double x,double y,double z);

}
