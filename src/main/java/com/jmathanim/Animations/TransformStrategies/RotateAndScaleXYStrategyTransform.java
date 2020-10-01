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
public class RotateAndScaleXYStrategyTransform extends MatrixTransformStrategy{

  

    @Override
    public void applyTransform(double t) {
        Point A = originalShapeBaseCopy.getJMPoint(0).p;
        Point B = originalShapeBaseCopy.getJMPoint(1).p;
        Point C = originalShapeBaseCopy.getJMPoint(2).p;
        Point D = mobjDestiny.getJMPoint(0).p;
        Point E = mobjDestiny.getJMPoint(1).p;
        Point F = mobjDestiny.getJMPoint(2).p;

        //First map A,B into (0,0) and (1,0)
        AffineTransform tr1 = AffineTransform.createDirect2DHomotopy(A, B, new Point(0, 0), new Point(1, 0), 1);

        //Now I create a transformation that adjust the y-scale, proportionally
        //This transform will be applied inversely too
        AffineTransform tr2 = new AffineTransform();
        final double proportionalHeight = (F.to(E).norm() / D.to(E).norm()) / (B.to(C).norm() / B.to(A).norm());
        tr2.setV2Img(0, proportionalHeight * t + (1 - t) * 1); //Interpolated here

        //Finally, and homotopy to carry A,B into D,E
        AffineTransform tr3 = AffineTransform.createDirect2DHomotopy(A, B, D, E, t);//Interpolated here
        AffineTransform id = new AffineTransform();
        //The final transformation
        AffineTransform tr = tr1.compose(tr2).compose(tr1.getInverse()).compose(tr3);

//        System.out.println("RotateXY Transform "+t);
        applyMatrixTransform(tr, t);
    }

    

}
