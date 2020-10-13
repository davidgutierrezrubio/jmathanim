/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 * Coordinates x,y,z are always given in (0,0) to (w,h), where (0,0) is upper
 * left corner.
 *
 * @author David
 */
public abstract class Renderer {

    protected final JMathAnimConfig cnf;
    protected final JMathAnimScene scene;
    public Renderer(JMathAnimScene parentScene) {
        this.scene=parentScene;
        this.cnf=JMathAnimConfig.getConfig();
    }

    public abstract void setCamera(Camera c);

    abstract public <T extends Camera> T getCamera();

    public abstract Camera getFixedCamera();


    public abstract void setCameraSize(int w, int h);

    public int getWidth() {
        return cnf.mediaW;
    }

    public int getHeight() {
       return cnf.mediaH;
    }

    abstract public void saveFrame(int frameCount);

    abstract public void finish(int frameCount);

    /**
     * Clear current renderer, with the background color
     */
    abstract public void clear();


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
