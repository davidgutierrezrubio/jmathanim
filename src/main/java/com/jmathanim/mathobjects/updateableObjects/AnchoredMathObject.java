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

package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Anchor.Type;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AnchoredMathObject implements Updateable {

   

    private MathObject mobj;
    private Point refPoint;
    private MathObject dstObject;
    private Type anchorMethodFrom;
    private Type anchorMethodTo;
    private double gap;

    public AnchoredMathObject(MathObject mobj, Point refPoint, MathObject dstObject) {
        this(mobj, refPoint, dstObject, Type.BY_CENTER);
    }

    public AnchoredMathObject(MathObject mobj, Type method, Point dstPoint) {
        this(mobj, method, dstPoint, Type.BY_POINT);

    }

    public AnchoredMathObject(MathObject mobj, Point refPoint, MathObject dstPoint, Type methodTo) {
        this.mobj = mobj;
        this.refPoint = refPoint;
        this.dstObject = dstPoint;
        anchorMethodFrom = Type.BY_POINT;
        anchorMethodTo = methodTo;
    }

    public AnchoredMathObject(MathObject mobj, Type methodFrom, MathObject dstPoint, Type methodTo) {
        this.mobj = mobj;
        this.dstObject = dstPoint;
        this.refPoint = new Point();//This point is not used, just for computing update level easily
        anchorMethodFrom = methodFrom;
        anchorMethodTo = methodTo;
    }

    public double getGap() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(Math.max(this.mobj.getUpdateLevel(), refPoint.getUpdateLevel()), dstObject.getUpdateLevel())+1;
    }

    @Override
    public void update(JMathAnimScene scene) {

        Point dst = Anchor.getAnchorPoint(dstObject, anchorMethodTo);

        Point src = new Point();
        if (anchorMethodFrom == Type.BY_POINT) {
            src = refPoint;
        } else {
            src = Anchor.getAnchorPoint(mobj, anchorMethodFrom,gap);
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
