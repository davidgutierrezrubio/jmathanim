/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.AbsoluteSizeUpdater;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arrow2D extends MathObject {

    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;

    private Point p1, p2;
    private final Shape body;
    public int arrowType = 0;
    private final SVGMathObject head;
//    private final File outputDir;
    private AbsoluteSizeUpdater absoluteSizeUpdater;
    private static final double DEFAULT_ARROW_HEAD_SIZE = .005;

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2) {
        return makeSimpleArrow2D(p1, p2, TYPE_1);
    }

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2, int type) {
        Arrow2D resul = null;
        SVGMathObject svg;
        File outputDir = JMathAnimConfig.getConfig().getResourcesDir();

        String name = "arrow" + type + ".svg";

        String baseFileName;
        try {
            baseFileName = outputDir.getCanonicalPath() + File.separator + "arrows" + File.separator + name;
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
        this.body = Shape.segment(p1, p2);
        this.head = svg;

        head.drawColor(this.body.mp.drawColor);
        head.fillColor(this.body.mp.drawColor);

//        shapes.get(0).mp = arrowHead.mp;
//        absoluteSizeUpdater = new AbsoluteSizeUpdater(arrowHead, DEFAULT_ARROW_HEAD_SIZE);
        //Default scale of arrowHead: width .5% of fixed camera width
        scaleArrowHead(1);
        head.setAbsoluteSize();
        head.setAbsoluteAnchorPoint(p2);

//        JMathAnimConfig.getConfig().getScene().registerObjectToBeUpdated(absoluteSizeUpdater);
    }

    /**
     * Returns the body of the Arrow
     *
     * @return A Shape object, with the body of the Arrow (a segment)
     */
    public Shape getBody() {
        return body;
    }

    /**
     * Returns the arrow head
     *
     * @return A SVGMathObject representing the arrow head
     */
    public SVGMathObject getArrowHead() {
        return head;
    }

    /**
     * Returns the starting point of the Arrow
     *
     * @return A reference to the starting Point
     */
    public Point getStart() {
        return p1;
    }

    /**
     * Returns the ending point of the Arrow
     *
     * @return A reference to the ending Point
     */
    public Point getEnd() {
        return p2;
    }

    public final void scaleArrowHead(double sc) {
        double mw = JMathAnimConfig.getConfig().getFixedCamera().getMathView().getWidth();
        double scaleFactor = sc * DEFAULT_ARROW_HEAD_SIZE * mw / head.getBoundingBox().getWidth();

        head.scale(scaleFactor);
    }

    @Override
    public void restoreState() {
        body.restoreState();
        head.restoreState();
    }

    @Override
    public void saveState() {
        body.saveState();
        head.saveState();
    }

    @Override
    public void draw(Renderer r) {
        //Move arrowhead to p2
        Point headPoint = this.head.getBoundingBox().getUpper();
        this.head.shift(headPoint.to(p2));

        //Create a copy to rotate properly and draw, as original is not rotated
        MathObject arrowHeadCopy = this.head.copy();
        Vec v = p1.to(p2);
        double angle = v.getAngle();
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(p2, -Math.PI / 2 + angle);
        tr.applyTransform(arrowHeadCopy);
        double vecLength = p1.to(p2).norm();
        //TODO: Uses 50% of arrow head height. Can be improved?
        double alpha = 1 - .5*head.getBoundingBox().getHeight() / vecLength;
        Shape bodyToDraw = body.copy().scale(body.getPoint(0), alpha, alpha);
        bodyToDraw.draw(r);
        arrowHeadCopy.draw(r);

//        head.draw(r);
    }

    @Override
    public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
        body.scale(scaleCenter, sx, sy, sz);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T scale(double sx, double sy, double sz) {
        body.scale(sx, sy, sz);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T scale(Point p, double sx, double sy) {
        body.scale(p, sx, sy);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T scale(double s) {
        body.scale(s); //To change body of generated methods, choose Tools | Templates.
        return (T) this;
    }

    @Override
    public <T extends MathObject> T scale(double sx, double sy) {
        body.scale(sx, sy); //To change body of generated methods, choose Tools | Templates.
        return (T) this;
    }

    @Override
    public <T extends MathObject> T layer(int layer) {
        head.layer(layer);
        body.layer(layer);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multDrawAlpha(double t) {
        head.multDrawAlpha(t);
        body.multDrawAlpha(t);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multFillAlpha(double t) {
        head.multFillAlpha(t);
        body.multFillAlpha(t);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T shift(double x, double y) {
        return super.shift(x, y); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Point getCenter() {
        return body.getCenter();
    }

    @Override
    public <T extends MathObject> T moveTo(Vec coords) {
        body.moveTo(coords);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        body.shift(shiftVector);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T copy() {
        return (T) Arrow2D.makeSimpleArrow2D(p1.copy(), p2.copy(), arrowType);

    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        return drawColor(fc); //Fill and draw color should be the same
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        head.drawColor(dc);
        head.fillColor(dc);
        body.drawColor(dc);

        return (T) this;
    }

    @Override
    public void prepareForNonLinearAnimation() {
    }

    @Override
    public void processAfterNonLinearAnimation() {
    }

    @Override
    public Rect getBoundingBox() {
        return head.getBoundingBox().union(body.getBoundingBox());
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void update() {
    }

}
