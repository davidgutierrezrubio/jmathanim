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

import com.jmathanim.Utils.Anchor;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.AbstractLaTeXMathObject;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class LabelTip extends AbstractTippableObject {

    
    private AbstractLaTeXMathObject abstractLaTeXMathObject;

    public static LabelTip makeLabelTip(Point A, Point B,String text) {
        return makeLabelTip(Shape.segment(A,B),.5,text);
    }


    public static LabelTip makeLabelTip(Shape shape, double location, String text) {
        return makeLabelTip(shape, location, LaTeXMathObject.make(text));
    }
      /**
     * Attach an AbstractLaTeXMathObject instance to a specific point of a Shape. The LaTeX is
     * attached outside the point to a distance of the height.
     *
     * @param shape Shape to attach the tip
     * @param location Point of the shape to locate the tip. A parameter between
     * 0 and 1. Values outside this range are normalized.
     * @param tipLabel LaTeX object
     * @return The tippable object
     */

    public static LabelTip makeLabelTip(Shape shape, double location, AbstractLaTeXMathObject tipLabel) {
        
        LabelTip resul = new LabelTip(shape, tipLabel, Anchor.Type.CENTER, location);
        resul.rebuildShape();
        return resul;
    }

    protected LabelTip(Shape shape, AbstractLaTeXMathObject tipLabel, Anchor.Type anchor, double location) {
        super(shape, tipLabel, Anchor.getAnchorPoint(tipLabel, anchor), location);
        abstractLaTeXMathObject=tipLabel;
        correctionAngle=0;
        distanceToShape=tipLabel.getHeight();
    }

    protected LabelTip(Shape shape, MathObject tipObject, Point anchorPoint, double location) {
        super(shape, tipObject, anchorPoint, location);
    }

    @Override
    public LabelTip copy() {
        LabelTip copy = new LabelTip(shape, mathobject.copy(), pivotPointRefMathObject.copy(), locationParameterOnShape);
        copy.copyStateFrom(this);
        return copy;
    }

}
