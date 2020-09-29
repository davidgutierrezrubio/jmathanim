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

    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;

    public Point p1, p2;
    public int arrowType = 0;
    public SVGMathObject arrowHead;
//    private final File outputDir;
    private AbsoluteSizeUpdater absoluteSizeUpdater;
    private static final double DEFAULT_ARROW_HEAD_SIZE = .005;

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2, int type) {
        Arrow2D resul = null;
        SVGMathObject svg;
        File outputDir = new File("resources");

        String name = "arrow" + type + ".svg";

        String baseFileName;
        try {
            baseFileName = outputDir.getCanonicalPath() + "\\" + name;
            svg = new SVGMathObject(baseFileName);
            resul = new Arrow2D(p1, p2, svg);
        } catch (IOException ex) {
            Logger.getLogger(Arrow2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resul;
    }

    public Arrow2D(Point p1, Point p2, SVGMathObject svg) {
        this.p1 = p1;
        this.p2 = p2;
        shapes.add(Shape.segment(p1, p2));
        this.arrowHead = svg;

        arrowHead.drawColor(shapes.get(0).mp.drawColor);
        arrowHead.fillColor(shapes.get(0).mp.drawColor);

//        shapes.get(0).mp = arrowHead.mp;
//        absoluteSizeUpdater = new AbsoluteSizeUpdater(arrowHead, DEFAULT_ARROW_HEAD_SIZE);
        //Default scale of arrowHead: width .5% of fixed camera width
        scaleArrowHead(1);
        arrowHead.setAbsoluteSize();
        arrowHead.setAbsolutAnchorPoint(p2);

//        JMathAnimConfig.getConfig().getScene().registerObjectToBeUpdated(absoluteSizeUpdater);
    }

    private void scaleArrowHead(double sc) {
        double mw = JMathAnimConfig.getConfig().getFixedCamera().getMathView().getWidth();
        double scaleFactor = sc * DEFAULT_ARROW_HEAD_SIZE * mw / arrowHead.getBoundingBox().getWidth();

        arrowHead.scale(scaleFactor);
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
//        head.draw(r);
    }

   


    public Arrow2D scale(double arrowSize) {
        scaleArrowHead(arrowSize);
        return this;
    }

}
