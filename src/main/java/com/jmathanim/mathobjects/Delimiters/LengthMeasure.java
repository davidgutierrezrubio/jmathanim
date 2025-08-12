/*
 * Copyright (C) 2021 David
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
package com.jmathanim.mathobjects.Delimiters;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.mathobjects.*;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 *
 * @author David
 */
 class LengthMeasure extends Delimiter {

    private final double hgap;
    private final MathObjectGroup delimiterShapeGroup;

    public enum TYPE {
        ARROW, SIMPLE
    }

    public static LengthMeasure make(Point A, Point B, Delimiter.Type type, double gap) {
        LengthMeasure resul = new LengthMeasure(A, B, type, gap);
        resul.buildDelimiterShape();
        return resul;
    }

    private LengthMeasure(Point A, Point B, Type type, double gap) {
        super(A, B, type, gap);
        this.gap = gap;
        hgap = .05;
        minimumWidthToShrink = .5;
        delimiterShapeGroup = MathObjectGroup.make();

    }
    @Override
    protected MathObjectGroup buildDelimiterShape() {
        delimiterLabelToDraw = MathObjectUtils.getSafeCopyOf(delimiterLabel);
        double width = A.to(B).norm();
        double angle = A.to(B).getAngle();
        Point AA = Point.at(0, 0);
        Point BB = Point.at(width, 0);
        double realAmplitudeScale = UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);

        double gapToUse = hgap * realAmplitudeScale;
        delimiterLabelToDraw.scale(realAmplitudeScale);
        double vCenter = .025 * delimiterScale;
         delimiterShapeGroup.clear();
//        Shape verticalBar = Shape.segment(Point.at(0, 0), Point.at(0, 2 * vCenter));
        double xOffset = 0;
        switch (type) {
            case LENGTH_ARROW:
                xOffset = vCenter;
                break;
            case LENGTH_BRACKET:
                xOffset = 0;
                break;
        }
        Shape verticalBar = Shape.polyLine(Point.at(xOffset, -vCenter), Point.at(0, 0), Point.at(xOffset, vCenter));
        delimiterShapeGroup.add(verticalBar);

        if (delimiterLabelToDraw instanceof  NullMathObject) {
            final Shape segment = Shape.segment(Point.at(0, 0), Point.at(width, 0));
            delimiterShapeGroup.add(segment);
        }
        else {
            double segmentLength = .5 * (width - delimiterLabelToDraw.getWidth()) - gapToUse;
//        segmentLength*=amplitudeScale;
            final Shape segment = Shape.segment(Point.at(0, 0), Point.at(segmentLength, 0));
            delimiterShapeGroup.add(segment);

            //Manages rotation of label
            switch (rotateLabel) {
                case FIXED:
                    delimiterLabelToDraw.rotate(-angle);
                    break;
                case ROTATE:
                    break;
                case SMART:
                    if ((angle > .5 * PI) && (angle < 1.5 * PI)) {
                        delimiterLabelToDraw.rotate(PI);
                    }
            }

            delimiterLabelToDraw.stackTo(segment, Anchor.Type.RIGHT, gapToUse);

            labelMarkPoint.v.copyFrom(delimiterLabelToDraw.getCenter().v);
            delimiterShapeGroup.add(segment.copy().stackTo(BB, Anchor.Type.LEFT));
            delimiterLabelToDraw.shift(0, +gap * amplitudeScale);
            delimiterLabelToDraw.scale(amplitudeScale);
        }
        delimiterShapeGroup.add(verticalBar.copy().scale(Point.at(0, 0), -1, 1).shift(width, 0));
        delimiterShapeGroup.shift(0, +gap * amplitudeScale);

        delimiterShapeGroup.scale(amplitudeScale);
        delimiterShapeGroup.getMp().copyFrom(mpDelimiter);
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(AA, BB, A, B, 1);
        tr.applyTransform(delimiterShapeGroup);
        tr.applyTransform(delimiterLabelToDraw);
        delimiterShapeGroup.add(delimiterLabelToDraw);

        return delimiterShapeGroup;
    }

    @Override
    public MathObjectGroup getDelimiterShape() {
        return delimiterShapeGroup;
    }

}
