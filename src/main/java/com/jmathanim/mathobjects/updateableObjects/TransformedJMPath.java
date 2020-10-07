/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformedJMPath extends Shape{

    private final AffineJTransform transform;
    private final Shape srcOBj;

    public TransformedJMPath(Shape jmpobj, AffineJTransform tr) {
        super();
        this.transform=tr;
        this.srcOBj=jmpobj;
        this.jmpath.addJMPoints(jmpobj.jmpath);
    }

    @Override
    public int getUpdateLevel() {
        return srcOBj.getUpdateLevel()+1;
    }

    @Override
    public void update() {
          int size = srcOBj.jmpath.size();
            for (int n = 0; n < size; n++) {
                JMPathPoint jmPDst = getJMPoint(n);
                JMPathPoint pSrc = srcOBj.getJMPoint(n);
                Point pDst = transform.getTransformedObject(pSrc.p);
                Point cp1Dst = transform.getTransformedObject(pSrc.cp1);
                Point cp2Dst = transform.getTransformedObject(pSrc.cp2);

                jmPDst.p.v.x = pDst.v.x;
                jmPDst.p.v.y = pDst.v.y;
                jmPDst.p.v.z = pDst.v.z;

                jmPDst.cp1.v.x = cp1Dst.v.x;
                jmPDst.cp1.v.y = cp1Dst.v.y;
                jmPDst.cp1.v.z = cp1Dst.v.z;

                jmPDst.cp2.v.x = cp2Dst.v.x;
                jmPDst.cp2.v.y = cp2Dst.v.y;
                jmPDst.cp2.v.z = cp2Dst.v.z;
            }
        }
        

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        scene.registerObjectToBeUpdated(srcOBj);
    }
    
    
}
