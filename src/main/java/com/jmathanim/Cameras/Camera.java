/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Cameras;

import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 * This class converts math coordinates to screen cordinates. Screen coordinates
 * are always (0,0)-(w,h) where (0,0) is upper left corner
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Camera {

    public boolean perspective;

    /**
     * Screen width size to be displayed 800x600, 1920x1280, etc.
     */
    public int screenWidth;

    /**
     * Screen height size to be displayed 800x600, 1920x1280, etc.
     */
    public int screenHeight;

    /**
     * Boundaries of the view in the math world
     */
    protected double xmin, xmax, ymin, ymax;

    /**
     * Values to reset the camera
     */
    protected double[] resetValues;

    /**
     * Gaps to add when adjusting view to an object or Rect
     */
    protected double hgap = .1, vgap = .1;

    private final JMathAnimScene scene;
    private double xminB, xmaxB, yminB, ymaxB;// Backup values for saveState()

    public Camera(JMathAnimScene scene, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scene = scene;

    }

    /**
     * Sets the camera to its default values
     */
    public void reset() {
        setMathXY(resetValues[0], resetValues[1], resetValues[2]);
    }

    /**
     * Center camera in math-coordinates x,y
     *
     * @param x
     * @param y
     */
    public final void setCenter(double x, double y) {
        double mWidth = xmax - xmin;
        setMathXY(x - .5 * mWidth, x + .5 * mWidth, y);

    }

    public void setCenter(MathObject obj) {
        double xx = obj.getCenter().v.x;
        double yy = obj.getCenter().v.y;
        setCenter(xx, yy);
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
    public Camera setMathXY(double xmin, double xmax, double ycenter) {

        if (xmax <= xmin) {
            return this;
        }
        this.xmin = xmin;
        this.xmax = xmax;
        // Compute y so that proportion is the same as the screen
        double ratioScreen = ((double) screenWidth) / ((double) screenHeight);
        // (xmax-xmin)/(ymax-ymin)=ratioScreen, so...
        this.ymax = ycenter + .5 * (xmax - xmin) / ratioScreen;
        this.ymin = ycenter - .5 * (xmax - xmin) / ratioScreen;
        return this;
    }

    public double mathToScreen(double mathScalar) {
        // xmin,ymin->(0,0)
        // xmax, ymax->(screenWidth,screenHeight)
//        resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return mathScalar * screenHeight / (ymax - ymin);
    }

    public double[] mathToScreen(double mathX, double mathY) {
        // xmin,ymin->(0,0)
        // xmax, ymax->(screenWidth,screenHeight)
        double x, y;
        x = (mathX - xmin) * screenWidth / (xmax - xmin);
        y = (ymax - mathY) * screenHeight / (ymax - ymin);
        return new double[]{x, y};
    }

    public double[] screenToMath(double x, double y) {
        double mx = (x * (xmax - xmin) / screenWidth + xmin);
        double my = -(y * (ymax - ymin) / screenHeight - ymax);
        return new double[]{mx, my};
    }

    public double screenToMath(double screenScalar) {
        // resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return screenScalar * (xmax - xmin) / screenWidth;
    }

    public double[] mathToScreenFX(Vec p) {
        return mathToScreen(p.x, p.y);
    }

    public void saveState() {
        xminB = xmin;
        xmaxB = xmax;
        yminB = ymin;
        ymaxB = ymax;
    }

    public void restoreState() {
        xmin = xminB;
        xmax = xmaxB;
        ymin = yminB;
        ymax = ymaxB;
    }

    public void setWidth(double d) {
        scale(d / getMathView().getWidth());
    }

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

    }

    public void setGaps(double h, double v) {
        hgap = h;
        vgap = v;
    }

    public Camera setMathView(Rect r) {
        setMathXY(r.xmin, r.xmax, .5 * (r.ymin + r.ymax));
        return this;
    }

    public void shift(Vec v) {
        shift(v.x, v.y);
    }

    public void shift(double x, double y) {
        setMathXY(xmin + x, xmax + x, .5 * (ymin + ymax) + y);
    }

    /**
     * Adjust the view so that if contains the area given by a Rect object.The
     * view is the minimal bounding box that contains r, with the proportions of
     * the screen.
     *
     * @param rAdjust Rectangle to adjust
     * @return This camera
     */
    public Camera adjustToRect(Rect rAdjust) {
        Rect r = getRectThatContains(rAdjust);
        setMathXY(r.xmin, r.xmax, .5 * (r.ymax + r.ymin));
        return this;
    }

    /**
     * Adjust the camera view so that all objects in the scene are visible,
     * zooming out as necessary. The difference with the zoom methods is that a
     * zoom in is not applied in this case. The setGaps method can be called
     * before to set the gaps between the view and the objects.
     *
     * @return This object
     */
    public Camera adjustToAllObjects() {
        if (!scene.getObjects().isEmpty()) {
            MathObject[] objs = scene.getObjects().toArray(new MathObject[scene.getObjects().size()]);
            adjustToObjects(objs);
        }
        return this;
    }

    /**
     * Adjust the camera view so that the specified objects are visible (objects
     * don't need to be added to the scene), zooming out as necessary. The
     * difference with the zoom methods is that a zoom in is not applied in this
     * case. The setGaps method can be called before to set the gaps between the
     * view and the objects.
     *
     * @param objs Objects to zoom in
     * @return This object
     */
    public Camera adjustToObjects(Boxable... objs) {
        Rect r = getMathView();
        for (Boxable obj : objs) {
            r = Rect.union(r, obj.getBoundingBox());
        }
        adjustToRect(r.addGap(hgap, hgap));
        return this;
    }

    /**
     * Center the camera around the given set of Boxable objects (MathObject or
     * Rect) and adjusts the zoom so that objects are visible.
     *
     * @param objs Boxable objects to compute center, varargs.
     * @return This camera.
     */
    public Camera centerAtObjects(Boxable... objs) {
        Rect r = objs[0].getBoundingBox();
        for (Boxable obj : objs) {
            r = Rect.union(r, obj.getBoundingBox());
        }
        if (r != null) {
            shift(getMathView().getCenter().to(r.getCenter()));
            adjustToObjects(objs);
        }
        return this;
    }

    /**
     * Center the camera around all current objects added to scene (including
     * invisible ones) and adjusts the zoom so that objects are visible.
     *
     * @return This camera.
     */
    public Camera centerAtAllObjects() {
        if (!scene.getObjects().isEmpty()) {
            MathObject[] objs = scene.getObjects().toArray(new MathObject[scene.getObjects().size()]);
            centerAtObjects(objs);
        }
        adjustToAllObjects();
        return this;
    }

    /**
     * Zoom the camera so that the specified objects are visible (objects don't
     * need to be added to the scene). The setGaps method can be called before
     * to set the gaps between the view and the objects.
     *
     * @return This object
     */
    public Camera zoomToObjects(MathObject... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = Rect.union(r, obj.getBoundingBox());
        }
        adjustToRect(r.addGap(hgap, hgap));
        return this;
    }

    /**
     * Zoom the camera so that all objects in the scene are visible. The setGaps
     * method can be called before to set the gaps between the view and the
     * objects.
     *
     * @return This object
     */
    public Camera zoomToAllObjects() {
        if (!scene.getObjects().isEmpty()) {
            MathObject[] objs = scene.getObjects().toArray(new MathObject[scene.getObjects().size()]);
            zoomToObjects(objs);
        }
        return this;
    }

    /**
     * Scales the visible area with the given factor.
     *
     * @param scale Scale factor. A value of 1 does nothing. A value of 0.5
     * applies a 2x zoom.
     * @return This object
     */
    public Camera scale(double scale) {
        setMathView(getMathView().scale(scale, scale));
        return this;
    }

    /**
     * Returns the smallest rectangle which contains a given one, with the
     * proportions of the screen view
     *
     * @param r A Rect object, rectangle to contain
     * @return The rectangle which contains r, with the screen proportions
     */
    public Rect getRectThatContains(Rect r) {
        Rect resul = new Rect(0, 0, 0, 0);
        double ratio = ((double) screenWidth) / screenHeight; // Ratio W/H

        double ratioR = (r.xmax - r.xmin) / (r.ymax - r.ymin);

        if (ratio <= ratioR) // If R is wider than the screen...
        {
//            
            double camHeight = (r.ymax - r.ymin) / ratio;
            double minY = .5 * ((r.ymin + r.ymax) - camHeight);
            double maxY = .5 * ((r.ymin + r.ymax) + camHeight);
            resul.xmin = r.xmin;
            resul.xmax = r.xmax;
            resul.ymin = minY;
            resul.ymax = maxY;

        } else // If the screen is wider than R...
        {
            double camWidth = (r.ymax - r.ymin) * ratio;
            double minX = .5 * ((r.xmin + r.xmax) - camWidth);
            double maxX = .5 * ((r.xmin + r.xmax) + camWidth);
            resul.xmin = minX;
            resul.xmax = maxX;
            resul.ymin = r.ymin;
            resul.ymax = r.ymax;
        }
        return resul;
    }

    /**
     * Return the visible math space portion.
     *
     * @return a Rect object that represents the visible math space portion.
     */
    public Rect getMathView() {
        return new Rect(xmin, ymin, xmax, ymax);
    }

    public Vec getGaps() {
        return new Vec(hgap, vgap);
    }

    public void initialize(double xmin, double xmax, double ycenter) {
        setMathXY(xmin, xmax, ycenter);
        resetValues = new double[]{xmin, xmax, ycenter};
    }

    public void setViewFrom(hasCameraParameters param) {
        setMathXY(param.getMinX(), param.getMaxX(), param.getYCenter());
    }
    
}
