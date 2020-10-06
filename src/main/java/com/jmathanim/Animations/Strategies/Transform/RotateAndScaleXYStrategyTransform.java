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
public class RotateAndScaleXYStrategyTransform extends MatrixTransformStrategy{

    public RotateAndScaleXYStrategyTransform(Shape mobjTransformed, Shape mobjDestiny,JMathAnimScene scene) {
        super(mobjTransformed, mobjDestiny,scene);
    }

  

    @Override
    public void applyTransform(double t,double lt) {
        Point A = originalShapeBaseCopy.getJMPoint(0).p;
        Point B = originalShapeBaseCopy.getJMPoint(1).p;
        Point C = originalShapeBaseCopy.getJMPoint(2).p;
        Point D = mobjDestiny.getJMPoint(0).p;
        Point E = mobjDestiny.getJMPoint(1).p;
        Point F = mobjDestiny.getJMPoint(2).p;

        //First map A,B into (0,0) and (1,0)
        AffineJTransform tr1 = AffineJTransform.createDirect2DHomotopy(A, B, new Point(0, 0), new Point(1, 0), 1);

        //Now I create a transformation that adjust the y-scale, proportionally
        //This transform will be applied inversely too
        AffineJTransform tr2 = new AffineJTransform();
        final double proportionalHeight = (F.to(E).norm() / D.to(E).norm()) / (B.to(C).norm() / B.to(A).norm());
        tr2.setV2Img(0, proportionalHeight * lt + (1 - lt) * 1); //Interpolated here

        //Finally, and homotopy to carry A,B into D,E
        AffineJTransform tr3 = AffineJTransform.createDirect2DHomotopy(A, B, D, E, lt);//Interpolated here
        AffineJTransform id = new AffineJTransform();
        //The final transformation
        AffineJTransform tr = tr1.compose(tr2).compose(tr1.getInverse()).compose(tr3);

//        System.out.println("RotateXY Transform "+t);
        applyMatrixTransform(tr, lt);
    }

    @Override
    public void addObjectsToScene() {
    }

    

}
