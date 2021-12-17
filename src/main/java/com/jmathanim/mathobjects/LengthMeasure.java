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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 *
 * @author David
 */
public class LengthMeasure extends MathObject {

    private final Point A, B;

    private double gap;//gap between delimiter and object
    private double hgap;//Gap between number and lines
    private TYPE type;
    public double innerScale;
    private double outerScale;
    private final JMNumber measure;

    public enum TYPE {
        ARROW, SIMPLE
    }

    public LengthMeasure(Point A, Point B, double gap, TYPE type) {
        this.A = A;
        this.B = B;
        this.gap = gap;
        this.type = type;
        innerScale = 1;//1 means draw normally. Used mostly for ShowCreation
        outerScale = 1;//Scale to draw the delimiter. 1 is normal. bigger values give bigger delimiters
        measure = JMNumber.makeJMnumber(0);
        measure.setDecimalFormat(2);
        hgap = .05;
    }

    @Override
    public LengthMeasure copy() {
        LengthMeasure copy = new LengthMeasure(A, B, gap, type);
        copy.copyStateFrom(this);
        return this;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof LengthMeasure)) {
            return;
        }
        LengthMeasure lm = (LengthMeasure) obj;
        A.copyStateFrom(lm.A);
        A.copyStateFrom(lm.B);
        gap = lm.gap;
        type = lm.type;
        innerScale = lm.innerScale;
        outerScale = lm.outerScale;
    }

    @Override
    public Rect getBoundingBox() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (isVisible()) {
            if ((A.isEquivalentTo(B, 0)) || (innerScale == 0)) {
                return;// Do nothing
            }
            MathObjectGroup delimiterToDraw = buildDelimiterShape();
            for (MathObject obj : delimiterToDraw) {
                obj.draw(scene, r);
            }
        }
    }

    private MathObjectGroup buildDelimiterShape() {
        double width = A.to(B).norm();
        double angle = A.to(B).getAngle();
        Point AA = Point.at(0, 0);
        Point BB = Point.at(width, 0);
        measure.setNumber(width * innerScale);
        final JMNumber measureCopy = measure.copy();
        measureCopy.setDecimalFormat(2);
        measureCopy.scale(.65 * innerScale);

        double vCenter = .025 * outerScale;
        MathObjectGroup delShape = MathObjectGroup.make();
//        Shape verticalBar = Shape.segment(Point.at(0, 0), Point.at(0, 2 * vCenter));
        Shape verticalBar = Shape.polyLine(Point.at(vCenter,0),Point.at(0, vCenter), Point.at(vCenter, 2 * vCenter));
        delShape.add(verticalBar);
        double segmentLength = .5 * (width - measureCopy.getWidth()) - hgap;
        final Shape segment = Shape.segment(Point.at(0, vCenter), Point.at(segmentLength, vCenter));
        delShape.add(segment);

        if ((angle > .5 * PI) && (angle < 1.5 * PI)) {
            measureCopy.rotate(PI);
        }

        delShape.add(measureCopy.stackTo(segment, Anchor.Type.RIGHT, hgap));
        delShape.add(segment.copy().stackTo(measureCopy, Anchor.Type.RIGHT, hgap));
        delShape.add(verticalBar.copy().scale(Point.at(0,0),-1,1).shift(width, 0));
        delShape.shift(0, gap);
        delShape.scale(innerScale);
        AffineJTransform tr = AffineJTransform.createDirect2DHomothecy(AA, BB, A, B, 1);
        tr.applyTransform(delShape);

        return delShape;
    }

}
