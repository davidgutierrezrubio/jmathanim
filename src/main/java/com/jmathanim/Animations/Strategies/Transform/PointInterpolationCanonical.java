/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 * Separates paths into canonical forms to interpolate point by point
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointInterpolationCanonical extends TransformStrategy {

    public CanonicalJMPath connectedOrigin, connectedDst, connectedOriginaRawCopy;
    private final ArrayList<Shape> addedAuxiliaryObjectsToScene;
    private Shape mobjTransformed;
    private Shape mobjDestiny;
    private Shape originalShapeBaseCopy;
    private static final boolean DEBUG_COLORS = false;

    public PointInterpolationCanonical(Shape mobjTransformed, Shape mobjDestiny,JMathAnimScene scene) {
        super(scene);
        this.mobjTransformed=mobjTransformed;
        this.mobjDestiny=mobjDestiny;
        this.addedAuxiliaryObjectsToScene = new ArrayList<>();
    }

    @Override
    public void prepareObjects() {
        //This is the initialization for the point-to-point interpolation
        //Prepare paths. Firs, I ensure they have the same number of points
        //and be in connected components form.
        
        //Remove consecutive hidden vertices, in case.
        this.mobjTransformed.getPath().removeConsecutiveHiddenVertices();
        this.mobjDestiny.getPath().removeConsecutiveHiddenVertices();
        
        originalShapeBaseCopy = mobjTransformed.copy();
        preparePaths(mobjTransformed.jmpath, mobjDestiny.jmpath);
        if (DEBUG_COLORS) {
            for (int n = 0; n < connectedOrigin.getNumberOfPaths(); n++) {
                JMColor color = mobjTransformed.mp.drawColor.copy();
                Shape sh = new Shape(connectedOrigin.get(n), null);
//            Shape sh2 = new Shape(connectedDst.get(n), null);

                sh.drawColor(JMColor.random());
                scene.add(sh);
                addedAuxiliaryObjectsToScene.add(sh);
            }

        }
        mobjTransformed.getPath().clear();
        mobjTransformed.getPath().addPointsFrom(connectedOrigin.toJMPath());
    }

    @Override
    public void applyTransform(double t,double lt) {
        JMPathPoint interPoint, basePoint, dstPoint;

        for (int numConnected = 0; numConnected < this.connectedDst.getNumberOfPaths(); numConnected++) {
            JMPath convertedPath = connectedOrigin.get(numConnected);
            JMPath fromPath = connectedOriginaRawCopy.get(numConnected);
            JMPath toPath = connectedDst.get(numConnected);
            for (int n = 0; n < convertedPath.size(); n++) {
                interPoint = convertedPath.getJMPoint(n);
                basePoint = fromPath.getJMPoint(n);
                dstPoint = toPath.getJMPoint(n);

                //Interpolate point
                interPoint.p.v.x = (1 - lt) * basePoint.p.v.x + lt * dstPoint.p.v.x;
                interPoint.p.v.y = (1 - lt) * basePoint.p.v.y + lt * dstPoint.p.v.y;
                interPoint.p.v.z = (1 - lt) * basePoint.p.v.z + lt * dstPoint.p.v.z;

                //Interpolate control point 1
                interPoint.cp1.v.x = (1 - lt) * basePoint.cp1.v.x + lt * dstPoint.cp1.v.x;
                interPoint.cp1.v.y = (1 - lt) * basePoint.cp1.v.y + lt * dstPoint.cp1.v.y;
                interPoint.cp1.v.z = (1 - lt) * basePoint.cp1.v.z + lt * dstPoint.cp1.v.z;

                //Interpolate control point 2
                interPoint.cp2.v.x = (1 - lt) * basePoint.cp2.v.x + lt * dstPoint.cp2.v.x;
                interPoint.cp2.v.y = (1 - lt) * basePoint.cp2.v.y + lt * dstPoint.cp2.v.y;
                interPoint.cp2.v.z = (1 - lt) * basePoint.cp2.v.z + lt * dstPoint.cp2.v.z;
            }
            //Now interpolate properties from objects

        }
        mobjTransformed.mp.interpolateFrom(originalShapeBaseCopy.mp, mobjDestiny.mp, t);
        

    }

    @Override
    public void finish() {
        for (int numConnected = 0; numConnected < this.connectedDst.getNumberOfPaths(); numConnected++) {
//        for (int numConnected = 0; numConnected < 1; numConnected++) {
            JMPath convertedPath = connectedOrigin.get(numConnected);
            JMPath toPath = connectedDst.get(numConnected);

            for (int n = 0; n < convertedPath.size(); n++) {
                JMPathPoint p1 = convertedPath.getJMPoint(n);
                JMPathPoint p2 = toPath.getJMPoint(n);
                p1.type = p2.type;
                p1.isCurved = p2.isCurved;
                p1.isThisSegmentVisible = p2.isThisSegmentVisible;
                p1.cp1vBackup = p2.cp1vBackup;
                p1.cp2vBackup = p2.cp2vBackup;
            }
        }
//        //Now I should remove all interpolation auxilary points
//        mobjTransformed.removeInterpolationPoints();
//        System.out.println(mobjTransformed);
//        mobjDestiny.removeInterpolationPoints();

        JMPath pa = connectedDst.toJMPath();
        pa.removeInterpolationPoints();
        mobjTransformed.jmpath.clear();
        mobjTransformed.jmpath.addPointsFrom(pa);
        mobjTransformed.mp.copyFrom(mobjDestiny.mp);
        mobjTransformed.absoluteSize=mobjDestiny.absoluteSize;
//        scene.add(mobjTransformed);
        for (Shape shapesToRemove : addedAuxiliaryObjectsToScene) {
            scene.remove(shapesToRemove);
        }
    }

    private void preparePaths(JMPath path1, JMPath path2) {

        connectedOrigin = path1.canonicalForm();
        connectedDst = path2.canonicalForm();
        alignNumberOfComponents(connectedOrigin, connectedDst);
        connectedOriginaRawCopy = new CanonicalJMPath();
        for (JMPath p : connectedOrigin.getPaths()) {
            connectedOriginaRawCopy.add(p.rawCopy());
        }
        //Mark all points as curved during the transform
        for (int numConnected = 0; numConnected < this.connectedDst.getNumberOfPaths(); numConnected++) {
//        for (int numConnected = 0; numConnected < 1; numConnected++) {
            JMPath convertedPath = connectedOrigin.get(numConnected);
            for (JMPathPoint p : convertedPath.jmPathPoints) {
                p.isCurved = true;
            }

        }

    }

    private void alignNumberOfElements(JMPath path1, JMPath path2) {
        JMPath pathSmall, pathBig;
        if (path1.size() < path2.size()) {
            pathSmall = path1;
            pathBig = path2;
        } else {
            pathBig = path1;
            pathSmall = path2;
        }
        pathSmall.alignPathsToGivenNumberOfElements(pathBig.size());
    }

    private void alignNumberOfComponents(CanonicalJMPath con1, CanonicalJMPath con2) {
        CanonicalJMPath conBig, conSmall;
        if (con1.getNumberOfPaths() < con2.getNumberOfPaths()) {
            conSmall = con1;
            conBig = con2;
        } else {
            conBig = con1;
            conSmall = con2;
        }

        int numDivs = (conBig.getNumberOfPaths() / conSmall.getNumberOfPaths()); //Euclidean quotient
        int rest = conBig.getNumberOfPaths() % conSmall.getNumberOfPaths();//Euclidean rest

        //Should divide the connect components in numDivs pieces (+1 if n<rest)
        CanonicalJMPath tempCon = new CanonicalJMPath();
        for (int n = 0; n < conSmall.getNumberOfPaths(); n++) {
            JMPath pathToDivide = conSmall.get(n);
            int numberOfDivisions = numDivs + (n < rest ? 1 : 0);
            CanonicalJMPath divisionResult = divideConnectedComponent(pathToDivide, numberOfDivisions);
            tempCon.addAll(divisionResult);
        }
        conSmall.clear();
        conSmall.addAll(tempCon);

        //Now that I am sure we have the same number of connected components, let's align the number in each one
        for (int n = 0; n < conSmall.getNumberOfPaths(); n++) {
            alignNumberOfElements(conSmall.get(n), conBig.get(n));
        }

    }

    private CanonicalJMPath divideConnectedComponent(JMPath pathToDivide, int numberOfDivisions) {
        if (pathToDivide.size() < numberOfDivisions + 1) {
            //I must ensure they have at least numDivs+1 points! (+1 if n<rest)
            pathToDivide.alignPathsToGivenNumberOfElements(numberOfDivisions + 1);
        }
        //Length of each SEGMENT
        int stepDiv = ((pathToDivide.size() - 1) / (numberOfDivisions)); //Euclidean quotient
        int rest = ((pathToDivide.size() - 1) % (numberOfDivisions));//Euclidean rest

        //Now separate appropiate vertices
        int step = stepDiv;
        ArrayList<JMPathPoint> pointsToSeparate = new ArrayList<>();
        for (int k = 1; k < numberOfDivisions; k++) {
            pointsToSeparate.add(pathToDivide.getJMPoint(step));
            step += stepDiv;
            step += (k < rest ? 1 : 0);
        }
        //Now that I marked correspondent points to separate, do the separation
        for (JMPathPoint p : pointsToSeparate) {
            int k = pathToDivide.jmPathPoints.indexOf(p);
            pathToDivide.separate(k);
        }

        return pathToDivide.canonicalForm();
    }

    @Override
    public void addObjectsToScene() {
    }

}