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
import com.jmathanim.mathobjects.updateableObjects.AbsoluteSizeUpdater;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Arrow2D extends MultiShapeObject {

    public Point p1, p2;
    public int arrowType = 0;
    public SVGMathObject arrowHead;
    private final File outputDir;
    private final AbsoluteSizeUpdater absoluteSizeUpdater;
    private static final double DEFAULT_ARROW_HEAD_SIZE=.02;

    public Arrow2D(Point p1, Point p2,String name) {
        this.p1 = p1;
        this.p2 = p2;
        shapes.add(new Segment(p1, p2));
        outputDir = new File("resources");
        String baseFileName;
        try {
            baseFileName = outputDir.getCanonicalPath() + "\\" + name;
            arrowHead = new SVGMathObject(baseFileName);
        } catch (IOException ex) {
            Logger.getLogger(Arrow2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        arrowHead.drawColor(shapes.get(0).mp.drawColor);
        arrowHead.fillColor(shapes.get(0).mp.drawColor);
        
        
//        shapes.get(0).mp = arrowHead.mp;
        absoluteSizeUpdater = new AbsoluteSizeUpdater(arrowHead, DEFAULT_ARROW_HEAD_SIZE);
        JMathAnimConfig.getConfig().getScene().registerObjectToBeUpdated(absoluteSizeUpdater);
    }

    @Override
    public void draw(Renderer r) {
        Point head = arrowHead.getBoundingBox().getUpper();
        arrowHead.shift(head.to(p2));
        MathObject arrowHeadCopy = arrowHead.copy();
        Vec v = p1.to(p2);
        double angle = v.getAngle();

        AffineTransform tr = AffineTransform.create2DRotationTransform(head, -Math.PI / 2 + angle);
        tr.applyTransform(arrowHeadCopy);
        super.draw(r);
        arrowHeadCopy.draw(r);
        head.draw(r);
    }

    public double getArrowSize() {
        return absoluteSizeUpdater.ratio/DEFAULT_ARROW_HEAD_SIZE;
    }

    public void setArrowSize(double arrowSize) {
        absoluteSizeUpdater.ratio=arrowSize*DEFAULT_ARROW_HEAD_SIZE;
    }

}
