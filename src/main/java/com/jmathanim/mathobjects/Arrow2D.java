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

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.net.URL;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arrow2D extends MathObject {

    private MultiShapeObject arrrowHeadToDraw1;
    private MultiShapeObject arrrowHeadToDraw2;
    private Shape bodyToDraw;
    private double scaleFactorHead1;
    private double scaleFactorHead2;
    private double scaleFactorHead1Backup;
    private double scaleFactorHead2Backup;

    private int anchorPoint1;
    private int anchorPoint2;

    public enum ArrowType {
        NONE, TYPE_1, TYPE_2
    }

    private final Point p1, p2;
    private final Shape body;
    public ArrowType arrowType = ArrowType.TYPE_1;
    private MultiShapeObject head1, head2;
//    private final File outputDir;
    private static final double DEFAULT_ARROW_HEAD_SIZE = .015;

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2) {
        return makeSimpleArrow2D(p1, p2, ArrowType.TYPE_1);
    }

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2, ArrowType type) {
        return new Arrow2D(p1, p2, type, ArrowType.NONE);
    }

    public static Arrow2D makeDoubleArrow2D(Point p1, Point p2, ArrowType type1, ArrowType type2) {
        return new Arrow2D(p1, p2, type1, type2);
    }

    private Arrow2D(Point p1, Point p2, ArrowType type1, ArrowType type2) {
        this.p1 = p1;
        this.p2 = p2;
        this.body = Shape.segment(p1, p2);
        this.head1 = buildArrowHead(type1);
        this.head2 = buildArrowHead(type2);

        head1.drawColor(this.body.mp.getDrawColor());
        head1.fillColor(this.body.mp.getDrawColor());
        scaleArrowHead1(1);
        head1.fillWithDrawColor(true);
        head1.setAbsoluteSize();
        head1.setAbsoluteAnchorPoint(p2);

        head2.drawColor(this.body.mp.getDrawColor());
        head2.fillColor(this.body.mp.getDrawColor());
        scaleArrowHead2(1);
        head2.fillWithDrawColor(true);
        head2.setAbsoluteSize();
        head2.setAbsoluteAnchorPoint(p1);
    }

    public Arrow2D(Point p1, Point p2, MultiShapeObject head1) {
        this(p1, p2, head1, new MultiShapeObject());
    }

    public Arrow2D(Point p1, Point p2, MultiShapeObject msh1, MultiShapeObject msh2) {
        this.p1 = p1;
        this.p2 = p2;
        this.body = Shape.segment(p1, p2);
        this.head1 = msh1;
        this.head2 = msh2;

        head1.drawColor(this.body.mp.getDrawColor());
        head1.fillColor(this.body.mp.getDrawColor());
        scaleArrowHead1(1);
        head1.fillWithDrawColor(true);
        head1.setAbsoluteSize();
        head1.setAbsoluteAnchorPoint(p2);

        head2.drawColor(this.body.mp.getDrawColor());
        head2.fillColor(this.body.mp.getDrawColor());
        scaleArrowHead2(1);
        head2.fillWithDrawColor(true);
        head2.setAbsoluteSize();
        head2.setAbsoluteAnchorPoint(p1);
    }

    public Arrow2D(Point p1, Point p2, Shape head) {
        this(p1, p2, new MultiShapeObject(head));
    }

    public Arrow2D(Point p1, Point p2, Shape head1, Shape head2) {
        this(p1, p2, new MultiShapeObject(head1), new MultiShapeObject(head2));
    }

    public final MultiShapeObject buildArrowHead(ArrowType type) {
        SVGMathObject head = null;
        String name = "#arrow";
        if (type != ArrowType.NONE) {//If type=NONE, head=null
            switch (type) {//TODO: Improve this
                case TYPE_1:
                    name += "1";
                    anchorPoint1 = 2;
                    break;
                case TYPE_2:
                    name += "2";
                    anchorPoint1 = 7;
                    break;
                default:
                    name += "1";
            }
            name += ".svg";
            try {
//            baseFileName = outputDir.getCanonicalPath() + File.separator + "arrows" + File.separator + name;
                ResourceLoader rl = new ResourceLoader();
                URL arrowUrl = rl.getResource(name, "arrows");
                head = new SVGMathObject(arrowUrl);

            } catch (NullPointerException ex) {
                JMathAnimScene.logger.error("Arrow head " + name + " not found");
            }
        } else {
            head = new SVGMathObject();
        }
        return head;
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
    public MultiShapeObject getArrowHead() {
        return head1;
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

    /**
     * Sets the scale of the arrow head 1
     *
     * @param <T> Implementation of Arrow2D
     * @param sc Scale value. By default is 1.
     * @return This object
     */
    public final <T extends Arrow2D> T scaleArrowHead1(double sc) {
        this.scaleFactorHead1 = sc;
        return (T) this;
    }

    /**
     * Sets the scale of the arrow head 2
     *
     * @param <T> Implementation of Arrow2D
     * @param sc Scale value. By default is 1.
     * @return This object
     */
    public final <T extends Arrow2D> T scaleArrowHead2(double sc) {
        this.scaleFactorHead2 = sc;
        return (T) this;
    }

    @Override
    public void restoreState() {
        body.restoreState();
        head1.restoreState();
        scaleFactorHead1 = scaleFactorHead1Backup;
        scaleFactorHead2 = scaleFactorHead2Backup;
    }

    @Override
    public void saveState() {
        body.saveState();
        head1.saveState();
        scaleFactorHead1Backup = scaleFactorHead1;
        scaleFactorHead2Backup = scaleFactorHead2;
    }

    @Override
    public void draw(Renderer r) {
        //Move arrowhead to p2

        bodyToDraw.draw(r);
        arrrowHeadToDraw1.draw(r);
        arrrowHeadToDraw2.draw(r);

    }

    @Override
    public void update(JMathAnimScene scene) {
        bodyToDraw = body.copy();
        arrrowHeadToDraw1 = this.head1.copy();
        arrrowHeadToDraw2 = this.head2.copy();
        if (this.head1.size() > 0) {
            //Scaling
            double mw = JMathAnimConfig.getConfig().getFixedCamera().getMathView().getHeight();
            double sc1 = this.scaleFactorHead1 * DEFAULT_ARROW_HEAD_SIZE * mw / head1.getBoundingBox().getHeight();
            arrrowHeadToDraw1.scale(sc1);

            //Shifting
            Point headPoint = this.arrrowHeadToDraw1.getBoundingBox().getUpper();
            this.arrrowHeadToDraw1.shift(headPoint.to(p2));

            //Rotating
            Vec v = p1.to(p2);
            double angle = v.getAngle();
            AffineJTransform tr = AffineJTransform.create2DRotationTransform(p2, -Math.PI / 2 + angle);
            tr.applyTransform(arrrowHeadToDraw1);
            double vecLength = p1.to(p2).norm();
            arrrowHeadToDraw1.setAbsoluteSize(Anchor.Type.BY_POINT);
            arrrowHeadToDraw1.setAbsoluteAnchorPoint(p2);
            arrrowHeadToDraw1.draw(scene.getConfig().getRenderer());

            JMPathPoint pa = bodyToDraw.getPath().getJMPoint(1);
            pa.p.v.copyFrom(arrrowHeadToDraw1.get(0).getPoint(anchorPoint1).v);
        }

        if (this.head2.size() > 0) {

            //Scaling
            double mw = JMathAnimConfig.getConfig().getFixedCamera().getMathView().getHeight();
            double sc2 = this.scaleFactorHead2 * DEFAULT_ARROW_HEAD_SIZE * mw / head2.getBoundingBox().getHeight();
            arrrowHeadToDraw2.scale(sc2);

            //Shifting
            Point headPoint = this.arrrowHeadToDraw2.getBoundingBox().getUpper();
            this.arrrowHeadToDraw2.shift(headPoint.to(p1));

            //Rotating
            Vec v = p2.to(p1);
            double angle = v.getAngle();
            AffineJTransform tr = AffineJTransform.create2DRotationTransform(p1, -Math.PI / 2 + angle);
            tr.applyTransform(arrrowHeadToDraw2);
            arrrowHeadToDraw2.setAbsoluteSize(Anchor.Type.BY_POINT);
            arrrowHeadToDraw2.setAbsoluteAnchorPoint(p2);

            JMPathPoint pa = bodyToDraw.getPath().getJMPoint(0);
            pa.p.v.copyFrom(arrrowHeadToDraw2.get(0).getPoint(anchorPoint2).v);
        }

    }

    @Override
    public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
        body.scale(scaleCenter, sx, sy, sz);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T layer(int layer) {
        head1.layer(layer);
        body.layer(layer);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multDrawAlpha(double t) {
        head1.multDrawAlpha(t);
        body.multDrawAlpha(t);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multFillAlpha(double t) {
        head1.multFillAlpha(t);
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
        copy.head1 = head1.copy();
//        copy.head.mp.copyFrom(this.head.mp);
        copy.body.mp.copyFrom(this.body.mp);
        return (T) copy;
    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        return drawColor(fc); //Fill and draw color should be the same
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        head1.drawColor(dc);
        head1.fillColor(dc);
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
        scene.registerUpdateable(head1);
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        scene.unregisterUpdateable(body);
        scene.unregisterUpdateable(head1);
    }

    @Override
    public void interpolateMPFrom(MODrawProperties mpDst, double alpha) {
        body.interpolateMPFrom(mpDst, alpha); //To change body of generated methods, choose Tools | Templates.
        head1.interpolateMPFrom(mpDst, alpha); //To change body of generated methods, choose Tools | Templates.
    }

    public double getScaleArrowHead1() {
        return scaleFactorHead1;
    }

    public double getScaleArrowHead2() {
        return scaleFactorHead2;
    }
}
