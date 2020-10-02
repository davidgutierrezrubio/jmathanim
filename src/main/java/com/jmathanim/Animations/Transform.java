/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.TransformStrategies.PointInterpolationCanonical;
import com.jmathanim.Animations.TransformStrategies.AffineStrategyTransform;
import com.jmathanim.Animations.TransformStrategies.HomotopyStrategyTransform;
import com.jmathanim.Animations.TransformStrategies.RotateAndScaleXYStrategyTransform;
import com.jmathanim.Animations.TransformStrategies.TransformStrategy;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.MathObject;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Transform extends Animation {

    public static final int METHOD_INTERPOLATE_POINT_BY_POINT = 1;
    public static final int METHOD_HOMOTOPY_TRANSFORM = 2;
    public static final int METHOD_AFFINE_TRANSFORM = 3;
    public static final int METHOD_ROTATE_AND_SCALEXY_TRANSFORM = 4;
    private JMPath jmpathOrig, jmpathDstBackup;
    public final Shape mobjDestiny;
    public final Shape mobjTransformed;
    private MathObjectDrawingProperties propBase;
    private Integer method;
    public boolean shouldOptimizePathsFirst;
    public boolean forceChangeDirection;
    private boolean isFinished;
    private JMathAnimScene scene;
    private TransformStrategy strategy;
    

    public Transform(Shape ob1, Shape ob2, double runTime) {
        super(runTime);
        mobjTransformed = ob1;
        mobjDestiny = ob2.copy();
        method = null;
        shouldOptimizePathsFirst = true;
        forceChangeDirection = false;
        isFinished = false;
    }

    @Override
    public void initialize() {
        //Determine optimal transformation

        //Should use an homotopy instead of point-to-point interpolation 
        //in the following cases:
        //2 segments/lines or segment/line
        //2 circles/ellipses
        //2 regular polygons with same number of sides
        if (method == null) {
            determineTransformMethod();
        }
        createStrategy();

        //Variable strategy should have proper strategy to transform
        //If method is null means that user didn't force one
        strategy.prepareObjects(mobjTransformed, mobjDestiny);
    }

    /**
     * Cycles the point of closed path (and inverts its orientation if
     * necessary) in order to minimize the sum of squared distances from the
     * points of two paths with the same number of nodes
     *
     * @param path1
     * @param path2
     * @param forceChangeDirection
     */
    public void minimizeSumDistance(JMPath path1, JMPath path2, boolean forceChangeDirection) {
        ArrayList<Double> distances = new ArrayList<Double>();
        double minSumDistances = 999999999;
        int optimalStep = 0;
        //this variable is negative if both paths have different orientation
        //so the transformed path reverses itself to better adjust
        int changeDirection = path1.getOrientation() * path2.getOrientation();
        changeDirection = (forceChangeDirection ? -changeDirection : changeDirection);
        //If the path is open, we can't cycle the path, so 
        //we set numberOfCycles to 1
        int numberOfCycles = path1.size();//(path1.isClosed ? path1.size() : 1);
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
        String methodTextOutput = "Transform method: By point";

        //Segment & Segment
        method = METHOD_INTERPOLATE_POINT_BY_POINT;//Default method if not specified
        if ((mobjTransformed.getObjectType() == MathObject.SEGMENT) && (mobjDestiny.getObjectType() == MathObject.SEGMENT)) {
            method = METHOD_HOMOTOPY_TRANSFORM;
            shouldOptimizePathsFirst = true;
            methodTextOutput = "Transform method: Homotopy";
        }

        //Circle & Circle
        if ((mobjTransformed.getObjectType() == MathObject.CIRCLE) && (mobjDestiny.getObjectType() == MathObject.CIRCLE)) {
            method = METHOD_HOMOTOPY_TRANSFORM;
            shouldOptimizePathsFirst = true;
            methodTextOutput = "Transform method: Homotopy";
        }

        //Rectangle & Rectangle
        if ((mobjTransformed.getObjectType() == MathObject.RECTANGLE) && (mobjDestiny.getObjectType() == MathObject.RECTANGLE)) {
            method = METHOD_ROTATE_AND_SCALEXY_TRANSFORM;
            shouldOptimizePathsFirst = true;
            methodTextOutput = "Transform method: Rotate and Scale XY";
        }

        //Regular Polygons with the same number of vertices
        if ((mobjTransformed.getObjectType() == MathObject.REGULAR_POLYGON) && (mobjDestiny.getObjectType() == MathObject.REGULAR_POLYGON)) {
            if (mobjTransformed.jmpath.size() == mobjDestiny.jmpath.size()) {
                method = METHOD_ROTATE_AND_SCALEXY_TRANSFORM;
                shouldOptimizePathsFirst = true;
                methodTextOutput = "Transform method: Rotate and Scale XY";
            } else {
                method = METHOD_INTERPOLATE_POINT_BY_POINT;
                 methodTextOutput = "Transform method: By point";
            }

        }

        System.out.println(methodTextOutput);

    }

    private void createStrategy() {
        //Now I choose strategy
        switch (method) {
            case METHOD_INTERPOLATE_POINT_BY_POINT:
                strategy = new PointInterpolationCanonical();
                break;
            case METHOD_HOMOTOPY_TRANSFORM:
                strategy = new HomotopyStrategyTransform();
                break;
            case METHOD_AFFINE_TRANSFORM:
                strategy = new AffineStrategyTransform();
                break;
            case METHOD_ROTATE_AND_SCALEXY_TRANSFORM:
                strategy = new RotateAndScaleXYStrategyTransform();
                break;
        }
    }

    @Override
    public void doAnim(double t) {
        strategy.applyTransform(t);

    }

    @Override
    public void finishAnimation() {
        if (isFinished) {
            return;
        } else {
            isFinished = true;
        }
        strategy.finish();
        //Copy type of destiny object to transformed one
        mobjTransformed.setObjectType(mobjDestiny.getObjectType());
    }

    /*
     * Align the number of points of this path with the given one.Align the
 paths so that they have the same number of points, interpolating the
 smaller one if necessary.
     *
//    public void alignPaths(JMPath path1, JMPath path2) {//TODO: Move this to Transform
//        //TODO: What about open paths?
//        JMPath pathSmall;
//        JMPath pathBig;
//
//        //If path1 is closed but path2 is not, open path1, duplicating first vertex 
//        if (path1.isClosed && !path2.isClosed) {
//            path1.addJMPoint(path1.getPoint(0).copy());
//            path1.getPoint(0).isVisible = false;
//            path1.getPoint(-1).type = JMPathPoint.TYPE_INTERPOLATION_POINT;
//            path1.isClosed = false;
//        }
//
////         if (path2.isClosed && !this.isClosed)
////        {
////            path2.addPoint(path2.getPoint(0).copy());
////            path2.getPoint(0).isVisible=false;
////        }
//        if (path1.size() == path2.size()) {
//            return;
//        }
//
//        if (path1.size() < path2.size()) {
//            pathSmall = path1;
//            pathBig = path2;
//        } else {
//            pathBig = path1;
//            pathSmall = path2;
//        }
//
//        //At this point pathSmall points to the smaller path who is going to be
//        //interpolated
//        int nSmall = (pathSmall.isClosed ? pathSmall.size() : pathSmall.size() - 1);
//        int nBig = (pathBig.isClosed ? pathBig.size() : pathBig.size() - 1);
////        int nBig = pathBig.size();
//
//        JMPath resul = new JMPath();
//
//        int numDivs = (nBig / nSmall); //Euclidean quotient
//        int rest = nBig % nSmall;//Euclidean rest
//        int numDivForThisVertex;
//        for (int n = 0; n < nSmall; n++) {
////                int k = (n + 1) % points.size(); //Next point, first if curve is closed
//            JMPathPoint v1 = pathSmall.getPoint(n);
//            JMPathPoint v2 = pathSmall.getPoint(n + 1);
//            v1.type = JMPathPoint.TYPE_VERTEX;
//            resul.addJMPoint(v1); //Add the point of original curve
//            numDivForThisVertex = numDivs; //number of segments to divide, NOT number of intermediate new points
//            if (n < rest) { //The <rest> first vertex have an extra interpolation point (should distribute these along the path? maybe...)
//                numDivForThisVertex += 1;
//            }
//            dividePathSegment(v1, v2, numDivForThisVertex, resul);
//        }
//        if (!pathSmall.isClosed) {
//            resul.addJMPoint(pathSmall.getPoint(-1));
//        }
//        pathSmall.clear();
//        pathSmall.addPointsFrom(resul);
////        pathSmall.generateControlPoints();//Not necessary
//    }
    */
    
    
    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }

}
