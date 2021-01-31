/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects.Tippable;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import static com.jmathanim.mathobjects.Tippable.ArrowTip.make;

/**
 * A MathObject that is permanently anchored to a specified point of a Shape.
 * For example: marks in segments do denote equal lengths, arrows to denote
 * directions, etc.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class TippableObject extends MathObject {

    public static final double DELTA_DERIVATIVE = .0001;
    private slopeDirection direction;
    private Anchor.Type anchor;
    MODrawPropertiesArray mpArray;

    private double offsetAngle = 0;
    private Point pointLoc;
    private Vec pointTo;
//    abstract public void updateLocations(JMPathPoint location);
    private double scaleFactorX;
    private double scaleFactorY;
    protected Shape shape;
    private double tLocation;
    private MathObject tip;
    private MathObject tipCopy;
    protected double totalRotationAngle;

    public TippableObject() {
        super();
        mpArray = new MODrawPropertiesArray();
        scaleFactorX = 1;
        scaleFactorY = 1;
        anchor = Anchor.Type.UPPER;//Default anchor value
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

    @Override
    public <T extends MathObject> T copy() {
        return (T) make(shape, getLocation(), direction, getTip().copy());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (isVisible()) {
            getTipCopy().draw(scene, r);
            scene.markAsAlreadyDrawed(this);
        }
    }

    @Override
    public Rect getBoundingBox() {
        updateLocations();
        return getTipCopy().getBoundingBox();
    }

    public slopeDirection getDirection() {
        return direction;
    }

    public double getLocation() {
        return tLocation;
    }

    public final void setLocation(double location) {
        this.tLocation = location;
    }

    public double getOffsetAngle() {
        return offsetAngle;
    }

    public MathObject getTip() {
        return tip;
    }

    public final void setTip(MathObject tip) {
        mpArray.remove(this.tip);
        this.tip = tip;
        mpArray.add(tip);
    }

    protected MathObject getTipCopy() {
        return tipCopy;
    }

    protected void setTipCopy(MathObject tipCopy) {
        this.tipCopy = tipCopy;
    }

    public MathObject getTippedObject() {
        update(scene);
        return getTipCopy();
    }

    @Override
    public int getUpdateLevel() {
        return shape.getUpdateLevel() + 1;
    }

    @Override
    public void restoreState() {
        super.restoreState();
        tip.restoreState();
    }

    @Override
    public <T extends MathObject> T rotate(double angle) {
        tip.rotate(angle);
        return (T) this;
    }

    @Override
    public void saveState() {
        super.saveState();
        tip.saveState();
    }

    @Override
    public <T extends MathObject> T scale(double sx, double sy) {
        scaleFactorX *= sx;
        scaleFactorY *= sy;
        return (T) this;
    }

    @Override
    public <T extends MathObject> T scale(double s) {
        return this.scale(s, s);
    }

    public final <T extends MathObject> T setDirection(slopeDirection direction) {
        this.direction = direction;
        return (T) this;
    }

    @Override
    public <T extends MathObject> T setHeight(double h) {
        tip.setHeight(h);
        return (T) this;
    }

    public <T extends TippableObject> T setOffsetAngle(double angle) {
        this.offsetAngle = angle;
        return (T) this;
    }

    @Override
    public <T extends MathObject> T setWidth(double w) {
        tip.setWidth(w);
        return (T) this;
    }

    public Anchor.Type getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor.Type anchor) {
        this.anchor = anchor;
    }

    @Override
    public void update(JMathAnimScene scene) {
        updateLocations();
    }

    public void updateLocations() {

        this.pointLoc = shape.getPath().getPointAt(getLocation()).p;
        Point slopeTo;

        setTipCopy(getTip().copy());
//        getTipCopy().setHeight(.1);
        getTipCopy().scale(scaleFactorX, scaleFactorY);
        //Shifting
        Point headPoint = Anchor.getAnchorPoint(getTipCopy(), anchor);
//        Point headPoint = this.getTipCopy().getBoundingBox().getUpper();
        this.getTipCopy().shift(headPoint.to(pointLoc));

        //Rotating
        if (direction == ArrowTip.slopeDirection.NEGATIVE) {
//            this.pointTo = jmp.p.to(jmp.cpEnter);
            slopeTo = shape.getPath().getPointAt(getLocation() - DELTA_DERIVATIVE).p;

        } else {
            slopeTo = shape.getPath().getPointAt(getLocation() + DELTA_DERIVATIVE).p;
        }
        pointTo = pointLoc.to(slopeTo);
        double rotAngle = pointTo.getAngle();
        totalRotationAngle = -Math.PI / 2 + rotAngle + getOffsetAngle();
//        AffineJTransform tr = AffineJTransform.create2DRotationTransform(pointLoc, -Math.PI / 2 + rotAngle);
//        tr.applyTransform(getTipCopy());
        getTipCopy().rotate(pointLoc, totalRotationAngle);
    }

    public enum slopeDirection {
        NEGATIVE, POSITIVE
    }

    public static TippableObject parallelSign(Shape shape, double location, int numberOfMarks) {
        MultiShapeObject parallelSign = new MultiShapeObject();
        for (int i = 0; i < numberOfMarks; i++) {
            parallelSign.add(Shape.segment(Point.at(0, i), Point.at(2, i)));
        }
        TippableObject resul = new TippableObject(); //shape,location,slopeDirection.POSITIVE,parallelSign);
        resul.shape = shape;
        resul.setLocation(location);
        resul.setTip(parallelSign);
//        parallelSign.drawColor("black");
        resul.setWidth(.05);
        resul.setAnchor(Anchor.Type.CENTER);
        resul.getTip().setAbsoluteSize();
        return resul;
    }

//    public static TippableObject labelText(Shape shape, double location, String text) {
//        MathObjectGroup tip = MathObjectGroup.make();
//        tip.add(LaTeXMathObject.make(text));
//        tip.add(Point.at(0, 0));
//        tip.add(Point.at(0, 0));
//        tip.add(Point.at(0, 0));
//        tip.add(Point.at(0, 0));
//        tip.add(Point.at(0, 0));
//        tip.add(Point.at(0, 0));
//        tip.setLayout(Anchor.Type.LOWER, .05);
//        TippableObject resul = new TippableObject();
//        resul.shape = shape;
//        resul.setLocation(location);
//        resul.setTip(tip);
//        resul.setAnchor(Anchor.Type.LOWER);
//        resul.setOffsetAngle(-PI / 2);
//        return resul;
//    }
}
