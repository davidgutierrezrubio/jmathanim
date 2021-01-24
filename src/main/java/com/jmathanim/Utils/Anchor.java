/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubiogmail.com
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

import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 * A class that manages relevant points of a MathObject, or any class that
 * implements the Boxable interface.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Anchor {

    public enum Type {
        /**
         * Anchor is specified by a given point
         */
        BY_POINT,
        /**
         * Anchor determined by the center of the object
         */
        BY_CENTER,
        /**
         * Right anchor point. Vertically centered.
         */
        RIGHT,
        /**
         * Upper anchor point. Horizontally centered
         */
        UPPER,
        /**
         * Left anchor point. Vertically centered.
         */
        LEFT,
        /**
         * Lower anchor point. Horizontally centered
         */
        LOWER,
        /**
         * Down-Right anchor point
         */
        DR,
        /**
         * Up-Right anchor point
         */
        UR,
        /**
         * Up-Left anchor point
         */
        UL,
        /**
         * Down-Left anchor point
         */
        DL
    }

    /**
     * Return a {@link Point} object that represents the given anchor. For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box)
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor defined in the enum Anchor.Type
     * @return The anchor point
     */
    public static Point getAnchorPoint(Boxable obj, Type anchor) {
        return getAnchorPoint(obj, anchor, 0, 0);
    }

    /**
     * Return a {@link Point} object that represents the given anchor.For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box).A gap (in math coordinates) is
     * added, equal in x and y direction.
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor defined in the enum Anchor.Type
     * @param gap Gap to add to the anchor
     * @return The anchor point
     */
    public static Point getAnchorPoint(Boxable obj, Type anchor, double gap) {
        return getAnchorPoint(obj, anchor, gap, gap);
    }

    /**
     * Return a {@link Point} object that represents the given anchor.For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box).A gap (in math coordinates) is
     * added, specified both by its x component and y component.
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor defined in the enum Anchor.Type
     * @param xgap Horizontal gap
     * @param ygap Vertical gap
     * @return The anchor point
     */
    public static Point getAnchorPoint(Boxable obj, Type anchor, double xgap, double ygap) {
        Point resul = new Point();
        final Rect bb = obj.getBoundingBox();
        switch (anchor) {
            case BY_POINT:
                if (obj instanceof Point) {
                    Point p = (Point) obj;
                    resul = p.copy();
                } else {
                    if (obj instanceof MathObject) {
                        MathObject o = (MathObject) obj;
                        resul = o.getAbsoluteAnchorPoint();
                    }
                }
                break;
            case BY_CENTER:
                resul = bb.getCenter();
                break;

            case LEFT:
                resul = bb.addGap(xgap, ygap).getLeft();
                break;
            case RIGHT:
                resul = bb.addGap(xgap, ygap).getRight();
                break;
            case LOWER:
                resul = bb.addGap(xgap, ygap).getLower();
                break;
            case UPPER:
                resul = bb.addGap(xgap, ygap).getUpper();
                break;

            case UL:
                resul = bb.addGap(xgap, ygap).getUL();
                break;
            case UR:
                resul = bb.addGap(xgap, ygap).getUR();
                break;
            case DL:
                resul = bb.addGap(xgap, ygap).getDL();
                break;
            case DR:
                resul = bb.addGap(xgap, ygap).getDR();
                break;

        }
        return resul;
    }

    /**
     * Returns the reverse anchor (LEFT-RIGHT, UP-DOWN, etc.) The reverse of UR
     * is UL (not DL) so that we can anchor an object right to another and
     * vertically upper aligned
     *
     * @param anchorPoint Anchor to compute reverse
     * @return Reversed anchor
     */
    public static Type reverseAnchorPoint(Type anchorPoint) {
        Type resul = Type.BY_CENTER;//Default
        switch (anchorPoint) {
            case BY_POINT:
                resul = Type.BY_POINT;
                break;
            case BY_CENTER:
                resul = Type.BY_CENTER;
                break;

            case LEFT:
                resul = Type.RIGHT;
                break;
            case RIGHT:
                resul = Type.LEFT;
                break;
            case LOWER:
                resul = Type.UPPER;
                break;
            case UPPER:
                resul = Type.LOWER;
                break;

            case UL:
                resul = Type.UR;
                break;
            case UR:
                resul = Type.UL;
                break;
            case DL:
                resul = Type.DR;
                break;
            case DR:
                resul = Type.DL;
                break;
        }
        return resul;
    }

    /**
     * Returns an anchor point relative to the current screen, in math
     * coordinates
     *
     * @param anchor Anchor type
     * @param xMargin x margin to apply to the anchor
     * @param yMargin y margin to apply to the anchor
     * @return A {@link Point} located at the current anchor
     */
    public static Point getScreenAnchorPoint(Type anchor, double xMargin, double yMargin) {
        Point resul = new Point();
        Rect mathViewWithGap = JMathAnimConfig.getConfig().getCamera().getMathView().addGap(-xMargin, -yMargin);
        switch (anchor) {
            case BY_CENTER:
                resul = mathViewWithGap.getCenter();
                break;
            case LEFT:
                resul = mathViewWithGap.getLeft();
                break;
            case RIGHT:
                resul = mathViewWithGap.getRight();
                break;
            case LOWER:
                resul = mathViewWithGap.getLower();
                break;
            case UPPER:
                resul = mathViewWithGap.getUpper();
                break;

            case UL:
                resul = mathViewWithGap.getUL();
                break;
            case UR:
                resul = mathViewWithGap.getUR();
                break;
            case DL:
                resul = mathViewWithGap.getDL();
                break;
            case DR:
                resul = mathViewWithGap.getDR();
                break;
        }
        return resul;
    }

}
