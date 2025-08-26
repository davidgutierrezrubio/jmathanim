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
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;

import java.text.DecimalFormat;

/**
 * This class represents a point in 2D or 3D space
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Point extends AbstractPoint<Point> {


    /**
     * Creates a new Point with coordinates (0,0,0), with default style.
     */
    public Point() {
        this(0, 0, 0);
    }


    /**
     * Creates a new point linking coordinates from given vector, with default style.
     *
     * @param v Vector with coordinates
     */
    public Point(Vec v) {
        super(v);
    }

    /**
     * Overloaded method. Creates a new Point with coordinates x,y, with default style. The z coordinates is set to 0.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public Point(double x, double y) {
        this(x, y, 0);
    }


    /**
     * Creates a new Point with coordinates x,y,z, with default style.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Point(double x, double y, double z) {
        super(new Vec(x, y, z));
    }

    /**
     * Static method. Returns a new point at (0,0), with default style
     *
     * @return The created Point
     */
    public static Point origin() {
        return Point.at(0, 0);
    }

    public static Point relAt(double x, double y) {
        return Point.at(JMathAnimConfig.getConfig().getCamera().getMathView().getRelCoordinates(x, y));
    }

    /**
     * Static method. Returns a new point at (1,0), with default style
     *
     * @return The created Point
     */
    public static Point unitX() {
        return Point.at(1, 0);
    }

    /**
     * Static method. Returns a new point at (0,1), with default style
     *
     * @return The created Point
     */
    public static Point unitY() {
        return Point.at(0, 1);
    }

    public static Point unitZ() {
        return new Point(0, 0, 1);
    }

    public static Vec segmentIntersection(Coordinates A, Coordinates B, Coordinates C, Coordinates D) {
        AffineJTransform tr = AffineJTransform.createAffineTransformation(A, B, C, Point.unitX(), Point.unitY(),
                Point.origin(), 1);
        Vec P = D.getVec().copy().applyAffineTransform(tr);
        double r = P.x + P.y;
        if ((r >= 1) & (P.x >= 0) & (P.y >= 0)) {
            P.x /= r;
            P.y /= r;
            return P.applyAffineTransform(tr.getInverse());
        } else {
            return null;
        }
    }

    /**
     * Static builder. Creates and returns a new point at given coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return The created point
     */
    public static Point at(double x, double y) {
        return new Point(x, y);
    }

    /**
     * Returns a new Point with coordinates those of given vector. Vector object is referenced
     *
     * @param coords Vector with coordinates
     * @return The new point
     */
    public static Point at(Coordinates<?> coords) {
        Vec v = coords.getVec();
        return new Point(v);
    }

    /**
     * Returns a new Point with coordinates those of given vector. Vector object is copied
     *
     * @param coords Vector with coordinates
     * @return The new point
     */
    public static Point atCopy(Coordinates<?> coords) {
        Vec v = coords.getVec().copy();
        return new Point(v);
    }


    /**
     * Static builder. Creates and returns a new point at given coordinates (3d space).
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return The created point
     */
    public static Point at(double x, double y, double z) {
        return new Point(x, y, z);
    }

    /**
     * Static builder.Creates and returns a new point at random coordinates, inside the math view.
     *
     * @return The created point
     */
    public static Point random() {
        return new Point(Vec.random());
    }

    @Override
    public Point copy() {
        Point copy = Point.origin();
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public String toString() {
        String pattern = "##0.####";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return objectLabel + "|Point(" + decimalFormat.format(getVec().x) + ",  " + decimalFormat.format(getVec().y) + ",  " + decimalFormat.format(getVec().z) + ")";

    }

    /**
     * Returns Vec object point from this Point to another one
     *
     * @param B The destination point
     * @return The vector from this point to B
     */
    public Vec to(Point B) {
        return new Vec(B.getVec().x - getVec().x, B.getVec().y - getVec().y, B.getVec().z - getVec().z);
    }


}
