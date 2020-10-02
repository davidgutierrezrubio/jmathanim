/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 * A class that manages relevant points of a {@link MathObject}
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Anchor {

    /**
     * Anchor is specified by a given point
     */
    public static final int BY_POINT = 1;
    /**
     * Anchor determined by the center of the object
     */
    public static final int BY_CENTER = 2;

    /**
     * Right anchor point. Vertically centered.
     */
    public static final int RIGHT = 3;
    /**
     * Upper anchor point. Horizontally centered
     */
    public static final int UPPER = 4;
    /**
     * Left anchor point. Vertically centered.
     */
    public static final int LEFT = 5;
    /**
     * Lower anchor point. Horizontally centered
     */
    public static final int LOWER = 6;

    /**
     * Down-Right anchor point
     */
    public static final int DR = 7;
    /**
     * Up-Right anchor point
     */
    public static final int UR = 8;
    /**
     * Up-Left anchor point
     */
    public static final int UL = 9;
    /**
     * Down-Left anchor point
     */
    public static final int DL = 10;

    /**
     * Return a {@link Point} object that represents the given anchor. For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box)
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor point null null null null null null null null     {@link BY_POINT}, {@link BY_CENTER}, {@link RIGHT}, {@link UPPER},
     * {@link LEFT}, {@link LOWER}, {@link DR}, {@link UR},
     * {@link UL}, {@link DL}
     * @return The anchor point
     */
    public static Point getAnchorPoint(MathObject obj, int anchor) {
        return getAnchorPoint(obj, anchor, 0, 0);
    }

    /**
     * Return a {@link Point} object that represents the given anchor.For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box).A gap (in math coordinates) is
     * added, equal in x and y direction.
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor point null null null null null null null
     * null null     {@link BY_POINT}, {@link BY_CENTER}, {@link RIGHT}, {@link UPPER},
     * {@link LEFT}, {@link LOWER}, {@link DR}, {@link UR},
     * {@link UL}, {@link DL}
     * @param gap Gap to add to the anchor
     * @return The anchor point
     */
    public static Point getAnchorPoint(MathObject obj, int anchor, double gap) {
        return getAnchorPoint(obj, anchor, gap, gap);
    }

    /**
     * Return a {@link Point} object that represents the given anchor.For
     * example getAnchorPoint(obj, Anchor.LEFT) returns the upper point of the
     * object (determined by its bounding box).A gap (in math coordinates) is
     * added, specified both by its x component and y component.
     *
     * @param obj Object to get anchor point
     * @param anchor Type of anchor point null null null null null null null null     {@link BY_POINT}, {@link BY_CENTER}, {@link RIGHT}, {@link UPPER},
     * {@link LEFT}, {@link LOWER}, {@link DR}, {@link UR},
     * {@link UL}, {@link DL}
     * @param xgap Horizontal gap
     * @param ygap Vertical gap
     * @return The anchor point
     */
    public static Point getAnchorPoint(MathObject obj, int anchor, double xgap, double ygap) {
        Point resul = new Point();
        switch (anchor) {
            case BY_POINT:
                resul = obj.getAbsoluteAnchorPoint();
                break;
            case BY_CENTER:
                resul = obj.getCenter();
                break;

            case LEFT:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getLeft();
                break;
            case RIGHT:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getRight();
                break;
            case LOWER:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getLower();
                break;
            case UPPER:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getUpper();
                break;

            case UL:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getUL();
                break;
            case UR:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getUR();
                break;
            case DL:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getDL();
                break;
            case DR:
                resul = obj.getBoundingBox().addGap(xgap, ygap).getDR();
                break;

        }
        return resul;
    }

    /**
     * Returns the reverse anchor (LEFT-RIGHT, UP-DOWN, etc.)
     *
     * @param anchorPoint Anchor to compute reverse
     * @return Reversed anchor
     */
    public static int reverseAnchorPoint(int anchorPoint) {
        int resul = BY_CENTER;//Default
        switch (anchorPoint) {
            case BY_POINT:
                resul = BY_POINT;
                break;
            case BY_CENTER:
                resul = BY_CENTER;
                break;

            case LEFT:
                resul = RIGHT;
                break;
            case RIGHT:
                resul = LEFT;
                break;
            case LOWER:
                resul = UPPER;
                break;
            case UPPER:
                resul = LOWER;
                break;

            case UL:
                resul = DR;
                break;
            case UR:
                resul = DL;
                break;
            case DL:
                resul = UR;
                break;
            case DR:
                resul = UL;
                break;
        }
        return resul;
    }

    public static Point getScreenAnchorPoint(int anchor, double xMargin, double yMargin) {
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
