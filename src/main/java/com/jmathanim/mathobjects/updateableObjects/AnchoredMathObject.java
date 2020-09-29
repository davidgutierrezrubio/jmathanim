/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;
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
        this(mobj, refPoint, dstObject, Anchor.BY_CENTER);
    }

    public AnchoredMathObject(MathObject mobj, int method, Point dstPoint) {
        this(mobj, method, dstPoint, Anchor.BY_POINT);

    }

    public AnchoredMathObject(MathObject mobj, Point refPoint, MathObject dstPoint, int methodTo) {
        this.mobj = mobj;
        this.refPoint = refPoint;
        this.dstObject = dstPoint;
        anchorMethodFrom = Anchor.BY_POINT;
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

        Point dst = Anchor.getAnchorPoint(dstObject, anchorMethodTo);

        Point src = new Point();
        if (anchorMethodFrom == Anchor.BY_POINT) {
            src = refPoint;
        } else {
            src = Anchor.getAnchorPoint(mobj, anchorMethodFrom);
        }
        Vec v = src.to(dst);

        mobj.shift(v);
    }

//    public Point getAnchorFromObject(MathObject obj, int anchor) {
//        Point resul = new Point();
//        switch (anchor) {
//            case Anchor.BY_POINT:
//                resul = (Point) obj;
//                break;
//            case Anchor.BY_CENTER:
//                resul = obj.getCenter();
//                break;
//
//            case Anchor.LEFT:
//                resul = obj.getBoundingBox().getLeft();
//                break;
//            case Anchor.RIGHT:
//                resul = obj.getBoundingBox().getRight();
//                break;
//            case Anchor.LOWER:
//                resul = obj.getBoundingBox().getLower();
//                break;
//            case Anchor.UPPER:
//                resul = obj.getBoundingBox().getUpper();
//                break;
//
//            case Anchor.UL:
//                resul = obj.getBoundingBox().getUL();
//                break;
//            case Anchor.UR:
//                resul = obj.getBoundingBox().getUR();
//                break;
//            case Anchor.DL:
//                resul = obj.getBoundingBox().getDL();
//                break;
//            case Anchor.DR:
//                resul = obj.getBoundingBox().getDR();
//                break;
//
//        }
//        return resul;
//    }
}