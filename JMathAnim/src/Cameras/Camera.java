/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cameras;

import java.util.HashSet;

/**
 * This class converts math coordinates to screen cordinates screen coordinates
 * are always (0,0)-(w,h) where (0,0) is upper left corner
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Camera {

    public int screenWidth;//Screen size 800x600, 1920x1280, etc.
    public int screenHeight;
    public double xmin, xmax, ymin, ymax; //Size of math world seen by the camera

    public Camera() {
        this(0, 0);//To initializa after
    }

    public Camera(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setMathXY();

    }

    public int[] mathToScreen(double mathX, double mathY) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
        int x, y;
        x = (int) ((mathX - xmin) * screenWidth / (xmax - xmin));
        y = (int) ((ymax - mathY) * screenHeight / (ymax - ymin));
        return new int[]{x, y};
    }

    public void setSize(int w, int h) {
        screenWidth = w;
        screenHeight = h;
        setMathXY();
    }

    public int mathToScreen(double mathScalar) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
        int resul;
        resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return resul;
    }

    public final void setMathXY() {
        this.xmin = -2;//Centered at (0,0)
        this.xmax = 2;
        //Compute y so that proportion is the same as the screen
        double ratioScreen = ((double) screenWidth) / ((double) screenHeight);
        //(xmax-xmin)/(ymax-ymin)=ratioScreen, so...
        this.ymax = .5 * (xmax - xmin) / ratioScreen;
        this.ymin = -.5 * (xmax - xmin) / ratioScreen;
    }

}
