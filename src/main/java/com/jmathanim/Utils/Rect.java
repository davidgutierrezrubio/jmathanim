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

import com.jmathanim.Enum.AnchorType;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Point;

import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates data about a rectangle
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Rect implements Boxable {// TODO: Adjust this to 3D coordinates

    public double xmin, ymin, xmax, ymax, zmin, zmax;
    private Rect rBackup;

    /**
     * Generates a Rect with the given lower-left and upper-right corners (2D version)
     *
     * @param xmin x-min coordinate
     * @param ymin y-min coordinate
     * @param xmax x-max coordinate
     * @param ymax y-max coordinate
     */
    public Rect(double xmin, double ymin, double xmax, double ymax) {
        this(xmin, ymin, 0, xmax, ymax, 0);
    }


    /**
     * Generates a Rect with the given lower-left and upper-right corners (3D version)
     *
     * @param xmin x-min coordinate
     * @param ymin y-min coordinate
     * @param zmin z-min coordinate
     * @param xmax x-max coordinate
     * @param ymax y-max coordinate
     * @param zmax z-max coordinate
     */
    public Rect(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.zmin = zmin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.zmax = zmax;

    }

    /**
     * Generates a basic Rect 2D square, side 1, centered at origin
     *
     * @return The generated Rect
     */
    public static Rect centeredUnitSquare() {
        return new Rect(-.5, -.5,
                .5, .5
        );
    }

    public static Rect makeFromVec(Vec v) {
        return new Rect(v.x, v.y, v.x, v.y);
    }

    /**
     * Generates a basic Rect 3D Cube, side 1, centered at origin
     *
     * @return The generated Rect
     */
    public static Rect centeredUnitCube() {
        return new Rect(-.5, -.5, -.5,
                .5, .5, .5
        );
    }

    /**
     * Return the smallest rect that contains all specified objects
     *
     * @param objs An array of Boxable objects.
     * @return The generated Rect
     */
    public static Rect make(Boxable... objs) {
        return make(Arrays.asList(objs));
    }

    /**
     * Return the smallest rect that contains all specified objects
     *
     * @param objs A list of Boxable objects.
     * @return The generated Rect
     */
    public static Rect make(List<Boxable> objs) {
        Rect resul = new EmptyRect();
        for (Boxable obj : objs) {
            resul = Rect.union(resul, obj.getBoundingBox());
        }
        return resul;
    }

    /**
     * Returns the smallest {@link Rect} which contains 2 given rects. The method is made static so that it can deal
     * with the case any of them is null
     *
     * @param a The another {@link Rect}
     * @param b The another {@link Rect}
     * @return A new {@link Rect} with the union of both rects
     */
    public static Rect union(Rect a, Rect b) {
        if ((a == null) || (a instanceof EmptyRect) || (a.isNan())) {
            return b;
        }
        if ((b == null) || (b instanceof EmptyRect) || (b.isNan())) {
            return a;
        }
        return new Rect(Math.min(a.xmin, b.xmin), Math.min(a.ymin, b.ymin), Math.min(a.zmin, b.zmin),
                Math.max(a.xmax, b.xmax), Math.max(a.ymax, b.ymax), Math.max(a.zmax, b.zmax));

    }

//    public Rect union(Rect b) {
//       return Rect.union(this, b);
//    }

    /**
     * Computes coordinates of the intersection of this Rect with the line defined by the coordinates x1,y1,x2,y2 (2D
     * version)
     *
     * @param x1 x-coordinate of the first point that defines the line
     * @param y1 y-coordinate of the first point that defines the line
     * @param x2 x-coordinate of the second point that defines the line
     * @param y2 y-coordinate of the second point that defines the line
     * @return A 4-tuple of coordinates, representing the 2 intersection points of the line with the rect.
     */
    public double[] intersectLine(double x1, double y1, double x2, double y2) {

        Vec v1, v2, v3, v4;
        double sc1, sc2, sc3, sc4;
        Vec vRect = new Vec(x2 - x1, y2 - y1);
        double lambda1, lambda2;
        // Particular cases:
        // Line lines in the left side
        if ((x1 == xmin) & (x2 == xmin)) {
            // ULEFT and DLEFT corners
            return new double[]{xmin, (y1 < y2 ? ymin : ymax), xmin, (y1 < y2 ? ymax : ymin)};
        }
        // Line lines in the right side
        if ((x1 == xmax) & (x2 == xmax)) {
            // URIGHT and DRIGHT corners
            return new double[]{xmax, (y1 < y2 ? ymin : ymax), xmax, (y1 < y2 ? ymax : ymin)};
        }
        // Line lines in the lower side
        if ((y1 == ymin) & (y2 == ymin)) {
            // ULEFT and URIGHT corners
            return new double[]{(x1 < x2 ? xmin : xmax), ymin, (x1 < x2 ? xmax : xmin), ymin};
        }
        // Line lines in the upper side
        if ((y1 == ymax) & (y2 == ymax)) {
            // ULEFT and URIGHT corners
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
        if (sc1 < 0) {// Ensure sc1 is always >0
            sc1 = -sc1;
            sc2 = -sc2;
            sc3 = -sc3;
            sc4 = -sc4;
        }

        // Now I test all possible cases:
        // Case 1:
        if (sc1 > 0 & sc2 > 0 && sc3 > 0 && sc4 > 0) { // There are no interesection points
            return null;
        }
        // Case 2:
        if (sc1 > 0 & sc2 < 0 && sc3 < 0 && sc4 < 0) { // Line cross at L and D
            // intersect with xmin:
            // x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRect.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRect.y;
            // intersect with ymin:
            // y1+lambda*vRecta.y=ymin;
            lambda2 = (ymin - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymin;
        }
        // Case 3:
        if (sc1 > 0 & sc2 < 0 && sc3 > 0 && sc4 > 0) { // Line cross at L and U
            // intersect with xmin:
            // x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRect.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRect.y;
            // intersect with ymax:
            // y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymax;
        }
        // Case 4:
        if (sc1 > 0 & sc2 > 0 && sc3 < 0 && sc4 > 0) { // Line cross at R and U
            // intersect with xmax:
            // x1+lambda*vRecta.x=xmax;
            lambda1 = (xmax - x1) / vRect.x;
            interx1 = xmax;
            intery1 = y1 + lambda1 * vRect.y;
            // intersect with ymax:
            // y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymax;
        }

        // Case 5:
        if (sc1 > 0 & sc2 > 0 && sc3 > 0 && sc4 < 0) { // Line cross at R and D
            // intersect with xmax:
            // x1+lambda*vRecta.x=xmax;
            lambda1 = (xmax - x1) / vRect.x;
            interx1 = xmax;
            intery1 = y1 + lambda1 * vRect.y;
            // intersect with ymin:
            // y1+lambda*vRecta.y=ymin;
            lambda2 = (ymin - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymin;
        }
        // Case 6:
        if (sc1 > 0 & sc2 < 0 && sc3 < 0 && sc4 > 0) { // Line cross at L and R
            // intersect with xmin:
            // x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRect.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRect.y;
            // intersect with xmax:
            // x1+lambda*vRecta.x=xmin;
            lambda2 = (xmax - x1) / vRect.x;
            interx2 = xmax;
            intery2 = y1 + lambda2 * vRect.y;
        }
        // Case 7:
        if (sc1 > 0 & sc2 > 0 && sc3 < 0 && sc4 < 0) { // Line cross at D and U
            // intersect with ymin:
            // y1+lambda*vRecta.y=ymin;
            lambda1 = (ymin - y1) / vRect.y;
            interx1 = x1 + lambda1 * vRect.x;
            intery1 = ymin;
            // intersect with ymax:
            // y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRect.y;
            interx2 = x1 + lambda2 * vRect.x;
            intery2 = ymax;
        }
        // Case 8:
        if (sc4 == 0 & sc1 > 0 & sc2 > 0 && sc3 > 0) { // Exterior line that intersects only at (xmax,ymin)
            return new double[]{xmax, ymin, xmax, ymin};
        }
        // Case 9:
        if (sc3 == 0 & sc1 > 0 & sc2 > 0 && sc4 > 0) { // Exterior line that intersects only at (xmax,ymax)
            return new double[]{xmax, ymax, xmax, ymax};
        }
        // Case 10:
        if (sc2 == 0 & sc1 > 0 & sc3 > 0 && sc4 > 0) { // Exterior line that intersects only at (xmin,ymax)
            return new double[]{xmin, ymax, xmin, ymax};
        }
        // Case 11:
        if (sc1 == 0) { // Exterior line that intersects only at (xmin,ymax)
            if ((sc2 > 0 & sc3 > 0 & sc4 > 0) | (sc2 < 0 & sc3 < 0 & sc4 < 0)) {
                return new double[]{xmin, ymin, xmin, ymin};
            }
        }

        // Now, determines the correct order of the solution
        double[] resul;
        v1 = new Vec(interx1 - x1, intery1 - y1);
        v2 = new Vec(interx2 - x1, intery2 - y1);
        if (vRect.dot(v1) > 0) {
            // In this case, interx1,intery2 is closer to x2,y2
            resul = new double[]{interx2, intery2, interx1, intery1};
        } else {
            resul = new double[]{interx1, intery1, interx2, intery2};
        }

        return resul;
    }

    /**
     * Returns the center of this Rect
     *
     * @return A {@link Point} representing the rect center
     */
    public Vec getCenter() {
        return Vec.to(.5 * (xmin + xmax), .5 * (ymin + ymax), .5 * (zmin + zmax));
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

    private boolean isNan() {
        return Double.isNaN(xmin) || Double.isNaN(xmax) || Double.isNaN(ymin) || Double.isNaN(ymax) || Double.isNaN(zmin) || Double.isNaN(zmax);
    }

    @Override
    public String toString() {
        if ((zmin == 0) && (zmax == 0))
            return String.format("Rect[%1.2f, %1.2f][%1.2f, %1.2f]", xmin, ymin, xmax, ymax);
        else
            return String.format("Rect[%1.2f, %1.2f, %1.2f][%1.2f, %1.2f, %1.2f]", xmin, ymin, zmin, xmax, ymax, zmax);
    }

    /**
     * Interpolates this rect with another given
     *
     * @param rDst The other rect to interpolate
     * @param t    Inerpolation parameter. 0 corresponds to this rect, and 1 to rDst
     * @return The interpolated rect
     */
    public Rect interpolate(Rect rDst, double t) {
        return new Rect(
                (1 - t) * xmin + t * rDst.xmin,
                (1 - t) * ymin + t * rDst.ymin,
                (1 - t) * zmin + t * rDst.zmin,
                (1 - t) * xmax + t * rDst.xmax,
                (1 - t) * ymax + t * rDst.ymax,
                (1 - t) * zmax + t * rDst.zmax
        );
    }

    public Vec getLeft() {
        return Vec.to(xmin, .5 * (ymin + ymax), .5 * (zmin + zmax));
    }

    public Vec getRight() {
        return Vec.to(xmax, .5 * (ymin + ymax), .5 * (zmin + zmax));
    }

    public Vec getUpper() {
        return Vec.to(.5 * (xmin + xmax), ymax, .5 * (zmin + zmax));
    }

    public Vec getLower() {
        return Vec.to(.5 * (xmin + xmax), ymin, .5 * (zmin + zmax));
    }

    public Vec getUL() {
        return Vec.to(xmin, ymax, .5 * (zmin + zmax));
    }

    public Vec getUR() {
        return Vec.to(xmax, ymax, .5 * (zmin + zmax));
    }

    public Vec getDL() {
        return Vec.to(xmin, ymin, .5 * (zmin + zmax));
    }

    public Vec getDR() {
        return Vec.to(xmax, ymin, .5 * (zmin + zmax));
    }

    public Vec getZTOP() {
        return Vec.to(.5 * (xmin + xmax), .5 * (ymin + ymax), zmax);
    }

    public Vec getZBOTTOM() {
        return Vec.to(.5 * (xmin + xmax), .5 * (ymin + ymax), zmin);
    }

    /**
     * Growns horizontally and vertically the rect adding specified gaps. Each gap is added twice (hgap left and right,
     * and vgap up and down). The original Rect is affected.
     *
     * @param xgap Horizontal gap
     * @param ygap Vertical gap
     * @return This object
     */
    public Rect addGap(double xgap, double ygap) {
        return addGap(xgap, ygap, 0);
    }

    /**
     * Growns horizontally, vertically and in z axis the rect adding specified gaps. Each gap is added twice (hgap left
     * and right, and vgap up and down). The original Rect is affected.
     *
     * @param xgap Horizontal gap
     * @param ygap Vertical gap
     * @param zgap Z gap
     * @return This object
     */
    public Rect addGap(double xgap, double ygap, double zgap) {
//        return new Rect(xmin - xgap, ymin - ygap, zmin, xmax + xgap, ymax + ygap, zmax);
        xmin -= xgap;
        ymin -= ygap;
        xmax += xgap;
        ymax += ygap;
        zmin -= zgap;
        zmax += zgap;
        return this;
    }

    /**
     * Computes a new Rect with the following gaps added (right, upper, left, lower).
     *
     * @param rightGap Right gap
     * @param upperGap Upper gap
     * @param leftGap  Left gap
     * @param lowerGap Lower gap
     * @return A new {@link Rect} with the gaps applied
     */
    public Rect addGap(double rightGap, double upperGap, double leftGap, double lowerGap) {
        return new Rect(xmin - leftGap, ymin - lowerGap, zmin, xmax + rightGap, ymax + upperGap, zmax);
    }

    /**
     * Computes a new Rect with the following gaps added (right, upper, left, lower).
     *
     * @param rightGap Right gap
     * @param upperGap Upper gap
     * @param leftGap  Left gap
     * @param lowerGap Lower gap
     * @param zMinGap  Lower z gap
     * @param zMaxGap  Upper z gap
     * @return A new {@link Rect} with the gaps applied
     */
    public Rect addGap(double rightGap, double upperGap, double leftGap, double lowerGap, double zMinGap, double zMaxGap) {
        return new Rect(xmin - leftGap, ymin - lowerGap, zmin - zMinGap, xmax + rightGap, ymax + upperGap, zmax + zMaxGap);
    }

    /**
     * Scale the rectangle around center. The current rect is modified.
     *
     * @param xs x scale
     * @param ys y scale
     * @param zs z scale
     * @return This object.
     */
    public Rect scale(double xs, double ys, double zs) {
        Vec vCenter = getCenter();
        double w = getWidth();
        double h = getHeight();
        xmin = vCenter.x - .5 * w * xs;
        xmax = vCenter.x + .5 * w * xs;
        ymin = vCenter.y - .5 * h * ys;
        ymax = vCenter.y + .5 * h * ys;
        zmin = vCenter.z - .5 * h * zs;
        zmax = vCenter.z + .5 * h * zs;
        return this;
    }

    /**
     * Scale the rectangle around center. The current object is modified.
     *
     * @param xs x scale
     * @param ys y scale
     * @return This object
     */
    public Rect scale(double xs, double ys) {
        return scale(xs, ys, 1);
    }

    /**
     * Scale the rectangle around center. The current object is modified.
     *
     * @param scale x scale
     * @return This object
     */
    public Rect scale(double scale) {
        return scale(scale, scale, scale);
    }

    /**
     * Shifts the rect by a given vector. The object is modified
     *
     * @param v Shift vector
     * @return This object.
     */
    public Rect shift(Vec v) {
        xmin += v.x;
        ymin += v.y;
        zmin += v.z;
        xmax += v.x;
        ymax += v.y;
        zmax += v.z;
        return this;
    }

    public void copyFrom(Rect r) {
        this.xmin = r.xmin;
        this.xmax = r.xmax;
        this.ymin = r.ymin;
        this.ymax = r.ymax;
        this.zmin = r.zmin;
        this.zmax = r.zmax;
    }

    /**
     * Return the smallest rect that contains this rect rotated the given angle (2D version)
     *
     * @param rotateAngle Rotation angle, in radians
     * @return A new Rect, the smallest containing the rotated rect.
     */
    public Rect getRotatedRect(double rotateAngle) {//TODO: Adapt this properly to 3D
        Vec vCenter = this.getCenter();
        Vec A = this.getUL().rotate(vCenter, rotateAngle);
        Vec B = this.getUR().rotate(vCenter, rotateAngle);
        Vec C = this.getDR().rotate(vCenter, rotateAngle);
        Vec D = this.getDL().rotate(vCenter, rotateAngle);
        return Rect.make(A, B, C, D);
    }

    /**
     * Gets the coordinates inside the rect with the relative coordinates from 0 to 1. The point (0,0) refers to the DL
     * corner, the (1,1) the UR corner, and (.5,.5) the center of the rect. This is the 2D version, which returns a
     * point with 0 z coordinate
     *
     * @param relX x relative coordinate, from 0 to 1
     * @param relY y relative coordinate, from 0 to 1
     * @return A Vec with the relative coordinates
     */
    public Vec getRelCoordinates(double relX, double relY) {
        return Vec.to(xmin + relX * (xmax - xmin), ymin + relY * (ymax - ymin), 0);
    }

    public Vec getRelVec(Vec v) {
        return Vec.to(xmin + v.x * (xmax - xmin), ymin + v.y * (ymax - ymin), 0);
    }

//    /**
//     * Gets the point inside the rect with the relative coordinates from 0 to 1. The point (0,0) refers to the DL
//     * corner, the (1,1) the UR corner, and (.5,.5) the center of the rect (3D version).
//     *
//     * @param relX x relative coordinate, from 0 to 1
//     * @param relY y relative coordinate, from 0 to 1
//     * @param relZ z relative coordinate, from 0 to 1
//     * @return A Point with the relative coordinates
//     */
//    public Point getRelPoint(double relX, double relY, double relZ) {
//        return Point.at(xmin + relX * (xmax - xmin), ymin + relY * (ymax - ymin), zmin + relZ * (zmax - zmin));
//    }

    /**
     * Creates a copy of this Rect
     *
     * @return The new Rect
     */
    public Rect copy() {
        return new Rect(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    /**
     * Shifts the current rect so that its center is located at the given one. The current Rect is modified.
     *
     * @param dstCenter A point with the new center
     * @return This object.
     */
    public Rect centerAt(Coordinates<?> dstCenter) {
        Vec v = getCenter().to(dstCenter);
        xmin += v.x;
        xmax += v.x;
        ymin += v.y;
        ymax += v.y;
        zmin += v.z;
        zmax += v.z;
        return this;
    }

    @Override
    public Rect getBoundingBox() {
        return this.copy();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Return the specified boundary point given by an anchor
     *
     * @param anchor A value from Enum type Type
     * @return The specified boundary point
     */
    public Vec getFromAnchor(AnchorType anchor) {
        switch (anchor) {
            case UPPER:
                return getUpper();
            case LOWER:
                return getLower();
            case LEFT:
                return getLeft();
            case RIGHT:
                return getRight();
            case ULEFT:
                return getUL();
            case URIGHT:
                return getUR();
            case DLEFT:
                return getDL();
            case DRIGHT:
                return getDR();
            case CENTER:
                return getCenter();
        }
        return null;//Unknow case, return null
    }

    /**
     * Gets the transformed Rect by the given affine transform. The current Rect is not modified. The transformed Rect
     * is the smallest Rect that contains the transformed points of the original Rect.
     *
     * @param tr Affine transform.
     * @return A new Rect representing the transformed Rect.
     */
    public Rect getTransformedRect(AffineJTransform tr) {
        Vec a = getUL();
        Vec b = getDL();
        Vec c = getUR();
        Vec d = getDR();
        return make(
                a.applyAffineTransform(tr),
                b.applyAffineTransform(tr),
                c.applyAffineTransform(tr),
                d.applyAffineTransform(tr));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rect other = (Rect) obj;
        if (Double.doubleToLongBits(this.xmin) != Double.doubleToLongBits(other.xmin)) {
            return false;
        }
        if (Double.doubleToLongBits(this.ymin) != Double.doubleToLongBits(other.ymin)) {
            return false;
        }
        if (Double.doubleToLongBits(this.xmax) != Double.doubleToLongBits(other.xmax)) {
            return false;
        }
        if (Double.doubleToLongBits(this.ymax) != Double.doubleToLongBits(other.ymax)) {
            return false;
        }
        if (Double.doubleToLongBits(this.zmin) != Double.doubleToLongBits(other.zmin)) {
            return false;
        }
        return Double.doubleToLongBits(this.zmax) == Double.doubleToLongBits(other.zmax);
    }

    /**
     * Move this Rect the minim amount to fit inside the given Rect r. If this Rect is wider or taller than r, no
     * changes are made. The original object is altered.
     *
     * @param containerBox  Boxable object to fit in. May be a Rect, MathObject or Camera
     * @param horizontalGap Horizontal gap between the smashed rect and the container bounding box
     * @param verticalGap   Vertical gap between the smashed rect and the container bounding box
     * @return This object
     */
    public Rect smash(Boxable containerBox, double horizontalGap, double verticalGap) {
        smashInH(containerBox, horizontalGap);
        smashInV(containerBox, verticalGap);
        return this;
    }

    private void smashInH(Boxable containerBox, double horizontalGap) {
        Rect rBig = containerBox.getBoundingBox().addGap(-horizontalGap, 0, 0);
        if (getWidth() >= rBig.getWidth()) {
            return;
        }
        if (xmin < rBig.xmin) {
            xmax += (rBig.xmin - xmin);
            xmin = rBig.xmin;
            return;
        }

        if (xmax > rBig.xmax) {
            xmin -= (xmax - rBig.xmax);
            xmax = rBig.xmax;
        }
    }

    private Rect smashInV(Boxable containerBox, double verticalGap) {
        Rect rBig = containerBox.getBoundingBox().addGap(0, -verticalGap, 0);
        if (getHeight() >= rBig.getHeight()) {
            return this;
        }
        if (ymin < rBig.ymin) {
            ymax += (rBig.ymin - ymin);
            ymin = rBig.ymin;
            return this;
        }

        if (ymax > rBig.ymax) {
            ymin -= (ymax - rBig.ymax);
            ymax = rBig.ymax;
        }
        return this;
    }

    /**
     * Return Compute a vector in rect-coordinates. The original vector is unaltered.
     *
     * @param v Vector in spatial coordinates
     * @return Vector in rect-coordinates. (0,0) is lower-left corner of the Rect, (1,1) upper-right.
     */
    public Vec toRelCoordinates(Vec v) {
        AffineJTransform tr = AffineJTransform.createAffineTransformation(this,
                Rect.centeredUnitSquare().shift(Vec.to(.5, .5)), 1);
        return v.copy().applyAffineTransform(tr);
    }


}
