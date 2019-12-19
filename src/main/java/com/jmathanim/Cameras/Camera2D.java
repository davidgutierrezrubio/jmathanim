/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Cameras;

/**
 * This class converts math coordinates to screen cordinates screen coordinates
 * are always (0,0)-(w,h) where (0,0) is upper left corner
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Camera2D extends Camera {

    public double xmin, xmax, ymin, ymax; //Size of math world seen by the camera

    public Camera2D() {
        this(0, 0);//To initializa after
    }

    public Camera2D(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        reset();

    }

    public int[] mathToScreen(double mathX, double mathY) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
        int x, y;
        x = (int) ((mathX - xmin) * screenWidth / (xmax - xmin));
        y = (int) ((ymax - mathY) * screenHeight / (ymax - ymin));
        return new int[]{x, y};
    }

    public int mathToScreen(double mathScalar) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
        int resul;
        resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return resul;
    }

    @Override
    public final void setCenter(double x, double y, double z) {
        setCenter(x,y);//Ignore the z, we are in 2D!
    }

    @Override
    public final void setCenter(double x, double y) {
        double mWidth = xmax - xmin;
        setMathXY(x - .5 * mWidth, x + .5 * mWidth, y);

    }

    public final void reset() {
        this.setMathXY(-2, 2, 0);
    }

    public final void setMathXY(double xmin, double xmax, double ycenter) {
        this.xmin = xmin;//Centered at (0,0)
        this.xmax = xmax;
        //Compute y so that proportion is the same as the screen
        double ratioScreen = ((double) screenWidth) / ((double) screenHeight);
        //(xmax-xmin)/(ymax-ymin)=ratioScreen, so...
        this.ymax = ycenter + .5 * (xmax - xmin) / ratioScreen;
        this.ymin = ycenter - .5 * (xmax - xmin) / ratioScreen;
    }

}
