/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Anchor {
    //Anchor types

    public static final int BY_POINT = 1;
    public static final int BY_CENTER = 2;

    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    public static final int UPPER = 5;
    public static final int LOWER = 6;

    public static final int UL = 7;
    public static final int UR = 8;
    public static final int DL = 9;
    public static final int DR = 10;

    public static Point getAnchorPoint(MathObject obj, int anchor) {
        return getAnchorPoint(obj, anchor, 0, 0);
    }

    public static Point getAnchorPoint(MathObject obj, int anchor, double gap) {
        return getAnchorPoint(obj, anchor, gap, gap);
    }

    public static Point getAnchorPoint(MathObject obj, int anchor, double xgap, double ygap) {//TODO: This method should go in an Anchor class along with its constants
        Point resul = new Point();
        switch (anchor) {
            case BY_POINT:
                resul = obj.getAbsoluteAnchorPoint();
                break;
            case BY_CENTER:
                resul = obj.getCenter();
                break;

            case LEFT:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getLeft();
                break;
            case RIGHT:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getRight();
                break;
            case LOWER:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getLower();
                break;
            case UPPER:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getUpper();
                break;

            case UL:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getUL();
                break;
            case UR:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getUR();
                break;
            case DL:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getDL();
                break;
            case DR:
                resul = obj.getBoundingBox().addGap(xgap,ygap).getDR();
                break;

        }
        return resul;
    }
}
