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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.scene.shape.StrokeLineCap;

import java.net.URL;

/**
 * DEPRECATED. This class will be removed in future versions. Use Arrow class instead.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arrow2D extends MathObject {

    private final MODrawPropertiesArray mpArray;
    private final ArrowType type1;
    private final ArrowType type2;
    private final Point p1, p2;
    private final Shape body;
    private final MultiShapeObject head1, head2;
    public ArrowType arrowType = ArrowType.TYPE_1;
    private MultiShapeObject arrowHeadToDraw1;
    private MultiShapeObject arrowHeadToDraw2;
    private Shape bodyToDraw;
    private int anchorPoint1;
    private int anchorPoint2;
    //    private final File outputDir;
    private double defaultArrowHead1Size1 = .015;
    private double defaultArrowHead1Size2 = .015;
    private Arrow2D(Point p1, Point p2, ArrowType type1, ArrowType type2) {
        JMathAnimScene.logger.warn("Class Arrow2D is DEPRECATED. You should consider using Arrow instead");
        this.type1 = type1;
        this.type2 = type2;
        this.p1 = p1;
        this.p2 = p2;
        this.body = Shape.segment(p1, p2);
        this.head1 = buildArrowHead(type1, 1);
        this.head2 = buildArrowHead(type2, 2);
        this.body.objectLabel = "body";
        this.head1.objectLabel = "head1";
        this.head2.objectLabel = "head2";

        head1.drawColor(this.body.getMp().getDrawColor());
        head1.fillColor(this.body.getMp().getDrawColor());
        head2.drawColor(this.body.getMp().getDrawColor());
        head2.fillColor(this.body.getMp().getDrawColor());
        mpArray = new MODrawPropertiesArray();
        mpArray.add(head1);
        mpArray.add(head2);
        mpArray.add(body);
    }

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2) {
        Arrow2D resul = makeSimpleArrow2D(p1, p2, ArrowType.TYPE_1);
        return resul;
    }

    public static Arrow2D makeSimpleArrow2D(Point p1, Point p2, ArrowType type) {
        Arrow2D resul = new Arrow2D(p1, p2, type, ArrowType.NONE);
        resul.style("arrowdefault");
        return resul;
    }

    public static Arrow2D makeDoubleArrow2D(Point p1, Point p2, ArrowType type1, ArrowType type2) {
        Arrow2D resul = new Arrow2D(p1, p2, type1, type2);
        resul.style("arrowdefault");
        return resul;
    }

    public static MultiShapeObject buildArrowHead(ArrowType type) {
        MultiShapeObject head;
        String name = "#arrow";
        if (type != ArrowType.NONE) {// If type=NONE, head=null
            switch (type) {
                case TYPE_1:
                    name += "1";
                    break;
                case TYPE_2:
                    name += "2";
                    break;
                case TYPE_3:
                    name += "3";
                    break;
                default:
                    name += "1";
                    break;
            }
            name += ".svg";
            try {
//            baseFileName = outputDir.getCanonicalPath() + File.separator + "arrows" + File.separator + name;
                ResourceLoader rl = new ResourceLoader();
                URL arrowUrl = rl.getResource(name, "svgResources/svg/arrows");
                head = new SVGMathObject(arrowUrl);

            } catch (NullPointerException ex) {
                JMathAnimScene.logger.error("Arrow head " + name + " not found");
                head = new MultiShapeObject();
            }

        } else {
            head = new MultiShapeObject();
        }
        return head;
    }

    private MultiShapeObject buildArrowHead(ArrowType type, int side) {
        int anchorValue;
        double scaleDefaultValue;
        switch (type) {
            case TYPE_1:
                anchorValue = 2;
                scaleDefaultValue = 1.5;
                break;
            case TYPE_2:
                anchorValue = 7;
                scaleDefaultValue = 1.5;
                break;
            case TYPE_3:
                anchorValue = 7;
                scaleDefaultValue = 1;
                break;
            default:
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

        MultiShapeObject resul = Arrow2D.buildArrowHead(type);
        resul.getMp().copyFrom(getMp());
        resul.thickness(4);
        resul.getMp().setLinecap(StrokeLineCap.SQUARE);
        return resul;
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
        update(JMathAnimConfig.getConfig().getScene());// TODO: Remove this coupling
        return arrowHeadToDraw1;
    }

    /**
     * Returns the arrow head 2, located at the beginning of the segment
     *
     * @return A SVGMathObject representing the arrow head
     */
    public MultiShapeObject getArrowHead2() {
        update(JMathAnimConfig.getConfig().getScene());// TODO: Remove this coupling
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
     * @param sc  Scale value. By default is 1.
     * @return This object
     */
    public final <T extends Arrow2D> T scaleArrowHead1(double sc) {
        this.getMp().setScaleArrowHead1(sc);
        return (T) this;
    }

    /**
     * Sets the scale of the arrow head 2
     *
     * @param <T> Implementation of Arrow2D
     * @param sc  Scale value. By default is 1.
     * @return This object
     */
    public final <T extends Arrow2D> T scaleArrowHead2(double sc) {
        this.getMp().setScaleArrowHead2(sc);
        return (T) this;
    }

    @Override
    public void restoreState() {
        super.restoreState();
        getMp().restoreState();
        body.restoreState();
        head1.restoreState();
        head2.restoreState();
    }

    @Override
    public void saveState() {
        super.saveState();
        getMp().saveState();
        body.saveState();
        head1.saveState();
        head2.saveState();
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        updateDrawableParts();
        if (isVisible()) {
            bodyToDraw.draw(scene, r, cam);
            arrowHeadToDraw1.draw(scene, r, cam);
            arrowHeadToDraw2.draw(scene, r, cam);
        }
        scene.markAsAlreadydrawn(this);
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        this.scene = scene;
        updateDrawableParts();

    }

    protected void updateDrawableParts() {
        bodyToDraw = body.copy();
        arrowHeadToDraw1 = this.head1.copy();
        arrowHeadToDraw2 = this.head2.copy();
        if (this.head1.size() > 0) {
            // Scaling
            double mw = getCamera().getMathView().getHeight();
            double sc1 = this.getMp().getScaleArrowHead1() * defaultArrowHead1Size1 * mw / head1.getBoundingBox().getHeight();
            arrowHeadToDraw1.scale(sc1);

            // Shifting
            Point headPoint = this.arrowHeadToDraw1.getBoundingBox().getUpper();
            this.arrowHeadToDraw1.shift(headPoint.to(p2));

            // Rotating
            Vec v = p1.to(p2);
            double angle = v.getAngle();
            AffineJTransform tr = AffineJTransform.create2DRotationTransform(p2, -Math.PI / 2 + angle);
            tr.applyTransform(arrowHeadToDraw1);

//            arrowHeadToDraw1.draw(scene.getConfig().getRenderer());
            JMPathPoint pa = bodyToDraw.get(1);
            pa.v.copyFrom(arrowHeadToDraw1.get(0).get(anchorPoint1).v);
            arrowHeadToDraw1.drawColor(getMp().getDrawColor());
            arrowHeadToDraw1.fillColor(getMp().getDrawColor());
            arrowHeadToDraw1.thickness(0);//A purely fill object
        }

        if (this.head2.size() > 0) {

            // Scaling
            double mw = JMathAnimConfig.getConfig().getCamera().getMathView().getHeight();
            double sc2 = this.getMp().getScaleArrowHead2() * defaultArrowHead1Size2 * mw / head2.getBoundingBox().getHeight();
            arrowHeadToDraw2.scale(sc2);
            // Shifting
            Point headPoint = this.arrowHeadToDraw2.getBoundingBox().getUpper();
            this.arrowHeadToDraw2.shift(headPoint.to(p1));

            // Rotating
            Vec v = p2.to(p1);
            double angle = v.getAngle();
            AffineJTransform tr = AffineJTransform.create2DRotationTransform(p1, -Math.PI / 2 + angle);
            tr.applyTransform(arrowHeadToDraw2);

            JMPathPoint pa = bodyToDraw.get(0);
            pa.v.copyFrom(arrowHeadToDraw2.get(0).get(anchorPoint2).v);
            arrowHeadToDraw2.drawColor(getMp().getDrawColor());
            arrowHeadToDraw2.fillColor(getMp().getDrawColor());
            arrowHeadToDraw2.thickness(0);//A purely fill object
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
        Arrow2D copy = new Arrow2D(p1.copy(), p2.copy(), this.type1, this.type2);
        copy.copyStateFrom(this);
        return (T) copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof Arrow2D)) {
            return;
        }
        Arrow2D ar = (Arrow2D) obj;
        this.getMp().copyFrom(ar.getMp());
        p1.copyStateFrom(ar.p1);
        p2.copyStateFrom(ar.p2);
        head1.copyStateFrom(ar.head1);
        head2.copyStateFrom(ar.head2);
        body.copyStateFrom(ar.body);
    }

    @Override
    public <T extends MathObject> T thickness(double th) {
        body.thickness(th);
        return (T) this;
    }

    @Override
    protected Rect computeBoundingBox() {
        updateDrawableParts();
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

    public MultiShapeObject getHead1() {
        return head1;
    }

    public MultiShapeObject getHead2() {
        return head2;
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        getBody().applyAffineTransform(tr);
        if (hasMPCreated())
            tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

    public enum ArrowType {
        NONE, TYPE_1, TYPE_2, TYPE_3
    }

}
