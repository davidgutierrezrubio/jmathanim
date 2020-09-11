/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Segment;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Transform extends Animation {

    public static final int METHOD_INTERPOLATE_POINT_BY_POINT = 1;
    public static final int METHOD_AFFINE_TRANSFORM = 2;
    private JMPath jmpathOrig, jmpathDstBackup;
    private final Shape mobjDestiny;
    private final Shape mobjTransformed;
    private MathObjectDrawingProperties propBase;
    private int method;
    public boolean shouldOptimizePathsFirst;
    public boolean forceChangeDirection;
    private boolean isFinished;

    public Transform(Shape ob1, Shape ob2, double runTime) {
        super(runTime);
        mobjTransformed = ob1;
        mobjDestiny = ob2;
        jmpathDstBackup = ob2.jmpath.copy();
        method = METHOD_INTERPOLATE_POINT_BY_POINT;//Default method
        shouldOptimizePathsFirst = true;
        forceChangeDirection = false;
        determineTransformMethod();
        isFinished=false;
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
        alignPaths(mobjTransformed.jmpath,mobjDestiny.jmpath);

        if (shouldOptimizePathsFirst) {
            //Now, adjust the points of the first to minimize distance from point-to-point
            minimizeSumDistance(mobjTransformed.jmpath,mobjDestiny.jmpath, forceChangeDirection);
        }
        //Base path and properties, to interpolate from
        jmpathOrig = mobjTransformed.jmpath.rawCopy();
        //This copy of ob1 is necessary to compute interpolations between base and destiny
        propBase = mobjTransformed.mp.copy();
        for (int n = 0; n < mobjTransformed.jmpath.jmPathPoints.size(); n++) {
            //Mark all point temporary as curved
            mobjTransformed.jmpath.jmPathPoints.get(n).isCurved = true;
        }
    }
 /**
     * Cycles the point of closed path (and inverts its orientation if
     * necessary) in order to minimize the sum of squared distances from the
     * points of two paths with the same number of nodes
     *
     * @param path2
     */
    public void minimizeSumDistance(JMPath path1,JMPath path2, boolean forceChangeDirection) {
        ArrayList<Double> distances = new ArrayList<Double>();
        double minSumDistances = 999999999;
        int optimalStep = 0;
        //this variable is negative if both paths have different orientation
        //so the transformed path reverses itself to better adjust
        int changeDirection = path1.getOrientation() * path2.getOrientation();
        changeDirection = (forceChangeDirection ? -changeDirection : changeDirection);
        //If the path is open, we can't cycle the path, so 
        //we set numberOfCycles to 1
        int numberOfCycles = (path1.isClosed ? path1.size() : 1);
        //First, without changing direction
        for (int step = 0; step < numberOfCycles; step++) {
            JMPath tempPath = path1.copy();
            tempPath.cyclePoints(step, changeDirection);
            double sumDistances = tempPath.sumDistance(path2);
            distances.add(sumDistances);
            if (sumDistances < minSumDistances) {
                minSumDistances = sumDistances;
                optimalStep = step;
            }

        }
        path1.cyclePoints(optimalStep, changeDirection);
    }
    private void determineTransformMethod() {
        if ((mobjTransformed instanceof Segment) && (mobjDestiny instanceof Segment)) {
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
        for (int n = 0; n < mobjTransformed.jmpath.jmPathPoints.size(); n++) {
            interPoint = mobjTransformed.jmpath.jmPathPoints.get(n);
            basePoint = jmpathOrig.jmPathPoints.get(n);
            dstPoint = mobjDestiny.jmpath.jmPathPoints.get(n);

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
        mobjTransformed.mp.interpolateFrom(propBase, mobjDestiny.mp, t);
    }

    @Override
    public void finishAnimation() {
        if (isFinished) {
            return;
        }else
            isFinished=true;
        //TODO: If mobjTransformed is open and mobjDestiny,should fix that
        //TODO: Works, but needs to be refined
        if (!mobjTransformed.jmpath.isClosed && mobjDestiny.jmpath.isClosed) {
            mobjTransformed.jmpath.jmPathPoints.remove(-1);
            mobjTransformed.jmpath.jmPathPoints.get(0).isVisible=true;
        }

//Here it should remove unnecessary points
        //First mark as vertex points all mobj1 points who match with vertex from obj2
        //also copy backup values of control points 
        {
            for (int n = 0; n < mobjTransformed.jmpath.size(); n++) {
                JMPathPoint p1 = mobjTransformed.jmpath.getPoint(n);
                JMPathPoint p2 = mobjDestiny.jmpath.getPoint(n);
                p1.type = p2.type;
                p1.isCurved = p2.isCurved;
                p1.isVisible = p2.isVisible;
                p1.cp1vBackup = p2.cp1vBackup;
                p1.cp2vBackup = p2.cp2vBackup;
            }
        }
        //Now I should remove all interpolation auxilary points
        mobjTransformed.removeInterpolationPoints();
        System.out.println(mobjTransformed);
        mobjDestiny.removeInterpolationPoints();
        mobjTransformed.jmpath.isClosed=mobjDestiny.jmpath.isClosed;
    }

    private void affineTransform(double t) {
        JMPathPoint interPoint, basePoint, dstPoint;
        Point A = jmpathOrig.getPoint(0).p;
        Point B = jmpathOrig.getPoint(1).p;
        Point C = mobjDestiny.getJMPoint(0).p;
        Point D = mobjDestiny.getJMPoint(1).p;

        AffineTransform tr = AffineTransform.createDirect2DHomotopy(A, B, C, D, t);
        for (int n = 0; n < mobjTransformed.jmpath.jmPathPoints.size(); n++) {
            interPoint = mobjTransformed.jmpath.jmPathPoints.get(n);
            basePoint = jmpathOrig.jmPathPoints.get(n);
            dstPoint = mobjDestiny.jmpath.jmPathPoints.get(n);

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
        mobjTransformed.mp.interpolateFrom(propBase, mobjDestiny.mp, t);
    }

     /**
     * Align the number of points of this path with the given one. Align the
     * paths so that they have the same number of points, interpolating the
     * smaller one if necessary.
     *
     * @param path2
     */
    public void alignPaths(JMPath path1,JMPath path2) {//TODO: Move this to Transform
        //TODO: What about open paths?
        JMPath pathSmall;
        JMPath pathBig;
        
        
        //If path1 is closed but path2 is not, open path1, duplicating first vertex 
        if (path1.isClosed && !path2.isClosed)
        {
            path1.addPoint(path1.getPoint(0).copy());
            path1.getPoint(0).isVisible=false;
            path1.getPoint(-1).type=JMPathPoint.TYPE_INTERPOLATION_POINT;
            path1.isClosed=false;
        }
        
//         if (path2.isClosed && !this.isClosed)
//        {
//            path2.addPoint(path2.getPoint(0).copy());
//            path2.getPoint(0).isVisible=false;
//        }
        
        if (path1.size() == path2.size()) {
            return;
        }

        if (path1.size() < path2.size()) {
            pathSmall = path1;
            pathBig = path2;
        } else {
            pathBig = path1;
            pathSmall = path2;
        }

        //At this point pathSmall points to the smaller path who is going to be
        //interpolated
        int nSmall = (pathSmall.isClosed ? pathSmall.size():pathSmall.size()-1);
        int nBig = (pathBig.isClosed ? pathBig.size():pathBig.size()-1);
//        int nBig = pathBig.size();

        JMPath resul = new JMPath();

        int numDivs = (nBig / nSmall); //Euclidean quotient
        int rest = nBig % nSmall;//Euclidean rest
        int numDivForThisVertex;
        for (int n = 0; n < nSmall; n++) {
//                int k = (n + 1) % points.size(); //Next point, first if curve is closed
            JMPathPoint v1 = pathSmall.getPoint(n);
            JMPathPoint v2 = pathSmall.getPoint(n + 1);
            v1.type = JMPathPoint.TYPE_VERTEX;
            resul.addPoint(v1); //Add the point of original curve
            numDivForThisVertex = numDivs; //number of segments to divide, NOT number of intermediate new points
            if (n < rest) { //The <rest> first vertex have an extra interpolation point (should distribute these along the path? maybe...)
                numDivForThisVertex += 1;
            }
            dividePathSegment(v1, v2, numDivForThisVertex, resul);
        }
        if (!pathSmall.isClosed) resul.addPoint(pathSmall.getPoint(-1));
        pathSmall.clear();
        pathSmall.addPointsFrom(resul);
//        pathSmall.generateControlPoints();//Not necessary
    }

    /**
     * Divide path into an equal number of parts. Stores new points in a new
     * path
     *
     * @param v1 Starting point
     * @param v2 Ending point
     * @param numDivForThisVertex Number of subdivisions
     * @param resul Path with new points to store
     */
    private void dividePathSegment(JMPathPoint v1, JMPathPoint v2, int numDivForThisVertex, JMPath resul) {
        JMPathPoint interpolate;
        if (numDivForThisVertex < 2) {
            return;
        }
        double alpha = 1.0d / numDivForThisVertex;
        interpolate = JMPath.interpolateBetweenTwoPoints(v1, v2, alpha);
        resul.addPoint(interpolate);
        if (numDivForThisVertex > 2) {
            dividePathSegment(interpolate, v2, numDivForThisVertex - 1, resul);
        }
    }
    
    
    
    
    
    
    
    
    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mobjTransformed);
    }

}
