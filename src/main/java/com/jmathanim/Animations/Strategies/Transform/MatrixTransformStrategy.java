/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MatrixTransformStrategy extends TransformStrategy {

    protected Shape mobjTransformed;
    protected Shape mobjDestiny;
    protected Shape originalShapeBaseCopy;

    public MatrixTransformStrategy(Shape mobjTransformed, Shape mobjDestiny,JMathAnimScene scene) {
        super(scene);
        this.mobjTransformed = mobjTransformed;
        this.mobjDestiny = mobjDestiny;
    }

    @Override
    public void prepareObjects() {
        originalShapeBaseCopy = mobjTransformed.copy();
        JMathAnimConfig.getConfig().getScene().add(mobjTransformed);
    }

    public void applyMatrixTransform(AffineJTransform tr, double t) {
        JMPathPoint interPoint;
        JMPathPoint basePoint;
        JMPathPoint dstPoint;
        for (int n = 0; n < mobjTransformed.jmpath.jmPathPoints.size(); n++) {
            interPoint = mobjTransformed.getJMPoint(n);
            basePoint = originalShapeBaseCopy.getJMPoint(n);
            dstPoint = mobjDestiny.getJMPoint(n);

            //Interpolate point
            interPoint.p.v = tr.getTransformedPoint(basePoint.p).v;

            //Interpolate control point 1
            interPoint.cp1.v = tr.getTransformedPoint(basePoint.cp1).v;

            //Interpolate control point 2
            interPoint.cp2.v = tr.getTransformedPoint(basePoint.cp2).v;

        }
        //Now interpolate properties from objects
        mobjTransformed.mp.interpolateFrom(originalShapeBaseCopy.mp, mobjDestiny.mp, t);
    }

    @Override
    public void finish() {
    }

}