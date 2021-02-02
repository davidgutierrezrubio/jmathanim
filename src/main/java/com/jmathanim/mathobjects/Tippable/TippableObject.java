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
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * A MathObject that is permanently anchored to a specified point of a Shape.
 * For example: marks in segments do denote equal lengths, arrows to denote
 * directions, etc.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class TippableObject extends MathObject {

    public enum slopeDirection {
        NEGATIVE, POSITIVE
    }
    public static final double DELTA_DERIVATIVE = .0001;

    /**
     * Creates a new Tippable object
     *
     * @param shape Shape to anchor to
     * @param location Location of the shape. A number between 0 and 1.
     * @param direction Direction (POSITIVE or NEGATIVE) to compute the slope. A
     * value of the enum TippableObject.slopeDirection
     * @param tip MathObject to tip. This object is not drawed, but a copy of it
     * properly scaled and rotated.
     * @return The tippable object.
     */
    public static TippableObject make(Shape shape, double location, TippableObject.slopeDirection direction, MathObject tip) {
        TippableObject resul = new TippableObject();
        resul.shape = shape;
        resul.setLocation(location);
        resul.setDirection(direction);
        resul.setTip(tip);
        return resul;
    }

    /**
     * Creates a parallel sign
     *
     * @param shape Shape to anchor to
     * @param location Location of the shape. A number between 0 and 1.
     * @param numberOfMarks Number of marks to draw
     * @return The tippable object
     */
    public static TippableObject equalLengthTip(Shape shape, double location, int numberOfMarks) {
        MultiShapeObject parallelSign = new MultiShapeObject();
        for (int i = 0; i < numberOfMarks; i++) {
            parallelSign.add(Shape.segment(Point.at(0, i), Point.at(2, i)));
        }
        TippableObject resul = new TippableObject(); //shape,location,slopeDirection.POSITIVE,equalLengthTip);
        resul.shape = shape;
        resul.setLocation(location);
        resul.setTip(parallelSign);
        resul.setWidth(.05);
        resul.setAnchor(Anchor.Type.CENTER);
        resul.getTip().setAbsoluteSize();
        return resul;
    }

    public static TippableObject arrowHead(Shape shape, double location, slopeDirection direction, Arrow2D.ArrowType type) {
        MultiShapeObject arrowHead = Arrow2D.buildArrowHead(type);
        arrowHead.fillColor(shape.getMp().getDrawColor());
        arrowHead.drawColor(shape.getMp().getDrawColor());
        TippableObject resul = new TippableObject(); //shape,location,slopeDirection.POSITIVE,equalLengthTip);
        resul.shape = shape;
        resul.setLocation(location);
        resul.setTip(arrowHead);
        resul.setWidth(.05);
        resul.setAnchor(Anchor.Type.UPPER);
        resul.getTip().setAbsoluteSize();
        return resul;
    }

    private Anchor.Type anchor;
    private slopeDirection direction;
    MODrawPropertiesArray mpArray;

    private double offsetAngle = 0;
    private Point pointLoc;
    private Vec pointTo;
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

    public Anchor.Type getAnchor() {
        return anchor;
    }

    /**
     * Sets the type of the anchor used to position the tip with respect to the
     * point of the shape
     *
     * @param <T> Calling class
     * @param anchor Anchor
     * @return This object
     */
    public <T extends TippableObject> T setAnchor(Anchor.Type anchor) {
        this.anchor = anchor;
        return (T) this;
    }

    @Override
    public Rect getBoundingBox() {
        updateLocations();
        return getTipCopy().getBoundingBox();
    }

    public slopeDirection getDirection() {
        return direction;
    }

    /**
     * Returns the parameter of the tip location in the shape
     *
     * @return The location parameter, a number between 0 and 1
     */
    public double getLocation() {
        return tLocation;
    }

    /**
     * Sets the current location of the tip
     *
     * @param location A number between 0 and 1. If the number lies outside of
     * this range, it is normalized.
     */
    public final void setLocation(double location) {
        while (location > 1) {
            location -= 1;
        }
        while (location < 0) {
            location += 1;
        }
        this.tLocation = location;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

    /**
     * Returns the extra rotation angle to apply to the tip
     *
     * @return The offset angle
     */
    public double getOffsetAngle() {
        return offsetAngle;
    }

    /**
     * Returns the current object used to build the tip
     *
     * @return The tip
     */
    public MathObject getTip() {
        return tip;
    }

    /**
     * Sets the object to use to build the tip.Note that this object is not
     * drawed, but a copy properyl rotated and scaled.
     *
     * @param <T> Calling subclass
     * @param tip The tip
     * @return This object
     */
    public final <T extends TippableObject> T setTip(MathObject tip) {
        mpArray.remove(this.tip);
        this.tip = tip;
        mpArray.add(tip);
        return (T) this;
    }

    protected MathObject getTipCopy() {
        return tipCopy;
    }

    protected void setTipCopy(MathObject tipCopy) {
        this.tipCopy = tipCopy;
    }

    /**
     * Returns the tipped object, properly scaled and rotated. This is the
     * object that will be drawed in the next draw routine.
     *
     * @return The tipped object
     */
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

    /**
     * Sets the extra rotation angle to apply to the tip
     *
     * @param <T>
     * @param angle Offset angle. A value of 0 means that the tip is rotated
     * along the direction of the shape in the location point.
     * @return This object
     */
    public <T extends TippableObject> T setOffsetAngle(double angle) {
        this.offsetAngle = angle;
        return (T) this;
    }

    @Override
    public <T extends MathObject> T setWidth(double w) {
        tip.setWidth(w);
        return (T) this;
    }

    @Override
    public void update(JMathAnimScene scene) {
        updateLocations();
    }

    protected void updateLocations() {

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
        if (direction == TippableObject.slopeDirection.NEGATIVE) {
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

}
