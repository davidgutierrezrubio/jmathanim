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
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
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

    private MODrawPropertiesArray mpArray;
    private MultiShapeObject arrowHeadToDraw1;
    private MultiShapeObject arrowHeadToDraw2;
    private Shape bodyToDraw;
    private double scaleFactorHead1;
    private double scaleFactorHead2;
    private double scaleFactorHead1Backup;
    private double scaleFactorHead2Backup;

    private int anchorPoint1;
    private int anchorPoint2;

    public enum ArrowType {
        NONE, TYPE_1, TYPE_2, TYPE_3
    }

    private final Point p1, p2;
    private final Shape body;
    public ArrowType arrowType = ArrowType.TYPE_1;
    private final MultiShapeObject head1, head2;
//    private final File outputDir;
    private double defaultArrowHead1Size1 = .015;
    private double defaultArrowHead1Size2 = .015;

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
        this.head1 = buildArrowHead(type1, 1);
        this.head2 = buildArrowHead(type2, 2);
        this.body.label = "body";
        this.head1.label = "head1";
        this.head2.label = "head2";

        head1.drawColor(this.body.getMp().getDrawColor());
        head1.fillColor(this.body.getMp().getDrawColor());
        scaleArrowHead1(1);
        head2.drawColor(this.body.getMp().getDrawColor());
        head2.fillColor(this.body.getMp().getDrawColor());
        scaleArrowHead2(1);
        mpArray = new MODrawPropertiesArray();
        mpArray.add(head1);
        mpArray.add(head2);
        mpArray.add(body);
    }

