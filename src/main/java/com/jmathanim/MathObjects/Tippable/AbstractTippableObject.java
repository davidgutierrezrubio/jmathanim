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
package com.jmathanim.MathObjects.Tippable;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.RotationType;
import com.jmathanim.Enum.SlopeDirectionType;
import com.jmathanim.MathObjects.*;
import com.jmathanim.Styling.DrawStylePropertiesObjectsArray;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Vec;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractTippableObject<T extends AbstractTippableObject<T>>
        extends Constructible<T> implements hasScalarParameter {

    public final Vec pivotPointRefMathObject;
    public final Vec markPoint;
    public final Vec pivotPointRefShape;
    public final RigidBox tipObjectRigidBox;
    private final DrawStylePropertiesObjectsArray mpArray;
    protected final hasPath path;
    public double locationParameterOnShape;
    //    public double rotationAngleAroundPivotPoint;
    public double rotationAngleAroundCenterOfMathObject;
    public boolean isParametrized;
    protected double correctionAngle;
    protected SlopeDirectionType slopeDirectionType;
    protected RotationType rotationType;
    private double distanceToShape;
    private boolean distanceToShapeRelative;
    private AnchorType anchor;
    private AnchorTypeUsed anchorType;
    private boolean alreadyRebuildingShape = false;
    private Vec anchorPoint;


    protected AbstractTippableObject(hasPath path, MathObject<?> tipObject, double location) {
        correctionAngle = PI / 2;
        this.path = path;
        addDependency(this.path.getPath());

        this.tipObjectRigidBox = new RigidBox(tipObject);
        this.locationParameterOnShape = location;
        this.slopeDirectionType = SlopeDirectionType.POSITIVE;
        distanceToShape = .5;
        distanceToShapeRelative = true;
        rotationType = RotationType.ROTATE;
//        rotationAngleAroundPivotPoint = 0;
        rotationAngleAroundCenterOfMathObject = 0;
        anchor = AnchorType.UPPER;
        anchorType = AnchorTypeUsed.ANCHOR;
        pivotPointRefMathObject = Vec.to(0, 0);

        //Point of the Shape
        markPoint = Vec.to(0, 0);

        //Reference point where the MathObject will be anchored, calculated at a certain distance from the markPoint
        pivotPointRefShape = Vec.to(0, 0);
        isParametrized = false;
        mpArray = new DrawStylePropertiesObjectsArray();
        mpArray.copyFrom(tipObject.getMp());
        mpArray.add(tipObjectRigidBox);
    }

    public T setAnchor(AnchorType anchor) {
        this.anchor = anchor;
        tipObjectRigidBox.resetMatrix();
        tipObjectRigidBox.rotate(rotationAngleAroundCenterOfMathObject);
        computePivotPointRefMathObject();
        anchorType = AnchorTypeUsed.ANCHOR;
        rebuildShape();
        return (T) this;
    }

    private void computePivotPointRefMathObject() {
        if (this.anchor == AnchorType.BY_POINT) {
            if (anchorPoint == null) {
                anchorPoint = Anchor.getAnchorPoint(tipObjectRigidBox, AnchorType.CENTER);
            }
            this.pivotPointRefMathObject.copyCoordinatesFrom(anchorPoint);
        } else {
            this.pivotPointRefMathObject.copyCoordinatesFrom(Anchor.getAnchorPoint(tipObjectRigidBox, this.anchor));
        }
    }

    protected T setAnchorPoint(Coordinates<?> anchorPoint) {
        this.anchorPoint = anchorPoint.getVec();
        this.pivotPointRefMathObject.copyCoordinatesFrom(this.anchorPoint);
        anchorType = AnchorTypeUsed.FIXED_POINT;
        rebuildShape();
        return (T) this;
    }

    /**
     * Checks if the distance to the shape is relative to the tip object's height.
     *
     * @return True if the distance is relative, false otherwise.
     */
    public boolean isDistanceToShapeRelative() {
        return distanceToShapeRelative;
    }

    /**
     * Sets whether the distance to the shape should be relative to the tip object's height.
     *
     * @param distanceToShapeRelative True to make the distance relative, false for absolute.
     */
    public void setDistanceToShapeRelative(boolean distanceToShapeRelative) {
        this.distanceToShapeRelative = distanceToShapeRelative;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public T setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
        rebuildShape();
        return (T) this;
    }

    public double getDistanceToShape() {
        return distanceToShape;
    }

    public T setDistanceToShape(double distanceToShape) {
        this.distanceToShape = distanceToShape;
        rebuildShape();
        return (T) this;
    }

    @Override
    public T applyAffineTransform(AffineJTransform affineJTransform) {
        if (isFreeMathObject()) {
            getMathObject().applyAffineTransform(affineJTransform);
        } else {
            tipObjectRigidBox.applyAffineTransformToBaseTransform(affineJTransform);
        }
        return (T) this;
    }

    @Override
    public MathObject<?> getMathObject() {
        return tipObjectRigidBox;
    }

    @Override
    public DrawStylePropertiesObjectsArray getMp() {
        return mpArray;
    }

    @Override
    public void copyStateFrom(Stateable obj) {

        if (!(obj instanceof AbstractTippableObject)) return;
        super.copyStateFrom(obj);
        AbstractTippableObject<?> nt = (AbstractTippableObject<?>) obj;
        pivotPointRefMathObject.copyCoordinatesFrom(nt.pivotPointRefMathObject);
        correctionAngle = nt.correctionAngle;
        this.tipObjectRigidBox.copyStateFrom(nt.tipObjectRigidBox);
        this.tipObjectRigidBox.getReferenceMathObject().copyStateFrom(nt.tipObjectRigidBox.getReferenceMathObject());
        this.pivotPointRefMathObject.copyStateFrom(nt.pivotPointRefMathObject);
        this.locationParameterOnShape = nt.locationParameterOnShape;
        this.distanceToShape = nt.distanceToShape;
        this.rotationType = nt.rotationType;
//            this.rotationAngleAroundPivotPoint = nt.rotationAngleAroundPivotPoint;
        this.markPoint.copyStateFrom(nt.markPoint);
        this.pivotPointRefShape.copyStateFrom(nt.pivotPointRefShape);
        this.isParametrized = nt.isParametrized;
        this.slopeDirectionType = nt.slopeDirectionType;
        rebuildShape();

    }

    @Override
    public void rebuildShape() {
        if ((isFreeMathObject()) || (path.getPath().isEmpty()) || (alreadyRebuildingShape)) {
            return;
        }
        //Reset. There may be a problem with scalars, as copyStateFrom overwrites scalars
        //TODO: This is not efficient. Both refMathObject and mathobject have to be updated with this code
//        tipObjectRigidBox.getReferenceMathObject().performMathObjectUpdateActions(scene);//This is needed as text content must be recreated if scalars changed
//        tipObjectRigidBox.markDirty();
//        tipObjectRigidBox.getReferenceMathObject().markDirty();
//        tipObjectRigidBox.update();//This is needed as text content must be recreated if scalars changed
        tipObjectRigidBox.resetMatrix();
        tipObjectRigidBox.rotate(rotationAngleAroundCenterOfMathObject);


        Vec tangent;
        if (isParametrized) {
            tangent = path.getPath().getParametrizedSlopeAt(locationParameterOnShape, slopeDirectionType == SlopeDirectionType.POSITIVE);
        } else {
            tangent = path.getPath().getSlopeAt(locationParameterOnShape, slopeDirectionType == SlopeDirectionType.POSITIVE);
        }

        Vec normal = Vec.to(-tangent.y, tangent.x).normalize();//Normal vec, rotated 90º counterclockwise


        double tangentAngle = tangent.getAngle();
        double totalRotationAngle = tangentAngle + correctionAngle;
        ;
        switch (rotationType) {
            case FIXED:
                if (anchorType == AnchorTypeUsed.ANCHOR) {
                    tipObjectRigidBox.rotate(-totalRotationAngle);
                    computePivotPointRefMathObject();
                }
            case ROTATE:
                totalRotationAngle = tangentAngle + correctionAngle;
                break;
            case SMART:
//                System.out.println(tangent.getAngle()*180/PI);
                if ((tangentAngle > PI / 2) && (tangentAngle < 3 * PI / 2))
                    if (anchorType == AnchorTypeUsed.ANCHOR) {
                        tipObjectRigidBox.rotate(PI);
                        computePivotPointRefMathObject();
                    }
                break;


        }

        //Compute the variable this.pivotPointRefMathObject
        computePivotPointRefMathObject();

        tipObjectRigidBox.rotate(pivotPointRefMathObject, totalRotationAngle);


        if (isParametrized) {
            markPoint.copyCoordinatesFrom(path.getPath().getParametrizedVecAt(locationParameterOnShape));
        } else {
            markPoint.copyCoordinatesFrom(path.getPath().getJMPointAt(locationParameterOnShape));
        }
//        Point labelAnchorPointDst=markPoint.copy();
        pivotPointRefShape.copyCoordinatesFrom(markPoint);


        double dist = (distanceToShapeRelative ? distanceToShape * tipObjectRigidBox.getHeight() : distanceToShape);
        pivotPointRefShape.shift(normal.scale(dist));


        Vec shiftVector = pivotPointRefMathObject.to(pivotPointRefShape);
        tipObjectRigidBox.shift(shiftVector);

        alreadyRebuildingShape = false;
    }

    @Override
    public double getValue() {
        return locationParameterOnShape;
    }

    @Override
    public void setValue(double scalar) {
        locationParameterOnShape = scalar;
        rebuildShape();
    }

    public SlopeDirectionType getSlopeDirection() {
        return slopeDirectionType;
    }

    public T setSlopeDirection(SlopeDirectionType slopeDirection) {
        this.slopeDirectionType = slopeDirection;
        rebuildShape();
        return (T) this;
    }

    public Vec getMarkLabelLocation() {
        return markPoint;
    }

    @Override
    public void performMathObjectUpdateActions() {
//        System.out.println("AbstractTippableObject.performMathObjectUpdateActions!!!!!!!");
        rebuildShape();
    }

    @Override
    public boolean needsUpdate() {
        return super.needsUpdate();
    }

    @Override
    public boolean update() {
        return super.update();
    }

    private enum AnchorTypeUsed {ANCHOR, FIXED_POINT}
}
