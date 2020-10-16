/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.Transform.AffineStrategyTransform;
import com.jmathanim.Animations.Strategies.Transform.HomothecyStrategyTransform;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationCanonical;
import com.jmathanim.Animations.Strategies.Transform.RotateAndScaleXYStrategyTransform;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
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
    private MODrawProperties propBase;
    private Integer method;
    public boolean shouldOptimizePathsFirst;
    public boolean forceChangeDirection;
    private boolean isFinished;
    private JMathAnimScene scene;
    private TransformStrategy strategy;
    

    public Transform( double runTime,Shape ob1, Shape ob2) {
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
            determineTransformStrategy();
        }
        createStrategy();

        //Variable strategy should have proper strategy to transform
        //If method is null means that user didn't force one
        strategy.prepareObjects();
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

    private void determineTransformStrategy() {
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
            } else { //Different number of vertices
                method = METHOD_INTERPOLATE_POINT_BY_POINT;
                 methodTextOutput = "Transform method: By point";
            }

        }

        JMathAnimScene.logger.info(methodTextOutput);

    }

    private void createStrategy() {
        //Now I choose strategy
        switch (method) {
            case METHOD_INTERPOLATE_POINT_BY_POINT:
                strategy = new PointInterpolationCanonical(mobjTransformed, mobjDestiny,scene);
                break;
            case METHOD_HOMOTOPY_TRANSFORM:
                strategy = new HomothecyStrategyTransform(mobjTransformed, mobjDestiny,scene);
                break;
            case METHOD_AFFINE_TRANSFORM:
                strategy = new AffineStrategyTransform(mobjTransformed, mobjDestiny,scene);
                break;
            case METHOD_ROTATE_AND_SCALEXY_TRANSFORM:
                strategy = new RotateAndScaleXYStrategyTransform(mobjTransformed, mobjDestiny,scene);
                break;
        }
    }

    @Override
    public void doAnim(double t,double lt) {
        strategy.applyTransform(t,lt);

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
