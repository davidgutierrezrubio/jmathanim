/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Cameras;

import com.jmathanim.Utils.Rect;

/**
 * This class manages conversion between math coordinates (usually
 * (-2,-2)..(2,2) to screen coordinates (depending on renderer, usually (0,0) to
 * (1920,1080).
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Camera {

    /**
     *Screen width size to be displayed 800x600, 1920x1280, etc.
     */
    public int screenWidth;
    

    /**
     *Screen height size to be displayed 800x600, 1920x1280, etc.
     */
    public int screenHeight;

    /**
     * Boundaries of the view in the math world
     */
    protected double xmin,xmax,ymin,ymax;
    /**
     * Set size of the screen to which the camera will compute coordinates
     * Screen size usually is 800x600, 1920x1080, etc.
     *
     * @param w width
     * @param h height
     */
    public void setSize(int w, int h) {
        screenWidth = w;
        screenHeight = h;
        reset();

    }

    /**
     * Set the boundaries of the math region displayed in the screen given by
     * xmin and xmax. ymin and ymax are automatically computed Rectangle
     * (xmin,ymin) to (xmax, ymax) vertically centered at ycenter
     *
     * @param xmin Left x-coordinate
     * @param xmax Right x-coordinate
     * @param ycenter y-center coordinate
     */
    abstract public void setMathXY(double xmin, double xmax, double ycenter);

    /**
     * Return an array with the corners of the math world to display
     * @return An array with the values {xmin,ymin,xmax,ymax}
     */
    public Rect getMathBoundaries()
    {
        return new Rect(xmin,ymin,xmax,ymax);
    }
    /**
     * Do the necessary recalculations (xmin, xmax, etc) when needed to. It
     * depends on the type of camera, so this method is abstract to be
     * implemented in the subclass
     */
    public abstract void reset();

    /**
     * Center camera in math-coordinates x,y
     *
     * @param x
     * @param y
     */
    public abstract void setCenter(double x, double y);

    /**
     * Center camera in math-coordinates x,y,Z
     *
     * @param x
     * @param y
     * @param z
     */
    public abstract void setCenter(double x, double y, double z);

    /**
     * Convert a scalar given in math coordinates to screen coordinates
     *
     * @param mathScalar
     * @return An integer with the scalar
     */
    abstract public int mathToScreen(double mathScalar);

    /**
     * Convert a 2d-coordinates given in math coordinates to screen coordinates
     *
     * @param mathX
     * @param mathY
     * @return and array int[] with the coordinates
     */
    abstract public int[] mathToScreen(double mathX, double mathY);

    /**
     * Converts a scalar in screen coordinates to math coordinates.
     *
     * @param screenScalar
     * @return scalar in math coordinates
     */
    abstract public double screenToMath(double screenScalar);

    /**
     * Returns a relative scalar to screen width, given in math coordinates It
     * is useful, for example, for determining the size of the circles that
     * represent a point or determining the width of a line, according to the
     * media. Thus relScalarToWidth(.01) gives in math coordinates a length 
     * equivalent to 1% of screen width.
     *
     * @param scalar
     * @return The scalar in math coordinates
     */
    abstract public double relScalarToWidth(double scalar);
}
