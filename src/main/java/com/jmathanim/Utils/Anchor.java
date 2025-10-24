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
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.ScreenAnchor;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Point;

/**
 * A class that manages relevant points of a MathObject, or any class that
 * implements the Boxable interface.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Anchor {




    /**
     * Return a {@link Vec} object that represents the given anchor. For
     * example getAnchorPoint(obj, LEFT) returns the upper point of the
     * object (determined by its bounding box)
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor defined in the enum Type
     * @return The anchor point
     */
    public static Vec getAnchorPoint(Boxable obj, AnchorType anchor) {
        return getAnchorPoint(obj, anchor, 0, 0, 0);
    }

    /**
     * Return a {@link Point} object that represents the given anchor.For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box).A gap (in math coordinates) is
     * added, equal in x and y direction.
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor defined in the enum Type
     * @param gap Gap to add to the anchor
     * @return The anchor point
     */
    public static Vec getAnchorPoint(Boxable obj, AnchorType anchor, double gap) {
        return getAnchorPoint(obj, anchor, gap, gap, gap);
    }

    /**
     * Return a {@link Point} object that represents the given anchor.For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box).A gap (in math coordinates) is
     * added, specified both by its x component and y component.
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor defined in the enum Type
     * @param xgap Horizontal gap. Applied to LEFT RIGHT, URIGHT, ULEFT, DRIGHT,
     * DLEFT anchors
     * @param ygap Vertical gap. Applied to UPPER, LOWER,
     * @param zgap Z Gap. Applied to ZTOP and ZBOTTOM
     * @return The anchor point
     */
    public static Vec getAnchorPoint(Boxable obj, AnchorType anchor, double xgap, double ygap, double zgap) {
        Vec resul = Vec.to(0,0);
        final Rect bb = obj.getBoundingBox();
        switch (anchor) {
            case BY_POINT:
                if (obj instanceof Coordinates<?>) {
                    Vec v = ((Coordinates<?>) obj).getVec();
                    resul = v.copy();
                } else {
                    if (obj instanceof MathObject) {
                        MathObject<?> o = (MathObject<?>) obj;
                        resul = o.getAbsoluteAnchorVec().copy();
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

            case LEFT_AND_ALIGNED_UPPER:
                resul = bb.addGap(xgap, 0).getUpperLeft();
                break;
            case RIGHT_AND_ALIGNED_UPPER:
                resul = bb.addGap(xgap, 0).getUpperRight();
                break;
            case LEFT_AND_ALIGNED_LOWER:
                resul = bb.addGap(xgap, 0).getLowerLeft();
                break;
            case RIGHT_AND_ALIGNED_LOWER:
                resul = bb.addGap(xgap, 0).getLowerRight();
                break;
            case LOWER_AND_ALIGNED_RIGHT:
                resul = bb.addGap(0, ygap).getLowerRight();
                break;
            case UPPER_AND_ALIGNED_RIGHT:
                resul = bb.addGap(0, ygap).getUpperRight();
                break;
            case LOWER_AND_ALIGNED_LEFT:
                resul = bb.addGap(0, ygap).getLowerLeft();
                break;
            case UPPER_AND_ALIGNED_LEFT:
                resul = bb.addGap(0, ygap).getUpperLeft();
                break;
            case DIAG1:
                resul = bb.addGap(xgap, ygap).getUpperRight();
                break;
            case DIAG2:
                resul = bb.addGap(xgap, ygap).getUpperLeft();
                break;
            case DIAG3:
                resul = bb.addGap(xgap, ygap).getLowerLeft();
                break;
            case DIAG4:
                resul = bb.addGap(xgap, ygap).getLowerRight();
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
    public static AnchorType reverseAnchorPoint(AnchorType anchorPoint) {
        AnchorType resul = AnchorType.CENTER;// Default
        switch (anchorPoint) {
            case BY_POINT:
                resul = AnchorType.BY_POINT;
                break;
            case CENTER:
                resul = AnchorType.CENTER;
                break;
            case LEFT:
                resul = AnchorType.RIGHT;
                break;
            case RIGHT:
                resul = AnchorType.LEFT;
                break;
            case LOWER:
                resul = AnchorType.UPPER;
                break;
            case UPPER:
                resul = AnchorType.LOWER;
                break;

            case LEFT_AND_ALIGNED_UPPER:
                resul = AnchorType.RIGHT_AND_ALIGNED_UPPER;
                break;
            case RIGHT_AND_ALIGNED_UPPER:
                resul = AnchorType.LEFT_AND_ALIGNED_UPPER;
                break;
            case LEFT_AND_ALIGNED_LOWER:
                resul = AnchorType.RIGHT_AND_ALIGNED_LOWER;
                break;
            case RIGHT_AND_ALIGNED_LOWER:
                resul = AnchorType.LEFT_AND_ALIGNED_LOWER;
                break;
            case LOWER_AND_ALIGNED_RIGHT:
                resul = AnchorType.UPPER_AND_ALIGNED_RIGHT;
                break;
            case UPPER_AND_ALIGNED_RIGHT:
                resul = AnchorType.LOWER_AND_ALIGNED_RIGHT;
                break;
            case LOWER_AND_ALIGNED_LEFT:
                resul = AnchorType.UPPER_AND_ALIGNED_LEFT;
                break;
            case UPPER_AND_ALIGNED_LEFT:
                resul = AnchorType.LOWER_AND_ALIGNED_LEFT;
                break;
            case DIAG1:
                resul = AnchorType.DIAG3;
                break;
            case DIAG2:
                resul = AnchorType.DIAG4;
                break;
            case DIAG3:
                resul = AnchorType.DIAG1;
                break;
            case DIAG4:
                resul = AnchorType.DIAG2;
                break;
            case ZTOP:
                resul = AnchorType.ZBOTTOM;
                break;
            case ZBOTTOM:
                resul = AnchorType.ZTOP;
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
    public static Vec getScreenAnchorPoint(Camera camera, ScreenAnchor anchor, double xMargin, double yMargin) {
        if (camera == null) {
            //If not set, use default
            camera = JMathAnimConfig.getConfig().getCamera();
        }
        Vec resul = Vec.to(0,0);
        Vec gaps = camera.getGaps();
        Rect bb = camera.getMathView();
        Rect mathViewWithGap = bb.addGap(-xMargin - gaps.x, -yMargin - gaps.y);
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
            case UPPER_LEFT:
                resul = mathViewWithGap.getUpperLeft();
                break;
            case UPPER_RIGHT:
                resul = mathViewWithGap.getUpperRight();
                break;
            case LOWER_LEFT:
                resul = mathViewWithGap.getLowerLeft();
                break;
            case LOWER_RIGHT:
                resul = mathViewWithGap.getLowerRight();
                break;
        }
        return resul;
    }

}
