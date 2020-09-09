/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathMathObject;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Segment;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Transform extends Animation {

    public static final int METHOD_INTERPOLATE_POINT_BY_POINT = 1;
    public static final int METHOD_AFFINE_TRANSFORM = 2;
    private JMPath jmpathOrig;
    private final JMPathMathObject mobj2;
    private final JMPathMathObject mobj1;
    private MathObjectDrawingProperties propBase;
    private int method;
    public boolean shouldOptimizePathsFirst;
    public boolean forceChangeDirection;

    public Transform(JMPathMathObject ob1, JMPathMathObject ob2, double runTime) {
        super(runTime);
        mobj1 = ob1;
        mobj2 = ob2;
        method = METHOD_INTERPOLATE_POINT_BY_POINT;//Default method
        shouldOptimizePathsFirst = true;
        forceChangeDirection=false;
        determineTransformMethod();
    }

    @Override
    public void initialize() {
        //Determine optimal transformation

        //Should use an homotopy instead of point-to-point interpolation 
        //in the following cases:
        //2 segments/lines or segment/line
        //2 circles/ellipses
        //2 regular polygons with same number of sides
        

        //This is the initialization for the point-to-point interpolation
        //Prepare paths. Firs, I ensure they have the same number of points
        mobj1.jmpath.alignPaths(mobj2.jmpath);

        if (shouldOptimizePathsFirst) {
            //Now, adjust the points of the first to minimize distance from point-to-point
            mobj1.jmpath.minimizeSumDistance(mobj2.jmpath,forceChangeDirection);
        }
        //Base path and properties, to interpolate from
        jmpathOrig = mobj1.jmpath.rawCopy();
        //This copy of ob1 is necessary to compute interpolations between base and destiny
        propBase = mobj1.mp.copy();
        for (int n = 0; n < mobj1.jmpath.jmPathPoints.size(); n++) {
            //Mark all point temporary as curved
            mobj1.jmpath.jmPathPoints.get(n).isCurved = true;
        }
    }

    private void determineTransformMethod() {
        if ((mobj1 instanceof Segment) && (mobj2 instanceof Segment)) {
            method = METHOD_AFFINE_TRANSFORM;
            shouldOptimizePathsFirst = true;
        }

    }

    @Override
    public void doAnim(double t) {
        System.out.println("Anim Transform " + t);
        switch (method) {
            case METHOD_INTERPOLATE_POINT_BY_POINT:
                interpolateByPoint(t);
                break;
            case METHOD_AFFINE_TRANSFORM:
                affineTransform(t);
                break;
        }
    }

    private void interpolateByPoint(double t) throws ArrayIndexOutOfBoundsException {
        JMPathPoint interPoint, basePoint, dstPoint;
        for (int n = 0; n < mobj1.jmpath.jmPathPoints.size(); n++) {
            interPoint = mobj1.jmpath.jmPathPoints.get(n);
            basePoint = jmpathOrig.jmPathPoints.get(n);
            dstPoint = mobj2.jmpath.jmPathPoints.get(n);

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
    }

    @Override
    public void finishAnimation() {
        //Here it should remove unnecessary points
        //First mark as vertex points all mobj1 points who match with vertex from obj2
        //also copy backup values of control points 
        for (int n = 0; n < mobj1.jmpath.size(); n++) {
            JMPathPoint p1 = mobj1.jmpath.getPoint(n);
            JMPathPoint p2 = mobj2.jmpath.getPoint(n);
            p1.type = p2.type;
            p1.isCurved = p2.isCurved;
            p1.isVisible = p2.isVisible;
            p1.cp1vBackup = p2.cp1vBackup;
            p1.cp2vBackup = p2.cp2vBackup;
        }
        //Now I should remove all interpolation auxilary points
        mobj1.removeInterpolationPoints();
        System.out.println(mobj1);
        mobj2.removeInterpolationPoints();
    }

    private void affineTransform(double t) {
        JMPathPoint interPoint, basePoint, dstPoint;
        Point A = jmpathOrig.getPoint(0).p;
        Point B = jmpathOrig.getPoint(1).p;
        Point C = mobj2.getPoint(0).p;
        Point D = mobj2.getPoint(1).p;

        AffineTransform tr = AffineTransform.createDirect2DHomotopy(A, B, C, D, t);
        for (int n = 0; n < mobj1.jmpath.jmPathPoints.size(); n++) {
            interPoint = mobj1.jmpath.jmPathPoints.get(n);
            basePoint = jmpathOrig.jmPathPoints.get(n);
            dstPoint = mobj2.jmpath.jmPathPoints.get(n);

            //Copy visibility attributes after t>.8
            if (t > .8) {
                interPoint.isVisible = dstPoint.isVisible;
            }

            //Interpolate point
            interPoint.p.v = tr.getTransformedPoint(basePoint.p).v;
            
            //Interpolate control point 1
            interPoint.cp1.v = tr.getTransformedPoint(basePoint.cp1).v;

            //Interpolate control point 2
            interPoint.cp2.v = tr.getTransformedPoint(basePoint.cp2).v;

        }
        //Now interpolate properties from objects
        mobj1.mp.interpolateFrom(propBase, mobj2.mp, t);
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
    
     @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mobj1);
    }

}
