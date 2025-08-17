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
import com.jmathanim.Enum.RotationType;
import com.jmathanim.Enum.SlopeDirectionType;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractTippableObject extends Constructible implements hasScalarParameter {

    public final Point pivotPointRefMathObject;
    public final Point markPoint;
    public final Point pivotPointRefShape;
    public final RigidBox tipObjectRigidBox;
    private final MODrawPropertiesArray mpArray;
    public Shape shape;
    public double locationParameterOnShape;
    public Double distanceToShape;
    //    public double rotationAngleAroundPivotPoint;
    public double rotationAngleAroundCenterOfMathObject;
    public boolean isParametrized;
    protected double correctionAngle;
    protected SlopeDirectionType slopeDirectionType;
    protected RotationType rotationType;
    private Anchor.Type anchor;
    private AnchorTypeUsed anchorType;
    private boolean alreadyRebuildingShape = false;

    protected AbstractTippableObject(Shape shape, MathObject tipObject, double location) {
        correctionAngle = PI / 2;
        this.shape = shape;
        this.tipObjectRigidBox = new RigidBox(tipObject);
        this.locationParameterOnShape = location;
        this.slopeDirectionType = SlopeDirectionType.POSITIVE;
        distanceToShape = null;
        rotationType = RotationType.ROTATE;
//        rotationAngleAroundPivotPoint = 0;
        rotationAngleAroundCenterOfMathObject = 0;
        anchor = Anchor.Type.UPPER;
        anchorType = AnchorTypeUsed.ANCHOR;
        pivotPointRefMathObject = new Point();

        //Point of the Shape
        markPoint = Point.origin();

        //Reference point where the MathObject will be anchored, calculated at a certain distance from the markPoint
        pivotPointRefShape = Point.origin();
        isParametrized = false;
        mpArray = new MODrawPropertiesArray();
        mpArray.copyFrom(tipObject.getMp());
        mpArray.add(tipObjectRigidBox);
    }

    public <T extends AbstractTippableObject> T setAnchor(Anchor.Type anchor) {
        this.anchor = anchor;
        tipObjectRigidBox.resetMatrix();
        tipObjectRigidBox.rotate(rotationAngleAroundCenterOfMathObject);
        computePivotPointRefMathObject();
        anchorType = AnchorTypeUsed.ANCHOR;
        rebuildShape();
        return (T) this;
    }

    private void computePivotPointRefMathObject() {
        this.pivotPointRefMathObject.v.copyFrom(Anchor.getAnchorPoint(tipObjectRigidBox, Anchor.reverseAnchorPoint(this.anchor)).v);
    }

    public <T extends AbstractTippableObject> T setAnchorPoint(Point anchorPoint) {
        this.pivotPointRefMathObject.v.copyFrom(anchorPoint.v);
        anchorType = AnchorTypeUsed.FIXED_POINT;
        rebuildShape();
        return (T) this;
    }

//    private <T extends AbstractTippableObject> T setRotationAngleAroundCenterOfMathObject(double rotationAngleAroundCenterOfMathObject) {
//        this.rotationAngleAroundCenterOfMathObject = rotationAngleAroundCenterOfMathObject;
//        if (anchorType == AnchorTypeUsed.ANCHOR) {
//            setAnchor(anchor);
//        }
//        rebuildShape();
//        return (T) this;
//    }

//    public double getRotationAngleAroundPivotPoint() {
//        return rotationAngleAroundPivotPoint;
//    }
//
//    public <T extends AbstractTippableObject> T setRotationAngleAroundPivotPoint(double rotationAngleAroundPivotPoint) {
//        this.rotationAngleAroundPivotPoint = rotationAngleAroundPivotPoint;
//        rebuildShape();
//        return (T) this;
//    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public <T extends AbstractTippableObject> T setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
        return (T) this;
    }

    public double getDistanceToShape() {
        return distanceToShape;
    }

    public <T extends AbstractTippableObject> T setDistanceToShape(Double distanceToShape) {
        this.distanceToShape = distanceToShape;
        rebuildShape();
        return (T) this;
    }

    @Override
    public Constructible applyAffineTransform(AffineJTransform transform) {
        if (isFreeMathObject()) {
            getMathObject().applyAffineTransform(transform);
        }
        return this;
    }

    @Override
    public MathObject getMathObject() {
        return tipObjectRigidBox;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (obj instanceof AbstractTippableObject) {
            AbstractTippableObject nt = (AbstractTippableObject) obj;
            pivotPointRefMathObject.v.copyFrom(nt.pivotPointRefMathObject.v);
            correctionAngle = nt.correctionAngle;
            this.tipObjectRigidBox.copyStateFrom(nt.tipObjectRigidBox);
            this.tipObjectRigidBox.getMathObject().copyStateFrom(nt.tipObjectRigidBox.getMathObject());
            this.pivotPointRefMathObject.copyStateFrom(nt.pivotPointRefMathObject);
            this.locationParameterOnShape = nt.locationParameterOnShape;
            this.distanceToShape = nt.distanceToShape;
            this.rotationType = nt.rotationType;
//            this.rotationAngleAroundPivotPoint = nt.rotationAngleAroundPivotPoint;
            this.markPoint.copyStateFrom(nt.markPoint);
            this.pivotPointRefShape.copyStateFrom(nt.pivotPointRefShape);
            this.isParametrized = nt.isParametrized;
            rebuildShape();
        }

    }

    @Override
    public void rebuildShape() {
        if ((isFreeMathObject()) || (shape.isEmpty()) || (alreadyRebuildingShape)) {
            return;
        }
        //Reset. There may be a problem with scalars, as copyStateFrom overwrites scalars
        //TODO: This is not efficient. Both refMathObject and mathobject have to be updated with this code
        tipObjectRigidBox.update(scene);//This is needed as text content must be recreated if scalars changed
        tipObjectRigidBox.resetMatrix();
        tipObjectRigidBox.rotate(rotationAngleAroundCenterOfMathObject);


        Vec tangent;
        if (isParametrized) {
            tangent = shape.getPath().getParametrizedSlopeAt(locationParameterOnShape, slopeDirectionType == SlopeDirectionType.POSITIVE);
        } else {
            tangent = shape.getPath().getSlopeAt(locationParameterOnShape, slopeDirectionType == SlopeDirectionType.POSITIVE);
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
                if ((tangentAngle>PI/2)&&(tangentAngle<3*PI/2))
                    if (anchorType == AnchorTypeUsed.ANCHOR) {
                        tipObjectRigidBox.rotate(PI);
                        computePivotPointRefMathObject();
                    }
                break;


        }
        tipObjectRigidBox.rotate(pivotPointRefMathObject, totalRotationAngle);


        if (isParametrized) {
            markPoint.v.copyFrom(shape.getParametrizedPointAt(locationParameterOnShape).v);
        } else {
            markPoint.v.copyFrom(shape.getPointAt(locationParameterOnShape).v);
        }
//        Point labelAnchorPointDst=markPoint.copy();
        pivotPointRefShape.v.copyFrom(markPoint.v);

        if (distanceToShape != null)
            pivotPointRefShape.v.addInSite(normal.multInSite(distanceToShape));
        else
            pivotPointRefShape.v.addInSite(normal.multInSite(tipObjectRigidBox.getHeight() * .5));
        Vec shiftVector = pivotPointRefMathObject.to(pivotPointRefShape);
        tipObjectRigidBox.shift(shiftVector);

        alreadyRebuildingShape = false;
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

    public <T extends AbstractTippableObject> T setSlopeDirection(SlopeDirectionType slopeDirection) {
        this.slopeDirectionType = slopeDirection;
        rebuildShape();
        return (T) this;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, this.shape);
    }

    public Point getMarkPoint() {
        return markPoint;
    }

    private enum AnchorTypeUsed {ANCHOR, FIXED_POINT}

}
