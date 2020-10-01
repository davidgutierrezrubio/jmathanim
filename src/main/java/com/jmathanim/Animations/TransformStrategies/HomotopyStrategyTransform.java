/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.TransformStrategies;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class HomotopyStrategyTransform extends MatrixTransformStrategy {

    @Override
    public void applyTransform(double t) {

        Point A = originalShapeBaseCopy.jmpath.getJMPoint(0).p;
        Point B = originalShapeBaseCopy.jmpath.getJMPoint(1).p;
        Point C = mobjDestiny.jmpath.getJMPoint(0).p;
        Point D = mobjDestiny.jmpath.getJMPoint(1).p;

        AffineTransform tr = AffineTransform.createDirect2DHomotopy(A, B, C, D, t);

        applyMatrixTransform(tr, t);
    }

}
