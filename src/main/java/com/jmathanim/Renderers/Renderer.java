/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import java.awt.Color;

/**
 * Coordinates x,y,z are always given in (0,0) to (w,h), where (0,0) is upper
 * left corner.
 *
 * @author David
 */
public abstract class Renderer {

    protected JMColor borderColor;
    protected JMColor fillColor;
    protected int width;
    protected int height;

    public Renderer() {
        borderColor = JMColor.WHITE;//Default color

    }

    public abstract void setCamera(Camera c);

    public abstract Camera getCamera();

    public abstract Camera getFixedCamera();

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

    public JMColor getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(JMColor color) {
        this.borderColor = color;
    }

    public JMColor getFillColor() {
        return fillColor;
    }

    public void setFillColor(JMColor fillColor) {
        this.fillColor = fillColor;
    }

    abstract public void saveFrame(int frameCount);

    abstract public void finish(int frameCount);

    /**
     * Clear current renderer, with the background color
     */
    abstract public void clear();

    /**
     * Defines an appropiate stroke, from the Properties of the object to draw
     *
     * @param obj MathObject to be drawed
     */
    abstract public void setStroke(MathObject obj);

    /**
     * Draws the path of a JMPathObject This method will draw most of the
     * objects in the screen
     *
     * @param mobj The JMPathObject
     */
    abstract public void drawPath(Shape mobj);

    /**
     * Draw a circle Most of the drawings will be paths, but this method will
     * basically draw points
     *
     * @param x x-coordinate (math scale)
     * @param y y-coordinate (math scale)
     * @param radius radius (math scale)
     */
    abstract public void drawCircle(double x, double y, double radius);

    public void drawDot(MathObject dot) {
        drawDot(dot.getCenter());
    }

    abstract public void drawDot(Point p);

    abstract public void drawAbsoluteCopy(Shape sh, Vec anchor);

}
