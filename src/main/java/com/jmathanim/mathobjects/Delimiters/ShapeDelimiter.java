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

import com.jmathanim.Utils.*;
import com.jmathanim.mathobjects.*;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 *
 * @author David
 */
public class ShapeDelimiter extends Delimiter {

    private SVGMathObject body;
    private final Shape delimiterShape;

    public static ShapeDelimiter make(Point A, Point B, Delimiter.Type type, double gap) {
        ShapeDelimiter resul = new ShapeDelimiter(A, B, type, gap);
        ResourceLoader rl = new ResourceLoader();
        String name;
        switch (type) {
            case PARENTHESIS:
                name = "#parenthesis.svg";
                break;
            case BRACKET:
                name = "#bracket.svg";
                break;
            default:
                name = "#braces.svg";
                break;
        }
        resul.body = new SVGMathObject(rl.getResource(name, "delimiters"));
        resul.mpDelimiter.add(resul.body);
        resul.style("latexdefault");
        return resul;
    }

    private ShapeDelimiter(Point A, Point B, Delimiter.Type type, double gap) {
        super(A, B, type, gap);
        minimumWidthToShrink = .5;
        delimiterShape = new Shape();
        mpDelimiter.add(delimiterShape);
    }

    @Override
    protected MathObjectGroup buildDelimiterShape() {

        if (amplitudeScale == 0) {
            return MathObjectGroup.make();//Nothing
        }

        double width = scaledA.to(scaledB).norm();//The final width of the delimiter
        double angle = A.to(B).getAngle();
        MultiShapeObject bodyCopy = body.copy();
        delimiterShape.getPath().clear();

        if (type == Type.BRACE) {
            minimumWidthToShrink = .5;
            double wr = .25 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            // 0,1,2,3,4,5 shapes Shapes 1 and 4 are extensible
            double wSpace = .5 * (hasToGrow);
            delimiterShape.getPath().jmPathPoints.addAll(bodyCopy.get(0).getPath().jmPathPoints);
            delimiterShape.merge(bodyCopy.get(2).shift(wSpace, 0), true, false)
                    .merge(bodyCopy.get(1).shift(2 * wSpace, 0), true, false)
                    .merge(bodyCopy.get(3).shift(wSpace, 0), true, true);
        }
        if (type == Type.BRACKET) {
            minimumWidthToShrink = .5;
            double wr = .8 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            double wSpace = hasToGrow;
            delimiterShape.merge(bodyCopy.get(0), false, false);
            delimiterShape.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
        }
        if (type == Type.PARENTHESIS) {
            minimumWidthToShrink = .5;
            double wr = 0.48 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double wSpace = Math.max(0, width - wr);
            delimiterShape.merge(bodyCopy.get(0), false, false);
            delimiterShape.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
        }

        MathObjectGroup resul = MathObjectGroup.make(delimiterShape);

        Rect bb = delimiterShape.getBoundingBox();
        delimiterShape.shift(0, gap * amplitudeScale);

        labelMarkPoint.stackTo(delimiterShape, Anchor.Type.UPPER, labelMarkGap * amplitudeScale);

        delimiterLabelToDraw = delimiterLabel.copy();
        resul.add(delimiterLabelToDraw);

        delimiterLabelToDraw.scale(amplitudeScale);

        //Manages rotation of label
        switch (rotateLabel) {
            case FIXED:
                delimiterLabelToDraw.rotate(-angle);
                break;
            case ROTATE:
                break;
            case SMART:
//                delimiterLabelToDraw.rotate(-angle);
                if ((angle > .5 * PI) && (angle < 1.5 * PI)) {
                    delimiterLabelToDraw.rotate(PI);
                }
        }
        delimiterLabelToDraw.stackTo(Anchor.Type.LOWER, labelMarkPoint, Anchor.Type.UPPER, 0);
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(bb.getDL(), bb.getDR(), scaledA, scaledB, 1);

        tr.applyTransform(resul);
        tr.applyTransform(labelMarkPoint);

        return resul;
    }

}
