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

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.Delimiters.Delimiter;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David
 */
public class LengthMeasure extends Delimiter {

    private final double hgap;

    public enum TYPE {
        ARROW, SIMPLE
    }

    public static LengthMeasure make(Point A, Point B, Delimiter.Type type, double gap) {
        LengthMeasure resul = new LengthMeasure(A, B, type, gap);
        return resul;
    }

    private LengthMeasure(Point A, Point B, Type type, double gap) {
        super(A, B, type, gap);
        this.gap = gap;
        hgap = .05;
    }

//    @Override
//    public Rect getBoundingBox() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

//    @Override
//    public void draw(JMathAnimScene scene, Renderer r) {
//        if (isVisible()) {
//            if ((A.isEquivalentTo(B, 0)) || (innerScale == 0)) {
//                return;// Do nothing
//            }
//            MathObjectGroup delimiterToDraw = buildDelimiterShape();
//            for (MathObject obj : delimiterToDraw) {
//                obj.draw(scene, r);
//            }
//        }
//    }
    @Override
    protected MathObjectGroup buildDelimiterShape() {

        double width = A.to(B).norm();
        double angle = A.to(B).getAngle();
        Point AA = Point.at(0, 0);
        Point BB = Point.at(width, 0);

        double vCenter = .025 * delimiterScale;
        MathObjectGroup delShape = MathObjectGroup.make();
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
        Shape verticalBar = Shape.polyLine(Point.at(xOffset, 0), Point.at(0, vCenter), Point.at(xOffset, 2 * vCenter));
        delShape.add(verticalBar);
        double segmentLength = .5 * (width - delimiterLabel.getWidth()) - hgap;
        final Shape segment = Shape.segment(Point.at(0, vCenter), Point.at(segmentLength, vCenter));
        delShape.add(segment);

        delimiterLabelToDraw = delimiterLabel.copy();

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

        delimiterLabelToDraw.stackTo(segment, Anchor.Type.RIGHT, hgap);

        labelMarkPoint.copyFrom(delimiterLabelToDraw.getCenter());
        delShape.add(segment.copy().stackTo(delimiterLabelToDraw, Anchor.Type.RIGHT, hgap));
        delShape.add(verticalBar.copy().scale(Point.at(0, 0), -1, 1).shift(width, 0));
        delShape.shift(0, gap * amplitudeScale);
        delimiterLabelToDraw.shift(0, gap * amplitudeScale);
        delimiterLabelToDraw.scale(amplitudeScale);
        delShape.scale(amplitudeScale);
        delShape.getMp().copyFrom(mpDelimiter);
        AffineJTransform tr = AffineJTransform.createDirect2DHomothecy(AA, BB, A, B, 1);
        tr.applyTransform(delShape);
        tr.applyTransform(delimiterLabelToDraw);
        delShape.add(delimiterLabelToDraw);

        return delShape;
    }

}
