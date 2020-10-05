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
public class AffineStrategyTransform extends MatrixTransformStrategy{

    public AffineStrategyTransform(Shape mobjTransformed, Shape mobjDestiny,JMathAnimScene scene) {
        super(mobjTransformed, mobjDestiny,scene);
    }

    @Override
    public void applyTransform(double t) {
        Point A = originalShapeBaseCopy.getJMPoint(0).p;
        Point B = originalShapeBaseCopy.getJMPoint(1).p;
        Point C = originalShapeBaseCopy.getJMPoint(2).p;
        Point D = mobjDestiny.getJMPoint(0).p;
        Point E = mobjDestiny.getJMPoint(1).p;
        Point F = mobjDestiny.getJMPoint(2).p;

        AffineJTransform tr = AffineJTransform.createAffineTransformation(A, B, C, D, E, F, t);
        applyMatrixTransform(tr, t);
        
    }

   

}
