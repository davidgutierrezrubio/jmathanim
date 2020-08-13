/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathMathObject;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Transform extends Animation {

    private final JMPath jmpathOrig;
    private final JMPathMathObject mobj2;
    private final JMPathMathObject mobj1;

    public Transform(JMPathMathObject ob1, JMPathMathObject ob2, double runTime) {
        super(ob1, runTime);
        mobj1 = ob1;
        mobj2 = ob2;
        ob1.jmpath.alignPaths(mobj2.jmpath);
        ob1.jmpath.minimizeDistanceVariance(mobj2.jmpath);
        jmpathOrig = mobj1.jmpath.rawCopy();
    }

    @Override
    public void doAnim(double t) {
        if (t > 1) {
            t = 1;
        }
        System.out.println("Anim Transform " + t);
        for (int n = 0; n < mobj1.jmpath.points.size(); n++) {
            Point p = mobj1.jmpath.points.get(n);
            Point a = jmpathOrig.points.get(n);
            Point b = mobj2.jmpath.points.get(n);

            p.v.x = (1 - t) * a.v.x + t * b.v.x;
            p.v.y = (1 - t) * a.v.y + t * b.v.y;
            p.v.z = (1 - t) * a.v.z + t * b.v.z;
        }
        mobj1.jmpath.computeControlPoints();

    }

}
