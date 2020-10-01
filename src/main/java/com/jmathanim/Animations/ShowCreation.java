/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShowCreation extends Animation {

    Shape mobj;
    CanonicalJMPath canonPath;
    private MultiShapeObject msh;
    private int numberOfSegments;

    public ShowCreation(Shape mobj) {
        super();
        this.mobj = mobj;
    }

    public ShowCreation(Shape mobj, double runtime) {
        super(runtime);
        this.mobj = mobj;
    }

    @Override
    public void initialize() {
        canonPath = mobj.jmpath.canonicalForm();
        //Create multishape with all canonical components and a copy of drawing attributes
        //This will be drawed instead of mobj during the ShowCreation animation
        msh = canonPath.createMultiShape(this.mobj.mp);
        mobj.visible = false;
        this.scene.add(msh);
        numberOfSegments = canonPath.getTotalNumberOfSegments();

    }

    @Override
    public void doAnim(double t) {
        double po = t * numberOfSegments;
        int k = (int) Math.floor(po); //Number of segment

        double alpha = po - k; //Alpha stores the 0-1 parameter inside the segment
        int[] pl = canonPath.getSegmentLocation(k);
        int pathNumber = pl[0];
        k = pl[1];
        for (int n = 0; n < msh.shapes.size(); n++) {
            //Restore all paths because in each loop there will be modified
            msh.shapes.get(n).jmpath.clear();
            final JMPath path = canonPath.get(n);
            msh.shapes.get(n).jmpath.addPointsFrom(path);

            if (n < pathNumber) {
                msh.shapes.get(n).visible = true;//Draw whole path
            }
            if (n > pathNumber) {
                msh.shapes.get(n).visible = false;//Still don't draw
            }
            if (n == pathNumber) {//This path should be drawn partly
                msh.shapes.get(n).visible = true;
                //k=point in this path, and alpha 0-1 relative position between k-1 and k
                final double alphaInThisPath = (k + alpha) / (msh.shapes.get(n).jmpath.size()-1);
                JMPath subpath = canonPath.subpath(n, alphaInThisPath);
                msh.shapes.get(n).jmpath.clear();
                msh.shapes.get(n).jmpath.addPointsFrom(subpath);
//                System.out.println("subpath lenght "+subpath.size());
            }

        }
    }

    @Override
    public void finishAnimation() {
        this.scene.remove(msh);
        this.mobj.visible = true;
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mobj);
    }
}
