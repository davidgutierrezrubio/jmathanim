/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_BY_CENTER;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_BY_POINT;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_DL;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_DR;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_LEFT;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_LOWER;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_RIGHT;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_UL;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_UPPER;
import static com.jmathanim.mathobjects.MathObject.ANCHOR_UR;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class AnchoredMathObject implements Updateable {

   

    private MathObject mobj;
    private Point refPoint;
    private MathObject dstObject;
    private int anchorMethodFrom;
    private int anchorMethodTo;

    public AnchoredMathObject(MathObject mobj, Point refPoint, MathObject dstObject) {
        this(mobj, refPoint, dstObject, ANCHOR_BY_CENTER);
    }

    public AnchoredMathObject(MathObject mobj, int method, Point dstPoint) {
        this(mobj, method, dstPoint, ANCHOR_BY_POINT);

    }

    public AnchoredMathObject(MathObject mobj, Point refPoint, MathObject dstPoint, int methodTo) {
        this.mobj = mobj;
        this.refPoint = refPoint;
        this.dstObject = dstPoint;
        anchorMethodFrom = ANCHOR_BY_POINT;
        anchorMethodTo = methodTo;
    }

    public AnchoredMathObject(MathObject mobj, int methodFrom, MathObject dstPoint, int methodTo) {
        this.mobj = mobj;
        this.dstObject = dstPoint;
        this.refPoint = new Point();//This point is not used, just for computing update level easily
        anchorMethodFrom = methodFrom;
        anchorMethodTo = methodTo;
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(Math.max(this.mobj.getUpdateLevel(), refPoint.getUpdateLevel()), dstObject.getUpdateLevel())+1;
    }

    @Override
    public void update() {

        Point dst = getAnchorFromObject(dstObject, anchorMethodTo);

        Point src = new Point();
        if (anchorMethodFrom == ANCHOR_BY_POINT) {
            src = refPoint;
        } else {
            src = getAnchorFromObject(mobj, anchorMethodFrom);
        }
        Vec v = src.to(dst);

        mobj.shift(v);
    }

    public Point getAnchorFromObject(MathObject obj, int anchor) {
        Point resul = new Point();
        switch (anchor) {
            case ANCHOR_BY_POINT:
                resul = (Point) obj;
                break;
            case ANCHOR_BY_CENTER:
                resul = obj.getCenter();
                break;

            case ANCHOR_LEFT:
                resul = obj.getBoundingBox().getLeft();
                break;
            case ANCHOR_RIGHT:
                resul = obj.getBoundingBox().getRight();
                break;
            case ANCHOR_LOWER:
                resul = obj.getBoundingBox().getLower();
                break;
            case ANCHOR_UPPER:
                resul = obj.getBoundingBox().getUpper();
                break;

            case ANCHOR_UL:
                resul = obj.getBoundingBox().getUL();
                break;
            case ANCHOR_UR:
                resul = obj.getBoundingBox().getUR();
                break;
            case ANCHOR_DL:
                resul = obj.getBoundingBox().getDL();
                break;
            case ANCHOR_DR:
                resul = obj.getBoundingBox().getDR();
                break;

        }
        return resul;
    }
}
