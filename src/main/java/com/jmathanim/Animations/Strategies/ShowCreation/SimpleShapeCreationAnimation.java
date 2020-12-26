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
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.Animation;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SimpleShapeCreationAnimation extends Animation {

    private final Shape mobj;
    private MultiShapeObject msh;
    private CanonicalJMPath canonPath;
    private int numberOfSegments;

    public SimpleShapeCreationAnimation(double runtime, Shape mobj) {
        super();
        this.runTime = runtime;
        this.mobj = mobj;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        canonPath = mobj.jmpath.canonicalForm();

        //Create multishape with all canonical components and a copy of drawing attributes
        //This will be drawed instead of mobj during the ShowCreation animation
        msh = canonPath.createMultiShape(this.mobj);
        for (int n = 0; n < msh.shapes.size(); n++) {
            msh.get(n).label = "msh" + n;
        }
        mobj.visible(false);
        scene.add(msh);
//        scene.add(mobj);

        doAnim(0);
        numberOfSegments = canonPath.getTotalNumberOfSegments();
    }

    @Override
    public void doAnim(double t) {
        double lt = lambda.applyAsDouble(t);
        if (lt == 1) {
            for (int n = 0; n < msh.shapes.size(); n++) {
                //Restore all paths because in each loop there will be modified
                msh.shapes.get(n).jmpath.clear();
                final JMPath path = canonPath.get(n);
                msh.shapes.get(n).jmpath.addJMPointsFrom(path);
            }
            return;
        }

        double po = lt * numberOfSegments;
        int k = (int) Math.floor(po); //Number of segment

        double alpha = po - k; //Alpha stores the 0-1 parameter inside the segment
        int[] pl = canonPath.getSegmentLocation(k);
        int pathNumber = pl[0];
        k = pl[1];
        for (int n = 0; n < msh.shapes.size(); n++) {
            //Restore all paths because in each loop there will be modified
            msh.shapes.get(n).jmpath.clear();
            final JMPath path = canonPath.get(n);
            msh.shapes.get(n).jmpath.addJMPointsFrom(path);

            if (n < pathNumber) {
                msh.shapes.get(n).visible(true);//Draw whole path
            }
            if (n > pathNumber) {
                msh.shapes.get(n).visible(true);//Still don't draw
            }
            if (n == pathNumber) {//This path should be drawn partly
                msh.shapes.get(n).visible(true);
                //k=point in this path, and alpha 0-1 relative position between k-1 and k
                final double alphaInThisPath = (k + alpha) / (msh.shapes.get(n).jmpath.size() - 1);
                JMPath subpath = canonPath.subpath(n, alphaInThisPath);
                msh.shapes.get(n).jmpath.clear();
                msh.shapes.get(n).jmpath.addJMPointsFrom(subpath);
            }
        }
    }

    @Override
    public void finishAnimation() {
         super.finishAnimation();
        doAnim(1);
        this.scene.remove(msh);
        mobj.visible(true);
        scene.add(mobj);
    }

}
