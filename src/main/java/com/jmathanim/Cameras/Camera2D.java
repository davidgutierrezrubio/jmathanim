/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Cameras;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;

/**
 * A Camera subclass designed to work with the Java2D library This class
 * converts math coordinates to screen cordinates. Screen coordinates are always
 * (0,0)-(w,h) where (0,0) is upper left corner
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Camera2D extends Camera {

    private double xminB, xmaxB, yminB, ymaxB;//Backup values for saveState()

    public Camera2D() {
        this(0, 0);//To initialize after
    }

    public Camera2D(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

    }

    /**
     * Center camera in math-coordinates x,y, ignores z
     *
     * @param x
     * @param y
     */
    @Override
    public final void setCenter(double x, double y, double z) {
        setCenter(x, y);//Ignore the z, we are in 2D!
    }

    /**
     * Center camera in math-coordinates x,y
     *
     * @param x
     * @param y
     */
    @Override
    public final void setCenter(double x, double y) {
        double mWidth = xmax - xmin;
        setMathXY(x - .5 * mWidth, x + .5 * mWidth, y);

    }

    public void setCenter(MathObject obj) {
        double xx = obj.getCenter().v.x;
        double yy = obj.getCenter().v.y;
        setCenter(xx, yy);
    }

    @Override
    public void setMathXY(double xmin, double xmax, double ycenter) {

        if (xmax <= xmin) {
            return;
        }
        this.xmin = xmin;
        this.xmax = xmax;
        //Compute y so that proportion is the same as the screen
        double ratioScreen = ((double) screenWidth) / ((double) screenHeight);
        //(xmax-xmin)/(ymax-ymin)=ratioScreen, so...
        this.ymax = ycenter + .5 * (xmax - xmin) / ratioScreen;
        this.ymin = ycenter - .5 * (xmax - xmin) / ratioScreen;
    }

    @Override
    public int mathToScreen(double mathScalar) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
//        resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return (int) Math.round(mathScalar * screenWidth / (xmax - xmin));
    }

    @Override
    public int[] mathToScreen(double mathX, double mathY) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
        int x;
        int y;
        x = (int) (Math.round((mathX - xmin) * screenWidth / (xmax - xmin)));
        y = (int) (Math.round((ymax - mathY) * screenHeight / (ymax - ymin)));
        return new int[]{x, y};
    }

    public double[] screenToMath(int x, int y) {
        double mx = (double) (x * (xmax - xmin) / screenWidth + xmin);
        double my = -(double) (y * (ymax - ymin) / screenHeight - ymax);
        return new double[]{mx, my};
    }

    @Override
    public double relScalarToWidth(double scalar) {
        return screenToMath(scalar * screenWidth);
    }

    @Override
    public double screenToMath(double screenScalar) {
        //resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return screenScalar * (xmax - xmin) / screenWidth;
    }

    public int[] mathToScreen(Vec p) {
        return mathToScreen(p.x, p.y);
    }

    @Override
    public void saveState() {
        xminB = xmin;
        xmaxB = xmax;
        yminB = ymin;
        ymaxB = ymax;
    }

    @Override
    public void restoreState() {
        xmin = xminB;
        xmax = xmaxB;
        ymin = yminB;
        ymax = ymaxB;
    }

}