//    private Arrow2D(Point p1, Point p2, MultiShapeObject head1) {
//        this(p1, p2, head1, new MultiShapeObject());
//    }
//
//    private Arrow2D(Point p1, Point p2, MultiShapeObject msh1, MultiShapeObject msh2) {
//        this.p1 = p1;
//        this.p2 = p2;
//        this.body = Shape.segment(p1, p2);
//        this.head1 = msh1;
//        this.head2 = msh2;
//
//        head1.drawColor(this.body.getMp().getDrawColor());
//        head1.fillColor(this.body.getMp().getDrawColor());
//        scaleArrowHead1(1);
//        head1.fillWithDrawColor(true);
//        head2.drawColor(this.body.getMp().getDrawColor());
//        head2.fillColor(this.body.getMp().getDrawColor());
//        scaleArrowHead2(1);
//        head2.fillWithDrawColor(true);
//        mpArray = new MODrawPropertiesArray();
//        mpArray.add(head1);
//        mpArray.add(head2);
//        mpArray.add(body);
//    }
//
//    private Arrow2D(Point p1, Point p2, Shape head) {
//        this(p1, p2, new MultiShapeObject(head));
//    }
//
//    private Arrow2D(Point p1, Point p2, Shape head1, Shape head2) {
//        this(p1, p2, new MultiShapeObject(head1), new MultiShapeObject(head2));
//    }
    public final MultiShapeObject buildArrowHead(ArrowType type, int side) {
        SVGMathObject head = null;
        String name = "#arrow";
        int anchorValue;
        double scaleDefaultValue;
        if (type != ArrowType.NONE) {//If type=NONE, head=null
            switch (type) {//TODO: Improve this
                case TYPE_1:
                    name += "1";
                    anchorValue = 2;
                    scaleDefaultValue = 1.5;
                    break;
                case TYPE_2:
                    name += "2";
                    anchorValue = 7;
                    scaleDefaultValue = 1.5;
                    break;
                case TYPE_3:
                    name += "3";
                    anchorValue = 7;
                    scaleDefaultValue = 1;
                    break;
                default:
                    name += "1";
                    anchorValue = 2;
                    scaleDefaultValue = 1.5;
            }
            if (side == 1) {
                anchorPoint1 = anchorValue;
                defaultArrowHead1Size1 *= scaleDefaultValue;
            } else {
                anchorPoint2 = anchorValue;
                defaultArrowHead1Size2 *= scaleDefaultValue;
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
     * Returns the arrow head 1, located at the end of the segment
     *
     * @return A SVGMathObject representing the arrow head
     */
    public MultiShapeObject getArrowHead1() {
        update(JMathAnimConfig.getConfig().getScene());//TODO: Remove this coupling
        return arrowHeadToDraw1;
    }

    /**
     * Returns the arrow head 2, located at the beginning of the segment
     *
     * @return A SVGMathObject representing the arrow head
     */
    public MultiShapeObject getArrowHead2() {
        update(JMathAnimConfig.getConfig().getScene());//TODO: Remove this coupling
        return arrowHeadToDraw2;
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
        super.restoreState();
        getMp().restoreState();
        body.restoreState();
        head1.restoreState();
        head2.restoreState();
        scaleFactorHead1 = scaleFactorHead1Backup;
        scaleFactorHead2 = scaleFactorHead2Backup;
    }

    @Override
    public void saveState() {
        super.saveState();
        getMp().saveState();
        body.saveState();
        head1.saveState();
        head2.saveState();
        scaleFactorHead1Backup = scaleFactorHead1;
        scaleFactorHead2Backup = scaleFactorHead2;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        bodyToDraw.draw(scene, r);
        arrowHeadToDraw1.draw(scene, r);
        arrowHeadToDraw2.draw(scene, r);
        scene.markAsAlreadyDrawed(this);
    }

    @Override
    public void update(JMathAnimScene scene) {
        bodyToDraw = body.copy();
        arrowHeadToDraw1 = this.head1.copy();
        arrowHeadToDraw2 = this.head2.copy();
        if (this.head1.size() > 0) {
            //Scaling
            double mw = scene.getCamera().getMathView().getHeight();
            double sc1 = this.scaleFactorHead1 * defaultArrowHead1Size1 * mw / head1.getBoundingBox().getHeight();
            arrowHeadToDraw1.scale(sc1);

            //Shifting
            Point headPoint = this.arrowHeadToDraw1.getBoundingBox().getUpper();
            this.arrowHeadToDraw1.shift(headPoint.to(p2));

            //Rotating
            Vec v = p1.to(p2);
            double angle = v.getAngle();
            AffineJTransform tr = AffineJTransform.create2DRotationTransform(p2, -Math.PI / 2 + angle);
            tr.applyTransform(arrowHeadToDraw1);

//            arrowHeadToDraw1.draw(scene.getConfig().getRenderer());
            JMPathPoint pa = bodyToDraw.getPath().getJMPoint(1);
            pa.p.v.copyFrom(arrowHeadToDraw1.get(0).get(anchorPoint1).p.v);
        }

        if (this.head2.size() > 0) {

            //Scaling
            double mw = JMathAnimConfig.getConfig().getCamera().getMathView().getHeight();
            double sc2 = this.scaleFactorHead2 * defaultArrowHead1Size2 * mw / head2.getBoundingBox().getHeight();
            arrowHeadToDraw2.scale(sc2);
            //Shifting
            Point headPoint = this.arrowHeadToDraw2.getBoundingBox().getUpper();
            this.arrowHeadToDraw2.shift(headPoint.to(p1));

            //Rotating
            Vec v = p2.to(p1);
            double angle = v.getAngle();
            AffineJTransform tr = AffineJTransform.create2DRotationTransform(p1, -Math.PI / 2 + angle);
            tr.applyTransform(arrowHeadToDraw2);

            JMPathPoint pa = bodyToDraw.getPath().getJMPoint(0);
            pa.p.v.copyFrom(arrowHeadToDraw2.get(0).get(anchorPoint2).p.v);
        }

    }

    @Override
    public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
        body.scale(scaleCenter, sx, sy, sz);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        body.shift(shiftVector);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T copy() {
        Arrow2D copy = Arrow2D.makeSimpleArrow2D(p1.copy(), p2.copy(), arrowType);
        copy.head1.getMp().copyFrom(this.head1.getMp());
        copy.head2.getMp().copyFrom(this.head2.getMp());
        copy.body.getMp().copyFrom(this.body.getMp());
        return (T) copy;
    }

    @Override
    public <T extends MathObject> T thickness(double th) {
        body.thickness(th);
        return (T) this;
    }

    @Override
    public Rect getBoundingBox() {
        Rect r = body.getBoundingBox();
        update(JMathAnimConfig.getConfig().getScene());
        if (arrowHeadToDraw1.size() > 0) {
            r = Rect.union(r, arrowHeadToDraw1.getBoundingBox());
        }
        if (arrowHeadToDraw2.size() > 0) {
            r = Rect.union(r, arrowHeadToDraw2.getBoundingBox());
        }
        return r;
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

    public double getScaleArrowHead1() {
        return scaleFactorHead1;
    }

    public double getScaleArrowHead2() {
        return scaleFactorHead2;
    }

    public MultiShapeObject getHead1() {
        return head1;
    }

    public MultiShapeObject getHead2() {
        return head2;
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        getBody().applyAffineTransform(tr);
        tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

}
