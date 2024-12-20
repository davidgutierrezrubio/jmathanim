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

import com.jmathanim.Cameras.Camera;
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
        CENTER,
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
        DRIGHT,
        /**
         * Up-Right anchor point
         */
        URIGHT,
        /**
         * Up-Left anchor point
         */
        ULEFT,
        /**
         * Down-Left anchor point
         */
        DLEFT,
        RLOWER,
        RUPPER,
        LLOWER,
        LUPPER,
        DIAG1,
        DIAG2,
        DIAG3,
        DIAG4,
        ZTOP,
        ZBOTTOM
    }

    public enum innerType {
        CENTER,
        UPPER,
        RUPPER,
        RIGHT,
        RLOWER,
        LOWER,
        LLOWER,
        LEFT,
        LUPPER
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
        return getAnchorPoint(obj, anchor, 0, 0, 0);
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
        return getAnchorPoint(obj, anchor, gap, gap, gap);
    }

    /**
     * Return a {@link Point} object that represents the given anchor.For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box).A gap (in math coordinates) is
     * added, specified both by its x component and y component.
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor defined in the enum Anchor.Type
     * @param xgap Horizontal gap. Applied to LEFT RIGHT, URIGHT, ULEFT, DRIGHT,
     * DLEFT anchors
     * @param ygap Vertical gap. Applied to UPPER, LOWER,
     * @param zgap Z Gap. Applied to ZTOP and ZBOTTOM
     * @return The anchor point
     */
    public static Point getAnchorPoint(Boxable obj, Type anchor, double xgap, double ygap, double zgap) {
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
            case CENTER:
                resul = bb.getCenter();
                break;

            case LEFT:
                resul = bb.addGap(xgap, 0).getLeft();
                break;
            case RIGHT:
                resul = bb.addGap(xgap, 0).getRight();
                break;
            case LOWER:
                resul = bb.addGap(0, ygap).getLower();
                break;
            case UPPER:
                resul = bb.addGap(0, ygap).getUpper();
                break;

            case ULEFT:
                resul = bb.addGap(xgap, 0).getUL();
                break;
            case URIGHT:
                resul = bb.addGap(xgap, 0).getUR();
                break;
            case DLEFT:
                resul = bb.addGap(xgap, 0).getDL();
                break;
            case DRIGHT:
                resul = bb.addGap(xgap, 0).getDR();
                break;
            case RLOWER:
                resul = bb.addGap(0, ygap).getDR();
                break;
            case RUPPER:
                resul = bb.addGap(0, ygap).getUR();
                break;
            case LLOWER:
                resul = bb.addGap(0, ygap).getDL();
                break;
            case LUPPER:
                resul = bb.addGap(0, ygap).getUL();
                break;
            case DIAG1:
                resul = bb.addGap(xgap, ygap).getUR();
                break;
            case DIAG2:
                resul = bb.addGap(xgap, ygap).getUL();
                break;
            case DIAG3:
                resul = bb.addGap(xgap, ygap).getDL();
                break;
            case DIAG4:
                resul = bb.addGap(xgap, ygap).getDR();
                break;
            case ZTOP:
                resul = bb.addGap(0, 0, zgap).getZTOP();
                break;
            case ZBOTTOM:
                resul = bb.addGap(0, 0, zgap).getZBOTTOM();
                break;

        }
        return resul;
    }

    /**
     * Returns the reverse anchor (LEFT-RIGHT, UP-DOWN, etc.) The reverse of
     * URIGHT is ULEFT (not DLEFT) so that we can anchor an object right to
     * another and vertically upper aligned
     *
     * @param anchorPoint Anchor to compute reverse
     * @return Reversed anchor
     */
    public static Type reverseAnchorPoint(Type anchorPoint) {
        Type resul = Type.CENTER;// Default
        switch (anchorPoint) {
            case BY_POINT:
                resul = Type.BY_POINT;
                break;
            case CENTER:
                resul = Type.CENTER;
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

            case ULEFT:
                resul = Type.URIGHT;
                break;
            case URIGHT:
                resul = Type.ULEFT;
                break;
            case DLEFT:
                resul = Type.DRIGHT;
                break;
            case DRIGHT:
                resul = Type.DLEFT;
                break;
            case RLOWER:
                resul = Type.RUPPER;
                break;
            case RUPPER:
                resul = Type.RLOWER;
                break;
            case LLOWER:
                resul = Type.LUPPER;
                break;
            case LUPPER:
                resul = Type.LLOWER;
                break;
            case DIAG1:
                resul = Type.DIAG3;
                break;
            case DIAG2:
                resul = Type.DIAG4;
                break;
            case DIAG3:
                resul = Type.DIAG1;
                break;
            case DIAG4:
                resul = Type.DIAG2;
                break;
            case ZTOP:
                resul = Type.ZBOTTOM;
                break;
            case ZBOTTOM:
                resul = Type.ZTOP;
                break;
        }
        return resul;
    }

    /**
     * Returns an anchor point relative to the current screen, in math
     * coordinates
     *
     * @param camera Camera
     * @param anchor Anchor type
     * @param xMargin x margin to apply to the anchor
     * @param yMargin y margin to apply to the anchor
     * @return A {@link Point} located at the current anchor
     */
    public static Point getScreenAnchorPoint(Camera camera, Type anchor, double xMargin, double yMargin) {
        if (camera == null) {
            //If not set, use default
            camera = JMathAnimConfig.getConfig().getCamera();
        }
        Point resul = new Point();
        Vec gaps = camera.getGaps();
        Rect mathViewWithGap = camera.getMathView().addGap(-xMargin - gaps.x, -yMargin - gaps.y);
        switch (anchor) {
            case CENTER:
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

            case ULEFT:
                resul = mathViewWithGap.getUL();
                break;
            case LUPPER:
                resul = mathViewWithGap.getUL();
                break;
            case RUPPER:
                resul = mathViewWithGap.getUR();
                break;
            case URIGHT:
                resul = mathViewWithGap.getUR();
                break;
            case DLEFT:
                resul = mathViewWithGap.getDL();
                break;
            case LLOWER:
                resul = mathViewWithGap.getDL();
                break;
            case DRIGHT:
                resul = mathViewWithGap.getDR();
                break;
            case RLOWER:
                resul = mathViewWithGap.getDR();
                break;
        }
        return resul;
    }

}
