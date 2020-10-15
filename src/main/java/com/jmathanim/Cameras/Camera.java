/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 * This class manages conversion between math coordinates (usually
 * (-2,-2)..(2,2) to screen coordinates (depending on renderer, usually (0,0) to
 * (1920,1080).
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Camera {

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
     * Gaps to add when adjusting view to an object or Rect
     */
    protected double hgap=.1, vgap=.1;

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
    
    public void setMathView(Rect r) {
        setMathXY(r.xmin, r.xmax, .5 * (r.ymin + r.ymax));
    }

    /**
     * Adjust the view so that if contains the area given by a Rect object. The
     * view is the minimal bounding box that contains r, with the proportions of
     * the screen.
     *
     * @param rAdjust Rectangle to adjust
     */
    public void adjustToRect(Rect rAdjust) {
        Rect r = getRectThatContains(rAdjust);
        setMathXY(r.xmin, r.xmax, .5 * (r.ymax + r.ymin));
    }

    public void adjustToAllObjects() {
        final JMathAnimScene scene = JMathAnimConfig.getConfig().getScene();
        MathObject[] objs = scene.getObjects().toArray(new MathObject[scene.getObjects().size()]);
        adjustToObjects(objs);
    }

    public void adjustToObjects(MathObject... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = r.union(obj.getBoundingBox());
        }
        adjustToRect(r.addGap(hgap, hgap));
    }

    public void zoomToObjects(MathObject... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = r.union(obj.getBoundingBox());
        }
        adjustToRect(r.addGap(hgap, hgap));
    }

    public void scale(double scale) {
        setMathView(getMathView().scaled(scale, scale));
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
        double ratio = ((double) screenWidth) / screenHeight; //Ratio W/H

        double ratioR = (r.xmax - r.xmin) / (r.ymax - r.ymin);
        
        if (ratio <= ratioR) //If R is wider than the screen...
        {
//            
            double camHeight = (r.ymax - r.ymin) / ratio;
            double minY = .5 * ((r.ymin + r.ymax) - camHeight);
            double maxY = .5 * ((r.ymin + r.ymax) + camHeight);
            resul.xmin = r.xmin;
            resul.xmax = r.xmax;
            resul.ymin = minY;
            resul.ymax = maxY;
            
        } else //If the screen is wider than R...
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
     * Return an array with the corners of the math world to display
     *
     * @return An array with the values {xmin,ymin,xmax,ymax}
     */
    public Rect getMathView() {
        return new Rect(xmin, ymin, xmax, ymax);
    }

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
    
    abstract public void saveState();
    
    abstract public void restoreState();
    
    public Vec getGaps()
    {
        return new Vec(hgap,vgap);
    }
}
