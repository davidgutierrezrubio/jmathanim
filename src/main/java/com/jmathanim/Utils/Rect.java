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
package com.jmathanim.Utils;

import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Stateable;

/**
 * Encapsulates data about a rectangle
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Rect implements Stateable {//TODO: Adjust this to 3D coordinates

    public double xmin, ymin, xmax, ymax, zmin, zmax;
    private Rect rBackup;

    public static Rect make(Point a, Point b) {
        double xmin = Math.min(a.v.x, b.v.x);
        double xmax = Math.max(a.v.x, b.v.x);
        double ymin = Math.min(a.v.y, b.v.y);
        double ymax = Math.max(a.v.y, b.v.y);
        double zmin = Math.min(a.v.z, b.v.z);
        double zmax = Math.max(a.v.z, b.v.z);
        return new Rect(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    public Rect(double xmin, double ymin, double xmax, double ymax) {
        this(xmin, ymin, 0, xmax, ymax, 0);
    }

    public Rect(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.zmin = zmin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.zmax = zmax;

    }

    /**
     * Computes coordinates of the intersection of this Rect with the line
     * defined by the coordinates x1,y1,x2,y2 (2D version)
     *
     *
     * @param x1 x-coordinate of the first point that defines the line
     * @param y1 y-coordinate of the first point that defines the line
     * @param x2 x-coordinate of the second point that defines the line
     * @param y2 y-coordinate of the second point that defines the line
     * @return A 4-tuple of coordinates, representing the 2 intersection points
     * of the line with the rect.
     */
    public double[] intersectLine(double x1, double y1, double x2, double y2) {

        Vec v1, v2, v3, v4;
        double sc1, sc2, sc3, sc4;
        Vec vRect = new Vec(x2 - x1, y2 - y1);
        double lambda1, lambda2;
        //Particular cases:
        //Line lines in the left side
        if ((x1 == xmin) & (x2 == xmin)) {
            //UL and DL corners
            return new double[]{xmin, (y1 < y2 ? ymin : ymax), xmin, (y1 < y2 ? ymax : ymin)};
        }
        //Line lines in the right side
        if ((x1 == xmax) & (x2 == xmax)) {
            //UR and DR corners
            return new double[]{xmax, (y1 < y2 ? ymin : ymax), xmax, (y1 < y2 ? ymax : ymin)};
        }
        //Line lines in the lower side
        if ((y1 == ymin) & (y2 == ymin)) {
            //UL and UR corners
            return new double[]{(x1 < x2 ? xmin : xmax), ymin, (x1 < x2 ? xmax : xmin), ymin};
        }
        //Line lines in the upper side
        if ((y1 == ymax) & (y2 == ymax)) {
            //UL and UR corners
            return new double[]{(x1 < x2 ? xmin : xmax), ymax, (x1 < x2 ? xmax : xmin), ymax};
        }
        double interx1 = xmin;
        double interx2 = xmax;
        double intery1 = ymin;
        double intery2 = ymax;

        v1 = new Vec(xmin - x1, ymin - y1);
        v2 = new Vec(xmin - x1, ymax - y1);
        v3 = new Vec(xmax - x1, ymax - y1);
        v4 = new Vec(xmax - x1, ymin - y1);
        sc1 = vRect.cross(v1).z;
        sc2 = vRect.cross(v2).z;
        sc3 = vRect.cross(v3).z;
        sc4 = vRect.cross(v4).z;
        if (sc1 < 0) {//Ensure sc1 is always >0
            sc1 = -sc1;
            sc2 = -sc2;
            sc3 = -sc3;
            sc4 = -sc4;
        }

        //Now I test all possible cases:
        //Case 1:
        if (sc1 > 0 & sc2 > 0 && sc3 > 0 && sc4 > 0) {   //There are no interesection points
            return null;
        }
        //Case 2:
        if (sc1 > 0 & sc2 < 0 && sc3 < 0 && sc4 < 0) {   //Line cross at L and D
            //intersect with xmin:
            //x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRect.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRect.y;
            //intersect with ymin:
            //y1+lambda*vRecta.y=ymin;
            lambda2 = (ymin - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymin;
        }
        //Case 3:
        if (sc1 > 0 & sc2 < 0 && sc3 > 0 && sc4 > 0) {   //Line cross at L and U
            //intersect with xmin:
            //x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRect.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRect.y;
            //intersect with ymax:
            //y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymax;
        }
        //Case 4:
        if (sc1 > 0 & sc2 > 0 && sc3 < 0 && sc4 > 0) {   //Line cross at R and U
            //intersect with xmax:
            //x1+lambda*vRecta.x=xmax;
            lambda1 = (xmax - x1) / vRect.x;
            interx1 = xmax;
            intery1 = y1 + lambda1 * vRect.y;
            //intersect with ymax:
            //y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymax;
        }

        //Case 5:
        if (sc1 > 0 & sc2 > 0 && sc3 > 0 && sc4 < 0) {   //Line cross at R and D
            //intersect with xmax:
            //x1+lambda*vRecta.x=xmax;
            lambda1 = (xmax - x1) / vRect.x;
            interx1 = xmax;
            intery1 = y1 + lambda1 * vRect.y;
            //intersect with ymin:
            //y1+lambda*vRecta.y=ymin;
            lambda2 = (ymin - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymin;
        }
        //Case 6:
        if (sc1 > 0 & sc2 < 0 && sc3 < 0 && sc4 > 0) {   //Line cross at L and R
            //intersect with xmin:
            //x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRect.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRect.y;
            //intersect with xmax:
            //x1+lambda*vRecta.x=xmin;
            lambda2 = (xmax - x1) / vRect.x;
            interx2 = xmax;
            intery2 = y1 + lambda2 * vRect.y;
        }
        //Case 7:
        if (sc1 > 0 & sc2 > 0 && sc3 < 0 && sc4 < 0) {   //Line cross at D and U
            //intersect with ymin:
            //y1+lambda*vRecta.y=ymin;
            lambda1 = (ymin - y1) / vRect.y;
            interx1 = x1 + lambda1 * vRect.x;
            intery1 = ymin;
            //intersect with ymax:
            //y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymax;
        }
        //Case 8: 
        if (sc4 == 0 & sc1 > 0 & sc2 > 0 && sc3 > 0) { //Exterior line that intersects only at (xmax,ymin)
            return new double[]{xmax, ymin, xmax, ymin};
        }
        //Case 9: 
        if (sc3 == 0 & sc1 > 0 & sc2 > 0 && sc4 > 0) { //Exterior line that intersects only at (xmax,ymax)
            return new double[]{xmax, ymax, xmax, ymax};
        }
        //Case 10: 
        if (sc2 == 0 & sc1 > 0 & sc3 > 0 && sc4 > 0) { //Exterior line that intersects only at (xmin,ymax)
            return new double[]{xmin, ymax, xmin, ymax};
        }
        //Case 11: 
        if (sc1 == 0) { //Exterior line that intersects only at (xmin,ymax)
            if ((sc2 > 0 & sc3 > 0 & sc4 > 0) | (sc2 < 0 & sc3 < 0 & sc4 < 0)) {
                return new double[]{xmin, ymin, xmin, ymin};
            }
        }

        //Now, determines the correct order of the solution
        double[] resul;
        v1 = new Vec(interx1 - x1, intery1 - y1);
        v2 = new Vec(interx2 - x1, intery2 - y1);
        if (vRect.dot(v1) > 0) {
            //In this case, interx1,intery2 is closer to x2,y2
            resul = new double[]{interx2, intery2, interx1, intery1};
        } else {
            resul = new double[]{interx1, intery1, interx2, intery2};
        }

        return resul;
    }

//    public Rect union(Rect b) {
//       return Rect.union(this, b);
//    }
    /**
     * Returns the smallest {@link Rect} which contains 2 given rects. The
     * method is made static so that it can deal with the case any of them is
     * null
     *
     * @param a The another {@link Rect}
     * @param b The another {@link Rect}
     * @return A new {@link Rect} with the union of both rects
     */
    public static Rect union(Rect a, Rect b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new Rect(Math.min(a.xmin, b.xmin), Math.min(a.ymin, b.ymin), Math.max(a.xmax, b.xmax), Math.max(a.ymax, b.ymax));

    }

    /**
     * Returns the center of this Rect
     *
     * @return A {@link Point} representing the rect center
     */
    public Point getCenter() {
        return new Point(.5 * (xmin + xmax), .5 * (ymin + ymax), .5 * (zmin + zmax));
    }

    /**
     * Computes the height of the rect
     *
     * @return Height of the rect
     */
    public double getHeight() {
        return ymax - ymin;
    }

    /**
     * Computes the width of the rect
     *
     * @return Width of the rect
     */
    public double getWidth() {
        return xmax - xmin;
    }

    @Override
    public String toString() {
        return "Rect{" + "xmin=" + xmin + ", ymin=" + ymin + ", xmax=" + xmax + ", ymax=" + ymax + '}';
    }

    /**
     * Interpolates this rect with another given
     *
     * @param rDst The other rect to interpolate
     * @param t Inerpolation parameter. 0 corresponds to this rect, and 1 to
     * rDst
     * @return The interpolated rect
     */
    public Rect interpolate(Rect rDst, double t) {
        return new Rect((1 - t) * xmin + t * rDst.xmin, (1 - t) * ymin + t * rDst.ymin, (1 - t) * xmax + t * rDst.xmax, (1 - t) * ymax + t * rDst.ymax);
    }

    public Point getLeft() {
        return new Point(xmin, .5 * (ymin + ymax));
    }

    public Point getRight() {
        return new Point(xmax, .5 * (ymin + ymax));
    }

    public Point getUpper() {
        return new Point(.5 * (xmin + xmax), ymax);
    }

    public Point getLower() {
        return new Point(.5 * (xmin + xmax), ymin);
    }

    public Point getUL() {
        return new Point(xmin, ymax);
    }

    public Point getUR() {
        return new Point(xmax, ymax);
    }

    public Point getDL() {
        return new Point(xmin, ymin);
    }

    public Point getDR() {
        return new Point(xmax, ymin);
    }

    /**
     * Computes a new Rect which is the original grown by a horizontal and
     * vertical gap
     *
     * @param xgap Horizontal gap
     * @param ygap Vertical gap
     * @return A new {@link Rect} with the gaps applied
     */
    public Rect addGap(double xgap, double ygap) {
        return new Rect(xmin - xgap, ymin - ygap, xmax + xgap, ymax + ygap);
    }

    /**
     * Scale the rectangle around center, and return a new one with the result.
     * Does not affect the current rect.
     *
     * @param xs x scale
     * @param ys y scale
     * @return The scaled rectangle.
     */
    public Rect scaled(double xs, double ys) {
        Point p = getCenter();
        double xminNew = p.v.x - .5 * getWidth() * xs;
        double xmaxNew = p.v.x + .5 * getWidth() * xs;
        double yminNew = p.v.y - .5 * getHeight() * ys;
        double ymaxNew = p.v.y + .5 * getHeight() * ys;
        return new Rect(xminNew, yminNew, xmaxNew, ymaxNew);
    }

    /**
     * Computes the {@link Rect} shifted by a given vector. Original Rect is not
     * modified
     *
     * @param v Shift vector
     * @return A copy of this Rect, shifted.
     */
    public Rect shifted(Vec v) {
        return new Rect(xmin + v.x, ymin + v.y, xmax + v.x, ymax + v.y);
    }

    public void copyFrom(Rect r) {
        this.xmin = r.xmin;
        this.xmax = r.xmax;
        this.ymin = r.ymin;
        this.ymax = r.ymax;
        this.zmin = r.zmin;
        this.zmax = r.zmax;
    }

    @Override
    public void saveState() {
        rBackup = new Rect(0, 0, 0, 0);
        this.rBackup.copyFrom(this);
    }

    @Override
    public void restoreState() {
        this.copyFrom(this.rBackup);
    }

    /**
     * Return the smallest rect that contains this rect rotated the given angle
     *
     * @param rotateAngle Rotation angle, in radians
     * @return A new Rect, the smallest containing the rotated rect.
     */
    public Rect getRotatedRect(double rotateAngle) {
        Point center = this.getCenter();
        Point A = this.getUL().rotate(center, rotateAngle);
        Point B = this.getUR().rotate(center, rotateAngle);
        Point C = this.getDR().rotate(center, rotateAngle);
        Point D = this.getDL().rotate(center, rotateAngle);
        Rect r1 = Rect.make(A, C);
        Rect r2 = Rect.make(B, D);
        return Rect.union(r1, r2);
    }

    /**
     * Gets the point inside the rect with the relative coordinates from 0 to 1.
     * The point (0,0) refers to the DL corner, the (1,1) the UR corner, and
     * (.5,.5) the center of the rect
     *
     * @param relX x relative coordinate, from 0 to 1
     * @param relY y relative coordinate, from 0 to 1
     * @return
     */
    public Point getRelPoint(double relX, double relY) {
        return Point.at(xmin + relX * (xmax - xmin), ymin + relY * (ymax - ymin));
    }

    public Rect copy() {
        return new Rect(xmin,ymin,xmax,ymax);
    }

    public void centerAt(Point dstCenter) {
        Vec v=getCenter().to(dstCenter);
        xmin+=v.x;
        xmax+=v.x;
        ymin+=v.y;
        ymax+=v.y;
    }
}
