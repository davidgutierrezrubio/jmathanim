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
                a.isCurved=true;
            }
            
        }
    }

    @Override
    public void doAnim(double t) {
        if (t >= 1) {
            t = 1;
            finishAnimation();
        }else
        {
        System.out.println("Anim Transform " + t);
        Point p, a, b;
        for (int n = 0; n < mobj1.jmpath.points.size(); n++) {
            //Interpolate point
            JMPathPoint puntoMierda=mobj1.jmpath.points.get(n);
            p = mobj1.jmpath.points.get(n).p;
            a = jmpathOrig.points.get(n).p;
            b = mobj2.jmpath.points.get(n).p;

            p.v.x = (1 - t) * a.v.x + t * b.v.x;
            p.v.y = (1 - t) * a.v.y + t * b.v.y;
            p.v.z = (1 - t) * a.v.z + t * b.v.z;

            //Interpolate control point 1
            p = mobj1.jmpath.getPoint(n).cp1;
            a = jmpathOrig.getPoint(n).cp1;
            b = mobj2.jmpath.getPoint(n).cp1;

            p.v.x = (1 - t) * a.v.x + t * b.v.x;
            p.v.y = (1 - t) * a.v.y + t * b.v.y;
            p.v.z = (1 - t) * a.v.z + t * b.v.z;

            //Interpolate control point 2
            p = mobj1.jmpath.getPoint(n).cp2;
            a = jmpathOrig.getPoint(n).cp2;
            b = mobj2.jmpath.getPoint(n).cp2;
            
            p.v.x = (1 - t) * a.v.x + t * b.v.x;
            p.v.y = (1 - t) * a.v.y + t * b.v.y;
            p.v.z = (1 - t) * a.v.z + t * b.v.z;
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
        for (int n=0;n<mobj1.jmpath.size();n++)
        {
               mobj1.jmpath.getPoint(n).type=mobj2.jmpath.getPoint(n).type;
        }
        //Now I should remove all interpolation auxilary points
        mobj1.removeInterpolationPoints();
//        mobj2.removeInterpolationPoints();
    }

}
