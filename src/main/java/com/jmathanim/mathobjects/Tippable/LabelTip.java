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

import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.RotationType;
import com.jmathanim.Enum.SlopeDirectionType;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;
import com.jmathanim.mathobjects.Text.AbstractLatexMathObject;
import com.jmathanim.mathobjects.Text.LatexMathObject;
import com.jmathanim.mathobjects.updaters.Updater;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * A tippable LaTexMathObject
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class LabelTip extends AbstractTippableObject<LabelTip> implements hasArguments {


    private final AbstractLatexMathObject<?> laTeXMathObject;

    protected LabelTip(AbstractShape<?> shape, AbstractLatexMathObject<?> tipLabel, AnchorType anchor, double locationParameter) {
        super(shape, tipLabel, locationParameter);
        setAnchor(anchor);
        laTeXMathObject = tipLabel;
        correctionAngle = 0;
        distanceToShape = .25 * tipLabel.getHeight();
    }


    protected LabelTip(AbstractShape<?> shape, AbstractLatexMathObject<?> tipObject, Coordinates<?> anchorPoint, double locationParameter) {
        super(shape, tipObject, locationParameter);
        setAnchorPoint(anchorPoint);
        laTeXMathObject = tipObject;
        correctionAngle = 0;
        distanceToShape = .25 * tipObject.getHeight();
    }

    /**
     * Overloaded method. Creates a LaTeX label from a String to the given Shape
     *
     * @param shape             Shape to add label
     * @param locationParameter Location parameter where to put the label, from 0 to 1
     * @param latexText         Text to display. A LaTeX String.
     * @param upSide            If true, label will be located in the upper side of the shape. In this context, upper
     *                          means a 90 degrees counterclockwise deviation from positive tangent. If false, label
     *                          will be located in the opposite side. You should set this to false if you want labels in
     *                          the exterior or counterclockwise shapes like Shape.circle() for example.
     * @return The LabelTip object created
     */
    public static LabelTip makeLabelTip(AbstractShape<?> shape, double locationParameter, String latexText, boolean upSide) {
        LabelTip resul = makeLabelTip(shape, locationParameter, LatexMathObject.make(latexText), upSide);
        if (!upSide) {
            resul.setSlopeDirection(SlopeDirectionType.NEGATIVE)
                    .setAnchor(AnchorType.LOWER);
        }
        resul.correctionAngle=0;
        resul.setRotationType(RotationType.SMART);
        return resul;
    }

    /**
     * /** Overloaded method. Creates a LaTeX label from a String to the given Shape. Label is located in the upper side
     * of the Shape (90 degrees counterclockwise deviation from positive tangent)
     *
     * @param shape             Shape to add label
     * @param locationParameter Location parameter where to put the label, from 0 to 1
     * @param latexText         Text to display. A LaTeX String.
     * @return The LabelTip object created
     */
    public static LabelTip makeLabelTip(AbstractShape<?> shape, double locationParameter, String latexText) {
        return makeLabelTip(shape, locationParameter, latexText, false);
    }


    /**
     * Creates a label in the middle of segment AB with the distance between the points. Text is automatically updated
     * every frame.
     *
     * @param A      First Point
     * @param B      Second Point
     * @param format Decimal format of length. Use a format string as defined in DecimalFormat class
     * @param upSide If true, label will be located in the upper side of the shape. In this context, upper means a 90
     *               degrees counterclockwise deviation from positive tangent. If false, label will be located in the
     *               opposite side.
     * @return The LabelTip object created
     */
    public static LabelTip makeLengthLabel(Point A, Point B, String format, boolean upSide) {
        LatexMathObject t = LatexMathObject.make("${#0}$");
        LabelTip resul = new LabelTip(Shape.segment(A, B), t, AnchorType.LOWER, .5);
        if (!upSide) {
            resul.setSlopeDirection(SlopeDirectionType.NEGATIVE)
                    .setAnchor(AnchorType.LOWER);
        }
        t.registerUpdater(new Updater() {
            @Override
            public void update(JMathAnimScene scene) {
                t.getArg(0).setValue(A.to(B).norm());
            }
        });
        resul.setRotationType(RotationType.SMART);
//        resul.correctionAngle=PI;
        resul.rebuildShape();
        return resul;
    }


    /**
     * Attach an AbstractLaTeXMathObject instance to a specific point of a Shape. The LaTeX is attached outside the
     * point to a distance of 1/4 the LaTeX object height.
     *
     * @param shape             Shape to attach the tip
     * @param locationParameter Point of the shape to locate the tip. A parameter between 0 and 1. Values outside this
     *                          range are normalized.
     * @param tipLabel          LaTeX object
     * @param upSide            If true, label will be located in the upper side of the shape. In this context, upper
     *                          means a 90 degrees counterclockwise deviation from positive tangent. If false, label
     *                          will be located in the opposite side.
     * @return The tippable object
     */
    public static LabelTip makeLabelTip(AbstractShape<?> shape, double locationParameter, AbstractLatexMathObject<?> tipLabel, boolean upSide) {

        LabelTip resul = new LabelTip(shape, tipLabel, AnchorType.LOWER, locationParameter);
        resul.setDistanceToShape(tipLabel.getHeight() * .25);
        if (!upSide) {
            resul.setSlopeDirection(SlopeDirectionType.NEGATIVE)
                    .setAnchor(AnchorType.UPPER);
        }
        resul.correctionAngle=PI;
        resul.rebuildShape();
        return resul;
    }

    @Override
    public LabelTip copy() {
        LabelTip copy = new LabelTip(shape, laTeXMathObject.copy(), pivotPointRefMathObject.copy(), locationParameterOnShape);
        copy.copyStateFrom(this);
        return copy;
    }

    public AbstractLatexMathObject<?> getLaTeXObject() {
        return laTeXMathObject;
    }


    @Override
    public Scalar getArg(int n) {
        return laTeXMathObject.getArg(n);
    }

    @Override
    protected boolean isHasBeenUpdated() {
        return super.isHasBeenUpdated() && MediatorMathObject.isHasBeenUpdated(laTeXMathObject);
    }

    @Override
    protected void setHasBeenUpdated(boolean hasBeenUpdated) {
        super.setHasBeenUpdated(hasBeenUpdated);
        if (!hasBeenUpdated) MediatorMathObject.setHasBeenUpdated(laTeXMathObject,hasBeenUpdated);
    }



}
