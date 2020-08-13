/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.Cameras.Camera;
import java.awt.Color;

/**
 * Coordinates x,y,z are always given in (0,0) to (w,h), where (0,0) is upper
 * left corner.
 *
 * @author David
 */
public abstract class Renderer {

    protected Color color;
    protected int width;
    protected int height;

    public Renderer() {
        color = Color.WHITE;//Default color

    }

    public abstract void setCamera(Camera c);

    public abstract Camera getCamera();

    public void setSize(int w, int h) {
        width = w;
        height = h;
        setCameraSize(width, height);
    }

    public abstract void setCameraSize(int w, int h);

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        setCameraSize(width, height);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        setCameraSize(width, height);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Draws an arc centered at x,y with given radius and angle
     *
     * @param x x in math coordinates
     * @param y y in math coordinates
     * @param radius radius in math coordinates
     * @param angle angle in math coordinates
     */
    abstract public void drawArc(double x, double y, double radius, double angle);

    /**
     * Draws a straight line from x1,y1 to x2,y2
     * @param x1 x coordinate of first point in math coordinates
     * @param y1 y coordinate of first point in math coordinates
     * @param x2 x coordinate of second point in math coordinates
     * @param y2 y coordinate of second point in math coordinates
     */
    abstract public void drawLine(double x1, double y1, double x2, double y2);


    /**
     * Draws a circle centered at x,y with given radius
     *
     * @param x x in math coordinates
     * @param y y in math coordinates
     * @param radius radius in math coordinates
     */
    abstract public void drawCircle(double x, double y, double radius);

    abstract public void saveFrame(int frameCount);

    abstract public void finish();

    /**
     * Clear current renderer, with the background color
     */
    abstract public void clear();

    abstract public void setStroke(double st);

//    abstract public void createPath(double x, double y);
//
//    abstract public void addPointToPath(double x, double y);
//
//    abstract public void closePath();

    abstract public void drawPath(JMPath c);

    abstract public void setAlpha(double alpha);
    

}
