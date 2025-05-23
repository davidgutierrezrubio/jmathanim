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

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.mathobjects.*;

/**
 * Convenience static constructors for some common tippable objects
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class TippableObject extends AbstractTippableObject {

    //    public static AbstractTippableObject makeLabel(Shape shape, String text, Anchor.Type anchor, double location) {
//        LaTeXMathObject tipObject = LaTeXMathObject.make(text);
//        Point anchorPoint = Anchor.getAnchorPoint(tipObject, anchor);
//        AbstractTippableObject resul = new TippableObject(shape, tipObject, anchorPoint, location);
//        return resul;
//    }
    protected TippableObject(Shape shape, MathObject tipObject, Point anchorPoint, double location) {
        super(shape, tipObject, anchorPoint, location);
    }

    /**
     * Creates a parallel sign
     *
     * @param shape         Shape to anchor to
     * @param location      Location of the shape. A number between 0 and 1.
     * @param numberOfMarks Number of marks to draw
     * @return The tippable object
     */
    public static TippableObject equalLengthTip(Shape shape, double location, int numberOfMarks) {
        MultiShapeObject parallelSign = MultiShapeObject.make();
        for (int i = 0; i < numberOfMarks; i++) {
            parallelSign.add(Shape.segment(Point.at(0, i), Point.at(2, i)));
        }
        parallelSign.setWidth(.05);
        parallelSign.setAbsoluteSize(Anchor.Type.CENTER);
        //(Shape shape,double location, AbstractTippableObject.slopeDirectionType dir,MathObject tipObject, Anchor.Type anchor ) {
        TippableObject resul = TippableObject.make(shape, location, AbstractTippableObject.SlopeDirectionType.POSITIVE, parallelSign, Anchor.Type.CENTER);
        return resul;
    }

    public static TippableObject arrowHead(Shape shape, double location, SlopeDirectionType dir, Arrow.ArrowType type) {
        Shape arrowHead = Arrow.buildArrowHead(type);
        Renderer r = JMathAnimConfig.getConfig().getScene().getRenderer();
//        arrowHead.setAbsoluteSize(Anchor.Type.CENTER);
//        arrowHead.thickness(0);
        arrowHead.setWidth(r.ThicknessToMathWidth(shape.getMp().getThickness() * 5));
//        arrowHead.
        arrowHead.fillColor(shape.getMp().getDrawColor());
        arrowHead.drawColor(shape.getMp().getDrawColor());
        Point anchor = Anchor.getAnchorPoint(arrowHead, Anchor.Type.UPPER);
        TippableObject resul = new TippableObject(shape, arrowHead, anchor, location); // shape,location,slopeDirectionType.POSITIVE,equalLengthTip);
        resul.setSlopeDirection(dir);
        return resul;
    }

    public static TippableObject make(Shape shape, double location, AbstractTippableObject.SlopeDirectionType dir, MathObject tipObject) {
        return make(shape, location, dir, tipObject, Anchor.Type.CENTER);
    }

    public static TippableObject make(Shape shape, double location, AbstractTippableObject.SlopeDirectionType dir, MathObject tipObject, Anchor.Type anchor) {
        Point anchorPoint = Anchor.getAnchorPoint(tipObject, anchor);
        TippableObject resul = new TippableObject(shape, tipObject, anchorPoint, location);
        resul.setSlopeDirection(dir);
        return resul;
    }

    public static TippableObject make(Shape shape, MathObject tipObject, Point anchorPoint, double location) {
        TippableObject resul = new TippableObject(shape, tipObject, anchorPoint, location);
        return resul;
    }

    @Override
    public TippableObject copy() {
        TippableObject copy = new TippableObject(shape, mathobject.copy(), pivotPointRefMathObject.copy(), locationParameterOnShape);
        copy.copyStateFrom(this);
        return copy;
    }

}
