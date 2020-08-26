/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathMathObject;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import javafx.scene.paint.Color;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Transform extends Animation {

    private final JMPath jmpathOrig;
    private final JMPathMathObject mobj2;
    private final JMPathMathObject mobj1;
    private final MathObjectDrawingProperties propBase;

    public Transform(JMPathMathObject ob1, JMPathMathObject ob2, double runTime) {
        super(ob1, runTime);
        mobj1 = ob1;
        mobj2 = ob2;
        //Prepare paths. Firs, I ensure they have the same number of points
        ob1.jmpath.alignPaths(mobj2.jmpath);
        //Now, adjust the points of the first to minimize distance from point-to-point
        ob1.jmpath.minimizeSquaredDistance(mobj2.jmpath);
        jmpathOrig = mobj1.jmpath.rawCopy();
        //If origin or destiny is CURVED, then the intermediate path is CURVED
//        if (ob1.jmpath.curveType == JMPath.CURVED || ob2.jmpath.curveType == JMPath.CURVED) {
//            mobj1.jmpath.curveType = JMPath.CURVED;
//        } else {
//            mobj1.jmpath.curveType = JMPath.STRAIGHT;
//        }//Should move this to the loop of points from the path
        //This copy of ob1 is necessary to compute interpolations between base and destiny
        propBase = ob1.mp.copy();
        //ob1=(1-t)*propBase+t*ob2
        for (int n = 0; n < mobj1.jmpath.points.size(); n++) {
            //Interpolate point
            JMPathPoint a = mobj1.jmpath.points.get(n);
            JMPathPoint b = mobj2.jmpath.points.get(n);
            if (b.isCurved) {
                a.isCurved = true;
            }
            a.isCurved = true;
        }
    }

    @Override
    public void doAnim(double t) {
        if (t >= 1) {
            t = 1;
            finishAnimation();
        } else {
            System.out.println("Anim Transform " + t);
            JMPathPoint interPoint, basePoint, dstPoint;
            for (int n = 0; n < mobj1.jmpath.points.size(); n++) {
                interPoint = mobj1.jmpath.points.get(n);
                basePoint = jmpathOrig.points.get(n);
                dstPoint = mobj2.jmpath.points.get(n);

                //Copy visibility attributes after t>.8
                if (t > .8) {
                    interPoint.isVisible = dstPoint.isVisible;
                }

                //Interpolate point
                interPoint.p.v.x = (1 - t) * basePoint.p.v.x + t * dstPoint.p.v.x;
                interPoint.p.v.y = (1 - t) * basePoint.p.v.y + t * dstPoint.p.v.y;
                interPoint.p.v.z = (1 - t) * basePoint.p.v.z + t * dstPoint.p.v.z;

                //Interpolate control point 1
                interPoint.cp1.v.x = (1 - t) * basePoint.cp1.v.x + t * dstPoint.cp1.v.x;
                interPoint.cp1.v.y = (1 - t) * basePoint.cp1.v.y + t * dstPoint.cp1.v.y;
                interPoint.cp1.v.z = (1 - t) * basePoint.cp1.v.z + t * dstPoint.cp1.v.z;

                //Interpolate control point 2
                interPoint.cp2.v.x = (1 - t) * basePoint.cp2.v.x + t * dstPoint.cp2.v.x;
                interPoint.cp2.v.y = (1 - t) * basePoint.cp2.v.y + t * dstPoint.cp2.v.y;
                interPoint.cp2.v.z = (1 - t) * basePoint.cp2.v.z + t * dstPoint.cp2.v.z;

            }
            //Now interpolate properties from objects
            mobj1.mp.interpolateFrom(propBase, mobj2.mp, t);
            //Update center from mobj1
            mobj1.updateCenter();
        }
    }

    private void finishAnimation() {
        //Here it should remove unnecessary points
        //First mark as vertex points all mobj1 points who match with vertex from obj2
        for (int n = 0; n < mobj1.jmpath.size(); n++) {
            mobj1.jmpath.getPoint(n).type = mobj2.jmpath.getPoint(n).type;
            mobj1.jmpath.getPoint(n).isCurved = mobj2.jmpath.getPoint(n).isCurved;
            mobj1.jmpath.getPoint(n).isVisible = mobj2.jmpath.getPoint(n).isVisible;
        }
        //Now I should remove all interpolation auxilary points
        mobj1.removeInterpolationPoints();
        mobj2.removeInterpolationPoints();
    }

}
