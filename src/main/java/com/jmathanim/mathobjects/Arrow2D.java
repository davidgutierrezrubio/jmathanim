/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arrow2D extends MathObject {

    public enum ArrowType {
        TYPE_1, TYPE_2
    }

    private Point p1, p2;
    private final Shape body;
    public ArrowType arrowType = ArrowType.TYPE_1;
    private final SVGMathObject head;
//    private final File outputDir;
    private static final double DEFAULT_ARROW_HEAD_SIZE = .015;

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2) {
        return makeSimpleArrow2D(p1, p2, ArrowType.TYPE_1);
    }

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2, ArrowType type) {
        return new Arrow2D(p1, p2, type);
    }

    public final SVGMathObject buildArrowHead(ArrowType type) {
        SVGMathObject svg = null;
        File outputDir = JMathAnimConfig.getConfig().getResourcesDir();
        String name = "#arrow";
        switch (type) {//TODO: Improve this
            case TYPE_1:
                name += "1";
                break;
            case TYPE_2:
                name += "2";
                break;
            default:
                name += "1";
        }
        name += ".svg";
        String baseFileName;
        try {
//            baseFileName = outputDir.getCanonicalPath() + File.separator + "arrows" + File.separator + name;
            ResourceLoader rl=new ResourceLoader();
            URL arrowUrl = rl.getResource(name,"arrows");
            svg = new SVGMathObject(arrowUrl);

        } catch (NullPointerException ex) {
            JMathAnimScene.logger.error("Arrow head " + name + " not found");
        }
        return svg;
    }

    public Arrow2D(Point p1, Point p2, ArrowType type) {
        this.p1 = p1;
        this.p2 = p2;
        this.body = Shape.segment(p1, p2);
        this.head = buildArrowHead(type);

        head.drawColor(this.body.mp.getDrawColor());
        head.fillColor(this.body.mp.getDrawColor());
        scaleArrowHead(1);
        head.fillWithDrawColor(true);
        head.setAbsoluteSize();
        head.setAbsoluteAnchorPoint(p2);
    }

    public Arrow2D(Point p1, Point p2, SVGMathObject svg) {
        this.p1 = p1;
        this.p2 = p2;
        this.body = Shape.segment(p1, p2);
        this.head = svg;
        head.drawColor(this.body.mp.getDrawColor());
        head.fillColor(this.body.mp.getDrawColor());
        scaleArrowHead(1);
        head.fillWithDrawColor(true);
        head.setAbsoluteSize();
        head.setAbsoluteAnchorPoint(p2);
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
        //Draws arrow body only to (1-alpha)% of total length
        double w1 = r.getCamera().getMathView().getWidth();
        double w2 = r.getFixedCamera().getMathView().getWidth();

        double alpha = 1 - .5 * w1 / w2 * head.getBoundingBox().getHeight() / vecLength;
        Shape bodyToDraw = body.copy().scale(body.getPoint(0), alpha, alpha);
        bodyToDraw.draw(r);
        arrowHeadCopy.setAbsoluteSize(Anchor.Type.BY_POINT);
        arrowHeadCopy.setAbsoluteAnchorPoint(p2);
        arrowHeadCopy.draw(r);

//        head.draw(r);
    }

    @Override
    public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
        body.scale(scaleCenter, sx, sy, sz);
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
    public Point getCenter() {
        return body.getCenter();
    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        body.shift(shiftVector);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T copy() {
        Arrow2D copy = Arrow2D.makeSimpleArrow2D(p1.copy(), p2.copy(), arrowType);
        copy.head.mp.copyFrom(this.head.mp);
        copy.body.mp.copyFrom(this.body.mp);
        return (T) copy;
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
    public <T extends MathObject> T thickness(double th) {
        body.thickness(th);
        return (T) this;
    }

    @Override
    public Rect getBoundingBox() {
        return body.getBoundingBox();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        scene.registerUpdateable(body);
        scene.registerUpdateable(head);
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        scene.unregisterUpdateable(body);
        scene.unregisterUpdateable(head);
    }

    @Override
    public void update(JMathAnimScene scene) {
    }

    @Override
    public void interpolateMPFrom(MODrawProperties mpDst, double alpha) {
        body.interpolateMPFrom(mpDst, alpha); //To change body of generated methods, choose Tools | Templates.
        head.interpolateMPFrom(mpDst, alpha); //To change body of generated methods, choose Tools | Templates.
    }

}
