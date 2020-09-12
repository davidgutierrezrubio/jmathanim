/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;
import com.jmathanim.mathobjects.updateableObjects.absoluteSize;
import java.awt.Color;
import javafx.scene.transform.Affine;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Arrow2D extends MultiShapeObject {
    public Point p1,p2;
    public int arrowType=0;
    public SVGMathObject arrowHead;
    public Arrow2D(Point p1,Point p2) {
        this.p1=p1;
        this.p2=p2;
        shapes.add(new Segment(p1,p2));
        arrowHead=new SVGMathObject("c:\\media\\flecha1.svg");
        arrowHead.drawColor(Color.WHITE);
        arrowHead.fillColor(Color.WHITE);
        absoluteSize abs = new absoluteSize(arrowHead, .02);
        JMathAnimConfig.getConfig().getScene().registerObjectToBeUpdated(abs);
    }

    @Override
    public void draw(Renderer r) {
        Point head=arrowHead.getBoundingBox().getUpper();
        arrowHead.shift(head.to(p2));
        MathObject arrowHeadCopy = arrowHead.copy();
        Vec v=p1.to(p2);
        double angle=Math.atan(v.y/v.x);
        
        AffineTransform tr = AffineTransform.create2DRotationTransform(head, -Math.PI/2+angle);
        tr.applyTransform(arrowHeadCopy);
        super.draw(r);
        arrowHeadCopy.draw(r);
        head.draw(r);
    }
    
    
    
}
