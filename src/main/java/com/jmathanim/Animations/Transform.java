/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathMathObject;
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
        ob1.jmpath.alignPaths(mobj2.jmpath);
        ob1.jmpath.minimizeDistanceVariance(mobj2.jmpath);
        jmpathOrig = mobj1.jmpath.rawCopy();
        //If origin or destiny is CURVED, then the intermediate path is CURVED
        if (ob1.jmpath.curveType == JMPath.CURVED || ob2.jmpath.curveType == JMPath.CURVED) {
            mobj1.jmpath.curveType = JMPath.CURVED;
        } else {
            mobj1.jmpath.curveType = JMPath.STRAIGHT;
        }
        propBase=ob1.mp.copy();
    }

    @Override
    public void doAnim(double t) {
        if (t > 1) {
            t = 1;
        }
        System.out.println("Anim Transform " + t);
        Point p, a, b;
        for (int n = 0; n < mobj1.jmpath.points.size(); n++) {
            //Interpolate point
            p = mobj1.jmpath.points.get(n);
            a = jmpathOrig.points.get(n);
            b = mobj2.jmpath.points.get(n);

            p.v.x = (1 - t) * a.v.x + t * b.v.x;
            p.v.y = (1 - t) * a.v.y + t * b.v.y;
            p.v.z = (1 - t) * a.v.z + t * b.v.z;

            //Interpolate control point 1
            p = mobj1.jmpath.controlPoints1.get(n);
            a = jmpathOrig.controlPoints1.get(n);
            b = mobj2.jmpath.controlPoints1.get(n);

            p.v.x = (1 - t) * a.v.x + t * b.v.x;
            p.v.y = (1 - t) * a.v.y + t * b.v.y;
            p.v.z = (1 - t) * a.v.z + t * b.v.z;

            //Interpolate control point 2
            p = mobj1.jmpath.controlPoints2.get(n);
            a = jmpathOrig.controlPoints2.get(n);
            b = mobj2.jmpath.controlPoints2.get(n);

            p.v.x = (1 - t) * a.v.x + t * b.v.x;
            p.v.y = (1 - t) * a.v.y + t * b.v.y;
            p.v.z = (1 - t) * a.v.z + t * b.v.z;
        }
        //Now interpolate properties from objects
        mobj1.mp.interpolateFrom(propBase, mobj2.mp, t);
    }

}
