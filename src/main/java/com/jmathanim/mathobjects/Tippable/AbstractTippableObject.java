/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.mathobjects.Tippable;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.hasScalarParameter;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractTippableObject extends Constructible implements hasScalarParameter {

    public Shape shape;
    public MathObject mathobject, refMathObject;
    public final Point pivotPointRefMathObject;
    public double locationParameterOnShape;

    public double distanceToShape;

    public double rotationAngle;
    protected double correctionAngle;
    private boolean fixed;

    public final Point markPoint;
    public final Point locationPoint;

    public boolean isParametrized;
    private final MODrawPropertiesArray mpArray;

    public enum SlopeDirectionType {
        NEGATIVE, POSITIVE
    }
    protected SlopeDirectionType slopeDirectionType;

    protected AbstractTippableObject(Shape shape, MathObject tipObject, Point anchorPoint, double location) {
        correctionAngle = PI / 2;
        this.shape = shape;
        this.mathobject = tipObject.copy();
        this.refMathObject = tipObject;
        this.pivotPointRefMathObject = anchorPoint;
        this.locationParameterOnShape = location;
        distanceToShape = 0;
        fixed = false;
        rotationAngle = 0;
        markPoint = Point.origin();
        locationPoint = Point.origin().visible(false);
        isParametrized = false;
        scene.add(locationPoint);
        mpArray = new MODrawPropertiesArray();
        mpArray.add(refMathObject);
        mpArray.add(mathobject);
        mpArray.copyFrom(refMathObject.getMp());
    }

    public AbstractTippableObject setAnchor(Anchor.Type anchor) {
        this.pivotPointRefMathObject.v.copyFrom(Anchor.getAnchorPoint(refMathObject, anchor).v);
        rebuildShape();
        return this;
    }

    public AbstractTippableObject setAnchorPoint(Point anchorPoint) {
        this.pivotPointRefMathObject.v.copyFrom(anchorPoint.v);
        rebuildShape();
        return this;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public AbstractTippableObject setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
        rebuildShape();
        return this;
    }

    public boolean isFixed() {
        return fixed;
    }

    public AbstractTippableObject setFixedAngle(boolean fixed) {
        this.fixed = fixed;
        rebuildShape();
        return this;
    }

    public double getDistanceToShape() {
        return distanceToShape;
    }

    public void setDistanceToShape(double distanceToShape) {
        this.distanceToShape = distanceToShape;
        rebuildShape();
    }

    @Override
    public Constructible applyAffineTransform(AffineJTransform transform) {
        if (isThisMathObjectFree()) {
            getMathObject().applyAffineTransform(transform);
        } else {
            refMathObject.applyAffineTransform(transform);
            pivotPointRefMathObject.applyAffineTransform(transform);
            rebuildShape();
        }
        return this;
    }

    @Override
    public MathObject getMathObject() {
        return mathobject;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        super.copyStateFrom(obj);
        if (obj instanceof AbstractTippableObject) {
            AbstractTippableObject nt = (AbstractTippableObject) obj;
            pivotPointRefMathObject.copyFrom(nt.pivotPointRefMathObject);
            correctionAngle = nt.correctionAngle;
            this.mathobject.copyStateFrom(nt.mathobject);
            this.refMathObject.copyStateFrom(nt.refMathObject);
            this.pivotPointRefMathObject.copyStateFrom(nt.pivotPointRefMathObject);
            this.locationParameterOnShape = nt.locationParameterOnShape;
            this.distanceToShape = nt.distanceToShape;
            this.fixed = nt.fixed;
            this.rotationAngle = nt.rotationAngle;
            this.markPoint.copyStateFrom(nt.markPoint);
            this.locationPoint.copyStateFrom(nt.locationPoint);
            this.isParametrized = nt.isParametrized;
            rebuildShape();
        }

    }

    @Override
    public void rebuildShape() {
        if (isThisMathObjectFree()) {
            return;
        }
        //Reset
        mathobject.copyStateFrom(refMathObject);
        Vec tangent;
        if (isParametrized) {
            tangent = shape.getPath().getParametrizedSlopeAt(locationParameterOnShape, slopeDirectionType == SlopeDirectionType.POSITIVE);
        } else {
            tangent = shape.getPath().getSlopeAt(locationParameterOnShape, slopeDirectionType == SlopeDirectionType.POSITIVE);
        }

        Vec normal = Vec.to(-tangent.y, tangent.x).normalize();

        if (!fixed) {
            double angle = tangent.getAngle();
            angle += rotationAngle;
            angle -= correctionAngle;
            mathobject.rotate(pivotPointRefMathObject, angle);
        }

        if (isParametrized) {
            markPoint.v.copyFrom(shape.getParametrizedPointAt(locationParameterOnShape).v);
        } else {
            markPoint.v.copyFrom(shape.getPointAt(locationParameterOnShape).v);
        }

        locationPoint.v.copyFrom(markPoint.v);
        locationPoint.v.addInSite(normal.multInSite(distanceToShape));
        Vec shiftVector = pivotPointRefMathObject.to(locationPoint);
        mathobject.shift(shiftVector);

    }

    @Override
    public double getScalar() {
        return locationParameterOnShape;
    }

    @Override
    public void setScalar(double scalar) {
        locationParameterOnShape = scalar;
        rebuildShape();
    }

    public SlopeDirectionType getSlopeDirection() {
        return slopeDirectionType;
    }

    public AbstractTippableObject setSlopeDirection(SlopeDirectionType slopeDirection) {
        this.slopeDirectionType = slopeDirection;
        rebuildShape();
        return this;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, this.shape);
    }

    public Point getMarkPoint() {
        return markPoint;
    }

    public <T extends AbstractTippableObject> T visibleMarkPoint(boolean visible) {
        markPoint.visible(visible);
        return (T) this;
    }
}
