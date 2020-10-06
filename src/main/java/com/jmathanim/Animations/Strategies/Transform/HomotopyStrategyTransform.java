/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class HomotopyStrategyTransform extends MatrixTransformStrategy {

    public HomotopyStrategyTransform(Shape mobjTransformed, Shape mobjDestiny,JMathAnimScene scene) {
        super(mobjTransformed, mobjDestiny,scene);
    }

    @Override
    public void applyTransform(double t,double lt) {

        Point A = originalShapeBaseCopy.jmpath.getJMPoint(0).p;
        Point B = originalShapeBaseCopy.jmpath.getJMPoint(1).p;
        Point C = mobjDestiny.jmpath.getJMPoint(0).p;
        Point D = mobjDestiny.jmpath.getJMPoint(1).p;

        AffineJTransform tr = AffineJTransform.createDirect2DHomotopy(A, B, C, D, lt);

        applyMatrixTransform(tr, lt);
    }

    @Override
    public void addObjectsToScene() {
    }

}
